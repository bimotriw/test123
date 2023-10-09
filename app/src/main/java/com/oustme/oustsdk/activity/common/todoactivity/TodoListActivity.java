package com.oustme.oustsdk.activity.common.todoactivity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.FFContest.FFcontestStartActivity;
import com.oustme.oustsdk.activity.assessments.AssessmentPlayActivity;
import com.oustme.oustsdk.activity.assessments.AssessmentQuestionsActivity;
import com.oustme.oustsdk.activity.common.todoactivity.presenter.TodoPresenter;
import com.oustme.oustsdk.activity.common.todoactivity.view.TodoView;
import com.oustme.oustsdk.activity.courses.CourseMultiLingualActivity;
import com.oustme.oustsdk.activity.courses.LessonsActivity;
import com.oustme.oustsdk.activity.courses.newlearnngmap.NewLearningMapActivity;
import com.oustme.oustsdk.adapter.common.ToDoExpandListAdapter;
import com.oustme.oustsdk.assessment_ui.assessmentDetail.AssessmentDetailScreen;
import com.oustme.oustsdk.base.BaseActivity;
import com.oustme.oustsdk.customviews.CustomSearchView;
import com.oustme.oustsdk.firebase.FFContest.ContestNotificationMessage;
import com.oustme.oustsdk.firebase.FFContest.FastestFingerContestData;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.MultilingualCourse;
import com.oustme.oustsdk.model.response.common.ToDoChildModel;
import com.oustme.oustsdk.model.response.common.ToDoHeaderModel;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.ASSESSMENT_PATH;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.COURSE_PATH;

public class TodoListActivity extends BaseActivity implements SearchView.OnQueryTextListener, TodoView {

    private static final String TAG = "TodoListActivity";

    private ToDoExpandListAdapter mExpandableListAdapter;
    private ExpandableListView mExpandableListView;
    private ArrayList<ToDoHeaderModel> mToDoHeaderList;
    private HashMap<String, List<ToDoChildModel>> mToDoChildList;
    private boolean isTodo, isContest, isPlayList;
    private HashMap<String, String> myDeskMap;
    private List<ToDoChildModel> childModelList;

    //actionbar
    private ActionBar mActionBar;

    //for searching item
    private MenuItem actionSearch;
    private View searchPlate;
    public CustomSearchView newSearchView;
    private String searchText;
    private FastestFingerContestData fastestFingerContestData;
    private String ffcBannerURL;
    public int mPendingCount = 0;
    private List<CommonLandingData> mAssessmentList;
    private List<CommonLandingData> mCoursesList;

    private HashMap<String, CommonLandingData> mCoursePendingList;
    private HashMap<String, CommonLandingData> mAssessmentPendingList;

    private FirebaseRefClass ffcDataRefClass;
    private FirebaseRefClass ffcQDataRefClass;
    private ActiveUser activeUser;
    private TodoPresenter mTodoPresenter;


    int i = 0;
    int courseCount = 0;
    int assessmentCount = 0;
    int collectionCont = 0;
    int courseCompleted = 0;
    int coursePending = 0;

    int assessmentPending = 0;
    int assessmentCompleted = 0;

    @Override
    protected void onResume() {
        super.onResume();
        mTodoPresenter.getUserFFContest(activeUser.getStudentKey());
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_todo_list;
    }

    @Override
    protected void initView() {
        mExpandableListView = findViewById(R.id.lvExp);
        mTodoPresenter = new TodoPresenter(TodoListActivity.this);
    }

