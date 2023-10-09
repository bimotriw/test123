package com.oustme.oustsdk.layoutFour.components.feedList;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;
import static com.oustme.oustsdk.tools.OustSdkApplication.getContext;
import static com.oustme.oustsdk.tools.OustSdkTools.drawableColor;
import static com.oustme.oustsdk.tools.OustSdkTools.stringToURL;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Parcelable;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.feed_ui.ui.FeedsAPICallingActivity;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.interfaces.common.FeedClickListener;
import com.oustme.oustsdk.layoutFour.components.youtubeExtractor.VideoMeta;
import com.oustme.oustsdk.layoutFour.components.youtubeExtractor.YouTubeExtractor;
import com.oustme.oustsdk.layoutFour.components.youtubeExtractor.YtFile;
import com.oustme.oustsdk.room.dto.DTOUserFeeds;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.ThreadPoolProvider;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import pl.droidsonroids.gif.GifImageView;

public class FeedsViewHolder extends RecyclerView.ViewHolder {
    String TAG = "FeedsViewHolder";
    private Context context;
    private ActiveUser activeUser;
    private FeedClickListener feedClickListener;
    private String toolbarColorCode, shareContent = " Download Oust...";
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);
    private final int mPlayerLength = 0;
    LinearLayout feed_clickable_layout;
    FrameLayout feed_image_lay;
    ImageView feed_image;
    GifImageView feed_card_gif;
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
    String imageUrl;
    String gifImageUrl;
    private long mLastTimeClicked = 0;
    View parent;
    RecyclerView feedsRecyclerView;
    DTOUserFeeds.FeedList feed;
    boolean isMultipleCpl;
    private final int survey_feed = 1;
    private final int course_feed = 2;
    private final int assessment_feed = 3;
    private final int video_feed = 4;
    private final int youtube_video_feed = 5;
    private final int image_feed = 6;
    private final int general_feed = 0;
    FrameLayout public_video;
    ImageView play_button;
    ImageView feed_image_thumbnail;
    ProgressBar progressBar;
    String feedDate;

    private ImageView thumbnail, volumeControl;
    private View viewHolderParent;
    private FrameLayout frameLayout;
    private StyledPlayerView styledPlayerView;
    //    PlayerView player_view_ui;
    private ExoPlayer videoPlayer;
    // vars
    private List<DTOUserFeeds.FeedList> postDataList = new ArrayList<>();
    private int videoSurfaceDefaultHeight = 0;
    private int screenDefaultHeight = 0;
    private int playPosition = -1;
    private boolean isVideoViewAdded;
    String tenantId;

    public FeedsViewHolder(@NonNull View itemView, Context context, RecyclerView rvFeeds) {
        super(itemView);
        parent = itemView;
        this.feedsRecyclerView = rvFeeds;
        feed_clickable_layout = itemView.findViewById(R.id.feed_clickable_layout);
        feed_image_lay = itemView.findViewById(R.id.feed_image_lay);
        feed_image = itemView.findViewById(R.id.feed_image);
        feed_card_gif = itemView.findViewById(R.id.feed_card_gif);
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
        feed_image_thumbnail = itemView.findViewById(R.id.feed_image_thumbnail);
        play_button = itemView.findViewById(R.id.play_button);
        public_video = itemView.findViewById(R.id.public_video);
        progressBar = itemView.findViewById(R.id.progressBar);

//        this.context = context.getApplicationContext();
        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        videoSurfaceDefaultHeight = point.x;
        screenDefaultHeight = point.y;

        styledPlayerView = new StyledPlayerView(context);
        styledPlayerView.setLayoutParams(new StyledPlayerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        styledPlayerView.setBackgroundColor(Color.TRANSPARENT);
        styledPlayerView.setShowNextButton(false);
        styledPlayerView.setShowFastForwardButton(false);
        styledPlayerView.setUseController(true);
        styledPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);

//        videoPlayer = new SimpleExoPlayer.Builder(context).build();
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(context).build();
        SimpleExoPlayer.Builder exoBuilder = new SimpleExoPlayer.Builder(context);
        exoBuilder.setBandwidthMeter(bandwidthMeter);

        DefaultRenderersFactory rf = new DefaultRenderersFactory(context).setEnableDecoderFallback(true);
        videoPlayer = new SimpleExoPlayer.Builder(Objects.requireNonNull(context), rf).build();
        styledPlayerView.setUseController(true);
        styledPlayerView.setPlayer(videoPlayer);
        styledPlayerView.hideController();

        tenantId = OustPreferences.get("tanentid");

        feedsRecyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                if (viewHolderParent != null && viewHolderParent.equals(view)) {
                    resetVideoView();
                }
            }
        });

        videoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {

                    case Player.STATE_BUFFERING:
                        Log.e(TAG, "onPlayerStateChanged: Buffering video.");
                        if (progressBar != null) {
                            progressBar.setVisibility(View.VISIBLE);
                        }

                        break;
                    case Player.STATE_ENDED:
                        Log.d(TAG, "onPlayerStateChanged: Video ended.");
                        videoPlayer.seekTo(0);
                        videoPlayer.pause();
                        break;
                    case Player.STATE_IDLE:

                        break;
                    case Player.STATE_READY:
                        Log.e(TAG, "onPlayerStateChanged: Ready to play.");
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                        if (!isVideoViewAdded) {
                            addVideoView();
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });

    }

    public void onBindData(DTOUserFeeds.FeedList feedList, Context context, FeedClickListener feedClickListener, RecyclerView rvFeeds, ActiveUser activeUser, boolean isMultipleCpl, ArrayList<DTOUserFeeds.FeedList> tempFeedArrayList) {
        try {
            parent.setTag(this);
            try {
                this.feed = feedList;
                this.postDataList = tempFeedArrayList;
                this.context = context;
                this.feedClickListener = feedClickListener;
                this.feedsRecyclerView = rvFeeds;
                this.activeUser = activeUser;
                this.isMultipleCpl = isMultipleCpl;
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
            this.activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
            try {
                // TODO Common for all modules
                setIconColors();
                setFeedLike(feed, false);

                if (feed.isFeedViewed()) {
                    feedIndicator.setVisibility(View.GONE);
                }

                if (feed.getHeader() != null && !feed.getHeader().trim().isEmpty()) {
                    Spanned feedHeader = Html.fromHtml(feed.getHeader().trim());
                    feed_title.setText(feedHeader);
                    feed_title.setVisibility(View.VISIBLE);
                } else {
                    feed_title.setVisibility(View.GONE);
                }

                if (feed.getNumComments() != 0) {
                    feed_comment.setText(OustSdkTools.formatMilliinFormat(feed.getNumComments()));
                } else {
                    feed_comment.setText("0");
                }
                if (feed.isCommented()) {
                    Drawable feedCommented = context.getResources().getDrawable(R.drawable.ic_comment_selected);
                    feed_comment_image.setImageDrawable(drawableColor(feedCommented));
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

                feed_title.setOnClickListener(v -> feedOnClick(null, feed, false, false));
                feed_description.setOnClickListener(v -> feedOnClick(null, feed, false, false));
                feed_attach_lay.setOnClickListener(v -> feedOnClick(null, feed, false, true));
                feed_image.setOnClickListener(v -> feedOnClick(null, feed, false, false));
                feed_card_gif.setOnClickListener(v -> feedOnClick(null, feed, false, false));
                action_feed_button.setOnClickListener(v -> feedOnClick(null, feed, false, false));

                if (feed.getNumShares() != 0) {
                    feed_share.setText(OustSdkTools.returnValidString("" + feed.getNumShares()));
                } else {
                    feed_share.setText("0");
                }

                try {
                    if (feed.getContent() != null && !feed.getContent().trim().isEmpty()) {
                        feed_description.setVisibility(View.VISIBLE);
                        Spanned feedContent = Html.fromHtml(feed.getContent().trim());
                        feed_description.setText(feedContent);
                    } else {
                        feed_description.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }

                feed_share_lay.setOnClickListener(v -> {
                    if (SystemClock.elapsedRealtime() - mLastTimeClicked < 2000) {
                        return;
                    }
                    mLastTimeClicked = SystemClock.elapsedRealtime();
                    feed_share_lay.setClickable(false);
                   /* try {
                        if (feedsRecyclerView != null) {
                            feedsRecyclerView.resetVolume();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                    updateFeedViewed(feed);
                    feedClicked(feed.getFeedId(), 0);
                    shareContent = feed.getHeader() + "\nHi There, " + " ....Lets get more feed on Oust.The new way to study smarter .. https://bnc.oustme.com/rVzVFAzrw5";
                    setUserShareCount(feed.getFeedId(), feed);
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        ThreadPoolProvider.getInstance().getFeedShareSingleThreadExecutor().execute(() -> {
                            HttpURLConnection connection = null;

                            try {
                                connection = (HttpURLConnection) Objects.requireNonNull(stringToURL("" + imageUrl)).openConnection();
                                connection.connect();
                                InputStream inputStream = connection.getInputStream();
                                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                                Bitmap bm = BitmapFactory.decodeStream(bufferedInputStream);
                                Uri bmpUri;
                                if (bm != null) {
                                    String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bm, "IMG_" + System.currentTimeMillis(), null);
                                    bmpUri = Uri.parse(path);
                                    if (bmpUri != null) {
                                        ThreadPoolProvider.getInstance().getHandler().post(() -> {
                                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                                            shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
                                            shareIntent.setType("image/png");
                                            context.startActivity(Intent.createChooser(shareIntent, "Share with"));
                                            feed_share_lay.setClickable(true);
                                        });
                                    } else {
                                        runOnUiThread(() -> {
                                            feed_share_lay.setClickable(true);
                                            Toast.makeText(context, context.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                } else {
                                    runOnUiThread(() -> {
                                        feed_share_lay.setClickable(true);
                                        Toast.makeText(context, context.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                                    });
                                }
                            } catch (IOException e) {
                                runOnUiThread(() -> {
                                    feed_share_lay.setClickable(true);
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
                        feed_share_lay.setClickable(true);
                    }
                });

                try {
                    if (feed.getFeedExpiry() != null && !feed.getFeedExpiry().isEmpty()) {
                        String feedDeadLine = OustSdkTools.milliToDate("" + feed.getFeedExpiry());
                        if (!feedDeadLine.isEmpty() && feed.getFeedExpiry() != null) {
                            feed_dead_line.setVisibility(View.VISIBLE);
                            feedDeadLine = context.getResources().getString(R.string.deadline) + " " + feedDeadLine.toUpperCase();
                            feed_dead_line.setText(feedDeadLine);
                        } else {
                            feed_dead_line.setVisibility(View.GONE);
                        }
                    } else {
                        feed_dead_line.setVisibility(View.GONE);
                    }
                    if (feed.getDistributedOn() != 0) {
                        feedDate = OustSdkTools.milliToDate("" + feed.getDistributedOn());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }

                feed_like_lay.setOnClickListener(v -> feedLike(feed));
                feed_comment_lay.setOnClickListener(v -> feedOnClick(null, feed, true, false));
                feed_clickable_layout.setOnClickListener(v -> feedOnClick(null, feed, false, false));
                String feedType;
                String action_Button = context.getResources().getString(R.string.view);
                try {
                    // TODO Check the FeedType
                    if (getFeedType(feed) == video_feed) {
                        //TODO CARD Private Video
                        if (!feedDate.isEmpty()) {
                            feed_date.setVisibility(View.VISIBLE);
                            feed_date.setText(feedDate);
                        } else {
                            feed_date.setVisibility(View.GONE);
                        }
                        loadImageForVideo();
                        public_video.setVisibility(View.VISIBLE);
                        feed_image_thumbnail.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        action_feed_button.setVisibility(View.GONE);
                        feed_image.setVisibility(View.GONE);
                        feed_card_gif.setVisibility(View.GONE);
                    } else if (getFeedType(feed) == youtube_video_feed) {
                        //TODO CARD Youtube
                        if (!feedDate.isEmpty()) {
                            feed_date.setVisibility(View.VISIBLE);
                            feed_date.setText(feedDate);
                        } else {
                            feed_date.setVisibility(View.GONE);
                        }
                        loadImageForVideo();
                        public_video.setVisibility(View.VISIBLE);
                        feed_image_thumbnail.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        action_feed_button.setVisibility(View.GONE);
                        feed_image.setVisibility(View.GONE);
                        feed_card_gif.setVisibility(View.GONE);
                    } else if (getFeedType(feed) == survey_feed) {
                        //TODO Survey
                        loadImage();
                        feedType = context.getResources().getString(R.string.survey_text) + " / ";
                        if (!feedDate.isEmpty()) {
                            feed_date.setVisibility(View.VISIBLE);
                            feed_date.setText(feedType + feedDate);
                        } else {
                            feed_date.setVisibility(View.GONE);
                        }
                        action_feed_button.setText(context.getResources().getString(R.string.go_text));
                        action_feed_button.setVisibility(View.VISIBLE);
                        feed_image.setVisibility(View.VISIBLE);
                        feed_card_gif.setVisibility(View.GONE);
                        public_video.setVisibility(View.GONE);
                        feed_image_thumbnail.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    } else if (getFeedType(feed) == assessment_feed) {
                        //TODO Course or Assessment
                        loadImage();
                        feedType = context.getResources().getString(R.string.assessment) + " / ";
                        action_Button = context.getResources().getString(R.string.view_assessment);
                        if (!feedDate.isEmpty()) {
                            feed_date.setVisibility(View.VISIBLE);
                            feed_date.setText(feedType + feedDate);
                        } else {
                            feed_date.setVisibility(View.GONE);
                        }
                        action_feed_button.setText(action_Button);
                        action_feed_button.setVisibility(View.VISIBLE);
                        public_video.setVisibility(View.GONE);
                        feed_image_thumbnail.setVisibility(View.GONE);
                        feed_image.setVisibility(View.VISIBLE);
                        feed_card_gif.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    } else if (getFeedType(feed) == course_feed) {
                        //TODO Course or Course
                        loadImage();
                        feedType = context.getResources().getString(R.string.course_text) + " / ";
                        action_Button = context.getResources().getString(R.string.view_course);
                        if (!feedDate.isEmpty()) {
                            feed_date.setVisibility(View.VISIBLE);
                            feed_date.setText(feedType + feedDate);
                        } else {
                            feed_date.setVisibility(View.GONE);
                        }
                        action_feed_button.setText(action_Button);
                        action_feed_button.setVisibility(View.VISIBLE);
                        feed_image.setVisibility(View.VISIBLE);
                        public_video.setVisibility(View.GONE);
                        feed_image_thumbnail.setVisibility(View.GONE);
                        feed_card_gif.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    } else if (getFeedType(feed) == image_feed) {
                        //TODO CARDImage
                        if (!feedDate.isEmpty()) {
                            feed_date.setVisibility(View.VISIBLE);
                            feed_date.setText(feedDate);
                        } else {
                            feed_date.setVisibility(View.GONE);
                        }
                        loadImageForImage();
                        action_feed_button.setVisibility(View.GONE);
                        public_video.setVisibility(View.GONE);
                        feed_image_thumbnail.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        feed_image.setVisibility(View.VISIBLE);
                    } else {
                        //TODO General Feed
                        if (!feedDate.isEmpty()) {
                            feed_date.setVisibility(View.VISIBLE);
                            feed_date.setText(feedDate);
                        } else {
                            feed_date.setVisibility(View.GONE);
                        }
                        loadImage();
                        feed_image.setVisibility(View.VISIBLE);
                        action_feed_button.setVisibility(View.GONE);
                        public_video.setVisibility(View.GONE);
                        feed_image_thumbnail.setVisibility(View.GONE);
                        feed_card_gif.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void loadImageForImage() {
        try {
            if (feed.getMediaType() != null && !feed.getMediaType().isEmpty()) {
                switch (feed.getMediaType()) {
                    case "IMAGE":
                        if (feed.getMediaData() != null && !feed.getMediaData().isEmpty()) {
                            if (feed.getMediaData().contains(AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL)) {
                                imageUrl = feed.getMediaData();
                            } else if (feed.getMediaData().contains(AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_HTTPS)) {
                                imageUrl = feed.getMediaData();
                            } else if (feed.getMediaData().contains(AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH)) {
                                imageUrl = feed.getMediaData();
                            } else {
                                imageUrl = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_HTTPS + "course/media/image/" + feed.getMediaData();
                            }
                        } else if (feed.getImageUrl() != null && !feed.getImageUrl().isEmpty()) {
                            imageUrl = feed.getImageUrl();
                        } else if (feed.getBannerImg() != null && !feed.getBannerImg().isEmpty()) {
                            imageUrl = feed.getBannerImg();
                        }
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(context)
                                    .asBitmap()
                                    .load(imageUrl)
                                    .placeholder(OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.feed_default)))
                                    .error(OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.feed_default)))
                                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                    .into(new CustomTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                            try {
                                                if (resource.getHeight() <= 2048 && resource.getWidth() <= 2219) {
                                                    feed_image.setImageBitmap(resource);
                                                    feed_image.buildDrawingCache();
                                                    Log.d("TAG", "onResourceReady: --loading Normal Image--");
                                                } else if (resource.getHeight() > 10240 || resource.getWidth() > 11095) {
                                                    Bitmap resize10KBitmap = resize(resource, resource.getWidth() / 5, resource.getHeight() / 5);
                                                    feed_image.setImageBitmap(resize10KBitmap);
                                                    feed_image.buildDrawingCache();
                                                    Log.d("TAG", "onResourceReady: --loading 10K Image--");
                                                } else if (resource.getHeight() > 8192 || resource.getWidth() > 8876) {
                                                    Bitmap resize8KBitmap = resize(resource, resource.getWidth() / 4, resource.getHeight() / 4);
                                                    feed_image.setImageBitmap(resize8KBitmap);
                                                    feed_image.buildDrawingCache();
                                                    Log.d("TAG", "onResourceReady: --loading 8K Image--");
                                                } else if (resource.getHeight() > 6144 || resource.getWidth() > 6657) {
                                                    Bitmap resize6KBitmap = resize(resource, resource.getWidth() / 3, resource.getHeight() / 3);
                                                    feed_image.setImageBitmap(resize6KBitmap);
                                                    feed_image.buildDrawingCache();
                                                    Log.d("TAG", "onResourceReady: --loading 6K Image--");
                                                } else if (resource.getHeight() > 4096 || resource.getWidth() > 4438) {
                                                    Bitmap resize4KBitmap = resize(resource, resource.getWidth() / 2, resource.getHeight() / 2);
                                                    feed_image.setImageBitmap(resize4KBitmap);
                                                    feed_image.buildDrawingCache();
                                                    Log.d("TAG", "onResourceReady: --loading 4K Image--");
                                                } else {
                                                    feed_image.setImageBitmap(resource);
                                                    feed_image.buildDrawingCache();
                                                    Log.d("TAG", "onResourceReady: --loading Normal Image--");
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onLoadCleared(@Nullable Drawable placeholder) {
                                        }
                                    });
                        }

                        feed_image.setVisibility(View.VISIBLE);
                        feed_card_gif.setVisibility(View.GONE);
                        break;
                    case "GIF":
                        if (feed.getMediaData() != null && !feed.getMediaData().isEmpty()) {
                            if (feed.getMediaData().contains(AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL)) {
                                gifImageUrl = feed.getMediaData();
                            } else if (feed.getMediaData().contains(AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_HTTPS)) {
                                gifImageUrl = feed.getMediaData();
                            } else if (feed.getMediaData().contains(AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH)) {
                                gifImageUrl = feed.getMediaData();
                            } else {
                                gifImageUrl = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_HTTPS + "course/media/gif/" + feed.getMediaData();
                            }
                        } else if (!feed.getImageUrl().isEmpty()) {
                            gifImageUrl = feed.getImageUrl();
                        }
                        if (gifImageUrl != null && !gifImageUrl.isEmpty()) {
                            Glide.with(context).load(gifImageUrl)
                                    .placeholder(OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.feed_default)))
                                    .error(OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.feed_default)))
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .into(feed_card_gif);
                        }
                        feed_card_gif.setVisibility(View.VISIBLE);
                        feed_image.setVisibility(View.GONE);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
        }
        return image;
    }

    private void loadImageForVideo() {
        try {
            if (feed.getMediaThumbnail() != null && !feed.getMediaThumbnail().isEmpty()) {
                imageUrl = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_HTTPS + "course/mpower/" + feed.getMediaThumbnail();
            } else if (feed.getImageUrl() != null && !feed.getImageUrl().isEmpty()) {
                imageUrl = feed.getImageUrl();
            } else if (feed.getBannerImg() != null && !feed.getBannerImg().isEmpty()) {
                imageUrl = feed.getBannerImg();
            }
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(context)
                        .asBitmap()
                        .load(imageUrl)
                        .placeholder(OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.feed_default)))
                        .error(OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.feed_default)))
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                try {
                                    if (resource.getHeight() <= 2048 && resource.getWidth() <= 2219) {
                                        feed_image_thumbnail.setImageBitmap(resource);
                                        feed_image_thumbnail.buildDrawingCache();
                                        Log.d("TAG", "onResourceReady: --loading Normal Image--");
                                    } else if (resource.getHeight() > 10240 || resource.getWidth() > 11095) {
                                        Bitmap resize10KBitmap = resize(resource, resource.getWidth() / 5, resource.getHeight() / 5);
                                        feed_image_thumbnail.setImageBitmap(resize10KBitmap);
                                        feed_image_thumbnail.buildDrawingCache();
                                        Log.d("TAG", "onResourceReady: --loading 10K Image--");
                                    } else if (resource.getHeight() > 8192 || resource.getWidth() > 8876) {
                                        Bitmap resize8KBitmap = resize(resource, resource.getWidth() / 4, resource.getHeight() / 4);
                                        feed_image_thumbnail.setImageBitmap(resize8KBitmap);
                                        feed_image_thumbnail.buildDrawingCache();
                                        Log.d("TAG", "onResourceReady: --loading 8K Image--");
                                    } else if (resource.getHeight() > 6144 || resource.getWidth() > 6657) {
                                        Bitmap resize6KBitmap = resize(resource, resource.getWidth() / 3, resource.getHeight() / 3);
                                        feed_image_thumbnail.setImageBitmap(resize6KBitmap);
                                        feed_image_thumbnail.buildDrawingCache();
                                        Log.d("TAG", "onResourceReady: --loading 6K Image--");
                                    } else if (resource.getHeight() > 4096 || resource.getWidth() > 4438) {
                                        Bitmap resize4KBitmap = resize(resource, resource.getWidth() / 2, resource.getHeight() / 2);
                                        feed_image_thumbnail.setImageBitmap(resize4KBitmap);
                                        feed_image_thumbnail.buildDrawingCache();
                                        Log.d("TAG", "onResourceReady: --loading 4K Image--");
                                    } else {
                                        feed_image_thumbnail.setImageBitmap(resource);
                                        feed_image_thumbnail.buildDrawingCache();
                                        Log.d("TAG", "onResourceReady: --loading Normal Image--");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void loadImage() {
        try {
            if (feed.getImageUrl() != null && !feed.getImageUrl().isEmpty()) {
                imageUrl = feed.getImageUrl();
            } else if (feed.getBannerImg() != null && !feed.getBannerImg().isEmpty()) {
                imageUrl = feed.getBannerImg();
            }
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(context)
                        .asBitmap()
                        .load(imageUrl)
                        .placeholder(OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.feed_default)))
                        .error(OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.feed_default)))
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                try {
                                    if (resource.getHeight() <= 2048 && resource.getWidth() <= 2219) {
                                        feed_image.setImageBitmap(resource);
                                        feed_image.buildDrawingCache();
                                        Log.d("TAG", "onResourceReady: --loading Normal Image--");
                                    } else if (resource.getHeight() > 10240 || resource.getWidth() > 11095) {
                                        Bitmap resize10KBitmap = resize(resource, resource.getWidth() / 5, resource.getHeight() / 5);
                                        feed_image.setImageBitmap(resize10KBitmap);
                                        feed_image.buildDrawingCache();
                                        Log.d("TAG", "onResourceReady: --loading 10K Image--");
                                    } else if (resource.getHeight() > 8192 || resource.getWidth() > 8876) {
                                        Bitmap resize8KBitmap = resize(resource, resource.getWidth() / 4, resource.getHeight() / 4);
                                        feed_image.setImageBitmap(resize8KBitmap);
                                        feed_image.buildDrawingCache();
                                        Log.d("TAG", "onResourceReady: --loading 8K Image--");
                                    } else if (resource.getHeight() > 6144 || resource.getWidth() > 6657) {
                                        Bitmap resize6KBitmap = resize(resource, resource.getWidth() / 3, resource.getHeight() / 3);
                                        feed_image.setImageBitmap(resize6KBitmap);
                                        feed_image.buildDrawingCache();
                                        Log.d("TAG", "onResourceReady: --loading 6K Image--");
                                    } else if (resource.getHeight() > 4096 || resource.getWidth() > 4438) {
                                        Bitmap resize4KBitmap = resize(resource, resource.getWidth() / 2, resource.getHeight() / 2);
                                        feed_image.setImageBitmap(resize4KBitmap);
                                        feed_image.buildDrawingCache();
                                        Log.d("TAG", "onResourceReady: --loading 4K Image--");
                                    } else {
                                        feed_image.setImageBitmap(resource);
                                        feed_image.buildDrawingCache();
                                        Log.d("TAG", "onResourceReady: --loading Normal Image--");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }
                        });
            } else {
                Glide.with(context)
                        .asBitmap()
                        .load(OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.feed_default)))
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
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void feedOnClick(HashMap<String, String> myDeskMap, DTOUserFeeds.FeedList feed, boolean clickedOnComment, boolean clickedOnAttach) {
        // TODO interface
        updateFeedViewed(feed);
        feedIndicator.setVisibility(View.GONE);
        OustStaticVariableHandling.getInstance().setClicked(true);
      /*  try {
            if (feedsRecyclerView != null) {
                feedsRecyclerView.resetVolume();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        Log.d("UserFeeds", "StandardViewHolder->" + feed.getFeedId());
        Intent intent = new Intent(context, FeedsAPICallingActivity.class);
        intent.putExtra("isComment", clickedOnComment);
        intent.putExtra("FeedAttach", clickedOnAttach);
        intent.putExtra("userFeed", feed);
        intent.putExtra("deskDataMap", myDeskMap);
        intent.putExtra("isMultipleCpl", isMultipleCpl);
        ((Activity) context).startActivityForResult(intent, 1444);
    }

    public int getFeedType(DTOUserFeeds.FeedList feed) {
        try {
            switch (feed.getFeedType()) {
                case "SURVEY":
                    return survey_feed;
                case "COURSE_UPDATE":
                    return course_feed;
                case "ASSESSMENT_PLAY":
                    return assessment_feed;
                case "COURSE_CARD_L":
                    if (feed.getMediaType() != null && !feed.getMediaType().isEmpty()) {
                        String mediaType = feed.getMediaType();
                        if (mediaType.equalsIgnoreCase("IMAGE") || mediaType.equalsIgnoreCase("GIF") || mediaType.equalsIgnoreCase("AUDIO")) {
                            return image_feed;
                        } else {
                            if (feed.getMediaPrivacy() != null && feed.getMediaPrivacy().equals("PUBLIC")) {
                                return youtube_video_feed;
                            } else {
                                return video_feed;
                            }
                        }
                    } else {
                        return general_feed;
                    }
                default:
                    return general_feed;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            return general_feed;
        }
    }

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

            try {
                GradientDrawable surveyButtonBg = (GradientDrawable) action_feed_button.getBackground();
                surveyButtonBg.setStroke(3, Color.parseColor(OustPreferences.get("toolbarColorCode")));
                action_feed_button.setBackground(surveyButtonBg);
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setFeedLike(DTOUserFeeds.FeedList feed, boolean isAnimation) {
        try {
            if (feed.getNumLikes() != 0) {
                feed_like.setText(OustSdkTools.formatMilliinFormat(feed.getNumLikes()));
            } else {
                feed_like.setText("0");
            }

            Drawable feedLikeDrawable = context.getResources().getDrawable(R.drawable.ic_like_common);

            if (feed.isLiked()) {
                feedLikeDrawable = context.getResources().getDrawable(R.drawable.ic_liked_common);
                feed_like_image.setImageDrawable(drawableColor(feedLikeDrawable));
            }
            feed_like_image.setImageDrawable(feedLikeDrawable);

            LikeButtonAnimation(isAnimation, feedLikeDrawable);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void feedLike(DTOUserFeeds.FeedList feed) {
        updateFeedViewed(feed);
        feedClicked(feed.getFeedId(), 0);
//        if (feed.isLikeble()) {
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
//        }
    }

    private void setUserLike(DTOUserFeeds.FeedList feed, final boolean value) {
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

    private void updateFeedViewed(DTOUserFeeds.FeedList mFeed) {
        try {
            if (!mFeed.isFeedViewed()) {
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

    private void LikeButtonAnimation(boolean animated, Drawable feedLikeDrawable) {
        if (animated) {
            feed_like_image.setImageDrawable(feedLikeDrawable);
        } else {
            feed_like_image.setImageDrawable(feedLikeDrawable);
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

    private void setUserShareCount(long feedId, DTOUserFeeds.FeedList feed) {

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
                    runOnUiThread(() -> feed_share.setText(OustSdkTools.returnValidString("" + feed.getNumShares())));

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

    public void playVideo(boolean isEndOfList) {
        try {
            int targetPosition;
            if (!isEndOfList) {
                int startPosition = ((LinearLayoutManager) Objects.requireNonNull(feedsRecyclerView.getLayoutManager())).findFirstVisibleItemPosition();
                int endPosition = ((LinearLayoutManager) feedsRecyclerView.getLayoutManager()).findLastVisibleItemPosition();

                // if there is more than 2 list-items on the screen, set the difference to be 1
                if (endPosition - startPosition > 1) {
                    endPosition = startPosition + 1;
                }

                // something is wrong. return.
                if (startPosition < 0 || endPosition < 0) {
                    return;
                }

                // if there is more than 1 list-item on the screen
                if (startPosition != endPosition) {
                    int startPositionVideoHeight = getVisibleVideoSurfaceHeight(startPosition);
                    int endPositionVideoHeight = getVisibleVideoSurfaceHeight(endPosition);

                    targetPosition = startPositionVideoHeight > endPositionVideoHeight ? startPosition : endPosition;
                } else {
                    targetPosition = startPosition;

                }
            } else {
                targetPosition = postDataList.size() - 1;
            }

            Log.d(TAG, "playVideo: target position: " + targetPosition);

            // video is already playing so return
            if ((targetPosition == playPosition)) {
                return;
            }

            // set the position of the list-item that is to be played
            playPosition = targetPosition;
            if (styledPlayerView == null) {
                return;
            }

            // remove any old surface views from previously playing videos
            styledPlayerView.setVisibility(View.INVISIBLE);
            removeVideoView(styledPlayerView);

            int currentPosition = targetPosition - ((LinearLayoutManager) Objects.requireNonNull(feedsRecyclerView.getLayoutManager())).findFirstVisibleItemPosition();
            if (postDataList.get(targetPosition).getMediaType() != null && (postDataList.get(targetPosition).getMediaType().equalsIgnoreCase("VIDEO") || postDataList.get(targetPosition).getMediaType().equalsIgnoreCase("YOUTUBE_VIDEO")) && (postDataList.get(targetPosition).getModuleType() != null && postDataList.get(targetPosition).getModuleType().equalsIgnoreCase("COURSE_CARD_L"))) {
                View child = feedsRecyclerView.getChildAt(currentPosition);
                if (child == null) {
                    return;
                }

                FeedsViewHolder holder = (FeedsViewHolder) child.getTag();
                if (holder == null) {
                    playPosition = -1;
                    return;
                }
                viewHolderParent = holder.itemView;
                progressBar = holder.progressBar;
                thumbnail = holder.feed_image_thumbnail;
//                audio_image = holder.audio_image;
                frameLayout = holder.itemView.findViewById(R.id.public_video);
                styledPlayerView.setPlayer(videoPlayer);
//                viewHolderParent.setOnClickListener(videoViewClickListener);
                progressBar.setVisibility(View.VISIBLE);
                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, new DefaultHttpDataSource.Factory().setUserAgent(Util.getUserAgent(context, "Feeds RecyclerView")));
//                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context);
//                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, "Feeds RecyclerView"));
//                DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, "Feeds RecyclerView"));
                String videoUrl = null;
                if (postDataList.get(targetPosition).getGumletVideoUrl() != null && !postDataList.get(targetPosition).getGumletVideoUrl().isEmpty()) {
                    videoUrl = postDataList.get(targetPosition).getGumletVideoUrl();
                    Log.d("GumletVideoUrlFeeds", videoUrl);
                } else if (postDataList.get(targetPosition).getMediaData() != null && !postDataList.get(targetPosition).getMediaData().isEmpty()) {
                    videoUrl = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_HTTPS + "course/media/video/" + postDataList.get(targetPosition).getMediaData();
                    Log.d("NONGumletVideoUrlFeeds", videoUrl);
                }
                if (videoUrl != null && !videoUrl.contains("youtube.com") && !videoUrl.contains("youtu.be")) {
                    frameLayout.setVisibility(View.VISIBLE);
                    styledPlayerView.findViewById(R.id.exo_content_frame).setVisibility(View.VISIBLE);
                    styledPlayerView.setBackgroundColor(Color.TRANSPARENT);
                    styledPlayerView.setShowNextButton(false);
                    styledPlayerView.setShowFastForwardButton(false);
                    styledPlayerView.setUseController(true);
                    styledPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT);
                    MediaItem.Builder builder = MediaItem.fromUri(videoUrl).buildUpon();
                    MediaItem mediaItem = builder.build();
                    MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem);
                    videoPlayer.setMediaSource(videoSource);
//                    videoPlayer.prepare();
                    videoPlayer.prepare(videoSource);
                    videoPlayer.setPlayWhenReady(true);
                } else {
                    if (postDataList.get(targetPosition).getMediaType() != null && postDataList.get(targetPosition).getMediaType().equalsIgnoreCase("YOUTUBE_VIDEO")) {
                        new YouTubeExtractor(context) {
                            @Override
                            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                                if (ytFiles != null) {
                                    Log.d(TAG, "YT FILES: " + ytFiles);
                                    String videoUrl = null;
//                                    String audioUrl = null;
                                    try {
                                        if (ytFiles.get(160) != null && ytFiles.get(160).getUrl() != null) {
                                            videoUrl = ytFiles.get(160).getUrl();
//                                            audioUrl = ytFiles.get(141).getUrl();
                                        } else if (ytFiles.get(278) != null && ytFiles.get(278).getUrl() != null) {
                                            videoUrl = ytFiles.get(278).getUrl();
//                                            audioUrl = ytFiles.get(250).getUrl();
                                        } else if (ytFiles.get(242) != null && ytFiles.get(242).getUrl() != null) {
                                            videoUrl = ytFiles.get(242).getUrl();
//                                            audioUrl = ytFiles.get(250).getUrl();
                                        } else if (ytFiles.get(133) != null && ytFiles.get(133).getUrl() != null) {
                                            videoUrl = ytFiles.get(133).getUrl();
//                                            audioUrl = ytFiles.get(141).getUrl();
                                        } else if (ytFiles.get(134) != null && ytFiles.get(134).getUrl() != null) {
                                            videoUrl = ytFiles.get(134).getUrl();
//                                            audioUrl = ytFiles.get(141).getUrl();
                                        } else if (ytFiles.get(18) != null && ytFiles.get(18).getUrl() != null) {
                                            videoUrl = ytFiles.get(18).getUrl();
                                        } else if (ytFiles.get(22) != null && ytFiles.get(22).getUrl() != null) {
                                            videoUrl = ytFiles.get(22).getUrl();
                                        } else if (ytFiles.get(43) != null && ytFiles.get(43).getUrl() != null) {
                                            videoUrl = ytFiles.get(43).getUrl();
                                        } else if (ytFiles.get(134) != null && ytFiles.get(140) != null && ytFiles.get(134).getUrl() != null && ytFiles.get(140).getUrl() != null) {
                                            videoUrl = ytFiles.get(134).getUrl();
//                                            audioUrl = ytFiles.get(140).getUrl();
                                        } else if (ytFiles.get(243) != null && ytFiles.get(249) != null && ytFiles.get(243).getUrl() != null && ytFiles.get(249).getUrl() != null) {
                                            videoUrl = ytFiles.get(243).getUrl();
//                                            audioUrl = ytFiles.get(249).getUrl();
                                        } else if (ytFiles.get(243) != null && ytFiles.get(250) != null && ytFiles.get(243).getUrl() != null && ytFiles.get(250).getUrl() != null) {
                                            videoUrl = ytFiles.get(243).getUrl();
//                                            audioUrl = ytFiles.get(250).getUrl();
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        frameLayout.setVisibility(View.VISIBLE);
                                        styledPlayerView.setShowNextButton(false);
                                        styledPlayerView.setShowFastForwardButton(false);
                                        styledPlayerView.findViewById(R.id.exo_content_frame).setVisibility(View.VISIBLE);
                                        styledPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
                                        MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(videoUrl));
                                        videoPlayer.setMediaSource(videoSource);
                                        videoPlayer.prepare(videoSource);
                                        videoPlayer.setPlayWhenReady(true);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }.extract(postDataList.get(targetPosition).getMediaData());
                    } else {
                        frameLayout.setVisibility(View.GONE);
                        thumbnail.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        Log.d(TAG, "loading the thumbnail-->");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeVideoView(StyledPlayerView videoView) {
        videoPlayer.setVolume(0f);
        ViewGroup parent = (ViewGroup) videoView.getParent();
        if (parent == null) {
            return;
        }
        int index = parent.indexOfChild(videoView);
        if (index >= 0) {
            parent.removeViewAt(index);
            isVideoViewAdded = false;
            viewHolderParent.setOnClickListener(null);
        }
//        videoPlayer.setPlayWhenReady(false);
        thumbnail.setVisibility(View.VISIBLE);
    }

    private void addVideoView() {
        frameLayout.addView(styledPlayerView);
        isVideoViewAdded = true;
        styledPlayerView.requestFocus();
        styledPlayerView.setVisibility(View.VISIBLE);
        styledPlayerView.setAlpha(1);
        progressBar.setVisibility(View.GONE);
        thumbnail.setVisibility(View.GONE);
        videoPlayer.setVolume(0f); // Todo to set the volume change 0f to 1f
//        frameLayout.setBackground(null);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void resetVideoView() {
        if (isVideoViewAdded) {
            removeVideoView(styledPlayerView);
            playPosition = -1;
            styledPlayerView.setVisibility(View.INVISIBLE);
            videoPlayer.setVolume(0f);
            thumbnail.setVisibility(View.VISIBLE);
//            frameLayout.setBackground(getResources().getDrawable(R.drawable.default_learning_card));
        }
    }

    public void releasePlayer() {
        try {
            if (videoPlayer != null) {
                videoPlayer.setVolume(0f);
                videoPlayer.release();
                videoPlayer = null;
            }
            viewHolderParent = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetVolume() {
        if (videoPlayer != null) {
//            videoPlayer.setVolume(0f);
            videoPlayer.pause();
        }
    }

    public int getVisibleVideoSurfaceHeight(int playPosition) {
        int at = playPosition - ((LinearLayoutManager) feedsRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        Log.d(TAG, "getVisibleVideoSurfaceHeight: at: " + at);

        View child = feedsRecyclerView.getChildAt(at);
        if (child == null) {
            return 0;
        }

        int[] location = new int[2];
        child.getLocationInWindow(location);

        if (location[1] < 0) {
            return location[1] + videoSurfaceDefaultHeight;
        } else {
            return screenDefaultHeight - location[1];
        }
    }
}
