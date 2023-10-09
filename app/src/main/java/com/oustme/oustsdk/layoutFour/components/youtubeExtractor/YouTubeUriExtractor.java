package com.oustme.oustsdk.layoutFour.components.youtubeExtractor;

import android.content.Context;
import android.util.SparseArray;

@Deprecated
public abstract class YouTubeUriExtractor extends YouTubeExtractor {

    public YouTubeUriExtractor(Context con) {
        super(con);
    }

    @Override
    protected void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta videoMeta) {
        if (videoMeta != null) {
            onUrisAvailable(videoMeta.getVideoId(), videoMeta.getTitle(), ytFiles);
        }
    }

    public abstract void onUrisAvailable(String videoId, String videoTitle, SparseArray<YtFile> ytFiles);
}
