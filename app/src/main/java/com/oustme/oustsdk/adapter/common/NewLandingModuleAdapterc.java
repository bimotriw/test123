package com.oustme.oustsdk.adapter.common;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.assessments.AssessmentPlayActivity;
import com.oustme.oustsdk.activity.assessments.AssessmentQuestionsActivity;
import com.oustme.oustsdk.activity.common.CatalogInfoActivity;
import com.oustme.oustsdk.activity.courses.CourseMultiLingualActivity;
import com.oustme.oustsdk.activity.courses.LessonsActivity;
import com.oustme.oustsdk.activity.courses.newlearnngmap.NewLearningMapActivity;
import com.oustme.oustsdk.assessment_ui.assessmentDetail.AssessmentDetailScreen;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.firebase.course.MultilingualCourse;
import com.oustme.oustsdk.interfaces.common.CommonLandingDataCallback;
import com.oustme.oustsdk.interfaces.common.DataLoaderNotifier;
import com.oustme.oustsdk.interfaces.course.SendCertificateCallBack;
import com.oustme.oustsdk.response.common.GameType;
import com.oustme.oustsdk.survey_module.SurveyComponentActivity;
import com.oustme.oustsdk.survey_ui.SurveyDetailActivity;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustDataHandler;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by admin on 14/10/17.
 */

public class NewLandingModuleAdapterc extends RecyclerView.Adapter<NewLandingModuleAdapterc.MyViewHolder> {

    private static final String TAG = "ModuleAdapterC";
    private List<CommonLandingData> commonLandingDataList;
    private int lastPosition=0;
    private boolean isMyDeskData=true;
    private HashMap<String,String> myDeskDataMap;
    private int localSize;
    private DataLoaderNotifier dataLoaderNotifier;
    private boolean showLoading=false;

    public boolean isShowLoading() {
        return showLoading;
    }

    public void setShowLoading(boolean showLoading) {
        this.showLoading = showLoading;
    }

    public void setMyDeskData(boolean myDeskData) {
        isMyDeskData = myDeskData;
    }

    public void setmyDeskDataMap(HashMap<String,String> myDeskDataMap){
        this.myDeskDataMap=myDeskDataMap;
    }
    public void setAdapterSizeLimit(int localSize){
        this.localSize=localSize;
    }
    private SendCertificateCallBack sendCertificateCallBack;
    public void setSendCertificateCallBack(SendCertificateCallBack sendCertificateCallBack) {
        this.sendCertificateCallBack = sendCertificateCallBack;
    }

    public NewLandingModuleAdapterc(List<CommonLandingData> commonLandingDataList) {
        this.commonLandingDataList = commonLandingDataList;
        Log.d(TAG, "NewLandingModuleAdapterc: ");
    }

    public void setDataLoaderNotifier(DataLoaderNotifier dataLoaderNotifier){
        this.dataLoaderNotifier=dataLoaderNotifier;
    }

    public void notifyListChnage(List<CommonLandingData> commonLandingDataList,int localSize){
        this.commonLandingDataList = commonLandingDataList;
        newActivityOpened=false;
        this.localSize=localSize;
        notifyDataSetChanged();
    }
    public void updateListItems(List<CommonLandingData> commonLandingDataList1,int adapterSize) {
        try {
//            if(this.localSize==adapterSize){
//                this.commonLandingDataList = commonLandingDataList1;
//                newActivityOpened=false;
//                notifyDataSetChanged();
//            }else{
                this.localSize = adapterSize;
                newActivityOpened = false;
                final CommonLandingDataCallback diffCallback = new CommonLandingDataCallback(this.commonLandingDataList, commonLandingDataList1);
                final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

                this.commonLandingDataList=new ArrayList<>();
                this.commonLandingDataList.addAll(commonLandingDataList1);
                diffResult.dispatchUpdatesTo(this);
            //}
        }catch (Exception e){}
    }

