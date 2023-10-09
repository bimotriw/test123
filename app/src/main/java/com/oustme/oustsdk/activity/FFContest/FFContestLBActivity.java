package com.oustme.oustsdk.activity.FFContest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.EventLeaderboardActivity;
import com.oustme.oustsdk.activity.common.UserProfileActivity;
import com.oustme.oustsdk.adapter.FFContest.FFContestLBAdaptor;
import com.oustme.oustsdk.customviews.MyCustomLayoutManager;
import com.oustme.oustsdk.firebase.FFContest.FFCFirebaseQuestionResponse;
import com.oustme.oustsdk.firebase.FFContest.FFCFirebaseResponse;
import com.oustme.oustsdk.firebase.FFContest.FastestFingerContestData;
import com.oustme.oustsdk.firebase.FFContest.GetF3CContestLeaderBoardResponseData_v2;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.interfaces.common.RowClickCallBack;
import com.oustme.oustsdk.request.RewardMailRequest;
import com.oustme.oustsdk.response.FFContest.F3CUserMyPerfData;
import com.oustme.oustsdk.response.FFContest.F3CUserMyPerfDataResponseData;
import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustGATools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.htmlrender.HtmlTextView;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 04/08/17.
 */

public class FFContestLBActivity extends AppCompatActivity implements View.OnClickListener,RowClickCallBack {

    private List<FFCFirebaseResponse> ffcFirebaseResponseList;
    private List<FFCFirebaseQuestionResponse> ffcFirebaseQuestionResponseList;
    private List<FFCFirebaseResponse> ffcFirebaseMyResponseList=new ArrayList<>();
    private SwipeRefreshLayout ffc_lbswiperefreshlayout;
    private RecyclerView ffc_lbrecyclerview;
    private RelativeLayout ffc_lb_viewanswerlayout,ffc_lbqlayout;
    private ImageView ffclb_btnClose,ffclb_btnrewardwinner;
    private TextView bestresult_label,myresult_label,questionLabel,ffc_regitertext,ffclb_viewanstext,ffclb_viewanstexta,ffclb_viewanstextb,ffc_lbnodatatext,
            ffc_qlbtext,ffc_qlbtexta,ffc_qlbtextb,ffc_qlbtexttemp,myrank_text;
    private ImageView ffcstart_backimage,info_btn,ffclb_btnwinner,ffclb_btnluckywinner;
    private List<String> qList;
    private boolean isQuestionLB=false;
    private ActiveUser activeUser;
    private FastestFingerContestData fastestFingerContestData;
    private FirebaseRefClass firebaseRefClass;

    private boolean gotLBData=false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        try{
            OustSdkTools.setLocale(FFContestLBActivity.this);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        setContentView(R.layout.activity_ffclb);
        Intent callingIntent=getIntent();
        isQuestionLB=callingIntent.getBooleanExtra("isQuestionLB",false);
        isQuestionLB=false;
        initViews();
        activeUser=OustAppState.getInstance().getActiveUser();
        createLoader();
        Gson gson=new Gson();
        String fastestFingerContestDataStr=callingIntent.getStringExtra("fastestFingerContestData");
        if((fastestFingerContestDataStr!=null)&&(!fastestFingerContestDataStr.isEmpty())){
            fastestFingerContestData=gson.fromJson(fastestFingerContestDataStr,FastestFingerContestData.class);
        }
        refreshAllData();
        setListener();
        showLuckyWinnerIcon();
        if((fastestFingerContestData.getWinnerInfo()!=null)&&(!fastestFingerContestData.getWinnerInfo().isEmpty())){
            ffclb_btnwinner.setVisibility(View.VISIBLE);
        }
        getLuckyWinnerInfo();
//        OustGATools.getInstance().reportPageViewToGoogle(FFContestLBActivity.this,"FFContest Leader Board Page");

    }

    @Override
    public void onBackPressed() {
        if(isQuestionLB){
            isQuestionLB=false;
            showLoader();
            refreshAllData();
        }else {
            super.onBackPressed();
        }
    }

   
    @Override
    protected void onStart() {

        super.onStart();

    }

