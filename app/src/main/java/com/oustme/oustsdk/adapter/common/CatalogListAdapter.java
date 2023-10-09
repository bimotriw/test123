package com.oustme.oustsdk.adapter.common;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.assessments.AssessmentPlayActivity;
import com.oustme.oustsdk.activity.common.CatalogInfoActivity;
import com.oustme.oustsdk.activity.common.CatalogListActivity;
import com.oustme.oustsdk.activity.courses.LessonsActivity;
import com.oustme.oustsdk.course_ui.CourseDetailScreen;
import com.oustme.oustsdk.assessment_ui.assessmentDetail.AssessmentDetailScreen;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.model.request.CatalogViewUpdate;
import com.oustme.oustsdk.response.common.GameType;
import com.oustme.oustsdk.skill_ui.ui.SkillDetailActivity;
import com.oustme.oustsdk.survey_module.SurveyComponentActivity;
import com.oustme.oustsdk.survey_ui.SurveyDetailActivity;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by oust on 10/25/17.
 */

public class CatalogListAdapter extends RecyclerView.Adapter<CatalogListAdapter.MyViewHolder> {
    private static final String TAG = "CatalogListAdapter";
    private ArrayList<CommonLandingData> commonLandingDataList;
    private String categoryName;
    private Context context;
    private boolean isMyCourse=false;
    private HashMap<String,String> myDeskMap;
    private int type=0;
    public CatalogListAdapter(Context context,ArrayList<CommonLandingData> commonLandingDataList,int type){
        this.context=context;
        this.commonLandingDataList=commonLandingDataList;
        this.type=type;
    }
    public void isMyCourse(boolean isMyCourse){
        this.isMyCourse=isMyCourse;
    }

    public void setCategory(String categoryName){
        this.categoryName = categoryName;
    }

    public void setMyDeskMap(HashMap<String,String> myDeskMap){
        this.myDeskMap=myDeskMap;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView rowbanner_image,icon_fullimage;
        private TextView coursenametext,loading_text;
        private RelativeLayout cource_mainrow,main_card_ll,topSpace;
        private LinearLayout banner_layout;
        private ImageView imageViewIndicator;
        MyViewHolder(View view) {
            super(view);
            cource_mainrow= view.findViewById(R.id.cource_mainrow);
            coursenametext= view.findViewById(R.id.coursenametext);
            rowbanner_image= view.findViewById(R.id.rowbanner_image);
            main_card_ll= view.findViewById(R.id.main_card_ll);
            icon_fullimage= view.findViewById(R.id.icon_fullimage);
            banner_layout= view.findViewById(R.id.banner_layout);
            loading_text= view.findViewById(R.id.loading_text);
            topSpace= view.findViewById(R.id.topSpace);
            imageViewIndicator = view.findViewById(R.id.CatalogueUpdateIndicator);
        }
    }

