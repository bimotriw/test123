package com.oustme.oustsdk.adapter.common;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.common.AlertCommentData;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by oust on 4/25/18.
 */

public class AlertCommentAdapter extends RecyclerView.Adapter<AlertCommentAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<AlertCommentData> commentsList;

    public AlertCommentAdapter(Context context, ArrayList<AlertCommentData> commentsList) {
        this.context = context;
        this.commentsList = commentsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.comment_item_layout, parent, false);
        return new MyViewHolder(itemView);    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try{

            holder.user_avatar.setImageBitmap(null);

            holder.comment.setText(""+commentsList.get(position).getComment());
            holder.commentdate.setText(OustSdkTools.setTxtNotificationPeriod(""+commentsList.get(position).getAddedOnDate()));
            setUserAvatar(holder.user_avatar,commentsList.get(position).getUserAvatar());
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder{
        private TextView comment,commentdate;
        private ImageView user_avatar;


        public MyViewHolder(View itemView) {
            super(itemView);
            comment = itemView.findViewById(R.id.comment);
            commentdate= itemView.findViewById(R.id.commentdate);
            user_avatar= itemView.findViewById(R.id.user_avatar);
        }
    }
    private void setUserAvatar(ImageView view, String avatar){
        try {
            if ((avatar != null) && (!avatar.isEmpty())) {
                if (avatar.startsWith("http")) {
                    Picasso.get().load(avatar)
                            .placeholder(OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.profile_image_loading))).error(OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.maleavatar)))
                            .into(view);
                } else {
                    Picasso.get().load(avatar)
                            .placeholder(OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.profile_image_loading))).error(OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.maleavatar)))
                            .into(view);
                }
            } else {
                Picasso.get().load(OustSdkApplication.getContext().getResources().getString(R.string.maleavatar))
                        .placeholder(OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.profile_image_loading))).error(OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.maleavatar)))
                        .into(view);            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
    public void notifyChanges(ArrayList<AlertCommentData> commentsList){
        this.commentsList=commentsList;
        notifyDataSetChanged();
    }
}
