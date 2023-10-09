package com.oustme.oustsdk.layoutFour.components.myTask.fragment.contest;

import static com.oustme.oustsdk.utils.LayoutType.GRID;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;


import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.FFContest.FFcontestStartActivity;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TaskListContestAdapter extends RecyclerView.Adapter<TaskListContestAdapter.ViewHolder> {

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
        if (taskModuleList != null) {
            return taskModuleList;
        } else {
            return new ArrayList<>();
        }
    }

    @NonNull
    @Override
    public TaskListContestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v;
        if (type == GRID) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.include_modules_grid, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_module_item, parent, false);
        }


        TaskListContestAdapter.ViewHolder viewHolder = new TaskListContestAdapter.ViewHolder(v);
        viewHolder.setIsRecyclable(false);
        return viewHolder;


    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull TaskListContestAdapter.ViewHolder holder, int position) {

        CommonLandingData task = taskModuleList.get(position);
        taskModuleList.get(position).setCommonId(position);
        String toolbarColorCode = OustPreferences.get("toolbarColorCode");


        if (toolbarColorCode == null || toolbarColorCode.isEmpty()) {

            toolbarColorCode = "#01b5a2";
        }
        holder.tv_module_type.setVisibility(View.GONE);

        if (task != null) {
            taskModuleList.get(position).setHideDate(false);
            if (task.getType() != null && !task.getType().isEmpty()) {
                String id = task.getId();
                if (task.getType().equalsIgnoreCase("FFF_CONTEXT")) {
                    if (id != null) {
                        if (id.contains("f3c")) {
                            id = id.replace("f3c", "");
                        } else if (id.contains("F3C")) {
                            id = id.replace("f3c", "");
                        }
                        getData(task, holder, position);
//                        readDataFromFirebaseForCourse(id, holder, task, position, dataKey);
                    }
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
        TextView tv_module_type, tv_title, tv_coin, tv_timer, tv_percentage,
                tv_time, tv_time_end;//tv_action,
        ProgressBar pb_module;

        public ViewHolder(View itemView) {
            super(itemView);

            root_common_lay = itemView.findViewById(R.id.root_common_lay);
            tv_module_type = itemView.findViewById(R.id.tv_module_type);
            iv_banner = itemView.findViewById(R.id.iv_banner);
            tv_title = itemView.findViewById(R.id.tv_title);
            non_event_lay = itemView.findViewById(R.id.non_event_lay);
            event_lay = itemView.findViewById(R.id.event_lay);
            tv_coin = itemView.findViewById(R.id.tv_coin);

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
            pb_module = itemView.findViewById(R.id.pb_module);
            tv_percentage = itemView.findViewById(R.id.tv_percentage);
        }
    }

    private void getData(CommonLandingData task, TaskListContestAdapter.ViewHolder holder, int position) {
        try {
            if (task != null) {
                if (position < taskModuleList.size()) {
                    taskModuleList.set(position, task);
                }
                OustStaticVariableHandling.getInstance().getTaskModuleHashMap().put(dataKey, taskModuleList);

                String taskType = "";
                String coins = task.getTotalOc() + "";
                String imageUrl;
                holder.tv_module_type.setVisibility(View.GONE);

                if (task.getCompletionPercentage() != 0) {

                    holder.pb_module.setVisibility(View.VISIBLE);
                    holder.tv_percentage.setVisibility(View.VISIBLE);
                    String completionPercentageText = task.getCompletionPercentage() + "%";
                    holder.tv_percentage.setText(completionPercentageText);
                    holder.pb_module.setProgress((int) task.getCompletionPercentage());
                    //action_name = context.getResources().getString(R.string.resume);

                } else {

                    if (type == 1) {
                        holder.pb_module.setVisibility(View.GONE);
                        holder.tv_percentage.setVisibility(View.GONE);
                    } else {
                        holder.pb_module.setVisibility(View.INVISIBLE);
                        holder.tv_percentage.setVisibility(View.INVISIBLE);
                    }

                }

                if (task.getType() != null && !task.getType().isEmpty()) {

                    if (task.getType().equalsIgnoreCase("FFF_CONTEXT")) {
                        taskType = "" + context.getResources().getString(R.string.new_play_list).toUpperCase();
                    }

                    Date meetingStartDate = new Date(task.getStartTime());
                    Date meetingEndDate = new Date(task.getEndTime());


                    if (task.getStartTime() != 0 && task.getEndTime() != 0 && task.getTimeZone() != null) {
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

                holder.tv_module_type.setText(taskType);
                holder.tv_module_type.setVisibility(View.GONE);

                if (task.getName() != null && !task.getName().isEmpty()) {
                    if (Build.VERSION.SDK_INT >= 24) {
                        holder.tv_title.setText(Html.fromHtml(task.getName(), Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        holder.tv_title.setText(Html.fromHtml(task.getName()));
                    }
                }

                if (task.getBanner() != null && !task.getBanner().isEmpty()) {
                    imageUrl = task.getBanner();
                    Picasso.get().load(imageUrl).into(holder.iv_banner);
//                    Glide.with(context).load(imageUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.iv_banner);
                } else {
                    holder.iv_banner.setVisibility(View.GONE);
                }

                if (task.getTime() != 0) {
                    String totalTime = OustSdkTools.getTime(task.getTime());
                    holder.tv_timer.setVisibility(View.VISIBLE);
                    holder.tv_timer.setText(totalTime + " min");
                } else {
                    String durationText = "1 min";
                    holder.tv_timer.setVisibility(View.GONE);
                    holder.tv_timer.setText(durationText);
                }

                if (task.getUserOc() != 0) {
                    holder.tv_coin.setVisibility(View.VISIBLE);
                    holder.info_separator.setVisibility(View.GONE);
                    holder.tv_coin.setText(String.valueOf(task.getUserOc()));
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
        try {
            OustStaticVariableHandling.getInstance().setModuleClicked(true);

            if ((commonLandingData.getType() != null) && (commonLandingData.getType().contains("FFF_CONTEXT"))) {
                Intent intent = new Intent(context, FFcontestStartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Gson gson = new Gson();
                intent.putExtra("fastestFingerContestData", gson.toJson(commonLandingData.getFastestFingerContestData()));
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public void modifyItem(final int position, final int completionPercentage) {
        try {
            taskModuleList.get(position).setCompletionPercentage(completionPercentage);
            //feedArrayList.get(position).setPlaying(feedComment);
            taskModuleList.set(position, taskModuleList.get(position));
            notifyItemChanged(position);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
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