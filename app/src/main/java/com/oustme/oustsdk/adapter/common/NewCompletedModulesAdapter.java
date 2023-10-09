package com.oustme.oustsdk.adapter.common;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.assessments.AssessmentPlayActivity;
import com.oustme.oustsdk.activity.courses.CourseMultiLingualActivity;
import com.oustme.oustsdk.activity.courses.LessonsActivity;
import com.oustme.oustsdk.activity.courses.newlearnngmap.NewLearningMapActivity;
import com.oustme.oustsdk.assessment_ui.assessmentDetail.AssessmentDetailScreen;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.firebase.course.MultilingualCourse;
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
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NewCompletedModulesAdapter extends RecyclerView.Adapter<NewCompletedModulesAdapter.MyViewHolder> {
    private ArrayList<CommonLandingData> commonLandingDataList;
    private Context mContext;

    public NewCompletedModulesAdapter(Context mContext, ArrayList<CommonLandingData> commonLandingDataList) {
        this.commonLandingDataList = commonLandingDataList;
        this.mContext = mContext;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView rowbanner_image, rowicon_image, coins_icon;
        private TextView coursenametext, total_enrolled_count, totaloc_text, moduleprogress;
        private RelativeLayout main_layout;
        private RelativeLayout cource_mainrow, main_card_ll, module_progress_ll, bannerimage_layout;

        MyViewHolder(View view) {
            super(view);
            cource_mainrow = view.findViewById(R.id.cource_mainrow);
            coursenametext = view.findViewById(R.id.coursenametext);
            total_enrolled_count = view.findViewById(R.id.total_enrolled_count);
            totaloc_text = view.findViewById(R.id.totaloc_text);
            rowbanner_image = view.findViewById(R.id.rowbanner_image);
            main_card_ll = view.findViewById(R.id.main_card_ll);
            rowicon_image = view.findViewById(R.id.rowicon_image);
            main_layout = view.findViewById(R.id.main_layout);
//            moduleprogress=(TextView) view.findViewById(R.id.moduleprogress);
//            module_progress_ll=(RelativeLayout) view.findViewById(R.id.module_progress_ll);
            bannerimage_layout = view.findViewById(R.id.bannerimage_layout);

            try {
                coins_icon = view.findViewById(R.id.coins_icon);
                if (OustPreferences.getAppInstallVariable("showCorn")) {
                    coins_icon.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_coins_corn));
                }else{
                    OustSdkTools.setImage(coins_icon, OustSdkApplication.getContext().getResources().getString(R.string.coins_icon));
                }
            }catch (Exception e){
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

        }
    }

    @Override
    public NewCompletedModulesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.landing_item_3, parent, false);
        return new NewCompletedModulesAdapter.MyViewHolder(itemView);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(final NewCompletedModulesAdapter.MyViewHolder holder, final int position) {
        try {
                holder.cource_mainrow.setVisibility(View.VISIBLE);
                CommonLandingData commonLandingData = commonLandingDataList.get(position);
                holder.coursenametext.setVisibility(View.GONE);
                holder.rowbanner_image.setImageBitmap(null);
                holder.rowicon_image.setImageBitmap(null);
                OustSdkTools.setLayoutBackgroudDrawable(holder.main_card_ll, OustSdkTools.getImgDrawable(R.drawable.roundedcorner_litegrey));
                if ((commonLandingData.getIcon() != null) && (!commonLandingData.getIcon().isEmpty()) && (!commonLandingData.getIcon().equalsIgnoreCase("null"))) {
                    holder.rowbanner_image.setVisibility(View.GONE);
                    holder.rowicon_image.setVisibility(View.VISIBLE);
                    if (OustSdkTools.checkInternetStatus()) {
                        Picasso.get().load(commonLandingData.getIcon()).into(holder.rowicon_image);
                    } else {
                        Picasso.get().load(commonLandingData.getIcon()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.rowicon_image);
                    }
                } else if ((commonLandingData.getBanner() != null) && (!commonLandingData.getBanner().isEmpty())) {
                    holder.rowicon_image.setVisibility(View.GONE);
                    holder.rowbanner_image.setVisibility(View.VISIBLE);
                    if (OustSdkTools.checkInternetStatus()) {
                        Picasso.get().load(commonLandingData.getBanner()).into(holder.rowbanner_image);
                    } else {
                        Picasso.get().load(commonLandingData.getBanner()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.rowbanner_image);
                    }
                }
                if (commonLandingData.getName() != null) {
                    holder.coursenametext.setVisibility(View.VISIBLE);
                    holder.coursenametext.setText(commonLandingData.getName());
                } else {
                    holder.coursenametext.setVisibility(View.VISIBLE);
                    holder.coursenametext.setText("");
                }
                if (commonLandingData.getEnrollCount() > 0) {
                    holder.total_enrolled_count.setText("" + commonLandingData.getEnrollCount());
                } else {
                    holder.total_enrolled_count.setText("0");
                }
                if (commonLandingData.getOc() > 0) {
                    holder.totaloc_text.setText("" + commonLandingData.getOc());
                } else {
                    holder.totaloc_text.setText("0");
                }
                holder.cource_mainrow.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("NewApi")
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
                                launchActivity(commonLandingDataList.get(holder.getAdapterPosition()));
                                //clickonRow(holder.getAdapterPosition());
                                /*if (!OustStaticVariableHandling.getInstance().isModuleClicked()) {
                                    clickonRow(holder.getAdapterPosition());
                                }*/

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

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
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


    @Override
    public int getItemCount() {
        if (commonLandingDataList != null)
            return commonLandingDataList.size();
        else
            return 0;
    }

    private void launchActivity(CommonLandingData commonLandingData) {
        OustStaticVariableHandling.getInstance().setModuleClicked(true);
        if ((commonLandingData.getType() != null) && (commonLandingData.getType().contains("COLLECTION")))
        {
            Intent intent = new Intent(OustSdkApplication.getContext(), LessonsActivity.class);
            String id = commonLandingData.getId();
            id = id.replace("COLLECTION", "");
            intent.putExtra("collectionId", id);
            intent.putExtra("banner", commonLandingData.getBanner());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            OustSdkApplication.getContext().startActivity(intent);
        }
        else if ((commonLandingData.getType() != null) && (commonLandingData.getType().contains("ASSESSMENT"))) {
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
        }
        else {
            if (commonLandingData.getCourseType() != null && commonLandingData.getCourseType().equalsIgnoreCase("Multilingual")) {
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
