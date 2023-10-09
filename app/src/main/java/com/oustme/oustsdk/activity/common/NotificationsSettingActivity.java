package com.oustme.oustsdk.activity.common;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.customviews.CircleImageView;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustGATools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;


/**
 * Created by shilpysamaddar on 22/03/17.
 */

public class NotificationsSettingActivity extends AppCompatActivity implements View.OnClickListener {

    TextView txtTitle, userName;
    CircleImageView imgAvatarButton;
    SwitchCompat soundSwitch, tts_switch, all_switch, daily_switch, email_switch;
    ImageButton backArrowImgBtn;
    RelativeLayout tts_switchlayout;
    LinearLayout backarrow_back;

    private ActiveUser activeUser;

    String googleTtsPackage = "com.google.android.tts", picoPackage = "com.svox.pico";
    private final String soundStr = "issounddisable";
    private final String ttsfileInstalled = "isttsfileinstalled";
    private final String allNotificationDisabled = "allNotificationDisabled";
    private final String dailyNotificationDisabled = "dailyNotificationDisabled";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        try {
            OustSdkTools.setLocale(NotificationsSettingActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        setContentView(R.layout.user_notifications);
        initViews();
        initUserGeneral();
//        OustGATools.getInstance().reportPageViewToGoogle(NotificationsSettingActivity.this, "Notifications Setting Page");

    }

    private void initViews() {
        txtTitle = findViewById(R.id.txtTitle);
        imgAvatarButton = findViewById(R.id.imgAvatarButton);
        userName = findViewById(R.id.username_Txt);
        soundSwitch = findViewById(R.id.sound_switch);
        tts_switchlayout = findViewById(R.id.tts_switchlayout);
        tts_switch = findViewById(R.id.tts_switch);
        daily_switch = findViewById(R.id.daily_switch);
        email_switch = findViewById(R.id.email_switch);
        backArrowImgBtn = findViewById(R.id.backArrowImgBtn);
        backarrow_back = findViewById(R.id.backarrow_back);
        all_switch = findViewById(R.id.all_switch);
        backArrowImgBtn.setOnClickListener(this);
        backarrow_back.setOnClickListener(this);
    }

    public void initUserGeneral() {
        OustSdkTools.speakInit();
        activeUser = OustAppState.getInstance().getActiveUser();
        if ((activeUser != null) && (activeUser.getStudentid() != null)) {
        } else {
            String activeUserGet = OustPreferences.get("userdata");
            activeUser = OustSdkTools.getActiveUserData(activeUserGet);
        }
        setUserData();
        setFont();
    }

    private void setUserData() {
        try {
            savedSetting();
            String avatar = activeUser.getAvatar();
            if (OustSdkTools.tempProfile != null) {
                imgAvatarButton.setImageBitmap(OustSdkTools.tempProfile);
            } else {
                if ((avatar != null) && (!avatar.isEmpty())) {
                    if (OustSdkTools.checkInternetStatus()) {
                        Picasso.get().load(avatar).into(imgAvatarButton);
                    } else {
                        Picasso.get().load(avatar).networkPolicy(NetworkPolicy.OFFLINE).into(imgAvatarButton);
                    }
                }
            }
            if (activeUser.getUserDisplayName() != null) {
                userName.setText(activeUser.getUserDisplayName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setFont() {
        try {
            soundSwitch.setTypeface(OustSdkTools.getAvenirLTStdMedium());
            tts_switch.setTypeface(OustSdkTools.getAvenirLTStdMedium());
            all_switch.setTypeface(OustSdkTools.getAvenirLTStdMedium());
            daily_switch.setTypeface(OustSdkTools.getAvenirLTStdMedium());
            email_switch.setTypeface(OustSdkTools.getAvenirLTStdMedium());

            txtTitle.setText(getResources().getString(R.string.notifications));
            soundSwitch.setText(getResources().getString(R.string.sound));
            daily_switch.setText(getResources().getString(R.string.daily_notifications));
            tts_switch.setText(getResources().getString(R.string.text_to_speech));
            all_switch.setText(getResources().getString(R.string.all_notification));
            email_switch.setText(getResources().getString(R.string.email_summary));

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void savedSetting() {
        try {
            soundSwitch.setChecked(OustAppState.getInstance().isSoundDisabled());
            if (!OustPreferences.getAppInstallVariable(ttsfileInstalled)) {
                tts_switchlayout.setVisibility(View.VISIBLE);
                tts_switch.setChecked(OustPreferences.getAppInstallVariable("isttsfileinstalled"));
                tts_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (isChecked) {
                            checkTTS();
                        }
                    }
                });
            } else {
                tts_switchlayout.setVisibility(View.GONE);
            }
            all_switch.setChecked(!OustPreferences.getAppInstallVariable(allNotificationDisabled));
            daily_switch.setChecked(!OustPreferences.getAppInstallVariable(dailyNotificationDisabled));

            String node = "/users/" + activeUser.getStudentKey() + "/enableWeeklyReportByUserInApp";
            ValueEventListener enableWeeklyReportListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {


                        try {
                            final boolean enableWeeklyReport = (boolean) dataSnapshot.getValue();
                            email_switch.setChecked(enableWeeklyReport);
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            OustFirebaseTools.getRootRef().child(node).addValueEventListener(enableWeeklyReportListener);
            OustFirebaseTools.getRootRef().child(node).keepSynced(true);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void checkTTS() {
        try {
            if (!OustSdkTools.isPackageInstalled(getPackageManager(), googleTtsPackage)) {
                confirmDialogtts();
            }
        } catch (Exception e) {
            e.getMessage();
        }

    }


    private void confirmDialogtts() {
        AlertDialog.Builder d = new AlertDialog.Builder(NotificationsSettingActivity.this);
        d.setTitle(getResources().getString(R.string.installed_speech_engine));
        d.setMessage(getResources().getString(R.string.install_speech_engine_confirmation));
        d.setPositiveButton(getResources().getString(R.string.yes), new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + googleTtsPackage)));
                NotificationsSettingActivity.this.finish();
            }
        });
        d.setNegativeButton(getResources().getString(R.string.no), new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                tts_switch.setChecked(false);
            }
        });
        d.show();
    }

    public void onClick(final View v) {
        int id = v.getId();
        if (id == R.id.backArrowImgBtn) {
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(v, "scaleX", 1.0f, 0.94f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(v, "scaleY", 1.0f, 0.96f);
            scaleDownX.setDuration(150);
            scaleDownY.setDuration(150);
            scaleDownX.setRepeatCount(1);
            scaleDownY.setRepeatCount(1);
            scaleDownX.setRepeatMode(ValueAnimator.REVERSE);
            scaleDownY.setRepeatMode(ValueAnimator.REVERSE);
            scaleDownX.setInterpolator(new DecelerateInterpolator());
            scaleDownY.setInterpolator(new DecelerateInterpolator());
            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleDownX).with(scaleDownY);
            scaleDown.start();
            scaleDown.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    onBackPressed();
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
        } else if (id == R.id.backarrow_back) {
            onBackPressed();
        }
    }


    @Override
    public void onBackPressed() {
        //if()
        saveAllSetting();
        super.onBackPressed();
    }

    private void saveAllSetting() {
        try {
            if (soundSwitch.isChecked()) {
                OustAppState.getInstance().setIsSoundDisabled(false);
                OustPreferences.saveAppInstallVariable(soundStr, false);
            } else {
                OustAppState.getInstance().setIsSoundDisabled(true);
                OustPreferences.saveAppInstallVariable(soundStr, true);
            }
            if (tts_switch.isChecked()) {
                OustPreferences.saveAppInstallVariable(ttsfileInstalled, true);
            } else {
                OustPreferences.saveAppInstallVariable(ttsfileInstalled, false);
            }
            if (daily_switch.isChecked()) {
                OustPreferences.saveAppInstallVariable(dailyNotificationDisabled, false);
            } else {
                OustPreferences.saveAppInstallVariable(dailyNotificationDisabled, true);
            }
            if (all_switch.isChecked()) {
                OustPreferences.saveAppInstallVariable(allNotificationDisabled, false);
            } else {
                OustPreferences.saveAppInstallVariable(allNotificationDisabled, true);
            }
            if (email_switch.isChecked()) {
                emailNotificationEnabled(true);
            } else {
                emailNotificationEnabled(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void emailNotificationEnabled(boolean isEnabled) {

        if (activeUser != null) {

            String update_weekly_report = OustSdkApplication.getContext().getResources().getString(R.string.weekly_email_report);
            update_weekly_report = update_weekly_report + isEnabled + "/" + activeUser.getStudentid();
            String jsonParams = "";
            try {
                update_weekly_report = HttpManager.getAbsoluteUrl(update_weekly_report);

                ApiCallUtils.doNetworkCall(Request.Method.PUT, update_weekly_report, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

        }

    }

}
