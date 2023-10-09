package com.oustme.oustsdk.adapter.courses;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.UserProfileActivity;
import com.oustme.oustsdk.activity.courses.bulletinboardcomments.BulletinBoardCommentsActivity;
import com.oustme.oustsdk.firebase.course.BulletinBoardData;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStrings;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by shilpysamaddar on 03/08/17.
 */

public class BulletinQuesAdapter extends RecyclerView.Adapter<BulletinQuesAdapter.MyViewHolder> {
    private List<BulletinBoardData> bulletinBoardQuestionDatas;
    private Activity activity;
    private BulletinBoardData bulletinBoardData;
    private boolean isCplBulletin = false;

    public BulletinQuesAdapter(Activity activity, List<BulletinBoardData> bulletinBoardQuestionDatas) {
        this.bulletinBoardQuestionDatas = bulletinBoardQuestionDatas;
        this.activity = activity;
    }

    public void setCplBulletin(boolean cplBulletin) {
        isCplBulletin = cplBulletin;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.bulletin_ques, parent, false);
        return new BulletinQuesAdapter.MyViewHolder(itemView);
    }

    public void notifyDataChange(List<BulletinBoardData> bulletinBoardQuestionDatas) {
        this.bulletinBoardQuestionDatas = bulletinBoardQuestionDatas;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.main_layout.setEnabled(true);
        holder.replytext.setText(activity.getResources().getString(R.string.comment_text));
        setUserAvatar(holder.q_userImage, bulletinBoardQuestionDatas.get(position).getUserAvatar());
        if (bulletinBoardQuestionDatas.get(position).getUserDisplayName() != null) {
            holder.commentby_username.setText("" + bulletinBoardQuestionDatas.get(position).getUserDisplayName().trim());
        }
        holder.question.setText(bulletinBoardQuestionDatas.get(position).getQuestion());
        setDate(bulletinBoardQuestionDatas.get(position).getAddedOnDate(), holder.addedon_tv);
        if (bulletinBoardQuestionDatas.get(position).getNumComments() == 0) {
            holder.num_comments.setVisibility(View.GONE);
        } else if (bulletinBoardQuestionDatas.get(position).getNumComments() == 1) {
            holder.num_comments.setVisibility(View.VISIBLE);
            holder.num_comments.setText("" + bulletinBoardQuestionDatas.get(position).getNumComments() + " " + activity.getResources().getString(R.string.reply));
        } else {
            holder.num_comments.setVisibility(View.VISIBLE);
            holder.num_comments.setText("" + bulletinBoardQuestionDatas.get(position).getNumComments() + " " + activity.getResources().getString(R.string.replies));
        }
        holder.commentby.setText(activity.getResources().getString(R.string.comment));
        holder.commentby_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bulletinBoardData = bulletinBoardQuestionDatas.get(position);
                gotoUserProfile(bulletinBoardData);
            }
        });
        holder.startedby_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bulletinBoardData = bulletinBoardQuestionDatas.get(position);
                gotoUserProfile(bulletinBoardData);
            }
        });
        String toolbarColorCode = OustPreferences.get("toolbarColorCode");
        if ((toolbarColorCode != null) && (!toolbarColorCode.isEmpty())) {
            holder.answer_layout.setBackgroundColor(Color.parseColor(toolbarColorCode));
        }
        holder.answer_layout.setOnClickListener(v -> {
            holder.answer_layout.setEnabled(false);
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(v, "scaleX", 1.0f, 0.94f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(v, "scaleY", 1.0f, 0.96f);
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
                    bulletinBoardData = bulletinBoardQuestionDatas.get(position);
                    anwerButtonClicked(bulletinBoardData, holder.answer_layout);
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
        });
        holder.main_layout.setOnClickListener(v -> {
            holder.main_layout.setEnabled(false);
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(v, "scaleX", 1.0f, 0.94f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(v, "scaleY", 1.0f, 0.96f);
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
                    bulletinBoardData = bulletinBoardQuestionDatas.get(position);
                    anwerButtonClicked(bulletinBoardData, holder.main_layout);
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return bulletinBoardQuestionDatas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView commentby_username, question, replytext, commentby, num_comments, addedon_tv;
        LinearLayout answer_layout, main_layout, startedby_layout;
        ImageView q_userImage;
        private View currentView;

        public MyViewHolder(View itemView) {
            super(itemView);
            currentView = itemView;
            commentby_username = itemView.findViewById(R.id.commentby_username);
            question = itemView.findViewById(R.id.question);
            answer_layout = itemView.findViewById(R.id.answer_layout);
            replytext = itemView.findViewById(R.id.replytext);
            main_layout = itemView.findViewById(R.id.main_layout);
            commentby = itemView.findViewById(R.id.commentby);
            num_comments = itemView.findViewById(R.id.num_comments);
            addedon_tv = itemView.findViewById(R.id.addedon_tv);
            q_userImage = itemView.findViewById(R.id.q_userImage);
            startedby_layout = itemView.findViewById(R.id.startedby_layout);
        }
    }

    private void anwerButtonClicked(BulletinBoardData bulletinBoardData, LinearLayout main_layout) {
        Intent intent = new Intent(activity, BulletinBoardCommentsActivity.class);
        intent.putExtra("question", bulletinBoardData.getQuestion());
        intent.putExtra("questionKey", bulletinBoardData.getQuesKey());
        if (!isCplBulletin) {
            intent.putExtra("courseName", bulletinBoardData.getCourseName());
            intent.putExtra("courseId", bulletinBoardData.getCourseId());
        } else {
            intent.putExtra("cplName", bulletinBoardData.getCplName());
            intent.putExtra("cplId", bulletinBoardData.getCplId());
        }
        if (bulletinBoardData.getUserDisplayName() != null) {
            intent.putExtra("userDisplayName", bulletinBoardData.getUserDisplayName());
        }
        if (bulletinBoardData.getUserAvatar() != null) {
            intent.putExtra("userAvatar", bulletinBoardData.getUserAvatar());
        }
        OustSdkTools.newActivityAnimationB(intent, activity);
        if (main_layout != null) {
            main_layout.setEnabled(true);
        }
    }

    private void gotoUserProfile(BulletinBoardData bulletinBoardData) {
        try {
            if (!(bulletinBoardData.getUserId()).equalsIgnoreCase(OustSdkTools.getActiveUserData().getStudentid())) {
                Intent intent = new Intent(OustSdkApplication.getContext(), UserProfileActivity.class);
                if (bulletinBoardData.getUserAvatar() != null) {
                    intent.putExtra("avatar", bulletinBoardData.getUserAvatar());
                }
                if (bulletinBoardData.getUserDisplayName() != null) {
                    intent.putExtra("name", bulletinBoardData.getUserDisplayName());
                }
                intent.putExtra("studentId", "" + bulletinBoardData.getUserId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                OustSdkApplication.getContext().startActivity(intent);
            }
        } catch (Exception e) {
        }
    }

    private void setDate(long timeStamp, TextView view) {

        SimpleDateFormat formatter = new SimpleDateFormat("MMM d, y h:mm a");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        String date = formatter.format(calendar.getTime());
        view.setText("" + date);
    }

    private void setUserAvatar(ImageView image, String userAvatar) {
        try {
            if ((userAvatar != null) && (!userAvatar.isEmpty())) {
                if (userAvatar.startsWith("http")) {
                    Picasso.get().load(userAvatar)
                            .placeholder(OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.profile_image_loading))).error(OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.maleavatar)))
                            .into(image);
                } else {
                    Picasso.get().load(userAvatar)
                            .placeholder(OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.profile_image_loading))).error(OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.maleavatar)))
                            .into(image);
                }
            } else {
                Picasso.get().load(OustSdkApplication.getContext().getResources().getString(R.string.maleavatar)).placeholder(OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.profile_image_loading))).error(OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.maleavatar)))
                        .into(image);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
