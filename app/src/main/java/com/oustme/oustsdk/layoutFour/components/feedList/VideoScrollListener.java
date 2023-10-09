package com.oustme.oustsdk.layoutFour.components.feedList;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.StyledPlayerView;

public class VideoScrollListener extends RecyclerView.OnScrollListener {
    private Context context;
    private StyledPlayerView videoSurfaceView;
    SimpleExoPlayer videoPlayer;
    private int lastFirstVisiblePosition;

    public VideoScrollListener(Context context, StyledPlayerView videoSurfaceView, SimpleExoPlayer videoPlayer) {
        this.context = context;
        this.videoSurfaceView = videoSurfaceView;
        this.videoPlayer = videoPlayer;
        this.lastFirstVisiblePosition = -1;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            // Find the first and last visible items
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
            int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();

            // Pause or resume the video based on its position
            int currentPosition = recyclerView.getChildAdapterPosition(videoSurfaceView);
            if (currentPosition >= firstVisiblePosition && currentPosition <= lastVisiblePosition) {
                if (videoPlayer != null) {
                    videoPlayer.setPlayWhenReady(true);
//                    videoPlayer.play();
                }
            } else {
                if (videoPlayer != null) {
                    videoPlayer.setPlayWhenReady(false);
//                    videoPlayer.pause();
                }
            }
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        // Determine if the scrolling direction changed
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
        if (firstVisiblePosition > lastFirstVisiblePosition) {
            // Scrolling down
            videoPlayer.pause();
        } else if (firstVisiblePosition < lastFirstVisiblePosition) {
            // Scrolling up
            videoPlayer.pause();
        }

        lastFirstVisiblePosition = firstVisiblePosition;
    }
}
