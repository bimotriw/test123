package com.oustme.oustsdk.adapter.common;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.R;
import com.oustme.oustsdk.room.dto.DTODiaryDetailsModel;
import com.oustme.oustsdk.tools.BranchTools;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LearningDiaryAdapter extends RecyclerView.Adapter<LearningDiaryAdapter.MyViewHolder> {

    private static final String TAG = "LearningDiaryAdapter";
    private List<DTODiaryDetailsModel> dataSet;
    private Context mContext;
    private SelectEditDelete mListener;
    private static final int VIEW_TYPE_EMPTY = 0, VIEW_TYPE_DEFAULT = 1;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewBanner, mImageViewDelete, mImageViewEdit, lpocimage, completed_img, img_failed;
        TextView textViewDate, mTextViewStatus, mTextViewActivity, mTextXpEarned, mTextOCEarned;
        LinearLayout mLinearLayoutDelete;
        LinearLayout mLinearLayoutEdit, mLayout_coins;
        RelativeLayout coureseimagelayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.textViewDate = itemView.findViewById(R.id.coursecompletetext);
            this.mTextViewStatus = itemView.findViewById(R.id.completed_text);
            this.mTextViewActivity = itemView.findViewById(R.id.coursenametext);
            this.mTextXpEarned = itemView.findViewById(R.id.tv_xp_earned);
            this.mTextOCEarned = itemView.findViewById(R.id.tv_oc_earned);
            this.imageViewBanner = itemView.findViewById(R.id.coureseimage);
            this.mImageViewDelete = itemView.findViewById(R.id.deleteImage);
            this.mImageViewEdit = itemView.findViewById(R.id.editImage);
            this.mLinearLayoutDelete = itemView.findViewById(R.id.linearLayoutDelete);
            this.mLinearLayoutEdit = itemView.findViewById(R.id.linearLayoutEdit);
            this.lpocimage = itemView.findViewById(R.id.lpocimage);
            this.mLayout_coins = itemView.findViewById(R.id.mLayout_coins);
            this.coureseimagelayout = itemView.findViewById(R.id.coureseimagelayout);
            this.completed_img = itemView.findViewById(R.id.completed_img);
            this.img_failed = itemView.findViewById(R.id.img_failed);
        }
    }

    public LearningDiaryAdapter(Context context, List<DTODiaryDetailsModel> data) {
        this.dataSet = data;
        this.mContext = context;
        mListener = (SelectEditDelete) this.mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == VIEW_TYPE_EMPTY) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_rv_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friendprofile_rowlayout, parent, false);
        }
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        if (getItemViewType(listPosition) == VIEW_TYPE_EMPTY)
            return;

        //OustSdkTools.setImage(holder.lpocimage, OustSdkApplication.getContext().getResources().getString(R.string.coins_icon));

        if (dataSet.get(listPosition).getEndTS() != null) {
            holder.textViewDate.setText(OustSdkTools.timeStampToDate(Long.valueOf(dataSet.get(listPosition).getEndTS())));
        }
        holder.mTextViewActivity.setText(dataSet.get(listPosition).getActivity());
        final String status = dataSet.get(listPosition).getApprovalStatus();
        holder.mTextViewStatus.setText(status);
        if (dataSet.get(listPosition).isIsdeleted()) {
            holder.mLinearLayoutDelete.setVisibility(View.VISIBLE);
        } else {
            holder.mLinearLayoutDelete.setVisibility(View.GONE);
        }
        if (dataSet.get(listPosition).isEditable()) {
            holder.mLinearLayoutEdit.setVisibility(View.VISIBLE);
        } else {
            holder.mLinearLayoutEdit.setVisibility(View.GONE);
        }
        if(dataSet.get(listPosition).getmBanner()!=null) {
            Picasso.get().load(dataSet.get(listPosition).getmBanner()).into(holder.imageViewBanner);
        }
        /*else if(listPosition==0 && dataSet.get(listPosition).getmBanner()!=null && dataSet.get(listPosition).getmBanner().contains("localImage"))
        {
            String[] imagList = dataSet.get(listPosition).getmBanner().split(":");
            File imgFile = new  File(imagList[1]);

            if(imgFile.exists()){

                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                holder.imageViewBanner.setImageBitmap(myBitmap);

            }
        }*/
        else {
            Picasso.get().load(AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL+"mpower/defaultImage/bannerImage.jpg").into(holder.imageViewBanner);
            //Log.d(TAG, "onBindViewHolder: defaultBanner:"+OustPreferences.get(AppConstants.StringConstants.LEARNING_DIARY_DEFAULT_BANNER));
           // Picasso.get().load(AppConstants.StringConstants.S3_BUCKET_IMAGE_URL+ OustPreferences.get(AppConstants.StringConstants.LEARNING_DIARY_DEFAULT_BANNER)).into(holder.imageViewBanner);

            //Log.d(TAG, "onBindViewHolder: defaultBanner:"+OustPreferences.get(AppConstants.StringConstants.LEARNING_DIARY_DEFAULT_BANNER));
            // Picasso.with(mContext).load(AppConstants.StringConstants.S3_BUCKET_IMAGE_URL+ OustPreferences.get(AppConstants.StringConstants.LEARNING_DIARY_DEFAULT_BANNER)).into(holder.imageViewBanner);
        }




        if (dataSet.get(listPosition).getCoins() != null && dataSet.get(listPosition).getCoins() != 0) {
            String oc = mContext.getResources().getString(R.string.score_text)+" "+dataSet.get(listPosition).getCoins();
            holder.mTextOCEarned.setText(oc);
            holder.mTextOCEarned.setTypeface(null, Typeface.BOLD);
        } else {
            holder.mTextOCEarned.setVisibility(View.GONE);
            //holder.mLayout_coins.setVisibility(View.GONE);
        }

        if(dataSet.get(listPosition).getDataType()!=null && (dataSet.get(listPosition).getDataType().equalsIgnoreCase("ASSESSMENT"))){
            if(!dataSet.get(listPosition).isPassed()){
                holder.completed_img.setVisibility(View.GONE);
                holder.img_failed.setVisibility(View.VISIBLE);
            }else{
                holder.completed_img.setVisibility(View.VISIBLE);
                holder.img_failed.setVisibility(View.GONE);
            }
        }else{
            holder.completed_img.setVisibility(View.VISIBLE);
            holder.img_failed.setVisibility(View.GONE);
        }


        /*if (dataSet.get(listPosition).getXp() != null && dataSet.get(listPosition).getXp() != 0) {
            String xp = "XP " + +dataSet.get(listPosition).getXp();
            holder.mTextXpEarned.setText(xp);
            holder.mTextXpEarned.setVisibility(View.VISIBLE);
            holder.mTextXpEarned.setTypeface(null, Typeface.BOLD);
        } else if (dataSet.get(listPosition).getQuestionXp() != null && dataSet.get(listPosition).getQuestionXp() != 0) {
            String xp = "XP " + dataSet.get(listPosition).getQuestionXp();
            holder.mTextXpEarned.setText(xp);
            holder.mTextXpEarned.setVisibility(View.VISIBLE);
            holder.mTextXpEarned.setTypeface(null, Typeface.BOLD);
        } else {
            holder.mTextXpEarned.setVisibility(View.GONE);
        }*/


        holder.mImageViewEdit.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.selectedEditPosition(listPosition);
            }
        });
        holder.mImageViewDelete.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.selectedDeletePosition(listPosition);
            }
        });
        holder.mLinearLayoutEdit.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.selectedEditPosition(listPosition);
            }
        });
        holder.mLinearLayoutDelete.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.selectedDeletePosition(listPosition);
            }
        });


        holder.coureseimagelayout.setOnClickListener(view -> openCourseAssessmentPage(dataSet.get(listPosition)));
        holder.mTextViewActivity.setOnClickListener(view -> openCourseAssessmentPage(dataSet.get(listPosition)));
    }

    private void openCourseAssessmentPage(DTODiaryDetailsModel diaryDetailsModel) {
        Log.d(TAG, "openCourseAssessmentPage: ID:"+diaryDetailsModel.getUserLD_Id());
        if(diaryDetailsModel.getDataType()==null||diaryDetailsModel.getDataType().equalsIgnoreCase("NOTFOUND")){
         //   OustSdkTools.showToast("Could not open manual entry");
        }else{
            if(diaryDetailsModel.getDataType().equalsIgnoreCase("ASSESSMENT")){
                BranchTools.gotoAssessment(diaryDetailsModel.getLearningDiaryID());
            }else if(diaryDetailsModel.getDataType().equalsIgnoreCase("SURVEY")){
                BranchTools.gotoSurvey(diaryDetailsModel.getLearningDiaryID(),diaryDetailsModel.getMappedCourseId());
            }else{
                if(diaryDetailsModel.getMode()!=null&&diaryDetailsModel.getMode().equalsIgnoreCase("ARCHIVED")){
                    OustSdkTools.showToast(mContext.getResources().getString(R.string.sorry_archive_by_admin));
                }else{
                    BranchTools.gotoCoursePage(diaryDetailsModel.getLearningDiaryID());
                }

            }
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (position >= dataSet.size())
            return VIEW_TYPE_EMPTY;
        else
            return VIEW_TYPE_DEFAULT;
    }

    @Override
    public int getItemCount() {
        return dataSet.size() + 1;
    }

    public interface SelectEditDelete {
        void selectedEditPosition(int position);

        void selectedDeletePosition(int position);
    }
}
