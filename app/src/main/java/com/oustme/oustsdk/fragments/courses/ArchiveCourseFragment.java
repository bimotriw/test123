package com.oustme.oustsdk.fragments.courses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
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

public class ArchiveCourseFragment extends Fragment implements RowClickCallBack {

    RecyclerView newlanding_recyclerview;
    TextView nocourse_text;
    SwipeRefreshLayout swipeRefreshLayout;

    private List<CourseDataClass> allCources = new ArrayList<>();
    private ActiveUser activeUser;

    private FirebaseRefClass firebaseRefClass;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.up_course_fragment, container, false);
            initViews(view);
            initLanding();
            return view;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            return inflater.inflate(R.layout.up_course_fragment, container, false);
        }
    }


    private void initViews(View view) {
        try {
            newlanding_recyclerview = view.findViewById(R.id.up_course_recyclerview);
            nocourse_text = view.findViewById(R.id.nocourse_text);
            swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void initLanding() {
        try {
            activeUser = OustAppState.getInstance().getActiveUser();
            if ((activeUser != null) && (activeUser.getStudentid() != null)) {
            } else {
                String activeUserGet = OustPreferences.get("userdata");
                activeUser = OustSdkTools.getActiveUserData(activeUserGet);
            }
            createLoader();
            showLoader();
            getMyCourcesFromFirebase();
            setHasOptionsMenu(true);
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

    public void getMyCourcesFromFirebase() {
        try {
            final String message = "/landingPage/" + activeUser.getStudentKey() + "/course";
            ValueEventListener myCourceListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue() != null) {
                            allCources = new ArrayList<>();
                            List<Object> learningList = new ArrayList<>();
                            Map<String, Object> levelnewMap = new HashMap<>();
                            Object o1 = dataSnapshot.getValue();
                            if (o1.getClass().equals(ArrayList.class)) {
                                learningList = (List<Object>) dataSnapshot.getValue();
                                final int[] index = new int[]{0};
                                for (int i = 0; i < learningList.size(); i++) {
                                    if ((learningList.get(i) != null)) {
                                        final Map<String, Object> lpMap = (Map<String, Object>) learningList.get(i);
                                        if (lpMap != null) {
                                            if (((lpMap.get("archived") != null) && ((boolean) lpMap.get("archived")))) {
                                                CourseDataClass courseDataClass = new CourseDataClass();
                                                if (lpMap.get("addedOn") != null) {
                                                    courseDataClass.setAddedOn((String) lpMap.get("addedOn"));
                                                }
                                                if (lpMap.get("locked") != null) {
                                                    courseDataClass.setLocked((boolean) lpMap.get("locked"));
                                                } else {
                                                    courseDataClass.setLocked(true);
                                                }
                                                if (lpMap.get("enrolled") != null) {
                                                    courseDataClass.setEnrolled((boolean) lpMap.get("enrolled"));
                                                }
                                                if (lpMap.get("userOC") != null) {
                                                    courseDataClass.setMyTotalOc((long) lpMap.get("userOC"));
                                                }
                                                if (lpMap.get("enrolled") != null) {
                                                    courseDataClass.setEnrolled((boolean) lpMap.get("enrolled"));
                                                }
                                                if (lpMap.get("completionPercentage") != null) {
                                                    Object o3 = lpMap.get("completionPercentage");
                                                    if (o3.getClass().equals(Long.class)) {
                                                        courseDataClass.setUserCompletionPercentage((long) o3);
                                                    } else if (o3.getClass().equals(String.class)) {
                                                        String s3 = (String) o3;
                                                        courseDataClass.setUserCompletionPercentage(Long.parseLong(s3));
                                                    } else if (o3.getClass().equals(Double.class)) {
                                                        Double s3 = (Double) o3;
                                                        long l = (new Double(s3)).longValue();
                                                        courseDataClass.setUserCompletionPercentage(l);
                                                    }
                                                }
                                                if (lpMap.get("weightage") != null) {
                                                    courseDataClass.setWeightage((long) lpMap.get("weightage"));
                                                }
                                                courseDataClass.setCourseId(i);
                                                allCources.add(courseDataClass);
                                            }
                                        }
                                    }
                                }
                                if ((allCources.size() == 0)) {
                                    createNotificationList(allCources);
                                }
                            } else if (o1.getClass().equals(HashMap.class)) {
                                final int[] index = new int[]{0};
                                levelnewMap = (Map<String, Object>) dataSnapshot.getValue();
                                for (String courseKey : levelnewMap.keySet()) {
                                    if ((levelnewMap.get(courseKey) != null)) {
                                        final Map<String, Object> lpMap = (Map<String, Object>) levelnewMap.get(courseKey);
                                        if (lpMap != null) {
                                            if (((lpMap.get("archived") != null) && ((boolean) lpMap.get("archived")))) {
                                                CourseDataClass courseDataClass = new CourseDataClass();
                                                if (lpMap.get("addedOn") != null) {
                                                    courseDataClass.setAddedOn((String) lpMap.get("addedOn"));
                                                }

                                                if (lpMap.get("locked") != null) {
                                                    courseDataClass.setLocked((boolean) lpMap.get("locked"));
                                                } else {
                                                    courseDataClass.setLocked(true);
                                                }
                                                if (lpMap.get("enrolled") != null) {
                                                    courseDataClass.setEnrolled((boolean) lpMap.get("enrolled"));
                                                }
                                                if (lpMap.get("completionPercentage") != null) {
                                                    Object o3 = lpMap.get("completionPercentage");
                                                    if (o3.getClass().equals(Long.class)) {
                                                        courseDataClass.setUserCompletionPercentage((long) o3);
                                                    } else if (o3.getClass().equals(String.class)) {
                                                        String s3 = (String) o3;
                                                        courseDataClass.setUserCompletionPercentage(Long.parseLong(s3));
                                                    } else if (o3.getClass().equals(Double.class)) {
                                                        Double s3 = (Double) o3;
                                                        long l = (new Double(s3)).longValue();
                                                        courseDataClass.setUserCompletionPercentage(l);
                                                    }
                                                }
                                                if (lpMap.get("userOC") != null) {
                                                    courseDataClass.setMyTotalOc((long) lpMap.get("userOC"));
                                                }
                                                if (lpMap.get("weightage") != null) {
                                                    courseDataClass.setWeightage((long) lpMap.get("weightage"));
                                                }
                                                if (lpMap.get("enrolled") != null) {
                                                    courseDataClass.setEnrolled((boolean) lpMap.get("enrolled"));
                                                }
                                                try {
                                                    courseDataClass.setCourseId(Long.parseLong(courseKey));
                                                } catch (Exception e) {
                                                }
                                                allCources.add(courseDataClass);
                                            }
                                        }
                                    }
                                }
                            }
                            if ((allCources.size() == 0)) {
                                OustPreferences.saveAppInstallVariable("archiveCoursePageOpend", true);
                                createNotificationList(allCources);
                            } else {
                                setCourseSingletonListener();
                            }
                        } else {
                            OustPreferences.saveAppInstallVariable("archiveCoursePageOpend", true);
                            createNotificationList(allCources);
                        }
                    } catch (Exception e) {
                        //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    newlanding_recyclerview.setVisibility(View.GONE);
                    OustSdkTools.checkInternetStatus();
                }
            };
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
            OustFirebaseTools.getRootRef().child(message).addValueEventListener(myCourceListener);
            firebaseRefClass = new FirebaseRefClass(myCourceListener, message);

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
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    private void setCourseSingletonListener() {
        try {
            final int[] index = new int[]{0};
            Collections.sort(allCources, courseListSortRev);
            for (int i = 0; i < allCources.size(); i++) {
                String msg1 = ("course/course" + allCources.get(i).getCourseId());
                ValueEventListener eventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot student) {
                        try {
                            if (null != student.getValue()) {
                                index[0]++;
                                final Map<String, Object> learingMap = (Map<String, Object>) student.getValue();
                                long currentId = 0;
                                int courseNo = 0;
                                if (learingMap.get("courseId") != null) {
                                    currentId = (long) learingMap.get("courseId");
                                }
                                for (int n = 0; n < allCources.size(); n++) {
                                    if (allCources.get(n).getCourseId() == currentId) {
                                        courseNo = n;
                                    }
                                }
                                if (learingMap.get("courseName") != null) {
                                    allCources.get(courseNo).setCourseName((String) learingMap.get("courseName"));
                                }
                                if (learingMap.get("bgImg") != null) {
                                    allCources.get(courseNo).setBgImg((String) learingMap.get("bgImg"));
                                }
                                if (learingMap.get("removeDataAfterCourseCompletion") != null) {
                                    allCources.get(courseNo).setRemoveDataAfterCourseCompletion((boolean) learingMap.get("removeDataAfterCourseCompletion"));
                                }
                                if (learingMap.get("numCards") != null) {
                                    allCources.get(courseNo).setNumCards((long) learingMap.get("numCards"));
                                }
                                if (learingMap.get("numDislike") != null) {
                                    allCources.get(courseNo).setNumDislike((long) learingMap.get("numDislike"));
                                }
                                if (learingMap.get("numLevels") != null) {
                                    allCources.get(courseNo).setNumLevels((long) learingMap.get("numLevels"));
                                }
                                if (learingMap.get("numLike") != null) {
                                    allCources.get(courseNo).setNumLike((long) learingMap.get("numLike"));
                                }
                                if (learingMap.get("reviewStarCount") != null) {
                                    allCources.get(courseNo).setReviewStarCount((long) learingMap.get("reviewStarCount"));
                                }
                                if (learingMap.get("icon") != null) {
                                    allCources.get(courseNo).setIcon((String) learingMap.get("icon"));
                                }
                                if (learingMap.get("numEnrolledUsers") != null) {
                                    allCources.get(courseNo).setNumEnrolledUsers((long) learingMap.get("numEnrolledUsers"));
                                }
                                if (learingMap.get("courseTime") != null) {
                                    allCources.get(courseNo).setTotalTime((long) learingMap.get("courseTime"));
                                }
                                if (learingMap.get("notificationTitle") != null) {
                                    allCources.get(courseNo).setNotificationTitle((String) learingMap.get("notificationTitle"));
                                }
                                if (learingMap.get("enrollReminderNotificationContent") != null) {
                                    allCources.get(courseNo).setEnrollNotificationContent((String) learingMap.get("enrollReminderNotificationContent"));
                                }
                                if (learingMap.get("completeReminderNotificationContent") != null) {
                                    allCources.get(courseNo).setCompleteNotificationContent((String) learingMap.get("completeReminderNotificationContent"));
                                }
                                long courseTotalOc = 0;
                                Object o1 = learingMap.get("levels");
                                if (o1.getClass().equals(ArrayList.class)) {
                                    List<Object> levelsList = (List<Object>) learingMap.get("levels");
                                    if (levelsList != null) {
                                        for (int i = 0; i < levelsList.size(); i++) {
                                            if (levelsList.get(i) != null) {
                                                final HashMap<String, Object> levelMap = (HashMap<String, Object>) levelsList.get(i);
                                                if (levelMap.get("totalOc") != null) {
                                                    courseTotalOc += ((long) levelMap.get("totalOc"));
                                                }
                                            }
                                        }
                                    }
                                } else if (o1.getClass().equals(HashMap.class)) {
                                    Map<String, Object> levelsList = (Map<String, Object>) learingMap.get("levels");
                                    List<CourseLevelClass> courseLevelClassList = new ArrayList<>();
                                    if (levelsList != null) {
                                        for (String s1 : levelsList.keySet()) {
                                            if (levelsList.get(s1) != null) {
                                                final HashMap<String, Object> levelMap = (HashMap<String, Object>) levelsList.get(s1);
                                                if (levelMap.get("totalOc") != null) {
                                                    courseTotalOc += ((long) levelMap.get("totalOc"));
                                                }
                                            }
                                        }
                                    }
                                }
                                allCources.get(courseNo).setTotalOc(courseTotalOc);
                            } else {
                                String courseID = student.getKey();
                                courseID = courseID.replace("course", "");
                                for (int k = 0; k < allCources.size(); k++) {
                                    if (("" + (allCources.get(k).getCourseId())).equalsIgnoreCase(courseID)) {
                                        allCources.remove(k);
                                    }
                                }
                            }
                            if (index[0] >= allCources.size()) {
                                Collections.sort(allCources, courseListSortRev);
                                OustPreferences.saveAppInstallVariable("archiveCoursePageOpend", true);
                                createNotificationList(allCources);
                            }
                        } catch (Exception e) {
                            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
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
        }
    }


    private ArchiveCourseAdaptor archiveCourseAdaptor;

    public void createNotificationList(List<CourseDataClass> courseDataClassList) {
        try {
            if ((courseDataClassList != null) && (courseDataClassList.size() > 0)) {
                //showSearchIcon();
                nocourse_text.setVisibility(View.GONE);
                newlanding_recyclerview.setVisibility(View.VISIBLE);
                if (archiveCourseAdaptor == null) {
                    archiveCourseAdaptor = new ArchiveCourseAdaptor(new ArrayList<AssessmentFirebaseClass>(), courseDataClassList, 0);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    newlanding_recyclerview.setLayoutManager(mLayoutManager);
                    newlanding_recyclerview.setItemAnimator(new DefaultItemAnimator());
                    archiveCourseAdaptor.setRowClickCallBack(ArchiveCourseFragment.this);
                    newlanding_recyclerview.setAdapter(archiveCourseAdaptor);
                } else {
                    archiveCourseAdaptor.notifyDateChanges(new ArrayList<AssessmentFirebaseClass>(), courseDataClassList);
                }
            } else {
                nocourse_text.setText(OustStrings.getString("no_course_assign"));
                nocourse_text.setVisibility(View.VISIBLE);
                newlanding_recyclerview.setVisibility(View.GONE);


            }
            swipeRefreshLayout.setRefreshing(false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public Comparator<CourseDataClass> courseListSortRev = new Comparator<CourseDataClass>() {
        public int compare(CourseDataClass s1, CourseDataClass s2) {
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
        popupTitle.setText(OustStrings.getString("unarchivecourse_header"));
        popupContent.setText(OustStrings.getString("unarchivecourse_content"));
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
        String getPointsUrl = OustSdkApplication.getContext().getResources().getString(R.string.unarchivecourse_url);
        getPointsUrl = getPointsUrl.replace("{userId}", activeUser.getStudentid());
        getPointsUrl = getPointsUrl.replace("{courseId}", id);

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
