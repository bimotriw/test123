package com.oustme.oustsdk.adapter.courses;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.course.CourseCollectionFeatureInfo;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by shilpysamaddar on 03/04/17.
 */

public class LessonFeatureAdaptor extends RecyclerView.Adapter<LessonFeatureAdaptor.MyViewHolder> {
    private Activity activity;
    private List<CourseCollectionFeatureInfo> courseCollectionFeatureInfoList;
    public LessonFeatureAdaptor(Activity activity, List<CourseCollectionFeatureInfo> courseCollectionFeatureInfoList) {
        this.activity = activity;
        this.courseCollectionFeatureInfoList = courseCollectionFeatureInfoList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout feature_iconlayout;
        ImageView feature_icon;
        TextView feature_nametext;
        public MyViewHolder(View view) {
            super(view);
            feature_iconlayout = view.findViewById(R.id.feature_iconlayout);
            feature_nametext = view.findViewById(R.id.feature_nametext);
            feature_icon = view.findViewById(R.id.feature_icon);
        }
    }

    @Override
    public LessonFeatureAdaptor.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lesson_featurerow, parent, false);
        return new LessonFeatureAdaptor.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LessonFeatureAdaptor.MyViewHolder holder, final int position) {
        try {
            if (courseCollectionFeatureInfoList != null) {
                if(courseCollectionFeatureInfoList.get(position).getName()!=null){
                    holder.feature_nametext.setText(courseCollectionFeatureInfoList.get(position).getName());
                }
                if ((courseCollectionFeatureInfoList.get(position).getIcon() != null) && (!courseCollectionFeatureInfoList.get(position).getIcon().isEmpty())) {
                    if((OustSdkTools.checkInternetStatus())&&(OustStaticVariableHandling.getInstance().isNetConnectionAvailable())) {
                        Picasso.get().load(courseCollectionFeatureInfoList.get(position).getIcon())
                                .placeholder(R.drawable.leaderboard_icon)
                                .error(R.drawable.leaderboard_icon)
                                .into(holder.feature_icon);
                    }else {
                        Picasso.get().load(courseCollectionFeatureInfoList.get(position).getIcon())
                                .placeholder(R.drawable.leaderboard_icon)
                                .error(R.drawable.leaderboard_icon).networkPolicy(NetworkPolicy.OFFLINE)
                                .into(holder.feature_icon);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public int getItemCount() {
        if(courseCollectionFeatureInfoList!=null){
            return courseCollectionFeatureInfoList.size();
        }else {
            return 0;
        }
    }
    public void dataChanged(List<CourseCollectionFeatureInfo> courseCollectionFeatureInfoList) {
        try {
            this.courseCollectionFeatureInfoList = courseCollectionFeatureInfoList;
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

}
