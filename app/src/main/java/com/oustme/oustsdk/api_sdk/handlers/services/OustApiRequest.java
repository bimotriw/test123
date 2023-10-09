package com.oustme.oustsdk.api_sdk.handlers.services;

import androidx.annotation.NonNull;
import android.util.Log;


import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.api_sdk.models.OustCatalogueData;
import com.oustme.oustsdk.api_sdk.models.OustEventCardData;
import com.oustme.oustsdk.api_sdk.models.OustEventCourseData;
import com.oustme.oustsdk.api_sdk.models.OustEventResponseData;
import com.oustme.oustsdk.api_sdk.models.OustModuleData;
import com.oustme.oustsdk.api_sdk.models.OustMultilingualCourseData;
import com.oustme.oustsdk.api_sdk.utils.ApiConstants;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.launcher.OustAuthData;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.STUDE_KEY;

/**
 * Created by oust on 4/24/19.
 */

public class OustApiRequest {

    private OustApiListener oustApiListener;
    public OustApiRequest(OustApiListener oustApiListener) {
        this.oustApiListener = oustApiListener;
    }

    private int eventId = 0;

    public void getNextModuleData(final OustAuthData oustAuthData, OustModuleData moduledata) {

        String nextEventUrl = ApiConstants.NEXT_EVENT_URL;
//        nextEventUrl = nextEventUrl.replace("userKey",oustAuthData.getUsername());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, nextEventUrl,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                parseData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onError(""+OustSdkApplication.getContext().getResources().getString(R.string.retry_internet_msg));
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("api-key", "test_secret_key");
                    params.put("org-Id", oustAuthData.getOrgId());
                    params.put("userId",oustAuthData.getUsername());
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }

