package com.oustme.oustsdk.tools;

import android.content.pm.PackageInfo;
import android.os.Looper;
import androidx.annotation.NonNull;
import android.util.Log;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;

import javax.net.ssl.SSLContext;

/**
 * Created by shilpysamaddar on 07/03/17.
 */

public class HttpManager {
    private static final String POINTING_BASE_URL = "pointing_url";

    private static final String TAG = "HttpManager";
    //new central dev url
    private static String WEB_APP_STAGE = "https://stagew2.oustme.com/#!/";
    private static String WEB_APP_PROD = "https://webapp.oustme.com/#!/";
    private static String BASE_URL_STAGE = "http://stage-central.oustme.com/rest/services/";
    private static String BASE_URL_NEW_STAGE = "http://newstage.oustme.com/rest/services/";

    private static  String NEWBASE_URL_STAGE_INDIA="http://newstage.oustme.com/rest/services/";
    private static  String BASE_URL_STAGE_INDIA="https://stage-central-india.oustme.com:443/rest/services/";

    private static String BASE_URL_STAGE_MUMBAI = "http://stage-mumbai-portal-services.oustme.com/rest/services/";

    //new dev url
    private static String BASE_URL_DEV = "http://dev-api.oustme.com/rest/services/";

    private static String BASE_URL_PROD = "https://oust-central.oustme.com:443/rest/services/";

    private static String BASE_URL = "http://stage-central.oustme.com/rest/services/"; //staging server


    private static String BASE_DEV_MUMBAI = "https://dev-central-mumbai.oustme.com:443/rest/services/";

    private static String BASE_URL_PROD_MUMBAI = "https://central-india.oustme.com/rest/services/";

    private static String BASE_FOR_SIGNIN = "https://applicationlb-test.oustme.com/rest/services/";

    public static void setBaseUrl() {

//        String apiEndPoint=OustPreferences.get("apiServerEndpoint");
//        if((apiEndPoint!=null)&&(!apiEndPoint.isEmpty())) {
//            if(apiEndPoint.endsWith("/")){
//                BASE_URL=apiEndPoint;
//            }else {
//                BASE_URL = apiEndPoint + "/";
//            }
//        }else {
//            BASE_URL=MAIN_URL;
//        }
    }

    private static int DEFAULT_TIMEOUT = 60000;


    public static String getAbsoluteUrl(String relativeUrl) {
        // 1 - for dev pointing
        // 2 - stage pointing
        // 3 - for Production pointing
 /*       String baseURLPath = "/system/appConfig/baseURL";
        OustFirebaseTools.getRootRef().child(baseURLPath ).keepSynced(true);
        OustFirebaseTools.getRootRef().child(baseURLPath).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null) {
                    try {
                        long url = (long) dataSnapshot.getValue();
                        OustPreferences.saveTimeForNotification(POINTING_BASE_URL, url);
                    } catch (Exception e) {
                        OustPreferences.saveTimeForNotification(POINTING_BASE_URL, 3);
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
                else {
                    OustPreferences.saveTimeForNotification(POINTING_BASE_URL, 3);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: "+databaseError.getMessage());
                OustPreferences.saveTimeForNotification(POINTING_BASE_URL, 3);
            }
        });
        long url = OustPreferences.getTimeForNotification(POINTING_BASE_URL);
        //1 - for dev pointing 2 - stage pointing 3 - for Production pointing
        if(url!=0)
        {
            if(url==1){
                Log.d(TAG, "getAbsoluteUrl: "+BASE_URL_DEV + relativeUrl);
                return BASE_URL_DEV + relativeUrl;
            }
            else if(url==2)
            {
                Log.d(TAG, "getAbsoluteUrl: "+BASE_URL_STAGE + relativeUrl);
                return BASE_URL_STAGE + relativeUrl;
            }
            else {
                Log.d(TAG, "getAbsoluteUrl: "+BASE_URL_PROD + relativeUrl);
                return BASE_URL_PROD + relativeUrl;
            }
        }
        else {
            OustFirebaseTools.getRootRef().child(baseURLPath ).keepSynced(true);
            OustFirebaseTools.getRootRef().child(baseURLPath).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue()!=null) {
                        try {
                            long url = (long) dataSnapshot.getValue();
                            OustPreferences.saveTimeForNotification(POINTING_BASE_URL, url);
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    }
                    else {
                        OustPreferences.saveTimeForNotification(POINTING_BASE_URL, 3);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: "+databaseError.getMessage());
                    OustPreferences.saveTimeForNotification(POINTING_BASE_URL, 3);
                }
            });
        }
        Log.d(TAG, "getAbsoluteUrl: "+BASE_URL_PROD + relativeUrl);*/
        String baseURL = OustPreferences.get(AppConstants.StringConstants.BASE_URL_FROM_API);
        if(baseURL==null || baseURL.isEmpty())
        {
            baseURL = BASE_URL_PROD;
        }
       /* else
        {
            baseURL = BASE_URL_PROD;
        }*/

        return baseURL + relativeUrl;
    }

