package com.oustme.oustsdk.layoutFour.newnoticeBoard.presenters;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;


import com.google.gson.Gson;
import com.oustme.oustsdk.downloadmanger.DownloadManager;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.callBacks.NewPostDataRepository;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.data.handlers.NewGetPostDataTask;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.request.NewPostViewData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBCommentData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBPostData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.view.NewNBPostDetailsView;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class NewNBPostDetailsPresenter implements NewPostDataRepository {
    private static final String TAG = "NewNBPostDetailsPrese";
    private NewNBPostData nbPostData;
    private NewNBPostDetailsView nbPostDetailsView;
    private List<NewNBCommentData> tempNbCommentData = new ArrayList<>();
    private long postId;
    private boolean videoStarted = false;
    private String filepath;
    private long seekToTime = 0;

    public NewNBPostDetailsPresenter(NewNBPostDetailsView nbPostDetailsView) {
        this.nbPostDetailsView = nbPostDetailsView;
        setUserData();
    }

    public void getPostData(long postId) {
        this.postId = postId;
        new NewGetPostDataTask(this).execute(postId);
    }

    @Override
    public void gotAllPostData(ArrayList<NewNBPostData> nbPostDataList) {
        Log.d(TAG, "isis" + nbPostDataList);
    }

    public void resetVideoStarted(long time) {
        videoStarted = false;
        seekToTime = time;
    }

    @Override
    public void gotPostData(NewNBPostData nbPostData) {
        try {
            this.nbPostData = nbPostData;
            if (nbPostDetailsView != null) {
                nbPostDetailsView.setBannerBg(nbPostData.getBannerBg());
                if (nbPostData.getAudio() != null && !nbPostData.getAudio().isEmpty()) {
                    nbPostDetailsView.setAudioView(true);
                } else {
                    nbPostDetailsView.setAudioView(false);
                }

                if (nbPostData.getVideo() != null && !nbPostData.getVideo().isEmpty()) {
                    nbPostDetailsView.setVideoView(true);
                } else {
                    nbPostDetailsView.setVideoView(false);
                }

                if (!nbPostData.hasAttachment()) {
                    nbPostDetailsView.setAttachmentView(true);
                } else {
                    filepath = AppConstants.StringConstants.LOCAL_FILE_STORAGE_PATH;
                    if (nbPostData.getAttachmentsData() != null) {
                        String sizeInString = null;
                        File file = new File(filepath, getMediaFileName(nbPostData.getAttachmentsData().get(0).getFileName()));
                        nbPostDetailsView.setAttachmentDetails(getMediaFileName(nbPostData.getAttachmentsData().get(0).getFileName()), nbPostData.getAttachmentsData().get(0).getSizeInString(), nbPostData.getAttachmentsData().get(0).getFileType(), file.exists());
                    }
                    nbPostDetailsView.setAttachmentView(false);
                }

                nbPostDetailsView.setLikeStatus(nbPostData.hasLiked(), nbPostData.getLikeCount());
                nbPostDetailsView.setCommentStatus(nbPostData.hasCommented(), nbPostData.getCommentedCount());
                nbPostDetailsView.setShareStatus(nbPostData.hasShared(), nbPostData.getSharedCount());

                nbPostDetailsView.setPostCreatorDetails(nbPostData.getCreatedBy(), "" + nbPostData.getCreatedOn(),
                        nbPostData.getAssigneeRole(), nbPostData.getUserRole(), nbPostData.getDesignation(), nbPostData.getAvatar(), nbPostData);
                nbPostDetailsView.setPostDetails(nbPostData.getTitle(), nbPostData.getDescription());
                if (nbPostData.getNbCommentDataList() != null && nbPostData.getNbCommentDataList().size() > 0) {
                    tempNbCommentData = nbPostData.getNbCommentDataList();
                    for (int i = 0; i < nbPostData.getNbCommentDataList().size(); i++) {
                        if (nbPostData.getNbCommentDataList().get(i).getId() != 0) {
                            tempNbCommentData.set(i, nbPostData.getNbCommentDataList().get(i));
                        } else {
                            if (OustSdkTools.checkInternetStatus()) {
                                tempNbCommentData.remove(i);
                            }
                        }
                    }
                }
                nbPostDetailsView.setOrUpdateAdapter(tempNbCommentData, nbPostData.getNbId(), nbPostData.getId());
                if (!videoStarted && nbPostDetailsView != null) {
                    videoStarted = true;
                    nbPostDetailsView.startVideoPlayer(nbPostData.getVideo(), seekToTime);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public static String getMediaFileName(String mediaPath) {
        String fileName = null;
        try {
            String[] mediaStrs = mediaPath.split("/");
            fileName = mediaStrs[mediaStrs.length - 1];
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return fileName;
    }

    public void sendPostLikeData() {
        try {
            if (!nbPostData.hasLiked()) {
                nbPostData.setHasLiked(true);
                nbPostData.incrementLikeCount();
            } else {
                nbPostData.setHasLiked(false);
                nbPostData.decrementLikeCount();
            }
            NewPostViewData postViewData = new NewPostViewData(nbPostData.getNbId(), nbPostData.getId());
            postViewData.setLikeType()
                    .setLike(nbPostData.hasLiked());

            nbPostDetailsView.setLikeStatus(nbPostData.hasLiked(), nbPostData.getLikeCount());
            addToActionTask(postViewData);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void sendPostCommentData(String message) {
        try {
            NewNBCommentData nbCommentData = new NewNBCommentData().setComment(message)
                    .setAvatar(OustAppState.getInstance().getActiveUser().getAvatar())
                    .setUserKey(OustAppState.getInstance().getActiveUser().getStudentKey())
                    .setPostId(nbPostData.getId())
                    .setCommentedOn(System.currentTimeMillis())
                    .setCommentedBy(OustAppState.getInstance().getActiveUser().getUserDisplayName());
            nbPostData.setHasCommented(true);
            nbPostData.incrementCommentCount();
            if (!OustSdkTools.checkInternetStatus()) {
                nbPostData.addOfflineComment(nbCommentData);
            }
            NewPostViewData postViewData = new NewPostViewData(nbPostData.getNbId(), nbPostData.getId());
            postViewData.setCommentType().setNbCommentData(nbCommentData);
            nbPostDetailsView.setCommentStatus(nbPostData.hasCommented(), nbPostData.getCommentedCount());
            nbPostDetailsView.setOrUpdateAdapter(nbPostData.getNbCommentDataList(), nbPostData.getNbId(), nbPostData.getId());
            addToActionTask(postViewData);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void sendPostShareData() {
        try {
            nbPostData.setHasShared(true);
            nbPostData.incrementShareCount();

            nbPostDetailsView.setShareStatus(nbPostData.hasShared(), nbPostData.getSharedCount());
            NewPostViewData postViewData = new NewPostViewData(nbPostData.getNbId(), nbPostData.getId(), System.currentTimeMillis());
            postViewData.setShareType();
            nbPostDetailsView.startSharingScreen(nbPostData.getTitle(), nbPostData.getDescription(), postViewData);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void addToActionTask(NewPostViewData postViewData) {
        new NewNBPostAddActionTask(postViewData).execute();
    }

    public void playVideoPlayer() {
        try {
            if (nbPostDetailsView != null) {
                videoStarted = true;
                nbPostDetailsView.addVideoView(nbPostData.getVideo(), seekToTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void updateData() {
        getPostData(postId);
    }

    public void updatePlayerStarted(boolean b) {
        videoStarted = b;
    }

    public void deleteComment(NewPostViewData postViewData) {
        try {
            nbPostData.getNbCommentDataList().remove(postViewData.getNbCommentData());
            nbPostDetailsView.setOrUpdateAdapter(nbPostData.getNbCommentDataList(), nbPostData.getNbId(), nbPostData.getId());
            addToActionTask(postViewData);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public class NewNBPostAddActionTask extends AsyncTask<Void, Void, Void> {

        private final NewPostViewData postViewData;

        public NewNBPostAddActionTask(NewPostViewData postViewData) {
            this.postViewData = postViewData;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Gson gson = new Gson();
                String str = gson.toJson(postViewData);
                List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedNBRequests");
                requests.add(str);
                OustPreferences.saveLocalNotificationMsg("savedNBRequests", requests);
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (nbPostDetailsView != null) {
                nbPostDetailsView.startApiCalls();
            }
        }
    }

    public void getAudioData() {
        nbPostDetailsView.playAudio(nbPostData.getAudio());
    }

    String[] PERMISSIONS = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public void downLoadOrOpenFile() {
        try {
            String url = null;
            if (OustSdkTools.hasPermissions(OustSdkApplication.getContext(), PERMISSIONS)) {
                try {
                    filepath = AppConstants.StringConstants.LOCAL_FILE_STORAGE_PATH;
                    if (nbPostData.getAttachmentsData() != null && nbPostData.getAttachmentsData().get(0) != null) {
                        if (nbPostDetailsView != null) {
                            nbPostDetailsView.enableDisableAttachmentClick(true);
                        }
                        if (nbPostData.getAttachmentsData().get(0).getFileName() != null) {
                            if (!nbPostData.getAttachmentsData().get(0).getFileName().contains(AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_HTTPS)) {
                                url = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_HTTPS + "noticeBoard/" + nbPostData.getAttachmentsData().get(0).getFileName();
                            } else {
                                url = nbPostData.getAttachmentsData().get(0).getFileName();
                            }
                            url = OustMediaTools.getMediaFileName(nbPostData.getAttachmentsData().get(0).getFileName());
                            url = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_HTTPS + "noticeBoard/" + url;

                            final DownloadManager downloadManager = new DownloadManager(OustSdkApplication.getContext(), url, filepath, false);
                            downloadManager.setOnUpdateListener(new DownloadManager.onUpdateListener() {
                                @Override
                                public void onUpdate(int code, String message) {
                                    if (nbPostDetailsView != null) {
                                        if (code == DownloadManager.ON_COMPLETED) {
                                            nbPostDetailsView.attachmentDownloadCompleted();
                                            File file = new File(filepath, getMediaFileName(nbPostData.getAttachmentsData().get(0).getFileName()));
                                            openFile(file);
                                            nbPostDetailsView.enableDisableAttachmentClick(false);
                                            //downloadManager.cancel(true);
                                        } else if (DownloadManager.ON_PROGRASS == code) {
                                            //Log.d(TAG, "onUpdate: " + message);
                                        } else if (DownloadManager.ON_INIT == code) {
                                            nbPostDetailsView.attachmentDownloadStarted();
                                            nbPostDetailsView.enableDisableAttachmentClick(true);
                                        } else if (DownloadManager.ON_ERROR == code) {
                                            nbPostDetailsView.attachmentDownloadError(message);
                                            nbPostDetailsView.enableDisableAttachmentClick(false);
                                        } else if (DownloadManager.STATUS_DOWNLOADED == code) {
                                            //Log.d(TAG, "onUpdate: " + message);
                                        }
                                    }
                                }
                            });
                            downloadManager.execute();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            } else {
                if (nbPostDetailsView != null) {
                    nbPostDetailsView.checkPermissions();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void openFile(File url) {
        try {
            Uri uri = Uri.fromFile(url);
            //Uri uri = FileProvider.getUriForFile(OustSdkApplication.getContext(),BuildConfig.APPLICATION_ID + ".provider", url);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
                // Word document
                intent.setDataAndType(uri, "application/msword");
            } else if (url.toString().contains(".pdf")) {
                // PDF file
                intent.setDataAndType(uri, "application/pdf");
            } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
                // Powerpoint file
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
            } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
                // Excel file
                intent.setDataAndType(uri, "application/vnd.ms-excel");
            } else if (url.toString().contains(".zip") || url.toString().contains(".rar")) {
                // WAV audio file
                intent.setDataAndType(uri, "application/x-wav");
            } else if (url.toString().contains(".rtf")) {
                // RTF file
                intent.setDataAndType(uri, "application/rtf");
            } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
                // WAV audio file
                intent.setDataAndType(uri, "audio/x-wav");
            } else if (url.toString().contains(".gif")) {
                // GIF file
                intent.setDataAndType(uri, "image/gif");
            } else if (url.toString().contains(".csv")) {
                intent.setDataAndType(uri, "text/csv");
            } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
                // JPG file
                intent.setDataAndType(uri, "image/jpeg");
            } else if (url.toString().contains(".txt")) {
                // Text file
                intent.setDataAndType(uri, "text/plain");
            } else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") ||
                    url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
                // Video files
                intent.setDataAndType(uri, "video/*");
            } else {
                intent.setDataAndType(uri, "*/*");
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            nbPostDetailsView.openAttachment(intent);
        } catch (ActivityNotFoundException e) {
            nbPostDetailsView.onErrorWhileOpeningFile();
            //Toast.makeText(context, "No application found which can open the file", Toast.LENGTH_SHORT).show();
        }
    }

    private void setUserData() {
        try {
            ActiveUser activeUser = OustAppState.getInstance().getActiveUser();
            if ((activeUser != null) && (activeUser.getStudentid() != null)) {
            } else {
                String activeUserGet = OustPreferences.get("userdata");
                activeUser = OustSdkTools.getActiveUserData(activeUserGet);
                OustFirebaseTools.initFirebase();
                OustAppState.getInstance().setActiveUser(activeUser);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void onDestroy() {
        nbPostDetailsView = null;
    }
}