                return params;
            }
        };
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
        OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");

    }

    private void onError(String error) {
        oustApiListener.onError(error.equals("")?"Something went wrong!":error);
    }

    private void parseData(JSONObject response) {
        OustModuleData oustModuleData = new OustModuleData();
        oustModuleData.setId(response.optLong("contentId")+"");
        oustModuleData.setRequestType(response.optString("contentType"));
        new OustIntentHandler(oustModuleData).launchNewIntent();
        //oustApiListener.onModuleStatusChange((OustModuleData) null,response.toString());
    }

    public void getModuleStatusReport(final OustAuthData oustAuthData, OustModuleData moduledata) {

        String nextEventUrl = ApiConstants.GET_STATUS_URL;
//        nextEventUrl = nextEventUrl.replace("userKey",oustAuthData.getUsername());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, nextEventUrl,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                parseModuleStatusData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onError(""+OustSdkApplication.getContext().getResources().getString(R.string.retry_internet_msg));
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("api-key", "test_secret_key");
                    params.put("org-Id", oustAuthData.getOrgId());
                    params.put("userId",oustAuthData.getUsername());
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }

                return params;
            }
        };
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
        OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");

    }

    private void parseModuleStatusData(JSONObject response) {
        if(response.optBoolean("success")){
            OustModuleData oustModuleData = new OustModuleData(response.optString("contentId"),"","status");
            oustApiListener.onModuleStatusChange(oustModuleData,response.optString("eventStatus"));
        }else{
            onError(""+OustSdkApplication.getContext().getResources().getString(R.string.retry_internet_msg));
        }
    }

    public void launchCatalogue(){
        String key = OustPreferences.get(STUDE_KEY);
        if(key!=null) {
            //OustFirebaseTools.getRootRef().child("landingPage/" + key + "/catalogueId").addListenerForSingleValueEvent(new ValueEventListener() {
            OustFirebaseTools.getRootRef().child("landingPage/" + key + "/catalogue").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //if (dataSnapshot != null && (dataSnapshot.getValue() != null) && (long) dataSnapshot.getValue() != 0) {
                    if (dataSnapshot != null && (dataSnapshot.getValue() != null)) {
                        Log.d("CatalogueData",""+dataSnapshot.toString());
                        extractCatologeData(dataSnapshot);
                        /*long catalogueid = (long) dataSnapshot.getValue();
                        OustModuleData oustModuleData = new OustModuleData();
                        oustModuleData.setId("" + catalogueid);
                        oustModuleData.setRequestType("Catalogue");
                        new OustIntentHandler(oustModuleData).launchCatalogue();*/
                    } else {
                        extractCatologeData(null);
                        //onError(getResources().getString(R.string.no_catalogue_available"));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    extractCatologeData(null);
                    //onError(""+getResources().getString(R.string.retry_internet_msg"));
                }
            });
        }
    }

    private void extractCatologeData(DataSnapshot dataSnapshot){
        OustCatalogueData oustCatalogueData = new OustCatalogueData();
        if(dataSnapshot!=null){
            Object o1 = dataSnapshot.getValue();
            if (o1.getClass().equals(ArrayList.class)) {
                List<Object> lpMainList = (List<Object>) dataSnapshot.getValue();
                Map<String, Object> lpMap = (Map<String, Object>) lpMainList.get(0);
                if(lpMap!=null) {
                    if(lpMap.containsKey("bannerImg")){
                        oustCatalogueData.setBanner((String) lpMap.get("bannerImg"));
                    }

                    if(lpMap.containsKey("elementId")){
                        oustCatalogueData.setCatalogueId(OustSdkTools.convertToLong(lpMap.get("elementId")));
                    }

                    if(lpMap.containsKey("name")){
                        oustCatalogueData.setTitle((String) lpMap.get("name"));
                    }
                }

            } else if (o1.getClass().equals(HashMap.class)) {
                Map<String, Object> lpMainMap = (Map<String, Object>) dataSnapshot.getValue();
                Map<String, Object> mainMap = null;
                //String key = "";
                for (Map.Entry<String, Object> entry : lpMainMap.entrySet()) {
                    mainMap = (Map<String, Object>) entry.getValue();
                    //key = entry.getKey();
                }

                if(mainMap.containsKey("bannerImg")){
                    oustCatalogueData.setBanner((String) mainMap.get("bannerImg"));
                }

                if(mainMap.containsKey("elementId")){
                    oustCatalogueData.setCatalogueId(OustSdkTools.convertToLong(mainMap.get("elementId")));
                }

                if(mainMap.containsKey("name")){
                    oustCatalogueData.setTitle((String) mainMap.get("name"));
                }
            }
        }

        oustCatalogueData.setRequestType("Catalogue");
        new OustIntentHandler(oustCatalogueData).launchCatalogue();
    }

    public void getEventModuleReport(final OustAuthData oustAuthData, final JSONObject jsonRequest, final String eventType) {
        Log.d("EventApi",""+jsonRequest.toString());
        final String eventUrl = ApiConstants.GET_EVENT_STATUS_URL;
        Log.d("EventApi", "getEventModuleReport: "+eventUrl);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, eventUrl, jsonRequest, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("EventApi","Response:"+response);
                try{
                    eventId = jsonRequest.getInt("eventId");
                }catch (JSONException e){
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                if(eventType.equalsIgnoreCase("EventStatus")){
                    parseModuleEventStatusData(response);
                }else if(eventType.equalsIgnoreCase("LaunchModule")){
                    parseModuleEventLaunchData(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Log.e("EventApi", "" + error.getMessage());
                }catch (Exception e){
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                onError(""+OustSdkApplication.getContext().getResources().getString(R.string.retry_internet_msg));
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    //params.put("api-key", "test_secret_key");
                    params.put("org-Id", oustAuthData.getOrgId());
                    params.put("userId",oustAuthData.getUsername());

                    //params.put("Content-Type","application/json; charset=utf-8");
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                return params;
            }
        };

        try {
            Log.d("EventApi","contentType: "+jsonObjReq.getBodyContentType());
            Log.d("EventApi","Headers: "+jsonObjReq.getHeaders().toString());
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }

        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
        OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");

    }

    private void parseModuleEventStatusData(JSONObject response) {
        if(response!=null && response.optBoolean("success")){
            if(response.optString("contentType").equalsIgnoreCase("COURSE")){
                Gson gson = new Gson();
                OustEventResponseData oustEventResponseData = gson.fromJson(response.toString(), OustEventResponseData.class);
                oustApiListener.onEventCourseStatusChange(oustEventResponseData, response.optString("eventStatus"));
            }else {
                OustModuleData oustModuleData = new OustModuleData(response.optString("contentId"),response.optString("contentType"),"EventStatus");
                oustModuleData.setEventId(eventId);
                oustApiListener.onEventModuleStatusChange(oustModuleData, response.optString("eventStatus"));
            }

            /*JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("contentType", response.optString("contentType"));
                jsonObject.put("contentId", response.optLong("contentId"));
                jsonObject.put("contentStatus", response.optString("eventStatus"));
                jsonObject.put("requestType", ApiConstants.GET_EVENT_STATUS_URL);
            } catch (JSONException e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            oustApiListener.onEventModuleStatusChange(jsonObject);*/
        }else{
            onError("Failed to get Event status");
        }
    }

    private void parseModuleEventLaunchData(JSONObject response) {
        if(response==null){
            onError(""+OustSdkApplication.getContext().getResources().getString(R.string.retry_internet_msg));
            return;
        }
        if(!response.has("contentType") || response.optString("contentType")==null){
            Log.d("EventApi",""+response.optString("exceptionData"));
            onError(""+response.optString("exceptionData"));
            return;
        }

        if(response.optString("contentType").equalsIgnoreCase("CPL")) {
            getCPLData(response);
            return;
        }else{
            if (!response.has("contentId") || response.optInt("contentId") == 0) {
                Log.d("EventApi",""+response.optString("exceptionData"));
                onError(""+response.optString("exceptionData"));
                return;
            }
        }

        OustStaticVariableHandling.getInstance().setOustApiListener(oustApiListener);
        if(response.optString("contentType").equals("CARD")) {
            OustEventCardData oustEventCardData = new OustEventCardData();
            oustEventCardData.setCardId(response.optLong("contentId"));
            oustEventCardData.setLevelId(response.optLong("courselevelId"));
            oustEventCardData.setCourseId(response.optLong("parentEventId"));
            oustEventCardData.setRequestType(response.optString("contentType"));
            oustEventCardData.setEventId(eventId);
            new OustIntentHandler(oustEventCardData).launchCardIntent();
        }else if(response.has("regularMode") && response.optBoolean("regularMode")){
            OustEventCourseData oustEventCourseData = new OustEventCourseData();
            oustEventCourseData.setCourseId(response.optLong("contentId"));
            oustEventCourseData.setRequestType(response.optString("contentType"));
            oustEventCourseData.setRegularMode(true);
            oustEventCourseData.setEventId(eventId);
            new OustIntentHandler(oustEventCourseData).launchRegularCourse();
        }else {
            OustModuleData oustModuleData = new OustModuleData();
            oustModuleData.setId(response.optLong("contentId") + "");
            oustModuleData.setRequestType(response.optString("contentType"));
            oustModuleData.setEventId(eventId);
            new OustIntentHandler(oustModuleData).launchNewIntent();
        }
        //oustApiListener.onModuleStatusChange((OustModuleData) null,response.toString());
    }

    private void getCPLData(final JSONObject response) {
        String key = OustPreferences.get(STUDE_KEY);
        if(key!=null) {
            String msg = "landingPage/" + key + "/cpl";
            Log.d("CPLData", "getCPLData: "+msg);
            OustFirebaseTools.getRootRef().child(msg).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null && (dataSnapshot.getValue() != null)) {
                        Log.d("CPLData",""+dataSnapshot.getValue());
                        try {
                            String cplKey="";
                            Map<String, Object> lpMainMap = (Map<String, Object>) dataSnapshot.getValue();
                            for (Map.Entry<String, Object> entry : lpMainMap.entrySet()) {
                                cplKey = entry.getKey();
                            }
                            String cplId = response.optLong("contentId") + "";
                            /*OustStaticVariableHandling.getInstance().setOustApiListener(oustApiListener);
                            OustModuleData oustModuleData = new OustModuleData();
                            oustModuleData.setId(cplId);
                            oustModuleData.setRequestType(response.optString("contentType"));
                            oustModuleData.setEventId(eventId);
                            new OustIntentHandler(oustModuleData).launchCPL();*/

                            if(cplId==cplKey || cplId.equalsIgnoreCase(cplKey)){
                                OustStaticVariableHandling.getInstance().setOustApiListener(oustApiListener);
                                OustModuleData oustModuleData = new OustModuleData();
                                oustModuleData.setId(response.optLong("contentId") + "");
                                oustModuleData.setRequestType(response.optString("contentType"));
                                oustModuleData.setEventId(eventId);
                                new OustIntentHandler(oustModuleData).launchCPL();
                            }else{
                                onError("CPL data is mismatch");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            onError("Couldn't able to load CPL");
                        }
                    } else {
                        onError("Couldn't able to load CPL");
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    onError(""+OustSdkApplication.getContext().getResources().getString(R.string.retry_internet_msg));
                }
            });
        }
    }

    public void launchCourseCard(){
        Log.d("Login", "launchCourseCard: ");
        String key = OustPreferences.get(STUDE_KEY);
        if(key!=null) {
            OustFirebaseTools.getRootRef().child("landingPage/" + key + "/course").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null && (dataSnapshot.getValue() != null)) {
                        Log.d("CourseData",""+dataSnapshot.getValue());
                        extractCourseData(dataSnapshot);
                        //new OustIntentHandler().launchCourse();
                    } else {
                        onError("Course is not distributed. Please try again later.");
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    onError(""+OustSdkApplication.getContext().getResources().getString(R.string.retry_internet_msg));
                }
            });
        }
    }

    private void extractCourseData(DataSnapshot dataSnapshot){
        long courseId = 0;
        Object o1 = dataSnapshot.getValue();
        if (o1.getClass().equals(ArrayList.class)) {
            List<Object> lpMainList = (List<Object>) dataSnapshot.getValue();
            Map<String, Object> lpMap = (Map<String, Object>) lpMainList.get(lpMainList.size()-1);
            if(lpMap!=null) {
                if(lpMap.containsKey("elementId")){
                    courseId = OustSdkTools.convertToLong(lpMap.get("elementId"));
                }
            }

        } else if (o1.getClass().equals(HashMap.class)) {
            Map<String, Object> lpMainMap = (Map<String, Object>) dataSnapshot.getValue();
            Map<String, Object> mainMap = null;
            //String key = "";
            for (Map.Entry<String, Object> entry : lpMainMap.entrySet()) {
                mainMap = (Map<String, Object>) entry.getValue();
                //key = entry.getKey();
            }

            if(mainMap.containsKey("elementId")){
                courseId = OustSdkTools.convertToLong(mainMap.get("elementId"));
            }
        }

        Log.d("courseData", "extractCourseData: "+courseId);
        if(courseId>0) {
            OustStaticVariableHandling.getInstance().setOustApiListener(oustApiListener);
            OustEventCourseData oustEventCourseData = new OustEventCourseData();
            oustEventCourseData.setCourseId(courseId);
            oustEventCourseData.setRequestType("course");
            oustEventCourseData.setEventId(eventId);
            new OustIntentHandler(oustEventCourseData).launchCourse();
        }else{
            onError("Error on loading course data");
        }
    }

    public void distributeCplAndLaunch(final OustAuthData oustAuthData, String group) {
        Log.d("distributeCPL",""+group);

        String autoDistributeCPL_url = OustSdkApplication.getContext().getResources().getString(R.string.auto_distribute_cpl_for_cpl_group);
        //autoDistributeCPL_url = autoDistributeCPL_url.replace("{cplGroup}", group);

        final String eventUrl = HttpManager.getAbsoluteUrl(autoDistributeCPL_url);

        Map<String, Object> postParam = new HashMap<>();
        postParam.put("studentid", oustAuthData.getUsername());
        postParam.put("cplGroup", group);

        JSONObject jsonObject = new JSONObject(postParam);
        JSONObject jsonParams = OustSdkTools.getRequestObjectforJSONObject(jsonObject);

        Log.d("distibuteCPL", "distributeCplAndLaunch: "+jsonParams.toString());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, eventUrl, jsonParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("distributeCPL","Response:"+response.toString());
                if(response!=null && response.optBoolean("success")){
                    OustStaticVariableHandling.getInstance().setOustApiListener(oustApiListener);
                    new OustIntentHandler().launchCPL();
                }else{
                    onError("Failed to distribute the CPL");
                }
                //onError(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Log.e("distributeCPL", "" + error.getMessage());
                }catch (Exception e){
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                onError("Failed to distribute the CPL");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    //params.put("api-key", "test_secret_key");
                    params.put("org-Id", oustAuthData.getOrgId());
                    params.put("studentid",oustAuthData.getUsername());
                    //params.put("Content-Type","application/json; charset=utf-8");
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                return params;
            }
        };

        try {
            Log.d("distributeCPL","contentType: "+jsonObjReq.getBodyContentType());
            Log.d("distributeCPL","Headers: "+jsonObjReq.getHeaders().toString());
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }

        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
        OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");
    }

    public void launchSpecialCourse(final OustAuthData oustAuthData) {
        Log.d("launchSpecialCourse","");

        String autoDistributeSpecialCourse_url = OustSdkApplication.getContext().getResources().getString(R.string.distibute_special_course);
        autoDistributeSpecialCourse_url = autoDistributeSpecialCourse_url.replace("{userId}", oustAuthData.getUsername());

        final String eventUrl = HttpManager.getAbsoluteUrl(autoDistributeSpecialCourse_url);

        /*Map<String, Object> postParam = new HashMap<>();
        postParam.put("studentid", oustAuthData.getUsername());
        JSONObject jsonObject = new JSONObject(postParam);
        JSONObject jsonParams = OustSdkTools.getRequestObjectforJSONObject(jsonObject);*/

        JSONObject jsonParams = OustSdkTools.getRequestObjectforJSONObject(null);

        Log.d("launchSpecialCourse: ", eventUrl);
        Log.d("launchSpecialCourse", "JsonParams: "+jsonParams.toString());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, eventUrl, jsonParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("launchSpecialCourse","Response:"+response.toString());
                parseSpecialCourseLaunchData(response);

                /*if(response!=null && response.optBoolean("success")){
                    parseSpecialCourseLaunchData(response);
                }else{
                    onError("Failed to distribute the course");
                }*/
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Log.e("launchSpecialCourse", "" + error.getMessage());
                }catch (Exception e){
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                //onError("Failed to distribute the Course");

                parseSpecialCourseLaunchData(null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    //params.put("api-key", "test_secret_key");
                    params.put("org-Id", oustAuthData.getOrgId());
                    params.put("studentid",oustAuthData.getUsername());
                    //params.put("Content-Type","application/json; charset=utf-8");
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                return params;
            }
        };

        try {
            Log.d("launchSpecialCourse","contentType: "+jsonObjReq.getBodyContentType());
            Log.d("launchSpecialCourse","Headers: "+jsonObjReq.getHeaders().toString());
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }

        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
        OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");
    }

    private void parseSpecialCourseLaunchData(JSONObject response) {
        if (response == null) {
            onError("Failed to distribute the Course");

            /*OustStaticVariableHandling.getInstance().setOustApiListener(oustApiListener);
            OustMultilingualCourseData oustMultilingualCourseData = new OustMultilingualCourseData();
            oustMultilingualCourseData.setCourseChildId("227");
            oustMultilingualCourseData.setCourseParentId("230");
            oustMultilingualCourseData.setCourseLanguageId("");
            oustMultilingualCourseData.setCourseLanguageName("");
            new OustIntentHandler(oustMultilingualCourseData).launchMultilingualCourse();*/

            return;
        }

        if(response!=null && response.optBoolean("success")){
            if(!response.has("childCourseId") || !response.has("multilingualCourseId") || response.optLong("childCourseId")==0 || response.optLong("multilingualCourseId")==0){
                Log.d("EventApi",""+response.optString("exceptionData"));
                onError("Failed to distribute the Course");
                return;
            }

            OustStaticVariableHandling.getInstance().setOustApiListener(oustApiListener);
            OustMultilingualCourseData oustMultilingualCourseData = new OustMultilingualCourseData();
            oustMultilingualCourseData.setCourseChildId(""+response.optLong("childCourseId"));
            oustMultilingualCourseData.setCourseParentId(""+response.optLong("multilingualCourseId"));
            oustMultilingualCourseData.setCourseLanguageId(""+response.optLong("languageId"));
            oustMultilingualCourseData.setCourseLanguageName(response.optString("English"));
            oustMultilingualCourseData.setEventId(eventId);
            new OustIntentHandler(oustMultilingualCourseData).launchMultilingualCourse();
        }else{
            onError(""+response.optString("exceptionData"));
            return;
        }
    }

    public void getLanguage(final OustAuthData oustAuthData) {
        Log.d("getLanguage", "getLanguage: ");
        String url = OustSdkApplication.getContext().getResources().getString(R.string.user_preferred_language_get);
        url = url.replace("{userId}",""+oustAuthData.getUsername());

        final String eventUrl = HttpManager.getAbsoluteUrl(url);

        /*Map<String, Object> postParam = new HashMap<>();
        postParam.put("studentid", oustAuthData.getUsername());
        JSONObject jsonObject = new JSONObject(postParam);*/

        JSONObject jsonParams = OustSdkTools.getRequestObjectforJSONObject(null);
        Log.d("getLanguage", "jsonparams: "+jsonParams.toString());
        Log.d("getLanguage", "eventUrl: "+eventUrl);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, eventUrl, jsonParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("getLanguage","Response:"+response.toString());
                if(response!=null && response.optBoolean("success")){
                    if(oustApiListener!=null){
                        oustApiListener.onUserPreferredLanguage(response.optString("language"));
                    }
                }else{
                    if(oustApiListener!=null) {
                        oustApiListener.onError("Failed to get user preferred language");
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Log.e("getLanguage", "" + error.getMessage());
                }catch (Exception e){
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                if(oustApiListener!=null) {
                    oustApiListener.onError("Failed to get user preferred language");
                }
                //onError("Failed to update the language");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    //params.put("api-key", "test_secret_key");
                    params.put("org-Id", oustAuthData.getOrgId());
                    //params.put("studentid",oustAuthData.getUsername());
                    //params.put("Content-Type","application/json; charset=utf-8");
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                return params;
            }
        };

        try {
            Log.d("getLanguage","Headers: "+jsonObjReq.getHeaders().toString());
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
        OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");
    }


}