    private void refreshAllData(){
        try{
            customizeUI();
            showLoader();
            if(!isQuestionLB){
                if((fastestFingerContestData.getLeaderboardInfo()!=null)&&(!fastestFingerContestData.getLeaderboardInfo().isEmpty())){
                    info_btn.setVisibility(View.VISIBLE);
                }
                ffc_lbnodatatext.setVisibility(View.GONE);
                ffc_lbqlayout.setVisibility(View.VISIBLE);
                ffc_lb_viewanswerlayout.setVisibility(View.GONE);
                bestresult_label.setText("");
                questionLabel.setText("");
                myresult_label.setText("");
                gotLBData=true;
                getLeaderboardFromRest();
            }else {
                info_btn.setVisibility(View.GONE);
                bestresult_label.setText("");
                questionLabel.setText("");
                myresult_label.setText("");
                ffc_lbqlayout.setVisibility(View.GONE);
                ffc_lb_viewanswerlayout.setVisibility(View.VISIBLE);
                getMyLeaderboardFromRest();
            }
        }catch (Exception e){}
    }

    private void showLoader(){
        ffc_lbswiperefreshlayout.setVisibility(View.VISIBLE);
        ffc_lbswiperefreshlayout.setRefreshing(true);
        ffc_lbrecyclerview.setVisibility(View.GONE);
        ffc_lbnodatatext.setVisibility(View.GONE);
    }


    private long myRank=0;
    private void getLeaderboardFromRest(){
        if(!OustSdkTools.checkInternetStatus()){
            createFFCLeaderboardList();
            if((ffcFirebaseResponseList!=null)&&(ffcFirebaseResponseList.size()>0)){}else {
                ffc_lbrecyclerview.setVisibility(View.GONE);
                ffc_lbnodatatext.setVisibility(View.VISIBLE);
                ffc_lbnodatatext.setText(getResources().getString(R.string.no_internet_connection));
            }
        }else {
            String f3cleaderboard_url = OustSdkApplication.getContext().getResources().getString(R.string.f3cleaderboard_url);
            f3cleaderboard_url = f3cleaderboard_url.replace("{f3cId}", ("" + fastestFingerContestData.getFfcId()));
            f3cleaderboard_url = f3cleaderboard_url.replace("{userId}", activeUser.getStudentid());
            long topperCount = 25;
            if ((fastestFingerContestData.getLeaderboardToppersCount() > 0)) {
                topperCount = fastestFingerContestData.getLeaderboardToppersCount();
            }
            f3cleaderboard_url = f3cleaderboard_url.replace("{topperCount}", ("" + topperCount));
            JSONObject jsonParams = OustSdkTools.getRequestObjectforJSONObject(null);
            f3cleaderboard_url = HttpManager.getAbsoluteUrl(f3cleaderboard_url);

            ApiCallUtils.doNetworkCall(Request.Method.GET, f3cleaderboard_url, jsonParams, new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    GetF3CContestLeaderBoardResponseData_v2 getF3CContestLeaderBoardResponseData_v2s = new GetF3CContestLeaderBoardResponseData_v2();
                    try {
                        Gson gson = new GsonBuilder().create();
                        getF3CContestLeaderBoardResponseData_v2s = gson.fromJson(response.toString(), GetF3CContestLeaderBoardResponseData_v2.class);
                    } catch (Exception e) {
                    }
                    gotResponse(getF3CContestLeaderBoardResponseData_v2s);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    gotResponse(null);
                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, f3cleaderboard_url, jsonParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    GetF3CContestLeaderBoardResponseData_v2 getF3CContestLeaderBoardResponseData_v2s = new GetF3CContestLeaderBoardResponseData_v2();
                    try {
                        Gson gson = new GsonBuilder().create();
                        getF3CContestLeaderBoardResponseData_v2s = gson.fromJson(response.toString(), GetF3CContestLeaderBoardResponseData_v2.class);
                    } catch (Exception e) {
                    }
                    gotResponse(getF3CContestLeaderBoardResponseData_v2s);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    gotResponse(null);
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
        }
    }

    private void gotResponse(GetF3CContestLeaderBoardResponseData_v2 getF3CContestLeaderBoardResponseData_v2){
        ffcFirebaseResponseList=new ArrayList<>();
        if(getF3CContestLeaderBoardResponseData_v2!=null){
            if(getF3CContestLeaderBoardResponseData_v2.isSuccess()){
                myRank=getF3CContestLeaderBoardResponseData_v2.getMyRank();
                ffcFirebaseResponseList=getF3CContestLeaderBoardResponseData_v2.getLeaderBoard();
                bestresult_label.setVisibility(View.VISIBLE);
            }
        }
        createFFCLeaderboardList();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            if(firebaseRefClass!=null){
                OustFirebaseTools.getRootRef().child(firebaseRefClass.getFirebasePath()).removeEventListener(firebaseRefClass.getEventListener());
            }
        }catch (Exception e){}
    }


