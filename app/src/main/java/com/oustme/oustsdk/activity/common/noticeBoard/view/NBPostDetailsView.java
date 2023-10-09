package com.oustme.oustsdk.activity.common.noticeBoard.view;

import android.content.Intent;

import com.oustme.oustsdk.activity.common.noticeBoard.model.request.PostViewData;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBCommentData;

import java.util.List;

/**
 * Created by oust on 2/27/19.
 */

public interface NBPostDetailsView {
    void setLikeStatus(boolean isTrue, long count);
    void setCommentStatus(boolean isTrue, long count);
    void setShareStatus(boolean isTrue, long count);
    void setBannerBg(String imageUrl);
    void setAudioView(boolean isTrue);
    void setVideoView(boolean isTrue);
    void setAttachmentView(boolean isTrue);
    void setPostCreatorDetails(String createdBy,String createdOn,String assigneeRole);
    void setPostDetails(String title, String description);
    void startApiCalls();
    void setOrUpdateAdapter(List<NBCommentData> nbCommentDataList,long nbId,long postId);
    void playAudio(String audio);
  //  void pauseAudio(String audio);
    void addVideoView(String video,long seekTo);
    void addAudioView(String audio,long seekTo);
    void startVideoPlayer(String video,long seekTo);
    void openAttachment(Intent intent);
    void onErrorWhileOpeningFile();

    void startSharingScreen(String title, String description, PostViewData postViewData);

    void setAttachmentDetails(String fileName, String fileSize, String fileType, boolean b);

    void attachmentDownloadError(String message);

    void attachmentDownloadStarted();

    void attachmentDownloadCompleted();

    void enableDisableAttachmentClick(boolean isDownloadingFile);

    void checkPermissions();
}
