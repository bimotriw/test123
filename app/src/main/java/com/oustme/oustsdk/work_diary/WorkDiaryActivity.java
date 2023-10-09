package com.oustme.oustsdk.work_diary;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;

import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
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
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.assessments.learningdiary.CompressFile;
import com.oustme.oustsdk.base.BaseActivity;
import com.oustme.oustsdk.model.request.AddLearningDiaryManually;
import com.oustme.oustsdk.model.request.DetailsModel;
import com.oustme.oustsdk.model.request.LearningDiaryMediaDataList;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.FilePath;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustGATools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.oustme.oustsdk.work_diary.adapter.WorkDiaryAdapter;
import com.oustme.oustsdk.work_diary.model.WorkDiaryComponentModule;
import com.oustme.oustsdk.work_diary.model.WorkDiaryDetailsModel;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class WorkDiaryActivity extends BaseActivity implements WorkDiaryAdapter.SelectEditDelete, WorkDiaryAdapter.searchInterFace {

    Toolbar toolbar_lay;
    ImageView toolbar_close_icon, iv_sort, iv_filter;
    TextView no_data_text;//year_month;
    Spinner year_spinner, month_spinner, type_spinner;
    CircularProgressIndicator imageCircularProgressIndicator;
    RecyclerView work_diary_recycler;
    LinearLayout data_layout, filter_lay;
    FloatingActionButton add_entry;
    RelativeLayout work_diary_lay;
    TextView toolBar_text;
    Dialog entryDialog;

    LinearLayout file_layout;
    TextView file_name;

    //Branding loader
    RelativeLayout branding_mani_layout;
    ImageView branding_bg;
    ImageView branding_icon;
    TextView branding_percentage;
    //End

    File selectedFile;
    private ActiveUser activeUser;
    boolean isUploading = false;
    private boolean isUploadingImage;
    private long fileSize;
    private boolean isMediaChanged;
    private boolean isMediaDeleted;
    private boolean isMultipleCpl = false;

    private int year, month, day;
    private int mNoOfBackDays = 365;
    private long maxDateRange = 0;
    private long latestEntryInMill = 0;
    private static final long MILLI_SEC_PER_DAY = 86400000;
    private boolean isPreviousDateAllowed;
    private boolean isFutureDateAllowed;
    private boolean isStartDateFreeze;
    private boolean enableRDWD;
    private int color, bgColor, filterColor;
    private boolean clickSubmitBtn = false;
    String avatarUrl;

    //MVVM
    WorkDiaryViewModel workDiaryViewModel;
    WorkDiaryComponentModule workDiaryComponentModule;
    WorkDiaryAdapter adapter;
    ArrayList<WorkDiaryDetailsModel> workDiaryDetailsModelFilterList = new ArrayList<>();
    private LearningDiaryMediaDataList learningDiaryMediaDataList;
    private ArrayList<LearningDiaryMediaDataList> mediaDataLists;

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int SELECT_FILE = 1100;
    private static final int REQUEST_CAMERA = 1200;

    @Override
    protected int getContentView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_work_diary;
    }

    @Override
    protected void initView() {

        try {
            if (OustSdkApplication.getContext() == null) {
                OustSdkApplication.setmContext(WorkDiaryActivity.this);
            }
            OustSdkTools.setLocale(WorkDiaryActivity.this);
//            OustGATools.getInstance().reportPageViewToGoogle(WorkDiaryActivity.this, "Oust Work Diary Page");
            activeUser = OustAppState.getInstance().getActiveUser();

            toolbar_lay = findViewById(R.id.toolbar_lay);
            toolbar_close_icon = findViewById(R.id.toolbar_close_icon);
            iv_filter = findViewById(R.id.iv_filter);
            iv_sort = findViewById(R.id.iv_sort);
            data_layout = findViewById(R.id.data_layout);
            filter_lay = findViewById(R.id.filter_lay);
            //year_month = findViewById(R.id.year_month);
            no_data_text = findViewById(R.id.no_data_text);
            year_spinner = findViewById(R.id.year_spinner);
            month_spinner = findViewById(R.id.month_spinner);
            type_spinner = findViewById(R.id.type_spinner);
            work_diary_lay = findViewById(R.id.work_diary_layout);
            work_diary_recycler = findViewById(R.id.work_diary_recycler);
            add_entry = findViewById(R.id.add_entry);
            toolBar_text = findViewById(R.id.component_type);

            //Branding loader
            branding_mani_layout = findViewById(R.id.branding_main_layout);
            branding_bg = findViewById(R.id.branding_bg);
            branding_icon = findViewById(R.id.brand_loader);
            branding_percentage = findViewById(R.id.percentage_text);
            //End

            toolbar_close_icon.setOnClickListener(v -> onBackPressed());
            filterColor = Color.parseColor("#908F8F");
            isMultipleCpl = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_MULTIPLE_CPL);

            try {
                String tenantId = OustPreferences.get("tanentid");

                if (tenantId != null && !tenantId.isEmpty()) {
                    File brandingBg = new File(OustSdkApplication.getContext().getFilesDir(),
                            ("oustlearn_" + tenantId.toUpperCase().trim() + "splashScreen"));

                    if (brandingBg.exists() && OustPreferences.getAppInstallVariable(IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED)) {
                        Picasso.get().load(brandingBg).into(branding_bg);
                    } else {
                        String tenantBgImage = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "appImages/splash/org/" + (tenantId.toUpperCase().trim()) + "/android/bgImage";
                        Picasso.get().load(tenantBgImage).into(branding_bg);
                    }

                    File brandingLoader = new File(OustSdkApplication.getContext().getFilesDir(), ("oustlearn_" + tenantId.toUpperCase().trim() + "splashIcon"));
                    if (brandingLoader.exists()) {
                        Picasso.get().load(brandingLoader).into(branding_icon);
                    } else {
                        String tenantIcon = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "appImages/splash/org/" + (tenantId.toUpperCase().trim()) + "/android/icon";
                        Picasso.get().load(tenantIcon).error(getResources().getDrawable(R.drawable.app_icon)).into(branding_icon);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            getColors();
            setToolbarAndIconColor();
            //setDate();
            showLoader();

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void initData() {
        try {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                avatarUrl = bundle.getString("avatar");
            }
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);

            maxDateRange = MILLI_SEC_PER_DAY * OustPreferences.getTimeForNotification(AppConstants.StringConstants.MAX_DATE_RANGE);
            if (maxDateRange <= 0) {
                maxDateRange = MILLI_SEC_PER_DAY * 30;
            }
            if ((int) OustPreferences.getTimeForNotification(AppConstants.StringConstants.NO_OF_BACK_DAYS_ALLOWED) != 0) {
                mNoOfBackDays = (int) OustPreferences.getTimeForNotification(AppConstants.StringConstants.NO_OF_BACK_DAYS_ALLOWED);
            } else {
                mNoOfBackDays = 365;
            }

            isStartDateFreeze = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.FREEZE_WORK_DIARY_START_DATE);
            isPreviousDateAllowed = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.IS_PREV_DATE_ALLOWED);
            isFutureDateAllowed = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.IS_FUTURE_DATE_ALLOWED);
            enableRDWD = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.ENABLE_RD_WD);

            adapter = new WorkDiaryAdapter();
            fetchLayoutData();

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void initListener() {
        try {
            add_entry.setOnClickListener(v -> showEntryPopUp(null, false));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

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

    private void setToolbarAndIconColor() {
        try {
            toolbar_lay.setBackgroundColor(bgColor);
            toolBar_text.setTextColor(color);
            setSupportActionBar(toolbar_lay);
            OustResourceUtils.setDefaultDrawableColor(toolbar_close_icon.getDrawable(), color);
            add_entry.setBackgroundTintList(ColorStateList.valueOf(color));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showLoader() {
        try {
            branding_mani_layout.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void hideLoader() {
        try {
            branding_mani_layout.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void fetchLayoutData() {
        try {
            Bundle bundle = getIntent().getExtras();
            if (workDiaryViewModel != null) {
                getViewModelStore().clear();
                workDiaryViewModel = null;
                this.workDiaryComponentModule = null;
            }
            workDiaryViewModel = new ViewModelProvider(this).get(WorkDiaryViewModel.class);
            workDiaryViewModel.init(bundle, isMultipleCpl);
            workDiaryViewModel.getBaseComponentModuleMutableLiveData().observe(this, workDiaryComponentModule -> {
                if (workDiaryComponentModule == null)
                    return;

                this.workDiaryComponentModule = workDiaryComponentModule;
                latestEntryInMill = workDiaryComponentModule.getLatestEntryInMill();
                showLoader();
                setData(workDiaryComponentModule);
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setData(WorkDiaryComponentModule workDiaryComponentModule) {
        try {
            if (workDiaryComponentModule != null) {

                if (workDiaryComponentModule.getWorkDiaryDetailsModelArrayList() != null
                        && workDiaryComponentModule.getWorkDiaryDetailsModelArrayList().size() != 0) {

                    adapter = new WorkDiaryAdapter();
                    adapter.setWorkDiaryAdapter(workDiaryComponentModule.getWorkDiaryDetailsModelArrayList(), isMultipleCpl, activeUser.getStudentKey(), WorkDiaryActivity.this);

                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(WorkDiaryActivity.this);
                    work_diary_recycler.setLayoutManager(mLayoutManager);
                    work_diary_recycler.setItemAnimator(new DefaultItemAnimator());
                    work_diary_recycler.removeAllViews();
                    work_diary_recycler.setAdapter(adapter);
                    no_data_text.setVisibility(View.GONE);
                    work_diary_recycler.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();

                    hideLoader();

                    data_layout.setVisibility(View.VISIBLE);

                    HashMap<String, HashMap<String, ArrayList<WorkDiaryDetailsModel>>> workDiaryBaseHashMap = workDiaryComponentModule.getWorkDiaryBaseHashMap();

                    if (workDiaryBaseHashMap != null && workDiaryBaseHashMap.size() != 0) {
                        ArrayList<String> yearList = new ArrayList<>();
                        List<String> yearKeyList = new ArrayList<>(workDiaryBaseHashMap.keySet());
                        String selectYear = getResources().getString(R.string.year);
                        yearList.add(selectYear);
                        String allYear = getResources().getString(R.string.all);
                        yearList.add(allYear);
                        yearList.addAll(yearKeyList);

                        if (yearList.size() != 0) {
                            ArrayAdapter yearAdapter = new ArrayAdapter(WorkDiaryActivity.this, android.R.layout.simple_spinner_item, yearList);
                            yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            year_spinner.setAdapter(yearAdapter);

                            year_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                    if (position == 1 || position == 0) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            iv_filter.setImageTintList(ColorStateList.valueOf(Color.parseColor("#908F8F")));
                                        }
                                        month_spinner.setVisibility(View.INVISIBLE);
                                        type_spinner.setVisibility(View.INVISIBLE);

                                        if (adapter == null) {
                                            adapter = new WorkDiaryAdapter();
                                        }
                                        adapter.setWorkDiaryAdapter(workDiaryComponentModule.getWorkDiaryDetailsModelArrayList(), isMultipleCpl, activeUser.getStudentKey(), WorkDiaryActivity.this);
                                        work_diary_recycler.removeAllViews();
                                        work_diary_recycler.setAdapter(adapter);
                                        work_diary_recycler.setVisibility(View.VISIBLE);
                                        no_data_text.setVisibility(View.GONE);
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            iv_filter.setImageTintList(ColorStateList.valueOf(color));
                                        }
                                        if (workDiaryBaseHashMap.get(yearList.get(position)) != null) {
                                            HashMap<String, ArrayList<WorkDiaryDetailsModel>> workDiaryHashMap = workDiaryBaseHashMap.get(yearList.get(position));

                                            if (workDiaryHashMap != null && workDiaryHashMap.size() != 0) {
                                                ArrayList<String> monthList = new ArrayList<>();
                                                //List<String> monthKeyList = new ArrayList<>(workDiaryHashMap.keySet());
                                                List<String> monthKeyList = new ArrayList<>(workDiaryHashMap.keySet());
                                                Collections.sort(monthKeyList, (o1, o2) -> {
                                                    try {
                                                        SimpleDateFormat fmt = new SimpleDateFormat("MMM", Locale.getDefault());
                                                        return Objects.requireNonNull(fmt.parse(o1)).compareTo(fmt.parse(o2));
                                                    } catch (Exception ex) {
                                                        return o1.compareTo(o2);
                                                    }
                                                });
                                                String selectMonth = getResources().getString(R.string.month_text);
                                                monthList.add(selectMonth);
                                                String allMonth = getResources().getString(R.string.all);
                                                monthList.add(allMonth);
                                                monthList.addAll(monthKeyList);

                                                if (monthList.size() != 0) {
                                                    ArrayAdapter monthAdapter = new ArrayAdapter(WorkDiaryActivity.this, android.R.layout.simple_spinner_item, monthList);
                                                    monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                    month_spinner.setAdapter(monthAdapter);

                                                    month_spinner.setVisibility(View.VISIBLE);
                                                    type_spinner.setVisibility(View.VISIBLE);

                                                    workDiaryDetailsModelFilterList = new ArrayList<>();


                                                    month_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                        @Override
                                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                            showLoader();
                                                            type_spinner.setSelection(0);
                                                            if (position == 1 || position == 0) {
                                                                workDiaryDetailsModelFilterList = new ArrayList<>();
                                                                for (String key : workDiaryHashMap.keySet()) {
                                                                    ArrayList<WorkDiaryDetailsModel> valueList = workDiaryHashMap.get(key);
                                                                    if (valueList != null && valueList.size() != 0) {
                                                                        workDiaryDetailsModelFilterList.addAll(valueList);
                                                                    }
                                                                }
                                                            } else {
                                                                workDiaryDetailsModelFilterList = workDiaryHashMap.get(monthList.get(position));
                                                            }


                                                            if (workDiaryDetailsModelFilterList != null && workDiaryDetailsModelFilterList.size() != 0) {

                                                                if (adapter == null) {
                                                                    adapter = new WorkDiaryAdapter();
                                                                }
                                                                Collections.sort(workDiaryDetailsModelFilterList, WorkDiaryDetailsModel.workDiaryDetailsModelComparator);
                                                                adapter.setWorkDiaryAdapter(workDiaryDetailsModelFilterList, isMultipleCpl, activeUser.getStudentKey(), WorkDiaryActivity.this);
                                                                work_diary_recycler.removeAllViews();
                                                                work_diary_recycler.setAdapter(adapter);
                                                                no_data_text.setVisibility(View.GONE);
                                                                adapter.notifyDataSetChanged();
                                                                work_diary_recycler.setVisibility(View.VISIBLE);


                                                            } else {
                                                                work_diary_recycler.setVisibility(View.GONE);
                                                                no_data_text.setVisibility(View.VISIBLE);
                                                            }
                                                            hideLoader();
                                                        }

                                                        @Override
                                                        public void onNothingSelected(AdapterView<?> parent) {

                                                        }
                                                    });


                                                    type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                        @Override
                                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                                            showLoader();
                                                            ArrayList<WorkDiaryDetailsModel> typeFilterModelList = new ArrayList<>();
                                                            if (position == 0 || position == 1) {
                                                                typeFilterModelList = workDiaryDetailsModelFilterList;
                                                            } else {

                                                                if (workDiaryDetailsModelFilterList != null && workDiaryDetailsModelFilterList.size() != 0) {
                                                                    for (WorkDiaryDetailsModel workDiaryDetailsModel : workDiaryDetailsModelFilterList) {

                                                                        if (workDiaryDetailsModel.getDisplayType() != null && workDiaryDetailsModel.getDisplayType().equalsIgnoreCase(type_spinner.getSelectedItem().toString())) {
                                                                            typeFilterModelList.add(workDiaryDetailsModel);
                                                                        }
                                                                    }
                                                                }

                                                            }


                                                            if (typeFilterModelList != null && typeFilterModelList.size() != 0) {

                                                                if (adapter == null) {
                                                                    adapter = new WorkDiaryAdapter();
                                                                }
                                                                Collections.sort(typeFilterModelList, WorkDiaryDetailsModel.workDiaryDetailsModelComparator);
                                                                adapter.setWorkDiaryAdapter(typeFilterModelList, isMultipleCpl, activeUser.getStudentKey(), WorkDiaryActivity.this);
                                                                work_diary_recycler.removeAllViews();
                                                                work_diary_recycler.setAdapter(adapter);
                                                                work_diary_recycler.setVisibility(View.VISIBLE);
                                                                no_data_text.setVisibility(View.GONE);
                                                                adapter.notifyDataSetChanged();


                                                            } else {
                                                                work_diary_recycler.setVisibility(View.GONE);
                                                                no_data_text.setVisibility(View.VISIBLE);
                                                            }

                                                            hideLoader();
                                                        }

                                                        @Override
                                                        public void onNothingSelected(AdapterView<?> parent) {

                                                        }
                                                    });

                                                }
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }
                    }

                    iv_sort.setOnClickListener(v -> {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            if (filterColor == Color.parseColor("#908F8F")) {
                                filterColor = color;
                                iv_sort.setImageTintList(ColorStateList.valueOf(filterColor));
                            } else {
                                filterColor = Color.parseColor("#908F8F");
                                iv_sort.setImageTintList(ColorStateList.valueOf(Color.parseColor("#908F8F")));
                            }
                        }

                        if (adapter != null) {

                            ArrayList<WorkDiaryDetailsModel> workDiaryDetailsModelArrayList = adapter.getWorkDiaryDetailsModelArrayList();
                            Collections.reverse(workDiaryDetailsModelArrayList);
                            adapter.setWorkDiaryAdapter(workDiaryDetailsModelArrayList, isMultipleCpl, activeUser.getStudentKey(), WorkDiaryActivity.this);
                            work_diary_recycler.removeAllViews();
                            work_diary_recycler.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    });

                } else {
                    if (adapter != null) {
                        ArrayList<WorkDiaryDetailsModel> workDiaryDetailsModelArrayList = adapter.getWorkDiaryDetailsModelArrayList();
                        adapter.setWorkDiaryInterface(WorkDiaryActivity.this);
                    }
                    no_data_text.setVisibility(View.VISIBLE);
                    work_diary_recycler.setVisibility(View.GONE);
                    hideLoader();
                }
            } else {
                no_data_text.setVisibility(View.VISIBLE);
                work_diary_recycler.setVisibility(View.GONE);
                hideLoader();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            no_data_text.setVisibility(View.VISIBLE);
            work_diary_recycler.setVisibility(View.GONE);
            hideLoader();
        }
    }

    private void showEntryPopUp(WorkDiaryDetailsModel editModel, boolean isEdit) {
        try {
            if (entryDialog != null && entryDialog.isShowing()) {
                entryDialog.dismiss();
            }
            entryDialog = new Dialog(WorkDiaryActivity.this, R.style.DialogTheme);
            entryDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            entryDialog.setContentView(R.layout.work_diary_entry);
            Objects.requireNonNull(entryDialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            entryDialog.show();

            final TextView start_date = entryDialog.findViewById(R.id.start_date);
            final TextView end_date = entryDialog.findViewById(R.id.end_date);
            final EditText activity_text = entryDialog.findViewById(R.id.activity_text);
            final EditText comment = entryDialog.findViewById(R.id.comment);
            final ImageView select_from_gallery = entryDialog.findViewById(R.id.select_from_gallery);
            final ImageView upload_video = entryDialog.findViewById(R.id.upload_video);
            file_layout = entryDialog.findViewById(R.id.file_layout);
            file_name = entryDialog.findViewById(R.id.file_name);
            final ImageView cancel_video = entryDialog.findViewById(R.id.cancel_video);
            final LinearLayout entry_submit = entryDialog.findViewById(R.id.entry_submit);
            imageCircularProgressIndicator = entryDialog.findViewById(R.id.upload_loader_workDiary);
            imageCircularProgressIndicator.setIndicatorColor(color);
            imageCircularProgressIndicator.setTrackColor(getResources().getColor(R.color.gray));

            Drawable drawable = getResources().getDrawable(R.drawable.course_button_bg);
            entry_submit.setBackground(OustSdkTools.drawableColor(drawable));

            if (isStartDateFreeze && latestEntryInMill != 0) {
                SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                start_date.setEnabled(false);
                start_date.setText(displayFormat.format(new Date(latestEntryInMill)));

            } else {
                start_date.setOnClickListener(v -> {
                    DatePickerDialog startDatePicker = new DatePickerDialog(WorkDiaryActivity.this, (view, selectedYear, selectedMonth, selectedDay) -> {
                        String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                        try {
                            Date date = dateFormat.parse(selectedDate);
                            assert date != null;
                            start_date.setText(displayFormat.format(date));

                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }


                    }, year, month, day);

                    if (!isFutureDateAllowed)
                        startDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());

                    long defaultDate = 0;

                    if (!isPreviousDateAllowed) {
                        startDatePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 2000);
                    } else if (mNoOfBackDays > 0) {
                        long milli = (mNoOfBackDays - 1) * MILLI_SEC_PER_DAY;
                        if (milli == 0)
                            milli = 2000;

                        startDatePicker.getDatePicker().setMinDate(System.currentTimeMillis() - milli);
                        defaultDate = System.currentTimeMillis() - milli;
                        if (enableRDWD) {
                            if (workDiaryComponentModule.getActiveUser() != null) {
                                if (workDiaryComponentModule.getActiveUser().getRegisterDateTime() != 0) {
                                    startDatePicker.getDatePicker().setMinDate(workDiaryComponentModule.getActiveUser().getRegisterDateTime() - milli);
                                    defaultDate = workDiaryComponentModule.getActiveUser().getRegisterDateTime() - milli;
                                }
                            }
                        }
                    }

                    if (defaultDate != 0) {
                        int[] ar = getDates(defaultDate);
                        startDatePicker.updateDate(ar[0], ar[1], ar[2]);
                    }
                    startDatePicker.show();
                });
            }


            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            end_date.setOnClickListener(v -> {
                if (start_date.getText() == null || start_date.getText().toString().isEmpty()) {
                    OustSdkTools.showToast(getString(R.string.startdate_error));
                } else {
                    DatePickerDialog endDatePicker = new DatePickerDialog(WorkDiaryActivity.this, (view, selectedYear, selectedMonth, selectedDay) -> {

                        String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;

                        try {
                            Date date = dateFormat.parse(selectedDate);
                            assert date != null;
                            end_date.setText(displayFormat.format(date));

                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    }, year, month, day);

                    if (!isPreviousDateAllowed) {
                        endDatePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 2000);
                    }

                    Date date = null;
                    try {
                        date = displayFormat.parse(start_date.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }

                    if (date != null) {
                        endDatePicker.getDatePicker().setMinDate(date.getTime());
                    }

                    if (!isFutureDateAllowed) {
                        if (maxDateRange != 0 && date != null && (date.getTime() + (maxDateRange)) <= System.currentTimeMillis()) {
                            endDatePicker.getDatePicker().setMaxDate(date.getTime() + (maxDateRange));
                        } else {
                            endDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
                        }
                    }

                    if (maxDateRange != 0) {
                        if (date != null) {
                            endDatePicker.getDatePicker().setMaxDate(date.getTime() + (maxDateRange));
                        }
                    }

                    endDatePicker.show();
                }
            });


            select_from_gallery.setOnClickListener(v -> checkForStoragePermission());

            upload_video.setOnClickListener(v -> {
                if (selectedFile != null) {
                    File filePath = CompressFile.getCompressedImageFile(selectedFile, WorkDiaryActivity.this);
                    if (filePath != null) {
                        isUploading = false;
                        if (OustSdkTools.checkInternetStatus() && !isUploading) {
                            isUploading = true;
                            //showLoader();
                            //gif_loader_pop_up.setVisibility(View.VISIBLE);
                            uploadToAWS(filePath);
                        } else {
                            OustSdkTools.showToast(getString(R.string.no_internet_connection));
                        }
                    } else {
                        OustSdkTools.showToast(getString(R.string.no_internet_connection));
                    }
                }
            });

            cancel_video.setOnClickListener(v -> {
                if (!isUploadingImage) {
                    selectedFile = null;
                    fileSize = 0;
                    isMediaChanged = true;
                    isMediaDeleted = true;
                    if (learningDiaryMediaDataList != null) {
                        learningDiaryMediaDataList = null;
                    }
                    file_layout.setVisibility(View.GONE);
                } else {
                    OustSdkTools.showToast(getString(R.string.please_wait_uploading));
                }
            });

            entry_submit.setOnClickListener(v -> {
                try {
                    if (!isUploadingImage) {
                        WorkDiaryDetailsModel diaryDetailsModel = new WorkDiaryDetailsModel();
                        if (start_date.getText() != null && !start_date.getText().toString().isEmpty()) {
                            diaryDetailsModel.setStartTS(OustSdkTools.convertTimeForWD(start_date.getText().toString().trim()));
                        } else {
                            OustSdkTools.showToast(getString(R.string.startdate_error));
                            return;
                        }
                        if (end_date.getText() != null && !end_date.getText().toString().isEmpty()) {
                            diaryDetailsModel.setEndTS(OustSdkTools.convertTimeForWD(end_date.getText().toString().trim()));
                        } else {
                            OustSdkTools.showToast(getString(R.string.end_date_error));
                            return;
                        }


                        if (activity_text.getText() != null && !activity_text.getText().toString().trim().isEmpty()) {
                            diaryDetailsModel.setActivityName(activity_text.getText().toString().trim());
                        } else {
                            activity_text.setError(getString(R.string.topic_error_msg));
                            return;
                        }

                        if (selectedFile != null) {
                            Toast.makeText(WorkDiaryActivity.this, "Please Upload file ", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (comment.getText() != null && !comment.getText().toString().trim().isEmpty()) {
                            diaryDetailsModel.setComments(comment.getText().toString().trim());
                        } else {
                            comment.setError(getString(R.string.comment_error_msg));
                            return;
                        }
                        if (Long.parseLong(diaryDetailsModel.getStartTS()) > Long.parseLong(diaryDetailsModel.getEndTS())) {
                            OustSdkTools.showToast(getString(R.string.startdate_less_endate));
                            return;
                        }

                        if (maxDateRange != 0) {
                            if ((OustSdkTools.convertTimeToMiliWD(end_date.getText().toString().trim()) - (OustSdkTools.convertTimeToMiliWD(start_date.getText().toString().trim()))) > maxDateRange) {
                                OustSdkTools.showToast(getResources().getString(R.string.start_end_between) + (int) (maxDateRange / MILLI_SEC_PER_DAY) + " " + getResources().getString(R.string.days_text));
                                return;
                            }
                        }

                        if (learningDiaryMediaDataList != null) {
                            mediaDataLists.add(0, learningDiaryMediaDataList);
                        }

                        if (!countNoOfCharacters(activity_text, 150)) {
                            activity_text.setError(getString(R.string.not_exceed_150_chars));
                            return;
                        }

                        if (!countNoOfCharacters(comment, 300)) {
                            comment.setError(getString(R.string.not_exceed_300_chars));
                            return;
                        }

                        if (isEdit) {
                            //UpdateData();
                            if (editModel != null) {
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
                                        object.setChanged(learningDiaryMediaDataList != null);
                                        mediaDataLists.add(object);
                                    }
                                }
                                if (learningDiaryMediaDataList != null) {
                                    mediaDataLists.add(learningDiaryMediaDataList);
                                }
                                //this is if the attachment is deleted
                                if (mediaDataLists.size() == 1 && isMediaDeleted) {
                                    mediaDataLists.get(0).setChanged(true);
                                }
                            }
                            if (!clickSubmitBtn) {
                                clickSubmitBtn = true;
                                updateData(diaryDetailsModel, mediaDataLists, isMediaChanged);
                            }
                        } else {
                            if (!clickSubmitBtn) {
                                clickSubmitBtn = true;
                                sendData(diaryDetailsModel, mediaDataLists);
                            }
                        }
                    } else {
                        OustSdkTools.showToast(getString(R.string.please_wait_uploading));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            });

            if (isEdit) {
                start_date.setText(OustSdkTools.timeFormatForWD(Long.parseLong(editModel.getStartTS())));
                end_date.setText(OustSdkTools.timeFormatForWD(Long.parseLong(editModel.getEndTS())));
                activity_text.setText(editModel.getActivityName());
                comment.setText(editModel.getComments());
                try {
                    if (editModel.getLearningDiaryMediaDataList() != null) {
                        if (editModel.getLearningDiaryMediaDataList().get(0) != null) {
                            if (editModel.getLearningDiaryMediaDataList().get(0).getFileName() != null) {
                                String filename = editModel.getLearningDiaryMediaDataList().get(0).getFileName();
                                file_name.setText(filename);
                                file_layout.setVisibility(View.VISIBLE);

                            }
                            if (editModel.getLearningDiaryMediaDataList().get(0).getFileSize() != null) {
                                fileSize = Long.valueOf(editModel.getLearningDiaryMediaDataList().get(0).getFileSize().replace("KB", ""));
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private int[] getDates(long milli) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(milli);
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        int[] ar = new int[3];
        ar[0] = mYear;
        ar[1] = mMonth;
        ar[2] = mDay;

        return ar;
    }


    public void checkForStoragePermission() {
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        if (ContextCompat.checkSelfPermission(WorkDiaryActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            intentPopUp();
        }
    }

    private void intentPopUp() {
        Dialog dialog = new Dialog(WorkDiaryActivity.this, R.style.DialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.intent_popuup);
        Objects.requireNonNull(dialog.getWindow()).setGravity(Gravity.BOTTOM);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();

        TextView select_from_gallery = dialog.findViewById(R.id.select_from_gallery);
        TextView record_video = dialog.findViewById(R.id.record_video);
        TextView cancel_dialog = dialog.findViewById(R.id.cancel_dialog);
        record_video.setText(getResources().getString(R.string.capture_image_text));

        select_from_gallery.setOnClickListener(v -> {

            openGallery();
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        });
        record_video.setOnClickListener(v -> {

            openCamera();
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        });
        cancel_dialog.setOnClickListener(v -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        });

    }

    private void openGallery() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra("return-data", true);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, SELECT_FILE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void openCamera() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CAMERA);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == SELECT_FILE)
                    onSelectFromGalleryResult(data);
                else if (requestCode == REQUEST_CAMERA)
                    onCaptureImageResult(data);
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void onSelectFromGalleryResult(Intent data) {
        Uri path = data.getData();
        if (path != null) {
            if (path.toString().contains("com.google.android.apps.photos")) {
                OustSdkTools.showToast("can't select attachment from google photos app");
                return;
            }
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            String filePath = FilePath.getRealPathFromUri(WorkDiaryActivity.this, path);

            if (filePath != null) {
                selectedFile = new File(filePath);
                if (entryDialog != null && entryDialog.isShowing()) {
                    file_layout.setVisibility(View.VISIBLE);
                    file_name.setText(selectedFile.getName());
                    isUploading = false;
                }

            }
        } else {
            String[] proj = {MediaStore.Images.Media.DATA};
            String result = null;

            assert path != null;
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
                    selectedFile = new File(result);
                    if (entryDialog != null && entryDialog.isShowing()) {
                        file_layout.setVisibility(View.VISIBLE);
                        file_name.setText(selectedFile.getName());
                        isUploading = false;
                    }
                }
            }
        }
    }


    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        assert thumbnail != null;
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        selectedFile = new File(OustSdkApplication.getContext().getFilesDir(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            selectedFile.createNewFile();
            if (entryDialog != null && entryDialog.isShowing()) {
                file_layout.setVisibility(View.VISIBLE);
                file_name.setText(selectedFile.getName());
            }
            fo = new FileOutputStream(selectedFile);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void uploadToAWS(final File choosedFile) {
        try {
            isUploadingImage = true;
            ClientConfiguration clientConfiguration = new ClientConfiguration();
            clientConfiguration.setRetryPolicy(new RetryPolicy(null, null, 1, true));
            String awsKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeyId");
            String awsSecretKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeySecret");
            AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(awsKeyId, awsSecretKeyId), clientConfiguration);
            s3.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
            TransferUtility transferUtility = new TransferUtility(s3, this);
            if (choosedFile == null || !choosedFile.exists()) {
                return;
            }
            final String[][] filename = {{System.currentTimeMillis() + "_" + choosedFile.getName()}};
            if (workDiaryComponentModule != null && workDiaryComponentModule.getActiveUser() != null) {
                filename[0][0] = System.currentTimeMillis() + "_WD_" + workDiaryComponentModule.getActiveUser().getStudentKey();
            }

            fileSize = choosedFile.length() / 1024;
            //showLoader();
            imageCircularProgressIndicator.setVisibility(View.VISIBLE);
            final TransferObserver observer = transferUtility.upload(AppConstants.StringConstants.S3_BUCKET_NAME, AppConstants.StringConstants.S3_IMAGE_FOLDER + filename[0][0] + ".jpg", selectedFile);
            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (TransferState.COMPLETED.equals(observer.getState())) {
                        learningDiaryMediaDataList = new LearningDiaryMediaDataList();
                        mediaDataLists = new ArrayList<>();
                        if (!filename[0][0].contains(".jpg")) {
                            learningDiaryMediaDataList.setFileName(filename[0][0] + ".jpg");
                            filename[0][0] = filename[0][0] + ".jpg";
                        }
                        learningDiaryMediaDataList.setFileType("IMAGE");
                        learningDiaryMediaDataList.setFileSize(fileSize + "KB");
                        learningDiaryMediaDataList.setChanged(true);
                        learningDiaryMediaDataList.setUserLdMedia_Id(0);
                        isMediaChanged = true;

                        if (entryDialog != null && entryDialog.isShowing()) {
                            file_name.setText(filename[0][0]);
                        }

                        selectedFile = null;
                        isUploadingImage = false;
                        isUploading = false;
                        OustSdkTools.showToast(getString(R.string.upload_success));
                        imageCircularProgressIndicator.setVisibility(View.GONE);
                    } else if (TransferState.FAILED.equals(observer.getState())) {
                        imageCircularProgressIndicator.setVisibility(View.GONE);
                        isUploadingImage = false;
                        learningDiaryMediaDataList = null;
                        //OustSdkTools.showToast(getString(R.string.upload_fail_msg));
                        filename[0] = null;
                        isUploading = false;
                        Toast.makeText(WorkDiaryActivity.this, "" + getString(R.string.sorry_media_upload_failed), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                }

                @Override
                public void onError(int id, Exception ex) {
                    imageCircularProgressIndicator.setVisibility(View.GONE);
                    isUploadingImage = false;
                    isUploading = false;
                    learningDiaryMediaDataList = null;
                    //OustSdkTools.showToast(getString(R.string.upload_fail_msg));
                    filename[0] = null;
                    Toast.makeText(WorkDiaryActivity.this, "" + getString(R.string.sorry_media_upload_failed), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            imageCircularProgressIndicator.setVisibility(View.GONE);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void selectedEditPosition(WorkDiaryDetailsModel workDiaryDetailsModel) {
        try {
            showEntryPopUp(workDiaryDetailsModel, true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void selectedDeletePosition(WorkDiaryDetailsModel workDiaryDetailsModel) {

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("");
            builder.setMessage(getString(R.string.delete_confirm));
            builder.setPositiveButton(getString(R.string.ok), (dialogInterface, i) -> {
                if (OustSdkTools.checkInternetStatus() && workDiaryComponentModule != null && workDiaryComponentModule.getActiveUser() != null) {
                    String url = HttpManager.getAbsoluteUrl(OustSdkApplication.getContext().getResources().getString(R.string.delete_learning_diary) + workDiaryComponentModule.getActiveUser().getStudentid() + "/" + workDiaryDetailsModel.getUserLD_Id());
                    JSONObject js = new JSONObject();
                    try {
                        js.put("name", "anything");
                    } catch (JSONException e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }

                    ApiCallUtils.doNetworkCallWithContentType(Request.Method.DELETE, url, OustSdkTools.getRequestObjectforJSONObject(js), new ApiCallUtils.NetworkCallback() {
                        @Override
                        public void onResponse(JSONObject response) {
                            OustSdkTools.showToast(getString(R.string.deleted_success));
                            if (workDiaryComponentModule.getWorkDiaryDetailsModelArrayList() != null) {
                                for (int i = 0; i < workDiaryComponentModule.getWorkDiaryDetailsModelArrayList().size(); i++) {
                                    if (workDiaryComponentModule.getWorkDiaryDetailsModelArrayList().get(i).getUserLD_Id().equalsIgnoreCase(workDiaryDetailsModel.getUserLD_Id()))
                                        workDiaryComponentModule.getWorkDiaryDetailsModelArrayList().remove(i);
                                }
                            }

                            if (workDiaryComponentModule.getWorkDiaryDetailsManualArrayList() != null) {
                                for (int i = 0; i < workDiaryComponentModule.getWorkDiaryDetailsManualArrayList().size(); i++) {
                                    if (workDiaryComponentModule.getWorkDiaryDetailsManualArrayList().get(i).getUserLD_Id().equalsIgnoreCase(workDiaryDetailsModel.getUserLD_Id()))
                                        workDiaryComponentModule.getWorkDiaryDetailsManualArrayList().remove(i);
                                }
                            }

                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                            }

                            if (workDiaryComponentModule.getWorkDiaryDetailsManualArrayList() != null && workDiaryComponentModule.getWorkDiaryDetailsManualArrayList().size() != 0) {
                                workDiaryComponentModule.setLatestEntryInMill(Long.parseLong(workDiaryComponentModule.getWorkDiaryDetailsManualArrayList().get(0).getEndTS()) + MILLI_SEC_PER_DAY);
                                latestEntryInMill = Long.parseLong(workDiaryComponentModule.getWorkDiaryDetailsManualArrayList().get(0).getEndTS()) + MILLI_SEC_PER_DAY;
                            } else {
                                if (workDiaryComponentModule.getActiveUser() != null) {
                                    if (workDiaryComponentModule.getActiveUser().getRegisterDateTime() != 0) {
                                        workDiaryComponentModule.setLatestEntryInMill(workDiaryComponentModule.getActiveUser().getRegisterDateTime());
                                        latestEntryInMill = workDiaryComponentModule.getActiveUser().getRegisterDateTime();
                                    }
                                }
                            }

                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(WorkDiaryActivity.this, "" + getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    OustSdkTools.showToast(getString(R.string.no_internet_connection));
                }
            });
            builder.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> dialogInterface.dismiss());
            AlertDialog dialogForDelete = builder.create();
            dialogForDelete.show();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean countNoOfCharacters(EditText editText, int charcterCount) {
        return editText.getText().toString().length() <= charcterCount;
    }

    private void sendData(WorkDiaryDetailsModel workDiaryDetailsModel, ArrayList<LearningDiaryMediaDataList> mediaDataLists) {
        try {

            if (OustSdkTools.checkInternetStatus() && workDiaryComponentModule != null && workDiaryComponentModule.getActiveUser() != null) {

                showLoader();
                AddLearningDiaryManually addLearningDiaryManually = new AddLearningDiaryManually();
                DetailsModel dataModel = new DetailsModel();

                dataModel.setActivityName(workDiaryDetailsModel.getActivityName());
                dataModel.setComments(workDiaryDetailsModel.getComments());
                dataModel.setEndTS(workDiaryDetailsModel.getEndTS());
                dataModel.setStartTS(workDiaryDetailsModel.getStartTS());
                dataModel.setLearningDiaryMediaDataList(mediaDataLists);
                addLearningDiaryManually.setStudentid(workDiaryComponentModule.getActiveUser().getStudentid());
                addLearningDiaryManually.setDiaryDetailsModel(dataModel);

                Gson gson = new GsonBuilder().create();

                String url = HttpManager.getAbsoluteUrl(OustSdkApplication.getContext().getResources().getString(R.string.create_learning_diary));
                String requestDataJson = null;
                try {
                    requestDataJson = gson.toJson(addLearningDiaryManually);
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }


                ApiCallUtils.doNetworkCall(Request.Method.POST, url, OustSdkTools.getRequestObject(requestDataJson), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResult: uplaodto :" + response.toString());
                        hideLoader();
                        if (entryDialog != null && entryDialog.isShowing()) {
                            entryDialog.dismiss();
                        }

                        if (learningDiaryMediaDataList != null) {
                            learningDiaryMediaDataList = null;
                        }
                        if (mediaDataLists != null) {
                            mediaDataLists.clear();
                        }
                        clickSubmitBtn = false;
                        OustSdkTools.showToast(getString(R.string.added_success));

                        if (workDiaryComponentModule != null) {
                            final Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(() -> {
                                Intent restart = getIntent();
                                finish();
                                overridePendingTransition(0, 0);
                                startActivity(restart);
                            }, 1000);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoader();
                        clickSubmitBtn = false;
                        OustSdkTools.showToast(getString(R.string.unable_add_now));
                    }
                });


            } else {
                OustSdkTools.showToast(getString(R.string.no_internet_connection));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void updateData(WorkDiaryDetailsModel workDiaryDetailsModel, ArrayList<LearningDiaryMediaDataList> mediaDataLists, boolean isMediaChangedValue) {
        try {

            if (OustSdkTools.checkInternetStatus() && workDiaryComponentModule != null && workDiaryComponentModule.getActiveUser() != null) {

                showLoader();
                AddLearningDiaryManually addLearningDiaryManually = new AddLearningDiaryManually();
                DetailsModel dataModel = new DetailsModel();

                dataModel.setActivityName(workDiaryDetailsModel.getActivityName());
                dataModel.setComments(workDiaryDetailsModel.getComments());
                dataModel.setEndTS(workDiaryDetailsModel.getEndTS());
                dataModel.setStartTS(workDiaryDetailsModel.getStartTS());
                dataModel.setLearningDiaryMediaDataList(mediaDataLists);
                dataModel.setApprovalStatus(2 + "");
                dataModel.setUserLD_Id(Long.parseLong(workDiaryDetailsModel.getUserLD_Id()));
                dataModel.setMediaChanged(isMediaChangedValue);
                addLearningDiaryManually.setStudentid(workDiaryComponentModule.getActiveUser().getStudentid());
                addLearningDiaryManually.setDiaryDetailsModel(dataModel);

                Gson gson = new GsonBuilder().create();

                String url = HttpManager.getAbsoluteUrl(OustSdkApplication.getContext().getResources().getString(R.string.update_learning_diary) + workDiaryDetailsModel.getUserLD_Id());
                String requestDataJson = null;
                try {
                    requestDataJson = gson.toJson(addLearningDiaryManually);
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }

                ApiCallUtils.doNetworkCall(Request.Method.PUT, url, OustSdkTools.getRequestObject(requestDataJson), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResult: uplaodto :" + response.toString());
                        hideLoader();
                        if (entryDialog != null && entryDialog.isShowing()) {
                            entryDialog.dismiss();
                        }

                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }

                        if (learningDiaryMediaDataList != null) {
                            learningDiaryMediaDataList = null;
                        }
                        if (mediaDataLists != null) {
                            mediaDataLists.clear();
                        }

                        isMediaChanged = false;
                        isMediaDeleted = false;
                        clickSubmitBtn = false;
                        OustSdkTools.showToast(getString(R.string.updated_success));
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoader();
                        clickSubmitBtn = false;
                        OustSdkTools.showToast(getString(R.string.unable_update_now));
                    }
                });


            } else {
                hideLoader();
                clickSubmitBtn = false;
                OustSdkTools.showToast(getString(R.string.no_internet_connection));
            }
        } catch (Exception e) {
            hideLoader();
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.leaderboard_menu, menu);

        MenuItem itemFilter = menu.findItem(R.id.action_filter);
        Drawable filterDrawable = getResources().getDrawable(R.drawable.ic_filter);
        itemFilter.setIcon(OustResourceUtils.setDefaultDrawableColor(filterDrawable, color));
        itemFilter.setVisible(false);

        MenuItem itemSort = menu.findItem(R.id.action_sort);
        Drawable sortDrawable = getResources().getDrawable(R.drawable.ic_sort);
        itemSort.setIcon(OustResourceUtils.setDefaultDrawableColor(sortDrawable, color));
        itemSort.setVisible(false);

        MenuItem itemSearch = menu.findItem(R.id.action_search);
        itemSearch.setVisible(true);

        try {
            SearchManager manager = (SearchManager) getApplicationContext().getSystemService(Context.SEARCH_SERVICE);
            SearchView search = (SearchView) itemSearch.getActionView();
            search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

            EditText searchEditText = search.findViewById(androidx.appcompat.R.id.search_src_text);
            searchEditText.setTextColor(getResources().getColor(R.color.primary_text));
            searchEditText.setHintTextColor(getResources().getColor(R.color.unselected_text));

            ImageView searchIcon = search.findViewById(androidx.appcompat.R.id.search_button);
            searchIcon.setImageDrawable(OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.search_oust), color));

            search.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    toolBar_text.setVisibility(View.GONE);
                    itemFilter.setVisible(false);
                    itemSort.setVisible(false);
                }
            });

            search.setOnCloseListener(() -> {
                toolBar_text.setVisibility(View.VISIBLE);
                itemFilter.setVisible(false);
                itemSort.setVisible(false);
                return false;
            });

            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                   /* if (adapter.getItemCount() == 0) {
                        no_data_text.setVisibility(View.VISIBLE);
                    } else {
                        adapter.getFilter().filter(query);
                    }*/
                    adapter.getFilter().filter(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                   /* if (adapter.getItemCount() == 0) {
                        no_data_text.setVisibility(View.VISIBLE);
                    } else {
                        adapter.getFilter().filter(newText);
                    }*/
                    adapter.getFilter().filter(newText);

                    return false;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return true;
    }


    @Override
    public void searchModuleNotFound(String data) {
        try {
            if (data.equalsIgnoreCase("notFound")) {
                no_data_text.setVisibility(View.VISIBLE);
            } else {
                no_data_text.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
