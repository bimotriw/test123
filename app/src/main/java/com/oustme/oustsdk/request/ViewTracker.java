package com.oustme.oustsdk.request;

import android.graphics.Rect;
import android.util.Log;


import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.interfaces.common.DataLoaderNotifier;
import com.oustme.oustsdk.interfaces.common.FeedClickListener;
import com.oustme.oustsdk.interfaces.common.NewsFeedClickCallback;
import com.oustme.oustsdk.tools.OustDataHandler;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;

import java.util.ArrayList;
import java.util.Objects;

@Keep
public class ViewTracker {

    // Flag is required because 'addOnGlobalLayoutListener'
    // is called multiple times.
    // The flag limits the action inside 'onGlobalLayout' to only once.
    private boolean firstTrackFlag = false;


    private FeedClickListener feedClickListener;
    private boolean isFeedFromLanding;
    private DataLoaderNotifier dataLoaderNotifier;
    private NewsFeedClickCallback newsFeedClickCallback;
    private RecyclerView recyclerView;

    public void setFeedClickListener(FeedClickListener feedClickListener) {
        this.feedClickListener = feedClickListener;
    }

    public void setFeedFromLanding(boolean isFeedFromLanding, DataLoaderNotifier dataLoaderNotifier) {
        this.isFeedFromLanding = isFeedFromLanding;
        this.dataLoaderNotifier = dataLoaderNotifier;
    }

    public void setFeedClickListener(NewsFeedClickCallback newsFeedClickCallback) {
        this.newsFeedClickCallback = newsFeedClickCallback;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        try {
            this.recyclerView = recyclerView;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    // Start the tracking process.
    public void startTracking() {
        try {
            // Track the views when the data is loaded into
            // recycler view for the first time.
            if (recyclerView != null) {
                recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                    if (!firstTrackFlag) {

                        int firstVisibleItemPosition = ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findFirstVisibleItemPosition();
                        int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
//                        analyzeAndAddViewData(firstVisibleItemPosition, lastVisibleItemPosition);

                        firstTrackFlag = true;
                    }
                });

                // Track the views when user scrolls through the recyclerview.
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        // Scrolling up
                        // Scrolling down
                        OustPreferences.saveAppInstallVariable("feedScroll", dy <= 0);
                    }

                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);


                        // Scrolling has ended, start the tracking
                        // process by assigning a start time
                        // and maintaining a list of views being viewed.
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {


                            int firstVisibleItemPosition = ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findFirstVisibleItemPosition();
                            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();


                            // Log.d("ViewTracker2", "onScrollStateChanged: firstVisibleItemPosition:" + firstVisibleItemPosition + " ---- lastVisibleItemPosition:" + lastVisibleItemPosition);
                            //Log.d("ViewTracker", "onGlobalLayout: firstCompletelyVisibleItemPosition:"+firstCompletelyVisibleItemPosition+" ---- lastCompletelyVisibleItemPosition:"+lastCompletelyVisibleItemPosition);

//                            analyzeAndAddViewData(firstVisibleItemPosition, lastVisibleItemPosition);
                        }
                    }
                });
            } else {
                Log.d("ViewTracker Start", "recyclerview null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    // Track the items currently visible and then stop the tracking process.
    public void startNewFeed() {

        if (recyclerView != null) {
            int firstVisibleItemPosition = ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findFirstVisibleItemPosition();
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();

//            analyzeAndAddViewData(firstVisibleItemPosition, lastVisibleItemPosition);
        }
    }

/*
    private void analyzeAndAddViewData(int firstPosition, int lastPosition) {
        ArrayList<Integer> fullyVisibleItems = new ArrayList<>();
        try {
            if (recyclerView != null && (feedClickListener != null || newsFeedClickCallback != null)) {

                // Analyze all the views
                Rect rvRect = new Rect();
                recyclerView.getGlobalVisibleRect(rvRect);
                if (firstPosition >= 0 || lastPosition >= 0) {
                    for (int i = firstPosition; i <= lastPosition; i++) {
                        Rect rowRect = new Rect();
                        Objects.requireNonNull(Objects.requireNonNull(recyclerView.getLayoutManager()).findViewByPosition(i)).getGlobalVisibleRect(rowRect);

                        int percentFirst;
                        int visibleHeightFirst;
                        if (rowRect.bottom >= rvRect.bottom) {
                            visibleHeightFirst = rvRect.bottom - rowRect.top;
                        } else {
                            visibleHeightFirst = rowRect.bottom - rvRect.top;
                        }
                        percentFirst = (visibleHeightFirst * 100) / Objects.requireNonNull(recyclerView.getLayoutManager().findViewByPosition(i)).getHeight();

                        if (percentFirst > 100)
                            percentFirst = 100;

                        if (percentFirst >= 90) {
                            if (feedClickListener != null) {
                                if (OustStaticVariableHandling.getInstance().getFeeds() != null && OustStaticVariableHandling.getInstance().getFeeds().get(i) != null) {
//                                    Log.d("ViewTracker", "analyzeAndAddViewData: " + i + " --- FeedId:" + OustStaticVariableHandling.getInstance().getFeeds().get(i).getFeedId() + " --- FeedViewed:" + OustStaticVariableHandling.getInstance().getFeeds().get(i).isFeedViewed());
                                    if (!OustStaticVariableHandling.getInstance().getFeeds().get(i).isFeedViewed()) {
                                        feedClickListener.onFeedViewed(OustStaticVariableHandling.getInstance().getFeeds().get(i).getFeedId());
                                    }
                                } else {
//                                    Log.e("ViewTracker", "view added - " + i);
                                    feedClickListener.onFeedViewedOnScroll(i);
                                }
                            }

                            if (newsFeedClickCallback != null) {
//                                Log.e("Feed", "view added - " + i);
                                newsFeedClickCallback.onFeedViewedInScroll(i);
                            }
                        }

                        if (percentFirst >= 75) {
                            fullyVisibleItems.add(i);
                        }
                    }


                    if (fullyVisibleItems.size() != 0) {
                        if (fullyVisibleItems.contains(lastPosition)) {
                            int filterType = OustPreferences.getSavedInt("filterTypeFeed");
                            if (filterType <= 0) {
                                if (isFeedFromLanding && dataLoaderNotifier != null) {
                                    if (!OustDataHandler.getInstance().isAllFeedsLoaded() && OustPreferences.getAppInstallVariable("feedScroll")) {
                                        try {
                                            OustPreferences.saveAppInstallVariable("feedScroll", false);
                                            dataLoaderNotifier.getNextData("Alerts");
                                        } catch (Exception e) {
                                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                        }
                                    }
                                }
                            }
                        }
                        if (feedClickListener != null) {
//                            Log.d("Fully viewed", "" + fullyVisibleItems.get(0));
                            feedClickListener.onPlayVideo(fullyVisibleItems.get(0));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
*/


}
