package com.oustme.oustsdk.customviews;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.util.CacheDataSourceFactory;

import java.io.File;

/**
 * Created by oust on 10/22/18.
 */

public abstract class CustomExoPlayerView {

    private StyledPlayerView simpleExoPlayerView;
    private ExoPlayer simpleExoPlayer;
    //private LinearLayout nointernet_exo_ll;
    private boolean isVideoCompleted = false;
    private Context context;
    private long defaultTime = 0;
    private RelativeLayout parent_video_ll;
    private ImageButton imageButtonPause, imageButtonPlay;
    ImageButton exo_reverse;
    ImageButton exo_forward;
    private RelativeLayout layout_exoplayer_new_ui;
    private LinearLayout layout_exoplayer_old_ui;
    String tempPath;

    public void setVideoCompleted(boolean videoCompleted) {
        this.isVideoCompleted = videoCompleted;
    }

    public void initExoPlayer(RelativeLayout parent_video_l, Context context, final String path) {
        try {
            this.context = context;
            this.parent_video_ll = parent_video_l;
            this.tempPath = path;
            try {
                if (simpleExoPlayer != null) {
                    simpleExoPlayer.release();
                    simpleExoPlayer = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
            try {
                if (simpleExoPlayerView != null) {
                    simpleExoPlayerView.setPlayer(null);
                    simpleExoPlayerView.removeAllViews();
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }

            /*LayoutInflater layoutInflater = LayoutInflater.from(context);

            View view = layoutInflater.inflate(R.layout.exo_player_view, null);

            simpleExoPlayerView = (StyledPlayerView) view.findViewById(R.id.video_view);*/

            simpleExoPlayerView = new StyledPlayerView(context);
            simpleExoPlayerView.setLayoutParams(new StyledPlayerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            simpleExoPlayerView.setBackgroundColor(Color.TRANSPARENT);
            parent_video_ll.addView(simpleExoPlayerView);
            simpleExoPlayerView.setShowNextButton(false);
            simpleExoPlayerView.setShowFastForwardButton(false);
            simpleExoPlayerView.setVisibility(View.VISIBLE);

            DefaultTimeBar timeBar = simpleExoPlayerView.findViewById(R.id.exo_progress);
          /*  nointernet_exo_ll = simpleExoPlayerView.findViewById(R.id.nointernet_exo_ll);

            nointernet_exo_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reInitVideo(path);
                }
            });*/
            timeBar.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    return !isVideoCompleted;
                }
            });

            imageButtonPause = simpleExoPlayerView.findViewById(R.id.exo_pause);
            imageButtonPlay = simpleExoPlayerView.findViewById(R.id.exo_play);
           /* exo_reverse = simpleExoPlayerView.findViewById(R.id.exo_reverse);
            exo_forward = simpleExoPlayerView.findViewById(R.id.exo_forward);
            exo_forward.setVisibility(View.VISIBLE);
            exo_reverse.setVisibility(View.VISIBLE);*/
            /*layout_exoplayer_new_ui = (RelativeLayout) simpleExoPlayerView.findViewById(R.id.layout_exoplayer_new_ui);
            layout_exoplayer_old_ui = (LinearLayout) simpleExoPlayerView.findViewById(R.id.layout_exoplayer_old_ui);
            layout_exoplayer_new_ui.setVisibility(View.VISIBLE);
            layout_exoplayer_old_ui.setVisibility(View.GONE);*/

            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

            /*AdaptiveTrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory();
            TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
            simpleExoPlayer = SimpleExoPlayer.Builder(context, trackSelector);*/

            simpleExoPlayer = new SimpleExoPlayer.Builder(context).build();

            MediaSource videoSource;
            Uri videoUri = Uri.parse(path);
//            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, "exoplayer2example"), (TransferListener) bandwidthMeter);
            CacheDataSourceFactory dataSourceFactory = new CacheDataSourceFactory(context, 100 * 1024 * 1024, 5 * 1024 * 1024);
            videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(videoUri));

            simpleExoPlayerView.setPlayer(simpleExoPlayer);
            simpleExoPlayerView.hideController();
            simpleExoPlayer.setMediaSource(videoSource);
            simpleExoPlayer.setPlayWhenReady(true);
            simpleExoPlayer.seekTo(defaultTime);
            simpleExoPlayer.setPlaybackSpeed(1.0f);
            simpleExoPlayer.prepare();

            simpleExoPlayer.addListener(new Player.Listener() {
                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    Player.Listener.super.onPlaybackStateChanged(playbackState);
                    try {
                        if (playbackState == simpleExoPlayer.STATE_IDLE || playbackState == simpleExoPlayer.STATE_ENDED) {
                            if (simpleExoPlayerView != null)
                                simpleExoPlayerView.setKeepScreenOn(false);
                        } else { // STATE_IDLE, STATE_ENDED
                            // This prevents the screen from getting dim/lock
                            if (simpleExoPlayerView != null)
                                simpleExoPlayerView.setKeepScreenOn(true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }

                    if (playbackState == simpleExoPlayer.STATE_READY) {
                        Log.e("-------", "STATE_READY");
                        onPlayReady();
                    } else if (playbackState == simpleExoPlayer.STATE_BUFFERING) {
                        Log.e("-------", "STATE_BUFFERING");
                        onBuffering();
                    } else if (playbackState == simpleExoPlayer.STATE_IDLE) {
                        Log.e("-------", "STATE_IDLE");
                        onBuffering();
                    } else if (playbackState == simpleExoPlayer.STATE_ENDED) {
                        Log.e("-------", "STATE_ENDED");
                        simpleExoPlayer.setPlaybackSpeed(1.0f);
                        if (!isVideoCompleted)
                            onVideoComplete();
                        isVideoCompleted = true;
                        if (simpleExoPlayerView != null)
                            simpleExoPlayerView.showController();

                    }
                }

                @Override
                public void onPlayerError(@NonNull ExoPlaybackException error) {
                    Player.Listener.super.onPlayerError(error);
                    try {
                        simpleExoPlayer.prepare();
                        simpleExoPlayer.setPlayWhenReady(true);
                        simpleExoPlayer.getPlaybackState();
                        onVideoError();
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }
            });

            simpleExoPlayerView.setOnFocusChangeListener((v, hasFocus) -> {
                if (simpleExoPlayer != null && !hasFocus) {
                    pausePlayer();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void initExoPlayerWithFile(RelativeLayout parent_video_l, Context context, final File path) {
        try {
            this.context = context;
            this.parent_video_ll = parent_video_l;
            try {
                if (simpleExoPlayer != null) {
                    simpleExoPlayer.release();
                    simpleExoPlayer = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }

            try {
                if (simpleExoPlayerView != null) {
                    simpleExoPlayerView.setPlayer(null);
                    simpleExoPlayerView.removeAllViews();
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }

            /*LayoutInflater layoutInflater = LayoutInflater.from(context);

            View view = layoutInflater.inflate(R.layout.exo_player_view, null);

            simpleExoPlayerView = (StyledPlayerView) view.findViewById(R.id.video_view);*/

            simpleExoPlayerView = new StyledPlayerView(context);
            simpleExoPlayerView.setLayoutParams(new StyledPlayerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            simpleExoPlayerView.setBackgroundColor(Color.TRANSPARENT);
            parent_video_ll.addView(simpleExoPlayerView);
            simpleExoPlayerView.setShowNextButton(false);
            simpleExoPlayerView.setShowFastForwardButton(false);

            simpleExoPlayerView.setVisibility(View.VISIBLE);

            DefaultTimeBar timeBar = simpleExoPlayerView.findViewById(R.id.exo_progress);
          /*  nointernet_exo_ll = simpleExoPlayerView.findViewById(R.id.nointernet_exo_ll);

            nointernet_exo_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reInitVideo(path);
                }
            });*/
            timeBar.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    return !isVideoCompleted;
                }
            });

            imageButtonPause = simpleExoPlayerView.findViewById(R.id.exo_pause);
            imageButtonPlay = simpleExoPlayerView.findViewById(R.id.exo_play);
           /* exo_reverse = simpleExoPlayerView.findViewById(R.id.exo_reverse);
            exo_forward = simpleExoPlayerView.findViewById(R.id.exo_forward);
            exo_forward.setVisibility(View.VISIBLE);
            exo_reverse.setVisibility(View.VISIBLE);*/
            /*layout_exoplayer_new_ui = (RelativeLayout) simpleExoPlayerView.findViewById(R.id.layout_exoplayer_new_ui);
            layout_exoplayer_old_ui = (LinearLayout) simpleExoPlayerView.findViewById(R.id.layout_exoplayer_old_ui);
            layout_exoplayer_new_ui.setVisibility(View.VISIBLE);
            layout_exoplayer_old_ui.setVisibility(View.GONE);*/

            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

            /*AdaptiveTrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory();
            TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
            simpleExoPlayer = SimpleExoPlayer.Builder(context, trackSelector);*/

            simpleExoPlayer = new SimpleExoPlayer.Builder(context).build();

            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, "exoplayer2example"), (TransferListener) bandwidthMeter);
            MediaSource videoSource;
            Uri videoUri = Uri.fromFile(path);

            videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(videoUri));
            //videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(videoUri);

            simpleExoPlayerView.setPlayer(simpleExoPlayer);
            simpleExoPlayerView.hideController();
            simpleExoPlayer.setMediaSource(videoSource);
            simpleExoPlayer.setPlayWhenReady(true);
            simpleExoPlayer.seekTo(defaultTime);
            simpleExoPlayer.setPlaybackSpeed(1.0f);
            simpleExoPlayer.prepare();

            simpleExoPlayer.addListener(new Player.Listener() {
                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    try {
                        if (playbackState == simpleExoPlayer.STATE_IDLE || playbackState == simpleExoPlayer.STATE_ENDED) {
                            if (simpleExoPlayerView != null) {
                                simpleExoPlayerView.setKeepScreenOn(false);
                            }
                        } else { // STATE_IDLE, STATE_ENDED
                            // This prevents the screen from getting dim/lock
                            if (simpleExoPlayerView != null)
                                simpleExoPlayerView.setKeepScreenOn(true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }

                    if (playbackState == simpleExoPlayer.STATE_READY) {
                        Log.e("-------:", "STATE_READY");
                        onPlayReady();
                    } else if (playbackState == simpleExoPlayer.STATE_BUFFERING) {
                        Log.e("-------:", "STATE_BUFFERING");
                        onBuffering();
                    } else if (playbackState == simpleExoPlayer.STATE_IDLE) {
                        Log.e("-------:", "STATE_IDLE");
                        onBuffering();
                    } else if (playbackState == simpleExoPlayer.STATE_ENDED) {
                        Log.e("-------:", "STATE_ENDED");
                        if (!isVideoCompleted)
                            onVideoComplete();
                        isVideoCompleted = true;
                        if (simpleExoPlayerView != null) {
                            simpleExoPlayer.setPlaybackSpeed(1.0f);
                            simpleExoPlayerView.showController();
                        }
                    }
                }

                @Override
                public void onPlayerError(@NonNull ExoPlaybackException error) {
                    Player.Listener.super.onPlayerError(error);
                    try {
                        simpleExoPlayer.setPlayWhenReady(true);
                        simpleExoPlayer.getPlaybackState();
                        onVideoError();
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void reInitVideo(String path) {
        removeVideoPlayer();
        initExoPlayer(parent_video_ll, context, path);
    }

    public StyledPlayerView getSimpleExoPlayerView() {
        return simpleExoPlayerView;
    }

    public ExoPlayer getSimpleExoPlayer() {
        return simpleExoPlayer;
    }

    public void removeVideoPlayer() {
        try {
            if (simpleExoPlayerView != null && simpleExoPlayer != null && simpleExoPlayerView.getPlayer() != null) {
                defaultTime = simpleExoPlayerView.getPlayer().getCurrentPosition();
                simpleExoPlayerView.getPlayer().release();
                simpleExoPlayerView.setPlayer(null);
                simpleExoPlayerView.removeAllViews();
                parent_video_ll.removeAllViews();
                try {
                    Log.e("-------", "removeVideoPlayer ch");
//                    simpleExoPlayer.stop();
                    simpleExoPlayer.release();
                    simpleExoPlayer = null;
                    Log.e("-------", "removeVideoPlayer check");
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                    simpleExoPlayer.release();
                    simpleExoPlayer = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public abstract void onAudioComplete();

    public abstract void onVideoComplete();

    public abstract void onBuffering();

    public abstract void onVideoError();

    public abstract void onPlayReady();

    public void removeAudioPlayer() {
        try {
            if (simpleExoPlayerView != null && simpleExoPlayer != null && simpleExoPlayerView.getPlayer() != null) {
                defaultTime = simpleExoPlayerView.getPlayer().getCurrentPosition();
                simpleExoPlayerView.getPlayer().release();
                simpleExoPlayerView.setPlayer(null);
                simpleExoPlayerView.removeAllViews();
                parent_video_ll.removeAllViews();
                try {
                    Log.e("-------", "removeVideoPlayer ch:");
//                    simpleExoPlayer.stop();
                    simpleExoPlayer.release();
                    simpleExoPlayer = null;
                    Log.e("-------", "removeVideoPlayer check:");
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                    simpleExoPlayer.release();
                    simpleExoPlayer = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void performPauseclick() {
        if (imageButtonPause != null) {
            imageButtonPause.performClick();
        }
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setPlayWhenReady(false);
        }
    }

    public void performPlayClick() {
        if (imageButtonPlay != null) {
            imageButtonPlay.performClick();
        }
        if (simpleExoPlayer != null) {
//            simpleExoPlayer.play();
            simpleExoPlayer.setPlayWhenReady(true);
//            simpleExoPlayer.getPlaybackState();
        }
    }

    public int getPauseButtonVisibility() {
        if (imageButtonPause != null) {
            return imageButtonPause.getVisibility();
        }
        return View.GONE;
    }

    public int getPlayButtonVisibility() {
        if (imageButtonPlay != null) {
            return imageButtonPlay.getVisibility();
        }
        return View.GONE;
    }

    public void pausePlayer() {
        try {
            if (simpleExoPlayer != null) {
                simpleExoPlayer.pause();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void releasePlayer() {
        try {
            if (simpleExoPlayer != null) {
                simpleExoPlayer.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}