package com.oustme.oustsdk.tools;

import android.util.Log;


import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.interfaces.common.ServerResponseListener;
import com.oustme.oustsdk.network.VolleyRequest;
import com.oustme.oustsdk.request.AddFavCardsRequestData;
import com.oustme.oustsdk.request.AddFavReadMoreRequestData;
import com.oustme.oustsdk.response.assessment.QuestionResponse;
import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.response.course.LearningCardResponce;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by oust on 7/4/17.
 */

public class OustRestClient {
    private static final String TAG = "OustRestClient";
    public LearningCardResponce getlearningCardResponces(long cardId, long levelId, int courseId) {
        final LearningCardResponce[] learningCardResponces = {null};
        try {
            //String getCardUrl = OustSdkApplication.getContext().getResources().getString(R.string.getCard_url);
            String getCardUrl = OustSdkApplication.getContext().getResources().getString(R.string.getCardUrl_V2);
            getCardUrl = getCardUrl.replace("cardId", String.valueOf(cardId));
            getCardUrl = getCardUrl.replace("{courseId}", String.valueOf(courseId));
            getCardUrl = getCardUrl.replace("{levelId}", String.valueOf(levelId));
            Log.d(TAG, "getlearningCardResponces: "+getCardUrl);
            VolleyRequest volleyReq = new VolleyRequest(OustSdkApplication.getContext(), getCardUrl, 1, new ServerResponseListener() {
                @Override
                public void onSuccess(JSONObject jsonObject) {

                }

                @Override
                public void onSuccess(JSONObject jsonObject, int requestType) {
                    if (jsonObject != null && requestType == 1) {
                        try {
                            Gson gson = new Gson();
                            learningCardResponces[0] = gson.fromJson(jsonObject.toString(), LearningCardResponce.class);
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    }
                }

                @Override
                public void onError(String error) {

                }
            });

        } catch (Exception e) {
        }
        return learningCardResponces[0];
    }


    public CommonResponse sendFavCardRequest(AddFavCardsRequestData addFavCardsRequestData) {
        final CommonResponse[] commonResponses = {null};
        try {
            String submitCourseComplete_url = OustSdkApplication.getContext().getResources().getString(R.string.submitFavCard_url);
            Gson gson = new GsonBuilder().create();
            String jsonParams = gson.toJson(addFavCardsRequestData);

            /*VolleyRequest volleyReq = new VolleyRequest(OustSdkApplication.getContext(), submitCourseComplete_url, jsonParams, new ServerResponseListener() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                    commonResponses[0] = OustSdkTools.getCommonResponse(jsonObject.toString());
                }

                @Override
                public void onSuccess(JSONObject jsonObject, int requestType) {

                }

                @Override
                public void onError(String error) {

                }
            });
            volleyReq.executeToServer();*/

            ApiCallUtils.doNetworkCall(Request.Method.PUT, submitCourseComplete_url, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });

        } catch (Exception e) {
            Log.e("sendFavCardRequest ", "ERROR: ", e);
        }
        return commonResponses[0];
    }

