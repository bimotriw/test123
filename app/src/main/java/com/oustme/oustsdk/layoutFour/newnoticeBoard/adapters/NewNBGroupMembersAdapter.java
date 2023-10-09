package com.oustme.oustsdk.layoutFour.newnoticeBoard.adapters;

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
import com.oustme.oustsdk.layoutFour.newnoticeBoard.activity.NewNBMembersListActivity;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBGroupData;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.List;

public class NewNBGroupMembersAdapter extends RecyclerView.Adapter<NewNBGroupMembersAdapter.MyViewHolder> {
    private final String TAG = "NewLeaderBoardAdapter";
    List<NewNBGroupData> nbGroupDataList;
    private Context context;
    private boolean isClicked = false;
    private long nbId = 0;

    public NewNBGroupMembersAdapter(Context context, List<NewNBGroupData> nbGroupDataList) {
        this.nbGroupDataList = nbGroupDataList;
        this.context = context;
    }

    public void setNbId(long nbId) {
        this.nbId = nbId;
    }

    public void notifyListChnage(List<NewNBGroupData> nbMemberDataList) {
        this.nbGroupDataList = nbMemberDataList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return nbGroupDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout main_ll;
        TextView user_name, department;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            user_name = itemView.findViewById(R.id.user_name);
            department = itemView.findViewById(R.id.department);
            main_ll = itemView.findViewById(R.id.main_ll);
        }

    }
    private void openGroupMembersPage(long groupId) {
        Intent intent = new Intent(context, NewNBMembersListActivity.class);
        intent.putExtra("groupId", groupId);
        intent.putExtra("nbId", nbId);
        context.startActivity(intent);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.nb_members_item_2, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        try {
            holder.user_name.setText(nbGroupDataList.get(position).getGroupName());
            holder.department.setText(nbGroupDataList.get(position).getCreatorId());

            holder.main_ll.setOnClickListener(v -> clickView(v, nbGroupDataList.get(position).getGroupId()));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
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


}


