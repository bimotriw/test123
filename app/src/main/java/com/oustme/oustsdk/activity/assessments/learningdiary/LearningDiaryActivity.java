package com.oustme.oustsdk.activity.assessments.learningdiary;

import static android.view.Gravity.BOTTOM;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.TOOL_BAR_COLOR_CODE;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.DefaultItemAnimator;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.adapter.common.DiaryFilterAdapter;
import com.oustme.oustsdk.adapter.common.LearningDiaryAdapter;
import com.oustme.oustsdk.base.BaseActivity;
import com.oustme.oustsdk.customviews.CircleImageView;
import com.oustme.oustsdk.model.request.LearningDiaryMediaDataList;
import com.oustme.oustsdk.model.response.diary.FilterModel;
import com.oustme.oustsdk.room.dto.DTODiaryDetailsModel;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.BlurImage;
import com.oustme.oustsdk.tools.FilePath;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LearningDiaryActivity extends BaseActivity implements DiaryFilterAdapter.SelectFilter,
        CreateActivityFragment.OnFragmentInteractionListener,
        LearningDiaryView.LDView,
        LearningDiaryAdapter.SelectEditDelete {

    private static final String TAG = "LearningDiaryActivity";
    private static final int SELECT_FILE = 1100;
    private static final int REQUEST_CAMERA = 1200;
    private static final long MILLI_SEC_PER_DAY = 86400000;
    private RecyclerView mListViewFilter;
    private RecyclerView mRecyclerViewDiary;
    private DiaryFilterAdapter diaryFilterAdapter;
    private LearningDiaryAdapter learningDiaryAdapter;
    private List<DTODiaryDetailsModel> diaryDetailsModels, APIDataList, FBDataList;
    private List<DTODiaryDetailsModel> diaryFilteredList;
    private TextView mTextViewProfileName, mTextViewDesignation, mTextViewLocation, mTextViewCoins, mTextViewCertificates, mTextViewRank;
    private TextView mTextViewCoinsText, mTextViewCertificatesText, mTextViewRankText;
    private ActiveUser activeUser;
    private FloatingActionButton floatingActionButton;
    private PopupWindow mPopUpWindow;
    private ConstraintLayout constraintLayout;
    private LearningDiaryPresenter mPresenter;
    private String mName, mAvatarLink, mStudentId;
    private ProgressBar mProgressBar, alertProgressBar, uploadProgressBar;
    private String toolbarColorCode;
    private CircleImageView mCircleImageViewAvatar;
    private TextView mTextViewNoData, mTextViewAttachmentLabel, mTextViewNext;
    private ImageView mImageViewProfileBg, mImageViewDetach;
    private RelativeLayout mRelativeLayoutClose;
    private Drawable mBackgroundDrawable;
    private AlertDialog mAlertDialog, mAlertDialog2, mAlertDialogFail;
    private LinearLayout mLinearLayoutTitle, mLinearLayoutBottomNext, mLinearLayoutAttachmentLabel;
    private EditText mEditTextStartDate, mEditTextEndDate, mEditTextComment, mEditTextTopic;
    private Calendar calendar;
    private int year, month, day;
    private String startDate, endDate = null;
    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    private Uri picUri;
    private String userChoosenTask;
    private ActionBar mActionBar;
    private String filename;
    private List<DTODiaryDetailsModel> ManualEntryList;
    private long fileSize;
    private ArrayList<LearningDiaryMediaDataList> mediaDataLists;
    private boolean isAll;
    private DTODiaryDetailsModel editModel;
    private LearningDiaryMediaDataList learningDiaryMediaDataList;
    private boolean isMediaChanged;
    private AlertDialog dialogForDelete;
    private boolean isMediaDeleted;
    private int selectedPositionOfFilter;
    private int BLUR_PRECENTAGE = 15;
    private TextView mTextViewEntryTitle;
    private LinearLayout mLinearLayoutRetry, mLinearLayoutOk;

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private File savedImageFile;
    private boolean isUploadingImage;
    private boolean isAttachementDeleted;
    private ImageView imageViewClose;
    private boolean isPreviousDateAllowed;
    private boolean isFutureDateAllowed;
    private int mNoOfBackDays = 365;
    private long MaxDateRange = 0;
    private String lastEntryDate;
    private long latestEntryInMill;
    private int noOfDaysBackToBeShown;
    private boolean isStartDateFreeze;

    @Override
    protected int getContentView() {
        /*
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_learning_diary;
    }

    @Override
    protected void initView() {
        try{
            OustSdkTools.setLocale(LearningDiaryActivity.this);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        mPresenter = new LearningDiaryPresenter(this);
        //  mPresenter.AddManualData(mStudentId);

        mListViewFilter = findViewById(R.id.recyclerFilter);
        mRecyclerViewDiary = findViewById(R.id.recyclerViewDiary);

        mProgressBar = findViewById(R.id.progressBarLD);

        mTextViewProfileName = findViewById(R.id.up_username_Txt);
        mTextViewDesignation = findViewById(R.id.textViewDesignation);
        mTextViewLocation = findViewById(R.id.textViewLocation);

        mTextViewCertificates = findViewById(R.id.up_certifct_Txt);
        mTextViewCoins = findViewById(R.id.up_xp_Txt);
        mTextViewRank = findViewById(R.id.up_rank_Txt);

        mTextViewCertificatesText = findViewById(R.id.certificate_text);
        mTextViewCoinsText = findViewById(R.id.coins_text);
        mTextViewRankText = findViewById(R.id.rank_text);

        mCircleImageViewAvatar = findViewById(R.id.user_profile_Avatar);
        mImageViewProfileBg = findViewById(R.id.banner_img);

        floatingActionButton = findViewById(R.id.floatingAction);
        constraintLayout = findViewById(R.id.constraintLayout);

        mTextViewNoData = findViewById(R.id.textViewNoData);
        mRelativeLayoutClose = findViewById(R.id.up_close);
        mBackgroundDrawable = getResources().getDrawable(R.drawable.ic_add);
        mBackgroundDrawable.setColorFilter(getResources().getColor(R.color.white_presseda), PorterDuff.Mode.SRC_IN);
        floatingActionButton.setImageDrawable(mBackgroundDrawable);

    }

    @Override
    protected void initData() {
        try {
            calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            toolbarColorCode = OustPreferences.get(TOOL_BAR_COLOR_CODE);
            MaxDateRange = MILLI_SEC_PER_DAY * OustPreferences.getTimeForNotification(AppConstants.StringConstants.MAX_DATE_RANGE);
            if(MaxDateRange<=0)
            {
                MaxDateRange = MILLI_SEC_PER_DAY*30;
            }
            if((int)OustPreferences.getTimeForNotification(AppConstants.StringConstants.NO_OF_BACK_DAYS_ALLOWED)!=0)
            {
                mNoOfBackDays = (int)OustPreferences.getTimeForNotification(AppConstants.StringConstants.NO_OF_BACK_DAYS_ALLOWED);
            }
            else
            {
                mNoOfBackDays = 365;
            }

            isPreviousDateAllowed = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.IS_PREV_DATE_ALLOWED);
            isFutureDateAllowed = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.IS_FUTURE_DATE_ALLOWED);
            isStartDateFreeze = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.FREEZE_WORK_DIARY_START_DATE);

            try {
                if (toolbarColorCode != null) {
                    //mBackgroundDrawable = getResources().getDrawable(R.drawable.round_shape);
                    //mBackgroundDrawable.setColorFilter(Color.parseColor(toolbarColorCode), PorterDuff.Mode.SRC_IN);
                    floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(toolbarColorCode)));
                    mProgressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor(toolbarColorCode), PorterDuff.Mode.SRC_IN);
                } else {
                    floatingActionButton.setBackgroundColor(getResources().getColor(R.color.lgreen));
                    mProgressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.lgreen), PorterDuff.Mode.SRC_IN);
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

            try {
                if (toolbarColorCode != null) {
                    int color = Color.parseColor(toolbarColorCode);
                    DrawableCompat.setTintList(DrawableCompat.wrap(floatingActionButton.getBackground()), ColorStateList.valueOf(color));
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

            setUpActionBar();

            mStudentId = getIntent().getStringExtra("studentId");
            mName = getIntent().getStringExtra("Name");
            mAvatarLink = getIntent().getStringExtra("avatar");
            mPresenter.getFilterData();
            setProfileData();
            setCoinsData();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setUpActionBar() {
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            String diary = ""+getResources().getString(R.string.my_diary);
            mActionBar.setTitle(diary);
            mActionBar.setDisplayHomeAsUpEnabled(true);
            try {
                if (toolbarColorCode != null) {
                    mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(toolbarColorCode)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

        }
    }

    @Override
    protected void initListener() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showCreateManualEntryDialog();
                ShowAddTopicAlert(new DTODiaryDetailsModel(), false);
            }
        });
        mRelativeLayoutClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mRelativeLayoutClose.setOnClickListener(view -> onBackPressed());
        if (!OustSdkTools.hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        OustSdkApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void showPopUpWindow() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.fragment_create_activity, null);
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = false; // lets taps outside the popup also dismiss it
        mPopUpWindow = new PopupWindow(popupView, width, height, focusable);
        mPopUpWindow.showAtLocation(constraintLayout, Gravity.CENTER, 0, 0);
        /*popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mPopUpWindow.dismiss();
                return true;
            }
        });*/
    }

    private void setCoinsData() {
        mTextViewCertificatesText.setText(getResources().getString(R.string.certificates_text));
        mTextViewCoinsText.setText(getResources().getString(R.string.score_text));
        mTextViewRankText.setText(getResources().getString(R.string.rank));

        mTextViewCertificates.setText("345");
        mTextViewCoins.setText("4567");
        mTextViewRank.setText("3");
    }

    private void setProfileData() {
        String activeUserGet = OustPreferences.get("userdata");
        activeUser = OustSdkTools.getActiveUserData(activeUserGet);
        if (mStudentId == null) {
            mStudentId = activeUser.getStudentid();
            mAvatarLink = activeUser.getAvatar();
            mName = activeUser.getUserDisplayName();
        }
        if(activeUser!=null && activeUser.getStudentKey()!=null)
        {
            mPresenter.getLastEntryDate(activeUser.getStudentKey());
        }
        if (mName != null) {
            mTextViewProfileName.setText(mName);
        }
        //mTextViewLocation.setText("Bangalore");
        //mTextViewDesignation.setText("Android Developer");
        if (mAvatarLink != null) {
            Picasso.get().load(mAvatarLink).into(mCircleImageViewAvatar);

            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    mImageViewProfileBg.setImageBitmap(BlurImage.fastblur(bitmap, 1f, BLUR_PRECENTAGE));
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    //mImageViewProfileBg.setImageResource(R.drawable.dummy_img);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };

            Picasso.get()
                    .load(mAvatarLink)
                 /*   .error(R.drawable.dummy_img)
                    .placeholder(R.drawable.dummy_img)*/
                    .into(target);
        }

    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(LearningDiaryActivity.this);
        builder.setTitle("Select Attachment!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean gallery = OustSdkTools.checkPermission(LearningDiaryActivity.this);
                boolean camera = OustSdkTools.checkPermissionCamera(LearningDiaryActivity.this);
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

    @Override
    public void selectedPosition(int position) {
        selectedPositionOfFilter = position;
        diaryFilteredList = new ArrayList<>();
        if (diaryDetailsModels != null) {
            if (position == 1) {
                enableDisableFAB(false);
            } else {
                enableDisableFAB(true);
            }
            if (position == 0) {
                if (diaryDetailsModels.size() == 0) {
                    mTextViewNoData.setVisibility(View.VISIBLE);
                    mRecyclerViewDiary.setVisibility(View.GONE);
                } else {
                    mTextViewNoData.setVisibility(View.GONE);
                    mRecyclerViewDiary.setVisibility(View.VISIBLE);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(LearningDiaryActivity.this);
                    mRecyclerViewDiary.setLayoutManager(mLayoutManager);
                    mRecyclerViewDiary.setItemAnimator(new DefaultItemAnimator());
                    learningDiaryAdapter = new LearningDiaryAdapter(this, diaryDetailsModels);
                    mRecyclerViewDiary.setAdapter(learningDiaryAdapter);
                    isAll = true;
                }
            } else {
                for (int i = 0; i < diaryDetailsModels.size(); i++) {
                    if (diaryDetailsModels.get(i).getType() == position) {
                        diaryFilteredList.add(diaryDetailsModels.get(i));
                    }
                }
                if (diaryFilteredList.size() == 0) {
                    mTextViewNoData.setVisibility(View.VISIBLE);
                    mRecyclerViewDiary.setVisibility(View.GONE);
                } else {
                    mTextViewNoData.setVisibility(View.GONE);
                    mRecyclerViewDiary.setVisibility(View.VISIBLE);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(LearningDiaryActivity.this);
                    mRecyclerViewDiary.setLayoutManager(mLayoutManager);
                    mRecyclerViewDiary.setItemAnimator(new DefaultItemAnimator());
                    learningDiaryAdapter = new LearningDiaryAdapter(this, diaryFilteredList);
                    mRecyclerViewDiary.setAdapter(learningDiaryAdapter);
                    isAll = false;
                }
            }
        }
    }

    private void enableDisableFAB(boolean b) {
        if (b) {
            floatingActionButton.show();
        } else {
            floatingActionButton.hide();
        }
    }

    private void showCreateManualEntryDialog() {
        FragmentManager fm = getSupportFragmentManager();
        CreateActivityFragment editNameDialogFragment = CreateActivityFragment.newInstance("Some Title");
        editNameDialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.RatingDialog2);
        editNameDialogFragment.show(fm, "fragment_edit_name");
    }

    private void ShowAddTopicAlert(final DTODiaryDetailsModel detailsModel, final boolean isEdit) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(LearningDiaryActivity.this);
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.add_ld_manually, null);
            mLinearLayoutBottomNext = popupView.findViewById(R.id.linearLayoutNext);
            mLinearLayoutTitle = popupView.findViewById(R.id.linearLayoutTitle);
            mEditTextStartDate = popupView.findViewById(R.id.editTextStartDate);
            mEditTextEndDate = popupView.findViewById(R.id.editTextEndDate);
            mEditTextComment = popupView.findViewById(R.id.editTextComment);
            mEditTextTopic = popupView.findViewById(R.id.editTextActivity);
            mTextViewAttachmentLabel = popupView.findViewById(R.id.textViewAttachmentLabel);
            mImageViewDetach = popupView.findViewById(R.id.imageViewDetach);
            mLinearLayoutAttachmentLabel = popupView.findViewById(R.id.linearLayoutAttachmentUplaod);
            mTextViewNext = popupView.findViewById(R.id.textViewNext);
            mTextViewEntryTitle = popupView.findViewById(R.id.textViewTitle);
            String title = ""+getResources().getString(R.string.add_entry);
            mTextViewEntryTitle.setText(title);

            disableEditText(mEditTextStartDate);
            disableEditText(mEditTextEndDate);

            mEditTextTopic.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (i2 > 150) {
                        mEditTextTopic.setError(getString(R.string.not_exceed_150_chars));
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (mEditTextTopic.getText().toString().length() > 150) {
                        mEditTextTopic.setError(getString(R.string.not_exceed_150_chars));
                    }
                }
            });

            mEditTextComment.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (i2 > 300) {
                        mEditTextComment.setError(getString(R.string.not_exceed_300_chars));
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (mEditTextComment.getText().toString().length() > 300) {
                        mEditTextComment.setError(getString(R.string.not_exceed_300_chars));
                    }
                }
            });

            if (toolbarColorCode != null) {
                mLinearLayoutTitle.setBackgroundColor(Color.parseColor(toolbarColorCode));
                mLinearLayoutBottomNext.setBackgroundColor(Color.parseColor(toolbarColorCode));
               /* mEditTextComment.setHintTextColor(Color.parseColor(toolbarColorCode));
                mEditTextTopic.setHintTextColor(Color.parseColor(toolbarColorCode));
                mEditTextEndDate.setHintTextColor(Color.parseColor(toolbarColorCode));
                mEditTextStartDate.setHintTextColor(Color.parseColor(toolbarColorCode));*/
            }
            mImageViewDetach.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isUploadingImage) {
                        filename = null;
                        fileSize = 0;
                        isMediaChanged = true;
                        isMediaDeleted = true;
                        if (learningDiaryMediaDataList != null) {
                            learningDiaryMediaDataList = null;
                        }
                        mLinearLayoutAttachmentLabel.setVisibility(View.GONE);
                    } else {
                        OustSdkTools.showToast(getString(R.string.please_wait_uploading));
                    }

                }
            });
            mEditTextStartDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(LearningDiaryActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            String dd = "", mm = "";
                            if (i2 < 10) {
                                dd = "0" + i2;
                            } else {
                                dd = "" + i2;
                            }
                            if (i1 < 9) {
                                mm = "0" + (i1 + 1);
                            } else {
                                mm = "" + (i1 + 1);
                            }
                            mEditTextStartDate.setText(dd + "/" + mm + "/" + i);
                        }
                    }, year, month, day);
                    if(!isFutureDateAllowed)
                        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

                    long defaultDate = 0;

                    if(isPreviousDateAllowed){
                        latestEntryInMill = 0;
                    }
                    if(!isPreviousDateAllowed)
                    {
                        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-2000);
                    }
                    else if(latestEntryInMill!=0){
                        long timeDiff = System.currentTimeMillis()-latestEntryInMill;
                        int noOfDaysGap = (int)(timeDiff/MILLI_SEC_PER_DAY);
                        if(mNoOfBackDays==0){
                            noOfDaysBackToBeShown = noOfDaysGap;
                        }
                        else if(noOfDaysGap>mNoOfBackDays){
                            noOfDaysBackToBeShown = mNoOfBackDays;
                        }
                        else if(mNoOfBackDays>noOfDaysGap)
                        {
                            noOfDaysBackToBeShown = noOfDaysGap;
                        }
                        else
                        {
                            noOfDaysBackToBeShown = noOfDaysGap;
                        }
                        long milli=0;
                        if(noOfDaysBackToBeShown>1)
                        {
                            milli = (noOfDaysBackToBeShown-1) * MILLI_SEC_PER_DAY;
                        }
                        else
                        {
                            milli = (noOfDaysBackToBeShown) * MILLI_SEC_PER_DAY;
                        }

                        if(milli==0)
                            milli=2000;
                        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-milli);
                        if(isStartDateFreeze)
                        {
                            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()-milli);
                        }

                        defaultDate = System.currentTimeMillis()-milli;
                    }
                    else if(mNoOfBackDays>0)
                    {
                        long milli = (mNoOfBackDays-1) * MILLI_SEC_PER_DAY;
                        if(milli==0)
                            milli=2000;
                        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-milli);
                        defaultDate = System.currentTimeMillis()-milli;
                    }

                    if(defaultDate!=0) {
                        int[] ar = getDates(defaultDate);
                        datePickerDialog.updateDate(ar[0], ar[1], ar[2]);
                    }
                    datePickerDialog.show();
                }
            });

            mEditTextEndDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(LearningDiaryActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            String dd = "", mm = "";
                            if (i2 < 10) {
                                dd = "0" + i2;
                            } else {
                                dd = "" + i2;
                            }
                            if (i1 < 9) {
                                mm = "0" + (i1 + 1);
                            } else {
                                mm = "" + (i1 + 1);
                            }
                            mEditTextEndDate.setText(dd + "/" + mm + "/" + i);
                        }
                    }, year, month, day);

                    if(!isFutureDateAllowed)
                        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

                    if(!isPreviousDateAllowed)
                    {
                        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-2000);
                    }

                    datePickerDialog.show();
                }
            });


            alertProgressBar = popupView.findViewById(R.id.progressBarAlert);
            uploadProgressBar = popupView.findViewById(R.id.uploadProgressBar);

            if(toolbarColorCode.equalsIgnoreCase(""))
            {
                toolbarColorCode = "#01b5a2";
            }
            try {
                if (toolbarColorCode != null && !toolbarColorCode.equalsIgnoreCase(""))
                {
                    alertProgressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor(toolbarColorCode), PorterDuff.Mode.SRC_IN);
                    uploadProgressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor(toolbarColorCode), PorterDuff.Mode.SRC_IN);

                } else {
                    alertProgressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.lgreen), PorterDuff.Mode.SRC_IN);
                    uploadProgressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.lgreen), PorterDuff.Mode.SRC_IN);
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

            ImageView imageViewAttachmentClip = popupView.findViewById(R.id.imageViewClip);
            imageViewAttachmentClip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isUploadingImage) {
                        if (mLinearLayoutAttachmentLabel.getVisibility() == View.GONE) {
                            selectImage();
                        } else {
                            OustSdkTools.showToast(getString(R.string.delete_attachment));
                        }
                    } else {
                        OustSdkTools.showToast(getString(R.string.please_wait_uploading));
                    }
                }
            });

            mLinearLayoutBottomNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isUploadingImage) {
                        if (isEdit) {
                            UpdateData();
                        } else {
                            sendData();
                        }
                    } else {
                        OustSdkTools.showToast(getString(R.string.please_wait_uploading));
                    }

                }
            });

            ImageView imageViewClose = popupView.findViewById(R.id.imageViewClose);
            imageViewClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mAlertDialog != null) {
                        mAlertDialog.dismiss();
                        mAlertDialog = null;
                    }
                }
            });

            if (isEdit) {
                Log.d(TAG, "ShowAddTopicAlert: endTS:");
                mEditTextStartDate.setText(OustSdkTools.timeStampToDateDDMMYYYY(Long.valueOf(detailsModel.getStartTS())));
                mEditTextEndDate.setText(OustSdkTools.timeStampToDateDDMMYYYY(Long.valueOf(detailsModel.getEndTS())));
                mEditTextTopic.setText(detailsModel.getActivity());
                mEditTextComment.setText(detailsModel.getComments());
                mTextViewNext.setText(getString(R.string.save));
                mTextViewEntryTitle.setText(getString(R.string.edit));
                try {
                    if (detailsModel.getLearningDiaryMediaDataList() != null) {
                        if (detailsModel.getLearningDiaryMediaDataList().get(0) != null) {
                            if (detailsModel.getLearningDiaryMediaDataList().get(0).getFileName() != null) {
                                filename = detailsModel.getLearningDiaryMediaDataList().get(0).getFileName();
                                mTextViewAttachmentLabel.setText(filename);
                                mLinearLayoutAttachmentLabel.setVisibility(View.VISIBLE);

                            }
                            if (detailsModel.getLearningDiaryMediaDataList().get(0).getFileSize() != null) {
                                fileSize = Long.valueOf(detailsModel.getLearningDiaryMediaDataList().get(0).getFileSize().replace("KB", ""));
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

            builder.setView(popupView);
            mAlertDialog = builder.create();
            mAlertDialog.show();
            mAlertDialog.setCancelable(false);
            //showKeyboard();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private int[] getDates(long milli){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(milli);
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        int hr = c.get(Calendar.HOUR);
        int min = c.get(Calendar.MINUTE);
        int sec = c.get(Calendar.SECOND);
        int[] ar = new int[3];
        ar[0] = mYear;
        ar[1] = mMonth;
        ar[2] = mDay;

        return ar;
    }

    private void sendData() {
        try {
            DTODiaryDetailsModel diaryDetailsModel = new DTODiaryDetailsModel();
            if (mEditTextStartDate.getText() != null && !mEditTextStartDate.getText().toString().isEmpty()) {
                diaryDetailsModel.setStartTS(convertToTimestamp(mEditTextStartDate.getText().toString().trim()));
            } else {
                OustSdkTools.showToast(getString(R.string.startdate_error));
                return;
            }
            if (mEditTextEndDate.getText() != null && !mEditTextEndDate.getText().toString().isEmpty()) {
                diaryDetailsModel.setEndTS(convertToTimestamp(mEditTextEndDate.getText().toString().trim()));
            } else {
                OustSdkTools.showToast(getString(R.string.end_date_error));
                return;
            }
            if(MaxDateRange!=0) {
                if ((convertToTimestampInMilli(mEditTextEndDate.getText().toString().trim()) - (convertToTimestampInMilli(mEditTextStartDate.getText().toString().trim()))) > MaxDateRange) {
                    OustSdkTools.showToast("Start date and End date can't be more than " +(int) (MaxDateRange/MILLI_SEC_PER_DAY) +" Day(s)");
                    return;
                }
            }

            if (mEditTextTopic.getText() != null && !mEditTextTopic.getText().toString().trim().isEmpty()) {
                diaryDetailsModel.setActivity(mEditTextTopic.getText().toString().trim());
            } else {
                mEditTextTopic.setError(getString(R.string.topic_error_msg));
                return;
            }

            if (mEditTextComment.getText() != null && !mEditTextComment.getText().toString().trim().isEmpty()) {
                diaryDetailsModel.setComments(mEditTextComment.getText().toString().trim());
            } else {
                mEditTextComment.setError(getString(R.string.comment_error_msg));
                return;
            }
            if (Long.valueOf(diaryDetailsModel.getStartTS()) > Long.valueOf(diaryDetailsModel.getEndTS())) {
                OustSdkTools.showToast(getString(R.string.startdate_less_endate));
                return;
            }
            if (learningDiaryMediaDataList != null) {
                mediaDataLists.add(0, learningDiaryMediaDataList);
            }
            if (OustSdkTools.checkInternetStatus()) {
                mPresenter.AddManualData(diaryDetailsModel, activeUser.getStudentid(), mediaDataLists);
                mLinearLayoutBottomNext.setClickable(false);
            } else {
                OustSdkTools.showToast(getString(R.string.no_internet_connection));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void UpdateData() {
        try {
            DTODiaryDetailsModel diaryDetailsModel = new DTODiaryDetailsModel();
            // diaryDetailsModel = editModel;
            boolean isValidData = false;

            if (mEditTextEndDate.getText() != null && !mEditTextEndDate.getText().toString().trim().isEmpty()) {
                diaryDetailsModel.setEndTS(convertToTimestamp(mEditTextEndDate.getText().toString().trim()));
                isValidData = true;
            } else {
                OustSdkTools.showToast(getString(R.string.end_date_error));
                isValidData = false;
                return;
            }
            if(MaxDateRange!=0) {
                if ((convertToTimestampInMilli(mEditTextEndDate.getText().toString().trim()) - (convertToTimestampInMilli(mEditTextStartDate.getText().toString().trim()))) > MaxDateRange) {
                    OustSdkTools.showToast("Start date and End date can't be more than " +(int) (MaxDateRange/MILLI_SEC_PER_DAY) +" Day(s)");
                    return;
                }
            }
            if (mEditTextStartDate.getText() != null && !mEditTextStartDate.getText().toString().trim().isEmpty()) {
                diaryDetailsModel.setStartTS(convertToTimestamp(mEditTextStartDate.getText().toString().trim()));
                isValidData = true;
            } else {
                OustSdkTools.showToast(getString(R.string.startdate_error));
                isValidData = false;
                return;
            }

            if (mEditTextTopic.getText() != null && !mEditTextTopic.getText().toString().trim().isEmpty()) {
                diaryDetailsModel.setActivity(mEditTextTopic.getText().toString().trim());
                isValidData = true;
            } else {
                mEditTextTopic.setError(getString(R.string.topic_error_msg));
                isValidData = false;
                return;
            }

            if (mEditTextComment.getText() != null && !mEditTextComment.getText().toString().trim().isEmpty()) {
                diaryDetailsModel.setComments(mEditTextComment.getText().toString().trim());
                isValidData = true;
            } else {
                mEditTextComment.setError(getString(R.string.comment_error_msg));
                isValidData = false;
                return;
            }
            if (Long.valueOf(diaryDetailsModel.getStartTS()) > Long.valueOf(diaryDetailsModel.getEndTS())) {
                OustSdkTools.showToast(getString(R.string.startdate_less_endate));
                isValidData = false;
                return;
            }

            if (!countNoOfCharacters(mEditTextTopic, 1)) {
                mEditTextTopic.setError(getString(R.string.not_exceed_150_chars));
                isValidData = false;
                return;
            }

            if (!countNoOfCharacters(mEditTextComment, 2)) {
                mEditTextComment.setError(getString(R.string.not_exceed_300_chars));
                isValidData = false;
                return;
            }
            diaryDetailsModel.setUserLD_Id(editModel.getUserLD_Id());

            mediaDataLists = new ArrayList<>();
            diaryDetailsModel.setLearningDiaryMediaDataList(editModel.getLearningDiaryMediaDataList());
            if (diaryDetailsModel.getLearningDiaryMediaDataList() != null) {
                for (int i = 0; i < diaryDetailsModel.getLearningDiaryMediaDataList().size(); i++) {
                    LearningDiaryMediaDataList object = new LearningDiaryMediaDataList();
                    object.setUserLdMedia_Id(diaryDetailsModel.getLearningDiaryMediaDataList().get(i).getUserLdMedia_Id());
                    object.setFileType(diaryDetailsModel.getLearningDiaryMediaDataList().get(i).getFileType());
                    object.setFileSize(diaryDetailsModel.getLearningDiaryMediaDataList().get(i).getFileSize());
                    object.setFileName(diaryDetailsModel.getLearningDiaryMediaDataList().get(i).getFileName());
                    if (learningDiaryMediaDataList != null) {
                        object.setChanged(true);
                    } else {
                        object.setChanged(false);
                    }
                    mediaDataLists.add(object);
                }
                //add new file to list
                if (learningDiaryMediaDataList != null) {
                    mediaDataLists.add(learningDiaryMediaDataList);
                }
            }
            else {
                if (learningDiaryMediaDataList != null) {
                    mediaDataLists.add(learningDiaryMediaDataList);
                }
            }
            //this is if the attachment is deleted
            if (mediaDataLists.size() == 1 && isMediaDeleted) {
                mediaDataLists.get(0).setChanged(true);
            }
            if (OustSdkTools.checkInternetStatus() && isValidData) {
                mPresenter.UpdateManualData(diaryDetailsModel, activeUser.getStudentid(), mediaDataLists, isMediaChanged);
                mLinearLayoutBottomNext.setClickable(false);
            } else {
                OustSdkTools.showToast(getString(R.string.no_internet_connection));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean countNoOfCharacters(EditText editText, int type) {
        if (type == 1) {
            return editText.getText().toString().length() <= 150;
        } else {
            return editText.getText().toString().length() <= 300;
        }
    }

    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setCursorVisible(false);
    }

    private String convertToTimestamp(String date) {

        Date initDate = null;
        try {
            initDate = new SimpleDateFormat("dd/mm/yyyy").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        SimpleDateFormat formatter = new SimpleDateFormat("mm/dd/yyyy");
        String parsedDate = formatter.format(initDate);

        Timestamp ts = null;  //declare timestamp
        Date d = null; // Intialize date with the string date
        d = new Date(parsedDate);
        if (d != null) {  // simple null check
            ts = new java.sql.Timestamp(d.getTime()); // convert gettime from date and assign it to your timestamp.
        }
        /*
        Calendar calendar1 = new GregorianCalendar();
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);
        long fromMidNight = System.currentTimeMillis() - calendar1.getTimeInMillis();
        long totaltime = ts.getTime()+fromMidNight;
        */
        return ts.getTime() + "";
    }

    private long convertToTimestampInMilli(String date) {

        Date initDate = null;
        try {
            initDate = new SimpleDateFormat("dd/mm/yyyy").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        SimpleDateFormat formatter = new SimpleDateFormat("mm/dd/yyyy");
        String parsedDate = formatter.format(initDate);

        Timestamp ts = null;  //declare timestamp
        Date d = null; // Intialize date with the string date
        d = new Date(parsedDate);
        if (d != null) {  // simple null check
            ts = new java.sql.Timestamp(d.getTime()); // convert gettime from date and assign it to your timestamp.
        }
        /*
        Calendar calendar1 = new GregorianCalendar();
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);
        long fromMidNight = System.currentTimeMillis() - calendar1.getTimeInMillis();
        long totaltime = ts.getTime()+fromMidNight;
        */
        return ts.getTime();
    }

    @Override

    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void showProgressBar(int type) {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar(int type) {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onError(String error) {
        Log.d(TAG, "onError: ");
        OustSdkTools.showToast(getString(R.string.something_went_wrong));
    }

    boolean isValueChecked;
    @Override
    public void updateDataFromAPI(List<DTODiaryDetailsModel> diaryDetailsModelList, int totalCount, int type) {
        if (type == 1) {
            APIDataList = new ArrayList<>();
            APIDataList.addAll(diaryDetailsModelList);
        } else if (type == 2) {
            FBDataList = new ArrayList<>();
            FBDataList.addAll(diaryDetailsModelList);

            for(int j = 0; j<FBDataList.size(); j++){
                if(FBDataList.get(j).getType()==2)
                {
                    if(FBDataList.get(j).getEndTS()!=null) {
                        latestEntryInMill = Long.parseLong(FBDataList.get(j).getEndTS());
                        break;
                    }
                }
            }

            for(int  i = 1; i<FBDataList.size(); i++){
                if(FBDataList.get(i).getType()==2)
                {
                    if(FBDataList.get(i).getEndTS()!=null) {
                        if (latestEntryInMill < Long.parseLong(FBDataList.get(i).getEndTS())) {
                            latestEntryInMill = Long.parseLong(FBDataList.get(i).getEndTS());
                        }
                    }

                }
            }

        }

        if (APIDataList != null && APIDataList.size() > 0) {
            if (diaryDetailsModels == null)
                diaryDetailsModels = new ArrayList<>();
            diaryDetailsModels.clear();
            diaryDetailsModels.addAll(APIDataList);
        }
        if (FBDataList != null && diaryDetailsModels != null) {
            for (int i = 0; i < FBDataList.size(); i++) {
                for (int j = 0; j < diaryDetailsModels.size(); j++) {
                    if (FBDataList.get(i).getUserLD_Id().equalsIgnoreCase(diaryDetailsModels.get(j).getUserLD_Id())) {
                        diaryDetailsModels.remove(j);
                    }
                }
            }
        }
        if (FBDataList != null && FBDataList.size() > 0) {
            if (diaryDetailsModels == null)
                diaryDetailsModels = new ArrayList<>();
            diaryDetailsModels.addAll(FBDataList);
        }
        if (diaryDetailsModels == null || diaryDetailsModels.size() == 0) {
            mTextViewNoData.setVisibility(View.VISIBLE);
            mRecyclerViewDiary.setVisibility(View.GONE);
        } else {
            Collections.sort(diaryDetailsModels, new Comparator<DTODiaryDetailsModel>() {
                @Override
                public int compare(DTODiaryDetailsModel o1, DTODiaryDetailsModel o2) {
                    return o2.getSortingTime().compareTo(o1.getSortingTime());
                }
            });

            mTextViewCertificates.setText("" + diaryDetailsModels.size());
            mRecyclerViewDiary.setVisibility(View.VISIBLE);
            mTextViewNoData.setVisibility(View.GONE);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(LearningDiaryActivity.this);
            mRecyclerViewDiary.setLayoutManager(mLayoutManager);
            mRecyclerViewDiary.setItemAnimator(new DefaultItemAnimator());
            learningDiaryAdapter = new LearningDiaryAdapter(this, diaryDetailsModels);
            mRecyclerViewDiary.setAdapter(learningDiaryAdapter);
            learningDiaryAdapter.notifyDataSetChanged();
            isAll = true;
        }
        isAll = true;
        selectedPosition(selectedPositionOfFilter);
        diaryFilterAdapter.notifyDataSetChanged();
    }

    @Override
    public void hideAlertDialog() {
        alertProgressBar.setVisibility(View.GONE);
        mAlertDialog.dismiss();
        mAlertDialog = null;
    }

    @Override
    public void showAlertProgressBar() {
        alertProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideAlertProgressbar() {
        alertProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void successDelete(String userLD_id) {
        OustSdkTools.showToast(getString(R.string.deleted_success));
        if (diaryDetailsModels != null) {
            for (int i = 0; i < diaryDetailsModels.size(); i++) {
                if (diaryDetailsModels.get(i).getUserLD_Id().equalsIgnoreCase(userLD_id))
                    diaryDetailsModels.remove(i);
            }
        }
        if (diaryFilteredList != null) {
            for (int i = 0; i < diaryFilteredList.size(); i++) {
                if (diaryFilteredList.get(i).getUserLD_Id().equalsIgnoreCase(userLD_id))
                    diaryFilteredList.remove(i);
            }
        }
        learningDiaryAdapter.notifyDataSetChanged();

    }

    @Override
    public void successUpdate() {
        if (learningDiaryAdapter != null) {
            learningDiaryAdapter.notifyDataSetChanged();
        }
        if (learningDiaryMediaDataList != null) {
            learningDiaryMediaDataList = null;
        }
        if (mediaDataLists != null) {
            mediaDataLists.clear();
        }
        isMediaChanged = false;
        isMediaDeleted = false;
        if(mLinearLayoutBottomNext!=null)
        {
            mLinearLayoutBottomNext.setClickable(true);
        }
        OustSdkTools.showToast(getString(R.string.updated_success));
    }

    @Override
    public void successAdded() {
        if (learningDiaryMediaDataList != null) {
            learningDiaryMediaDataList = null;
        }
        if (mediaDataLists != null) {
            mediaDataLists.clear();
        }
        if(mLinearLayoutBottomNext!=null)
        {
            mLinearLayoutBottomNext.setClickable(true);
        }
        OustSdkTools.showToast(getString(R.string.added_success));
    }

    @Override
    public void failureAdded() {
        if(mLinearLayoutBottomNext!=null)
        {
            mLinearLayoutBottomNext.setClickable(true);
        }
        OustSdkTools.showToast(getString(R.string.unable_add_now));
    }

    @Override
    public void failureUpdate() {
        if(mLinearLayoutBottomNext!=null)
        {
            mLinearLayoutBottomNext.setClickable(true);
        }
        OustSdkTools.showToast(getString(R.string.unable_update_now));
    }

    @Override
    public void updateFilters(List<FilterModel> filterList) {
        try {
            diaryFilterAdapter = new DiaryFilterAdapter(this, filterList);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            mListViewFilter.setLayoutManager(linearLayoutManager);
            mListViewFilter.setAdapter(diaryFilterAdapter);
            if (activeUser != null) {
                if (activeUser.getStudentid() != null && activeUser.getStudentKey() != null) {
                    mPresenter.getManualData(activeUser.getStudentid(), activeUser.getStudentKey());
                }
            }
            if (OustSdkTools.checkInternetStatus()) {
                mPresenter.getUserContentFromAPI(mStudentId,LearningDiaryActivity.this);
            } else {
                mProgressBar.setVisibility(View.GONE);
                mPresenter.extractOfflineData(mStudentId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    @Override
    public void failureFilterData() {

    }

    public static Map<String, DTODiaryDetailsModel> listToHashmap(List<DTODiaryDetailsModel> detailsModelList) {
        Map<String, DTODiaryDetailsModel> detailsModelMap = new HashMap<String, DTODiaryDetailsModel>();
        for (DTODiaryDetailsModel detailsModel : detailsModelList) {
            if (detailsModel.getType() == 2) {
                if (detailsModelMap.get(detailsModel.getUserLD_Id()) != null) {
                    detailsModelMap.remove(detailsModel.getUserLD_Id());
                    detailsModelMap.put(detailsModel.getUserLD_Id(), detailsModel);
                } else {
                    detailsModelMap.put(detailsModel.getUserLD_Id(), detailsModel);
                }
            } else {
                detailsModelMap.put(detailsModel.getUserLD_Id(), detailsModel);
            }
        }
        return detailsModelMap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        try {
            if (requestCode == PERMISSION_ALL) {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                    }
                }
                if ("Choose from Library".equalsIgnoreCase(userChoosenTask))
                    galleryIntent();
                else if ("Take Photo".equalsIgnoreCase(userChoosenTask)) {
                    cameraIntent();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
     /*   switch (requestCode) {
            case OustSdkTools.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                try {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    {
                       if ("Choose from Library".equalsIgnoreCase(userChoosenTask))
                            galleryIntent();
                    }
                    else {
                        Toast.makeText(this, "Permission is Mandatory to select attachment", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                break;
            case OustSdkTools.CAMERA_PERMISSION:
                try {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    {
                        if ("Take Photo".equalsIgnoreCase(userChoosenTask))
                            cameraIntent();
                    }
                    else {
                        Toast.makeText(this, "Permission is Mandatory to Capture image", Toast.LENGTH_LONG).show();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
        }*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onSelectFromGalleryResult(Intent data) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Uri path = data.getData();
        if (path != null) {
            Log.d(TAG, "onSelectFromGalleryResult: URI:" + path.toString());
            if (path.toString().contains("com.google.android.apps.photos")) {
                OustSdkTools.showToast("can't select attachment from google photos app");
                return;
            }
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            String filePath = FilePath.getRealPathFromUri(LearningDiaryActivity.this,path);
            if (filePath != null) {
                getUploadConfirm(new File(filePath));
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
                    getUploadConfirm(new File(result));
                }
            }
        }
    }

    private void getUploadConfirm(final File file) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View mView = LayoutInflater.from(this).inflate(R.layout.image_upload_confirm_lyt, null);
        builder.setView(mView);
        TextView textViewTitle = mView.findViewById(R.id.textViewTitle);
        TextView textViewMessage = mView.findViewById(R.id.textViewMessage);
        LinearLayout mLinearLayoutRetry, mLinearLayoutOk;
        mLinearLayoutRetry = mView.findViewById(R.id.linearLayoutRetry);
        mLinearLayoutOk = mView.findViewById(R.id.linearLayoutConfirm);
        textViewTitle.setText(getString(R.string.confirmation));
        textViewMessage.setText(getString(R.string.upload_confirm_msg));
        mLinearLayoutRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAlertDialog2.dismiss();
                selectImage();
            }
        });
        mLinearLayoutOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File filePath = CompressFile.getCompressedImageFile(file, LearningDiaryActivity.this);
                if (filePath != null) {
                    if (OustSdkTools.checkInternetStatus()) {
                        uploadToAWS(filePath);
                        //uploadToAWS(file);
                        mAlertDialog2.dismiss();
                    } else {
                        OustSdkTools.showToast(getString(R.string.no_internet_connection));
                    }
                } else {
                    OustSdkTools.showToast(getString(R.string.unable_to_select_attachment));
                }
            }
        });
        mAlertDialog2 = builder.create();
        mAlertDialog2.show();
        mAlertDialog2.setCancelable(false);
    }

    private void uploadComplete(boolean value) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LearningDiaryActivity.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View mView = LayoutInflater.from(this).inflate(R.layout.upload_fail_success, null);
        builder.setView(mView);
        imageViewClose = mView.findViewById(R.id.imageViewClose);
        TextView mTextViewTitle = mView.findViewById(R.id.textViewTitle);
        TextView mTextViewMessage = mView.findViewById(R.id.textViewMessage);
        mLinearLayoutRetry = mView.findViewById(R.id.linearLayoutRetry);
        mLinearLayoutOk = mView.findViewById(R.id.linearLayoutOK);
        if (value) {
            mTextViewTitle.setText(getString(R.string.upload_complete_text));
            mTextViewMessage.setText(getString(R.string.media_upload_completed));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.weight = 2.0f;
            params.bottomMargin = 0;
            params.gravity = BOTTOM;
            mLinearLayoutOk.setLayoutParams(params);
        } else {
            mTextViewTitle.setText(getString(R.string.upload_failed));
            mTextViewMessage.setText(getString(R.string.sorry_media_upload_failed));
        }
        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAlertDialogFail != null && mAlertDialogFail.isShowing()) {
                    mAlertDialogFail.dismiss();
                }
            }
        });
        mLinearLayoutOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAlertDialogFail != null && mAlertDialogFail.isShowing()) {
                    mAlertDialogFail.dismiss();
                }
            }
        });

        mLinearLayoutRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAlertDialogFail.dismiss();
                if (OustSdkTools.checkInternetStatus()) {
                    if (savedImageFile != null) {
                        uploadToAWS(savedImageFile);
                    }
                } else {
                    OustSdkTools.showToast(getString(R.string.no_internet_connection));
                }
            }
        });

        mAlertDialogFail = builder.create();
        if (!mAlertDialogFail.isShowing()) {
            mAlertDialogFail.show();
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
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
            getUploadConfirm(destination);
        }
    }

    public String compressImage(String imageUri) {

        String filePath = getRealPathFromURI3(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        return filename;

    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    private String getRealPathFromURI3(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ");
        super.onBackPressed();
    }

    private void galleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void uploadToAWS(final File recorded_file) {
        try {
            final float percentage = 2.0f;
            isUploadingImage = true;
            savedImageFile = recorded_file;
            ClientConfiguration clientConfiguration = new ClientConfiguration();
            clientConfiguration.setRetryPolicy(new RetryPolicy(null, null, 1, true));
            String awsKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeyId");
            String awsSecretKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeySecret");
            AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(awsKeyId, awsSecretKeyId),clientConfiguration);
            s3.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
            TransferUtility transferUtility = new TransferUtility(s3, this);
            if (recorded_file == null || (recorded_file != null && !recorded_file.exists())) {
                Toast.makeText(this, "File Not Found!", Toast.LENGTH_SHORT).show();
                //mediaupload_progressbar.setVisibility(View.GONE);
                return;
            }
            filename = System.currentTimeMillis() + "_" + activeUser.getStudentKey();
            fileSize = recorded_file.length() / 1024;
            Log.d(TAG, "uploadToAWS:fileSize: " + fileSize + "KB");
            uploadProgressBar.setVisibility(View.VISIBLE);
            uploadProgressBar.setProgress(5);
            final TransferObserver observer = transferUtility.upload(AppConstants.StringConstants.S3_BUCKET_NAME, AppConstants.StringConstants.S3_IMAGE_FOLDER + filename + ".jpg", recorded_file);
            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (TransferState.COMPLETED.equals(observer.getState())) {
                        learningDiaryMediaDataList = new LearningDiaryMediaDataList();
                        mediaDataLists = new ArrayList<>();
                        if (!filename.contains(".jpg")) {
                            learningDiaryMediaDataList.setFileName(filename + ".jpg");
                            filename = filename + ".jpg";
                        }
                        learningDiaryMediaDataList.setFileType("IMAGE");
                        learningDiaryMediaDataList.setFileSize(fileSize + "KB");
                        learningDiaryMediaDataList.setChanged(true);
                        learningDiaryMediaDataList.setUserLdMedia_Id(0);
                        isMediaChanged = true;
                        //uploadComplete(true);
                        uploadProgressBar.setVisibility(View.GONE);
                        mLinearLayoutAttachmentLabel.setVisibility(View.VISIBLE);
                        mTextViewAttachmentLabel.setText(filename);
                        isUploadingImage = false;
                        OustSdkTools.showToast(getString(R.string.upload_success));
                    } else if (TransferState.FAILED.equals(observer.getState())) {
                        uploadProgressBar.setVisibility(View.GONE);
                        isUploadingImage = false;
                        learningDiaryMediaDataList = null;
                        //OustSdkTools.showToast(getString(R.string.upload_fail_msg));
                        filename = null;
                        uploadComplete(false);
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    long _bytesCurrent = bytesCurrent;
                    long _bytesTotal = bytesTotal;
                    float percentage = ((float) _bytesCurrent / (float) _bytesTotal * 100);
                    if (percentage == 0.0) {
                        uploadProgressBar.setProgress(2);
                    } else {
                        uploadProgressBar.setProgress((int) percentage);
                    }
                    Log.d("percentage", "" + percentage);
                }

                @Override
                public void onError(int id, Exception ex) {
                    uploadProgressBar.setVisibility(View.GONE);
                    isUploadingImage = false;
                    learningDiaryMediaDataList = null;
                    filename = null;
                    //uploadComplete(false);
                    //OustSdkTools.showToast(getString(R.string.upload_fail_msg));
//                    mediaupload_progressbar.setVisibility(View.GONE);
//                    attach_audio.setClickable(true);
//                    record_layout.setClickable(true);
//                    delete.setClickable(true);
//                    submit.setClickable(true);
//                    Log.e("upload media error", ex.getMessage());
                    //Toast.makeText(getActivity(), "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void selectedEditPosition(int position) {
        try {
            editModel = new DTODiaryDetailsModel();
            if (isAll) {
                if (diaryDetailsModels != null) {
                    editModel = diaryDetailsModels.get(position);
                }
            } else {
                if (diaryFilteredList != null) {
                    editModel = diaryFilteredList.get(position);
                }
            }
            ShowAddTopicAlert(editModel, true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void selectedDeletePosition(final int position) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.delete_msg));
            builder.setMessage(getString(R.string.delete_confirm));
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (isAll) {
                        if (diaryDetailsModels != null) {
                            if (OustSdkTools.checkInternetStatus()) {
                                mPresenter.deleteManualEntry(activeUser.getStudentid(), diaryDetailsModels.get(position).getUserLD_Id());
                            } else {
                                OustSdkTools.showToast(getString(R.string.no_internet_connection));
                            }
                        }
                    } else {
                        if (diaryFilteredList != null) {
                            if (OustSdkTools.checkInternetStatus()) {
                                mPresenter.deleteManualEntry(activeUser.getStudentid(), diaryFilteredList.get(position).getUserLD_Id());
                            } else {
                                OustSdkTools.showToast(getString(R.string.no_internet_connection));
                            }
                        }
                    }
                }
            });
            builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogForDelete.dismiss();
                }
            });
            dialogForDelete = builder.create();
            dialogForDelete.show();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            Log.d(TAG, "onNetworkConnectionChanged: ");
            //OustSdkTools.showToast("Internet connected");
        } else {
            OustSdkTools.showToast(getString(R.string.no_internet_connection));
        }
    }

    @Override
    public void updateLastEntryDate(String time){
        long lastEntryTime = Long.parseLong(time);
        Log.d(TAG, "updateLastEntryDate: "+time);
    }
}
