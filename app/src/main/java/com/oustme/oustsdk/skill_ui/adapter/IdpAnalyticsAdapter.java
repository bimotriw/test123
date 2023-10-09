package com.oustme.oustsdk.skill_ui.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.R;
import com.oustme.oustsdk.skill_ui.model.UserSoccerIDPSkillAnalyticsData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class IdpAnalyticsAdapter extends RecyclerView.Adapter<IdpAnalyticsAdapter.ViewHolder> {

    private ArrayList<UserSoccerIDPSkillAnalyticsData> userSoccerIDPSkillAnalyticsDataArrayList = new ArrayList<>();
    public Context context;


    public void setIdpAnalyticsAdapter(ArrayList<UserSoccerIDPSkillAnalyticsData> userSoccerIDPSkillAnalyticsDataArrayList, Context context) {
        this.userSoccerIDPSkillAnalyticsDataArrayList = userSoccerIDPSkillAnalyticsDataArrayList;
        this.context = context;
        setHasStableIds(true);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.idp_target_adapter, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        viewHolder.setIsRecyclable(false);
        return viewHolder;


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        UserSoccerIDPSkillAnalyticsData userSoccerIDPSkillAnalyticsData = userSoccerIDPSkillAnalyticsDataArrayList.get(position);

        if (userSoccerIDPSkillAnalyticsData != null) {

            String idpTargetDate = userSoccerIDPSkillAnalyticsData.getIdpTargetDate() + "";
            String idpTarget = userSoccerIDPSkillAnalyticsData.getIdpTargetScore() + "";
            String userScore = userSoccerIDPSkillAnalyticsData.getBestScore() + "";

            if (userSoccerIDPSkillAnalyticsData.getIdpTargetDate() != 0) {

                Date date = new Date(userSoccerIDPSkillAnalyticsData.getIdpTargetDate());
                DateFormat format = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                format.setTimeZone(TimeZone.getDefault());
                idpTargetDate = format.format(date);
            }

            holder.idp_target.setText(idpTarget);
            holder.idp_target_date.setText(idpTargetDate);
            holder.user_score.setText(userScore);
        }


    }


    @Override
    public int getItemCount() {
        if (userSoccerIDPSkillAnalyticsDataArrayList == null) {
            return 0;
        }
        return userSoccerIDPSkillAnalyticsDataArrayList.size();
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView idp_target_date;
        TextView idp_target;
        TextView user_score;

        public ViewHolder(View itemView) {
            super(itemView);
            user_score = itemView.findViewById(R.id.user_score);
            idp_target = itemView.findViewById(R.id.idp_target);
            idp_target_date = itemView.findViewById(R.id.idp_target_date);
        }
    }
}
