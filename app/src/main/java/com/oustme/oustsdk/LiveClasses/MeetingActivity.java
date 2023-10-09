package com.oustme.oustsdk.LiveClasses;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.media.AudioManager;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.services.chime.sdk.meetings.audiovideo.AttendeeInfo;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.AudioVideoConfiguration;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.AudioVideoObserver;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.SignalUpdate;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.VolumeUpdate;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.audio.AudioMode;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.audio.AudioRecordingPresetOverride;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.audio.AudioStreamType;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.contentshare.ContentShareObserver;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.contentshare.ContentShareSource;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.contentshare.ContentShareStatus;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.DefaultVideoRenderView;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.RemoteVideoSource;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.VideoTileObserver;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.VideoTileState;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.capture.CaptureSourceError;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.capture.CaptureSourceObserver;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.capture.DefaultScreenCaptureSource;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.capture.DefaultSurfaceTextureCaptureSourceFactory;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.capture.SurfaceTextureCaptureSourceFactory;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.gl.DefaultEglCoreFactory;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.gl.EglCoreFactory;
import com.amazonaws.services.chime.sdk.meetings.device.DeviceChangeObserver;
import com.amazonaws.services.chime.sdk.meetings.device.MediaDevice;
import com.amazonaws.services.chime.sdk.meetings.device.MediaDeviceType;
import com.amazonaws.services.chime.sdk.meetings.realtime.RealtimeObserver;
import com.amazonaws.services.chime.sdk.meetings.session.Attendee;
import com.amazonaws.services.chime.sdk.meetings.session.CreateAttendeeResponse;
import com.amazonaws.services.chime.sdk.meetings.session.CreateMeetingResponse;
import com.amazonaws.services.chime.sdk.meetings.session.DefaultMeetingSession;
import com.amazonaws.services.chime.sdk.meetings.session.MediaPlacement;
import com.amazonaws.services.chime.sdk.meetings.session.Meeting;
import com.amazonaws.services.chime.sdk.meetings.session.MeetingSession;
import com.amazonaws.services.chime.sdk.meetings.session.MeetingSessionConfiguration;
import com.amazonaws.services.chime.sdk.meetings.session.MeetingSessionStatus;
import com.amazonaws.services.chime.sdk.meetings.session.MeetingSessionStatusCode;
import com.amazonaws.services.chime.sdk.meetings.utils.logger.LogLevel;
import com.amazonaws.services.chime.sdk.meetings.utils.logger.Logger;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.oustme.oustsdk.LiveClasses.adapter.MeetingAdapter;
import com.oustme.oustsdk.LiveClasses.dialogFragment.LiveClassesMembersDialogFragment;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.ScreenCaptureService;
import com.oustme.oustsdk.feed_ui.tools.FullScreenHelper;
import com.oustme.oustsdk.request.MeetingCreateParticipant;
import com.oustme.oustsdk.response.common.JoinRegisterMeetingResponse;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;

