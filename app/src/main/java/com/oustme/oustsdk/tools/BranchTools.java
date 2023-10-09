package com.oustme.oustsdk.tools;

import static com.oustme.oustsdk.firebase.common.FeedType.ASSESSMENT_PLAY;
import static com.oustme.oustsdk.firebase.common.FeedType.COURSE_UPDATE;
import static com.oustme.oustsdk.firebase.common.FeedType.SURVEY;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.FFContest.FFcontestStartActivity;
import com.oustme.oustsdk.activity.assessments.AssessmentPlayActivity;
import com.oustme.oustsdk.activity.common.CatalogDetailListActivity;
import com.oustme.oustsdk.activity.common.CplBaseActivity;
import com.oustme.oustsdk.activity.common.languageSelector.LanguageSelectionActivity;
import com.oustme.oustsdk.activity.courses.LessonsActivity;
import com.oustme.oustsdk.assessment_ui.assessmentDetail.AssessmentDetailScreen;
import com.oustme.oustsdk.calendar_ui.ui.EventDataDetailScreen;
import com.oustme.oustsdk.catalogue_ui.CatalogueModuleListActivity;
import com.oustme.oustsdk.course_ui.CourseDetailScreen;
import com.oustme.oustsdk.course_ui.IntroScormCardActivity;
import com.oustme.oustsdk.feed_ui.services.FeedUpdatingServices;
import com.oustme.oustsdk.feed_ui.ui.GeneralFeedDetailScreen;
import com.oustme.oustsdk.feed_ui.ui.ImageFeedDetailScreen;
import com.oustme.oustsdk.feed_ui.ui.PublicVideoFeedCardScreen;
import com.oustme.oustsdk.feed_ui.ui.VideoCardDetailScreen;
import com.oustme.oustsdk.firebase.FFContest.BasicQuestionClass;
import com.oustme.oustsdk.firebase.FFContest.ContestNotificationMessage;
import com.oustme.oustsdk.firebase.FFContest.FastestFingerContestData;
import com.oustme.oustsdk.firebase.common.CplCollectionData;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.activity.NewNBCommentActivity;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.activity.NewNBPostDetailsActivity;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.activity.NewNBTopicDetailActivity;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.data.handlers.NewNBDataHandler;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBTopicData;
import com.oustme.oustsdk.model.response.common.ChildCplIdListModel;
import com.oustme.oustsdk.response.CheckModuleDistributedOrNot;
import com.oustme.oustsdk.response.MlCplDistributionResponse;
import com.oustme.oustsdk.response.catalogue.CatalogueResponse;
import com.oustme.oustsdk.response.common.BranchIoResponce;
import com.oustme.oustsdk.response.common.EncrypQuestions;
import com.oustme.oustsdk.response.common.GameType;
import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.response.common.QuestionType;
import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.room.dto.DTOCardColorScheme;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOCourseCardMedia;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.room.dto.DTOReadMore;
import com.oustme.oustsdk.room.dto.DTOSpecialFeed;
import com.oustme.oustsdk.room.dto.DTOUserFeedDetails;
import com.oustme.oustsdk.service.CourseNotificationReceiver;
import com.oustme.oustsdk.service.GCMType;
import com.oustme.oustsdk.survey_module.SurveyComponentActivity;
import com.oustme.oustsdk.survey_ui.SurveyDetailActivity;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 29/08/16.
 */
public class BranchTools {

    private static String studentKeyValue;
    private static FastestFingerContestData fastestFingerContestData;
    private static List<DTOSpecialFeed> dtoSpecialFeedList;
    private static FirebaseRefClass ffcDataRefClass;
    private static FirebaseRefClass ffcQDataRefClass;
    private static MutableLiveData<List<DTOSpecialFeed>> liveData;
    private static String notificationContestId;
    private static String avatarUrl;
    private static String userDisplayName;
    private static String ffcId;
    private static boolean childCplFound = false;
    private static final ArrayList<ChildCplIdListModel> childCplIdListModelArrayList = new ArrayList<>();
    private static final ArrayList<CplCollectionData> listCplCollectionData = new ArrayList<>();
    private static DTOUserFeedDetails dtoUserFeedDetails = new DTOUserFeedDetails();

