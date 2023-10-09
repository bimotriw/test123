package com.oustme.oustsdk.layoutFour.components.myTask.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.assessments.AssessmentPlayActivity;
import com.oustme.oustsdk.activity.courses.CourseMultiLingualActivity;
import com.oustme.oustsdk.activity.courses.LessonsActivity;
import com.oustme.oustsdk.activity.courses.newlearnngmap.NewLearningMapActivity;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.MultilingualCourse;
import com.oustme.oustsdk.response.common.GameType;
import com.oustme.oustsdk.survey_ui.SurveyDetailActivity;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.CommonTools;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.oustme.oustsdk.utils.LayoutType.GRID;

public class TaskModuleChildAdapter extends RecyclerView.Adapter<TaskModuleChildAdapter.ViewHolder> {

    private ArrayList<CommonLandingData> taskModuleList = new ArrayList<>();
    public Context context;
    private int type;


    public void setTaskRecyclerAdapter(ArrayList<CommonLandingData> taskModuleList, Context context, int type) {
        this.taskModuleList = taskModuleList;
        OustStaticVariableHandling.getInstance().setTaskModule(taskModuleList);
        this.context = context;
        this.type = type;
        setHasStableIds(true);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_module_item, parent, false);
//        ViewHolder viewHolder = new ViewHolder(v);
//        viewHolder.setIsRecyclable(false);

        View itemView = null;
        switch (type) {
            case GRID:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.catalog_module_grid_item, parent, false);
                break;
            default:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.catalog_module_item, parent, false);
                break;
        }

        return new ViewHolder(itemView);


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

        //  holder.tv_action.setTextColor(Color.parseColor(toolbarColorCode));

