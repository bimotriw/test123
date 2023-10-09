package com.oustme.oustsdk.activity.FFContest;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.EventLeaderboardActivity;
import com.oustme.oustsdk.adapter.FFContest.FFContestAnswerViewAdapter;
import com.oustme.oustsdk.firebase.FFContest.FFCFirebaseQuestionResponse;
import com.oustme.oustsdk.firebase.FFContest.FFCFirebaseResponse;
import com.oustme.oustsdk.firebase.FFContest.FastestFingerContestData;
import com.oustme.oustsdk.interfaces.common.FFContestAnswerCallback;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustGATools;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.List;


/**
 * Created by admin on 04/08/17.
 */

public class FFContestAnswersActivity extends AppCompatActivity implements FFContestAnswerCallback {

    private ActiveUser activeUser;
    private FastestFingerContestData fastestFingerContestData;
    private ViewPager pager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        try{
            OustSdkTools.setLocale(FFContestAnswersActivity.this);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        setContentView(R.layout.viewpager);
        pager =(ViewPager) findViewById(R.id.viewpager);
        Intent callingIntent=getIntent();
        String fastestFingerContestDataStr=callingIntent.getStringExtra("fastestFingerContestData");
        Gson gson=new Gson();
        if((fastestFingerContestDataStr!=null)&&(!fastestFingerContestDataStr.isEmpty())){
            fastestFingerContestData=gson.fromJson(fastestFingerContestDataStr,FastestFingerContestData.class);
        }
        // initViews();
        int rowNo=callingIntent.getIntExtra("rowNo",0);
        List<FFCFirebaseResponse> ffcFirebaseResponseList=gson.fromJson(callingIntent.getStringExtra("ffcFirebaseResponseList"), new TypeToken<List<FFCFirebaseResponse>>(){}.getType());
        List<FFCFirebaseQuestionResponse> ffcFirebaseQuestionResponseList=gson.fromJson(callingIntent.getStringExtra("ffcFirebaseQuestionResponseList"), new TypeToken<List<FFCFirebaseQuestionResponse>>(){}.getType());
        activeUser= OustAppState.getInstance().getActiveUser();
        FFContestAnswerViewAdapter ffContestAnswerViewAdapter = new FFContestAnswerViewAdapter(getSupportFragmentManager(),fastestFingerContestData,FFContestAnswersActivity.this,
                ffcFirebaseResponseList,ffcFirebaseQuestionResponseList);
        pager.setAdapter(ffContestAnswerViewAdapter);
        if(rowNo>0){
            pager.setCurrentItem(rowNo);
        }
//        OustGATools.getInstance().reportPageViewToGoogle(FFContestAnswersActivity.this,"FFContest View Answers Page");

    }

    @Override
    public void gotoNextSlide() {
        try {
            pager.setCurrentItem((pager.getCurrentItem() + 1));
        }catch (Exception e){}
    }

    @Override
    public void gotoPreviousSlide() {
        if((pager.getCurrentItem())>0){
            pager.setCurrentItem((pager.getCurrentItem()-1));
        }
    }

    @Override
    protected void onStart() {

        super.onStart();

    }
}
