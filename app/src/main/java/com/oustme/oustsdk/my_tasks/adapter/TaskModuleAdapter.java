package com.oustme.oustsdk.my_tasks.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.Html;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.assessments.AssessmentPlayActivity;
import com.oustme.oustsdk.assessment_ui.assessmentDetail.AssessmentDetailScreen;
import com.oustme.oustsdk.calendar_ui.ui.EventDataDetailScreen;
import com.oustme.oustsdk.course_ui.CourseDetailScreen;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.response.common.GameType;
import com.oustme.oustsdk.survey_module.SurveyComponentActivity;
import com.oustme.oustsdk.survey_ui.SurveyDetailActivity;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.CommonTools;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.appconstants.AppConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static com.oustme.oustsdk.utils.LayoutType.GRID;

public class TaskModuleAdapter extends RecyclerView.Adapter<TaskModuleAdapter.ViewHolder> {

    private ArrayList<CommonLandingData> taskModuleList = new ArrayList<>();
    public Context context;
    int type;
    int screenWidth;


    public void setTaskRecyclerAdapter(ArrayList<CommonLandingData> taskModuleList, Context context, int type, int screenWidth) {
        this.taskModuleList = taskModuleList;
        OustStaticVariableHandling.getInstance().setTaskModule(taskModuleList);
        this.context = context;
        this.type = type;
        this.screenWidth = screenWidth;

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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v;
        if (type == GRID) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_modules_grid, parent, false);
            if (screenWidth > 0) {
                ViewGroup.LayoutParams params = v.getLayoutParams();
                params.width = (int) (screenWidth * 0.50);
                v.setLayoutParams(params);
            }
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_adapter, parent, false);
        }


        ViewHolder viewHolder = new ViewHolder(v);
        viewHolder.setIsRecyclable(false);
        return viewHolder;


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CommonLandingData task = taskModuleList.get(position);
        taskModuleList.get(position).setCommonId(position);
        String toolbarColorCode = OustPreferences.get("toolbarColorCode");


        if (toolbarColorCode == null || toolbarColorCode.isEmpty()) {

            toolbarColorCode = "#01b5a2";
        }

        Drawable btn_image_drawable = holder.imageView2.getDrawable();
        holder.imageView2.setImageDrawable(OustSdkTools.drawableColor(btn_image_drawable));

        holder.tv_action.setTextColor(Color.parseColor(toolbarColorCode));

        //action_root_lay not found
        //GradientDrawable borderBackground = (GradientDrawable) context.getResources().getDrawable(R.drawable.bg_round_8);
        //borderBackground.setStroke(3, Color.parseColor(toolbarColorCode));
        //holder.action_root_lay.setBackground(borderBackground);

        // Drawable action_btn_bg = OustSdkTools.drawableColor(context.getResources().getDrawable(R.drawable.bg_round_8));
        //  holder.imageView2.setImageDrawable(OustSdkTools.drawableColor(context.getResources().getDrawable(R.drawable.ic_resume)));

        GradientDrawable borderBackground = (GradientDrawable) context.getResources().getDrawable(R.drawable.bg_round_8);
        borderBackground.setStroke(3, Color.parseColor(toolbarColorCode));
        holder.action_root_lay.setBackground(borderBackground);
        if (task != null) {

            String previousDate = "";
            String date = "";
            if (taskModuleList.get(position).getAddedOn() != null && !taskModuleList.get(position).getAddedOn().isEmpty()) {
                try {
                    long addedOn = Long.parseLong(taskModuleList.get(position).getAddedOn());
                    if (addedOn != 0) {
                        date = new SimpleDateFormat("dd", Locale.getDefault()).format(new Date(addedOn));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }

            } else {
                date = new SimpleDateFormat("dd", Locale.getDefault()).format(new Date());
            }

            if (taskModuleList.get(position - 1).getAddedOn() != null && !taskModuleList.get(position - 1).getAddedOn().isEmpty()) {
                try {
                    long addedOn = Long.parseLong(taskModuleList.get(position - 1).getAddedOn());
                    if (addedOn != 0) {
                        previousDate = new SimpleDateFormat("dd", Locale.getDefault()).format(new Date(addedOn));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }

            } else {
                previousDate = new SimpleDateFormat("dd", Locale.getDefault()).format(new Date());
            }
            if (date.equals(previousDate)) {
                holder.tv_date.setVisibility(View.INVISIBLE);
                if (type == 2) {

                    if (!task.isHideDate()) {
                        holder.tv_date.setVisibility(View.INVISIBLE);

                    }else{
                        holder.tv_date.setVisibility(View.VISIBLE);
                    }
                }


            } else {
                holder.tv_date.setVisibility(View.VISIBLE);
                if (position == (taskModuleList.size() - 1)) {
                    holder.view4.setVisibility(View.GONE);
                } else {
                    holder.view4.setVisibility(View.VISIBLE);
                }
            }
            if (type == 2) {

                if (task.isHideDate()) {
                    holder.include_date.setVisibility(View.GONE);

                } else {
                    holder.include_date.setVisibility(View.VISIBLE);
                    if (position == (taskModuleList.size() - 1)) {
                        holder.view4.setVisibility(View.GONE);
                    } else {
                        holder.view4.setVisibility(View.VISIBLE);
                    }
                }


            }




            taskModuleList.get(position).setHideDate(false);
            if (task.getType() != null && !task.getType().isEmpty()) {
                String id = task.getId();
                if (task.getType().equalsIgnoreCase("course")) {

                    if (id != null) {

                        if (id.contains("COURSE")) {
                            id = id.replace("COURSE", "");
                        } else if (id.contains("course")) {
                            id = id.replace("course", "");
                        }

                        readDataFromFirebaseForCourse(id, holder, task);
                    }

                } else if (task.getType().equalsIgnoreCase("Assessment")) {
                    if (id != null) {

                        if (id.contains("ASSESSMENT")) {
                            id = id.replace("ASSESSMENT", "");
                        } else if (id.contains("assessment")) {
                            id = id.replace("assessment", "");
                        }
                        holder.event_lay.setVisibility(View.GONE);
                        holder.non_event_lay.setVisibility(View.INVISIBLE);
                        readDataFromFirebaseForAssessment(id, holder, task);
                    }
                } else {


                    if (type == 1) {
                        holder.event_lay.setVisibility(View.VISIBLE);
                        holder.non_event_lay.setVisibility(View.GONE);
                    } else {
                        holder.event_lay.setVisibility(View.GONE);
                        holder.non_event_lay.setVisibility(View.INVISIBLE);
                    }
                    getData(task, holder);
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

        View include_date, include_module, tv_action_lay;
        ImageView  view4, iv_banner, imageView2;//view3,
        LinearLayout action_root_lay, event_lay, end_lay, root_common_lay;
        LinearLayout non_event_lay;
        ConstraintLayout task_root_layout;
        //CardView card_date;
        TextView tv_date, tv_module_type, tv_title, tv_coin, tv_timer, tv_percentage, tv_action, startlabel_text,
                tv_time, tv_time_end;
        ProgressBar pb_module;

        public ViewHolder(View itemView) {
            super(itemView);

            include_date = itemView.findViewById(R.id.include_date);
            include_module = itemView.findViewById(R.id.include_module);
           // view3 = include_date.findViewById(R.id.view3);
            view4 = include_date.findViewById(R.id.view4);
            //card_date = include_date.findViewById(R.id.card_date);
            tv_date = include_date.findViewById(R.id.tv_date);
            root_common_lay = include_module.findViewById(R.id.root_common_lay);
            tv_module_type = include_module.findViewById(R.id.tv_module_type);
            iv_banner = include_module.findViewById(R.id.iv_banner);
            tv_title = include_module.findViewById(R.id.tv_title);
            non_event_lay = include_module.findViewById(R.id.non_event_lay);
            event_lay = include_module.findViewById(R.id.event_lay);
            tv_coin = include_module.findViewById(R.id.tv_coin);
            try {
                if (OustPreferences.getAppInstallVariable("showCorn")) {
                    tv_coin.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_coins_corn,0,0,0);
                }else{
                    tv_coin.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_coins_golden,0,0,0);
                }
            }catch (Exception e){
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

            tv_timer = include_module.findViewById(R.id.tv_timer);
            startlabel_text = include_module.findViewById(R.id.startlabel_text);
            tv_time = include_module.findViewById(R.id.tv_time);
            end_lay = include_module.findViewById(R.id.end_lay);
            tv_time_end = include_module.findViewById(R.id.tv_time_end);
            tv_action_lay = include_module.findViewById(R.id.tv_action_lay);
            pb_module = include_module.findViewById(R.id.pb_module);
            tv_percentage = include_module.findViewById(R.id.tv_percentage);
            tv_action = tv_action_lay.findViewById(R.id.tv_action);
            imageView2 = tv_action_lay.findViewById(R.id.imageView2);
            //action_root_lay = tv_action_lay.findViewById(R.id.action_root_lay);
            task_root_layout = itemView.findViewById(R.id.task_root_layout);

        }
    }


    private void readDataFromFirebaseForCourse(final String courseID, ViewHolder holder, CommonLandingData task) {
        try {
            String message = ("course/course" + courseID);
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if (null != dataSnapshot.getValue()) {
                            CommonTools commonTools = new CommonTools();
                            final Map<String, Object> lpMap = (Map<String, Object>) dataSnapshot.getValue();
                            if ((lpMap != null) && (lpMap.get("courseId") != null)) {
                                CommonLandingData commonLandingData = new CommonLandingData();
                                commonLandingData.setId("COURSE" + courseID);
                                commonLandingData = commonTools.getCourseCommonData(lpMap, commonLandingData);
                                commonLandingData.setType("COURSE");
                                commonLandingData.setAddedOn(task.getAddedOn());
                                commonLandingData.setCompletionPercentage(task.getCompletionPercentage());
                                getData(commonLandingData, holder);
                            }
                        }

                    } catch (Exception e) {
                        getData(null, holder);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    getData(null, holder);
                }
            };
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(eventListener);
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        } catch (Exception e) {
            getData(null, holder);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void readDataFromFirebaseForAssessment(final String assessmentId, ViewHolder holder, CommonLandingData task) {
        try {
            String message = ("assessment/assessment" + assessmentId);
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if (null != dataSnapshot.getValue()) {
                            CommonTools commonTools = new CommonTools();
                            final Map<String, Object> lpMap = (Map<String, Object>) dataSnapshot.getValue();
                            if ((lpMap != null)) {
                                CommonLandingData commonLandingData = new CommonLandingData();
                                commonLandingData.setId("ASSESSMENT" + assessmentId);
                                commonLandingData = commonTools.getAssessmentCommonData(lpMap, commonLandingData);
                                commonLandingData.setType("ASSESSMENT");
                                commonLandingData.setAddedOn(task.getAddedOn());
                                commonLandingData.setCompletionPercentage(task.getCompletionPercentage());
                                getData(commonLandingData, holder);
                            }
                        }

                    } catch (Exception e) {
                        getData(null, holder);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    getData(null, holder);
                }
            };
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(eventListener);
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        } catch (Exception e) {
            getData(null, holder);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getData(CommonLandingData task, ViewHolder holder) {
        try {
            if (task != null) {

                String taskType = "";
                String coins = task.getTotalOc() + "";
                String imageUrl;
                holder.tv_module_type.setVisibility(View.INVISIBLE);
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
                        //holder.non_event_lay.setVisibility(View.GONE);

                        Date meetingStartDate = new Date(task.getStartTime());
                        Date meetingEndDate = new Date(task.getEndTime());


                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM - hh:mm a", Locale.getDefault());
                        dateFormat.setTimeZone(TimeZone.getDefault());
                        String startDateTime = timeZoneConversation("dd MMM - hh:mm a", task.getStartTime(), task.getTimeZone());
                        String endDateTime = timeZoneConversation("dd MMM - hh:mm a", task.getEndTime(), task.getTimeZone());
                        SimpleDateFormat sameDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

                        if (!endDateTime.isEmpty()) {
                            holder.startlabel_text.setText(context.getResources().getString(R.string.start));
                            holder.tv_time_end.setText(endDateTime);
                            holder.end_lay.setVisibility(View.VISIBLE);
                        } else {
                            holder.end_lay.setVisibility(View.GONE);
                        }

                        if (sameDateFormat.format(meetingStartDate).equalsIgnoreCase(sameDateFormat.format(meetingEndDate))) {
                            holder.startlabel_text.setText(context.getResources().getString(R.string.time_text));
                            startDateTime = timeZoneConversation("hh:mm a", task.getStartTime(), task.getTimeZone()) + " - " + timeZoneConversation("hh:mm a", task.getStartTime(), task.getTimeZone());
                            holder.end_lay.setVisibility(View.GONE);
                        }
                        holder.tv_time.setText(startDateTime);

                    }

                }

                holder.tv_module_type.setText(taskType);


                if (task.getName() != null && !task.getName().isEmpty()) {
                    if (Build.VERSION.SDK_INT >= 24) {
                        holder.tv_title.setText(Html.fromHtml(task.getName(), Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        holder.tv_title.setText(Html.fromHtml(task.getName()));
                    }
                }

                if (task.getAddedOn() != null && !task.getAddedOn().isEmpty()) {
                    try {
                        long addedOn = Long.parseLong(task.getAddedOn());
                        if (addedOn != 0) {
                            Date date = new Date(addedOn);
                            holder.tv_date.setText(new SimpleDateFormat("dd\nMMM", Locale.getDefault()).format(date));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }

                } else {
                    holder.include_date.setVisibility(View.GONE);
                }


                if (task.getIcon() != null && !task.getIcon().isEmpty()) {
                    imageUrl = task.getIcon();
                    Glide.with(context).load(imageUrl).into(holder.iv_banner);


                } else {
                    holder.iv_banner.setVisibility(View.GONE);
                }


                if (task.getCompletionPercentage() != 0) {

                    holder.pb_module.setVisibility(View.VISIBLE);
                    holder.tv_percentage.setVisibility(View.VISIBLE);
                    String completionPercentageText = task.getCompletionPercentage() + "%";
                    holder.tv_percentage.setText(completionPercentageText);
                    holder.pb_module.setProgress((int) task.getCompletionPercentage());
                    holder.tv_action.setText(context.getResources().getString(R.string.resume));


                } else {

                    holder.pb_module.setVisibility(View.GONE);
                    holder.tv_percentage.setVisibility(View.GONE);
                    holder.tv_action.setText(context.getResources().getString(R.string.start));

                }

                holder.task_root_layout.setOnClickListener(v -> launchActivity(task));


               /* if(task.getCourseDeadline()!=null&&!task.getCourseDeadline().isEmpty()){
                    Drawable drawable = context.getResources().getDrawable(R.drawable.ic_task_red);
                    holder.date_lay.setBackground(drawable);

                    Date date = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault()).parse(task.getCourseDeadline());
                    taskType = taskType+"/"+new SimpleDateFormat("dd MMM yyyy",Locale.getDefault()).format(date);
                    holder.task_module_type.setText(taskType);
                }*/

                if (task.getTime() != 0) {
                    double duration = (task.getTime() * 1.0) / 60;
                    String durationText = (int) duration + " min";
                    if (task.getTime() < 60) {
                        durationText = "1 min";

                    }
                    holder.tv_timer.setVisibility(View.VISIBLE);
                    holder.tv_timer.setText(durationText);
                } else {
                    String durationText = "1 min";
                    holder.tv_timer.setVisibility(View.VISIBLE);
                    holder.tv_timer.setText(durationText);
                }

                if (!coins.equalsIgnoreCase("0")) {

                    holder.tv_coin.setVisibility(View.VISIBLE);
                    holder.tv_coin.setText(coins);
                } else {
                    holder.tv_coin.setVisibility(View.GONE);
                }


            }
        } catch (Exception e) {

            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void launchActivity(CommonLandingData commonLandingData) {

        OustStaticVariableHandling.getInstance().setModuleClicked(true);

        if ((commonLandingData.getType() != null) && (commonLandingData.getType().contains("ASSESSMENT"))) {
            Gson gson = new Gson();
            Intent intent;
            if(OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI)){
                intent = new Intent(context, AssessmentDetailScreen.class);
            }else{
                intent = new Intent(context, AssessmentPlayActivity.class);
            }
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
            ActiveGame activeGame = setGame(OustAppState.getInstance().getActiveUser());
            intent.putExtra("ActiveGame", gson.toJson(activeGame));
            String id = commonLandingData.getId();
            id = id.replace("ASSESSMENT", "");
            intent.putExtra("assessmentId", id);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }  else if ((commonLandingData.getType() != null) && (commonLandingData.getType().contains("WEBINAR")||commonLandingData.getType().contains("CLASSROOM"))) {
            Intent eventDetail = new Intent(context, EventDataDetailScreen.class);
            long meetingId = Long.parseLong(commonLandingData.getId().replace("MEETINGCALENDAR",""));
            eventDetail.putExtra("meetingId", meetingId);
            context.startActivity(eventDetail);
        } else {
            String courseID = "" + commonLandingData.getId();
            if (courseID.contains("COURSE")) {
                courseID = courseID.replace("COURSE", "");
            } else if (courseID.contains("course")) {
                courseID = courseID.replace("course", "");
            }

            Intent taskCourseIntent = new Intent(context, CourseDetailScreen.class);
            taskCourseIntent.putExtra("learningId", courseID);
            taskCourseIntent.putExtra("catalog_id", "" + courseID);
            taskCourseIntent.putExtra("catalog_type", "COURSE");
            taskCourseIntent.putExtra("taskPosition", commonLandingData.getCommonId());
            taskCourseIntent.putExtra("taskCompletion", 0);
            context.startActivity(taskCourseIntent);
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
