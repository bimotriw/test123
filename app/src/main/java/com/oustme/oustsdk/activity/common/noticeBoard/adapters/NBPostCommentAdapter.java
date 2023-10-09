package com.oustme.oustsdk.activity.common.noticeBoard.adapters;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;

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
import com.oustme.oustsdk.activity.common.noticeBoard.activity.NBCommentActivity;
import com.oustme.oustsdk.activity.common.noticeBoard.callBacks.NoticeBoardCommentDeleteListener;
import com.oustme.oustsdk.activity.common.noticeBoard.extras.ClickState;
import com.oustme.oustsdk.activity.common.noticeBoard.model.request.PostViewData;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBCommentData;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStrings;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by oust on 3/5/19.
 */

public class NBPostCommentAdapter extends RecyclerView.Adapter<NBPostCommentAdapter.MyViewHolder> {
    private final String TAG = "NBPostCommentAdapter";
    private List<NBCommentData> nbCommentDataList;
    private Context context;
    private long postId;
    private long nbId;
    private boolean isClicked = false;
    private  NoticeBoardCommentDeleteListener noticeBoardCommentDeleteListener;
    BitmapDrawable bd = OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.profile_image));
    BitmapDrawable bd_loading = OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.profile_image_loading));

    public NBPostCommentAdapter(Context context, List<NBCommentData> nbCommentDataList, NoticeBoardCommentDeleteListener noticeBoardCommentDeleteListener) {
        this.context = context;
        this.nbCommentDataList = nbCommentDataList;
        this.noticeBoardCommentDeleteListener = noticeBoardCommentDeleteListener;
    }

    public void notifyListChnage(List<NBCommentData> nbCommentDataList) {
        this.nbCommentDataList = nbCommentDataList;
        notifyDataSetChanged();
    }

    public void setPostParams(long nbId, long postId) {
        this.postId = postId;
        this.nbId = nbId;
    }

    @Override
    public int getItemCount() {
        return nbCommentDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView commenter_img, deleteImg;
        private TextView commenter_name, commented_on, comment, reply_text;
        private LinearLayout reply_ll;
        private View view_ul;

        MyViewHolder(View view) {
            super(view);
            commenter_img = view.findViewById(R.id.commenter_img);
            commenter_name = view.findViewById(R.id.commenter_name);
            commented_on = view.findViewById(R.id.commented_on);
            comment = view.findViewById(R.id.comment);
            reply_text = view.findViewById(R.id.reply_text);
            reply_ll = view.findViewById(R.id.reply_ll);
            view_ul = view.findViewById(R.id.view_ul);
            deleteImg = view.findViewById(R.id.deleteImg);
        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.nb_post_comment, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        try {
            if (position == nbCommentDataList.size() - 1) {
                holder.view_ul.setVisibility(View.GONE);
            }else{
                holder.view_ul.setVisibility(View.GONE);
            }
//            final NBCommentData nbCommentData = nbCommentDataList.get(position);
            if (nbCommentDataList.get(position).hasAvatar()) {
                Picasso.get().load(nbCommentDataList.get(position).getAvatar())
                        .placeholder(bd_loading).error(bd).into(holder.commenter_img);
            } else {
                Picasso.get().load(context.getString(R.string.oust_user_avatar_link) + nbCommentDataList.get(position).getAvatar())
                        .placeholder(bd_loading).error(bd).into(holder.commenter_img);
            }
            holder.comment.setText("" + nbCommentDataList.get(position).getComment());
            holder.commenter_name.setText("" + nbCommentDataList.get(position).getCommentedBy());
            holder.commented_on.setText(OustSdkTools.getDate("" + nbCommentDataList.get(position).getCommentedOn()));
            if (nbCommentDataList.get(position).getId() == 0) {
                holder.reply_ll.setVisibility(View.GONE);
                holder.deleteImg.setVisibility(View.GONE);

            } else {
                if(nbCommentDataList.get(position).getUserKey().equals(OustAppState.getInstance().getActiveUser().getStudentKey())) {
                    holder.deleteImg.setVisibility(View.VISIBLE);
                }else {
                    holder.deleteImg.setVisibility(View.GONE);
                }
                holder.reply_ll.setVisibility(View.VISIBLE);
                holder.reply_text.setText(""+ context.getResources().getString(R.string.reply));
            }
            if (nbCommentDataList.get(position).getNbReplyData() != null && nbCommentDataList.get(position).getNbReplyData().size() == 1) {
                holder.reply_text.setText(nbCommentDataList.get(position).getNbReplyData().size() + " "+ context.getResources().getString(R.string.reply));
            } else if (nbCommentDataList.get(position).getNbReplyData() != null && nbCommentDataList.get(position).getNbReplyData().size() > 1) {
                holder.reply_text.setText(nbCommentDataList.get(position).getNbReplyData().size() + " "+ context.getResources().getString(R.string.replies));
            }

            holder.reply_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickView(v, nbCommentDataList.get(position), ClickState.OPEN_DETAILS);
                }
            });
            holder.deleteImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickView(v, nbCommentDataList.get(position), ClickState.DELETE_COMMENT);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void clickView(View view, final NBCommentData nbCommentData, final ClickState clickState) {
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
                    if (clickState == ClickState.OPEN_DETAILS) {
                        Intent intent = new Intent(context, NBCommentActivity.class);
                        intent.putExtra("nbId", nbId);
                        intent.putExtra("postId", postId);
                        intent.putExtra("commentId", nbCommentData.getId());
                        context.startActivity(intent);
                    }else if(clickState==ClickState.DELETE_COMMENT){
                        performDelete(nbCommentData);
                    }
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

    private void performDelete(final NBCommentData nbCommentData) {
        PostViewData postViewData = new PostViewData(nbId,postId);
        postViewData.setCommentDeleteType();
        postViewData.setNbCommentData(nbCommentData);
        noticeBoardCommentDeleteListener.onDeleteComment(postViewData);
    }

}



