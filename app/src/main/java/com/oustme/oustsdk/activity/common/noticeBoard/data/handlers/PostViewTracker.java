package com.oustme.oustsdk.activity.common.noticeBoard.data.handlers;

import android.graphics.Rect;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;

import android.view.View;
import android.view.ViewTreeObserver;

import com.oustme.oustsdk.activity.common.noticeBoard.callBacks.NBPostClickCallBack;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.Objects;

/**
 * Created by oust on 2/22/19.
 */

public class PostViewTracker {

    private boolean firstTrackFlag = false;

    private double minimumVisibleHeightThreshold = 75;

    private NBPostClickCallBack nbPostClickCallBack;
    private RecyclerView recyclerView;

    public void setNbPostClickCallBack(NBPostClickCallBack nbPostClickCallBack) {
        this.nbPostClickCallBack = nbPostClickCallBack;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public void startTracking() {

        try {
            if (recyclerView != null) {
                recyclerView.getViewTreeObserver()
                        .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {

                                if (!firstTrackFlag) {

                                    int firstVisibleItemPosition = ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findFirstVisibleItemPosition();
                                    int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();


                                    analyzeAndAddViewData(firstVisibleItemPosition, lastVisibleItemPosition);


                                    firstTrackFlag = true;
                                }
                            }
                        });

                // Track the views when user scrolls through the recyclerview.
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView,
                                                     int newState) {
                        super.onScrollStateChanged(recyclerView, newState);

                        // User is scrolling, calculate and store the tracking
                        // data of the views that were being viewed
                        // before the scroll.
                        if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
//                    endTime = System.currentTimeMillis();

//                    for (int trackedViewsCount = 0; trackedViewsCount < viewsViewed.size(); trackedViewsCount++ ) {

//                        trackingData.add(prepareTrackingData(String.valueOf(viewsViewed.get(trackedViewsCount)), (endTime - startTime)/1000));
//                    }

                            // We clear the list of current item positions.
                            // If we don't do this, the items will be tracked
                            // every time the new items are added.
//                    viewsViewed.clear();
                        }

                        // Scrolling has ended, start the tracking
                        // process by assigning a start time
                        // and maintaining a list of views being viewed.
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {

//                    startTime = System.currentTimeMillis();

                            int firstVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();

                            analyzeAndAddViewData(firstVisibleItemPosition, lastVisibleItemPosition);

                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    // Track the items currently visible and then stop the tracking process.
    public void stopTracking() {

        int firstVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

        int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();

        analyzeAndAddViewData(firstVisibleItemPosition, lastVisibleItemPosition);

    }

    private void analyzeAndAddViewData(int firstPosition, int lastPosition) {
        try {
            if (recyclerView != null &&  nbPostClickCallBack != null) {

                // Analyze all the views
                Rect rvRect = new Rect();
                recyclerView.getGlobalVisibleRect(rvRect);

                for (int i = firstPosition; i <= lastPosition; i++)
                {
                    Rect rowRect = new Rect();
                    Objects.requireNonNull(Objects.requireNonNull(recyclerView.getLayoutManager()).findViewByPosition(i)).getGlobalVisibleRect(rowRect);

                    int percentFirst;
                    if (rowRect.bottom >= rvRect.bottom){
                        int visibleHeightFirst =rvRect.bottom - rowRect.top;
                        percentFirst = (visibleHeightFirst * 100) / Objects.requireNonNull(recyclerView.getLayoutManager().findViewByPosition(i)).getHeight();
                    }else {
                        int visibleHeightFirst = rowRect.bottom - rvRect.top;
                        percentFirst = (visibleHeightFirst * 100) / Objects.requireNonNull(recyclerView.getLayoutManager().findViewByPosition(i)).getHeight();
                    }

                    if (percentFirst>100)
                        percentFirst = 100;

                    if (percentFirst >= minimumVisibleHeightThreshold) {
                        if (nbPostClickCallBack != null)
                            nbPostClickCallBack.onPostViewed(i);

                        Log.e("Post Tracker", "view added - " + i);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    // Method to calculate how much of the view is visible
    // (i.e. within the screen) wrt the view height.
    // @param view
    // @return Percentage of the height visible.
    private double getVisibleHeightPercentage(View view) {

        Rect itemRect = new Rect();
        view.getLocalVisibleRect(itemRect);

        // Find the height of the item.
        double visibleHeight = itemRect.height();
        double height = view.getMeasuredHeight();

        double viewVisibleHeightPercentage = ((visibleHeight / height) * 100);

        Log.e("Feed", String.valueOf(viewVisibleHeightPercentage));

        return viewVisibleHeightPercentage;
    }

}

