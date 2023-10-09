package com.oustme.oustsdk.course_ui.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.R;
import com.oustme.oustsdk.adapter.courses.RegularModeAssessmentAdapter;
import com.oustme.oustsdk.adapter.courses.RegularModeSurveyAdapter;
import com.oustme.oustsdk.firebase.course.SearchCourseLevel;
import com.oustme.oustsdk.interfaces.course.ReviewModeCallBack;
import com.oustme.oustsdk.response.course.AssessmentNavModel;
import com.oustme.oustsdk.response.course.SurveyNavModel;
import com.oustme.oustsdk.room.dto.DTOUserCardData;
import com.oustme.oustsdk.room.dto.DTOUserCourseData;
import com.oustme.oustsdk.room.dto.DTOUserLevelData;
import com.oustme.oustsdk.sqlite.UserCourseScoreDatabaseHandler;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.utils.OustResourceUtils;

import java.util.ArrayList;
import java.util.List;

public class NonPlayModeAdapter extends RecyclerView.Adapter<NonPlayModeAdapter.ViewHolder> {

    Context context;
    List<SearchCourseLevel> searchCourseLevelList = new ArrayList<>();
    boolean isSearchMode;
    AssessmentNavModel assessmentNavModel;
    SurveyNavModel surveyNavModel;
    DTOUserCourseData userCourseData;
    boolean isSalesMode;
    ReviewModeCallBack reviewModeCallBack;
    UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler;
    boolean isReviewMode;

    public void setNonPlayModeAdapter(Context context, List<SearchCourseLevel> searchCourseLevelList, boolean isSearchMode,
                                      AssessmentNavModel assessmentNavModel, SurveyNavModel surveyNavModel, DTOUserCourseData userCourseData, boolean isSalesMode) {
        this.context = context;
        this.searchCourseLevelList = searchCourseLevelList;
        this.isSearchMode = isSearchMode;
        this.assessmentNavModel = assessmentNavModel;
        this.surveyNavModel = surveyNavModel;
        this.userCourseData = userCourseData;
        this.isSalesMode = isSalesMode;
    }

    public void setReviewMode(boolean isReviewMode) {
        this.isReviewMode = isReviewMode;
        this.notifyDataSetChanged();
    }

    public void setReviewModeCallBack(ReviewModeCallBack reviewModeCallBack) {
        this.reviewModeCallBack = reviewModeCallBack;
    }

    public void notifyDateChanges(List<SearchCourseLevel> searchCourseLevelList, boolean isSearchMode,
                                  AssessmentNavModel assessmentNavModel, SurveyNavModel surveyNavModel, DTOUserCourseData userCourseData, boolean isSalesMode) {
        if (searchCourseLevelList != null && searchCourseLevelList.size() > 0) {
            this.searchCourseLevelList.clear();
            this.searchCourseLevelList.addAll(searchCourseLevelList);
        }
        this.isSearchMode = isSearchMode;
        this.assessmentNavModel = assessmentNavModel;
        this.surveyNavModel = surveyNavModel;
        this.userCourseData = userCourseData;
        this.isSalesMode = isSalesMode;
        this.notifyDataSetChanged();
    }

