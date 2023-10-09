package com.oustme.oustsdk.activity.common.noticeBoard.adapters;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.noticeBoard.callBacks.NoticeBoardCommentDeleteListener;
import com.oustme.oustsdk.activity.common.noticeBoard.model.request.PostViewData;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBReplyData;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by oust on 3/6/19.
 */

public class NBReplyAdapter extends RecyclerView.Adapter<NBReplyAdapter.MyViewHolder> {
    private final String TAG = "NBPostCommentAdapter";
    private List<NBReplyData> nbReplyDataList;
    private Context context;
    private BitmapDrawable bd= OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.profile_image));
    private BitmapDrawable bd_loading=OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.profile_image_loading));
    private NoticeBoardCommentDeleteListener listener;
    private boolean isClicked = false;


    public NBReplyAdapter(Context context,List<NBReplyData> nbReplyDataList,NoticeBoardCommentDeleteListener listener) {
        this.context=context;
        this.nbReplyDataList=nbReplyDataList;
        this.listener=listener;
    }

    public void notifyListChnage(List<NBReplyData> nbReplyDataList) {
        this.nbReplyDataList = nbReplyDataList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return nbReplyDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatar,deleteBtn;
        private TextView replied_by, relied_on, reply;


        MyViewHolder(View view) {
            super(view);
            avatar = view.findViewById(R.id.avatar);
            replied_by = view.findViewById(R.id.replied_by);
            relied_on = view.findViewById(R.id.relied_on);
            reply = view.findViewById(R.id.reply);
            deleteBtn = view.findViewById(R.id.deleteBtn);
        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.nb_reply_item, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        try {
            final NBReplyData nbReplyData = nbReplyDataList.get(position);
            if(nbReplyData.hasAvatar()){
                Picasso.get().load(nbReplyData.getAvatar())
                        .placeholder(bd_loading).error(bd).into(holder.avatar);
            }else{
                Picasso.get().load(context.getString(R.string.oust_user_avatar_link) + nbReplyData.getAvatar())
                        .placeholder(bd_loading).error(bd).into(holder.avatar);
            }
            holder.reply.setText(""+nbReplyData.getReply());
            holder.replied_by.setText(""+nbReplyData.getReplied_by());
            holder.relied_on.setText(OustSdkTools.getDate(""+nbReplyData.getReplied_on()));
            if(nbReplyData.getId()==0){
                holder.deleteBtn.setVisibility(View.GONE);
            }else {
                if(OustAppState.getInstance().getActiveUser().getStudentKey().equals(nbReplyData.getUserKey())){
                    holder.deleteBtn.setVisibility(View.VISIBLE);
                }else{
                    holder.deleteBtn.setVisibility(View.GONE);
                }

            }

            holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickView(v,nbReplyData);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }
    private void clickView(View view,final NBReplyData nbReplyData) {
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

    private void performReplyDelete(NBReplyData nbReplyData) {
        PostViewData postViewData = new PostViewData();
        postViewData.setNbReplyData(nbReplyData);
        postViewData.setReplyDeleteType();
        postViewData.setTimeStamp(System.currentTimeMillis());
        listener.onDeleteComment(postViewData);
    }

}




