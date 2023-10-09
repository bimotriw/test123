package com.oustme.oustsdk.calendar_ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.assessments.AssessmentPlayActivity;
import com.oustme.oustsdk.assessment_ui.assessmentDetail.AssessmentDetailScreen;
import com.oustme.oustsdk.calendar_ui.model.CalendarBaseData;
import com.oustme.oustsdk.calendar_ui.model.CalendarCommonData;
import com.oustme.oustsdk.calendar_ui.ui.EventDataDetailScreen;
import com.oustme.oustsdk.course_ui.CourseDetailScreen;
import com.oustme.oustsdk.response.common.GameType;
import com.oustme.oustsdk.survey_module.SurveyComponentActivity;
import com.oustme.oustsdk.survey_ui.SurveyDetailActivity;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.BranchTools;
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
import java.util.TimeZone;

public class CalendarEventDataAdapter extends RecyclerView.Adapter<CalendarEventDataAdapter.ViewHolder> {

    private final ArrayList<CalendarBaseData> calendarBaseDataArrayList;
    private Context mContext;

    public CalendarEventDataAdapter(Context context, ArrayList<CalendarBaseData> calendarBaseDataArrayList) {
        this.mContext = context;
        this.calendarBaseDataArrayList = calendarBaseDataArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_adapter, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        viewHolder.setIsRecyclable(false);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CalendarBaseData calendarBaseData = calendarBaseDataArrayList.get(position);
        if (calendarBaseData != null) {
            String type = "";
            String eventType = "";
            String name = "";
            String coins = "";
            String durationText = "1 min";
            String date;
            String dateTime = "";
            String score;
            String action_name;
            String image = "";
            int progress;
            holder.pb_module.setVisibility(View.GONE);
            holder.tv_percentage.setVisibility(View.GONE);

            String toolbarColorCode;


            //Drawable action_drawable = mContext.getResources().getDrawable(R.drawable.ic_play_button);
            if (calendarBaseData.getTime() != 0) {
                date = new SimpleDateFormat("dd\nMMM", Locale.getDefault()).format(new Date(calendarBaseData.getTime()));
            } else {
                date = new SimpleDateFormat("dd\nMMM", Locale.getDefault()).format(new Date());
            }

            String previousDate;
            if (position > 0) {
                if (calendarBaseDataArrayList.get(position - 1).getTime() != 0) {
                    previousDate = new SimpleDateFormat("dd\nMMM", Locale.getDefault()).format(new Date(calendarBaseDataArrayList.get(position - 1).getTime()));
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


            if (calendarBaseData.isEventData()) {

                holder.event_lay.setVisibility(View.VISIBLE);
                holder.non_event_lay.setVisibility(View.GONE);
                holder.pb_module.setVisibility(View.GONE);
                holder.tv_percentage.setVisibility(View.GONE);
                if (calendarBaseData.getMeetingCalendar() != null) {
                    if (calendarBaseData.getMeetingCalendar().getEventType() != null) {
                        type = calendarBaseData.getMeetingCalendar().getEventType();
                    }
                    name = calendarBaseData.getMeetingCalendar().getClassTitle();
                    if (calendarBaseData.getMeetingCalendar().getThumbnailImage() != null && !calendarBaseData.getMeetingCalendar().getThumbnailImage().isEmpty()) {
                        image = calendarBaseData.getMeetingCalendar().getThumbnailImage();
                    } else {
                        image = calendarBaseData.getMeetingCalendar().getBannerImg();
                    }

                    Date meetingStartDate = new Date(calendarBaseData.getMeetingCalendar().getMeetingStartTime());
                    Date meetingEndDate = new Date(calendarBaseData.getMeetingCalendar().getMeetingEndTime());


                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM - hh:mm a", Locale.getDefault());
                    dateFormat.setTimeZone(TimeZone.getDefault());
                    String startDateTime = timeZoneConversation("dd MMM - hh:mm a", calendarBaseData.getMeetingCalendar().getMeetingStartTime(), calendarBaseData.getMeetingCalendar().getTimeZone());
                    String endDateTime = timeZoneConversation("dd MMM - hh:mm a", calendarBaseData.getMeetingCalendar().getMeetingEndTime(), calendarBaseData.getMeetingCalendar().getTimeZone());

                    SimpleDateFormat sameDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

                    if (type.equalsIgnoreCase("CLASSROOM")) {
                        eventType = mContext.getResources().getString(R.string.classroom_text);
                    } else {
                        eventType = mContext.getResources().getString(R.string.webinar_text);
                    }

                    if (!endDateTime.isEmpty()) {
                        holder.tv_time_end.setText(endDateTime);
                        holder.end_lay.setVisibility(View.VISIBLE);
                        holder.end_view.setVisibility(View.VISIBLE);
                    } else {
                        holder.end_lay.setVisibility(View.GONE);
                        holder.end_view.setVisibility(View.GONE);
                    }

                    if (sameDateFormat.format(meetingStartDate).equalsIgnoreCase(sameDateFormat.format(meetingEndDate))) {
                        startDateTime = timeZoneConversation("hh:mm a", calendarBaseData.getMeetingCalendar().getMeetingStartTime(), calendarBaseData.getMeetingCalendar().getTimeZone()) + " - " + timeZoneConversation("hh:mm a", calendarBaseData.getMeetingCalendar().getMeetingEndTime(), calendarBaseData.getMeetingCalendar().getTimeZone());
                        holder.end_lay.setVisibility(View.GONE);
                        holder.end_view.setVisibility(View.GONE);
                    }
                    holder.tv_time.setText(startDateTime);
                }


                holder.task_root_layout.setOnClickListener(v -> {
                    BranchTools.gotoCalendarPage(calendarBaseData.getMeetingCalendar().getMeetingId());
                });
            } else {
                holder.event_lay.setVisibility(View.GONE);
                holder.non_event_lay.setVisibility(View.VISIBLE);
                holder.info_separator.setVisibility(View.GONE);
                if (calendarBaseData.getCalendarCommonData() != null) {
                    if (calendarBaseData.getCalendarCommonData().getType() != null) {
                        name = calendarBaseData.getCalendarCommonData().getName();
                        image = calendarBaseData.getCalendarCommonData().getIcon();
                        if (calendarBaseData.getCalendarCommonData().getContentDuration() != 0) {
                            double duration = (calendarBaseData.getCalendarCommonData().getContentDuration() * 1.0) / 60;
                            durationText = (int) duration + " min";
                            if (calendarBaseData.getCalendarCommonData().getContentDuration() < 60) {
                                durationText = "1 min";
                            }
                        }

                        if (calendarBaseData.getCalendarCommonData().getType().equalsIgnoreCase("course")) {
                            eventType = mContext.getResources().getString(R.string.course_text);
                        } else if (calendarBaseData.getCalendarCommonData().getType().equalsIgnoreCase("assessment")) {
                            eventType = mContext.getResources().getString(R.string.assessment);
                        } else if (calendarBaseData.getCalendarCommonData().getType().equalsIgnoreCase("survey")) {
                            eventType = mContext.getResources().getString(R.string.survey_text);
                        }

                        if (calendarBaseData.getCalendarCommonData().getType().equalsIgnoreCase("course")) {
                            coins = calendarBaseData.getCalendarCommonData().getCoins() + "";
                            holder.tv_score.setVisibility(View.GONE);

                            if (calendarBaseData.getCalendarCommonData().getUserCompletionPercentage() != 0) {
                                if (calendarBaseData.getCalendarCommonData().getUserCompletionPercentage() < 100) {
                                    // action_name = ""+mContext.getResources().getString(R.string.resume);
                                    progress = calendarBaseData.getCalendarCommonData().getUserCompletionPercentage();
                                    holder.pb_module.setVisibility(View.VISIBLE);
                                    holder.tv_percentage.setVisibility(View.VISIBLE);

                                    holder.pb_module.setProgress(progress);
                                    String percentage = progress + " %";
                                    holder.tv_percentage.setText(percentage);
                                } else {
                                    action_name = "" + mContext.getResources().getString(R.string.completed);
                                    //action_drawable = mContext.getResources().getDrawable(R.drawable.ic_tick_done);
                                    toolbarColorCode = "#0E9430";
                                    holder.status.setVisibility(View.VISIBLE);
                                    holder.status.setText(action_name);
                                    holder.status.setBackgroundColor(Color.parseColor(toolbarColorCode));
                                }
                            }
                        } else {

                            //score = "Score: " + calendarBaseData.getCalendarCommonData().getAssessmentScore();
                            score = mContext.getResources().getString(R.string.score_text) + ": " + calendarBaseData.getCalendarCommonData().getAssessmentScore();

                            Spannable spanText = new SpannableString(score);
                            if (score.contains(" ")) {
                                String[] scoreCount = score.split(" ");
                                spanText.setSpan(new ForegroundColorSpan(Color.parseColor("#F87800")), 0, scoreCount[0].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                //spanText.setSpan(new RelativeSizeSpan(1.75f), 0, scoreCount[0].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }

                            if (calendarBaseData.getCalendarCommonData().getAssessmentScore() != 0 &&
                                    calendarBaseData.getCalendarCommonData().isShowAssessmentResultScore()) {
                                holder.tv_score.setVisibility(View.VISIBLE);
                                holder.info_separator.setVisibility(View.VISIBLE);
                                holder.tv_score.setText(spanText);

                            } else {
                                holder.info_separator.setVisibility(View.GONE);
                            }

                            if (calendarBaseData.getCalendarCommonData().getUserCompletionTime() != null &&
                                    !calendarBaseData.getCalendarCommonData().getUserCompletionTime().isEmpty() && !calendarBaseData.getCalendarCommonData().getUserCompletionTime().equals("null")) {
                                if (calendarBaseData.getCalendarCommonData().getType().equalsIgnoreCase("assessment")) {
                                    if (calendarBaseData.getCalendarCommonData().getAssessmentState() != null &&
                                            calendarBaseData.getCalendarCommonData().getAssessmentState().equalsIgnoreCase("COMPLETED")) {

                                        if (calendarBaseData.getCalendarCommonData().isShowAssessmentResultScore()) {
                                            if (calendarBaseData.getCalendarCommonData().isPassed()) {
                                                action_name = "" + mContext.getResources().getString(R.string.passed);
                                                toolbarColorCode = "#0E9430";
                                            } else {
                                                action_name = "" + mContext.getResources().getString(R.string.failed_text);
                                                toolbarColorCode = "#D93838";
                                            }
                                        } else {
                                            action_name = "" + mContext.getResources().getString(R.string.completed);
                                            toolbarColorCode = "#0E9430";
                                        }
                                        holder.status.setVisibility(View.VISIBLE);
                                        holder.status.setText(action_name);
                                        holder.status.setBackgroundColor(Color.parseColor(toolbarColorCode));
                                    }
                                }
                            } else {
                                if (calendarBaseData.getCalendarCommonData().getUserCompletionPercentage() != 0) {
                                    if (calendarBaseData.getCalendarCommonData().getUserCompletionPercentage() < 100) {
                                        progress = calendarBaseData.getCalendarCommonData().getUserCompletionPercentage();
                                        holder.pb_module.setVisibility(View.VISIBLE);
                                        holder.tv_percentage.setVisibility(View.VISIBLE);

                                        holder.pb_module.setProgress(progress);
                                        String percentage = progress + " %";
                                        holder.tv_percentage.setText(percentage);
                                    }
                                }
                            }
                        }

                        holder.task_root_layout.setOnClickListener(v -> launchActivity(calendarBaseData.getCalendarCommonData()));
                    }
                }

                holder.tv_timer.setText(dateTime);
            }


            holder.tv_module_type.setText(eventType);
            holder.tv_module_type.setVisibility(View.VISIBLE);
            holder.tv_date.setText(date);
            holder.tv_timer.setText(durationText);

            if (name.contains("<br/>")) {
                name = name.replaceAll("<br/>", "");
            }

            if (name.contains("<br>")) {
                name = name.replaceAll("<br>", "");
            }
            if (Build.VERSION.SDK_INT >= 24) {
                holder.tv_title.setText(Html.fromHtml(name, Html.FROM_HTML_MODE_COMPACT));
            } else {
                holder.tv_title.setText(Html.fromHtml(name));
            }


            if (coins.equalsIgnoreCase("0") || coins.isEmpty()) {
                holder.tv_coin.setVisibility(View.GONE);
            } else {
                holder.tv_coin.setVisibility(View.VISIBLE);
                holder.info_separator.setVisibility(View.VISIBLE);
                holder.tv_coin.setText(coins);
            }

            if (image != null && !image.isEmpty()) {
                Glide.with(mContext).load(image).into(holder.iv_banner);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (calendarBaseDataArrayList == null) {
            return 0;
        }
        return calendarBaseDataArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View include_date, include_module;
        ImageView iv_banner, view_dot;
        TextView status;
        LinearLayout end_lay;
        ImageView end_view;
        LinearLayout task_root_layout;
        View info_separator;
        TextView tv_score;
        LinearLayout event_lay;
        LinearLayout non_event_lay;
        TextView tv_date, tv_module_type, tv_title, tv_coin, tv_timer, tv_time,
                tv_time_end, tv_percentage;
        ProgressBar pb_module;

        public ViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();

            include_date = itemView.findViewById(R.id.include_date);
            include_module = itemView.findViewById(R.id.include_module);
            tv_date = include_date.findViewById(R.id.tv_date);
            view_dot = include_date.findViewById(R.id.view_dot);
            tv_module_type = include_module.findViewById(R.id.tv_module_type);
            iv_banner = include_module.findViewById(R.id.iv_banner);
            status = include_module.findViewById(R.id.status);
            tv_title = include_module.findViewById(R.id.tv_title);
            event_lay = include_module.findViewById(R.id.event_lay);
            non_event_lay = include_module.findViewById(R.id.non_event_lay);
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
            end_view = include_module.findViewById(R.id.end_view);
            tv_time_end = include_module.findViewById(R.id.tv_time_end);
            pb_module = include_module.findViewById(R.id.pb_module);
            tv_percentage = include_module.findViewById(R.id.tv_percentage);
            task_root_layout = itemView.findViewById(R.id.task_root_layout);
        }
    }

    private void launchActivity(CalendarCommonData commonLandingData) {

        OustStaticVariableHandling.getInstance().setModuleClicked(true);

        if ((commonLandingData.getType() != null) && ((commonLandingData.getType().toUpperCase().contains("ASSESSMENT")))) {
            Gson gson = new Gson();
            Intent assessmentIntent;
            if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI)) {
                assessmentIntent = new Intent(mContext, AssessmentDetailScreen.class);
            } else {
                assessmentIntent = new Intent(mContext, AssessmentPlayActivity.class);
            }
            ActiveGame activeGame = setGame(OustAppState.getInstance().getActiveUser());
            assessmentIntent.putExtra("ActiveGame", gson.toJson(activeGame));
            String id = "" + commonLandingData.getId();
            //id = id.replace("ASSESSMENT", "");
            assessmentIntent.putExtra("assessmentId", id);
            assessmentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(assessmentIntent);
        } else if ((commonLandingData.getType() != null) && (commonLandingData.getType().toUpperCase().contains("SURVEY"))) {
            Gson gson = new Gson();
            Intent intent = new Intent(OustSdkApplication.getContext(), SurveyDetailActivity.class);
            if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_SURVEY_NEW_UI)) {
                intent = new Intent(OustSdkApplication.getContext(), SurveyComponentActivity.class);
            }
            ActiveGame activeGame = setGame(OustAppState.getInstance().getActiveUser());
            intent.putExtra("ActiveGame", gson.toJson(activeGame));
            String id = "" + commonLandingData.getId();
            //id = id.replace("ASSESSMENT", "");
            intent.putExtra("assessmentId", id);
            intent.putExtra("courseId", commonLandingData.getMappedCourseId());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        } else {

            if (commonLandingData.getMode() != null && commonLandingData.getMode().equalsIgnoreCase("ARCHIVED")) {
                Toast.makeText(mContext, "Sorry. This course has been archived by admin", Toast.LENGTH_SHORT).show();
            } else {
                String courseID = "" + commonLandingData.getId();
                if (courseID.contains("COURSE")) {
                    courseID = courseID.replace("COURSE", "");
                } else if (courseID.contains("course")) {
                    courseID = courseID.replace("course", "");
                }

                Intent taskCourseIntent = new Intent(mContext, CourseDetailScreen.class);
                taskCourseIntent.putExtra("learningId", courseID);
                taskCourseIntent.putExtra("catalog_id", "" + courseID);
                taskCourseIntent.putExtra("catalog_type", "COURSE");
                mContext.startActivity(taskCourseIntent);
            }
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

    private String timeZoneConversation(String dateFormat, long dateInMillis, String timeZone) {

        long dataMilli = TimeZone.getTimeZone(timeZone).getRawOffset();
        long displayMilli = TimeZone.getDefault().getRawOffset();
        long diffMilli = displayMilli - dataMilli;

        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.getDefault());
        Date dataTime = new Date((dateInMillis + diffMilli));

        return formatter.format(dataTime);


    }
}