    private void setListener(){
        try {
            ffclb_btnClose.setOnClickListener(this);
            ffc_lb_viewanswerlayout.setOnClickListener(this);
            ffc_lbqlayout.setOnClickListener(this);
            info_btn.setOnClickListener(this);
            ffclb_btnluckywinner.setOnClickListener(this);
            ffclb_btnwinner.setOnClickListener(this);
            ffclb_btnrewardwinner.setOnClickListener(this);
        }catch (Exception e){}
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.ffclb_btnClose) {
            FFContestLBActivity.this.finish();
        }else if(view.getId()== R.id.ffc_lb_viewanswerlayout){
            if(isQuestionLB) {
                Intent intent = new Intent(FFContestLBActivity.this, FFContestAnswersActivity.class);
                Gson gson = new Gson();
                intent.putExtra("fastestFingerContestData", gson.toJson(fastestFingerContestData));
                String ffcFirebaseResponseListStr = new Gson().toJson(ffcFirebaseMyResponseList);
                intent.putExtra("ffcFirebaseResponseList",ffcFirebaseResponseListStr);
                String ffcFirebaseQuestionResponseListStr = new Gson().toJson(ffcFirebaseQuestionResponseList);
                intent.putExtra("ffcFirebaseQuestionResponseList",ffcFirebaseQuestionResponseListStr);
                startActivity(intent);
            }
        }else if(view.getId()== R.id.ffc_lbqlayout){
            isQuestionLB=true;
            showLoader();
            refreshAllData();
        }else if(view.getId()==R.id.info_btn){
            if((fastestFingerContestData.getLeaderboardInfo()!=null)&&(!fastestFingerContestData.getLeaderboardInfo().isEmpty())){
                showInfoPopup(fastestFingerContestData.getLeaderboardInfo());
            }
        }else if(view.getId()==R.id.ffclb_btnluckywinner){
            if((fastestFingerContestData.getLuckyWinnerInfoText()!=null)&&(!fastestFingerContestData.getLuckyWinnerInfoText().isEmpty())){
                showInfoPopup(fastestFingerContestData.getLuckyWinnerInfoText());
            }
        }else if(view.getId()==R.id.ffclb_btnwinner){
            if((fastestFingerContestData.getWinnerInfo()!=null)&&(!fastestFingerContestData.getWinnerInfo().isEmpty())){
                showInfoPopup(fastestFingerContestData.getWinnerInfo());
            }
        }else if(view.getId()==R.id.ffclb_btnrewardwinner){
            sendRewardToMailPopup();
        }
    }

    private void showInfoPopup(String info) {
        try {
            View popUpView =getLayoutInflater().inflate(R.layout.ffc_lbinfopopup, null);
            final PopupWindow info_popup = OustSdkTools.createPopUp(popUpView);
            final HtmlTextView content = popUpView.findViewById(R.id.content);
            content.setHtml(info);
            content.setTypeface(OustSdkTools.getTypefaceLight());

            RelativeLayout ok_layout= popUpView.findViewById(R.id.ok_layout);
            RelativeLayout desclaimer_popup_inside_roundedcornerlayout= popUpView.findViewById(R.id.desclaimer_popup_inside_roundedcornerlayout);
            if(OustPreferences.get("toolbarColorCode")!=null){
                int color = Color.parseColor(OustPreferences.get("toolbarColorCode"));
                GradientDrawable drawable = (GradientDrawable) getApplicationContext().getResources().getDrawable(R.drawable.desclaimer_popup_inside_roundedcorner);
                drawable.setStroke(3, color);
                OustSdkTools.setLayoutBackgroudDrawable(desclaimer_popup_inside_roundedcornerlayout,drawable);

                GradientDrawable drawable1 = (GradientDrawable) OustSdkTools.getImgDrawable(R.drawable.rounded_backa);
                drawable1.setColor(color);
                OustSdkTools.setLayoutBackgroudDrawable(ok_layout,drawable1);
            }
            ok_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    info_popup.dismiss();
                }
            });
            LinearLayout info_mainLayout= popUpView.findViewById(R.id.info_mainLayout);
            OustSdkTools.popupAppearEffect(info_mainLayout);
        }catch (Exception e){
        }
    }


    private void initViews(){
        ffc_lb_viewanswerlayout= findViewById(R.id.ffc_lb_viewanswerlayout);
        myresult_label= findViewById(R.id.myresult_label);
        ffc_lbswiperefreshlayout= findViewById(R.id.ffc_lbswiperefreshlayout);
        ffc_lbrecyclerview= findViewById(R.id.ffc_lbrecyclerview);
        ffclb_btnClose= findViewById(R.id.ffclb_btnClose);
        bestresult_label= findViewById(R.id.bestresult_label);
        questionLabel= findViewById(R.id.questionLabel);
        ffc_regitertext= findViewById(R.id.ffc_regitertext);
        ffclb_viewanstext= findViewById(R.id.ffclb_viewanstext);
        ffclb_viewanstexta= findViewById(R.id.ffclb_viewanstexta);
        ffclb_viewanstextb= findViewById(R.id.ffclb_viewanstextb);
        ffc_lbnodatatext= findViewById(R.id.ffc_lbnodatatext);
        ffc_qlbtext= findViewById(R.id.ffc_qlbtext);
        ffc_qlbtexta= findViewById(R.id.ffc_qlbtexta);
        ffc_qlbtextb= findViewById(R.id.ffc_qlbtextb);
        ffc_qlbtexttemp= findViewById(R.id.ffc_qlbtexttemp);

        ffcstart_backimage= findViewById(R.id.ffcstart_backimage);
        ffc_lbqlayout= findViewById(R.id.ffc_lbqlayout);
        myrank_text= findViewById(R.id.myrank_text);
        info_btn= findViewById(R.id.info_btn);
        ffclb_btnluckywinner= findViewById(R.id.ffclb_btnluckywinner);
        ffclb_btnwinner= findViewById(R.id.ffclb_btnwinner);
        ffclb_btnrewardwinner= findViewById(R.id.ffclb_btnrewardwinner);

        OustSdkTools.setImage(ffclb_btnrewardwinner,getResources().getString(R.string.reward_icon));
        OustSdkTools.setImage(ffclb_btnwinner,getResources().getString(R.string.winner_info));
        OustSdkTools.setImage(ffclb_btnluckywinner,getResources().getString(R.string.lucky_winner));

    }

    private void customizeUI(){
        try {
            if(isQuestionLB){
                if((fastestFingerContestData.getQuestionLBBtnText()!=null)&&(!fastestFingerContestData.getQuestionLBBtnText().isEmpty())){
                    ffc_regitertext.setText(fastestFingerContestData.getQuestionLBBtnText());
                }
            }else {
                if((fastestFingerContestData.getOverallLBBtnText()!=null)&&(!fastestFingerContestData.getOverallLBBtnText().isEmpty())){
                    ffc_regitertext.setText(fastestFingerContestData.getOverallLBBtnText());
                }
            }
            if((fastestFingerContestData.getBgImage()!=null)&&(!fastestFingerContestData.getBgImage().isEmpty())){
                if (OustSdkTools.checkInternetStatus()) {
                    Picasso.get().load(fastestFingerContestData.getBgImage()).into(ffcstart_backimage);
                } else {
                    Picasso.get().load(fastestFingerContestData.getBgImage()).networkPolicy(NetworkPolicy.OFFLINE).into(ffcstart_backimage);
                }
            }
            if ((fastestFingerContestData.getQuestionTxtColor() != null) && (!fastestFingerContestData.getQuestionTxtColor().isEmpty())) {
                int color = Color.parseColor(fastestFingerContestData.getQuestionTxtColor());
                bestresult_label.setTextColor(color);
                myresult_label.setTextColor(color);
                questionLabel.setTextColor(color);
                ffc_regitertext.setTextColor(color);
                ffclb_btnClose.setColorFilter(color);
                ffc_lbnodatatext.setTextColor(color);
                info_btn.setColorFilter(color);
                GradientDrawable drawable = (GradientDrawable) getApplicationContext().getResources().getDrawable(R.drawable.circle_blackcorner);
                drawable.setStroke(3, color);
            }else {
                GradientDrawable drawable = (GradientDrawable) getApplicationContext().getResources().getDrawable(R.drawable.circle_blackcorner);
                drawable.setStroke(3, OustSdkTools.getColorBack(R.color.whitelight));
            }
            if((fastestFingerContestData.getQuestionLBBtnText()!=null)&&(!fastestFingerContestData.getQuestionLBBtnText().isEmpty())){
                ffc_qlbtexttemp.setText(fastestFingerContestData.getQuestionLBBtnText());
                ffc_qlbtext.setText(fastestFingerContestData.getQuestionLBBtnText());
                ffc_qlbtexta.setText(fastestFingerContestData.getQuestionLBBtnText());
                ffc_qlbtextb.setText(fastestFingerContestData.getQuestionLBBtnText());
            }
            if(((fastestFingerContestData.getBtnColorTop()!=null)&&(!fastestFingerContestData.getBtnColorTop().isEmpty()))&&
                    ((fastestFingerContestData.getBtnColorBottom()!=null)&&(!fastestFingerContestData.getBtnColorBottom().isEmpty()))){
                int color=Color.parseColor(fastestFingerContestData.getBtnColorTop());
                GradientDrawable drawable = (GradientDrawable) OustSdkTools.getImgDrawable(R.drawable.ffc_button_backa);
                drawable.setColor(color);
                OustSdkTools.setLayoutBackgroudDrawable(ffclb_viewanstexta,drawable);
                OustSdkTools.setLayoutBackgroudDrawable(ffc_qlbtexta,drawable);

                color=Color.parseColor(fastestFingerContestData.getBtnColorBottom());
                drawable = (GradientDrawable) OustSdkTools.getImgDrawable(R.drawable.ffc_button_backb);
                drawable.setColor(color);
                OustSdkTools.setLayoutBackgroudDrawable(ffclb_viewanstextb,drawable);
                OustSdkTools.setLayoutBackgroudDrawable(ffc_qlbtextb,drawable);
            }
            if((fastestFingerContestData.getBtnTextColor()!=null)&&(!fastestFingerContestData.getBtnTextColor().isEmpty())){
                ffclb_viewanstext.setTextColor(Color.parseColor(fastestFingerContestData.getBtnTextColor()));
                ffc_qlbtext.setTextColor(Color.parseColor(fastestFingerContestData.getBtnTextColor()));
            }
        }catch (Exception e){}
    }



    private FFContestLBAdaptor ffContestLBAdaptor;
    private void createFFCLeaderboardList(){
        try {
            if (((ffcFirebaseResponseList != null) && (ffcFirebaseResponseList.size() > 0) && (!isQuestionLB)) ||
                    ((isQuestionLB) && (ffcFirebaseQuestionResponseList != null) && (ffcFirebaseQuestionResponseList.size() > 0))) {
                questionLabel.setVisibility(View.VISIBLE);
                ffc_lbrecyclerview.setVisibility(View.VISIBLE);
                ffc_lbnodatatext.setVisibility(View.GONE);
                showRank();
                showRewardIcon();
                if (isQuestionLB) {
                    ffContestLBAdaptor = new FFContestLBAdaptor(ffcFirebaseMyResponseList, ffcFirebaseQuestionResponseList, isQuestionLB);
                } else {
                    ffContestLBAdaptor = new FFContestLBAdaptor(ffcFirebaseResponseList, ffcFirebaseQuestionResponseList, isQuestionLB);
                }
                MyCustomLayoutManager mLayoutManager = new MyCustomLayoutManager(this);
                mLayoutManager.setExtraLayoutSpace(1200);
                ffc_lbrecyclerview.setLayoutManager(mLayoutManager);
                ffc_lbrecyclerview.setItemAnimator(new DefaultItemAnimator());
                ffContestLBAdaptor.setRowClickCallBack(this);
                ffContestLBAdaptor.setTextColor(fastestFingerContestData.getQuestionTxtColor());
                ffc_lbrecyclerview.setAdapter(ffContestLBAdaptor);
                if (!isQuestionLB) {
                    bestresult_label.setText("");
                    questionLabel.setText(getResources().getString(R.string.rank));
                    bestresult_label.setText(getResources().getString(R.string.name_text));
                } else {
                    bestresult_label.setText(getResources().getString(R.string.best_result));
                    questionLabel.setText(getResources().getString(R.string.question_text));
                    myresult_label.setText(getResources().getString(R.string.my_result));
                }
            } else {
                questionLabel.setVisibility(View.GONE);
                ffc_lbrecyclerview.setVisibility(View.GONE);
                ffc_lbnodatatext.setVisibility(View.VISIBLE);
                bestresult_label.setText("");
                ffc_lbnodatatext.setText(getResources().getString(R.string.no_winner));
            }
            ffc_lbswiperefreshlayout.setVisibility(View.GONE);
            ffc_lbswiperefreshlayout.setRefreshing(false);
        }catch (Exception e){}
    }

    private void showRank(){
        if(myRank>0){
            myrank_text.setText(getResources().getString(R.string.my_rank)+myRank);
        }else {
            myrank_text.setText("");
        }
    }

    private void showRewardIcon(){
        if((myRank<=fastestFingerContestData.getRewardWinnerCount())&&(myRank>0)){
            ffclb_btnrewardwinner.setVisibility(View.VISIBLE);
            OustSdkTools.setImage(ffclb_btnrewardwinner,getResources().getString(R.string.reward_icon));
            if((fastestFingerContestData.getRewardWinnerIcon()!=null)&&(!fastestFingerContestData.getRewardWinnerIcon().isEmpty())){
                if (OustSdkTools.checkInternetStatus()) {
                    Picasso.get().load(fastestFingerContestData.getRewardWinnerIcon()).into(ffclb_btnrewardwinner);
                } else {
                    Picasso.get().load(fastestFingerContestData.getRewardWinnerIcon()).networkPolicy(NetworkPolicy.OFFLINE).into(ffclb_btnrewardwinner);
                }
            }
        }else {
            ffclb_btnrewardwinner.setVisibility(View.GONE);
        }
    }




    private void createLoader(){
        try {
            ffc_lbswiperefreshlayout.setColorSchemeColors(OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen), OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen));
