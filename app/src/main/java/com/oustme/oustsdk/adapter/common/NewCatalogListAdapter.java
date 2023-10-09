package com.oustme.oustsdk.adapter.common;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import com.oustme.oustsdk.activity.common.CatalogListActivity;
import com.oustme.oustsdk.activity.common.NewCatalogInfoActivity;
import com.oustme.oustsdk.activity.courses.CourseMultiLingualActivity;
import com.oustme.oustsdk.activity.courses.LessonsActivity;
import com.oustme.oustsdk.activity.courses.newlearnngmap.NewLearningMapActivity;
import com.oustme.oustsdk.assessment_ui.assessmentDetail.AssessmentDetailScreen;
import com.oustme.oustsdk.customviews.CustomTextView;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.firebase.course.MultilingualCourse;
import com.oustme.oustsdk.response.common.GameType;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.TOOL_BAR_COLOR_CODE;

/**
 * Created by oust on 10/25/17.
 */

public class NewCatalogListAdapter extends RecyclerView.Adapter<NewCatalogListAdapter.MyViewHolder> {
    private static final String TAG = "NewCatalogListAdapter";
    private final DecimalFormat formatter;
    private ArrayList<CommonLandingData> commonLandingDataList;
    private Context context;
    private boolean isMyCourse=false;
    private HashMap<String,String> myDeskMap;
    private int type=0;
    private SelectItem mListener;
    private String toolBarColor;
    private boolean isClicked;
    //Picasso picasso;

    public NewCatalogListAdapter(Context context, ArrayList<CommonLandingData> commonLandingDataList, int type){
        this.context=context;
        this.commonLandingDataList=commonLandingDataList;
        this.type=type;
        this.toolBarColor = OustPreferences.get(TOOL_BAR_COLOR_CODE);
        this.formatter = new DecimalFormat("##,##,###");
        this.mListener = (SelectItem)this.context;
        OustPreferences.saveAppInstallVariable("CLICK", false);

        /*Picasso.Builder builder = new Picasso.Builder(context);
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                try {
                    Log.e("Picasso error", "Path: " + uri.getPath());
                    Log.e("Picasso error", "Exception:" + exception.getMessage());
                    exception.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }
        });

        picasso = builder.build();
        picasso.setLoggingEnabled(true);*/

    }
    public void isMyCourse(boolean isMyCourse){
        this.isMyCourse=isMyCourse;
    }
    public void setClick(boolean isClicked)
    {
        this.isClicked = isClicked;
    }

