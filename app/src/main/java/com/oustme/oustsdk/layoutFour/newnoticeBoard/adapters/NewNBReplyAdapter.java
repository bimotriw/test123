package com.oustme.oustsdk.layoutFour.newnoticeBoard.adapters;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.callBacks.NewNoticeBoardCommentDeleteListener;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.request.NewPostViewData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBReplyData;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.text.WordUtils;

import java.util.List;

public class NewNBReplyAdapter extends RecyclerView.Adapter<NewNBReplyAdapter.MyViewHolder> {
    private final String TAG = "NBPostCommentAdapter";
    private List<NewNBReplyData> nbReplyDataList;
    private Context context;
    //    private BitmapDrawable bd = OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.profile_image));
//    private BitmapDrawable bd_loading = OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.profile_image_loading));
    private NewNoticeBoardCommentDeleteListener listener;
    private boolean isClicked = false;


    public NewNBReplyAdapter(Context context, List<NewNBReplyData> nbReplyDataList, NewNoticeBoardCommentDeleteListener listener) {
        this.context = context;
        this.nbReplyDataList = nbReplyDataList;
        this.listener = listener;
    }

    public void notifyListChnage(List<NewNBReplyData> nbReplyDataList) {
        this.nbReplyDataList = nbReplyDataList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return nbReplyDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatar, deleteBtn;
        private TextView replied_by, relied_on, reply, designation, userRole;

        MyViewHolder(View view) {
            super(view);
            avatar = view.findViewById(R.id.avatar);
            replied_by = view.findViewById(R.id.replied_by);
            relied_on = view.findViewById(R.id.relied_on);
            reply = view.findViewById(R.id.reply);
            deleteBtn = view.findViewById(R.id.deleteBtn);
            designation = view.findViewById(R.id.designation);
            userRole = view.findViewById(R.id.userRole);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.nb_reply_item2, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        try {
//            final NewNBReplyData nbReplyData = nbReplyDataList.get(position);
            if (nbReplyDataList.get(position).hasAvatar()) {
                holder.avatar.setVisibility(View.VISIBLE);
                Picasso.get().load(nbReplyDataList.get(position).getAvatar())
                        .placeholder(R.drawable.ic_person_profile_image_nb).error(R.drawable.ic_person_profile_image_nb).into(holder.avatar);
            } else {
                holder.avatar.setVisibility(View.VISIBLE);
                Picasso.get().load(R.drawable.ic_person_profile_image_nb)
                        .placeholder(R.drawable.ic_person_profile_image_nb).error(R.drawable.ic_person_profile_image_nb).into(holder.avatar);
            }
            holder.reply.setText("" + nbReplyDataList.get(position).getReply());
            holder.replied_by.setText(WordUtils.capitalize("" + nbReplyDataList.get(position).getReplied_by()));
            holder.relied_on.setText(OustSdkTools.getDate("" + nbReplyDataList.get(position).getReplied_on()));
            if (nbReplyDataList.get(position).getId() == 0) {
                holder.deleteBtn.setVisibility(View.GONE);
            } else {
                if (OustAppState.getInstance().getActiveUser().getStudentKey().equals(nbReplyDataList.get(position).getUserKey())) {
                    holder.deleteBtn.setVisibility(View.VISIBLE);
                } else {
                    holder.deleteBtn.setVisibility(View.GONE);
                }
            }

            if (nbReplyDataList.get(position).getDesignation() != null) {
                holder.designation.setText(nbReplyDataList.get(position).getDesignation() + "  ");
            }

            if (nbReplyDataList.get(position).getUserRole() != null) {
                holder.userRole.setText(nbReplyDataList.get(position).getUserRole());
            }

            holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickView(v, nbReplyDataList.get(position));
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void clickView(View view, final NewNBReplyData nbReplyData) {
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
                    performReplyDelete(nbReplyData);
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

    private void performReplyDelete(NewNBReplyData nbReplyData) {
        NewPostViewData postViewData = new NewPostViewData();
        postViewData.setNbReplyData(nbReplyData);
        postViewData.setReplyDeleteType();
        postViewData.setTimeStamp(System.currentTimeMillis());
        listener.onDeleteComment(postViewData);
    }

}




