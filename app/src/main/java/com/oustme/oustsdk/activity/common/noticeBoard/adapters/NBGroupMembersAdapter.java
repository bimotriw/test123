package com.oustme.oustsdk.activity.common.noticeBoard.adapters;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.noticeBoard.activity.NBMembersListActivity;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBGroupData;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStrings;

import java.util.List;

/**
 * Created by oust on 3/6/19.
 */

public class NBGroupMembersAdapter extends RecyclerView.Adapter<NBGroupMembersAdapter.MyViewHolder> {
    private final String TAG = "NewLeaderBoardAdapter";
    List<NBGroupData> nbGroupDataList;
    private Context context;
    private boolean isClicked = false;
    private long nbId = 0;

    public NBGroupMembersAdapter(Context context, List<NBGroupData> nbGroupDataList) {
        this.nbGroupDataList = nbGroupDataList;
        this.context = context;
    }

    public void setNbId(long nbId) {
        this.nbId = nbId;
    }

    public void notifyListChnage(List<NBGroupData> nbMemberDataList) {
        this.nbGroupDataList = nbMemberDataList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return nbGroupDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatar;
        private LinearLayout email_ll, phone_ll, main_ll;
        private TextView user_name, user_role, department, user_email, location, user_mobile;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            email_ll = itemView.findViewById(R.id.email_ll);
            phone_ll = itemView.findViewById(R.id.phone_ll);
            user_name = itemView.findViewById(R.id.user_name);
            user_role = itemView.findViewById(R.id.user_role);
            department = itemView.findViewById(R.id.department);
            user_email = itemView.findViewById(R.id.user_email);
            location = itemView.findViewById(R.id.location);
            user_mobile = itemView.findViewById(R.id.user_mobile);
            main_ll = itemView.findViewById(R.id.main_ll);
        }

        public void onBingView(NBGroupData nbGroupData) {
            try {
                user_name.setText("" + nbGroupData.getGroupName());
                department.setText(OustStrings.getString("created_by") + " " + nbGroupData.getCreatorId());

                main_ll.setOnClickListener(v -> clickView(v, nbGroupData.getGroupId()));
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }

    private void clickView(View view, final long groupId) {
        if (!isClicked) {
            isClicked = true;
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 0.94f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 0.96f);
            scaleDownX.setDuration(150);
            scaleDownY.setDuration(150);
            scaleDownX.setRepeatCount(1);
            scaleDownY.setRepeatCount(1);
            scaleDownX.setRepeatMode(ValueAnimator.REVERSE);
            scaleDownY.setRepeatMode(ValueAnimator.REVERSE);
            scaleDownX.setInterpolator(new DecelerateInterpolator());
            scaleDownY.setInterpolator(new DecelerateInterpolator());
            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleDownX).with(scaleDownY);
            scaleDown.start();
            scaleDown.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    isClicked = false;
                    openGroupMembersPage(groupId);

                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
        }
    }

    private void openGroupMembersPage(long groupId) {
        Intent intent = new Intent(context, NBMembersListActivity.class);
        intent.putExtra("groupId", groupId);
        intent.putExtra("nbId", nbId);
        context.startActivity(intent);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.nb_members_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(listItem);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.onBingView(nbGroupDataList.get(position));
    }

}


