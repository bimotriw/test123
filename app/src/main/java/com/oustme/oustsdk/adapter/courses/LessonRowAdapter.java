package com.oustme.oustsdk.adapter.courses;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.assessments.AssessmentPlayActivity;
import com.oustme.oustsdk.activity.courses.newlearnngmap.NewLearningMapActivity;
import com.oustme.oustsdk.assessment_ui.assessmentDetail.AssessmentDetailScreen;
import com.oustme.oustsdk.firebase.course.CourseCollectionData;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.interfaces.course.RowClickPositionCallBack;
import com.oustme.oustsdk.response.common.GameType;
import com.oustme.oustsdk.room.dto.DTOUserCourseData;
import com.oustme.oustsdk.service.DownloadCourseService;
import com.oustme.oustsdk.sqlite.UserCourseScoreDatabaseHandler;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.appconstants.AppConstants;

import java.util.List;

/**
 * Created by shilpysamaddar on 03/04/17.
 */

public class LessonRowAdapter extends RecyclerView.Adapter<LessonRowAdapter.MyViewHolder> {
    private int lastPosition=0;
    private List<CourseDataClass> courseDataClassList;
    private CourseCollectionData courseCollectionData;
    private String courseColnId;
    private boolean purchased;
    private ActiveUser activeUser;
    private boolean[] courseDownloadData;
    private int totalRowSize=0;
    private RowClickPositionCallBack rowClickPositionCallBack;
    private int currentCourseNo=0;
    public void setCurrentCourseNo(int currentCourseNo) {
        this.currentCourseNo = currentCourseNo;
    }
    public void setRowClickPositionCallBack(RowClickPositionCallBack rowClickPositionCallBack) {
        this.rowClickPositionCallBack = rowClickPositionCallBack;
    }

    public LessonRowAdapter(CourseCollectionData courseCollectionData, String courseColnId, boolean purchased, boolean[] courseDownloadData) {
        this.courseDataClassList = courseCollectionData.getCourseDataClassList();
        this.courseCollectionData=courseCollectionData;
        this.purchased=purchased;
        this.courseColnId=courseColnId;
        lastPosition=0;
        activeUser= OustAppState.getInstance().getActiveUser();
        this.courseDownloadData=courseDownloadData;
    }

