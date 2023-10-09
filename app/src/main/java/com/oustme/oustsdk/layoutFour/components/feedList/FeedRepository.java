package com.oustme.oustsdk.layoutFour.components.feedList;

import android.os.Build;
import android.os.Handler;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.common.FeedType;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.layoutFour.components.feedList.adapter.AllFeedsData;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.room.dto.DTONewFeed;
import com.oustme.oustsdk.room.dto.DTOSpecialFeed;
import com.oustme.oustsdk.room.dto.DTOUserFeeds;
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

public class FeedRepository {

    private static final String TAG = "FeedRepository";
    private ArrayList<Long> expiredFeedPosition = new ArrayList<>();

    AllFeedsData feedsData = new AllFeedsData();
    ArrayList<DTOSpecialFeed> specialFeedArrayList = new ArrayList<>();

    DTOUserFeeds dtoUserFeedsResponse = new DTOUserFeeds();
    private MutableLiveData<AllFeedsData> allFeedsData;
    FirebaseRefClass firebaseRefClass;
    int totalCount, announcementCount;

    public FeedRepository(String filters, int pageNum, String query) {
        allFeedsData = new MutableLiveData<>();
        fetchUserFeeds(filters, pageNum, query);
    }

    public MutableLiveData<AllFeedsData> getDtoUserFeeds() {
        return allFeedsData;
    }