//        holder.action_root_lay.setBackground(OustSdkTools.drawableColor(context.getResources().getDrawable(R.drawable.bg_round_8)));
        //  holder.imageView2.setImageDrawable(OustSdkTools.drawableColor(context.getResources().getDrawable(R.drawable.ic_resume)));

        if (task != null) {

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

                        readDataFromFirebaseForAssessment(id, holder, task);
                    }
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

        View tv_action_lay;
        ImageView  iv_banner, imageView2;//view3,
        //CardView card_date;
        TextView tv_module_type, tv_title, tv_coin, tv_timer, tv_percentage, tv_action;
        ProgressBar pb_module;

        public ViewHolder(View itemView) {
            super(itemView);

            /*view3 = itemView.findViewById(R.id.view3);
            card_date = itemView.findViewById(R.id.card_date);*/
            tv_module_type = itemView.findViewById(R.id.tv_module_type);
            iv_banner = itemView.findViewById(R.id.iv_banner);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_coin = itemView.findViewById(R.id.tv_coin);
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
            tv_timer = itemView.findViewById(R.id.tv_timer);
            tv_action_lay = itemView.findViewById(R.id.tv_action_lay);
            pb_module = itemView.findViewById(R.id.pb_module);
            tv_percentage = itemView.findViewById(R.id.tv_percentage);
            tv_action = itemView.findViewById(R.id.tv_action);
            imageView2 = itemView.findViewById(R.id.imageView2);

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
                                Log.d("Assessment data ", lpMap.toString());
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
                String imageUrl;
                if (task.getType() != null && !task.getType().isEmpty()) {

                    if (task.getType().equalsIgnoreCase("course")) {
                        taskType = "COURSE";


                    } else if (task.getType().equalsIgnoreCase("Assessment")) {
                        taskType = "ASSESSMENT";
                    }

                }

                holder.tv_module_type.setText(taskType);
                holder.tv_module_type.setVisibility(View.GONE);


                if (task.getName() != null && !task.getName().isEmpty()) {
                    holder.tv_title.setText(task.getName());
                }

                if (task.getIcon() != null && !task.getIcon().isEmpty()) {
                    imageUrl = task.getIcon();
                    Glide.with(OustSdkApplication.getContext()).load(imageUrl).placeholder(R.drawable.app_icon).into(holder.iv_banner);
                }


                if (task.getCompletionPercentage() != 0) {

                    holder.pb_module.setVisibility(View.VISIBLE);
                    holder.tv_percentage.setVisibility(View.VISIBLE);
                    String completionPercentageText = task.getCompletionPercentage() + "%";
                    holder.tv_percentage.setText(completionPercentageText);
                    holder.pb_module.setProgress((int) task.getCompletionPercentage());
                    holder.tv_action.setText("Resume");


                } else {

                    holder.pb_module.setVisibility(View.GONE);
                    holder.tv_percentage.setVisibility(View.GONE);
                    holder.tv_action.setText("Start");

                }

                  holder.itemView.setOnClickListener(v -> launchActivity(task));


               /* if(task.getCourseDeadline()!=null&&!task.getCourseDeadline().isEmpty()){
                    Drawable drawable = context.getResources().getDrawable(R.drawable.ic_task_red);
                    holder.date_lay.setBackground(drawable);

                    Date date = new SimpleDateFormat("yyyy-MM-dd",Locale.US).parse(task.getCourseDeadline());
                    taskType = taskType+"/"+new SimpleDateFormat("dd MMM yyyy",Locale.US).format(date);
                    holder.task_module_type.setText(taskType);
                }*/

                if (task.getTime() != 0) {
                    double duration = (task.getTime() * 1.0) / 60;
                    String durationText = (int) duration + " min";
                    if (task.getTime() < 60) {
                        durationText = task.getTime() + " sec";

                    }

                    holder.tv_timer.setText(durationText);
                } else {
                    /*holder.t.setVisibility(View.GONE);
                    holder.view_hide.setVisibility(View.GONE);*/
                }


            }
        } catch (Exception e) {

            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void launchActivity(CommonLandingData commonLandingData) {

        OustStaticVariableHandling.getInstance().setModuleClicked(true);

        if ((commonLandingData.getType() != null) && (commonLandingData.getType().contains("COLLECTION"))) {
            Intent intent = new Intent(OustSdkApplication.getContext(), LessonsActivity.class);
            String id = commonLandingData.getId();
            id = id.replace("COLLECTION", "");
            intent.putExtra("collectionId", id);
            intent.putExtra("banner", commonLandingData.getBanner());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            OustSdkApplication.getContext().startActivity(intent);
        }
/*

        else if ((commonLandingData.getType() != null) && (commonLandingData.getType().contains("CPL")))
        {
        }
*/

        else if ((commonLandingData.getType() != null) && (commonLandingData.getType().contains("ASSESSMENT"))) {
            Gson gson = new Gson();
            Intent intent = new Intent(OustSdkApplication.getContext(), AssessmentPlayActivity.class);
            ActiveGame activeGame = setGame(OustAppState.getInstance().getActiveUser());
            intent.putExtra("ActiveGame", gson.toJson(activeGame));
            String id = commonLandingData.getId();
            id = id.replace("ASSESSMENT", "");
            intent.putExtra("assessmentId", id);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            OustSdkApplication.getContext().startActivity(intent);
        } else if ((commonLandingData.getType() != null) && (commonLandingData.getType().contains("SURVEY"))) {
            Gson gson = new Gson();
            Intent intent = new Intent(OustSdkApplication.getContext(), SurveyDetailActivity.class);
            ActiveGame activeGame = setGame(OustAppState.getInstance().getActiveUser());
            intent.putExtra("ActiveGame", gson.toJson(activeGame));
            String id = commonLandingData.getId();
            id = id.replace("ASSESSMENT", "");
            intent.putExtra("assessmentId", id);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            OustSdkApplication.getContext().startActivity(intent);
        } else {
            if (commonLandingData.getCourseType() != null && commonLandingData.getCourseType().equalsIgnoreCase("Multilingual")) {
                Intent intent = new Intent(OustSdkApplication.getContext(), CourseMultiLingualActivity.class);
                String id = commonLandingData.getId();
                List<MultilingualCourse> multilingualCourseList;
                multilingualCourseList = commonLandingData.getMultilingualCourseListList();
                id = id.replace("COURSE", "");
                intent.putExtra("learningId", id);
                Bundle bundle = new Bundle();
                bundle.putSerializable("multilingualDataList", (Serializable) multilingualCourseList);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                OustSdkApplication.getContext().startActivity(intent);
            } else {
                Intent intent = new Intent(OustSdkApplication.getContext(), NewLearningMapActivity.class);
                String id = commonLandingData.getId();
                id = id.replace("COURSE", "");
                intent.putExtra("learningId", id);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                OustSdkApplication.getContext().startActivity(intent);
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


}
