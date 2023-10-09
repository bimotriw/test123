package com.oustme.oustsdk.activity.common.todoactivity.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.oustme.oustsdk.activity.FFContest.FFcontestStartActivity;
import com.oustme.oustsdk.activity.assessments.AssessmentPlayActivity;
import com.oustme.oustsdk.activity.common.todoactivity.view.TodoView;
import com.oustme.oustsdk.activity.courses.CourseMultiLingualActivity;
import com.oustme.oustsdk.activity.courses.LessonsActivity;
import com.oustme.oustsdk.activity.courses.newlearnngmap.NewLearningMapActivity;
import com.oustme.oustsdk.assessment_ui.assessmentDetail.AssessmentDetailScreen;
import com.oustme.oustsdk.firebase.FFContest.FastestFingerContestData;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.MultilingualCourse;
import com.oustme.oustsdk.response.common.GameType;
import com.oustme.oustsdk.survey_module.SurveyComponentActivity;
import com.oustme.oustsdk.survey_ui.SurveyDetailActivity;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.appconstants.AppConstants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TodoPresenter {
    private static final String TAG = "TodoPresenter";
    TodoView mView;
    private FastestFingerContestData fastestFingerContestData;
    private FirebaseRefClass ffcDataRefClass;
    private FirebaseRefClass ffcQDataRefClass;
    private ActiveUser activeUser;
    public TodoPresenter(TodoView todoView)
    {
        this.mView = todoView;
    }

    public void getUserFFContest(String studentKey ) {
        Log.d(TAG, "getUserFFContest: ");
        try {
            final String message = "/landingPage/" + studentKey + "/f3c";
            Log.d(TAG, "getUserFFContest: " + message);
            ValueEventListener myFFCListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                      mView.updateUserFFCUserContest(dataSnapshot);
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            };

            DatabaseReference newsfeedRef = OustFirebaseTools.getRootRef().child(message);
            newsfeedRef.keepSynced(true);
            newsfeedRef.addValueEventListener(myFFCListener);
            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(myFFCListener, message));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
    public void removeFFCDataListener() {
        Log.d(TAG, "removeFFCDataListener: ");
        try {
            if (ffcDataRefClass != null) {
                OustFirebaseTools.getRootRef().child(ffcDataRefClass.getFirebasePath()).removeEventListener(ffcDataRefClass.getEventListener());
            }
            if (ffcQDataRefClass != null) {
                OustFirebaseTools.getRootRef().child(ffcQDataRefClass.getFirebasePath()).removeEventListener(ffcQDataRefClass.getEventListener());
            }
        } catch (Exception e) {
        }
    }
    public void fetctFFCData(String ffcId) {
        Log.d(TAG, "fetctFFCData: ");
        try {
            final String message = "/f3cData/f3c" + ffcId;
            Log.d(TAG, "fetctFFCData: " + message);
            ValueEventListener ffcDataListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mView.extractFFCData(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            };
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
            OustFirebaseTools.getRootRef().child(message).addValueEventListener(ffcDataListener);
            ffcDataRefClass = new FirebaseRefClass(ffcDataListener, message);
            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(ffcDataListener, message));
            getFFCEnrolldedUsersCount(ffcId);
        } catch (Exception e) {
        }
    }
    private void getFFCEnrolldedUsersCount(String contestId) {
        final String path = "/f3cEnrolledUserCount/f3c" + contestId + "/participants";
        DatabaseReference databaseReference = OustFirebaseTools.getRootRef().child(path);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    try {
                        //fastestFingerContestData.setEnrolledCount((long) dataSnapshot.getValue());
                        mView.updateFFCEnrolledCount((long) dataSnapshot.getValue());
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Error:");
            }
        });
    }

  /*  private void extractFFCData(DataSnapshot dataSnapshot) {
        try {
            if (dataSnapshot != null) {
                final Map<String, Object> ffcDataMap = (Map<String, Object>) dataSnapshot.getValue();
                if (null != ffcDataMap) {
                    try {
                        fastestFingerContestData = OustSdkTools.getFastestFingerContestData(fastestFingerContestData, ffcDataMap);
                        // setF3cBannerSize();
                        //   Log.d(TAG, "extractFFCData: TODO:"+fastestFingerContestData.get);
                        long bannerHideTimeNo = 1;
                        if (ffcDataMap.get("bannerHideTime") != null) {
                            bannerHideTimeNo = (long) ffcDataMap.get("bannerHideTime");
                        }
                        long bannerHideTime = (bannerHideTimeNo * (86400000));
                        if ((System.currentTimeMillis() - fastestFingerContestData.getStartTime()) > bannerHideTime) {
                        } else {
                            OustStaticVariableHandling.getInstance().setContestLive(true);
                            //showFFcBanner();
//                            if ((notificationContestId != null) && (!notificationContestId.isEmpty()) && (notificationContestId.equalsIgnoreCase(("" + fastestFingerContestData.getFfcId())))) {
//                                notificationContestId="";
//                                Intent intent = new Intent(NewLandingActivity.this, FFcontestStartActivity.class);
//                                Gson gson = new Gson();
//                                intent.putExtra("fastestFingerContestData", gson.toJson(fastestFingerContestData));
//                                startActivity(intent);
//                            }
                        }
                        updateFFCData();
                        //setContestNotificationData(ffcDataMap);
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }*/

    public void gotoFFContest() {
        if (fastestFingerContestData != null) {
            Intent intent4 = new Intent(OustSdkApplication.getContext(), FFcontestStartActivity.class);
            Gson gson = new Gson();
            intent4.putExtra(AppConstants.StringConstants.FFC_DATA, gson.toJson(fastestFingerContestData));
            OustSdkApplication.getContext().startActivity(intent4);
        }
    }
    public void launchActivity(CommonLandingData commonLandingData) {
        OustStaticVariableHandling.getInstance().setModuleClicked(true);
        if ((commonLandingData.getType() != null) && (commonLandingData.getType().contains("COLLECTION"))) {
            Log.d(TAG, "launchActivity: Lesson:");
            Intent intent = new Intent(OustSdkApplication.getContext(), LessonsActivity.class);
            String id = commonLandingData.getId();
            id = id.replace("COLLECTION", "");
            intent.putExtra("collectionId", id);
            intent.putExtra("banner", commonLandingData.getBanner());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            OustSdkApplication.getContext().startActivity(intent);
        } else if ((commonLandingData.getType() != null) && (commonLandingData.getType().contains("ASSESSMENT"))) {
            Log.d(TAG, "launchActivity: assessmentPl");
            Gson gson = new Gson();
            Intent intent;
            if(OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI)){
                intent = new Intent(OustSdkApplication.getContext(), AssessmentDetailScreen.class);
            }else{
                intent = new Intent(OustSdkApplication.getContext(), AssessmentPlayActivity.class);
            }
            //Intent intent = new Intent(OustSdkApplication.getContext(), AssessmentDetailScreen.class);
            ActiveGame activeGame = setGame(OustAppState.getInstance().getActiveUser());
            intent.putExtra("ActiveGame", gson.toJson(activeGame));
            String id = commonLandingData.getId();
            id = id.replace("ASSESSMENT", "");
            intent.putExtra("assessmentId", id);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            OustSdkApplication.getContext().startActivity(intent);
        } else if ((commonLandingData.getType() != null) && (commonLandingData.getType().contains("SURVEY"))) {
            Gson gson = new Gson();
            Log.d(TAG, "launchActivity: assessmentquestion:");
            Intent intent = new Intent(OustSdkApplication.getContext(), SurveyDetailActivity.class);
            if(OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_SURVEY_NEW_UI)){
                intent = new Intent(OustSdkApplication.getContext(), SurveyComponentActivity.class);
            }
            ActiveGame activeGame = setGame(OustAppState.getInstance().getActiveUser());
            intent.putExtra("ActiveGame", gson.toJson(activeGame));
            String id = commonLandingData.getId();
            id = id.replace("ASSESSMENT", "");
            intent.putExtra("assessmentId", id);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            OustSdkApplication.getContext().startActivity(intent);
        } else {
            if (commonLandingData.getCourseType() != null && commonLandingData.getCourseType().equalsIgnoreCase("Multilingual")) {
                Log.d(TAG, "launchActivity: Multilingual");
                Intent intent = new Intent(OustSdkApplication.getContext(), CourseMultiLingualActivity.class);
                String id = commonLandingData.getId();
                List<MultilingualCourse> multilingualCourseList = new ArrayList<>();
                multilingualCourseList = commonLandingData.getMultilingualCourseListList();
                id = id.replace("COURSE", "");
                intent.putExtra("learningId", id);
                Bundle bundle = new Bundle();
                bundle.putSerializable("multilingualDataList", (Serializable) multilingualCourseList);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                OustSdkApplication.getContext().startActivity(intent);
            } else {
                Log.d(TAG, "launchActivity:NewLearningMapActivity ");
                Intent intent = new Intent(OustSdkApplication.getContext(), NewLearningMapActivity.class);
                String id = commonLandingData.getId();
                id = id.replace("COURSE", "");
                intent.putExtra("learningId", id);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                OustSdkApplication.getContext().startActivity(intent);
            }
        }
    }
    public ActiveGame setGame(ActiveUser activeUser) {
        ActiveGame activeGame = new ActiveGame();
        activeGame.setGameid("");
        activeGame.setGames(activeUser.getGames());
        activeGame.setStudentid(activeUser.getStudentid());
        activeGame.setChallengerid(activeUser.getStudentid());
        activeGame.setChallengerDisplayName(activeUser.getUserDisplayName());
        activeGame.setChallengerAvatar(activeUser.getAvatar());
        activeGame.setOpponentAvatar(OustSdkTools.getMysteryAvatar());
        activeGame.setOpponentid("Mystery");
        activeGame.setOpponentDisplayName("Mystery");
        activeGame.setGameType(GameType.MYSTERY);
        activeGame.setGuestUser(false);
        activeGame.setRematch(false);
        activeGame.setGroupId("");
        activeGame.setLevel(activeUser.getLevel());
        activeGame.setLevelPercentage(activeUser.getLevelPercentage());
        activeGame.setWins(activeUser.getWins());
        activeGame.setIsLpGame(false);
        return activeGame;
    }
}
