package com.oustme.oustsdk.feed_ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.common.AlertCommentData;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedCommentAdapter extends RecyclerView.Adapter<FeedCommentAdapter.FeedCommentViewHolder> {

    private Context context;
    private ArrayList<AlertCommentData> commentsList;
    private ActiveUser activeUser;

    public FeedCommentAdapter(Context context, ArrayList<AlertCommentData> commentsList, ActiveUser activeUser) {
        this.context = context;
        this.commentsList = commentsList;
        if (activeUser != null) {
            this.activeUser = activeUser;
        } else {
            this.activeUser = OustAppState.getInstance().getActiveUser();
        }

    }

    @NonNull
    @Override
    public FeedCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.comment_adapter, parent, false);
        return new FeedCommentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedCommentViewHolder holder, int position) {
        try {

            if (commentsList.get(position) != null) {
                //holder.user_avatar.setImageBitmap(null);

                holder.comment.setText(commentsList.get(position).getComment());
                if (commentsList.get(position).getUserId().equals(activeUser.getStudentid())) {
                    holder.user_name.setText(R.string.you_text);
                } else {
                    holder.user_name.setText(commentsList.get(position).getUserDisplayName());
                }

                holder.comment_date.setText(OustSdkTools.setTxtNotificationPeriod("" + commentsList.get(position).getAddedOnDate()));
                setUserAvatar(holder.user_avatar, commentsList.get(position).getUserAvatar());

            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    static class FeedCommentViewHolder extends RecyclerView.ViewHolder {

        CircleImageView user_avatar;
        TextView user_name;
        TextView comment_date;
        TextView comment;

        FeedCommentViewHolder(View itemView) {
            super(itemView);
            user_avatar = itemView.findViewById(R.id.user_avatar);
            user_name = itemView.findViewById(R.id.user_name);
            comment_date = itemView.findViewById(R.id.comment_date);
            comment = itemView.findViewById(R.id.comment);
        }
    }

    private void setUserAvatar(ImageView view, String avatar) {
        try {
            if ((avatar != null) && (!avatar.isEmpty())) {
                if (avatar.contains("http:")) {
                    avatar = avatar.replace("http:", "https:");
                }
                Glide.with(context).load(avatar)
                        .placeholder(R.drawable.ic_person_profile_image).error(R.drawable.ic_person_profile_image)
                        .into(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void notifyChanges(ArrayList<AlertCommentData> commentsList) {
        this.commentsList = commentsList;
        notifyDataSetChanged();
    }
}
