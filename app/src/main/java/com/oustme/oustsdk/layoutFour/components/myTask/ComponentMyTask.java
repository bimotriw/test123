package com.oustme.oustsdk.layoutFour.components.myTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.customviews.CircleImageView;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.layoutFour.components.myTask.adapter.TasksVPAdapter;
import com.oustme.oustsdk.layoutFour.components.myTask.fragment.AssessmentModuleFragment;
import com.oustme.oustsdk.layoutFour.components.myTask.fragment.CourseModuleFragment;
import com.oustme.oustsdk.my_tasks.fragment.SurveyModuleFragment;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.filters.CommonLandingFilter;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.oustme.oustsdk.work_diary.WorkDiaryActivity;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ComponentMyTask extends LinearLayout {

    TextView tv_completed_count;
    TextView tv_completed;
    CircleImageView ivAvatar;
    TextView tv_pending_count;
    ProgressBar user_task_progressbar;
    NestedScrollView scroll_layout;
    ViewPager task_vp;
    TabLayout task_type;

    View no_data_layout;
    ImageView no_image;
    TextView no_data_content;

    ActiveUser activeUser;
    ArrayList<CommonLandingData> allModule = new ArrayList<>();
    ArrayList<CommonLandingData> allList = new ArrayList<>();
    ArrayList<CommonLandingData> courseList = new ArrayList<>();
    ArrayList<CommonLandingData> assessmentList = new ArrayList<>();
    ArrayList<CommonLandingData> completedList = new ArrayList<>();
    ArrayList<CommonLandingData> surveyList = new ArrayList<>();
    HashMap<String, String> commonInfoMap;

    private TasksVPAdapter tasksVPAdapter;
    int color;

    final String TAG = "ComponentMyTask";

    public ComponentMyTask(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.component_my_task, this, true);
        initViews();
    }

    private void initViews() {

        try {
            tv_completed_count = findViewById(R.id.tv_completed_count);
            tv_completed = findViewById(R.id.tv_completed);
            ivAvatar = findViewById(R.id.user_avatar);
            tv_pending_count = findViewById(R.id.tv_pending_count);
            user_task_progressbar = findViewById(R.id.user_task_progressbar);
            scroll_layout = findViewById(R.id.scroll_layout);
            task_vp = findViewById(R.id.task_vp);
            task_type = findViewById(R.id.task_type);

            no_data_layout = findViewById(R.id.no_data_layout);
            no_image = no_data_layout.findViewById(R.id.no_image);
            no_data_content = no_data_layout.findViewById(R.id.no_data_content);

            color = OustResourceUtils.getColors();
            task_type.setSelectedTabIndicatorColor(color);

            task_type.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    View customView = tab.getCustomView();

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


        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }


    }

    public void setData(HashMap<String, CommonLandingData> myDeskData, FragmentManager fragmentManager) {
        try {
            if (myDeskData == null)
                return;

            Log.d(TAG, "setData: ");
            String avatarUrl = OustPreferences.get("avatarUrl");
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                Picasso.get().load(avatarUrl).placeholder(R.drawable.ic_user_avatar).networkPolicy(NetworkPolicy.OFFLINE).into(ivAvatar);
            }

            if (myDeskData.size() != 0) {
                Log.d(TAG, "setData: size:" + myDeskData.size());
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


                if (courseList != null && courseList.size() != 0) {
                    allModule.addAll(courseList);
                }

                if (assessmentList != null && assessmentList.size() != 0) {
                    allModule.addAll(assessmentList);
                }

                if (surveyList != null && surveyList.size() != 0) {
                    allModule.addAll(surveyList);
                }


            }
            tasksVPAdapter = new TasksVPAdapter(fragmentManager);
            setData();
            initFragment();

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }


    private void setData() {

        activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));

        if (allList.size() != 0) {
            double completedPercentage = (((completedList.size() * 1.0) / allList.size()) * 100);
            user_task_progressbar.setProgress((int) completedPercentage);
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

                //Need to handle UI required
                Log.d("MyTask", "Module Completed");
                scroll_layout.setVisibility(View.GONE);

                if (allList == null || allList.size() == 0) {
                    no_image.setImageDrawable(getResources().getDrawable(R.drawable.no_pending_module));
                    no_data_content.setText(getResources().getString(R.string.no_pending_content));
                } else {
                    no_image.setImageDrawable(getResources().getDrawable(R.drawable.no_pending_completed));
                    no_data_content.setText(getResources().getString(R.string.no_pending_content_completed));
                }

            } else {

                if (courseList != null && courseList.size() != 0) {
                    tasksVPAdapter.addFragment(new CourseModuleFragment(courseList, null), getResources().getString(R.string.courses_text));
                    no_data_content.setVisibility(GONE);
                }

                if (assessmentList != null && assessmentList.size() != 0) {
                    tasksVPAdapter.addFragment(new AssessmentModuleFragment(assessmentList, null), getResources().getString(R.string.assessments_heading));
                    no_data_content.setVisibility(GONE);
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

    private void openWorkDiary() {

        try {
            if (activeUser != null) {
                Intent intent = new Intent(getContext(), WorkDiaryActivity.class);
                intent.putExtra("avatar", activeUser.getAvatar());
                intent.putExtra("Name", activeUser.getUserDisplayName());
                intent.putExtra("studentId", activeUser.getStudentid());
                getContext().startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }
}
