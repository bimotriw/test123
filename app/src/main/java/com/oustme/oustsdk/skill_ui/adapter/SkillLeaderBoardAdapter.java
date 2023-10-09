package com.oustme.oustsdk.skill_ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.R;
import com.oustme.oustsdk.skill_ui.model.LeaderBoardData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SkillLeaderBoardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<LeaderBoardData> leaderBoardDataArrayList = new ArrayList<>();
    public Context context;


    public void setSkillLeaderBoardAdapter(ArrayList<LeaderBoardData> leaderBoardDataArrayList, Context context) {
        this.leaderBoardDataArrayList = leaderBoardDataArrayList;
        this.context = context;
        setHasStableIds(true);

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v;
        switch (viewType) {
            case 0:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_adapter, parent, false);
                ViewHolder viewHolder = new ViewHolder(v);
                viewHolder.setIsRecyclable(false);
                return viewHolder;

            default:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_normal, parent, false);
                NormalViewHolder normalviewHolder = new NormalViewHolder(v);
                normalviewHolder.setIsRecyclable(false);
                return normalviewHolder;

        }


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        LeaderBoardData leaderBoardData = leaderBoardDataArrayList.get(position);


        if (leaderBoardData != null) {
            String rank_user_image = leaderBoardData.getAvatar();
            String rank_user_rank = leaderBoardData.getRank() + "";
            String displayName = "" + leaderBoardData.getDisplayName();
            String score = "" + leaderBoardData.getUserScore();

            switch (holder.getItemViewType()) {

                case 0:
                    ViewHolder viewHolder = (ViewHolder) holder;
                    if (rank_user_image != null && !rank_user_image.isEmpty()) {
                        Picasso.get().load(rank_user_image)
                                .into(viewHolder.user_image);
                    }


                    viewHolder.user_name.setText(displayName);
                    viewHolder.user_score.setText(score);
                    viewHolder.serial_no.setText(rank_user_rank);

                    if (position == 0) {


                        viewHolder.leaderboard_border.setBackground(setBorderColor(context.getResources().getDrawable(R.drawable.card_rounded_ten), "#EFA139"));

                    }
                    if (position == 1) {

                        viewHolder.leaderboard_border.setBackground(setBorderColor(context.getResources().getDrawable(R.drawable.card_rounded_ten), "#68D4F6"));

                    }
                    if (position == 2) {

                        viewHolder.leaderboard_border.setBackground(setBorderColor(context.getResources().getDrawable(R.drawable.card_rounded_ten), "#54D5B6"));

                    }
                    break;
                default:
                    NormalViewHolder normalviewHolder = (NormalViewHolder) holder;
                    if (rank_user_image != null && !rank_user_image.isEmpty()) {
                        Picasso.get().load(rank_user_image)
                                .into(normalviewHolder.user_image);
                    }


                    normalviewHolder.user_name.setText(displayName);
                    normalviewHolder.user_score.setText(score);
                    normalviewHolder.serial_no.setText(rank_user_rank);
                    break;


            }


            /*   if (position > 2) {
             *//* holder.serial_no.setTextColor(Color.parseColor("#212121"));
                holder.user_name.setTextColor(Color.parseColor("#212121"));
                holder.user_score.setTextColor(Color.parseColor("#212121"));
                holder.score_text.setTextColor(Color.parseColor("#212121"));

                holder.leaderboard_border.setBackground(setBorderColor(context.getResources().getDrawable(R.drawable.rounded_10), "#FFFFFF"));*//*

            }*/
           /* if (position == 0) {

                setFillColor(holder.leaderboard_border, "#F39C12");

            }
            if (position == 1) {

                setFillColor(holder.leaderboard_border, "#008000");

            }
            if (position == 2) {

                setFillColor(holder.leaderboard_border, "#800080");

            }

            if(position>2){

                setFillColor(holder.leaderboard_border, "#F5918F8F");

            }*/
        }


    }


    @Override
    public int getItemCount() {
        if (leaderBoardDataArrayList == null) {
            return 0;
        }
        return leaderBoardDataArrayList.size() - 1;
    }


    @Override
    public int getItemViewType(int position) {
        if (position > 2) {
            return 1;
        } else {
            return 0;
        }
        //  return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout leaderboard_border;
        TextView serial_no;
        CircleImageView user_image;
        TextView user_name;
        TextView user_score;


        public ViewHolder(View itemView) {
            super(itemView);
            user_score = itemView.findViewById(R.id.user_score);
            user_name = itemView.findViewById(R.id.user_name);
            user_image = itemView.findViewById(R.id.user_image);
            serial_no = itemView.findViewById(R.id.serial_no);
            leaderboard_border = itemView.findViewById(R.id.leaderboard_border);
        }
    }

    public static class NormalViewHolder extends RecyclerView.ViewHolder {

        LinearLayout leaderboard_border;
        TextView serial_no;
        CircleImageView user_image;
        TextView user_name;
        TextView user_score;
        TextView score_text;


        public NormalViewHolder(View itemView) {
            super(itemView);
            score_text = itemView.findViewById(R.id.score_text);
            user_score = itemView.findViewById(R.id.user_score);
            user_name = itemView.findViewById(R.id.user_name);
            user_image = itemView.findViewById(R.id.user_image);
            serial_no = itemView.findViewById(R.id.serial_no);
            leaderboard_border = itemView.findViewById(R.id.leaderboard_border);
        }
    }

    private void setFillColor(LinearLayout leaderboard_border, String color) {

        GradientDrawable borderBackground = (GradientDrawable) leaderboard_border.getBackground();
        borderBackground.setStroke(3, Color.parseColor(color));
        leaderboard_border.setBackground(borderBackground);
    }

    private Drawable setBorderColor(Drawable drawable, String color) {


        DrawableCompat.setTint(
                DrawableCompat.wrap(drawable),
                Color.parseColor(color)
        );

        return drawable;
    }


}
