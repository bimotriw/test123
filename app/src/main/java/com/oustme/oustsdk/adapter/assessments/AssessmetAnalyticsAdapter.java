package com.oustme.oustsdk.adapter.assessments;

import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.assessments.AssessmentAnalyticsActivity;
import com.oustme.oustsdk.activity.common.EventLeaderboardActivity;
import com.oustme.oustsdk.firebase.assessment.AssessmentFirebaseClass;
import com.oustme.oustsdk.response.assessment.AssessmentAnalyticsData;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by shilpysamaddar on 28/01/17.
 */
public class AssessmetAnalyticsAdapter extends RecyclerView.Adapter<AssessmetAnalyticsAdapter.DataViewHolder>{

    private List<AssessmentAnalyticsData> assessmentlist;
    AssessmentAnalyticsActivity activity;

    public AssessmetAnalyticsAdapter(AssessmentAnalyticsActivity activity,List<AssessmentAnalyticsData> assessmentAnalyticsDataList) {
        this.assessmentlist=assessmentAnalyticsDataList;
        this.activity=activity;
    }

    public void notifyAssessmentDataChange(List<AssessmentAnalyticsData> assessmentAnalyticsDataList){
        this.assessmentlist=assessmentAnalyticsDataList;
        notifyDataSetChanged();
    }


    public class DataViewHolder extends RecyclerView.ViewHolder {
        TextView assessmentname_text,assessmentenddate_text,assessmentstartdate_text;
        LinearLayout assessment_analytics_mainrow;
        public DataViewHolder(View itemView) {
            super(itemView);
            assessmentname_text= itemView.findViewById(R.id.assessmentname_text);
            assessmentenddate_text= itemView.findViewById(R.id.assessmentenddate_text);
            assessmentstartdate_text= itemView.findViewById(R.id.assessmentstartdate_text);
            assessment_analytics_mainrow= itemView.findViewById(R.id.assessment_analytics_mainrow);
        }
    }
    @Override
    public AssessmetAnalyticsAdapter.DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.assessment_analytics_row, parent, false);
        return new AssessmetAnalyticsAdapter.DataViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DataViewHolder holder, final int position) {
        try{
            holder.assessmentname_text.setText("");
            holder.assessmentstartdate_text.setText("");
            holder.assessmentenddate_text.setText("");
            if(assessmentlist.get(position).getAssessmentName()!=null) {
                holder.assessmentname_text.setText(assessmentlist.get(position).getAssessmentName());
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
            if(assessmentlist.get(position).getStartDate()!=null && !assessmentlist.get(position).getStartDate().equals("0")) {
                Date parsedDate = new Date(Long.valueOf(assessmentlist.get(position).getStartDate()));
                holder.assessmentstartdate_text.setText(dateFormat.format(parsedDate));
            }
            if(assessmentlist.get(position).getEndDate()!=null && !assessmentlist.get(position).getEndDate().equals("0")) {
                Date parsedDate = new Date(Long.valueOf(assessmentlist.get(position).getEndDate()));
                holder.assessmentenddate_text.setText(dateFormat.format(parsedDate));
            }
//            if(position<9) {
//                holder.assessmentindex_text.setText(" " + (position + 1) + ".  ");
//            }else {
//                holder.assessmentindex_text.setText(" " + (position + 1) + ". ");
//            }
            holder.assessment_analytics_mainrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AssessmentFirebaseClass assessmentFirebaseClass=new AssessmentFirebaseClass();
                    assessmentFirebaseClass.setAsssessemntId(assessmentlist.get(position).getAssessmentId());
                    assessmentFirebaseClass.setName(assessmentlist.get(position).getAssessmentName());
                    OustAppState.getInstance().setAssessmentFirebaseClass(assessmentFirebaseClass);

                    Intent intent = new Intent(activity, EventLeaderboardActivity.class);
                    intent.putExtra("isassessmentleaderboard", "true");
                    activity.startActivity(intent);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public int getItemCount() {
        if(assessmentlist==null){
            return 0;
        }
        return assessmentlist.size();
    }

}
