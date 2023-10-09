package com.oustme.oustsdk.firebase.common;


import android.util.Log;



import androidx.annotation.Keep;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;

/**
 * Created by shilpysamaddar on 07/03/17.
 */

@Keep
public class OustFirebaseTools {
    private static final String TAG = "OustFirebaseTools";
    private static DatabaseReference oustFirebase;
    private static boolean isSetPersistance = false;

    //    dev url
//    private static String firebaseUrl="https://blinding-fire-600.firebaseio.com/";

    //    prod url
    private static String firebaseUrl = "https://blinding-fire-600.firebaseio.com/";

    public static void initFirebase() {
        try {
            Log.d(TAG, "initFirebase: init");
            if (FirebaseApp.getApps(OustSdkApplication.getContext()).size() > 0) {
                FirebaseApp.getApps(OustSdkApplication.getContext()).removeAll(FirebaseApp.getApps(OustSdkApplication.getContext()));
            }
            String endPoint = OustPreferences.get("firebaseEndpoint");
            FirebaseOptions.Builder options = new FirebaseOptions.Builder();
            if ((endPoint != null) && (!endPoint.isEmpty())) {
                if (endPoint.endsWith("/")) {
                } else {
                    endPoint = endPoint + "/";
                }
                options.setDatabaseUrl(endPoint);
            } else {
                options.setDatabaseUrl(firebaseUrl);
            }
            String gcm_sender_id = OustPreferences.get("firebaseGCMId");
            if (gcm_sender_id != null && gcm_sender_id.length() > 0) {
                options.setGcmSenderId(gcm_sender_id);
            }
            String appId = OustPreferences.get("firebaseAppId");
            if ((appId != null) && (!appId.isEmpty())) {
                options.setApplicationId(appId);
            } else {
                options.setApplicationId("1:223834299820:android:2e5c98ec0d3f7870");
            }

           /* String projectId = OustPreferences.get("firebaseProjectId");
            if((projectId!=null)&&(!projectId.isEmpty())){
                options.setProjectId(projectId);
            }else {
                options.setProjectId("oust-qat");
            }

            try {
                Log.d(TAG, "initFirebase: firebaseProjectId: " + projectId);
            }catch (Exception e){
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }*/

            String firebaseApiKey = OustPreferences.get("firebaseAPIKey");
            if ((firebaseApiKey != null) && (!firebaseApiKey.isEmpty())) {
                options.setApiKey(firebaseApiKey);
            } else {
                options.setApiKey(OustSdkTools.decodeBase64("QUl6YVN5RGJNZE1ndllrN2JibVhXMVZjOG5hWHBpRElUQ2I4RWJR"));
            }

            try {
                if ((OustPreferences.get("tanentid") != null && !OustPreferences.get("tanentid").isEmpty()) && endPoint != null && !endPoint.isEmpty()) {
                    FirebaseApp.initializeApp(OustSdkApplication.getContext(), options.build(), OustPreferences.get("tanentid"));
                    FirebaseApp firebaseApp = FirebaseApp.getInstance(OustPreferences.get("tanentid"));
                    FirebaseDatabase.getInstance(firebaseApp).setPersistenceEnabled(true);
                    FirebaseApp.initializeApp(OustSdkApplication.getContext(), options.build());
                    Log.e("firebase params", "tanent id " + OustPreferences.get("tanentid") + "app id " + appId + "firebase api key  " + firebaseApiKey);
                } else {
                    FirebaseApp.initializeApp(OustSdkApplication.getContext(), options.build());
                    FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
                //OustSdkTools.sendSentryException(e);
            }
            try {
                if (OustPreferences.get("tanentid") != null && !OustPreferences.get("tanentid").isEmpty()) {
                    FirebaseApp firebaseApp = FirebaseApp.getInstance(OustPreferences.get("tanentid"));
                    oustFirebase = FirebaseDatabase.getInstance(firebaseApp).getReference();
                } else {
                    oustFirebase = FirebaseDatabase.getInstance().getReference();
                }
            } catch (Exception e) {
                e.printStackTrace();
                //OustSdkTools.sendSentryException(e);
            }

        } catch (Exception e) {
            OustSdkTools.showToast("Init Error");
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public static DatabaseReference getRootRef() {
        if (oustFirebase == null) {
            initFirebase();
        }
        return oustFirebase;
    }

    public static void resetFirebase() {
        try {
            if (OustAppState.getInstance().getFirebaseRefClassList().size() != 0) {
                for (int i = 0; i < OustAppState.getInstance().getFirebaseRefClassList().size(); i++) {
                    oustFirebase.child(OustAppState.getInstance().getFirebaseRefClassList().get(i).getFirebasePath()).
                            removeEventListener(OustAppState.getInstance().getFirebaseRefClassList().get(i).getEventListener());
                }
                OustAppState.getInstance().clearAll();
            }
            oustFirebase = null;
        } catch (Exception e) {
            Log.e("firebase listener error", "--" + e.getMessage());
        }
    }
}
