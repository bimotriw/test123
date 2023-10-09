package com.oustme.oustsdk.activity.courses;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.PopupActivity;
import com.oustme.oustsdk.activity.common.leaderboard.activity.GroupLBDataActivity;
import com.oustme.oustsdk.adapter.courses.LessonRowAdapter;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.CourseCollectionData;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.firebase.course.CourseDesclaimerData;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.interfaces.common.LessonCallBackInteface;
import com.oustme.oustsdk.interfaces.course.RowClickPositionCallBack;
import com.oustme.oustsdk.presenter.courses.LessonsActivityPresenter;
import com.oustme.oustsdk.request.ContentType;
import com.oustme.oustsdk.request.CourseBuyRequest;
import com.oustme.oustsdk.request.PaymentCategory;
import com.oustme.oustsdk.response.common.OustPopupButton;
import com.oustme.oustsdk.response.common.OustPopupCategory;
import com.oustme.oustsdk.response.common.Popup;
import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.service.DownloadCourseService;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.AvenuesParams;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustGATools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.ServiceUtility;
import com.oustme.oustsdk.tools.charttools.RSAUtility;
import com.oustme.oustsdk.tools.htmlrender.PaymentGetRSAMobileResponseData;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * Created by shilpysamaddar on 08/03/17.
 */

public class LessonsActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, RowClickPositionCallBack, View.OnClickListener, View.OnTouchListener{

    private RecyclerView lessons_recyclerview,feature_recyclerview;
    private LinearLayout lesson_course_info;
    private ImageView downarrow_image,lessons_appbar_image,mainback_button;
    private RelativeLayout lesson_course_detail,toolbar,mainloader_back;
    private SwipeRefreshLayout swipe_refresh_layout;
    private TextView lesson_course_name,pageTitle,coursecollection_topcontenttext,
            lessonbuy_text;
    private Button lessonbuy_btn;
    private CollapsingToolbarLayout collapsing_toolbar;
    private WebView payment_webview;
    private ProgressBar lpmain_loader;
    private ImageView mainbackbutton;
    private LinearLayout lessonmain_layout;
    private ImageButton lesson_closebtn;

    private boolean[] couseDownloadData = new boolean[20];

    private LessonRowAdapter lessonRowAdapter;

    private ActiveUser activeUser;
    private CourseCollectionData courseCollectionData;
    private FirebaseRefClass firebaseRefClass;
    private FirebaseRefClass firebaseRefSubClass;
    private String mainCollectionId;
    private boolean isActivityOpen = true;