//               creates loader
            ffc_lbswiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    ffc_lbswiperefreshlayout.setRefreshing(false);
                }
            });
//            show loader
            ffc_lbswiperefreshlayout.post(new Runnable() {
                @Override
                public void run() {
                    ffc_lbswiperefreshlayout.setRefreshing(true);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onMainRowClick(String name, int position) {
        if(isQuestionLB) {
            Intent intent = new Intent(FFContestLBActivity.this, FFContestAnswersActivity.class);
            Gson gson = new Gson();
            intent.putExtra("fastestFingerContestData", gson.toJson(fastestFingerContestData));
            String ffcFirebaseResponseListStr = new Gson().toJson(ffcFirebaseMyResponseList);
            intent.putExtra("ffcFirebaseResponseList",ffcFirebaseResponseListStr);
            String ffcFirebaseQuestionResponseListStr = new Gson().toJson(ffcFirebaseQuestionResponseList);
            intent.putExtra("ffcFirebaseQuestionResponseList",ffcFirebaseQuestionResponseListStr);
            intent.putExtra("rowNo",position);
            startActivity(intent);
        }else {
            gotoUserProfile( ffcFirebaseResponseList.get(position).getUserId(),ffcFirebaseResponseList.get(position).getDisplayName(),
                    ffcFirebaseResponseList.get(position).getAvatar());
        }
    }

    private void gotoUserProfile(String studentId,String name, String avatar){
        try {
            if (!studentId.equalsIgnoreCase(OustSdkTools.getActiveUserData().getStudentid())) {
                Intent intent = new Intent(this, UserProfileActivity.class);
                intent.putExtra("avatar",avatar);
                intent.putExtra("name", name);
                intent.putExtra("studentId", studentId);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }catch (Exception e){}
    }

//====================================================================================================

    private void getMyLeaderboardFromRest(){
        if(!OustSdkTools.checkInternetStatus()){
            createFFCLeaderboardList();
            if((ffcFirebaseMyResponseList.size()>0)&&(ffcFirebaseQuestionResponseList.size()>0)){}else {
                ffc_lbrecyclerview.setVisibility(View.GONE);
                ffc_lbnodatatext.setVisibility(View.VISIBLE);
                ffc_lbnodatatext.setText("NO INTERNET CONNECTION");
            }
        }else {
            String f3cmyleaderboard_url = OustSdkApplication.getContext().getResources().getString(R.string.f3cmyleaderboard_url);
            f3cmyleaderboard_url = f3cmyleaderboard_url.replace("{f3cId}", ("" + fastestFingerContestData.getFfcId()));
            f3cmyleaderboard_url = f3cmyleaderboard_url.replace("{userId}", activeUser.getStudentid());
            JSONObject jsonParams = OustSdkTools.getRequestObjectforJSONObject(null);
            f3cmyleaderboard_url = HttpManager.getAbsoluteUrl(f3cmyleaderboard_url);

            ApiCallUtils.doNetworkCall(Request.Method.GET, f3cmyleaderboard_url, jsonParams, new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    F3CUserMyPerfDataResponseData f3CUserMyPerfDataResponseData = new F3CUserMyPerfDataResponseData();
                    try {
                        Gson gson = new GsonBuilder().create();
                        f3CUserMyPerfDataResponseData = gson.fromJson(response.toString(), F3CUserMyPerfDataResponseData.class);
                    } catch (Exception e) {}
                    gotMyPerformanceResponse(f3CUserMyPerfDataResponseData);
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, f3cmyleaderboard_url, jsonParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    F3CUserMyPerfDataResponseData f3CUserMyPerfDataResponseData = new F3CUserMyPerfDataResponseData();
                    try {
                        Gson gson = new GsonBuilder().create();
                        f3CUserMyPerfDataResponseData = gson.fromJson(response.toString(), F3CUserMyPerfDataResponseData.class);
                    } catch (Exception e) {}
                    gotMyPerformanceResponse(f3CUserMyPerfDataResponseData);
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
        }
    }

    private void gotMyPerformanceResponse(F3CUserMyPerfDataResponseData f3CUserMyPerfDataResponseData){
        ffcFirebaseQuestionResponseList=new ArrayList<>();
        ffcFirebaseMyResponseList=new ArrayList<>();
        if(f3CUserMyPerfDataResponseData!=null){
            if(f3CUserMyPerfDataResponseData.isSuccess()){
                List<F3CUserMyPerfData> f3CUserMyPerfDataList=f3CUserMyPerfDataResponseData.getMyPerfData();
                if(f3CUserMyPerfDataList!=null){
                    for(int j=0;j<f3CUserMyPerfDataList.size();j++){
                        FFCFirebaseQuestionResponse ffcFirebaseQuestionResponse=new FFCFirebaseQuestionResponse();
                        ffcFirebaseQuestionResponse.setUserId(f3CUserMyPerfDataList.get(j).getUserId());
                        ffcFirebaseQuestionResponse.setAvatar(f3CUserMyPerfDataList.get(j).getAvatar());
                        ffcFirebaseQuestionResponse.setDisplayName(f3CUserMyPerfDataList.get(j).getDisplayName());
                        ffcFirebaseQuestionResponse.setReponseTime(f3CUserMyPerfDataList.get(j).getResponseTime());
                        ffcFirebaseQuestionResponseList.add(ffcFirebaseQuestionResponse);

                        FFCFirebaseResponse ffcFirebaseResponse=new FFCFirebaseResponse();
                        ffcFirebaseResponse.setUserId(activeUser.getStudentid());
                        ffcFirebaseResponse.setAvatar(activeUser.getAvatar());
                        ffcFirebaseResponse.setDisplayName(activeUser.getUserDisplayName());
                        ffcFirebaseResponse.setAnswer(f3CUserMyPerfDataList.get(j).getMyAnswer());
                        ffcFirebaseResponse.setAverageTime(f3CUserMyPerfDataList.get(j).getMyResponseTime());
                        ffcFirebaseResponse.setCorrect(f3CUserMyPerfDataList.get(j).isMyCorrect());
                        ffcFirebaseMyResponseList.add(ffcFirebaseResponse);
                    }
                }
            }
        }
        createFFCLeaderboardList();
    }
    //====================================================================================================
    public void getLuckyWinnerInfo(){
        try{
            final String message="/f3cData/f3c"+fastestFingerContestData.getFfcId()+"/luckyWinnerInfo/message";
            ValueEventListener ffcDataListener=new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot != null) {
                            String luckyWinnerMessage=(String) dataSnapshot.getValue();
                            fastestFingerContestData.setLuckyWinnerInfoText(luckyWinnerMessage);
                        }
                        showLuckyWinnerIcon();
                    }catch (Exception e){}
                }
                @Override
                public void onCancelled(DatabaseError error) {}
            };
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
            OustFirebaseTools.getRootRef().child(message).addValueEventListener(ffcDataListener);
            firebaseRefClass=new FirebaseRefClass(ffcDataListener,message);
        }catch (Exception e){
        }
    }

    private void showLuckyWinnerIcon(){
        try {
            if ((fastestFingerContestData.getLuckyWinnerInfoText() != null) && (!fastestFingerContestData.getLuckyWinnerInfoText().isEmpty())) {
                ffclb_btnluckywinner.setVisibility(View.VISIBLE);
            } else {
                ffclb_btnluckywinner.setVisibility(View.GONE);
            }
        }catch (Exception e){}
    }


//======================================================================================

    private RelativeLayout rewardmail_layout;
    private ProgressBar rewardmail_loader;
    public void showCertificateLoader(){
        try {
            rewardmail_layout.setVisibility(View.VISIBLE);
            Animation rotateAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_anim);
            rewardmail_loader.startAnimation(rotateAnim);
            rewardmail_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {}
            });
        }catch (Exception e){}
    }
    public void hideCertificateLoader(){
        try {
            rewardmail_layout.setVisibility(View.GONE);
            rewardmail_loader.setAnimation(null);
        }catch (Exception e){}
    }
    private PopupWindow rewardmail_popup;
    public void sendRewardToMailPopup() {
        try {
            View popUpView =getLayoutInflater().inflate(R.layout.certificateemail_popup, null);
            rewardmail_popup = OustSdkTools.createPopUp(popUpView);
            final Button btnOK = popUpView.findViewById(R.id.otp_okbtn);
            final ImageButton btnClose = popUpView.findViewById(R.id.certificatepopup_btnClose);
            final EditText edittext_email = popUpView.findViewById(R.id.edittext_email);
            final TextView certifucate_titletxt = popUpView.findViewById(R.id.certifucate_titletxt);
            certifucate_titletxt.setVisibility(View.GONE);

            TextView certificatemsg= popUpView.findViewById(R.id.certificatemsg);
            if(fastestFingerContestData.getRewardWinnerContent()!=null) {
                certificatemsg.setText(fastestFingerContestData.getRewardWinnerContent());
            }else {
                certificatemsg.setText(getResources().getString(R.string.reward_msg));
            }
            rewardmail_layout= popUpView.findViewById(R.id.certificateloader_layout);
            rewardmail_loader= popUpView.findViewById(R.id.certificate_loader);
            if (OustAppState.getInstance().getActiveUser().getEmail() != null) {
                edittext_email.setText(OustAppState.getInstance().getActiveUser().getEmail());
            }
            InputMethodManager inputMethodManager=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            edittext_email.requestFocus();
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OustSdkTools.oustTouchEffect(view,200);
                    if(OustSdkTools.isValidEmail(edittext_email.getText().toString().trim())){
                        showCertificateLoader();
                        sendRewardtomail(edittext_email.getText().toString().trim());
                    }else {
                        OustSdkTools.showToast(getResources().getString(R.string.enter_valid_mail));
                    }
                }
            });
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideKeyboard(edittext_email);
                    rewardmail_popup.dismiss();
                }
            });
            LinearLayout certificateanim_layout= popUpView.findViewById(R.id.certificateanim_layout);
            OustSdkTools.popupAppearEffect(certificateanim_layout);
        }catch (Exception e){
        }
    }
    public void hideKeyboard(View v){
        try{
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }catch (Exception e){}
    }

    private void sendRewardtomail(final String mail){
        try {
            RewardMailRequest rewardMailRequest=new RewardMailRequest();
            rewardMailRequest.setStudentid(activeUser.getStudentid());
            rewardMailRequest.setEmailId(mail);
            rewardMailRequest.setF3cId((""+fastestFingerContestData.getFfcId()));
            
            Gson gson = new GsonBuilder().create();
            String jsonDataReq = gson.toJson(rewardMailRequest);
            JSONObject jsonParams = OustSdkTools.getRequestObject(jsonDataReq);
            
            String rewardmailrequest_url = "f3c/sendRewardMailToTopper";
            rewardmailrequest_url = HttpManager.getAbsoluteUrl(rewardmailrequest_url);

            ApiCallUtils.doNetworkCall(Request.Method.POST, rewardmailrequest_url, jsonParams, new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    CommonResponse commonResponses = OustSdkTools.getCommonResponse(response.toString());
                    gotRewardtomailResponce(commonResponses);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    gotRewardtomailResponce(null);
                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, rewardmailrequest_url, jsonParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    CommonResponse commonResponses = OustSdkTools.getCommonResponse(response.toString());
                    gotRewardtomailResponce(commonResponses);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    gotRewardtomailResponce(null);
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

        }
    }
    

    public void gotRewardtomailResponce(CommonResponse commonResponse){
        try{
            hideCertificateLoader();
            if((commonResponse!=null)){
                if(commonResponse.isSuccess()) {
                    if ((rewardmail_popup != null) && (rewardmail_popup.isShowing())) {
                        rewardmail_popup.dismiss();
                    }
                }else {
                    OustSdkTools.handlePopup(commonResponse);
                }
            }else {
                OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg));
            }
        }catch (Exception e){}
    }    
}