import org.amazon.chime.webrtc.voiceengine.WebRtcAudioUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MeetingActivity extends AppCompatActivity {

    private static final int SCREEN_CAPTURE_REQUEST_CODE = 102;
    private final String TAG = "MeetingActivity";
    MeetingSession meetingSession1;
    private ArrayList<MeetingCreateParticipant> participantArrayList;
    RecyclerView meeting_recycler_view;
    LinearLayout bottom_icons;
    ImageView call_end;
    ImageView video_enable_disable;
    ImageView audio_mute_unMute;
    DefaultVideoRenderView full_screen_video_surface;
    ImageView camera_switch_live_class;
    ImageView speaker_icon_live_class;
    ImageView setting_live_class;
    LinearLayout members_layout;
    TextView members_count_txt;
    CardView meeting_share_screen_card_view;
    CardView full_screen_share_meeting_card_view;
    DefaultVideoRenderView meeting_share_screen_surface;
    TextView meeting_stop_screen_share;
    TextView meeting_stop_full_screen_share;
    TextView full_screen_meeting_presenter_name;
    ImageView stop_screen_sharing_icon;
    ImageView stop_full_screen_sharing_icon;
    TextView meeting_presenter_name;
    ImageView screen_max_icon;
    ImageView screen_min_icon;
    AudioManager audioManager;
    boolean muteAndUnMuteIcon = true;
    boolean shareAndUnShareIcon = false;
    boolean stopAndStartVideoIcon = false;

    ServiceConnection serviceConnection;
    EglCoreFactory eglCoreFactory;

    MediaProjectionManager mediaProjectionManager;
    DisplayManager displayManager;
    PowerManager powerManager;

    DefaultScreenCaptureSource defaultScreenCaptureSource;
    JoinRegisterMeetingResponse joinRegisterMeetingResponse;
    MeetingActivityViewModel meetingActivityViewModel;
    MeetingAdapter meetingAdapter;
    int position = 0;
    FullScreenHelper fullScreenHelper;
    Intent intent2;
    String meetStartTime;
    String meetEndTime;
    String responseString;
    String meetName;
    String meetTrainerName;
    String tempValLiveClassId;
    String tempValLiveClassMeetingMapId;
    long liveClassId;
    long liveClassMeetingMapId;
    String userId;
    String orgId;
    boolean screenSharingStatus = false;
    boolean isServiceConnected = false;
    boolean openingFromBranchTools = false;
    boolean muteWhileJoiningUser = false;
    int screenSharingCount = 0;
    private CounterClass timer;
    ActiveUser activeUser;
    Menu menu;
    public static boolean isDialogFragmentShown = false;
    MeetingCreateParticipant meetingScreenShareParticipant = new MeetingCreateParticipant();
//    List<MediaDevice> audioDevices;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (isServiceConnected && intent2 != null) {
                stopService(intent2);
            }

            if (meetingSession1 != null) {
                if (defaultScreenCaptureSource != null) {
                    defaultScreenCaptureSource.stop();
                }
                meetingSession1.getAudioVideo().stopContentShare();
                meetingSession1.getAudioVideo().stopRemoteVideo();
                meetingSession1.getAudioVideo().stop();
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_meeting);

        meeting_recycler_view = findViewById(R.id.meeting_recycler_view);
        bottom_icons = findViewById(R.id.bottom_icons);
        call_end = findViewById(R.id.call_end);
        video_enable_disable = findViewById(R.id.video_enable_disable);
        audio_mute_unMute = findViewById(R.id.audio_mute_unMute);
        members_count_txt = findViewById(R.id.members_count);
        members_layout = findViewById(R.id.members_layout);
        speaker_icon_live_class = findViewById(R.id.speaker_icon_live_class);
        camera_switch_live_class = findViewById(R.id.camera_switch_live_class);
        full_screen_video_surface = findViewById(R.id.full_screen_video_surface);
        setting_live_class = findViewById(R.id.setting_live_class);
        meeting_share_screen_card_view = findViewById(R.id.meeting_share_screen_card_view);
        full_screen_share_meeting_card_view = findViewById(R.id.full_screen_share_meeting_card_view);
        meeting_share_screen_surface = findViewById(R.id.meeting_share_screen_surface);
        meeting_stop_screen_share = findViewById(R.id.meeting_stop_screen_share);
        meeting_stop_full_screen_share = findViewById(R.id.meeting_stop_full_screen_share);
        stop_full_screen_sharing_icon = findViewById(R.id.stop_full_screen_sharing_icon);
        stop_screen_sharing_icon = findViewById(R.id.stop_screen_sharing_icon);
        meeting_presenter_name = findViewById(R.id.meeting_presenter_name);
        screen_max_icon = findViewById(R.id.screen_max_icon);
        screen_min_icon = findViewById(R.id.screen_min_icon);
        full_screen_meeting_presenter_name = findViewById(R.id.full_screen_meeting_presenter_name);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent intent = getIntent();
        meetName = intent.getStringExtra("meetName");
        meetTrainerName = intent.getStringExtra("meetTrainerName");
        meetStartTime = intent.getStringExtra("meetStartTime");
        meetEndTime = intent.getStringExtra("meetEndTime");
        responseString = intent.getStringExtra("joinRegisterMeetingResponse");
        tempValLiveClassId = intent.getStringExtra("meetClassId");
        tempValLiveClassMeetingMapId = intent.getStringExtra("meetLiveClassId");
        openingFromBranchTools = intent.getBooleanExtra("openingFromBranchTools", false);

        Gson gson = new Gson();
        joinRegisterMeetingResponse = gson.fromJson(responseString, JoinRegisterMeetingResponse.class);

        if (joinRegisterMeetingResponse != null) {
            liveClassId = OustSdkTools.convertToLong(tempValLiveClassId);
            liveClassMeetingMapId = OustSdkTools.convertToLong(tempValLiveClassMeetingMapId);
            userId = joinRegisterMeetingResponse.getUserId();
            String tenantName = "";
            if (OustPreferences.get("tanentid") != null && !OustPreferences.get("tanentid").isEmpty()) {
                tenantName = OustPreferences.get("tanentid").replaceAll(" ", "");
            }
            orgId = tenantName;
        }

        activeUser = OustAppState.getInstance().getActiveUser();
        fullScreenHelper = new FullScreenHelper(MeetingActivity.this);
        fullScreenHelper.enterFullScreen();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        meetingActivityViewModel = new ViewModelProvider(this).get(MeetingActivityViewModel.class);
        intent2 = new Intent(MeetingActivity.this, ScreenCaptureService.class);

        String meetingStrWithOutSpace = joinRegisterMeetingResponse.getMediaPlacement().replace(" ", "");
        Log.d(TAG, "onCreate: meetingStrWithOutSpace:" + meetingStrWithOutSpace);
        String[] meetingArray = meetingStrWithOutSpace.split(",");
        String[] array1 = meetingArray[0].split("Url:");
        String[] array2 = meetingArray[1].split("Url:");
        String[] array3 = meetingArray[5].split("Url:");
        String[] array4 = meetingArray[6].split("Url:");
        String[] array5 = meetingArray[7].split("Url:");

        String audioFallbackURL = array2[1];
        Log.d(TAG, "onCreate: audioFallbackURL:" + audioFallbackURL);

        String audioHostURL = array1[1];
        Log.d(TAG, "onCreate: audioHostURL:" + audioHostURL);

        String signalingURL = array3[1];
        Log.d(TAG, "onCreate: signalingURL:" + signalingURL);

        String turnControlURL = array4[1];
        Log.d(TAG, "onCreate: turnControlURL:" + turnControlURL);

        String ingestionURL = array5[1];
        Log.d(TAG, "onCreate: ingestionURL:" + ingestionURL);

        String externalMeetingId = "";
        String mediaRegion = joinRegisterMeetingResponse.getMediaRegion();
        String meetingId = joinRegisterMeetingResponse.getMeetingId();

        String attendId = joinRegisterMeetingResponse.getAttendeeId(),
                externalUserId = joinRegisterMeetingResponse.getUserId(),
                joinToken = joinRegisterMeetingResponse.getJoinToken();

        mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        displayManager = (DisplayManager) getApplicationContext().getSystemService(Context.DISPLAY_SERVICE);
        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(true);
        WebRtcAudioUtils.setWebRtcBasedAutomaticGainControl(true);
        WebRtcAudioUtils.setWebRtcBasedNoiseSuppressor(true);
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);

        /*DeviceChangeObserver deviceChangeObserver = list -> {
            for (MediaDevice mediaDevice : audioDevices) {
                Log.e(TAG, "onCreate:- onAudioDeviceChanged--> " + mediaDevice + " TYpe--> " + mediaDevice.getType().name());
//            meetingSession1.getAudioVideo().chooseAudioDevice();

            }
        };*/

        VideoTileObserver videoTileObserver = new VideoTileObserver() {
            final String TAG1 = "VideoTileObserver";

            @Override
            public void onVideoTileAdded(@NonNull VideoTileState videoTileState) {
                Log.d(TAG1, "onVideoTileAdded: ");
                try {
                    if (videoTileState.getAttendeeId().contains("#content")) {
                        Log.e(TAG, "check --> -if- onAttendeesJoined --> " + videoTileState);
                        if (meetingScreenShareParticipant != null && meetingScreenShareParticipant.getAttendeeId() != null && meetingScreenShareParticipant.getAttendeeId().equals(videoTileState.getAttendeeId())) {
                            meetingScreenShareParticipant.setVideoTileState(videoTileState);
                            meetingScreenShareParticipant.setVideo(true);
                            meetingScreenShareParticipant.setUserSpeaking(false);
                            meetingScreenShareParticipant.setScreenSharing(true);
                        }
                        setScreenShare(meetingScreenShareParticipant, "START");
                    } else {
                        if (participantArrayList != null && participantArrayList.size() > 0) {
                            for (int i = 0; i < participantArrayList.size(); i++) {
                                if (participantArrayList.get(i).getAttendeeId().equals(videoTileState.getAttendeeId())) {
                                    participantArrayList.get(i).setVideoTileState(videoTileState);
                                    participantArrayList.get(i).setVideo(true);
                                    participantArrayList.get(i).setScreenSharing(false);
                                    break;
                                }
                            }
                        }
                        setUpParticipantAdapter();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onVideoTileRemoved(@NonNull VideoTileState videoTileState) {
                Log.d(TAG1, "onVideoTileRemoved: ");
                try {
                    if (videoTileState.getAttendeeId().contains("#content")) {
                        if (meetingScreenShareParticipant.getAttendeeId().equals(videoTileState.getAttendeeId())) {
                            meetingScreenShareParticipant.setVideoTileState(videoTileState);
                            meetingScreenShareParticipant.setVideo(false);
                            meetingScreenShareParticipant.setScreenSharing(false);
                        }
                        setScreenShare(meetingScreenShareParticipant, "STOP");
                    } else {
                        if (participantArrayList != null && participantArrayList.size() > 0) {
                            for (int i = 0; i < participantArrayList.size(); i++) {
                                if (participantArrayList.get(i).getAttendeeId().equals(videoTileState.getAttendeeId())) {
                                    participantArrayList.get(i).setVideoTileState(videoTileState);
                                    participantArrayList.get(i).setVideo(false);
                                    participantArrayList.get(i).setScreenSharing(false);
                                    break;
                                }
                            }
                        }
                        setUpParticipantAdapter();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onVideoTilePaused(@NonNull VideoTileState videoTileState) {
                Log.d(TAG1, "onVideoTilePaused: ");
            }

            @Override
            public void onVideoTileResumed(@NonNull VideoTileState videoTileState) {
                Log.d(TAG1, "onVideoTileResumed: ");
            }

            @Override
            public void onVideoTileSizeChanged(@NonNull VideoTileState videoTileState) {
                Log.d(TAG1, "onVideoTileSizeChanged: ");
            }
        };

        AudioVideoObserver audioVideoObserver = new AudioVideoObserver() {
            final String TAG1 = "audioVideoObserver";

            @Override
            public void onAudioSessionStartedConnecting(boolean b) {
                Log.d(TAG1, "onAudioSessionStartedConnecting: " + b);
            }

            @Override
            public void onAudioSessionStarted(boolean b) {
                Log.d(TAG1, "onAudioSessionStarted: " + b);
            }

            @Override
            public void onAudioSessionDropped() {
                Log.d(TAG1, "onAudioSessionDropped: ");
            }

            @Override
            public void onAudioSessionStopped(@NonNull MeetingSessionStatus meetingSessionStatus) {
                Log.d(TAG1, "onAudioSessionStopped: " + meetingSessionStatus.getStatusCode());
                if (meetingSessionStatus.getStatusCode() == MeetingSessionStatusCode.AudioAuthenticationRejected) {
                    showErrorPopUp("Audio Authentication Rejected : Please try again later");
                }
            }

            @Override
            public void onAudioSessionCancelledReconnect() {
                Log.d(TAG1, "onAudioSessionCancelledReconnect: ");
            }

            @Override
            public void onConnectionRecovered() {
                Log.d(TAG1, "onConnectionRecovered: ");
            }

            @Override
            public void onConnectionBecamePoor() {
                Log.d(TAG1, "onConnectionBecamePoor: ");
            }

            @Override
            public void onVideoSessionStartedConnecting() {
                Log.d(TAG1, "onVideoSessionStartedConnecting: ");
            }

            @Override
            public void onVideoSessionStarted(@NonNull MeetingSessionStatus meetingSessionStatus) {
                Log.d(TAG1, "onVideoSessionStarted: " + meetingSessionStatus.getStatusCode());
            }

            @Override
            public void onVideoSessionStopped(@NonNull MeetingSessionStatus meetingSessionStatus) {
                Log.d(TAG1, "onVideoSessionStopped: " + meetingSessionStatus.getStatusCode());
            }

            @Override
            public void onRemoteVideoSourceUnavailable(@NonNull List<RemoteVideoSource> list) {
                Log.d(TAG1, "onRemoteVideoSourceUnavailable: ");
            }

            @Override
            public void onRemoteVideoSourceAvailable(@NonNull List<RemoteVideoSource> list) {
                Log.d(TAG1, "onRemoteVideoSourceAvailable: ");
            }

            @Override
            public void onCameraSendAvailabilityUpdated(boolean b) {
                Log.d(TAG1, "onCameraSendAvailabilityUpdated: " + b);
            }
        };

        ContentShareObserver contentShareObserver = new ContentShareObserver() {
            final String TAG2 = "contentShareObserver";

            @Override
            public void onContentShareStarted() {
                Log.d(TAG2, "onContentShareStarted: ");
            }

            @Override
            public void onContentShareStopped(@NonNull ContentShareStatus contentShareStatus) {
                Log.d(TAG2, "onContentShareStopped: ");
            }
        };

        RealtimeObserver realtimeObserver = new RealtimeObserver() {
            final String TAG3 = "RealtimeObserver";

            @Override
            public void onVolumeChanged(@NonNull VolumeUpdate[] volumeUpdates) {
                Log.d(TAG3, "onVolumeChanged: ");
                if (volumeUpdates.length > 1) {
                    for (VolumeUpdate volumeUpdate : volumeUpdates) {
                        if (volumeUpdate != null) {
                            for (int i = 0; i < participantArrayList.size(); i++) {
                                if (participantArrayList.get(i).getAttendeeId().equals(volumeUpdate.getAttendeeInfo().getAttendeeId())) {
                                    participantArrayList.get(i).setUserSpeaking(!volumeUpdate.getVolumeLevel().name().equals("NotSpeaking") && !volumeUpdate.getVolumeLevel().name().equals("Muted"));
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < participantArrayList.size(); i++) {
                        if (participantArrayList.get(i).getAttendeeId().equals(volumeUpdates[0].getAttendeeInfo().getAttendeeId())) {
                            participantArrayList.get(i).setUserSpeaking(!volumeUpdates[0].getVolumeLevel().name().equals("NotSpeaking") && !volumeUpdates[0].getVolumeLevel().name().equals("Muted"));
                            break;
                        }
                    }
                }
                setUpParticipantAdapter();
            }

            @Override
            public void onSignalStrengthChanged(@NonNull SignalUpdate[] signalUpdates) {
                Log.d(TAG3, "onSignalStrengthChanged: ");
            }

            @Override
            public void onAttendeesJoined(@NonNull AttendeeInfo[] attendeeInfos) {
                Log.d(TAG3, "onAttendeesJoined: ");
                if (participantArrayList == null || participantArrayList.size() < 1 || attendeeInfos.length > 1) {
                    participantArrayList = new ArrayList<>();
                    for (AttendeeInfo attendeeInfo : attendeeInfos) {
                        if (attendeeInfo.getAttendeeId().contains("#content")) {
                            meetingScreenShareParticipant = new MeetingCreateParticipant();
                            meetingScreenShareParticipant.setExternalUserId(attendeeInfo.getExternalUserId());
                            meetingScreenShareParticipant.setAttendeeId(attendeeInfo.getAttendeeId());
                            meetingScreenShareParticipant.setScreenSharing(attendeeInfo.getAttendeeId().contains("#content"));
                            meetingScreenShareParticipant.setMuted(true);
                            meetingScreenShareParticipant.setVideo(false);
                            meetingScreenShareParticipant.setUserSpeaking(false);
                            setScreenShare(meetingScreenShareParticipant, "START");
                        } else {
                            MeetingCreateParticipant meetingCreateParticipant = new MeetingCreateParticipant();
                            meetingCreateParticipant.setExternalUserId(attendeeInfo.getExternalUserId());
                            meetingCreateParticipant.setScreenSharing(attendeeInfo.getAttendeeId().contains("#content"));
                            meetingCreateParticipant.setAttendeeId(attendeeInfo.getAttendeeId());
                            meetingCreateParticipant.setMuted(true);
                            meetingCreateParticipant.setVideo(false);
                            meetingCreateParticipant.setUserSpeaking(false);
                            participantArrayList.add(meetingCreateParticipant);
                        }
                    }
                } else {
                    if (attendeeInfos[0].getAttendeeId().contains("#content")) {
                        meetingScreenShareParticipant = new MeetingCreateParticipant();
                        meetingScreenShareParticipant.setExternalUserId(attendeeInfos[0].getExternalUserId());
                        meetingScreenShareParticipant.setAttendeeId(attendeeInfos[0].getAttendeeId());
                        meetingScreenShareParticipant.setScreenSharing(attendeeInfos[0].getAttendeeId().contains("#content"));
                        meetingScreenShareParticipant.setMuted(true);
                        meetingScreenShareParticipant.setVideo(false);
                        meetingScreenShareParticipant.setUserSpeaking(false);
                        setScreenShare(meetingScreenShareParticipant, "START");
                    } else {
                        MeetingCreateParticipant meetingCreateParticipant = new MeetingCreateParticipant();
                        meetingCreateParticipant.setExternalUserId(attendeeInfos[0].getExternalUserId());
                        meetingCreateParticipant.setAttendeeId(attendeeInfos[0].getAttendeeId());
                        meetingCreateParticipant.setScreenSharing(attendeeInfos[0].getAttendeeId().contains("#content"));
                        meetingCreateParticipant.setMuted(true);
                        meetingCreateParticipant.setVideo(false);
                        meetingCreateParticipant.setUserSpeaking(false);
                        participantArrayList.add(meetingCreateParticipant);
                    }
                }

                try {
                    if (!muteWhileJoiningUser) {
                        muteWhileJoiningUser = true;
                        muteAndUnMuteIcon = false;
                        micMuteUnMute();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
                setUpParticipantAdapter();
            }

            @Override
            public void onAttendeesLeft(@NonNull AttendeeInfo[] attendeeInfos) {
                Log.d(TAG3, "onAttendeesLeft: ");
                if (attendeeInfos[0].getAttendeeId().contains("#content")) {
                    meetingScreenShareParticipant = new MeetingCreateParticipant();
                    meetingScreenShareParticipant.setExternalUserId(attendeeInfos[0].getExternalUserId());
                    meetingScreenShareParticipant.setAttendeeId(attendeeInfos[0].getAttendeeId());
                    meetingScreenShareParticipant.setScreenSharing(attendeeInfos[0].getAttendeeId().contains("#content"));
                    meetingScreenShareParticipant.setMuted(true);
                    meetingScreenShareParticipant.setVideo(false);
                    meetingScreenShareParticipant.setUserSpeaking(false);
                    setScreenShare(meetingScreenShareParticipant, "STOP");
                } else {
                    if (participantArrayList != null && participantArrayList.size() > 0) {
                        for (int i = 0; i < participantArrayList.size(); i++) {
                            if (participantArrayList.get(i).getAttendeeId().equals(attendeeInfos[0].getAttendeeId())) {
                                participantArrayList.remove(i);
                                break;
                            }
                        }
                    }
                    setUpParticipantAdapter();
                }
            }

            @Override
            public void onAttendeesDropped(@NonNull AttendeeInfo[] attendeeInfos) {
                Log.d(TAG3, "onAttendeesDropped: ");
                if (participantArrayList != null && participantArrayList.size() > 0) {
                    for (int i = 0; i < participantArrayList.size(); i++) {
                        if (participantArrayList.get(i).getAttendeeId().equals(attendeeInfos[0].getAttendeeId())) {
                            participantArrayList.remove(i);
                            break;
                        }
                    }
                }
                setUpParticipantAdapter();
            }

            @Override
            public void onAttendeesMuted(@NonNull AttendeeInfo[] attendeeInfos) {
                Log.d(TAG3, "onAttendeesMuted: ");
                if (participantArrayList != null && participantArrayList.size() > 0) {
                    for (int i = 0; i < participantArrayList.size(); i++) {
                        if (participantArrayList.get(i).getAttendeeId().equals(attendeeInfos[0].getAttendeeId())) {
                            participantArrayList.get(i).setMuted(false);
                            participantArrayList.get(i).setUserSpeaking(false);
                            position = i;
                            break;
                        }
                    }
                }
                setUpParticipantAdapter();
            }

            @Override
            public void onAttendeesUnmuted(@NonNull AttendeeInfo[] attendeeInfos) {
                Log.d(TAG3, "onAttendeesUnMuted: ");
                if (participantArrayList != null && participantArrayList.size() > 0) {
                    for (int i = 0; i < participantArrayList.size(); i++) {
                        if (participantArrayList.get(i).getAttendeeId().equals(attendeeInfos[0].getAttendeeId())) {
                            participantArrayList.get(i).setMuted(true);
                            position = i;
                            break;
                        }
                    }
                }
                updateSingleArrayListItem();
            }
        };

        Logger awsLogger = new Logger() {
            @Override
            public void verbose(@NonNull String s, @NonNull String s1) {
                //Log.d(TAG, "verbose: "+s1);
            }

            @Override
            public void debug(@NonNull String s, @NonNull String s1) {
                Log.d(TAG, "debug: " + s1);
            }

            @Override
            public void info(@NonNull String s, @NonNull String s1) {
                Log.d(TAG, "info: " + s1);
            }

            @Override
            public void warn(@NonNull String s, @NonNull String s1) {
                Log.d(TAG, "warn: " + s1);
            }

            @Override
            public void error(@NonNull String s, @NonNull String s1) {
                Log.d(TAG, "error: " + s1);
            }

            @Override
            public void setLogLevel(@NonNull LogLevel logLevel) {
                Log.d(TAG, "setLogLevel: " + logLevel);
            }

            @NonNull
            @Override
            public LogLevel getLogLevel() {
                Log.d(TAG, "getLogLevel: ");
                return LogLevel.VERBOSE;
            }
        };

        MediaPlacement mediaPlacement = new MediaPlacement(audioFallbackURL, audioHostURL, signalingURL, turnControlURL, ingestionURL);
        Meeting meeting = new Meeting(externalMeetingId, mediaPlacement, mediaRegion, meetingId);
        CreateMeetingResponse createMeetingResponse = new CreateMeetingResponse(meeting);

        Attendee attendee = new Attendee(attendId, externalUserId, joinToken);
        CreateAttendeeResponse createAttendeeResponse = new CreateAttendeeResponse(attendee);
        eglCoreFactory = new DefaultEglCoreFactory();

        MeetingSessionConfiguration meetingSessionConfiguration = new MeetingSessionConfiguration(createMeetingResponse, createAttendeeResponse);
        meetingSession1 = new DefaultMeetingSession(meetingSessionConfiguration, awsLogger, MeetingActivity.this, eglCoreFactory);

        meetingSession1.getAudioVideo().addAudioVideoObserver(audioVideoObserver);
        meetingSession1.getAudioVideo().addContentShareObserver(contentShareObserver);
        meetingSession1.getAudioVideo().addRealtimeObserver(realtimeObserver);
        meetingSession1.getAudioVideo().addVideoTileObserver(videoTileObserver);
       /* meetingSession1.getAudioVideo().addDeviceChangeObserver(deviceChangeObserver);
        audioDevices = meetingSession1.getAudioVideo().listAudioDevices();*/

        handleSpeaker(false);

        //        meetingSession1.getAudioVideo().addActiveSpeakerObserver(new DefaultActiveSpeakerPolicy(), activeSpeakerObserver);
        joinLiveMeeting();

        call_end.setOnClickListener(v -> showCallEndConformationPopup(false));

        audio_mute_unMute.setOnClickListener(v -> {
            try {
                micMuteUnMute();
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        });

        video_enable_disable.setOnClickListener(v -> {
            if (!stopAndStartVideoIcon) {
                Log.d(TAG, "onClick: btnStartVideo");
                if (meetingSession1 != null) {
                    meetingSession1.getAudioVideo().startLocalVideo();
                    video_enable_disable.setBackground(getResources().getDrawable(R.drawable.camera));
                    stopAndStartVideoIcon = true;
                    camera_switch_live_class.setVisibility(View.VISIBLE);
                    if (meetingActivityViewModel != null) {
                        meetingActivityViewModel.initData(liveClassId, liveClassMeetingMapId, userId, "VIDEO", "ON", orgId);
                    }
                }
            } else {
                Log.d(TAG, "onClick: btnStopVideo");
                if (meetingSession1 != null) {
                    video_enable_disable.setBackground(getResources().getDrawable(R.drawable.stop_camera));
                    stopAndStartVideoIcon = false;
                    meetingSession1.getAudioVideo().stopLocalVideo();
                    camera_switch_live_class.setVisibility(View.GONE);
                    if (meetingActivityViewModel != null) {
                        meetingActivityViewModel.initData(liveClassId, liveClassMeetingMapId, userId, "VIDEO", "OFF", orgId);
                    }
                }
            }
        });

        camera_switch_live_class.setOnClickListener(v -> {
            try {
                handleCamera();
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        });

        speaker_icon_live_class.setOnClickListener(v -> {
            try {
                handleSpeaker(true);
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        });

        members_layout.setOnClickListener(v -> {
            try {
                if (participantArrayList != null && participantArrayList.size() > 0) {
                    if (!isDialogFragmentShown) {
                        LiveClassesMembersDialogFragment dialogFragment = new LiveClassesMembersDialogFragment(participantArrayList, activeUser, MeetingActivity.this, status -> isDialogFragmentShown = status);
                        dialogFragment.setStyle(BottomSheetDialogFragment.STYLE_NO_TITLE, R.style.LiveClassBottomSheetDialogTheme);
                        dialogFragment.show(getSupportFragmentManager(), "Live Classes Dialog");
                        isDialogFragmentShown = true;
                    }
                }
            } catch (Exception e) {
                isDialogFragmentShown = false;
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        });

        setting_live_class.setOnClickListener(v -> {
            try {
                Context wrapper = new ContextThemeWrapper(MeetingActivity.this, R.style.PopupMenu);
                PopupMenu popupMenu = new PopupMenu(wrapper, setting_live_class);
                popupMenu.getMenuInflater().inflate(R.menu.live_class_menu, popupMenu.getMenu());
                menu = popupMenu.getMenu();
                if (menu != null) {
                    if (screenSharingStatus) {
                        menu.getItem(0).setTitle("Stop screen sharing");
                    } else {
                        menu.getItem(0).setTitle("Screen Share");
                    }
                }
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    int itemId = menuItem.getItemId();
                    if (itemId == R.id.action_screen_share) {
                        if (screenSharingCount < 1 || screenSharingStatus) {
                            shareScreen(menu);
                        } else {
                            OustSdkTools.showToast(this.getResources().getString(R.string.screen_share_restriction));
                        }
                    } else if (itemId == R.id.action_about_info) {
                        WebinarAboutPopup();
                    }
                    return true;
                });
                popupMenu.show();
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        });
    }

    private void setScreenShare(MeetingCreateParticipant meetingScreenShareParticipant, String screenShare) {
        try {
            if (meetingScreenShareParticipant != null) {
                if (meetingScreenShareParticipant.getAttendeeId().contains("#content") && screenShare.equalsIgnoreCase("START")) {
                    meeting_share_screen_card_view.setVisibility(View.VISIBLE);
                    full_screen_share_meeting_card_view.setVisibility(View.GONE);
                    if (activeUser.getStudentid().equalsIgnoreCase(meetingScreenShareParticipant.getExternalUserId())) {
                        meeting_stop_screen_share.setVisibility(View.VISIBLE);
                        stop_screen_sharing_icon.setVisibility(View.VISIBLE);
                        meeting_presenter_name.setText(getResources().getString(R.string.your_presentation));
                    } else {
                        meeting_stop_screen_share.setVisibility(View.GONE);
                        stop_screen_sharing_icon.setVisibility(View.GONE);
                        meeting_presenter_name.setText(meetingScreenShareParticipant.getExternalUserId() + " " + getResources().getString(R.string.is_presenting));
                        if (meetingSession1 != null && meetingScreenShareParticipant.getVideoTileState() != null) {
                            meeting_share_screen_surface.setZOrderOnTop(false);
                            meeting_share_screen_surface.setMirror(false);
                            meetingSession1.getAudioVideo().bindVideoView(meeting_share_screen_surface, meetingScreenShareParticipant.getVideoTileState().getTileId());
                        }
                    }
                    screenSharingCount++;
                } else {
                    meeting_share_screen_card_view.setVisibility(View.GONE);
                    full_screen_share_meeting_card_view.setVisibility(View.GONE);
                    meeting_recycler_view.setVisibility(View.VISIBLE);
                    screenSharingCount = 0;
                    if (meetingSession1 != null && meetingScreenShareParticipant.getVideoTileState() != null) {
                        meetingSession1.getAudioVideo().unbindVideoView(meetingScreenShareParticipant.getVideoTileState().getTileId());
                    }
                    meeting_share_screen_surface.release();
                    full_screen_video_surface.release();
                }
            }

            screen_min_icon.setOnClickListener(view -> {
                meeting_share_screen_card_view.setVisibility(View.VISIBLE);
                full_screen_share_meeting_card_view.setVisibility(View.GONE);
                meeting_recycler_view.setVisibility(View.VISIBLE);
                if (meetingSession1 != null && meetingScreenShareParticipant != null && meetingScreenShareParticipant.getVideoTileState() != null) {
                    meeting_share_screen_surface.setZOrderOnTop(false);
                    meeting_share_screen_surface.setMirror(false);
                    meetingSession1.getAudioVideo().bindVideoView(meeting_share_screen_surface, meetingScreenShareParticipant.getVideoTileState().getTileId());
                }
            });

            screen_max_icon.setOnClickListener(view -> {
                meeting_recycler_view.setVisibility(View.GONE);
                meeting_share_screen_card_view.setVisibility(View.GONE);
                full_screen_share_meeting_card_view.setVisibility(View.VISIBLE);
                if (meetingScreenShareParticipant != null) {
                    if (meetingScreenShareParticipant.getAttendeeId().contains("#content")) {
                        if (activeUser.getStudentid().equalsIgnoreCase(meetingScreenShareParticipant.getExternalUserId())) {
                            meeting_stop_full_screen_share.setVisibility(View.VISIBLE);
                            stop_full_screen_sharing_icon.setVisibility(View.VISIBLE);
                            full_screen_meeting_presenter_name.setText(getResources().getString(R.string.your_presentation));
                        } else {
                            meeting_stop_full_screen_share.setVisibility(View.GONE);
                            stop_full_screen_sharing_icon.setVisibility(View.GONE);
                            full_screen_meeting_presenter_name.setText(meetingScreenShareParticipant.getExternalUserId() + " " + getResources().getString(R.string.is_presenting));
                        }
                        if (meetingSession1 != null && meetingScreenShareParticipant.getVideoTileState() != null) {
                            full_screen_video_surface.setZOrderOnTop(false);
                            full_screen_video_surface.setMirror(false);
                            meetingSession1.getAudioVideo().bindVideoView(full_screen_video_surface, meetingScreenShareParticipant.getVideoTileState().getTileId());
                        }
                    }
                }
            });

            stop_screen_sharing_icon.setOnClickListener(view -> {
                try {
                    shareScreen(menu);
                    meeting_recycler_view.setVisibility(View.VISIBLE);
                    meeting_share_screen_card_view.setVisibility(View.GONE);
                    full_screen_share_meeting_card_view.setVisibility(View.GONE);
                } catch (Exception e) {
                    meeting_recycler_view.setVisibility(View.VISIBLE);
                    meeting_share_screen_card_view.setVisibility(View.GONE);
                    full_screen_share_meeting_card_view.setVisibility(View.GONE);
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            });

            stop_full_screen_sharing_icon.setOnClickListener(view -> {
                try {
                    shareScreen(menu);
                    meeting_recycler_view.setVisibility(View.VISIBLE);
                    meeting_share_screen_card_view.setVisibility(View.GONE);
                    full_screen_share_meeting_card_view.setVisibility(View.GONE);
                } catch (Exception e) {
                    meeting_recycler_view.setVisibility(View.VISIBLE);
                    meeting_share_screen_card_view.setVisibility(View.GONE);
                    full_screen_share_meeting_card_view.setVisibility(View.GONE);
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showErrorPopUp(String errorMessage) {
        try {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MeetingActivity.this);
            alertBuilder.setCancelable(false);
            alertBuilder.setMessage(errorMessage);
            alertBuilder.setPositiveButton(android.R.string.yes, (dialog, which) -> showCallEndConformationPopup(true));
            AlertDialog alert = alertBuilder.create();
            alert.show();
        } catch (Exception e) {
            finish();
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void shareScreen(Menu menu) {
        try {
            String TAG1 = "btnStartShare";
            Log.d(TAG1, "onClick: btnStartShare");
            if (!shareAndUnShareIcon) {
                if (meetingSession1 != null) {
                    startActivityForResult(
                            mediaProjectionManager.createScreenCaptureIntent(),
                            SCREEN_CAPTURE_REQUEST_CODE
                    );
                    screenSharingStatus = true;
                    if (menu != null) {
                        menu.getItem(0).setTitle("Stop Sharing");
                    }
                    if (meetingActivityViewModel != null) {
                        meetingActivityViewModel.initData(liveClassId, liveClassMeetingMapId, userId, "SCREEN_SHARING", "ON", orgId);
                    }
                }
            } else {
                if (meetingSession1 != null) {
                    if (defaultScreenCaptureSource != null) {
                        defaultScreenCaptureSource.stop();
                    }

                    meetingSession1.getAudioVideo().stopContentShare();
                    shareAndUnShareIcon = false;
                    if (meetingActivityViewModel != null) {
                        meetingActivityViewModel.initData(liveClassId, liveClassMeetingMapId, userId, "SCREEN_SHARING", "OFF", orgId);
                    }
                    screenSharingStatus = false;
                    screenSharingCount = 0;
                    if (menu != null) {
                        menu.getItem(0).setTitle("Screen Share");
                    }
                    if (isServiceConnected && intent2 != null) {
                        stopService(intent2);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void WebinarAboutPopup() {
        try {
            Dialog aboutInfoPopup;
            aboutInfoPopup = new Dialog(this, R.style.DialogTheme);
            aboutInfoPopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
            aboutInfoPopup.setContentView(R.layout.about_webinar_info);
            Objects.requireNonNull(aboutInfoPopup.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

            TextView info_meeting_name = aboutInfoPopup.findViewById(R.id.info_meeting_name);
            TextView info_meeting_id = aboutInfoPopup.findViewById(R.id.info_meeting_id);
            TextView info_meeting_trainer_name = aboutInfoPopup.findViewById(R.id.info_meeting_trainer_name);
            FrameLayout info_dismiss = aboutInfoPopup.findViewById(R.id.info_dismiss);

            if (meetName != null) {
                info_meeting_name.setText(meetName);
            }
            if (joinRegisterMeetingResponse != null && joinRegisterMeetingResponse.getMeetingId() != null) {
                info_meeting_id.setText(joinRegisterMeetingResponse.getMeetingId());
            }

            if (meetTrainerName != null) {
                info_meeting_trainer_name.setText(meetTrainerName);
            }

            info_dismiss.setOnClickListener(v ->
                    aboutInfoPopup.dismiss());

            Drawable eventActionDrawable = getResources().getDrawable(R.drawable.course_button_bg);
            DrawableCompat.setTint(
                    DrawableCompat.wrap(eventActionDrawable),
                    Color.parseColor("#38404A")
            );
            info_dismiss.setBackground(eventActionDrawable);

            aboutInfoPopup.setCancelable(false);
            aboutInfoPopup.show();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showCallEndConformationPopup(boolean hideCancelBtn) {
        try {
            Dialog callEndDialog = new Dialog(MeetingActivity.this, R.style.DialogTheme);
            callEndDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            callEndDialog.setContentView(R.layout.call_end_popup);
            Objects.requireNonNull(callEndDialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            callEndDialog.setCancelable(false);
            callEndDialog.show();

            FrameLayout call_event_cancel = callEndDialog.findViewById(R.id.call_event_cancel);
            FrameLayout call_event_exit = callEndDialog.findViewById(R.id.call_event_exit);

            Drawable drawable = getResources().getDrawable(R.drawable.course_button_bg);
            call_event_exit.setBackground(OustSdkTools.drawableColor(drawable));

            if (hideCancelBtn) {
                call_event_cancel.setVisibility(View.GONE);
            }

            call_event_cancel.setOnClickListener(v -> callEndDialog.dismiss());

            call_event_exit.setOnClickListener(v -> {
                Log.d(TAG, "onClick: stopped");
                if (isServiceConnected && intent2 != null) {
                    stopService(intent2);
                }

                if (meetingSession1 != null) {
                    if (defaultScreenCaptureSource != null) {
                        defaultScreenCaptureSource.stop();
                    }
                    meetingSession1.getAudioVideo().stopContentShare();
                    meetingSession1.getAudioVideo().stopRemoteVideo();
                    meetingSession1.getAudioVideo().stop();
                }

                if (fullScreenHelper != null) {
                    fullScreenHelper.exitFullScreen();
                }
                try {
                    if (timer != null) {
                        timer.cancel();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
                callEndDialog.dismiss();
                if (meetingActivityViewModel != null) {
                    meetingActivityViewModel.initData(liveClassId, liveClassMeetingMapId, userId, "SESSION", "EXIT", orgId);
                }
                Intent intent = new Intent(MeetingActivity.this, CallEndActivity.class);
                intent.putExtra("joinRegisterMeetingResponse", responseString);
                intent.putExtra("meetName", meetName);
                intent.putExtra("meetStartTime", meetStartTime);
                intent.putExtra("meetEndTime", meetEndTime);
                intent.putExtra("meetClassId", tempValLiveClassId);
                intent.putExtra("meetLiveClassId", tempValLiveClassMeetingMapId);
                startActivity(intent);
                MeetingActivity.this.finish();
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void joinLiveMeeting() {
        try {
            Log.d(TAG, "joinLiveMeeting: ");
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.MODIFY_AUDIO_SETTINGS) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                AudioVideoConfiguration audioVideoConfiguration = new AudioVideoConfiguration(AudioMode.Mono48K, AudioStreamType.VoiceCall, AudioRecordingPresetOverride.Generic);
                meetingSession1.getAudioVideo().start(audioVideoConfiguration);
                meetingSession1.getAudioVideo().startRemoteVideo();
                if (meetingActivityViewModel != null) {
                    meetingActivityViewModel.initData(liveClassId, liveClassMeetingMapId, userId, "SESSION", "JOINED", orgId);
                }
            } else {
                ActivityCompat.requestPermissions(MeetingActivity.this, new
                        String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS}, 120);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 120) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.MODIFY_AUDIO_SETTINGS) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                setFinishOnTouchOutside(true);
                joinLiveMeeting();
            } else {
                openingSettingPage();
            }
        }
    }

    private void openingSettingPage() {
        try {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.MODIFY_AUDIO_SETTINGS)) {
                // Show a dialog that directs the user to the app settings where they can manually grant the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(MeetingActivity.this);
                builder.setMessage("Camera, Audio and Audio Settings permissions are required to use this feature. Please grant the permissions in the app settings.")
                        .setPositiveButton("Go to Settings", (dialog, which) -> {
                            // Open the app settings
                            openingFromBranchTools = true;
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> showCallEndConformationPopup(true))
                        .create()
                        .show();
            } else {
                // Request the camera, audio, and audio settings permissions
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS}, 120);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (openingFromBranchTools) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.MODIFY_AUDIO_SETTINGS) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    setFinishOnTouchOutside(true);
                    openingFromBranchTools = false;
                    joinLiveMeeting();
                } else {
                    openingSettingPage();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void handleCamera() {
        try {
            if (meetingSession1 != null) {
                meetingSession1.getAudioVideo().switchCamera();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void handleSpeaker(boolean onClick) {
        try {
            Log.d(TAG, "onClick: btnSpeaker");
            Drawable mDrawable;
            if (meetingSession1 != null) {
                if (audioManager != null) {
                    if (audioManager.isSpeakerphoneOn() && onClick) {
                        audioManager.setSpeakerphoneOn(false);
                        if (meetingActivityViewModel != null) {
                            meetingActivityViewModel.initData(liveClassId, liveClassMeetingMapId, userId, "SPEAKER", "OFF", orgId);
                        }
                        mDrawable = getResources().getDrawable(R.drawable.ic_audiooff);
                    } else {
                       /* for (MediaDevice mediaDevice : audioDevices) {
                            Log.e(TAG, "onCreate: audioDevices--> " + mediaDevice);
                            if (mediaDevice.getType().name().equalsIgnoreCase(MediaDeviceType.AUDIO_BUILTIN_SPEAKER.name())) {
                                meetingSession1.getAudioVideo().chooseAudioDevice(mediaDevice);
                                break;
                            }
                        }*/
                        audioManager.setSpeakerphoneOn(true);
                        if (meetingActivityViewModel != null) {
                            meetingActivityViewModel.initData(liveClassId, liveClassMeetingMapId, userId, "SPEAKER", "ON", orgId);
                        }
                        mDrawable = getResources().getDrawable(R.drawable.ic_audio_on);
                    }
                    mDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                    speaker_icon_live_class.setBackground(mDrawable);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setUpParticipantAdapter() {
        try {
            Log.d(TAG, "setUpParticipantAdapter: ");
            if (participantArrayList != null && participantArrayList.size() > 0) {
                String size = String.valueOf(participantArrayList.size());
                members_count_txt.setText(size);
            } else {
                members_count_txt.setText("00");
            }
            if (participantArrayList != null && participantArrayList.size() > 0) {
                if (meetingAdapter != null) {
                    meetingAdapter.notifyListAdapter(participantArrayList);
                } else {
                    meetingAdapter = new MeetingAdapter(participantArrayList, meetingSession1, activeUser, MeetingActivity.this);
                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(MeetingActivity.this, 2);
                    meeting_recycler_view.setLayoutManager(mLayoutManager);
                    meeting_recycler_view.setAdapter(meetingAdapter);
                }
            }
            try {
                startTimer();
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void updateSingleArrayListItem() {
        Log.d(TAG, "updateSingleArrayListItem: position:" + position);
        if (meetingAdapter != null) {
            meetingAdapter.notifyItemChanged(position);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (SCREEN_CAPTURE_REQUEST_CODE == requestCode && resultCode == Activity.RESULT_OK && data != null) {
            SurfaceTextureCaptureSourceFactory surfaceTextureCaptureSourceFactory = new DefaultSurfaceTextureCaptureSourceFactory(meetingSession1.getLogger(), eglCoreFactory);
            DefaultScreenCaptureSource defaultScreenCaptureSource = new DefaultScreenCaptureSource(this,
                    meetingSession1.getLogger(),
                    surfaceTextureCaptureSourceFactory,
                    resultCode,
                    data,
                    displayManager,
                    mediaProjectionManager
            );

            CaptureSourceObserver captureSourceObserver = new CaptureSourceObserver() {
                @Override
                public void onCaptureStarted() {
                    Log.d(TAG, "onCaptureStarted: ");
                    ContentShareSource contentShareSource = new ContentShareSource();
                    contentShareSource.setVideoSource(defaultScreenCaptureSource);
                    meetingSession1.getAudioVideo().startContentShare(contentShareSource);
                    shareAndUnShareIcon = true;
                }

                @Override
                public void onCaptureStopped() {
                    Log.d(TAG, "onCaptureStopped: ");
                    shareAndUnShareIcon = false;
                }

                @Override
                public void onCaptureFailed(@NonNull CaptureSourceError captureSourceError) {
                    Log.d(TAG, "onCaptureFailed: ");
                    meetingSession1.getAudioVideo().stopContentShare();
                    shareAndUnShareIcon = false;
                }
            };

            serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    isServiceConnected = true;
                    defaultScreenCaptureSource.addCaptureSourceObserver(captureSourceObserver);
                    defaultScreenCaptureSource.start();
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    isServiceConnected = false;
                    Log.d(TAG, "onServiceDisconnected: ");
                }
            };

            // (Android Q and above) Start the service created in step 1
            this.bindService(intent2, serviceConnection, Context.BIND_AUTO_CREATE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.startForegroundService(intent2);
            } else {
                startService(new Intent(this, ScreenCaptureService.class));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        return;
    }

    private void micMuteUnMute() {
        try {
            if (!muteAndUnMuteIcon) {
                if (meetingSession1 != null) {
                    audio_mute_unMute.setBackground(getResources().getDrawable(R.drawable.microphone_off));
                    meetingSession1.getAudioVideo().realtimeLocalMute();
                    if (meetingActivityViewModel != null) {
                        meetingActivityViewModel.initData(liveClassId, liveClassMeetingMapId, userId, "MICROPHONE", "OFF", orgId);
                    }
                    muteAndUnMuteIcon = true;
                }
            } else {
                if (meetingSession1 != null) {
                    audio_mute_unMute.setBackground(getResources().getDrawable(R.drawable.microphone_on));
                    meetingSession1.getAudioVideo().realtimeLocalUnmute();
                    if (meetingActivityViewModel != null) {
                        meetingActivityViewModel.initData(liveClassId, liveClassMeetingMapId, userId, "MICROPHONE", "ON", orgId);
                    }
                    muteAndUnMuteIcon = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void startTimer() {
        try {
            Log.e(TAG, "CounterClass -- startTimer: " + participantArrayList.size());
            if (participantArrayList != null && participantArrayList.size() == 1) {
                if (timer == null) {
                    timer = new CounterClass(120000, 1000);
                    timer.start();
                }
            } else {
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private class CounterClass extends CountDownTimer {
        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            try {
                Log.e(TAG, "CounterClass -- onFinish: -- ");
                micMuteUnMute();
                timer = null;
                startTimer();
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        }
    }
}