    @Override
    protected void onStart() {
        isActivityOpen = true;

        super.onStart();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        try{
            OustSdkTools.setLocale(LessonsActivity.this);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        setContentView(R.layout.activity_lessons);
        initViews();
        intLessonsView();
//        OustGATools.getInstance().reportPageViewToGoogle(LessonsActivity.this,"Course Collection Landing Page");

    }

    @Override
    public void onStop() {
        super.onStop();
        isActivityOpen = false;
    }

    private void initViews() {
        lessons_recyclerview= findViewById(R.id.lessons_recyclerview);
        lesson_course_info= findViewById(R.id.lesson_course_info);
        downarrow_image= findViewById(R.id.downarrow_image);
        lesson_course_detail= findViewById(R.id.lesson_course_detail);
        toolbar= findViewById(R.id.tabanim_toolbar);
        swipe_refresh_layout= findViewById(R.id.swipe_refresh_layout);
        lesson_course_name= findViewById(R.id.lesson_course_name);
        lessons_appbar_image= findViewById(R.id.lessons_appbar_image);
        feature_recyclerview= findViewById(R.id.feature_recyclerview);
        mainback_button= findViewById(R.id.mainback_button);
        pageTitle= findViewById(R.id.pageTitle);
        lessonbuy_btn= findViewById(R.id.lessonbuy_btn);
        collapsing_toolbar= findViewById(R.id.collapsing_toolbar);
        coursecollection_topcontenttext= findViewById(R.id.coursecollection_topcontenttext);
        lessonbuy_text= findViewById(R.id.lessonbuy_text);
        payment_webview= findViewById(R.id.payment_webview);
        lpmain_loader= findViewById(R.id.lpmain_loader);
        mainloader_back= findViewById(R.id.mainloader_back);
        mainbackbutton= findViewById(R.id.mainbackbutton);
        lessonmain_layout= findViewById(R.id.lessonmain_layout);
        lesson_closebtn= findViewById(R.id.lesson_closebtn);

        lessonbuy_btn.setText(getResources().getString(R.string.buy));
        pageTitle.setText(getResources().getString(R.string.lessons_text));

        downarrow_image.setOnClickListener(this);
        lessonbuy_btn.setOnClickListener(this);
        mainback_button.setOnClickListener(this);
        lesson_course_info.setOnTouchListener(this);
        lesson_closebtn.setOnClickListener(this);


        OustSdkTools.setImage(lessons_appbar_image,getResources().getString(R.string.contests));
    }

    public void intLessonsView() {
        try {
            activeUser = OustAppState.getInstance().getActiveUser();
            if ((activeUser != null) && (activeUser.getStudentid() != null)) {} else {
                HttpManager.setBaseUrl();
                OustFirebaseTools.initFirebase();
                String activeUserGet = OustPreferences.get("userdata");
                activeUser = OustSdkTools.getActiveUserData(activeUserGet);
            }
            Intent CallingIntent = getIntent();
            if (CallingIntent.getStringExtra("collectionId") != null) {
                mainCollectionId = CallingIntent.getStringExtra("collectionId");
            }
            if (CallingIntent.getStringExtra("banner") != null) {
                setTopImage(CallingIntent.getStringExtra("banner"));
            }
            setToolbar();
            createAndShowLoader();
            setStartingPosition();
            getMyCourcesColletionsFromFirebase();
            setLayoutAspectRatiosmall();
            presenter= new LessonsActivityPresenter(LessonsActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //set top toolbar name and color depend on backend
    private void setToolbar() {
        try {
            mainbackbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LessonsActivity.this.finish();
                }
            });
            String toolbarColorCode = OustPreferences.get("toolbarColorCode");
            if ((toolbarColorCode != null) && (!toolbarColorCode.isEmpty())) {
                toolbar.setBackgroundColor(Color.parseColor(toolbarColorCode));
            }
        } catch (Exception e) {
        }
    }

    //show swipe refresh layout as loader
    private void createAndShowLoader() {
        try {
            swipe_refresh_layout.setColorSchemeColors(OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen), OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen));
            swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipe_refresh_layout.setRefreshing(false);
                }
            });
            swipe_refresh_layout.post(new Runnable() {
                @Override
                public void run() {
                    swipe_refresh_layout.setRefreshing(true);
                }
            });
        } catch (Exception e) {
        }
    }
    private void hideLoader() {
        try {
            swipe_refresh_layout.setRefreshing(false);
            swipe_refresh_layout.setVisibility(View.GONE);
        } catch (Exception e) {
        }
    }
    //starting positon for top view to animate it smoothly
    private void setStartingPosition() {
        try {
            lesson_course_info.setVisibility(View.VISIBLE);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(lesson_course_info, "translationY", 0, -lesson_course_info.getHeight());
            scaleDownY.setDuration(10);
            scaleDownY.setInterpolator(new AccelerateInterpolator());
            scaleDownY.start();
            scaleDownY.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {}
                @Override
                public void onAnimationEnd(Animator animator) {
                    lesson_course_info.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {}

                @Override
                public void onAnimationRepeat(Animator animator) {}
            });
        } catch (Exception e) {}
    }
    //set fix banner size
    private void setLayoutAspectRatiosmall() {
        try {
            DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            int scrWidth = metrics.widthPixels;
            int size = (int) getResources().getDimension(R.dimen.oustlayout_dimen40);
            int imageWidth = (scrWidth) - size;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) lessons_appbar_image.getLayoutParams();
            float h = (imageWidth * 0.34f);
            int h1 = (int) h;
            params.height = h1;
            lessons_appbar_image.setLayoutParams(params);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    //get course collection info from firebase
    public void getMyCourcesColletionsFromFirebase() {
        try {
            courseCollectionData = new CourseCollectionData();
            final String message = "/landingPage/" + activeUser.getStudentKey() + "/courseColn/" + mainCollectionId;
            ValueEventListener myCourceListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue() != null) {
                            if (courseCollectionData.getCourseDataClassList() == null) {
                                courseCollectionData.setCourseDataClassList(new ArrayList<CourseDataClass>());
                            }
                            final List<CourseDataClass> allCources = courseCollectionData.getCourseDataClassList();
                            courseCollectionData.setCollectionId(Long.parseLong(mainCollectionId));
                            Map<String, Object> courseCollectionMap = (Map<String, Object>) dataSnapshot.getValue();
                            if (courseCollectionMap.get("addedOn") != null) {
                                courseCollectionData.setAddedOn((String) courseCollectionMap.get("addedOn"));
                            }
                            if (courseCollectionMap.get("currentCourseId") != null) {
                                courseCollectionData.setCurrentCourseId((long) courseCollectionMap.get("currentCourseId"));
                            }
                            if (courseCollectionMap.get("purchased") != null) {
                                courseCollectionData.setPurchased((boolean) courseCollectionMap.get("purchased"));
                            }
                            if (courseCollectionMap.get("mappedAssessment") != null) {
                                final Map<String, Object> assessmentMap = (Map<String, Object>) courseCollectionMap.get("mappedAssessment");
                                if (assessmentMap != null) {
                                    if (assessmentMap.get("completionDate") != null) {
                                        courseCollectionData.setAssessmentCompletionDate((String) assessmentMap.get("completionDate"));
                                    }
                                    if (assessmentMap.get("enrolled") != null) {
                                        courseCollectionData.setAssessmentEnrolled((boolean) assessmentMap.get("enrolled"));
                                    }
                                    if (assessmentMap.get("locked") != null) {
                                        courseCollectionData.setAssessmentLoacked((boolean) assessmentMap.get("locked"));
                                    }
                                }
                            }
                            if (courseCollectionMap.get("userOC") != null) {
                                courseCollectionData.setUserOc((long) courseCollectionMap.get("userOC"));
                            }
                            if (courseCollectionMap.get("userCompletionPercentage") != null) {
                                Object o3 = courseCollectionMap.get("userCompletionPercentage");
                                if (o3.getClass().equals(Long.class)) {
                                    courseCollectionData.setCompletePresentage((long) o3);
                                } else if (o3.getClass().equals(String.class)) {
                                    String s3 = (String) o3;
                                    courseCollectionData.setCompletePresentage(Long.parseLong(s3));
                                } else if (o3.getClass().equals(Double.class)) {
                                    Double s3 = (Double) o3;
                                    long l = (new Double(s3)).longValue();
                                    courseCollectionData.setCompletePresentage(l);
                                }
                            }
                            if (courseCollectionMap.get("courses") != null) {
                                Object o2 = courseCollectionMap.get("courses");
                                if (o2.getClass().equals(HashMap.class)) {
                                    Map<String, Object> featureMainMap = (Map<String, Object>) o2;
                                    for (String key : featureMainMap.keySet()) {
                                        Object o4 = featureMainMap.get(key);
                                        if (o4 != null) {
                                            key = key.replace("course", "");
                                            long currentId = Long.parseLong(key);
                                            int courseNo = -1;
                                            for (int n = 0; n < allCources.size(); n++) {
                                                if (allCources.get(n).getCourseId() == currentId) {
                                                    courseNo = n;
                                                }
                                            }
                                            if (courseNo < 0) {
                                                CourseDataClass courseDataClass = new CourseDataClass();
                                                courseDataClass.setCourseId(currentId);
                                                allCources.add(courseDataClass);
                                                courseNo = allCources.size() - 1;
                                            }
                                            final Map<String, Object> courseMap = (Map<String, Object>) o4;
                                            allCources.get(courseNo).setCourseId(Long.parseLong(key));
                                            if (courseMap.get("locked") != null) {
                                                allCources.get(courseNo).setLocked((boolean) courseMap.get("locked"));
                                            }
                                            if (courseMap.get("completionPercentage") != null) {
                                                Object o3 = courseMap.get("completionPercentage");
                                                if (o3.getClass().equals(Long.class)) {
                                                    allCources.get(courseNo).setUserCompletionPercentage((long) o3);
                                                } else if (o3.getClass().equals(String.class)) {
                                                    String s3 = (String) o3;
                                                    allCources.get(courseNo).setUserCompletionPercentage(Long.parseLong(s3));
                                                } else if (o3.getClass().equals(Double.class)) {
                                                    Double s3 = (Double) o3;
                                                    long l = (new Double(s3)).longValue();
                                                    allCources.get(courseNo).setUserCompletionPercentage(l);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            courseCollectionData.setCourseDataClassList(allCources);
                            getCollectionData();
                        }
                    } catch (Exception e) {}
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            };
            OustFirebaseTools.getRootRef().child(message).addValueEventListener(myCourceListener);
            firebaseRefClass = new FirebaseRefClass(myCourceListener, message);
        } catch (Exception e) {}
    }

    private void getCollectionData() {
        if (firebaseRefSubClass == null) {
            final List<CourseDataClass> allCources = courseCollectionData.getCourseDataClassList();
            String collectionLinkMsg = "courseCollection/courseColn" + mainCollectionId;
            ValueEventListener eventListener1 = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot student) {
                    try {
                        if (null != student.getValue()) {
                            final Map<String, Object> collectionMap = (Map<String, Object>) student.getValue();
                            if (collectionMap.get("banner") != null) {
                                courseCollectionData.setBanner((String) collectionMap.get("banner"));
                            }
                            if (collectionMap.get("name") != null) {
                                courseCollectionData.setName((String) collectionMap.get("name"));
                            }
                            if (collectionMap.get("description") != null) {
                                courseCollectionData.setDescription((String) collectionMap.get("description"));
                            }
                            if(collectionMap.get("freeCourse")!=null){
                                courseCollectionData.setFreeCourse((boolean) collectionMap.get("freeCourse"));
                            }
                            if (collectionMap.get("mappedAssessmentId") != null) {
                                courseCollectionData.setMappedAssessmentId((long) collectionMap.get("mappedAssessmentId"));
                            }
                            if(collectionMap.get("mappedAssessmentDetails")!=null){
                                Map<String,Object> mappedAssessmentMap=(Map<String,Object>)collectionMap.get("mappedAssessmentDetails");
                                if(mappedAssessmentMap.get("name")!=null){
                                    courseCollectionData.setMappedAssessmentName((String) mappedAssessmentMap.get("name"));
                                }
                                if(mappedAssessmentMap.get("description")!=null){
                                    courseCollectionData.setMappedAssessmentDescription((String) mappedAssessmentMap.get("description"));
                                }
                                if(mappedAssessmentMap.get("icon")!=null){
                                    courseCollectionData.setMappedAssessmentIcon((String) mappedAssessmentMap.get("icon"));
                                }
                                if(mappedAssessmentMap.get("id")!=null){
                                    courseCollectionData.setMappedAssessmentId((long) mappedAssessmentMap.get("id"));
                                }
                            }
                            if (collectionMap.get("numEnrolledUsers") != null) {
                                courseCollectionData.setNumEnrolledUsers((long) collectionMap.get("numEnrolledUsers"));
                            }
                            if (collectionMap.get("courseCollectionTime") != null) {
                                courseCollectionData.setCourseCollectionTime((long) collectionMap.get("courseCollectionTime"));
                            }
                            if (collectionMap.get("rating") != null) {
                                courseCollectionData.setRating((long) collectionMap.get("rating"));
                            }
                            if (collectionMap.get("price") != null) {
                                courseCollectionData.setPrise((long) collectionMap.get("price"));
                            }
//                            List<CourseCollectionFeatureInfo> courseCollectionFeatureInfos = new ArrayList<>();
//                            if (collectionMap.get("courseColnFeatures") != null) {
//                                Object o2 = collectionMap.get("courseColnFeatures");
//                                if (o2.getClass().equals(ArrayList.class)) {
//                                    List<Object> featuresList = (List<Object>) o2;
//                                    for (int i = 0; i < featuresList.size(); i++) {
//                                        if (featuresList.get(i) != null) {
//                                            Object o4 = featuresList.get(i);
//                                            if (o4 != null) {
//                                                CourseCollectionFeatureInfo courseCollectionFeatureInfo = new CourseCollectionFeatureInfo();
//                                                final Map<String, Object> featureMap = (Map<String, Object>) featuresList.get(i);
//                                                if (featureMap.get("name") != null) {
//                                                    courseCollectionFeatureInfo.setName((String) featureMap.get("name"));
//                                                }
//                                                if (featureMap.get("description") != null) {
//                                                    courseCollectionFeatureInfo.setDescription((String) featureMap.get("description"));
//                                                }
//                                                if (featureMap.get("icon") != null) {
//                                                    courseCollectionFeatureInfo.setIcon((String) featureMap.get("icon"));
//                                                }
//                                                if (featureMap.get("featureIconBgColor") != null) {
//                                                    courseCollectionFeatureInfo.setFeatureIconBgColor((String) featureMap.get("featureIconBgColor"));
//                                                }
//                                                courseCollectionFeatureInfos.add(courseCollectionFeatureInfo);
//                                            }
//                                        }
//                                    }
//                                } else if (o2.getClass().equals(HashMap.class)) {
//                                    Map<String, Object> featureMainMap = (Map<String, Object>) o2;
//                                    for (String key : featureMainMap.keySet()) {
//                                        CourseCollectionFeatureInfo courseCollectionFeatureInfo = new CourseCollectionFeatureInfo();
//                                        final Map<String, Object> featureMap = (Map<String, Object>) featureMainMap.get(key);
//                                        if (featureMap.get("name") != null) {
//                                            courseCollectionFeatureInfo.setName((String) featureMap.get("name"));
//                                        }
//                                        if (featureMap.get("description") != null) {
//                                            courseCollectionFeatureInfo.setDescription((String) featureMap.get("description"));
//                                        }
//                                        if (featureMap.get("icon") != null) {
//                                            courseCollectionFeatureInfo.setIcon((String) featureMap.get("icon"));
//                                        }
//                                        if (featureMap.get("featureIconBgColor") != null) {
//                                            courseCollectionFeatureInfo.setFeatureIconBgColor((String) featureMap.get("featureIconBgColor"));
//                                        }
//                                        courseCollectionFeatureInfos.add(courseCollectionFeatureInfo);
//                                    }
//                                }
//                                courseCollectionData.setCourseCollectionFeatureInfoList(courseCollectionFeatureInfos);
//                            }
                            Object o1 = collectionMap.get("courses");
                            if (o1.getClass().equals(HashMap.class)) {
                                final int[] index = new int[]{0};
                                Map<String, Object> levelnewMap = (Map<String, Object>) o1;
                                for (String courseKey : levelnewMap.keySet()) {
                                    if ((levelnewMap.get(courseKey) != null)) {
                                        final Map<String, Object> lpMap = (Map<String, Object>) levelnewMap.get(courseKey);
                                        if (lpMap != null) {
                                            long currentId = 0;
                                            int courseNo = -1;
                                            if (lpMap.get("courseId") != null) {
                                                currentId = (long) lpMap.get("courseId");
                                            }
                                            for (int n = 0; n < allCources.size(); n++) {
                                                if (allCources.get(n).getCourseId() == currentId) {
                                                    courseNo = n;
                                                }
                                            }
                                            if (courseNo < 0) {
                                                allCources.add(new CourseDataClass());
                                                courseNo = allCources.size() - 1;
                                            }
                                            if (lpMap.get("addedOn") != null) {
                                                allCources.get(courseNo).setAddedOn((String) lpMap.get("addedOn"));
                                            }
                                            if (lpMap.get("courseId") != null) {
                                                allCources.get(courseNo).setCourseId((long) lpMap.get("courseId"));
                                            }
                                            if (lpMap.get("sequence") != null) {
                                                allCources.get(courseNo).setWeightage((long) lpMap.get("sequence"));
                                            }
                                            if (lpMap.get("enrolled") != null) {
                                                allCources.get(courseNo).setEnrolled((boolean) lpMap.get("enrolled"));
                                            }

                                            if (lpMap.get("completionPercentage") != null) {
                                                Object o3 = lpMap.get("completionPercentage");
                                                if (o3.getClass().equals(Long.class)) {
                                                    allCources.get(courseNo).setUserCompletionPercentage((long) o3);
                                                } else if (o3.getClass().equals(String.class)) {
                                                    String s3 = (String) o3;
                                                    allCources.get(courseNo).setUserCompletionPercentage(Long.parseLong(s3));
                                                } else if (o3.getClass().equals(Double.class)) {
                                                    Double s3 = (Double) o3;
                                                    long l = (new Double(s3)).longValue();
                                                    allCources.get(courseNo).setUserCompletionPercentage(l);
                                                }
                                            }
                                            if (lpMap.get("userOC") != null) {
                                                allCources.get(courseNo).setMyTotalOc((long) lpMap.get("userOC"));
                                            }
                                            if (lpMap.get("enrolled") != null) {
                                                allCources.get(courseNo).setEnrolled((boolean) lpMap.get("enrolled"));
                                            }
                                            String msg1 = ("course/course" + allCources.get(courseNo).getCourseId());
                                            ValueEventListener eventListener = new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot student) {
                                                    try {
                                                        final int allCourseSize = allCources.size();
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
                                                            if (learingMap.get("description") != null) {
                                                                allCources.get(courseNo).setDescription((String) learingMap.get("description"));
                                                            }
                                                            if (learingMap.get("courseName") != null) {
                                                                allCources.get(courseNo).setCourseName((String) learingMap.get("courseName"));
                                                            }
                                                            if (learingMap.get("bgImg") != null) {
                                                                allCources.get(courseNo).setBgImg((String) learingMap.get("bgImg"));
                                                            }
                                                            if (learingMap.get("lpBgImageNew") != null) {
                                                                allCources.get(courseNo).setLpBgImage((String) learingMap.get("lpBgImageNew"));
                                                            }
                                                            if (learingMap.get("removeDataAfterCourseCompletion") != null) {
                                                                allCources.get(courseNo).setRemoveDataAfterCourseCompletion((boolean) learingMap.get("removeDataAfterCourseCompletion"));
                                                            }
                                                            if (learingMap.get("numCards") != null) {
                                                                allCources.get(courseNo).setNumCards((long) learingMap.get("numCards"));
                                                            }
                                                            if(learingMap.get("needsAck")!=null){
                                                                allCources.get(courseNo).setNeedsAck((boolean)learingMap.get("needsAck"));
                                                            }
                                                            if(learingMap.get("ackPopup")!=null){
                                                                Map<String,Object> desclaimerMap=(Map<String,Object>)learingMap.get("ackPopup");
                                                                if(desclaimerMap!=null){
                                                                    CourseDesclaimerData courseDesclaimerData=new CourseDesclaimerData();
                                                                    if(desclaimerMap.get("body")!=null){
                                                                        courseDesclaimerData.setContent((String) desclaimerMap.get("body"));
                                                                    }
                                                                    if(desclaimerMap.get("buttonLabel")!=null){
                                                                        courseDesclaimerData.setBtnText((String) desclaimerMap.get("buttonLabel"));
                                                                    }
                                                                    if(desclaimerMap.get("checkBoxText")!=null){
                                                                        courseDesclaimerData.setCheckBoxText((String) desclaimerMap.get("checkBoxText"));
                                                                    }
                                                                    if(desclaimerMap.get("header")!=null){
                                                                        courseDesclaimerData.setHeader((String) desclaimerMap.get("header"));
                                                                    }
                                                                    allCources.get(courseNo).setCourseDesclaimerData(courseDesclaimerData);
                                                                }
                                                            }
                                                            if(learingMap.get("descriptionCard")!=null){
                                                                allCources.get(courseNo).setCardInfo((Map<String, Object>) learingMap.get("descriptionCard"));
                                                            }
                                                            if (learingMap.get("rating") != null) {
                                                                allCources.get(courseNo).setRating((long) learingMap.get("rating"));
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
                                                            long totalLevels = 0;
                                                            List<CourseLevelClass> courseLevelClassList = new ArrayList<>();
                                                            Object o1 = learingMap.get("levels");
                                                            if (o1 != null) {
                                                                if (o1.getClass().equals(ArrayList.class)) {
                                                                    List<Object> levelsList = (List<Object>) learingMap.get("levels");
                                                                    if (levelsList != null) {
                                                                        for (int i = 0; i < levelsList.size(); i++) {
                                                                            CourseLevelClass courseLevelClass = new CourseLevelClass();
                                                                            if (levelsList.get(i) != null) {
                                                                                totalLevels++;
                                                                                courseLevelClass.setLevelId(i);
                                                                                final HashMap<String, Object> levelMap = (HashMap<String, Object>) levelsList.get(i);
                                                                                if (levelMap.get("totalOc") != null) {
                                                                                    courseTotalOc += ((long) levelMap.get("totalOc"));
                                                                                }
                                                                                Object o2 = levelMap.get("cards");
                                                                                if (o2 != null) {
                                                                                    if (o2.getClass().equals(ArrayList.class)) {
                                                                                        List<Object> objectCardList = (List<Object>) o2;
                                                                                        if (objectCardList != null) {
                                                                                            List<DTOCourseCard> courseCardClassList = new ArrayList<>();
                                                                                            for (int j = 0; j < objectCardList.size(); j++) {
                                                                                                if (objectCardList.get(j) != null) {
                                                                                                    DTOCourseCard courseCardClass = new DTOCourseCard();
                                                                                                    courseCardClass.setCardId(j);
                                                                                                    courseCardClassList.add(courseCardClass);
                                                                                                }
                                                                                            }
                                                                                            courseLevelClass.setCourseCardClassList(courseCardClassList);
                                                                                        }
                                                                                    } else if (o2.getClass().equals(HashMap.class)) {
                                                                                        final Map<String, Object> objectCardMap = (Map<String, Object>) o2;
                                                                                        List<DTOCourseCard> courseCardClassList = new ArrayList<>();
                                                                                        for (String cardKey : objectCardMap.keySet()) {
                                                                                            if (objectCardMap.get(cardKey) != null) {
                                                                                                DTOCourseCard courseCardClass = new DTOCourseCard();
                                                                                                courseCardClass.setCardId(Long.parseLong(cardKey));
                                                                                                courseCardClassList.add(courseCardClass);
                                                                                            }
                                                                                        }
                                                                                        courseLevelClass.setCourseCardClassList(courseCardClassList);
                                                                                    }
                                                                                }
                                                                                courseLevelClassList.add(courseLevelClass);
                                                                            }
                                                                        }
                                                                    }
                                                                } else if (o1.getClass().equals(HashMap.class)) {
                                                                    Map<String, Object> levelsList = (Map<String, Object>) learingMap.get("levels");
                                                                    if (levelsList != null) {
                                                                        for (String s1 : levelsList.keySet()) {
                                                                            CourseLevelClass courseLevelClass = new CourseLevelClass();
                                                                            if (levelsList.get(s1) != null) {
                                                                                totalLevels++;
                                                                                final HashMap<String, Object> levelMap = (HashMap<String, Object>) levelsList.get(s1);
                                                                                if (levelMap.get("totalOc") != null) {
                                                                                    courseTotalOc += ((long) levelMap.get("totalOc"));
                                                                                }
                                                                                courseLevelClass.setLevelId(Long.parseLong(s1));
                                                                                Object o2 = levelMap.get("cards");
                                                                                if (o2 != null) {
                                                                                    if (o2.getClass().equals(ArrayList.class)) {
                                                                                        List<Object> objectCardList = (List<Object>) o2;
                                                                                        if (objectCardList != null) {
                                                                                            List<DTOCourseCard> courseCardClassList = new ArrayList<>();
                                                                                            for (int j = 0; j < objectCardList.size(); j++) {
                                                                                                if (objectCardList.get(j) != null) {
                                                                                                    DTOCourseCard courseCardClass = new DTOCourseCard();
                                                                                                    courseCardClass.setCardId(j);
                                                                                                    courseCardClassList.add(courseCardClass);
                                                                                                }
                                                                                            }
                                                                                            courseLevelClass.setCourseCardClassList(courseCardClassList);
                                                                                        }
                                                                                    } else if (o2.getClass().equals(HashMap.class)) {
                                                                                        final Map<String, Object> objectCardMap = (Map<String, Object>) o2;
                                                                                        List<DTOCourseCard> courseCardClassList = new ArrayList<>();
                                                                                        for (String cardKey : objectCardMap.keySet()) {
                                                                                            if (objectCardMap.get(cardKey) != null) {
                                                                                                DTOCourseCard courseCardClass = new DTOCourseCard();
                                                                                                courseCardClass.setCardId(Long.parseLong(cardKey));
                                                                                                courseCardClassList.add(courseCardClass);
                                                                                            }
                                                                                        }
                                                                                        courseLevelClass.setCourseCardClassList(courseCardClassList);
                                                                                    }
                                                                                }
                                                                                courseLevelClassList.add(courseLevelClass);
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                            allCources.get(courseNo).setCourseLevelClassList(courseLevelClassList);
                                                            allCources.get(courseNo).setNumLevels(totalLevels);
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
                                                            Collections.sort(allCources, courseListSort);
                                                            courseCollectionData.setCourseDataClassList(allCources);
                                                            if (isActivityOpen) {
                                                                setTopData();
                                                                createList();
                                                            }
                                                        }
                                                    } catch (Exception e) {
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError error) {
                                                }
                                            };
                                            OustFirebaseTools.getRootRef().child(msg1).addListenerForSingleValueEvent(eventListener);
                                            OustFirebaseTools.getRootRef().child(msg1).keepSynced(true);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            };
            OustFirebaseTools.getRootRef().child(collectionLinkMsg).addValueEventListener(eventListener1);
            firebaseRefSubClass = new FirebaseRefClass(eventListener1, collectionLinkMsg);
        } else {
            if (isActivityOpen) {
                setTopData();
                createList();
            }
        }
    }

    public Comparator<CourseDataClass> courseListSort = new Comparator<CourseDataClass>() {
        public int compare(CourseDataClass s1, CourseDataClass s2) {
            return ((int)s2.getWeightage()-(int)s2.getWeightage());
        }
    };

    @Override
    protected void onDestroy() {
        try {
            if (firebaseRefClass != null) {
                OustFirebaseTools.getRootRef().child(firebaseRefClass.getFirebasePath()).removeEventListener(firebaseRefClass.getEventListener());
            }
            if (firebaseRefSubClass != null) {
                OustFirebaseTools.getRootRef().child(firebaseRefSubClass.getFirebasePath()).removeEventListener(firebaseRefSubClass.getEventListener());
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        super.onDestroy();
    }

    //set top data userOC and usercount etc
    //set top data userOC and usercount etc
    private void setTopData() {
        if (courseCollectionData.isPurchased()) {
            lessonbuy_text.setVisibility(View.GONE);
            lessonbuy_btn.setVisibility(View.GONE);
        } else {
            if(courseCollectionData.isFreeCourse()){
                lessonbuy_text.setVisibility(View.GONE);
//                lessonbuy_text.setText("Rs " + courseCollectionData.getPrise() + "/-");
                lessonbuy_btn.setText(getResources().getString(R.string.try_for_free));
                lessonbuy_btn.setVisibility(View.VISIBLE);
            }else {
                lessonbuy_text.setVisibility(View.VISIBLE);
                lessonbuy_text.setText("Rs " + courseCollectionData.getPrise() + "/-");
                lessonbuy_btn.setVisibility(View.VISIBLE);
            }
            //setCardAnimation();
        }
        if (courseCollectionData.getName() != null) {
            lesson_course_name.setText(courseCollectionData.getName());
        }
        setTopImage(courseCollectionData.getBanner());
        serTopCollectionDescription();
        hideWebViewIFPaymentDone();
    }

    private void setTopImage(String banner){
        if ((banner != null) && (!banner.isEmpty())) {
            if (OustSdkTools.checkInternetStatus()) {
                Picasso.get().load(banner)
                        .placeholder(R.drawable.roundedcornergraysolid)
                        .error(R.drawable.roundedcornergraysolid)
                        .into(lessons_appbar_image);
            } else {
                Picasso.get().load(banner)
                        .placeholder(R.drawable.roundedcornergraysolid)
                        .error(R.drawable.roundedcornergraysolid)
                        .networkPolicy(NetworkPolicy.OFFLINE).into(lessons_appbar_image);
            }
        }
    }
    private static LessonsActivityPresenter presenter;
    public static LessonCallBackInteface lessonCallBackInteface=new LessonCallBackInteface() {
        @Override
        public void hideLoader() {
            presenter.hideLoaderandShowError();
        }
    };
    public  void hideWebViewError() {
        try {
            payment_webview.setVisibility(View.GONE);
            lessonbuy_text.setVisibility(View.GONE);
            lessonbuy_btn.setVisibility(View.GONE);
            hideWebLoader();
        } catch (Exception e) {}
    }

    private void hideWebViewIFPaymentDone() {
        try {
            if (courseCollectionData.isPurchased()) {
                if (mainloader_back.getVisibility() == View.VISIBLE) {
                    payment_webview.setVisibility(View.GONE);
                    lessonbuy_text.setVisibility(View.GONE);
                    lessonbuy_btn.setVisibility(View.GONE);
                    Popup popup=new Popup();
                    OustPopupButton oustPopupButton=new OustPopupButton();
                    oustPopupButton.setBtnText("OK");
                    List<OustPopupButton> btnList=new ArrayList<>();
                    btnList.add(oustPopupButton);
                    popup.setButtons(btnList);
                    if(courseCollectionData.isFreeCourse()){
                        popup.setContent(getResources().getString(R.string.freecourse_enrollsuccess_msg));
                    }else {
                        popup.setContent(getResources().getString(R.string.paymentsuccess_msg));
                    }
                    popup.setCategory(OustPopupCategory.NOACTION);
                    OustStaticVariableHandling.getInstance().setOustpopup(popup);
                    Intent intent = new Intent(OustSdkApplication.getContext(), PopupActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    OustSdkApplication.getContext().startActivity(intent);
                }
                hideWebLoader();
            }
        } catch (Exception e) {}
    }



    // create list of courses
    private void createList() {
        hideLoader();
        int currentCourseNo = 0;
        List<CourseDataClass> courseDataClassList = courseCollectionData.getCourseDataClassList();
        Collections.sort(courseDataClassList, courseListSortIncomplete);
        courseCollectionData.setCourseDataClassList(courseDataClassList);
        for (int i = 0; i < courseCollectionData.getCourseDataClassList().size(); i++) {
            if (!courseCollectionData.getCourseDataClassList().get(i).isLocked()) {
                currentCourseNo++;
            }
        }
        if (lessonRowAdapter == null) {
            lessonRowAdapter = new LessonRowAdapter(courseCollectionData, ("" + courseCollectionData.getCollectionId()), courseCollectionData.isPurchased(), couseDownloadData);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            lessons_recyclerview.setLayoutManager(mLayoutManager);
            lessons_recyclerview.setItemAnimator(new DefaultItemAnimator());
            lessonRowAdapter.setRowClickPositionCallBack(LessonsActivity.this);
            lessonRowAdapter.setCurrentCourseNo(currentCourseNo);
            lessons_recyclerview.setAdapter(lessonRowAdapter);
        } else {
            lessonRowAdapter.setCurrentCourseNo(currentCourseNo);
            lessonRowAdapter.onCollectionDataChanged(courseCollectionData, ("" + courseCollectionData.getCollectionId()), courseCollectionData.isPurchased(), couseDownloadData);
        }
    }

    public Comparator<CourseDataClass> courseListSortIncomplete = new Comparator<CourseDataClass>() {
        public int compare(CourseDataClass s1, CourseDataClass s2) {
            return (int) s1.getWeightage() - (int) s2.getWeightage();
        }
    };

    private void serTopCollectionDescription() {
        try {
            if (courseCollectionData.getDescription() != null) {
                String s1 = courseCollectionData.getDescription();
                if (s1.length() > 100) {
                    s1 = s1.substring(0, 100);
                    s1 = s1.substring(0, s1.lastIndexOf(" "));
                }
                Spanned contentText;
                contentText = Html.fromHtml(s1 + " <a href=\"" + "www.oustme.com" + "/\">" + "Read more" + "</a>");
                coursecollection_topcontenttext.setLinkTextColor(OustSdkTools.getColorBack(R.color.Blue));
                coursecollection_topcontenttext.setText(contentText);
                coursecollection_topcontenttext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(lesson_course_info.getVisibility()==View.GONE) {
                            coursecollection_topcontenttext.setText(courseCollectionData.getDescription());
                            showAllCollectionInfo();
                        }else {
                            hideAllCollectionInfo();
                        }
                    }
                });
            }
        } catch (Exception e) {
        }
    }


    private void hideAllCollectionInfo() {
        lesson_course_detail.bringToFront();
        downarrow_image.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_downarrow));
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(lesson_course_info, "translationY", 0, -lesson_course_info.getHeight());
        scaleDownY.setDuration(400);
        scaleDownY.setInterpolator(new AccelerateInterpolator());
        scaleDownY.start();
        scaleDownY.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                serTopCollectionDescription();
                lesson_course_info.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
    }

    private void showAllCollectionInfo() {
        lesson_course_detail.bringToFront();
        downarrow_image.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_uparrow));
        lesson_course_info.setVisibility(View.VISIBLE);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(lesson_course_info, "translationY", -lesson_course_info.getHeight(), 0);
        scaleDownY.setDuration(400);
        scaleDownY.setInterpolator(new DecelerateInterpolator());
        scaleDownY.start();
    }

    @Override
    public void onClick(View view) {
        int id= view.getId();
        if(id==R.id.lessonbuy_btn){
            if(courseCollectionData.isFreeCourse()){
                showLoader();
                sendFreeCourseEnrollApi();
            }else {
                paymentInfoFillPopup();
            }
        } else if(id==R.id.mainback_button){
            LessonsActivity.this.finish();
        }else if(id==R.id.lesson_closebtn){
            LessonsActivity.this.finish();
        }

    }

    //on back pressed check whether feature info is visible, if so hide it
    @Override
    public void onBackPressed() {
        if (lesson_course_info.getVisibility() == View.VISIBLE) {
            hideAllCollectionInfo();
        } else {
            super.onBackPressed();
        }
    }

    private float x1, x2;
    private float y1, y2;
    private final int MIN_DISTANCE = 20;
    private boolean tochedScreen = false;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action=event.getAction();
        if(action==MotionEvent.ACTION_DOWN) {
            tochedScreen = true;
            x1 = event.getX();
            y1 = event.getY();
        }else if(action==MotionEvent.ACTION_UP) {
            if (tochedScreen) {
                tochedScreen = false;
                x2 = event.getX();
                y2 = event.getY();
                float deltaX = x1 - x2;
                float deltaY = y1 - y2;
                if (deltaY > 0) {
                    if (deltaY > deltaX) {
                        if (deltaY > MIN_DISTANCE) {
                            if (lesson_course_info.getVisibility() == View.VISIBLE) {
                                hideAllCollectionInfo();
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    //--------------------------------------------------------------------------------------
    //free course enroll api
    public void sendFreeCourseEnrollApi(){
        String freecourseenroll_url = OustSdkApplication.getContext().getResources().getString(R.string.freecourseenroll_url);
        freecourseenroll_url = freecourseenroll_url.replace("{courseColnId}",(""+courseCollectionData.getCollectionId()));
        String param="{studentid:"+activeUser.getStudentid()+"}";
        try {
            freecourseenroll_url = HttpManager.getAbsoluteUrl(freecourseenroll_url);

            ApiCallUtils.doNetworkCall(Request.Method.PUT, freecourseenroll_url, OustSdkTools.getRequestObject(param), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    CommonResponse commonResponse = gson.fromJson(response.toString(), CommonResponse.class);
                    gotEnrollApiResponce(commonResponse);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    gotEnrollApiResponce(null);
                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, freecourseenroll_url, OustSdkTools.getRequestObject(param), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    CommonResponse commonResponse = gson.fromJson(response.toString(), CommonResponse.class);
                    gotEnrollApiResponce(commonResponse);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    gotEnrollApiResponce(null);
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
        } catch (Exception e) {}
    }

    public void gotEnrollApiResponce(CommonResponse commonResponse){
        hideLoader();
        OustSdkTools.handlePopup(commonResponse);
    }

    //-----------------------------------------------------
    private CourseDownloadReceiver receiver;

    @Override
    protected void onResume() {
        super.onResume();
        OustSdkTools.setSnackbarElements(lessonmain_layout, LessonsActivity.this);
        try {
            IntentFilter filter = new IntentFilter(CourseDownloadReceiver.PROCESS_RESPONSE);
            filter.addCategory(Intent.CATEGORY_DEFAULT);
            receiver = new CourseDownloadReceiver();
            registerReceiver(receiver, filter);
            if ((courseCollectionData != null) && (courseCollectionData.getCollectionId() > 0)) {
                setTopData();
                createList();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private String coursenoToRestartAfterPermission;

    public class CourseDownloadReceiver extends BroadcastReceiver {
        public static final String PROCESS_RESPONSE = "course_download";

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                coursenoToRestartAfterPermission = "";
                coursenoToRestartAfterPermission = intent.getStringExtra("courseId");
                if ((coursenoToRestartAfterPermission != null) && (!coursenoToRestartAfterPermission.isEmpty())) {
                    ActivityCompat.requestPermissions(LessonsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102);
                }
                lessonRowAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {
            if (requestCode == 102) {
                if (grantResults != null) {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        for (int i = 0; i < courseCollectionData.getCourseDataClassList().size(); i++) {
                            if (("" + courseCollectionData.getCourseDataClassList().get(i).getCourseId()).equalsIgnoreCase(coursenoToRestartAfterPermission)) {
                                Intent intent1 = new Intent(LessonsActivity.this, DownloadCourseService.class);
                                Gson gson = new Gson();
                                String str = gson.toJson(courseCollectionData.getCourseDataClassList().get(i));
                                intent1.putExtra("courseDataStr", str);
                                OustSdkApplication.getContext().startService(intent1);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onMainRowClick(int position) {
        couseDownloadData[position] = true;
        courseCollectionData.getCourseDataClassList().get(position).setDownloading(true);
    }

    @Override
    public void onIconClick(int position) {
        showCourseInfoPopup(courseCollectionData.getCourseDataClassList().get(position).getDescription());
    }

    private void showCourseInfoPopup(String description) {
        try {
            if ((description != null) && (!description.isEmpty())) {
                View popUpView = getLayoutInflater().inflate(R.layout.ask_infopopup, null);
                final PopupWindow likeOustPopup = OustSdkTools.createPopUp(popUpView);
                final Button btnYes = popUpView.findViewById(R.id.btnYes);
                final Button btnNo = popUpView.findViewById(R.id.btnNo);
                final ImageButton btnClose = popUpView.findViewById(R.id.btnClose);
                TextView popupContent = popUpView.findViewById(R.id.txtRejectChallengeMsg);
                LinearLayout infopopup_animlayout = popUpView.findViewById(R.id.infopopup_animlayout);

                popupContent.setText(description);
                btnYes.setText(getResources().getString(R.string.ok));
                btnNo.setVisibility(View.GONE);

                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        likeOustPopup.dismiss();
                    }
                });
                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        likeOustPopup.dismiss();
                    }
                });
                OustSdkTools.popupAppearEffect(infopopup_animlayout);
            }
        } catch (Exception e) {
        }
    }
    //---------------------------------------------------------------------------------------
//cceaveneu payment methodes

    public void paymentInfoFillPopup() {
        try {
            if (!OustSdkTools.checkInternetStatus()) {
                return;
            }
            View popUpView = getLayoutInflater().inflate(R.layout.paymentinfo_popup, null);
            final PopupWindow paymentinfo_popup = OustSdkTools.createPopUp(popUpView);

            final Button btnOK = popUpView.findViewById(R.id.otp_okbtn);
            final ImageButton payment_popupclose_btn = popUpView.findViewById(R.id.payment_popupclose_btn);
            TextView payment_popuptitle= popUpView.findViewById(R.id.payment_popuptitle);
            final EditText edittext_name= popUpView.findViewById(R.id.edittext_name);
            final EditText edittext_email = popUpView.findViewById(R.id.edittext_email);
            final EditText edittext_mobile= popUpView.findViewById(R.id.edittext_mobile);
            final EditText edittext_address= popUpView.findViewById(R.id.edittext_address);
            final EditText edittext_city= popUpView.findViewById(R.id.edittext_city);
            final EditText edittext_state= popUpView.findViewById(R.id.edittext_state);
            final EditText edittext_country= popUpView.findViewById(R.id.edittext_country);
            final EditText edittext_zip= popUpView.findViewById(R.id.edittext_zip);
            if(OustAppState.getInstance().getActiveUser()!=null) {
                if(OustPreferences.get("payment_useremail")!=null){
                    edittext_email.setText(OustPreferences.get("payment_useremail"));
                }else if (OustAppState.getInstance().getActiveUser().getEmail() != null) {
                    edittext_email.setText(OustAppState.getInstance().getActiveUser().getEmail());
                }
                if(OustPreferences.get("payment_username")!=null){
                    edittext_name.setText(OustPreferences.get("payment_username"));
                }else if (OustAppState.getInstance().getActiveUser().getUserDisplayName() != null) {
                    edittext_name.setText(OustAppState.getInstance().getActiveUser().getUserDisplayName());
                }
                if(OustPreferences.get("payment_usermobile")!=null){
                    edittext_mobile.setText(OustPreferences.get("payment_usermobile"));
                }else if (OustAppState.getInstance().getActiveUser().getUserMobile() > 100) {
                    edittext_mobile.setText(("" + OustAppState.getInstance().getActiveUser().getUserMobile()));
                }
            }
            if(OustPreferences.get("payment_useraddress")!=null){
                edittext_address.setText(OustPreferences.get("payment_useraddress"));
            }
            if(OustPreferences.get("payment_usercity")!=null){
                edittext_city.setText(OustPreferences.get("payment_usercity"));
            }
            if(OustPreferences.get("payment_userstate")!=null){
                edittext_state.setText(OustPreferences.get("payment_userstate"));
            }
            if(OustPreferences.get("payment_usercountry")!=null){
                edittext_country.setText(OustPreferences.get("payment_usercountry"));
            }
            if(OustPreferences.get("payment_userzip")!=null){
                edittext_zip.setText(OustPreferences.get("payment_userzip"));
            }

            payment_popuptitle.setText(getResources().getString(R.string.enter_user_details));
            if (OustAppState.getInstance().getActiveUser().getEmail() != null) {
                edittext_email.setText(OustAppState.getInstance().getActiveUser().getEmail());
            }
            InputMethodManager inputMethodManager=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            edittext_name.requestFocus();
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OustSdkTools.oustTouchEffect(view,200);
                    if((edittext_name.getText().toString().isEmpty())){
                        OustSdkTools.showToast("Enter valid name");
                        return;
                    }
                    OustPreferences.save("payment_username",edittext_name.getText().toString());
                    if(!isValidEmail(edittext_email.getText().toString().trim())){
                        OustSdkTools.showToast(getResources().getString(R.string.enter_email_id));
                        return;
                    }
                    OustPreferences.save("payment_useremail",edittext_email.getText().toString());
                    if((edittext_mobile.getText().toString().isEmpty())){
                        OustSdkTools.showToast(getResources().getString(R.string.enter_mobile_number));
                        return;
                    }
                    OustPreferences.save("payment_usermobile",edittext_mobile.getText().toString());
                    OustPreferences.save("payment_useraddress",edittext_address.getText().toString());
                    OustPreferences.save("payment_usercountry",edittext_country.getText().toString());
                    OustPreferences.save("payment_userstate",edittext_state.getText().toString());
                    OustPreferences.save("payment_usercity",edittext_city.getText().toString());
                    OustPreferences.save("payment_userzip",edittext_zip.getText().toString());

                    if((edittext_address.getText().toString().isEmpty())||(edittext_city.getText().toString().isEmpty())||
                            (edittext_state.getText().toString().isEmpty())||(edittext_country.getText().toString().isEmpty())||(edittext_zip.getText().toString().isEmpty())){
                        OustSdkTools.showToast("Enter valid details");
                        return;
                    }
                    CourseBuyRequest courseBuyRequest = new CourseBuyRequest();
                    courseBuyRequest.setBillingName(edittext_name.getText().toString());
                    courseBuyRequest.setBillingEmail(edittext_email.getText().toString());
                    courseBuyRequest.setBillingAddress(edittext_address.getText().toString());

                    courseBuyRequest.setBillingCountry(edittext_country.getText().toString());
                    courseBuyRequest.setBillingState(edittext_state.getText().toString());
                    courseBuyRequest.setBillingCity(edittext_city.getText().toString());
                    courseBuyRequest.setBillingZip(edittext_zip.getText().toString());
                    courseBuyRequest.setBillingMobile(edittext_mobile.getText().toString());

                    paymentinfo_popup.dismiss();
                    clickOnBuyLessonButton(courseBuyRequest);
                }
            });

            payment_popupclose_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    paymentinfo_popup.dismiss();
                }
            });
            LinearLayout certificateanim_layout= popUpView.findViewById(R.id.certificateanim_layout);
            OustSdkTools.popupAppearEffect(certificateanim_layout);
        }catch (Exception e){
        }
    }

    public  boolean isValidEmail(CharSequence target) {
        Pattern EMAIL_ADDRESS
                = Pattern.compile(
                "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                        "\\@" +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                        "(" +
                        "\\." +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                        ")+");
        return EMAIL_ADDRESS.matcher(target).matches();
    }




    private String ACCESS_CODE = "AVZC69EC57AO21CZOA";//AVRF00EA85BQ75FRQB
    private String MERCHANT_ID = "122502";//
    private String ORDER_ID = "";
    private String CURRENCY = "INR";
    private String AMOUNT = "50";
    private String REDIRECT_URL = "http://52.52.207.220:8080/oust-ccavenue-gateway-server/rest/services/do/mobile/transStatus";//
    private String CANCEL_URL = "http://122.182.6.216/merchant/ccavResponseHandler.jsp";
    private String RSA_KEY_URL = "";
    String encVal;
    //http://52.8.3.12:8080/bringup/rest/services/payment/transStatus
//121453
    public void clickOnBuyLessonButton(CourseBuyRequest courseBuyRequest) {
        if (!OustSdkTools.checkInternetStatus()) {
            return;
        }
        showLoader();
        Integer randomNum = ServiceUtility.randInt(0, 9999999);
        ORDER_ID = randomNum.toString();
        AMOUNT = ("" + courseCollectionData.getPrise());
        courseBuyRequest.setStudentid(activeUser.getStudentid());
        courseBuyRequest.setTenantId(OustPreferences.get("tanentid"));
        courseBuyRequest.setPaymentAmount(AMOUNT);
        courseBuyRequest.setContentId("" + courseCollectionData.getCollectionId());
        courseBuyRequest.setOrderId(ORDER_ID);
        courseBuyRequest.setAccessCode(ACCESS_CODE);
        courseBuyRequest.setPaymentCategory(PaymentCategory.COURSE_COLN_PURCHASE);
        courseBuyRequest.setContentType(ContentType.COURSE_COLN);
        payment_webview.setVisibility(View.VISIBLE);
        showLoader();
        sendOrderRequest(courseBuyRequest);
//        new RenderView().execute();
    }

    public void showLoader() {
        try {
            ObjectAnimator imageViewObjectAnimator = ObjectAnimator.ofFloat(lpmain_loader, "rotation", 0f, 360f);
            imageViewObjectAnimator.setDuration(1500); // miliseconds
            imageViewObjectAnimator.setRepeatCount(5);
            imageViewObjectAnimator.start();
            mainloader_back.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void hideWebLoader() {
        try {
            mainloader_back.setVisibility(View.GONE);
            lpmain_loader.setAnimation(null);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showPaymentSuccessMethode() {
        if (payment_webview.getVisibility() == View.VISIBLE) {
            payment_webview.setVisibility(View.GONE);
            showLoader();
            //OustSdkTools.showToast("payment was successfull");
        }
    }
    //make api call to our backend

    public void sendOrderRequest(CourseBuyRequest courseBuyRequest) {
        String paymentrequest_url = "http://52.52.207.220:8080/oust-ccavenue-gateway-server/rest/services/do/mobile/rsaKey";
        Gson gson = new GsonBuilder().create();
        String jsonParams = gson.toJson(courseBuyRequest);
        final PaymentGetRSAMobileResponseData[] paymentGetRSAMobileResponseDatas = {null};
        try {

            ApiCallUtils.doNetworkCall(Request.Method.POST, paymentrequest_url, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
//                    String encryptedStr=response.toString();
//                    gotPaymentResponce(encryptedStr,response);
                    paymentGetRSAMobileResponseDatas[0] = OustSdkTools.getInstance().getPaymentGetRSAMobileResponseData(response.toString());
                    gotPaymentResponce(paymentGetRSAMobileResponseDatas[0]);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    gotPaymentResponce(null);
                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, paymentrequest_url, OustSdkTools.getRequestObject(jsonParams), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
//                    String encryptedStr=response.toString();
//                    gotPaymentResponce(encryptedStr,response);
                    paymentGetRSAMobileResponseDatas[0] = OustSdkTools.getInstance().getPaymentGetRSAMobileResponseData(response.toString());
                    gotPaymentResponce(paymentGetRSAMobileResponseDatas[0]);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    gotPaymentResponce(null);
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
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void gotPaymentResponce(PaymentGetRSAMobileResponseData paymentGetRSAMobileResponseData){
        if(paymentGetRSAMobileResponseData!=null&&(paymentGetRSAMobileResponseData.isSuccess())){
            openWebWithRsaKey(paymentGetRSAMobileResponseData.getRsaKey(),paymentGetRSAMobileResponseData.getParams(),paymentGetRSAMobileResponseData.getTransactionUrl());
        }
    }

    public void openWebWithRsaKey(String rsaKey, final Map<String,String> reqParams,String transactionUrl) {
        hideWebLoader();
        if ((rsaKey != null) && (!rsaKey.isEmpty())) {
            try {
                rsaKey = rsaKey.trim();
                System.out.println(rsaKey);
                if (!ServiceUtility.chkNull(rsaKey).equals("")
                        && ServiceUtility.chkNull(rsaKey).toString().indexOf("ERROR") == -1) {
                    StringBuffer vEncVal = new StringBuffer();
                    vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.AMOUNT, reqParams.get(AvenuesParams.AMOUNT)));
                    vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.CURRENCY, reqParams.get(AvenuesParams.CURRENCY)));
                    encVal = RSAUtility.encrypt(vEncVal.substring(0, vEncVal.length() - 1), rsaKey);
                }
                payment_webview.getSettings().setJavaScriptEnabled(true);
                //payment_webview.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
                payment_webview.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(payment_webview, url);
                        if (url.indexOf("/ccavResponseHandler.jsp") != -1) {
                            payment_webview.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                        }
                        if (url.equalsIgnoreCase(reqParams.get(AvenuesParams.REDIRECT_URL))) {
                            showPaymentSuccessMethode();
                        }
                    }

                    @Override
                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                        Toast.makeText(getApplicationContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
                    }
                });

			/* An instance of this class will be registered as a JavaScript interface */
                StringBuffer params = new StringBuffer();
                params.append(ServiceUtility.addToPostParams(AvenuesParams.ACCESS_CODE, reqParams.get(AvenuesParams.ACCESS_CODE)));
                params.append(ServiceUtility.addToPostParams(AvenuesParams.MERCHANT_ID, reqParams.get(AvenuesParams.MERCHANT_ID)));
                params.append(ServiceUtility.addToPostParams(AvenuesParams.ORDER_ID, reqParams.get(AvenuesParams.ORDER_ID)));
                params.append(ServiceUtility.addToPostParams(AvenuesParams.REDIRECT_URL, reqParams.get(AvenuesParams.REDIRECT_URL)));
                params.append(ServiceUtility.addToPostParams(AvenuesParams.CANCEL_URL, reqParams.get(AvenuesParams.CANCEL_URL)));
                if(reqParams.get(AvenuesParams.INTEGRATION_TYPE)!=null) {
                    params.append(ServiceUtility.addToPostParams(AvenuesParams.INTEGRATION_TYPE, reqParams.get(AvenuesParams.INTEGRATION_TYPE)));
                }
                if(reqParams.get(AvenuesParams.BILLING_NAME)!=null) {
                    params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_NAME, reqParams.get(AvenuesParams.BILLING_NAME)));
                }
                if(reqParams.get(AvenuesParams.BILLING_ADDRESS)!=null) {
                    params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_ADDRESS, reqParams.get(AvenuesParams.BILLING_ADDRESS)));
                }
                if(reqParams.get(AvenuesParams.BILLING_COUNTRY)!=null) {
                    params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_COUNTRY, reqParams.get(AvenuesParams.BILLING_COUNTRY)));
                }
                if(reqParams.get(AvenuesParams.BILLING_TEL)!=null) {
                    params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_TEL, reqParams.get(AvenuesParams.BILLING_TEL)));
                }
                if(reqParams.get(AvenuesParams.BILLING_STATE)!=null) {
                    params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_STATE, reqParams.get(AvenuesParams.BILLING_STATE)));
                }
                if(reqParams.get(AvenuesParams.BILLING_ZIP)!=null) {
                    params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_ZIP, reqParams.get(AvenuesParams.BILLING_ZIP)));
                }
                if(reqParams.get(AvenuesParams.BILLING_MAIL)!=null) {
                    params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_MAIL, reqParams.get(AvenuesParams.BILLING_MAIL)));
                }
                if(reqParams.get(AvenuesParams.BILLING_CITY)!=null) {
                    params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_CITY, reqParams.get(AvenuesParams.BILLING_CITY)));
                }

                String encStr = URLEncoder.encode(encVal);
                params.append(ServiceUtility.addToPostParams(AvenuesParams.ENC_VAL, encStr));

                String vPostParams = params.substring(0, params.length() - 1);
                payment_webview.postUrl(transactionUrl, EncodingUtils.getBytes(vPostParams, "UTF-8"));
            } catch (Exception e) {
                OustSdkTools.showToast(getResources().getString(R.string.exception_text));
                payment_webview.setVisibility(View.GONE);
            }
        } else {
            payment_webview.setVisibility(View.GONE);
        }
    }



    public void gotPaymentResponce(String encryptedStr, JSONObject jsonObject) {
        hideWebLoader();
        if ((encryptedStr != null) && (!encryptedStr.isEmpty())) {
            try {
                encryptedStr = getEncriptedStr(jsonObject);
                System.out.println(encryptedStr);
                if (!ServiceUtility.chkNull(encryptedStr).equals("")
                        && ServiceUtility.chkNull(encryptedStr).toString().indexOf("ERROR") == -1) {
                    StringBuffer vEncVal = new StringBuffer();
                    vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.AMOUNT, AMOUNT));
                    vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.CURRENCY, CURRENCY));
                    encVal = RSAUtility.encrypt(vEncVal.substring(0, vEncVal.length() - 1), encryptedStr);
                }
                payment_webview.getSettings().setJavaScriptEnabled(true);
                //payment_webview.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
                payment_webview.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(payment_webview, url);
                        if (url.indexOf("/ccavResponseHandler.jsp") != -1) {
                            payment_webview.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                        }
                        if (url.equalsIgnoreCase(REDIRECT_URL)) {
                            showPaymentSuccessMethode();
                        }
                    }

                    @Override
                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                        Toast.makeText(getApplicationContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
                    }
                });

			/* An instance of this class will be registered as a JavaScript interface */
                StringBuffer params = new StringBuffer();
                params.append(ServiceUtility.addToPostParams(AvenuesParams.ACCESS_CODE, ACCESS_CODE));
                params.append(ServiceUtility.addToPostParams(AvenuesParams.MERCHANT_ID, MERCHANT_ID));
                params.append(ServiceUtility.addToPostParams(AvenuesParams.ORDER_ID, ORDER_ID));
                params.append(ServiceUtility.addToPostParams(AvenuesParams.REDIRECT_URL, REDIRECT_URL));
                params.append(ServiceUtility.addToPostParams(AvenuesParams.CANCEL_URL, CANCEL_URL));
                String encStr = URLEncoder.encode(encVal);
                params.append(ServiceUtility.addToPostParams(AvenuesParams.ENC_VAL, encStr));

                String vPostParams = params.substring(0, params.length() - 1);
             //   payment_webview.postUrl(Constants.TRANS_URL, EncodingUtils.getBytes(vPostParams, "UTF-8"));
            } catch (Exception e) {
                OustSdkTools.showToast(getResources().getString(R.string.exception_text));
                payment_webview.setVisibility(View.GONE);
            }
        } else {
            payment_webview.setVisibility(View.GONE);
        }
    }

    private String getEncriptedStr(JSONObject encryptedStr) {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("rsaKey",encryptedStr.optString("rsaKey"));
            jsonObject.put("redirectUrl",encryptedStr.optString("redirectUrl"));
            jsonObject.put("cancelUrl",encryptedStr.optString("cancelUrl"));
            jsonObject.put("transactionUrl",encryptedStr.optString("transactionUrl"));
            JSONObject jsonObject1=encryptedStr.optJSONObject("params");
            jsonObject.put("amount",jsonObject1.optString("amount"));
            jsonObject.put("access_code",jsonObject1.optString("access_code"));
            jsonObject.put("billing_country",jsonObject1.optString("billing_country"));
            jsonObject.put("billing_address",jsonObject1.optString("billing_address"));
            jsonObject.put("merchant_id",jsonObject1.optString("merchant_id"));
            jsonObject.put("integration_type",jsonObject1.optString("integration_type"));
            jsonObject.put("billing_name",jsonObject1.optString("billing_name"));
            jsonObject.put("billing_city",jsonObject1.optString("billing_city"));
            jsonObject.put("billing_tel",jsonObject1.optString("billing_tel"));
            jsonObject.put("billing_state",jsonObject1.optString("billing_state"));
            jsonObject.put("billing_email",jsonObject1.optString("billing_email"));
            jsonObject.put("billing_zip",jsonObject1.optString("billing_zip"));
            jsonObject.put("currency",jsonObject1.optString("currency"));
            jsonObject.put("order_id",jsonObject1.optString("order_id"));
            jsonObject.put("cancel_url",jsonObject1.optString("cancel_url"));
            jsonObject.put("redirect_url",jsonObject1.optString("redirect_url"));
        } catch (JSONException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return jsonObject.toString().trim();
    }


}
