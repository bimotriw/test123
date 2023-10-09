package com.oustme.oustsdk.skill_ui;

import static android.view.View.VISIBLE;

import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class FullScreenVideoActivity extends AppCompatActivity {

    ImageView close_screen;
    FrameLayout media_container;
    VideoView private_video;
    ImageView skill_video_thumbnail;
    ImageView play_button;

    //public
    RelativeLayout public_layout;
    YouTubePlayerView youtube_frame;

    String videoUri;
    String type;


    private String youtubeKey = "";
    private boolean fullScreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OustSdkTools.setLocale(FullScreenVideoActivity.this);
        setContentView(R.layout.activity_full_screen_video);

        youtube_frame = findViewById(R.id.youtube_frame);
        public_layout = findViewById(R.id.public_layout);
        play_button = findViewById(R.id.play_button);
        skill_video_thumbnail = findViewById(R.id.skill_video_thumbnail);
        private_video = findViewById(R.id.private_video);
        media_container = findViewById(R.id.media_container);
        close_screen = findViewById(R.id.close_screen);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            videoUri = bundle.getString("videoName");
            type = bundle.getString("videoType");
        }

        close_screen.setOnClickListener(v -> FullScreenVideoActivity.this.finish());

        if (type != null && type.equalsIgnoreCase("Private")) {
            if (videoUri != null && !videoUri.isEmpty()) {

                media_container.setVisibility(VISIBLE);
                Uri uri = Uri.parse(videoUri);
                private_video.setVideoURI(uri);
                private_video.setMediaController(new MediaController(this));
                private_video.setOnErrorListener((mp, what, extra) -> {
                    skill_video_thumbnail.setVisibility(VISIBLE);
                    play_button.setVisibility(VISIBLE);
                    return false;
                });

                private_video.setOnPreparedListener(mp -> {
                    play_button.setVisibility(View.GONE);
                    skill_video_thumbnail.setVisibility(View.GONE);
                    mp.setOnVideoSizeChangedListener((mp1, arg1, arg2) -> {
                        play_button.setVisibility(View.GONE);
                        skill_video_thumbnail.setVisibility(View.GONE);
                    });
                });

                private_video.setOnCompletionListener(mp -> {
                    play_button.setVisibility(VISIBLE);
                    skill_video_thumbnail.setVisibility(View.VISIBLE);
                });

                private_video.requestFocus();
                private_video.start();


            }
        } else {
            if (videoUri != null && !videoUri.isEmpty()) {

                youtubeKey = videoUri;
                if (youtubeKey.contains("https://www.youtube.com/watch?v=")) {
                    youtubeKey = youtubeKey.replace("https://www.youtube.com/watch?v=", "");
                }
                if (youtubeKey.contains("https://youtu.be/")) {
                    youtubeKey = youtubeKey.replace("https://youtu.be/", "");
                }
                if (youtubeKey.contains("&")) {
                    int position = youtubeKey.indexOf("&");
                    youtubeKey = youtubeKey.substring(0, position);
                }
                initYouTubeView();
            }

        }
    }

    private void initYouTubeView() {
        try {
            public_layout.setVisibility(VISIBLE);
            getLifecycle().addObserver(youtube_frame);

            YouTubePlayerListener youTubePlayerListener = (new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                    //String videoId = "S0Q4gqBUs7c";
                    Log.d("Youtube", "initYouTubeView -> onReady: " + youtubeKey);
                    youTubePlayer.loadVideo(youtubeKey, 0f);
                }
            });

            youtube_frame.initialize(youTubePlayerListener);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
}
