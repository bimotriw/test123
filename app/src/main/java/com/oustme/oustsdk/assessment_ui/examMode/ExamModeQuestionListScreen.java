package com.oustme.oustsdk.assessment_ui.examMode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Bundle;
import android.util.Log;

import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.assessment_ui.assessmentDetail.AssessmentDetailScreen;
import com.oustme.oustsdk.question_module.adapter.QuestionListAdapter;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.utils.OustResourceUtils;

import java.util.ArrayList;
import java.util.Collections;

public class ExamModeQuestionListScreen extends AppCompatActivity {

    CollapsingToolbarLayout collapsing_toolbar_layout;
    ImageView assessment_banner_image;
    RecyclerView question_list_rv;

    private int color;
    private int bgColor;
    private String bannerImage,moduleName;
    private ArrayList<DTOQuestions> questionsArrayList;

    QuestionListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);

        setContentView(R.layout.activity_exam_mode_question_list_screen);

        try{

            if (OustSdkApplication.getContext() == null) {
                OustSdkApplication.setmContext(ExamModeQuestionListScreen.this);
            }
            OustSdkTools.setLocale(ExamModeQuestionListScreen.this);

            getColors();
            Toolbar toolbar = (Toolbar)findViewById(R.id.collapsing_toolbar);
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            if(actionBar!=null)
            {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            collapsing_toolbar_layout = findViewById(R.id.collapsing_toolbar_layout);
            assessment_banner_image = findViewById(R.id.assessment_banner_image);
            question_list_rv = findViewById(R.id.question_list_rv);

            Bundle bundle = getIntent().getExtras();
            if(bundle!=null){
                bannerImage = bundle.getString("bannerImage");
                moduleName = bundle.getString("moduleName");
            }

            questionsArrayList = OustStaticVariableHandling.getInstance().getQuestionsArrayList();

            initData();

        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }


    }

    private void getColors() {

        if (OustPreferences.getAppInstallVariable("isLayout4")) {
            color = OustResourceUtils.getColors();
            bgColor = OustResourceUtils.getToolBarBgColor();
        } else {
            bgColor = OustResourceUtils.getColors();
            color = OustResourceUtils.getToolBarBgColor();

        }

    }

    private void initData(){
        try{

           // collapsing_toolbar_layout.setContentScrimColor(color);
            if(bannerImage!=null&&!bannerImage.isEmpty()){
                Glide.with(ExamModeQuestionListScreen.this).load(bannerImage).diskCacheStrategy(DiskCacheStrategy.ALL).into(assessment_banner_image);
            }

            if(moduleName!=null&&!moduleName.isEmpty()){
                collapsing_toolbar_layout.setTitle(moduleName);
            }

            if(questionsArrayList!=null&&questionsArrayList.size()!=0){
                Log.e("ExamMode","Questions "+questionsArrayList.size());
                adapter = new QuestionListAdapter();
                adapter.setAdapter(ExamModeQuestionListScreen.this,questionsArrayList);
                question_list_rv.setAdapter(adapter);

            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();
        if(itemId==android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}