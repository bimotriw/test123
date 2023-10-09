package com.oustme.oustsdk.skill_ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.R;
import com.oustme.oustsdk.skill_ui.FullScreenVideoActivity;
import com.oustme.oustsdk.skill_ui.model.SoccerSkillLevelDataList;
import com.oustme.oustsdk.skill_ui.model.UserSkillLevelAnalyticsDataList;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class SkillHistoryAdapter extends RecyclerView.Adapter<SkillHistoryAdapter.ViewHolder> {

    private ArrayList<UserSkillLevelAnalyticsDataList> userSkillLevelAnalyticsDataLists = new ArrayList<>();
    public Context context;


    public void setSkillHistoryAdapter(ArrayList<UserSkillLevelAnalyticsDataList> userSkillLevelAnalyticsDataLists, Context context) {
        this.userSkillLevelAnalyticsDataLists = userSkillLevelAnalyticsDataLists;
        this.context = context;
        setHasStableIds(true);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_skill_history, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        viewHolder.setIsRecyclable(false);
        return viewHolder;


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        UserSkillLevelAnalyticsDataList userSkillLevelAnalyticsDataList = userSkillLevelAnalyticsDataLists.get(position);

        if (userSkillLevelAnalyticsDataList != null) {

            if (userSkillLevelAnalyticsDataList.isVerifyFlag()) {
                holder.flag.setVisibility(View.VISIBLE);
                Drawable drawable = context.getResources().getDrawable(R.drawable.ic_tick_done);
                DrawableCompat.setTint(
                        DrawableCompat.wrap(drawable),
                        Color.parseColor("#34C759")
                );
                holder.flag.setImageDrawable(drawable);
            } else {
                if (userSkillLevelAnalyticsDataList.isRedFlag()) {
                    holder.flag.setVisibility(View.VISIBLE);
                    holder.flag.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_red_flag));
                } else {
                    holder.flag.setVisibility(View.GONE);
                }
            }


            holder.skill_score.setText(String.valueOf(userSkillLevelAnalyticsDataList.getUserScore()));
            if (userSkillLevelAnalyticsDataList.getLevelName() != null && !userSkillLevelAnalyticsDataList.getLevelName().isEmpty()) {
                holder.current_level.setText(userSkillLevelAnalyticsDataList.getLevelName());
            }
            if (userSkillLevelAnalyticsDataList.getUserSubmittedMediaFileName() != null && !userSkillLevelAnalyticsDataList.getUserSubmittedMediaFileName().isEmpty()) {
                holder.image_container.setVisibility(View.VISIBLE);


            }
            int levelIndex = 0;
            long scoreValue = userSkillLevelAnalyticsDataList.getUserScore();
            ArrayList<SoccerSkillLevelDataList> soccerSkillLevelDataLists = OustAppState.getInstance().getSoccerSkillLevelDataList();
            if (soccerSkillLevelDataLists != null && soccerSkillLevelDataLists.size() != 0) {

                for (int index = 0; index < soccerSkillLevelDataLists.size(); index++) {
                    if (scoreValue >= soccerSkillLevelDataLists.get(index).getScoreStartRange() &&
                            scoreValue <= soccerSkillLevelDataLists.get(index).getScoreEndRange()) {
                        levelIndex = index;
                        break;
                    } else if (scoreValue < soccerSkillLevelDataLists.get(index).getScoreStartRange()) {
                        levelIndex = index - 1;
                        break;
                    } else if (scoreValue > soccerSkillLevelDataLists.get(index).getScoreEndRange()) {
                        levelIndex = soccerSkillLevelDataLists.size() - 1;

                    }

                }

                String levelImage = soccerSkillLevelDataLists.get(levelIndex).getLevelBannerImg();
                String levelName = soccerSkillLevelDataLists.get(levelIndex).getLevelName();
                if (levelName != null && !levelName.isEmpty()) {
                    holder.current_level.setText(levelName);
                }
                if (levelImage != null && !levelImage.isEmpty()) {
                    holder.image_card.setVisibility(View.VISIBLE);
                    holder.badge_icon.setVisibility(View.VISIBLE);
                    Picasso.get().load(levelImage).into(holder.badge_icon);
                }

            }


            if (userSkillLevelAnalyticsDataList.getUserSubmissionDateTime() != null && !userSkillLevelAnalyticsDataList.getUserSubmissionDateTime().isEmpty()) {

                SimpleDateFormat responseDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
                responseDate.setTimeZone(TimeZone.getTimeZone("UTC"));
                SimpleDateFormat displayDate = new SimpleDateFormat("dd\n MMM", Locale.getDefault());
                displayDate.setTimeZone(TimeZone.getDefault());
                try {
                    Date responseDateDate = responseDate.parse(userSkillLevelAnalyticsDataList.getUserSubmissionDateTime());
                    holder.skill_date.setText(displayDate.format(responseDateDate));

                } catch (ParseException e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }

            }

            holder.image_container.setOnClickListener(v -> {

                if (userSkillLevelAnalyticsDataList.getUserSubmittedMediaFileName() != null && !userSkillLevelAnalyticsDataList.getUserSubmittedMediaFileName().isEmpty()) {

                    Intent video = new Intent(context, FullScreenVideoActivity.class);
                    video.putExtra("videoName", userSkillLevelAnalyticsDataList.getUserSubmittedMediaFileName());
                    video.putExtra("videoType", "Private");
                    context.startActivity(video);
                } else {
                    Toast.makeText(context, "No Video available", Toast.LENGTH_SHORT).show();
                }
            });


        }


    }


    @Override
    public int getItemCount() {
        if (userSkillLevelAnalyticsDataLists == null) {
            return 0;
        }
        return userSkillLevelAnalyticsDataLists.size();
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

        FrameLayout date_lay;
        TextView skill_date;
        TextView current_level;
        TextView skill_score;
        CardView image_container;
        FrameLayout image_card;
        ImageView skill_image_thumbnail;
        ImageView badge_icon;
        ImageView flag;
        LinearLayout score_level;

        public ViewHolder(View itemView) {
            super(itemView);
            score_level = itemView.findViewById(R.id.score_level);
            flag = itemView.findViewById(R.id.flag);
            badge_icon = itemView.findViewById(R.id.badge_icon);
            skill_image_thumbnail = itemView.findViewById(R.id.skill_image_thumbnail);
            image_card = itemView.findViewById(R.id.image_card);
            image_container = itemView.findViewById(R.id.image_container);
            skill_score = itemView.findViewById(R.id.skill_score);
            current_level = itemView.findViewById(R.id.current_level);
            skill_date = itemView.findViewById(R.id.skill_date);
            date_lay = itemView.findViewById(R.id.date_lay);
        }
    }
}
