package com.oustme.oustsdk.activity.common.leaderboard.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.UserProfileActivity;
import com.oustme.oustsdk.customviews.CustomTextView;
import com.oustme.oustsdk.response.common.LeaderBoardDataRow;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by oust on 3/6/19.
 */

public class NewLeaderBoardAdapter extends RecyclerView.Adapter<NewLeaderBoardAdapter.MyViewHolder> {
    private final String TAG = "NewLeaderBoardAdapter";
    List<LeaderBoardDataRow> mLeadersList;
    private Context context;
    private boolean isClicked = false;
    private long nbId = 0;
    private DecimalFormat formatter;

    public NewLeaderBoardAdapter(Context context, List<LeaderBoardDataRow> mLeadersList) {
        this.mLeadersList = mLeadersList;
        this.context = context;
    }


    @Override
    public int getItemCount() {
        //Log.d(TAG, "getItemCount: "+mLeadersList.size());
        return mLeadersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatar;
        private CustomTextView user_name, mTextViewUserRank, mTextViewUserCoins, mTextViewDetails;
        private ConstraintLayout rootLayout;

        MyViewHolder(View view) {
            super(view);
            avatar = view.findViewById(R.id.circleImageViewUserProfile);
            user_name = view.findViewById(R.id.textViewUserName);
            mTextViewUserRank = view.findViewById(R.id.textViewUserRank);
            mTextViewUserCoins = view.findViewById(R.id.textViewUserCoins);
            mTextViewDetails = view.findViewById(R.id.textViewDetails);
            rootLayout = view.findViewById(R.id.rootLayout);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_leader_board_row_item, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        try {
            LeaderBoardDataRow leaderData = mLeadersList.get(position);
            if(leaderData!=null) {
                if(leaderData.getAvatar()!=null)
                    Picasso.get().load(leaderData.getAvatar()).into(holder.avatar);
                if(leaderData.getRank()!=null)
                    holder.mTextViewUserRank.setText(leaderData.getRank());
                if (leaderData.getDisplayName() != null) {
                    holder.user_name.setText(leaderData.getDisplayName());
                    holder.user_name.setSelected(true);
                }

                Log.d(TAG, "onBindViewHolder: lbDetails:" + leaderData.getLbDetails() + " --- showLocation:" + OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_LB_USER_LOCATION));
                if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_LB_USER_LOCATION) && leaderData.getLbDetails() != null && !leaderData.getLbDetails().isEmpty()) {
                    holder.mTextViewDetails.setText(leaderData.getLbDetails());
                    holder.mTextViewDetails.setVisibility(View.VISIBLE);
                } else {
                    //holder.mTextViewDetails.setText("Bangalore");
                    holder.mTextViewDetails.setVisibility(View.GONE);
                }

                formatter = new DecimalFormat("##,##,###");
                String formattedScore;//= formatter.format(leaderData.getScore());
                formattedScore = OustSdkTools.formatMilliinFormat(leaderData.getScore());
                holder.mTextViewUserCoins.setText(formattedScore);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickOnItem(holder.getAdapterPosition());
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void clickOnItem(int position) {
        try {
            /*if(isLearningPathLeaderBoard==1) {*/
            if (!mLeadersList.get(position).getUserid().equalsIgnoreCase(OustSdkTools.getActiveUserData().getStudentid())) {
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra("avatar", ("" + mLeadersList.get(position).getAvatar()));
                intent.putExtra("name", ("" + mLeadersList.get(position).getDisplayName()));
                intent.putExtra("studentId", ("" + mLeadersList.get(position).getUserid()));
                intent.putExtra("xp", ("" + mLeadersList.get(position).getScore()));
                intent.putExtra("rank", ("" + mLeadersList.get(position).getRank()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                // }
            }/*else {
                if(!allArrayList.get(position).getStudentid().equalsIgnoreCase(OustSdkTools.getActiveUserData().getStudentid())) {
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra("avatar", ("" + allArrayList.get(position).getAvatar()));
                    intent.putExtra("name", ("" + allArrayList.get(position).getUserDisplayName()));
                    intent.putExtra("studentId", ("" + allArrayList.get(position).getStudentid()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }*/

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

}


