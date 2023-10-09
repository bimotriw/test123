package com.oustme.oustsdk.LiveClasses;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.response.common.JoinRegisterMeetingResponse;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.Date;


public class CallEndActivity extends AppCompatActivity {

    ImageView back_image;
    TextView call_end_heading;
    LinearLayout call_end_rejoin_layout;
    LinearLayout call_end_event_rejoin;
    TextView call_end_timer;
    TextView back_to_oust_txt;
    JoinRegisterMeetingResponse joinRegisterMeetingResponse;
    String meetStartTime;
    String meetEndTime;
    String responseString;
    String meetName;
    TimerClass timer;
    String tempValLiveClassId;
    String tempValLiveClassMeetingMapId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_end);

        back_image = findViewById(R.id.back_image);
        call_end_heading = findViewById(R.id.call_end_heading);
        call_end_rejoin_layout = findViewById(R.id.call_end_rejoin_layout);
        call_end_event_rejoin = findViewById(R.id.call_end_event_rejoin);
        call_end_timer = findViewById(R.id.call_end_timer);
        back_to_oust_txt = findViewById(R.id.back_to_oust_txt);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent intent = getIntent();
        meetStartTime = intent.getStringExtra("meetStartTime");
        meetEndTime = intent.getStringExtra("meetEndTime");
        meetName = intent.getStringExtra("meetName");
        responseString = intent.getStringExtra("joinRegisterMeetingResponse");
        tempValLiveClassId = intent.getStringExtra("meetClassId");
        tempValLiveClassMeetingMapId = intent.getStringExtra("meetLiveClassId");
        Gson gson = new Gson();
        joinRegisterMeetingResponse = gson.fromJson(responseString, JoinRegisterMeetingResponse.class);

        long currentTime = new Date().getTime();
        Date meetingStartDate = new Date(OustSdkTools.convertToLong(meetStartTime));
        Date meetingEndDate = new Date(OustSdkTools.convertToLong(meetEndTime));

        call_end_event_rejoin.setOnClickListener(v -> {
            try {
                Intent intent1 = new Intent(CallEndActivity.this, MeetingActivity.class);
                intent1.putExtra("joinRegisterMeetingResponse", responseString);
                intent1.putExtra("meetName", meetName);
                intent1.putExtra("meetStartTime", String.valueOf(meetStartTime));
                intent1.putExtra("meetEndTime", String.valueOf(meetEndTime));
                intent1.putExtra("meetClassId", tempValLiveClassId);
                intent1.putExtra("meetLiveClassId", tempValLiveClassMeetingMapId);
                startActivity(intent1);
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        });

        if (currentTime >= meetingStartDate.getTime() && currentTime <= meetingEndDate.getTime()) {
            call_end_heading.setText(getResources().getString(R.string.you_left_the_event));
            call_end_rejoin_layout.setVisibility(View.VISIBLE);
        } else {
            call_end_heading.setText(getResources().getString(R.string.webinar_is_completed));
            call_end_rejoin_layout.setVisibility(View.GONE);
        }

        try {
            timer = new TimerClass(60000, 1000);
            timer.start();
        } catch (Exception e) {
            finish();
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        back_image.setOnClickListener(v -> finish());

        back_to_oust_txt.setOnClickListener(v -> finish());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (timer != null) {
                timer.cancel();
                timer = null;
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (timer != null) {
                timer.cancel();
                timer = null;
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private class TimerClass extends CountDownTimer {
        public TimerClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            call_end_timer.setText(millisUntilFinished / 1000 + "sec");
        }

        @Override
        public void onFinish() {
            finish();
        }
    }
}