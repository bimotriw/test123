package com.oustme.oustsdk.data.handlers.impl;

import static com.oustme.oustsdk.firebase.common.FeedType.GAMELET_WORDJUMBLE;
import static com.oustme.oustsdk.firebase.common.FeedType.GAMELET_WORDJUMBLE_V2;
import static com.oustme.oustsdk.firebase.common.FeedType.GAMELET_WORDJUMBLE_V3;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;


import com.clevertap.android.sdk.CleverTapAPI;
import com.google.gson.Gson;
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
import com.oustme.oustsdk.interfaces.common.CplCloseListener;
import com.oustme.oustsdk.interfaces.common.FeedClickListener;
import com.oustme.oustsdk.response.common.GameType;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTONewFeed;
import com.oustme.oustsdk.survey_module.SurveyComponentActivity;
import com.oustme.oustsdk.survey_ui.SurveyDetailActivity;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.CplDataHandler;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustDataHandler;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.appconstants.AppConstants;

import java.util.HashMap;

/**
 * Created by oust on 8/21/18.
 */

public class UserCatalogueHandler {
    private static final String TAG = "UserCatalogueHandler";
    public Context context;
    private CplModelData cplModelData;
    private FeedClickListener feedClickListener;
    private CplCloseListener cplCloseListener;
    private boolean progressAfterAssessmentFail;
    private boolean rateCourse;
    private boolean reDistributeCPLonFAIL = false;
    private boolean isEvent;
    private int eventId = 0;
    private boolean isMultipleCpl = false;

    public UserCatalogueHandler(Context mContext, CplModelData cplModelData,
                                FeedClickListener feedClickListener, CplCloseListener cplCloseListener, boolean isEvent, int eventId, boolean isMultipleCpl) {
        this.context = mContext;
        this.cplModelData = cplModelData;
        this.cplCloseListener = cplCloseListener;
        this.feedClickListener = feedClickListener;
        this.isEvent = isEvent;
        this.eventId = eventId;
        this.isMultipleCpl = isMultipleCpl;
    }

    public boolean isProgressAfterAssessmentFail() {
        return progressAfterAssessmentFail;
    }

    public void setProgressAfterAssessmentFail(boolean progressAfterAssessmentFail) {
        this.progressAfterAssessmentFail = progressAfterAssessmentFail;
    }

    public void setRateCourse(boolean rateCourse) {
        this.rateCourse = rateCourse;
    }

