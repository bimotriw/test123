package com.oustme.oustsdk.activity.courses.newlearnngmap;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.util.Log;

import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.courses.learningmapmodule.LearningMapModuleActivity;
import com.oustme.oustsdk.base.BaseActivity;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.interfaces.common.ServerResponseListener;
import com.oustme.oustsdk.network.VolleyRequest;
import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.response.course.UserCardData;
import com.oustme.oustsdk.response.course.UserCourseData;
import com.oustme.oustsdk.response.course.UserLevelData;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.room.dto.DTOUserCardData;
import com.oustme.oustsdk.room.dto.DTOUserCourseData;
import com.oustme.oustsdk.room.dto.DTOUserLevelData;
import com.oustme.oustsdk.sqlite.UserCourseScoreDatabaseHandler;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.CommonTools;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewCourseLauncherActivity extends BaseActivity {

    private final String TAG = "NewCourseLaunch";
    private UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler;

    private CourseDataClass courseDataClass = null;
    private DTOUserCourseData userCourseData = null;
    private int learningPathId = 0;
    private ActiveUser activeUser;
    private boolean isLauncherCalled = false;

    @Override
    protected int getContentView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        OustSdkTools.setLocale(NewCourseLauncherActivity.this);
        return R.layout.activity_new_card_launcher;
    }

    @Override
    protected void initView() {
        isLauncherCalled = false;
    }

    @Override
    protected void initData() {

        activeUser = OustAppState.getInstance().getActiveUser();
        if ((activeUser != null) && (activeUser.getStudentid() != null)) {
        } else {
            Log.e(TAG, "active user is null ");
            OustSdkApplication.setmContext(NewCourseLauncherActivity.this);
            String activeUserGet = OustPreferences.get("userdata");
            activeUser = OustSdkTools.getActiveUserData(activeUserGet);
            OustFirebaseTools.initFirebase();
            OustAppState.getInstance().setActiveUser(activeUser);
        }

        Intent intent = getIntent();
        if(intent.hasExtra("learningId")){
            learningPathId = Integer.parseInt(intent.getStringExtra("learningId"));
        }
        getLearningMap(learningPathId);
    }

    @Override
    protected void initListener() {
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    void getLearningMap(final int learningPathId){
        Log.e(TAG, "inside load data from firebase ");
        String message = "/course/course" + learningPathId;
        ValueEventListener learningMapListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (null != dataSnapshot.getValue()) {
                        Log.e(TAG, "got data from firebase ");
                        final Map<String, Object> learingMap = (Map<String, Object>) dataSnapshot.getValue();
                        courseDataClass = CommonTools.extractCourseData(learningPathId, learingMap);

                        OustStaticVariableHandling.getInstance().setCurrentLearningPathId(learningPathId);
                        String s1 = "" + activeUser.getStudentKey() + "" + OustStaticVariableHandling.getInstance().getCurrentLearningPathId();
                        Log.d(TAG, "onDataChange: s1:"+s1);
                        OustStaticVariableHandling.getInstance().setCourseUniqNo(Integer.parseInt(s1));

                        loadUserDataFromFirebase(learningPathId, null);
                    }else{
                        performFinish("Couldn't able to find the course");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onCancelled(DatabaseError DatabaseError) {
                performFinish("Something went wrong!");
            }
        };
        OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(learningMapListener);
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
    }

    private void performFinish(String error){
        OustSdkTools.showToast(error);
        NewCourseLauncherActivity.this.finish();
    }
    public void loadUserDataFromFirebase(final int lpId, final Map<String, Object> landingMap){
        try {
            Log.e(TAG, "inside loadUserDataFromFirebase ");
            userCourseData = getUserCourseData();
            List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedSubmitRequest");
            if (userCourseData != null || (OustSdkTools.checkInternetStatus() && (requests.size() == 0))) {
                if ((landingMap != null) && (landingMap.size() > 0)) {
                    userCourseData = updateUseProgressCoursData(landingMap);
                    if(!courseDataClass.isEnrolled()) {
                        enrolledLp(null, null);
                    }else{
                        addUserData(userCourseData);
                        launchMapModuleActivity();
                    }
                } else {
                    ValueEventListener enventInfoListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot courseData) {
                            try {
                                if (courseData.getValue() != null) {
                                    final Map<String, Object> userDataMap = (Map<String, Object>) courseData.getValue();
                                    userCourseData = updateUseProgressCoursData(userDataMap);
                                    if(!courseDataClass.isEnrolled()) {
                                        enrolledLp(null, null);
                                    }else{
                                        addUserData(userCourseData);
                                        launchMapModuleActivity();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            //gotLpDataFromFirebase(courseDataClass);
                            performFinish("Couldn't able to load course data");
                        }
                    };
                    String msg = "landingPage/" + OustAppState.getInstance().getActiveUser().getStudentKey() + "/course/" + lpId;
                    Log.e(TAG, "firebase link " + msg);

                    OustFirebaseTools.getRootRef().child(msg).addListenerForSingleValueEvent(enventInfoListener);
                    OustFirebaseTools.getRootRef().child(msg).keepSynced(true);
                }
            }else{
                performFinish("Couldn't able to load course data");
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public DTOUserCourseData getUserCourseData() {
        if (userCourseScoreDatabaseHandler == null) {
            userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
        }
        return userCourseScoreDatabaseHandler.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
    }

    public void addUserData(DTOUserCourseData userCourseData) {
        try {
            if (userCourseScoreDatabaseHandler == null) {
                userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
            }
            userCourseScoreDatabaseHandler.addUserScoreToRealm(userCourseData, OustStaticVariableHandling.getInstance().getCourseUniqNo());
        } catch (Exception e) {
            Log.e("exception", e.getMessage());
        }
    }

    public void enrolledLp(String courseColnId, String multilingualID) {
        Log.d(TAG, "enrolledLp: ");
        String studentId = OustAppState.getInstance().getActiveUser().getStudentid();
        final CommonResponse[] commonResponses = new CommonResponse[]{null};
        try {
            String enrolllp_url = OustSdkApplication.getContext().getResources().getString(R.string.enrolllp_url);
            enrolllp_url = enrolllp_url.replace("{courseId}", ("" + learningPathId));
            enrolllp_url = enrolllp_url.replace("{userId}", studentId);
            if (courseColnId != null) {
                enrolllp_url = enrolllp_url.replace("{courseColnId}", courseColnId);
            } else {
                enrolllp_url = enrolllp_url.replace("{courseColnId}", "");
            }
            if (multilingualID != null && !multilingualID.isEmpty()) {
                enrolllp_url = enrolllp_url.replace("{mlCourseId}", multilingualID);
            } else {
                enrolllp_url = enrolllp_url.replace("{mlCourseId}", "");
            }
            try {
                PackageInfo pinfo = OustSdkApplication.getContext().getPackageManager().getPackageInfo(OustSdkApplication.getContext().getPackageName(), 0);
                Log.e("--------", enrolllp_url);
                enrolllp_url = enrolllp_url + "&devicePlatformName=Android";
                Log.e("--------", enrolllp_url);
                enrolllp_url = enrolllp_url + "&appVersion=" + pinfo.versionName;
                Log.e("--------", enrolllp_url);
            } catch (Exception e) {
                Log.e("--------", enrolllp_url);
            }
            enrolllp_url = HttpManager.getAbsoluteUrl(enrolllp_url);

            Log.d(TAG, "enrolledLp: URL-"+enrolllp_url);
            VolleyRequest volleyRequest = new VolleyRequest(OustSdkApplication.getContext(), enrolllp_url, "", new ServerResponseListener() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                    try {
                        commonResponses[0] = OustSdkTools.getCommonResponse(jsonObject.toString());
                        enrollLpDone(commonResponses[0]);
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onSuccess(JSONObject jsonObject, int requestType) {
                    if (jsonObject != null) {
                        if (requestType == 1) {
                            commonResponses[0] = OustSdkTools.getCommonResponse(jsonObject.toString());
                            enrollLpDone(commonResponses[0]);
                        }
                    }
                }

                @Override
                public void onError(String error) {
                    Log.d("Network_error", error);
                    enrollLpDone(commonResponses[0]);
                }
            });
            volleyRequest.executeToServer();
        } catch (Exception e) {
            Log.d(TAG, "", e);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void enrollLpDone(CommonResponse commonResponse) {
        try {
            if ((commonResponse != null)) {
                if (commonResponse.isSuccess()) {
                    if (courseDataClass != null)
                        courseDataClass.setEnrolled(true);

                    userCourseData.setCurrentLevel(1);
                    addUserData(userCourseData);

                    launchMapModuleActivity();
                } else {
                    Log.d(TAG, "enrollLpDone: Failed");
                    performFinish("Course enrollment error!");
                }
            } else {
                performFinish(OustStrings.getString("retry_internet_msg"));
                //OustSdkTools.showToast(OustStrings.getString("retry_internet_msg"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void launchMapModuleActivity(){
        Intent intent = new Intent(NewCourseLauncherActivity.this, LearningMapModuleActivity.class);
        Gson gson = new Gson();
        CourseLevelClass courseLevelClass = courseDataClass.getCourseLevelClassList().get(0);
        String courseLevelStr = gson.toJson(courseLevelClass);
        String courseDataStr = gson.toJson(courseDataClass);
        OustStaticVariableHandling.getInstance().setCourseDataStr(courseDataStr);
        OustStaticVariableHandling.getInstance().setCourseLevelStr(courseLevelStr);
        intent.putExtra("containCertificate", courseDataClass.isCertificate());
        intent.putExtra("learningId", ("" + courseDataClass.getCourseId()));
        intent.putExtra("levelNo", 0);
        intent.putExtra("reviewModeQuestionNo", 0);
        intent.putExtra("isReviewMode", false);
        intent.putExtra("isDisableLevelCompletePopup",true);
        intent.putExtra("isLauncher",courseDataClass.isDisableBackButton());

        /*if ((courseColnId != null) && (!courseColnId.isEmpty())) {
            intent.putExtra("courseColnId", courseColnId);
        }*/

        isLauncherCalled = true;
        OustSdkTools.newActivityAnimationB(intent, NewCourseLauncherActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isLauncherCalled){
            Log.d(TAG, "onResume: Clear data");
            try {
                UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
                DTOUserCourseData userCourseData1 = userCourseScoreDatabaseHandler.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
                userCourseData1.setDownloadCompletePercentage(0);
                userCourseScoreDatabaseHandler.addUserScoreToRealm(userCourseData1, OustStaticVariableHandling.getInstance().getCourseUniqNo());

                List<DTOUserLevelData> mUserLevelDataList = userCourseData1.getUserLevelDataList();
                if (mUserLevelDataList != null && mUserLevelDataList.size() > 0) {
                    for (int i = 0; i < mUserLevelDataList.size(); i++) {
                        mUserLevelDataList.get(i).setCompletePercentage(0);
                        RoomHelper.getAllCourseInLevel(mUserLevelDataList.get(i).getLevelId());
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

            NewCourseLauncherActivity.this.finish();
        }
    }

    public DTOUserCourseData updateUseProgressCoursData(Map<String, Object> learingMap) {
        DTOUserCourseData userCourseData1 = new DTOUserCourseData();
        try {
            if (learingMap != null && learingMap.get("completionPercentage") != null) {
                Object o3 = learingMap.get("completionPercentage");
                if (o3.getClass().equals(Long.class)) {
                    userCourseData1.setPresentageComplete((long) learingMap.get("completionPercentage"));
                } else if (o3.getClass().equals(String.class)) {
                    String s3 = (String) o3;
                    userCourseData1.setPresentageComplete(Long.parseLong(s3));
                } else if (o3.getClass().equals(Double.class)) {
                    Double s3 = (Double) o3;
                    long l = (new Double(s3)).longValue();
                    userCourseData1.setPresentageComplete(l);
                }
            } else {
                userCourseData1.setPresentageComplete(0);
            }
            if (userCourseData1.getPresentageComplete() == 100) {
                //isCourseAlreadyComplete = true;
            }
            if (learingMap != null && learingMap.get("currentCourseLevelId") != null) {
                userCourseData1.setCurrentLevelId(OustSdkTools.convertToLong(learingMap.get("currentCourseLevelId")));
            }
            if (learingMap != null && learingMap.get("currentCourseCardId") != null) {
                userCourseData1.setCurrentCard(OustSdkTools.convertToLong(learingMap.get("currentCourseCardId")));
            }
            boolean isEnrolled = false;
            if (learingMap != null && learingMap.get("enrolled") != null) {
                isEnrolled = ((boolean) learingMap.get("enrolled"));
                courseDataClass.setEnrolled(isEnrolled);
            }
            if (learingMap != null && learingMap.get("userOC") != null) {
                userCourseData1.setTotalOc(OustSdkTools.convertToLong(learingMap.get("userOC")));
            }
            if (learingMap != null && learingMap.get("rating") != null) {
                int rating = (int) (OustSdkTools.convertToLong(learingMap.get("rating")));
                userCourseData1.setMyCourseRating(rating);
            } else {
                userCourseData1.setMyCourseRating(0);
            }
            if (learingMap != null && learingMap.get("mappedAssessment") != null) {
                Map<String, Object> mappedAssessmentMap = (Map<String, Object>) learingMap.get("mappedAssessment");
                if (mappedAssessmentMap.get("passed") != null) {
                    userCourseData1.setMappedAssessmentPassed((boolean) mappedAssessmentMap.get("passed"));
                }
            }

            if (learingMap != null && learingMap.get("contentPlayListId") != null) {
                courseDataClass.setContentPlayListId(OustSdkTools.convertToLong(learingMap.get("contentPlayListId")));
            }
            if (learingMap != null && learingMap.get("contentPlayListSlotId") != null) {
                courseDataClass.setContentPlayListSlotId(OustSdkTools.convertToLong(learingMap.get("contentPlayListSlotId")));
            }
            if (learingMap != null && learingMap.get("contentPlayListSlotItemId") != null) {
                courseDataClass.setContentPlayListSlotItemId(OustSdkTools.convertToLong(learingMap.get("contentPlayListSlotItemId")));
            }
            if (learingMap != null && learingMap.get("levels") != null) {
                Object o1 = learingMap.get("levels");
                if (o1.getClass().equals(ArrayList.class)) {
                    List<Object> objectList = (List<Object>) o1;
                    if (objectList != null) {
                        List<DTOUserLevelData> userLevelDataList = userCourseData1.getUserLevelDataList();
                        for (int k = 0; k < objectList.size(); k++) {
                            if (objectList.get(k) != null) {
                                final Map<String, Object> levelMap = (Map<String, Object>) objectList.get(k);
                                if (userLevelDataList == null) {
                                    userLevelDataList = new ArrayList<>();
                                }
                                int courseLevelNo = -1;
                                for (int l = 0; l < userLevelDataList.size(); l++) {
                                    if (userLevelDataList.get(l).getLevelId() == k) {
                                        courseLevelNo = l;
                                    }
                                }
                                for (int i = 0; i < courseDataClass.getCourseLevelClassList().size(); i++) {
                                    if (courseDataClass.getCourseLevelClassList().get(i).getLevelId() == k) {
                                        if (courseLevelNo < 0) {
                                            userLevelDataList.add(new DTOUserLevelData());
                                            courseLevelNo = (userLevelDataList.size() - 1);
                                        }
                                        userLevelDataList.get(courseLevelNo).setSequece(courseDataClass.getCourseLevelClassList().get(i).getSequence());
                                        userLevelDataList.get(courseLevelNo).setLevelId(k);
                                        if (levelMap.get("userLevelOC") != null) {
                                            userLevelDataList.get(courseLevelNo).setTotalOc(OustSdkTools.convertToLong(levelMap.get("userLevelOC")));
                                        }
                                        if (levelMap.get("userLevelXp") != null) {
                                            userLevelDataList.get(courseLevelNo).setXp(OustSdkTools.convertToLong(levelMap.get("userLevelXp")));
                                        }
                                        if (levelMap.get("locked") != null) {
                                            userLevelDataList.get(courseLevelNo).setLocked((boolean) levelMap.get("locked"));
                                        } else {
                                            userLevelDataList.get(courseLevelNo).setLocked(true);
                                        }
                                        Object o2 = levelMap.get("cards");
                                        if (o2 != null) {
                                            if (o2.getClass().equals(ArrayList.class)) {
                                                List<Object> objectCardList = (List<Object>) o2;
                                                if (objectCardList != null) {
                                                    List<DTOUserCardData> userCardDataList = new ArrayList<>();
                                                    for (int j = 0; j < objectCardList.size(); j++) {
                                                        if (objectCardList.get(j) != null) {
                                                            final Map<String, Object> cardMap = (Map<String, Object>) objectCardList.get(j);
                                                            DTOUserCardData userCardData = new DTOUserCardData();
                                                            if (cardMap.get("userCardAttempt") != null) {
                                                                userCardData.setNoofAttempt(OustSdkTools.convertToLong(cardMap.get("userCardAttempt")));
                                                            }else{
                                                                userCardData.setNoofAttempt(0);
                                                            }

                                                            if (cardMap.get("cardCompleted") != null) {
                                                                userCardData.setCardCompleted((boolean)cardMap.get("cardCompleted"));
                                                            }else{
                                                                userCardData.setCardCompleted(false);
                                                            }

                                                            if (cardMap.get("cardViewInterval") != null) {
                                                                userCardData.setCardViewInterval(OustSdkTools.convertToLong(cardMap.get("cardViewInterval")));
                                                            }else{
                                                                userCardData.setCardViewInterval(0);
                                                            }

                                                            if (userCardData.isCardCompleted()) {
                                                                if (cardMap.get("userCardScore") != null) {
                                                                    //userCardData.setOc((long) cardMap.get("userCardScore"));
                                                                }
                                                                userCardData.setCardId(j);
                                                                userCardDataList.add(userCardData);
                                                            }
                                                        }
                                                    }
                                                    userLevelDataList.get(courseLevelNo).setUserCardDataList(userCardDataList);
                                                }
                                            } else if (o2.getClass().equals(HashMap.class)) {
                                                final Map<String, Object> objectCardMap = (Map<String, Object>) o2;
                                                List<DTOUserCardData> userCardDataList = new ArrayList<>();
                                                for (String cardKey : objectCardMap.keySet()) {
                                                    final Map<String, Object> cardMap = (Map<String, Object>) objectCardMap.get(cardKey);
                                                    DTOUserCardData userCardData = new DTOUserCardData();
                                                    if (cardMap.get("userCardAttempt") != null) {
                                                        userCardData.setNoofAttempt(OustSdkTools.convertToLong(cardMap.get("userCardAttempt")));
                                                    }else{
                                                        userCardData.setNoofAttempt(0);
                                                    }
                                                    if (cardMap.get("cardCompleted") != null) {
                                                        userCardData.setCardCompleted((boolean)cardMap.get("cardCompleted"));
                                                    }else{
                                                        userCardData.setCardCompleted(false);
                                                    }

                                                    if (cardMap.get("cardViewInterval") != null) {
                                                        userCardData.setCardViewInterval(OustSdkTools.convertToLong(cardMap.get("cardViewInterval")));
                                                    }else{
                                                        userCardData.setCardViewInterval(0);
                                                    }

                                                    if (userCardData.isCardCompleted()) {
                                                    //if (userCardData.getNoofAttempt() > 0) {
                                                        if (cardMap.get("userCardScore") != null) {
                                                            // userCardData.setOc((long) cardMap.get("userCardScore"));
                                                        }
                                                        userCardData.setCardId(Integer.parseInt(cardKey));
                                                        userCardDataList.add(userCardData);
                                                    }
                                                }
                                                userLevelDataList.get(courseLevelNo).setUserCardDataList(userCardDataList);
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                        userCourseData1.setUserLevelDataList(userLevelDataList);
                    }
                } else if (o1.getClass().equals(HashMap.class)) {
                    final Map<String, Object> objectMap = (Map<String, Object>) o1;
                    if (objectMap != null) {
                        List<DTOUserLevelData> userLevelDataList = userCourseData1.getUserLevelDataList();

                        for (String levelKey : objectMap.keySet()) {
                            final Map<String, Object> levelMap = (Map<String, Object>) objectMap.get(levelKey);
                            if (userLevelDataList == null) {
                                userLevelDataList = new ArrayList<>();
                            }
                            int courseLevelNo = -1;
                            int k = Integer.parseInt(levelKey);
                            for (int l = 0; l < userLevelDataList.size(); l++) {
                                if (userLevelDataList.get(l).getLevelId() == k) {
                                    courseLevelNo = l;
                                }
                            }
                            for (int i = 0; i < courseDataClass.getCourseLevelClassList().size(); i++) {
                                if (courseDataClass.getCourseLevelClassList().get(i).getLevelId() == k) {
                                    if (courseLevelNo < 0) {
                                        userLevelDataList.add(new DTOUserLevelData());
                                        courseLevelNo = (userLevelDataList.size() - 1);
                                    }
                                    userLevelDataList.get(courseLevelNo).setSequece(courseDataClass.getCourseLevelClassList().get(i).getSequence());
                                    userLevelDataList.get(courseLevelNo).setLevelId(k);
                                    if (levelMap.get("userLevelOC") != null) {
                                        userLevelDataList.get(courseLevelNo).setTotalOc(OustSdkTools.convertToLong(levelMap.get("userLevelOC")));
                                    }
                                    if (levelMap.get("userLevelXp") != null) {
                                        userLevelDataList.get(courseLevelNo).setXp(OustSdkTools.convertToLong(levelMap.get("userLevelXp")));
                                    }
                                    if (levelMap.get("locked") != null) {
                                        userLevelDataList.get(courseLevelNo).setLocked((boolean) levelMap.get("locked"));
                                    } else {
                                        userLevelDataList.get(courseLevelNo).setLocked(true);
                                    }
                                    Object o2 = levelMap.get("cards");
                                    if (o2 != null) {
                                        if (o2.getClass().equals(ArrayList.class)) {
                                            List<Object> objectCardList = (List<Object>) o2;
                                            List<DTOUserCardData> userCardDataList = new ArrayList<>();
                                            if (objectCardList != null) {
                                                for (int j = 0; j < objectCardList.size(); j++) {
                                                    if (objectCardList.get(j) != null) {
                                                        final Map<String, Object> cardMap = (Map<String, Object>) objectCardList.get(j);
                                                        DTOUserCardData userCardData = new DTOUserCardData();
                                                        if (cardMap.get("userCardAttempt") != null) {
                                                            userCardData.setNoofAttempt(OustSdkTools.convertToLong(cardMap.get("userCardAttempt")));
                                                        }else{
                                                            userCardData.setNoofAttempt(0);
                                                        }

                                                        if (cardMap.get("cardCompleted") != null) {
                                                            userCardData.setCardCompleted((boolean)cardMap.get("cardCompleted"));
                                                        }else{
                                                            userCardData.setCardCompleted(false);
                                                        }

                                                        if (cardMap.get("cardViewInterval") != null) {
                                                            userCardData.setCardViewInterval(OustSdkTools.convertToLong(cardMap.get("cardViewInterval")));
                                                        }else{
                                                            userCardData.setCardViewInterval(0);
                                                        }

                                                        if (userCardData.isCardCompleted()) {
                                                        //if (userCardData.getNoofAttempt() > 0) {
                                                            if (cardMap.get("userCardScore") != null) {
                                                                //userCardData.setOc((long) cardMap.get("userCardScore"));
                                                            }
                                                            userCardData.setCardId(j);
                                                            userCardDataList.add(userCardData);
                                                        }
                                                    }
                                                }
                                            }
                                            userLevelDataList.get(courseLevelNo).setUserCardDataList(userCardDataList);
                                        } else if (o2.getClass().equals(HashMap.class)) {
                                            List<DTOUserCardData> userCardDataList = new ArrayList<>();
                                            final Map<String, Object> objectCardMap = (Map<String, Object>) o2;
                                            for (String cardKey : objectCardMap.keySet()) {
                                                final Map<String, Object> cardMap = (Map<String, Object>) objectCardMap.get(cardKey);
                                                DTOUserCardData userCardData = new DTOUserCardData();
                                                if (cardMap.get("userCardAttempt") != null) {
                                                    userCardData.setNoofAttempt(OustSdkTools.convertToLong(cardMap.get("userCardAttempt")));
                                                }else{
                                                    userCardData.setNoofAttempt(0);
                                                }

                                                if (cardMap.get("cardCompleted") != null) {
                                                    userCardData.setCardCompleted((boolean)cardMap.get("cardCompleted"));
                                                }else{
                                                    userCardData.setCardCompleted(false);
                                                }

                                                if (cardMap.get("cardViewInterval") != null) {
                                                    userCardData.setCardViewInterval(OustSdkTools.convertToLong(cardMap.get("cardViewInterval")));
                                                }else{
                                                    userCardData.setCardViewInterval(0);
                                                }

                                                if (userCardData.isCardCompleted()) {
                                                //if (userCardData.getNoofAttempt() > 0) {
                                                    if (cardMap.get("userCardScore") != null) {
                                                        //userCardData.setOc((long) cardMap.get("userCardScore"));
                                                    }
                                                    userCardData.setCardId(Integer.parseInt(cardKey));
                                                    userCardDataList.add(userCardData);
                                                }
                                            }
                                            userLevelDataList.get(courseLevelNo).setUserCardDataList(userCardDataList);
                                        }
                                    }
                                }
                            }
                        }
                        Collections.sort(userLevelDataList, CommonTools.courseUserCardSorter);
                        userCourseData1.setUserLevelDataList(userLevelDataList);
                    }
                }

            }
            /*if (!isEnrolled) {
                if (courseDesclaimerData != null) {
                    setDesclaimerPopup(courseDesclaimerData);
                } else if (cardInfo != null) {
                    checkForDescriptionCardData(cardInfo);
                }
            }*/

            if (courseDataClass != null) {
                List<DTOUserLevelData> levelDataList = new ArrayList<>();
                if (userCourseData1.getUserLevelDataList() == null) {
                    userCourseData1.setUserLevelDataList(new ArrayList<DTOUserLevelData>());
                }

                for (int k = 0; k < userCourseData1.getUserLevelDataList().size(); k++) {
                    boolean containLevel = false;
                    for (int l = 0; l < courseDataClass.getCourseLevelClassList().size(); l++) {
                        if (userCourseData1.getUserLevelDataList().get(k).getLevelId() == courseDataClass.getCourseLevelClassList().get(l).getLevelId()) {
                            containLevel = true;
                        }
                    }
                    if (containLevel) {
                        boolean alreadyIn = false;
                        for (int n = 0; n < levelDataList.size(); n++) {
                            if (userCourseData1.getUserLevelDataList().get(k).getLevelId() == levelDataList.get(n).getLevelId()) {
                                alreadyIn = true;
                            }
                        }
                        if (!alreadyIn) {
                            levelDataList.add(userCourseData1.getUserLevelDataList().get(k));
                        }
                    }
                }

                userCourseData1.setUserLevelDataList(levelDataList);
                DTOUserLevelData userLevelData = new DTOUserLevelData();
                for (int l = 0; l < courseDataClass.getCourseLevelClassList().size(); l++) {
                    boolean alreadyIn = false;
                    for (int k = 0; k < userCourseData1.getUserLevelDataList().size(); k++) {
                        if (userCourseData1.getUserLevelDataList().get(k).getLevelId() == courseDataClass.getCourseLevelClassList().get(l).getLevelId()) {
                            alreadyIn = true;
                        }
                    }
                    if (!alreadyIn) {
                        userLevelData = new DTOUserLevelData();
                        userLevelData.setLevelId(courseDataClass.getCourseLevelClassList().get(l).getLevelId());
                        userLevelData.setSequece(courseDataClass.getCourseLevelClassList().get(l).getSequence());
                        userCourseData1.getUserLevelDataList().add(userLevelData);
                    }
                }
            }
            Collections.sort(userCourseData1.getUserLevelDataList(), CommonTools.courseUserCardSorter);

            int currentLevel = 0;
            for (int i = 0; i < courseDataClass.getCourseLevelClassList().size(); i++) {
                if ((courseDataClass.getCourseLevelClassList() != null) && (courseDataClass.getCourseLevelClassList().get(i) != null)
                        && (userCourseData1.getUserLevelDataList() != null) && (userCourseData1.getUserLevelDataList().get(i) != null)) {
                    if (!userCourseData1.getUserLevelDataList().get(i).isLocked() && !courseDataClass.getCourseLevelClassList().get(i).isLevelLock()) {
                        currentLevel++;
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }
            userCourseData1.setCurrentLevel(currentLevel);
            if (userCourseData1.getCurrentLevel() > (courseDataClass.getCourseLevelClassList().size() + 1)) {
                userCourseData1.setCurrentLevel((courseDataClass.getCourseLevelClassList().size() + 1));
            }
            if (userCourseData1.getPresentageComplete() == 100) {
                userCourseData1.setCourseComplete(true);
                userCourseData1.setCurrentLevel(courseDataClass.getCourseLevelClassList().size() + 1);
                if (courseDataClass.getMappedAssessmentId() > 0) {
                    if (userCourseData1.isMappedAssessmentPassed()) {
                        userCourseData1.setCurrentLevel(courseDataClass.getCourseLevelClassList().size() + 2);
                    }
                }
//                if(userCourseData1.getCurrentCompleteLevel()==0) {
//                    shouldSetCardNo=true;
//                    userCourseData1.setCurrentCompleteLevel((int) userCourseData1.getCurrentLevel());
//                }
            } else {
                userCourseData1.setCourseComplete(false);
            }
            if (!isEnrolled) {
                userCourseData1.setCurrentLevel(0);
            }
            if (userCourseData1.getUserLevelDataList() != null) {
                if (userCourseData1.getCurrentLevel() > 0) {
                    for (int l = 0; l < courseDataClass.getCourseLevelClassList().get((int) userCourseData1.getCurrentLevel() - 1).getCourseCardClassList().size(); l++) {
                        if (courseDataClass.getCourseLevelClassList().get((int) userCourseData1.getCurrentLevel() - 1).getCourseCardClassList().get(l).getCardId() == userCourseData1.getCurrentCard()) {
                            userCourseData1.getUserLevelDataList().get((int) userCourseData1.getCurrentLevel() - 1).setCurrentCardNo(l);
//                            if((userCourseData1.getPresentageComplete()==100)&&(shouldSetCardNo)){
//                                userCourseData1.getUserLevelDataList().get((int) userCourseData1.getCurrentCompleteLevel()-1).setCurrentCardNo(l);
//                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        Log.d(TAG, "updateUseProgressCoursData: "+userCourseData1.getUserLevelDataList().size());
        return userCourseData1;
        //addUserData(userCourseData1);
        //gotLpDataFromFirebase(courseDataClass);
    }
}
