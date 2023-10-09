package com.oustme.oustsdk.work_diary.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.tools.BranchTools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.work_diary.WorkDiaryActivity;
import com.oustme.oustsdk.work_diary.model.WorkDiaryDetailsModel;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class WorkDiaryAdapter extends RecyclerView.Adapter<WorkDiaryAdapter.ViewHolder> {

    private ArrayList<WorkDiaryDetailsModel> workDiaryDetailsModelArrayList = new ArrayList<>();
    private ArrayList<WorkDiaryDetailsModel> tempWorkDiaryDetailsModelArrayList = new ArrayList<>();
    public Context context;
    private SelectEditDelete mListener;
    private boolean isMultipleCpl = false;
    private String studentKey;
    private Filter fRecords;
    private searchInterFace searchInterFace;


    public void setWorkDiaryAdapter(ArrayList<WorkDiaryDetailsModel> workDiaryDetailsModelArrayList, boolean multipleCpl, String studentKey, Context context) {
        this.workDiaryDetailsModelArrayList = workDiaryDetailsModelArrayList;
        this.tempWorkDiaryDetailsModelArrayList = workDiaryDetailsModelArrayList;
        this.context = context;
        this.searchInterFace = (searchInterFace) context;
        this.isMultipleCpl = multipleCpl;
        this.studentKey = studentKey;
        mListener = (SelectEditDelete) this.context;
//        setHasStableIds(true);
    }

    public ArrayList<WorkDiaryDetailsModel> getWorkDiaryDetailsModelArrayList() {
        if (tempWorkDiaryDetailsModelArrayList != null) {
            return tempWorkDiaryDetailsModelArrayList;
        } else {
            return new ArrayList<>();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_adapter, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        viewHolder.setIsRecyclable(false);
        return viewHolder;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        WorkDiaryDetailsModel workDiaryDetailsModel = tempWorkDiaryDetailsModelArrayList.get(position);
        if (workDiaryDetailsModel != null) {
            String eventType = "";
            String name;
            String coins = "";
            String durationText = "1";
            String date = "";
            String score;
            String action_name;
            String image;
            holder.pb_module.setVisibility(View.GONE);
            holder.tv_percentage.setVisibility(View.GONE);
            holder.tv_score.setVisibility(View.GONE);

            String toolbarColorCode;

            if (workDiaryDetailsModel.getDataType() != null && !workDiaryDetailsModel.getDataType().isEmpty()) {
                //eventType = workDiaryDetailsModel.getDisplayType();
                if (workDiaryDetailsModel.getDataType().equalsIgnoreCase("course")) {
                    eventType = context.getResources().getString(R.string.course_text);
                } else if (workDiaryDetailsModel.getDataType().equalsIgnoreCase("assessment")) {
                    eventType = context.getResources().getString(R.string.assessment);
                } else if (workDiaryDetailsModel.getDataType().equalsIgnoreCase("survey")) {
                    eventType = context.getResources().getString(R.string.survey_text);
                } else if (workDiaryDetailsModel.getDataType().equalsIgnoreCase("MY ENTRY")) {
                    eventType = context.getResources().getString(R.string.my_entry);
                } else if (workDiaryDetailsModel.getDataType().equalsIgnoreCase("PLAYLIST")) {
                    eventType = context.getResources().getString(R.string.play_list);
                }
            }


            if (workDiaryDetailsModel.getContentDuration() != 0) {
                durationText = OustSdkTools.getTime((long) workDiaryDetailsModel.getContentDuration());
            }
            if (workDiaryDetailsModel.getDataType().equalsIgnoreCase("PLAYLIST")) {
                holder.tv_timer.setVisibility(View.GONE);
            } else {
                holder.tv_timer.setVisibility(View.VISIBLE);
                if (durationText.equalsIgnoreCase("1")) {
                    holder.tv_timer.setText(durationText + " min");
                } else {
                    holder.tv_timer.setText(durationText + " mins");
                }
            }

            if (workDiaryDetailsModel.getEndTS() != null && !workDiaryDetailsModel.getEndTS().isEmpty()) {
                try {
                    Date endTsDate = new Date(Long.parseLong(workDiaryDetailsModel.getEndTS()));
                    date = new SimpleDateFormat("dd\nMMM", Locale.getDefault()).format(endTsDate);
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }

            }
            String previousDate = null;
            if (position > 0) {
                if (tempWorkDiaryDetailsModelArrayList.get(position - 1).getEndTS() != null && !tempWorkDiaryDetailsModelArrayList.get(position - 1).getEndTS().isEmpty() && !tempWorkDiaryDetailsModelArrayList.get(position - 1).getEndTS().contains("-")) {
                    try {
                        Date endTsDatePrevious = new Date(Long.parseLong(tempWorkDiaryDetailsModelArrayList.get(position - 1).getEndTS()));
                        previousDate = new SimpleDateFormat("dd\nMMM", Locale.getDefault()).format(endTsDatePrevious);
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                } else {
                    previousDate = new SimpleDateFormat("dd\nMMM", Locale.getDefault()).format(new Date());
                }

                if (date.equals(previousDate)) {
                    holder.tv_date.setVisibility(View.INVISIBLE);
                    holder.view_dot.setVisibility(View.INVISIBLE);
                } else {
                    holder.tv_date.setVisibility(View.VISIBLE);
                    holder.view_dot.setVisibility(View.VISIBLE);
                }
            }


            if (workDiaryDetailsModel.getApprovalStatus() != null && !workDiaryDetailsModel.getApprovalStatus().isEmpty()) {

                if (workDiaryDetailsModel.getApprovalStatus().equalsIgnoreCase("TO_BE_REVIEWED")) {

                    holder.entry_layout.setVisibility(View.VISIBLE);
                    holder.non_event_lay.setVisibility(View.GONE);

                } else if (workDiaryDetailsModel.getApprovalStatus().equalsIgnoreCase("NOT_APPROVED")) {
                    action_name = "" + context.getResources().getString(R.string.not_approved);
                    toolbarColorCode = "#D93838";
                    //      textColorCode = "#D93838";
                    holder.status.setVisibility(View.VISIBLE);
                    holder.status.setText(action_name);
                    holder.status.setBackgroundColor(Color.parseColor(toolbarColorCode));


                } else if (workDiaryDetailsModel.getApprovalStatus().equalsIgnoreCase("APPROVED")) {
                    action_name = "" + context.getResources().getString(R.string.approved);
                    toolbarColorCode = "#0E9430";
                    //textColorCode = "#34C759";

                    holder.status.setVisibility(View.VISIBLE);
                    holder.status.setText(action_name);
                    holder.status.setBackgroundColor(Color.parseColor(toolbarColorCode));

                } else {
                    if (workDiaryDetailsModel.getDataType() != null &&
                            workDiaryDetailsModel.getDataType().equalsIgnoreCase("Assessment")) {

                        if (workDiaryDetailsModel.isShowAssessmentResultScore()) {
                            if (workDiaryDetailsModel.isPassed()) {
                                action_name = "" + context.getResources().getString(R.string.passed);
                                //action_drawable = mContext.getResources().getDrawable(R.drawable.ic_tick_done);
                                toolbarColorCode = "#0E9430";
                            } else {
                                action_name = "" + context.getResources().getString(R.string.failed_text);
                                //action_drawable = mContext.getResources().getDrawable(R.drawable.ic_close_circle);
                                toolbarColorCode = "#D93838";
                            }
                        } else {
                            action_name = "" + context.getResources().getString(R.string.completed);
                            //action_drawable = mContext.getResources().getDrawable(R.drawable.ic_tick_done);
                            toolbarColorCode = "#0E9430";
                        }
                        holder.status.setVisibility(View.VISIBLE);
                        holder.status.setText(action_name);
                        holder.status.setBackgroundColor(Color.parseColor(toolbarColorCode));

                    } else {
                        action_name = "" + context.getResources().getString(R.string.completed);
                        //action_drawable = mContext.getResources().getDrawable(R.drawable.ic_tick_done);
                        toolbarColorCode = "#0E9430";
                        holder.status.setVisibility(View.VISIBLE);
                        holder.status.setText(action_name);
                        holder.status.setBackgroundColor(Color.parseColor(toolbarColorCode));

                    }
                }
            }

            if (workDiaryDetailsModel.getDataType() != null && !workDiaryDetailsModel.getDataType().isEmpty()) {
                if (workDiaryDetailsModel.getDataType().equalsIgnoreCase("Assessment")) {
                    score = context.getResources().getString(R.string.score_text) + " " + workDiaryDetailsModel.getCoins();

                    Spannable spanText = new SpannableString(score);
                    if (score.contains(" ")) {
                        String[] scoreCount = score.split(" ");
                        spanText.setSpan(new ForegroundColorSpan(Color.parseColor("#F87800")), 0, scoreCount[0].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        //spanText.setSpan(new RelativeSizeSpan(1.75f), 0, scoreCount[0].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    }

                    if (workDiaryDetailsModel.getCoins() != 0 && workDiaryDetailsModel.isShowAssessmentResultScore()) {
                        holder.temp_tv_score.setVisibility(View.VISIBLE);
                        holder.info_separator.setVisibility(View.VISIBLE);
                        holder.temp_tv_score.setText(spanText);

                    } else {
                        holder.temp_tv_score.setVisibility(View.GONE);
                        holder.info_separator.setVisibility(View.GONE);
                    }
                } else {
                    coins = workDiaryDetailsModel.getCoins() + "";
                    holder.temp_tv_score.setVisibility(View.GONE);
                    holder.info_separator.setVisibility(View.GONE);
                }
            }

            if (workDiaryDetailsModel.getDataType() != null && workDiaryDetailsModel.getDataType().equalsIgnoreCase("PLAYLIST")) {
                if (workDiaryDetailsModel.getThumbnailIcon() != null && !workDiaryDetailsModel.getThumbnailIcon().isEmpty()) {
                    image = workDiaryDetailsModel.getThumbnailIcon();
                } else {
                    if (workDiaryDetailsModel.getBanner() != null && !workDiaryDetailsModel.getBanner().isEmpty()) {
                        image = workDiaryDetailsModel.getBanner();
                    } else {
                        image = AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL + "mpower/defaultImage/bannerImage.jpg";
                    }
                }
            } else {
                if (workDiaryDetailsModel.getBanner() != null && !workDiaryDetailsModel.getBanner().isEmpty()) {
                    image = workDiaryDetailsModel.getBanner();
                } else {
                    image = AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL + "mpower/defaultImage/bannerImage.jpg";
                }
            }

            name = workDiaryDetailsModel.getActivityName();


            holder.tv_module_type.setText(eventType);
            holder.tv_module_type.setVisibility(View.VISIBLE);
            holder.tv_date.setText(date);

            holder.tv_title.setText(name);
            // holder.tv_action.setText(action_name);


            if (coins.equalsIgnoreCase("0") || coins.isEmpty() || coins.equalsIgnoreCase("null")) {
                holder.tv_coin.setVisibility(View.GONE);
            } else {
                if (workDiaryDetailsModel.getDataType() != null && workDiaryDetailsModel.getDataType().equalsIgnoreCase("PLAYLIST")) {
                    holder.info_separator.setVisibility(View.GONE);
                    holder.tv_coin.setVisibility(View.GONE);
                } else {
                    holder.tv_coin.setVisibility(View.VISIBLE);
                    holder.info_separator.setVisibility(View.VISIBLE);
                }
                holder.tv_coin.setText(coins);
            }

            if (image != null && !image.isEmpty()) {
                try {
                    Glide.with(context).load(image).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.iv_banner);
                } catch (Exception e) {
                    if (workDiaryDetailsModel.getDataType() != null && !workDiaryDetailsModel.getDataType().isEmpty() &&
                            workDiaryDetailsModel.getDataType().equalsIgnoreCase("PLAYLIST")) {
                        Picasso.get().load(image).error(R.drawable.cpl_thumbnail).into(holder.iv_banner);
                    }
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

            holder.edit_iv.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.selectedEditPosition(workDiaryDetailsModel);
                }
            });

            holder.delete_iv.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.selectedDeletePosition(workDiaryDetailsModel);
                }
            });

            holder.task_root_layout.setOnClickListener(v -> workDiaryRedirection(workDiaryDetailsModel));
        }
    }


    @Override
    public int getItemCount() {
        if (tempWorkDiaryDetailsModelArrayList == null) {
            return 0;
        }
        return tempWorkDiaryDetailsModelArrayList.size();
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public Filter getFilter() {
        if (fRecords == null) {
            fRecords = new RecordFilter();
        }
        return fRecords;
    }

    public void setWorkDiaryInterface(WorkDiaryActivity context) {
        this.context = context;
        this.searchInterFace = (searchInterFace) context;
        mListener = (SelectEditDelete) this.context;
    }

    private class RecordFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
//            Log.e("TAG", "constraint Data: " + constraint);
            FilterResults filterResults = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                filterResults.values = workDiaryDetailsModelArrayList;
                filterResults.count = workDiaryDetailsModelArrayList.size();
            } else {
                ArrayList<WorkDiaryDetailsModel> list = new ArrayList<>();
                constraint = constraint.toString().toLowerCase();
                for (WorkDiaryDetailsModel item : workDiaryDetailsModelArrayList) {
                    if (item.getActivityName().toLowerCase().contains(constraint.toString())) {
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
            if (results.count > 0) {
                tempWorkDiaryDetailsModelArrayList = (ArrayList<WorkDiaryDetailsModel>) results.values;
                notifyDataSetChanged();
                searchInterFace.searchModuleNotFound("");
            } else {
                tempWorkDiaryDetailsModelArrayList = (ArrayList<WorkDiaryDetailsModel>) results.values;
                notifyDataSetChanged();
                if (searchInterFace != null) {
                    searchInterFace.searchModuleNotFound("notFound");
                }
            }
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        View include_date, include_module;
        ImageView iv_banner, view_dot;
        TextView status;
        LinearLayout end_lay;
        LinearLayout non_event_lay;
        LinearLayout task_root_layout;
        View info_separator;
        TextView tv_score, temp_tv_score;
        TextView tv_date, tv_module_type, tv_title, tv_coin, tv_timer, tv_time,
                tv_time_end, tv_percentage;//tv_action
        ProgressBar pb_module;

        ImageView edit_iv, delete_iv;
        LinearLayout entry_layout;


        public ViewHolder(View itemView) {
            super(itemView);

            include_date = itemView.findViewById(R.id.include_date);
            include_module = itemView.findViewById(R.id.include_module);
            tv_date = include_date.findViewById(R.id.tv_date);
            view_dot = include_date.findViewById(R.id.view_dot);
            tv_module_type = include_module.findViewById(R.id.tv_module_type);
            iv_banner = include_module.findViewById(R.id.iv_banner);
            status = include_module.findViewById(R.id.status);
            tv_title = include_module.findViewById(R.id.tv_title);
            non_event_lay = include_module.findViewById(R.id.non_event_lay);
            entry_layout = include_module.findViewById(R.id.entry_layout);
            edit_iv = include_module.findViewById(R.id.edit_iv);
            delete_iv = include_module.findViewById(R.id.delete_iv);
            tv_coin = include_module.findViewById(R.id.tv_coin);
            try {
                if (OustPreferences.getAppInstallVariable("showCorn")) {
                    tv_coin.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_coins_corn, 0, 0, 0);
                } else {
                    tv_coin.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_coins_golden, 0, 0, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

            tv_timer = include_module.findViewById(R.id.tv_timer);
            tv_score = include_module.findViewById(R.id.tv_score);
            temp_tv_score = include_module.findViewById(R.id.temp_tv_score);
            info_separator = include_module.findViewById(R.id.info_separator);
            tv_time = include_module.findViewById(R.id.tv_time);
            end_lay = include_module.findViewById(R.id.end_lay);
            tv_time_end = include_module.findViewById(R.id.tv_time_end);
            pb_module = include_module.findViewById(R.id.pb_module);
            tv_percentage = include_module.findViewById(R.id.tv_percentage);
            task_root_layout = itemView.findViewById(R.id.task_root_layout);
        }
    }

    public interface SelectEditDelete {
        void selectedEditPosition(WorkDiaryDetailsModel workDiaryDetailsModel);

        void selectedDeletePosition(WorkDiaryDetailsModel workDiaryDetailsModel);
    }


    private void workDiaryRedirection(WorkDiaryDetailsModel diaryDetailsModel) {
        if (diaryDetailsModel.getDataType() == null || diaryDetailsModel.getDataType().equalsIgnoreCase("MY ENTRY")) {
            //   OustSdkTools.showToast("Could not open manual entry");
            Log.e("WDAdapter", "MY Entry");
        } else {
            if (diaryDetailsModel.getDataType().equalsIgnoreCase("ASSESSMENT")) {
                BranchTools.gotoAssessmentForWorkDiary(diaryDetailsModel.getLearningDiaryID(), diaryDetailsModel.getAttachedCourseId());
            } else if (diaryDetailsModel.getDataType().equalsIgnoreCase("SURVEY")) {
                BranchTools.gotoSurvey(diaryDetailsModel.getLearningDiaryID(), diaryDetailsModel.getMappedCourseId());
            } else if (diaryDetailsModel.getDataType().equalsIgnoreCase("PLAYLIST")) {
                BranchTools.gotoCplPage(diaryDetailsModel.getLearningDiaryID());
            } else {
                if (diaryDetailsModel.getMode() != null && diaryDetailsModel.getMode().equalsIgnoreCase("ARCHIVED")) {
                    OustSdkTools.showToast(context.getResources().getString(R.string.sorry_archive_by_admin));
                } else {
                    BranchTools.gotoCoursePage(diaryDetailsModel.getLearningDiaryID());
                }

            }
        }
    }

    public interface searchInterFace {
        void searchModuleNotFound(String data);
    }
}
