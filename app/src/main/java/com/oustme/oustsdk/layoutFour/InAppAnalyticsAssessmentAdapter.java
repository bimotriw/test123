package com.oustme.oustsdk.layoutFour;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.assessments.AssessmentPlayActivity;
import com.oustme.oustsdk.assessment_ui.assessmentDetail.AssessmentDetailScreen;
import com.oustme.oustsdk.response.common.GameType;
import com.oustme.oustsdk.response.common.InAppAnalyticsResponse;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.BranchTools;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.utils.OustResourceUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class InAppAnalyticsAssessmentAdapter extends RecyclerView.Adapter<InAppAnalyticsAssessmentAdapter.ViewHolder> {

    Context context;
    String courses;
    ArrayList<InAppAnalyticsResponse.Assessments> assessmentsArrayList;
    int color;
    int bgColor;

    public InAppAnalyticsAssessmentAdapter(InAppAnalyticsDetails inAppAnalyticsDetails, ArrayList<InAppAnalyticsResponse.Assessments> inAppAnalyticsResponse, String courses) {
        context = inAppAnalyticsDetails;
        this.assessmentsArrayList = inAppAnalyticsResponse;
        this.courses = courses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_analytics, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        viewHolder.setIsRecyclable(false);
        return viewHolder;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        InAppAnalyticsResponse.Assessments inAppAnalyticsResponseIs = assessmentsArrayList.get(position);

        getColors();

        if (inAppAnalyticsResponseIs != null) {

            String eventType = "";
            String name;
            String coins = "";
            String durationText = "1 min";
            String date = "";
            String score;
            String action_name;
            String image;
            holder.pb_module.setVisibility(View.GONE);
            holder.tv_percentage.setVisibility(View.GONE);

            String toolbarColorCode;

            if (inAppAnalyticsResponseIs.getContentDuration() != 0) {
                double duration = (inAppAnalyticsResponseIs.getContentDuration()) / 60;
                durationText = (int) duration + " mins";
                if (inAppAnalyticsResponseIs.getContentDuration() < 60) {
                    durationText = "1 min";
                }
            }

            holder.tv_timer.setVisibility(View.VISIBLE);
            holder.tv_timer.setText(durationText);

            if (inAppAnalyticsResponseIs.getAssignedOn() != null && !inAppAnalyticsResponseIs.getAssignedOn().isEmpty()) {
                try {
                    Date endTsDate = new Date(Long.parseLong(inAppAnalyticsResponseIs.getAssignedOn()));
                    date = new SimpleDateFormat("dd\nMMM", Locale.getDefault()).format(endTsDate);
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }

            }
            String previousDate;
            if (position > 0) {
                if (assessmentsArrayList.get(position - 1).getAssignedOn() != null && !assessmentsArrayList.get(position - 1).getAssignedOn().isEmpty()) {
                    Date endTsDatePrevious = new Date(Long.parseLong(assessmentsArrayList.get(position - 1).getAssignedOn()));
                    previousDate = new SimpleDateFormat("dd\nMMM", Locale.getDefault()).format(endTsDatePrevious);
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

            if (inAppAnalyticsResponseIs.getStatus().equalsIgnoreCase("Pass")) {
//                action_name = "" + context.getResources().getString(R.string.passed);
                action_name = "Pass";
                toolbarColorCode = "#34C759";
                holder.status.setVisibility(View.VISIBLE);
                holder.status.setText(action_name);
                holder.status.setBackgroundColor(Color.parseColor(toolbarColorCode));
            } else if (inAppAnalyticsResponseIs.getStatus().equalsIgnoreCase("Fail")) {
                action_name = "Fail";
                toolbarColorCode = "#E21B1B";
                holder.status.setVisibility(View.VISIBLE);
                holder.status.setText(action_name);
                holder.status.setBackgroundColor(Color.parseColor(toolbarColorCode));
            } else if (inAppAnalyticsResponseIs.getStatus().equalsIgnoreCase("In Progress")) {
                action_name = "In Progress";
                holder.status.setVisibility(View.VISIBLE);
                holder.status.setText(action_name);
                holder.status.setBackgroundColor(Color.parseColor(OustPreferences.get("secondaryColor")));
            } else if (inAppAnalyticsResponseIs.getStatus().equalsIgnoreCase("Not Started")) {
                action_name = "Not Started";
                holder.status.setVisibility(View.VISIBLE);
                holder.status.setText(action_name);
                holder.status.setBackgroundColor(OustSdkTools.getColorBack(R.color.grey_b));
            }

            coins = inAppAnalyticsResponseIs.getEarnedCoins() + "";
            holder.tv_score.setVisibility(View.GONE);
            holder.info_separator.setVisibility(View.GONE);

            if (inAppAnalyticsResponseIs.getBanner() != null && !inAppAnalyticsResponseIs.getBanner().isEmpty()) {
                image = inAppAnalyticsResponseIs.getBanner();
            } else {
                image = AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL + "mpower/defaultImage/bannerImage.jpg";
            }

            name = inAppAnalyticsResponseIs.getAssessmentName();

            holder.tv_module_type.setText(eventType);
            holder.tv_module_type.setVisibility(View.VISIBLE);
            holder.tv_date.setText(date);

            holder.tv_title.setText(name);
            // holder.tv_action.setText(action_name);


            if (coins.equalsIgnoreCase("0") || coins.isEmpty() || coins.equalsIgnoreCase("null")) {
                holder.tv_coin.setVisibility(View.GONE);
            } else {
                holder.tv_coin.setVisibility(View.VISIBLE);
                holder.info_separator.setVisibility(View.VISIBLE);
                holder.tv_coin.setText(coins);
            }

            if (image != null && !image.isEmpty()) {
                Glide.with(context).load(image).into(holder.iv_banner);
            }

            holder.task_root_layout.setOnClickListener(v -> {
                gotoAssessment(inAppAnalyticsResponseIs.getAssessmentId());
//                workDiaryRedirection(inAppAnalyticsResponseIs);
            });
        }


    }

    public void gotoAssessment(String assessmentId) {
        try {
            Gson gson = new GsonBuilder().create();
            ActiveUser activeUser = OustAppState.getInstance().getActiveUser();
            Intent intent;
            if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI)) {
                intent = new Intent(OustSdkApplication.getContext(), AssessmentDetailScreen.class);
            } else {
                intent = new Intent(OustSdkApplication.getContext(), AssessmentPlayActivity.class);
            }
            //Intent intent = new Intent(OustSdkApplication.getContext(), AssessmentDetailScreen.class);
            ActiveGame activeGame = new ActiveGame();
            activeGame.setIsLpGame(false);
            activeGame.setGameid("");
            activeGame.setGames(activeUser.getGames());
            activeGame.setStudentid(activeUser.getStudentid());
            activeGame.setChallengerid(activeUser.getStudentid());
            activeGame.setChallengerDisplayName(activeUser.getUserDisplayName());
            activeGame.setChallengerAvatar(activeUser.getAvatar());
            activeGame.setOpponentAvatar(OustSdkTools.getMysteryAvatar());
            activeGame.setOpponentid("Mystery");
            activeGame.setOpponentDisplayName("Mystery");
            activeGame.setGameType(GameType.MYSTERY);
            activeGame.setGuestUser(false);
            activeGame.setRematch(false);
            activeGame.setGrade(activeUser.getGrade());
            activeGame.setGroupId("");
            activeGame.setSubject(activeUser.getSubject());
            activeGame.setTopic(activeUser.getTopic());
            activeGame.setLevel(activeUser.getLevel());
            activeGame.setLevelPercentage(activeUser.getLevelPercentage());
            activeGame.setWins(activeUser.getWins());
            activeGame.setModuleId(activeUser.getModuleId());
            activeGame.setModuleName(activeUser.getModuleName());
            boolean isAssessmentValid = false;
            if ((OustAppState.getInstance().getAssessmentFirebaseClassList() != null) && (OustAppState.getInstance().getAssessmentFirebaseClassList().size() > 0)) {
                for (int i = 0; i < OustAppState.getInstance().getAssessmentFirebaseClassList().size(); i++) {
                    if ((assessmentId.equalsIgnoreCase(("" + OustAppState.getInstance().getAssessmentFirebaseClassList().get(i).getAsssessemntId())))) {
                        OustAppState.getInstance().setAssessmentFirebaseClass(OustAppState.getInstance().getAssessmentFirebaseClassList().get(i));
                        isAssessmentValid = true;
                    }
                }
                if (!isAssessmentValid) {
                    OustSdkTools.showToast(context.getResources().getString(R.string.assessment_no_longer));
                    return;
                }
            } else {
                intent.putExtra("assessmentId", assessmentId);
            }
            intent.putExtra("ActiveGame", gson.toJson(activeGame));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            OustSdkApplication.getContext().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getColors() {
        try {
            if (OustPreferences.getAppInstallVariable("isLayout4")) {
                color = OustResourceUtils.getColors();
                bgColor = OustResourceUtils.getToolBarBgColor();
            } else {
                bgColor = OustResourceUtils.getColors();
                color = OustResourceUtils.getToolBarBgColor();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public int getItemCount() {
        return assessmentsArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

 /*       TextView tv_title, tv_timer, tv_coin, date_text;
        View info_separator;
        ImageView iv_banner;*/

        View include_date, include_module;
        ImageView iv_banner, view_dot;
        TextView status;
        LinearLayout end_lay;
        LinearLayout non_event_lay;
        LinearLayout task_root_layout;
        View info_separator;
        TextView tv_score;
        TextView tv_date, tv_module_type, tv_title, tv_coin, tv_timer, tv_time,
                tv_time_end, tv_percentage;//tv_action
        ProgressBar pb_module;

        ImageView edit_iv, delete_iv;
        LinearLayout entry_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            /*tv_coin = itemView.findViewById(R.id.tv_coin);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_timer = itemView.findViewById(R.id.tv_timer);
            iv_banner = itemView.findViewById(R.id.iv_banner);
            date_text = itemView.findViewById(R.id.date_text);
            info_separator = itemView.findViewById(R.id.info_separator);*/

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
            info_separator = include_module.findViewById(R.id.info_separator);
            tv_time = include_module.findViewById(R.id.tv_time);
            end_lay = include_module.findViewById(R.id.end_lay);
            tv_time_end = include_module.findViewById(R.id.tv_time_end);
            pb_module = include_module.findViewById(R.id.pb_module);
            tv_percentage = include_module.findViewById(R.id.tv_percentage);
            task_root_layout = itemView.findViewById(R.id.task_root_layout);
        }
    }

    private void workDiaryRedirection(InAppAnalyticsResponse.Assessments diaryDetailsModel) {

        //BranchTools.gotoAssessmentForWorkDiary(diaryDetailsModel.getAssessmentId());

/*        if (diaryDetailsModel.getDataType() == null || diaryDetailsModel.getDataType().equalsIgnoreCase("MY ENTRY")) {
            //   OustSdkTools.showToast("Could not open manual entry");
            Log.e("WDAdapter", "MY Entry");
        } else {
            if (diaryDetailsModel.getDataType().equalsIgnoreCase("ASSESSMENT")) {
                BranchTools.gotoAssessmentForWorkDiary(diaryDetailsModel.getLearningDiaryID());
            } else if (diaryDetailsModel.getDataType().equalsIgnoreCase("SURVEY")) {
                BranchTools.gotoSurvey(diaryDetailsModel.getLearningDiaryID(), diaryDetailsModel.getMappedCourseId());
            } else {
                if (diaryDetailsModel.getMode() != null && diaryDetailsModel.getMode().equalsIgnoreCase("ARCHIVED")) {
                    OustSdkTools.showToast(context.getResources().getString(R.string.sorry_archive_by_admin));
                } else {
                    BranchTools.gotoCoursePage(diaryDetailsModel.getCourseId());
                }

            }
        }*/

    }
}
