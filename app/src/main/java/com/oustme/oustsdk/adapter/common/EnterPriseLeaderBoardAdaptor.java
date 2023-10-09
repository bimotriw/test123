package com.oustme.oustsdk.adapter.common;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.UserProfileActivity;
import com.oustme.oustsdk.customviews.TryRippleView;
import com.oustme.oustsdk.response.common.LeaderBoardDataRow;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shilpysamaddar on 21/03/17.
 */

public class EnterPriseLeaderBoardAdaptor extends RecyclerView.Adapter<EnterPriseLeaderBoardAdaptor.MyViewHolder>  {
    List<LeaderBoardDataRow> leaderBaordDataRowList=new ArrayList<>();

    public EnterPriseLeaderBoardAdaptor(List<LeaderBoardDataRow> leaderBaordDataRowList) {
        this.leaderBaordDataRowList=leaderBaordDataRowList;
    }
    @Override
    public int getItemCount() {
        if (leaderBaordDataRowList == null) {
            return 0;
        }
        return leaderBaordDataRowList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView leaderBoardImage;
        TextView indexText,name,xpValue,leaderBoardImageText;
        RelativeLayout leaderboardlist_mainlayout;
        TryRippleView list_mainlayout_rippleView;

        public MyViewHolder(View view) {
            super(view);
            indexText = view.findViewById(R.id.indexText);
            leaderBoardImage = view.findViewById(R.id.leaderBoardImage);
            name = view.findViewById(R.id.name);
            xpValue = view.findViewById(R.id.xpValue);
            leaderboardlist_mainlayout= view.findViewById(R.id.leaderboardlist_mainlayout);
            leaderBoardImageText= view.findViewById(R.id.leaderBoardImageText);
            list_mainlayout_rippleView= view.findViewById(R.id.list_mainlayout_rippleView);
        }
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.enterprise_leaderboard_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder,final int position) {
        try {
            holder.indexText.setText("");
            holder.name.setText("");
            holder.leaderBoardImage.setImageDrawable(null);
            holder.xpValue.setText("");
            holder.leaderBoardImageText.setVisibility(View.GONE);
            holder.leaderBoardImage.setVisibility(View.VISIBLE);
            holder.xpValue.setText(""+leaderBaordDataRowList.get(position).getScore());
            try {
                if (leaderBaordDataRowList.get(position).getUserid().equalsIgnoreCase(OustSdkTools.getActiveUserData().getStudentid())) {
                    String toolbarColorCode = OustPreferences.get("toolbarColorCode");
                    if (toolbarColorCode != null) {
                        holder.name.setTextColor(Color.parseColor(toolbarColorCode));
                        holder.xpValue.setTextColor(Color.parseColor(toolbarColorCode));
                        holder.indexText.setTextColor(Color.parseColor(toolbarColorCode));
                    }
                    holder.name.setTypeface(OustSdkTools.getAvenirLTStdHeavy());
                    holder.indexText.setTypeface(OustSdkTools.getAvenirLTStdHeavy());
                } else {
                    holder.name.setTextColor(OustSdkTools.getColorBack(R.color.DarkGray));
                    holder.xpValue.setTextColor(OustSdkTools.getColorBack(R.color.DarkGray));
                    holder.indexText.setTextColor(OustSdkTools.getColorBack(R.color.DarkGray));
                    holder.name.setTypeface(OustSdkTools.getAvenirLTStdMedium());
                    holder.indexText.setTypeface(OustSdkTools.getAvenirLTStdMedium());
                }
            }catch (Exception e){
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            if (leaderBaordDataRowList.get(position).getDisplayName() != null) {
                holder.name.setText(leaderBaordDataRowList.get(position).getDisplayName());
            }
            if (leaderBaordDataRowList.get(position).getRank() != null) {
                holder.indexText.setText(leaderBaordDataRowList.get(position).getRank());
            }
            if ((leaderBaordDataRowList.get(position).getAvatar() != null)&&(!leaderBaordDataRowList.get(position).getAvatar().isEmpty())) {
                BitmapDrawable bd=OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.profile_image));
                BitmapDrawable bd_loading=OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.profile_image_loading));
                if (leaderBaordDataRowList.get(position).getAvatar().startsWith("http")) {
                    Picasso.get().load(leaderBaordDataRowList.get(position).getAvatar())
                            .placeholder(bd_loading).error(bd)
                            .into(holder.leaderBoardImage);
                } else {
                    Picasso.get().load(OustSdkApplication.getContext().getString(R.string.oust_user_avatar_link) + leaderBaordDataRowList.get(position).getAvatar())
                            .placeholder(bd_loading).error(bd).into(holder.leaderBoardImage);
                }
            }else {
                if ((leaderBaordDataRowList.get(position).getDisplayName() != null)&&(!leaderBaordDataRowList.get(position).getDisplayName().isEmpty())) {
                    holder.leaderBoardImage.setVisibility(View.GONE);
                    holder.leaderBoardImageText.setVisibility(View.VISIBLE);
                    holder.leaderBoardImageText.setText(leaderBaordDataRowList.get(position).getDisplayName().toUpperCase());
                    setGroupImage(holder.leaderBoardImageText,leaderBaordDataRowList.get(position).getDisplayName());
                }
            }
            holder.list_mainlayout_rippleView.setOnRippleCompleteListener(new TryRippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(TryRippleView rippleView) {
                    clickOnItem(position);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void clickOnItem(int position) {
        try {
            if (!(leaderBaordDataRowList.get(position).getUserid().equalsIgnoreCase(OustAppState.getInstance().getActiveUser().getStudentid()))) {
                Intent intent = new Intent(OustSdkApplication.getContext(), UserProfileActivity.class);
                intent.putExtra("avatar", ("" + leaderBaordDataRowList.get(position).getAvatar()));
                intent.putExtra("name", ("" + leaderBaordDataRowList.get(position).getDisplayName()));
                intent.putExtra("xp", ("" + leaderBaordDataRowList.get(position).getScore()));
                intent.putExtra("rank", ("" + leaderBaordDataRowList.get(position).getRank()));
                intent.putExtra("studentId", ("" + leaderBaordDataRowList.get(position).getUserid()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                OustSdkApplication.getContext().startActivity(intent);
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setGroupImage(TextView txtGroupName,String initial) {
        try {
            if(initial.length()>2) {
                char c = initial.toUpperCase().charAt(1);
                if (c < 'D') {
                    OustSdkTools.setTxtBackgroud(txtGroupName, R.drawable.orange_round_textview);
                } else if (c < 'I') {
                    OustSdkTools.setTxtBackgroud(txtGroupName, R.drawable.litegray_round_textview);
                } else if (c < 'O') {
                    OustSdkTools.setTxtBackgroud(txtGroupName, R.drawable.greenlite_round_textview);
                } else if (c < 'Z') {
                    OustSdkTools.setTxtBackgroud(txtGroupName, R.drawable.darkgreen_round_textview);
                } else{
                    OustSdkTools.setTxtBackgroud(txtGroupName, R.drawable.orangelite_round_textview);
                }
            }
            else{
                OustSdkTools.setTxtBackgroud(txtGroupName, R.drawable.orange_round_textview);
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}

