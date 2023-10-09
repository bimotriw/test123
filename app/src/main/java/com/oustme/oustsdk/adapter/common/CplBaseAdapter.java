package com.oustme.oustsdk.adapter.common;

import static com.oustme.oustsdk.firebase.common.FeedType.GAMELET_WORDJUMBLE;
import static com.oustme.oustsdk.firebase.common.FeedType.GAMELET_WORDJUMBLE_V2;
import static com.oustme.oustsdk.firebase.common.FeedType.GAMELET_WORDJUMBLE_V3;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.Html;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.clevertap.android.sdk.CleverTapAPI;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.assessments.GameLetActivity;
import com.oustme.oustsdk.activity.common.FeedCardActivity;
import com.oustme.oustsdk.activity.common.ZoomBaseActivity;
import com.oustme.oustsdk.activity.courses.LessonsActivity;
import com.oustme.oustsdk.activity.courses.newlearnngmap.NewLearningMapActivity;
import com.oustme.oustsdk.assessment_ui.assessmentDetail.AssessmentDetailScreen;
import com.oustme.oustsdk.course_ui.CourseLearningMapActivity;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.firebase.common.CplModelData;
import com.oustme.oustsdk.firebase.common.FeedType;
import com.oustme.oustsdk.interfaces.common.FeedClickListener;
import com.oustme.oustsdk.response.common.GameType;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTONewFeed;
import com.oustme.oustsdk.survey_module.SurveyComponentActivity;
import com.oustme.oustsdk.survey_ui.SurveyDetailActivity;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustDataHandler;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.appconstants.AppConstants;

import java.util.ArrayList;
import java.util.HashMap;

public class CplBaseAdapter extends RecyclerView.Adapter<CplBaseAdapter.MyViewHolder> {

    private static final String TAG = "CplBaseAdapter";
    private ArrayList<CplModelData> cplModelDataArrayList;
    private final Context context;
    private long milliSec;
    private final FeedClickListener feedClickListener;
    private CplModelData currentCplData;
    private boolean isMultipleCpl;
    private String isCplId, cplName;

    public void setCurrentCplData(CplModelData currentCplData, boolean multipleCpl, String cplId, String cplName) {
        this.currentCplData = currentCplData;
        this.isMultipleCpl = multipleCpl;
        this.isCplId = cplId;
        this.cplName = cplName;
    }