    public static void gotLinkOnResume(Object referringParams) {
        try {
            JSONObject jsonObject = new JSONObject(referringParams.toString());
            if (jsonObject.getString("+clicked_branch_link").equalsIgnoreCase("true")) {
                OustPreferences.save("refereingParam", (referringParams.toString()));
                Gson gson = new Gson();
                BranchIoResponce branchIoResponce = gson.fromJson(referringParams.toString(), BranchIoResponce.class);
                if ((branchIoResponce.getAssessmentId() != null) && (!branchIoResponce.getAssessmentId().isEmpty())) {
                    gotoAssessment(branchIoResponce.getAssessmentId());
                } else if ((branchIoResponce.getCourseId() != null) && (!branchIoResponce.getCourseId().isEmpty())) {
                    gotoCoursePage(branchIoResponce.getCourseId());
                } else if ((branchIoResponce.getCollectionId() != null) && (!branchIoResponce.getCollectionId().isEmpty())) {
                    gotoCollectionPage(branchIoResponce.getCollectionId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public static void gotoAssessment(String assessmentId) {
        try {
            Gson gson = new GsonBuilder().create();
            ActiveUser activeUser = OustAppState.getInstance().getActiveUser();
            if (activeUser == null || activeUser.getStudentid() == null) {
                String activeUserGet = OustPreferences.get("userdata");
                activeUser = OustSdkTools.getActiveUserData(activeUserGet);
                OustAppState.getInstance().setActiveUser(activeUser);
            }
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
            if ((OustAppState.getInstance().getAssessmentFirebaseClassList() != null)
                    && (OustAppState.getInstance().getAssessmentFirebaseClassList().size() > 0)) {
                for (int i = 0; i < OustAppState.getInstance().getAssessmentFirebaseClassList().size(); i++) {
                    if ((assessmentId.equalsIgnoreCase(("" + OustAppState.getInstance().getAssessmentFirebaseClassList().get(i).getAsssessemntId())))) {
                        OustAppState.getInstance().setAssessmentFirebaseClass(OustAppState.getInstance().getAssessmentFirebaseClassList().get(i));
                        isAssessmentValid = true;
                    }
                }
                if (!isAssessmentValid) {
                    OustSdkTools.showToast(OustStrings.getString("assessment_no_longer"));
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

    public static void gotoAssessmentForWorkDiary(String assessmentId, String attachedCourseId) {
        try {
            Gson gson = new GsonBuilder().create();
            ActiveUser activeUser = OustAppState.getInstance().getActiveUser();
            if (activeUser == null || activeUser.getStudentid() == null) {
                String activeUserGet = OustPreferences.get("userdata");
                activeUser = OustSdkTools.getActiveUserData(activeUserGet);
                OustAppState.getInstance().setActiveUser(activeUser);
            }
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
            if ((OustAppState.getInstance().getAssessmentFirebaseClassList() != null)
                    && (OustAppState.getInstance().getAssessmentFirebaseClassList().size() > 0)) {
                for (int i = 0; i < OustAppState.getInstance().getAssessmentFirebaseClassList().size(); i++) {
                    if ((assessmentId.equalsIgnoreCase(("" + OustAppState.getInstance().getAssessmentFirebaseClassList().get(i).getAsssessemntId())))) {
                        OustAppState.getInstance().setAssessmentFirebaseClass(OustAppState.getInstance().getAssessmentFirebaseClassList().get(i));
                        isAssessmentValid = true;
                    }
                }
                if (!isAssessmentValid) {
                    OustSdkTools.showToast(OustStrings.getString("assessment_no_longer"));
                    return;
                }
            } else {
                intent.putExtra("assessmentId", assessmentId);
            }
            intent.putExtra("ActiveGame", gson.toJson(activeGame));
            intent.putExtra("isFromWorkDairy", true);
            if (attachedCourseId != null && !attachedCourseId.isEmpty()) {
                intent.putExtra("courseId", attachedCourseId);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            OustSdkApplication.getContext().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public static void gotoSurvey(String assessmentId, long mappedCourseId) {
        try {
            Gson gson = new Gson();
            Intent intent = new Intent(OustSdkApplication.getContext(), SurveyDetailActivity.class);
            if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_SURVEY_NEW_UI)) {
                intent = new Intent(OustSdkApplication.getContext(), SurveyComponentActivity.class);
            }
            ActiveUser activeUser = OustAppState.getInstance().getActiveUser();
            if (activeUser == null || activeUser.getStudentid() == null) {
                String activeUserGet = OustPreferences.get("userdata");
                activeUser = OustSdkTools.getActiveUserData(activeUserGet);
                OustAppState.getInstance().setActiveUser(activeUser);
            }
            ActiveGame activeGame = setGame(activeUser);
            intent.putExtra("ActiveGame", gson.toJson(activeGame));
            String id = "" + assessmentId;
            //id = id.replace("ASSESSMENT", "");
            intent.putExtra("assessmentId", id);
            intent.putExtra("courseId", mappedCourseId);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            OustSdkApplication.getContext().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public static ActiveGame setGame(ActiveUser activeUser) {
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

    public static void gotoCoursePage(String courseId) {
        try {
            Intent intent = new Intent(OustSdkApplication.getContext(), CourseDetailScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("learningId", courseId);
            OustSdkApplication.getContext().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public static void readDataFromFirebaseForNB(final String nb_Id, String commentId, String noticeBoardId, String replyId, String type) {
        Log.d("TAG", "readDataFromFirebaseForNB: " + nb_Id);
        try {
            String message = "";
            if (type.equalsIgnoreCase(GCMType.NOTICE_BOARD_DISTRIBUTION.name())) {
                message = "/noticeBoard/noticeBoard" + nb_Id;
            } else if (type.equalsIgnoreCase(GCMType.NOTICEBOARD_POST.name())) {
                message = "/noticeBoard/noticeBoard" + noticeBoardId;
            } else if (type.equalsIgnoreCase(GCMType.NOTICEBOARD_COMMENT.name())) {
                message = "/noticeBoard/noticeBoard" + noticeBoardId;
            } else if (type.equalsIgnoreCase(GCMType.NOTICEBOARD_REPLY.name())) {
                message = "/noticeBoard/noticeBoard" + noticeBoardId;
            }

            ValueEventListener myNBListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue() != null) {
                            //Object o1 = dataSnapshot.getValue();
                            Log.d("TAG", "noticeboard data: " + dataSnapshot.getValue());
                            Map<String, Object> lpMainMap = (Map<String, Object>) dataSnapshot.getValue();
                            NewNBTopicData nbTopicData = new NewNBTopicData();

                            if (lpMainMap.containsKey("nbId")) {
                                long id = OustSdkTools.convertToLong(lpMainMap.get("nbId"));
                                nbTopicData.setId(id);
                                nbTopicData.setExtraNBData(nbTopicData, lpMainMap);
                            }

                            gotoNBPage(nbTopicData, nb_Id, commentId, noticeBoardId, replyId, type);

                        } else {
                            Log.d("TAG", "noticeboard not found: ");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("TAG", "noticeboard onCancelled: ");
                }
            };
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(myNBListener);
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private static void gotoNBPage(final NewNBTopicData nbTopicData, String nb_Id, String commentId, String noticeBoardId, String replyId, String type) {
        try {
            if (type.equalsIgnoreCase(GCMType.NOTICE_BOARD_DISTRIBUTION.name())) {
                NewNBDataHandler.getInstance().setNbTopicData(nbTopicData);
                Intent intent = new Intent(OustSdkApplication.getContext(), NewNBTopicDetailActivity.class);
                intent.putExtra("nbId", Long.parseLong(nb_Id));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                OustSdkApplication.getContext().startActivity(intent);
            } else if (type.equalsIgnoreCase(GCMType.NOTICEBOARD_POST.name())) {
                Intent intent = new Intent(OustSdkApplication.getContext(), NewNBPostDetailsActivity.class);
                intent.putExtra("nbId", Long.parseLong(noticeBoardId));
                intent.putExtra("postId", Long.parseLong(nb_Id));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                OustSdkApplication.getContext().startActivity(intent);
            } else if (type.equalsIgnoreCase(GCMType.NOTICEBOARD_COMMENT.name())) {
                Intent intent = new Intent(OustSdkApplication.getContext(), NewNBPostDetailsActivity.class);
                intent.putExtra("nbId", Long.parseLong(noticeBoardId));
                intent.putExtra("postId", Long.parseLong(nb_Id));
                intent.putExtra("commentId", Long.parseLong(commentId));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                OustSdkApplication.getContext().startActivity(intent);
            } else if (type.equalsIgnoreCase(GCMType.NOTICEBOARD_REPLY.name())) {
                Intent intent = new Intent(OustSdkApplication.getContext(), NewNBCommentActivity.class);
                intent.putExtra("nbId", Long.parseLong(noticeBoardId));
                intent.putExtra("postId", Long.parseLong(nb_Id));
                intent.putExtra("commentId", Long.parseLong(commentId));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                OustSdkApplication.getContext().startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public static void gotoFeedPage(String feedId, ActiveUser activeUser) {
        String userFeedsDetails = OustSdkApplication.getContext().getResources().getString(R.string.userFeedDetails);
        userFeedsDetails = userFeedsDetails.replace("{feedId}", feedId);
        userFeedsDetails = userFeedsDetails.replace("{userId}", activeUser.getStudentid());
        try {
            userFeedsDetails = HttpManager.getAbsoluteUrl(userFeedsDetails);
            ApiCallUtils.doNetworkCall(Request.Method.GET, userFeedsDetails, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.d("TAG", "onResponse: user feeds:" + response.toString());
                        Gson gson = new Gson();
                        dtoUserFeedDetails = gson.fromJson(response.toString(), DTOUserFeedDetails.class);
                        getFeedsDataFromFirebase(feedId, activeUser, dtoUserFeedDetails);

                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                        OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.feed_removed_not_assigned));
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.feed_removed_not_assigned));
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private static void getFeedsDataFromFirebase(String feedId, ActiveUser activeUser, DTOUserFeedDetails feedNode) {
        try {
            String studentKey;
            if (activeUser == null) {
                activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
            }
            studentKey = activeUser.getStudentKey();
            String message = "/userFeed/" + studentKey + "/feed" + feedId;
            Log.e("TAG", "gotoUsersFeedsPage: " + message);
            Query query = OustFirebaseTools.getRootRef().child(message);
            ActiveUser finalActiveUser = activeUser;
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        HashMap<String, Object> userFeedNode = (HashMap<String, Object>) dataSnapshot.getValue();
                        if (userFeedNode != null) {
                            boolean feedCoinsAdded = false;

                            if (userFeedNode.get("feedCoinsAdded") != null) {
                                feedCoinsAdded = (boolean) userFeedNode.get("feedCoinsAdded");
                            }
                            if (userFeedNode.get("feedExpiry") != null) {
                                dtoUserFeedDetails.getFeedDetails().setFeedExpiry(String.valueOf(OustSdkTools.convertToLong(userFeedNode.get("feedExpiry"))));
                            }
                            if (userFeedNode.get("timeStamp") != null) {
                                dtoUserFeedDetails.getFeedDetails().setTimeStamp(OustSdkTools.newConvertToLong(userFeedNode.get("timeStamp")));
                            }
                            if (userFeedNode.get("isLiked") != null) {
                                dtoUserFeedDetails.getFeedDetails().setLiked((boolean) userFeedNode.get("isLiked"));
                            } else {
                                dtoUserFeedDetails.getFeedDetails().setLiked(false);
                            }
                            if (userFeedNode.get("isClicked") != null) {
                                dtoUserFeedDetails.getFeedDetails().setClicked((boolean) userFeedNode.get("isClicked"));
                            } else {
                                dtoUserFeedDetails.getFeedDetails().setClicked(false);
                            }
                            if (userFeedNode.get("isCommented") != null) {
                                dtoUserFeedDetails.getFeedDetails().setCommented((boolean) userFeedNode.get("isCommented"));
                            } else {
                                dtoUserFeedDetails.getFeedDetails().setCommented(false);
                            }
                            if (userFeedNode.get("isFeedViewed") != null) {
                                dtoUserFeedDetails.getFeedDetails().setFeedViewed((boolean) userFeedNode.get("isFeedViewed"));
                            } else {
                                dtoUserFeedDetails.getFeedDetails().setFeedViewed(false);
                            }
                            if (userFeedNode.get("timeStamp") != null) {
                                dtoUserFeedDetails.getFeedDetails().setTimeStamp(OustSdkTools.newConvertToLong(userFeedNode.get("timeStamp")));
                            } else {
                                dtoUserFeedDetails.getFeedDetails().setTimeStamp(0);
                            }
                            if (userFeedNode.get("feedCoinsAdded") != null) {
                                dtoUserFeedDetails.getFeedDetails().setFeedCoinsAdded((boolean) userFeedNode.get("feedCoinsAdded"));
                            } else {
                                dtoUserFeedDetails.getFeedDetails().setFeedCoinsAdded(false);
                            }

                            try {
                                String feedDetailsMes = "/feeds/feed" + dtoUserFeedDetails.getFeedDetails().getFeedId();
                                boolean finalFeedCoinsAdded = feedCoinsAdded;
                                ValueEventListener feedsNodeData = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        final Map<String, Object> feedsNodeDataVal = (Map<String, Object>) snapshot.getValue();
                                        if (feedsNodeDataVal != null) {
                                            if (feedsNodeDataVal.get("numLikes") != null) {
                                                dtoUserFeedDetails.getFeedDetails().setNumLikes(Math.toIntExact(OustSdkTools.newConvertToLong(feedsNodeDataVal.get("numLikes"))));
                                            } else {
                                                dtoUserFeedDetails.getFeedDetails().setNumLikes(0);
                                            }
                                            if (feedsNodeDataVal.get("numComments") != null) {
                                                dtoUserFeedDetails.getFeedDetails().setNumComments(Math.toIntExact(OustSdkTools.newConvertToLong(feedsNodeDataVal.get("numComments"))));
                                            } else {
                                                dtoUserFeedDetails.getFeedDetails().setNumComments(0);
                                            }
                                            if (feedsNodeDataVal.get("numShares") != null) {
                                                dtoUserFeedDetails.getFeedDetails().setNumShares(Math.toIntExact(OustSdkTools.newConvertToLong(feedsNodeDataVal.get("numShares"))));
                                            } else {
                                                dtoUserFeedDetails.getFeedDetails().setNumShares(0);
                                            }
                                            if (feedsNodeDataVal.get("shareable") != null) {
                                                dtoUserFeedDetails.getFeedDetails().setShareable((boolean) feedsNodeDataVal.get("shareable"));
                                            } else {
                                                dtoUserFeedDetails.getFeedDetails().setShareable(false);
                                            }
                                            if (dtoUserFeedDetails.getFeedDetails().getCardInfo() != null) {
                                                dtoUserFeedDetails.getFeedDetails().setCourseCardClass(BranchTools.getCardFromMap(dtoUserFeedDetails.getFeedDetails().getCardInfo()));
                                            }
                                            gotoUsersFeedsPage(dtoUserFeedDetails.getFeedDetails(), finalActiveUser, finalFeedCoinsAdded);
                                        } else {
                                            OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_data_is_or_not));
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e("TAG", "onCancelled:: --> " + error.getMessage());
                                        OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_data_is_or_not));
                                    }
                                };
                                DatabaseReference newsFeeds = OustFirebaseTools.getRootRef().child(feedDetailsMes);
                                Query query = newsFeeds.orderByChild("timeStamp");
                                query.keepSynced(true);
                                query.addListenerForSingleValueEvent(feedsNodeData);
                                OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(feedsNodeData, feedDetailsMes));
                            } catch (Exception e) {
                                OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_data_is_or_not));
                                e.printStackTrace();
                                OustSdkTools.sendSentryException(e);
                            }
                        }
                    } else {
                        OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.feed_removed_not_assigned));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    updateFeedViewed(dtoUserFeedDetails.getFeedDetails(), studentKey);
                    feedRewardUpdate(dtoUserFeedDetails.getFeedDetails());
                    Intent feedIntent = new Intent(OustSdkApplication.getContext(), GeneralFeedDetailScreen.class);
                    feedIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Bundle feedBundle = new Bundle();
                    feedBundle.putParcelable("Feed", feedNode);
                    feedBundle.putBoolean("FeedComment", false);
                    feedBundle.putBoolean("FeedAttach", false);
                    feedBundle.putLong("timeStamp", dtoUserFeedDetails.getFeedDetails().getTimeStamp());
                    if (dtoUserFeedDetails.getFeedDetails().getCourseCardClass() != null) {
                        feedBundle.putString("CardData", new Gson().toJson(dtoUserFeedDetails.getFeedDetails().getCourseCardClass()));
                    }
                    feedIntent.putExtra("feedType", dtoUserFeedDetails.getFeedDetails().getType());
                    feedIntent.putExtras(feedBundle);
                    OustSdkApplication.getContext().startActivity(feedIntent);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private static void gotoUsersFeedsPage(DTOUserFeedDetails.FeedDetails feedNode, ActiveUser activeUser, boolean feedCoinsAdded) {
        try {
            if (feedNode.getType() != null) {
                String type = feedNode.getType();
                feedNode.setFeedCoinsAdded(feedCoinsAdded);
                if (type != null) {
                    if (type.equalsIgnoreCase(SURVEY.toString())) {
                        updateFeedViewed(feedNode, activeUser.getStudentKey());
                        feedRewardUpdate(feedNode);
                        if (feedNode.getDeepLinkInfo() != null) {
                            if (feedNode.getDeepLinkInfo().getAssessmentId() != 0) {
                                gotoSurvey(feedNode.getDeepLinkInfo().getAssessmentId() + "", 0);
                            }
                        }
                    } else if (type.equalsIgnoreCase(ASSESSMENT_PLAY.toString())) {
                        updateFeedViewed(feedNode, activeUser.getStudentKey());
                        feedRewardUpdate(feedNode);
                        if (feedNode.getDeepLinkInfo() != null) {
                            if (feedNode.getDeepLinkInfo().getDistributedId() != 0) {
                                checkModuleDistributionOrNot(activeUser, feedNode.getDeepLinkInfo().getDistributedId() + "", "ASSESSMENT");
                            } else if (feedNode.getDeepLinkInfo().getAssessmentId() != 0) {
                                checkModuleDistributionOrNot(activeUser, feedNode.getDeepLinkInfo().getAssessmentId() + "", "ASSESSMENT");
                            } else {
                                OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_Assessment_distribute_or_not));
                            }
                        } else {
                            OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_Assessment_distribute_or_not));
                        }
                    } else if (type.equalsIgnoreCase(COURSE_UPDATE.toString())) {
                        updateFeedViewed(feedNode, activeUser.getStudentKey());
                        feedRewardUpdate(feedNode);
                        if (feedNode.getDeepLinkInfo() != null) {
                            if (feedNode.getDeepLinkInfo().getDistributedId() != 0) {
                                checkModuleDistributionOrNot(activeUser, feedNode.getDeepLinkInfo().getDistributedId() + "", "COURSE");
                            } else if (feedNode.getDeepLinkInfo().getCourseId() != 0) {
                                checkModuleDistributionOrNot(activeUser, feedNode.getDeepLinkInfo().getCourseId() + "", "COURSE");
                            } else {
                                OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_course_distribute_or_not));
                            }
                        } else {
                            OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_course_distribute_or_not));
                        }
                    } else if (type.equalsIgnoreCase("COURSE_CARD_L")) {
                        updateFeedViewed(feedNode, activeUser.getStudentKey());
                        feedRewardUpdate(feedNode);
                        if (feedNode.getCardInfo() != null && feedNode.getCardInfo().getCardType().equalsIgnoreCase("SCORM")) {
                            try {
                                Intent intent = new Intent(OustSdkApplication.getContext(), IntroScormCardActivity.class);
                                Bundle bundle = new Bundle();
                                Gson gson = new Gson();
                                String courseDataStr = gson.toJson(dtoUserFeedDetails.getFeedDetails().getCourseCardClass());
                                OustStaticVariableHandling.getInstance().setCourseDataStr(courseDataStr);
                                intent.putExtras(bundle);
                                intent.putExtra("type", "card");
                                intent.putExtra("Feed", feedNode);
                                intent.putExtra("timeStamp", feedNode.getTimeStamp());
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                OustSdkApplication.getContext().startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                                OustSdkTools.sendSentryException(e);
                            }
                        } else if (feedNode.getCardInfo().getCardMediaList() != null && feedNode.getCardInfo().getCardMediaList().size() != 0) {
                            if (feedNode.getCardInfo().getCardMediaList().get(0).getMediaType().equalsIgnoreCase("IMAGE") || feedNode.getCardInfo().getCardMediaList().get(0).getMediaType().equalsIgnoreCase("GIF") || feedNode.getCardInfo().getCardMediaList().get(0).getMediaType().equalsIgnoreCase("AUDIO")) {
                                try {
                                    Intent feedIntent = new Intent(OustSdkApplication.getContext(), ImageFeedDetailScreen.class);
                                    Bundle feedBundle = new Bundle();
                                    feedBundle.putParcelable("Feed", feedNode);
                                    if (feedNode.getCourseCardClass() != null) {
                                        feedBundle.putString("CardData", new Gson().toJson(feedNode.getCourseCardClass()));
                                    }
                                    feedBundle.putSerializable("ActiveUser", activeUser);
                                    feedBundle.putBoolean("FeedComment", false);
                                    feedBundle.putBoolean("FeedAttach", false);
                                    feedBundle.putLong("timeStamp", feedNode.getTimeStamp());
                                    feedBundle.putString("feedType", type);
                                    feedIntent.putExtras(feedBundle);
                                    feedIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    OustSdkApplication.getContext().startActivity(feedIntent);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    OustSdkTools.sendSentryException(e);
                                }
                            } else {
                                if (feedNode.getCardInfo().getCardMediaList().get(0).getMediaPrivacy() != null && feedNode.getCardInfo().getCardMediaList().get(0).getMediaPrivacy().equalsIgnoreCase("PUBLIC")) {
                                    Intent feedIntent = new Intent(OustSdkApplication.getContext(), PublicVideoFeedCardScreen.class);
                                    Bundle feedBundle = new Bundle();
                                    feedBundle.putParcelable("Feed", feedNode);
                                    feedBundle.putString("CardData", new Gson().toJson(feedNode.getCourseCardClass()));
                                    feedBundle.putSerializable("ActiveUser", activeUser);
                                    feedBundle.putLong("timeStamp", feedNode.getTimeStamp());
                                    feedBundle.putBoolean("FeedComment", false);
                                    feedIntent.putExtras(feedBundle);
                                    feedIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    OustSdkApplication.getContext().startActivity(feedIntent);
                                } else {
                                    try {
                                        Intent feedIntent = new Intent(OustSdkApplication.getContext(), VideoCardDetailScreen.class);
                                        Bundle feedBundle = new Bundle();
                                        feedBundle.putParcelable("Feed", feedNode);
                                        feedBundle.putString("CardData", new Gson().toJson(feedNode.getCourseCardClass()));
                                        feedBundle.putLong("timeStamp", feedNode.getTimeStamp());
                                        feedBundle.putSerializable("ActiveUser", activeUser);
                                        feedBundle.putBoolean("FeedComment", false);
                                        feedIntent.putExtras(feedBundle);
                                        feedIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        OustSdkApplication.getContext().startActivity(feedIntent);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        OustSdkTools.sendSentryException(e);
                                    }
                                }
                            }
                        } else if (feedNode.getCardInfo() != null && feedNode.getCardInfo().getClCode().equalsIgnoreCase("CL_T")) {
                            Intent feedIntent = new Intent(OustSdkApplication.getContext(), GeneralFeedDetailScreen.class);
                            Bundle feedBundle = new Bundle();
                            feedBundle.putParcelable("Feed", feedNode);
                            feedBundle.putBoolean("FeedComment", false);
                            feedBundle.putLong("timeStamp", feedNode.getTimeStamp());
                            feedBundle.putBoolean("FeedAttach", false);
                            if (feedNode.getCourseCardClass() != null) {
                                feedBundle.putString("CardData", new Gson().toJson(feedNode.getCourseCardClass()));
                            }
                            feedIntent.putExtra("feedType", type);
                            feedBundle.putSerializable("ActiveUser", activeUser);
                            feedIntent.putExtras(feedBundle);
                            feedIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            OustSdkApplication.getContext().startActivity(feedIntent);
                        }
                    } else if (feedNode.getType().equalsIgnoreCase("GENERAL")) {
                        updateFeedViewed(feedNode, activeUser.getStudentKey());
                        feedRewardUpdate(feedNode);
                        Intent feedIntent = new Intent(OustSdkApplication.getContext(), GeneralFeedDetailScreen.class);
                        Bundle feedBundle = new Bundle();
                        feedBundle.putParcelable("Feed", feedNode);
                        feedBundle.putBoolean("FeedComment", false);
                        feedBundle.putLong("timeStamp", feedNode.getTimeStamp());
                        feedBundle.putBoolean("FeedAttach", false);
                        if (feedNode.getCourseCardClass() != null) {
                            feedBundle.putString("CardData", new Gson().toJson(feedNode.getCourseCardClass()));
                        }
                        feedIntent.putExtra("feedType", type);
                        feedBundle.putSerializable("ActiveUser", activeUser);
                        feedIntent.putExtras(feedBundle);
                        feedIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        OustSdkApplication.getContext().startActivity(feedIntent);
                    } else {
                        updateFeedViewed(feedNode, activeUser.getStudentKey());
                        feedRewardUpdate(feedNode);
                        Intent feedIntent = new Intent(OustSdkApplication.getContext(), GeneralFeedDetailScreen.class);
                        feedIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Bundle feedBundle = new Bundle();
                        feedBundle.putParcelable("Feed", feedNode);
                        feedBundle.putBoolean("FeedComment", false);
                        feedBundle.putBoolean("FeedAttach", false);
                        feedBundle.putLong("timeStamp", feedNode.getTimeStamp());
                        if (feedNode.getCourseCardClass() != null) {
                            feedBundle.putString("CardData", new Gson().toJson(feedNode.getCourseCardClass()));
                        }
                        feedIntent.putExtra("feedType", feedNode.getType());
                        feedIntent.putExtras(feedBundle);
                        OustSdkApplication.getContext().startActivity(feedIntent);
                    }
                }
            } else {
                Intent feedIntent = new Intent(OustSdkApplication.getContext(), GeneralFeedDetailScreen.class);
                feedIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle feedBundle = new Bundle();
                feedBundle.putParcelable("Feed", feedNode);
                feedBundle.putBoolean("FeedComment", false);
                feedBundle.putBoolean("FeedAttach", false);
                feedBundle.putLong("timeStamp", feedNode.getTimeStamp());
                if (feedNode.getCourseCardClass() != null) {
                    feedBundle.putString("CardData", new Gson().toJson(feedNode.getCourseCardClass()));
                }
                feedIntent.putExtra("feedType", feedNode.getType());
                feedIntent.putExtras(feedBundle);
                OustSdkApplication.getContext().startActivity(feedIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private static void updateFeedViewed(DTOUserFeedDetails.FeedDetails mFeed, String studentKey) {
        try {
            if (!mFeed.isClicked()) {
                long feedId = mFeed.getFeedId();
                String message1 = "/userFeed/" + studentKey + "/feed" + feedId + "/" + AppConstants.StringConstants.IS_FEED_CLICKED;
                OustFirebaseTools.getRootRef().child(message1).setValue(true);
                OustFirebaseTools.getRootRef().child(message1).keepSynced(true);
                DatabaseReference firebase = OustFirebaseTools.getRootRef().child(message1);
                firebase.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                        currentData.setValue(true);
                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(DatabaseError DatabaseError, boolean b, DataSnapshot dataSnapshot) {
                        if (DatabaseError != null) {
                            Log.e("", "Firebase counter increment failed. New Count:{}" + dataSnapshot);
                        } else {
                            Log.e("", "Firebase counter increment succeeded.");
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private static void feedRewardUpdate(DTOUserFeedDetails.FeedDetails newFeed) {
        try {
            if (newFeed == null) {
                Log.d("TAG", "feedUpdated: reward newfeed null");
                return;
            }

            if (newFeed.isFeedCoinsAdded()) {
                Log.d("TAG", "feedUpdated: reward coins already added");
                return;
            }

            if (newFeed.getFeedCoins() < 1) {
                Log.d("TAG", "feedUpdated: reward feedcoins is less than zero");
                return;
            }
            Log.d("TAG", "feedRewardUpdate: coins:" + newFeed.getFeedCoins());

            if (newFeed.getFeedId() > 0 && newFeed.getFeedCoins() > 0 && !newFeed.isFeedCoinsAdded()) {
                Intent intent = new Intent(OustSdkApplication.getContext(), FeedUpdatingServices.class);
                intent.putExtra("feedId", newFeed.getFeedId());
                intent.putExtra("feedCoins", newFeed.getFeedCoins());
                intent.putExtra("feedCoinsUpdate", true);
                OustSdkApplication.getContext().startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public static void gotoCollectionPage(String collectionId) {
        try {
            Intent intent = new Intent(OustSdkApplication.getContext(), LessonsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("collectionId", ("" + collectionId));
            OustSdkApplication.getContext().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public static void gotoNoticeBoardPage(String nb_Id, String studentKey, String commentId, String noticeBoardId, String replyId, String type) {
        try {
            String message = "";
            if (type.equalsIgnoreCase(GCMType.NOTICE_BOARD_DISTRIBUTION.name())) {
                message = "/landingPage/" + studentKey + "/noticeBoard/noticeBoard" + nb_Id;
            } else if (type.equalsIgnoreCase(GCMType.NOTICEBOARD_POST.name())) {
                message = "/landingPage/" + studentKey + "/noticeBoard/noticeBoard" + noticeBoardId;
            } else if (type.equalsIgnoreCase(GCMType.NOTICEBOARD_COMMENT.name())) {
                message = "/landingPage/" + studentKey + "/noticeBoard/noticeBoard" + noticeBoardId;
            } else if (type.equalsIgnoreCase(GCMType.NOTICEBOARD_REPLY.name())) {
                message = "/landingPage/" + studentKey + "/noticeBoard/noticeBoard" + noticeBoardId;
            }
            Log.d("TAG", "gotoNotificationPage: " + null);
            ValueEventListener myNBListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue() != null) {
                            String noticeBoardKey = (String) dataSnapshot.getKey();
                            if (noticeBoardKey != null && !noticeBoardKey.isEmpty()) {
                                String key;
                                if (type.equalsIgnoreCase(GCMType.NOTICE_BOARD_DISTRIBUTION.name())) {
                                    key = "noticeBoard" + nb_Id;
                                } else {
                                    key = "noticeBoard" + noticeBoardId;
                                }
                                if (noticeBoardKey.equalsIgnoreCase(key)) {
                                    Map<String, Object> lpMainMap = (Map<String, Object>) dataSnapshot.getValue();
                                    NewNBTopicData nbTopicData = new NewNBTopicData();

                                    if (lpMainMap.containsKey("nbId")) {
                                        long id = OustSdkTools.convertToLong(lpMainMap.get("nbId"));
                                        nbTopicData.setId(id);
                                        nbTopicData.setExtraNBData(nbTopicData, lpMainMap);
                                    }

                                    readDataFromFirebaseForNB(nb_Id, commentId, noticeBoardId, replyId, type);

                                } else {
                                    OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_noticeBoard_distribute_or_not));
                                }
                            }
                        } else {
                            OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_noticeBoard_distribute_or_not));
                            Log.d("TAG", "noticeboard not found: ");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("TAG", "noticeboard onCancelled: ");
                }
            };

            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(myNBListener);
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public static void gotoCataloguePage(String courseId, HashMap<String, String> myDeskInfoMap, boolean hasDeskData) {
        try {
            if (OustSdkTools.checkInternetStatus() && myDeskInfoMap != null) {
                // Layout 3
                Intent intent = new Intent(OustSdkApplication.getContext(), CatalogDetailListActivity.class);
                // Layout 4
                if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_CATALOGUE_NEW_UI)) {
                    intent = new Intent(OustSdkApplication.getContext(), CatalogueModuleListActivity.class);
                }
                intent.putExtra("hasDeskData", false);
                intent.putExtra("deskDataMap", myDeskInfoMap);
                intent.putExtra("overAllCatalogue", true);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                OustSdkApplication.getContext().startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public static void getUserFFContest(String studentKey, String avatar, String name, String courseId) {
        try {
            studentKeyValue = studentKey;
            avatarUrl = avatar;
            userDisplayName = name;
            ffcId = courseId;
            final String message = "/landingPage/" + studentKey + "/f3c";
            Log.d("TAG", "getUserFFContest: " + message);
            ValueEventListener myFFCListener = new ValueEventListener() {
                @SuppressLint("ShortAlarm")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {

                        if (dataSnapshot.getValue() != null) {
                            Object o1 = dataSnapshot.getValue();
                            if (o1.getClass().equals(HashMap.class)) {
                                Map<String, Object> ffcMap = (Map<String, Object>) dataSnapshot.getValue();
                                if (ffcMap.get("elementId") != null) {
                                    long contestId = (long) ffcMap.get("elementId");
                                    if (ffcId.equalsIgnoreCase(String.valueOf(contestId))) {
                                        if (fastestFingerContestData == null) {
                                            fastestFingerContestData = new FastestFingerContestData();
                                        }
                                        int lastContestId = OustPreferences.getSavedInt("lastContestTime");
                                        if ((lastContestId > 0) && (lastContestId != contestId)) {
                                            OustPreferences.clear("contestScore");
                                        }
                                        OustPreferences.saveintVar("lastContestTime", (int) contestId);
                                        if (fastestFingerContestData.getFfcId() != contestId) {
                                            fastestFingerContestData = new FastestFingerContestData();
                                            fastestFingerContestData.setFfcId(contestId);
                                            OustStaticVariableHandling.getInstance().setContestOver(false);
                                            removeFFCDataListener();
                                            fetchFFCData(("" + fastestFingerContestData.getFfcId()));
                                            fetchQData(("" + fastestFingerContestData.getFfcId()));
                                        }

                                        if (ffcMap.get("enrolled") != null) {
                                            fastestFingerContestData.setEnrolled((boolean) ffcMap.get("enrolled"));
                                            ffcBannerStatus();
                                        }

                                        if (OustPreferences.getAppInstallVariable("sendPushNotifications")) {
                                            AlarmManager manager = (AlarmManager) OustSdkApplication.getContext().getSystemService(Context.ALARM_SERVICE);
                                            Intent f3cAlarmIntent = new Intent(OustSdkApplication.getContext(), CourseNotificationReceiver.class);
                                            PendingIntent f3cPendingIntent;
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                                f3cPendingIntent = PendingIntent.getService(OustSdkApplication.getContext(), 0, f3cAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
                                            } else {
                                                f3cPendingIntent = PendingIntent.getService(OustSdkApplication.getContext(), 0, f3cAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                            }
                                            Calendar f3cCalendar = Calendar.getInstance();
                                            f3cCalendar.setTimeInMillis(System.currentTimeMillis());
                                            manager.setRepeating(AlarmManager.RTC_WAKEUP, f3cCalendar.getTimeInMillis(), 30 * 1000, f3cPendingIntent);
                                        }
                                    } else {
                                        OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_contest_distribute_or_not));
                                    }
                                }
                            }
                        } else {
                            OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_contest_distribute_or_not));
                            OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_FFC_ON_LANDING_PAGE, false);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            };


            DatabaseReference newsfeedRef = OustFirebaseTools.getRootRef().child(message);
            newsfeedRef.keepSynced(true);
            newsfeedRef.addValueEventListener(myFFCListener);
            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(myFFCListener, message));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private static void removeFFCDataListener() {
        try {
            if (ffcDataRefClass != null) {
                OustFirebaseTools.getRootRef().child(ffcDataRefClass.getFirebasePath()).removeEventListener(ffcDataRefClass.getEventListener());
            }
            if (ffcQDataRefClass != null) {
                OustFirebaseTools.getRootRef().child(ffcQDataRefClass.getFirebasePath()).removeEventListener(ffcQDataRefClass.getEventListener());
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private static void fetchFFCData(String ffcId) {
        try {
            final String message = "/f3cData/f3c" + ffcId;
            Log.d("TAG", "fetctFFCData: " + message);
            ValueEventListener ffcDataListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    extractFFCData(dataSnapshot);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            };
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
            OustFirebaseTools.getRootRef().child(message).addValueEventListener(ffcDataListener);
            ffcDataRefClass = new FirebaseRefClass(ffcDataListener, message);
            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(ffcDataListener, message));
            getFFCEnrolldedUsersCount(ffcId);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private static void fetchQData(String ffcId) {
        try {
            final String message = "/f3cQData/f3c" + ffcId;
            ValueEventListener ffcDataListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        final Map<String, Object> ffcDataMap = (Map<String, Object>) dataSnapshot.getValue();
                        if (null != ffcDataMap) {
                            List<BasicQuestionClass> basicQuestionClassList = new ArrayList<>();
                            if (ffcDataMap.get("questions") != null) {
                                Map<String, Object> questionMap = (Map<String, Object>) ffcDataMap.get("questions");
                                if (questionMap != null) {
                                    for (String key : questionMap.keySet()) {
                                        Map<String, Object> questionSubMap = (Map<String, Object>) questionMap.get(key);
                                        if (questionSubMap != null) {
                                            BasicQuestionClass basicQuestionClass = new BasicQuestionClass();
                                            if (questionSubMap.get("qId") != null) {
                                                basicQuestionClass.setqId((long) questionSubMap.get("qId"));
                                            }
                                            if (questionSubMap.get("sequence") != null) {
                                                basicQuestionClass.setSequence((long) questionSubMap.get("sequence"));
                                            }
                                            basicQuestionClassList.add(basicQuestionClass);
                                        }
                                    }
                                }
                                Collections.sort(basicQuestionClassList, questionSorter);
                            }
                            List<BasicQuestionClass> basicWarmUpQuestionClassList = new ArrayList<>();
                            if (ffcDataMap.get("warmupQuestions") != null) {
                                Map<String, Object> questionMap = (Map<String, Object>) ffcDataMap.get("warmupQuestions");
                                if (questionMap != null) {
                                    for (String key : questionMap.keySet()) {
                                        Map<String, Object> questionSubMap = (Map<String, Object>) questionMap.get(key);
                                        if (questionSubMap != null) {
                                            BasicQuestionClass basicQuestionClass = new BasicQuestionClass();
                                            if (questionSubMap.get("qId") != null) {
                                                basicQuestionClass.setqId((long) questionSubMap.get("qId"));
                                            }
                                            if (questionSubMap.get("sequence") != null) {
                                                basicQuestionClass.setSequence((long) questionSubMap.get("sequence"));
                                            }
                                            basicWarmUpQuestionClassList.add(basicQuestionClass);
                                        }
                                    }
                                }
                                Collections.sort(basicWarmUpQuestionClassList, questionSorter);
                            }
                            long updateChecksum = 0;
                            if (ffcDataMap.get("updateChecksum") != null) {
                                updateChecksum = (long) ffcDataMap.get("updateChecksum");
                            }
                            boolean update = true;
                            if ((updateChecksum > 0) && (OustPreferences.getTimeForNotification("updateChecksum") > 0) && (updateChecksum == OustPreferences.getTimeForNotification("updateChecksum"))) {
                                update = false;
                            }
                            List<String> qList = new ArrayList<>();
                            for (int i = 0; i < basicQuestionClassList.size(); i++) {
                                if (basicQuestionClassList.get(i).getqId() > 0) {
                                    if (update) {
                                        downloadQuestion(("" + basicQuestionClassList.get(i).getqId()), updateChecksum);
                                    }
                                    qList.add(("" + basicQuestionClassList.get(i).getqId()));
                                }
                            }
                            fastestFingerContestData.setqIds(qList);
                            List<String> warmUpQList = new ArrayList<>();
                            for (int i = 0; i < basicWarmUpQuestionClassList.size(); i++) {
                                if (basicWarmUpQuestionClassList.get(i).getqId() > 0) {
                                    if (update) {
                                        downloadQuestion(("" + basicWarmUpQuestionClassList.get(i).getqId()), updateChecksum);
                                    }
                                    warmUpQList.add(("" + basicWarmUpQuestionClassList.get(i).getqId()));
                                }
                            }
                            fastestFingerContestData.setWarmupQList(warmUpQList);
                            if ((notificationContestId != null) && (!notificationContestId.isEmpty()) &&
                                    (notificationContestId.equalsIgnoreCase(("" + fastestFingerContestData.getFfcId())))) {
                                liveData.postValue(dtoSpecialFeedList);
                                notificationContestId = "";
                                DTOSpecialFeed dtoCPLSpecialFeed = new DTOSpecialFeed();
                                dtoCPLSpecialFeed.setFastestFingerContestData(fastestFingerContestData);
                                dtoSpecialFeedList.add(dtoCPLSpecialFeed);
                                Intent intent = new Intent(OustSdkApplication.getContext(), FFcontestStartActivity.class);
                                Gson gson = new Gson();
                                intent.putExtra("fastestFingerContestData", gson.toJson(fastestFingerContestData));
                                OustSdkApplication.getContext().startActivity(intent);
                            }
                            setContestNotificationData(ffcDataMap);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            };
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
            OustFirebaseTools.getRootRef().child(message).addValueEventListener(ffcDataListener);
            ffcQDataRefClass = new FirebaseRefClass(ffcDataListener, message);
            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(ffcDataListener, message));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public static Comparator<BasicQuestionClass> questionSorter = new Comparator<BasicQuestionClass>() {
        public int compare(BasicQuestionClass s1, BasicQuestionClass s2) {
            return ((int) s1.getSequence()) - ((int) s2.getSequence());
        }
    };

    private static void extractFFCData(DataSnapshot dataSnapshot) {
        try {
            if (dataSnapshot != null) {
                final Map<String, Object> ffcDataMap = (Map<String, Object>) dataSnapshot.getValue();
                if (null != ffcDataMap) {
                    try {
                        fastestFingerContestData = OustSdkTools.getFastestFingerContestData(fastestFingerContestData, ffcDataMap);
                        //setF3cBannerSize();
                        long bannerHideTimeNo = 1;
                        if (ffcDataMap.get("bannerHideTime") != null) {
                            bannerHideTimeNo = (long) ffcDataMap.get("bannerHideTime");
                        }
                        long bannerHideTime = (bannerHideTimeNo * (86400000));
                        if ((System.currentTimeMillis() - fastestFingerContestData.getStartTime()) > bannerHideTime) {
                        } else {
                            OustStaticVariableHandling.getInstance().setContestLive(true);
                        }
                        setContestNotificationData(ffcDataMap);
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private static void setContestNotificationData(Map<String, Object> ffcDataMap) {
        try {
            if (OustStaticVariableHandling.getInstance().isContestLive()) {
                String contestnotification_message = OustPreferences.get("contestnotification_message");
                Gson gson = new Gson();
                ContestNotificationMessage contestNotificationMessage;
                if ((contestnotification_message != null) && (!contestnotification_message.isEmpty())) {
                    contestNotificationMessage = gson.fromJson(contestnotification_message, ContestNotificationMessage.class);
                    if (contestNotificationMessage.getContestID() != fastestFingerContestData.getFfcId()) {
                        contestNotificationMessage = new ContestNotificationMessage();
                    }
                } else {
                    contestNotificationMessage = new ContestNotificationMessage();
                }
                contestNotificationMessage.setContestID((fastestFingerContestData.getFfcId()));
                contestNotificationMessage.setStartTime(fastestFingerContestData.getStartTime());
                contestNotificationMessage.setContestName(fastestFingerContestData.getName());
                contestNotificationMessage.setStudentId(studentKeyValue);
                contestNotificationMessage.setAvatar(avatarUrl);
                contestNotificationMessage.setDisplayName(userDisplayName);
                contestNotificationMessage.setJoinBanner(fastestFingerContestData.getJoinBanner());
                contestNotificationMessage.setPlayBanner(fastestFingerContestData.getPlayBanner());
                contestNotificationMessage.setRrBanner(fastestFingerContestData.getRrBanner());
                contestNotificationMessage.setRegistered(fastestFingerContestData.isEnrolled());
                if (ffcDataMap.get("greaterThan24") != null) {
                    Map<String, Object> subMap = (Map<String, Object>) ffcDataMap.get("greaterThan24");
                    if (subMap != null) {
                        if (subMap.get("frequency") != null) {
                            long frequency = (long) subMap.get("frequency");
                            if ((subMap.get("message") != null) && (frequency > 0)) {
                                contestNotificationMessage.setGreater24Time(86400 / frequency);
                                contestNotificationMessage.setGreater24Message((String) subMap.get("message"));
                            }
                        }
                    }
                }
                if (ffcDataMap.get("lessThan24") != null) {
                    Map<String, Object> subMap = (Map<String, Object>) ffcDataMap.get("lessThan24");
                    if (subMap != null) {
                        if (subMap.get("frequency") != null) {
                            long frequency = (long) subMap.get("frequency");
                            if ((subMap.get("message") != null) && (frequency > 0)) {
                                contestNotificationMessage.setGreatehourTime(86400 / frequency);
                                contestNotificationMessage.setGreatehourMessage((String) subMap.get("message"));
                            }
                        }
                    }
                }
                long lastMinute = 0;
                if (ffcDataMap.get("lastMinute") != null) {
                    Map<String, Object> subMap = (Map<String, Object>) ffcDataMap.get("lastMinute");
                    if (subMap != null) {
                        if (subMap.get("message") != null) {
                            contestNotificationMessage.setLastMinuteMessage((String) subMap.get("message"));
                        }
                        if (subMap.get("minutes") != null) {
                            contestNotificationMessage.setLastMinuteTime(((long) subMap.get("minutes") * 60));
                            lastMinute = ((long) subMap.get("minutes") * 60);
                        }
                    }
                }
                if (ffcDataMap.get("lessThanHour") != null) {
                    Map<String, Object> subMap = (Map<String, Object>) ffcDataMap.get("lessThanHour");
                    if (subMap != null) {
                        if (subMap.get("frequency") != null) {
                            long frequency = (long) subMap.get("frequency");
                            if ((subMap.get("message") != null) && (frequency > 0)) {
                                contestNotificationMessage.setLesshourTime((3600 - lastMinute) / frequency);
                                contestNotificationMessage.setLesshourMessage((String) subMap.get("message"));
                            }
                        }
                    }
                }
                if (ffcDataMap.get("LBReadyMessage") != null) {
                    contestNotificationMessage.setLBReadyMessage((String) ffcDataMap.get("LBReadyMessage"));
                }
                if (fastestFingerContestData.getqIds() != null) {
                    long totalContestTime = ((fastestFingerContestData.getQuestionTime() * fastestFingerContestData.getqIds().size()) +
                            (fastestFingerContestData.getRestTime() * (fastestFingerContestData.getqIds().size() - 1)));
                    contestNotificationMessage.setLeaderboardNotificationTime(((totalContestTime + fastestFingerContestData.getConstructingLBTime()) / 1000));
                    contestNotificationMessage.setTotalContestTime((totalContestTime / 1000));
                }

                if (ffcDataMap.get("contestStartMessage") != null) {
                    contestNotificationMessage.setContestStartMessage((String) ffcDataMap.get("contestStartMessage"));
                }
                String contestnotification_message1 = gson.toJson(contestNotificationMessage);
                OustPreferences.save("contestnotification_message", contestnotification_message1);
                ffcBannerStatus();
            } else {
                OustPreferences.clear("contestnotification_message");
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private static void getFFCEnrolldedUsersCount(String contestId) {
        try {
            final String path = "/f3cEnrolledUserCount/f3c" + contestId + "/participants";
            DatabaseReference databaseReference = OustFirebaseTools.getRootRef().child(path);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        fastestFingerContestData.setEnrolledCount((long) dataSnapshot.getValue());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("TAG", "onCancelled: Error:");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private static void ffcBannerStatus() {
        try {
            Log.d("TAG", "ffcBannerStatus isContestLive-: " + OustStaticVariableHandling.getInstance().isContestLive());
            Log.d("TAG", "ffcBannerStatus- isEnrolled: " + fastestFingerContestData.isEnrolled());
            if (OustStaticVariableHandling.getInstance().isContestLive()) {
                DTOSpecialFeed dtoCPLSpecialFeed = new DTOSpecialFeed();
                dtoCPLSpecialFeed.setFastestFingerContestData(fastestFingerContestData);
                dtoCPLSpecialFeed.setHeader(fastestFingerContestData.getName());
                if (fastestFingerContestData.getDescription() != null) {
                    dtoCPLSpecialFeed.setContent(fastestFingerContestData.getDescription());
                } else {
                    dtoCPLSpecialFeed.setContent("");
                }
                dtoCPLSpecialFeed.setType("FFF_CONTEXT");
                String contestnotification_message = OustPreferences.get("contestnotification_message");

                if ((fastestFingerContestData.isEnrolled())) {

                    if ((fastestFingerContestData.getPlayBanner() != null) && (!fastestFingerContestData.getPlayBanner().isEmpty())) {
                        dtoCPLSpecialFeed.setImageUrl(fastestFingerContestData.getPlayBanner());
                        saveData(dtoCPLSpecialFeed);
                    } else {
                        if ((fastestFingerContestData.getJoinBanner() != null) && (!fastestFingerContestData.getJoinBanner().isEmpty())) {
                            dtoCPLSpecialFeed.setImageUrl(fastestFingerContestData.getJoinBanner());
                            saveData(dtoCPLSpecialFeed);
                        }
                    }

                    if ((contestnotification_message != null) && (!contestnotification_message.isEmpty())) {
                        Gson gson = new Gson();
                        ContestNotificationMessage contestNotificationMessage = gson.fromJson(contestnotification_message, ContestNotificationMessage.class);
                        if (contestNotificationMessage != null) {
                            long currentTime = System.currentTimeMillis();
                            if (((contestNotificationMessage.getTotalContestTime() * 1000) + fastestFingerContestData.getStartTime()) < currentTime) {
                                if ((fastestFingerContestData.getRrBanner() != null) && (!fastestFingerContestData.getRrBanner().isEmpty())) {
                                    dtoCPLSpecialFeed.setImageUrl(fastestFingerContestData.getRrBanner());
                                    saveData(dtoCPLSpecialFeed);
                                }
                            }
                        }
                    }
                } else {
                    if ((fastestFingerContestData.getJoinBanner() != null) && (!fastestFingerContestData.getJoinBanner().isEmpty())) {
                        dtoCPLSpecialFeed.setImageUrl(fastestFingerContestData.getJoinBanner());
                        saveData(dtoCPLSpecialFeed);
                    }
                    if ((contestnotification_message != null) && (!contestnotification_message.isEmpty())) {
                        Gson gson = new Gson();
                        ContestNotificationMessage contestNotificationMessage = gson.fromJson(contestnotification_message, ContestNotificationMessage.class);
                        if (contestNotificationMessage != null) {
                            long currentTime = System.currentTimeMillis();
                            if (((contestNotificationMessage.getTotalContestTime() * 1000) + fastestFingerContestData.getStartTime()) < currentTime) {
                                if ((fastestFingerContestData.getRrBanner() != null) && (!fastestFingerContestData.getRrBanner().isEmpty())) {
                                    dtoCPLSpecialFeed.setImageUrl(fastestFingerContestData.getRrBanner());
                                    saveData(dtoCPLSpecialFeed);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private static void saveData(DTOSpecialFeed dtoCPLSpecialFeed) {
        try {
            if (dtoCPLSpecialFeed != null) {
                if (dtoSpecialFeedList != null && dtoSpecialFeedList.size() != 0) {
                    for (int i = 0; i < dtoSpecialFeedList.size(); i++) {
                        if (dtoSpecialFeedList.get(i).getType().equalsIgnoreCase("FFF_CONTEXT")) {
                            dtoSpecialFeedList.remove(dtoSpecialFeedList.get(i));
                        }
                    }
                }
                Intent intent4 = new Intent(OustSdkApplication.getContext(), FFcontestStartActivity.class);
                Gson gson = new Gson();
                intent4.putExtra("fastestFingerContestData", gson.toJson(dtoCPLSpecialFeed.getFastestFingerContestData()));
                intent4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                OustSdkApplication.getContext().startActivity(intent4);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private static void downloadQuestion(final String qId, final long updateChecksum) {
        try {
            final String message = "/questions/Q" + qId;
            ValueEventListener ffcDataListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Map<String, Object> questionMap = (Map<String, Object>) dataSnapshot.getValue();
                    EncrypQuestions encrypQuestions = new EncrypQuestions();
                    if (questionMap != null) {
                        if (questionMap.get("image") != null) {
                            encrypQuestions.setImage((String) questionMap.get("image"));
                        }
                        if (questionMap.get("encryptedQuestions") != null) {
                            encrypQuestions.setEncryptedQuestions((String) questionMap.get("encryptedQuestions"));
                        }
                        DTOQuestions questions = OustSdkTools.decryptQuestion(encrypQuestions, null);
                        try {
                            if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_A)) {
                                questions.setQuestionType(QuestionType.UPLOAD_AUDIO);
                            } else if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_I)) {
                                questions.setQuestionType(QuestionType.UPLOAD_IMAGE);
                            } else if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_V)) {
                                questions.setQuestionType(QuestionType.UPLOAD_VIDEO);
                            } else if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.LONG_ANSWER)) {
                                questions.setQuestionType(QuestionType.LONG_ANSWER);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                        OustSdkTools.databaseHandler.addToRealmQuestions(questions, true);
                        OustPreferences.saveTimeForNotification("updateChecksum", updateChecksum);

                        OustPreferences.saveTimeForNotification("updateChecksum", updateChecksum);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            };
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(ffcDataListener);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public static void getCatalog(long parseLong) {
        try {
            Intent intent = new Intent(OustSdkApplication.getContext(), CatalogueModuleListActivity.class);
            intent.putExtra("hasDeskData", false);
            intent.putExtra("overAllCatalogue", true);
            intent.putExtra("catalog_id", parseLong);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            OustSdkApplication.getContext().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private static void gotoAssessmentFormNotification(String assessmentId) {
        Gson gson = new GsonBuilder().create();
        ActiveUser activeUser = OustAppState.getInstance().getActiveUser();
        if (activeUser == null || activeUser.getStudentid() == null) {
            String activeUserGet = OustPreferences.get("userdata");
            activeUser = OustSdkTools.getActiveUserData(activeUserGet);
            OustAppState.getInstance().setActiveUser(activeUser);
        }
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
        if ((OustAppState.getInstance().getAssessmentFirebaseClassList() != null)
                && (OustAppState.getInstance().getAssessmentFirebaseClassList().size() > 0)) {
            for (int i = 0; i < OustAppState.getInstance().getAssessmentFirebaseClassList().size(); i++) {
                if ((assessmentId.equalsIgnoreCase(("" + OustAppState.getInstance().getAssessmentFirebaseClassList().get(i).getAsssessemntId())))) {
                    OustAppState.getInstance().setAssessmentFirebaseClass(OustAppState.getInstance().getAssessmentFirebaseClassList().get(i));
                    isAssessmentValid = true;
                }
            }
            if (!isAssessmentValid) {
                OustSdkTools.showToast(OustStrings.getString("assessment_no_longer"));
                return;
            }
        } else {
            intent.putExtra("assessmentId", assessmentId);
        }
        intent.putExtra("ActiveGame", gson.toJson(activeGame));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        OustSdkApplication.getContext().startActivity(intent);
    }

    public static void checkCatalogExistOrNot(long catalogueId, ActiveUser activeUser) {
        try {
            if (OustSdkTools.checkInternetStatus()) {
                String tenantName = OustPreferences.get("tanentid").trim();
                String catalogueModuleUrl = OustSdkApplication.getContext().getResources().getString(R.string.get_catalogue_id);
                catalogueModuleUrl = catalogueModuleUrl.replace("{orgId}", tenantName);
                catalogueModuleUrl = catalogueModuleUrl.replace("{userId}", activeUser.getStudentid());
                catalogueModuleUrl = HttpManager.getAbsoluteUrl(catalogueModuleUrl);
                Log.d("BranchTools", "getCatalogueId-->: " + catalogueModuleUrl);

                ApiCallUtils.doNetworkCall(Request.Method.GET, catalogueModuleUrl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("BranchTools", "onResponse: --> " + response);
                        Gson gson = new Gson();
                        CatalogueResponse catalogueResponse = gson.fromJson(response.toString(), CatalogueResponse.class);
                        if (catalogueResponse.getSuccess()) {
                            if (catalogueResponse.getCatalogueDetailsList() != null && !catalogueResponse.getCatalogueDetailsList().isEmpty()) {
                                if (catalogueResponse.getCatalogueDetailsList().get(0) != null && catalogueResponse.getCatalogueDetailsList().get(0).getCatalogueId() > 0) {
                                    if (catalogueResponse.getCatalogueDetailsList().get(0).getCatalogueId() == catalogueId) {
                                        getCatalog(catalogueResponse.getCatalogueDetailsList().get(0).getCatalogueId());
                                    } else {
                                        OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_catalogue_distribute_or_not));
                                    }
                                } else {
                                    OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_catalogue_distribute_or_not));
                                }
                            } else {
                                OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_catalogue_distribute_or_not));
                            }
                        } else {
                            OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_catalogue_distribute_or_not));
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_catalogue_distribute_or_not));
                    }
                });
            } else {
                OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_catalogue_distribute_or_not));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public static void gotoCplPage(String cpl_id) {
        try {
            Intent intent = new Intent(OustSdkApplication.getContext(), CplBaseActivity.class);
            intent.putExtra("cplId", cpl_id);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            OustSdkApplication.getContext().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public static void checkMlCPLDistributionOrNot(ActiveUser activeUser, String ml_cpl_id) {
        try {
            String tenantName = OustPreferences.get("tanentid").trim();
            String validateChildDistributionUrl = OustSdkApplication.getContext().getResources().getString(R.string.cpl_child_distribution_or_not);
            validateChildDistributionUrl = validateChildDistributionUrl.replace("{orgId}", tenantName);
            validateChildDistributionUrl = validateChildDistributionUrl.replace("{mlCplId}", ml_cpl_id);
            validateChildDistributionUrl = validateChildDistributionUrl.replace("{userId}", activeUser.getStudentid());
            validateChildDistributionUrl = HttpManager.getAbsoluteUrl(validateChildDistributionUrl);
            Gson mGson = new Gson();
            JSONObject jsonParams = OustSdkTools.getRequestObjectforJSONObject(null);
            Log.e("BranchTools", "checkMlCPLDistributionOrNot: --> " + validateChildDistributionUrl);
            ApiCallUtils.doNetworkCall(Request.Method.GET, validateChildDistributionUrl, jsonParams, new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        MlCplDistributionResponse mlCplDistributionResponse = mGson.fromJson(response.toString(), MlCplDistributionResponse.class);
                        final CommonResponse[] commonResponses = new CommonResponse[]{null};
                        if (mlCplDistributionResponse != null) {
                            if (mlCplDistributionResponse.getSuccess()) {
                                Intent intent;
                                if (mlCplDistributionResponse.getChildCplId() == 0) {
                                    intent = new Intent(OustSdkApplication.getContext(), LanguageSelectionActivity.class);
                                    intent.putExtra("CPL_ID", OustSdkTools.convertToLong(ml_cpl_id));
                                    intent.putExtra("allowBackPress", true);
                                    intent.putExtra("FEED", false);
                                    intent.putExtra("isChildCplDistributed", false);
                                } else {
                                    intent = new Intent(OustSdkApplication.getContext(), CplBaseActivity.class);
                                    intent.putExtra("cplId", String.valueOf(mlCplDistributionResponse.getChildCplId()));
                                }
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                OustSdkApplication.getContext().startActivity(intent);
                            } else {
                                commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                                if (commonResponses[0] != null && commonResponses[0].getExceptionData() != null) {
                                    if (commonResponses[0].getExceptionData().getMessage() != null) {
                                        OustSdkTools.showToast(commonResponses[0].getExceptionData().getMessage());
                                    } else {
                                        OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_cpl_distribute_or_not));
                                    }
                                } else if (commonResponses[0] != null && commonResponses[0].getError() != null) {
                                    OustSdkTools.showToast(commonResponses[0].getError());
                                } else {
                                    OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_cpl_distribute_or_not));
                                }
                            }
                        } else {
                            commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                            if (commonResponses[0] != null && commonResponses[0].getExceptionData() != null) {
                                if (commonResponses[0].getExceptionData().getMessage() != null) {
                                    OustSdkTools.showToast(commonResponses[0].getExceptionData().getMessage());
                                } else {
                                    OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_cpl_distribute_or_not));
                                }
                            } else if (commonResponses[0] != null && commonResponses[0].getError() != null) {
                                OustSdkTools.showToast(commonResponses[0].getError());
                            } else {
                                OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.error_message));
                            }
                        }
                    } catch (Exception e) {
                        OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_cpl_distribute_or_not));
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_cpl_distribute_or_not));
                }
            });
        } catch (Exception e) {
            OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_cpl_distribute_or_not));
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public static void checkModuleDistributionOrNot(ActiveUser activeUser, String moduleId, String moduleType) {
        try {
            String tenantName = OustPreferences.get("tanentid").trim();
            String checkModuleDistributedUrl = OustSdkApplication.getContext().getResources().getString(R.string.check_module_distributedOrNot);
            checkModuleDistributedUrl = checkModuleDistributedUrl.replace("{orgId}", tenantName);
            checkModuleDistributedUrl = checkModuleDistributedUrl.replace("{userId}", activeUser.getStudentid());
            checkModuleDistributedUrl = checkModuleDistributedUrl.replace("{moduleType}", moduleType);
            checkModuleDistributedUrl = checkModuleDistributedUrl.replace("{moduleId}", moduleId);

            checkModuleDistributedUrl = HttpManager.getAbsoluteUrl(checkModuleDistributedUrl);
            Gson mGson = new Gson();
            JSONObject jsonParams = OustSdkTools.getRequestObjectforJSONObject(null);
            Log.e("BranchTools", "checkModuleDistributionOrNot: --> " + checkModuleDistributedUrl);
            ApiCallUtils.doNetworkCall(Request.Method.GET, checkModuleDistributedUrl, jsonParams, new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("BranchTools", "onResponse: checkModuleDistributionOrNot--> " + response);
                    if (response.optBoolean("success")) {
                        Gson gson = new Gson();
                        CheckModuleDistributedOrNot checkModuleDistributedOrNot = gson.fromJson(response.toString(), CheckModuleDistributedOrNot.class);
                        if (checkModuleDistributedOrNot != null) {
                            if (checkModuleDistributedOrNot.getDistributed()) {
                                if (checkModuleDistributedOrNot.getModuleId() != 0) {
                                    String moduleId = String.valueOf(checkModuleDistributedOrNot.getModuleId());
                                    if (moduleType.equalsIgnoreCase("course")) {
                                        gotoCoursePage(moduleId);
                                    } else if (moduleType.equalsIgnoreCase("assessment")) {
                                        gotoAssessmentFormNotification(moduleId);
                                    } else if (moduleType.equalsIgnoreCase("cpl")) {
                                        gotoCplPage(moduleId);
                                    }
                                } else if (checkModuleDistributedOrNot.getMessage() != null && !checkModuleDistributedOrNot.getMessage().isEmpty()) {
                                    OustSdkTools.showToast(checkModuleDistributedOrNot.getMessage());
                                } else {
                                    if (moduleType.equalsIgnoreCase("course")) {
                                        OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_course_distribute_or_not));
                                    } else if (moduleType.equalsIgnoreCase("assessment")) {
                                        OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_Assessment_distribute_or_not));
                                    } else if (moduleType.equalsIgnoreCase("cpl")) {
                                        OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_cpl_distribute_or_not));
                                    } else {
                                        OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_data_is_or_not));
                                    }
                                }
                            } else {
                                if (checkModuleDistributedOrNot.getMessage() != null && !checkModuleDistributedOrNot.getMessage().isEmpty()) {
                                    OustSdkTools.showToast(checkModuleDistributedOrNot.getMessage());
                                } else {
                                    if (moduleType.equalsIgnoreCase("course")) {
                                        OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_course_distribute_or_not));
                                    } else if (moduleType.equalsIgnoreCase("assessment")) {
                                        OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_Assessment_distribute_or_not));
                                    } else if (moduleType.equalsIgnoreCase("cpl")) {
                                        OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_cpl_distribute_or_not));
                                    } else {
                                        OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_data_is_or_not));
                                    }
                                }
                            }
                        }
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_data_is_or_not));
                }
            });
        } catch (Exception e) {
            OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_data_is_or_not));
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public static void gotoCalendarPage(long meeting_id) {
        try {
            Intent eventDetail = new Intent(OustSdkApplication.getContext(), EventDataDetailScreen.class);
            eventDetail.putExtra("meetingId", meeting_id);
            eventDetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            OustSdkApplication.getContext().startActivity(eventDetail);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public static DTOCourseCard getCardFromMap(DTOUserFeedDetails.CardInfo cardInfo) {
        DTOCourseCard courseCardClass = new DTOCourseCard();
        if (cardInfo != null) {
            if (cardInfo.getBgImg() != null) {
                courseCardClass.setBgImg(cardInfo.getBgImg());
            }
            if (cardInfo.getCardBgColor() != null) {
                courseCardClass.setCardBgColor(cardInfo.getCardBgColor());
            }
            if (cardInfo.getCardSolutionColor() != null) {
                courseCardClass.setCardSolutionColor(cardInfo.getCardSolutionColor());
            }
            if (cardInfo.getCardTextColor() != null) {
                courseCardClass.setCardTextColor(cardInfo.getCardTextColor());
            }
            String cardType = "";
            if (cardInfo.getCardType() != null) {
                cardType = cardInfo.getCardType();
            }
            courseCardClass.setCardType(cardType);
            if (cardInfo.getClCode() != null) {
                courseCardClass.setClCode(cardInfo.getClCode());
            }
            if (cardInfo.getScormIndexFile() != null) {
                courseCardClass.setScormIndexFile(cardInfo.getScormIndexFile());
            }
            if (cardInfo.getContent() != null) {
                courseCardClass.setContent(cardInfo.getContent());
            }

            if (cardInfo.getCardTitle() != null) {
                courseCardClass.setCardTitle(cardInfo.getCardTitle());
            }

            if (cardInfo.getMandatoryViewTime() != 0) {
                courseCardClass.setMandatoryViewTime(cardInfo.getMandatoryViewTime());
            }

            if (cardInfo.getCardLayout() != null) {
                courseCardClass.setCardLayout(cardInfo.getCardLayout());
            }

            if (cardInfo.getCardId() != 0) {
                courseCardClass.setCardId(cardInfo.getCardId());
            }
            if (cardInfo.getLanguage() != null) {
                courseCardClass.setLanguage(cardInfo.getLanguage());
            }
            if (cardInfo.getXp() != 0) {
                courseCardClass.setXp(cardInfo.getXp());
            }
            courseCardClass.setShareToSocialMedia(cardInfo.isShareToSocialMedia());
            courseCardClass.setPotraitModeVideo(cardInfo.isPotraitModeVideo());
            List<DTOCourseCardMedia> courseCardMediaList = new ArrayList<>();
            if (cardInfo.getCardMediaList() != null) {
                List<DTOUserFeedDetails.CardMedia> mediaMap = cardInfo.getCardMediaList();
                if (mediaMap != null) {
                    for (int k = 0; k < mediaMap.size(); k++) {
                        if (mediaMap.get(k) != null) {
                            DTOCourseCardMedia courseCardMedia = new DTOCourseCardMedia();
                            if (mediaMap.get(k).getData() != null) {
                                courseCardMedia.setData(mediaMap.get(k).getData());
                            }
                            if (mediaMap.get(k).getGumletVideoUrl() != null) {
                                courseCardMedia.setGumletVideoUrl(mediaMap.get(k).getGumletVideoUrl());
                            }
                            if (mediaMap.get(k).getMediaType() != null) {
                                courseCardMedia.setMediaType(mediaMap.get(k).getMediaType());
                            }
                            courseCardMedia.setFastForwardMedia(mediaMap.get(k).isFastForwardMedia());

                            if (mediaMap.get(k).getMediaPrivacy() != null) {
                                courseCardMedia.setMediaPrivacy(mediaMap.get(k).getMediaPrivacy());
                            }
                            if (mediaMap.get(k).getMediaThumbnail() != null) {
                                courseCardMedia.setMediaThumbnail(mediaMap.get(k).getMediaThumbnail());
                            }
                            courseCardMediaList.add(courseCardMedia);
                        }
                    }
                }
            }
            courseCardClass.setCardMedia(courseCardMediaList);
            if ((courseCardClass.getXp() == 0)) {
                courseCardClass.setXp(100);
            }
            DTOUserFeedDetails.CardColorScheme cardColorSchemes = cardInfo.getCardColorScheme();
            if (cardColorSchemes != null) {
                DTOCardColorScheme cardColorScheme1 = new DTOCardColorScheme();
                if (cardColorSchemes.getContentColor() != null) {
                    cardColorScheme1.setContentColor(cardColorSchemes.getContentColor());
                }
                if (cardColorSchemes.getBgImage() != null) {
                    cardColorScheme1.setBgImage(cardColorSchemes.getBgImage());
                }
                if (cardColorSchemes.getIconColor() != null) {
                    cardColorScheme1.setIconColor(cardColorSchemes.getIconColor());
                }
                if (cardColorSchemes.getLevelNameColor() != null) {
                    cardColorScheme1.setLevelNameColor(cardColorSchemes.getLevelNameColor());
                }
                courseCardClass.setCardColorScheme(cardColorScheme1);
            }

            try {
                DTOUserFeedDetails.ReadMoreData readmoremaps = cardInfo.getReadMoreData();
                if (readmoremaps != null) {
                    DTOReadMore readMoreData = new DTOReadMore();
                    if (readmoremaps.getData() != null) {
                        readMoreData.setData(readmoremaps.getData());
                    }
                    if (readmoremaps.getDisplayText() != null) {
                        readMoreData.setDisplayText(readmoremaps.getDisplayText());
                    }
                    if (readmoremaps.getRmId() != 0) {
                        try {
                            readMoreData.setRmId(OustSdkTools.newConvertToLong(readmoremaps.getRmId()));
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                    }
                    if (readmoremaps.getScope() != null) {
                        readMoreData.setScope(readmoremaps.getScope());
                    }
                    if (readmoremaps.getType() != null) {
                        readMoreData.setType(readmoremaps.getType());
                    }
                    courseCardClass.setReadMoreData(readMoreData);
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
            return courseCardClass;
        }
        return courseCardClass;
    }
}
