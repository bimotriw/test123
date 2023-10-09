package com.oustme.oustsdk.customviews;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.util.CacheDataSourceFactory;

import java.io.File;

public abstract class NewCustomExoPlayerView {

    private StyledPlayerView simpleExoPlayerView;
    private ExoPlayer simpleExoPlayer;
    private LinearLayout nointernet_exo_ll;
    private boolean isVideoCompleted = false;
    private Context context;

    DefaultTimeBar timeBar;

    public void setDefaultTime(long defaultTime) {
        this.defaultTime = defaultTime;
    }

    private long defaultTime = 0;
    private RelativeLayout parent_video_ll;
    private ImageButton imageButtonPause, imageButtonPlay, imageButtoRewind;
    private RelativeLayout layout_exoplayer_new_ui;
    private LinearLayout layout_exoplayer_old_ui, layout_bottom;
    private ImageButton imageButtonRewindNew, imageButtonPlayNew, imageButtonPauseNew, imagebutton_exo_mute, imageButtonFfwdNew;
    private final String TAG = "NewCustomExoPlayer";
    private long currentPosition = 0;
    private boolean isSeekbarchanged = false;
    TextView exo_duration;
    long userVideoViewInterval;

    public void setVideoCompleted(boolean videoCompleted) {
        this.isVideoCompleted = videoCompleted;
    }

    public void initExoPlayer(RelativeLayout parent_video_l, Context context, final String path, final String fileName, long userVideoViewInterval) {
        try {
            this.context = context;
            this.parent_video_ll = parent_video_l;
            currentPosition = 0;
            isSeekbarchanged = false;
            this.userVideoViewInterval = userVideoViewInterval;

            LayoutInflater layoutInflater = LayoutInflater.from(context);

            View view = layoutInflater.inflate(R.layout.exo_player_view, null);

            simpleExoPlayerView = (StyledPlayerView) view.findViewById(R.id.video_view);


            //simpleExoPlayerView = new PlayerView(context);
            simpleExoPlayerView.setLayoutParams(new PlayerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            simpleExoPlayerView.setBackgroundColor(Color.TRANSPARENT);

            parent_video_ll.addView(simpleExoPlayerView);

            simpleExoPlayerView.setVisibility(View.VISIBLE);

            layout_bottom = simpleExoPlayerView.findViewById(R.id.layout_bottom);
            if (layout_bottom != null) {
                layout_bottom.setBackground(null);
            }
            timeBar = simpleExoPlayerView.findViewById(R.id.exo_progress);

            nointernet_exo_ll = simpleExoPlayerView.findViewById(R.id.nointernet_exo_ll);
            nointernet_exo_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reInitVideo(path, fileName);
                }
            });

            timeBar.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    Log.d(TAG, "onTouch: timebar -- " + motionEvent.getAction() + " -- position:" + simpleExoPlayer.getCurrentPosition() + "--- current positiion:" + currentPosition);
                    if (isVideoCompleted) {
                        isVideoCompleted = false;
                        imageButtonPauseNew.setVisibility(View.VISIBLE);
                        imageButtonPlayNew.setVisibility(View.GONE);
                    }

