package com.oustme.oustsdk.LiveClasses;

import android.util.Log;


import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.LiveClasses.Request.MeetingEventRequest;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

public class MeetingActivityRepository {

    private static MeetingActivityRepository instance;

    public static MeetingActivityRepository getInstance() {
        if (instance == null)
            instance = new MeetingActivityRepository();
        return instance;
    }

    public void saveMetingEvent(long liveClassId, long liveClassMeetingMapId, String userId, String eventType, String eventValue, String orgId) {
        try {
            if (OustSdkTools.checkInternetStatus()) {
                String MeetingEventStatusUrl = OustSdkApplication.getContext().getResources().getString(R.string.meeting_event_status);
                MeetingEventStatusUrl = HttpManager.getAbsoluteUrl(MeetingEventStatusUrl);

                MeetingEventRequest meetingEventRequest = new MeetingEventRequest();
                meetingEventRequest.setLiveClassId(liveClassId);
                meetingEventRequest.setLiveClassMeetingMapId(liveClassMeetingMapId);
                meetingEventRequest.setUserId(userId);
                meetingEventRequest.setEventValue(eventValue);
                meetingEventRequest.setEventType(eventType);
                meetingEventRequest.setOrgId(orgId);

                Gson gson = new GsonBuilder().create();
                String jsonParams = gson.toJson(meetingEventRequest);
                Log.e("TAG", "saveMetingEvent: MeetingEventStatusUrl --> " + MeetingEventStatusUrl);
                ApiCallUtils.doNetworkCall(Request.Method.POST, MeetingEventStatusUrl, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.optBoolean("success")) {
                            Log.d("TAG", "saveMetingEvent - onResponse: successfully send Meeting Event");
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
