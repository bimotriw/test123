package com.oustme.oustsdk.adapter.common;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.assessment.AssessmentFirebaseClass;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.interfaces.common.RowClickCallBack;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shilpysamaddar on 23/03/17.
 */

public class ArchiveCourseAdaptor extends RecyclerView.Adapter<ArchiveCourseAdaptor.MyViewHolder> {
    List<AssessmentFirebaseClass> assessmentFirebaseClasses=new ArrayList<>();
    List<CourseDataClass> allCources=new ArrayList<>();
    private int fragmentNo=0;
    private int lastPosition=0;

    private RowClickCallBack rowClickCallBack;

    public void setRowClickCallBack(RowClickCallBack rowClickCallBack) {
        this.rowClickCallBack = rowClickCallBack;
    }

    public ArchiveCourseAdaptor(List<AssessmentFirebaseClass> assessmentFirebaseClasses, List<CourseDataClass> allCources, int fragmentNo) {
        this.assessmentFirebaseClasses = assessmentFirebaseClasses;
        this.allCources = allCources;
        this.fragmentNo=fragmentNo;
        lastPosition=0;
    }


    public void notifyDateChanges(List<AssessmentFirebaseClass> assessmentFirebaseClasses, List<CourseDataClass> allCources){
        this.assessmentFirebaseClasses = assessmentFirebaseClasses;
        this.allCources = allCources;
        lastPosition=0;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(fragmentNo==0){
            return allCources.size();
        }else if(fragmentNo==1){
            return assessmentFirebaseClasses.size();
        }else {
            return 0;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView coureseimage,archive_courseicon;
        TextView coursenametext;
        CardView cource_mainrow;
        FrameLayout coursemain_framelayout;
        RelativeLayout locklayout;
        MyViewHolder(View view) {
            super(view);
            cource_mainrow= view.findViewById(R.id.cource_mainrow);
            coureseimage= view.findViewById(R.id.coureseimage);
            coursenametext= view.findViewById(R.id.coursenametext);
            coursemain_framelayout= view.findViewById(R.id.coursemain_framelayout);
            locklayout= view.findViewById(R.id.locklayout);
            archive_courseicon= view.findViewById(R.id.archive_courseicon);
            coursenametext.setSelected(true);
            try {
                DisplayMetrics metrics = OustSdkApplication.getContext().getResources().getDisplayMetrics();
                int scrWidth = metrics.widthPixels;
                int size=(int) OustSdkApplication.getContext().getResources().getDimension(R.dimen.oustlayout_dimen15);
                int imageWidth=(scrWidth);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) coureseimage.getLayoutParams();
                float h = (imageWidth * 0.34f);
                int h1 = (int) (h-size);
                params.height = h1;
                coureseimage.setLayoutParams(params);
            }catch (Exception e){
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.archive_courserow, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder,final int position) {
        try {
            holder.coureseimage.setImageDrawable(null);
            holder.coursenametext.setText("");
            holder.archive_courseicon.setVisibility(View.VISIBLE);
            if(fragmentNo==0){
                if(allCources.get(position).getCourseName()!=null){
                    holder.coursenametext.setText(allCources.get(position).getCourseName()+"\n ");
                }
                if((allCources.get(position).getBgImg()!=null)&&(!allCources.get(position).getBgImg().isEmpty())){
                    if(OustSdkTools.checkInternetStatus()) {
                        Picasso.get().load(allCources.get(position).getBgImg())
                                .placeholder(R.drawable.roundedcornergraysolid)
                                .error(R.drawable.roundedcornergraysolid)
                                .into(holder.coureseimage);
                    }else {
                        Picasso.get().load(allCources.get(position).getBgImg())
                                .placeholder(R.drawable.roundedcornergraysolid)
                                .error(R.drawable.roundedcornergraysolid)
                                .networkPolicy(NetworkPolicy.OFFLINE).into(holder.coureseimage);
                    }
                }
                if(allCources.get(position).isLocked()){
                    holder.locklayout.setVisibility(View.VISIBLE);
                    holder.locklayout.bringToFront();
                    final int color = OustSdkTools.getColorBack(R.color.MoreLiteGrayd);
                    final Drawable drawable = new ColorDrawable(color);
                    holder.coursemain_framelayout.setForeground(drawable);
                }else {
                    holder.locklayout.setVisibility(View.GONE);
                    final int color = OustSdkTools.getColorBack(R.color.fulltransparent);
                    final Drawable drawable = new ColorDrawable(color);
                    holder.coursemain_framelayout.setForeground(drawable);
                }
            }else if(fragmentNo==1){
                if((assessmentFirebaseClasses.get(position).getBanner()!=null)&&(!assessmentFirebaseClasses.get(position).getBanner().isEmpty())){
                    if(OustSdkTools.checkInternetStatus()){
                        Picasso.get().load(assessmentFirebaseClasses.get(position).getBanner())
                                .placeholder(R.drawable.roundedcornergraysolid).error(R.drawable.roundedcornergraysolid)
                                .into(holder.coureseimage);
                    }else {
                        Picasso.get().load(assessmentFirebaseClasses.get(position).getBanner())
                                .placeholder(R.drawable.roundedcornergraysolid).error(R.drawable.roundedcornergraysolid)
                                .networkPolicy(NetworkPolicy.OFFLINE).into(holder.coureseimage);
                    }
                }
                if(assessmentFirebaseClasses.get(position).getName()!=null){
                    holder.coursenametext.setText(assessmentFirebaseClasses.get(position).getName()+"\n ");
                }
                if(assessmentFirebaseClasses.get(position).isLocked()){
                    holder.locklayout.setVisibility(View.VISIBLE);
                    holder.locklayout.bringToFront();
                    final int color = OustSdkTools.getColorBack(R.color.MoreLiteGrayd);
                    final Drawable drawable = new ColorDrawable(color);
                    holder.coursemain_framelayout.setForeground(drawable);
                }else {
                    holder.locklayout.setVisibility(View.GONE);
                    final int color = OustSdkTools.getColorBack(R.color.fulltransparent);
                    final Drawable drawable = new ColorDrawable(color);
                    holder.coursemain_framelayout.setForeground(drawable);
                }
            }
            if(lastPosition<position) {
                setAnimation(holder.cource_mainrow);
            }else {
                if(lastPosition==0&&position==0){
                    setAnimation(holder.cource_mainrow);
                }
            }
            lastPosition=position;
            holder.archive_courseicon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f,0.9f);
                    ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f,0.9f);
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
                            clickOnHideIcon(position);
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

    public void clickOnHideIcon(final int position){
        if(fragmentNo==0){
            rowClickCallBack.onMainRowClick(""+allCources.get(position).getCourseId(),position);
        }else {
            rowClickCallBack.onMainRowClick(""+assessmentFirebaseClasses.get(position).getAsssessemntId(),position);
        }
    }

    private void setAnimation(View view){
        try{
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX",0.9f,1.0f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY",0.9f,1.0f);
            scaleDownX.setDuration(500);
            scaleDownY.setDuration(500);
            scaleDownX.setInterpolator(new DecelerateInterpolator());
            scaleDownY.setInterpolator(new DecelerateInterpolator());
            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleDownX).with(scaleDownY);
            scaleDown.start();
        }catch (Exception e){}
    }

    @Override
    public void onViewDetachedFromWindow(MyViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.cource_mainrow.clearAnimation();
    }

}
