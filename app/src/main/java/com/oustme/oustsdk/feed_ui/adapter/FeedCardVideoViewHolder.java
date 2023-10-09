package com.oustme.oustsdk.feed_ui.adapter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;
import static com.oustme.oustsdk.tools.OustSdkTools.drawableColor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
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
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import android.view.WindowManager;
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


import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.feed_ui.custom.BounceInterPolator;
import com.oustme.oustsdk.feed_ui.ui.RedirectWebView;
import com.oustme.oustsdk.feed_ui.ui.VideoCardDetailScreen;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.interfaces.common.FeedClickListener;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTONewFeed;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.ThreadPoolProvider;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.utils.OustResourceUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FeedCardVideoViewHolder extends RecyclerView.ViewHolder {

    private Context context;
    FeedClickListener feedClickListener;
    private String shareContent = " Download Oustsdk...";

    FrameLayout media_container;
    ImageView feed_image_thumbnail;
    View opacity_view;
    ImageView feedIndicator;
    ImageView play_button;
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

    public RequestManager requestManager;
    public ActiveUser activeUser;
    private final View parent;

    //exoplayer
    private StyledPlayerView videoSurfaceView;
    private SimpleExoPlayer videoPlayer;

    //private enum VolumeState {ON, OFF}

    // private boolean isVideoViewAdded;

    // controlling playback state
    //private VolumeState volumeState;
    String imageUrl;
    private long mLastTimeClicked = 0;


    FeedCardVideoViewHolder(@NonNull View itemView) {
        super(itemView);
        this.parent = itemView;

        media_container = itemView.findViewById(R.id.media_container);
        feed_image_thumbnail = itemView.findViewById(R.id.feed_image_thumbnail);
        //video_player_view = itemView.findViewById(R.id.video_player_view);
        /* pause_control = itemView.findViewById(R.id.pause_control);
         */
        opacity_view = itemView.findViewById(R.id.opacity_view);
        feedIndicator = itemView.findViewById(R.id.feedIndicator);
        play_button = itemView.findViewById(R.id.play_button);
        //volume_control = itemView.findViewById(R.id.volume_control);
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
    void setFeedDetails(DTONewFeed feed, Context context, RequestManager requestManager, ActiveUser activeUser, FeedClickListener feedClickListener) {
        this.context = context;
        this.feedClickListener = feedClickListener;
        this.requestManager = requestManager;
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
            feedIndicator.setVisibility(GONE);
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
            feed_comment_image.setImageDrawable(drawableColor(feedCommented));
        }

        feed_like_image.setOnClickListener(v -> feedLike(feed));
        feed_comment_image.setOnClickListener(v -> feedRedirect(feed, true));
        String feedDate = OustSdkTools.milliToDate("" + feed.getTimestamp());
        if (!feedDate.isEmpty()) {
            feed_date.setVisibility(View.VISIBLE);
            feed_date.setText(feedDate);

            //   pairArrayList.add(new Pair<View, String>(feed_date, "datetran_feed"));

        }

        imageUrl = null;
        if (feed.getImageUrl() != null && !feed.getImageUrl().isEmpty()) {
            imageUrl = feed.getImageUrl();
            Glide.with(context).load(feed.getImageUrl()).placeholder(OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.feed_default))).
                    error(OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.feed_default))).into(feed_image_thumbnail);
        } else if (feed.getCourseCardClass().getCardMedia() != null && feed.getCourseCardClass().getCardMedia().size() != 0) {

            if (feed.getCourseCardClass().getCardMedia().get(0).getMediaThumbnail() != null && !feed.getCourseCardClass().getCardMedia().get(0).getMediaThumbnail().isEmpty()) {

                imageUrl = feed.getCourseCardClass().getCardMedia().get(0).getMediaThumbnail();
                this.requestManager.load(feed.getCourseCardClass().getCardMedia().get(0).getMediaThumbnail()).into(feed_image_thumbnail);


            } else {
                Glide.with(context).load("no").placeholder(OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.feed_default))).
                        error(OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.feed_default))).into(feed_image_thumbnail);
            }


        } else {
            Glide.with(context).load("no").placeholder(OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.feed_default))).
                    error(com.oustme.oustsdk.tools.OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.feed_default))).into(feed_image_thumbnail);


        }

        String finalImageUrl = imageUrl;
        feed_share_image.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastTimeClicked < 2000) {
                return;
            }
            mLastTimeClicked = SystemClock.elapsedRealtime();
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

                                    } else {
                                        runOnUiThread(() -> {
                                            feed_share_image.setClickable(true);
                                            Toast.makeText(context, context.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                } else {
                                    runOnUiThread(() -> {
                                        feed_share_image.setClickable(true);
                                        Toast.makeText(context, context.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                                    });
                                }

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
                } else {
                    feed_share_image.setClickable(true);
                }
            } catch (Exception e) {
                feed_share_image.setClickable(true);
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

        });


        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);


        // setVideoSurfaceView(feed,false);


        play_button.setOnClickListener(v -> feedRedirect(feed, false));//playVideo(feed.getCourseCardClass(), feed)

        if (feed.isPlaying() && !feed.isAutoPlay()) {
            feed.setPlaying(false);
            OustPreferences.saveLongVar("FeedId", 0);
            if (videoSurfaceView != null) {
                videoSurfaceView.setVisibility(VISIBLE);
                removeVideoView();
            }

            playVideo(feed.getCourseCardClass(), feed, false);


        }


        if (feed.isAutoPlay()) {
            feed.setAutoPlay(false);
            if (videoSurfaceView != null) {
                videoSurfaceView.setVisibility(VISIBLE);
                removeVideoView();
            }

            playVideo(feed.getCourseCardClass(), feed, true);
        }


        media_container.setOnClickListener(v -> {
            if (videoPlayer != null) {

                if (videoPlayer.getPlaybackState() == Player.STATE_READY && videoPlayer.getPlayWhenReady()) {


                    OustPreferences.saveLongVar("FeedId", 0);
                    videoPlayer.setPlayWhenReady(false);
                    videoPlayer.stop();
                    if (opacity_view != null) {
                        opacity_view.setVisibility(VISIBLE);
                    }
                    if (play_button != null) {
                        play_button.setVisibility(VISIBLE);
                    }
                    feed_image_thumbnail.setVisibility(VISIBLE);

                } else {

                    feed.setPlaying(true);

                    OustPreferences.saveLongVar("FeedId", feed.getFeedId());
                    playVideo(feed.getCourseCardClass(), feed, true);
                }
            }
            //feedRedirect(feed, false);
        });
        feed_clickable_layout.setOnClickListener(v -> feedRedirect(feed, false));


    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private void setIconColors() {
        try {
            //comment_image_icon
            Drawable feedCommentDrawable = context.getResources().getDrawable(R.drawable.ic_comment_unselected);
            feed_comment_image.setImageDrawable(OustResourceUtils.setDefaultDrawableColor(feedCommentDrawable, context.getResources().getColor(R.color.unselected_text)));

            //share_image_icon
            Drawable feedShareDrawable = context.getResources().getDrawable(R.drawable.ic_share_common);
            feed_share_image.setImageDrawable(OustResourceUtils.setDefaultDrawableColor(feedShareDrawable, context.getResources().getColor(R.color.unselected_text)));

            //feed_attach_image
            Drawable feedAttachDrawable = context.getResources().getDrawable(R.drawable.ic_attach_fill);
            feed_attach_image.setImageDrawable(OustResourceUtils.setDefaultDrawableColor(feedAttachDrawable, context.getResources().getColor(R.color.unselected_text)));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setFeedLike(DTONewFeed feed, boolean isAnimation) {
        try {
            if (feed.getNumLikes() != 0) {
                feed_like.setText(OustSdkTools.formatMilliinFormat(feed.getNumLikes()));
                feed_like.setVisibility(VISIBLE);
            } else {
                feed_like.setVisibility(GONE);
            }

            Drawable feedLikeDrawable = context.getResources().getDrawable(R.drawable.ic_like_common);

            if (feed.isLiked()) {
                feedLikeDrawable = context.getResources().getDrawable(R.drawable.ic_liked_common);
                feed_like_image.setImageDrawable(drawableColor(feedLikeDrawable));
            }
            feed_like_image.setImageDrawable(feedLikeDrawable);
            likeButtonAnimation(isAnimation, feedLikeDrawable);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

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

        try {

            feed_image_thumbnail.setVisibility(VISIBLE);
            updateFeedViewed(feed);
            if (videoSurfaceView != null) {
                ViewGroup parent = (ViewGroup) videoSurfaceView.getParent();
                if (parent != null) {
                    int index = parent.indexOfChild(videoSurfaceView);
                    if (index >= 0) {
                        parent.removeViewAt(index);
                        //   isVideoViewAdded = false;
                    }
                }
            }

            feed_image_thumbnail.setVisibility(VISIBLE);

            feedClicked(feed.getFeedId(), feed.getCplId());
            Intent feedIntent = new Intent(context, VideoCardDetailScreen.class);
            Bundle feedbundle = new Bundle();
            feedbundle.putParcelable("Feed", feed);
            feedbundle.putString("CardData", new Gson().toJson(feed.getCourseCardClass()));
            feedbundle.putSerializable("ActiveUser", activeUser);
            feedbundle.putBoolean("FeedComment", isComment);
            feedbundle.putBoolean("isFeedLikeable", feed.isLikeble());
            feedIntent.putExtras(feedbundle);
            ((Activity) context).startActivityForResult(feedIntent, 1444);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


   /* private void setVolumeControl(VolumeState state) {
        //volumeState = state;
        if (state == VolumeState.OFF) {
            videoPlayer.setVolume(0f);
            // animateVolumeControl();
        } else if (state == VolumeState.ON) {
            videoPlayer.setVolume(1f);
            //  animateVolumeControl();
        }
    }*/

   /* private void animateVolumeControl() {
        if (volume_control != null) {
            volume_control.bringToFront();
            if (volumeState == VolumeState.OFF) {
                requestManager.load(R.drawable.ic_volume_off_grey_24dp)
                        .into(volume_control);
            } else if (volumeState == VolumeState.ON) {
                requestManager.load(R.drawable.ic_volume_up_grey_24dp)
                        .into(volume_control);
            }
            volume_control.animate().cancel();

            volume_control.setAlpha(1f);

            volume_control.animate()
                    .alpha(0f)
                    .setDuration(600).setStartDelay(1000);
        }
    }*/

    public void playVideo(DTOCourseCard courseCardClass, DTONewFeed feed, boolean play) {

        setVideoSurfaceView(feed, play);

        if (opacity_view != null) {
            opacity_view.setVisibility(GONE);
        }
        if (play_button != null) {
            play_button.setVisibility(GONE);
        }

        videoSurfaceView.setPlayer(videoPlayer);

        if (videoSurfaceView != null) {
            videoSurfaceView.setVisibility(VISIBLE);
        }
        //setVolumeControl(VolumeState.ON);

        /*volume_control.setOnClickListener(v -> {
            if (videoPlayer != null) {
                if (volumeState == VolumeState.OFF) {
                    Log.d("VideoAdapter", "togglePlaybackState: enabling volume.");
                    setVolumeControl(VolumeState.OFF);

                } else if (volumeState == VolumeState.ON) {
                    Log.d("VideoAdapter", "togglePlaybackState: disabling volume.");
                    setVolumeControl(VolumeState.ON);

                }
            }

        });*/

        feed.setPlaying(true);


        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, "Feed VideoPlayer"));

        if (feed.getVideoSource() != null) {
            videoPlayer.prepare(feed.getVideoSource());
            videoPlayer.setPlayWhenReady(true);
        } else if (courseCardClass.getCardMedia() != null && courseCardClass.getCardMedia().size() != 0) {

            String videoFileName = courseCardClass.getCardMedia().get(0).getData();
            if (videoFileName != null) {
                String filename = videoFileName;
                if (videoFileName.contains(".mp4")) {
                    filename = videoFileName.replace(".mp4", "");
                }
                String hlspath = "HLS/" + filename + "-HLS-Segment/" + filename + "-master-playlist.m3u8";
                Log.d("Video adapter", "startPlayingSignedUrlVideo: " + hlspath);
                String path = isVideoHlsPresentOnS3(hlspath);
                boolean ishlsVideo = true;
                if (path.isEmpty()) {
                    ishlsVideo = false;
                    path = "course/media/video/" + videoFileName;
                }

                if (path.contains(AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL)) {
                    path = path.replace(AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL, "");
                }
                path = AppConstants.StringConstants.CLOUD_FRONT_HLS_BASE_URL + path;
                Uri videoUri = Uri.parse(path);

                MediaSource videoSource;
                if (ishlsVideo) {
                    videoSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(videoUri);//, dataSourceFactory, 1, null, null);

                } else {
                    videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(videoUri);
                }
                feed.setVideoSource(videoSource);
                videoPlayer.prepare(videoSource);
                videoPlayer.setPlayWhenReady(true);


            }

        }

        OustPreferences.saveLongVar("FeedId", feed.getFeedId());


    }

    private String isVideoHlsPresentOnS3(final String keyName) {


        try {
            String bucketName = AppConstants.MediaURLConstants.BUCKET_NAME;
            String awsKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeyId");
            String awsSecretKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeySecret");
            AmazonS3 s3Client = new AmazonS3Client(new BasicAWSCredentials(awsKeyId, awsSecretKeyId));
            s3Client.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
            s3Client.getObjectMetadata(bucketName, keyName);
            return keyName;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            return "";
        }

    }

    private void addVideoView() {
        if (media_container.indexOfChild(videoSurfaceView) == -1) {
            media_container.addView(videoSurfaceView);
        }

        // isVideoViewAdded = true;
        if (videoSurfaceView != null) {
            videoSurfaceView.setVisibility(GONE);
            videoSurfaceView.requestFocus();
            videoSurfaceView.setVisibility(VISIBLE);
            videoSurfaceView.setAlpha(1);
        }

        feed_image_thumbnail.setVisibility(GONE);
        feedIndicator.setVisibility(GONE);
    }

    private void removeVideoView() {
        if (videoPlayer != null && videoPlayer.getPlaybackState() == Player.STATE_READY && videoPlayer.getPlayWhenReady()) {
            OustPreferences.saveLongVar("FeedId", 0);
            videoPlayer.setPlayWhenReady(false);
            videoPlayer.stop();
        }

        if (opacity_view != null) {
            opacity_view.setVisibility(VISIBLE);
        }
        if (play_button != null) {
            play_button.setVisibility(VISIBLE);
        }

        feed_image_thumbnail.setVisibility(VISIBLE);
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

    private void setVideoSurfaceView(DTONewFeed feed, boolean play) {


        try {

            videoSurfaceView = new StyledPlayerView(context);
            videoSurfaceView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);

            videoPlayer = new SimpleExoPlayer.Builder(context).build();

            videoSurfaceView.setUseController(false);
            videoSurfaceView.setPlayer(videoPlayer);
            videoPlayer.setVolume(0f);

            videoPlayer.addListener(new Player.EventListener() {
                @Override
                public void onTimelineChanged(@NonNull Timeline timeline, int reason) {

                }

                @Override
                public void onTracksChanged(@NonNull TrackGroupArray trackGroups, @NonNull TrackSelectionArray trackSelections) {

                }

                @Override
                public void onLoadingChanged(boolean isLoading) {

                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    switch (playbackState) {

                        case Player.STATE_BUFFERING:
                            Log.e("Video Adapter", "onPlayerStateChanged: Buffering video.");
                            if (opacity_view != null) {
                                opacity_view.setVisibility(VISIBLE);
                            }
                            if (play_button != null) {
                                play_button.setVisibility(VISIBLE);
                            }
                            videoSurfaceView.setVisibility(GONE);
                            feed_image_thumbnail.setVisibility(VISIBLE);


                            break;
                        case Player.STATE_ENDED:
                            feed.setPlaying(false);
                            // videoPlayer.release();
                            if (opacity_view != null) {
                                opacity_view.setVisibility(VISIBLE);
                            }
                            if (play_button != null) {
                                play_button.setVisibility(VISIBLE);
                            }
                            if (imageUrl != null) {

                                videoSurfaceView.setVisibility(GONE);
                                feed_image_thumbnail.setVisibility(VISIBLE);

                            } else {
                                videoSurfaceView.setVisibility(VISIBLE);
                                feed_image_thumbnail.setVisibility(GONE);
                            }
                            videoPlayer.seekTo(1);
                            videoPlayer.stop(true);
                            videoPlayer.setPlayWhenReady(false);
                            // removeVideoView(videoSurfaceView);
                            break;


                        case Player.STATE_IDLE:
                            Log.d("Video ", "onPlayerStateChanged: Video idle.");
                            if (opacity_view != null) {
                                opacity_view.setVisibility(VISIBLE);
                            }
                            if (play_button != null) {
                                play_button.setVisibility(VISIBLE);
                            }
                            if (imageUrl != null) {
                                videoSurfaceView.setVisibility(GONE);
                                feed_image_thumbnail.setVisibility(VISIBLE);

                            } else {
                                videoSurfaceView.setVisibility(VISIBLE);
                                feed_image_thumbnail.setVisibility(GONE);
                            }
                            videoPlayer.seekTo(1);
                            videoPlayer.stop();
                            videoPlayer.setPlayWhenReady(false);
                            //  videoPlayer.release();
                            break;


                        case Player.STATE_READY:
                            Log.e("Video", "onPlayerStateChanged: Ready to play.");

                            if (opacity_view != null) {
                                opacity_view.setVisibility(GONE);
                            }
                            if (play_button != null) {
                                play_button.setVisibility(GONE);
                            }
                            videoPlayer.setPlayWhenReady(true);
                            if (play) {
                                addVideoView();
                            }

                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void onPlaybackSuppressionReasonChanged(int playbackSuppressionReason) {

                }

                @Override
                public void onIsPlayingChanged(boolean isPlaying) {

                }

                @Override
                public void onRepeatModeChanged(int repeatMode) {

                }

                @Override
                public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

                }

                @Override
                public void onPlayerError(@NonNull ExoPlaybackException error) {

                }

                @Override
                public void onPositionDiscontinuity(int reason) {

                }

                @Override
                public void onPlaybackParametersChanged(@NonNull PlaybackParameters playbackParameters) {

                }

                @Override
                public void onSeekProcessed() {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }


    }

    /*private void removeVideoView(PlayerView videoView) {
        ViewGroup parent = (ViewGroup) videoView.getParent();
        if (parent == null) {
            return;
        }

        int index = parent.indexOfChild(videoView);
        if (index >= 0) {
            parent.removeViewAt(index);
        }

    }*/
}
