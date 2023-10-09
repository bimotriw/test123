package com.oustme.oustsdk.adapter.courses;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.courses.LessonsActivity;
import com.oustme.oustsdk.firebase.course.CourseCollectionData;
import com.oustme.oustsdk.interfaces.common.RowClickCallBack;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStrings;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shilpysamaddar on 09/03/17.
 */

public class NewLandingCoursesCollectionAdapter extends RecyclerView.Adapter<NewLandingCoursesCollectionAdapter.MyViewHolder> {
    private Activity activity;
    private List<CourseCollectionData> courseCollectionDataList=new ArrayList<>();
    private RowClickCallBack rowClickCallBack;
    private boolean newActivityOpened;
    public void setRowClickCallBack(RowClickCallBack rowClickCallBack) {
        this.rowClickCallBack = rowClickCallBack;
    }

    public NewLandingCoursesCollectionAdapter(Activity activity, List<CourseCollectionData> courseCollectionDataList1) {
        this.activity = activity;
        this.courseCollectionDataList = courseCollectionDataList1;
        newActivityOpened=false;
    }


    public void notifyDateChanges(List<CourseCollectionData> courseCollectionDataList1){
        this.courseCollectionDataList = courseCollectionDataList1;
        newActivityOpened=false;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return courseCollectionDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView coureseimage,course_complete_icon;
        TextView coursenametext,totaltime_text,totalenroll_text,totalcoin_text,collection_buytext;
        LinearLayout rowcoin_layout;
        CardView cource_mainrow;
        MyViewHolder(View view) {
            super(view);
            cource_mainrow= view.findViewById(R.id.cource_mainrow);
            coureseimage= view.findViewById(R.id.coureseimage);
            coursenametext= view.findViewById(R.id.coursenametext);
            totalenroll_text= view.findViewById(R.id.totalenroll_text);
            totalcoin_text= view.findViewById(R.id.totalcoin_text);
            totaltime_text= view.findViewById(R.id.totaltime_text);
            rowcoin_layout= view.findViewById(R.id.rowcoin_layout);
            course_complete_icon= view.findViewById(R.id.course_complete_icon);
            collection_buytext= view.findViewById(R.id.collection_buytext);
            coursenametext.setSelected(true);
            try {
                DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
                int scrWidth = metrics.widthPixels;
                int size=(int)activity.getResources().getDimension(R.dimen.oustlayout_dimen15);
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
    public NewLandingCoursesCollectionAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_coursecollectionrow, parent, false);
        return new NewLandingCoursesCollectionAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final NewLandingCoursesCollectionAdapter.MyViewHolder holder, final int position) {
        try {
            holder.coureseimage.setImageDrawable(null);
            holder.coursenametext.setText("");
            holder.totaltime_text.setText("0:00");
            holder.totalenroll_text.setText("0");
            holder.totalcoin_text.setText("0");
            // holder.rowcoin_layout.setVisibility(View.VISIBLE);
            // holder.rowcoin_layout.setVisibility(View.GONE);
            holder.collection_buytext.setText(OustStrings.getString("buy_text"));
            holder.course_complete_icon.setBackgroundColor(OustSdkTools.getColorBack(R.color.Orange));
            if((courseCollectionDataList.get(position).getBanner()!=null)&&(!courseCollectionDataList.get(position).getBanner().isEmpty())){
                if(OustSdkTools.checkInternetStatus()){
                    Picasso.get().load(courseCollectionDataList.get(position).getBanner())
                            .placeholder(R.drawable.roundedcornergraysolid).error(R.drawable.roundedcornergraysolid)
                            .into(holder.coureseimage);
                }else {
                    Picasso.get().load(courseCollectionDataList.get(position).getBanner())
                            .placeholder(R.drawable.roundedcornergraysolid).error(R.drawable.roundedcornergraysolid)
                            .networkPolicy(NetworkPolicy.OFFLINE).into(holder.coureseimage);
                }
            }
            if(courseCollectionDataList.get(position).getName()!=null){
                holder.coursenametext.setText(courseCollectionDataList.get(position).getName()+"\n ");
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
                            Intent intent=new Intent(activity, LessonsActivity.class);
                            intent.putExtra("collectionId",(""+courseCollectionDataList.get(position).getCollectionId()));
                            activity.startActivity(intent);
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

}