    public void setMyDeskMap(HashMap<String,String> myDeskMap){
        this.myDeskMap=myDeskMap;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView rowbanner_image,icon_fullimage, imageViewBg;
        private TextView coursenametext,loading_text;
        private RelativeLayout cource_mainrow,main_card_ll,topSpace;
        private LinearLayout banner_layout;

        private CustomTextView customTextViewTitle, customTextViewCompletedPerc, textViewPeopleCount, textViewCoins;
        private ImageView imageViewPeopleCount,ic_grey_coin;
        private LinearLayout mLinearLayoutBackGround;

        MyViewHolder(View view) {
            super(view);
            customTextViewTitle = view.findViewById(R.id.textViewTitle);
            customTextViewCompletedPerc = view.findViewById(R.id.textViewCompletedPerc);
            textViewPeopleCount = view.findViewById(R.id.textViewPeopleCount);
            textViewCoins = view.findViewById(R.id.textViewCoinsCount);


            mLinearLayoutBackGround = view.findViewById(R.id.linearLayoutBackGround);
            imageViewBg = view.findViewById(R.id.imageViewBg);
            if(toolBarColor!=null)
            {
                customTextViewCompletedPerc.setTextColor(Color.parseColor(toolBarColor));
            }

            try {
                ic_grey_coin = view.findViewById(R.id.ic_grey_coin);
                if (OustPreferences.getAppInstallVariable("showCorn")) {
                    ic_grey_coin.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_coins_corn));
                }else{
                    ic_grey_coin.setImageResource(R.drawable.ic_coin);
                }
            }catch (Exception e){
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

        }
    }

    public void notifyAdapter(ArrayList<CommonLandingData> commonLandingDataList){
        this.commonLandingDataList=commonLandingDataList;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        /*if(type==1){*/
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_catalog_item1, parent, false);
        /*}else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.catalogitem_2, parent, false);
        }*/
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        try {
            CommonLandingData commonLandingData=commonLandingDataList.get(position);
            if(commonLandingData.getName()!=null){
                holder.customTextViewTitle.setText(commonLandingData.getName());
            }

            holder.textViewPeopleCount.setText(withSuffix(commonLandingData.getEnrollCount()));
            //holder.textViewPeopleCount.setText(formatter.format(commonLandingData.getEnrollCount())+"");
            holder.textViewCoins.setText(formatter.format(commonLandingData.getOc())+"");

            if((commonLandingData.getBanner() != null) && (!commonLandingData.getBanner().isEmpty())) {
                holder.mLinearLayoutBackGround.bringToFront();
                holder.mLinearLayoutBackGround.setVisibility(View.VISIBLE);

                String imagePath = commonLandingData.getIcon();

                if(imagePath==null || imagePath.isEmpty() || imagePath.equalsIgnoreCase("null")){
                    imagePath = commonLandingData.getBanner();
                }
                Log.d(TAG,"Course Banner url:"+imagePath+" --- icon:"+commonLandingData.getIcon()+" -- Banner:"+commonLandingData.getBanner());

                Picasso.get().setLoggingEnabled(true);
                Picasso.get().load(imagePath)
                        .memoryPolicy(MemoryPolicy.NO_CACHE )
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .placeholder(R.drawable.cpl_banner_bg)
                        .error(R.drawable.ic_catalogue_illustration)
                        .into(holder.imageViewBg);

                //picasso.load(commonLandingData.getBanner()).placeholder(R.drawable.cpl_banner_bg).error(R.drawable.ic_catalogue_illustration).into(holder.imageViewBg);
                //holder.imageViewBg.setBackgroundResource(R.drawable.rounded_corner_plain);

                /*Picasso.get().load(commonLandingData.getBanner()).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        holder.mLinearLayoutBackGround.setBackground(new BitmapDrawable(bitmap));
                        //holder.mLinearLayoutBackGround.setBackground(new BitmapDrawable(getRoundedCornerBitmap(bitmap, 20)));
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });*/
            }else{
                Log.d(TAG,"Course Banner is not available");
            }
            /*
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
                holder.loading_text.setText(OustStrings.getString("loading"));
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
            }else if ((commonLandingData.getBanner() != null) && (!commonLandingData.getBanner().isEmpty())) {
                holder.banner_layout.setVisibility(View.VISIBLE);
                if (OustSdkTools.checkInternetStatus()) {
                    Picasso.get().load(commonLandingData.getBanner()).into(holder.rowbanner_image);
                } else {
                    Picasso.get().load(commonLandingData.getBanner()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.rowbanner_image);
                }
            }else
                holder.banner_layout.setVisibility(View.VISIBLE);

                */

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!OustPreferences.getAppInstallVariable("CLICK")) {
                        OustPreferences.saveAppInstallVariable("CLICK", true);
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
                                if (!OustStaticVariableHandling.getInstance().isModuleClicked()) {
                                    //clickonRow(position);
                                    mListener.selected(commonLandingDataList.get(position));
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
                }
            });
        }catch(Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            isClicked = false;
        }
    }
    public interface SelectItem {
        void selected( CommonLandingData commonLandingData);
    }

    public static String withSuffix(long count) {
        if (count < 1000) return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));
        return String.format("%.1f %c",
                count / Math.pow(1000, exp),
                "kMGTPE".charAt(exp-1));
    }

    @Override
    public int getItemCount() {
        return commonLandingDataList.size();
    }

    public void clickonRow(int position) {
        try {
            CommonLandingData commonLandingData=commonLandingDataList.get(position);
            if(commonLandingData!=null) {
                if (isMyCourse) {
                    startCatalogActivity(commonLandingData);
                } else {
                    if ((commonLandingData.getType() != null) && (commonLandingData.getType().contains("CATEGORY"))) {
                        try {
                            if(OustSdkTools.checkInternetStatus()) {
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
                    } else {
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
                            else {
                                OustStaticVariableHandling.getInstance().setModuleClicked(true);
                                Intent intent = new Intent(context, NewCatalogInfoActivity.class);
                                intent.putExtra("catalog_id", commonLandingData.getId());
                                intent.putExtra("catalog_type", commonLandingData.getType());
                                //intent.putExtra("deskmap", myDeskMap);
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
            Intent intent = new Intent(OustSdkApplication.getContext(), AssessmentQuestionsActivity.class);
            ActiveGame activeGame = setGame(OustAppState.getInstance().getActiveUser());
            intent.putExtra("ActiveGame", gson.toJson(activeGame));
            String id = commonLandingData.getId();
            id = id.replace("ASSESSMENT", "");
            intent.putExtra("assessmentId", id);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            OustSdkApplication.getContext().startActivity(intent);
        } else {
            if(commonLandingData.getCourseType()!=null && commonLandingData.getCourseType().equalsIgnoreCase("Multilingual")){
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
            }
        }
    }
}
