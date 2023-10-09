package com.oustme.oustsdk.activity.common.leaderboard.presenter;

import android.util.Log;


import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.leaderboard.model.leaderBoardResponse;
import com.oustme.oustsdk.activity.common.leaderboard.view.NewLeaderBoardView;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class NewLeaderBoardPresenter {
    private static final String TAG = "NewLeaderBoardPresenter";
private NewLeaderBoardView mNewLeaderBoardView;
private String activeUserGet;
private ActiveUser activeUser;

    public NewLeaderBoardPresenter(NewLeaderBoardView mNewLeaderBoardView) {
        this.mNewLeaderBoardView = mNewLeaderBoardView;
        activeUserGet = OustPreferences.get("userdata");
        activeUser = OustSdkTools.getActiveUserData(activeUserGet);
    }
    public void getRankersData(){
        mNewLeaderBoardView.showProgressBar();

        String leaderBoardURL = OustSdkApplication.getContext().getResources().getString(R.string.get_lb_url_v2);
        final Gson mGson = new Gson();
        leaderBoardURL = HttpManager.getAbsoluteUrl(leaderBoardURL+"/"+activeUser.getStudentid());
        Log.d(TAG, "getRankersData: ");


        ApiCallUtils.doNetworkCall(Request.Method.GET, leaderBoardURL, OustSdkTools.getRequestObject("") , new ApiCallUtils.NetworkCallback() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d(TAG, "onResult: "+response.toString());
                    if(response.getBoolean("success")) {
                        leaderBoardResponse leaderBoardResponseObject = new leaderBoardResponse();
                        leaderBoardResponseObject = mGson.fromJson(response.toString(), leaderBoardResponse.class);
                        mNewLeaderBoardView.hideProgressBar();
                        mNewLeaderBoardView.updateRankersData(leaderBoardResponseObject.getLeaderBoardDataList());

                        if(leaderBoardResponseObject.getGroupLbDataList()!=null && leaderBoardResponseObject.getGroupLbDataList().size()>0)
                        {
                            Log.d(TAG, "Grouplist is found: "+leaderBoardResponseObject.getGroupLbDataList());
                            mNewLeaderBoardView.updateGroupData(leaderBoardResponseObject.getGroupLbDataList());
                        }
                    }
                    else {
                        mNewLeaderBoardView.showError(OustStrings.getString("something_went_wrong"));
                        mNewLeaderBoardView.hideProgressBar();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    mNewLeaderBoardView.showError(OustStrings.getString("invalid_response"));
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                mNewLeaderBoardView.hideProgressBar();
                mNewLeaderBoardView.showError("Not able to connect to server");
            }
        });

        /*ApiClient.jsonRequest(OustSdkApplication.getContext(), Request.Method.GET, leaderBoardURL, new HashMap<>(), null, new ApiClient.NResultListener<JSONObject>() {
            @Override
            public void onResult(int resultCode, JSONObject tResult) {
                try {
                    Log.d(TAG, "onResult: "+tResult.toString());
                    if(tResult.getBoolean("success"))
                    {
                        leaderBoardResponse leaderBoardResponseObject = new leaderBoardResponse();
                        leaderBoardResponseObject = mGson.fromJson(tResult.toString(), leaderBoardResponse.class);
                        mNewLeaderBoardView.hideProgressBar();
                        mNewLeaderBoardView.updateRankersData(leaderBoardResponseObject.getLeaderBoardDataList());

                        if(leaderBoardResponseObject.getGroupLbDataList()!=null && leaderBoardResponseObject.getGroupLbDataList().size()>0)
                        {
                            Log.d(TAG, "Grouplist is found: "+leaderBoardResponseObject.getGroupLbDataList());
                            mNewLeaderBoardView.updateGroupData(leaderBoardResponseObject.getGroupLbDataList());
                        }
                    }
                    else {
                        mNewLeaderBoardView.showError(OustStrings.getString("something_went_wrong"));
                        mNewLeaderBoardView.hideProgressBar();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    mNewLeaderBoardView.showError(OustStrings.getString("invalid_response"));
                }
            }

            @Override
            public void onFailure(int mError) {
                mNewLeaderBoardView.hideProgressBar();
                mNewLeaderBoardView.showError("Not able to connect to server");
            }
        });*/





    }
    private Gson mGson = new Gson();
    public void getCourseLB(String lppathId){
        mNewLeaderBoardView.showProgressBar();

        String getLeaderboardUrl = OustSdkApplication.getContext().getResources().getString(R.string.getcourse_lb_url_v2);
        getLeaderboardUrl = getLeaderboardUrl.replace("{courseId}", lppathId);

        getLeaderboardUrl = HttpManager.getAbsoluteUrl(getLeaderboardUrl+""+activeUser.getStudentid());
        //String leaderBoardURL = OustSdkApplication.getContext().getResources().getString(R.string.get_lb_url_v2);

        Log.d(TAG, "getCourseLB: "+getLeaderboardUrl);
       // leaderBoardURL = HttpManager.getAbsoluteUrl(leaderBoardURL+"/"+activeUser.getStudentid());


        ApiCallUtils.doNetworkCall(Request.Method.GET, getLeaderboardUrl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getBoolean("success")) {
                        leaderBoardResponse leaderBoardResponseObject = new leaderBoardResponse();
                        leaderBoardResponseObject = mGson.fromJson(response.toString(), leaderBoardResponse.class);
                        mNewLeaderBoardView.hideProgressBar();

                        if(leaderBoardResponseObject.getLeaderBoardDataList()!=null && leaderBoardResponseObject.getLeaderBoardDataList().size()==0) {
                            if(leaderBoardResponseObject.getGroupLbDataList()!=null && leaderBoardResponseObject.getGroupLbDataList().size()==0){
                                mNewLeaderBoardView.noLB();
                            }

                        } else {
                            if (leaderBoardResponseObject.getLeaderBoardDataList() != null && leaderBoardResponseObject.getLeaderBoardDataList().size() > 0) {
                                mNewLeaderBoardView.updateRankersData(leaderBoardResponseObject.getLeaderBoardDataList());
                            }
                        }if(leaderBoardResponseObject.getGroupLbDataList()!=null && leaderBoardResponseObject.getGroupLbDataList().size()>0) {
                            mNewLeaderBoardView.updateGroupData(leaderBoardResponseObject.getGroupLbDataList());
                        }else if(leaderBoardResponseObject.getLeaderBoardDataList()==null && leaderBoardResponseObject.getGroupLbDataList()==null){
                            mNewLeaderBoardView.noLB();
                        }

                    }
                    else {
                        mNewLeaderBoardView.showError("Something is not right");
                        mNewLeaderBoardView.hideProgressBar();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    mNewLeaderBoardView.showError(OustStrings.getString("invalid_response"));
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                mNewLeaderBoardView.hideProgressBar();
                mNewLeaderBoardView.showError("Not able to connect to server");
            }
        });

        /*ApiClient.jsonRequest(OustSdkApplication.getContext(), Request.Method.GET, getLeaderboardUrl, new HashMap<>(), null, new ApiClient.NResultListener<JSONObject>() {
            @Override
            public void onResult(int resultCode, JSONObject tResult) {
                try {
                    if(tResult.getBoolean("success"))
                    {
                        leaderBoardResponse leaderBoardResponseObject = new leaderBoardResponse();
                        leaderBoardResponseObject = mGson.fromJson(tResult.toString(), leaderBoardResponse.class);
                        mNewLeaderBoardView.hideProgressBar();

                        if(leaderBoardResponseObject.getLeaderBoardDataList()!=null && leaderBoardResponseObject.getLeaderBoardDataList().size()==0)
                        {
                            if(leaderBoardResponseObject.getGroupLbDataList()!=null && leaderBoardResponseObject.getGroupLbDataList().size()==0){
                                mNewLeaderBoardView.noLB();
                            }
                        }
                        else {
                            if (leaderBoardResponseObject.getLeaderBoardDataList() != null && leaderBoardResponseObject.getLeaderBoardDataList().size() > 0) {
                                mNewLeaderBoardView.updateRankersData(leaderBoardResponseObject.getLeaderBoardDataList());
                            }
                        }
                        if(leaderBoardResponseObject.getGroupLbDataList()!=null && leaderBoardResponseObject.getGroupLbDataList().size()>0)
                        {
                            mNewLeaderBoardView.updateGroupData(leaderBoardResponseObject.getGroupLbDataList());
                        }

                    }
                    else {
                        mNewLeaderBoardView.showError("Something is not right");
                        mNewLeaderBoardView.hideProgressBar();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    mNewLeaderBoardView.showError("invalid format of response");
                }
            }

            @Override
            public void onFailure(int mError) {
                mNewLeaderBoardView.hideProgressBar();
                mNewLeaderBoardView.showError("Not able to connect to server");
            }
        });*/
    }

    public void getCourseLBGroupWise(String lppathId, String groupId, final String groupName){
        mNewLeaderBoardView.showProgressBar();

        String getLeaderboardUrl = OustSdkApplication.getContext().getResources().getString(R.string.getcourse_lb_url_group);
        getLeaderboardUrl = getLeaderboardUrl.replace("{courseId}", lppathId);
        getLeaderboardUrl = getLeaderboardUrl.replace("{userId}", activeUser.getStudentid());
        getLeaderboardUrl = getLeaderboardUrl+"?groupId="+groupId;

        getLeaderboardUrl = HttpManager.getAbsoluteUrl(getLeaderboardUrl);//+"/"+activeUser.getStudentid());
        //String leaderBoardURL = OustSdkApplication.getContext().getResources().getString(R.string.get_lb_url_v2);
        final Gson mGson = new Gson();
        Log.d(TAG, "getCourseLBGroup: "+getLeaderboardUrl);
        // leaderBoardURL = HttpManager.getAbsoluteUrl(leaderBoardURL+"/"+activeUser.getStudentid());

        ApiCallUtils.doNetworkCall(Request.Method.GET, getLeaderboardUrl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d(TAG, "onResult: "+response.toString());
                    if(response.getBoolean("success"))
                    {
                        leaderBoardResponse leaderBoardResponseObject = new leaderBoardResponse();
                        leaderBoardResponseObject = mGson.fromJson(response.toString(), leaderBoardResponse.class);
                        mNewLeaderBoardView.hideProgressBar();
                        if(leaderBoardResponseObject.getLeaderBoardDataList()!=null && leaderBoardResponseObject.getLeaderBoardDataList().size()>0)
                        {
                            mNewLeaderBoardView.updateGroupLBData(leaderBoardResponseObject.getLeaderBoardDataList(), groupName);
                        }
                        else
                        {
                            mNewLeaderBoardView.showError("No Data found for this group");
                        }
                        //mNewLeaderBoardView.updateRankersData(leaderBoardResponseObject.getLeaderBoardDataList());
                        if(leaderBoardResponseObject.getGroupLbDataList()!=null && leaderBoardResponseObject.getGroupLbDataList().size()>0)
                        {
                            // mNewLeaderBoardView.updateGroupData(leaderBoardResponseObject.getGroupLbDataList());
                        }

                    }
                    else {
                        mNewLeaderBoardView.showError("Something is not right");
                        mNewLeaderBoardView.hideProgressBar();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    mNewLeaderBoardView.showError("invalid format of response");
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                mNewLeaderBoardView.hideProgressBar();
                mNewLeaderBoardView.showError("Not able to connect to server");
            }
        });

        /*ApiClient.jsonRequest(OustSdkApplication.getContext(), Request.Method.GET, getLeaderboardUrl, new HashMap<>(), null, new ApiClient.NResultListener<JSONObject>() {
            @Override
            public void onResult(int resultCode, JSONObject tResult) {
                try {
                    Log.d(TAG, "onResult: "+tResult.toString());
                    if(tResult.getBoolean("success"))
                    {
                        leaderBoardResponse leaderBoardResponseObject = new leaderBoardResponse();
                        leaderBoardResponseObject = mGson.fromJson(tResult.toString(), leaderBoardResponse.class);
                        mNewLeaderBoardView.hideProgressBar();
                        if(leaderBoardResponseObject.getLeaderBoardDataList()!=null && leaderBoardResponseObject.getLeaderBoardDataList().size()>0)
                        {
                            mNewLeaderBoardView.updateGroupLBData(leaderBoardResponseObject.getLeaderBoardDataList(), groupName);
                        }
                        else
                        {
                            mNewLeaderBoardView.showError("No Data found for this group");
                        }
                        //mNewLeaderBoardView.updateRankersData(leaderBoardResponseObject.getLeaderBoardDataList());
                        if(leaderBoardResponseObject.getGroupLbDataList()!=null && leaderBoardResponseObject.getGroupLbDataList().size()>0)
                        {
                           // mNewLeaderBoardView.updateGroupData(leaderBoardResponseObject.getGroupLbDataList());
                        }

                    }
                    else {
                        mNewLeaderBoardView.showError("Something is not right");
                        mNewLeaderBoardView.hideProgressBar();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    mNewLeaderBoardView.showError("invalid format of response");
                }
            }

            @Override
            public void onFailure(int mError) {
                mNewLeaderBoardView.hideProgressBar();
                mNewLeaderBoardView.showError("Not able to connect to server");
            }
        });*/
    }

    public void getOverallLBGroupWise(String groupId, final String groupName){
        mNewLeaderBoardView.showProgressBar();

        String getLeaderboardUrl = OustSdkApplication.getContext().getResources().getString(R.string.getoverall_lb_url_group);
        getLeaderboardUrl = getLeaderboardUrl.replace("{userId}", activeUser.getStudentid());
        getLeaderboardUrl = getLeaderboardUrl+"?groupId="+groupId;

        getLeaderboardUrl = HttpManager.getAbsoluteUrl(getLeaderboardUrl);
        final Gson mGson = new Gson();
        Log.d(TAG, "getOverallLBGroup: "+getLeaderboardUrl);

        ApiCallUtils.doNetworkCall(Request.Method.GET, getLeaderboardUrl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d(TAG, "onResult: "+response.toString());
                    if(response.getBoolean("success")) {
                        leaderBoardResponse leaderBoardResponseObject = mGson.fromJson(response.toString(), leaderBoardResponse.class);
                        mNewLeaderBoardView.hideProgressBar();
                        if(leaderBoardResponseObject.getLeaderBoardDataList()!=null && leaderBoardResponseObject.getLeaderBoardDataList().size()>0) {
                            mNewLeaderBoardView.updateGroupLBData(leaderBoardResponseObject.getLeaderBoardDataList(), groupName);
                        }
                        else {
                            mNewLeaderBoardView.showError("No Data found for this group");
                        }
                        if(leaderBoardResponseObject.getGroupLbDataList()!=null && leaderBoardResponseObject.getGroupLbDataList().size()>0) {
                            // mNewLeaderBoardView.updateGroupData(leaderBoardResponseObject.getGroupLbDataList());
                        }
                    } else {
                        mNewLeaderBoardView.showError("Network connection failed");
                        mNewLeaderBoardView.hideProgressBar();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    mNewLeaderBoardView.showError("invalid format of response");
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                mNewLeaderBoardView.hideProgressBar();
                mNewLeaderBoardView.showError("Not able to connect to server");
            }
        });

        /*ApiClient.jsonRequest(OustSdkApplication.getContext(), Request.Method.GET, getLeaderboardUrl, new HashMap<>(), null, new ApiClient.NResultListener<JSONObject>() {
            @Override
            public void onResult(int resultCode, JSONObject tResult) {
                try {
                    Log.d(TAG, "onResult: "+tResult.toString());
                    if(tResult.getBoolean("success")) {
                        leaderBoardResponse leaderBoardResponseObject = mGson.fromJson(tResult.toString(), leaderBoardResponse.class);
                        mNewLeaderBoardView.hideProgressBar();
                        if(leaderBoardResponseObject.getLeaderBoardDataList()!=null && leaderBoardResponseObject.getLeaderBoardDataList().size()>0) {
                            mNewLeaderBoardView.updateGroupLBData(leaderBoardResponseObject.getLeaderBoardDataList(), groupName);
                        }
                        else {
                            mNewLeaderBoardView.showError("No Data found for this group");
                        }
                        if(leaderBoardResponseObject.getGroupLbDataList()!=null && leaderBoardResponseObject.getGroupLbDataList().size()>0) {
                            // mNewLeaderBoardView.updateGroupData(leaderBoardResponseObject.getGroupLbDataList());
                        }
                    } else {
                        mNewLeaderBoardView.showError("Network connection failed");
                        mNewLeaderBoardView.hideProgressBar();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    mNewLeaderBoardView.showError("invalid format of response");
                }
            }

            @Override
            public void onFailure(int mError) {
                mNewLeaderBoardView.hideProgressBar();
                mNewLeaderBoardView.showError("Not able to connect to server");
            }
        });*/
    }

    public void getUserData() {
        mNewLeaderBoardView.updateUserData(activeUser);
    }
}
