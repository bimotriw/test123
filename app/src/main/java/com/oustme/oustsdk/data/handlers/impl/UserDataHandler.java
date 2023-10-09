package com.oustme.oustsdk.data.handlers.impl;

import android.util.Log;


import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.data.handlers.ModuleType;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.tools.CommonTools;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by oust on 7/31/18.
 */

public abstract class UserDataHandler {
    private static final String TAG = "UserDataHandler";
    private final String LANDINGPAGE_BASENODE = "/landingPage/";
    private final String type;

    public UserDataHandler(String studentKey, long key, ModuleType type) {
        Log.d(TAG, "UserDataHandler: ");
        this.type = type.getModuleType();
        if (this.type.equalsIgnoreCase(ModuleType.COURSE.getModuleType())) {
            getCourseUserData(studentKey, key);
        } else {
            getUserData(studentKey, key);
        }
    }

    private void getCourseUserData(String studentKey, long key) {
        try {
            if (OustSdkTools.checkInternetStatus()) {
                String courseUserProgressBaseurl = OustSdkApplication.getContext().getResources().getString(R.string.get_course_user_progress_api);
                String tenantName = OustPreferences.get("tanentid").replaceAll(" ", "");
                courseUserProgressBaseurl = courseUserProgressBaseurl.replace("{org-id}", "" + tenantName);
                courseUserProgressBaseurl = courseUserProgressBaseurl.replace("{courseId}", "" + key);
                courseUserProgressBaseurl = courseUserProgressBaseurl.replace("{userId}", "" + studentKey);

                courseUserProgressBaseurl = HttpManager.getAbsoluteUrl(courseUserProgressBaseurl);

                Log.d(TAG, "loadUserDataFromFirebase: " + courseUserProgressBaseurl);
                ApiCallUtils.doNetworkCall(Request.Method.GET, courseUserProgressBaseurl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "loadUserDataFromFirebase - onResponse: " + response.toString());
                        Map<String, Object> userDataMap = new HashMap<>();
                        CommonTools commonTools = new CommonTools();
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            userDataMap = mapper.readValue(String.valueOf(response), new TypeReference<Map<String, Object>>() {
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                        if (userDataMap != null) {
                            String id = type.toUpperCase() + key;
                            CommonLandingData commonLandingData = new CommonLandingData();
                            commonTools.getCourseLandingDataForCPLv1(userDataMap, commonLandingData);
                            commonLandingData.setType(type.toUpperCase());
                            commonLandingData.setId(id);
                            notifyDataFound(commonLandingData, key, type);
                        } else {
                            notifyDataFound(null, key, type);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        notifyDataFound(null, key, type);

                    }
                });
            } else {
                notifyDataFound(null, key, type);
            }
        } catch (Exception e) {
            notifyDataFound(null, key, type);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void getUserData(String studentKey, final long key) {
        try {
            Log.d(TAG, "getUserData: ");
            String message = LANDINGPAGE_BASENODE + studentKey + "/" + type + "/" + key;
            ValueEventListener myassessmentListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue() != null) {
                            CommonTools commonTools = new CommonTools();
                            Map<String, Object> lpMap = (Map<String, Object>) dataSnapshot.getValue();
                            if (lpMap != null) {
                                String id = type.toUpperCase() + key;
                                CommonLandingData commonLandingData = new CommonLandingData();
                                commonTools.getCourseLandingDataForCPLv1(lpMap, commonLandingData);
                                commonLandingData.setType(type.toUpperCase());
                                commonLandingData.setId(id);
                                notifyDataFound(commonLandingData, key, type);
                            }
                        } else {
                            notifyDataFound(null, key, type);
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
            addListenerToFireBase(message, myassessmentListener);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void addListenerToFireBase(String message, ValueEventListener myassessmentListener) {
        Log.d(TAG, "addListenerToFireBase: ");
        OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(myassessmentListener);
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
    }

    public abstract void notifyDataFound(CommonLandingData commonLandingData, long key, String type);

}