    public void notifyAdapter(ArrayList<CommonLandingData> commonLandingDataList){
        this.commonLandingDataList=commonLandingDataList;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if(type==1){
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.catalog_item, parent, false);
        }else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.catalogitem_2, parent, false);
        }
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        try {
            CommonLandingData commonLandingData=commonLandingDataList.get(position);
            if(commonLandingData.getName()!=null){
                holder.coursenametext.setText(commonLandingData.getName());
            }else {
                holder.coursenametext.setText("");
            }
            holder.coursenametext.setVisibility(View.VISIBLE);
            holder.banner_layout.setVisibility(View.GONE);
            holder.icon_fullimage.setVisibility(View.GONE);
            holder.icon_fullimage.setImageBitmap(null);
            holder.rowbanner_image.setImageBitmap(null);
            if(holder.loading_text!=null) {
                holder.loading_text.setText(context.getResources().getString(R.string.loading));
            }
            if((type==1)&&((position==0)||(position==1))){
                holder.topSpace.setVisibility(View.VISIBLE);
            }else{
                holder.topSpace.setVisibility(View.GONE);
            }
            OustSdkTools.setLayoutBackgroudDrawable(holder.main_card_ll,OustSdkTools.getImgDrawable(R.drawable.roundedcorner_litegrey));
            if((commonLandingData.getIcon()!=null)&&(!commonLandingData.getIcon().isEmpty())&&(!commonLandingData.getIcon().equalsIgnoreCase("null"))){
                holder.main_card_ll.setBackgroundDrawable(null);
                OustSdkTools.setLayoutBackgroudDrawable(holder.main_card_ll,OustSdkTools.getImgDrawable(R.drawable.roundedcorner_withotstroke));
                holder.coursenametext.setVisibility(View.VISIBLE);
                holder.banner_layout.setVisibility(View.GONE);
                holder.icon_fullimage.setVisibility(View.VISIBLE);
                holder.main_card_ll.setPadding(0,0,0,0);

                if (OustSdkTools.checkInternetStatus()) {
                    Picasso.get().load(commonLandingData.getIcon()).into(holder.icon_fullimage);
                } else {
                    Picasso.get().load(commonLandingData.getIcon()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.icon_fullimage);
                }
            }else if ((commonLandingData.getBanner() != null) && (!commonLandingData.getBanner().isEmpty())&& (!commonLandingData.getBanner().equalsIgnoreCase("null"))) {
                holder.banner_layout.setVisibility(View.VISIBLE);
                if (OustSdkTools.checkInternetStatus()) {
                    Picasso.get().load(commonLandingData.getBanner()).into(holder.rowbanner_image);
                } else {
                    Picasso.get().load(commonLandingData.getBanner()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.rowbanner_image);
                }
            }else
                holder.banner_layout.setVisibility(View.VISIBLE);

            if(commonLandingData.getViewStatus()!=null && (commonLandingData.getViewStatus().equalsIgnoreCase("NEW") || commonLandingData.getViewStatus().equalsIgnoreCase("UPDATE")))
            {
             holder.imageViewIndicator.setVisibility(View.VISIBLE);
            }
            else{
                holder.imageViewIndicator.setVisibility(View.GONE);
            }
            holder.cource_mainrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(v, "scaleX", 1.0f,0.94f);
                    ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(v, "scaleY", 1.0f,0.96f);
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
                        public void onAnimationStart(Animator animator) {}
                        @Override
                        public void onAnimationEnd(Animator animator) {
//                            if(!OustStaticVariableHandling.getInstance().isModuleClicked()) {
                                clickonRow(position);
//                            }
                        }
                        @Override
                        public void onAnimationCancel(Animator animator) {}

                        @Override
                        public void onAnimationRepeat(Animator animator) {}
                    });
                }
            });
        }catch(Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void updateViewStatus(CommonLandingData commonLandingData, int pos) {
        if(commonLandingData.getViewStatus()!=null && (commonLandingData.getViewStatus().equalsIgnoreCase("NEW") || commonLandingData.getViewStatus().equalsIgnoreCase("UPDATE"))){
            commonLandingDataList.get(pos).setViewStatus("SEEN");
            ActiveUser activeUser;
            String activeUserGet = OustPreferences.get("userdata");
            activeUser = OustSdkTools.getActiveUserData(activeUserGet);
            CatalogViewUpdate catalogViewUpdate = new CatalogViewUpdate();
            catalogViewUpdate.setCatalogId(commonLandingData.getCatalogId());
            catalogViewUpdate.setContentType(commonLandingData.getType());
            catalogViewUpdate.setContentId(commonLandingData.getCatalogContentId());
            catalogViewUpdate.setCategoryId(commonLandingData.getCatalogCategoryId());
            catalogViewUpdate.setStudentid(activeUser.getStudentid());

            String url = OustSdkApplication.getContext().getResources().getString(R.string.catalog_view_update);
            final Gson mGson = new Gson();
            url = HttpManager.getAbsoluteUrl(url);
            String jsonParams = mGson.toJson(catalogViewUpdate);

            ApiCallUtils.doNetworkCall(Request.Method.PUT, url, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    // OustSdkTools.showToast("CPL Distributed Successfully");
                    // mProgressbarAPICall.setVisibility(View.GONE);
                    notifyDataSetChanged();
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    //  OustSdkTools.showToast("Unable to Distribute CPL, Please try again.");
                    //  mProgressbarAPICall.setVisibility(View.GONE);
                    Log.d(TAG, "onErrorResponse: onError:"+error.getLocalizedMessage());
                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, url, OustSdkTools.getRequestObject(jsonParams), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    // OustSdkTools.showToast("CPL Distributed Successfully");
                    // mProgressbarAPICall.setVisibility(View.GONE);
                    notifyDataSetChanged();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //  OustSdkTools.showToast("Unable to Distribute CPL, Please try again.");
                    //  mProgressbarAPICall.setVisibility(View.GONE);
                    Log.d(TAG, "onErrorResponse: onError:"+error.getLocalizedMessage());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
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

    @Override
    public int getItemCount() {
        return commonLandingDataList.size();
    }
    public void clickonRow(int position) {
        try {
            CommonLandingData commonLandingData=commonLandingDataList.get(position);
            updateViewStatus(commonLandingData, position);
            if(commonLandingData!=null) {
                if (isMyCourse) {
                    startCatalogActivity(commonLandingData);
                }
                else
                    {
                    if ((commonLandingData.getType() != null) && (commonLandingData.getType().contains("CATEGORY")))
                    {
                        try {
                            if(OustSdkTools.checkInternetStatus())
                            {
                                Intent intent = new Intent(context, CatalogListActivity.class);
                                String cat_id = commonLandingData.getId();
                                if (cat_id.contains(commonLandingData.getType())) {
                                    cat_id.replace(commonLandingData.getType(), "");
                                }
                                long catalogId = Long.parseLong(cat_id);
                                OustStaticVariableHandling.getInstance().setModuleClicked(true);
                                intent.putExtra("catalog_id", catalogId);
                                intent.putExtra("hasBanner", true);
                                intent.putExtra("deskDataMap", myDeskMap);
                                context.startActivity(intent);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    }
                    else if(commonLandingData.getType()!=null&&commonLandingData.getType().equalsIgnoreCase("SOCCER_SKILL")){

                        Intent intent = new Intent(OustSdkApplication.getContext(), SkillDetailActivity.class);
                        intent.putExtra("SkillId", commonLandingData.getId());
                        intent.putExtra("categoryId",commonLandingData.getCatalogCategoryId());
                        intent.putExtra("catalog_type", commonLandingData.getType());
                        intent.putExtra("category",categoryName);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        OustSdkApplication.getContext().startActivity(intent);


                    }else {
                        String cat_id=commonLandingData.getId();
                        if (cat_id.contains("COURSE")) {
                            cat_id = cat_id.replace("COURSE", "");
                        }else if(cat_id.contains("course")){
                            cat_id = cat_id.replace("course","");
                        } else if (cat_id.contains("ASSESSMENT")) {
                            cat_id = cat_id.replace("ASSESSMENT", "");
                        }else if(cat_id.contains("assessment")){
                            cat_id = cat_id.replace("assessment", "");
                        } else if (cat_id.contains("COLLECTION")) {
                            cat_id = cat_id.replace("COLLECTION", "");
                        }else if(cat_id.contains("collection")){
                            cat_id = cat_id.replace("collection", "");
                        }
                        if(!cat_id.contains(commonLandingData.getType()) || !cat_id.contains(commonLandingData.getType().toLowerCase())){
                            cat_id=commonLandingData.getType()+""+cat_id;
                        }
                        if(cat_id.contains("SURVEY")){
                            cat_id=cat_id.replace("SURVEY"," ");
                            cat_id="ASSESSMENT"+""+cat_id;
                        }
                        if (myDeskMap != null && myDeskMap.containsKey(cat_id)) {
                            commonLandingData.setId(cat_id);
                            startCatalogActivity(commonLandingData);
                        } else {
                            if(myDeskMap!=null) {
                                OustStaticVariableHandling.getInstance().setModuleClicked(true);
                                Intent intent = new Intent(context, CatalogInfoActivity.class);
                                intent.putExtra("catalog_id", commonLandingData.getId());
                                intent.putExtra("catalog_type", commonLandingData.getType());
                                intent.putExtra("deskmap", myDeskMap);
                                context.startActivity(intent);
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
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
    private void startCatalogActivity(CommonLandingData commonLandingData){
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
        } else if(commonLandingData.getType()!=null&&commonLandingData.getType().equalsIgnoreCase("SOCCER_SKILL")){

            Intent intent = new Intent(OustSdkApplication.getContext(), SkillDetailActivity.class);
            intent.putExtra("SkillId", commonLandingData.getId());
            intent.putExtra("categoryId",commonLandingData.getCatalogCategoryId());
            intent.putExtra("catalog_type", commonLandingData.getType());
            intent.putExtra("category",categoryName);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            OustSdkApplication.getContext().startActivity(intent);


        }else {
          /*  if(commonLandingData.getCourseType()!=null && commonLandingData.getCourseType().equalsIgnoreCase("Multilingual")){
                Intent intent = new Intent(OustSdkApplication.getContext(), CourseMultiLingualActivity.class);
                String id = commonLandingData.getId();
                id = id.replace("COURSE", "");
                intent.putExtra("learningId", id);
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
            }*/

            Intent intent = new Intent(OustSdkApplication.getContext(), CourseDetailScreen.class);
            String id = commonLandingData.getId();
            id = id.replace("COURSE", "");
            intent.putExtra("learningId", id);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            OustSdkApplication.getContext().startActivity(intent);
        }
    }
}