    public void onCollectionDataChanged(CourseCollectionData courseCollectionData,String courseColnId,boolean purchased,boolean[] courseDownloadData){
        this.courseDataClassList = courseCollectionData.getCourseDataClassList();
        this.courseCollectionData=courseCollectionData;
        this.courseColnId=courseColnId;
        this.purchased=purchased;
        lastPosition=0;
        this.courseDownloadData=courseDownloadData;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lessons_row, parent, false);
        return new MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout bottom_line,topSpace,lessonrow_imgview_layout,mainlessonrow_imgview_layout,lessonrow_icon_layout,bottomline,lessonrow_mainlayout,downloadcourse_gifimg;
        ImageView lessonrow_lock,lessonrow_info,downloadcourse_img,lessonrow_imgview,lessonrow_star;
        TextView lessonrow_rating,lessonrow_name,lessonrow_level,downloadcourse_text,assessmnet_text ;
        ProgressBar course_progress;
        public MyViewHolder(View view) {
            super(view);
            bottom_line= view.findViewById(R.id.bottom_line);
            topSpace= view.findViewById(R.id.topSpace);
            lessonrow_imgview_layout= view.findViewById(R.id.lessonrow_imgview_layout);
            mainlessonrow_imgview_layout= view.findViewById(R.id.mainlessonrow_imgview_layout);
            lessonrow_icon_layout= view.findViewById(R.id.lessonrow_icon_layout);
            bottomline= view.findViewById(R.id.bottomline);

            downloadcourse_img= view.findViewById(R.id.downloadcourse_img);
            downloadcourse_gifimg= view.findViewById(R.id.downloadcourse_gifimg);
            lessonrow_lock= view.findViewById(R.id.lessonrow_lock);
            lessonrow_info= view.findViewById(R.id.lessonrow_info);

            lessonrow_rating= view.findViewById(R.id.lessonrow_rating);
            lessonrow_name= view.findViewById(R.id.lessonrow_name);
            lessonrow_level= view.findViewById(R.id.lessonrow_level);
            lessonrow_mainlayout= view.findViewById(R.id.lessonrow_mainlayout);
            course_progress= view.findViewById(R.id.course_progress);
            downloadcourse_text= view.findViewById(R.id.downloadcourse_text);
            lessonrow_imgview= view.findViewById(R.id.lessonrow_imgview);
            assessmnet_text= view.findViewById(R.id.assessmnet_text);
            lessonrow_star= view.findViewById(R.id.lessonrow_star);
        }
    }
    @Override
    public int getItemCount() {
        if(courseDataClassList!=null){
            if(courseCollectionData.getMappedAssessmentId()>0){
                totalRowSize= (courseDataClassList.size()+1);
            }else {
                totalRowSize= courseDataClassList.size();
            }
        }
        return totalRowSize;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if(purchased) {
            if (currentCourseNo > (position+1)) {
                OustSdkTools.setLayoutBackgroud(holder.lessonrow_imgview_layout, R.drawable.learning_lockedlevel);
            } else if (currentCourseNo == (position+1)) {
                OustSdkTools.setLayoutBackgroud(holder.lessonrow_imgview_layout, R.drawable.currentlevel_back);
            } else {
                OustSdkTools.setLayoutBackgroud(holder.lessonrow_imgview_layout, R.drawable.learing_unlocklevel);
            }
        }else {
            OustSdkTools.setLayoutBackgroud(holder.lessonrow_imgview_layout, R.drawable.learing_unlocklevel);
        }
        holder.lessonrow_imgview_layout.setAnimation(null);
        holder.topSpace.setVisibility(View.GONE);
        holder.bottom_line.setVisibility(View.VISIBLE);
        if(position==0){
            holder.topSpace.setVisibility(View.VISIBLE);
        } else if(position==(totalRowSize-1)){
            holder.bottom_line.setVisibility(View.GONE);
        }
        if((position==(totalRowSize-1))&&(courseCollectionData.getMappedAssessmentId()>0)) {
            holder.assessmnet_text.setVisibility(View.VISIBLE);
            holder.assessmnet_text.setText("A");
            holder.lessonrow_imgview.setVisibility(View.GONE);
            holder.downloadcourse_img.setVisibility(View.GONE);
            holder.downloadcourse_gifimg.setVisibility(View.GONE);
            OustSdkTools.setLayoutBackgroud(holder.lessonrow_imgview_layout, R.drawable.yellow_round);
            holder.downloadcourse_text.setText("");
            if((courseCollectionData.getAssessmentCompletionDate()!=null)&&(!courseCollectionData.getAssessmentCompletionDate().isEmpty())){
                holder.course_progress.setProgress(100);
            }
            holder.lessonrow_info.setVisibility(View.GONE);
            holder.lessonrow_rating.setText("");
            holder.lessonrow_star.setVisibility(View.GONE);
            holder.lessonrow_name.setText(OustStrings.getString("assessment"));
            holder.lessonrow_level.setVisibility(View.GONE);
            if (!courseCollectionData.isAssessmentLoacked()) {
                holder.lessonrow_lock.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_lock_open));//ic_lock_open
            } else {
                holder.lessonrow_lock.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_lock_img));
            }
        }else {
            holder.assessmnet_text.setVisibility(View.VISIBLE);
            holder.assessmnet_text.setText(""+(position+1));
            holder.lessonrow_imgview.setVisibility(View.GONE);
            holder.lessonrow_info.setVisibility(View.GONE);
            holder.lessonrow_star.setVisibility(View.VISIBLE);
            holder.lessonrow_level.setVisibility(View.VISIBLE);
            if (courseDataClassList.get(position).isLocked()) {
                holder.lessonrow_lock.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_lock_img));
            } else {
                holder.lessonrow_lock.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_lock_open));
            }
            if (courseDataClassList.get(position).getCourseName() != null) {
                holder.lessonrow_name.setText(courseDataClassList.get(position).getCourseName());
            }
            holder.lessonrow_level.setText("" + courseDataClassList.get(position).getNumLevels() + " Levels");
            if (courseDataClassList.get(position).getRating() > 0) {
                holder.lessonrow_rating.setText("" + courseDataClassList.get(position).getRating());
            }
            holder.course_progress.setProgress((int) courseDataClassList.get(position).getUserCompletionPercentage());
            try {
                String s1 = "" + activeUser.getStudentKey() + "" + courseDataClassList.get(position).getCourseId();
                long courseUniqNo = Long.parseLong(s1);

                UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler=new UserCourseScoreDatabaseHandler();
                DTOUserCourseData userCourseData = userCourseScoreDatabaseHandler.getScoreById(courseUniqNo);
                if (userCourseData != null && userCourseData.isDownloading()) {
                    if (userCourseData.getDownloadCompletePercentage() == 100) {
                        holder.downloadcourse_text.setText("");
                        holder.downloadcourse_img.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_cloud_download));
                        holder.downloadcourse_gifimg.setVisibility(View.GONE);
                        holder.downloadcourse_img.setVisibility(View.VISIBLE);
                        holder.downloadcourse_img.setColorFilter(OustSdkTools.getColorBack(R.color.LiteGreen));
                        holder.downloadcourse_gifimg.setAnimation(null);
                    } else {
                        holder.downloadcourse_text.setText(userCourseData.getDownloadCompletePercentage() + "%");
                        holder.downloadcourse_img.setVisibility(View.GONE);
                        holder.downloadcourse_gifimg.setVisibility(View.VISIBLE);
                        if (courseDownloadData[position] == false) {
                            rowClickPositionCallBack.onMainRowClick(position);
                            courseDownloadData[position] = true;
                            Intent intent = new Intent(OustSdkApplication.getContext(), DownloadCourseService.class);
                            Gson gson = new Gson();
                            String str = gson.toJson(courseDataClassList.get(position));
                            intent.putExtra("courseDataStr", str);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            OustSdkApplication.getContext().startService(intent);
                            setCardAnimation(holder.downloadcourse_gifimg);
                        }

                    }
                } else {
                    boolean downloaded=true;
                    if((userCourseData!=null)&&(userCourseData.getUserLevelDataList()!=null)&&(userCourseData.getUserLevelDataList().size()>0)){
                        for(int i=0;i<userCourseData.getUserLevelDataList().size();i++){
                            if(userCourseData.getUserLevelDataList().get(i).getCompletePercentage()<100){
                                downloaded=false;
                                break;
                            }
                        }
                    }else {
                        downloaded=false;
                    }
                    holder.downloadcourse_text.setText("");
                    holder.downloadcourse_img.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_cloud_download));
                    if(downloaded){
                        holder.downloadcourse_img.setColorFilter(OustSdkTools.getColorBack(R.color.LiteGreen));
                        holder.downloadcourse_gifimg.setVisibility(View.GONE);
                        holder.downloadcourse_img.setVisibility(View.VISIBLE);
                        userCourseData.setDownloadCompletePercentage(100);
                        userCourseScoreDatabaseHandler.addUserScoreToRealm(userCourseData,courseUniqNo);

                    }else {
                        holder.downloadcourse_img.setColorFilter(OustSdkTools.getColorBack(R.color.LiteGray));
                        holder.downloadcourse_gifimg.setVisibility(View.GONE);
                        holder.downloadcourse_img.setVisibility(View.VISIBLE);
                    }
                }
                if(!OustSdkTools.checkInternetStatus()) {
                    if ((userCourseData != null) && (userCourseData.getPresentageComplete() > 0)) {
                        holder.course_progress.setProgress((int) userCourseData.getPresentageComplete());
                    }
                }
            } catch (Exception e) {
            }

            holder.lessonrow_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rowClickPositionCallBack.onIconClick(position);
                }
            });
            holder.downloadcourse_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if(courseCollectionData.isPurchased()) {
                            if (!courseDownloadData[position]) {
                                rowClickPositionCallBack.onMainRowClick(position);
                                String s1 = "" + activeUser.getStudentKey() + "" + courseDataClassList.get(position).getCourseId();
                                long courseUniqNo = Long.parseLong(s1);
                                UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler=new UserCourseScoreDatabaseHandler();
                                DTOUserCourseData userCourseData1 = userCourseScoreDatabaseHandler.getScoreById(courseUniqNo);
                                if(userCourseData1.getDownloadCompletePercentage()<100) {
                                    userCourseData1.setDownloading(true);
                                    userCourseScoreDatabaseHandler.addUserScoreToRealm(userCourseData1,courseUniqNo);
                                    Intent intent = new Intent(OustSdkApplication.getContext(), DownloadCourseService.class);
                                    Gson gson = new Gson();
                                    String str = gson.toJson(courseDataClassList.get(position));
                                    intent.putExtra("courseDataStr", str);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    OustSdkApplication.getContext().startService(intent);
                                    holder.downloadcourse_img.setVisibility(View.GONE);
                                    holder.downloadcourse_gifimg.setVisibility(View.VISIBLE);
                                    setCardAnimation(holder.downloadcourse_gifimg);
                                    courseDownloadData[position] = true;
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            });
        }
        holder.lessonrow_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((position==(totalRowSize-1))&&(courseCollectionData.getMappedAssessmentId()>0)) {
                    if ((!courseCollectionData.isAssessmentLoacked())) {
                        gotoAssessmentPage((int)courseCollectionData.getMappedAssessmentId());
                    }
                }else {
                    Intent intent = new Intent(OustSdkApplication.getContext(), NewLearningMapActivity.class);
                    intent.putExtra("learningId", ("" + courseDataClassList.get(position).getCourseId()));
                    intent.putExtra("courseColnId", courseColnId);
                    intent.putExtra("purchased", purchased);
                    intent.putExtra("locked",courseDataClassList.get(position).isLocked());
                    if(courseDataClassList.get(position).isNeedsAck()&&(!courseDataClassList.get(position).isAcknowLedged())){
                        if(!courseDataClassList.get(position).isEnrolled()) {
                            if (courseDataClassList.get(position).getCourseDesclaimerData() != null) {
                                Gson gson = new Gson();
                                String desclaimerData = gson.toJson(courseDataClassList.get(position).getCourseDesclaimerData());
                                intent.putExtra("desclaimerData", desclaimerData);
                            }
                        }
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    OustSdkApplication.getContext().startActivity(intent);
                }
            }
        });
        if (lastPosition < position) {
            setAnimation(holder.mainlessonrow_imgview_layout, position);
            lastPosition = position;
        } else {
            if (lastPosition == 0 && position == 0) {
                setAnimation(holder.mainlessonrow_imgview_layout, position);
            }
        }
    }

    public void gotoAssessmentPage(int assessmentId){
        ActiveUser activeUser=OustAppState.getInstance().getActiveUser();
        if((activeUser!=null)&&(activeUser.getStudentid()!=null)){}else {
            String activeUserGet = OustPreferences.get("userdata");
            activeUser = OustSdkTools.getActiveUserData(activeUserGet);
            OustAppState.getInstance().setActiveUser(activeUser);
        }
        Intent intent;
        if(OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI)){
            intent = new Intent(OustSdkApplication.getContext(), AssessmentDetailScreen.class);
        }else{
            intent = new Intent(OustSdkApplication.getContext(), AssessmentPlayActivity.class);
        }
        if((courseColnId!=null)&&(!courseColnId.isEmpty())){
            intent.putExtra("courseColnId",courseColnId);
        }
        ActiveGame activeGame=new ActiveGame();
        activeGame.setIsLpGame(false);
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
        activeGame.setGrade(activeUser.getGrade());
        activeGame.setGroupId("");
        intent.putExtra("assessmentId", (""+assessmentId));
        Gson gson=new Gson();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("ActiveGame", gson.toJson(activeGame));
        try{
            int sdk = Build.VERSION.SDK_INT;
            if (sdk > Build.VERSION_CODES.JELLY_BEAN) {
                Bundle translateBundle = ActivityOptions.makeCustomAnimation(OustSdkApplication.getContext(), R.anim.anim_slidein, R.anim.activityb_anim).toBundle();
                OustSdkApplication.getContext().startActivity(intent, translateBundle);
            } else {
                OustSdkApplication.getContext().startActivity(intent);
            }
        }catch (Exception e){
            OustSdkApplication.getContext().startActivity(intent);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void setAnimation(View view,int position){
        try{
            view.setAlpha(0);
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX",0f,1.0f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY",0f,1.0f);
            ObjectAnimator alphaAnim=ObjectAnimator.ofFloat(view, "alpha",0f,1.0f);
            scaleDownX.setDuration(250);
            scaleDownY.setDuration(250);
            alphaAnim.setDuration(250);
            scaleDownX.setInterpolator(new DecelerateInterpolator());
            scaleDownY.setInterpolator(new DecelerateInterpolator());
            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleDownX).with(alphaAnim).with(scaleDownY);
            scaleDown.setStartDelay(((position*160)));
            scaleDown.start();
        }catch (Exception e){}
    }

    private void setCardAnimation(View view) {
        try {
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "translationY", 0.0f, -7.0f);
            scaleDownX.setDuration(2000);
            scaleDownX.setRepeatCount(ValueAnimator.REVERSE);
            scaleDownX.setRepeatCount(ValueAnimator.INFINITE);
            scaleDownX.setInterpolator(new BounceInterpolator());
            scaleDownX.start();
        } catch (Exception e) {}
    }
}
