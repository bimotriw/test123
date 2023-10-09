package com.oustme.oustsdk.activity.common;

import android.Manifest;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.adapter.common.ReportProblemRowAdapter;
import com.oustme.oustsdk.customviews.TryRippleView;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.request.ReportAProblemRequest;
import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustGATools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by shilpysamaddar on 14/07/17.
 */

public class ReportProblemListActivity extends AppCompatActivity implements View.OnClickListener{

    private ExpandableListView rerport_prblm_exLv;
    private ReportProblemRowAdapter adapter;
    private Toolbar toolbar;
    private AppBarLayout report_prblm_appbar;
    private RelativeLayout reportprob_listlayout,attachment_layout,reportprob_mainlayout,loader_layout,formrow_submit_btnlayout;
    private TextView question_text,rp_attachment_text;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView  reportprob_screenimag,rm_image;
    private ProgressBar loader;
    private ReportAProblemRequest reportAProblemRequest=new ReportAProblemRequest();
    private EditText rp_commentedittext;
    private LinearLayout rp_mainlayout;
    private TryRippleView formrow_submit_ripple;
    private Button formrow_submit_btn;

    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    protected void onStart() {

        super.onStart();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_reportproblm_list);
        initViews();
        setToolBarColor();
        showLoader();
        getDataFormFirebase();
        OustSdkTools.setSnackbarElements(rp_mainlayout,ReportProblemListActivity.this);
//        OustGATools.getInstance().reportPageViewToGoogle(ReportProblemListActivity.this,"Report Problem Page");

    }

    private void showLoader(){
        try {
            swipeRefreshLayout.setColorSchemeColors(OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen), OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen));
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
        }catch (Exception e){}
    }

    private void hideLoader(){
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setVisibility(View.GONE);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==android.R.id.home){
            if (reportprob_mainlayout.getVisibility()==View.VISIBLE){
                reportprob_listlayout.setVisibility(View.VISIBLE);
                reportprob_mainlayout.setVisibility(View.GONE);
                rp_commentedittext.setText(null);
            } else {
                onBackPressed();
            }
        }else{
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void initViews() {
        rerport_prblm_exLv= findViewById(R.id.rerport_prblm_exLv);
        toolbar = findViewById(R.id.tabanim_toolbar);
        report_prblm_appbar = findViewById(R.id.report_prblm_appbar);
        question_text= findViewById(R.id.question_text);
        attachment_layout= findViewById(R.id.attachment_layout);
        attachment_layout.setOnClickListener(this);
        rp_attachment_text= findViewById(R.id.rp_attachment_text);

        reportprob_listlayout= findViewById(R.id.reportprob_listlayout);
        reportprob_mainlayout= findViewById(R.id.reportprob_mainlayout);
        swipeRefreshLayout= findViewById(R.id.swipe_refresh_layout);
        loader_layout= findViewById(R.id.loader_layout);
        formrow_submit_btnlayout= findViewById(R.id.formrow_submit_btnlayout);
        formrow_submit_btnlayout.setOnClickListener(this);

        reportprob_screenimag= findViewById(R.id.reportprob_screenimag);
        loader= findViewById(R.id.loader);

        rp_commentedittext= findViewById(R.id.rp_commentedittext);
        formrow_submit_btn= findViewById(R.id.formrow_submit_btn);
        formrow_submit_btn.setOnClickListener(this);
        rp_mainlayout= findViewById(R.id.rp_mainlayout);
        formrow_submit_ripple= findViewById(R.id.formrow_submit_ripple);
        formrow_submit_ripple.setOnClickListener(this);

        question_text.setText(getResources().getString(R.string.your_ques));
        rp_commentedittext.setHint(getResources().getString(R.string.explain_briefly));
        rp_attachment_text.setText(getResources().getString(R.string.attach_screenshot));
        formrow_submit_btn.setText(getResources().getString(R.string.submit));

        rm_image= findViewById(R.id.rm_image);
        OustSdkTools.setImage(rm_image,getResources().getString(R.string.rm_imagetype));
    }

    private void setToolBarColor(){
        try{
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(false);
            getSupportActionBar().setTitle("");
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            TextView titleTextView= toolbar.findViewById(R.id.title);
            titleTextView.setText(getResources().getString(R.string.report_problem));
            String toolbarColorCode= OustPreferences.get("toolbarColorCode");
            if((toolbarColorCode!=null)&&(!toolbarColorCode.isEmpty())){
                report_prblm_appbar.setBackgroundColor(Color.parseColor(toolbarColorCode));
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setRows() {
        try {
            hideLoader();
            if ((listDataHeader.size() > 0) && (listDataChild.size() > 0)) {
                adapter = new ReportProblemRowAdapter(this, listDataHeader, listDataChild);
                // setting list adapter
                rerport_prblm_exLv.setAdapter(adapter);

                // Listview Group click listener
                rerport_prblm_exLv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                    @Override
                    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                        return false;
                    }
                });

                // Listview Group expanded listener
                rerport_prblm_exLv.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

                    @Override
                    public void onGroupExpand(int groupPosition) {
                    }
                });

                // Listview Group collasped listener
                rerport_prblm_exLv.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
                    @Override
                    public void onGroupCollapse(int groupPosition) {
                    }
                });

                // Listview on child click listener
                rerport_prblm_exLv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                    @Override
                    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                        // TODO Auto-generated method stub
                        String string = listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
                        showReportProbUI(string);
