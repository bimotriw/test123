package com.oustme.oustsdk.adapter.FFContest;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.FFContest.FastestFingerContestData;
import com.oustme.oustsdk.interfaces.common.RowClickCallBack;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by admin on 16/08/17.
 */

public class FFContestListAdapter extends RecyclerView.Adapter<FFContestListAdapter.MyViewHolder> {
    private List<FastestFingerContestData> fastestFingerContestDataList;
    private RowClickCallBack rowClickCallBack;

    public FFContestListAdapter(List<FastestFingerContestData> fastestFingerContestDataList,RowClickCallBack rowClickCallBack) {
        this.fastestFingerContestDataList = fastestFingerContestDataList;
        this.rowClickCallBack=rowClickCallBack;
    }

    @Override
    public int getItemCount() {
        if (fastestFingerContestDataList == null) {
            return 0;
        }
        return fastestFingerContestDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgEventBanner;
        public TextView txtContestDate,txtContestMonth,conteststatus_text;
        public RelativeLayout dateLayout;
        public RelativeLayout contenstrow_mainlayout;

        public MyViewHolder(View view) {
            super(view);
            dateLayout = view.findViewById(R.id.contest_dateLayout);
            imgEventBanner = view.findViewById(R.id.contest_eventBannerImg);
            txtContestMonth = view.findViewById(R.id.contest_month_date);
            txtContestDate = view.findViewById(R.id.contest_dates);
            contenstrow_mainlayout= view.findViewById(R.id.contenstrow_mainlayout);
            conteststatus_text= view.findViewById(R.id.conteststatus_text);
        }
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ffcontestlist_row, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder,final int position) {
        try {
            holder.imgEventBanner.setImageDrawable(null);
            holder.txtContestMonth.setText("");
            holder.txtContestDate.setText("");
            if(position == 0){
                OustSdkTools.setLayoutBackgroud(holder.dateLayout,R.drawable.contest_first_date_bgd);
            } else if(position == (fastestFingerContestDataList.size()-1)){
                OustSdkTools.setLayoutBackgroud(holder.dateLayout,R.drawable.contest_last_date_bgd);
            } else{
                OustSdkTools.setLayoutBackgroud(holder.dateLayout,R.drawable.contest_date_bgd);
            }

            SimpleDateFormat monthFormat = new SimpleDateFormat("MMM dd");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
            String startdate, startmnth;
            Date parsedstrtDate = new Date(fastestFingerContestDataList.get(position).getStartTime());
            startdate = dateFormat.format(parsedstrtDate);
            startmnth = monthFormat.format(parsedstrtDate);
            holder.txtContestMonth.setText(startmnth);
            holder.txtContestDate.setText(startdate);

            holder.contenstrow_mainlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rowClickCallBack.onMainRowClick("",position);
                }
            });

            long currentTime = System.currentTimeMillis();
            long totalContestTime=0;
            if(fastestFingerContestDataList.get(position).getqIds()!=null) {
                totalContestTime = ((fastestFingerContestDataList.get(position).getQuestionTime() * fastestFingerContestDataList.get(position).getqIds().size()) +
                        (fastestFingerContestDataList.get(position).getRestTime() * (fastestFingerContestDataList.get(position).getqIds().size() - 1)));
            }
            holder.conteststatus_text.setText("");
            if((fastestFingerContestDataList.get(position).getJoinBanner()!=null)&&(!fastestFingerContestDataList.get(position).getJoinBanner().isEmpty())){
                if (OustSdkTools.checkInternetStatus()) {
                    Picasso.get().load(fastestFingerContestDataList.get(position).getJoinBanner()).into(holder.imgEventBanner);
                } else {
                    Picasso.get().load(fastestFingerContestDataList.get(position).getJoinBanner()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.imgEventBanner);
                }
            }
            if(((totalContestTime)+fastestFingerContestDataList.get(position).getStartTime())<currentTime){
                //holder.conteststatus_text.setText("REVIEW RESULT");
                if((fastestFingerContestDataList.get(position).getRrBanner()!=null)&&(!fastestFingerContestDataList.get(position).getRrBanner().isEmpty())){
                    if (OustSdkTools.checkInternetStatus()) {
                        Picasso.get().load(fastestFingerContestDataList.get(position).getRrBanner()).into(holder.imgEventBanner);
                    } else {
                        Picasso.get().load(fastestFingerContestDataList.get(position).getRrBanner()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.imgEventBanner);
                    }
                }
            }else {
                if(fastestFingerContestDataList.get(position).isEnrolled()){
                    //holder.conteststatus_text.setText("PLAY");
                    if((fastestFingerContestDataList.get(position).getPlayBanner()!=null)&&(!fastestFingerContestDataList.get(position).getPlayBanner().isEmpty())){
                        if (OustSdkTools.checkInternetStatus()) {
                            Picasso.get().load(fastestFingerContestDataList.get(position).getPlayBanner()).into(holder.imgEventBanner);
                        } else {
                            Picasso.get().load(fastestFingerContestDataList.get(position).getPlayBanner()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.imgEventBanner);
                        }
                    }
                }else {
                    //holder.conteststatus_text.setText("JOIN NOW");
                }
            }
        }catch(Exception e) {
        }
    }


}