    public CommonResponse sendUnFavCardRequest(AddFavCardsRequestData addFavCardsRequestData) {
        final CommonResponse[] commonResponses = {null};
        try {
            String submitCourseComplete_url = "card/bulk/unfavorite";
            Gson gson = new GsonBuilder().create();
            String jsonParams = gson.toJson(addFavCardsRequestData);

            /*VolleyRequest volleyReq = new VolleyRequest(OustSdkApplication.getContext(), submitCourseComplete_url, jsonParams, new ServerResponseListener() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                    commonResponses[0] = OustSdkTools.getCommonResponse(jsonObject.toString());
                }

                @Override
                public void onSuccess(JSONObject jsonObject, int requestType) {

                }

                @Override
                public void onError(String error) {

                }
            });
            volleyReq.executeToServer();*/

            ApiCallUtils.doNetworkCall(Request.Method.POST, submitCourseComplete_url, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });

        } catch (Exception e) {
            Log.e("sendUnFavCardRequest ", "ERROR: ", e);
        }
        return commonResponses[0];
    }

    public CommonResponse sendFavRMRequest(AddFavReadMoreRequestData addFavReadMoreRequestData) {
        final CommonResponse[] commonResponses = {null};
        try {
            String submitCourseComplete_url = "card/readMore/favourite";
            Gson gson = new GsonBuilder().create();
            String jsonParams = gson.toJson(addFavReadMoreRequestData);
            /*VolleyRequest volleyReq = new VolleyRequest(OustSdkApplication.getContext(), submitCourseComplete_url, jsonParams, new ServerResponseListener() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                    commonResponses[0] = OustSdkTools.getCommonResponse(jsonObject.toString());
                }

                @Override
                public void onSuccess(JSONObject jsonObject, int requestType) {

                }

                @Override
                public void onError(String error) {

                }
            });
            volleyReq.executeToServer();*/

            ApiCallUtils.doNetworkCall(Request.Method.POST, submitCourseComplete_url, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });

        } catch (Exception e) {
            Log.e("sendFavRMRequest", "ERROR: ", e);
        }
        return commonResponses[0];
    }

    public CommonResponse sendUnFavRMRequest(AddFavReadMoreRequestData addFavReadMoreRequestData) {
        final CommonResponse[] commonResponses = {null};
        try {
            String submitCourseComplete_url = "card/readMore/unfavourite";
            Gson gson = new GsonBuilder().create();
            String jsonParams = gson.toJson(addFavReadMoreRequestData);
            /*VolleyRequest volleyReq = new VolleyRequest(OustSdkApplication.getContext(), submitCourseComplete_url, jsonParams, new ServerResponseListener() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                    commonResponses[0] = OustSdkTools.getCommonResponse(jsonObject.toString());
                }

                @Override
                public void onSuccess(JSONObject jsonObject, int requestType) {

                }

                @Override
                public void onError(String error) {

                }
            });
            volleyReq.executeToServer();*/

            ApiCallUtils.doNetworkCall(Request.Method.POST, submitCourseComplete_url, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });

        } catch (Exception e) {
            Log.e("sendUnFavRMRequest", "ERROR: ", e);
        }
        return commonResponses[0];
    }

    StringEntity parsedJsonParams = null;

    public LearningCardResponce downloadCardData(String url) {
        Log.d(TAG, "downloadCardData: restclient:"+url);
        final LearningCardResponce[] learningCardResponces = {null};
        try {
            /*HttpManager.get(url, null, new JsonHttpResponseHandler() {
                public void onSuccess(int statusCode, Header[] headers, JSONObject resultData) {
                    Gson gson = new Gson();
                    Log.d(TAG, "onSuccess:JasonData: "+resultData.toString());
                    learningCardResponces[0] = gson.fromJson(resultData.toString(), LearningCardResponce.class);
                }

                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Log.d(TAG, "onFailure: "+throwable.getLocalizedMessage());
                    learningCardResponces[0] = null;
                }
            });*/

            final Gson gson = new GsonBuilder().create();
            String jsonParams = gson.toJson(parsedJsonParams);
            url = HttpManager.getAbsoluteUrl(url);

            ApiCallUtils.doNetworkCall(Request.Method.GET, url, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    Log.d(TAG, "onSuccess:JasonData: "+response.toString());
                    learningCardResponces[0] = gson.fromJson(response.toString(), LearningCardResponce.class);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onFailure: "+error.getLocalizedMessage());
                    learningCardResponces[0] = null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return learningCardResponces[0];
    }

    public DTOQuestions getQuestionById(String url) {
        final DTOQuestions[] questionses = {null};
        try {
            HttpManager.get(url, null, new JsonHttpResponseHandler() {
                public void onSuccess(int statusCode, Header[] headers, JSONObject resultData) {
                    Gson gson = new GsonBuilder().create();
                    QuestionResponse questionResponce = gson.fromJson(resultData.toString(), QuestionResponse.class);
                    Log.d(TAG, "onSuccess: "+resultData.toString());
                    questionses[0] = questionResponce.getQuestions().get(0);//OustSdkTools.decryptQuestion(questionResponce.getQuestionsList().get(0), null);
                }

                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    questionses[0] = null;
                }
            });
        } catch (Exception e) {
        }
        return questionses[0];
    }


}