//                        Intent intent = new Intent(ReportProblemListActivity.this, ReportProblemActivity.class);
//                        startActivity(intent);
                        return false;
                    }
                });
            }
        }catch (Exception e){}
    }

    private void getDataFormFirebase(){
        try{
            final String message="/system/reportProblemData";
            ValueEventListener myCourceListener=new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        listDataHeader = new ArrayList<String>();
                        listDataChild = new HashMap<String, List<String>>();
                        if(dataSnapshot.getValue()!=null) {
                            List<Object> reportProblemDataList =(List<Object>) dataSnapshot.getValue();
                            if(reportProblemDataList!=null){
                                for(int i=0;i<reportProblemDataList.size();i++){
                                    Map<String,Object> subMap=(Map<String,Object>)reportProblemDataList.get(i);
                                    if((subMap!=null)&&(subMap.get("name")!=null)){
                                        listDataHeader.add((String) subMap.get("name"));
                                        List<Object> subList =(List<Object>) subMap.get("subList");
                                        List<String> pageSubList = new ArrayList<String>();
                                        for(int j=0;j<subList.size();j++){
                                            if(subList.get(j)!=null){
                                                pageSubList.add((String) subList.get(j));
                                            }
                                        }
                                        listDataChild.put((String) subMap.get("name"),pageSubList);
                                    }
                                }
                            }
                            setRows();
                        }else {
                            prepareLists();
                        }
                    }catch (Exception e){
                        //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
                    }
                }
                @Override
                public void onCancelled(DatabaseError error) {}
            };
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(myCourceListener);
        }catch (Exception e){
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }



    }

    private void prepareLists() {
        // Adding child data
        listDataHeader.add("Login Page");
        listDataHeader.add("Home Page");
        listDataHeader.add("Course");
        listDataHeader.add("Assessment");
//        listDataHeader.add("Setting");

        // Adding child data
        List<String> loginPage = new ArrayList<String>();
        loginPage.add("Login with Mobile");
        loginPage.add("Login with UserName & Password");
        loginPage.add("Forgot Password");
        loginPage.add("Related to OTP");

        // Adding child data
        List<String> landingPage = new ArrayList<String>();
        landingPage.add("Alerts/News Feeds");
        landingPage.add("Leader Board");
        landingPage.add("Favorites");
        landingPage.add("Side Drawer");
        landingPage.add("Loading Course/Course Collection");
        landingPage.add("Analytics");
        landingPage.add("Learn/Play switch");
        landingPage.add("Coins");
        landingPage.add("Search Bar");

        // Adding child data
        List<String> course = new ArrayList<String>();
        course.add("Learning Map");
        course.add("Level download");
        course.add("Content");
        course.add("Learn Cards");
        course.add("Question Cards");
        course.add("Card's Content");
        course.add("Result Page");
        course.add("Certifiacte");
        course.add("Leaderboard");

        // Adding child data
        List<String> assessment = new ArrayList<String>();
        assessment.add("Assessment Start Page");
        assessment.add("Questions");
        assessment.add("Result Page");
        assessment.add("View Answers");

        listDataChild.put(listDataHeader.get(0), loginPage); // Header, Child data
        listDataChild.put(listDataHeader.get(1), landingPage);
        listDataChild.put(listDataHeader.get(2), course);
        listDataChild.put(listDataHeader.get(3), assessment);
        setRows();
    }