    @Override
    protected void initData() {

        String activeUserGet = OustPreferences.get("userdata");
        activeUser = OustSdkTools.getActiveUserData(activeUserGet);

        Bundle bundle = getIntent().getExtras();
        mToDoHeaderList = new ArrayList<>();
        mToDoHeaderList = bundle.getParcelableArrayList(AppConstants.StringConstants.EXP_HEADER_LIST);
        isTodo = bundle.getBoolean(AppConstants.StringConstants.SHOW_TO_DO_ON_LANDING_PAGE);
        isContest = bundle.getBoolean(AppConstants.StringConstants.SHOW_FFC_ON_LANDING_PAGE);
        isPlayList = bundle.getBoolean(AppConstants.StringConstants.SHOW_CPL_ON_LANDING_PAGE);
        mToDoChildList = (HashMap<String, List<ToDoChildModel>>) getIntent().getSerializableExtra(AppConstants.StringConstants.EXP_CHILD_LIST);
        myDeskMap = (HashMap<String, String>) getIntent().getSerializableExtra("deskDataMap");

        if (myDeskMap != null) {
            //getDataOfCourseAssessment();
        }

        Intent callingIntent = getIntent();
        String str = callingIntent.getStringExtra(AppConstants.StringConstants.FFC_DATA);
        Gson gson = new Gson();
        fastestFingerContestData = gson.fromJson(str, FastestFingerContestData.class);
        if (fastestFingerContestData != null)
            if (fastestFingerContestData.getStartTime() > System.currentTimeMillis() && isContest) {
                mPendingCount++;
            }
        if (isPlayList) {
            mPendingCount++;
        }
        if (mToDoHeaderList != null)
            setActionBarTitle();
            mTodoPresenter.getUserFFContest(activeUser.getStudentKey());
        if (mToDoChildList != null) {
            mExpandableListAdapter = new ToDoExpandListAdapter(this, mToDoHeaderList, mToDoChildList);
            mExpandableListView.setAdapter(mExpandableListAdapter);
        }

    }

    @Override
    protected void initListener() {

    }

