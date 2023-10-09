package com.oustme.oustsdk.layoutFour.newnoticeBoard.activity;

import static android.provider.MediaStore.Video.Thumbnails.MINI_KIND;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.TOOL_BAR_COLOR_CODE;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
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
import com.oustme.oustsdk.activity.common.noticeBoard.activity.AudioRecordActivity;
import com.oustme.oustsdk.base.BaseActivity;
import com.oustme.oustsdk.compression.video.MediaController;
import com.oustme.oustsdk.customviews.CustomTextView;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.adapters.NewNBCreatePostAttachmentsAdapter;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.callBacks.NewNBPostCreateView;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.data.handlers.NewNBDataHandler;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.request.NewNBPostData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBTopicData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.presenters.NewNBPostCreatePresenter;
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

public class NewNBPostCreateActivity extends BaseActivity implements NewNBPostCreateView, NewNBCreatePostAttachmentsAdapter.SelectFileToDelete {

    private static final String TAG = "NewNBPostCreateActivity";
    private NewNBPostCreatePresenter mNbPostCreatePresenter;
    private ProgressBar mProgressBar;
    private String toolbarColorCode;
    private RecyclerView mRecyclerViewAttachments;
    private EditText mEditTextPostTitle, mEditTextDescription;
    private AlertDialog mAlertDialogSaveDiscard, mAlertDialogForCompress;
    private Button mButtonCreatePost;
    private ImageView imageViewClose;
    private ImageView mImageViewVideo, mImageViewPhoto, mImageViewAudio, mImageViewFile;
    private Drawable mBackgroundDrawable;
    private long mNBId;
    private CustomTextView mTextViewTopicTitle;
    private String filename;
    private long fileSize;
    private List<NewNBPostData.NewNBPostAttachmentsData> mAttachmentsDataList;
    private boolean isUploadingImage;
    private File savedImageFile;
    private String mStudentId;
    private TextView mTextViewUploadProgressText;
    private File ImageFilePathToLocal;
    private String mBannerImagePath = null, mVideoPath = null, mAudiPath = null;
    private List<Boolean> isAttachmentList;
    private LinearLayout nb_coin_layout;
    TextView nb_coins_text;
    private boolean isImageSelected, isVideoSelected, isAudioSelected;
    private ImageView mImageViewPreview, mImageViewPreviewClose, mImageViewPreview2, mImageViewPreviewClose2;
    private String extension;
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
    ScrollView scroll_layout;
    File file; //new File(filePath.toString());

    private static final int SELECT_FILE = 1100;
    private static final int REQUEST_CAMERA = 1200;
    private static final int SELECT_VIDEO = 1300;
    private static final int REQUEST_CODE_DOC = 1400;
    private static final int RQS_RECORDING = 1500;
    private static final int VIDEO_CAPTURE = 1600;
    private static final int AUDIO_LOCAL = 1700;