    @Override
    public int getItemCount() {
        if (localSize != 0) {
            if(OustDataHandler.getInstance().isAllCoursesLoaded()){
                return commonLandingDataList.size();
            }else{
                showLoading=true;
                return commonLandingDataList.size();
            }
        } else{
            return commonLandingDataList.size();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView rowbanner_image,icon_fullimage;
        private TextView coursenametext;
        private LinearLayout main_layout,banner_layout;
        private RelativeLayout cource_mainrow,banner_overlay,main_card_ll;
        MyViewHolder(View view) {
            super(view);
            cource_mainrow= view.findViewById(R.id.cource_mainrow);
            coursenametext= view.findViewById(R.id.coursenametext);
            rowbanner_image= view.findViewById(R.id.rowbanner_image);
            //ll_progress=(RelativeLayout) view.findViewById(R.id.ll_progress);
            main_card_ll= view.findViewById(R.id.main_card_ll);
            banner_overlay= view.findViewById(R.id.banner_overlay);
            icon_fullimage= view.findViewById(R.id.icon_fullimage);
            banner_layout= view.findViewById(R.id.banner_layout);
            //loading_text= (TextView) view.findViewById(R.id.loading_text);
        }
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView =  LayoutInflater.from(parent.getContext()).inflate(R.layout.catalogitem_2, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        try {
            if(localSize == 0 || position < commonLandingDataList.size()) {
                holder.cource_mainrow.setVisibility(View.VISIBLE);
                CommonLandingData commonLandingData = commonLandingDataList.get(position);
                holder.coursenametext.setVisibility(View.GONE);
                holder.banner_layout.setVisibility(View.GONE);
                holder.icon_fullimage.setVisibility(View.GONE);
                holder.icon_fullimage.setImageBitmap(null);
                holder.rowbanner_image.setImageBitmap(null);

                OustSdkTools.setLayoutBackgroudDrawable(holder.main_card_ll,OustSdkTools.getImgDrawable(R.drawable.roundedcorner_litegrey));
                if((commonLandingData.getIcon()!=null)&&(!commonLandingData.getIcon().isEmpty())&&(!commonLandingData.getIcon().equalsIgnoreCase("null"))){
                    //holder.banner_overlay.setVisibility(View.GONE);
                    holder.coursenametext.setVisibility(View.GONE);
                    holder.banner_layout.setVisibility(View.GONE);
                    holder.icon_fullimage.setVisibility(View.VISIBLE);
                    holder.main_card_ll.setBackgroundDrawable(null);
                    OustSdkTools.setLayoutBackgroudDrawable(holder.main_card_ll,OustSdkTools.getImgDrawable(R.drawable.roundedcorner_withotstroke));
                    holder.main_card_ll.setPadding(0,0,0,0);
                    //holder.main_card_ll.setBackgroundColor(OustApplication.getContext().getResources().getColor(R.color.MoreLiteGray));
                    if (OustSdkTools.checkInternetStatus()) {Picasso.get().load(commonLandingData.getIcon()).into(holder.icon_fullimage);
                    } else {
                        Picasso.get().load(commonLandingData.getIcon()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.icon_fullimage);
                    }
                }else if ((commonLandingData.getBanner() != null) && (!commonLandingData.getBanner().isEmpty())) {

                    holder.banner_layout.setVisibility(View.VISIBLE);
                    if (OustSdkTools.checkInternetStatus()) {
                        Picasso.get().load(commonLandingData.getBanner()).into(holder.rowbanner_image);
                    } else {
                        Picasso.get().load(commonLandingData.getBanner()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.rowbanner_image);
                    }
                }else {
                    holder.banner_layout.setVisibility(View.VISIBLE);
                }
                if(commonLandingData.getName()!=null){
                    holder.coursenametext.setVisibility(View.VISIBLE);
                    holder.coursenametext.setText(commonLandingData.getName());
                }else {
                    holder.coursenametext.setVisibility(View.VISIBLE);
                    holder.coursenametext.setText("");
                }
                holder.cource_mainrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(v, "scaleX", 1.0f, 0.94f);
                        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(v, "scaleY", 1.0f, 0.96f);
                        scaleDownX.setDuration(150);
                        scaleDownY.setDuration(150);
                        scaleDownX.setRepeatCount(1);
                        scaleDownY.setRepeatCount(1);
                        scaleDownX.setRepeatMode(ValueAnimator.REVERSE);
                        scaleDownY.setRepeatMode(ValueAnimator.REVERSE);
                        scaleDownX.setInterpolator(new DecelerateInterpolator());
                        scaleDownY.setInterpolator(new DecelerateInterpolator());
                        AnimatorSet scaleDown = new AnimatorSet();
                        scaleDown.play(scaleDownX).with(scaleDownY);
                        scaleDown.start();
                        scaleDown.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {
                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                if(!OustStaticVariableHandling.getInstance().isModuleClicked()) {
                                    clickonRow(position);
                                }
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {
                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {
                            }
                        });
                    }
                });
            }else{
//                if(holder.loading_text!=null) {
//                    holder.loading_text.setText(OustStrings.getString("loading"));
//                }
//                holder.ll_progress.setVisibility(View.VISIBLE);
                holder.cource_mainrow.setVisibility(View.GONE);
                callForNextData();
            }
//            if(localSize!=0 && position==localSize){
//                if(!OustStaticVariableHandling.getInstance().isNewLandingSearch()) {
//
//                }else {
//                    holder.ll_progress.setVisibility(View.GONE);
//                }
//            }

            if ((position == commonLandingDataList.size() - 1)) {
                if(!OustDataHandler.getInstance().isAllCoursesLoaded()) {
                    dataLoaderNotifier.getNextData("");
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
    private void callForNextData(){
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (dataLoaderNotifier != null) {
                            dataLoaderNotifier.getNextData("");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
            }, 150);
        }catch (Exception e){}
    }


    private boolean newActivityOpened=false;
    public void clickonRow(int position){
        try {
            CommonLandingData commonLandingData=commonLandingDataList.get(position);
            if(isMyDeskData) {
                if (!newActivityOpened) {
                    launchActivity(commonLandingData);
                }
            }else{
                String catalog_id=commonLandingData.getId();
                String catalog_type=commonLandingData.getType();
                if (catalog_type.equals("COURSE")) {
                    catalog_id = catalog_id.replace("course","");
                    catalog_id = catalog_id.replace("COURSE", "");
                } else if (catalog_type.equals("ASSESSMENT")) {
                    catalog_id = catalog_id.replace("assessment", "");
                    catalog_id = catalog_id.replace("ASSESSMENT", "");
                } else if (catalog_type.equals("COLLECTION")) {
                    catalog_id = catalog_id.replace("collection", "");
                    catalog_id = catalog_id.replace("COLLECTION", "");
                }
                if(catalog_id.contains("SURVEY")){
                    catalog_id.replace("SURVEY","");
                }
                commonLandingData.setId(catalog_id);
                if(!catalog_id.contains(commonLandingData.getType())){
                    catalog_id=commonLandingData.getType()+""+catalog_id;
                }
                if (myDeskDataMap != null && myDeskDataMap.containsKey(catalog_id)) {
                    launchActivity(commonLandingData);
                } else {
                    if(myDeskDataMap!=null) {
                        OustStaticVariableHandling.getInstance().setModuleClicked(true);
                        Intent intent = new Intent(OustSdkApplication.getContext(), CatalogInfoActivity.class);
                        intent.putExtra("catalog_id", commonLandingData.getId());
                        intent.putExtra("catalog_type", commonLandingData.getType());
                        intent.putExtra("deskmap", myDeskDataMap);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        OustSdkApplication.getContext().startActivity(intent);
                    }
                }
            }
        }catch (Exception e){}
    }

    public ActiveGame setGame(ActiveUser activeUser) {
        ActiveGame activeGame=new ActiveGame();
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
    private void launchActivity(CommonLandingData commonLandingData){
        OustStaticVariableHandling.getInstance().setModuleClicked(true);
        if ((commonLandingData.getType() != null) && (commonLandingData.getType().contains("COLLECTION"))) {
            Intent intent = new Intent(OustSdkApplication.getContext(), LessonsActivity.class);
            String id = commonLandingData.getId();
            id = id.replace("COLLECTION", "");
            intent.putExtra("collectionId", id);
            intent.putExtra("banner", commonLandingData.getBanner());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            OustSdkApplication.getContext().startActivity(intent);
        } else if ((commonLandingData.getType() != null) && (commonLandingData.getType().contains("ASSESSMENT"))) {
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
        }else if ((commonLandingData.getType() != null) && (commonLandingData.getType().contains("SURVEY"))) {
            Gson gson = new Gson();
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
            if(commonLandingData.getCourseType()!=null &&
                    commonLandingData.getCourseType().equalsIgnoreCase("Multilingual")){
                Intent intent = new Intent(OustSdkApplication.getContext(), CourseMultiLingualActivity.class);
                String id = commonLandingData.getId();
                id = id.replace("COURSE", "");
                intent.putExtra("learningId", id);
                List<MultilingualCourse> multilingualCourseList=new ArrayList<>();
                multilingualCourseList=commonLandingData.getMultilingualCourseListList();
                Bundle bundle = new Bundle();
                bundle.putSerializable("multilingualDataList", (Serializable) multilingualCourseList);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                OustSdkApplication.getContext().startActivity(intent);
            }else {
                Intent intent = new Intent(OustSdkApplication.getContext(), NewLearningMapActivity.class);
                String id = commonLandingData.getId();
                id = id.replace("COURSE", "");
                intent.putExtra("learningId", id);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                OustSdkApplication.getContext().startActivity(intent);
            }
        }
    }

}
