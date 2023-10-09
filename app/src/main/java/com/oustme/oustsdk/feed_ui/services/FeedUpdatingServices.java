package com.oustme.oustsdk.feed_ui.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.request.ClickedFeedData;
import com.oustme.oustsdk.request.ClickedFeedRequestData;
import com.oustme.oustsdk.request.FeedRewardCoinsUpdate;
import com.oustme.oustsdk.request.ViewedFeedData;
import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FeedUpdatingServices extends Service {

    //private Looper serviceLooper;
    String feedUpdatingRunning;
    //private ServiceHandler serviceHandler;
    private List<String> requests;
    private final String SAVED_FEED_REQUEST = "savedFeedClickedRequests";

    @Override
    public void onCreate() {

        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getExtras() != null) {
            if (intent.hasExtra("feedCoinsUpdate") && intent.getBooleanExtra("feedCoinsUpdate", false)) {
                sendFeedRewardCoinsUpdate(intent);
            } else {
                int feedId = intent.getIntExtra("FeedId", 0);
                if (feedId == 0) {
                    feedId = intent.getIntExtra("FeedId", 0);
                }
                int commentSize = intent.getIntExtra("FeedCommentSize", 0);

                if (feedId != 0 && commentSize != 0) {
                    updateCommentCount("" + feedId, commentSize);
                }
            }
        } else {
            feedUpdatingRunning = OustPreferences.get("feedUpdatingRunning");
            if (feedUpdatingRunning == null) {
                OustPreferences.save("feedUpdatingRunning", "feedUpdatingRunning");
                checkForFeedClickedRequestAvailability();
            }

        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        // Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
        try {
            OustPreferences.save("feedUpdatingRunning", "");
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }


    }

    private void updateCommentCount(String feedId, long count) {
        String message = "feeds/feed" + feedId + "/numComments";
        DatabaseReference firebase = OustFirebaseTools.getRootRef().child(message);
        firebase.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                currentData.setValue(count);


                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError DatabaseError, boolean b, DataSnapshot dataSnapshot) {
                if (DatabaseError != null) {
                    Log.e("", "Firebase counter increment failed. New Count:{}" + dataSnapshot);
                } else {
                    Log.e("", "Firebase counter increment succeeded.");
                }
            }
        });
    }

    private void checkForFeedClickedRequestAvailability() {
        requests = OustPreferences.getLoacalNotificationMsgs(SAVED_FEED_REQUEST);
        if (requests.size() > 0) {

            Gson gson = new Gson();
//            used same class to store data as format is same
            ClickedFeedRequestData clickedFeedRequestData = gson.fromJson(requests.get(0), ClickedFeedRequestData.class);
            if (clickedFeedRequestData != null) {
                sendFeedClickedRequest(clickedFeedRequestData);
            }
        } else {
            this.stopSelf();
        }
    }

    private void sendFeedClickedRequest(ClickedFeedRequestData clickedFeedRequestData) {
        final CommonResponse[] commonResponses = {null};

        try {
            String send_feed_request = getResources().getString(R.string.send_feed_request);
            Gson gson = new GsonBuilder().create();
            send_feed_request = HttpManager.getAbsoluteUrl(send_feed_request);
            String jsonParams = gson.toJson(clickedFeedRequestData);

            Log.d("TAG", "sendFeedClickedRequest: " + send_feed_request);
            Log.d("TAG", "sendFeedClickedRequest: " + jsonParams);
            ApiCallUtils.doNetworkCall(Request.Method.POST, send_feed_request, com.oustme.oustsdk.tools.OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                    FeedRequestSent(commonResponses[0]);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    FeedRequestSent(commonResponses[0]);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            Log.e("sendUnFavRMRequest", "ERROR: ", e);
        }
    }

    private void FeedRequestSent(CommonResponse commonResponse) {
        try {
            if ((commonResponse != null) && (commonResponse.isSuccess())) {
                requests.remove(0);
                OustPreferences.saveLocalNotificationMsg(SAVED_FEED_REQUEST, requests);
                ArrayList<ClickedFeedData> clickedFeedDataArrayList = new ArrayList<>();
                HashMap<String, ViewedFeedData> viewedFeedDataMap = new HashMap<>();
                OustPreferences.save("viewFeedLIst", new Gson().toJson(viewedFeedDataMap));
                OustPreferences.save("clickFeedList", new Gson().toJson(clickedFeedDataArrayList));
                OustPreferences.save("feedUpdatingRunning", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void sendFeedRewardCoinsUpdate(Intent intent) {
        final CommonResponse[] commonResponses = {null};
        try {
            final long feedCoins = intent.getLongExtra("feedCoins", 0);
            int feedId = intent.getIntExtra("feedId", 0);

            String send_feed_reward_update = getResources().getString(R.string.send_feed_reward_update);
            send_feed_reward_update = HttpManager.getAbsoluteUrl(send_feed_reward_update);

            FeedRewardCoinsUpdate feedRewardCoinsUpdate = new FeedRewardCoinsUpdate();
            feedRewardCoinsUpdate.setFeedId(feedId);
            feedRewardCoinsUpdate.setFeedCoins("" + feedCoins);
            feedRewardCoinsUpdate.setTimestamp("" + System.currentTimeMillis());
            feedRewardCoinsUpdate.setUserId(OustAppState.getInstance().getActiveUser().getStudentid());

            Gson gson = new GsonBuilder().create();
            String jsonParams = gson.toJson(feedRewardCoinsUpdate);

            Log.d("TAG", "sendFeedRewardCoinsUpdate: " + send_feed_reward_update);
            Log.d("TAG", "sendFeedRewardCoinsUpdate: " + jsonParams);
            ApiCallUtils.doNetworkCall(Request.Method.PUT, send_feed_reward_update, com.oustme.oustsdk.tools.OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                    feedRewardUpdateSent(commonResponses[0], feedCoins);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    feedRewardUpdateSent(null, feedCoins);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void feedRewardUpdateSent(CommonResponse commonResponse, long coins) {
        try {
            if ((commonResponse != null) && (commonResponse.isSuccess())) {
                Log.d("TAG", "feedRewardUpdateSent: success");
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.toast_layout, null);

                try {
                    ImageView img = layout.findViewById(R.id.img_coin);
                    if (OustPreferences.getAppInstallVariable("showCorn")) {
                        img.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_coins_corn));
                    } else {
                        img.setImageResource(R.drawable.ic_coins_golden);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }

                //View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_layout_root));

                TextView text = layout.findViewById(R.id.toast_msg);
                String feedCoins = OustSdkApplication.getContext().getResources().getString(R.string.yay_text) + "\n" +
                        OustSdkApplication.getContext().getResources().getString(R.string.have_earned) + " " + coins + " " +
                        OustSdkApplication.getContext().getResources().getString(R.string.coins_text);
                text.setText(feedCoins);

                OustSdkTools.showCustomToast(layout, getApplicationContext());

                //OustSdkTools.showToast("You have earned "+coins+" coins");
            } else {
                Log.d("TAG", "feedRewardUpdateSent: failed");
                OustSdkTools.showToast("Failed to update earned coins");
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

}
