package com.oustme.oustsdk.fragments.assessments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.adapter.common.ArchiveCourseAdaptor;
import com.oustme.oustsdk.firebase.assessment.AssessmentFirebaseClass;
import com.oustme.oustsdk.firebase.assessment.AssessmentType;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.interfaces.common.RowClickCallBack;
import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shilpysamaddar on 23/03/17.
 */

public class ArchiveAssessmentFragment extends Fragment implements RowClickCallBack {

    RecyclerView newlanding_recyclerview;
    TextView nocourse_text;
    SwipeRefreshLayout swipeRefreshLayout;
    RelativeLayout reflayout;

    private List<AssessmentFirebaseClass> assessmentFirebaseClassList = new ArrayList<>();

    private ActiveUser activeUser;
    private FirebaseRefClass firebaseRefClass;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.up_course_fragment, container, false);
        initViews(view);
        initLanding();
        return view;
    }

    private void initViews(View view) {

        newlanding_recyclerview = view.findViewById(R.id.up_course_recyclerview);
        nocourse_text = view.findViewById(R.id.nocourse_text);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        reflayout = view.findViewById(R.id.reflayout);
    }

    public void initLanding() {
        activeUser = OustAppState.getInstance().getActiveUser();
        if ((activeUser != null) && (activeUser.getStudentid() != null)) {
        } else {
            String activeUserGet = OustPreferences.get("userdata");
            activeUser = OustSdkTools.getActiveUserData(activeUserGet);
        }
        createLoader();
        showLoader();
        setHasOptionsMenu(true);
        getUserAssessments();
    }

    @Override
    public void onDestroy() {
        try {
            if ((firebaseRefClass != null) && (firebaseRefClass.getFirebasePath() != null)) {
                OustFirebaseTools.getRootRef().child(firebaseRefClass.getFirebasePath()).removeEventListener(firebaseRefClass.getEventListener());
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        super.onDestroy();
    }

    private void createLoader() {
        try {
            swipeRefreshLayout.setColorSchemeColors(OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen), OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen));
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showLoader() {
        try {
            swipeRefreshLayout.setColorSchemeColors(OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen), OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen));
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private ArchiveCourseAdaptor archiveCourseAdaptor;

    public void createNotificationList(List<AssessmentFirebaseClass> assessmentFirebaseClassList1) {
        try {
            if (((assessmentFirebaseClassList1 != null) && (assessmentFirebaseClassList1.size() > 0))) {
                nocourse_text.setVisibility(View.GONE);
                newlanding_recyclerview.setVisibility(View.VISIBLE);
                if (archiveCourseAdaptor == null) {
                    archiveCourseAdaptor = new ArchiveCourseAdaptor(assessmentFirebaseClassList1, new ArrayList<CourseDataClass>(), 1);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    newlanding_recyclerview.setLayoutManager(mLayoutManager);
                    newlanding_recyclerview.setItemAnimator(new DefaultItemAnimator());
                    archiveCourseAdaptor.setRowClickCallBack(ArchiveAssessmentFragment.this);
                    newlanding_recyclerview.setAdapter(archiveCourseAdaptor);
                } else {
                    archiveCourseAdaptor.notifyDateChanges(assessmentFirebaseClassList1, new ArrayList<CourseDataClass>());
                }
            } else {
                nocourse_text.setText(OustStrings.getString("no_assessment_assigned"));
                nocourse_text.setVisibility(View.VISIBLE);
                newlanding_recyclerview.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }
            swipeRefreshLayout.setRefreshing(false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public void getUserAssessments() {
        try {
            final String message = "/landingPage/" + activeUser.getStudentKey() + "/assessment";
            ValueEventListener myassessmentListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue() != null) {
                            List<Object> assessmentList = new ArrayList<>();
                            Map<String, Object> assessmentMap = new HashMap<>();
                            Object o1 = dataSnapshot.getValue();
                            if (o1.getClass().equals(ArrayList.class)) {
                                assessmentList = (List<Object>) dataSnapshot.getValue();
                                assessmentFirebaseClassList = new ArrayList<>();
                                for (int i = 0; i < assessmentList.size(); i++) {
                                    if ((assessmentList.get(i) != null)) {
                                        final Map<String, Object> assmtMap = (Map<String, Object>) assessmentList.get(i);
                                        if (assmtMap != null) {
                                            if (((assmtMap.get("archived") != null) && ((boolean) assmtMap.get("archived")))) {
                                                final AssessmentFirebaseClass assessmentFirebaseClass = new AssessmentFirebaseClass();
                                                if (assmtMap.get("addedOn") != null) {
                                                    assessmentFirebaseClass.setAddedOn((String) assmtMap.get("addedOn"));
                                                }
                                                if (assmtMap.get("elementId") != null) {
                                                    assessmentFirebaseClass.setAsssessemntId((long) assmtMap.get("elementId"));
                                                }
                                                if (assmtMap.get("locked") != null) {
                                                    assessmentFirebaseClass.setLocked((boolean) assmtMap.get("locked"));
                                                }

                                                if (assmtMap.get("completionDate") != null) {
                                                    assessmentFirebaseClass.setCompletionDate((String) assmtMap.get("completionDate"));
                                                }
                                                if (assmtMap.get("nQuestionAnswered") != null) {
                                                    int n1 = (int) ((long) assmtMap.get("nQuestionAnswered"));
                                                    assessmentFirebaseClass.setQuesAttempted(n1);
                                                }
                                                if (assmtMap.get("nQuestionCorrect") != null) {
                                                    int n1 = (int) ((long) assmtMap.get("nQuestionCorrect"));
                                                    assessmentFirebaseClass.setRightQues(n1);
                                                }
                                                if (assmtMap.get("nQuestionSkipped") != null) {
                                                    int n1 = (int) ((long) assmtMap.get("nQuestionSkipped"));
                                                    assessmentFirebaseClass.setSkippedQues(n1);
                                                }
                                                if (assmtMap.get("nQuestionWrong") != null) {
                                                    int n1 = (int) ((long) assmtMap.get("nQuestionWrong"));
                                                    assessmentFirebaseClass.setWrongQues(n1);
                                                }
                                                if (assmtMap.get("enrolled") != null) {
                                                    assessmentFirebaseClass.setEnrolled((boolean) assmtMap.get("enrolled"));
                                                }
                                                if (assmtMap.get("score_text") != null) {
                                                    Object o3 = assmtMap.get("score_text");
                                                    if (o3.getClass().equals(String.class)) {
                                                        int n1 = Integer.parseInt((String) o3);
                                                        assessmentFirebaseClass.setScore(n1);
                                                    } else if (o3.getClass().equals(Long.class)) {
                                                        int n1 = (int) ((long) o3);
                                                        assessmentFirebaseClass.setScore(n1);
                                                    }
                                                }

                                                if (assmtMap.get("weightage") != null) {
                                                    assessmentFirebaseClass.setPriority((long) assmtMap.get("weightage"));
                                                }
                                                assessmentFirebaseClassList.add(assessmentFirebaseClass);
                                            }
                                        }
                                    }
                                }
                            } else if (o1.getClass().equals(HashMap.class)) {
                                assessmentMap = (Map<String, Object>) dataSnapshot.getValue();
                                assessmentFirebaseClassList = new ArrayList<>();
                                if (assessmentMap != null) {
                                    for (String assessmentKey : assessmentMap.keySet()) {
                                        if ((assessmentMap.get(assessmentKey) != null)) {
                                            final Map<String, Object> assmtMap = (Map<String, Object>) assessmentMap.get(assessmentKey);
                                            if (assmtMap != null) {
                                                if (((assmtMap.get("archived") != null) && ((boolean) assmtMap.get("archived")))) {
                                                    final AssessmentFirebaseClass assessmentFirebaseClass = new AssessmentFirebaseClass();
                                                    if (assmtMap.get("addedOn") != null) {
                                                        assessmentFirebaseClass.setAddedOn((String) assmtMap.get("addedOn"));
                                                    }
                                                    if (assmtMap.get("elementId") != null) {
                                                        assessmentFirebaseClass.setAsssessemntId((long) assmtMap.get("elementId"));
                                                    }
                                                    if (assmtMap.get("locked") != null) {
                                                        assessmentFirebaseClass.setLocked((boolean) assmtMap.get("locked"));
                                                    }
                                                    if (assmtMap.get("completionDate") != null) {
                                                        assessmentFirebaseClass.setCompletionDate((String) assmtMap.get("completionDate"));
                                                    }
                                                    if (assmtMap.get("nQuestionAnswered") != null) {
                                                        int n1 = (int) ((long) assmtMap.get("nQuestionAnswered"));
                                                        assessmentFirebaseClass.setQuesAttempted(n1);
                                                    }
                                                    if (assmtMap.get("nQuestionCorrect") != null) {
                                                        int n1 = (int) ((long) assmtMap.get("nQuestionCorrect"));
                                                        assessmentFirebaseClass.setRightQues(n1);
                                                    }
                                                    if (assmtMap.get("enrolled") != null) {
                                                        assessmentFirebaseClass.setEnrolled((boolean) assmtMap.get("enrolled"));
                                                    }
                                                    if (assmtMap.get("nQuestionSkipped") != null) {
                                                        int n1 = (int) ((long) assmtMap.get("nQuestionSkipped"));
                                                        assessmentFirebaseClass.setSkippedQues(n1);
                                                    }
                                                    if (assmtMap.get("nQuestionWrong") != null) {
                                                        int n1 = (int) ((long) assmtMap.get("nQuestionWrong"));
                                                        assessmentFirebaseClass.setWrongQues(n1);
                                                    }
                                                    if (assmtMap.get("score_text") != null) {
                                                        Object o3 = assmtMap.get("score_text");
                                                        if (o3.getClass().equals(String.class)) {
                                                            int n1 = Integer.parseInt((String) o3);
                                                            assessmentFirebaseClass.setScore(n1);
                                                        } else if (o3.getClass().equals(Long.class)) {
                                                            int n1 = (int) ((long) o3);
                                                            assessmentFirebaseClass.setScore(n1);
                                                        }
                                                    }
                                                    if (assmtMap.get("weightage") != null) {
                                                        assessmentFirebaseClass.setPriority((long) assmtMap.get("weightage"));
                                                    }
                                                    assessmentFirebaseClassList.add(assessmentFirebaseClass);
                                                }
                                            }
                                        }
                                    }
                                    if ((assessmentFirebaseClassList.size() == 0)) {
                                        createNotificationList(assessmentFirebaseClassList);
                                    }
                                }
                            }
                            if ((assessmentFirebaseClassList.size() == 0)) {
                                createNotificationList(assessmentFirebaseClassList);
                            } else {
                                addAssessmentSinglotenListeners();
                            }
                        } else {
                            createNotificationList(assessmentFirebaseClassList);
                        }
                    } catch (Exception e) {
                        createNotificationList(assessmentFirebaseClassList);
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
                @Override
                public void onCancelled(DatabaseError error) {
                    newlanding_recyclerview.setVisibility(View.GONE);
                    OustSdkTools.checkInternetStatus();
                }
            };
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
            OustFirebaseTools.getRootRef().child(message).addValueEventListener(myassessmentListener);
            firebaseRefClass = new FirebaseRefClass(myassessmentListener, message);


            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        boolean connected = dataSnapshot.getValue(Boolean.class);
                        if (!connected) {
                            if (!OustPreferences.getAppInstallVariable("archiveCoursePageOpend")) {
                                newlanding_recyclerview.setVisibility(View.GONE);
                                if (swipeRefreshLayout != null)
                                    swipeRefreshLayout.setRefreshing(false);
                                OustSdkTools.checkInternetStatus();
                                nocourse_text.setText(OustStrings.getString("no_internet_connection"));
                                nocourse_text.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError error) {
                    if (!OustPreferences.getAppInstallVariable("archiveCoursePageOpend")) {
                        newlanding_recyclerview.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        OustSdkTools.checkInternetStatus();
                        nocourse_text.setText(OustStrings.getString("no_internet_connection"));
                        nocourse_text.setVisibility(View.VISIBLE);
                    }
                }
            };
            OustFirebaseTools.getRootRef().child(".info/connected");
            OustFirebaseTools.getRootRef().child(".info/connected").addListenerForSingleValueEvent(listener);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void addAssessmentSinglotenListeners() {
        try {
            final int[] index = new int[]{0};
            for (int i = 0; i < assessmentFirebaseClassList.size(); i++) {
                String msg1 = ("assessment/assessment" + assessmentFirebaseClassList.get(i).getAsssessemntId());
                ValueEventListener eventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot student) {
                        try {
                            if (null != student.getValue()) {
                                index[0]++;
                                final Map<String, Object> assessmentMap = (Map<String, Object>) student.getValue();
                                long currentId = 0;
                                int assessmentNo = 0;
                                if (assessmentMap.get("assessmentId") != null) {
                                    currentId = (long) assessmentMap.get("assessmentId");
                                }
                                for (int n = 0; n < assessmentFirebaseClassList.size(); n++) {
                                    if (assessmentFirebaseClassList.get(n).getAsssessemntId() == currentId) {
                                        assessmentNo = n;
                                    }
                                }
                                try {
                                    if (null != assessmentMap) {
                                        if (assessmentMap.get("active") != null) {
                                            assessmentFirebaseClassList.get(assessmentNo).setActive((boolean) assessmentMap.get("active"));
                                        }
                                        if (assessmentMap.get("showQuestionsOnCompletion") != null) {
                                            assessmentFirebaseClassList.get(assessmentNo).setShowQuestionsOnCompletion((boolean) assessmentMap.get("showQuestionsOnCompletion"));
                                        }
                                        if (assessmentMap.get("assessmentId") != null) {
                                            assessmentFirebaseClassList.get(assessmentNo).setAsssessemntId((long) assessmentMap.get("assessmentId"));
                                        }
                                        if (assessmentMap.get("totalTime") != null) {
                                            assessmentFirebaseClassList.get(assessmentNo).setTotalTime((long) assessmentMap.get("totalTime"));
                                        }
                                        if (assessmentMap.get("numQuestions") != null) {
                                            assessmentFirebaseClassList.get(assessmentNo).setNumQuestions((long) assessmentMap.get("numQuestions"));
                                        }
                                        if (assessmentMap.get("banner") != null) {
                                            assessmentFirebaseClassList.get(assessmentNo).setBanner((String) assessmentMap.get("banner"));
                                        }
                                        if (assessmentMap.get("description") != null) {
                                            assessmentFirebaseClassList.get(assessmentNo).setDescription((String) assessmentMap.get("description"));
                                        }
                                        if (assessmentMap.get("endDate") != null) {
                                            assessmentFirebaseClassList.get(assessmentNo).setEnddate((String) assessmentMap.get("endDate"));
                                        }
                                        if (assessmentMap.get("name") != null) {
                                            assessmentFirebaseClassList.get(assessmentNo).setName((String) assessmentMap.get("name"));
                                        }
                                        if (assessmentMap.get("passcode") != null) {
                                            assessmentFirebaseClassList.get(assessmentNo).setPasscode((String) assessmentMap.get("passcode"));
                                        }
                                        if (assessmentMap.get("scope") != null) {
                                            assessmentFirebaseClassList.get(assessmentNo).setScope((String) assessmentMap.get("scope"));
                                        }
                                        if (assessmentMap.get("type") != null) {
                                            assessmentFirebaseClassList.get(assessmentNo).setAssessmentType(AssessmentType.valueOf((String) assessmentMap.get("type")));
                                        }
                                        if (assessmentMap.get("startDate") != null) {
                                            assessmentFirebaseClassList.get(assessmentNo).setStartdate((String) assessmentMap.get("startDate"));
                                        }
                                        if (assessmentMap.get("logo") != null) {
                                            assessmentFirebaseClassList.get(assessmentNo).setLogo((String) assessmentMap.get("logo"));
                                        }
                                        if (assessmentMap.get("weightage") != null) {
                                            assessmentFirebaseClassList.get(assessmentNo).setPriority((long) assessmentMap.get("weightage"));
                                        }
                                        if (assessmentMap.get("participants") != null) {
                                            assessmentFirebaseClassList.get(assessmentNo).setEnrolledCount((long) assessmentMap.get("participants"));
                                        }
                                        if (assessmentMap.get("module") != null) {
                                            Map<String, Object> language = (Map<String, Object>) assessmentMap.get("module");
                                            Map<String, String> moduleMap = (Map<String, String>) language.get("language");
                                            assessmentFirebaseClassList.get(assessmentNo).setModulesMap(moduleMap);
                                        }
                                        if (assessmentMap.get("reattemptAllowed") != null) {
                                            assessmentFirebaseClassList.get(assessmentNo).setReattemptAllowed((boolean) assessmentMap.get("reattemptAllowed"));
                                        }
                                        if (assessmentMap.get("hideLeaderboard") != null) {
                                            assessmentFirebaseClassList.get(assessmentNo).setHideLeaderboard((boolean) assessmentMap.get("hideLeaderboard"));
                                        }
                                        if (assessmentMap.get("showPercentage") != null) {
                                            assessmentFirebaseClassList.get(assessmentNo).setShowPercentage((boolean) assessmentMap.get("showPercentage"));
                                        }
                                        if (assessmentMap.get("otpVerification") != null) {
                                            assessmentFirebaseClassList.get(assessmentNo).setOtpVerification((boolean) assessmentMap.get("otpVerification"));
                                        }
                                        if (assessmentMap.get("resultMessage") != null) {
                                            assessmentFirebaseClassList.get(assessmentNo).setResultMessage((String) assessmentMap.get("resultMessage"));
                                        }
                                    }
                                } catch (Exception e) {
                                }
                            } else {
                                String assessmentId = student.getKey();
                                assessmentId = assessmentId.replace("assessment", "");
                                for (int k = 0; k < assessmentFirebaseClassList.size(); k++) {
                                    if (("" + (assessmentFirebaseClassList.get(k).getAsssessemntId())).equalsIgnoreCase(assessmentId)) {
                                        assessmentFirebaseClassList.remove(k);
                                    }
                                }
                            }
                            if (index[0] >= assessmentFirebaseClassList.size()) {
                                Collections.sort(assessmentFirebaseClassList, assessmentListSortRev);
                                createNotificationList(assessmentFirebaseClassList);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                };
                OustFirebaseTools.getRootRef().child(msg1).keepSynced(true);
                OustFirebaseTools.getRootRef().child(msg1).addListenerForSingleValueEvent(eventListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public Comparator<AssessmentFirebaseClass> assessmentListSortRev = new Comparator<AssessmentFirebaseClass>() {
        public int compare(AssessmentFirebaseClass s1, AssessmentFirebaseClass s2) {
            if (s1.getAddedOn() != null && s2.getAddedOn() != null) {
                String source1 = s1.getAddedOn().toUpperCase();
                String source2 = s2.getAddedOn().toUpperCase();
                return source2.compareTo(source1);
            } else {
                return 0;
            }
        }
    };

    @Override
    public void onMainRowClick(final String name, int position) {
        if (!OustSdkTools.checkInternetStatus()) {
            return;
        }
        final View popUpView = getActivity().getLayoutInflater().inflate(R.layout.hide_course_popup, null); // inflating popup layout
        final PopupWindow challengeGamePopUp = OustSdkTools.createPopUp(popUpView);
        final LinearLayout course_removemain_layout = popUpView.findViewById(R.id.course_removemain_layout);
        final Button btnYes = popUpView.findViewById(R.id.btnYes);
        final Button btnNo = popUpView.findViewById(R.id.btnNo);
        final ImageButton btnClose = popUpView.findViewById(R.id.btnClose);
        TextView popupTitle = popUpView.findViewById(R.id.txtTitle);
        TextView popupContent = popUpView.findViewById(R.id.txtRejectChallengeMsg);
        popupTitle.setText(OustStrings.getString("unarchiveassessment_header"));
        popupContent.setText(OustStrings.getString("unarchiveassesssment_content"));
        btnYes.setText(OustStrings.getString("yes"));
        btnNo.setText(OustStrings.getString("no"));

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OustSdkTools.showProgressBar();
                sendArchiveRequest(name);
                challengeGamePopUp.dismiss();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                challengeGamePopUp.dismiss();
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                challengeGamePopUp.dismiss();
            }
        });
        OustSdkTools.popupAppearEffect(course_removemain_layout);

    }

    public void sendArchiveRequest(String id) {

        String getPointsUrl = OustSdkApplication.getContext().getResources().getString(R.string.unarchiveassessment_url);
        getPointsUrl = getPointsUrl.replace("{userId}", activeUser.getStudentid());
        getPointsUrl = getPointsUrl.replace("{assessmentId}", id);
        try {
            getPointsUrl = HttpManager.getAbsoluteUrl(getPointsUrl);

            ApiCallUtils.doNetworkCall(Request.Method.PUT, getPointsUrl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    CommonResponse commonResponse = gson.fromJson(response.toString(), CommonResponse.class);
                    archiveListOver(commonResponse);
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, getPointsUrl, OustSdkTools.getRequestObject(null), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    CommonResponse commonResponse=gson.fromJson(response.toString(),CommonResponse.class);
                    archiveListOver(commonResponse);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    ;
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<String, String>();
                    try {
                        params.put("api-key", OustPreferences.get("api_key"));
                        params.put("org-id", OustPreferences.get("tanentid"));
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    return params;
                }
            };
            jsonObjReq.setShouldCache(false);
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    public void archiveListOver(CommonResponse commonResponse) {
        OustSdkTools.hideProgressbar();
        try {
            if (commonResponse != null) {
                if (!commonResponse.isSuccess()) {
                    if ((commonResponse.getError() != null) && (!commonResponse.getError().isEmpty())) {
                        OustSdkTools.showToast(commonResponse.getError());

                    }
                }
            } else {
                OustSdkTools.showToast(OustStrings.getString("retry_internet_msg"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

}
