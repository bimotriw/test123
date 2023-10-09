package com.oustme.oustsdk.feed_ui.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
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
import androidx.core.graphics.drawable.DrawableCompat;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.assessments.AssessmentPlayActivity;
import com.oustme.oustsdk.assessment_ui.assessmentDetail.AssessmentDetailScreen;
import com.oustme.oustsdk.course_ui.CourseDetailScreen;
import com.oustme.oustsdk.feed_ui.custom.BounceInterPolator;
import com.oustme.oustsdk.feed_ui.ui.GeneralFeedDetailScreen;
import com.oustme.oustsdk.feed_ui.ui.RedirectWebView;
import com.oustme.oustsdk.firebase.common.FeedType;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.interfaces.common.FeedClickListener;
import com.oustme.oustsdk.response.common.GameType;
import com.oustme.oustsdk.room.dto.DTONewFeed;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.ThreadPoolProvider;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.oustme.oustsdk.utils.OustResourceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;

class AssessmentViewHolder extends RecyclerView.ViewHolder {

    private Context context;
    private String toolbarColorCode, shareContent = " Download Oustsdk...";
    String imageUrl;
    public ActiveUser activeUser;

    LinearLayout feed_clickable_layout;
    ImageView feed_image;
    ImageView feedIndicator;
    TextView feed_date;
    TextView feed_viewed;
    TextView feed_title;
    TextView feed_description;
    TextView feed_dead_line;
    TextView action_feed_button;
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


    private HashMap<String, String> myDeskMap;
    FeedClickListener feedClickListener;
    private long mLastTimeClicked=0;

    public void setMyDeskMap(HashMap<String, String> myDeskMap) {
        this.myDeskMap = myDeskMap;
    }