//    @Override
//    public void onBackPressed() {
//        if (reportprob_mainlayout.getVisibility()==View.VISIBLE){
//            reportprob_listlayout.setVisibility(View.VISIBLE);
//            reportprob_mainlayout.setVisibility(View.GONE);
//            rp_commentedittext.setText(null);
//            return;
//        } else {
//            ReportProblemListActivity.this.overridePendingTransition(R.anim.enter, R.anim.exit);
//        }
//        super.onBackPressed();
//    }


    @Override
    public void onBackPressed() {
        if(loader_layout.getVisibility()==View.VISIBLE) {
            loader_layout.setVisibility(View.VISIBLE);
            return;
        }
        else
        {
            super.onBackPressed();
        }
    }



    //=============================================================================================

    private final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    @Override
    public void onClick(View view) {
        int id=view.getId();
        if(id==R.id.attachment_layout){
//            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            startActivityForResult(i, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
            checkForStoragePermission();
        }else if(id==R.id.formrow_submit_btnlayout){
            sendReportData();
        }else if(id==R.id.formrow_submit_ripple){
            sendReportData();
        }
    }


    final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE=1;
    public void checkForStoragePermission(){
        if (ContextCompat.checkSelfPermission(ReportProblemListActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
            showAddPicOption();
        }else {
            ActivityCompat.requestPermissions(ReportProblemListActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        try {
            if(requestCode==MY_PERMISSIONS_REQUEST_WRITE_STORAGE){
                if(grantResults!=null) {
                    if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                        showAddPicOption();
                    }
                }
            }
        }catch (Exception e){}
    }

    public void showAddPicOption(){
        try{
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    Uri selectedImageUri = data.getData();
                    String[] projection = {MediaStore.MediaColumns.DATA};
                    CursorLoader cursorLoader = new CursorLoader(this, selectedImageUri, projection, null, null, null);
                    Cursor cursor = cursorLoader.loadInBackground();
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                    cursor.moveToFirst();
                    String selectedImagePath = cursor.getString(column_index);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(selectedImagePath, options);
                    final int REQUIRED_SIZE = 1000;
                    int scale = 1;
                    while (options.outWidth / scale / 2 >= REQUIRED_SIZE && options.outHeight / scale / 2 >= REQUIRED_SIZE) scale *= 2;
                    options.inSampleSize = scale;
                    options.inJustDecodeBounds = false;
                    Bitmap bm = BitmapFactory.decodeFile(selectedImagePath, options);
                    bitMapToString(bm);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }
    private String imageString;
    List<String> screenshots=new ArrayList<>();
    public void bitMapToString(Bitmap bitmap){
        try {
            reportprob_screenimag.setImageBitmap(bitmap);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            imageString = Base64.encodeToString(b, Base64.DEFAULT);

            screenshots.add(imageString);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    private String reportProbStr;
    private void showReportProbUI(String str){
        reportProbStr=str;
        reportprob_listlayout.setVisibility(View.GONE);
        reportprob_mainlayout.setVisibility(View.VISIBLE);
        question_text.setText(str);

        reportAProblemRequest.setProblemScreen(str);
//        TextView titleTextView=(TextView) toolbar.findViewById(R.id.title);
//        titleTextView.setText("Feedback");

    }
    public void showMainLoader(){
        try {
            loader_layout.setVisibility(View.VISIBLE);
            Animation rotateAnim = AnimationUtils.loadAnimation(ReportProblemListActivity.this, R.anim.rotate_anim);
            loader.startAnimation(rotateAnim);
            loader_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {}
            });
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void hideMainLoader(){
        try {
            loader_layout.setVisibility(View.GONE);
            loader.setAnimation(null);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
    public boolean canCancelAnimation() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    //    Rest call run on background thread
    public void sendReportData(){
        try {
            if (!OustSdkTools.checkInternetStatus()) {
                return;
            } else {
                if ((rp_commentedittext.getText()!= null) && (!rp_commentedittext.getText().toString().trim().isEmpty())) {
                    reportAProblemRequest.setProblemDesc(rp_commentedittext.getText().toString());
                }
                if (screenshots != null && screenshots.size() > 0) {
                    reportAProblemRequest.setScreenShots(screenshots);
                }
                if ((rp_commentedittext.getText() != null && (!rp_commentedittext.getText().toString().trim().isEmpty())) || (screenshots != null && screenshots.size() > 0)) {

                    showMainLoader();


//                    AsyncTask.execute(new Runnable() {
//                        @Override
//                        public void run() {
//                            OustRestClient oustRestClient = new OustRestClient();
//                            final CommonResponse commonResponse = oustRestClient.sendBugReport(reportAProblemRequest);
//                            Handler mainHandler = new Handler(OustApplication.getContext().getMainLooper());
//                            Runnable myRunnable = new Runnable() {
//                                @Override
//                                public void run() {
//                                    gotResponse(commonResponse);
//                                }
//                            };
//                            mainHandler.post(myRunnable);
//                        }
//                    });
                    Gson gson = new GsonBuilder().create();
                    String jsonParams = gson.toJson(reportAProblemRequest);
                    JSONObject j=(OustSdkTools.getRequestObject(jsonParams));
                    String reportbug_url = getResources().getString(R.string.bgreport_url);
                    reportbug_url= HttpManager.getAbsoluteUrl(reportbug_url);

                    ApiCallUtils.doNetworkCall(Request.Method.PUT, reportbug_url, j, new ApiCallUtils.NetworkCallback() {
                        @Override
                        public void onResponse(JSONObject response) {
                            gotResponse(OustSdkTools.getCommonResponse(response.toString()));
                            //gotCertificatetomailResponce(sendCertificateRequest.getEmailid(),courseDataClass,commonResponses[0],isComingFormCourseCompletePopup);
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            hideMainLoader();
                        }
                    });


                    /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, reportbug_url, j, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            gotResponse(OustSdkTools.getCommonResponse(response.toString()));
                            //gotCertificatetomailResponce(sendCertificateRequest.getEmailid(),courseDataClass,commonResponses[0],isComingFormCourseCompletePopup);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            hideMainLoader();
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


                } else {
                    OustSdkTools.showToast(getResources().getString(R.string.report_required_message));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void gotResponse(final CommonResponse commonResponse){
        try{
            if ((commonResponse != null) && (commonResponse.isSuccess())) {
                OustSdkTools.showToast(getResources().getString(R.string.problem_reported));
                hideMainLoader();
                hideLoader();
                finish();
            } else {
                hideMainLoader();
                hideLoader();
                OustSdkTools.handlePopup(commonResponse);
            }
            hideMainLoader();
            reportprob_screenimag.setImageBitmap(null);
            rp_commentedittext.setText(null);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onDestroy() {
        imageString=null;
        super.onDestroy();
    }
}
