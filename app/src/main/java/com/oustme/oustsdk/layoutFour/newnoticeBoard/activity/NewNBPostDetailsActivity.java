package com.oustme.oustsdk.layoutFour.newnoticeBoard.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.noticeBoard.extras.ClickState;
import com.oustme.oustsdk.base.BaseActivity;
import com.oustme.oustsdk.customviews.CustomExoPlayerView;
import com.oustme.oustsdk.customviews.CustomSearchView;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.adapters.NewNBPostCommentAdapter;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.callBacks.NewNBDeleteListener;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.callBacks.NewNoticeBoardCommentDeleteListener;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.data.handlers.NewSubmitNBPostService;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.dialogs.NewCommentDeleteConfirmationPopup;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.request.NewPostViewData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBCommentData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBPostData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.presenters.NewNBPostDetailsPresenter;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.view.NewNBPostDetailsView;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustShareTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.text.WordUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class NewNBPostDetailsActivity extends BaseActivity implements NewNBPostDetailsView, View.OnClickListener,
        NewNBDeleteListener, NewNoticeBoardCommentDeleteListener, SearchView.OnQueryTextListener {

    private static final String TAG = "NewNBPostDetailsActi";
    private ImageView post_img, author_img, imgLike, imgComment, imgShare, play_icon,
            send_attachment, send_audio, send_imgview, closeBtn;
    private LinearLayout post_attchment_ll, shareable_lay, like_ll, comment_ll, share_ll, play_soundbtn, post_audio_ll, play_video_btn;
    private TextView authorNameTv, posted_on_tv, authorDesignationTv, post_title, post_desciption,
            attachment_name, attachment_size, like_count, comment_count, share_count, audio_time, posted_in, no_data, designation, userRole;
    //    private View attachment_ul, attachment_ul_2;
    private RelativeLayout player_view;
    private EditText comments_et;
    private RelativeLayout send_ll;
    private CardView post_img_ll;
    private RecyclerView post_comment_rv;
    private int toolbarColor = 0;
    private NewNBPostDetailsPresenter mPresenter;
    private boolean isClicked = false;
    private NewNBPostCommentAdapter nbPostCommentAdapter;
    private SimpleExoPlayer exoPlayer;
    private boolean autoPlayVideo, autoPlayAudio;
    private GifImageView mGifImageViewDownload;
    private ImageView mImageViewAttachmentDownloadIcon;
    private boolean isAttachmetnDownLoad;
    int PERMISSION_ALL = 1;
    private long videoTime = 0;
    String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private boolean isAudioPause = true;
    private boolean isDownLoadingAttachment;

    private MediaPlayer mediaPlayer;

    private SeekBar seekbar;
    Handler mHandler;
    Runnable myRunnable;

    private MenuItem actionSearch, add;
    private View searchPlate;
    private CustomSearchView newSearchView;
    private int color;
    private int bgColor;
    Toolbar toolbar;
    ImageView back_button;
    PopupWindow zoomImagePopup;
    List<NewNBCommentData> newNBCommentData1;
    NewNBCommentData nbCommentDataList1;
    NestedScrollView nested_view;
    GifImageView notification_gif_loader;
    RelativeLayout send_main_ll;
    LinearLayout comment_layout;
    long checkCommentedOn;
    boolean sendButtonClicked = false;
    NewNBPostData nbPostData;

    @Override
    protected int getContentView() {
        Log.d(TAG, "getContentView: ");
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
        OustSdkTools.setLocale(NewNBPostDetailsActivity.this);
        return R.layout.activity_nbpost_details_2;
    }

    @Override
    protected void initView() {
        getColors();
        Log.d(TAG, "initView: ");
        seekbar = findViewById(R.id.download_progressbar);
        post_img = findViewById(R.id.post_img);
        play_icon = findViewById(R.id.play_icon);
        author_img = findViewById(R.id.author_img);
        imgLike = findViewById(R.id.imgLike);
        imgComment = findViewById(R.id.imgComment);
        imgShare = findViewById(R.id.imgShare);
        send_attachment = findViewById(R.id.send_attachment);
        send_audio = findViewById(R.id.send_audio);
        send_imgview = findViewById(R.id.send_imgview);
        closeBtn = findViewById(R.id.closeBtn);
        post_img_ll = findViewById(R.id.post_img_ll);
        post_attchment_ll = findViewById(R.id.post_attchment_ll);
        post_attchment_ll.setClickable(false);
        post_attchment_ll.setEnabled(false);
        like_ll = findViewById(R.id.like_ll);
        comment_ll = findViewById(R.id.comment_ll);
        share_ll = findViewById(R.id.share_ll);
        post_audio_ll = findViewById(R.id.post_audio_ll);
        play_video_btn = findViewById(R.id.play_video_btn);
        play_soundbtn = findViewById(R.id.play_soundbtn);
        authorNameTv = findViewById(R.id.authorNameTv);
        posted_on_tv = findViewById(R.id.posted_on_tv);
        authorDesignationTv = findViewById(R.id.authorDesignationTv);
        post_title = findViewById(R.id.post_title);
        post_desciption = findViewById(R.id.post_desciption);
        attachment_name = findViewById(R.id.attachment_name);
        attachment_size = findViewById(R.id.attachment_size);
        like_count = findViewById(R.id.like_count);
        comment_count = findViewById(R.id.comment_count);
        share_count = findViewById(R.id.share_count);
        audio_time = findViewById(R.id.audio_time);
        posted_in = findViewById(R.id.posted_in);
        nested_view = findViewById(R.id.nested_view);
        notification_gif_loader = findViewById(R.id.notification_gif_loader);
        no_data = findViewById(R.id.no_data);

        designation = findViewById(R.id.designation);
        userRole = findViewById(R.id.userRole);
        send_main_ll = findViewById(R.id.send_main_ll);
        comment_layout = findViewById(R.id.comment_layout);

        comments_et = findViewById(R.id.comments_et);
        send_ll = findViewById(R.id.send_ll);
        shareable_lay = findViewById(R.id.shareable_lay);
        post_comment_rv = findViewById(R.id.post_comment_rv);
        mGifImageViewDownload = findViewById(R.id.attachment_download_gif);
        mImageViewAttachmentDownloadIcon = findViewById(R.id.attachment_download_icon);

        player_view = findViewById(R.id.player_view);
        toolbar = findViewById(R.id.toolbar_notification_layout);
        back_button = findViewById(R.id.back_button);

        try {
            isAttachmetnDownLoad = getIntent().getBooleanExtra("downloadAttachment", false);
            if (isAttachmetnDownLoad) {
                isDownLoadingAttachment = true;
                post_attchment_ll.setClickable(false);
                post_attchment_ll.setEnabled(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        comments_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    send_imgview.setColorFilter(toolbarColor);
                } else {
                    send_imgview.setColorFilter(OustSdkApplication.getContext().getResources().getColor(R.color.common_grey));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        setDefaultColor();

        like_ll.setOnClickListener(this);
        share_ll.setOnClickListener(this);
        send_ll.setOnClickListener(this);
        play_video_btn.setOnClickListener(this);
        closeBtn.setOnClickListener(this);
        play_soundbtn.setOnClickListener(this);
        post_attchment_ll.setOnClickListener(this);
        setToolbar();

        if (!OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_NB_POST_COMMENT)) {
            send_main_ll.setVisibility(View.GONE);
            comment_ll.setVisibility(View.GONE);
        } else {
            send_main_ll.setVisibility(View.VISIBLE);
            comment_ll.setVisibility(View.VISIBLE);
        }
        if (!OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_NB_POST_LIKE)) {
            like_ll.setVisibility(View.GONE);
        } else {
            like_ll.setVisibility(View.VISIBLE);
        }
        if (!OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_NB_POST_SHARE)) {
            share_ll.setVisibility(View.GONE);
        } else {
            share_ll.setVisibility(View.VISIBLE);
        }
        if (nbCommentDataList1 != null) {
            nbCommentDataList1 = null;
        }
    }

    private void getColors() {
        try {
            if (OustPreferences.getAppInstallVariable("isLayout4")) {
                color = OustResourceUtils.getColors();
                bgColor = OustResourceUtils.getToolBarBgColor();
            } else {
                bgColor = OustResourceUtils.getColors();
                color = OustResourceUtils.getToolBarBgColor();

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setToolbar() {
        try {
            toolbar.setBackgroundColor(Color.WHITE);
            OustResourceUtils.setDefaultDrawableColor(back_button.getDrawable(), color);
            toolbar.setTitle("");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                toolbar.setElevation(0f);
            }
            setSupportActionBar(toolbar);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setDefaultColor() {
        try {
            String toolbarColorCode = OustPreferences.get("toolbarColorCode");
            if ((toolbarColorCode != null) && (!toolbarColorCode.isEmpty())) {
                toolbarColor = Color.parseColor(toolbarColorCode);
            } else {
                toolbarColor = context.getResources().getColor(R.color.lgreen);
            }
            comments_et.setText("");
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void initData() {
        try {
            mPresenter = new NewNBPostDetailsPresenter(this);
            Log.d(TAG, "mPresenter: " + mPresenter);
            long postId = getIntent().getLongExtra("postId", 0);
            Log.e("Notification", "NewLanding " + postId);
            autoPlayAudio = getIntent().getBooleanExtra("autoPlayAudio", false);
            autoPlayVideo = getIntent().getBooleanExtra("autoPlayVideo", false);
            mPresenter.getPostData(postId);
            GifDrawable gifFromResource = new GifDrawable(getResources(), R.drawable.white_to_green_new);
            mGifImageViewDownload.setImageDrawable(gifFromResource);
            //            mPresenter.getNextData(nbId, groupId);
            if (OustPreferences.get("saveTitle") != null) {
                posted_in.setText("Posted in '" + OustPreferences.get("saveTitle") + "'");
            } else {
                posted_in.setVisibility(View.GONE);
//                posted_in.setText("Posted in 'Notice Board'");
            }
            nested_view.setVisibility(View.VISIBLE);
            notification_gif_loader.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void initListener() {
        post_img.setOnClickListener(v -> gifZoomPopup(post_img.getDrawable()));

        back_button.setOnClickListener(v -> onBackPressed());

    }

    public void closePopWindow() {
        try {
            if (zoomImagePopup != null && zoomImagePopup.isShowing())
                zoomImagePopup.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void gifZoomPopup(final Drawable gif) {
        try {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams")
            View popUpView = inflater.inflate(R.layout.gifzoom_popup, null);
            zoomImagePopup = OustSdkTools.createPopUp(popUpView);
            GifImageView gifImageView = popUpView.findViewById(R.id.mainzooming_img);
            gifImageView.setImageDrawable(gif);
            ImageButton imageCloseButton = popUpView.findViewById(R.id.zooming_imgclose_btn);
            final RelativeLayout zoomLayout = popUpView.findViewById(R.id.mainzoomimg_layout);
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(zoomLayout, "scaleX", 0.0f, 1);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(zoomLayout, "scaleY", 0.0f, 1);
            scaleDownX.setDuration(400);
            scaleDownY.setDuration(400);
            scaleDownX.setInterpolator(new DecelerateInterpolator());
            scaleDownY.setInterpolator(new DecelerateInterpolator());
            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleDownX).with(scaleDownY);
            scaleDown.start();
            zoomLayout.setVisibility(View.VISIBLE);
            zoomImagePopup.setOnDismissListener(zoomImagePopup::dismiss);
            imageCloseButton.setOnClickListener(view -> {
                ObjectAnimator scaleDownX1 = ObjectAnimator.ofFloat(zoomLayout, "scaleX", 1.0f, 0);
                ObjectAnimator scaleDownY1 = ObjectAnimator.ofFloat(zoomLayout, "scaleY", 1.0f, 0);
                scaleDownX1.setDuration(350);
                scaleDownY1.setDuration(350);
                scaleDownX1.setInterpolator(new DecelerateInterpolator());
                scaleDownY1.setInterpolator(new DecelerateInterpolator());
                AnimatorSet scaleDown1 = new AnimatorSet();
                scaleDown1.play(scaleDownX1).with(scaleDownY1);
                scaleDown1.start();
                scaleDown1.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        try {
                            if (zoomImagePopup.isShowing()) {
                                zoomImagePopup.dismiss();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                });
            });
            popUpView.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    ObjectAnimator scaleDownX12 = ObjectAnimator.ofFloat(zoomLayout, "scaleX", 1.0f, 0);
                    ObjectAnimator scaleDownY12 = ObjectAnimator.ofFloat(zoomLayout, "scaleY", 1.0f, 0);
                    scaleDownX12.setDuration(350);
                    scaleDownY12.setDuration(350);
                    scaleDownX12.setInterpolator(new DecelerateInterpolator());
                    scaleDownY12.setInterpolator(new DecelerateInterpolator());
                    AnimatorSet scaleDown12 = new AnimatorSet();
                    scaleDown12.play(scaleDownX12).with(scaleDownY12);
                    scaleDown12.start();
                    scaleDown12.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            if (zoomImagePopup.isShowing()) {
                                zoomImagePopup.dismiss();
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {
                        }
                    });
                    return true;
                }
                return false;
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nbPostCommentAdapter != null) {
            mPresenter.updateData();
        }
    }

    @Override
    public void setLikeStatus(boolean isTrue, long count) {
        setPostImgTintColor(isTrue, imgLike, like_count, R.drawable.ic_like, R.drawable.ic_likes);
        if (count == 0) {
            like_count.setText("");
        } else {
            like_count.setText("" + count);
        }
    }

    @Override
    public void setCommentStatus(boolean isTrue, long count) {
        setPostImgTintColor(isTrue, imgComment, comment_count, R.drawable.ic_comment, R.drawable.ic_comments_new);
        if (count == 0) {
            comment_count.setText("");
        } else {
            comment_count.setText("" + count);
        }
    }

    @Override
    public void setShareStatus(boolean isTrue, long count) {
        setPostImgTintColor(isTrue, imgShare, share_count, R.drawable.ic_share_new, R.drawable.ic_shares);
        if (count == 0) {
            share_count.setText("");
        } else {
            share_count.setText("" + count);
        }
    }

    @Override
    public void setBannerBg(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(post_img);
        }
//        else {
//            post_img_ll.setVisibility(View.GONE);
//        }
    }

    @Override
    public void setAudioView(boolean isTrue) {
        if (isTrue) {
            post_audio_ll.setVisibility(View.VISIBLE);
            if (mPresenter != null) {
                mPresenter.getAudioData();
            }
        } else {
            post_audio_ll.setVisibility(View.GONE);
        }
    }

    @Override
    public void setVideoView(boolean isTrue) {
        if (isTrue) {
            if (customExoPlayerView == null) {
                play_video_btn.setVisibility(View.VISIBLE);
            }
        } else {
            //simpleExoPlayerView.setVisibility(View.GONE);
            play_video_btn.setVisibility(View.GONE);
        }
    }

    @Override
    public void setAttachmentView(boolean isTrue) {
        if (isTrue) {
            post_attchment_ll.setVisibility(View.GONE);
        } else {
            post_attchment_ll.setVisibility(View.VISIBLE);
            post_attchment_ll.setEnabled(true);
            post_attchment_ll.setClickable(true);
//            attachment_ul.setVisibility(View.GONE);
        }
    }

    @Override
    public void setPostCreatorDetails(String createdBy, String createdOn, String assigneeRole, String designation1, String userRole1, String avatar1, NewNBPostData nbPostData) {
        try {
            this.nbPostData = nbPostData;
            authorNameTv.setText(WordUtils.capitalize(createdBy));
            posted_on_tv.setText(OustSdkTools.getDate(createdOn));
            if (designation1 != null && designation1.equals("null")) {
                designation.setVisibility(View.VISIBLE);
                designation.setText(userRole1 + "  ");
            } else {
                designation.setVisibility(View.GONE);
            }
            if (userRole1 != null && userRole1.equals("null")) {
                userRole.setVisibility(View.VISIBLE);
                userRole.setText(designation1);
            } else {
                userRole.setVisibility(View.GONE);
            }
            if (avatar1 != null) {
                Picasso.get().load(avatar1)
                        .placeholder(R.drawable.ic_person_profile_image_nb).error(R.drawable.ic_person_profile_image_nb).into(author_img);
            } else {
                Picasso.get().load(R.drawable.ic_person_profile_image_nb)
                        .placeholder(R.drawable.ic_person_profile_image_nb).error(R.drawable.ic_person_profile_image_nb).into(author_img);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void setPostDetails(String title, String description) {
        if (title != null) {
            post_title.setText(title);
        }
        if (description != null) {
            post_desciption.setText(description);
        }
    }

    @Override
    public void startApiCalls() {
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, NewSubmitNBPostService.class);
        OustSdkApplication.getContext().startService(intent);
    }

    @Override
    public void setOrUpdateAdapter(List<NewNBCommentData> nbCommentDataList, long nbId, long postId) {
        if (nbCommentDataList != null && nbCommentDataList.size() > 0) {
            nbCommentDataList1 = nbCommentDataList.get(0);
            if (nbPostCommentAdapter == null) {
                newNBCommentData1 = nbCommentDataList;
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(NewNBPostDetailsActivity.this, LinearLayoutManager.VERTICAL, false);
                post_comment_rv.setLayoutManager(mLayoutManager);
                nbPostCommentAdapter = new NewNBPostCommentAdapter(NewNBPostDetailsActivity.this, nbCommentDataList, NewNBPostDetailsActivity.this);
                nbPostCommentAdapter.setPostParams(nbId, postId);
                post_comment_rv.setAdapter(null);
                post_comment_rv.setAdapter(nbPostCommentAdapter);
            } else {
                nbPostCommentAdapter.notifyListChnage(nbCommentDataList);
            }
        }
        if (OustSdkTools.hasPermissions(NewNBPostDetailsActivity.this, PERMISSIONS)) {
            if (isAttachmetnDownLoad) {
                isDownLoadingAttachment = true;
                post_attchment_ll.setEnabled(false);
                post_attchment_ll.setClickable(false);
                mPresenter.downLoadOrOpenFile();
            }
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    @Override
    public void checkPermissions() {
        if (OustSdkTools.hasPermissions(NewNBPostDetailsActivity.this, PERMISSIONS)) {

        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {
            if (requestCode == PERMISSION_ALL) {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                    }
                }
                isDownLoadingAttachment = true;
                post_attchment_ll.setEnabled(false);
                post_attchment_ll.setClickable(false);
                mPresenter.downLoadOrOpenFile();

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void playAudio(String audio) {
        try {
            setUpAudio(audio);
            //prepareExoPlayerFromUri(Uri.parse(audio));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void addVideoView(String video, long seekTo) {
        initializeVideo(video, seekTo);
    }

    @Override
    public void addAudioView(String audio, long seekTo) {

    }

    protected void initializeVideo(String video, long seekTo) {

        Log.d("initializeVideo", " initializeVideo" + seekTo);

        if (customExoPlayerView != null)
            customExoPlayerView.removeVideoPlayer();

        customExoPlayerView = new CustomExoPlayerView() {
            @Override
            public void onAudioComplete() {

            }

            @Override
            public void onVideoComplete() {
            }

            @Override
            public void onBuffering() {
                //video_loader.setVisibility(View.VISIBLE);
            }

            @Override
            public void onVideoError() {
                //video_loader.setVisibility(View.GONE);
            }

            @Override
            public void onPlayReady() {
                //video_loader.setVisibility(View.GONE);
            }
        };
        player_view.setVisibility(View.VISIBLE);
        customExoPlayerView.initExoPlayer(player_view, this, video);
        customExoPlayerView.setVideoCompleted(true);
        customExoPlayerView.getSimpleExoPlayer().seekTo(seekTo);
    }

    @Override
    public void startVideoPlayer(String video, long seekTo) {
        if (autoPlayVideo) {
            post_img.setVisibility(View.GONE);
            play_video_btn.setVisibility(View.GONE);
            initializeVideo(video, seekTo);
        }
    }

    @Override
    public void openAttachment(Intent intent) {
        try {
            if (intent != null) {
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onErrorWhileOpeningFile() {
        Toast.makeText(NewNBPostDetailsActivity.this, "No application found which can open the file", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void startSharingScreen(String title, String description, NewPostViewData postViewData) {
        if (ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(NewNBPostDetailsActivity.this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 123);
        } else {
            OustShareTools.share(NewNBPostDetailsActivity.this, OustSdkTools.getInstance().getScreenShot(shareable_lay), " " + title + "/n" + description);
            mPresenter.addToActionTask(postViewData);
        }
    }

    @Override
    public void setAttachmentDetails(String fileName, String fileSize, String fileType, boolean fileDownloaded) {
        if (fileName != null)
            attachment_name.setText(fileName);
        if (fileSize != null) {
            attachment_size.setText(fileSize);
        }
        if (fileDownloaded) {
            mImageViewAttachmentDownloadIcon.setColorFilter(toolbarColor);
        } else {
            Log.d(TAG, "setAttachmentDetails: not downloaded");
        }
    }

    @Override
    public void attachmentDownloadError(String message) {
        try {
            mGifImageViewDownload.setVisibility(View.GONE);
            mImageViewAttachmentDownloadIcon.setVisibility(View.VISIBLE);
            isAttachmetnDownLoad = false;
            OustSdkTools.showToast(message);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void attachmentDownloadStarted() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    isDownLoadingAttachment = true;
                    mGifImageViewDownload.setVisibility(View.VISIBLE);
                    mImageViewAttachmentDownloadIcon.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }
        });

    }

    @Override
    public void attachmentDownloadCompleted() {
        mGifImageViewDownload.setVisibility(View.GONE);
        mImageViewAttachmentDownloadIcon.setVisibility(View.VISIBLE);
        mImageViewAttachmentDownloadIcon.setColorFilter(toolbarColor);
        isAttachmetnDownLoad = false;
    }

    @Override
    public void enableDisableAttachmentClick(boolean isDownloadingFile) {
        isDownLoadingAttachment = isDownloadingFile;
        if (isDownloadingFile) {
            post_attchment_ll.setEnabled(false);
            post_attchment_ll.setClickable(false);
        } else {
            post_attchment_ll.setEnabled(true);
            post_attchment_ll.setClickable(true);
        }
    }

    private void setPostImgTintColor(boolean change, ImageView img, TextView tv, int ic_no_select, int ic_select) {
        try {
            if (change) {
                if (ic_select != 0)
                    img.setImageResource(ic_select);
                if (toolbarColor != 0) {
                    if (img != null)
                        img.setColorFilter(toolbarColor);
                    if (tv != null)
                        tv.setTextColor(toolbarColor);
                }
            } else {
                if (ic_no_select != 0) {
                    if (img != null)
                        img.setImageResource(ic_no_select);
                    if (tv != null)
                        tv.setTextColor(OustSdkApplication.getContext().getResources().getColor(R.color.common_grey));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.like_ll) {
            clickView(v, "", ClickState.LIKE);
        } else if (id == R.id.send_ll) {
            sendButtonClicked = true;
            comments_et.setError(null);
            String message = comments_et.getText().toString();
            if (!message.trim().isEmpty()) {
                comments_et.setText("");
                hideKeyboard();
                clickView(v, message.trim(), ClickState.SEND_COMMENT);
            } else {
                comments_et.setError(context.getResources().getString(R.string.type_comment_post));
            }
        } else if (id == R.id.share_ll) {
            clickView(v, "", ClickState.SHARE);
        } else if (id == R.id.closeBtn) {
            clickView(v, "", ClickState.CLOSE);
        } else if (id == R.id.play_soundbtn) {
            clickView(v, "", ClickState.PLAY_AUDIO);
        } else if (id == R.id.play_video_btn) {
            post_img.setVisibility(View.GONE);
            play_video_btn.setVisibility(View.GONE);
            mPresenter.playVideoPlayer();
        } else if (id == R.id.post_attchment_ll) {
            if (isDownLoadingAttachment) {
                OustSdkTools.showToast("Please wait file is downloading.");
            } else {
                isDownLoadingAttachment = true;
                post_attchment_ll.setEnabled(false);
                post_attchment_ll.setClickable(false);
                mPresenter.downLoadOrOpenFile();
            }
        }
    }

    public void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(comments_et.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private CustomExoPlayerView customExoPlayerView;


    private void clickView(View view, final String message, final ClickState clickState) {
        try {
            if (!isClicked) {
                isClicked = true;
                ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 0.94f);
                ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 0.96f);
                scaleDownX.setDuration(200);
                scaleDownY.setDuration(200);
                scaleDownX.setRepeatCount(1);
                scaleDownY.setRepeatCount(1);
                scaleDownX.setRepeatMode(ValueAnimator.REVERSE);
                scaleDownY.setRepeatMode(ValueAnimator.REVERSE);
                scaleDownX.setInterpolator(new DecelerateInterpolator());
                scaleDownY.setInterpolator(new DecelerateInterpolator());
                AnimatorSet scaleDown = new AnimatorSet();
                scaleDown.play(scaleDownX).with(scaleDownY);
                scaleDown.start();
                scaleDown.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        isClicked = false;
                        if (clickState == ClickState.LIKE) {
                            mPresenter.sendPostLikeData();
                        } else if (clickState == ClickState.SEND_COMMENT) {
                            mPresenter.sendPostCommentData(message);
                        } else if (clickState == ClickState.SHARE) {
                            mPresenter.sendPostShareData();
                        } else if (clickState == ClickState.PLAY_AUDIO) {
                            playMe();
                        } else if (clickState == ClickState.CLOSE) {
                            finish();
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void prepareExoPlayerFromUri(Uri uri) {
        try {
            resetExoPlayer();
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();

            exoPlayer = new SimpleExoPlayer.Builder(NewNBPostDetailsActivity.this).build();

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.CONTENT_TYPE_MOVIE)
                    .build();

            exoPlayer.setAudioAttributes(audioAttributes, true);


            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "exoplayer2example"), null);
            //videoSource = new ExtractorMediaSource(videoUri, defaultHttpDataSourceFactory, extractorsFactory, null, null);
            MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uri));

            exoPlayer.addListener(eventListener);
            exoPlayer.setMediaSource(videoSource);
            exoPlayer.prepare();

            exoPlayer.setPlayWhenReady(true);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void resetExoPlayer() {
        try {
            if (exoPlayer != null) {
                exoPlayer.removeListener(eventListener);
                exoPlayer.stop();
                exoPlayer.release();
                exoPlayer = null;
            }
            if (customExoPlayerView != null) {
                customExoPlayerView.removeVideoPlayer();
                customExoPlayerView = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.alert_menu_2, menu);
            actionSearch = menu.findItem(R.id.action_search);
            Drawable searchDrawable = getResources().getDrawable(R.drawable.search);
            actionSearch.setIcon(OustResourceUtils.setDefaultDrawableColor(searchDrawable, color));

            add = menu.findItem(R.id.action_add);
            Drawable addDrawable = getResources().getDrawable(R.drawable.ic_add_nb);
            add.setIcon(OustResourceUtils.setDefaultDrawableColor(addDrawable, color));

            //newSearchView = (CustomSearchView) MenuItemCompat.getActionView(actionSearch);
            newSearchView = (CustomSearchView) actionSearch.getActionView();

            newSearchView.setOnQueryTextListener(this);
            newSearchView.setQueryHint(getResources().getString(R.string.search_text));
            newSearchView.setVisibility(View.VISIBLE);
            newSearchView.requestFocusFromTouch();
            View searchPlate = newSearchView.findViewById(R.id.search_plate);
            searchPlate.setBackgroundColor(OustSdkTools.getColorBack(R.color.black_semi_transparent));
            showSearchIcon(true);
            showCreateIcon(false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void showSearchIcon(boolean visible) {
        try {
            if (visible) {
                actionSearch.setVisible(true);
            } else {
                actionSearch.setVisible(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void showCreateIcon(boolean visible) {
        try {
            if (visible) {
                add.setVisible(true);
            } else {
                add.setVisible(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_search) {
            actionSearch.setVisible(true);
            searchPlate = newSearchView.findViewById(R.id.search_plate);
            searchPlate.setBackgroundColor(OustSdkTools.getColorBack(R.color.black_semi_transparent));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    //private String TAG="exoplayer";
    private ExoPlayer.EventListener eventListener = new ExoPlayer.EventListener() {


        @Override
        public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            Log.i(TAG, "onTracksChanged");
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            Log.i(TAG, "onLoadingChanged");
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            Log.i(TAG, "onPlayerStateChanged: playWhenReady = " + playWhenReady
                    + " playbackState = " + playbackState);
            switch (playbackState) {
                case ExoPlayer.STATE_ENDED:
                    Log.i(TAG, "Playback ended!");
                    if (exoPlayer != null)
                        exoPlayer.seekTo(0);
                    setPostImgTintColor(true, play_icon, null, R.drawable.ic_pause_new, R.drawable.ic_play_without_circle);
                    break;
                case ExoPlayer.STATE_READY:
                    String time = String.format("%02d:%02d:%02d",
                            TimeUnit.MILLISECONDS.toHours(exoPlayer.getDuration()),
                            TimeUnit.MILLISECONDS.toMinutes(exoPlayer.getDuration()) -
                                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(exoPlayer.getDuration())),
                            TimeUnit.MILLISECONDS.toSeconds(exoPlayer.getDuration()) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(exoPlayer.getDuration())));
                    audio_time.setText(time);
                    setPostImgTintColor(false, play_icon, null, R.drawable.ic_pause_new, R.drawable.ic_play_without_circle);
                    //play_icon.setImageResource();
                    Log.i(TAG, "ExoPlayer ready!");
                case ExoPlayer.STATE_BUFFERING:
                    Log.i(TAG, "Playback buffering!");
                    break;
                case ExoPlayer.STATE_IDLE:
                    Log.i(TAG, "ExoPlayer idle!");
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
            Log.i(TAG, "onPlaybackError: " + error.getMessage());
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
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            try {
                closePopWindow();
                resetExoPlayer();
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            if (mPresenter != null) {
                mPresenter.onDestroy();
            }
            if (mHandler != null) {
                mHandler.removeCallbacks(myRunnable);
                mHandler = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        pausePlayer();
        try {
            closePopWindow();
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        //mPresenter.updatePlayerStarted(false);
        Log.d(TAG, "onPause: ");
        // pausePlayer();
    }

    private void pausePlayer() {
        try {
            if (exoPlayer != null) {
                exoPlayer.setPlayWhenReady(false);
                exoPlayer.getPlaybackState();
            }
            long time = 0;
            if (customExoPlayerView != null) {
                time = customExoPlayerView.getSimpleExoPlayer().getCurrentPosition();
            }
            if (mPresenter != null) {
                mPresenter.resetVideoStarted(time);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    @Override
    public void onDelete(NewPostViewData postViewData) {
        mPresenter.deleteComment(postViewData);
    }

    @Override
    public void onDeleteCancel() {

    }

    @Override
    public void onDeleteComment(NewPostViewData postViewData) {
        new NewCommentDeleteConfirmationPopup(NewNBPostDetailsActivity.this, postViewData, this).show();
    }

    public void playMe() {
        if (!isAudioPause) {
            isAudioPause = true;
            pauseMe();
        } else {
            isAudioPause = false;
            if (mediaPlayer != null) {
                mediaPlayer.start();
                setPostImgTintColor(true, play_icon, null, R.drawable.ic_play_without_circle, R.drawable.ic_pause_new);
            }
        }
    }

    public void pauseMe() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            setPostImgTintColor(true, play_icon, null, R.drawable.ic_pause_new, R.drawable.ic_play_without_circle);
        }
    }


    public void setUpAudio(String audio) {
        mediaPlayer = MediaPlayer.create(this, Uri.parse(audio));
        seekbar.setMax(mediaPlayer.getDuration());
        String time = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(mediaPlayer.getDuration()),
                TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getDuration()) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mediaPlayer.getDuration())),
                TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getDuration()) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getDuration())));
        audio_time.setText(time);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mediaPlayer.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mediaPlayer.setOnCompletionListener(mp -> {
            setPostImgTintColor(true, play_icon, null, R.drawable.ic_pause_new, R.drawable.ic_play_without_circle);
            seekbar.setProgress(0);
            mediaPlayer.seekTo(0);
        });

        //Customize timeLine seekbar

        mHandler = new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekbar.setProgress(mCurrentPosition);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                mHandler.postDelayed(this, 1000);
            }
        };

        /*new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
//                int currentPosition = 0;
                try {
//                    currentPosition = mediaPlayer.getCurrentPosition();
                    if (seekbar != null) {
                        seekbar.setProgress(mediaPlayer.getCurrentPosition());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }


//                if(mediaPlayer != null && mediaPlayer.isPlaying() )
//                {
//                   // seekbar.setProgress(mediaPlayer.getCurrentPosition());
//                    Log.d(TAG,"mediaPlayer.getCurrentPosition"+mediaPlayer.getCurrentPosition());
//                }
            }
        }, 0, 1000);*/

//        new Thread() {
//            @Override
//            public void run() {
//                int runtime = mediaPlayer.getDuration();
//                int currentPosition = 0;
//                int adv = 0;
//                while ((adv = ((adv = runtime - currentPosition) < 1000) ? adv : 1000) > 2) {
//
//                    try {
//                        currentPosition = mediaPlayer.getCurrentPosition();
//                        if (seekbar != null) {
//                            seekbar.setProgress(currentPosition);
//                        }
//                        sleep(adv);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    } catch (IllegalStateException e) {
//                        seekbar.setProgress(runtime);
//                        break;
//                    }
//
//                }
//            }
//        };
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (newNBCommentData1 != null && newNBCommentData1.size() > 0) {
            nbPostCommentAdapter.getFilter().filter(query);
            new Handler().postDelayed(() -> {
                if (nbPostCommentAdapter.getItemCount() != 0) {
                    no_data.setVisibility(View.GONE);
                    nested_view.setVisibility(View.VISIBLE);
                    if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_NB_POST_COMMENT)) {
                        post_comment_rv.setVisibility(View.VISIBLE);
                    } else {
                        post_comment_rv.setVisibility(View.GONE);
                    }
                } else {
                    no_data.setVisibility(View.VISIBLE);
                    nested_view.setVisibility(View.GONE);
                    post_comment_rv.setVisibility(View.GONE);
                }

            }, 2000);
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newNBCommentData1 != null && newNBCommentData1.size() > 0) {
            nbPostCommentAdapter.getFilter().filter(newText);
        }
        return false;
    }

}
