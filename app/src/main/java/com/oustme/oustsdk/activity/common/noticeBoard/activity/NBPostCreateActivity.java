package com.oustme.oustsdk.activity.common.noticeBoard.activity;

import static android.provider.MediaStore.Video.Thumbnails.MINI_KIND;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.TOOL_BAR_COLOR_CODE;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.retry.RetryPolicy;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.noticeBoard.adapters.NBCreatePostAttachmentsAdapter;
import com.oustme.oustsdk.activity.common.noticeBoard.callBacks.NBPostCreateView;
import com.oustme.oustsdk.activity.common.noticeBoard.data.handlers.NBDataHandler;
import com.oustme.oustsdk.activity.common.noticeBoard.model.request.NBPostData;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBTopicData;
import com.oustme.oustsdk.activity.common.noticeBoard.presenters.NBPostCreatePresenter;
import com.oustme.oustsdk.base.BaseActivity;
import com.oustme.oustsdk.compression.video.MediaController;
import com.oustme.oustsdk.customviews.CustomTextView;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.FilePath;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.AlertBuilder;
import com.oustme.oustsdk.util.VersionUtils;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class NBPostCreateActivity extends BaseActivity implements NBPostCreateView, NBCreatePostAttachmentsAdapter.SelectFileToDelete {
    private static final String TAG = "NBPostCreateActivity";
    private NBPostCreatePresenter mNbPostCreatePresenter;
    private ProgressBar mProgressBar;
    private String toolbarColorCode;
    private RecyclerView mRecyclerViewAttachments;
    private EditText mEditTextPostTitle, mEditTextDescription;
    private AlertDialog mAlertDialogSaveDiscard, mAlertDialogForCompress;
    private Button mButtonCreatePost;
    private ImageView imageViewClose;
    private ImageView mImageViewVideo, mImageViewPhoto, mImageViewAudio, mImageViewFile;
    private Drawable mBackgroundDrawable, previewDrawable;
    private long mNBId;
    private CustomTextView mTextViewTopicTitle;
    private NBTopicData nbTopicData;
    private String filename;
    private long fileSize;
    private List<NBPostData.NBPostAttachmentsData> mAttachmentsDataList;
    private boolean isUploadingImage;
    private File savedImageFile;
    private ActiveUser activeUser;
    private String mName, mAvatarLink, mStudentId;
    private TextView mTextViewUploadProgressText;
    private File ImageFilePathToLocal;
    private String mBannerImagePath = null, mVideoPath = null, mAudiPath = null;
    private List<Boolean> isAttachmentList;
    private LinearLayout mLinearLayoutVideoAttachment, mLinearLayoutImageAttachment, mLinearLayoutDocumentAttachment, mLinearLayoutAudioAttachment, nb_coin_layout;
    TextView nb_coins_text;
    private boolean isImageSelected, isVideoSelected, isAudioSelected;
    private ImageView mImageViewPreview, mImageViewPreviewClose, mImageViewPreview2, mImageViewPreviewClose2;
    private String extension;
    private NBCreatePostAttachmentsAdapter adapter;
    private ConstraintLayout mConstraintLayoutPreview, mConstraintLayoutPreview2;
    private List<Bitmap> thumbNail;
    private Bitmap bitmap;
    private View linearLayoutProgressBar;
    private LinearLayout mConstraintLayoutRoot;
    private ProgressBar mProgressBarPostCreate;
    private boolean isThereTitle, isThereDescription;
    private boolean mIsActivityLive;
    private int imageIn, videoIn, audioIn;
    long nbPostRewardOC;
    private CustomTextView mediaUploadMsg;
    private File mFileToCompress;
    ScrollView scroll_layout;
    private int mDarkGrey_a_color;

    private static final int SELECT_FILE = 1100;
    private static final int REQUEST_CAMERA = 1200;
    private static final int SELECT_VIDEO = 1300;
    private static final int REQUEST_CODE_DOC = 1400;
    private static final int RQS_RECORDING = 1500;
    private static final int VIDEO_CAPTURE = 1600;
    private static final int AUDIO_LOCAL = 1700;

    @Override
    protected int getContentView() {
        if (new VersionUtils(24).validateVersionNumber()) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_nbpost_create;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        mIsActivityLive = true;
        super.onResume();
    }

    @Override
    protected void onPause() {
        mIsActivityLive = false;
        super.onPause();
    }

    @Override
    protected void onStop() {
        mIsActivityLive = false;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mIsActivityLive = false;
        super.onDestroy();
    }

    @Override
    protected void initView() {
        try {
            if (OustSdkApplication.getContext() == null) {
                OustSdkApplication.setmContext(NBPostCreateActivity.this);
            }
            OustSdkTools.setLocale(NBPostCreateActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        thumbNail = new ArrayList<>();
        mProgressBar = findViewById(R.id.uploadprogress);
        mProgressBarPostCreate = findViewById(R.id.progressBarNBcreate);
        mRecyclerViewAttachments = findViewById(R.id.linearLayoutRectAttachments);
        mEditTextPostTitle = findViewById(R.id.editTextNBPostTitle);
        mEditTextDescription = findViewById(R.id.editTextNBPostDesc);
        mButtonCreatePost = findViewById(R.id.buttonSubmitPost);
        imageViewClose = findViewById(R.id.imageViewClose);

        mImageViewVideo = findViewById(R.id.imageViewVideo);
        mImageViewAudio = findViewById(R.id.imageViewAudio);
        mImageViewFile = findViewById(R.id.imageViewDocument);
        mImageViewPhoto = findViewById(R.id.imageViewImage);

        mImageViewPreview = findViewById(R.id.imageViewPreview);
        mImageViewPreview2 = findViewById(R.id.imageViewPreview2);
        mTextViewTopicTitle = findViewById(R.id.TopicTitle);

        nb_coin_layout = findViewById(R.id.nb_coin_layout);
        nb_coins_text = findViewById(R.id.nb_coins_text);
        scroll_layout = findViewById(R.id.scroll_layout);
        mDarkGrey_a_color = getResources().getColor(R.color.DarkGray_a);

        try {
            if (OustPreferences.getAppInstallVariable("showCorn")) {
                nb_coins_text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_coins_corn, 0, 0, 0);
            } else {
                nb_coins_text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_coins_golden, 0, 0, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        mediaUploadMsg = findViewById(R.id.mediaUploadMsg);
        RelativeLayout baseLayout = findViewById(R.id.baseLayout);

        mImageViewPreviewClose = findViewById(R.id.imageViewPreviewClose);
        mImageViewPreviewClose2 = findViewById(R.id.imageViewPreviewClose2);
        mLinearLayoutVideoAttachment = findViewById(R.id.linearLayoutVideoAttachment);
        mBackgroundDrawable = getResources().getDrawable(R.drawable.ic_add);
        mBackgroundDrawable.setColorFilter(getResources().getColor(R.color.white_presseda), PorterDuff.Mode.SRC_IN);
        mTextViewUploadProgressText = findViewById(R.id.uploadprogresstext);
        mConstraintLayoutPreview = findViewById(R.id.previewCL);
        mConstraintLayoutPreview2 = findViewById(R.id.previewCL2);
        linearLayoutProgressBar = findViewById(R.id.linearLayoutProgressBar);
        mConstraintLayoutRoot = findViewById(R.id.constraintLayoutRoot);

        mLinearLayoutAudioAttachment = findViewById(R.id.linearLayoutAudioAttachment);
        mLinearLayoutDocumentAttachment = findViewById(R.id.linearLayoutDocumentAttachment);
        mLinearLayoutImageAttachment = findViewById(R.id.linearLayoutImageAttachment);
    }

    @Override
    protected void initData() {
        mAttachmentsDataList = new ArrayList<>();
        isAttachmentList = new ArrayList<>();
        mButtonCreatePost.setBackground(null);
        mButtonCreatePost.setBackgroundResource(R.drawable.roundedcorner_darkgray_outline2);
        GradientDrawable drawable = (GradientDrawable) mButtonCreatePost.getBackground();
        mButtonCreatePost.setBackground(drawable);

        nbTopicData = NBDataHandler.getInstance().getNbTopicData();
        if (nbTopicData != null) {
            if (nbTopicData.getTopic() != null) {
                mTextViewTopicTitle.setText(nbTopicData.getTopic());
                mTextViewTopicTitle.setSelected(true);
            }
        }
        String activeUserGet = OustPreferences.get("userdata");
        activeUser = OustSdkTools.getActiveUserData(activeUserGet);
        if (mStudentId == null && activeUser != null) {
            if (activeUser.getStudentid() != null) {
                mStudentId = activeUser.getStudentid();
            }
            if (activeUser.getAvatar() != null) {
                mAvatarLink = activeUser.getAvatar();
            }
            if (activeUser.getUserDisplayName() != null) {
                mName = activeUser.getUserDisplayName();
            }
        }
        mNBId = getIntent().getLongExtra("NBID", 0);
        nbPostRewardOC = getIntent().getLongExtra("nbPostRewardOC", 0);
        mNbPostCreatePresenter = new NBPostCreatePresenter(this);
        toolbarColorCode = OustPreferences.get(TOOL_BAR_COLOR_CODE);
        if (toolbarColorCode.equalsIgnoreCase("")) {
            toolbarColorCode = "#01b5a2";
        }
        try {
            if (!toolbarColorCode.equalsIgnoreCase("")) {
                mButtonCreatePost.setTextColor(Color.parseColor(toolbarColorCode));
                mBackgroundDrawable.setColorFilter(Color.parseColor(toolbarColorCode), PorterDuff.Mode.SRC_IN);
                mProgressBarPostCreate.getIndeterminateDrawable().setColorFilter(Color.parseColor(toolbarColorCode), PorterDuff.Mode.SRC_IN);
                mProgressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor(toolbarColorCode), PorterDuff.Mode.SRC_IN);
            } else {
                mProgressBarPostCreate.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.lgreen), PorterDuff.Mode.SRC_IN);
                mProgressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.lgreen), PorterDuff.Mode.SRC_IN);
            }

            if (nbPostRewardOC != 0) {
                nb_coin_layout.setVisibility(View.VISIBLE);
                nb_coins_text.setText(String.valueOf(nbPostRewardOC));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void initListener() {
        mButtonCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAndSubmitData();
            }
        });
        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mImageViewVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isImageSelected) {
                    if (!isVideoSelected) {
                        addVideo();
                    } else {
                        OustSdkTools.showToast("Delete existing video to select another");
                    }
                } else {
                    OustSdkTools.showToast("You can't select Image and Video Simultaneously");
                }
            }
        });
        mImageViewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isVideoSelected) {
                    if (!isImageSelected) {
                        addPhoto();
                    } else {
                        OustSdkTools.showToast("Delete existing photo to select another");
                    }
                } else {
                    OustSdkTools.showToast("You can't select Image and Video Simultaneously");
                }
            }
        });

        mImageViewFile.setOnClickListener(view -> addDocument());
        mImageViewAudio.setOnClickListener(view -> {
            if (!isAudioSelected) {
                addAudio();
            } else {
                OustSdkTools.showToast("Delete existing Audio to select another");
            }
        });
        mImageViewPreviewClose.setOnClickListener(view -> {
            previewAttachmentData = null;

            if (videoIn == 1) {
                isVideoSelected = false;
                mVideoPath = null;
                mImageViewVideo.setColorFilter(mDarkGrey_a_color);
                mImageViewVideo.setClickable(true);
            }
            if (imageIn == 1) {
                isImageSelected = false;
                mBannerImagePath = null;
                mImageViewPhoto.setColorFilter(mDarkGrey_a_color);
                mImageViewPhoto.setClickable(true);
            }
            if (audioIn == 1) {
                isAudioSelected = false;
                mAudiPath = null;
                mImageViewAudio.setColorFilter(mDarkGrey_a_color);
                mImageViewAudio.setClickable(true);
            }

            mConstraintLayoutPreview.setVisibility(View.GONE);
        });

        mImageViewPreviewClose2.setOnClickListener(view -> {
            if (videoIn == 2) {
                isVideoSelected = false;
                mVideoPath = null;
                mImageViewVideo.setColorFilter(mDarkGrey_a_color);
                mImageViewVideo.setClickable(true);
            }
            if (imageIn == 2) {
                isImageSelected = false;
                mBannerImagePath = null;
                mImageViewPhoto.setColorFilter(mDarkGrey_a_color);
                mImageViewPhoto.setClickable(true);
            }
            if (audioIn == 2) {
                isAudioSelected = false;
                mAudiPath = null;
                mImageViewAudio.setColorFilter(mDarkGrey_a_color);
                mImageViewAudio.setClickable(true);
            }

            mConstraintLayoutPreview2.setVisibility(View.GONE);
        });


        mEditTextDescription.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() != 0) {
                    isThereDescription = true;
                }
                if (isThereDescription && isThereTitle) {
                    mButtonCreatePost.setEnabled(true);
                    mButtonCreatePost.setBackgroundResource(R.drawable.roundedcorner_darkgray_outline);
                    GradientDrawable drawable = (GradientDrawable) mButtonCreatePost.getBackground();
                    drawable.setColor(Color.parseColor(toolbarColorCode));
                    mButtonCreatePost.setBackground(drawable);
                    mButtonCreatePost.setTextColor(getResources().getColor(R.color.whitea));
                }
            }
        });

        mEditTextPostTitle.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() != 0) {
                    isThereTitle = true;
                }
                if (isThereDescription && isThereTitle) {
                    mButtonCreatePost.setEnabled(true);
                    mButtonCreatePost.setBackgroundResource(R.drawable.roundedcorner_darkgray_outline);
                    GradientDrawable drawable = (GradientDrawable) mButtonCreatePost.getBackground();
                    drawable.setColor(Color.parseColor(toolbarColorCode));
                    mButtonCreatePost.setBackground(drawable);
                    mButtonCreatePost.setTextColor(getResources().getColor(R.color.whitea));
                }
            }
        });

    }

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.RECORD_AUDIO
    };


    private void updateAttachmentList(NBPostData.NBPostAttachmentsData attachmentsData, Bitmap bitmap) {
        mAttachmentsDataList.add(0, attachmentsData);
        thumbNail.add(0, bitmap);
        isAttachmentList.add(0, true);
        if (mAttachmentsDataList.size() > 0) {
            mRecyclerViewAttachments.setVisibility(View.VISIBLE);
        } else
            mRecyclerViewAttachments.setVisibility(View.GONE);
        adapter = new NBCreatePostAttachmentsAdapter(NBPostCreateActivity.this, mAttachmentsDataList, savedImageFile, isAttachmentList, thumbNail);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerViewAttachments.setLayoutManager(linearLayoutManager);
        mRecyclerViewAttachments.setAdapter(adapter);
    }

    private void addPhoto() {
        if (!OustSdkTools.hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else {
            selectImage();
        }
    }

    private void addVideo() {
        if (!OustSdkTools.hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else {
            selectVideo();
        }
    }

    private void addDocument() {
        if (!OustSdkTools.hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else {
            getDocument();
        }
    }

    private void addAudio() {
        if (!OustSdkTools.hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else {
            selectAudio();
        }

    }

    private void validateAndSubmitData() {
        if (!OustSdkTools.checkInternetStatus()) {
            OustSdkTools.showToast("No Internet connected");
            return;
        }

        if (validateIsNullOrEmpty(mEditTextPostTitle)) {
            OustSdkTools.showToast(getString(R.string.no_title_empty));
            return;
        }
        if (validateIsNullOrEmpty(mEditTextDescription)) {
            OustSdkTools.showToast("Description can't be empty");
            return;
        }
        mButtonCreatePost.setClickable(false);
        mButtonCreatePost.setEnabled(false);
        NBPostData nbPostData = new NBPostData()
                .setName(mEditTextPostTitle.getText().toString())
                .setDescription(mEditTextDescription.getText().toString())
                .setNbId((int) mNBId);
        if (mAttachmentsDataList != null && mAttachmentsDataList.size() > 0) {
            nbPostData.setAttachmentsData(mAttachmentsDataList)
                    .setHasAttachment(true);
        }
        nbPostData.setNbPostRewardOC(nbPostRewardOC);
        if (mAttachmentsDataList != null && previewAttachmentData != null) {
            mAttachmentsDataList.add(previewAttachmentData);
        }
        try {
            if (mAudiPath != null) {
                nbPostData.setAudio(mAudiPath);
            }
            if (mVideoPath != null) {
                nbPostData.setVideo(mVideoPath);
            }
            if (mBannerImagePath != null) {
                nbPostData.setBannerImage(mBannerImagePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        mNbPostCreatePresenter.createPost(nbPostData, mStudentId);
    }

    protected boolean validateIsNullOrEmpty(EditText mEditTex) {
        return mEditTex.getText() == null || mEditTex.getText().toString().trim().isEmpty();
    }

    private Bitmap createThumbnailFromBitmap(String filePath) {
        return ThumbnailUtils.createVideoThumbnail(filePath, MINI_KIND);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    @Override
    public void errorCreatingPost(String msg) {
        if (mIsActivityLive) {
            mButtonCreatePost.setEnabled(true);
            mButtonCreatePost.setClickable(true);
            OustSdkTools.showToast(msg);
        }
    }

    @Override
    public void showProgressBar(int type) {
        switch (type) {
            case 2:
                mProgressBar.setVisibility(View.VISIBLE);
                linearLayoutProgressBar.setVisibility(View.VISIBLE);
                scroll_layout.setVisibility(View.GONE);
                mConstraintLayoutRoot.setVisibility(View.GONE);
                break;
            case 1:
                mProgressBarPostCreate.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void hideProgressBar(int type) {
        switch (type) {
            case 2:
                mProgressBar.setVisibility(View.GONE);
                linearLayoutProgressBar.setVisibility(View.GONE);
                scroll_layout.setVisibility(View.VISIBLE);
                mConstraintLayoutRoot.setVisibility(View.VISIBLE);
                break;
            case 1:
                mProgressBarPostCreate.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void postCreatedSuccessfully(boolean success) {
        Log.d(TAG, "postCreatedSuccessfully: " + success);
        if (success) {
            OustStaticVariableHandling.getInstance().setNewPostcreated(true);
            mButtonCreatePost.setEnabled(false);
            mButtonCreatePost.setClickable(false);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 2000);
        }
    }

    @Override
    public void postCreateRequest() {

    }

    public void saveDiscardAlertDialog() {
        AlertBuilder alertBuilder = new AlertBuilder(NBPostCreateActivity.this, getResources().getString(R.string.save_discard_post), getResources().getString(R.string.save), getResources().getString(R.string.discard)) {
            @Override
            public void positiveButtonClicked() {
                dismiss();
                validateAndSubmitData();
            }

            @Override
            public void negativeButtonClicked() {
                mEditTextPostTitle.setText(null);
                mEditTextDescription.setText(null);
                finish();
            }
        };
        alertBuilder.show();
    }

    public void mediaUploadAlertDialog() {
        AlertBuilder alertBuilder = new AlertBuilder(NBPostCreateActivity.this, getResources().getString(R.string.media_upload_confirm_message), getResources().getString(R.string.cancel), getResources().getString(R.string.ok)) {

            @Override
            public void negativeButtonClicked() {
                hideProgressBar(2);
                finish();
            }
        };
        alertBuilder.show();
    }

    @Override
    public void onBackPressed() {
        if (isUploadingImage) {
            mediaUploadAlertDialog();
            return;
        }
        if (isThereDescription || isThereTitle) {
            saveDiscardAlertDialog();
        } else {
            finish();
        }
    }

    private String userChoosenTask;

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(NBPostCreateActivity.this);
        builder.setTitle("Select Attachment!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean gallery = OustSdkTools.checkPermission(NBPostCreateActivity.this);
                boolean camera = OustSdkTools.checkPermissionCamera(NBPostCreateActivity.this);
                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (camera)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (gallery)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void selectVideo() {
        final CharSequence[] items = {"Capture Video", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(NBPostCreateActivity.this);
        builder.setTitle("Select Attachment!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean gallery = OustSdkTools.checkPermission(NBPostCreateActivity.this);
                boolean camera = OustSdkTools.checkPermissionCamera(NBPostCreateActivity.this);
                if (items[item].equals("Capture Video")) {
                    userChoosenTask = "Capture Video";
                    if (hasCamera() && camera) {
                        videoCaptureIntent();
                    } else {
                        OustSdkTools.showToast("No Camera Found");
                    }
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (gallery)
                        videoFromGalleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void selectAudio() {
        final CharSequence[] items = {"Record Audio", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(NBPostCreateActivity.this);
        builder.setTitle("Select Attachment!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean gallery = OustSdkTools.checkPermission(NBPostCreateActivity.this);
                boolean camera = OustSdkTools.checkPermissionCamera(NBPostCreateActivity.this);
                if (items[item].equals("Record Audio")) {
                    userChoosenTask = "Record Audio";
                    audioIntent();
                    //recordingAlert();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    AudioLocalIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private boolean hasCamera() {
        return getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_ANY);
    }

    private void galleryIntent() {
        Intent pickImage = new Intent(Intent.ACTION_PICK);
        pickImage.setType("image/*");
        startActivityForResult(pickImage, SELECT_FILE);
    }

    private void cameraIntent() {
        if (!OustSdkTools.checkInternetStatus()) {
            OustSdkTools.showToast("No Internet connected");
            return;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void videoFromGalleryIntent() {
        if (!OustSdkTools.checkInternetStatus()) {
            OustSdkTools.showToast("No Internet connected");
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra("return-data", true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivityForResult(Intent.createChooser(intent, "Select a Video "), SELECT_VIDEO);
    }

    private void videoCaptureIntent() {
        if (!OustSdkTools.checkInternetStatus()) {
            OustSdkTools.showToast("No Internet connected");
            return;
        }
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        /*if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            videoUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", mediaFile);
        }*/
        startActivityForResult(intent, VIDEO_CAPTURE);
    }

    private void audioIntent() {
        if (!OustSdkTools.checkInternetStatus()) {
            OustSdkTools.showToast("No Internet connected");
            return;
        }
        Intent intent = new Intent(NBPostCreateActivity.this, AudioRecordActivity.class);
        startActivityForResult(intent, RQS_RECORDING);
    }

    private void AudioLocalIntent() {
        if (!OustSdkTools.checkInternetStatus()) {
            OustSdkTools.showToast("No Internet connected");
            return;
        }
        Intent intent_upload = new Intent();
        intent_upload.setType("audio/*");
        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent_upload, AUDIO_LOCAL);
    }

    private void getDocument() {
        if (!OustSdkTools.checkInternetStatus()) {
            OustSdkTools.showToast("No Internet connected");
            return;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        //intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    REQUEST_CODE_DOC);
        } catch (android.content.ActivityNotFoundException ex) {
            ex.printStackTrace();
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data, "IMAGE");
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
            else if (requestCode == SELECT_VIDEO)
                onSelectFromGalleryResult(data, "VIDEO");
            else if (requestCode == REQUEST_CODE_DOC) {
                onSelectFromGalleryResult(data, "ALL");
            } else if (requestCode == AUDIO_LOCAL) {
                onSelectFromGalleryResult(data, "AUDIO");
            } else if (requestCode == RQS_RECORDING) {
                String result = data.getStringExtra("result");
                if (result != null) {
                    uploadImageToAWS(new File(result), "AUDIO");
                } else {

                }
            } else if (requestCode == VIDEO_CAPTURE) {
                onSelectFromGalleryResult(data, "VIDEO");
            }
        }
    }

    private void onSelectFromGalleryResult(Intent data, String mediaType) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Uri path = data.getData();
        if (path != null) {
            if (path.toString().contains("com.google.android.apps.photos")) {
                Log.d(TAG, "From android photos ");
                String filePath = FilePath.getPathFromInputStreamUri(this, path);

                File original = new File(filePath);
                String extension_file = original.getAbsolutePath().substring(original.getAbsolutePath().lastIndexOf("."));
                if (extension_file.equalsIgnoreCase(".jpg") || extension_file.equalsIgnoreCase(".jpeg") || extension_file.equalsIgnoreCase(".png")) {
                    crop_ImageAndUpload(original, extension_file, mediaType);
                } else {
                    uploadImageToAWS(new File(filePath), mediaType);
                }
                //OustSdkTools.showToast("can't select attachment from google photos app");
                //return;
            } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                String filePath = FilePath.getRealPathFromUri(NBPostCreateActivity.this, path);
                if (filePath != null) {
                    File original = new File(filePath);
                    String extension_file = original.getAbsolutePath().substring(original.getAbsolutePath().lastIndexOf("."));
                    if (extension_file.equalsIgnoreCase(".jpg") || extension_file.equalsIgnoreCase(".jpeg") || extension_file.equalsIgnoreCase(".png")) {
                        crop_ImageAndUpload(original, extension_file, mediaType);
                    } else {
                        uploadImageToAWS(new File(filePath), mediaType);
                    }
                } else {
                    OustSdkTools.showToast("unable to get file");
                }
            } else {
                String[] proj = {MediaStore.Images.Media.DATA};
                String result = null;

                CursorLoader cursorLoader = new CursorLoader(
                        context,
                        path, proj, null, null, null);
                Cursor cursor = cursorLoader.loadInBackground();

                if (cursor != null) {
                    int column_index =
                            cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    result = cursor.getString(column_index);
                    if (result != null) {
                        uploadImageToAWS(new File(result), mediaType);
                    }
                }
            }
        }
    }

    private void storeImage(Bitmap image, File pictureFile) {
        //File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d(TAG, "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }

    public void crop_ImageAndUpload(File original, String extension_file, String mediaType) {
        try {
            //change the filepath
            Bitmap d = new BitmapDrawable(getApplicationContext().getResources(), original.getPath()).getBitmap();
            int nh = (int) (d.getHeight() * (512.0 / d.getWidth()));
            Bitmap bitmap_new = Bitmap.createScaledBitmap(d, 512, nh, true);
            Log.d(TAG, "original:" + d.getByteCount() + " -- duplicate:" + bitmap_new.getByteCount());
            //Log.d(TAG, "Bitmap width:" + bitmap_new.getWidth() + " -- height:" + bitmap_new.getHeight());

            File destination = new File(OustSdkApplication.getContext().getFilesDir(), System.currentTimeMillis() + "" + extension_file);
            storeImage(bitmap_new, destination);

            Log.d(TAG, "file size  duplicate:" + destination.length() + " -- Original:" + original.length());
            uploadImageToAWS(destination, mediaType);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            uploadImageToAWS(original, mediaType);
            //Toast.makeText(this,"Couldn't able to load the image. Please try again.",Toast.LENGTH_LONG).show();
        }
    }


    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(OustSdkApplication.getContext().getFilesDir(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        } catch (IOException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        if (destination != null) {
            String extension_file = destination.getAbsolutePath().substring(destination.getAbsolutePath().lastIndexOf("."));
            if (extension_file.equalsIgnoreCase(".jpg") || extension_file.equalsIgnoreCase(".jpeg") || extension_file.equalsIgnoreCase(".png")) {
                crop_ImageAndUpload(destination, extension_file, "IMAGE");
            } else {
                uploadImageToAWS(destination, "IMAGE");
            }

        }
    }


    NBPostData nbPostData = new NBPostData();
    NBPostData.NBPostAttachmentsData previewAttachmentData = nbPostData.new NBPostAttachmentsData();
    NBPostData.NBPostAttachmentsData previewAttachmentData2 = nbPostData.new NBPostAttachmentsData();

    private void uploadImageToAWS(final File recorded_file, final String mediaType) {
        try {
            if (!OustSdkTools.checkInternetStatus()) {
                OustSdkTools.showToast("No Internet connected");
                return;
            }
            extension = recorded_file.getAbsolutePath().substring(recorded_file.getAbsolutePath().lastIndexOf("."));
            if (extension.equalsIgnoreCase(".mp4") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                //compressVideo(recorded_file.toString(), recorded_file, mediaType);
                compressVideoAlert(recorded_file, mediaType);
            } else {
                ConfirmUpload(recorded_file, mediaType);
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void ConfirmUpload(final File recorded_file, final String mediaType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(NBPostCreateActivity.this);
        builder.setMessage("Are you sure you want to Upload?");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finalUpload(recorded_file, mediaType);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mAlertDialogSaveDiscard.dismiss();
                mAlertDialogSaveDiscard = null;
            }
        });
        mAlertDialogSaveDiscard = builder.create();
        mAlertDialogSaveDiscard.show();
        mAlertDialogSaveDiscard.setCancelable(false);
    }

    private void finalUpload(final File recorded_file, final String mediaType) {
        isUploadingImage = true;
        ImageFilePathToLocal = null;
        extension = recorded_file.getAbsolutePath().substring(recorded_file.getAbsolutePath().lastIndexOf("."));
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setRetryPolicy(new RetryPolicy(null, null, 1, true));
        String awsKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeyId");
        String awsSecretKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeySecret");
        AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(awsKeyId, awsSecretKeyId), clientConfiguration);
        s3.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
        TransferUtility transferUtility = new TransferUtility(s3, NBPostCreateActivity.this);
        if (recorded_file == null || (recorded_file != null && !recorded_file.exists())) {
            Toast.makeText(NBPostCreateActivity.this, "File Not Found!", Toast.LENGTH_SHORT).show();
            return;
        }
        filename = OustMediaTools.getMediaFileName(recorded_file.toString()) + "_" + SystemClock.currentThreadTimeMillis();
        fileSize = recorded_file.length();
        Log.d(TAG, "uploadImageToAWS:fileSize: " + fileSize / 1024 + "KB" + " File Extension:" + extension);
        mediaUploadMsg.setText(getResources().getString(R.string.media_uploading));
        enableDisableOnclicks(false);
        mProgressBar.setMax(100);
        mProgressBar.setProgress(0);
        mTextViewUploadProgressText.setText(" 0 %");
        String key = AppConstants.StringConstants.NOTICE_BOARD_FOLDER + filename;
        final TransferObserver observer = transferUtility.upload(AppConstants.StringConstants.S3_BUCKET_NAME, key, recorded_file);
        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {

                if (TransferState.COMPLETED.equals(observer.getState())) {
                    try {
                        NBPostData nbPostData = new NBPostData();
                        bitmap = null;
                        previewAttachmentData = nbPostData.new NBPostAttachmentsData();
                        previewAttachmentData2 = nbPostData.new NBPostAttachmentsData();
                        NBPostData.NBPostAttachmentsData attachmentsData = nbPostData.new NBPostAttachmentsData();
                        if (mediaType.equalsIgnoreCase("ALL")) {
                            mImageViewFile.setColorFilter(Color.parseColor(toolbarColorCode));
                            attachmentsData.setAttachmentId(0);
                            if (extension.equalsIgnoreCase(".jpg") || extension.equalsIgnoreCase(".jpeg") || extension.equalsIgnoreCase(".png")) {
                                attachmentsData.setFileType("IMAGE");
                                bitmap = BitmapFactory.decodeFile(recorded_file.getPath());
                            } else if (extension.equalsIgnoreCase(".mp4")) {
                                attachmentsData.setFileType("VIDEO");
                                bitmap = createThumbnailFromBitmap(recorded_file.getPath());
                            } else if (extension.equalsIgnoreCase(".pdf")) {
                                attachmentsData.setFileType("OTHER");
                                //attachmentsData.setFileType(extension.replace(".","").toUpperCase());
                            } else if (extension.equalsIgnoreCase(".mp3") || extension.equalsIgnoreCase(".amr")) {
                                attachmentsData.setFileType("AUDIO");
                            } else {
                                attachmentsData.setFileType("OTHER");
                            }
                            attachmentsData.setPostId("" + 0);
                            attachmentsData.setFileSize(fileSize + "");
                            updateAttachmentList(attachmentsData, bitmap);
                            attachmentsData.setFileName(filename);
                            isUploadingImage = false;
                            enableDisableOnclicks(true);
                            filename = null;
                            fileSize = 0;
                            savedImageFile = recorded_file;
                        } else if (mediaType.equalsIgnoreCase("VIDEO")) {
                            mVideoPath = filename;
                            previewAttachmentData.setFileName(filename);
                            previewAttachmentData.setAttachmentId(0);
                            previewAttachmentData.setFileType(mediaType);
                            previewAttachmentData.setPostId("" + 0);
                            previewAttachmentData.setFileSize(fileSize + "");
                            isUploadingImage = false;
                            enableDisableOnclicks(true);
                            filename = null;
                            fileSize = 0;
                            ImageFilePathToLocal = recorded_file;
                            mBannerImagePath = null;
                            isVideoSelected = true;
                            isImageSelected = false;
                            changeIconColor2();
                        } else if (mediaType.equalsIgnoreCase("AUDIO")) {
                            mAudiPath = filename;
                            previewAttachmentData.setFileName(filename);
                            previewAttachmentData.setAttachmentId(0);
                            previewAttachmentData.setFileType(mediaType);
                            previewAttachmentData.setPostId("" + 0);
                            previewAttachmentData.setFileSize(fileSize + "");
                            isUploadingImage = false;
                            enableDisableOnclicks(true);
                            filename = null;
                            fileSize = 0;
                            ImageFilePathToLocal = recorded_file;
                            isAudioSelected = true;
                            changeIconColor3();
                        } else if (mediaType.equalsIgnoreCase("IMAGE")) {
                            mBannerImagePath = filename;
                            previewAttachmentData.setFileName(filename);
                            previewAttachmentData.setAttachmentId(0);
                            previewAttachmentData.setFileType(mediaType);
                            previewAttachmentData.setPostId("" + 0);
                            previewAttachmentData.setFileSize(fileSize + "");
                            isUploadingImage = false;
                            enableDisableOnclicks(true);
                            filename = null;
                            fileSize = 0;
                            mVideoPath = null;
                            ImageFilePathToLocal = recorded_file;
                            isVideoSelected = false;
                            isImageSelected = true;
                            changeIconColor2();
                        }
                        if (mIsActivityLive) {
                            OustSdkTools.showToast(getString(R.string.upload_success));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                } else if (TransferState.FAILED.equals(observer.getState())) {
                    enableDisableOnclicks(true);
                    if (mIsActivityLive) {
                        OustSdkTools.showToast(getString(R.string.upload_failed));
                    }
                    isUploadingImage = false;
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                long _bytesCurrent = bytesCurrent;
                long _bytesTotal = bytesTotal;
                float percentage = ((float) _bytesCurrent / (float) _bytesTotal * 100);
                mProgressBar.setProgress((int) percentage);
                mTextViewUploadProgressText.setText((int) percentage + " %");
                Log.d("percentage", "" + percentage);
            }

            @Override
            public void onError(int id, Exception ex) {
                isUploadingImage = false;
                filename = null;
                enableDisableOnclicks(true);
                if (mIsActivityLive) {
                    OustSdkTools.showToast(getString(R.string.upload_failed));
                }
            }
        });
    }

   /* private void changeIconColor() {
        mImageViewFile.setClickable(true);
        if(isImageSelected )
        {
            if (mConstraintLayoutPreview.getVisibility()==View.GONE) {
                mImageViewPreview.setImageURI(Uri.fromFile(ImageFilePathToLocal));
                mConstraintLayoutPreview.setVisibility(View.VISIBLE);
                    *//*else {
                        mImageViewPreview2.setImageURI(Uri.fromFile(ImageFilePathToLocal));
                        mConstraintLayoutPreview2.setVisibility(View.VISIBLE);
                    }*//*
            }
            else {
                mImageViewPreview2.setImageURI(Uri.fromFile(ImageFilePathToLocal));
                mConstraintLayoutPreview2.setVisibility(View.VISIBLE);
            }
            mImageViewPhoto.setColorFilter(Color.parseColor(toolbarColorCode));
            mImageViewAudio.setColorFilter(getResources().getColor(R.color.DarkGray_a));
            mImageViewVideo.setColorFilter(getResources().getColor(R.color.DarkGray_a));
            //mImageViewVideo.setClickable(false);
            //mImageViewAudio.setClickable(false);
        }
        else if(isVideoSelected)
        {
            try {
                if(mConstraintLayoutPreview.getVisibility()==View.GONE) {
                    mImageViewPreview.setImageBitmap(createThumbnailFromBitmap(ImageFilePathToLocal.getPath()));
                    mConstraintLayoutPreview.setVisibility(View.VISIBLE);
                }
                else {
                    mImageViewPreview2.setImageBitmap(createThumbnailFromBitmap(ImageFilePathToLocal.getPath()));
                    mConstraintLayoutPreview2.setVisibility(View.VISIBLE);
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            mImageViewPhoto.setColorFilter(getResources().getColor(R.color.DarkGray_a));
            mImageViewAudio.setColorFilter(getResources().getColor(R.color.DarkGray_a));
            mImageViewVideo.setColorFilter(Color.parseColor(toolbarColorCode));
            //mImageViewPhoto.setClickable(false);
            //mImageViewAudio.setClickable(false);
        }
        else if(mAudiPath!=null && mBannerImagePath!=null || mVideoPath!=null)
        {
            try {
                previewDrawable = getResources().getDrawable(R.drawable.ic_attachaudio);
                previewDrawable.setColorFilter(Color.parseColor(toolbarColorCode), PorterDuff.Mode.SRC_IN);
                mImageViewPreview2.setImageDrawable(previewDrawable);
                mConstraintLayoutPreview2.setVisibility(View.VISIBLE);
                mImageViewAudio.setColorFilter(Color.parseColor(toolbarColorCode));
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
        else if(mAudiPath!=null && (mBannerImagePath==null || mVideoPath==null)){
            try {
                previewDrawable = getResources().getDrawable(R.drawable.ic_attachaudio);
                previewDrawable.setColorFilter(Color.parseColor(toolbarColorCode), PorterDuff.Mode.SRC_IN);
                mImageViewPreview.setImageDrawable(previewDrawable);
                mConstraintLayoutPreview.setVisibility(View.VISIBLE);
                mImageViewAudio.setColorFilter(Color.parseColor(toolbarColorCode));
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
        else if(isAudioSelected)
        {
            if(mConstraintLayoutPreview.getVisibility()==View.GONE) {
                mConstraintLayoutPreview.setVisibility(View.VISIBLE);
                mImageViewAudio.setColorFilter(Color.parseColor(toolbarColorCode));
                previewDrawable = getResources().getDrawable(R.drawable.ic_attachaudio);
                previewDrawable.setColorFilter(Color.parseColor(toolbarColorCode), PorterDuff.Mode.SRC_IN);
                mImageViewPreview.setImageDrawable(previewDrawable);
            }
        }
    }*/

    private void changeIconColor3() {
        if (isAudioSelected) {
            mImageViewAudio.setColorFilter(Color.parseColor(toolbarColorCode));
            if (mConstraintLayoutPreview.getVisibility() == View.GONE) {
                mConstraintLayoutPreview.setVisibility(View.VISIBLE);
                mImageViewAudio.setColorFilter(Color.parseColor(toolbarColorCode));
                previewDrawable = getResources().getDrawable(R.drawable.ic_attachaudio);
                previewDrawable.setColorFilter(Color.parseColor(toolbarColorCode), PorterDuff.Mode.SRC_IN);
                mImageViewPreview.setImageDrawable(previewDrawable);
                audioIn = 1;
            } else {
                mConstraintLayoutPreview2.setVisibility(View.VISIBLE);
                mImageViewAudio.setColorFilter(Color.parseColor(toolbarColorCode));
                previewDrawable = getResources().getDrawable(R.drawable.ic_attachaudio);
                previewDrawable.setColorFilter(Color.parseColor(toolbarColorCode), PorterDuff.Mode.SRC_IN);
                mImageViewPreview2.setImageDrawable(previewDrawable);
                audioIn = 2;
            }
        }

    }

    private void changeIconColor2() {
        try {
            if (isImageSelected) {
                if (mConstraintLayoutPreview.getVisibility() == View.GONE) {
                    mImageViewPreview.setImageURI(Uri.fromFile(ImageFilePathToLocal));
                    mConstraintLayoutPreview.setVisibility(View.VISIBLE);
                    Picasso.get().load(ImageFilePathToLocal).fit().centerCrop().into(mImageViewPreview);
                    imageIn = 1;
                } else {
                    mImageViewPreview2.setImageURI(Uri.fromFile(ImageFilePathToLocal));
                    mConstraintLayoutPreview2.setVisibility(View.VISIBLE);
                    Picasso.get().load(ImageFilePathToLocal).fit().centerCrop().into(mImageViewPreview2);
                    imageIn = 2;
                }
                if (toolbarColorCode != null && !toolbarColorCode.equalsIgnoreCase("")) {
                    mImageViewPhoto.setColorFilter(Color.parseColor(toolbarColorCode));
                } else {
                    mImageViewPhoto.setColorFilter(getResources().getColor(R.color.lgreen));
                }
                mImageViewVideo.setColorFilter(mDarkGrey_a_color);
            } else if (isVideoSelected) {
                if (mConstraintLayoutPreview.getVisibility() == View.GONE) {
                    mImageViewPreview.setImageBitmap(createThumbnailFromBitmap(ImageFilePathToLocal.getPath()));
                    mConstraintLayoutPreview.setVisibility(View.VISIBLE);
                    videoIn = 1;
                } else {
                    mImageViewPreview2.setImageBitmap(createThumbnailFromBitmap(ImageFilePathToLocal.getPath()));
                    mConstraintLayoutPreview2.setVisibility(View.VISIBLE);
                    videoIn = 2;
                }
                mImageViewPhoto.setColorFilter(mDarkGrey_a_color);
                if (toolbarColorCode != null && !toolbarColorCode.equalsIgnoreCase("")) {
                    mImageViewVideo.setColorFilter(Color.parseColor(toolbarColorCode));
                } else {
                    mImageViewVideo.setColorFilter(getResources().getColor(R.color.lgreen));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    protected void enableDisableOnclicks(boolean value) {
        if (!value) {
            showProgressBar(2);
            mTextViewUploadProgressText.setVisibility(View.VISIBLE);
        } else {
            hideProgressBar(2);
            mTextViewUploadProgressText.setVisibility(View.GONE);
        }
    }

    @Override
    public void selectedPosition(int position) {
        if (mAttachmentsDataList != null && mAttachmentsDataList.size() > 0) {
            mAttachmentsDataList.remove(position);
            isAttachmentList.remove(position);
            thumbNail.remove(position);
            if (mAttachmentsDataList.size() > 0) {
                mRecyclerViewAttachments.setVisibility(View.VISIBLE);
            } else {
                mImageViewFile.setColorFilter(mDarkGrey_a_color);
                mRecyclerViewAttachments.setVisibility(View.GONE);

            }
            NBCreatePostAttachmentsAdapter adapter = new NBCreatePostAttachmentsAdapter(NBPostCreateActivity.this, mAttachmentsDataList, savedImageFile, isAttachmentList, thumbNail);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            mRecyclerViewAttachments.setLayoutManager(linearLayoutManager);
            mRecyclerViewAttachments.setAdapter(adapter);
        }
    }

    /*
        private void compressVideo(final String inputFile, final File recordedFile, final String mediaType, Uri videoUri){
            try {
                linearLayoutProgressBar.setVisibility(View.VISIBLE);
                mConstraintLayoutRoot.setVisibility(View.GONE);
                mediaUploadMsg.setText("Preparing Media");
                String fileName = OustMediaTools.getMediaFileName(inputFile);
                File file = new File(inputFile);
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                if(videoUri==null)
                    return;
                retriever.setDataSource(this, videoUri);
                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                long timeInMillisec = Long.parseLong(time );
                int sec = (int)timeInMillisec/1000;
                int min = sec/60;
                retriever.release();
                Log.d(TAG, "video duration : "+min+":"+sec);
                long size = (long) (800000 * sec * .0075);//bitrate * seconds
                long kilobytes = size/(8);
                long mb = kilobytes/1024;
                Log.d(TAG, "Original Size: "+Formatter.formatFileSize(this, file.length()));
                GiraffeCompressor.init(this);
                final File out = new File(Environment.getExternalStorageDirectory(), COMPRESSED_FOLDER+fileName);
                if(!out.exists()){
                    try {
                        out.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
                GiraffeCompressor.create() //two implementations: mediacodec and ffmpeg,default is mediacodec
                        .input(inputFile) //set video to be compressed
                        .output(out) //set compressed video output
                        .bitRate(800000)//set bitrate
                        .resizeFactor(1.0f)//set video resize factor
                        //.watermark("/sdcard/videoCompressor/watermarker.png")//add watermark(take a long time) 水印图片(需要长时间处理)
                        .ready()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<GiraffeCompressor.Result>() {
                            @Override
                            public void onCompleted() {
                                //$.id(R.id.btn_start).enabled(true).text("start compress");
                            }
                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                // $.id(R.id.btn_start).enabled(true).text("start compress");
                                //$.id(R.id.tv_console).text("error:"+e.getMessage());
                            }
                            @Override
                            public void onNext(GiraffeCompressor.Result s) {
                                String msg = String.format("compress completed \ntake time:%s \nout put file:%s", s.getCostTime(), s.getOutput());
                                msg = msg + "\n input file size:"+ Formatter.formatFileSize(getApplication(),inputFile.length());
                                msg = msg + "\n out file size:"+ Formatter.formatFileSize(getApplication(),new File(s.getOutput()).length());
                                System.out.println(msg);
                                linearLayoutProgressBar.setVisibility(View.GONE);
                                mConstraintLayoutRoot.setVisibility(View.VISIBLE);
                                finalUpload(out, mediaType);
                                // $.id(R.id.tv_console).text(msg);
                            }
                        });
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            } catch (SecurityException e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    */
    private void compressVideoAlert(final File filePath, final String mediaType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(NBPostCreateActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.compress_popup, null);
        builder.setView(alertLayout);
        LinearLayout mLinearLayoutCompress, mLinearLayoutOriginal;
        ImageView mImageViewClose;
        CustomTextView mTextViewSize;
        mTextViewSize = alertLayout.findViewById(R.id.textViewSize);
        final Uri fileUrI = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", filePath);
        final File file = filePath; //new File(filePath.toString());
        String sizeMessage = "This file is of " + Formatter.formatFileSize(this, file.length());
        mLinearLayoutCompress = alertLayout.findViewById(R.id.linearLayoutCompress);
        mLinearLayoutOriginal = alertLayout.findViewById(R.id.linearLayoutOriginal);
        mImageViewClose = alertLayout.findViewById(R.id.imageViewClose);
        mTextViewSize.setText(sizeMessage);

        mLinearLayoutCompress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OustSdkTools.createApplicationFolder();
                mAlertDialogForCompress.dismiss();
                if (filePath != null && file.exists()) {
                    File out = new File(
                            OustSdkApplication.getContext().getFilesDir(),
                            "compress.mp4"
                    );
                    if (out.exists())
                        out.delete();
                    mFileToCompress = filePath;
                    new VideoCompressor().execute();
                }
            }
        });
        mLinearLayoutOriginal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialogForCompress.dismiss();
                finalUpload(filePath, mediaType);
            }
        });

        mImageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialogForCompress.dismiss();
                mAlertDialogForCompress = null;
            }
        });

        mAlertDialogForCompress = builder.create();
        mAlertDialogForCompress.setCancelable(false);
        mAlertDialogForCompress.show();
    }


    class VideoCompressor extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            linearLayoutProgressBar.setVisibility(View.VISIBLE);
            scroll_layout.setVisibility(View.GONE);
            mConstraintLayoutRoot.setVisibility(View.GONE);
            mediaUploadMsg.setText("" + getResources().getString(R.string.preparing_media));
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return MediaController.getInstance().convertVideo(mFileToCompress.getPath());
        }

        @Override
        protected void onPostExecute(Boolean compressed) {
            super.onPostExecute(compressed);
            if (compressed) {
                linearLayoutProgressBar.setVisibility(View.GONE);
                scroll_layout.setVisibility(View.VISIBLE);
                mConstraintLayoutRoot.setVisibility(View.VISIBLE);


                File sourceFile = new File(
                        OustSdkApplication.getContext().getFilesDir(),
                        "compress.mp4"
                );
                File out = new File(OustSdkApplication.getContext().getFilesDir(), System.currentTimeMillis()+".mp4");
                try {
                    OustSdkTools.copyFile(sourceFile, out);
                } catch (IOException e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                finalUpload(out, "VIDEO");
            }
        }
    }

}
