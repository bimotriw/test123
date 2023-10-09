package com.oustme.oustsdk.activity.common;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.adapter.common.AlertCommentAdapter;
import com.oustme.oustsdk.firebase.common.AlertCommentData;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlertCommentsActivity extends Activity implements View.OnClickListener{
    private static final String TAG = "AlertCommentsActivity";
    private RelativeLayout comment_ll, cancel_comment_ll, send_comment_ll, send_ll, swiperefreshparent_layout,comments_ll;
    private LinearLayout  close_ll;
    private TextView comment_header;
    private EditText comment_et, comments_et;
    private RecyclerView comments_rv;
    private SwipeRefreshLayout swipe_refresh_layout;
    private ImageView send_imgview;
    private ActiveUser activeUser;
    private String feedId;
    private ArrayList<AlertCommentData> commentsList;
    private AlertCommentAdapter alertCommentAdapter;
    private RelativeLayout relativeLayoutToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_alert_comments);

        initViews();

        initComments();
    }

    private void initViews() {
        comment_ll = findViewById(R.id.comment_ll);
        cancel_comment_ll = findViewById(R.id.cancel_comment_ll);
        send_comment_ll = findViewById(R.id.send_comment_ll);
        send_ll = findViewById(R.id.send_ll);
        comments_ll = findViewById(R.id.comments_ll);
        close_ll = findViewById(R.id.close_ll);
        comment_header = findViewById(R.id.comment_header);
        comment_et = findViewById(R.id.comment_et);
        comments_et = findViewById(R.id.comments_et);
        comments_rv = findViewById(R.id.comments_rv);
        send_imgview = findViewById(R.id.send_imgview);
        swiperefreshparent_layout = findViewById(R.id.swiperefreshparent_layout);
        swipe_refresh_layout = findViewById(R.id.swipe_refresh_layout);
        relativeLayoutToolBar = findViewById(R.id.toolbar_ll);

        //send_imgview.setColorFilter(ContextCompat.getColor(this, R.color.whitea), android.graphics.PorterDuff.Mode.MULTIPLY);
        OustSdkTools.setImage(send_imgview, getResources().getString(R.string.send));

        int color = OustSdkTools.getColorBack(R.color.lgreen);
        Log.e("AIRTEL", "set to default color green" + color);
        if (OustPreferences.get("toolbarColorCode") != null) {
            color = Color.parseColor(OustPreferences.get("toolbarColorCode"));
            Log.e("AIRTEL", "THIS IS preference color" + color);
        }

        PorterDuff.Mode mode = PorterDuff.Mode.SRC_OVER;
        final GradientDrawable ld = (GradientDrawable) getApplicationContext().getResources().getDrawable(R.drawable.greenlite_round_textview);
        ld.setColorFilter(color, mode);
        send_ll.setBackgroundDrawable(ld);

        relativeLayoutToolBar.setBackgroundColor(color);

        close_ll.setOnClickListener(this);
        cancel_comment_ll.setOnClickListener(this);
        send_comment_ll.setOnClickListener(this);
        send_ll.setOnClickListener(this);
    }

    private void initComments() {
        activeUser = OustAppState.getInstance().getActiveUser();
        if ((activeUser != null) && (activeUser.getStudentid() != null)) {
        } else {
            OustSdkApplication.setmContext(AlertCommentsActivity.this);
            String activeUserGet = OustPreferences.get("userdata");
            activeUser = OustSdkTools.getActiveUserData(activeUserGet);
            HttpManager.setBaseUrl();
            OustFirebaseTools.initFirebase();
            OustAppState.getInstance().setActiveUser(activeUser);
        }
        showLoader();
        getAllComments();

    }

    private void getAllComments() {
        if (getIntent() != null) {
            feedId = getIntent().getStringExtra("feedId");
            if (feedId!=null && feedId.contains("feed")) {
                feedId = feedId.replace("feed", "");
            }
        } else {
            OustSdkTools.showToast(getResources().getString(R.string.error_message));
            finish();
        }
        try {
            final String message = "/userFeedComments/" + "feed" + feedId;
            ValueEventListener commentsListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        Map<String, Object> allCommentsMap = (Map<String, Object>) dataSnapshot.getValue();
                        if (allCommentsMap != null) {
                            commentsList = new ArrayList<>();
                            for (String queskey : allCommentsMap.keySet()) {
                                Object commentDataObject = allCommentsMap.get(queskey);
                                final Map<String, Object> commentsDataMap = (Map<String, Object>) commentDataObject;
                                if (commentsDataMap != null) {

                                    AlertCommentData alertCommentData = new AlertCommentData();
                                    if (commentsDataMap.get("addedOnDate") != null) {

                                        if(commentsDataMap.get("addedOnDate") instanceof String){
                                            alertCommentData.setAddedOnDate(Long.parseLong((String)commentsDataMap.get("addedOnDate")));
                                        }
                                        else{
                                            alertCommentData.setAddedOnDate((long) commentsDataMap.get("addedOnDate"));
                                        }
                                    }

                                    if (commentsDataMap.get("comment") != null) {
                                        alertCommentData.setComment((String) commentsDataMap.get("comment"));
                                    }
                                    if (commentsDataMap.get("userAvatar") != null) {
                                        alertCommentData.setUserAvatar((String) commentsDataMap.get("userAvatar"));
                                    }
                                    if (commentsDataMap.get("userDisplayName") != null) {
                                        alertCommentData.setUserDisplayName((String) commentsDataMap.get("userDisplayName"));
                                    }
                                    if (commentsDataMap.get("userId") != null) {
                                        alertCommentData.setUserId((String) commentsDataMap.get("userId"));
                                    }
                                    if (commentsDataMap.get("userKey") != null) {
                                        alertCommentData.setUserKey(OustSdkTools.convertToLong(commentsDataMap.get("userKey")));
                                    }
                                    commentsList.add(alertCommentData);

                                }
                            }
                            setComments();
                        }else{
                            setComments();
                        }
                    }else{
                        setComments();
                    }
                }
                @Override
                public void onCancelled(DatabaseError error) {
                }
            };
            DatabaseReference gameHistoryRef = OustFirebaseTools.getRootRef().child(message);
            Query query = gameHistoryRef.orderByChild("addedOn");
            query.keepSynced(true);
            query.addValueEventListener(commentsListener);
            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(commentsListener, message));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void setComments() {
        hideLoader();
        //if(commentsList!=null && commentsList.size()>0){
            comment_ll.setVisibility(View.GONE);
            comments_ll.setVisibility(View.VISIBLE);
            setAdapter();
//        }else{
//            comments_ll.setVisibility(View.GONE);
//            comment_ll.setVisibility(View.VISIBLE);
//        }
    }

    private void setAdapter() {
        if(commentsList!=null && commentsList.size()>0){
            Collections.sort(commentsList, commentSorter);
            if(alertCommentAdapter==null) {
                alertCommentAdapter = new AlertCommentAdapter(this, commentsList);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(OustSdkApplication.getContext(), LinearLayoutManager.VERTICAL, true);
                mLayoutManager.setStackFromEnd(true);
                comments_rv.setLayoutManager(mLayoutManager);
                comments_rv.setItemAnimator(new DefaultItemAnimator());
                comments_rv.setAdapter(alertCommentAdapter);
            }else{
                alertCommentAdapter.notifyChanges(commentsList);
            }
        }
    }

    public Comparator<AlertCommentData> commentSorter = new Comparator<AlertCommentData>() {
        public int compare(AlertCommentData s1, AlertCommentData s2) {
            if (s1.getAddedOnDate() == 0) {
                return -1;
            }
            if (s2.getAddedOnDate() == 0) {
                return 1;
            }
            if (s1.getAddedOnDate() == s2.getAddedOnDate()) {
                return 0;
            }
            return Long.valueOf(s2.getAddedOnDate()).compareTo(Long.valueOf(s1.getAddedOnDate()));
        }
    };
    private void showLoader() {
        try {
            swipe_refresh_layout.bringToFront();
            swipe_refresh_layout.setColorSchemeColors(OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen), OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen));
            swipe_refresh_layout.post(new Runnable() {
                @Override
                public void run() {
                    swipe_refresh_layout.setRefreshing(true);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void hideLoader() {
        try {
            swipe_refresh_layout.setRefreshing(false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        swiperefreshparent_layout.setVisibility(View.GONE);
        swipe_refresh_layout.setVisibility(View.GONE);
    }


    @Override
    public void onClick(View view) {
        int id =view.getId();
        if(id==R.id.close_ll){
            finish();
        }else if(id==R.id.send_ll){
            checkValidComments();
        }else if(id==R.id.send_comment_ll){
            checkValidComment();
        }else if(id == R.id.cancel_comment_ll){
            finish();
        }
    }

    private void checkValidComments() {
        if(comments_et.getText().toString()!=null && comments_et.getText().toString().trim().length()>0){
            buildCommentData(comments_et.getText().toString());
        }else{
            OustSdkTools.showToast(getResources().getString(R.string.type_comment_post));
        }
    }

    private void buildCommentData(String comment) {
        try {
            AlertCommentData alertCommentData = new AlertCommentData();
            alertCommentData.setComment(comment);
            alertCommentData.setAddedOnDate(System.currentTimeMillis());
            alertCommentData.setDevicePlatform("Android");
            alertCommentData.setUserAvatar(activeUser.getAvatar());
            alertCommentData.setUserId(activeUser.getStudentid());
            alertCommentData.setUserKey(Long.parseLong(activeUser.getStudentKey()));
            alertCommentData.setUserDisplayName(activeUser.getUserDisplayName());
            alertCommentData.setNumReply(0);

            sendCommentToFirebase(alertCommentData);

            comments_et.setText("");
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void sendCommentToFirebase(AlertCommentData alertCommentData) {
        String message = "/userFeedComments/"+"feed"+feedId;
        DatabaseReference postRef=OustFirebaseTools.getRootRef().child(message);
        alertCommentData.setFeedId(Long.parseLong(feedId));
        postComment(alertCommentData);
        /*DatabaseReference newPostRef=postRef.push();
        newPostRef.setValue(alertCommentData);
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        setidToUserFeedThread(newPostRef.getKey());
        updateCommentCountForQuestion();*/
    }

    public void postComment(AlertCommentData alertCommentData){
        String post_feed_comment = OustSdkApplication.getContext().getResources().getString(R.string.post_feed_comment);
        Gson gson = new GsonBuilder().create();

        String jsonParams = gson.toJson(alertCommentData);
        try {
            post_feed_comment = HttpManager.getAbsoluteUrl(post_feed_comment);

            ApiCallUtils.doNetworkCall(Request.Method.PUT, post_feed_comment, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    CommonResponse commonResponse=gson.fromJson(response.toString(),CommonResponse.class);
                    // sendRquestOver(commonResponse);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    // sendRquestOver(null);
                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, post_feed_comment, OustSdkTools.getRequestObject(jsonParams), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    CommonResponse commonResponse=gson.fromJson(response.toString(),CommonResponse.class);
                   // sendRquestOver(commonResponse);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                   // sendRquestOver(null);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<String, String>();
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
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
    private void postComment2(AlertCommentData alertCommentData) {
        Gson gson = new GsonBuilder().create();

        String url = HttpManager.getAbsoluteUrl(OustSdkApplication.getContext().getResources().getString(R.string.post_feed_comment));
        Log.d(TAG, "UpdateManualData: "+url);
        String json = null;
        HashMap jsonObject = new HashMap();
        List<AlertCommentData> commentDataList = new ArrayList<>();
        commentDataList.add(alertCommentData);

        try {
            PackageManager manager = AlertCommentsActivity.this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(
                    AlertCommentsActivity.this.getPackageName(), 0);
            String version = info.versionName;
            json = gson.toJson(commentDataList);
            jsonObject.put("feedCommentDataList", json);
            jsonObject.put("appVersion",version);
            jsonObject.put("devicePlatformName","android");
            Log.d(TAG, "UpdateManualData: "+jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        String requestDataJson = gson.toJson(jsonObject);

        ApiCallUtils.doNetworkCall(Request.Method.PUT, url, OustSdkTools.getRequestObject(requestDataJson), new ApiCallUtils.NetworkCallback() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResult: Comment saved success :"+response.toString());

            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onFailure: ");
            }
        });

        /*ApiClient.jsonRequest(OustSdkApplication.getContext(), Request.Method.PUT, url, jsonObject, json, new ApiClient.NResultListener<JSONObject>() {
            @Override
            public void onResult(int resultCode, JSONObject tResult) {
                Log.d(TAG, "onResult: Comment saved success :"+tResult.toString());

            }

            @Override
            public void onFailure(int mError) {
                Log.d(TAG, "onFailure: ");
            }
        });*/
    }

    private void updateCommentCountForQuestion() {
        String message="feeds/feed"+feedId+"/numComments";
        DatabaseReference firebase = OustFirebaseTools.getRootRef().child(message);
        firebase.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentData) {
                if (currentData.getValue()== null) {
                    currentData.setValue(1);
                } else {
                    currentData.setValue((Long) currentData.getValue() + 1);
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError DatabaseError, boolean b, DataSnapshot dataSnapshot) {
                if (DatabaseError != null) {
                    Log.e("","Firebase counter increment failed. New Count:{}"+dataSnapshot);
                } else {
                    Log.e("","Firebase counter increment succeeded.");
                }
            }
        });
    }

    private void setidToUserFeedThread(String key) {
        String message = "/userFeed/"+activeUser.getStudentKey()+"/feed"+feedId+"/"+"commentThread/"+key;
        OustFirebaseTools.getRootRef().child(message).setValue(true);
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);

        String message1 = "/userFeed/"+activeUser.getStudentKey()+"/feed"+feedId+"/"+"isCommented";
        OustFirebaseTools.getRootRef().child(message1).setValue(true);
        OustFirebaseTools.getRootRef().child(message1).keepSynced(true);

    }

    private void checkValidComment() {
        comment_et.setError(null);
        if(comment_et.getText().toString()!=null && comment_et.getText().toString().trim().length()>0){
            buildCommentData(comment_et.getText().toString());
        }else{
            comment_et.setError("Please enter comment !");
        }
    }

    @Override
    public void onBackPressed() {
        if(comment_et!=null){
            comment_et.setError(null);
        }
        if(comments_et!=null){
            comments_et.setError(null);
        }
        super.onBackPressed();
    }
}
