package com.oustme.oustsdk.activity.courses.learningmapmodule;

import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.request.SubmitCourseCardRequestData;
import com.oustme.oustsdk.request.SubmitCourseCardRequestDataV3;
import com.oustme.oustsdk.response.assessment.submitResponse.SubmitDataResponse;
import com.oustme.oustsdk.response.course.LearningCardResponceData;
import com.oustme.oustsdk.room.EntityLevelCardCourseIDUpdateTime;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.room.dto.DTOUserCourseData;
import com.oustme.oustsdk.sqlite.UserCourseScoreDatabaseHandler;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class LearningMapModulePresenter {
    private static final String TAG = "LearningModulePresenter";
    private final LearningMapModuleView mView;

    public LearningMapModulePresenter(LearningMapModuleView view) {
        this.mView = view;
    }

    public void getFavouriteCardsFromFirebase(String studentKey, String courseId) {
        try {
            String message = "/userFavouriteCards/" + "user" + studentKey + "/course" + courseId;
            ValueEventListener allFavCard = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mView.updateFavCardsFromFB(dataSnapshot);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError DatabaseError) {
                    Log.e(TAG, String.valueOf(DatabaseError));
                    mView.onError();
                }
            };
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(allFavCard);
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setFavCardDetails(String studentKey, long mCourseId, long rmId, Map<String, Object> favRMCardDetails) {
        Log.d(TAG, "setFavCardDetails: ");
        String message = "/userFavouriteCards" + "/user" + studentKey + "/course" + mCourseId + "/readMore" + "/rm" + rmId;
        OustFirebaseTools.getRootRef().child(message).setValue(favRMCardDetails);
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
    }

    public void setNonRMFavCardDetails(String studentKey, long mCourseId, String cardId, Map<String, Object> favRMCardDetails) {
        Log.d(TAG, "setNonRMFavCardDetails: ");
        String message = "/userFavouriteCards" + "/user" + studentKey + "/course" + mCourseId + "/cards" + "/card" + cardId;
        OustFirebaseTools.getRootRef().child(message).setValue(favRMCardDetails);
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
    }

    public void setFavCardName(String studentKey, long mCourseId, String mCourseName) {
        Log.d(TAG, "setFavCardName: ");
        String message1 = "/userFavouriteCards" + "/user" + studentKey + "/course" + mCourseId + "/courseName";
        OustFirebaseTools.getRootRef().child(message1).setValue(mCourseName);
        OustFirebaseTools.getRootRef().child(message1).keepSynced(true);
    }

    public void setFavCardId(String studentKey, long mCourseId, String mCourseName) {
        Log.d(TAG, "setFavCardId: " + mCourseName);
        String message1 = "/userFavouriteCards" + "/user" + studentKey + "/course" + mCourseId + "/courseId";
        OustFirebaseTools.getRootRef().child(message1).setValue(mCourseId);
        OustFirebaseTools.getRootRef().child(message1).keepSynced(true);
    }

    public void removeFavCardFromFB(String studentKey, long mCourseId, String cardId) {
        Log.d(TAG, "removeFavCardFromFB: ");
        String message = "/userFavouriteCards" + "/user" + studentKey + "/course" + mCourseId + "/cards" + "/card" + cardId;
        OustFirebaseTools.getRootRef().child(message).setValue(null);
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
    }

    public void removeRMCardFromFB(String studentKey, long courseId, long rmId) {
        Log.d(TAG, "removeRMCardFromFB: ");
        String message = "/userFavouriteCards" + "/user" + studentKey + "/course" + courseId + "/readMore" + "/rm" + rmId;
        Log.e("ReadMore", message);
        OustFirebaseTools.getRootRef().child(message).setValue(null);
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
    }

    public void saveCurrentCardNumber(boolean isReverseTrans, long levelId, int questionNo) {
        try {
            UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
            DTOUserCourseData dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
            if (dtoUserCourseData.getUserLevelDataList() != null) {
                userCourseScoreDatabaseHandler.setLastPlayedLevel(levelId, dtoUserCourseData);
                for (int n = 0; n < dtoUserCourseData.getUserLevelDataList().size(); n++) {
                    if (dtoUserCourseData.getUserLevelDataList().get(n) != null) {
                        if (dtoUserCourseData.getUserLevelDataList().get(n).getLevelId() == levelId) {
                            int no = questionNo;
                            if ((isReverseTrans) && (no > 0)) {
                                no = no - 1;
                            }
                            RoomHelper.setCurrentCardNumber(dtoUserCourseData, n, no);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void saveCardData(SubmitCourseCardRequestData submitCourseCardRequestData, long mappedSurveyId, long mappedAssessmentId) {
        try {
            SubmitCourseCardRequestDataV3 submitCourseCardRequestDataV3 = new SubmitCourseCardRequestDataV3();
            submitCourseCardRequestDataV3.setCplId(submitCourseCardRequestData.getCplId());
            submitCourseCardRequestDataV3.setDeviceToken(submitCourseCardRequestData.getDeviceToken());
            submitCourseCardRequestDataV3.setStudentid(submitCourseCardRequestData.getStudentid());
            submitCourseCardRequestDataV3.setOfflineRequest(submitCourseCardRequestData.isOfflineRequest());
            submitCourseCardRequestDataV3.setUserCardResponse(submitCourseCardRequestData.getUserCardResponse());

            Gson gson = new GsonBuilder().create();
            final String jsonParams = gson.toJson(submitCourseCardRequestDataV3);
            String submitCard_url_v3 = OustSdkApplication.getContext().getResources().getString(R.string.submitCard_url_v4);

            if (mappedAssessmentId > 0) {
                submitCard_url_v3 = submitCard_url_v3 + "mappedAssessmentId=" + mappedAssessmentId;
            }
            if (mappedSurveyId > 0) {
                submitCard_url_v3 = submitCard_url_v3 + "&mappedSurveyId=" + mappedSurveyId;
            }

            try {
                submitCard_url_v3 = HttpManager.getAbsoluteUrl(submitCard_url_v3);
                Log.d(TAG, "sendRequest: url:" + submitCard_url_v3);
                Log.d(TAG, "sendRequest: cardSubmitData:" + jsonParams);

                ApiCallUtils.doNetworkCall(Request.Method.POST, submitCard_url_v3, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: cardSubmitData:" + response.toString());
                        SubmitDataResponse submitDataResponse = gson.fromJson(response.toString(), SubmitDataResponse.class);
                        if (response.optBoolean("success")) {
                            if (submitDataResponse.getLevelUpdateTimeMap() != null && submitDataResponse.getLevelUpdateTimeMap().size() == 1) {
                                long levelId = submitDataResponse.getLevelUpdateTimeMap().get(0).getLevelId();
                                long levelUpdateTime = submitDataResponse.getLevelUpdateTimeMap().get(0).getCourseLevelUpdateTS();
                                List<EntityLevelCardCourseIDUpdateTime> entityLevelCardCourseIDUpdateTime = RoomHelper.getLevelCourseCardUpdateTimeMap((int) levelId);
                                if (entityLevelCardCourseIDUpdateTime != null && entityLevelCardCourseIDUpdateTime.size() > 0) {
                                    if (OustAppState.getInstance().getActiveUser().getStudentKey().equalsIgnoreCase(entityLevelCardCourseIDUpdateTime.get(0).getStudentKey())) {
                                        if (levelUpdateTime > entityLevelCardCourseIDUpdateTime.get(0).getUpdateTime()) {
                                            callBackMethod(true, false, null, 0, 0);
                                        } else if (levelUpdateTime == entityLevelCardCourseIDUpdateTime.get(0).getUpdateTime()) {
                                            callBackMethod(false, false, null, 0, 0);
                                        } else {
                                            callBackMethod(false, false, null, 0, 0);
                                        }
                                    } else {
                                        callBackMethod(false, false, null, 0, 0);
                                    }
                                } else {
                                    callBackMethod(false, false, null, 0, 0);
                                }
                            } else if (submitDataResponse.getLevelUpdateTimeMap() != null && submitDataResponse.getLevelUpdateTimeMap().size() > 1) {
                                long levelId = submitDataResponse.getLevelUpdateTimeMap().get(submitDataResponse.getLevelUpdateTimeMap().size() - 1).getLevelId();
                                long levelUpdateTime = submitDataResponse.getLevelUpdateTimeMap().get(submitDataResponse.getLevelUpdateTimeMap().size() - 1).getCourseLevelUpdateTS();
                                List<EntityLevelCardCourseIDUpdateTime> entityLevelCardCourseIDUpdateTime = RoomHelper.getLevelCourseCardUpdateTimeMap((int) levelId);
                                if (entityLevelCardCourseIDUpdateTime != null && entityLevelCardCourseIDUpdateTime.size() > 0) {
                                    if (OustAppState.getInstance().getActiveUser().getStudentKey().equalsIgnoreCase(entityLevelCardCourseIDUpdateTime.get(0).getStudentKey())) {
                                        if (levelUpdateTime > entityLevelCardCourseIDUpdateTime.get(submitDataResponse.getLevelUpdateTimeMap().size() - 1).getUpdateTime()) {
                                            callBackMethod(false, false, null, 0, 0);
                                        } else if (levelUpdateTime == entityLevelCardCourseIDUpdateTime.get(submitDataResponse.getLevelUpdateTimeMap().size() - 1).getUpdateTime()) {
                                            callBackMethod(false, false, null, 0, 0);
                                        } else {
                                            callBackMethod(false, false, null, 0, 0);
                                        }
                                    } else {
                                        callBackMethod(false, false, null, 0, 0);
                                    }
                                } else {
                                    callBackMethod(false, false, null, 0, 0);
                                }
                            } else {
                                callBackMethod(false, false, null, 0, 0);

                            }
                            Log.e(TAG, "onResponse: courseSubmitMapData--> " + submitDataResponse);
                        } else {
                            if (submitDataResponse != null && submitDataResponse.getExceptionData() != null) {
                                if (submitDataResponse.getExceptionData().getMessage() != null) {
                                    OustSdkTools.showToast(submitDataResponse.getExceptionData().getMessage());
                                } else {
                                    OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.retry_internet_msg));
                                }
                            } else if (submitDataResponse != null && submitDataResponse.getError() != null) {
                                OustSdkTools.showToast(submitDataResponse.getError());
                            } else {
                                OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.retry_internet_msg));
                            }
                            callBackMethod(false, true, submitCourseCardRequestData.getUserCardResponse(), mappedSurveyId, mappedAssessmentId);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callBackMethod(false, true, submitCourseCardRequestData.getUserCardResponse(), mappedSurveyId, mappedAssessmentId);
                    }
                });

            } catch (Exception e) {
                callBackMethod(false, true, submitCourseCardRequestData.getUserCardResponse(), mappedSurveyId, mappedAssessmentId);
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void callBackMethod(boolean levelDataIsUpDated, boolean apiIsFalling, List<LearningCardResponceData> getUserCardResponse, long mappedSurveyId, long mappedAssessmentId) {
        try {
            if (mView != null) {
                mView.updateSubmitCardData(levelDataIsUpDated, apiIsFalling, getUserCardResponse, mappedSurveyId, mappedAssessmentId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