    private String userChosenTask;

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
                OustSdkApplication.setmContext(NewNBPostCreateActivity.this);
            }
            OustSdkTools.setLocale(NewNBPostCreateActivity.this);
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

        mImageViewPreviewClose = findViewById(R.id.imageViewPreviewClose);
        mImageViewPreviewClose2 = findViewById(R.id.imageViewPreviewClose2);
        mBackgroundDrawable = getResources().getDrawable(R.drawable.ic_add);
        mBackgroundDrawable.setColorFilter(getResources().getColor(R.color.white_presseda), PorterDuff.Mode.SRC_IN);
        mTextViewUploadProgressText = findViewById(R.id.uploadprogresstext);
        mConstraintLayoutPreview = findViewById(R.id.previewCL);
        mConstraintLayoutPreview2 = findViewById(R.id.previewCL2);
        linearLayoutProgressBar = findViewById(R.id.linearLayoutProgressBar);
        mConstraintLayoutRoot = findViewById(R.id.constraintLayoutRoot);

    }

    @Override
    protected void initData() {
        mAttachmentsDataList = new ArrayList<>();
        isAttachmentList = new ArrayList<>();
        mButtonCreatePost.setBackground(null);
        mButtonCreatePost.setBackgroundResource(R.drawable.roundedcorner_darkgray_outline2);
        GradientDrawable drawable = (GradientDrawable) mButtonCreatePost.getBackground();
        mButtonCreatePost.setBackground(drawable);


        NewNBTopicData nbTopicData = NewNBDataHandler.getInstance().getNbTopicData();
        if (nbTopicData != null) {
            if (nbTopicData.getTopic() != null) {
                mTextViewTopicTitle.setText(nbTopicData.getTopic());
                mTextViewTopicTitle.setSelected(true);
            }
        }
        String activeUserGet = OustPreferences.get("userdata");
        ActiveUser activeUser = OustSdkTools.getActiveUserData(activeUserGet);
        if (mStudentId == null && activeUser != null) {
            if (activeUser.getStudentid() != null) {
                mStudentId = activeUser.getStudentid();
            }
        }
        mNBId = getIntent().getLongExtra("NBID", 0);
        String nbTitile = getIntent().getStringExtra("NBTitle");
        nbPostRewardOC = getIntent().getLongExtra("nbPostRewardOC", 0);
        mNbPostCreatePresenter = new NewNBPostCreatePresenter(this);
        toolbarColorCode = OustPreferences.get(TOOL_BAR_COLOR_CODE);
        if (toolbarColorCode.equalsIgnoreCase("")) {
            toolbarColorCode = "#01b5a2";
        }
        if (nbTitile != null) {
            mTextViewTopicTitle.setText(nbTitile);
        }
        try {
            if (toolbarColorCode != null && !toolbarColorCode.equalsIgnoreCase("")) {
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
        mButtonCreatePost.setOnClickListener(view -> validateAndSubmitData());
        imageViewClose.setOnClickListener(view -> onBackPressed());
        mImageViewVideo.setOnClickListener(view -> {
            if (!isImageSelected) {
                if (!isVideoSelected) {
                    addVideo();
                } else {
                    OustSdkTools.showToast("Delete existing video to select another");
                }
            } else {
                OustSdkTools.showToast("You can't select Image and Video Simultaneously");
            }
        });
        mImageViewPhoto.setOnClickListener(view -> {
            if (!isVideoSelected) {
                if (!isImageSelected) {
                    addPhoto();
                } else {
                    OustSdkTools.showToast("Delete existing photo to select another");
                }
            } else {
                OustSdkTools.showToast("You can't select Image and Video Simultaneously");
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
                mImageViewVideo.setColorFilter(getResources().getColor(R.color.DarkGray_a));
                mImageViewVideo.setClickable(true);
            }
            if (imageIn == 1) {
                isImageSelected = false;
                mBannerImagePath = null;
                mImageViewPhoto.setColorFilter(getResources().getColor(R.color.DarkGray_a));
                mImageViewPhoto.setClickable(true);
            }
            if (audioIn == 1) {
                isAudioSelected = false;
                mAudiPath = null;
                mImageViewAudio.setColorFilter(getResources().getColor(R.color.DarkGray_a));
                mImageViewAudio.setClickable(true);
            }

            mConstraintLayoutPreview.setVisibility(View.GONE);
        });

        mImageViewPreviewClose2.setOnClickListener(view -> {
            if (videoIn == 2) {
                isVideoSelected = false;
                mVideoPath = null;
                mImageViewVideo.setColorFilter(getResources().getColor(R.color.DarkGray_a));
                mImageViewVideo.setClickable(true);
            }
            if (imageIn == 2) {
                isImageSelected = false;
                mBannerImagePath = null;
                mImageViewPhoto.setColorFilter(getResources().getColor(R.color.DarkGray_a));
                mImageViewPhoto.setClickable(true);
            }
            if (audioIn == 2) {
                isAudioSelected = false;
                mAudiPath = null;
                mImageViewAudio.setColorFilter(getResources().getColor(R.color.DarkGray_a));
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


    private void updateAttachmentList(NewNBPostData.NewNBPostAttachmentsData attachmentsData, Bitmap bitmap) {
        mAttachmentsDataList.add(0, attachmentsData);
        thumbNail.add(0, bitmap);
        isAttachmentList.add(0, true);
        if (mAttachmentsDataList.size() > 0) {
            mRecyclerViewAttachments.setVisibility(View.VISIBLE);
        } else
            mRecyclerViewAttachments.setVisibility(View.GONE);
        NewNBCreatePostAttachmentsAdapter adapter = new NewNBCreatePostAttachmentsAdapter(NewNBPostCreateActivity.this, mAttachmentsDataList, savedImageFile, isAttachmentList, thumbNail);
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
        }
        {
            getDocument();
        }
    }

    private void addAudio() {
        if (!OustSdkTools.hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        selectAudio();
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
        NewNBPostData nbPostData = new NewNBPostData()
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
        nbPostData.setAudio(mAudiPath)
                .setVideo(mVideoPath)
                .setBannerImage(mBannerImagePath);
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
            mButtonCreatePost.setClickable(false);
            mButtonCreatePost.setEnabled(false);
            new Handler().postDelayed(() -> {
                try {
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }, 2000);
        }
    }

    @Override
    public void postCreateRequest() {

    }

    public void saveDiscardAlertDialog() {
        try {
            AlertBuilder alertBuilder = new AlertBuilder(NewNBPostCreateActivity.this, getResources().getString(R.string.save_discard_post), getResources().getString(R.string.save), getResources().getString(R.string.discard)) {
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
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void mediaUploadAlertDialog() {
        try {
            AlertBuilder alertBuilder = new AlertBuilder(NewNBPostCreateActivity.this, getResources().getString(R.string.media_upload_confirm_message), getResources().getString(R.string.cancel), getResources().getString(R.string.ok)) {

                @Override
                public void negativeButtonClicked() {
                    hideProgressBar(2);
                    finish();
                }
            };
            alertBuilder.show();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (isUploadingImage) {
                mediaUploadAlertDialog();
                return;
            }
            if (isThereDescription || isThereTitle) {
                saveDiscardAlertDialog();
            } else {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void selectImage() {
        try {
            final CharSequence[] items = {"Take Photo", "Choose from Library",
                    "Cancel"};
            AlertDialog.Builder builder = new AlertDialog.Builder(NewNBPostCreateActivity.this);
            builder.setTitle("Select Attachment!");
            builder.setItems(items, (dialog, item) -> {
                boolean gallery = OustSdkTools.checkPermission(NewNBPostCreateActivity.this);
                boolean camera = OustSdkTools.checkPermissionCamera(NewNBPostCreateActivity.this);
                if (items[item].equals("Take Photo")) {
                    userChosenTask = "Take Photo";
                    if (camera)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChosenTask = "Choose from Library";
                    if (gallery)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            });
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void selectVideo() {
        final CharSequence[] items = {"Capture Video", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(NewNBPostCreateActivity.this);
        builder.setTitle("Select Attachment!");
        builder.setItems(items, (dialog, item) -> {
            boolean gallery = OustSdkTools.checkPermission(NewNBPostCreateActivity.this);
            boolean camera = OustSdkTools.checkPermissionCamera(NewNBPostCreateActivity.this);
            if (items[item].equals("Capture Video")) {
                userChosenTask = "Capture Video";
                if (camera) {
                    videoCaptureIntent();
                } else {
                    OustSdkTools.showToast("No Camera Found");
                }
            } else if (items[item].equals("Choose from Library")) {
                userChosenTask = "Choose from Library";
                if (gallery)
                    videoFromGalleryIntent();
            } else if (items[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void selectAudio() {
        final CharSequence[] items = {"Record Audio", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(NewNBPostCreateActivity.this);
        builder.setTitle("Select Attachment!");
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals("Record Audio")) {
                userChosenTask = "Record Audio";
                audioIntent();
                //recordingAlert();
            } else if (items[item].equals("Choose from Library")) {
                userChosenTask = "Choose from Library";
                AudioLocalIntent();
            } else if (items[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    private void galleryIntent() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void cameraIntent() {
        try {
            if (!OustSdkTools.checkInternetStatus()) {
                OustSdkTools.showToast("No Internet connected");
                return;
            }
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CAMERA);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void videoFromGalleryIntent() {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void videoCaptureIntent() {
        try {
            if (!OustSdkTools.checkInternetStatus()) {
                OustSdkTools.showToast("No Internet connected");
                return;
            }
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            startActivityForResult(intent, VIDEO_CAPTURE);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void audioIntent() {
        if (!OustSdkTools.checkInternetStatus()) {
            OustSdkTools.showToast("No Internet connected");
            return;
        }
        Intent intent = new Intent(NewNBPostCreateActivity.this, AudioRecordActivity.class);
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
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
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
                }
            } else if (requestCode == VIDEO_CAPTURE) {
                onSelectFromGalleryResult(data, "VIDEO");
            }
        }
    }

    private void onSelectFromGalleryResult(Intent data, String mediaType) {
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
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Log.d(TAG, "from SDK more than Kitkat");
                String filePath = FilePath.getRealPathFromUri(NewNBPostCreateActivity.this, path);
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
                String result;

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
        try {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
            File destination = new File(OustSdkApplication.getContext().getFilesDir(),
                    System.currentTimeMillis() + ".jpg");
            FileOutputStream fo;

            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();

            String extension_file = destination.getAbsolutePath().substring(destination.getAbsolutePath().lastIndexOf("."));
            if (extension_file.equalsIgnoreCase(".jpg") || extension_file.equalsIgnoreCase(".jpeg") || extension_file.equalsIgnoreCase(".png")) {
                crop_ImageAndUpload(destination, extension_file, "IMAGE");
            } else {
                uploadImageToAWS(destination, "IMAGE");
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    NewNBPostData nbPostData = new NewNBPostData();
    NewNBPostData.NewNBPostAttachmentsData previewAttachmentData = nbPostData.new NewNBPostAttachmentsData();
    NewNBPostData.NewNBPostAttachmentsData previewAttachmentData2 = nbPostData.new NewNBPostAttachmentsData();

    private void uploadImageToAWS(final File recorded_file, final String mediaType) {
        try {
            if (!OustSdkTools.checkInternetStatus()) {
                OustSdkTools.showToast("No Internet connected");
                return;
            }
            extension = recorded_file.getAbsolutePath().substring(recorded_file.getAbsolutePath().lastIndexOf("."));
            if (extension.equalsIgnoreCase(".mp4") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
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
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(NewNBPostCreateActivity.this);
            builder.setMessage("Are you sure you want to Upload?");
            builder.setPositiveButton("Confirm", (dialogInterface, i) -> finalUpload(recorded_file, mediaType));

            builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
                mAlertDialogSaveDiscard.dismiss();
                mAlertDialogSaveDiscard = null;
            });
            mAlertDialogSaveDiscard = builder.create();
            mAlertDialogSaveDiscard.show();
            mAlertDialogSaveDiscard.setCancelable(false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void finalUpload(final File recorded_file, final String mediaType) {
        try {
            isUploadingImage = true;
            ImageFilePathToLocal = null;
            extension = recorded_file.getAbsolutePath().substring(recorded_file.getAbsolutePath().lastIndexOf("."));
            ClientConfiguration clientConfiguration = new ClientConfiguration();
            clientConfiguration.setRetryPolicy(new RetryPolicy(null, null, 1, true));
            String awsKeyId = OustPreferences.get("awsS3KeyId");
            String awsSecretKeyId = OustPreferences.get("awsS3KeySecret");
            AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(awsKeyId, awsSecretKeyId), clientConfiguration);
            s3.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
            TransferUtility transferUtility = new TransferUtility(s3, NewNBPostCreateActivity.this);
            if (!recorded_file.exists()) {
                Toast.makeText(NewNBPostCreateActivity.this, "File Not Found!", Toast.LENGTH_SHORT).show();
                return;
            }
            filename = OustMediaTools.getMediaFileName(recorded_file.toString());
            //https://di5jfel2ggs8k.cloudfront.net/noticeBoard/Screenrecorder-2021-10-10-20-26-45-906(0).mp4_20800
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
                            NewNBPostData nbPostData = new NewNBPostData();
                            bitmap = null;
                            previewAttachmentData = nbPostData.new NewNBPostAttachmentsData();
                            previewAttachmentData2 = nbPostData.new NewNBPostAttachmentsData();
                            NewNBPostData.NewNBPostAttachmentsData attachmentsData = nbPostData.new NewNBPostAttachmentsData();
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
                    float percentage = ((float) bytesCurrent / (float) bytesTotal * 100);
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
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void changeIconColor3() {
        try {
            if (isAudioSelected) {
                mImageViewAudio.setColorFilter(Color.parseColor(toolbarColorCode));
                Drawable previewDrawable;
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
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
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
                mImageViewVideo.setColorFilter(getResources().getColor(R.color.DarkGray_a));
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
                mImageViewPhoto.setColorFilter(getResources().getColor(R.color.DarkGray_a));
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
        try {
            if (!value) {
                showProgressBar(2);
                mTextViewUploadProgressText.setVisibility(View.VISIBLE);
            } else {
                hideProgressBar(2);
                mTextViewUploadProgressText.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void selectedPosition(int position) {
        try {
            if (mAttachmentsDataList != null && mAttachmentsDataList.size() > 0) {
                mAttachmentsDataList.remove(position);
                isAttachmentList.remove(position);
                thumbNail.remove(position);
                if (mAttachmentsDataList.size() > 0) {
                    mRecyclerViewAttachments.setVisibility(View.VISIBLE);
                } else {
                    mImageViewFile.setColorFilter(context.getResources().getColor(R.color.DarkGray_a));
                    mRecyclerViewAttachments.setVisibility(View.GONE);

                }
                NewNBCreatePostAttachmentsAdapter adapter = new NewNBCreatePostAttachmentsAdapter(NewNBPostCreateActivity.this, mAttachmentsDataList, savedImageFile, isAttachmentList, thumbNail);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                mRecyclerViewAttachments.setLayoutManager(linearLayoutManager);
                mRecyclerViewAttachments.setAdapter(adapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void compressVideoAlert(final File filePath, final String mediaType) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(NewNBPostCreateActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View alertLayout = inflater.inflate(R.layout.compress_popup, null);
            builder.setView(alertLayout);
            LinearLayout mLinearLayoutCompress, mLinearLayoutOriginal;
            ImageView mImageViewClose;
            CustomTextView mTextViewSize;
            mTextViewSize = alertLayout.findViewById(R.id.textViewSize);
//        final Uri fileUrI = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", filePath);
            file = filePath;
            String sizeMessage = "This file is of " + Formatter.formatFileSize(this, file.length());
            mLinearLayoutCompress = alertLayout.findViewById(R.id.linearLayoutCompress);
            mLinearLayoutOriginal = alertLayout.findViewById(R.id.linearLayoutOriginal);
            mImageViewClose = alertLayout.findViewById(R.id.imageViewClose);
            mTextViewSize.setText(sizeMessage);

            mLinearLayoutCompress.setOnClickListener(v -> {
                OustSdkTools.createApplicationFolder();
                mAlertDialogForCompress.dismiss();
                if (file.exists()) {
                    try {
                        OustSdkTools.createApplicationFolder();
                        File out = new File(
                                OustSdkApplication.getContext().getFilesDir(),
                                "compress.mp4"
                        );
                        if (out.exists()) {
                            boolean b = out.delete();
                            Log.e(TAG, "File delete " + b);
                        }
                        new VideoCompressor().execute();
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
            });
            mLinearLayoutOriginal.setOnClickListener(v -> {
                mAlertDialogForCompress.dismiss();
                finalUpload(filePath, mediaType);
            });

            mImageViewClose.setOnClickListener(v -> {
                mAlertDialogForCompress.dismiss();
                mAlertDialogForCompress = null;
            });

            mAlertDialogForCompress = builder.create();
            mAlertDialogForCompress.setCancelable(false);
            mAlertDialogForCompress.show();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    class VideoCompressor extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            linearLayoutProgressBar.setVisibility(View.VISIBLE);
            scroll_layout.setVisibility(View.GONE);
            mConstraintLayoutRoot.setVisibility(View.GONE);
            mediaUploadMsg.setText("" + getResources().getString(R.string.preparing_media));
//            uploadProgress();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return MediaController.getInstance().convertVideo(file.getPath());
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
                file = new File(OustSdkApplication.getContext().getFilesDir(), System.currentTimeMillis() + ".mp4");
                try {
                    OustSdkTools.copyFile(sourceFile, file);
                } catch (IOException e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                finalUpload(file, "VIDEO");
            }
        }
    }
}