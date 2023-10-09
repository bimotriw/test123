package com.oustme.oustsdk.adapter.assessments;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.customviews.CircleImageView;
import com.oustme.oustsdk.customviews.CustomTextView;
import com.oustme.oustsdk.firebase.assessment.Comments;
import com.oustme.oustsdk.presenter.assessments.CheckAnswerFragmentPresenter;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.squareup.picasso.Picasso;

import java.net.URLDecoder;
import java.util.List;

/**
 * Created by shilpysamaddar on 20/03/17.
 */

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MyViewHolder> {

    private Activity activity;
    private List<Comments> commentsList;
    private CheckAnswerFragmentPresenter checkAnswerFragmentPresenter;
    private boolean oustCommentType;

    public CommentsAdapter(Activity acty, List<Comments> dataList, CheckAnswerFragmentPresenter checkAnswerFragmentPresenter){
        this.activity = acty;
        this.commentsList = dataList;
        this.checkAnswerFragmentPresenter=checkAnswerFragmentPresenter;
    }

    public void notifyListChange(List<Comments> dataList){
        this.commentsList = dataList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView commenterAvatarImg;
        public TextView commenterName,commentDate,comment;

        public MyViewHolder(View view) {
            super(view);
            commenterAvatarImg = (CircleImageView)view.findViewById(R.id.commenterAvatarImg);
            commenterName = (CustomTextView) view.findViewById(R.id.commenter_name);
            commentDate = (CustomTextView) view.findViewById(R.id.commentdate);
            comment = (CustomTextView) view.findViewById(R.id.comment);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder,final int position) {
        try {
            if (commentsList != null) {
                oustCommentType= commentsList.get(position).getCommentType().equalsIgnoreCase("oust");
                if(!oustCommentType) {
                    if (commentsList.get(position).getUserName() != null) {
                        holder.commenterName.setText(commentsList.get(position).getUserName());
                    }
                    if ((commentsList.get(position).getTimeStamp()>1000)) {
                        holder.commentDate.setText(OustSdkTools.setTxtNotificationPeriod(String.valueOf(commentsList.get(position).getTimeStamp())));
                    }
                    if ((commentsList.get(position).getUserAvatar() != null)&&(!commentsList.get(position).getUserAvatar().isEmpty())) {
                        BitmapDrawable bd=OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.profile_image));
                        BitmapDrawable bd_loading=OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.profile_image_loading));
                        if (commentsList.get(position).getUserAvatar().startsWith("http")) {
                            Picasso.get().load(commentsList.get(position).getUserAvatar())
                                    .placeholder(bd_loading).error(bd)
                                    .into(holder.commenterAvatarImg);
                        }
                        else{
                            Picasso.get().load(activity.getString(R.string.oust_user_avatar_link)+commentsList.get(position).getUserAvatar())
                                    .placeholder(bd_loading).error(bd)
                                    .into(holder.commenterAvatarImg);
                        }
                    }
                }else {
                    holder.commenterName.setText(activity.getResources().getString(R.string.oustsolution_text));
                    OustSdkTools.setImage(holder.commenterAvatarImg, OustSdkApplication.getContext().getString(R.string.app_icon));
                    //holder.commenterAvatarImg.setImageResource(R.drawable.app_icon);
                    holder.commentDate.setVisibility(View.GONE);
                }

                if (commentsList.get(position).getUserComment() != null) {
                    String message=commentsList.get(position).getUserComment();
                    try {
                        if((message.contains("115%"))) {
                            message = message.replaceAll("115%", "0");
                        }
                        message= Html.fromHtml(URLDecoder.decode(message, "UTF-8")).toString();
                        if(message.contains("}-->")) {
                            message = message.substring(message.lastIndexOf("}-->") +4);
                        }
                        if(message.contains("\n\n")){
                            message=message.replaceAll("\n\n","\n");
                        }
                        holder.comment.setText(message);
                    }catch (Exception e){
                    }
                }

            }

        }catch(Exception e) {}
    }

    @Override
    public int getItemCount() {
        if(commentsList!=null) {
            return commentsList.size();
        } else {
            return 0;
        }
    }
}
