package com.oustme.oustsdk.my_tasks;

import static android.view.View.GONE;

import android.content.Intent;
import android.util.Log;

import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.base.BaseActivity;
import com.oustme.oustsdk.calendar_ui.ui.CalendarScreen;
import com.oustme.oustsdk.customviews.CircleImageView;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.layoutFour.components.myTask.adapter.TasksVPAdapter;
import com.oustme.oustsdk.my_tasks.fragment.AssessmentModuleFragment;
import com.oustme.oustsdk.my_tasks.fragment.CourseModuleFragment;
import com.oustme.oustsdk.my_tasks.fragment.SurveyModuleFragment;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustDataHandler;
import com.oustme.oustsdk.tools.OustGATools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.filters.CommonLandingFilter;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.oustme.oustsdk.work_diary.WorkDiaryActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class MyTasksScreen extends BaseActivity {


    Toolbar toolbar_lay;
    FrameLayout toolbar_close_icon;
    FrameLayout calendar_lay;
    ImageView calendar_icon;
    ImageView indicator;
    CircleImageView ivAvatar;
    View include_user_progress;
    TabLayout task_type;
    ViewPager task_vp;
    View view_line;
    TextView tv_completed_count;
    TextView tv_completed;
    ProgressBar user_task_progressbar;
    TextView tv_pending_count;
    TextView tv_pending;
    TextView progress_text;
    ImageView progress_view;
    FrameLayout progress_lay;
    FrameLayout mProgressCircle;
    int width = 0;


    private int color;

    TasksVPAdapter tasksVPAdapter;
    FragmentManager fragmentManager;
    ActiveUser activeUser;
    private final ArrayList<CommonLandingData> allModule = new ArrayList<>();
    private ArrayList<CommonLandingData> allList = new ArrayList<>();
    private ArrayList<CommonLandingData> courseList = new ArrayList<>();
    private ArrayList<CommonLandingData> assessmentList = new ArrayList<>();
    private ArrayList<CommonLandingData> completedList = new ArrayList<>();
    private ArrayList<CommonLandingData> surveyList = new ArrayList<>();


    @Override
    protected int getContentView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_my_tasks_screen;
    }

    @Override
    protected void initView() {

        try {

            if (OustSdkApplication.getContext() == null) {
                OustSdkApplication.setmContext(MyTasksScreen.this);
            }
            OustSdkTools.setLocale(MyTasksScreen.this);
//            OustGATools.getInstance().reportPageViewToGoogle(MyTasksScreen.this, "Oust My Task Page");


            toolbar_lay = findViewById(R.id.toolbar_lay);
            toolbar_close_icon = findViewById(R.id.toolbar_close_icon);
            calendar_lay = findViewById(R.id.calendar_lay);
            calendar_icon = findViewById(R.id.calendar_icon);
            indicator = findViewById(R.id.indicator);
            ivAvatar = findViewById(R.id.user_avatar);
            include_user_progress = findViewById(R.id.include_user_progress);
            task_type = findViewById(R.id.task_type);
            task_vp = findViewById(R.id.task_vp);
            view_line = findViewById(R.id.view_line);
            tv_completed_count = include_user_progress.findViewById(R.id.tv_completed_count);
            tv_completed = include_user_progress.findViewById(R.id.tv_completed);
            user_task_progressbar = include_user_progress.findViewById(R.id.user_task_progressbar);
            tv_pending_count = include_user_progress.findViewById(R.id.tv_pending_count);
            tv_pending = include_user_progress.findViewById(R.id.tv_pending);
            progress_text = findViewById(R.id.progress_text);
            progress_view = findViewById(R.id.progress_view);
            progress_lay = include_user_progress.findViewById(R.id.progress_lay);
            mProgressCircle = findViewById(R.id.progress_circle);


        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    @Override
    protected void initData() {

        try {

            getColors();
            setToolbarAndIconColor();
            this.fragmentManager = getSupportFragmentManager();
            tasksVPAdapter = new TasksVPAdapter(fragmentManager);
            fetchDataForLayout();


        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    @Override
    protected void initListener() {

        try {


            calendar_lay.setOnClickListener(v -> {

                Intent calendar = new Intent(MyTasksScreen.this, CalendarScreen.class);
                startActivity(calendar);
            });

            toolbar_close_icon.setOnClickListener(v -> MyTasksScreen.this.finish());

            tv_completed.setOnClickListener(v -> openCalendar());

            tv_completed_count.setOnClickListener(v -> openCalendar());


        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void initFragment() {

        try {
            if (allModule == null || allModule.size() == 0) {

                //Need to handle UI required
                Log.d("MyTask", "Module Completed");
                view_line.setVisibility(View.GONE);

            } else {


                if (courseList != null && courseList.size() != 0) {
                    tasksVPAdapter.addFragment(new CourseModuleFragment(), getResources().getString(R.string.courses_text));
                }

                if (assessmentList != null && assessmentList.size() != 0) {
                    tasksVPAdapter.addFragment(new AssessmentModuleFragment(), getResources().getString(R.string.assessments_heading));
                }

                if (surveyList != null && surveyList.size() != 0) {
                    tasksVPAdapter.addFragment(new SurveyModuleFragment(), getResources().getString(R.string.surveys_text));
                }

                task_vp.setAdapter(tasksVPAdapter);
                task_type.setupWithViewPager(task_vp);

                for (int i = 0; i < task_type.getTabCount(); i++) {
                    View tab = ((ViewGroup) task_type.getChildAt(0)).getChildAt(i);
                    ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
                    p.setMargins(0, 0, 20, 0);
                    tab.requestLayout();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }


    }

    private void fetchDataForLayout() {

        try {

            HashMap<String, CommonLandingData> myDeskData = new HashMap<>(OustDataHandler.getInstance().getMyDeskData());
            HashMap<String, CommonLandingData> myAssessmentData = OustDataHandler.getInstance().getMyAssessmentData();
            if (myAssessmentData != null && myAssessmentData.size() > 0) {
                myDeskData.putAll(myAssessmentData);
            }

            String avatarUrl = OustPreferences.get("avatarUrl");
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                Picasso.get().load(avatarUrl).placeholder(R.drawable.ic_user_avatar).into(ivAvatar);
            }

            if (myDeskData.size() != 0) {
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


                if (courseList != null && courseList.size() != 0) {
                    allModule.addAll(courseList);
                }

                if (assessmentList != null && assessmentList.size() != 0) {
                    allModule.addAll(assessmentList);
                }

                if (surveyList != null && surveyList.size() != 0) {
                    allModule.addAll(surveyList);
                }

                setData();
                initFragment();
            }


        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void setData() {

        activeUser = OustAppState.getInstance().getActiveUser();

        if (allList.size() != 0) {
            double completedPercentage = (((completedList.size() * 1.0) / allList.size()) * 100);
//            user_task_progressbar.setProgress((int) completedPercentage);
            int progressPercentage = (int) completedPercentage;
            progress_text.setText(progressPercentage + "%");

            if (progressPercentage >= 1) {
                progress_view.setVisibility(View.VISIBLE);
            } else if (progressPercentage == 0) {
                progress_view.setVisibility(GONE);
            }
            progress_lay.post(new Runnable() {
                @Override
                public void run() {
                    width = progress_lay.getWidth();
                    double widthPercentage = width * (progressPercentage * 1.0 / 100);
                    Log.d("MyTaskscreen", "Layout width : " + width + " " + progressPercentage + " " + widthPercentage);
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
            });

        }


        String taskDone = completedList.size() + "";
        tv_completed_count.setText(taskDone);
        tv_completed.setClickable(true);


        String pendingText = (allModule.size()) + "";
        tv_pending_count.setText(pendingText);


    }

    public ArrayList<CommonLandingData> getCourseList() {


        Collections.sort(courseList, CommonLandingData.sortByDate);
        return courseList;

    }

    public ArrayList<CommonLandingData> getAssessmentList() {
        Collections.sort(assessmentList, CommonLandingData.sortByDate);
        return assessmentList;

    }

    public ArrayList<CommonLandingData> getSurveyList() {
        Collections.sort(surveyList, CommonLandingData.sortByDate);
        return surveyList;

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

        //Not working this function

    }

    private void getColors() {
        try {
            color = OustResourceUtils.getColors();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void setToolbarAndIconColor() {

        try {

            toolbar_lay.setBackgroundColor(color);
            setSupportActionBar(toolbar_lay);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void openCalendar() {

        try {
            if (activeUser != null) {
                Intent intent = new Intent(MyTasksScreen.this, WorkDiaryActivity.class);
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
}
