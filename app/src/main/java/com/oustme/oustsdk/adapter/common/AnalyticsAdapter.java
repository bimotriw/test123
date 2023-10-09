package com.oustme.oustsdk.adapter.common;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.interfaces.common.RowClickCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shilpysamaddar on 21/03/17.
 */

public class AnalyticsAdapter extends RecyclerView.Adapter<AnalyticsAdapter.DataViewHolder>{
    private List<String> courseList = new ArrayList<>();

    private RowClickCallBack rowClickCallBack;

    public void setRowClickCallBack(RowClickCallBack rowClickCallBack) {
        this.rowClickCallBack = rowClickCallBack;
    }

    public AnalyticsAdapter(List<String> completedCourseList) {
        this.courseList=completedCourseList;
    }

    public class DataViewHolder extends RecyclerView.ViewHolder {
        TextView analyticsText;
        LinearLayout analytics_mainrow;
        public DataViewHolder(View itemView) {
            super(itemView);
            analyticsText= itemView.findViewById(R.id.textData);
            analytics_mainrow= itemView.findViewById(R.id.analytics_mainrow);
        }
    }
    @Override
    public DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.analytics_data, parent, false);

        return new DataViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DataViewHolder holder, final int position) {
        try{
            holder.analyticsText.setText(courseList.get(position));
            holder.analytics_mainrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rowClickCallBack.onMainRowClick(courseList.get(position),position);
                }
            });
        }catch (Exception e){}
    }

    @Override
    public int getItemCount() {
        if(courseList==null){
            return 0;
        }
        return courseList.size();
    }

}
