package com.oustme.oustsdk.activity.common;

import static com.oustme.oustsdk.tools.OustSdkTools.removeAllReminderNotification;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_APP_TUTORIAL_SHOWN;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.ORG_ID_USER_ID_APP_TUTORIAL_VIEWED;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.common.CplCollectionData;
import com.oustme.oustsdk.request.SignOutRequest;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.CplDataHandler;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustDataHandler;
import com.oustme.oustsdk.tools.OustLogDetailHandler;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.util.ArrayList;

import io.branch.referral.Branch;

public class CplIntroActivity extends AppCompatActivity {

    private static final String TAG = "CplIntroActivity";

    private CplCollectionData cplCollectionData;
    private TextView enter_button;
    private TextView logout_txt;
    private ImageView intro_bg_img, cpl_intro_icon;
    private boolean isLoadDataAgain = false;
    private ActiveUser activeUser;
    public static Activity activity;
    AlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OustSdkTools.setLocale(CplIntroActivity.this);
        setContentView(R.layout.dialog_cpl_intro);
        activity = CplIntroActivity.this;

        if (OustSdkApplication.getContext() == null) {
            OustSdkApplication.setmContext(getApplicationContext());
        }
        OustStaticVariableHandling.getInstance().setCheckCPL(true);
        cplCollectionData = CplDataHandler.getInstance().getCplCollectionData();
        Log.d(TAG, "onCreate: NewCPLDistributed:" + OustStaticVariableHandling.getInstance().isNewCplDistributed());
        initViews();
        if (!OustPreferences.getAppInstallVariable("NotificationPopupShown")) {
            checkIfNotificationEnabled();
        }

