package com.oustme.oustsdk.layoutFour.newnoticeBoard.adapters;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.DecelerateInterpolator;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.noticeBoard.extras.ClickState;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.activity.NewNBCommentActivity;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.callBacks.NewNoticeBoardCommentDeleteListener;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.request.NewPostViewData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBCommentData;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;

public class NewNBPostCommentAdapter extends RecyclerView.Adapter<NewNBPostCommentAdapter.MyViewHolder> implements Filterable {
    private final String TAG = "NewNBPostCommentAdapter";
    private List<NewNBCommentData> nbCommentDataList;
    private List<NewNBCommentData> tempList;
    private Context context;
    private long postId;
    private long nbId;
    private boolean isClicked = false;
    private NewNoticeBoardCommentDeleteListener noticeBoardCommentDeleteListener;
    BitmapDrawable bd = OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.profile_image));
    BitmapDrawable bd_loading = OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.profile_image_loading));
    private Filter fRecords;

    public NewNBPostCommentAdapter(Context context, List<NewNBCommentData> nbCommentDataList, NewNoticeBoardCommentDeleteListener noticeBoardCommentDeleteListener) {
        this.context = context;
        this.nbCommentDataList = nbCommentDataList;
        this.tempList = nbCommentDataList;
        this.noticeBoardCommentDeleteListener = noticeBoardCommentDeleteListener;
    }

    public void notifyListChnage(List<NewNBCommentData> nbCommentDataList) {
        this.nbCommentDataList = nbCommentDataList;
        this.tempList = nbCommentDataList;
        notifyDataSetChanged();
    }

    public void setPostParams(long nbId, long postId) {
        this.postId = postId;
        this.nbId = nbId;
    }

    @Override
    public int getItemCount() {
        return nbCommentDataList == null ? 0 : nbCommentDataList.size();
    }

    @Override
    public Filter getFilter() {
        if (fRecords == null) {
            fRecords = new RecordFilter();
        }
        return fRecords;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView commenter_img, deleteImg;
        TextView commenter_name, commented_on, comment, reply_text, reply_count, designation, userRole;
        LinearLayout reply_ll;
        View view_ul;

        MyViewHolder(View view) {
            super(view);
            commenter_img = view.findViewById(R.id.commenter_img);
            commenter_name = view.findViewById(R.id.commenter_name);
            commented_on = view.findViewById(R.id.commented_on);
            comment = view.findViewById(R.id.comment);
            reply_text = view.findViewById(R.id.reply_text);
            reply_count = view.findViewById(R.id.reply_count);
            reply_ll = view.findViewById(R.id.reply_ll);
            view_ul = view.findViewById(R.id.view_ul);
            designation = view.findViewById(R.id.designation);
            userRole = view.findViewById(R.id.userRole);
            deleteImg = view.findViewById(R.id.deleteImg);
        }

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.nb_post_comment_2, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        try {
            if (position == nbCommentDataList.size() - 1) {
                holder.view_ul.setVisibility(View.GONE);
            } else {
                holder.view_ul.setVisibility(View.GONE);
            }
//            final NewNBCommentData nbCommentData = nbCommentDataList.get(position);
            if (nbCommentDataList.get(position).hasAvatar()) {
                Picasso.get().load(nbCommentDataList.get(position).getAvatar())
                        .placeholder(R.drawable.ic_person_profile_image_nb).error(R.drawable.ic_person_profile_image_nb).into(holder.commenter_img);
            } else {
                Picasso.get().load(R.drawable.ic_person_profile_image_nb)
                        .placeholder(R.drawable.ic_person_profile_image_nb).error(R.drawable.ic_person_profile_image_nb).into(holder.commenter_img);
            }
            holder.comment.setText("" + nbCommentDataList.get(position).getComment());
            holder.commenter_name.setText(WordUtils.capitalize("" + nbCommentDataList.get(position).getCommentedBy()));
            holder.commented_on.setText(OustSdkTools.getDate("" + nbCommentDataList.get(position).getCommentedOn()));
            if (nbCommentDataList.get(position).getId() == 0) {
                holder.reply_ll.setVisibility(View.GONE);
                holder.deleteImg.setVisibility(View.GONE);

            } else {
                if (nbCommentDataList.get(position).getUserKey().equals(OustAppState.getInstance().getActiveUser().getStudentKey())) {
                    holder.deleteImg.setVisibility(View.VISIBLE);
                    if (nbCommentDataList.get(position).getCommentedBy() != null && !nbCommentDataList.get(position).getCommentedBy().isEmpty()) {
                        holder.commenter_name.setText(WordUtils.capitalize(OustAppState.getInstance().getActiveUser().getStudentid()));
                    }
                } else {
                    holder.deleteImg.setVisibility(View.GONE);
                }
                holder.reply_ll.setVisibility(View.VISIBLE);
                holder.reply_text.setText("" + context.getResources().getString(R.string.reply));
            }

            if (!OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_NB_POST_COMMENT)) {
                holder.reply_count.setVisibility(View.GONE);
                holder.reply_ll.setVisibility(View.GONE);
            } else {
                if (nbCommentDataList.get(position).getNbReplyData() != null && nbCommentDataList.get(position).getNbReplyData().size() >= 0) {
                    if (nbCommentDataList.get(position).getNbReplyData() != null && nbCommentDataList.get(position).getNbReplyData().size() == 0) {
                        holder.reply_count.setVisibility(View.GONE);
                    } else if (nbCommentDataList.get(position).getNbReplyData() != null && nbCommentDataList.get(position).getNbReplyData().size() == 1) {
                        holder.reply_count.setVisibility(View.VISIBLE);
                        holder.reply_count.setText("View " + nbCommentDataList.get(position).getNbReplyData().size() + " " + context.getResources().getString(R.string.reply));
                    } else if (nbCommentDataList.get(position).getNbReplyData() != null && nbCommentDataList.get(position).getNbReplyData().size() > 1) {
                        holder.reply_count.setVisibility(View.VISIBLE);
                        holder.reply_count.setText("View " + nbCommentDataList.get(position).getNbReplyData().size() + " " + context.getResources().getString(R.string.replies));
                    }
                } else {
                    holder.reply_count.setVisibility(View.GONE);
                }
            }

            if (nbCommentDataList.get(position).getDesignation() != null) {
                holder.designation.setText(nbCommentDataList.get(position).getDesignation() + "  ");
            }

            if (nbCommentDataList.get(position).getUserRole() != null) {
                holder.userRole.setText(nbCommentDataList.get(position).getUserRole());
            }

            holder.reply_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickView(v, nbCommentDataList.get(position), ClickState.OPEN_DETAILS);
                }
            });
            holder.reply_count.setOnClickListener(new View.OnClickListener() {
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

    private void clickView(View view, final NewNBCommentData nbCommentData, final ClickState clickState) {
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
                        Intent intent = new Intent(context, NewNBCommentActivity.class);
                        intent.putExtra("nbId", nbId);
                        intent.putExtra("postId", postId);
                        intent.putExtra("commentId", nbCommentData.getId());
                        context.startActivity(intent);
                    } else if (clickState == ClickState.DELETE_COMMENT) {
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

    private void performDelete(final NewNBCommentData nbCommentData) {
        NewPostViewData postViewData = new NewPostViewData(nbId, postId);
        postViewData.setCommentDeleteType();
        postViewData.setNbCommentData(nbCommentData);
        noticeBoardCommentDeleteListener.onDeleteComment(postViewData);
    }

    private class RecordFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            Log.e(TAG, "constraint Data: " + constraint);
            FilterResults filterResults = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                filterResults.values = tempList;
                filterResults.count = tempList.size();
            } else {
                ArrayList<NewNBCommentData> list = new ArrayList<>();
                constraint = constraint.toString().toLowerCase();
                for (NewNBCommentData item : tempList) {

                    if ((item.getCommentedBy() != null && item.getCommentedBy().toLowerCase().contains(constraint.toString())) ||
                            (item.getComment() != null && item.getComment().toLowerCase().contains(constraint.toString()))) {
                        list.add(item);
                    }
                    filterResults.count = list.size();
                    filterResults.values = list;
                }
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            nbCommentDataList = (ArrayList<NewNBCommentData>) results.values;
            notifyDataSetChanged();
        }
    }
}