    public void fetchUserFeeds(String filters, int pageNum, String query) {
        Log.d(TAG, "getAllUserNewsFeed: ");
        // Todo implement feed api
        ActiveUser activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
        String feedsAPI;
        if (query != null && !query.isEmpty()) {
            feedsAPI = OustSdkApplication.getContext().getResources().getString(R.string.search_Feed_page);
            feedsAPI = feedsAPI.replace("{studentId}", activeUser.getStudentid().trim());
            feedsAPI = feedsAPI.replace("{orgId}", OustPreferences.get("tanentid").trim());
            feedsAPI = feedsAPI.replace("{query}", "" + query);
        } else {
            feedsAPI = OustSdkApplication.getContext().getResources().getString(R.string.userFeed_page);
            feedsAPI = feedsAPI.replace("{studentId}", activeUser.getStudentid().trim());
            feedsAPI = feedsAPI.replace("{pageNum}", "" + pageNum);
            feedsAPI = feedsAPI.replace("{orgId}", OustPreferences.get("tanentid").trim());
        }
        if (filters != null && !filters.isEmpty())
            feedsAPI = feedsAPI + filters;
        feedsAPI = HttpManager.getAbsoluteUrl(feedsAPI);
        try {
            ApiCallUtils.doNetworkCall2(Request.Method.GET, feedsAPI, OustSdkTools.getRequestObject(""), query, new ApiCallUtils.NetworkCallback2() {
                @Override
                public void onResponse(JSONObject response, String queryCheck) {
                    String QuerySearch = OustPreferences.get("QuerySearch");
                    if (QuerySearch != null && !QuerySearch.isEmpty() && !QuerySearch.equalsIgnoreCase(queryCheck)) {
                        return;
                    }
                    Log.d(TAG, "onResponse:(-) user feeds:" + response.toString());
                    Gson gson = new Gson();
                    dtoUserFeedsResponse = gson.fromJson(response.toString(), DTOUserFeeds.class);
                    ArrayList<DTOUserFeeds.FeedList> feedLists = dtoUserFeedsResponse.getFeedList();
                    try {
                        if (filters != null && filters.isEmpty())
                            RoomHelper.deleterAllUserFeeds();
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                    try {
                        if (feedLists != null && feedLists.size() != 0) {
                            totalCount = 0;
                            for (DTOUserFeeds.FeedList feed : feedLists) {
                                try {
                                    String feedDetailsMes = "/feeds/feed" + feed.getFeedId();

                                    ValueEventListener feedsNodeData = new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            final Map<String, Object> feedsNodeDataVal = (Map<String, Object>) snapshot.getValue();
                                            if (feedsNodeDataVal != null) {
                                                if (feedsNodeDataVal.get("numLikes") != null) {
                                                    feed.setNumLikes(Math.toIntExact(OustSdkTools.newConvertToLong(feedsNodeDataVal.get("numLikes"))));
                                                } else {
                                                    feed.setNumLikes(0);
                                                }
                                                if (feedsNodeDataVal.get("numComments") != null) {
                                                    feed.setNumComments(Math.toIntExact(OustSdkTools.newConvertToLong(feedsNodeDataVal.get("numComments"))));
                                                } else {
                                                    feed.setNumComments(0);
                                                }
                                                if (feedsNodeDataVal.get("numShares") != null) {
                                                    feed.setNumShares(Math.toIntExact(OustSdkTools.newConvertToLong(feedsNodeDataVal.get("numShares"))));
                                                } else {
                                                    feed.setNumShares(0);
                                                }
                                                if (feedsNodeDataVal.get("shareable") != null) {
                                                    feed.setShareable((Boolean) feedsNodeDataVal.get("shareable"));
                                                } else {
                                                    feed.setShareable(false);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Log.e(TAG, "onCancelled:: --> " + error.getMessage());
                                        }
                                    };
                                    DatabaseReference newsFeeds = OustFirebaseTools.getRootRef().child(feedDetailsMes);
                                    Query query = newsFeeds.orderByChild("timeStamp");
                                    query.keepSynced(false);
                                    query.addListenerForSingleValueEvent(feedsNodeData);
                                    OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(feedsNodeData, feedDetailsMes));
                                    firebaseRefClass = new FirebaseRefClass(feedsNodeData, feedDetailsMes);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    OustSdkTools.sendSentryException(e);
                                    allFeedsData.postValue(null);
                                }

                                try {
                                    String message = "/userFeed/" + activeUser.getStudentKey() + "/feed" + feed.getFeedId();
                                    ValueEventListener userFeedsData = new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            final Map<String, Object> userFeedsDataVal = (Map<String, Object>) snapshot.getValue();
                                            if (userFeedsDataVal != null) {
                                                if (userFeedsDataVal.get("isLiked") != null) {
                                                    feed.setLiked((boolean) userFeedsDataVal.get("isLiked"));
                                                } else {
                                                    feed.setLiked(false);
                                                }
                                                feed.setClicked(feed.isFeedViewed());
                                                if (userFeedsDataVal.get("isCommented") != null) {
                                                    feed.setCommented((boolean) userFeedsDataVal.get("isCommented"));
                                                } else {
                                                    feed.setCommented(false);
                                                }
                                                if (userFeedsDataVal.get("feedCoinsAdded") != null) {
                                                    feed.setFeedCoinsAdded((boolean) userFeedsDataVal.get("feedCoinsAdded"));
                                                } else {
                                                    feed.setFeedCoinsAdded(false);
                                                }
                                            }
                                            // TODO here addFeedsToRealm is happening but not getting anywhere
                                            /*try {
                                                if (filters != null && filters.isEmpty())
                                                    RoomHelper.addFeedsToRealm(feed);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }*/
                                            try {
                                                totalCount++;
                                                if (totalCount >= feedLists.size()) {
                                                    feedsData.setDtoUserFeeds(feedLists);
                                                    feedsData.setFeedCount(dtoUserFeedsResponse.getFeedCount());
                                                    allFeedsData.postValue(feedsData);
                                                    totalCount = 0;
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                OustSdkTools.sendSentryException(e);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Log.e(TAG, "onCancelled: --> " + error.getMessage());
                                        }
                                    };
                                    DatabaseReference newsFeedRef = OustFirebaseTools.getRootRef().child(message);
                                    newsFeedRef.keepSynced(false);
                                    newsFeedRef.addListenerForSingleValueEvent(userFeedsData);
                                    OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(userFeedsData, message));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    OustSdkTools.sendSentryException(e);
                                    allFeedsData.postValue(null);
                                }

                            }
                        } else {
                            allFeedsData.postValue(null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        allFeedsData.postValue(null);
                        OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onResponse: Error:" + error.getMessage());
                    allFeedsData.postValue(null);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            allFeedsData.postValue(null);
            OustSdkTools.sendSentryException(e);
        }
    }
}
