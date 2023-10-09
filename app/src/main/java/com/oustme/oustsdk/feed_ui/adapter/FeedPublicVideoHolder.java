package com.oustme.oustsdk.feed_ui.adapter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;
import static com.oustme.oustsdk.feed_ui.tools.OustSdkTools.getYTVID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.feed_ui.custom.BounceInterPolator;
import com.oustme.oustsdk.feed_ui.ui.PublicVideoFeedCardScreen;
import com.oustme.oustsdk.feed_ui.ui.RedirectWebView;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.interfaces.common.FeedClickListener;
import com.oustme.oustsdk.room.dto.DTONewFeed;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.ThreadPoolProvider;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FeedPublicVideoHolder extends RecyclerView.ViewHolder {

    private Context context;
    FeedClickListener feedClickListener;

    private String shareContent = " Download Oustsdk...";

    FrameLayout media_container;
    YouTubePlayerView feed_public_video;
    ImageView feed_image_thumbnail;
    View opacity_view;
    /*ImageView volume_control;
    ImageView pause_control;*/
    ImageView play_button;
    ImageView feedIndicator;
    LinearLayout feed_clickable_layout;
    TextView feed_date;
    TextView feed_viewed;
    TextView feed_title;
    TextView feed_description;
    LinearLayout feed_action_lay;
    RelativeLayout feed_like_lay;
    ImageView feed_like_image;
    TextView feed_like;
    RelativeLayout feed_comment_lay;
    ImageView feed_comment_image;
    TextView feed_comment;
    RelativeLayout feed_attach_lay;
    ImageView feed_attach_image;
    RelativeLayout feed_share_lay;
    ImageView feed_share_image;
    TextView feed_share;

    public ActiveUser activeUser;
    YouTubePlayer youtubePlayer;
    private final View parent;
    private long mLastTimeClicked=0;


    FeedPublicVideoHolder(@NonNull View itemView) {
        super(itemView);
        this.parent = itemView;

        media_container = itemView.findViewById(R.id.media_container);
        feed_public_video = itemView.findViewById(R.id.feed_public_video);
        feed_image_thumbnail = itemView.findViewById(R.id.feed_image_thumbnail);
        opacity_view = itemView.findViewById(R.id.opacity_view);
        play_button = itemView.findViewById(R.id.play_button);
        feedIndicator = itemView.findViewById(R.id.feedIndicator);
        feed_clickable_layout = itemView.findViewById(R.id.feed_clickable_layout);
        feed_date = itemView.findViewById(R.id.feed_date);
        feed_viewed = itemView.findViewById(R.id.feed_viewed);
        feed_title = itemView.findViewById(R.id.feed_title);
        feed_description = itemView.findViewById(R.id.feed_description);
        feed_action_lay = itemView.findViewById(R.id.feed_action_lay);
        feed_like_lay = itemView.findViewById(R.id.feed_like_lay);
        feed_like_image = itemView.findViewById(R.id.feed_like_image);
        feed_like = itemView.findViewById(R.id.feed_like);
        feed_comment_lay = itemView.findViewById(R.id.feed_comment_lay);
        feed_comment_image = itemView.findViewById(R.id.feed_comment_image);
        feed_comment = itemView.findViewById(R.id.feed_comment);
        feed_attach_lay = itemView.findViewById(R.id.feed_attach_lay);
        feed_attach_image = itemView.findViewById(R.id.feed_attach_image);
        feed_share_lay = itemView.findViewById(R.id.feed_share_lay);
        feed_share_image = itemView.findViewById(R.id.feed_share_image);
        feed_share = itemView.findViewById(R.id.feed_share);


    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void setFeedDetails(DTONewFeed feed, Context context, ActiveUser activeUser, FeedClickListener feedClickListener) {
        this.context = context;
        this.feedClickListener = feedClickListener;
        this.activeUser = activeUser;


        if (feed.getCourseCardClass().isPotraitModeVideo()) {

            media_container.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 560, context.getResources().getDisplayMetrics());
            media_container.requestLayout();
        }
        parent.setTag(this);
        setIconColors();
        setFeedLike(feed, false);

        if (feed.getHeader() != null && !feed.getHeader().trim().isEmpty()) {
            Spanned feedHeader = Html.fromHtml(feed.getHeader().trim());
            feed_title.setText(feedHeader);
            feed_title.setVisibility(View.VISIBLE);
        }

        if (feed.isClicked()) {
            feedIndicator.setVisibility(View.GONE);
        }

        if (feed.getFeedViewCount() != 0) {
            feed_viewed.setVisibility(View.VISIBLE);
            String viewCount = OustSdkTools.numberToString(feed.getFeedViewCount());
            feed_viewed.setText(viewCount);
        }

        if (feed.getContent() != null && !feed.getContent().trim().isEmpty()) {
            feed_description.setVisibility(View.VISIBLE);
            Spanned feedContent = Html.fromHtml(feed.getContent().trim());
            feed_description.setText(feedContent);

        } else {
            if (feed.getCourseCardClass() != null && feed.getCourseCardClass().getContent() != null && !feed.getCourseCardClass().getContent().isEmpty()) {
                feed_description.setVisibility(View.VISIBLE);
                Spanned feedContent = Html.fromHtml(feed.getCourseCardClass().getContent().trim());
                feed_description.setText(feedContent);
            } else {
                feed_description.setVisibility(View.GONE);
            }
        }

        if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.FEED_SHARE_DISABLE) ||
                OustPreferences.getAppInstallVariable(AppConstants.StringConstants.FEED_COMMENT_DISABLE) ||
                OustPreferences.getAppInstallVariable(AppConstants.StringConstants.FEED_LIKE_DISABLE) ||
                (feed.getFileName() != null && !feed.getFileName().isEmpty())) {
            feed_action_lay.setVisibility(View.VISIBLE);
            boolean hideLike = false;
            boolean hideComment = false;
            boolean hideShare = false;
            boolean hideFile = false;
            if (!OustPreferences.getAppInstallVariable(AppConstants.StringConstants.FEED_LIKE_DISABLE)) {
                feed_like_lay.setVisibility(View.GONE);
                hideLike = true;
            }

            if (!OustPreferences.getAppInstallVariable(AppConstants.StringConstants.FEED_COMMENT_DISABLE)) {
                feed_comment_lay.setVisibility(View.GONE);
                hideComment = true;
            }

            if (!OustPreferences.getAppInstallVariable(AppConstants.StringConstants.FEED_SHARE_DISABLE)) {
                feed_share_lay.setVisibility(View.GONE);
                hideShare = true;
            }

            if (feed.getFileName() == null || feed.getFileName().isEmpty()) {
                feed_attach_lay.setVisibility(View.GONE);
                hideFile = true;
            }

            if (hideLike) {
                feed_comment_lay.setGravity(Gravity.START);
            }

            if (hideLike && hideComment) {
                feed_attach_lay.setGravity(Gravity.START);
            }

            if (hideLike && hideComment && hideFile) {
                feed_share_lay.setGravity(Gravity.START);
            }


            if (!hideLike && (hideFile && hideShare)) {
                feed_comment_lay.setGravity(Gravity.END);
            }

            if (!hideLike && (hideComment && hideShare)) {
                feed_attach_lay.setGravity(Gravity.END);
            }

            if (!hideComment && (hideLike && hideShare)) {
                feed_attach_lay.setGravity(Gravity.END);
            }


        } else {
            feed_action_lay.setVisibility(View.GONE);
        }


        feed_attach_image.setOnClickListener(v -> {
            String fileName = AppConstants.StringConstants.CLOUD_FRONT_BASE_FEED + feed.getFileName();
            Intent redirectScreen = new Intent(context, RedirectWebView.class);
            redirectScreen.putExtra("url", fileName);
            redirectScreen.putExtra("feed_title", feed.getHeader());
            context.startActivity(redirectScreen);
        });

        if (feed.getNumShares() != 0) {
            feed_share.setText(String.valueOf(feed.getNumShares()));
        }

        feed_comment.setText(OustSdkTools.formatMilliinFormat(feed.getNumComments()));
        if (feed.isCommented()) {
            Drawable feedCommented = context.getResources().getDrawable(R.drawable.ic_comment_selected);
            feed_comment_image.setImageDrawable(OustSdkTools.drawableColor(feedCommented));
        }

        feed_public_video.setVisibility(View.GONE);


        initializePlayer(feed);
        if (feed.isPlaying() && !feed.isAutoPlay()) {
            feed.setPlaying(false);
            // initializePlayer(feed);
            OustPreferences.saveLongVar("FeedId", 0);
            if (youtubePlayer != null) {
                feed.setPlaying(false);
                youtubePlayer.pause();
            }
        }


        if (feed.isAutoPlay()) {

            feed.setAutoPlay(false);

            if (youtubePlayer == null)
                return;

            if (feed.getCourseCardClass().getCardMedia().get(0).getData() != null) {
                // youtubePlayer.cueVideo(getYTVID(feed.getCourseCardClass().getCardMedia().get(0).getData()), 0);
                OustPreferences.saveLongVar("FeedId", feed.getFeedId());
                youtubePlayer.play();
                youtubePlayer.mute();
            }
        }


        play_button.setOnClickListener(view -> feedRedirect(feed, false));

        feed_public_video.setOnClickListener(v -> {

            if (youtubePlayer != null) {
                feed.setPlaying(false);
                youtubePlayer.pause();
            }
        });


        feed_like_image.setOnClickListener(v -> feedLike(feed));
        feed_comment_image.setOnClickListener(v -> feedRedirect(feed, true));
        String feedDate = OustSdkTools.milliToDate("" + feed.getTimestamp());
        if (!feedDate.isEmpty()) {
            feed_date.setVisibility(View.VISIBLE);
            feed_date.setText(feedDate);
            feed_date.setTransitionName("datetran_feed");

        }

        String imageUrl = "";

        if (feed.getCourseCardClass().getCardMedia() != null && feed.getCourseCardClass().getCardMedia().size() != 0) {
            {
                if (feed.getCourseCardClass().getCardMedia().get(0).getMediaThumbnail() != null && !feed.getCourseCardClass().getCardMedia().get(0).getMediaThumbnail().isEmpty()) {
                    imageUrl = feed.getCourseCardClass().getCardMedia().get(0).getMediaThumbnail();
                } else {
                    if (feed.getImageUrl() != null && !feed.getImageUrl().isEmpty()) {
                        imageUrl = feed.getImageUrl();

                    } else {
                        imageUrl = "https://img.youtube.com/vi/" + getYTVID(feed.getCourseCardClass().getCardMedia().get(0).getData()) + "/0.jpg";

                    }
                }
            }
        }

        if (!imageUrl.isEmpty()) {
            Glide.with(context).load(imageUrl).placeholder(OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.feed_default))).
                    error(OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.feed_default))).into(feed_image_thumbnail);

        }

        String finalImageUrl = imageUrl;
        feed_share_image.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastTimeClicked < 2000){
                return;
            }
            mLastTimeClicked=SystemClock.elapsedRealtime();
            try {
                feed_share_image.setClickable(false);
                boolean storagePermission = OustSdkTools.checkPermission(context);
                if (storagePermission) {
                    updateFeedViewed(feed);
                    feedClicked(feed.getFeedId(), feed.getCplId());
                    setUserShareCount(feed.getFeedId(), feed);
                    shareContent = feed.getHeader() + "\nHi There, " + " ....Lets get more feed on Oust.The new way to study smarter .. https://bnc.oustme.com/rVzVFAzrw5";

                    if (finalImageUrl != null) {
                        ThreadPoolProvider.getInstance().getFeedShareSingleThreadExecutor().execute(() -> {
                            HttpURLConnection connection = null;

                            try {
                                connection = (HttpURLConnection) stringToURL("" + finalImageUrl).openConnection();

                                connection.connect();

                                InputStream inputStream = connection.getInputStream();


                                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                                Bitmap bm = BitmapFactory.decodeStream(bufferedInputStream);
                                Uri bmpUri;
                                if (bm != null) {
                                    String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bm, "IMG_" + System.currentTimeMillis(), null);
                                    bmpUri = Uri.parse(path);

                                    if (bmpUri != null) {
                                        //posting to UI thread
                                        ThreadPoolProvider.getInstance().getHandler().post(() -> {
                                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                                            shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
                                            shareIntent.setType("image/png");
                                            context.startActivity(Intent.createChooser(shareIntent, "Share with"));
                                            feed_share_image.setClickable(true);
                                        });

                                    }else {
                                        runOnUiThread(() -> {
                                            feed_share_image.setClickable(true);
                                            Toast.makeText(context, context.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                }else {
                                    runOnUiThread(() -> {
                                        feed_share_image.setClickable(true);
                                        Toast.makeText(context, context.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                                    });                                  }

                            } catch (IOException e) {
                                runOnUiThread(() -> {
                                    feed_share_image.setClickable(true);
                                    Toast.makeText(context, "Unable to send,Check Permission", Toast.LENGTH_SHORT).show();
                                });
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            } finally {
                                assert connection != null;
                                connection.disconnect();
                            }


                        });

                    } else {
                        Intent shareIntent;
                        shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
                        shareIntent.setType("text/plain");

                        context.startActivity(Intent.createChooser(shareIntent, "Share with"));
                        feed_share_image.setClickable(true);

                    }

                }else {
                    feed_share_image.setClickable(true);
                }
            } catch (Exception e) {
                feed_share_image.setClickable(true);
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

        });

        feed_clickable_layout.setOnClickListener(v -> feedRedirect(feed, false));


    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private void setIconColors() {
        //comment_image_icon
        Drawable feedCommentDrawable = context.getResources().getDrawable(R.drawable.ic_comment_unselected);
        feed_comment_image.setImageDrawable(OustResourceUtils.setDefaultDrawableColor(feedCommentDrawable, context.getResources().getColor(R.color.unselected_text)));

        //share_image_icon
        Drawable feedShareDrawable = context.getResources().getDrawable(R.drawable.ic_share_common);
        feed_share_image.setImageDrawable(OustResourceUtils.setDefaultDrawableColor(feedShareDrawable, context.getResources().getColor(R.color.unselected_text)));

        //feed_attach_image
        Drawable feedAttachDrawable = context.getResources().getDrawable(R.drawable.ic_attach_fill);
        feed_attach_image.setImageDrawable(OustResourceUtils.setDefaultDrawableColor(feedAttachDrawable, context.getResources().getColor(R.color.unselected_text)));

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setFeedLike(DTONewFeed feed, boolean isAnimation) {

        if (feed.getNumLikes() != 0) {
            feed_like.setText(OustSdkTools.formatMilliinFormat(feed.getNumLikes()));
            feed_like.setVisibility(VISIBLE);
        } else {
            feed_like.setVisibility(GONE);
        }


        Drawable feedLikeDrawable = context.getResources().getDrawable(R.drawable.ic_like_common);

        if (feed.isLiked()) {
            feedLikeDrawable = context.getResources().getDrawable(R.drawable.ic_liked_common);
            feed_like_image.setImageDrawable(OustSdkTools.drawableColor(feedLikeDrawable));
        }
        feed_like_image.setImageDrawable(feedLikeDrawable);
        likeButtonAnimation(isAnimation, feedLikeDrawable);
    }

    private void feedLike(DTONewFeed feed) {
        updateFeedViewed(feed);
        feedClicked(feed.getFeedId(), feed.getCplId());
        if (feed.isLikeble()) {
            if (!feed.isLiked()) {
                feed.setNumLikes(feed.getNumLikes() + 1);
                feed.setLiked(true);
                setUserLike(feed, true);
            } else {
                if (feed.getNumLikes() > 0) {
                    feed.setLiked(false);
                    feed.setNumLikes(feed.getNumLikes() - 1);
                    setUserLike(feed, false);
                }
            }
        }


    }

    private void setUserLike(DTONewFeed feed, final boolean value) {
        try {
            String message1 = "/userFeed/" + activeUser.getStudentKey() + "/feed" + feed.getFeedId() + "/" + "isLiked";
            OustFirebaseTools.getRootRef().child(message1).setValue(value);
            OustFirebaseTools.getRootRef().child(message1).keepSynced(true);

            String message = "feeds/feed" + feed.getFeedId() + "/numLikes";
            DatabaseReference firebase = OustFirebaseTools.getRootRef().child(message);
            firebase.runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                    if (currentData.getValue() == null) {
                        currentData.setValue(1);
                    } else {
                        if (value) {
                            currentData.setValue((Long) currentData.getValue() + 1);
                        } else {
                            if ((Long) currentData.getValue() > 0) {
                                currentData.setValue((Long) currentData.getValue() - 1);
                            }
                        }
                    }
                    return Transaction.success(currentData);
                }

                @Override
                public void onComplete(DatabaseError DatabaseError, boolean b, DataSnapshot dataSnapshot) {
                    if (DatabaseError != null) {
                        Log.e("", "Firebase counter increment failed. New Count:{}" + dataSnapshot);
                    } else {
                        Log.e("", "Firebase counter increment succeeded.");

                        setFeedLike(feed, feed.isLiked());
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void updateFeedViewed(DTONewFeed mFeed) {
        try {
            if (!mFeed.isClicked()) {
                long feedId = mFeed.getFeedId();
                String message1 = "/userFeed/" + activeUser.getStudentKey() + "/feed" + feedId + "/" + AppConstants.StringConstants.IS_FEED_CLICKED;
                OustFirebaseTools.getRootRef().child(message1).setValue(true);
                OustFirebaseTools.getRootRef().child(message1).keepSynced(true);
                DatabaseReference firebase = OustFirebaseTools.getRootRef().child(message1);
                firebase.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                        currentData.setValue(true);
                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(DatabaseError DatabaseError, boolean b, DataSnapshot dataSnapshot) {
                        if (DatabaseError != null) {
                            Log.e("", "Firebase counter increment failed. New Count:{}" + dataSnapshot);
                        } else {
                            Log.e("", "Firebase counter increment succeeded.");
                        }
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void likeButtonAnimation(boolean animated, Drawable feedLikeDrawable) {
        if (animated) {

            Animation likeAnimation = AnimationUtils.loadAnimation(context, R.anim.bounce);
            double animationDuration = 2 * 1000;
            likeAnimation.setDuration((long) animationDuration);
            //feed_like_image.setImageDrawable(feedLikeDrawable);
            // Use bounce interpolator with amplitude 0.2 and frequency 20
            BounceInterPolator interpolator = new BounceInterPolator(0.2, 20);
            likeAnimation.setInterpolator(interpolator);

            feed_like_image.startAnimation(likeAnimation);

            likeAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation arg) {

                    feed_like_image.setImageDrawable(feedLikeDrawable);
                }

                @Override
                public void onAnimationRepeat(Animation arg) {
                }

                @Override
                public void onAnimationEnd(Animation arg) {
                    //animateButton();

                }
            });


        } else {
            feed_like_image.setImageDrawable(feedLikeDrawable);
        }
    }

    private URL stringToURL(String urlString) {
        try {
            return new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return null;
    }

    private void feedRedirect(DTONewFeed feed, boolean isComment) {
        updateFeedViewed(feed);
        feedClicked(feed.getFeedId(), feed.getCplId());

        if (feed_public_video != null) {
            //   feed_public_video.release();
            feed_image_thumbnail.setVisibility(View.VISIBLE);
            opacity_view.setVisibility(View.VISIBLE);
            feed_public_video.setVisibility(View.GONE);
            play_button.setVisibility(View.VISIBLE);
        }
        Intent feedIntent = new Intent(context, PublicVideoFeedCardScreen.class);
        Bundle feedbundle = new Bundle();
        feedbundle.putParcelable("Feed", feed);
        feedbundle.putString("CardData", new Gson().toJson(feed.getCourseCardClass()));
        feedbundle.putSerializable("ActiveUser", activeUser);
        feedbundle.putBoolean("FeedComment", isComment);
        feedbundle.putBoolean("isFeedLikeable", feed.isLikeble());
        feedIntent.putExtras(feedbundle);
        ((Activity) context).startActivityForResult(feedIntent, 1444);

    }


    public void feedClicked(long feedId, long cplId) {
        try {
            if (feedClickListener != null) {
                feedClickListener.onFeedClick(feedId, (int) cplId);
                feedClickListener.onFeedViewed(feedId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setUserShareCount(long feedId, DTONewFeed feed) {

        try {
            String message1 = "/userFeed/" + activeUser.getStudentKey() + "/feed" + feedId + "/" + "isShared";
            OustFirebaseTools.getRootRef().child(message1).setValue(true);
            OustFirebaseTools.getRootRef().child(message1).keepSynced(true);

            String message = "feeds/feed" + feedId + "/numShares";
            DatabaseReference firebase = OustFirebaseTools.getRootRef().child(message);

            firebase.runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                    int count;
                    if (currentData.getValue() == null) {
                        currentData.setValue(1);
                        count = 1;
                    } else {
                        currentData.setValue((Long) currentData.getValue() + 1);
                        count = (int) (long) currentData.getValue();
                    }
                    feed.setNumShares(count);
                    runOnUiThread(() -> feed_share.setText(String.valueOf(feed.getNumShares())));

                    return Transaction.success(currentData);
                }

                @Override
                public void onComplete(DatabaseError DatabaseError, boolean b, DataSnapshot dataSnapshot) {
                    if (DatabaseError != null) {
                        Log.e("", "Firebase counter increment failed. New Count:{}" + dataSnapshot);
                    } else {
                        Log.e("", "Firebase counter increment succeeded.");
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }


    }

    private void initializePlayer(DTONewFeed feed) {
        feed_public_video.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {

            @Override
            public void onError(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerError error) {
                feed.setPlaying(false);
                feed_image_thumbnail.setVisibility(View.VISIBLE);
                opacity_view.setVisibility(View.VISIBLE);
                feed_public_video.setVisibility(View.GONE);
                play_button.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                // feed.setPlaying(true);
                youtubePlayer = youTubePlayer;
                youtubePlayer.mute();
                if (feed.getCourseCardClass().getCardMedia().get(0).getData() != null) {
                    youtubePlayer.cueVideo(getYTVID(feed.getCourseCardClass().getCardMedia().get(0).getData()), 0);
                    youtubePlayer.mute();
                }

            }

            @Override
            public void onStateChange(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerState state) {
                if (state == PlayerConstants.PlayerState.ENDED || state == PlayerConstants.PlayerState.PAUSED) {
                    feed.setPlaying(false);
                    feed_image_thumbnail.setVisibility(View.VISIBLE);
                    opacity_view.setVisibility(View.VISIBLE);
                    feed_public_video.setVisibility(View.GONE);
                    play_button.setVisibility(View.VISIBLE);
                } else if (state == PlayerConstants.PlayerState.PLAYING) {

                    feed.setPlaying(true);
                    youtubePlayer.mute();
                    feed_image_thumbnail.setVisibility(View.GONE);
                    feedIndicator.setVisibility(View.GONE);
                    opacity_view.setVisibility(View.GONE);
                    feed_public_video.setVisibility(View.VISIBLE);
                    play_button.setVisibility(View.GONE);
                }
            }
        });
    }

}

