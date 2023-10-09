package com.oustme.oustsdk.my_tasks.adapter;

import static com.oustme.oustsdk.utils.LayoutType.GRID;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;


import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.assessments.AssessmentPlayActivity;
import com.oustme.oustsdk.assessment_ui.assessmentDetail.AssessmentDetailScreen;
import com.oustme.oustsdk.catalogue_ui.model.CatalogueModuleUpdate;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.response.common.GameType;
import com.oustme.oustsdk.survey_module.SurveyComponentActivity;
import com.oustme.oustsdk.survey_ui.SurveyDetailActivity;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class TaskListModuleAdapter extends RecyclerView.Adapter<TaskListModuleAdapter.ViewHolder> {

    private ArrayList<CommonLandingData> taskModuleList = new ArrayList<>();
    public Context context;
    int type;
    int parentPosition;
    String dataKey;

    public void setTaskRecyclerAdapter(ArrayList<CommonLandingData> taskModuleList, Context context, int type, int parentPosition, String dateKey) {
        this.taskModuleList = taskModuleList;
        OustStaticVariableHandling.getInstance().setTaskModule(taskModuleList);
        this.context = context;
        this.type = type;
        this.parentPosition = parentPosition;
        this.dataKey = dateKey;

    }

    public ArrayList<CommonLandingData> getList() {
        return Objects.requireNonNullElseGet(taskModuleList, ArrayList::new);
    }

    @NonNull
    @Override
    public TaskListModuleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (type == GRID) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.include_modules_grid, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_module_item, parent, false);
        }

        ViewHolder viewHolder = new ViewHolder(v);
        viewHolder.setIsRecyclable(false);
        return viewHolder;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull TaskListModuleAdapter.ViewHolder holder, int position) {

        CommonLandingData task = taskModuleList.get(position);
        taskModuleList.get(position).setCommonId(position);

        if (task != null) {
            taskModuleList.get(position).setHideDate(false);
            if (task.getType() != null && !task.getType().isEmpty()) {
                String id = task.getId();
                if (task.getType().equalsIgnoreCase("Assessment")) {
                    task.setType("ASSESSMENT");
                    getData(task, holder, position);
                } else {
                    if (type == 1) {
                        holder.event_lay.setVisibility(View.VISIBLE);
                        holder.non_event_lay.setVisibility(View.GONE);
                    } else {
                        holder.event_lay.setVisibility(View.GONE);
                        holder.non_event_lay.setVisibility(View.INVISIBLE);
                    }
                    getData(task, holder, position);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if (taskModuleList == null) {
            return 0;
        }
        return taskModuleList.size();
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return taskModuleList.get(position).getCommonId();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        View tv_action_lay, info_separator;
        ImageView iv_banner, end_view;//imageView2;
        LinearLayout event_lay, non_event_lay, end_lay, root_common_lay;//action_root_lay,
        TextView tv_module_type, tv_title, tv_coin, tv_timer, tv_time, tv_time_end, tv_status;

        public ViewHolder(View itemView) {
            super(itemView);

            root_common_lay = itemView.findViewById(R.id.root_common_lay);
            tv_module_type = itemView.findViewById(R.id.tv_module_type);
            iv_banner = itemView.findViewById(R.id.iv_banner);
            tv_title = itemView.findViewById(R.id.tv_title);
            non_event_lay = itemView.findViewById(R.id.non_event_lay);
            event_lay = itemView.findViewById(R.id.event_lay);
            tv_coin = itemView.findViewById(R.id.tv_coin);
            tv_status = itemView.findViewById(R.id.status);

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

            info_separator = itemView.findViewById(R.id.info_separator);
            tv_timer = itemView.findViewById(R.id.tv_timer);
            tv_time = itemView.findViewById(R.id.tv_time);
            end_lay = itemView.findViewById(R.id.end_lay);
            end_view = itemView.findViewById(R.id.end_view);
            tv_time_end = itemView.findViewById(R.id.tv_time_end);
            tv_action_lay = itemView.findViewById(R.id.tv_action_lay);
        }
    }

    private void getData(CommonLandingData task, TaskListModuleAdapter.ViewHolder holder, int position) {
        try {
            if (task != null) {
                if (position < taskModuleList.size()) {
                    taskModuleList.set(position, task);
                }
                OustStaticVariableHandling.getInstance().getTaskModuleHashMap().put(dataKey, taskModuleList);

                String taskType = "";
                String coins = task.getTotalOc() + "";
                String imageUrl;
                if (task.isRecurring()) {
                    holder.tv_module_type.setVisibility(View.VISIBLE);
                    holder.tv_module_type.setText(context.getText(R.string.recurring_assessment));
                    holder.tv_status.setVisibility(View.GONE);
                } else {
                    holder.tv_module_type.setVisibility(View.GONE);
                    holder.tv_status.setVisibility(View.VISIBLE);
                }

                if (task.isEnrolled() && task.getEnrolledDateTime() != null && !task.getEnrolledDateTime().isEmpty()) {
                    holder.tv_status.setText(context.getResources().getString(R.string.in_progress_txt));
                    String inProgressColor = OustPreferences.get("secondaryColor");
                    if (inProgressColor != null && !inProgressColor.isEmpty()) {
                        holder.tv_status.setBackgroundColor(Color.parseColor(inProgressColor));
                    } else {
                        holder.tv_status.setBackgroundColor(context.getResources().getColor(R.color.light_orange));
                    }
                } else {
                    holder.tv_status.setText(context.getResources().getString(R.string.not_started_txt));
                    holder.tv_status.setBackgroundColor(OustSdkTools.getColorBack(R.color.grey_b));
                }

                if (task.getType() != null && !task.getType().isEmpty()) {
                    if (task.getType().equalsIgnoreCase("course")) {
                        taskType = "" + context.getResources().getString(R.string.course_text).toUpperCase();

                    } else if (task.getType().equalsIgnoreCase("Assessment")) {
                        taskType = "" + context.getResources().getString(R.string.assessment).toUpperCase();
                    } else {
                        if (task.getType().equalsIgnoreCase("CLASSROOM")) {
                            taskType = "" + context.getResources().getString(R.string.classroom_text).toUpperCase();
                        } else if (task.getType().equalsIgnoreCase("WEBINAR")) {
                            taskType = "" + context.getResources().getString(R.string.webinar_text).toUpperCase();
                        }
                        holder.tv_module_type.setVisibility(View.VISIBLE);
                        holder.tv_module_type.setText(taskType);

                        Date meetingStartDate = new Date(task.getStartTime());
                        Date meetingEndDate = new Date(task.getEndTime());

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM - hh:mm a", Locale.getDefault());
                        dateFormat.setTimeZone(TimeZone.getDefault());
                        String startDateTime = timeZoneConversation("dd MMM - hh:mm a", task.getStartTime(), task.getTimeZone());
                        String endDateTime = timeZoneConversation("dd MMM - hh:mm a", task.getEndTime(), task.getTimeZone());
                        SimpleDateFormat sameDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

                        if (!endDateTime.isEmpty()) {
                            holder.tv_time_end.setText(endDateTime);
                            holder.end_lay.setVisibility(View.VISIBLE);
                            holder.end_view.setVisibility(View.VISIBLE);
                        } else {
                            holder.end_lay.setVisibility(View.GONE);
                            holder.end_view.setVisibility(View.GONE);
                        }

                        if (sameDateFormat.format(meetingStartDate).equalsIgnoreCase(sameDateFormat.format(meetingEndDate))) {
                            startDateTime = timeZoneConversation("hh:mm a", task.getStartTime(), task.getTimeZone()) + " - " + timeZoneConversation("hh:mm a", task.getEndTime(), task.getTimeZone());
                            holder.end_lay.setVisibility(View.GONE);
                            holder.end_view.setVisibility(View.GONE);
                        }
                        holder.tv_time.setText(startDateTime);
                    }
                }

                if (task.getName() != null && !task.getName().isEmpty()) {
                    if (Build.VERSION.SDK_INT >= 24) {
                        holder.tv_title.setText(Html.fromHtml(task.getName(), Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        holder.tv_title.setText(Html.fromHtml(task.getName()));
                    }
                }

                if (task.getIcon() != null && !task.getIcon().isEmpty()) {
                    imageUrl = task.getIcon();
                    Picasso.get().load(imageUrl).error(R.drawable.assessment_thumbnail).into(holder.iv_banner);
//                    Glide.with(context).load(imageUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.iv_banner);
                } else {
                    Picasso.get().load(R.drawable.assessment_thumbnail).into(holder.iv_banner);
                }

                if (task.getTime() != 0) {
                    String totalTime = OustSdkTools.getTime(task.getTime());

                    holder.tv_timer.setVisibility(View.VISIBLE);
                    holder.tv_timer.setText(totalTime + " min");
                } else {
                    String durationText = "1 min";
                    holder.tv_timer.setVisibility(View.VISIBLE);
                    holder.tv_timer.setText(durationText);
                }

                if (!coins.equalsIgnoreCase("0")) {

                    holder.tv_coin.setVisibility(View.VISIBLE);
                    holder.info_separator.setVisibility(View.VISIBLE);
                    holder.tv_coin.setText(coins);
                } else {
                    holder.tv_coin.setVisibility(View.GONE);
                    holder.info_separator.setVisibility(View.GONE);
                }

                holder.root_common_lay.setOnClickListener(v -> {
                    ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(v, "scaleX", 1.0f, 0.94f);
                    ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(v, "scaleY", 1.0f, 0.96f);
                    scaleDownX.setDuration(150);
                    scaleDownY.setDuration(150);
                    scaleDownX.setRepeatCount(2);
                    scaleDownY.setRepeatCount(2);
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
                            launchActivity(task, holder.getAdapterPosition());
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
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void launchActivity(CommonLandingData commonLandingData, int pos) {

        OustStaticVariableHandling.getInstance().setModuleClicked(true);

        CatalogueModuleUpdate catalogueModuleUpdate = new CatalogueModuleUpdate();
        catalogueModuleUpdate.setPosition(pos);
        catalogueModuleUpdate.setParentPosition(parentPosition);
        catalogueModuleUpdate.setType(commonLandingData.getType());
        catalogueModuleUpdate.setCommonLandingData(commonLandingData);
        OustStaticVariableHandling.getInstance().setCatalogueModuleUpdate(catalogueModuleUpdate);

        if ((commonLandingData.getType() != null) && (commonLandingData.getType().contains("ASSESSMENT"))) {
            Gson gson = new Gson();
            Intent intent;
            if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI)) {
                intent = new Intent(context, AssessmentDetailScreen.class);
            } else {
                intent = new Intent(context, AssessmentPlayActivity.class);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ActiveGame activeGame = setGame(OustAppState.getInstance().getActiveUser());
            intent.putExtra("ActiveGame", gson.toJson(activeGame));
            String id = commonLandingData.getId();
            id = id.replace("ASSESSMENT", "");
            intent.putExtra("assessmentId", id);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else if ((commonLandingData.getType() != null) && (commonLandingData.getType().contains("SURVEY"))) {
            Gson gson = new Gson();
            Intent intent = new Intent(OustSdkApplication.getContext(), SurveyDetailActivity.class);
            if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_SURVEY_NEW_UI)) {
                intent = new Intent(OustSdkApplication.getContext(), SurveyComponentActivity.class);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ActiveGame activeGame = setGame(OustAppState.getInstance().getActiveUser());
            intent.putExtra("ActiveGame", gson.toJson(activeGame));
            String id = commonLandingData.getId();
            id = id.replace("ASSESSMENT", "");
            intent.putExtra("assessmentId", id);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public ActiveGame setGame(ActiveUser activeUser) {
        ActiveGame activeGame = new ActiveGame();
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
        activeGame.setGroupId("");
        activeGame.setLevel(activeUser.getLevel());
        activeGame.setLevelPercentage(activeUser.getLevelPercentage());
        activeGame.setWins(activeUser.getWins());
        activeGame.setIsLpGame(false);
        return activeGame;
    }

    public void modifyItem(final int position, final int completionPercentage) {
        taskModuleList.get(position).setCompletionPercentage(completionPercentage);
        //feedArrayList.get(position).setPlaying(feedComment);
        taskModuleList.set(position, taskModuleList.get(position));
        notifyItemChanged(position);
    }

    private String timeZoneConversation(String dateFormat, long dateInMillis, String timeZone) {

        long dataMilli = TimeZone.getTimeZone(timeZone).getRawOffset();
        long displayMilli = TimeZone.getDefault().getRawOffset();
        long diffMilli = displayMilli - dataMilli;

        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.getDefault());
        Date dataTime = new Date((dateInMillis + diffMilli));

        return formatter.format(dataTime);
    }
}