    public void notifyDateChanges(List<SearchCourseLevel> searchCourseLevelList, boolean isSearchMode) {
        if (searchCourseLevelList != null && searchCourseLevelList.size() > 0) {
            this.searchCourseLevelList.clear();
            this.searchCourseLevelList.addAll(searchCourseLevelList);
        }
        //this.searchCourseLevelList = searchCourseLevelList;
        this.isSearchMode = isSearchMode;
        this.notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout progress_lay;
        ProgressBar user_level_progress;
        TextView level_no;
        TextView level_name;
        RecyclerView level_card_rv;
        ImageView arrow_iv;
        LevelCardAdapter levelCardAdapter;
        RegularModeAssessmentAdapter regularModeAssessmentAdapter;
        RegularModeSurveyAdapter regularModeSurveyAdapter;

        ViewHolder(View view) {
            super(view);
            progress_lay = view.findViewById(R.id.progress_lay);
            user_level_progress = view.findViewById(R.id.user_level_progress);
            level_no = view.findViewById(R.id.level_no);
            level_name = view.findViewById(R.id.level_name);
            level_card_rv = view.findViewById(R.id.level_card_rv);
            arrow_iv = view.findViewById(R.id.arrow_iv);
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_non_play_mode, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.setIsRecyclable(false);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try {
            DTOUserCourseData dtoUserCourseData = getUserCourseData();
            if (position < searchCourseLevelList.size()) {

                SearchCourseLevel searchCourseLevel = searchCourseLevelList.get(position);
                if (searchCourseLevel != null) {

                    if (searchCourseLevel.getName() != null && !searchCourseLevel.getName().isEmpty()) {
                        holder.level_name.setText(searchCourseLevel.getName());
                    }

                    holder.level_no.setText(String.valueOf(position + 1));

                    boolean isCourseCompleted = false;
                    boolean isLevelLocked = true;
                    Drawable progressDrawable = context.getResources().getDrawable(R.drawable.circle_shape);

                    if ((dtoUserCourseData != null) && (dtoUserCourseData.getUserLevelDataList() != null) &&
                            (dtoUserCourseData.getUserLevelDataList().size() > position) &&
                            (dtoUserCourseData.getUserLevelDataList().get(position) != null)) {

                        if (isReviewMode) {
                            if ((int) dtoUserCourseData.getPresentageComplete() >= 100) {
                                isCourseCompleted = true;
                            }
                        } else {
                            if ((int) dtoUserCourseData.getPresentageComplete() >= 100 && !(dtoUserCourseData.getDownloadCompletePercentage() < 100)) {
                                isCourseCompleted = true;
                            }
                        }

                        isLevelLocked = dtoUserCourseData.getUserLevelDataList().get(position).isLocked();

                        if (position == dtoUserCourseData.getCurrentLevel()) {
                            holder.user_level_progress.setProgress(100);
                            holder.user_level_progress.setProgressDrawable(OustResourceUtils.setDefaultDrawableColor(progressDrawable, context.getResources().getColor(R.color.current_level_bg)));
                        } else if (position < dtoUserCourseData.getCurrentLevel()) {
                            holder.user_level_progress.setProgress(100);
                            holder.user_level_progress.setProgressDrawable(OustResourceUtils.setDefaultDrawableColor(progressDrawable, context.getResources().getColor(R.color.completed_level_bg)));
                        }
                    }

                    if (!isSalesMode) {
                        if (dtoUserCourseData != null && dtoUserCourseData.getUserLevelDataList() != null && dtoUserCourseData.getUserLevelDataList().size() > position && (dtoUserCourseData.getUserLevelDataList().get(position) != null)) {
                            if (!isCourseCompleted && (position > 0 && isLevelLocked)) {
                                holder.arrow_iv.setImageDrawable(OustResourceUtils.setDefaultDrawableColor(context.getResources().getDrawable(R.drawable.ic_lock_img),
                                        context.getResources().getColor(R.color.unselected_text)));
                                holder.arrow_iv.setTag(R.drawable.ic_lock_img);

                            } else {
                                holder.arrow_iv.setImageDrawable(OustResourceUtils.setDefaultDrawableColor(context.getResources().getDrawable(R.drawable.ic_down_arrow_expand),
                                        context.getResources().getColor(R.color.primary_text)));
                                holder.arrow_iv.setTag(R.drawable.ic_down_arrow_expand);

                            }
                        }
                    } else {
                        holder.arrow_iv.setImageDrawable(OustResourceUtils.setDefaultDrawableColor(context.getResources().getDrawable(R.drawable.ic_down_arrow_expand),
                                context.getResources().getColor(R.color.primary_text)));
                        holder.arrow_iv.setTag(R.drawable.ic_down_arrow_expand);

                    }

                    if ((int) holder.arrow_iv.getTag() == R.drawable.ic_down_arrow_expand) {
                        holder.arrow_iv.setTag(R.drawable.ic_down_arrow_expand);
                        holder.arrow_iv.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_down_arrow_expand));
                        holder.level_card_rv.setVisibility(View.GONE);

                    } else if ((int) holder.arrow_iv.getTag() == R.drawable.ic_up_arrow_collapse) {
                        if (holder.levelCardAdapter != null) {
                            holder.arrow_iv.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_up_arrow_collapse));

                            List<DTOUserCardData> userCardDataList = null;
                            if ((userCourseData != null && userCourseData.getUserLevelDataList() != null &&
                                    userCourseData.getUserLevelDataList().size() > position &&
                                    userCourseData.getUserLevelDataList().get(position).getUserCardDataList() != null)) {
                                userCardDataList = userCourseData.getUserLevelDataList().get(position).getUserCardDataList();
                            }
                            boolean finalIsLevelLocked = false;
                            boolean finalIsCourseCompleted = false;

                            DTOUserCourseData dtoUserCourseData1 = getUserCourseData();
                            if ((dtoUserCourseData1 != null) && (dtoUserCourseData1.getUserLevelDataList() != null) &&
                                    (dtoUserCourseData1.getUserLevelDataList().size() > position) &&
                                    (dtoUserCourseData1.getUserLevelDataList().get(position) != null)) {
                                if ((int) dtoUserCourseData.getPresentageComplete() >= 100 && !(dtoUserCourseData.getDownloadCompletePercentage() < 100)) {
                                    finalIsCourseCompleted = true;
                                }
                                finalIsLevelLocked = dtoUserCourseData.getUserLevelDataList().get(position).isLocked();
                            }
                            List<DTOUserCardData> finalUserCardDataList = userCardDataList;
                            List<DTOUserCardData> dtoUserCardDataList1 = dtoUserCourseData1.getUserLevelDataList().get(position).getUserCardDataList();
                            holder.levelCardAdapter.setAdapter(context, searchCourseLevelList.get(position).getSearchCourseCards(), position,
                                    dtoUserCardDataList1, finalIsLevelLocked, isSalesMode, finalIsCourseCompleted, finalUserCardDataList);
                            holder.levelCardAdapter.setReviewModeCallBack(reviewModeCallBack);
                            LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                            holder.level_card_rv.setLayoutManager(mLayoutManager);
                            holder.level_card_rv.setAdapter(holder.levelCardAdapter);
                        } else {
                            holder.arrow_iv.setTag(R.drawable.ic_down_arrow_expand);
                            holder.arrow_iv.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_down_arrow_expand));
                            holder.level_card_rv.setVisibility(View.GONE);
                        }
                    }

                    holder.arrow_iv.setOnClickListener(v -> {
                        if ((int) holder.arrow_iv.getTag() == R.drawable.ic_down_arrow_expand) {
                            holder.level_card_rv.setVisibility(View.VISIBLE);
                            holder.arrow_iv.setTag(R.drawable.ic_up_arrow_collapse);
                            holder.arrow_iv.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_up_arrow_collapse));

                            holder.levelCardAdapter = new LevelCardAdapter();

                            //handle level cards and questions
                            List<DTOUserCardData> userCardDataList = null;
                            if ((userCourseData != null && userCourseData.getUserLevelDataList() != null &&
                                    userCourseData.getUserLevelDataList().size() > position &&
                                    userCourseData.getUserLevelDataList().get(position).getUserCardDataList() != null)) {
                                userCardDataList = userCourseData.getUserLevelDataList().get(position).getUserCardDataList();
                            }

                            boolean finalIsLevelLocked = false;
                            boolean finalIsCourseCompleted = false;

                            DTOUserCourseData dtoUserCourseData1 = getUserCourseData();

                            if ((dtoUserCourseData1 != null) && (dtoUserCourseData1.getUserLevelDataList() != null) &&
                                    (dtoUserCourseData1.getUserLevelDataList().size() > position) &&
                                    (dtoUserCourseData1.getUserLevelDataList().get(position) != null)) {
                                //dtoUserCardDataList = dtoUserCourseData.getUserLevelDataList().get(position).getUserCardDataList();
                                if (dtoUserCourseData != null && (int) dtoUserCourseData.getPresentageComplete() >= 100 && !(dtoUserCourseData.getDownloadCompletePercentage() < 100)) {
                                    finalIsCourseCompleted = true;
                                }
                                finalIsLevelLocked = dtoUserCourseData.getUserLevelDataList().get(position).isLocked();
                            }
                            List<DTOUserCardData> finalUserCardDataList = userCardDataList;


                            List<DTOUserCardData> dtoUserCardDataList1 = null;
                            if (dtoUserCourseData1 != null && dtoUserCourseData1.getUserLevelDataList() != null) {
                                dtoUserCardDataList1 = dtoUserCourseData1.getUserLevelDataList().get(position).getUserCardDataList();
                                holder.levelCardAdapter.setAdapter(context, searchCourseLevelList.get(position).getSearchCourseCards(), position,
                                        dtoUserCardDataList1, finalIsLevelLocked, isSalesMode, finalIsCourseCompleted, finalUserCardDataList);
                                holder.levelCardAdapter.setReviewModeCallBack(reviewModeCallBack);
                                LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                                holder.level_card_rv.setLayoutManager(mLayoutManager);
                                holder.level_card_rv.setAdapter(holder.levelCardAdapter);
                            }
                            // reviewModeCallBack.onMainRowClick(position);

                        } else if ((int) holder.arrow_iv.getTag() == R.drawable.ic_up_arrow_collapse) {
                            //handle animation for hiding
                            holder.level_card_rv.animate()
                                    .alpha(0.0f)
                                    .translationY(0)
                                    .setDuration(1000)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            holder.arrow_iv.setTag(R.drawable.ic_down_arrow_expand);
                                            holder.arrow_iv.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_down_arrow_expand));
                                            holder.level_card_rv.setVisibility(View.GONE);
                                            holder.level_card_rv.animate()
                                                    .alpha(1f)
                                                    .setDuration(100)
                                                    .setListener(null);
                                        }
                                    });

                            //reviewModeCallBack.onMainRowClick(position);

                        } else {
                            if (dtoUserCourseData != null && !dtoUserCourseData.getUserLevelDataList().get(position).isLocked()) {
                                return;
                            }
                            OustSdkTools.showToast(context.getResources().getString(R.string.complete_level_unlock));
                        }
                    });
                }

            } else if (assessmentNavModel != null && surveyNavModel != null && position == getItemCount() - 2) {
                holder.regularModeAssessmentAdapter = null;
                List<DTOUserCardData> userCardDataList = null;
                boolean isCourseCompleted = false;
                boolean isAssessmentLocked = true;
                List<DTOUserLevelData> userLevelDataList = dtoUserCourseData.getUserLevelDataList();

                if (userLevelDataList != null && userLevelDataList.size() > position && userLevelDataList.get(position) != null) {
                    userCardDataList = userLevelDataList.get(position).getUserCardDataList();
                }

                holder.level_name.setText(context.getResources().getString(R.string.assessment));
                holder.level_no.setText("A");

                if (((int) dtoUserCourseData.getPresentageComplete() >= 95 || (int) dtoUserCourseData.getPresentageComplete() >= 90) && dtoUserCourseData.isCourseCompleted()) {
                    holder.arrow_iv.setTag(R.drawable.ic_down_arrow_expand);
                    holder.arrow_iv.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_down_arrow_expand));
                    holder.level_card_rv.setVisibility(View.GONE);
                    isCourseCompleted = true;
                    isAssessmentLocked = false;
                } else {
                    holder.arrow_iv.setImageDrawable(OustResourceUtils.setDefaultDrawableColor(context.getResources().getDrawable(R.drawable.ic_lock_img),
                            context.getResources().getColor(R.color.unselected_text)));
                    holder.arrow_iv.setTag(R.drawable.ic_lock_img);
                }

                boolean finalIsAssessmentLocked = isAssessmentLocked;
                boolean finalIsCourseCompleted = isCourseCompleted;
                List<DTOUserCardData> finalUserCardDataList = userCardDataList;
                holder.arrow_iv.setOnClickListener(v -> {
                    if ((int) holder.arrow_iv.getTag() == R.drawable.ic_down_arrow_expand) {

                        holder.level_card_rv.setVisibility(View.VISIBLE);
                        holder.arrow_iv.setTag(R.drawable.ic_up_arrow_collapse);
                        holder.arrow_iv.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_up_arrow_collapse));

                        try {
                            List<AssessmentNavModel> assessmentNavModels = new ArrayList<>();
                            if (dtoUserCourseData.getPresentageComplete() == 100) {
                                assessmentNavModel.setMappedAssessmentPercentage(100);
                            }
                            assessmentNavModels.add(assessmentNavModel);

                            List<DTOUserCardData> userCardDataLists = null;
                            if ((userCourseData != null && userCourseData.getUserLevelDataList() != null && userCourseData.getUserLevelDataList().size() > position && userCourseData.getUserLevelDataList().get(position).getUserCardDataList() != null)) {
                                userCardDataLists = userCourseData.getUserLevelDataList().get(position).getUserCardDataList();
                            }

                            if (holder.regularModeAssessmentAdapter == null) {
                                LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                                holder.level_card_rv.setLayoutManager(mLayoutManager);
                                holder.regularModeAssessmentAdapter = new RegularModeAssessmentAdapter(assessmentNavModels, finalIsCourseCompleted, finalUserCardDataList, userCardDataLists, true);
                                holder.level_card_rv.setAdapter(holder.regularModeAssessmentAdapter);
                            } else {
                                holder.regularModeAssessmentAdapter.notifyDateChanges(assessmentNavModels, finalIsCourseCompleted, finalUserCardDataList, userCardDataLists, true);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }


                    } else if ((int) holder.arrow_iv.getTag() == R.drawable.ic_up_arrow_collapse) {
                        //handle animation for hiding
                        holder.level_card_rv.animate()
                                .alpha(0.0f)
                                .translationY(0)
                                .setDuration(1000)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        holder.arrow_iv.setTag(R.drawable.ic_down_arrow_expand);
                                        holder.arrow_iv.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_down_arrow_expand));
                                        holder.level_card_rv.setVisibility(View.GONE);
                                        holder.level_card_rv.animate()
                                                .alpha(1f)
                                                .setDuration(100)
                                                .setListener(null);
                                    }
                                });


                    } else {
                        if (!finalIsAssessmentLocked) {
                            return;
                        }
                        OustSdkTools.showToast(context.getResources().getString(R.string.complete_level_unlock));
                    }
                });

            } else if (surveyNavModel == null && assessmentNavModel != null && position == getItemCount() - 1) {
                holder.regularModeAssessmentAdapter = null;
                List<DTOUserCardData> userCardDataList = null;
                boolean isCourseCompleted = false;
                boolean isAssessmentLocked = true;
                List<DTOUserLevelData> userLevelDataList = dtoUserCourseData.getUserLevelDataList();

                if (userLevelDataList != null && userLevelDataList.size() > position && userLevelDataList.get(position) != null) {
                    Log.d("RegularModeAdapterAss", "RelamData Found");
                    userCardDataList = userLevelDataList.get(position).getUserCardDataList();

                }

                holder.level_name.setText(context.getResources().getString(R.string.assessment));
                holder.level_no.setText("A");

                if ((int) dtoUserCourseData.getPresentageComplete() >= 95 && dtoUserCourseData.isCourseCompleted()) {
                    holder.arrow_iv.setTag(R.drawable.ic_down_arrow_expand);
                    holder.arrow_iv.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_down_arrow_expand));
                    holder.level_card_rv.setVisibility(View.GONE);
                    isCourseCompleted = true;
                    isAssessmentLocked = false;
                } else {

                    holder.arrow_iv.setImageDrawable(OustResourceUtils.setDefaultDrawableColor(context.getResources().getDrawable(R.drawable.ic_lock_img),
                            context.getResources().getColor(R.color.unselected_text)));
                    holder.arrow_iv.setTag(R.drawable.ic_lock_img);
                }

                boolean finalIsAssessmentLocked = isAssessmentLocked;
                boolean finalIsCourseCompleted = isCourseCompleted;
                List<DTOUserCardData> finalUserCardDataList = userCardDataList;
                holder.arrow_iv.setOnClickListener(v -> {
                    if ((int) holder.arrow_iv.getTag() == R.drawable.ic_down_arrow_expand) {

                        holder.level_card_rv.setVisibility(View.VISIBLE);
                        holder.arrow_iv.setTag(R.drawable.ic_up_arrow_collapse);
                        holder.arrow_iv.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_up_arrow_collapse));

                        try {
                            List<AssessmentNavModel> assessmentNavModels = new ArrayList<>();
                            if (dtoUserCourseData.getPresentageComplete() == 100) {
                                assessmentNavModel.setMappedAssessmentPercentage(100);
                            }
                            assessmentNavModels.add(assessmentNavModel);

                            List<DTOUserCardData> userCardDataLists = null;
                            if ((userCourseData != null && userCourseData.getUserLevelDataList() != null && userCourseData.getUserLevelDataList().size() > position && userCourseData.getUserLevelDataList().get(position).getUserCardDataList() != null)) {
                                userCardDataLists = userCourseData.getUserLevelDataList().get(position).getUserCardDataList();
                            }

                            if (holder.regularModeAssessmentAdapter == null) {
                                LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                                holder.level_card_rv.setLayoutManager(mLayoutManager);
                                holder.regularModeAssessmentAdapter = new RegularModeAssessmentAdapter(assessmentNavModels, finalIsCourseCompleted, finalUserCardDataList, userCardDataLists, false);
                                holder.level_card_rv.setAdapter(holder.regularModeAssessmentAdapter);
                            } else {
                                holder.regularModeAssessmentAdapter.notifyDateChanges(assessmentNavModels, finalIsCourseCompleted, finalUserCardDataList, userCardDataLists, false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                    } else if ((int) holder.arrow_iv.getTag() == R.drawable.ic_up_arrow_collapse) {
                        //handle animation for hiding
                        holder.level_card_rv.animate()
                                .alpha(0.0f)
                                .translationY(0)
                                .setDuration(1000)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        holder.arrow_iv.setTag(R.drawable.ic_down_arrow_expand);
                                        holder.arrow_iv.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_down_arrow_expand));
                                        holder.level_card_rv.setVisibility(View.GONE);
                                        holder.level_card_rv.animate()
                                                .alpha(1f)
                                                .setDuration(100)
                                                .setListener(null);
                                    }
                                });


                    } else {
                        if (!finalIsAssessmentLocked) {
                            return;
                        }
                        OustSdkTools.showToast(context.getResources().getString(R.string.complete_level_unlock));
                    }
                });

            } else if (surveyNavModel != null && position == getItemCount() - 1) {
                holder.regularModeSurveyAdapter = null;
                List<DTOUserCardData> userCardDataList = null;
                boolean isCourseCompleted = false;
                boolean isSurveyLocked = true;
                List<DTOUserLevelData> userLevelDataList = dtoUserCourseData.getUserLevelDataList();

                if (userLevelDataList != null && userLevelDataList.size() > position && userLevelDataList.get(position) != null) {
                    userCardDataList = userLevelDataList.get(position).getUserCardDataList();
                }

                holder.level_name.setText(context.getResources().getString(R.string.survey_text));
                holder.level_no.setText("S");

                if ((int) dtoUserCourseData.getPresentageComplete() >= 95 && dtoUserCourseData.isCourseCompleted()) {
                    holder.arrow_iv.setTag(R.drawable.ic_down_arrow_expand);
                    holder.arrow_iv.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_down_arrow_expand));
                    holder.level_card_rv.setVisibility(View.GONE);
                    isCourseCompleted = true;
                    isSurveyLocked = false;
                } else {
                    holder.arrow_iv.setImageDrawable(OustResourceUtils.setDefaultDrawableColor(context.getResources().getDrawable(R.drawable.ic_lock_img),
                            context.getResources().getColor(R.color.unselected_text)));
                    holder.arrow_iv.setTag(R.drawable.ic_lock_img);
                }

                boolean finalIsSurveyLocked = isSurveyLocked;
                boolean finalIsCourseCompleted = isCourseCompleted;
                List<DTOUserCardData> finalUserCardDataList = userCardDataList;
                holder.arrow_iv.setOnClickListener(v -> {
                    if ((int) holder.arrow_iv.getTag() == R.drawable.ic_down_arrow_expand) {
                        holder.level_card_rv.setVisibility(View.VISIBLE);
                        holder.arrow_iv.setTag(R.drawable.ic_up_arrow_collapse);
                        holder.arrow_iv.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_up_arrow_collapse));

                        try {
                            List<SurveyNavModel> surveyNavModels = new ArrayList<>();
                            if (dtoUserCourseData.getPresentageComplete() == 100) {
                                surveyNavModel.setMappedSurveyPercentage(100);
                            }
                            surveyNavModels.add(surveyNavModel);

                            List<DTOUserCardData> userCardDataLists = null;
                            if ((userCourseData != null && userCourseData.getUserLevelDataList() != null && userCourseData.getUserLevelDataList().size() > position && userCourseData.getUserLevelDataList().get(position).getUserCardDataList() != null)) {
                                userCardDataLists = userCourseData.getUserLevelDataList().get(position).getUserCardDataList();
                            }

                            if (holder.regularModeSurveyAdapter == null) {
                                LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                                holder.level_card_rv.setLayoutManager(mLayoutManager);
                                holder.regularModeSurveyAdapter = new RegularModeSurveyAdapter(surveyNavModels, finalIsCourseCompleted, finalUserCardDataList, userCardDataLists);
                                holder.level_card_rv.setAdapter(holder.regularModeSurveyAdapter);
                            } else {
                                holder.regularModeSurveyAdapter.notifyDateChanges(surveyNavModels, finalIsCourseCompleted, finalUserCardDataList, userCardDataLists);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }

                    } else if ((int) holder.arrow_iv.getTag() == R.drawable.ic_up_arrow_collapse) {
                        //handle animation for hiding
                        holder.level_card_rv.animate()
                                .alpha(0.0f)
                                .translationY(0)
                                .setDuration(1000)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        holder.arrow_iv.setTag(R.drawable.ic_down_arrow_expand);
                                        holder.arrow_iv.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_down_arrow_expand));
                                        holder.level_card_rv.setVisibility(View.GONE);
                                        holder.level_card_rv.animate()
                                                .alpha(1f)
                                                .setDuration(100)
                                                .setListener(null);
                                    }
                                });

                    } else {
                        if (!finalIsSurveyLocked) {
                            return;
                        }
                        Log.d("TAG", "onBindViewHolder: please complete previous level");
                        OustSdkTools.showToast(context.getResources().getString(R.string.complete_level_unlock));
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    @Override
    public int getItemCount() {
        if (surveyNavModel != null && assessmentNavModel != null) {
            return searchCourseLevelList.size() + 2;
        } else if (assessmentNavModel != null || surveyNavModel != null) {
            return searchCourseLevelList.size() + 1;
        } else {
            return searchCourseLevelList.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    //data handle methods - user defined
    public DTOUserCourseData getUserCourseData() {
        if (userCourseScoreDatabaseHandler == null) {
            userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
        }
        return userCourseScoreDatabaseHandler.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
    }
}