    @Override
    public void updateFFCData() {
        try {
            for (int i = 0; i < mToDoHeaderList.size(); i++) {
                if (mToDoHeaderList.get(i).getTitle().contains("Contest")) {
                    if (OustStaticVariableHandling.getInstance().isContestLive()) {
                        String contestnotification_message = OustPreferences.get("contestnotification_message");
                        if ((fastestFingerContestData.isEnrolled())) {
                            //cpl_main_ll.setVisibility(View.GONE);
                            if ((fastestFingerContestData.getPlayBanner() != null) && (!fastestFingerContestData.getPlayBanner().isEmpty())) {
                                mToDoHeaderList.get(i).setmUrl(fastestFingerContestData.getPlayBanner());
                                ffcBannerURL = fastestFingerContestData.getPlayBanner();

                            } else {
                                if ((fastestFingerContestData.getJoinBanner() != null) && (!fastestFingerContestData.getJoinBanner().isEmpty())) {
                                    mToDoHeaderList.get(i).setmUrl(fastestFingerContestData.getJoinBanner());
                                    ffcBannerURL = fastestFingerContestData.getJoinBanner();
                                }
                            }
                            if ((contestnotification_message != null) && (!contestnotification_message.isEmpty())) {
                                Gson gson = new Gson();
                                ContestNotificationMessage contestNotificationMessage = gson.fromJson(contestnotification_message, ContestNotificationMessage.class);
                                if (contestNotificationMessage != null) {
                                    long currentTime = System.currentTimeMillis();
                                    if (((contestNotificationMessage.getTotalContestTime() * 1000) + fastestFingerContestData.getStartTime()) < currentTime) {
                                        if ((fastestFingerContestData.getRrBanner() != null) && (!fastestFingerContestData.getRrBanner().isEmpty())) {
                                            mToDoHeaderList.get(i).setmUrl(fastestFingerContestData.getRrBanner());

                                            ffcBannerURL = fastestFingerContestData.getRrBanner();
                                        }
                                    }
                                }
                            }
                        } else {
                            if ((fastestFingerContestData.getJoinBanner() != null) && (!fastestFingerContestData.getJoinBanner().isEmpty())) {
                                mToDoHeaderList.get(i).setmUrl(fastestFingerContestData.getJoinBanner());
                            }
                            if ((contestnotification_message != null) && (!contestnotification_message.isEmpty())) {
                                Gson gson = new Gson();
                                ContestNotificationMessage contestNotificationMessage = gson.fromJson(contestnotification_message, ContestNotificationMessage.class);
                                if (contestNotificationMessage != null) {
                                    long currentTime = System.currentTimeMillis();
                                    if (((contestNotificationMessage.getTotalContestTime() * 1000) + fastestFingerContestData.getStartTime()) < currentTime) {
                                        if ((fastestFingerContestData.getRrBanner() != null) && (!fastestFingerContestData.getRrBanner().isEmpty())) {
                                            mToDoHeaderList.get(i).setmUrl(fastestFingerContestData.getRrBanner());
                                            ffcBannerURL = fastestFingerContestData.getRrBanner();
                                        }
                                    }
                                }
                            }
                        }
                    }
                  /*  ToDoHeaderModel contestHeader = new ToDoHeaderModel();
                    contestHeader.setType(0);
                    contestHeader.setHasBanner(true);
                    contestHeader.setTitle("Contest");
                    contestHeader.setmUrl(ffcBannerURL);
                    ToDoChildModel contestChild = new ToDoChildModel();
                    contestChild.setFfcUsersCount(""+fastestFingerContestData.getUserCount());
                    contestChild.setFfcEnrolledCount(fastestFingerContestData.getEnrolledCount()+"");
                    mToDoHeaderList.add(i,contestHeader);
                  */
                    mExpandableListAdapter.notifyDataSetChanged();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    /*private void removeFFCDataListener() {
        try {
            if (ffcDataRefClass != null) {
                OustFirebaseTools.getRootRef().child(ffcDataRefClass.getFirebasePath()).removeEventListener(ffcDataRefClass.getEventListener());
            }
            if (ffcQDataRefClass != null) {
                OustFirebaseTools.getRootRef().child(ffcQDataRefClass.getFirebasePath()).removeEventListener(ffcQDataRefClass.getEventListener());
            }
        } catch (Exception e) {
        }
    }*/

    /*public void fetctFFCData(String ffcId) {
        try {
            final String message = "/f3cData/f3c" + ffcId;
            Log.d(TAG, "fetctFFCData: " + message);
            ValueEventListener ffcDataListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    extractFFCData(dataSnapshot);
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
    }*/

   /* private void getFFCEnrolldedUsersCount(String contestId) {
        final String path = "/f3cEnrolledUserCount/f3c" + contestId + "/participants";
        DatabaseReference databaseReference = OustFirebaseTools.getRootRef().child(path);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    try {
                        fastestFingerContestData.setEnrolledCount((long) dataSnapshot.getValue());
                        updateFFCData();
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
    }*/

    @Override
    public void extractFFCData(DataSnapshot dataSnapshot) {
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
    }

    @Override
    public void updateFFCEnrolledCount(long count) {
        fastestFingerContestData.setEnrolledCount(count);
        updateFFCData();
    }

    @Override
    public void updateUserFFCUserContest(DataSnapshot dataSnapshot) {
        try {
            if (dataSnapshot.getValue() != null)
            {
                if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_FFC_ON_LANDING_PAGE)) {
                    isContest = true;
                }
                Object o1 = dataSnapshot.getValue();
                if (o1.getClass().equals(HashMap.class)) {
                    Map<String, Object> ffcMap = (Map<String, Object>) dataSnapshot.getValue();
                    if (ffcMap.get("elementId") != null) {
                        long contestId = (long) ffcMap.get("elementId");
                        //
                        if (fastestFingerContestData == null) {
                            fastestFingerContestData = new FastestFingerContestData();
                        }
                        mTodoPresenter.fetctFFCData(("" + fastestFingerContestData.getFfcId()));
                        int lastContestId = OustPreferences.getSavedInt("lastContestTime");
                        if ((lastContestId > 0) && (lastContestId != contestId)) {
                            OustPreferences.clear("contestScore");
                        }
                        OustPreferences.saveintVar("lastContestTime", (int) contestId);
                        if (fastestFingerContestData.getFfcId() != contestId) {
                            fastestFingerContestData = new FastestFingerContestData();
                            fastestFingerContestData.setFfcId(contestId);
                            OustStaticVariableHandling.getInstance().setContestOver(false);
                            mTodoPresenter.removeFFCDataListener();
                            mTodoPresenter.fetctFFCData(("" + fastestFingerContestData.getFfcId()));
                            //fetchQData(("" + fastestFingerContestData.getFfcId()));
                        }
                        if (ffcMap.get("enrolled") != null) {
                            fastestFingerContestData.setEnrolled((boolean) ffcMap.get("enrolled"));
                            //ffcBannerStatus();
                        }

                    }
                }
            } else {
                isContest = false;
                OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_FFC_ON_LANDING_PAGE, false);

            }
        } catch (Exception e) {
        }
    }

    private void getDataOfCourseAssessment() {
        mAssessmentList = new ArrayList<>();
        mCoursesList = new ArrayList<>();

        mAssessmentPendingList = new HashMap<>();
        mCoursePendingList = new HashMap<>();

        for (String name : myDeskMap.keySet()) {
            String mainPath = "landingPage/" + activeUser.getStudentKey() + "/";
            String key = name;
            String value = myDeskMap.get(name);
            Log.d(TAG, "getDataOfCourseAssessment: key:" + key);
            Log.d(TAG, "type: " + value);
            if (value.contains("ASSESSMENT")) {
                assessmentCount++;
                final String path = key.replace("ASSESSMENT", "");
                String compPath = mainPath + "assessment" + path;
                OustFirebaseTools.getRootRef().child(mainPath + "assessment/" + path).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            CommonLandingData data = new CommonLandingData();
                            //dataSnapshot.getValue(CommonLandingData.class);
                            // data.setId(dataSnapshot.child("elementId").getValue()+"");
                            //data.setType("ASSESSMENT");
                            //data.setType((String)dataSnapshot.child("type").getValue().toString());
                            //data.setEnrollCount((long) dataSnapshot.child("participants").getValue());
                            //data.setBanner((String) dataSnapshot.child("banner").getValue().toString());
                            //data.setDescription((String) dataSnapshot.child("description").getValue().toString());
                            //data.setOc((long) dataSnapshot.child("rewardOC").getValue());
                            if (dataSnapshot.child("completionDate").getValue() != null) {
                                // Log.d(TAG, "onDataChange: completi:" + dataSnapshot.child("completionDate").getValue());
                                assessmentCompleted++;

                            } else {
                                CommonLandingData data1 = new CommonLandingData();
                                mAssessmentPendingList.put(path, data1);
                                assessmentPending++;
                                getAssessmentPendingDetails(path);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else if (value.contains("COURSE")) {
                courseCount++;
                final String path = key.replace("COURSE", "");
                OustFirebaseTools.getRootRef().child(mainPath + "course/" + path).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            CommonLandingData data = new CommonLandingData();
                            //data.setName((String) dataSnapshot.child("courseName").getValue().toString());
                            //data.setCourseTags((String) dataSnapshot.child("courseTags").getValue().toString());
                            //data.setTime((long) dataSnapshot.child("courseTime").getValue());
                            //data.setType((String) dataSnapshot.child("courseType").getValue().toString());
                            data.setType("COURSE");
                            data.setCourseType("COURSE");
                            //data.setCompletionPercentage(100);
                            // data.setCplId((long) dataSnapshot.child("cplId").getValue());
                            // data.setId(dataSnapshot.child("elementId").getValue()+"");
                            //data.setOc((long) dataSnapshot.child("totalOc").getValue());
                            //data.setBanner((String) dataSnapshot.child("bgImg").getValue().toString());

                            if (dataSnapshot.child("completionPercentage").getValue() != null) {
                                if (dataSnapshot.child("completionPercentage").getValue().toString().contains("100")) {
                                    courseCompleted++;
                                } else {
                                    mCoursesList.add(data);
                                    coursePending++;
                                    CommonLandingData data1 = new CommonLandingData();
                                    double doi = Double.parseDouble(dataSnapshot.child("completionPercentage").getValue().toString());
                                    data1.setCompletionPercentage((long) doi);
                                    mCoursePendingList.put(path, data1);
                                    getCoursePendingDetails(path);
                                }
                            } else {
                                CommonLandingData data1 = new CommonLandingData();
                                data1.setCompletionPercentage(0);
                                mCoursePendingList.put(path, data1);
                                coursePending++;
                                getCoursePendingDetails(path);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                        // Log.d(TAG, "onDataChange: course pending:"+coursePending+" course completed:"+courseCompleted);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else if (value.contains("COLLECTION")) {
                collectionCont++;
                final String path = key.replace("COLLECTION", "");
               /* OustFirebaseTools.getRootRef().child(mainPath + "courseColn/" + path).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: collections:"+dataSnapshot.child("courses").getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });*/
            }

        }


    }

    List<ToDoChildModel> mToDoChildAssessmentList = new ArrayList<>();
    List<ToDoChildModel> mToDoChildCourseList = new ArrayList<>();

    private void getAssessmentPendingDetails(final String path) {
        OustFirebaseTools.getRootRef().child(ASSESSMENT_PATH + path).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CommonLandingData data = mAssessmentPendingList.get(path);
                if (dataSnapshot.child("rewardOC").getValue() != null) {
                    data.setOc((long) dataSnapshot.child("rewardOC").getValue());
                }
                if (dataSnapshot.child("name").getValue() != null) {
                    data.setName((String) dataSnapshot.child("name").getValue());
                }

                if (dataSnapshot.child("icon").getValue() != null) {
                    data.setIcon((String) dataSnapshot.child("icon").getValue());
                }

                if (dataSnapshot.child("banner").getValue() != null) {
                    data.setBanner((String) dataSnapshot.child("banner").getValue());
                }

                if (dataSnapshot.child("assessmentId").getValue() != null) {
                    data.setId((long) dataSnapshot.child("assessmentId").getValue() + "");
                }
                data.setType("ASSESSMENT");

                if (dataSnapshot.child("participants").getValue() != null) {
                    data.setEnrollCount((long) dataSnapshot.child("participants").getValue());
                }
                mAssessmentPendingList.put(path, data);
                List<CommonLandingData> as = new ArrayList<>(mAssessmentPendingList.values());

                mToDoChildAssessmentList = new ArrayList<>();
                for (int i = 0; i < as.size(); i++) {
                    ToDoChildModel toDoChildModel = new ToDoChildModel();
                    toDoChildModel.setCommonLandingDataAssessment(as.get(i));
                    if (as.get(i).getIcon() != null)
                        //Log.d(TAG, "onDataChange: icon: "+toDoChildModel.getCommonLandingDataAssessment().getIcon());
                        mToDoChildAssessmentList.add(toDoChildModel);
                }
                for (int j = 0; j < mToDoHeaderList.size(); j++) {
                    if (mToDoHeaderList.get(j).getTitle().contains("Assessments")) {
                        mToDoChildList.put(mToDoHeaderList.get(j).getTitle(), mToDoChildAssessmentList);
                        mExpandableListAdapter.notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getCoursePendingDetails(final String path) {
        OustFirebaseTools.getRootRef().child(COURSE_PATH + path).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CommonLandingData data = mCoursePendingList.get(path);
                if (dataSnapshot.child("courseName").getValue() != null)
                    data.setName(dataSnapshot.child("courseName").getValue().toString());

                if (dataSnapshot.child("courseId").getValue() != null) {
                    data.setId((long) dataSnapshot.child("courseId").getValue() + "");
                }
                if (dataSnapshot.child("contentDuration").getValue() != null) {
                    data.setTime((long) dataSnapshot.child("contentDuration").getValue());
                }

                if (dataSnapshot.child("icon").getValue() != null) {
                    data.setIcon((String) dataSnapshot.child("icon").getValue());
                }

                if (dataSnapshot.child("bgImg").getValue() != null) {
                    data.setBanner(dataSnapshot.child("bgImg").getValue().toString());
                }
                if (dataSnapshot.child("totalOc").getValue() != null) {
                    data.setOc((long) dataSnapshot.child("totalOc").getValue());
                }
                if (dataSnapshot.child("cplId").getValue() != null) {
                    data.setCplId((long) dataSnapshot.child("cplId").getValue());
                }
                if (dataSnapshot.child("courseTags").getValue() != null)
                    data.setCourseTags((String) dataSnapshot.child("courseTags").getValue());

                if (dataSnapshot.child("contentDuration").getValue() != null)
                    data.setTime((long) dataSnapshot.child("contentDuration").getValue());

                data.setType("COURSE");
                if (dataSnapshot.child("courseType").getValue() != null)
                    data.setCourseType((String) dataSnapshot.child("courseType").getValue());

                if (dataSnapshot.child("description").getValue() != null) {
                    data.setDescription((String) dataSnapshot.child("description").getValue());
                }

                mCoursePendingList.put(path, data);
                Log.d(TAG, "onDataChange: course name:" + data.getName());

                List<CommonLandingData> as = new ArrayList<>(mCoursePendingList.values());

                mToDoChildCourseList = new ArrayList<>();
                for (int i = 0; i < as.size(); i++) {
                    ToDoChildModel toDoChildModel = new ToDoChildModel();
                    toDoChildModel.setCommonLandingDataCourse(as.get(i));
                    if (as.get(i).getIcon() != null)
                        mToDoChildCourseList.add(toDoChildModel);
                }
                for (int j = 0; j < mToDoHeaderList.size(); j++) {
                    if (mToDoHeaderList.get(j).getTitle().contains("Courses")) {
                        mToDoChildList.put(mToDoHeaderList.get(j).getTitle(), mToDoChildCourseList);
                        Log.d(TAG, "onDataChange child size: " + mToDoChildCourseList.size());
                        mExpandableListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            // Inflate the options menu from XML
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.alert_menu, menu);

            // Get the SearchView and set the searchable configuration
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
            // Assumes current activity is the searchable activity
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
            searchView.setVisibility(View.VISIBLE);
            return true;
            //showSearchIcon();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        if (!query.isEmpty()) {
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.isEmpty()) {
            searchText = "";
            // notifyAdapter(commonLandingDatas);
        } else {
            searchText = newText;
            getFilteredCourse();
        }
        getFilteredCourse();
        return false;
    }

    public void setActionBarTitle() {
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            int totalCount = mPendingCount + OustPreferences.getSavedInt("coursePendingCount") + OustPreferences.getSavedInt("assessmentPendingCount");
            mActionBar.setTitle("To Do");
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(OustPreferences.get(AppConstants.StringConstants.TOOL_BAR_COLOR_CODE))));
        }
    }

    public void gotoFFContest() {
        if (fastestFingerContestData != null) {
            Intent intent4 = new Intent(TodoListActivity.this, FFcontestStartActivity.class);
            Gson gson = new Gson();
            intent4.putExtra(AppConstants.StringConstants.FFC_DATA, gson.toJson(fastestFingerContestData));
            startActivity(intent4);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getFilteredCourse() {
        /*ArrayList<CommonLandingData> allCourcesFilter = new ArrayList<>();
        if (commonLandingDatas.size() > 0) {
            CourseFilter courseFilter = new CourseFilter();
            allCourcesFilter = courseFilter.meetCriteria(commonLandingDatas, searchText);
            notifyAdapter(allCourcesFilter);
        }*/
    }

    /*private void showSearchIcon() {
        try {
            if (filter_type!=null && (filter_type.equals("Pending") ||(filter_type.equals("Complete")))) {
                if (actionSearch != null && commonLandingDatas.size() > 4) {
                    actionSearch.setVisible(true);
                } else {
                    actionSearch.setVisible(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTodoPresenter.removeFFCDataListener();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }
}
