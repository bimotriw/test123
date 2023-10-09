package com.oustme.oustsdk.feed_ui.adapter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.assessments.GameLetActivity;
import com.oustme.oustsdk.activity.common.CplBaseActivity;
import com.oustme.oustsdk.activity.common.FeedCardActivity;
import com.oustme.oustsdk.activity.common.ZoomBaseActivity;
import com.oustme.oustsdk.feed_ui.custom.BounceInterPolator;
import com.oustme.oustsdk.feed_ui.ui.GeneralFeedDetailScreen;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.interfaces.common.FeedClickListener;
import com.oustme.oustsdk.room.dto.DTONewFeed;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustDataHandler;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.ThreadPoolProvider;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.oustme.oustsdk.utils.OustResourceUtils;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


class StandardViewHolder extends RecyclerView.ViewHolder {

    private Context context;
    private String shareContent = " Download Oust...";
    public ActiveUser activeUser;
    private FeedClickListener feedClickListener;
    private boolean isMultipleCpl = false;

    LinearLayout feed_clickable_layout;
    ImageView feed_image;
    ImageView feedIndicator;
    LinearLayout image_bottom_lay;
    TextView feed_date;
    TextView feed_viewed;
    TextView feed_title;
    TextView feed_description;
    TextView feed_dead_line;
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


    String imageUrl = "";

    private HashMap<String, String> myDeskMap;
    private long mLastTimeClicked = 0;

    public void setMyDeskMap(HashMap<String, String> myDeskMap) {
        this.myDeskMap = myDeskMap;
    }