    public void initHandler() {
        Log.d(TAG, "initHandler: ");
        if (cplModelData.getCommonLandingData() != null) {
            startCatalogActivity(cplModelData.getCommonLandingData(), cplModelData);
        } else if (cplModelData.getNewFeed() != null) {
            DTONewFeed DTONewFeed = cplModelData.getNewFeed();
            try {
                CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(OustSdkApplication.getContext());
                HashMap<String, Object> eventUpdate = OustSdkTools.getCleverTapEventData();
                eventUpdate.put("ClickedOnPlaylist", true);
                eventUpdate.put("Playlist ID", DTONewFeed.getCplId());
                eventUpdate.put("Playlist Name", DTONewFeed.getHeader());
                eventUpdate.put("Module Type", cplModelData.getContentType());
                eventUpdate.put("Module ID", cplModelData.getContentId());
                eventUpdate.put("Module Name", cplModelData.getCommonLandingData().getName());
                Log.d("TAG", "CleverTap instance: " + eventUpdate.toString());
                if (clevertapDefaultInstance != null) {
                    clevertapDefaultInstance.pushEvent("Playlist_ModuleDetail_Click", eventUpdate);
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            feedClicked(DTONewFeed.getFeedId(), DTONewFeed.getCplId());
            openFeed(DTONewFeed);
        }
    }

    private void openFeed(DTONewFeed DTONewFeed) {
        if (DTONewFeed.getFeedType() == FeedType.ASSESSMENT_PLAY) {
            gotoAssessment("" + DTONewFeed.getAssessmentId(), DTONewFeed.getCplId());
        } else if (DTONewFeed.getFeedType() == FeedType.COURSE_UPDATE) {
            gotoCourse("" + DTONewFeed.getCourseId(), DTONewFeed.getCplId());
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

    private void joinMeeting(String meetingId) {
        if ((meetingId != null) && (meetingId.length() > 8) && (meetingId.length() < 12)) {
            boolean isAppInstalled = appInstalledOrNot("com.oustme.oustlive");
            if (isAppInstalled) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.oustme.oustlive", "com.oustme.oustlive.ZoomJoinActivity"));
                intent.putExtra("zoommeetingId", meetingId);
                intent.putExtra("userName", OustAppState.getInstance().getActiveUser().getUserDisplayName());
                intent.putExtra("isComingThroughOust", true);
                context.startActivity(intent);
            } else {
                Intent intent = new Intent(OustSdkApplication.getContext(), ZoomBaseActivity.class);
                intent.putExtra("joinMeeting", true);
                context.startActivity(intent);
            }
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

    private void startCatalogActivity(CommonLandingData commonLandingData, CplModelData cplModelData) {
        try {
            CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(OustSdkApplication.getContext());
            HashMap<String, Object> eventUpdate = OustSdkTools.getCleverTapEventData();
            eventUpdate.put("ClickedOnPlaylist", true);
            eventUpdate.put("Playlist ID", commonLandingData.getCplId());
            eventUpdate.put("Playlist Name", commonLandingData.getName());
            eventUpdate.put("Module Type", cplModelData.getContentType());
            eventUpdate.put("Module ID", cplModelData.getContentId());
            eventUpdate.put("Module Name", cplModelData.getCommonLandingData().getName());
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
            Log.d(TAG, "AttemptCount:" + cplModelData.getAttemptCount() + "  --  AttemptsAllowed:" + commonLandingData.getNoOfAttemptAllowedToPass() + " -- DisableUser:" + OustPreferences.getAppInstallVariable("disableUser"));
            if (!cplModelData.isPass()) {
                if ((cplModelData.getAttemptCount() == 0) || cplModelData.getAttemptCount() < commonLandingData.getNoOfAttemptAllowedToPass()) {
                    //if ((commonLandingData.getNoOfAttemptAllowedToPass()==0) || cplModelData.getAttemptCount() < commonLandingData.getNoOfAttemptAllowedToPass()) {
                    CplDataHandler.getInstance().setCplAssessmentAttemptCount((int) cplModelData.getAttemptCount());
                    /*if(cplModelData.getAttemptCount()>0 && isReDistributeCPLonFAIL()){
                        if(cplCloseListener!=null){
                            cplCloseListener.onAssessmentFail((int)cplModelData.getAttemptCount(), false);
                        }
                    }else {*/

                    String id = commonLandingData.getId();
                    id = id.replace("ASSESSMENT", "");
                    if (isReDistributeCPLonFAIL() && cplCloseListener != null) {
                        OustStaticVariableHandling.getInstance().setCplCloseListener(cplCloseListener);
                    }
                    gotoAssessment(id, commonLandingData.getCplId());

                    //}
                } else {
                    OustStaticVariableHandling.getInstance().setModuleClicked(false);
                    /*if(isReDistributeCPLonFAIL()){
                        if(cplCloseListener!=null){
                            cplCloseListener.onAssessmentFail((int)cplModelData.getAttemptCount(), true);
                        }
                    }else {*/
                    if (cplCloseListener != null) {
                        cplCloseListener.onAssessmentFailure(OustPreferences.getAppInstallVariable("disableUser"));
                    }
                    //}
                }
            } else {
                OustStaticVariableHandling.getInstance().setModuleClicked(false);
                OustSdkTools.showToast("You have already passed this assessment!");
            }
        } else if ((commonLandingData.getType() != null) && (commonLandingData.getType().contains("SURVEY"))) {
            String id = commonLandingData.getId();
            id = id.replace("ASSESSMENT", "");
            gotoSurvey(id, "", commonLandingData.getCplId());
        } else {
            String id = commonLandingData.getId();
            id = id.replace("COURSE", "");
            gotoCourse(id, commonLandingData.getCplId());
        }
    }

    private void gotoSurvey(String surveyId, String surveyTitle, long cplId) {
        try {
            Log.d(TAG, "gotoSurvey: CPLID:" + cplId);
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
            intent.putExtra("cplId", cplId);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            OustSdkApplication.getContext().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void gotoCourse(String courseId, long cplId) {
        try {
            Intent intent = new Intent(OustSdkApplication.getContext(), NewLearningMapActivity.class);
            intent.putExtra("learningId", courseId);
            if (OustPreferences.getAppInstallVariable("isLayout4") &&
                    OustPreferences.getAppInstallVariable(AppConstants.StringConstants.IS_NEW_LEARNING_MAP_MODE)) {
                intent = new Intent(OustSdkApplication.getContext(), CourseLearningMapActivity.class);
                intent.putExtra("learningId", Long.parseLong(courseId));
            }
            if (isEvent) {
                intent.putExtra("isEventLaunch", true);
                intent.putExtra("eventId", eventId);
            }
            intent.putExtra("isComingFromCpl", true);
            intent.putExtra("isMultipleCplModule", isMultipleCpl);
            intent.putExtra("currentCplId", String.valueOf(cplId));
            intent.putExtra("rateCourse", rateCourse);
            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (context != null)
                context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void gotoAssessment(String assessmentId, long cplId) {
        Gson gson = new Gson();
        Intent intent;
        intent = new Intent(OustSdkApplication.getContext(), AssessmentDetailScreen.class);
        //Intent intent = new Intent(OustSdkApplication.getContext(), AssessmentDetailScreen.class);
        ActiveGame activeGame = setGame(OustAppState.getInstance().getActiveUser());
        intent.putExtra("ActiveGame", gson.toJson(activeGame));
        intent.putExtra("assessmentId", assessmentId);
        intent.putExtra("isCplModule", true);
        intent.putExtra("isMultipleCplModule", isMultipleCpl);
        intent.putExtra("currentCplId", cplId);
        intent.putExtra("isComingFromCpl", true);
        if (isEvent) {
            intent.putExtra("isEventLaunch", true);
            intent.putExtra("eventId", eventId);
        }
        context.startActivity(intent);
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

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = OustSdkApplication.getContext().getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return false;
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

    public boolean isReDistributeCPLonFAIL() {
        return reDistributeCPLonFAIL;
    }

    public void setReDistributeCPLonFAIL(boolean reDistributeCPLonFAIL) {
        this.reDistributeCPLonFAIL = reDistributeCPLonFAIL;
    }
}
