package com.oustme.oustsdk.activity.common.languageSelector.presenter;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.TENANT_ID;

import android.util.Log;


import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.languageSelector.model.request.ChildCplDistributionRequest;
import com.oustme.oustsdk.activity.common.languageSelector.model.request.CplDistributionRequest;
import com.oustme.oustsdk.activity.common.languageSelector.model.response.LanguageListResponse;
import com.oustme.oustsdk.activity.common.languageSelector.view.LanguageView;
import com.oustme.oustsdk.response.CheckModuleDistributedOrNot;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LanguagePresenter {
    private static final String TAG = "LanguagePresenter";

    LanguageView.LView mView;

    public LanguagePresenter(LanguageView.LView view) {
        mView = view;
    }

    public void getLanguages(long cplId) {
        mView.showProgressBar(1);
        String LanguageURL = OustSdkApplication.getContext().getResources().getString(R.string.get_language_list);
        LanguageURL = LanguageURL.replace("{cplId}", "" + cplId);
        final Gson mGson = new Gson();
        LanguageURL = HttpManager.getAbsoluteUrl(LanguageURL);
        Log.d(TAG, "getLanguages: " + LanguageURL);

        ApiCallUtils.doNetworkCall(Request.Method.GET, LanguageURL, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
            @Override
            public void onResponse(JSONObject response) {
                mView.hideProgressBar(1);
                mView.extractCplLanguageData(response, mGson);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                mView.onError("");
                mView.hideProgressBar(1);
            }
        });
    }

    public void distributeCplData(String mCplIDForLanguage, String mSelectedLanguage, int mSelectedLanguageId) {
        try {
            ActiveUser activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
            if (activeUser != null) {
                mView.showProgressBar(2);
                String user_name = activeUser.getStudentid();
                String cplURL = OustSdkApplication.getContext().getResources().getString(R.string.cpl_language_switch_and_distribution);

                ChildCplDistributionRequest childCplDistributionRequest = new ChildCplDistributionRequest();
                List<String> userNameStringList = new ArrayList<>();
                List<String> groupIdStringList = new ArrayList<>();
                userNameStringList.add(user_name);
                childCplDistributionRequest.setUsers(userNameStringList);
                childCplDistributionRequest.setLanguageSwitch(true);
                childCplDistributionRequest.setEnableMultipleCPL(true);
                childCplDistributionRequest.setAllUsers(false);
                childCplDistributionRequest.setGroupIdList(groupIdStringList);
                childCplDistributionRequest.setSendSMS(false);
                childCplDistributionRequest.setSendEmail(false);
                childCplDistributionRequest.setSendNotification(false);
                childCplDistributionRequest.setAutoDistributeAsFeed(false);
                childCplDistributionRequest.setReusabilityAllowed(true);

                final Gson mGson = new Gson();
                String requestDataJson = mGson.toJson(childCplDistributionRequest);

                cplURL = HttpManager.getAbsoluteUrl(cplURL);
                cplURL = cplURL.replace("{newCPLId}", String.valueOf(mCplIDForLanguage));

                ApiCallUtils.doNetworkCallWithErrorBody(Request.Method.POST, cplURL, OustSdkTools.getRequestObject(requestDataJson), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LanguageListResponse languageListResponse = mGson.fromJson(response.toString(), LanguageListResponse.class);
                        if (languageListResponse.isSuccess())
                            mView.updateLanguage(languageListResponse, mSelectedLanguage, mSelectedLanguageId, mCplIDForLanguage);
                        else {
                            mView.onErrorCPLData();
                            mView.hideProgressBar(2);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: error--> " + error);
                        mView.hideProgressBar(2);
                        mView.onErrorCPLData();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            mView.hideProgressBar(2);
            mView.onErrorCPLData();
        }
    }

    public void getCPLData(String mCplIDForLanguage, String mSelectedLanguage, int mSelectedLanguageId, String mSelectedLanguageCplId) {
        ActiveUser activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
        try {
            if (activeUser != null) {
                mView.showProgressBar(2);
                String user_id = activeUser.getStudentid();
                String cplURL = OustSdkApplication.getContext().getResources().getString(R.string.get_cpl_data);
                final Gson mGson = new Gson();
                cplURL = HttpManager.getAbsoluteUrl(cplURL);
                String orgId = OustPreferences.get(TENANT_ID);
                CplDistributionRequest cplDistributionRequest = new CplDistributionRequest();
                cplDistributionRequest.setCplId(Integer.valueOf(mCplIDForLanguage));
                cplDistributionRequest.setLanguageId(mSelectedLanguageId);
                if (orgId != null) {
                    cplDistributionRequest.setOrgId(orgId.trim());
                    cplDistributionRequest.setTenantId(orgId.trim());
                }
                cplDistributionRequest.setStudentid(user_id);
                cplDistributionRequest.setReusabilityAllowed(true);

                String requestDataJson = mGson.toJson(cplDistributionRequest);
                Log.d(TAG, "CplDistribution API--> " + cplURL);

                try {
                    ApiCallUtils.doNetworkCall(Request.Method.PUT, cplURL, OustSdkTools.getRequestObject(requestDataJson), new ApiCallUtils.NetworkCallback() {
                        @Override
                        public void onResponse(JSONObject response) {
                            LanguageListResponse languageListResponse = mGson.fromJson(response.toString(), LanguageListResponse.class);
                            if (languageListResponse.isSuccess())
                                mView.updateLanguage(languageListResponse, mSelectedLanguage, mSelectedLanguageId, mSelectedLanguageCplId);
                            else {
                                mView.hideProgressBar(2);
                                mView.onErrorCPLData();
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mView.hideProgressBar(2);
                            mView.onErrorCPLData();
                        }
                    });
                } catch (Exception e) {
                    mView.hideProgressBar(2);
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }
        } catch (Exception ex) {
            mView.hideProgressBar(2);
            ex.printStackTrace();
        }
    }
}
