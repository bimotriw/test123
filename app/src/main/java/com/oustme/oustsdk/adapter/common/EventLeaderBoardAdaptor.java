package com.oustme.oustsdk.adapter.common;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.UserProfileActivity;
import com.oustme.oustsdk.customviews.TryRippleView;
import com.oustme.oustsdk.response.common.All;
import com.oustme.oustsdk.response.common.LeaderBoardDataRow;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shilpysamaddar on 21/03/17.
 */

public class EventLeaderBoardAdaptor extends RecyclerView.Adapter<EventLeaderBoardAdaptor.MyViewHolder>  {

    private static final String TAG = "EventLeaderBoardAdaptor";

    private int isLearningPathLeaderBoard=0;
    List<All> allArrayList = new ArrayList<All>();
    List<LeaderBoardDataRow> leaderBaordDataRowList=new ArrayList<>();
    BitmapDrawable bd=OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.profile_image));
    BitmapDrawable bd_loading=OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.profile_image_loading));
    private Context context;
    public EventLeaderBoardAdaptor(Context context, List<All> allArrayList, List<LeaderBoardDataRow> leaderBaordDataRowList, int isLearningPathLeaderBoard) {
        this.isLearningPathLeaderBoard=isLearningPathLeaderBoard;
        this.leaderBaordDataRowList=leaderBaordDataRowList;
        this.allArrayList = allArrayList;
        this.context=context;
    }
    @Override
    public int getItemCount() {
        if(isLearningPathLeaderBoard==1){
            if (leaderBaordDataRowList == null) {
                return 0;
            }
            return leaderBaordDataRowList.size();
        }else {
            if (allArrayList == null) {
                return 0;
            }
            return allArrayList.size();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView leaderBoardImage;
        TextView indexText,name,xpValue,victories,gamecountNo,leaderBoardImageText;
        TryRippleView list_mainlayout_rippleView;

        public MyViewHolder(View view) {
            super(view);
            indexText = view.findViewById(R.id.indexText);
            leaderBoardImage = view.findViewById(R.id.leaderBoardImage);
            name = view.findViewById(R.id.name);
            xpValue = view.findViewById(R.id.xpValue);
            gamecountNo= view.findViewById(R.id.gamecountNo);
            leaderBoardImageText= view.findViewById(R.id.leaderBoardImageText);
            list_mainlayout_rippleView= view.findViewById(R.id.list_mainlayout_rippleView);
        }
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.leaderboardlist, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        try {

          /*  Log.d(TAG, "onBindViewHolder: Rank: "+leaderBaordDataRowList.get(position).getRank());
            Log.d(TAG, "onBindViewHolder: XP: "+leaderBaordDataRowList.get(position).getXp());
            Log.d(TAG, "onBindViewHolder: Score: "+leaderBaordDataRowList.get(position).getScore());
            Log.d(TAG, "onBindViewHolder: LBAd: "+leaderBaordDataRowList.get(position).getLbAddInfo());
*/
            holder.indexText.setText("");
            holder.name.setText("");
            holder.leaderBoardImage.setImageDrawable(null);
            holder.xpValue.setText("");
            holder.leaderBoardImage.setVisibility(View.VISIBLE);
            holder.leaderBoardImageText.setVisibility(View.GONE);
            if(isLearningPathLeaderBoard==1)
            {
                try {
                    if (leaderBaordDataRowList.get(position).getUserid().equalsIgnoreCase(OustSdkTools.getActiveUserData().getStudentid())) {
                        String toolbarColorCode = "#ffffff";//OustPreferences.get("toolbarColorCode");
                        if (toolbarColorCode != null)
                        {
                            holder.name.setTextColor(Color.parseColor(toolbarColorCode));
                            holder.xpValue.setTextColor(Color.parseColor(toolbarColorCode));
                            holder.indexText.setTextColor(Color.parseColor(toolbarColorCode));
                            holder.itemView.setBackgroundColor(OustSdkTools.getColorBack(R.color.common_grey));
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
                holder.gamecountNo.setVisibility(View.GONE);
                holder.xpValue.setText(""+leaderBaordDataRowList.get(position).getScore());
                if (leaderBaordDataRowList.get(position).getDisplayName() != null) {
                    holder.name.setText(leaderBaordDataRowList.get(position).getDisplayName());
                }
                if (leaderBaordDataRowList.get(position).getRank() != null) {
                    holder.indexText.setText(leaderBaordDataRowList.get(position).getRank());
                }
                if ((leaderBaordDataRowList.get(position).getAvatar() != null)&&(!leaderBaordDataRowList.get(position).getAvatar().isEmpty())) {
                    if (leaderBaordDataRowList.get(position).getAvatar().startsWith("http")) {
                        Picasso.get().load(leaderBaordDataRowList.get(position).getAvatar())
                                .placeholder(bd_loading).error(bd)
                                .into(holder.leaderBoardImage);
                    } else {
                        Picasso.get().load(context.getString(R.string.oust_user_avatar_link) + leaderBaordDataRowList.get(position).getAvatar())
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
            }else {
                try {
                    if (allArrayList.get(position).getStudentid().equalsIgnoreCase(OustSdkTools.getActiveUserData().getStudentid())) {
                        String toolbarColorCode ="#ffffff";// OustPreferences.get("toolbarColorCode");
                        if (toolbarColorCode != null) {
                            holder.name.setTextColor(Color.parseColor(toolbarColorCode));
                            holder.xpValue.setTextColor(Color.parseColor(toolbarColorCode));
                            holder.indexText.setTextColor(Color.parseColor(toolbarColorCode));
                            holder.itemView.setBackgroundColor(OustSdkTools.getColorBack(R.color.common_grey));
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
                holder.gamecountNo.setVisibility(View.VISIBLE);
                if (isLearningPathLeaderBoard == 2) {
                    holder.gamecountNo.setVisibility(View.GONE);
                } else {
                    holder.gamecountNo.setText(OustStrings.getString("questionattemptedLabel") + allArrayList.get(position).getEventProgress() + "%");
                }
                if (allArrayList.get(position).getXp() != null) {
                    holder.xpValue.setText(allArrayList.get(position).getXp());
                }
                if (allArrayList.get(position).getUserDisplayName() != null) {
                    holder.name.setText(allArrayList.get(position).getUserDisplayName());
                }
                if (allArrayList.get(position).getRank() != null) {
                    holder.indexText.setText(allArrayList.get(position).getRank());
                }
                if ((allArrayList.get(position).getAvatar() != null)&&(!allArrayList.get(position).getAvatar().isEmpty()))
                {
                    if (allArrayList.get(position).getAvatar().startsWith("http"))
                    {
                        Picasso.get().load(allArrayList.get(position).getAvatar())
                                .placeholder(bd_loading).error(bd)
                                .into(holder.leaderBoardImage);
                    }
                    else
                        {
                        Picasso.get().load(context.getString(R.string.oust_user_avatar_link) + allArrayList.get(position).getAvatar())
                                .placeholder(bd_loading).error(bd).into(holder.leaderBoardImage);
                    }
                }else
                    {
                    if ((allArrayList.get(position).getUserDisplayName() != null)&&(!allArrayList.get(position).getUserDisplayName().isEmpty())) {
                        holder.leaderBoardImage.setVisibility(View.GONE);
                        holder.leaderBoardImageText.setVisibility(View.VISIBLE);
                        holder.leaderBoardImageText.setText(allArrayList.get(position).getUserDisplayName().toUpperCase());
                        setGroupImage(holder.leaderBoardImageText,allArrayList.get(position).getUserDisplayName());
                    }
                }
            }
            holder.list_mainlayout_rippleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        holder.list_mainlayout_rippleView.setOnRippleCompleteListener(new TryRippleView.OnRippleCompleteListener() {
                            @Override
                            public void onComplete(TryRippleView rippleView) {
                                if(OustStaticVariableHandling.getInstance().isEnterpriseUser()){
                                    clickOnItem(position);
                                }
                            }
                        });
                    }catch (Exception e){
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void clickOnItem(int position) {
        try {
            if(isLearningPathLeaderBoard==1) {
                if(!leaderBaordDataRowList.get(position).getUserid().equalsIgnoreCase(OustSdkTools.getActiveUserData().getStudentid())) {
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra("avatar", ("" + leaderBaordDataRowList.get(position).getAvatar()));
                    intent.putExtra("name", ("" + leaderBaordDataRowList.get(position).getDisplayName()));
                    intent.putExtra("studentId", ("" + leaderBaordDataRowList.get(position).getUserid()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }else {
                if(!allArrayList.get(position).getStudentid().equalsIgnoreCase(OustSdkTools.getActiveUserData().getStudentid())) {
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra("avatar", ("" + allArrayList.get(position).getAvatar()));
                    intent.putExtra("name", ("" + allArrayList.get(position).getUserDisplayName()));
                    intent.putExtra("studentId", ("" + allArrayList.get(position).getStudentid()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
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
            } else{
                OustSdkTools.setTxtBackgroud(txtGroupName, R.drawable.orange_round_textview);
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