    StandardViewHolder(@NonNull View itemView) {
        super(itemView);
        try {

            feed_clickable_layout = itemView.findViewById(R.id.feed_clickable_layout);
            feed_image = itemView.findViewById(R.id.feed_image);
            feedIndicator = itemView.findViewById(R.id.feedIndicator);
            image_bottom_lay = itemView.findViewById(R.id.image_bottom_lay);
            feed_date = itemView.findViewById(R.id.feed_date);
            feed_viewed = itemView.findViewById(R.id.feed_viewed);
            feed_title = itemView.findViewById(R.id.feed_title);
            feed_description = itemView.findViewById(R.id.feed_description);
            feed_dead_line = itemView.findViewById(R.id.feed_dead_line);
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

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void setFeedDetails(DTONewFeed feed, Context context, ActiveUser activeUser, FeedClickListener feedClickListener, boolean isMultipleCpl) {
        this.context = context;
        this.feedClickListener = feedClickListener;
//        Activity activity = (Activity) context;
        this.activeUser = activeUser;
        this.isMultipleCpl = isMultipleCpl;

        setIconColors();
        setFeedLike(feed, false);

        if (feed.isClicked()) {
            feedIndicator.setVisibility(View.GONE);
        }

        if (feed.getFeedViewCount() > 0) {
            feed_viewed.setVisibility(View.VISIBLE);
            String viewCount = com.oustme.oustsdk.tools.OustSdkTools.numberToString(feed.getFeedViewCount());
            feed_viewed.setText(viewCount);
        }

        if (feed.getHeader() != null && !feed.getHeader().trim().isEmpty()) {
            Spanned feedHeader = Html.fromHtml(feed.getHeader().trim());
            feed_title.setText(feedHeader);
            feed_title.setVisibility(View.VISIBLE);
        }

        feed_comment.setText(com.oustme.oustsdk.tools.OustSdkTools.formatMilliinFormat(feed.getNumComments()));
        if (feed.isCommented()) {
            Drawable feedCommented = context.getResources().getDrawable(R.drawable.ic_comment_selected);
            feed_comment_image.setImageDrawable(com.oustme.oustsdk.tools.OustSdkTools.drawableColor(feedCommented));
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


        feed_attach_image.setOnClickListener(v -> feedRedirect(feed, false, true));

        if (feed.getNumShares() != 0) {
            feed_share.setText(com.oustme.oustsdk.tools.OustSdkTools.returnValidString("" + feed.getNumShares()));
        }

        if (feed.getContent() != null && !feed.getContent().trim().isEmpty()) {
            feed_description.setVisibility(View.VISIBLE);
            Spanned feedContent = Html.fromHtml(feed.getContent().trim());
            feed_description.setText(feedContent);

        } else {
            feed_description.setVisibility(View.GONE);
        }

        if (!feed.getImageUrl().isEmpty()) {

            imageUrl = feed.getImageUrl();
            Glide.with(context)
                    .asBitmap()
                    .load(feed.getImageUrl())
                    .placeholder(com.oustme.oustsdk.tools.OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.feed_default)))
                    .error(com.oustme.oustsdk.tools.OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.feed_default)))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                            feed_image.setImageBitmap(resource);
                            feed_image.buildDrawingCache();


                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });


            //  Glide.with(context).load(feed.getImageUrl()).into(feed_image);
        } else {
            Glide.with(context).load("no").placeholder(com.oustme.oustsdk.tools.OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.feed_default))).
                    error(com.oustme.oustsdk.tools.OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.feed_default))).into(feed_image);
        }


        feed_share_image.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastTimeClicked < 2000) {
                return;
            }
            mLastTimeClicked = SystemClock.elapsedRealtime();
            try {
                boolean storagePermission = OustSdkTools.checkPermission(context);
                feed_share_image.setClickable(false);
                if (storagePermission) {
                    final String finalImageUrl = imageUrl;
                    updateFeedViewed(feed);
                    feedClicked(feed.getFeedId(), feed.getCplId());
                    shareContent = feed.getHeader() + "\nHi There, " + " ....Lets get more feed on Oust.The new way to study smarter .. https://bnc.oustme.com/rVzVFAzrw5";
                    setUserShareCount(feed.getFeedId(), feed);
                    if (!finalImageUrl.isEmpty()) {
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

        String feedDate = com.oustme.oustsdk.tools.OustSdkTools.milliToDate("" + feed.getTimestamp());
        String feedDeadLine = com.oustme.oustsdk.tools.OustSdkTools.milliToDate("" + feed.getExpiryTime());
        if (!feedDate.isEmpty()) {
            feed_date.setVisibility(View.VISIBLE);
            feed_date.setText(feedDate);

        } else {
            feed_date.setVisibility(View.GONE);
        }
        if (!feedDeadLine.isEmpty() && feed.getExpiryTime() != 0) {
            feed_dead_line.setVisibility(View.VISIBLE);
            feedDeadLine = context.getResources().getString(R.string.deadline) + " " + feedDeadLine.toUpperCase();
            feed_dead_line.setText(feedDeadLine);

        } else {
            feed_dead_line.setVisibility(View.GONE);
        }
        feed_like_image.setOnClickListener(v -> feedLike(feed));
        feed_comment_image.setOnClickListener(v -> feedRedirect(feed, true, false));
        feed_clickable_layout.setOnClickListener(v -> feedRedirect(feed, false, false));


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
        int likeColor = context.getResources().getColor(R.color.unselected_text);
        if (feed.isLiked()) {
            feedLikeDrawable = context.getResources().getDrawable(R.drawable.ic_liked_common);
            likeColor = OustResourceUtils.getColors();
        }
        feed_like_image.setImageDrawable(OustResourceUtils.setDefaultDrawableColor(feedLikeDrawable, likeColor));
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


    private void feedRewardUpdate(DTONewFeed newFeed) {
        try {
            if (newFeed == null) {
                Log.d("TAG", "feedUpdated: reward newfeed null");
                return;
            }

            if (newFeed.isFeedCoinsAdded()) {
                Log.d("TAG", "feedUpdated: reward coins already added");
                return;
            }

            if (newFeed.getFeedCoins() < 1) {
                Log.d("TAG", "feedUpdated: reward feedcoins is less than zero");
                return;
            }
            Log.d("TAG", "feedRewardUpdate: coins:" + newFeed.getFeedCoins());

            if (feedClickListener != null) {
                feedClickListener.onFeedRewardCoinsUpdate(newFeed);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void feedRedirect(DTONewFeed feed, boolean isComment, boolean isAttach) {
        updateFeedViewed(feed);
        feed.setClicked(true);
        //updateFeedViewed(feed);
        feedClicked(feed.getFeedId(), feed.getCplId());
        feedRewardUpdate(feed);

        String feedType = feed.getFeedType().toString();
        if (feed.getCourseCardClass() != null && !isComment) {

            OustDataHandler.getInstance().setCourseCardClass(feed.getCourseCardClass());
            Intent intent = new Intent(OustSdkApplication.getContext(), FeedCardActivity.class);
            intent.putExtra("type", "card");
            context.startActivity(intent);

        } else {
            if (!feedType.isEmpty() && !isComment) {
                switch (feedType) {

                    case "APP_UPGRADE":
                        rateApp();
                        break;
                    case "GAMELET_WORDJUMBLE":
                    case "GAMELET_WORDJUMBLE_V2":
                    case "GAMELET_WORDJUMBLE_V3":
                        gotoGamelet("" + feed.getAssessmentId(), feedType);
                        break;
                    case "JOIN_MEETING":
                        joinMeeting("" + feed.getId());
                        break;
                    case "CONTENT_PLAY_LIST":
                        checkingCplExistOrNot(feed.getParentCplId());
                        break;
                    default:
                        feedIntentScreen(feed, false, isAttach);
                        break;
                }
            } else {
                feedIntentScreen(feed, isComment, isAttach);
            }
        }
    }

    private void checkingCplExistOrNot(long cpl_id) {
        try {
            String message;
            Log.e("TAG", "checkCPLDistributionOrNot: cpl id-> " + cpl_id + "  isMultipleCpl--> " + isMultipleCpl);
            if (!isMultipleCpl) {
                message = "/landingPage/" + activeUser.getStudentKey() + "/cpl/" + cpl_id;
            } else {
                message = "/landingPage/" + activeUser.getStudentKey() + "/multipleCPL/" + cpl_id;
            }
            Log.e("TAG", "checkCPLDistributionOrNot: " + message);
            ValueEventListener avatarListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        if (null == snapshot.getValue()) {
                            distributeCPL(cpl_id);
                        } else {
                            Intent intent = new Intent(context, CplBaseActivity.class);
                            intent.putExtra("cplId", String.valueOf(cpl_id));
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            context.startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            };
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(avatarListener);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void feedIntentScreen(DTONewFeed feed, boolean isComment, boolean isAttach) {

        Intent feedIntent = new Intent(context, GeneralFeedDetailScreen.class);
        Bundle feedBundle = new Bundle();
        feedBundle.putParcelable("Feed", feed);
        feedBundle.putBoolean("FeedComment", isComment);
        feedBundle.putBoolean("FeedAttach", isAttach);
        if (feed.getCourseCardClass() != null) {
            feedBundle.putString("CardData", new Gson().toJson(feed.getCourseCardClass()));
        }

        feedBundle.putBoolean("isFeedLikeable", feed.isLikeble());
        feedIntent.putExtra("feedType", feed.getFeedType().toString());
        feedBundle.putSerializable("ActiveUser", activeUser);
        feedIntent.putExtra("deskDataMap", myDeskMap);
        feedIntent.putExtras(feedBundle);

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
                    runOnUiThread(() -> feed_share.setText(com.oustme.oustsdk.tools.OustSdkTools.returnValidString("" + feed.getNumShares())));

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

    private void rateApp() {
        try {
            String packageName = OustSdkApplication.getContext().getPackageName();
            Log.e("Package : ", packageName);
            if (!packageName.isEmpty()) {
                OustSdkApplication.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
            } else {
                OustSdkApplication.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.mili.jobsmili")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void gotoGamelet(String assessmentId, String type) {
        try {
            if ((assessmentId != null) && (!assessmentId.isEmpty())) {
                Intent intent = new Intent(context, GameLetActivity.class);
                intent.putExtra("assessmentId", assessmentId);
                intent.putExtra("feedType", type);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void joinMeeting(String meetingId) {
        if ((meetingId != null) && (meetingId.length() > 8) && (meetingId.length() < 12)) {
            boolean isAppInstalled = appInstalledOrNot();
            Intent intent;
            if (isAppInstalled) {
                intent = new Intent();
                intent.setComponent(new ComponentName("com.oustme.oustlive", "com.oustme.oustlive.ZoomJoinActivity"));
                intent.putExtra("zoommeetingId", meetingId);
                intent.putExtra("userName", OustAppState.getInstance().getActiveUser().getUserDisplayName());
                intent.putExtra("isComingThroughOust", true);
            } else {
                intent = new Intent(OustSdkApplication.getContext(), ZoomBaseActivity.class);
                intent.putExtra("joinMeeting", true);
            }
            context.startActivity(intent);
        } else {

            com.oustme.oustsdk.tools.OustSdkTools.showToast(context.getResources().getString(R.string.invalid_meeting_id));
        }
    }

    private boolean appInstalledOrNot() {
        PackageManager pm = OustSdkApplication.getContext().getPackageManager();
        try {
            pm.getPackageInfo("com.oustme.oustlive", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {

            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return false;
    }

    private void distributeCPL(long cplId) {
        String cplURL = OustSdkApplication.getContext().getResources().getString(R.string.cpl_distribution_api);
        cplURL = HttpManager.getAbsoluteUrl(cplURL);
        cplURL = cplURL.replace("{cplId}", "" + cplId);
        String user_id = activeUser.getStudentid();
        List<String> users = new ArrayList<>();
        users.add(user_id);
        CPLDistrClass cplDistrClass = new CPLDistrClass();
        cplDistrClass.setDistributeDateTime(com.oustme.oustsdk.tools.OustSdkTools.getDateTimeFromMilli2(SystemClock.currentThreadTimeMillis()));
        cplDistrClass.setUsers(users);
        cplDistrClass.setReusabilityAllowed(true);
        Gson gson = new GsonBuilder().create();
        String jsonParams = gson.toJson(cplDistrClass);

        ApiCallUtils.doNetworkCall(Request.Method.PUT, cplURL, com.oustme.oustsdk.tools.OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null && response.optBoolean("success")) {
                    OustSdkTools.showToast(context.getResources().getString(R.string.success));
                    Log.e("TAG", "onResponse: feed - cplId --> " + cplId);
                    Intent intent = new Intent(context, CplBaseActivity.class);
                    intent.putExtra("cplId", String.valueOf(cplId));
                    context.startActivity(intent);
                } else {
                    OustSdkTools.showToast(context.getResources().getString(R.string.something_went_wrong));
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                com.oustme.oustsdk.tools.OustSdkTools.showToast(context.getResources().getString(R.string.something_went_wrong));
            }
        });


    }

    public static class CPLDistrClass {
        List<String> users;
        boolean sendSMS;
        boolean sendEmail;
        boolean sendNotification;
        boolean onlyPANIndia;
        String distributeDateTime;
        boolean reusabilityAllowed;


        CPLDistrClass() {
        }

        public boolean isReusabilityAllowed() {
            return reusabilityAllowed;
        }

        public void setReusabilityAllowed(boolean reusabilityAllowed) {
            this.reusabilityAllowed = reusabilityAllowed;
        }

        public List<String> getUsers() {
            return users;
        }

        public void setUsers(List<String> users) {
            this.users = users;
        }

        public boolean isSendSMS() {
            return sendSMS;
        }

        public void setSendSMS(boolean sendSMS) {
            this.sendSMS = sendSMS;
        }

        public boolean isSendEmail() {
            return sendEmail;
        }

        public void setSendEmail(boolean sendEmail) {
            this.sendEmail = sendEmail;
        }

        public boolean isSendNotification() {
            return sendNotification;
        }

        public void setSendNotification(boolean sendNotification) {
            this.sendNotification = sendNotification;
        }

        public boolean isOnlyPANIndia() {
            return onlyPANIndia;
        }

        public void setOnlyPANIndia(boolean onlyPANIndia) {
            this.onlyPANIndia = onlyPANIndia;
        }

        public String getDistributeDateTime() {
            return distributeDateTime;
        }

        public void setDistributeDateTime(String distributeDateTime) {
            this.distributeDateTime = distributeDateTime;
        }
    }


}
