package com.oustme.oustsdk.layoutFour.navigationFragments;

import static android.view.View.GONE;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.clevertap.android.sdk.CleverTapAPI;
import com.google.android.material.tabs.TabLayout;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.layoutFour.LandingActivity;
import com.oustme.oustsdk.layoutFour.components.myTask.MyTaskViewModel;
import com.oustme.oustsdk.layoutFour.components.myTask.adapter.TasksVPAdapter;
import com.oustme.oustsdk.layoutFour.components.myTask.fragment.AssessmentModuleFragment;
import com.oustme.oustsdk.layoutFour.components.myTask.fragment.CourseModuleFragment;
import com.oustme.oustsdk.layoutFour.components.myTask.fragment.catalogue.CatalogueLearningFragment;
import com.oustme.oustsdk.layoutFour.components.myTask.fragment.contest.ContestModuleFragment;
import com.oustme.oustsdk.layoutFour.components.myTask.fragment.playList.CplModuleFragment;
import com.oustme.oustsdk.layoutFour.components.userOverView.ActiveUserViewModel;
import com.oustme.oustsdk.layoutFour.data.Navigation;
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
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class LearningFragment extends Fragment implements SwipeRefreshHandling {

    public static final String TAG = "LearningFragment";

    ConstraintLayout data_layout;
    View user_progress;
    TextView tv_completed_count;
    TextView tv_completed;
    CircleImageView ivAvatar;
    TextView tv_pending_count;
    FrameLayout progress_lay;
    ImageView progress_view;
    TextView progress_text;
    LinearLayout content_layout;
    ViewPager task_vp;
    TabLayout task_type;
    //Branding loader
    RelativeLayout branding_mani_layout;
    ImageView branding_bg;
    ImageView branding_icon;
    TextView branding_percentage;
    //End

    View no_data_layout;
    ImageView no_image;
    TextView no_data_content;
    private String selected_tab;
    private boolean checkCatalogueExistOrNot = false;
    int color;

    ActiveUser activeUser;
    ArrayList<CommonLandingData> allModule = new ArrayList<>();
    ArrayList<CommonLandingData> allList = new ArrayList<>();
    ArrayList<CommonLandingData> courseList = new ArrayList<>();
    ArrayList<CommonLandingData> cplList = new ArrayList<>();
    ArrayList<CommonLandingData> FFF_contest_List = new ArrayList<>();
    ArrayList<CommonLandingData> assessmentList = new ArrayList<>();
    ArrayList<CommonLandingData> completedList = new ArrayList<>();
    ArrayList<CommonLandingData> surveyList = new ArrayList<>();
    ArrayList<CommonLandingData> catalogueList = new ArrayList<>();
    private TasksVPAdapter tasksVPAdapter;

    //View model
    MyTaskViewModel myTaskViewModel;

    //common for all base fragment
    private static final String ARG_NAV_ITEM = "navItem";
    HashMap<String, CommonLandingData> commonLandingDataHashMap;
    LandingActivity landingActivity;
    CourseModuleFragment courseModuleFragment;
    CatalogueLearningFragment catalogueLearningFragment;
    CplModuleFragment cplModuleFragment;
    ContestModuleFragment contestModuleFragment;
    AssessmentModuleFragment assessmentModuleFragment;
    private FrameLayout mProgressCircle;
    int pendingModuleCount;
    int completedModuleCount;

    public LearningFragment() {
        // Required empty public constructor
    }

    public static LearningFragment newInstance(Navigation navigation) {
        LearningFragment fragment = new LearningFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_NAV_ITEM, navigation);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_learning, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        try {
            data_layout = view.findViewById(R.id.data_layout);
            user_progress = view.findViewById(R.id.user_progress);
            tv_completed_count = view.findViewById(R.id.tv_completed_count);
            tv_completed = view.findViewById(R.id.tv_completed);
            ivAvatar = view.findViewById(R.id.user_avatar);
            tv_pending_count = view.findViewById(R.id.tv_pending_count);
            progress_lay = view.findViewById(R.id.progress_lay);
            progress_view = view.findViewById(R.id.progress_view);
            mProgressCircle = view.findViewById(R.id.progress_circle);
            progress_text = view.findViewById(R.id.progress_text);
            content_layout = view.findViewById(R.id.content_layout);
            task_vp = view.findViewById(R.id.task_vp);
            task_type = view.findViewById(R.id.task_type);

            no_data_layout = view.findViewById(R.id.no_data_layout);
            no_image = no_data_layout.findViewById(R.id.no_image);
            no_data_content = no_data_layout.findViewById(R.id.no_data_content);

            //Branding loader
            branding_mani_layout = view.findViewById(R.id.branding_main_layout);
            branding_bg = branding_mani_layout.findViewById(R.id.branding_bg);
            branding_icon = branding_mani_layout.findViewById(R.id.brand_loader);
            branding_percentage = branding_mani_layout.findViewById(R.id.percentage_text);
            //End

            color = OustResourceUtils.getColors();
            task_type.setSelectedTabIndicatorColor(color);
            checkCatalogueExistOrNot = OustPreferences.getAppInstallVariable("checkCatalogueInBottomNav");

            try {
                activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
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

            task_type.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    View customView = tab.getCustomView();
                    if (tab.getText() != null) {
                        selected_tab = String.valueOf(tab.getText());

                        if (selected_tab.equalsIgnoreCase(getResources().getString(R.string.courses_text))) {
                            try {
                                CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getActivity());
                                HashMap<String, Object> eventUpdate = OustSdkTools.getCleverTapEventData();
                                eventUpdate.put("ClickedOnCourseTab", true);
                                Log.d(TAG, "CleverTap instance: " + eventUpdate);

                                if (clevertapDefaultInstance != null) {
                                    clevertapDefaultInstance.pushEvent("CourseTab_Clicks", eventUpdate);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                OustSdkTools.sendSentryException(e);
                            }
                        } else if (selected_tab.equalsIgnoreCase(getResources().getString(R.string.assessments_heading))) {
                            try {
                                CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getActivity());
                                HashMap<String, Object> eventUpdate = OustSdkTools.getCleverTapEventData();
                                eventUpdate.put("ClickedOnAssessmentTab", true);
                                Log.d(TAG, "CleverTap instance: " + eventUpdate);

                                if (clevertapDefaultInstance != null) {
                                    clevertapDefaultInstance.pushEvent("AssessmentTab_Clicks", eventUpdate);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                OustSdkTools.sendSentryException(e);
                            }
                        } else if (selected_tab.equalsIgnoreCase(getResources().getString(R.string.new_play_list))) {
                            try {
                                CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getActivity());
                                HashMap<String, Object> eventUpdate = OustSdkTools.getCleverTapEventData();
                                eventUpdate.put("ClickedOnPlaylistTab", true);
                                Log.d(TAG, "CleverTap instance: " + eventUpdate);

                                if (clevertapDefaultInstance != null) {
                                    clevertapDefaultInstance.pushEvent("PlaylistTab_Clicks", eventUpdate);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                OustSdkTools.sendSentryException(e);
                            }
                        }
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
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            landingActivity = (LandingActivity) context;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void setContentComponent() {
        try {
            this.commonLandingDataHashMap = OustDataHandler.getInstance().getCommonLandingDataHashMap();
            tasksVPAdapter = null;
            task_type.setVisibility(GONE);
            task_vp.setVisibility(GONE);
            courseList = new ArrayList<>();
            catalogueList = new ArrayList<>();
            cplList = new ArrayList<>();
            FFF_contest_List = new ArrayList<>();
            assessmentList = new ArrayList<>();

            fetchMyTaskData();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void fetchMyTaskData() {
        try {
            no_data_content.setVisibility(GONE);
            no_image.setVisibility(GONE);
            data_layout.setVisibility(View.VISIBLE);
            branding_mani_layout.setVisibility(View.VISIBLE);
            content_layout.setVisibility(View.VISIBLE);
            task_type.setVisibility(View.VISIBLE);
            myTaskViewModel = new ViewModelProvider(this).get(MyTaskViewModel.class);
            myTaskViewModel.init();
            Objects.requireNonNull(requireActivity()).runOnUiThread(() -> myTaskViewModel.getTaskMap().observe(getViewLifecycleOwner(), commonLandingDataMap -> {
                commonLandingDataHashMap = commonLandingDataMap;
                OustDataHandler.getInstance().setCommonLandingDataHashMap(commonLandingDataHashMap);
                pendingModuleCount = OustPreferences.getSavedInt("pendingModuleCount");
                completedModuleCount = OustPreferences.getSavedInt("completedModuleCount");
                setData(commonLandingDataHashMap, getChildFragmentManager());
            }));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            branding_mani_layout.setVisibility(GONE);
        }
    }

    public void setData(HashMap<String, CommonLandingData> myDeskData, FragmentManager fragmentManager) {
        try {
            data_layout.setVisibility(View.VISIBLE);
            branding_mani_layout.setVisibility(View.VISIBLE);
            if (myDeskData == null) {
                branding_mani_layout.setVisibility(GONE);
                return;
            }

            if (activeUser.getAvatar() != null && !activeUser.getAvatar().isEmpty()) {
                Picasso.get().load(activeUser.getAvatar()).placeholder(R.drawable.ic_user_avatar).into(ivAvatar);
            } else {
                ActiveUserViewModel activeUserViewModel = new ViewModelProvider(this).get(ActiveUserViewModel.class);
                activeUserViewModel.init();
                activeUserViewModel.getmActiveUser().observe(getViewLifecycleOwner(), activeUserModel -> {
                    activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
                    if (activeUser != null) {
                        if (activeUser.getAvatar() != null && !activeUser.getAvatar().isEmpty()) {
                            Picasso.get().load(activeUser.getAvatar()).placeholder(R.drawable.ic_user_avatar).networkPolicy(NetworkPolicy.OFFLINE).into(ivAvatar);
                        }
                    }
                });
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
                catalogueList = commonLandingFilter.getCatalogueModules(allList).get();

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
            } else {
                allList = new ArrayList<>();
//                allModule = new ArrayList<>();
            }
            tasksVPAdapter = new TasksVPAdapter(fragmentManager);
            setData();
            initFragment();
        } catch (Exception e) {
            branding_mani_layout.setVisibility(GONE);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setData() {
        try {
            if (allList.size() != 0 || pendingModuleCount == 0) {
                double completedPercentage;
                int totalModuleCount = completedModuleCount + allList.size();
                completedPercentage = (((completedModuleCount * 1.0) / totalModuleCount) * 100);
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
            tv_completed_count.setText(String.valueOf(completedModuleCount));
            tv_pending_count.setText(String.valueOf(allList.size()));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initFragment() {
        try {
            if ((allModule == null || allModule.size() == 0) || (allList.size() == 0 && pendingModuleCount == 0)) {
                if (!checkCatalogueExistOrNot) {
                    catalogueLearningFragment = new CatalogueLearningFragment();
                    tasksVPAdapter.addFragment(catalogueLearningFragment, getResources().getString(R.string.catalogue_label));
                    no_data_content.setVisibility(GONE);
                    no_image.setVisibility(GONE);
                    branding_mani_layout.setVisibility(GONE);
                    content_layout.setVisibility(View.VISIBLE);
                } else {
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
                    branding_mani_layout.setVisibility(GONE);
                    task_type.setVisibility(GONE);
                }
            } else {
                try {
                    task_type.setVisibility(View.VISIBLE);
                    task_vp.setVisibility(View.VISIBLE);

                    if (FFF_contest_List != null && FFF_contest_List.size() != 0) {
                        OustPreferences.saveAppInstallVariable("contestScroll", true);
                        OustPreferences.saveAppInstallVariable("allContestLoad", false);
                        contestModuleFragment = new ContestModuleFragment(FFF_contest_List, this);
                        tasksVPAdapter.addFragment(contestModuleFragment, getResources().getString(R.string.new_contest_list));
                        no_data_content.setVisibility(GONE);
                        no_image.setVisibility(GONE);
                        branding_mani_layout.setVisibility(GONE);
                        content_layout.setVisibility(View.VISIBLE);
                    }

                    if (cplList != null && cplList.size() != 0) {
                        OustPreferences.saveAppInstallVariable("cplScroll", true);
                        OustPreferences.saveAppInstallVariable("allCplLoad", false);
                        cplModuleFragment = new CplModuleFragment(cplList, this);
                        tasksVPAdapter.addFragment(cplModuleFragment, getResources().getString(R.string.new_play_list));
                        no_data_content.setVisibility(GONE);
                        no_image.setVisibility(GONE);
                        branding_mani_layout.setVisibility(GONE);
                        content_layout.setVisibility(View.VISIBLE);
                    }


                    if (courseList != null && courseList.size() != 0) {
                        OustPreferences.saveAppInstallVariable("courseScroll", true);
                        OustPreferences.saveAppInstallVariable("allCourseLoad", false);
                        courseModuleFragment = new CourseModuleFragment(courseList, this);
                        tasksVPAdapter.addFragment(courseModuleFragment, getResources().getString(R.string.courses_text));
                        no_data_content.setVisibility(GONE);
                        no_image.setVisibility(GONE);
                        branding_mani_layout.setVisibility(GONE);
                        content_layout.setVisibility(View.VISIBLE);
                    }

                    if (assessmentList != null && assessmentList.size() != 0) {
                        OustPreferences.saveAppInstallVariable("assessmentScroll", true);
                        OustPreferences.saveAppInstallVariable("allAssessmentLoad", false);
                        assessmentModuleFragment = new AssessmentModuleFragment(assessmentList, this);
                        tasksVPAdapter.addFragment(assessmentModuleFragment, getResources().getString(R.string.assessments_heading));
                        no_data_content.setVisibility(GONE);
                        no_image.setVisibility(GONE);
                        branding_mani_layout.setVisibility(GONE);
                        content_layout.setVisibility(View.VISIBLE);
                    }

                    if (!checkCatalogueExistOrNot) {
                        if (catalogueList.size() > 0) {
                            catalogueLearningFragment = new CatalogueLearningFragment();
                            tasksVPAdapter.addFragment(catalogueLearningFragment, getResources().getString(R.string.catalogue_label));
                            no_data_content.setVisibility(GONE);
                            no_image.setVisibility(GONE);
                            branding_mani_layout.setVisibility(GONE);
                            content_layout.setVisibility(View.VISIBLE);
                        }
                    }

                    if (surveyList != null && surveyList.size() != 0) {
                        tasksVPAdapter.addFragment(new SurveyModuleFragment(), getResources().getString(R.string.surveys_text));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }

                int itemIndexToSelect = -1;
                PagerAdapter currentPagerAdapter = task_vp.getAdapter();
                if (currentPagerAdapter != null) {
                    CharSequence currentPageTitle = currentPagerAdapter.getPageTitle(task_vp.getCurrentItem());
                    if (currentPageTitle != null) {
                        for (int i = 0; i < tasksVPAdapter.getCount(); i++) {
                            if (currentPageTitle.equals(tasksVPAdapter.getPageTitle(i))) {
                                itemIndexToSelect = i;
                                break;
                            }
                        }
                    }
                }
                task_vp.setAdapter(tasksVPAdapter);
                if (itemIndexToSelect != -1) {
                    task_vp.setCurrentItem(itemIndexToSelect);
                }
                task_type.setupWithViewPager(task_vp);
                for (int i = 0; i < task_type.getTabCount(); i++) {
                    View tab = ((ViewGroup) task_type.getChildAt(0)).getChildAt(i);
                    ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
                    p.setMargins(30, 0, 10, 0);
                    tab.requestLayout();
                }
            }
        } catch (Exception e) {
            branding_mani_layout.setVisibility(GONE);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void openWorkDiary() {
        try {
            if (activeUser != null) {
                try {
                    CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(OustSdkApplication.getContext());
                    HashMap<String, Object> eventUpdate = OustSdkTools.getCleverTapEventData();
                    eventUpdate.put("ClickedOnMyDiary", true);
                    Log.d(TAG, "CleverTap instance: " + eventUpdate);
                    if (clevertapDefaultInstance != null) {
                        clevertapDefaultInstance.pushEvent("MyDiary_Views", eventUpdate);
                        clevertapDefaultInstance.pushEvent("MyDiary_Clicks", eventUpdate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
                Intent intent = new Intent(getContext(), WorkDiaryActivity.class);
                intent.putExtra("avatar", activeUser.getAvatar());
                intent.putExtra("Name", activeUser.getUserDisplayName());
                intent.putExtra("studentId", activeUser.getStudentid());
                Objects.requireNonNull(requireActivity()).startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void onSearch(String query, boolean showData) {
        try {
            if (query != null) {
                if (selected_tab != null && !selected_tab.isEmpty()) {
                    if (selected_tab.equalsIgnoreCase(getResources().getString(R.string.courses_text))) {
                        if (courseModuleFragment != null) {
                            courseModuleFragment.setCourseData(query, showData);
                        }
                    } else if (selected_tab.equalsIgnoreCase(getResources().getString(R.string.assessments_heading))) {
                        if (assessmentModuleFragment != null) {
                            assessmentModuleFragment.setAssessmentData(query, showData);
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
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void swipeRefresh(boolean isEnable) {
        try {
           /* swipe_container.setEnabled(isEnable);
            swipe_container.setRefreshing(false);*/
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        try {
            if (!hidden) {
                data_layout.setVisibility(View.VISIBLE);
                branding_mani_layout.setVisibility(View.VISIBLE);
                setContentComponent();
            }

            if (landingActivity != null) {
                landingActivity.startOustServices();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            String currentFragment = OustPreferences.get("currentFragment");
            if (currentFragment != null && currentFragment.equalsIgnoreCase("learningFragment")) {
                data_layout.setVisibility(View.VISIBLE);
                branding_mani_layout.setVisibility(View.VISIBLE);
                setContentComponent();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}