        enter_button.setOnClickListener(v -> {
            isLoadDataAgain = true;
            if (cplCollectionData != null && cplCollectionData.getCplId() != null && !cplCollectionData.getCplId().equalsIgnoreCase("")) {
                Intent intent = new Intent(CplIntroActivity.this, CplBaseActivity.class);
                intent.putExtra("cplId", cplCollectionData.getCplId());
                startActivity(intent);
            } else {
                OustStaticVariableHandling.getInstance().setNewCplDistributed(false);
                OustStaticVariableHandling.getInstance().setCheckCPL(false);
                finish();
            }
        });
    }

    private void initViews() {
        try {
            Log.d(TAG, "initViews: ");
            TextView cpl_header = findViewById(R.id.cpl_header);
            enter_button = findViewById(R.id.enter_button);
            LinearLayout closeBtn = findViewById(R.id.closeBtn);
            LinearLayout logoutButton = findViewById(R.id.logoutButton);
            cpl_intro_icon = findViewById(R.id.cpl_intro_icon);
            intro_bg_img = findViewById(R.id.intro_bg_img);
            LinearLayout enter_button_ll = findViewById(R.id.enter_button_ll);
            logout_txt = findViewById(R.id.logout_txt);

            setBgImages();

            String toolbarColorCode = OustPreferences.get("toolbarColorCode");
            int color = StringUtils.isBlank(toolbarColorCode) ? OustSdkTools.getColorBack(R.color.lgreen) : Color.parseColor(toolbarColorCode);

            activeUser = OustAppState.getInstance().getActiveUser();
            if (OustPreferences.get("userdata") != null && !OustPreferences.get("userdata").isEmpty()) {
                activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
            }

            logoutButton.setBackgroundColor(color);
            closeBtn.setBackgroundColor(color);
            enter_button_ll.setBackgroundColor(color);
            logout_txt.setText("" + getResources().getString(R.string.logout));

            if (cplCollectionData.getProgress() > 0) {
                enter_button.setText("" + getResources().getString(R.string.resume));
            }
            if (cplCollectionData.getProgress() >= 99) {
                enter_button.setText("" + getResources().getString(R.string.review_text));
            }
            cpl_header.setText(cplCollectionData.getCplName());

            if (OustPreferences.getAppInstallVariable("hideCplCloseBtn")) {
                closeBtn.setVisibility(View.GONE);
            } else {
                closeBtn.setVisibility(View.VISIBLE);
            }

            if (OustPreferences.getAppInstallVariable("disableCplLogout")) {
                logoutButton.setVisibility(View.GONE);
            } else {
                logoutButton.setVisibility(View.VISIBLE);
            }

            if (OustPreferences.getAppInstallVariable("disableCplTitle")) {
                cpl_header.setVisibility(View.GONE);
            } else {
                cpl_header.setVisibility(View.VISIBLE);
            }

            if (OustPreferences.getAppInstallVariable("disableCplIntroIcon")) {
                cpl_intro_icon.setVisibility(View.GONE);
            } else {
                cpl_intro_icon.setVisibility(View.VISIBLE);
            }

            logoutButton.setOnClickListener(v -> onLogOut());

            closeBtn.setOnClickListener(v -> {
                OustStaticVariableHandling.getInstance().setNewCplDistributed(false);
                OustStaticVariableHandling.getInstance().setCheckCPL(false);
                finish();
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setBgImages() {
        Log.d(TAG, "setBgImages: ");
        try {
            if (cplCollectionData != null) {
                if (cplCollectionData.getIntroBgImg() != null && !cplCollectionData.getIntroBgImg().isEmpty()) {
                    Picasso.get().load(cplCollectionData.getIntroBgImg()).into(intro_bg_img);
                }
                if (cplCollectionData.getIntroIcon() != null && !cplCollectionData.getIntroIcon().isEmpty()) {
                    Picasso.get().load(cplCollectionData.getIntroIcon()).into(cpl_intro_icon);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void onLogOut() {
        try {
            OustStaticVariableHandling.getInstance().setCheckCPL(false);

            Log.d(TAG, "onLogOut: ");
            String signOutUrl = OustSdkApplication.getContext().getResources().getString(R.string.signout);
            signOutUrl = HttpManager.getAbsoluteUrl(signOutUrl);
            String userName = activeUser.getStudentid();
            String institutional_id = OustPreferences.get("tanentid");

            SignOutRequest signOutRequest = new SignOutRequest();
            if ((OustPreferences.get("gcmToken") != null)) {
                signOutRequest.setDeviceToken(OustPreferences.get("gcmToken"));
            }
            signOutRequest.setDeviceIdentity("android");
            signOutRequest.setStudentid(userName);
            signOutRequest.setInstitutionLoginId(institutional_id);
            signOutRequest.setDevicePlatformName("android");
            String authToken = OustPreferences.get("authToken");
            if (!TextUtils.isEmpty(authToken)) {
                signOutRequest.setAuthToken(authToken);

                final Gson gson = new Gson();
                String jsonParams = gson.toJson(signOutRequest);

                ApiCallUtils.doNetworkCall(Request.Method.PUT, signOutUrl, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response != null && response.optBoolean("success"))
                            localLogout();
                        ApiCallUtils.setIsLoggedOut(true);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "unable to logout");
                        if (error.networkResponse != null && error.networkResponse.statusCode == 401)
                            localLogout();
                        else
                            Toast.makeText(CplIntroActivity.this, "Something went wrong, unable to logout", Toast.LENGTH_SHORT).show();
                        OustSdkTools.hideProgressbar();
                    }
                });
            } else {
                localLogout();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void localLogout() {
        try {
            if (!OustLogDetailHandler.getInstance().isUserForcedOut()) {
                boolean isAppTutorialShow = OustPreferences.getAppInstallVariable(IS_APP_TUTORIAL_SHOWN);
                String tenantName = OustPreferences.get("tanentid").replaceAll(" ", "");
                removeAllReminderNotification();
                OustDataHandler.getInstance().resetData();
                OustStaticVariableHandling.getInstance().setEnterpriseUser(false);
                OustAppState.getInstance().setLandingPageLive(false);
                OustStaticVariableHandling.getInstance().setAppActive(false);
                OustStaticVariableHandling.getInstance().setNewCplDistributed(false);

                OustStaticVariableHandling.getInstance().setFeeds(new ArrayList<>());

                OustSdkTools.showProgressBar();
                OustSdkTools.clearAlldataAndlogout();
                OustPreferences.saveAppInstallVariable("LOGOUT", true);
                if (isAppTutorialShow) {
                    if (activeUser != null) {
                        String tempValue = tenantName + "_" + activeUser.getStudentKey() + "_" + isAppTutorialShow;
                        OustPreferences.save(ORG_ID_USER_ID_APP_TUTORIAL_VIEWED, tempValue);
                    }
                }
                try {

                    Intent logOutIntent = new Intent().setComponent(new ComponentName("com.oustme.oustapp",
                            "com.oustme.oustapp.newLayout.view.activity.NewLoginScreenActivity"));
                    logOutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (getPackageManager().resolveActivity(logOutIntent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                        startActivity(logOutIntent);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Failed to launch AutoStart Screen ", e);
                }

                Branch.getInstance().logout();
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    @Override
    public void onBackPressed() {
        OustStaticVariableHandling.getInstance().setNewCplDistributed(false);
        Log.e("back action", "back button pressed");
    }

    @Override
    protected void onResume() {
        super.onResume();
        logout_txt.setText("" + getResources().getString(R.string.logout));
        if (isLoadDataAgain) {
            isLoadDataAgain = false;
            try {
                cplCollectionData = CplDataHandler.getInstance().getCplCollectionData();
                Log.d(TAG, "onResume: loaded data again");

                if (cplCollectionData.getProgress() > 0) {
                    enter_button.setText("" + getResources().getString(R.string.resume));
                }
                if (cplCollectionData.getProgress() >= 99) {
                    enter_button.setText("" + getResources().getString(R.string.review_text));
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }

    private void checkIfNotificationEnabled() {
        Log.e(TAG, "inside checkIfNotificationEnabled() method");
        if (NotificationManagerCompat.from(OustSdkApplication.getContext()).areNotificationsEnabled()) {

            Log.e("Notification", "notification is enabled");

            if (!OustPreferences.getAppInstallVariable("auto_start_notif")) {
                askXiomiAutoStart(android.os.Build.MANUFACTURER);
            }

        } else {
            askToTurnOnNotificarion();
        }
    }

    public void askToTurnOnNotificarion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please turn on Notifications in order to get alerted about contests, prizes, new learning modules and more.")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    try {
                        Intent intent = new Intent();
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                            intent.putExtra("android.provider.extra.APP_PACKAGE", OustSdkApplication.getContext().getPackageName());
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                            intent.putExtra("app_package", OustSdkApplication.getContext().getPackageName());
                            intent.putExtra("app_uid", OustSdkApplication.getContext().getApplicationInfo().uid);
                        } else {
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.setData(Uri.parse("package:" + OustSdkApplication.getContext().getPackageName()));
                        }

                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    dialog.cancel();
                })
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());

        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.setTitle("Oust Notification");
        alert.show();
    }

    public void askXiomiAutoStart(String deviceManufacturer) {
        if (deviceManufacturer.equalsIgnoreCase("Xiaomi")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("To receive Oust Notifications , we need you to set Oust to Auto Start")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        try {
                            OustPreferences.saveAppInstallVariable("auto_start_notif", true);
                            for (Intent intent : AUTO_START_INTENTS) {
                                if (getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                                    startActivity(intent);
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                        dialog.cancel();
                    })
                    .setNegativeButton("No", (dialog, id) -> dialog.cancel());

            //Creating dialog box
            alert = builder.create();
            alert.setTitle("Oust Notification");
            alert.show();
        }
    }

    private static final Intent[] AUTO_START_INTENTS = {
            new Intent().setComponent(new ComponentName("com.samsung.android.lool",
                    "com.samsung.android.sm.ui.battery.BatteryActivity")),
            new Intent("miui.intent.action.OP_AUTO_START").addCategory(Intent.CATEGORY_DEFAULT),
            new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
            new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
            new Intent().setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
            new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.entry.FunctionActivity")).setData(
                    Uri.parse("mobilemanager://function/entry/AutoStart"))
    };

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (alert != null && alert.isShowing()) {
                alert.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