    public static String getUrlForWebApp(String relativeUrl,String orgId) {
        String baseURL = WEB_APP_STAGE;
        if(orgId!=null && !orgId.isEmpty()&&!orgId.equalsIgnoreCase("oustqa"))
        {
            baseURL = WEB_APP_PROD;
        }

        return baseURL + relativeUrl;
    }


    private static final String CONTENT_TYPE_JSON = "application/json";

    // A SyncHttpClient is an AsyncHttpClient
    public static AsyncHttpClient syncHttpClient = new SyncHttpClient(true, 80, 8443);
    public static AsyncHttpClient asyncHttpClient = new AsyncHttpClient(true, 80, 8443);

    private static SSLContext context;

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        DEFAULT_TIMEOUT = 60000;
        getClient().get(getAbsoluteUrl(url), params, responseHandler);
    }

    private static AsyncHttpClient getClient() {
        // Return the synchronous HTTP client when the thread is not prepared
        try {
            PackageInfo pinfo = OustSdkApplication.getContext().getPackageManager().getPackageInfo(OustSdkApplication.getContext().getPackageName(), 0);
            syncHttpClient.addHeader("app-version", pinfo.versionName);
            asyncHttpClient.addHeader("app-version", pinfo.versionName);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        if (Looper.myLooper() == null) {
            syncHttpClient.setTimeout(DEFAULT_TIMEOUT);
            syncHttpClient.setConnectTimeout(DEFAULT_TIMEOUT);
            syncHttpClient.setResponseTimeout(DEFAULT_TIMEOUT);
            syncHttpClient.addHeader("deviceplatform", "android");
//            if(OustAppEvents.getInstance().getApiKeyClass()!=null) {
//                syncHttpClient.addHeader("api-key", OustAppEvents.getInstance().getApiKeyClass().getApiKey());
//            }
            if (OustPreferences.get("oust_apikey") != null) {
                syncHttpClient.addHeader("api-key", OustPreferences.get("oust_apikey"));
            }
            if (OustPreferences.get("tanentid") != null) {
                syncHttpClient.addHeader("org-id", OustPreferences.get("tanentid"));
            }
            return syncHttpClient;
        }

        asyncHttpClient.setTimeout(DEFAULT_TIMEOUT);
        asyncHttpClient.setConnectTimeout(DEFAULT_TIMEOUT);
        asyncHttpClient.setResponseTimeout(DEFAULT_TIMEOUT);
        if (OustPreferences.get("oust_apikey") != null) {
            asyncHttpClient.addHeader("api-key", OustPreferences.get("oust_apikey"));
        }
        if (OustPreferences.get("tanentid") != null) {
            asyncHttpClient.addHeader("org-id", OustPreferences.get("tanentid"));
        }
        asyncHttpClient.addHeader("deviceplatform", "android");
        if (OustPreferences.get("oust_apikey") != null) {
            asyncHttpClient.addHeader("api-key", OustPreferences.get("oust_apikey"));
        }
        return asyncHttpClient;
    }


}