    AssessmentViewHolder(@NonNull View itemView) {
        super(itemView);
        try {

            feed_clickable_layout = itemView.findViewById(R.id.feed_clickable_layout);
            feed_image = itemView.findViewById(R.id.feed_image);
            feedIndicator = itemView.findViewById(R.id.feedIndicator);
            feed_date = itemView.findViewById(R.id.feed_date);
            feed_viewed = itemView.findViewById(R.id.feed_viewed);
            feed_title = itemView.findViewById(R.id.feed_title);
            feed_description = itemView.findViewById(R.id.feed_description);
            feed_dead_line = itemView.findViewById(R.id.feed_dead_line);
            action_feed_button = itemView.findViewById(R.id.action_feed_button);
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
    void setFeedDetails(DTONewFeed feed, Context context, ActiveUser activeUser, FeedClickListener feedClickListener) {
        this.context = context;
        this.toolbarColorCode = OustPreferences.get("toolbarColorCode");
        this.activeUser = activeUser;
        this.feedClickListener = feedClickListener;

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
            String numShares = "" + feed.getNumShares();
            feed_share.setText(numShares);
        }

        if (feed.getContent() != null && !feed.getContent().trim().isEmpty()) {
            Spanned feedContent = Html.fromHtml(feed.getContent());
            feed_description.setText(feedContent);
        } else {
            feed_description.setVisibility(View.GONE);
        }

        if (feed.getNumComments() != 0) {
            feed_comment.setText(OustSdkTools.formatMilliinFormat(feed.getNumComments()));
        }

        if (feed.isCommented()) {
            Drawable feedCommented = context.getResources().getDrawable(R.drawable.ic_comment_selected);
            feed_comment_image.setImageDrawable(drawableColor(feedCommented));
        }

        if (!feed.getImageUrl().isEmpty()) {

            imageUrl = feed.getImageUrl();
           /* Glide.with(context).load(feed.getImageUrl()).placeholder(OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.feed_default))).
                    error(OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.feed_default))).into(feed_image);*/
            Glide.with(context)
                    .asBitmap()
                    .load(feed.getImageUrl())
                    .placeholder(OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.feed_default)))
                    .error(OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.feed_default)))
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


        }

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
                    shareContent = feed.getHeader() + "\nHi There, " + " ....Lets get more feed on Oust.The new way to study smarter .. https://bnc.oustme.com/rVzVFAzrw5";
                    setUserShareCount(feed.getFeedId(), feed);

                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        ThreadPoolProvider.getInstance().getFeedShareSingleThreadExecutor().execute(() -> {
                            HttpURLConnection connection = null;

                            try {
                                connection = (HttpURLConnection) stringToURL("" + imageUrl).openConnection();

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
                }else {
                    feed_share_image.setClickable(true);
                }

            } catch (Exception e) {
                feed_share_image.setClickable(true);
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

        });


        //feed_like_image.setOnClickListener(v -> feedLike(feed));
        String feedType = "";
        String action_Button = context.getResources().getString(R.string.view);
        if (feed.getFeedType() == FeedType.ASSESSMENT_PLAY) {
            feedType = context.getResources().getString(R.string.assessment) + " / ";
            action_Button = context.getResources().getString(R.string.view_assessment);
        } else if (feed.getFeedType() == FeedType.COURSE_UPDATE) {
            feedType = context.getResources().getString(R.string.course_text) + " / ";
            action_Button = context.getResources().getString(R.string.view_course);
        }
        if (feed.getNewBtnText() != null && !feed.getNewBtnText().isEmpty()) {
            action_Button = OustSdkTools.returnValidString(feed.getNewBtnText());
        }
        action_feed_button.setText(action_Button);
        String feedDate = feedType + OustSdkTools.milliToDate("" + feed.getTimestamp());


        String feedDeadLine = OustSdkTools.milliToDate("" + feed.getExpiryTime());
        if (!feedDate.isEmpty()) {
            feed_date.setVisibility(View.VISIBLE);
            feed_date.setText(feedDate);


        }
        if (!feedDeadLine.isEmpty() && feed.getExpiryTime() != 0) {
            feed_dead_line.setVisibility(View.VISIBLE);
            feedDeadLine = context.getResources().getString(R.string.deadline) + " " + feedDeadLine.toUpperCase();
            feed_dead_line.setText(feedDeadLine);

        }

        action_feed_button.setOnClickListener(v -> feedRedirect(feed, false));

        feed_like_image.setOnClickListener(v -> feedLike(feed));
        feed_comment_image.setOnClickListener(v -> feedRedirect(feed, true));
        feed_clickable_layout.setOnClickListener(v -> feedRedirect(feed, false));


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

        GradientDrawable surveyButtonBg = (GradientDrawable) action_feed_button.getBackground();
        surveyButtonBg.setStroke(3, Color.parseColor(toolbarColorCode));
        action_feed_button.setBackground(surveyButtonBg);


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
        }


        feed_like_image.setImageDrawable(drawableColor(feedLikeDrawable));
        likeButtonAnimation(isAnimation, feedLikeDrawable);
    }

    private void feedLike(DTONewFeed feed) {
        feedClicked(feed.getFeedId(), feed.getCplId());
        updateFeedViewed(feed);
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

    private void feedRedirect(DTONewFeed feed, boolean isComment) {
        updateFeedViewed(feed);
        feedClicked(feed.getFeedId(), feed.getCplId());

        feedRewardUpdate(feed);

        Intent feedIntent = new Intent(context, GeneralFeedDetailScreen.class);
        Bundle feedBundle = new Bundle();
        feedBundle.putSerializable("deskDataMap", myDeskMap);
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(OustAppState.getInstance().getActiveUser().getStudentid());
        String catalog_distribution_url;

        if (feed.getFeedType() == FeedType.COURSE_UPDATE) {


            feedIntent = new Intent(context, CourseDetailScreen.class);
            feedIntent.putExtra("CourseId", feed.getCourseId());
            feedIntent.putExtra("catalog_id", "" + feed.getCourseId());
            feedIntent.putExtra("catalog_type", "COURSE");
            feedBundle.putParcelable("Feed", feed);
            feedBundle.putBoolean("FeedComment", isComment);
            feedBundle.putBoolean("isFeedLikeable", feed.isLikeble());
            feedBundle.putSerializable("ActiveUser", activeUser);
            feedBundle.putSerializable("deskDataMap", myDeskMap);
            if (feed.getCourseCardClass() != null) {
                feedBundle.putString("CardData", new Gson().toJson(feed.getCourseCardClass()));
            }
            feedIntent.putExtras(feedBundle);
            ((Activity) context).startActivityForResult(feedIntent, 1444);

        } else {
            feedIntent.putExtra("IsAssessment", true);
            feedIntent.putExtra("feedType", feed.getFeedType().toString());
            feedIntent.putExtra("AssessmentId", feed.getAssessmentId());

            Log.e("Feed", "inside gotoAssessment() method");
            String assessmentId = "" + feed.getAssessmentId();
            if (assessmentId.contains("COURSE")) {
                assessmentId = assessmentId.replace("COURSE", "");
            } else if (assessmentId.contains("course")) {
                assessmentId = assessmentId.replace("course", "");
            }

            String id = "ASSESSMENT" + feed.getAssessmentId();
            if (myDeskMap != null && myDeskMap.containsKey(id)) {

                if (isComment) {
                    feedBundle.putParcelable("Feed", feed);
                    feedBundle.putBoolean("FeedComment", true);
                    feedBundle.putBoolean("isFeedLikeable", feed.isLikeble());
                    feedBundle.putSerializable("ActiveUser", activeUser);
                    if (feed.getCourseCardClass() != null) {
                        feedBundle.putString("CardData", new Gson().toJson(feed.getCourseCardClass()));
                    }
                    feedIntent.putExtras(feedBundle);
                    ((Activity) context).startActivityForResult(feedIntent, 1444);
                } else {
                    if (!assessmentId.isEmpty()) {
                        Gson gson = new GsonBuilder().create();
                        Intent intent;
                        if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI)) {
                            intent = new Intent(OustSdkApplication.getContext(), AssessmentDetailScreen.class);
                        } else {
                            intent = new Intent(OustSdkApplication.getContext(), AssessmentPlayActivity.class);
                        }
                        //Intent intent = new Intent(OustSdkApplication.getContext(), AssessmentDetailScreen.class);
                        boolean isAssessmentValid = false;
                        if ((OustAppState.getInstance().getAssessmentFirebaseClassList() != null) && (OustAppState.getInstance().getAssessmentFirebaseClassList().size() > 0)) {
                            for (int i = 0; i < OustAppState.getInstance().getAssessmentFirebaseClassList().size(); i++) {
                                if ((assessmentId.equalsIgnoreCase(("" + OustAppState.getInstance().getAssessmentFirebaseClassList().get(i).getAsssessemntId())))) {
                                    OustAppState.getInstance().setAssessmentFirebaseClass(OustAppState.getInstance().getAssessmentFirebaseClassList().get(i));
                                    isAssessmentValid = true;
                                }
                            }
                            if (!isAssessmentValid) {
                                OustSdkTools.showToast(context.getResources().getString(R.string.assessment_no_longer));
                                return;
                            }
                        } else {
                            intent.putExtra("assessmentId", assessmentId);
                        }
                        intent.putExtra("ActiveGame", gson.toJson(setGame(activeUser)));
                        intent.putExtra("ActiveUser", gson.toJson(activeUser));
                        context.startActivity(intent);
                    }
                }


            } else {
                if (myDeskMap != null) {
                    catalog_distribution_url = context.getResources().getString(R.string.distribut_assessment_url);
                    catalog_distribution_url = catalog_distribution_url.replace("{assessmentId}", "" + feed.getAssessmentId());
                    catalog_distribution_url = HttpManager.getAbsoluteUrl(catalog_distribution_url);
                    JSONObject jsonParams = new JSONObject();
                    try {
                        jsonParams.put("users", jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }

                    String finalAssessmentId = assessmentId;
                    Intent finalFeedIntent = feedIntent;
                    ApiCallUtils.doNetworkCall(Request.Method.PUT, catalog_distribution_url, OustSdkTools.getRequestObjectforJSONObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.optBoolean("success")) {
                                    OustPreferences.save("catalogId", "ASSESSMENT" + "" + "" + feed.getAssessmentId());
                                    if (myDeskMap != null) {

                                        myDeskMap.put("ASSESSMENT" + feed.getAssessmentId(), "ASSESSMENT");
                                        if (isComment) {
                                            feedBundle.putParcelable("Feed", feed);
                                            feedBundle.putBoolean("FeedComment", isComment);
                                            feedBundle.putBoolean("isFeedLikeable", feed.isLikeble());
                                            feedBundle.putSerializable("ActiveUser", activeUser);
                                            if (feed.getCourseCardClass() != null) {
                                                feedBundle.putString("CardData", new Gson().toJson(feed.getCourseCardClass()));
                                            }
                                            finalFeedIntent.putExtras(feedBundle);
                                            ((Activity) context).startActivityForResult(finalFeedIntent, 1444);
                                        } else {
                                            Gson gson = new GsonBuilder().create();
                                            Intent intent;
                                            if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI)) {
                                                intent = new Intent(OustSdkApplication.getContext(), AssessmentDetailScreen.class);
                                            } else {
                                                intent = new Intent(OustSdkApplication.getContext(), AssessmentPlayActivity.class);
                                            }
                                            //Intent intent = new Intent(OustSdkApplication.getContext(), AssessmentDetailScreen.class);
                                            intent.putExtra("assessmentId", finalAssessmentId);
                                            intent.putExtra("ActiveGame", gson.toJson(setGame(activeUser)));
                                            intent.putExtra("ActiveUser", gson.toJson(activeUser));
                                            context.startActivity(intent);
                                        }


                                    }

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                }
            }


        }


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
                    runOnUiThread(() -> {

                        String numShares = "" + feed.getNumShares();
                        feed_share.setText(numShares);

                    });

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

    public ActiveGame setGame(ActiveUser activeUser) {
        ActiveGame activeGame = new ActiveGame();
        try {

            activeGame.setGameid("");
            activeGame.setGames(activeUser.getGames());
            activeGame.setStudentid(activeUser.getStudentid());
            activeGame.setChallengerid(activeUser.getStudentid());
            activeGame.setChallengerDisplayName(activeUser.getUserDisplayName());
            activeGame.setChallengerAvatar(activeUser.getAvatar());
            activeGame.setOpponentAvatar(OustSdkTools.getMysteryAvatar());
            activeGame.setOpponentid("Mystery");
            activeGame.setOpponentDisplayName("Mystery");
            activeGame.setGameType(GameType.MYSTERY);
            activeGame.setGuestUser(false);
            activeGame.setRematch(false);
            activeGame.setGrade(activeUser.getGrade());
            activeGame.setGroupId("");
            activeGame.setSubject(activeUser.getSubject());
            activeGame.setTopic(activeUser.getTopic());
            activeGame.setLevel(activeUser.getLevel());
            activeGame.setLevelPercentage(activeUser.getLevelPercentage());
            activeGame.setWins(activeUser.getWins());
            activeGame.setModuleId(activeUser.getModuleId());
            activeGame.setModuleName(activeUser.getModuleName());
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);

        }

        return activeGame;
    }

    private Drawable drawableColor(Drawable drawable) {

        DrawableCompat.setTint(
                DrawableCompat.wrap(drawable),
                Color.parseColor(toolbarColorCode)
        );

        return drawable;

    }

}
