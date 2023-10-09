package com.oustme.oustsdk.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.Nullable;
import android.util.Log;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oustme.oustsdk.firebase.FFContest.ContestNotificationMessage;
import com.oustme.oustsdk.request.ContestUserDataRequest;
import com.oustme.oustsdk.request.UserF3CQuestionScoreData;
import com.oustme.oustsdk.request.UserF3CScoreData;
import com.oustme.oustsdk.request.UserF3CScoreRequestData_v2;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 26/10/17.
 */

public class ContestScoreSubmitService extends Service {
//    public ContestScoreSubmitService() {
//        super(ContestScoreSubmitService.class.getName());
//    }

private long timeToEndContest;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timeToEndContest=OustPreferences.getTimeForNotification("timeToEndContest");
        if(timeToEndContest>0) {
            checkForContestOver();
        }
    }


    private void checkForContestOver(){
        timeToEndContest--;
        Log.e("--------------------", "" + timeToEndContest);
        String contestNotificationMessage = OustPreferences.get("contestnotification_message");
        String contestScoreStr=OustPreferences.get("contestScore");
        if((contestScoreStr!=null)&&(!contestScoreStr.isEmpty())) {
            if ((contestNotificationMessage != null) && (!contestNotificationMessage.isEmpty())) {
                Gson gson = new Gson();
                ContestNotificationMessage contestNotificationMessage1 = gson.fromJson(contestNotificationMessage, ContestNotificationMessage.class);
                if (((timeToEndContest+ 5) < 0)) {
                    checkForSavedScore(contestNotificationMessage1);
                } else {
                    waitForContestOver();
                }
            } else {
                this.stopSelf();
            }
        }else {
            this.stopSelf();
        }
    }

    private void waitForContestOver(){
        Log.e("--------------------","waitForContestOver");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    checkForContestOver();
                } catch (Exception e) {}
            }
        },1000);
    }


    private void checkForSavedScore(ContestNotificationMessage contestNotificationMessage){
        String contestScoreStr=OustPreferences.get("contestScore");
        Log.e("--------------------","checkForSavedScore");
        if((contestScoreStr!=null)&&(!contestScoreStr.isEmpty())){
            Gson gson=new Gson();
            OustPreferences.clear("contestScore");
            List<Map<String,Object>> ffcResponceList=gson.fromJson(contestScoreStr, new TypeToken<List<Map<String,Object>>>(){}.getType());
            calculateFinalScore(ffcResponceList,contestNotificationMessage);
        }
    }

    private void calculateFinalScore(List<Map<String,Object>> ffcResponceList,ContestNotificationMessage contestNotificationMessage){
        try {
            Log.e("--------------------","calculateFinalScore");
            int totalRight = 0;
            long totalScore = 0;
            long totalTimea = 0;
            List<UserF3CQuestionScoreData> questionScoreData = new ArrayList<>();
            for (int i = 0; i < ffcResponceList.size(); i++) {
                try {
                    Map<String, Object> ffcResponce = ffcResponceList.get(i);
                    if (((boolean) ffcResponce.get("correct"))) {
                        totalRight++;
                        long respTime = 0;
                        if (ffcResponce.get("responseTime").getClass().equals(Double.class)) {
                            double d = (double) ffcResponce.get("responseTime");
                            respTime = (long) d;
                        } else if (ffcResponce.get("responseTime").getClass().equals(Long.class)) {
                            respTime = (long) ffcResponce.get("responseTime");
                        }
                        totalTimea += respTime;
                    }
                    UserF3CQuestionScoreData userF3CQuestionScoreData = new UserF3CQuestionScoreData();
                    if (ffcResponce.get("qId").getClass().equals(Double.class)) {
                        double d = (double) ffcResponce.get("qId");
                        userF3CQuestionScoreData.setqId(((long) d));
                    } else if (ffcResponce.get("qId").getClass().equals(Long.class)) {
                        userF3CQuestionScoreData.setqId(((long) ffcResponce.get("qId")));
                    }
                    if (ffcResponce.get("responseTime").getClass().equals(Double.class)) {
                        double d = (double) ffcResponce.get("responseTime");
                        userF3CQuestionScoreData.setResponseTime(((long) d));
                    } else if (ffcResponce.get("responseTime").getClass().equals(Long.class)) {
                        userF3CQuestionScoreData.setResponseTime(((long) ffcResponce.get("responseTime")));
                    }
                    if (ffcResponce.get("answer") != null) {
                        userF3CQuestionScoreData.setAnswer((String) ffcResponce.get("answer"));
                    }
                    userF3CQuestionScoreData.setCorrect(((boolean) ffcResponce.get("correct")));
                    questionScoreData.add(userF3CQuestionScoreData);
                } catch (Exception e) {
                }
            }
            long avgTime = 0;
            if (totalRight > 0) {
                avgTime = (totalTimea / totalRight);
                totalScore = ((contestNotificationMessage.getQuestionTime() * totalRight) + (contestNotificationMessage.getQuestionTime() - avgTime));
            }
            UserF3CScoreRequestData_v2 userF3CScoreRequestData_v2 = new UserF3CScoreRequestData_v2();
            userF3CScoreRequestData_v2.setQuestionScoreData(questionScoreData);
            UserF3CScoreData userF3CScoreData = new UserF3CScoreData();
            userF3CScoreData.setAvatar(contestNotificationMessage.getAvatar());
            userF3CScoreData.setAverageTime(avgTime);
            userF3CScoreData.setDisplayName(contestNotificationMessage.getDisplayName());
            userF3CScoreData.setUserId(contestNotificationMessage.getStudentId());
            userF3CScoreData.setF3cId(contestNotificationMessage.getContestID());
            userF3CScoreData.setRightCount(totalRight);
            userF3CScoreData.setScore(totalScore);
            userF3CScoreRequestData_v2.setScoreData(userF3CScoreData);
            Gson gson = new Gson();
            String str = gson.toJson(userF3CScoreRequestData_v2);
            OustPreferences.save("f3contestrequest", str);
            sendUserRigntCount(totalScore, totalRight,contestNotificationMessage);
            Intent intent = new Intent(Intent.ACTION_SYNC, null, OustSdkApplication.getContext(),SendApiServices.class );
            OustSdkApplication.getContext().startService(intent);
            this.stopSelf();
        }catch (Exception e){}

    }

    private void sendUserRigntCount(long score,int totalRight,ContestNotificationMessage contestNotificationMessage){
        try {
            if(totalRight>contestNotificationMessage.getLuckyWinnerCorrectCount()) {
                ContestUserDataRequest contestUserDataRequest = new ContestUserDataRequest();
                contestUserDataRequest.setStudentid(contestNotificationMessage.getStudentId());
                contestUserDataRequest.setF3cId(("" + contestNotificationMessage.getContestID()));
                contestUserDataRequest.setScore(("" + score));
                Gson gson = new Gson();
                String str = gson.toJson(contestUserDataRequest);
                OustPreferences.save("f3cuser_rightcount_request", str);
            }
        }catch (Exception e){}
    }
}

