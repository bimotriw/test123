package com.oustme.oustsdk.activity.assessments;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.leaderboard.activity.GroupLBDataActivity;
import com.oustme.oustsdk.adapter.assessments.CheckAnswerViewAdapter;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.request.SubmitRequest;
import com.oustme.oustsdk.response.assessment.CreateGameResponse;
import com.oustme.oustsdk.response.assessment.GamePoints;
import com.oustme.oustsdk.response.assessment.UserGamePoints;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustGATools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;


/**
 * Created by shilpysamaddar on 17/03/17.
 */

public class CheckAnswerActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {
    LinearLayout checkanswermainlayout;

    CheckAnswerViewAdapter pageAdapter;
    ActiveGame activeGame;
    SubmitRequest submitRequest;
    GamePoints gamePoints;
    UserGamePoints userGamePoints;
    ActiveUser activeUser;
    CreateGameResponse createGameResponse;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            try{
                OustSdkTools.setLocale(CheckAnswerActivity.this);
            }catch (Exception e){
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            setContentView(R.layout.viewpager);
            checkanswermainlayout = (LinearLayout) findViewById(R.id.checkanswermainlayout);
            initCheckAnswer();
//            OustGATools.getInstance().reportPageViewToGoogle(CheckAnswerActivity.this,"Assessment View Answer Page");
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void initCheckAnswer() {
        try {
            OustSdkTools.setSnackbarElements(checkanswermainlayout, CheckAnswerActivity.this);
            OustSdkTools.checkInternetStatus();

            Intent CallingIntent = getIntent();
            activeUser = OustAppState.getInstance().getActiveUser();

            if ((activeUser != null) && (activeUser.getStudentid() != null)) {
            } else {
                HttpManager.setBaseUrl();
                OustFirebaseTools.initFirebase();
                String activeUserGet = OustPreferences.get("userdata");
                activeUser = OustSdkTools.getActiveUserData(activeUserGet);
            }

            Gson gson = new GsonBuilder().create();
            activeGame = gson.fromJson(CallingIntent.getStringExtra("ActiveGame"), ActiveGame.class);

            createGameResponse = gson.fromJson(CallingIntent.getStringExtra("CreateGameResponse"), CreateGameResponse.class);

            submitRequest = gson.fromJson(CallingIntent.getStringExtra("SubmitRequest"), SubmitRequest.class);

            gamePoints = gson.fromJson(CallingIntent.getStringExtra("GamePoints"), GamePoints.class);

            userGamePoints = gson.fromJson(CallingIntent.getStringExtra("UserGamePoints"), UserGamePoints.class);

            gson = new GsonBuilder().create();
            pageAdapter = new CheckAnswerViewAdapter(getSupportFragmentManager(), gson.toJson(activeUser), gson.toJson(activeGame), gson.toJson(createGameResponse), gson.toJson(submitRequest), gson.toJson(gamePoints), gson.toJson(userGamePoints));
            ViewPager pager = findViewById(R.id.viewpager);
            pager.setAdapter(pageAdapter);
            pager.addOnPageChangeListener(this);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
