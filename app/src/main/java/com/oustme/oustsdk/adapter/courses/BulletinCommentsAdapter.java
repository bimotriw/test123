package com.oustme.oustsdk.adapter.courses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.UserProfileActivity;
import com.oustme.oustsdk.firebase.course.BulletinBoardData;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by shilpysamaddar on 08/08/17.
 */

public class BulletinCommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_HEADER = 0;
    private final int TYPE_ITEM = 1;
    List<BulletinBoardData> bulletinBoardCommentsDatas;
    String question,userDisplayName,userAvatar;
    BulletinBoardData bulletinBoardData;
    Activity activity;
    public BulletinCommentsAdapter(List<BulletinBoardData> bulletinBoardCommentsDatas,String question,String userDisplayName,String userAvatar,Activity activity) {
        this.bulletinBoardCommentsDatas=bulletinBoardCommentsDatas;
        this.question=question;
        this.userDisplayName=userDisplayName;
        this.userAvatar=userAvatar;
        this.activity=activity;
    }

    public void notifyDataChange(List<BulletinBoardData> bulletinBoardCommentsDatas){
        this.bulletinBoardCommentsDatas=bulletinBoardCommentsDatas;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==TYPE_HEADER){
            Context context = parent.getContext();
            View itemView = LayoutInflater.from(context).inflate(R.layout.bulletin_comments_header_view, parent, false);
            return new BulletinCommentsAdapter.MyHeaderHolder(itemView);
        }
        else if(viewType==TYPE_ITEM) {
            Context context = parent.getContext();
            View itemView = LayoutInflater.from(context).inflate(R.layout.bulletin_comments, parent, false);
            return new BulletinCommentsAdapter.MyViewHolder(itemView);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof MyHeaderHolder)
        {
            MyHeaderHolder myHeaderHolder = (MyHeaderHolder)holder;
            myHeaderHolder.question.setText(question);
            myHeaderHolder.user_display_name.setText(userDisplayName);
            setUserAvatar(myHeaderHolder.user_image,userAvatar);
        }
        else if(holder instanceof MyViewHolder)
        {
            MyViewHolder myViewHolder = (MyViewHolder)holder;
                setUserAvatar(myViewHolder.userImage, bulletinBoardCommentsDatas.get(position - 1).getUserAvatar());
            myViewHolder.comment.setText(bulletinBoardCommentsDatas.get(position-1).getComment());
            setDate(bulletinBoardCommentsDatas.get(position-1).getAddedOnDate(),myViewHolder.addedon_text);
            if(bulletinBoardCommentsDatas.get(position-1).getUserDisplayName()!=null) {
                myViewHolder.user_display_name.setText(bulletinBoardCommentsDatas.get(position - 1).getUserDisplayName());
                myViewHolder.user_display_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bulletinBoardData = bulletinBoardCommentsDatas.get(position - 1);
                        gotoUserProfile(bulletinBoardData);
                    }
                });
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }
    private boolean isPositionHeader(int position)
    {
        return position == 0;
    }

    @Override
    public int getItemCount() {
        return bulletinBoardCommentsDatas.size()+1;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView user_display_name,comment,addedon_text;
        ImageView userImage;
        private View currentView;
        public MyViewHolder(View itemView) {
            super(itemView);
            currentView=itemView;
            user_display_name= itemView.findViewById(R.id.user_display_name);
            comment= itemView.findViewById(R.id.comment);
            userImage= itemView.findViewById(R.id.user_image);
            addedon_text= itemView.findViewById(R.id.addedon_text);
        }
    }

    public class MyHeaderHolder extends RecyclerView.ViewHolder {
        TextView question,user_display_name;
        ImageView user_image;
        public MyHeaderHolder(View itemView) {
            super(itemView);
            question= itemView.findViewById(R.id.question);
            user_display_name= itemView.findViewById(R.id.user_display_name);
            user_image= itemView.findViewById(R.id.user_image);
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

    private void gotoUserProfile(BulletinBoardData bulletinBoardData){
        try {
            if (!(bulletinBoardData.getUserId()).equalsIgnoreCase(OustSdkTools.getActiveUserData().getStudentid())) {
                Intent intent = new Intent(OustSdkApplication.getContext(), UserProfileActivity.class);
                if(bulletinBoardData.getUserAvatar()!=null) {
                    intent.putExtra("avatar", bulletinBoardData.getUserAvatar());
                }
                intent.putExtra("name", bulletinBoardData.getUserDisplayName());
                intent.putExtra("studentId", ""+bulletinBoardData.getUserId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                OustSdkApplication.getContext().startActivity(intent);
            }
        }catch (Exception e){}
    }

    private void setDate(long timeStamp,TextView view){

        SimpleDateFormat formatter = new SimpleDateFormat("MMM d, y h:mm a");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        String date=formatter.format(calendar.getTime());
        view.setText(""+date);
    }
}