                    if (currentPosition < simpleExoPlayer.getCurrentPosition()) {
                        currentPosition = simpleExoPlayer.getCurrentPosition();
                    }
                    isSeekbarchanged = true;
                    return false;
                }
            });


            layout_exoplayer_new_ui = simpleExoPlayerView.findViewById(R.id.layout_exoplayer_new_ui);
            layout_exoplayer_old_ui = simpleExoPlayerView.findViewById(R.id.layout_exoplayer_old_ui);
            layout_exoplayer_new_ui.setVisibility(View.VISIBLE);
            layout_exoplayer_old_ui.setVisibility(View.GONE);

            imageButtonPause = simpleExoPlayerView.findViewById(R.id.exo_pause);
            imageButtonPlay = simpleExoPlayerView.findViewById(R.id.exo_play);
            imageButtoRewind = simpleExoPlayerView.findViewById(R.id.exo_rew);
            imageButtonFfwdNew = simpleExoPlayerView.findViewById(R.id.exo_ffw_new);
            exo_duration = simpleExoPlayerView.findViewById(R.id.exo_duration);
            imageButtonFfwdNew.setVisibility(View.GONE);

            imageButtonPauseNew = simpleExoPlayerView.findViewById(R.id.exo_pause_new);
            imageButtonPlayNew = simpleExoPlayerView.findViewById(R.id.exo_play_new);
            imageButtonRewindNew = simpleExoPlayerView.findViewById(R.id.exo_rew_new);

            imageButtonFfwdNew.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OustSdkTools.showToast("FastForward will not work now");
                }
            });

            imageButtonPauseNew.setVisibility(View.VISIBLE);
            imageButtonPlayNew.setVisibility(View.GONE);

            imageButtonRewindNew.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: Imagebutton Rewind");
                    //imageButtoRewind.performClick();
                    if (simpleExoPlayer.getCurrentPosition() > 10000) {
                        simpleExoPlayer.seekTo(simpleExoPlayer.getCurrentPosition() - 10000);
                    } else {
                        simpleExoPlayer.seekTo(0);
                    }
                    //OustSdkTools.showToast("Rewind will not work now");
                }
            });

            imageButtonPlayNew.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    performPlayclick();
                }
            });

            imageButtonPauseNew.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    performPauseclick();
                }
            });
            imageButtonPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    simpleExoPlayer.setPlayWhenReady(true);
                }
            });

            imageButtonPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    simpleExoPlayer.setPlayWhenReady(false);
                }
            });

            DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter(); //test

            /*AdaptiveTrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory();
            TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);*/

            simpleExoPlayer = new SimpleExoPlayer.Builder(context).build();
            CacheDataSourceFactory dataSourceFactory = new CacheDataSourceFactory(context, 100 * 1024 * 1024, 5 * 1024 * 1024);

            String downloaded_path = Environment.getExternalStorageDirectory() + "/Android/data/" + OustSdkApplication.getContext().getPackageName() + "/files/" + fileName;
            File file = new File(downloaded_path);
            MediaSource videoSource;

            if (!file.exists()) {
                Uri videoUri = Uri.parse(path);
                videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(videoUri));
                //videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(videoUri);
            } else {
                ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                Uri videoUri = Uri.fromFile(file);
                Log.e("Player", "" + videoUri);
                Log.d(TAG, "startVideoPlayer: play from FILE");
                DataSpec dataSpec = new DataSpec(videoUri);
                final FileDataSource fileDataSource = new FileDataSource();
                try {
                    fileDataSource.open(dataSpec);
                } catch (FileDataSource.FileDataSourceException e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }

                DataSource.Factory factory = new DataSource.Factory() {
                    @Override
                    public DataSource createDataSource() {
                        return fileDataSource;
                    }
                };
                //videoSource = new ExtractorMediaSource(fileDataSource.getUri(), factory, extractorsFactory, null, null);
                videoSource = new ProgressiveMediaSource.Factory(factory).createMediaSource(MediaItem.fromUri(fileDataSource.getUri()));

            }

            //videoSource = new HlsMediaSource(videoUri, dataSourceFactory, 1, null, null);

            simpleExoPlayerView.setPlayer(simpleExoPlayer);
            simpleExoPlayerView.hideController();
            simpleExoPlayer.setMediaSource(videoSource);
            simpleExoPlayer.prepare();
            simpleExoPlayer.addListener(new ExoPlayer.EventListener() {
                @Override
                public void onTimelineChanged(Timeline timeline, int reason) {
                    Log.d(TAG, "onTimelineChanged: ");
                }

                @Override
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                    Log.e(TAG, "onTracksChanged");
                }

                @Override
                public void onLoadingChanged(boolean isLoading) {
                    Log.e(TAG, "onLoadingChanged----" + isLoading + " -- position:" + simpleExoPlayer.getCurrentPosition() + "--- current Position:" + currentPosition);
                    try {
                        if (isLoading) {
                            if (nointernet_exo_ll.getVisibility() == View.VISIBLE) {
                                nointernet_exo_ll.setVisibility(View.GONE);
                            }
                        }

                        if (isSeekbarchanged) {
                            isSeekbarchanged = false;
                            if (simpleExoPlayer.getCurrentPosition() > currentPosition) {
                                simpleExoPlayer.seekTo(currentPosition);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//                    Log.e(TAG, "onPlayerStateChanged-----" + playbackState);

                    try {
                        if (playbackState == simpleExoPlayer.STATE_IDLE || playbackState == simpleExoPlayer.STATE_ENDED ||
                                !playWhenReady) {
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

                    if (isSeekbarchanged) {
                        isSeekbarchanged = false;
                        if (simpleExoPlayer.getCurrentPosition() > currentPosition) {
                            simpleExoPlayer.seekTo(currentPosition);
                        }
                    }

                    if (playbackState == simpleExoPlayer.STATE_READY) {
                        Log.e(TAG, "STATE_READY");
                        onPlayReady();
                        timeBar.setVisibility(View.VISIBLE);
                    } else if (playbackState == simpleExoPlayer.STATE_BUFFERING) {
                        Log.e(TAG, "STATE_BUFFERING");
                        onBuffering();
                        timeBar.setVisibility(View.GONE);
//                        timeBar.setOnTouchListener((v, event) -> false);
                    } else if (playbackState == simpleExoPlayer.STATE_IDLE) {
                        Log.e(TAG, "STATE_IDLE");
                        onIdle();
                        timeBar.setVisibility(View.VISIBLE);
                    } else if (playbackState == simpleExoPlayer.STATE_ENDED) {
                        Log.e(TAG, "STATE_ENDED");
                        if (!isVideoCompleted)
                            onVideoComplete();
                        isVideoCompleted = true;
                        imageButtonPauseNew.setVisibility(View.GONE);
                        imageButtonPlayNew.setVisibility(View.VISIBLE);
                        timeBar.setVisibility(View.VISIBLE);
                        if (simpleExoPlayerView != null)
                            simpleExoPlayerView.showController();

                    }
                }

                @Override
                public void onRepeatModeChanged(int repeatMode) {
                    Log.d(TAG, "onRepeatModeChanged: ");
                }

                @Override
                public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
                    Log.d(TAG, "onShuffleModeEnabledChanged: ");
                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {
                    error.getStackTrace();
                    Log.e(TAG, "onPlayerError->" + error.getMessage());
                    if (nointernet_exo_ll.getVisibility() == View.GONE) {
                        nointernet_exo_ll.setVisibility(View.VISIBLE);
                    }
                    onVideoError();
                }

                @Override
                public void onPositionDiscontinuity(int reason) {
                    Log.d(TAG, "onPositionDiscontinuity: ");
                }

                @Override
                public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                    Log.e(TAG, "onPlaybackParametersChanged");
                }

                @Override
                public void onSeekProcessed() {
                    Log.d(TAG, "onSeekProcessed: ");
                }
            });
            // simpleExoPlayer.addListener(eventListener);
            simpleExoPlayer.setPlayWhenReady(true);
            //            long seeKtime = OustPreferences.getTimeForNotification("VideoOverlayCardCurrentPositionTime");
            //if(seeKtime>0 && (seeKtime<simpleExoPlayer.getDuration())){
            if (userVideoViewInterval > 0) {
                simpleExoPlayer.seekTo(userVideoViewInterval);
                Log.d("userVideoViewInterval0", " " + userVideoViewInterval);
            } else {
                simpleExoPlayer.seekTo(defaultTime);
                Log.d("userVideoViewInterval1", " " + userVideoViewInterval);
            }
            Log.d("userVideoViewInterval2", " " + userVideoViewInterval);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void reInitVideo(String path, String fileName) {
        removeVideoPlayer();
        initExoPlayer(parent_video_ll, context, path, fileName, userVideoViewInterval);
    }

    public StyledPlayerView getSimpleExoPlayerView() {
        return simpleExoPlayerView;
    }

    public ExoPlayer getSimpleExoPlayer() {
        return simpleExoPlayer;
    }

    public void removeVideoPlayer() {
        try {
            currentPosition = 0;
            isSeekbarchanged = false;
            if (simpleExoPlayerView != null && simpleExoPlayer != null && simpleExoPlayerView.getPlayer() != null) {
                defaultTime = simpleExoPlayerView.getPlayer().getCurrentPosition();
                simpleExoPlayerView.getPlayer().release();
                simpleExoPlayerView.setPlayer(null);
                simpleExoPlayerView.removeAllViews();
                parent_video_ll.removeAllViews();
                if (simpleExoPlayer != null) {
                    simpleExoPlayer.stop();
                    simpleExoPlayer.release();
                    simpleExoPlayer = null;
                }
                Log.e(TAG, "removeVideoPlayer");
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

    public abstract void onIdle();

    public abstract void onRestarted();

    public void removeAudioPlayer() {
        try {
            if (simpleExoPlayerView != null && simpleExoPlayer != null && simpleExoPlayerView.getPlayer() != null) {
                defaultTime = simpleExoPlayerView.getPlayer().getCurrentPosition();
                simpleExoPlayerView.getPlayer().release();
                simpleExoPlayerView.setPlayer(null);
                simpleExoPlayerView.removeAllViews();
                parent_video_ll.removeAllViews();
                simpleExoPlayer.stop();
                simpleExoPlayer.release();
                simpleExoPlayer = null;
                Log.e(TAG, "removeVideoPlayer");
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void performPauseclick() {
        Log.d(TAG, "performPauseclick: ");
        if (imageButtonPause != null) {
            imageButtonPause.performClick();
        }

        imageButtonPauseNew.setVisibility(View.GONE);
        imageButtonPlayNew.setVisibility(View.VISIBLE);
    }

    public void performPlayclick() {
        Log.d(TAG, "performPlayclick: ");
        if (imageButtonPlay != null) {
            imageButtonPlay.performClick();
        }

        if (isVideoCompleted) {
            Log.d(TAG, "performPlayclick: ");
            isVideoCompleted = false;
            onRestarted();
        }

        imageButtonPauseNew.setVisibility(View.VISIBLE);
        imageButtonPlayNew.setVisibility(View.GONE);
    }

    public int getPauseButtonVisibility() {
        if (imageButtonPauseNew != null) {
            return imageButtonPauseNew.getVisibility();
        }
        return View.GONE;
    }

    public void changeExoplayerSeekTime(long seekTime) {
        Log.d(TAG, "changeExoplayerSeekTime: " + seekTime);
        if (simpleExoPlayer != null) {
            simpleExoPlayer.seekTo(seekTime);
        }
    }

    public void getTotalTime() {
        OustPreferences.saveTimeForNotification("VideoOverlayCardTotalVideoTime", Long.parseLong(exo_duration.getText().toString()));
    }
}