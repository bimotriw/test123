package com.oustme.oustsdk.adapter.appTutorial;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.model.orgSettings.AppTutorial;
import com.oustme.oustsdk.model.response.common.AppTutorialDataModel;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.ArrayList;
import java.util.Objects;

public class AppTutorialAdapter extends RecyclerView.Adapter<AppTutorialAdapter.ViewHolder> {

    private final ArrayList<AppTutorialDataModel> appTutorialList;
    private final Context context;
    private final VideoInterFace videoInterFace;
    private SimpleExoPlayer tempSimpleExo;

    public AppTutorialAdapter(ArrayList<AppTutorialDataModel> appTutorialList, Context context) {
        this.appTutorialList = appTutorialList;
        this.context = context;
        this.videoInterFace = (VideoInterFace) context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.app_tutorial_screen, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (appTutorialList.get(position) != null) {
            if (appTutorialList.get(position).getMediaType() != null) {
                if (appTutorialList.get(position).getMediaType().equalsIgnoreCase("VIDEO")) {
                    holder.app_tutorial_video.setVisibility(View.VISIBLE);
                    holder.app_tutorial_image.setVisibility(View.GONE);
                    tempSimpleExo = holder.player;
                    /*if (appTutorialList.get(position).getGumletVideoUrl() != null && !appTutorialList.get(position).getGumletVideoUrl().isEmpty()) {
                        playVideo(appTutorialList.get(position).getGumletVideoUrl(), holder.player);
                    } else*/
                    if (appTutorialList.get(position).getMediaUrl() != null && !appTutorialList.get(position).getMediaUrl().isEmpty()) {
                        playVideo(appTutorialList.get(position).getMediaUrl(), holder.player);
                    }
                } else {
                    holder.app_tutorial_image.setVisibility(View.VISIBLE);
                    holder.app_tutorial_video.setVisibility(View.GONE);
                    Glide.with(context)
                            .load(appTutorialList.get(position).getMediaUrl())
                            .apply(new RequestOptions().override(720, 1280))
                            .into(holder.app_tutorial_image);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return appTutorialList.size();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.pausePlayer();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder.getAbsoluteAdapterPosition() >= 0) {
            if (appTutorialList.get(holder.getAdapterPosition()).getMediaType().equalsIgnoreCase("VIDEO")) {
                holder.startPlayer();
            }
        }
    }

    void playVideo(String videoPath, ExoPlayer player) {
        try {
            MediaSource mediaSource;
            Uri videoUri = Uri.parse(videoPath);
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context);
            mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(videoUri));
            Log.d("playVideo", "video source path: " + videoPath);
            player.addMediaSource(mediaSource);
            player.prepare();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    public void stopPlayer() {
        try {
            if (tempSimpleExo != null) {
                tempSimpleExo.setPlayWhenReady(false);
                tempSimpleExo.getPlaybackState();
                appTutorialList.clear();
                tempSimpleExo.release();
                notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView app_tutorial_image;
        StyledPlayerView app_tutorial_video;
        SimpleExoPlayer player;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            app_tutorial_image = itemView.findViewById(R.id.app_tutorial_image);
            app_tutorial_video = itemView.findViewById(R.id.app_tutorial_video);

            DefaultRenderersFactory rf = new DefaultRenderersFactory(context).setEnableDecoderFallback(true);
            player = new SimpleExoPlayer.Builder(Objects.requireNonNull(context), rf).build();
//            player = new SimpleExoPlayer.Builder(context).build();
            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);
//            app_tutorial_video.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
            app_tutorial_video.setShowNextButton(false);
            app_tutorial_video.setShowFastForwardButton(false);
            app_tutorial_video.setShowPreviousButton(false);
            app_tutorial_video.setShowRewindButton(false);

            app_tutorial_video.setPlayer(player);
            tempSimpleExo = player;
            player.addListener(new Player.Listener() {
                @Override
                public void onPlaybackStateChanged(int state) {
                    Player.Listener.super.onPlaybackStateChanged(state);
                    if (state == Player.STATE_ENDED) {
                        if (videoInterFace != null) {
                            videoInterFace.moveToNextPosition(getAbsoluteAdapterPosition());
                        }
                    }
                }

                @Override
                public void onPlayerError(@NonNull ExoPlaybackException error) {
                    Player.Listener.super.onPlayerError(error);
                }
            });
        }

        public void startPlayer() {
            try {
                player.setPlayWhenReady(true);
                player.getPlaybackState();
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        }

        public void pausePlayer() {
            try {
                player.setPlayWhenReady(false);
                player.getPlaybackState();
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        }
    }

    public interface VideoInterFace {
        void moveToNextPosition(int position);
    }

}