package com.oustme.oustsdk.layoutFour.components.myTask.fragment.playList;

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
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.CplBaseActivity;
import com.oustme.oustsdk.activity.common.languageSelector.LanguageSelectionActivity;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class TaskListCplAdapter extends RecyclerView.Adapter<TaskListCplAdapter.ViewHolder> {

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
    public TaskListCplAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (type == GRID) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.include_modules_grid, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_module_item, parent, false);
        }
        TaskListCplAdapter.ViewHolder viewHolder = new TaskListCplAdapter.ViewHolder(v);
        viewHolder.setIsRecyclable(false);
        return viewHolder;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull TaskListCplAdapter.ViewHolder holder, int position) {

        CommonLandingData task = taskModuleList.get(position);
        taskModuleList.get(position).setCommonId(position);
        holder.tv_module_type.setVisibility(View.GONE);

        if (task != null) {
            taskModuleList.get(position).setHideDate(false);
            if (task.getType() != null && !task.getType().isEmpty()) {
                String id = task.getId();
                if (task.getType().equalsIgnoreCase("CPL") || task.getType().equalsIgnoreCase("MULTILINGUAL")) {
                    if (id != null) {
                        if (id.contains("CPL")) {
                            id = id.replace("CPL", "");
                        } else if (id.contains("cpl")) {
                            id = id.replace("cpl", "");
                        }
                        getData(task, holder, position, id);
                    }
                } else {
                    holder.event_lay.setVisibility(View.GONE);
                    holder.non_event_lay.setVisibility(View.GONE);
                    getData(task, holder, position, id);
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
        LinearLayout event_lay, non_event_lay, end_lay, root_common_lay, multiple_multilingual_cpl_layout;//action_root_lay,
        TextView tv_module_type, tv_title, tv_coin, tv_timer, tv_percentage, tv_status,
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
            pb_module = itemView.findViewById(R.id.pb_module);
            tv_percentage = itemView.findViewById(R.id.tv_percentage);
            multiple_multilingual_cpl_layout = itemView.findViewById(R.id.multiple_multilingual_cpl_layout);
        }
    }

    private void getData(CommonLandingData task, ViewHolder holder, int position, String id) {
        try {
            if (task != null) {
                if (position < taskModuleList.size()) {
                    taskModuleList.set(position, task);
                }
                OustStaticVariableHandling.getInstance().getTaskModuleHashMap().put(dataKey, taskModuleList);

                String imageUrl;
                holder.tv_module_type.setVisibility(View.GONE);

                if (task.isEnrolled()) {
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
                holder.tv_status.setVisibility(View.VISIBLE);

                if (task.getType() != null && !task.getType().isEmpty()) {
                    if (task.getType().equalsIgnoreCase("MULTILINGUAL")) {
                        holder.multiple_multilingual_cpl_layout.setVisibility(View.VISIBLE);
                    } else {
                        holder.multiple_multilingual_cpl_layout.setVisibility(View.GONE);
                    }
                    holder.end_lay.setVisibility(View.GONE);
                    holder.end_view.setVisibility(View.GONE);
                }
                holder.tv_module_type.setVisibility(View.GONE);

                if (task.getName() != null && !task.getName().isEmpty()) {
                    if (Build.VERSION.SDK_INT >= 24) {
                        holder.tv_title.setText(Html.fromHtml(task.getName(), Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        holder.tv_title.setText(Html.fromHtml(task.getName()));
                    }
                }

                if (task.getIcon() != null && !task.getIcon().isEmpty()) {
                    imageUrl = task.getIcon();
                    Picasso.get().load(imageUrl).error(R.drawable.cpl_thumbnail).into(holder.iv_banner);
//                    Glide.with(context).load(imageUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.iv_banner);
                } else {
                    if (task.getBanner() != null && !task.getBanner().isEmpty()) {
                        imageUrl = task.getBanner();
                        Picasso.get().load(imageUrl).error(R.drawable.cpl_thumbnail).into(holder.iv_banner);
//                        Glide.with(context).load(imageUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.iv_banner);
                    } else {
                        Picasso.get().load(R.drawable.cpl_thumbnail).into(holder.iv_banner);
                    }
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
                    holder.tv_coin.setVisibility(View.GONE);
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
                            launchActivity(task, id);
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

    private void launchActivity(CommonLandingData commonLandingData, String id) {
        try {
            OustStaticVariableHandling.getInstance().setModuleClicked(true);
            long cplId = 0;
            if (id != null && !id.isEmpty()) {
                cplId = OustSdkTools.convertToLong(id);
            }
            if (commonLandingData.getType() != null) {
                if (commonLandingData.getType().contains("CPL")) {
                    Intent intent = new Intent(context, CplBaseActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("cplId", id);
                    context.startActivity(intent);
                } else if (commonLandingData.getType().contains("MULTILINGUAL")) {
                    Intent intent = new Intent(context, LanguageSelectionActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("CPL_ID", cplId);
                    intent.putExtra("allowBackPress", true);
                    intent.putExtra("FEED", false);
                    intent.putExtra("isChildCplDistributed", false);
                    context.startActivity(intent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public void modifyItem(final int position, final int completionPercentage) {
        try {
            taskModuleList.get(position).setCompletionPercentage(completionPercentage);
            taskModuleList.set(position, taskModuleList.get(position));
            notifyItemChanged(position);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
