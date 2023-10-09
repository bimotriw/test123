package com.oustme.oustsdk.layoutFour.components.myTask;

import static android.view.View.GONE;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.FIVE_HUNDRED_MILLI_SECONDS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.leaderboard.activity.NewLeaderBoardActivity;
import com.oustme.oustsdk.calendar_ui.ui.CalendarScreen;
import com.oustme.oustsdk.customviews.CircleImageView;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.layoutFour.components.myTask.adapter.TasksVPAdapter;
import com.oustme.oustsdk.layoutFour.components.myTask.fragment.AssessmentModuleFragment;
import com.oustme.oustsdk.layoutFour.components.myTask.fragment.CourseModuleFragment;
import com.oustme.oustsdk.layoutFour.components.myTask.fragment.catalogue.CatalogueLearningFragment;
import com.oustme.oustsdk.layoutFour.components.myTask.fragment.contest.ContestModuleFragment;
import com.oustme.oustsdk.layoutFour.components.myTask.fragment.playList.CplModuleFragment;
import com.oustme.oustsdk.layoutFour.interfaces.SwipeRefreshHandling;
import com.oustme.oustsdk.my_tasks.fragment.SurveyModuleFragment;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustDataHandler;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.tools.filters.CommonLandingFilter;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.oustme.oustsdk.work_diary.WorkDiaryActivity;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class MyTaskActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, SwipeRefreshHandling {

    //View initialize
    SwipeRefreshLayout swipe_container;
    ConstraintLayout data_layout;
    View user_progress;
    TextView tv_completed_count;
    TextView tv_completed;
    CircleImageView ivAvatar;
    TextView tv_pending_count;
    LinearLayout content_layout;
    ViewPager task_vp;
    TabLayout task_type;
    Toolbar toolbar;
    TextView screen_name;
    ImageView back_button;
    FrameLayout progress_lay;
    ImageView progress_view;
    TextView progress_text;
    RelativeLayout layout_loader;
    ImageView branding_bg;
    ImageView branding_icon;
    TextView layoutText;
    private FrameLayout mProgressCircle;

    View no_data_layout;
    ImageView no_image;
    TextView no_data_content;
    private String selected_tab;

    ActiveUser activeUser;
    ArrayList<CommonLandingData> allModule = new ArrayList<>();
    ArrayList<CommonLandingData> allList = new ArrayList<>();
    ArrayList<CommonLandingData> courseList = new ArrayList<>();
    ArrayList<CommonLandingData> cplList = new ArrayList<>();
    ArrayList<CommonLandingData> FFF_contest_List = new ArrayList<>();
    ArrayList<CommonLandingData> assessmentList = new ArrayList<>();
    ArrayList<CommonLandingData> completedList = new ArrayList<>();
    ArrayList<CommonLandingData> surveyList = new ArrayList<>();
    private TasksVPAdapter tasksVPAdapter;

    //View model
    MyTaskViewModel myTaskViewModel;

    //common for all base fragment
    HashMap<String, CommonLandingData> commonLandingDataHashMap;
    CourseModuleFragment courseModuleFragment;
    CatalogueLearningFragment catalogueLearningFragment;
    CplModuleFragment cplModuleFragment;
    ContestModuleFragment contestModuleFragment;
    AssessmentModuleFragment assessmentModuleFragment;
    boolean isRefresh = false;
    boolean isObserverLoaded = false;

    private int color;
    private int bgColor;
    private boolean checkCatalogueExistOrNot = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        setContentView(R.layout.activity_my_task);

        if (OustSdkApplication.getContext() == null) {
            OustSdkApplication.setmContext(MyTaskActivity.this);
        }
        OustSdkTools.setLocale(MyTaskActivity.this);
        getColors();
        initView();
        initData();
    }

    private void getColors() {

        try {
            color = OustResourceUtils.getColors();
            bgColor = OustResourceUtils.getToolBarBgColor();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            color = OustSdkTools.getColorBack(R.color.lgreen);
        }

    }

    private void initView() {
        try {
            toolbar = findViewById(R.id.toolbar_lay);
            screen_name = findViewById(R.id.screen_name);
            back_button = findViewById(R.id.back_button);
            swipe_container = findViewById(R.id.swipe_container);
            data_layout = findViewById(R.id.data_layout);
            user_progress = findViewById(R.id.user_progress);
            tv_completed_count = findViewById(R.id.tv_completed_count);
            tv_completed = findViewById(R.id.tv_completed);
            ivAvatar = findViewById(R.id.user_avatar);
            tv_pending_count = findViewById(R.id.tv_pending_count);
            progress_lay = findViewById(R.id.progress_lay);
            progress_view = findViewById(R.id.progress_view);
            progress_text = findViewById(R.id.progress_text);
            mProgressCircle = findViewById(R.id.progress_circle);
            content_layout = findViewById(R.id.content_layout);
            task_vp = findViewById(R.id.task_vp);
            task_type = findViewById(R.id.task_type);
            no_data_layout = findViewById(R.id.no_data_layout);
            no_image = no_data_layout.findViewById(R.id.no_image);
            no_data_content = no_data_layout.findViewById(R.id.no_data_content);
            layout_loader = findViewById(R.id.my_task_loader);
            branding_icon = findViewById(R.id.my_task_brand_loader);
            branding_bg = findViewById(R.id.my_task_branding_bg);
            layoutText = findViewById(R.id.my_task_message_text);

            screen_name.setText(getResources().getString(R.string.my_tasks_text));
            toolbar.setBackgroundColor(bgColor);
            toolbar.setTitle("");
            screen_name.setTextColor(color);
            OustResourceUtils.setDefaultDrawableColor(back_button.getDrawable(), color);
            setSupportActionBar(toolbar);
            checkCatalogueExistOrNot = OustPreferences.getAppInstallVariable("checkCatalogueInBottomNav");

            back_button.setOnClickListener(v -> onBackPressed());

            try {
                activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

            task_type.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    View customView = tab.getCustomView();
                    if (tab.getText() != null) {
                        selected_tab = String.valueOf(tab.getText());
                    }
                    if (customView instanceof AppCompatTextView) {
                        AppCompatTextView customTextView = (AppCompatTextView) customView;
                        (customTextView).setTypeface(customTextView.getTypeface(), Typeface.BOLD);
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    View customView = tab.getCustomView();
                    if (customView instanceof AppCompatTextView) {
                        AppCompatTextView customTextView = (AppCompatTextView) customView;
                        (customTextView).setTypeface(customTextView.getTypeface(), Typeface.NORMAL);
                    }
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

            tv_completed.setOnClickListener(v -> openWorkDiary());

            tv_completed_count.setOnClickListener(v -> openWorkDiary());

            Rect scrollBounds = new Rect();
            user_progress.getHitRect(scrollBounds);

            swipe_container.getViewTreeObserver().addOnScrollChangedListener(() -> swipe_container.setEnabled(user_progress.getLocalVisibleRect(scrollBounds)));

            swipe_container.setOnRefreshListener(this);
            swipe_container.setColorSchemeColors(OustResourceUtils.getColors());

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
            loaderAnimi();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void loaderAnimi() {
        try {
            new Handler().postDelayed(() -> {
                try {
                    ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(branding_icon, "scaleX", 1.0f, 0.85f);
                    scaleDownX.setDuration(1200);
                    scaleDownX.setRepeatCount(ValueAnimator.INFINITE);
                    scaleDownX.setRepeatMode(ValueAnimator.REVERSE);
                    scaleDownX.setInterpolator(new LinearInterpolator());
                    ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(branding_icon, "scaleY", 1.0f, 0.85f);
                    scaleDownY.setDuration(1200);
                    scaleDownY.setRepeatCount(ValueAnimator.INFINITE);
                    scaleDownY.setRepeatMode(ValueAnimator.REVERSE);
                    scaleDownY.setInterpolator(new LinearInterpolator());
                    scaleDownY.start();
                    scaleDownX.start();

                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }, FIVE_HUNDRED_MILLI_SECONDS);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void initData() {
        try {
            new Handler().postDelayed(this::setContentComponent, 800);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void onCalender() {
        Intent calendarIntent = new Intent(this, CalendarScreen.class);
        startActivity(calendarIntent);
    }

    private void onLeaderBoard() {
        Intent intent = new Intent(this, NewLeaderBoardActivity.class);
        startActivity(intent);
    }

    private void setContentComponent() {
        try {
            this.commonLandingDataHashMap = OustDataHandler.getInstance().getCommonLandingDataHashMap();
            OustPreferences.saveAppInstallVariable("assessmentScroll", true);
            OustPreferences.saveAppInstallVariable("allAssessmentLoad", false);
            OustPreferences.saveAppInstallVariable("courseScroll", true);
            OustPreferences.saveAppInstallVariable("allCourseLoad", false);
            tasksVPAdapter = null;
            task_type.setVisibility(GONE);
            task_vp.setVisibility(GONE);
            courseList = new ArrayList<>();
            cplList = new ArrayList<>();
            FFF_contest_List = new ArrayList<>();
            assessmentList = new ArrayList<>();

            fetchMyTaskData();

            swipe_container.setRefreshing(false);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void fetchMyTaskData() {
        try {
            if (!isRefresh) {
                setData(commonLandingDataHashMap, this);
            }

            myTaskViewModel = new ViewModelProvider(this).get(MyTaskViewModel.class);
            myTaskViewModel.init();
            runOnUiThread(() -> myTaskViewModel.getTaskMap().observe(this, commonLandingDataMap -> {
                commonLandingDataHashMap = commonLandingDataMap;
                if (isObserverLoaded || isRefresh) {
                    setData(commonLandingDataHashMap, this);
                }
                isRefresh = false;
                isObserverLoaded = true;
                OustDataHandler.getInstance().setCommonLandingDataHashMap(commonLandingDataHashMap);
            }));
            data_layout.setVisibility(View.VISIBLE);
            layout_loader.setVisibility(GONE);


        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setData(HashMap<String, CommonLandingData> myDeskData, MyTaskActivity myTaskActivity) {
        try {
            if (myDeskData == null)
                return;

            if (activeUser.getAvatar() != null && !activeUser.getAvatar().isEmpty()) {
                Picasso.get().load(activeUser.getAvatar()).placeholder(R.drawable.ic_user_avatar).into(ivAvatar);
            }


            if (myDeskData.size() != 0) {
                allModule = new ArrayList<>();
                ArrayList<CommonLandingData> allLists = new ArrayList<>(myDeskData.values());
                Collections.sort(allLists, CommonLandingData.sortByDate);
                allList = (new ArrayList<>(allLists));

                CommonLandingFilter commonLandingFilter = new CommonLandingFilter();
                courseList = commonLandingFilter.getCourseModules(allList).get();

                commonLandingFilter = new CommonLandingFilter();
                assessmentList = commonLandingFilter.getAssessmentModules(allList).get();

                commonLandingFilter = new CommonLandingFilter();
                surveyList = commonLandingFilter.getSurveyModules(allList).get();

                commonLandingFilter = new CommonLandingFilter();
                completedList = commonLandingFilter.getCompletedModules(allList).get();

                commonLandingFilter = new CommonLandingFilter();
                cplList = commonLandingFilter.getCplModules(allList).get();

                commonLandingFilter = new CommonLandingFilter();
                FFF_contest_List = commonLandingFilter.getFFFCModules(allList).get();

                if (courseList != null && courseList.size() != 0) {
                    allModule.addAll(courseList);
                }

                if (cplList != null && cplList.size() != 0) {
                    allModule.addAll(cplList);
                }

                if (FFF_contest_List != null && FFF_contest_List.size() != 0) {
                    allModule.addAll(FFF_contest_List);
                }

                if (assessmentList != null && assessmentList.size() != 0) {
                    allModule.addAll(assessmentList);
                }

                if (surveyList != null && surveyList.size() != 0) {
                    allModule.addAll(surveyList);
                }
            }
            tasksVPAdapter = new TasksVPAdapter(getSupportFragmentManager());
            setData();
            initFragment();

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setData() {

        if (allList.size() != 0) {
            double completedPercentage = (((completedList.size() * 1.0) / allList.size()) * 100);
            int progressPercentage = (int) completedPercentage;
            progress_text.setText(progressPercentage + "%");

            if (progressPercentage >= 1) {
                progress_view.setVisibility(View.VISIBLE);
            } else if (progressPercentage == 0) {
                progress_view.setVisibility(GONE);
            }

            int width = progress_lay.getWidth();
            double widthPercentage = width * (progressPercentage * 1.0 / 100);
            ViewGroup.LayoutParams params = progress_view.getLayoutParams();
            params.width = (int) widthPercentage;
            progress_view.requestLayout();

            ViewGroup.MarginLayoutParams params1 = (ViewGroup.MarginLayoutParams) mProgressCircle.getLayoutParams();
            params1.setMarginStart(0);
            if (progressPercentage >= 80 && progressPercentage < 90) {
                //-80 pixels
                //-80 pixels
                params1.setMarginStart(-80);
                if (params1.getMarginStart() <= (-80)) {
                    params1.setMarginStart(0);
                    params1.setMarginStart(-80);
                }
            } else if (progressPercentage >= 90) {
                //-110 pixels
                params1.setMarginStart(0);
                params1.setMarginStart(-110);
                if (params1.getMarginStart() <= (-110)) {
                    params1.setMarginStart(0);
                    params1.setMarginStart(-110);
                }
            } else {
                params1.setMarginStart(0);
            }
            mProgressCircle.setLayoutParams(params1);
            mProgressCircle.requestLayout();
        }

        String taskDone = completedList.size() + "";
        tv_completed_count.setText(taskDone);


        String pendingText = (allModule.size()) + "";
        tv_pending_count.setText(pendingText);

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initFragment() {
        try {
            if (allModule == null || allModule.size() == 0) {
                if (!checkCatalogueExistOrNot) {
                    catalogueLearningFragment = new CatalogueLearningFragment();
                    tasksVPAdapter.addFragment(catalogueLearningFragment, getResources().getString(R.string.catalogue_label));
                    no_data_content.setVisibility(GONE);
                    no_image.setVisibility(GONE);
                    content_layout.setVisibility(View.VISIBLE);
                } else {
                    content_layout.setVisibility(GONE);
                    if (allList == null || allList.size() == 0) {
                        no_image.setImageDrawable(getResources().getDrawable(R.drawable.no_pending_module));
                        no_data_content.setText(getResources().getString(R.string.no_pending_content));
                    } else {
                        no_image.setImageDrawable(getResources().getDrawable(R.drawable.no_pending_completed));
                        no_data_content.setText(getResources().getString(R.string.no_pending_content_completed));
                    }
                    no_data_content.setVisibility(View.VISIBLE);
                    no_image.setVisibility(View.VISIBLE);
                    content_layout.setVisibility(GONE);
                }
            } else {
                task_type.setVisibility(View.VISIBLE);
                task_vp.setVisibility(View.VISIBLE);

                if (FFF_contest_List != null && FFF_contest_List.size() != 0) {
                    OustPreferences.saveAppInstallVariable("contestScroll", true);
                    OustPreferences.saveAppInstallVariable("allContestLoad", false);
                    contestModuleFragment = new ContestModuleFragment(FFF_contest_List, this);
                    tasksVPAdapter.addFragment(contestModuleFragment, getResources().getString(R.string.new_contest_list));
                    no_data_content.setVisibility(GONE);
                    no_image.setVisibility(GONE);
                    content_layout.setVisibility(View.VISIBLE);
                }

                if (cplList != null && cplList.size() != 0) {
                    OustPreferences.saveAppInstallVariable("cplScroll", true);
                    OustPreferences.saveAppInstallVariable("allCplLoad", false);
                    cplModuleFragment = new CplModuleFragment(cplList, this);
                    tasksVPAdapter.addFragment(cplModuleFragment, getResources().getString(R.string.new_play_list));
                    no_data_content.setVisibility(GONE);
                    no_image.setVisibility(GONE);
                    content_layout.setVisibility(View.VISIBLE);
                }


                if (courseList != null && courseList.size() != 0) {
                    OustPreferences.saveAppInstallVariable("courseScroll", true);
                    OustPreferences.saveAppInstallVariable("allCourseLoad", false);
                    courseModuleFragment = new CourseModuleFragment(courseList, this);
                    tasksVPAdapter.addFragment(courseModuleFragment, getResources().getString(R.string.courses_text));
                    no_data_content.setVisibility(GONE);
                    no_image.setVisibility(GONE);
                    content_layout.setVisibility(View.VISIBLE);
                }

                if (assessmentList != null && assessmentList.size() != 0) {
                    OustPreferences.saveAppInstallVariable("assessmentScroll", true);
                    OustPreferences.saveAppInstallVariable("allAssessmentLoad", false);
                    assessmentModuleFragment = new AssessmentModuleFragment(assessmentList, this);
                    tasksVPAdapter.addFragment(assessmentModuleFragment, getResources().getString(R.string.assessments_heading));
                    no_data_content.setVisibility(GONE);
                    no_image.setVisibility(GONE);
                    content_layout.setVisibility(View.VISIBLE);
                }

                if (!checkCatalogueExistOrNot) {
                    catalogueLearningFragment = new CatalogueLearningFragment();
                    tasksVPAdapter.addFragment(catalogueLearningFragment, getResources().getString(R.string.catalogue_label));
                    no_data_content.setVisibility(GONE);
                    no_image.setVisibility(GONE);
                    content_layout.setVisibility(View.VISIBLE);
                }

                if (surveyList != null && surveyList.size() != 0) {
                    tasksVPAdapter.addFragment(new SurveyModuleFragment(), getResources().getString(R.string.surveys_text));
                }
                task_vp.setAdapter(tasksVPAdapter);
                task_type.setupWithViewPager(task_vp);
                for (int i = 0; i < task_type.getTabCount(); i++) {
                    View tab = ((ViewGroup) task_type.getChildAt(0)).getChildAt(i);
                    ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
                    p.setMargins(30, 0, 10, 0);
                    tab.requestLayout();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void openWorkDiary() {
        try {
            if (activeUser != null) {
                Intent intent = new Intent(this, WorkDiaryActivity.class);
                intent.putExtra("avatar", activeUser.getAvatar());
                intent.putExtra("Name", activeUser.getUserDisplayName());
                intent.putExtra("studentId", activeUser.getStudentid());
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onRefresh() {
        try {
            if (!isRefresh) {
                isRefresh = true;
                data_layout.setVisibility(GONE);
                loaderAnimi();
                layout_loader.setVisibility(View.VISIBLE);
                setContentComponent();
            }
            swipe_container.setRefreshing(false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.catalog_menu, menu);

        MenuItem itemCalendar = menu.findItem(R.id.action_calender_my_task);
        Drawable calendarDrawable = getResources().getDrawable(R.drawable.ic_calendar_icon);
        itemCalendar.setIcon(OustResourceUtils.setDefaultDrawableColor(calendarDrawable, color));
        itemCalendar.setVisible(true);

        MenuItem itemSearch = menu.findItem(R.id.action_search_my_task);
        Drawable searchDrawable = getResources().getDrawable(R.drawable.ic_landing_search);
        itemSearch.setIcon(OustResourceUtils.setDefaultDrawableColor(searchDrawable, color));
        itemSearch.setVisible(true);

        try {
            SearchManager manager = (SearchManager) getApplicationContext().getSystemService(Context.SEARCH_SERVICE);
            SearchView search = (SearchView) itemSearch.getActionView();
            search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

            EditText searchEditText = search.findViewById(R.id.search_src_text);
            searchEditText.setTextColor(getResources().getColor(R.color.primary_text));
            searchEditText.setHintTextColor(getResources().getColor(R.color.unselected_text));

            search.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    screen_name.setVisibility(View.GONE);
                    itemCalendar.setVisible(false);
                }
            });

            search.setOnCloseListener(() -> {
                search.onActionViewCollapsed();
                screen_name.setVisibility(View.VISIBLE);
                itemCalendar.setVisible(true);
                return false;
            });

            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if (selected_tab != null && !selected_tab.isEmpty()) {
                        if (selected_tab.equalsIgnoreCase(getResources().getString(R.string.courses_text))) {
                            if (courseModuleFragment != null) {
                                courseModuleFragment.setCourseData(query, true);
                            }
                        } else if (selected_tab.equalsIgnoreCase(getResources().getString(R.string.assessments_heading))) {
                            if (assessmentModuleFragment != null) {
                                assessmentModuleFragment.setAssessmentData(query, true);
                            }
                        } else if (selected_tab.equalsIgnoreCase(getResources().getString(R.string.new_play_list))) {
                            if (cplModuleFragment != null) {
                                cplModuleFragment.setCplData(query);
                            }
                        } else if (selected_tab.equalsIgnoreCase(getResources().getString(R.string.new_contest_list))) {
                            if (contestModuleFragment != null) {
                                contestModuleFragment.setContestData(query);
                            }
                        } else if (selected_tab.equalsIgnoreCase(getResources().getString(R.string.catalogue_label))) {
                            if (catalogueLearningFragment != null) {
                                catalogueLearningFragment.setCatalogueData(query);
                            }
                        }
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    if (selected_tab != null && !selected_tab.isEmpty()) {
                        if (selected_tab.equalsIgnoreCase(getResources().getString(R.string.courses_text))) {
                            if (courseModuleFragment != null) {
                                courseModuleFragment.setCourseData(query, true);
                            }
                        } else if (selected_tab.equalsIgnoreCase(getResources().getString(R.string.assessments_heading))) {
                            if (assessmentModuleFragment != null) {
                                assessmentModuleFragment.setAssessmentData(query, true);
                            }
                        } else if (selected_tab.equalsIgnoreCase(getResources().getString(R.string.new_play_list))) {
                            if (cplModuleFragment != null) {
                                cplModuleFragment.setCplData(query);
                            }
                        } else if (selected_tab.equalsIgnoreCase(getResources().getString(R.string.new_contest_list))) {
                            if (contestModuleFragment != null) {
                                contestModuleFragment.setContestData(query);
                            }
                        } else if (selected_tab.equalsIgnoreCase(getResources().getString(R.string.catalogue_label))) {
                            if (catalogueLearningFragment != null) {
                                catalogueLearningFragment.setCatalogueData(query);
                            }
                        }
                    }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
        } else if (itemId == R.id.action_calender_my_task) {
            onCalender();
        }
        return true;
    }

    @Override
    public void swipeRefresh(boolean isEnable) {
        try {
            swipe_container.setEnabled(isEnable);
            swipe_container.setRefreshing(false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
