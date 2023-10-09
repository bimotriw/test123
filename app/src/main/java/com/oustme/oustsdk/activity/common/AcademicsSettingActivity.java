package com.oustme.oustsdk.activity.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.customviews.CircleImageView;
import com.oustme.oustsdk.presenter.common.AcademicsSettingActivityPresenter;
import com.oustme.oustsdk.request.UserSettingRequest;
import com.oustme.oustsdk.response.common.Popup;
import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustGATools;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;


/**
 * Created by shilpysamaddar on 22/03/17.
 */

public class AcademicsSettingActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView txtTitle,userName,userschool_Label,usergrade_Label;
    private CircleImageView imgAvatarButton;
    private EditText userSchoolCollege;
    private AutoCompleteTextView userGrade;
    private Button saveBtn;
    private ProgressBar academicSetting_progressbar;
    private LinearLayout academicsmain_layoyt,backArrow_back;
    private ImageButton backArrowImgBtn;

    private AcademicsSettingActivityPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            OustSdkTools.setLocale(AcademicsSettingActivity.this);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        setContentView(R.layout.user_acedamics);
        initViews();
        initUserAcademics();
//        OustGATools.getInstance().reportPageViewToGoogle(AcademicsSettingActivity.this,"Academics Setting Page");
    }
    
    private void initViews(){
        txtTitle= findViewById(R.id.txtTitle);
        imgAvatarButton= findViewById(R.id.imgAvatarButton);
        userName= findViewById(R.id.username_Txt);
        userSchoolCollege= findViewById(R.id.acdstng_userSchoolCollege);
        userGrade= findViewById(R.id.acdstng_userGrade);
        saveBtn= findViewById(R.id.saveBtn);
        userschool_Label= findViewById(R.id.userschool_Label);
        usergrade_Label= findViewById(R.id.usergrade_Label);
        academicSetting_progressbar= findViewById(R.id.academicSetting_progressbar);
        academicsmain_layoyt= findViewById(R.id.academicsmain_layoyt);
        backArrowImgBtn= findViewById(R.id.backArrowImgBtn);
        backArrow_back= findViewById(R.id.backArrow_back);

        backArrowImgBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        backArrow_back.setOnClickListener(this);
    }

    public void initUserAcademics() {
        OustSdkTools.setSnackbarElements(academicsmain_layoyt,AcademicsSettingActivity.this);
        presenter=new AcademicsSettingActivityPresenter(this);
        setFont();
    }

    public void setUserAvatar(String avatar){
        Picasso.get().load(avatar).into(imgAvatarButton);
    }
    public void setSavedAvatar(){
        if(OustSdkTools.tempProfile!=null) {
            imgAvatarButton.setImageBitmap(OustSdkTools.tempProfile);
        }
    }

    public void setUserName(String name){
        userName.setText(name);
    }
    public void setUserSchool(String school){
        userSchoolCollege.setText(school);
    }

    public void setGradeList(String currentGrade){
        try {
            List<String> grades = (Arrays.asList(getResources().getStringArray(R.array.settinggrade)));
            presenter.setGradeList(grades);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simplecity_name, grades);
            userGrade.setThreshold(0);
            userGrade.setAdapter(adapter);
            if (currentGrade != null) {
                if (grades.contains(currentGrade)) {
                    userGrade.setText(currentGrade);
                }
            }
        }catch (Exception e){}
    }

    private void setFont(){
        try {
            userGrade.setTypeface(OustSdkTools.getAvenirLTStdMedium());
            txtTitle.setText(getResources().getString(R.string.qualifications));
            userschool_Label.setText(getResources().getString(R.string.school_college));
            usergrade_Label.setText(getResources().getString(R.string.grade_exam));
            saveBtn.setText(getResources().getString(R.string.save));
        }catch (Exception e){}
    }


    public void onClick(View v) {
        int id=v.getId();
            if(id==R.id.backArrowImgBtn) {
                onBackPressed();
            }
            else if(id==R.id.saveBtn) {
                if (!OustSdkTools.checkInternetStatus()) {
                    return;
                }
                presenter.saveBtnClick(userGrade.getText().toString(), userSchoolCollege.getText().toString());
            }
            else if(id==R.id.backArrow_back){
                onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void saveAllSetting(UserSettingRequest userSettingRequest,ActiveUser activeUser){
        try {
            OustSdkTools.setProgressbar(academicSetting_progressbar);
            OustSdkTools.showProgressBar();
            sendSaveSettingRequest(userSettingRequest,activeUser);
        }catch (Exception e){}
    }

    public void sendSaveSettingRequest(final UserSettingRequest userSettingRequest,ActiveUser activeUser) {
        String settingUrl = OustSdkApplication.getContext().getResources().getString(R.string.usersetting_url);
        settingUrl= settingUrl.replace("{studentid}",activeUser.getStudentid());
        Gson gson = new GsonBuilder().create();
        String jsonParams = gson.toJson(userSettingRequest);

        try {
            settingUrl = HttpManager.getAbsoluteUrl(settingUrl);

            ApiCallUtils.doNetworkCall(Request.Method.POST, settingUrl, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    CommonResponse commonResponse =gson.fromJson(response.toString(),CommonResponse.class);
                    saveSettingProcessFinish(commonResponse,userSettingRequest);
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, settingUrl, OustSdkTools.getRequestObject(jsonParams), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    CommonResponse commonResponse =gson.fromJson(response.toString(),CommonResponse.class);
                    saveSettingProcessFinish(commonResponse,userSettingRequest);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

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

    public void saveSettingProcessFinish(CommonResponse commonResponse, UserSettingRequest userSettingRequest){
        try {
            OustSdkTools.hideProgressbar();
            presenter.saveProcessFinish(commonResponse,userSettingRequest);
        }catch (Exception e){

        }
    }
    public void finishSettingView(){
        try {
            AcademicsSettingActivity.this.finish();
        }catch (Exception e){}
    }
    public void showErrorPopup(Popup popup){
        try {
            OustStaticVariableHandling.getInstance().setOustpopup(popup);
            Intent intent = new Intent(AcademicsSettingActivity.this, PopupActivity.class);
            startActivity(intent);
        }catch (Exception e){}
    }
    public void showToast(String message){
        OustSdkTools.showToast(message);
    }
}

