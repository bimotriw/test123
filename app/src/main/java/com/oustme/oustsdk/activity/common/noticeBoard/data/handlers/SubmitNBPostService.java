package com.oustme.oustsdk.activity.common.noticeBoard.data.handlers;

import android.app.IntentService;
import android.content.Intent;
import androidx.annotation.Nullable;
import android.util.Log;


import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.noticeBoard.model.request.PostViewData;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by oust on 2/26/19.
 */

public class SubmitNBPostService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    private ActiveUser activeUser;
    private boolean isLikeDataDone = false;
    private boolean isShareDataDone = false;
    private boolean isCommentDataDone = false;

    public SubmitNBPostService() {
        super(SubmitNBPostService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        checkForUpdates();

    }

    private void checkForUpdates() {
        String activeUserGet = OustPreferences.get("userdata");
        activeUser = OustSdkTools.getActiveUserData(activeUserGet);

        List<PostViewData> likePostDataList = RoomHelper.getTypeBasedPostViewData("like");

        List<PostViewData> commentPostDataList = RoomHelper.getTypeBasedPostViewData("comment");

        List<PostViewData> sharePostDataList = RoomHelper.getTypeBasedPostViewData("share_text");

        List<PostViewData> replyPostDataList = RoomHelper.getTypeBasedPostViewData("reply");

        List<PostViewData> deleteReplyPostDataList = RoomHelper.getTypeBasedPostViewData("delete_reply");

        List<PostViewData> deleteCommentPostDataList = RoomHelper.getTypeBasedPostViewData("delete_comment");

        if (likePostDataList.size() > 0) {
            sendLikeData(likePostDataList);
        }
        if (commentPostDataList.size() > 0) {
            sendCommentData(commentPostDataList);
        }
        if(sharePostDataList.size()>0){
            sendNBShareData(sharePostDataList);
        }
        if(replyPostDataList.size()>0){
            sendNBReplyData(replyPostDataList);
        }

        if(deleteCommentPostDataList.size()>0){
            deleteNBCommentData(deleteCommentPostDataList);
        }
        if(deleteReplyPostDataList.size()>0){
            deleteNBReplyData(deleteReplyPostDataList);
        }
    }

    private void deleteNBReplyData(final List<PostViewData> postDataList) {
        try {
            String deleteReplyNbPostUrl = getResources().getString(R.string.nb_delete_reply);
            deleteReplyNbPostUrl = HttpManager.getAbsoluteUrl(deleteReplyNbPostUrl);
            if (postDataList != null && postDataList.size() > 0) {
                try {
                    JSONObject jsonObject = OustSdkTools.getRequestObject("");
                    jsonObject.put("tenantId", OustPreferences.get("tanentid"));
                    jsonObject.put("studentid", activeUser.getStudentid());
                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < postDataList.size(); i++) {
                        JSONObject j = new JSONObject();
                        if (postDataList.get(i) != null) {
                            PostViewData postViewData = postDataList.get(i);
                            j.put("nbId", postViewData.getNbId());
                            j.put("postId", postViewData.getPostid());
                            j.put("commentId", postViewData.getNbReplyData().getCommentId());
                            j.put("timeStamp", postViewData.getTimeStamp());
                            j.put("reply", postViewData.getNbReplyData().getReply());
                            j.put("replyId",postViewData.getNbReplyData().getId());
                            jsonArray.put(j);
                        }
                    }
                    jsonObject.put("dataList", jsonArray);

                    ApiCallUtils.doNetworkCall(Request.Method.PUT, deleteReplyNbPostUrl, jsonObject, new ApiCallUtils.NetworkCallback() {
                        @Override
                        public void onResponse(JSONObject response) {
                            deletePostViewData(postDataList);
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Error", "" + error);
                        }
                    });


                    /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, deleteReplyNbPostUrl, jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            deletePostViewData(postDataList);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Error", "" + error);
                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            try {
                                params.put("api-key", OustPreferences.get("api_key"));
                                params.put("org-id", OustPreferences.get("tanentid"));
                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }

                            return params;
                        }
                    };
                    jsonObjReq.setShouldCache(false);
                    jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 2, 100000f));
                    OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/

                } catch (JSONException e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void deleteNBCommentData(final List<PostViewData> postDataList) {
        try {
            String deleteNbCommentUrl = getResources().getString(R.string.nb_delete_comment);
            deleteNbCommentUrl = HttpManager.getAbsoluteUrl(deleteNbCommentUrl);
            if (postDataList != null && postDataList.size() > 0) {
                try {
                    JSONObject jsonObject = OustSdkTools.getRequestObject("");
                    jsonObject.put("tenantId", OustPreferences.get("tanentid"));
                    jsonObject.put("studentid", activeUser.getStudentid());
                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < postDataList.size(); i++) {
                        JSONObject j = new JSONObject();
                        if (postDataList.get(i) != null) {
                            PostViewData postViewData = postDataList.get(i);
                            j.put("nbId", postViewData.getNbId());
                            j.put("postId", postViewData.getPostid());
                            j.put("commentId", postViewData.getNbCommentData().getId());
                            j.put("timestamp", postViewData.getTimeStamp());
                            j.put("comment", postViewData.getNbCommentData().getComment());
                            jsonArray.put(j);
                        }
                    }
                    jsonObject.put("dataList", jsonArray);

                    ApiCallUtils.doNetworkCall(Request.Method.PUT, deleteNbCommentUrl, jsonObject, new ApiCallUtils.NetworkCallback() {
                        @Override
                        public void onResponse(JSONObject response) {
                            deletePostViewData(postDataList);
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Error", "" + error);
                        }
                    });


                    /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, deleteNbCommentUrl, jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            deletePostViewData(postDataList);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Error", "" + error);
                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            try {
                                params.put("api-key", OustPreferences.get("api_key"));
                                params.put("org-id", OustPreferences.get("tanentid"));
                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }

                            return params;
                        }
                    };
                    jsonObjReq.setShouldCache(false);
                    jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 2, 100000f));
                    OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/

                } catch (JSONException e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void sendNBReplyData(final List<PostViewData> replyPostDataList) {
        try {
            String replyNbPostUrl = getResources().getString(R.string.nb_post_reply_url);
            replyNbPostUrl = HttpManager.getAbsoluteUrl(replyNbPostUrl);
            if (replyPostDataList != null && replyPostDataList.size() > 0) {
                try {
                    JSONObject jsonObject = OustSdkTools.getRequestObject("");
                    jsonObject.put("tenantId", OustPreferences.get("tanentid"));
                    jsonObject.put("studentid", activeUser.getStudentid());
                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < replyPostDataList.size(); i++) {
                        JSONObject j = new JSONObject();
                        if (replyPostDataList.get(i) != null) {
                            PostViewData postViewData = replyPostDataList.get(i);
                            j.put("nbId", postViewData.getNbId());
                            j.put("postId", postViewData.getPostid());
                            j.put("commentId", postViewData.getNbReplyData().getCommentId());
                            j.put("timeStamp", postViewData.getTimeStamp());
                            j.put("reply", postViewData.getNbReplyData().getReply());
                            jsonArray.put(j);
                        }
                    }
                    jsonObject.put("dataList", jsonArray);

                    ApiCallUtils.doNetworkCall(Request.Method.PUT, replyNbPostUrl, jsonObject, new ApiCallUtils.NetworkCallback() {
                        @Override
                        public void onResponse(JSONObject response) {
                            deletePostViewData(replyPostDataList);
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Error", "" + error);
                        }
                    });


                    /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, replyNbPostUrl, jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            deletePostViewData(replyPostDataList);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Error", "" + error);
                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            try {
                                params.put("api-key", OustPreferences.get("api_key"));
                                params.put("org-id", OustPreferences.get("tanentid"));
                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }

                            return params;
                        }
                    };
                    jsonObjReq.setShouldCache(false);
                    jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 2, 100000f));
                    OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/

                } catch (JSONException e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void sendCommentData(final List<PostViewData> commentPostDataList) {
        try {
            if(!OustSdkTools.checkInternetStatus()){
                Log.d("TAG", "sendCommentData: No internet");
                return;
            }

            String commentNbPostUrl = getResources().getString(R.string.nb_post_comment_url_old);
            commentNbPostUrl = HttpManager.getAbsoluteUrl(commentNbPostUrl);
            if (commentPostDataList != null && commentPostDataList.size() > 0) {
                try {
                    JSONObject jsonObject = OustSdkTools.getRequestObject("");
                    jsonObject.put("tenantId", OustPreferences.get("tanentid"));
                    jsonObject.put("studentid", activeUser.getStudentid());
                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < commentPostDataList.size(); i++) {
                        JSONObject j = new JSONObject();
                        if (commentPostDataList.get(i) != null) {
                            PostViewData postViewData = commentPostDataList.get(i);
                            j.put("nbId", postViewData.getNbId());
                            j.put("postId", postViewData.getPostid());
                            j.put("comment", postViewData.getNbCommentData().getComment());
                            j.put("timestamp", postViewData.getTimeStamp());
                            jsonArray.put(j);
                        }
                    }
                    jsonObject.put("dataList", jsonArray);
                    Log.d("TAG", "sendCommentData: "+jsonObject.toString());

                    ApiCallUtils.doNetworkCall(Request.Method.PUT, commentNbPostUrl, jsonObject, new ApiCallUtils.NetworkCallback() {
                        @Override
                        public void onResponse(JSONObject response) {
                            deletePostViewData(commentPostDataList);
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try{
                                if(error!=null && error.networkResponse!=null && error.networkResponse.statusCode>=400){
                                    deletePostViewData(commentPostDataList);
                                }
                                Log.d("TAG", "onErrorResponse: Erroecode : "+error.networkResponse.statusCode);
                            }catch (Exception e){
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }
                            Log.e("Error", "" + error);
                        }
                    });


                    /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, commentNbPostUrl, jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            deletePostViewData(commentPostDataList);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Error", "" + error);
                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            try {
                                params.put("api-key", OustPreferences.get("api_key"));
                                params.put("org-id", OustPreferences.get("tanentid"));
                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }

                            return params;
                        }
                    };
                    jsonObjReq.setShouldCache(false);
                    jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 2, 100000f));
                    OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/

                } catch (JSONException e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void sendLikeData(final List<PostViewData> likePostDataList) {
        try {
            String likeNbPostUrl = getResources().getString(R.string.nb_post_like_url);
            likeNbPostUrl = HttpManager.getAbsoluteUrl(likeNbPostUrl);
            if (likePostDataList != null && likePostDataList.size() > 0) {
                try {
                    JSONObject jsonObject = OustSdkTools.getRequestObject("");
                    jsonObject.put("tenantId", OustPreferences.get("tanentid"));
                    jsonObject.put("studentid", activeUser.getStudentid());
                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < likePostDataList.size(); i++) {
                        JSONObject j = new JSONObject();
                        if (likePostDataList.get(i) != null) {
                            PostViewData postViewData = likePostDataList.get(i);
                            j.put("nbId", postViewData.getNbId());
                            j.put("postId", postViewData.getPostid());
                            j.put("likeStatus", postViewData.isLike());
                            j.put("timestamp", postViewData.getTimeStamp());
                            jsonArray.put(j);
                        }
                    }
                    jsonObject.put("dataList", jsonArray);

                    ApiCallUtils.doNetworkCall(Request.Method.PUT, likeNbPostUrl, jsonObject, new ApiCallUtils.NetworkCallback() {
                        @Override
                        public void onResponse(JSONObject response) {
                            deletePostViewData(likePostDataList);
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Error", "" + error);
                        }
                    });


                    /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, likeNbPostUrl, jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            deletePostViewData(likePostDataList);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Error", "" + error);
                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            try {
                                params.put("api-key", OustPreferences.get("api_key"));
                                params.put("org-id", OustPreferences.get("tanentid"));
                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }

                            return params;
                        }
                    };
                    jsonObjReq.setShouldCache(false);
                    jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 2, 100000f));
                    OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/


                } catch (JSONException e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void sendNBShareData(final List<PostViewData> sharePostDataList) {

        try {
            String shareNbPostUrl = getResources().getString(R.string.nb_post_share_url);
            shareNbPostUrl = HttpManager.getAbsoluteUrl(shareNbPostUrl);
            if (sharePostDataList != null && sharePostDataList.size() > 0) {
                try {
                    JSONObject jsonObject = OustSdkTools.getRequestObject("");
                    jsonObject.put("tenantId", OustPreferences.get("tanentid"));
                    jsonObject.put("studentid", activeUser.getStudentid());
                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < sharePostDataList.size(); i++) {
                        JSONObject j = new JSONObject();
                        if (sharePostDataList.get(i) != null) {
                            PostViewData postViewData = sharePostDataList.get(i);
                            j.put("nbId", postViewData.getNbId());
                            j.put("postId", postViewData.getPostid());
                            j.put("timestamp", postViewData.getTimeStamp());
                            jsonArray.put(j);
                        }
                    }
                    jsonObject.put("dataList", jsonArray);

                    ApiCallUtils.doNetworkCall(Request.Method.PUT, shareNbPostUrl, jsonObject, new ApiCallUtils.NetworkCallback() {
                        @Override
                        public void onResponse(JSONObject response) {
                            deletePostViewData(sharePostDataList);
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Error", "" + error);
                        }
                    });


                    /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, shareNbPostUrl, jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            deletePostViewData(sharePostDataList);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Error", "" + error);
                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            try {
                                params.put("api-key", OustPreferences.get("api_key"));
                                params.put("org-id", OustPreferences.get("tanentid"));
                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }

                            return params;
                        }
                    };
                    jsonObjReq.setShouldCache(false);
                    jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 2, 100000f));
                    OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/

                } catch (JSONException e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public static void deletePostViewData(final List<PostViewData> postViewDataList) {
        try {
            List<PostViewData> nowData = new ArrayList<>();
            List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedNBRequests");
            Gson gson = new Gson();
            if (requests != null && requests.size() > 0) {
                for (int i = 0; i < requests.size(); i++) {
                    String request = requests.get(i);
                    PostViewData postViewData = gson.fromJson(request, PostViewData.class);
                    nowData.add(postViewData);
                }
            }

            for (int i = 0; i < postViewDataList.size(); i++) {
                for (int j = 0; j < nowData.size(); j++) {
                    if (nowData.get(j) != null) {
                        if (postViewDataList.get(i).getTimeStamp() == nowData.get(j).getTimeStamp()) {
                            nowData.remove(j);
                        }
                    }
                }
            }
            List<String> remain_requests = new ArrayList<>();

            if (nowData != null) {
                for (int i = 0; i < nowData.size(); i++) {
                    String str = gson.toJson(nowData.get(i));
                    remain_requests.add(str);
                }
                OustPreferences.saveLocalNotificationMsg("savedNBRequests", remain_requests);
            } else {
                OustPreferences.saveLocalNotificationMsg("savedNBRequests", remain_requests);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }


}