    public CplBaseAdapter(Context context, ArrayList<CplModelData> cplModelDataArrayList, FeedClickListener feedClickListener, String cplId, String cplName) {
        this.context = context;
        this.cplModelDataArrayList = cplModelDataArrayList;
        milliSec = System.currentTimeMillis();
        this.feedClickListener = feedClickListener;
        this.isCplId = cplId;
        this.cplName = cplName;

        for (int i = 0; i < cplModelDataArrayList.size(); i++) {
            if (i == 0) {
                this.cplModelDataArrayList.get(i).setModuleCompleted(true);
            } else if (this.cplModelDataArrayList.get(i - 1).isCompleted()) {
                this.cplModelDataArrayList.get(i).setModuleCompleted(true);
            } else {
                this.cplModelDataArrayList.get(i).setModuleCompleted(false);
            }
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final View upper_line;
        private final TextView contentName, contentDescription, content_time_status;
        private final ProgressBar content_progress_bar;
        private final ImageView content_img, content_status_img;
        private final LinearLayout main_content_ll, shadow_ll, white_space_ll;
        private final RelativeLayout content_status_bg;

        MyViewHolder(View view) {
            super(view);
            upper_line = view.findViewById(R.id.upper_line);
            contentName = view.findViewById(R.id.contentName);
            contentDescription = view.findViewById(R.id.contentDescription);
            content_time_status = view.findViewById(R.id.content_time_status);
            content_progress_bar = view.findViewById(R.id.content_progress_bar);
            content_img = view.findViewById(R.id.content_img);
            content_status_img = view.findViewById(R.id.content_status_img);
            main_content_ll = view.findViewById(R.id.main_content_ll);
            shadow_ll = view.findViewById(R.id.shadow_ll);
            white_space_ll = view.findViewById(R.id.white_space_ll);
            content_status_bg = view.findViewById(R.id.content_status_bg);

        }
    }

    public void notifyAdapter(ArrayList<CplModelData> cplModelDataArrayList, CplModelData cplModelData, boolean isMultipleCpl, String cplId, String cplName) {
        this.cplModelDataArrayList = cplModelDataArrayList;
        for (int i = 0; i < cplModelDataArrayList.size(); i++) {
            if (i == 0) {
                this.cplModelDataArrayList.get(i).setModuleCompleted(true);
            } else if (this.cplModelDataArrayList.get(i - 1).isCompleted()) {
                this.cplModelDataArrayList.get(i).setModuleCompleted(true);
            } else {
                this.cplModelDataArrayList.get(i).setModuleCompleted(false);
            }
        }
        this.currentCplData = cplModelData;
        this.isMultipleCpl = isMultipleCpl;
        this.isCplId = cplId;
        this.cplName = cplName;
        milliSec = System.currentTimeMillis();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cpl_item_data, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
    }

    @Override
    public long getItemId(int position) {
        return cplModelDataArrayList.get(position).getContentId();
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {

            CplModelData cplModelData = cplModelDataArrayList.get(position);
            if (position == 0) {
                holder.upper_line.setVisibility(View.INVISIBLE);
            }

            Log.d(TAG, "onBindViewHolder: " + cplModelData.getContentType());
            Log.d(TAG, "onBindViewHolder: " + cplModelData.getCommonLandingData().getName());

            if (cplModelData.getContentType().equalsIgnoreCase("course")) {
                setCplCourseData(cplModelData, holder);
            } else if (cplModelData.getContentType().equalsIgnoreCase("assessment")) {
                setCplAssessmentData(cplModelData, holder);
            } else if (cplModelData.getContentType().equalsIgnoreCase("Survey")) {
                setCplAssessmentData(cplModelData, holder);
            } else if (cplModelData.getContentType().equalsIgnoreCase("newsfeed")) {
                setCplFeedData(cplModelData, holder);
            } else if (cplModelData.getContentType().equalsIgnoreCase("collection")) {
                setCplCollectionData(cplModelData, holder);
            }


            final int pos = position;
            holder.main_content_ll.setOnClickListener(v -> {
                ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(v, "scaleX", 1.0f, 0.94f);
                ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(v, "scaleY", 1.0f, 0.96f);
                scaleDownX.setDuration(150);
                scaleDownY.setDuration(150);
                scaleDownX.setRepeatCount(1);
                scaleDownY.setRepeatCount(1);
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
                        if (cplModelDataArrayList.get(pos).getStartDate() < milliSec && cplModelDataArrayList.get(pos).isModuleCompleted()) {
                            if (!OustStaticVariableHandling.getInstance().isModuleClicked()) {
                                clickonRow(pos);
                            }
                        } else if (!cplModelDataArrayList.get(pos).isModuleCompleted()) {
                            OustSdkTools.showToast(OustSdkApplication.getContext().getString(R.string.cpl_locked_msg));
                        } else {
                            String date = OustSdkTools.getDate("" + cplModelDataArrayList.get(pos).getStartDate());
                            String message = context.getResources().getString(R.string.cpl_lock_msg);
                            if (date.contains("Today")) {
                                message = message + " " + date;
                            } else {
                                message = message + " on " + date;
                            }
                            OustSdkTools.showToast(message);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setCplCollectionData(CplModelData cplModelData, MyViewHolder holder) {
        CommonLandingData commonLandingData = cplModelData.getCommonLandingData();
        holder.content_img.setImageDrawable(context.getResources().getDrawable(R.drawable.collection));
        if (commonLandingData != null) {
            holder.contentName.setText(commonLandingData.getName());
            String description = commonLandingData.getDescription();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.contentDescription.setText(Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT));
            } else {
                holder.contentDescription.setText(Html.fromHtml(description));
            }
            if (commonLandingData.getCompletionPercentage() > 0) {
                holder.content_progress_bar.setProgress((int) commonLandingData.getCompletionPercentage());
                holder.content_progress_bar.setVisibility(View.VISIBLE);
                holder.content_time_status.setVisibility(View.GONE);
            } else {
                holder.content_progress_bar.setVisibility(View.GONE);
                holder.content_time_status.setVisibility(View.VISIBLE);
                holder.content_time_status.setText("Available at : " + OustSdkTools.getDate("" + cplModelData.getStartDate()));
            }

            setCPlImageStatus(cplModelData, cplModelData.getContentId(), cplModelData.getStartDate(), commonLandingData.getCompletionPercentage(), holder);
        }
    }

    private void setCPlImageStatus(CplModelData cplModelData, long id, long startDate, long completionPercentage, MyViewHolder holder) {
        if (startDate < milliSec && cplModelData.isModuleCompleted()) {
            if (completionPercentage >= 99) {
                holder.content_status_img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_check));
            } else {
                holder.content_status_img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_play_white));
            }
            if (currentCplData != null && currentCplData.getContentId() == id) {
                setCurrentContentBg(holder);
            } else {
                setAvailableContentBg(holder);
            }
        } else {
            setLockedBackGround(holder);
            holder.content_status_img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_lock_img));
        }
    }

    private void setLockedBackGround(MyViewHolder holder) {
        holder.main_content_ll.setBackgroundColor(context.getResources().getColor(R.color.popupBackGroundwhitea));
        holder.shadow_ll.setVisibility(View.GONE);
        holder.white_space_ll.setVisibility(View.VISIBLE);
        holder.content_status_bg.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.orangelite_circle_bg));
    }

    private void setAvailableContentBg(MyViewHolder holder) {
        holder.main_content_ll.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        holder.shadow_ll.setVisibility(View.GONE);
        holder.white_space_ll.setVisibility(View.VISIBLE);
    }

    private void setCurrentContentBg(MyViewHolder holder) {
        holder.main_content_ll.setBackgroundColor(context.getResources().getColor(R.color.white_transparent));
        holder.shadow_ll.setVisibility(View.VISIBLE);
        holder.white_space_ll.setVisibility(View.VISIBLE);
    }


    private void setCplFeedData(CplModelData cplModelData, MyViewHolder holder) {
        DTONewFeed DTONewFeed = cplModelData.getNewFeed();
        if (DTONewFeed != null) {
            holder.content_progress_bar.setVisibility(View.GONE);
            holder.content_time_status.setVisibility(View.VISIBLE);
            holder.content_time_status.setText("Available at : " + OustSdkTools.getDate("" + cplModelData.getStartDate()));
            if (cplModelData.getCompletedDate() != 0) {
                holder.content_time_status.setText("Completed on : " + OustSdkTools.getDate("" + cplModelData.getCompletedDate()));
            }
            holder.contentName.setText(DTONewFeed.getHeader());
            String description = DTONewFeed.getContent();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.contentDescription.setText(Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT));
            } else {
                holder.contentDescription.setText(Html.fromHtml(description));
            }
            if (DTONewFeed.getFeedType() != null) {
                if (DTONewFeed.getFeedType() == GAMELET_WORDJUMBLE ||
                        DTONewFeed.getFeedType() == GAMELET_WORDJUMBLE_V2 ||
                        DTONewFeed.getFeedType() == GAMELET_WORDJUMBLE_V3) {
                    holder.content_img.setImageDrawable(context.getResources().getDrawable(R.drawable.wj));
                } else if (DTONewFeed.getFeedType() == FeedType.COURSE_CARD_L) {
                    holder.content_img.setImageDrawable(context.getResources().getDrawable(R.drawable.card));
                }
            }
            setCPlImageStatus(cplModelData, cplModelData.getContentId(), cplModelData.getStartDate(), 0, holder);
        }
    }

    private void setCplAssessmentData(CplModelData cplModelData, MyViewHolder holder) {
        holder.content_img.setImageDrawable(context.getResources().getDrawable(R.drawable.like));
        CommonLandingData commonLandingData = cplModelData.getCommonLandingData();
        if (commonLandingData != null) {
            if (commonLandingData.getType().contains("SURVEY")) {
                holder.content_img.setImageDrawable(context.getResources().getDrawable(R.drawable.survey));
            }
            holder.contentName.setText(commonLandingData.getName());
            String description = commonLandingData.getDescription();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.contentDescription.setText(Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT));
            } else {
                holder.contentDescription.setText(Html.fromHtml(description));
            }
            if (commonLandingData.getCompletionPercentage() > 0) {
                holder.content_progress_bar.setProgress((int) commonLandingData.getCompletionPercentage());
                holder.content_progress_bar.setVisibility(View.VISIBLE);
                holder.content_time_status.setVisibility(View.GONE);
            } else {
                holder.content_progress_bar.setVisibility(View.GONE);
                holder.content_time_status.setVisibility(View.VISIBLE);
                holder.content_time_status.setText("Available at : " + OustSdkTools.getDate("" + cplModelData.getStartDate()));
            }
            if (cplModelData.getCompletedDate() != 0) {
                holder.content_time_status.setVisibility(View.VISIBLE);
                holder.content_time_status.setText("Completed on : " + OustSdkTools.getDate("" + cplModelData.getCompletedDate()));
            }
            setCPlImageStatus(cplModelData, cplModelData.getContentId(), cplModelData.getStartDate(), commonLandingData.getCompletionPercentage(), holder);
        }
    }

    private void setCplCourseData(CplModelData cplModelData, MyViewHolder holder) {
        CommonLandingData commonLandingData = cplModelData.getCommonLandingData();
        holder.content_img.setImageDrawable(context.getResources().getDrawable(R.drawable.course));
        if (commonLandingData != null) {
            holder.contentName.setText(commonLandingData.getName());
            Log.d(TAG, "setCplCourseData: " + commonLandingData.getName());
            String description = commonLandingData.getDescription();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.contentDescription.setText(Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT));
            } else {
                holder.contentDescription.setText(Html.fromHtml(description));
            }

            Log.d(TAG, "setCplCourseData: " + commonLandingData.getDescription());
            if (commonLandingData.getCompletionPercentage() > 0) {
                holder.content_progress_bar.setProgress((int) commonLandingData.getCompletionPercentage());
                holder.content_progress_bar.setVisibility(View.VISIBLE);
                holder.content_time_status.setVisibility(View.GONE);
            } else {
                holder.content_progress_bar.setVisibility(View.GONE);
                holder.content_time_status.setVisibility(View.VISIBLE);
                holder.content_time_status.setText("Available at : " + OustSdkTools.getDate("" + cplModelData.getStartDate()));
            }
            if (cplModelData.getCompletedDate() != 0) {
                holder.content_time_status.setVisibility(View.VISIBLE);
                holder.content_time_status.setText("Completed on : " + OustSdkTools.getDate("" + cplModelData.getCompletedDate()));
            }
            setCPlImageStatus(cplModelData, cplModelData.getContentId(), cplModelData.getStartDate(), commonLandingData.getCompletionPercentage(), holder);
        }
    }


    @Override
    public int getItemCount() {
        return cplModelDataArrayList.size();
    }


    public void clickonRow(int position) {
        try {
            Log.d(TAG, "clickonRow: " + position);
            CplModelData cplModelData = cplModelDataArrayList.get(position);
            if (cplModelData.getCommonLandingData() != null)
                startCatalogActivity(cplModelDataArrayList.get(position).getCommonLandingData());
            else if (cplModelData.getNewFeed() != null) {
                DTONewFeed DTONewFeed = cplModelData.getNewFeed();
                feedClicked(DTONewFeed.getFeedId(), DTONewFeed.getCplId());
                openFeed(DTONewFeed, position);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void openFeed(DTONewFeed DTONewFeed, int position) {
        try {
            CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(OustSdkApplication.getContext());
            HashMap<String, Object> eventUpdate = OustSdkTools.getCleverTapEventData();
            eventUpdate.put("ClickedOnPlaylist", true);
            eventUpdate.put("Playlist ID", isCplId);
            eventUpdate.put("Playlist Name", cplName);
            eventUpdate.put("Module Type", DTONewFeed.getFeedType());
            eventUpdate.put("Module ID", cplModelDataArrayList.get(position).getContentId());
            eventUpdate.put("Module Name", cplModelDataArrayList.get(position).getCommonLandingData().getName());
            Log.d("TAG", "CleverTap instance: " + eventUpdate.toString());
            if (clevertapDefaultInstance != null) {
                clevertapDefaultInstance.pushEvent("Playlist_ModuleDetail_Click", eventUpdate);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        if (DTONewFeed.getFeedType() == FeedType.ASSESSMENT_PLAY) {
            gotoAssessment("" + DTONewFeed.getAssessmentId());
        } else if (DTONewFeed.getFeedType() == FeedType.COURSE_UPDATE) {
            gotoCourse("" + DTONewFeed.getCourseId());
        } else if (DTONewFeed.getFeedType() == FeedType.SURVEY) {
            gotoSurvey("" + DTONewFeed.getAssessmentId(), DTONewFeed.getHeader(), DTONewFeed.getCplId());
            OustAppState.getInstance().setCurrentSurveyFeed(DTONewFeed);
        } else if (DTONewFeed.getFeedType() == FeedType.JOIN_MEETING) {
            joinMeeting("" + DTONewFeed.getId());
        } else if (DTONewFeed.getFeedType() == GAMELET_WORDJUMBLE ||
                DTONewFeed.getFeedType() == GAMELET_WORDJUMBLE_V2 ||
                DTONewFeed.getFeedType() == GAMELET_WORDJUMBLE_V3) {
            String feedType = String.valueOf(DTONewFeed.getFeedType());
            gotoGamelet("" + DTONewFeed.getAssessmentId(), feedType);
        } else if (DTONewFeed.getFeedType() == FeedType.GENERAL) {
            String mUrl = DTONewFeed.getLink();

            if ((mUrl != null) && (!mUrl.isEmpty())) {
                if (!mUrl.startsWith("http://") && !mUrl.startsWith("https://")) {
                    mUrl = "http://" + mUrl;
                }
                onnewsfeedClick(mUrl);
            }
        } else if (DTONewFeed.getFeedType() == FeedType.COURSE_CARD_L) {
            if (DTONewFeed.getCourseCardClass() != null)
                clickOnCard(DTONewFeed.getCourseCardClass());
        }
    }


    private void clickOnCard(DTOCourseCard courseCardClass) {
        OustDataHandler.getInstance().setCourseCardClass(courseCardClass);
        Intent intent = new Intent(OustSdkApplication.getContext(), FeedCardActivity.class);
        intent.putExtra("type", "card");
        context.startActivity(intent);
    }

    private void onnewsfeedClick(String mUrl) {
        Intent intent = new Intent(OustSdkApplication.getContext(), FeedCardActivity.class);
        intent.putExtra("type", "url");
        intent.putExtra("mUrl", mUrl);
        context.startActivity(intent);
    }


    public void gotoGamelet(String assessmentId, String type) {
        try {
            if ((assessmentId != null) && (!assessmentId.isEmpty())) {
                Intent intent = new Intent(OustSdkApplication.getContext(), GameLetActivity.class);
                intent.putExtra("assessmentId", assessmentId);
                intent.putExtra("feedType", type);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void joinMeeting(String meetingId) {
        if ((meetingId != null) && (meetingId.length() > 8) && (meetingId.length() < 12)) {
            boolean isAppInstalled = appInstalledOrNot("com.oustme.oustlive");
            Intent intent;
            if (isAppInstalled) {
                intent = new Intent();
                intent.setComponent(new ComponentName("com.oustme.oustlive", "com.oustme.oustlive.ZoomJoinActivity"));
                intent.putExtra("zoommeetingId", meetingId);
                intent.putExtra("userName", OustAppState.getInstance().getActiveUser().getUserDisplayName());
                intent.putExtra("isComingThroughOust", true);
            } else {
                intent = new Intent(OustSdkApplication.getContext(), ZoomBaseActivity.class);
                intent.putExtra("joinMeeting", true);
            }
            context.startActivity(intent);
        }
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = OustSdkApplication.getContext().getPackageManager();
        try {
            if (uri != null) {
                pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return false;
    }


    private void gotoSurvey(String surveyId, String surveyTitle, Long cplId) {
        try {
            Log.d(TAG, "gotoSurvey: " + cplId);
            Gson gson = new Gson();
            Intent intent = new Intent(OustSdkApplication.getContext(), SurveyDetailActivity.class);
            if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_SURVEY_NEW_UI)) {
                intent = new Intent(OustSdkApplication.getContext(), SurveyComponentActivity.class);
            }
            ActiveGame activeGame = setGame(OustAppState.getInstance().getActiveUser());
            intent.putExtra("ActiveGame", gson.toJson(activeGame));
            intent.putExtra("assessmentId", surveyId);
            intent.putExtra("surveyTitle", surveyTitle);
            intent.putExtra("isMultipleCplModule", isMultipleCpl);
            intent.putExtra("cplId", Long.parseLong(isCplId));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            OustSdkApplication.getContext().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void gotoCourse(String courseId) {
        try {
            Intent intent = new Intent(OustSdkApplication.getContext(), NewLearningMapActivity.class);
            intent.putExtra("learningId", courseId);
            if (OustPreferences.getAppInstallVariable("isLayout4") &&
                    OustPreferences.getAppInstallVariable(AppConstants.StringConstants.IS_NEW_LEARNING_MAP_MODE)) {
                intent = new Intent(OustSdkApplication.getContext(), CourseLearningMapActivity.class);
                intent.putExtra("learningId", Long.parseLong(courseId));
                intent.putExtra("currentCplId", isCplId);
                intent.putExtra("isMultipleCplModule", isMultipleCpl);
            }
            intent.putExtra("isComingFromCpl", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            OustSdkApplication.getContext().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void gotoAssessment(String assessmentId) {
        try {
            Gson gson = new Gson();
            Intent intent;
            intent = new Intent(OustSdkApplication.getContext(), AssessmentDetailScreen.class);
            //Intent intent = new Intent(OustSdkApplication.getContext(), AssessmentDetailScreen.class);
            ActiveGame activeGame = setGame(OustAppState.getInstance().getActiveUser());
            intent.putExtra("ActiveGame", gson.toJson(activeGame));
            intent.putExtra("assessmentId", assessmentId);
            intent.putExtra("currentCplId", Long.parseLong(isCplId));
            intent.putExtra("isMultipleCplModule", isMultipleCpl);
            intent.putExtra("isComingFromCpl", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            OustSdkApplication.getContext().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void feedClicked(long feedId, long cplId) {
        try {
            if (feedClickListener != null) {
                feedClickListener.onFeedClick(feedId, (int) cplId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
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

    private void startCatalogActivity(CommonLandingData commonLandingData) {
        Log.e(TAG, "gotoCourse:isMultipleCplModule->  " + isMultipleCpl);
        try {
            CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(OustSdkApplication.getContext());
            HashMap<String, Object> eventUpdate = OustSdkTools.getCleverTapEventData();
            eventUpdate.put("ClickedOnPlaylist", true);
            eventUpdate.put("Playlist ID", isCplId);
            eventUpdate.put("Playlist Name", cplName);
            eventUpdate.put("Module Type", commonLandingData.getType());
            eventUpdate.put("Module ID", commonLandingData.getModuleId());
            eventUpdate.put("Module Name", commonLandingData.getName());
            Log.d("TAG", "CleverTap instance: " + eventUpdate.toString());
            if (clevertapDefaultInstance != null) {
                clevertapDefaultInstance.pushEvent("Playlist_ModuleDetail_Click", eventUpdate);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        OustStaticVariableHandling.getInstance().setModuleClicked(true);
        if ((commonLandingData.getType() != null) && (commonLandingData.getType().contains("COLLECTION"))) {
            String id = commonLandingData.getId();
            id = id.replace("COLLECTION", "");
            Intent intent = new Intent(OustSdkApplication.getContext(), LessonsActivity.class);
            intent.putExtra("collectionId", id);
            intent.putExtra("banner", commonLandingData.getBanner());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            OustSdkApplication.getContext().startActivity(intent);

        } else if ((commonLandingData.getType() != null) && (commonLandingData.getType().contains("ASSESSMENT"))) {
            String id = commonLandingData.getId();
            id = id.replace("ASSESSMENT", "");
            gotoAssessment(id);

        } else if ((commonLandingData.getType() != null) && (commonLandingData.getType().contains("SURVEY"))) {
            String id = commonLandingData.getId();
            id = id.replace("SURVEY", "");
            id = id.replace("ASSESSMENT", "");
            gotoSurvey(id, "", commonLandingData.getCplId());

        } else {
            String id = commonLandingData.getId();
            id = id.replace("COURSE", "");
            gotoCourse(id);
        }
    }
}
