package com.oustme.oustsdk.tools;

import static android.content.Context.ACTIVITY_SERVICE;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Base64;
import android.util.Log;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.DecelerateInterpolator;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.PopupActivity;
import com.oustme.oustsdk.compression.video.Config;
import com.oustme.oustsdk.customviews.CircleImageView;
import com.oustme.oustsdk.feed_ui.ui.RedirectWebView;
import com.oustme.oustsdk.firebase.FFContest.FastestFingerContestData;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.common.UserBalanceState;
import com.oustme.oustsdk.interfaces.course.DialogKeyListener;
import com.oustme.oustsdk.pojos.BranchIOEncryptedResponce;
import com.oustme.oustsdk.reminderNotification.ReminderNotificationManager;
import com.oustme.oustsdk.request.ClickedFeedData;
import com.oustme.oustsdk.request.DeviceInfoData;
import com.oustme.oustsdk.request.PingApiRequest;
import com.oustme.oustsdk.request.SubmitRequest;
import com.oustme.oustsdk.request.ViewedFeedData;
import com.oustme.oustsdk.response.assessment.AssessmentPlayResponse;
import com.oustme.oustsdk.response.common.BranchIoResponce;
import com.oustme.oustsdk.response.common.EncrypQuestions;
import com.oustme.oustsdk.response.common.PlayResponse;
import com.oustme.oustsdk.response.common.SignInResponse;
import com.oustme.oustsdk.response.common.VerifyAndSignInResponse;
import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.room.dto.DTOQuestionOption;
import com.oustme.oustsdk.room.dto.DTOQuestionOptionCategory;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.service.SendApiServices;
import com.oustme.oustsdk.sqlite.DatabaseHandler;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.tools.htmlrender.NumberSpan;
import com.oustme.oustsdk.tools.htmlrender.OustBulletSpan;
import com.oustme.oustsdk.tools.htmlrender.PaymentGetRSAMobileResponseData;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import io.sentry.Sentry;
import pl.droidsonroids.gif.GifImageView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by shilpysamaddar on 07/03/17.
 */

public class OustSdkTools {

    private static final String TAG = "OustAndroid:" + OustSdkTools.class.getName();
    private static final String AES_KEY = "AES";

    private static final String AES_ENCRYPTION_KEY = "OUSTMESECUREDKEY";

    private static final String UNICODE_FORMAT = "UTF8";
    public static boolean isReadMoreFragmentVisible = false;

    private static final Object monitor = new Object();
    private static OustSdkTools oustTools = null;

    private static Snackbar snackbar;
    private static LinearLayout layout;
    private static Activity activity;
    private static ProgressBar progressBar;
    public static Bitmap tempProfile;
    public static boolean isAssessmentQuestion = false;

    public static int totalAttempt = 0, optionSelected = 0, totalCategoryRight = 0;
    public static List<DTOQuestionOptionCategory> categoryData;
    public static List<DTOQuestionOption> optionData;

    private static Gson gson = new GsonBuilder().create();

    private static Typeface typefaceMedium, typefaceHeavy, typefacePro, typefaceLight, typefaceLithoPro;
    public static DatabaseHandler databaseHandler = new DatabaseHandler();
    private static Toast toast = null;

    public static OustSdkTools getInstance() {
        if (oustTools == null) {
            synchronized (monitor) {
                if (oustTools == null)
                    oustTools = new OustSdkTools();
            }
        }
        return oustTools;
    }

    public static Typeface getAvenirLTStdMedium() {
        if (typefaceMedium == null) {
            try {
                try {
                    if (OustSdkApplication.getContext() != null) {
                        File file = new File(OustSdkApplication.getContext().getFilesDir(), "fonta_light.otf");
//                    FileOutputStream output = new FileOutputStream(file);
//                    EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
//                    String fontData = enternalPrivateStorage.readSavedData("fonta_light.otf");
//                    byte[] binary = Base64.decode(fontData, 0);
//                    output.write(binary);
//                    String path = file.getPath();
                        if (file != null && file.exists()) {
                            typefaceMedium = Typeface.createFromFile(file);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    sendSentryException(e);
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendSentryException(e);
            }
        }
        return typefaceMedium;
    }

    public static Typeface getAvenirLTStdHeavy() {
        if (typefaceHeavy == null) {
            try {
                if (OustSdkApplication.getContext() != null) {
                    File file = new File(OustSdkApplication.getContext().getFilesDir(), "fonta_heavy.otf");
                    if (file != null && file.exists()) {
                        typefaceHeavy = Typeface.createFromFile(file);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendSentryException(e);
            }
        }
        return typefaceHeavy;
    }

    public static Typeface getTypefacePro() {
        if (typefacePro == null) {
            try {
                File file = new File(OustSdkApplication.getContext().getFilesDir(), "fontb_heavy.ttf");
                if (file.exists())
                    typefacePro = Typeface.createFromFile(file);
            } catch (Exception e) {
                e.printStackTrace();
                sendSentryException(e);
            }
        }
        return typefacePro;
    }

    public static Typeface getTypefaceLight() {
        if (typefaceLight == null) {
            try {
                File file = new File(OustSdkApplication.getContext().getFilesDir(), "fontb_light.ttf");
                if (file.exists())
                    typefaceLight = Typeface.createFromFile(file);
            } catch (Exception e) {
                e.printStackTrace();
                sendSentryException(e);
            }
        }
        return typefaceLight;
    }

    public static Typeface getTypefaceLithoPro() {
        if (typefaceLithoPro == null) {
            try {
                File file = new File(OustSdkApplication.getContext().getFilesDir(), "fontc.otf");
                if (file.exists())
                    typefaceLithoPro = Typeface.createFromFile(file);
            } catch (Exception e) {
                e.printStackTrace();
                sendSentryException(e);
            }
        }
        return typefaceLithoPro;
    }


    public static boolean checkInternetStatus() {
        String status = NetworkUtil.getConnectivityStatusString(activity);
        boolean isNetworkAvailable = false;
        try {
            switch (status) {
                case "Connected to Internet with Mobile Data":
                    isNetworkAvailable = true;
                    break;
                case "Connected to Internet with WIFI":
                    isNetworkAvailable = true;
                    break;
                default:
                    isNetworkAvailable = false;
                    break;
            }
            //showToast(""+R.string.retry_internet_msg);
            //showSnackBarBasedonStatus(isNetworkAvailable);
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
        return isNetworkAvailable;
    }

    public static boolean checkInternetStatus(Context context) {
        String status = NetworkUtil.getConnectivityStatusString(activity);
        boolean isNetworkAvailable = false;
        try {
            switch (status) {
                case "Connected to Internet with Mobile Data":
                case "Connected to Internet with WIFI":
                    isNetworkAvailable = true;
                    break;
                default:
                    break;
            }
            if (!isNetworkAvailable) {
                // showToast("" + context.getResources().getString(R.string.retry_internet_msg));
            }
            //showSnackBarBasedonStatus(isNetworkAvailable);
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
        return isNetworkAvailable;
    }

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    public static final int CAMERA_PERMISSION = 151;
    public static final int RECORD_AUDIO = 155;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 124;


    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean checkPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public static boolean mCheckPermissionForWrite(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public static boolean checkPermissionCamera(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.CAMERA)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Camera permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public static boolean checkPermissionRecord(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.RECORD_AUDIO)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Record Audio permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public static void setSnackbarElements(LinearLayout layout1, final Activity activity1) {
        layout = layout1;
        activity = activity1;
    }

    public static boolean canDisplayPdf(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType("application/pdf");
        return packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY).size() >= 0;
    }

    public static void showSnackBarBasedonStatus(boolean netStatus) {
        try {
            if (netStatus) {
                if (snackbar != null) {
                    if (snackbar.isShown()) {
                        snackbar.dismiss();
                    }
                }
            } else {
                String messageToDisplay = OustSdkApplication.getContext().getResources().getString(R.string.no_internet_connection);
                snackbar = Snackbar
                        .make(layout, messageToDisplay, Snackbar.LENGTH_INDEFINITE)
                        .setAction("HIDE", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snackbar.dismiss();
                            }
                        });
                View view = snackbar.getView();
                float height = view.getHeight();
                view.setScaleY((float) .8);
                view.setBackgroundColor(getColorBack(R.color.popupBackGround));
                view.setY(15f);
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
                params.gravity = Gravity.BOTTOM;
                view.setLayoutParams(params);

                TextView tv = view.findViewById(R.id.snackbar_text);
                TextView tv1 = view.findViewById(R.id.snackbar_action);
                tv1.setTextSize(OustSdkApplication.getContext().getResources().getDimension(R.dimen.ousttext_dimen6));
                tv.setTextSize(OustSdkApplication.getContext().getResources().getDimension(R.dimen.ousttext_dimen6));
                tv.setTextColor(getColorBack(R.color.whitea));
                tv1.setTypeface(getAvenirLTStdMedium());
                tv.setTypeface(getAvenirLTStdMedium());
                snackbar.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
    }

    public static void newActivityAnimationB(Intent intent, Activity activity) {
        try {
            int sdk = Build.VERSION.SDK_INT;
            if (sdk > Build.VERSION_CODES.JELLY_BEAN) {
                Bundle translateBundle = ActivityOptions.makeCustomAnimation(activity, R.anim.anim_slidein, R.anim.activityb_anim).toBundle();
                activity.startActivity(intent, translateBundle);
            } else {
                activity.startActivity(intent);
            }
        } catch (Exception e) {
            activity.startActivity(intent);
        }
    }

    public static boolean isAppInstalled(String uri) {
        PackageManager pm = OustSdkApplication.getContext().getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }


    public static void setTxtBackgroud(TextView txt, int id) {
        try {
            Drawable d;
            int sdk = Build.VERSION.SDK_INT;
            if (sdk > Build.VERSION_CODES.LOLLIPOP) {
                d = OustSdkApplication.getContext().getDrawable(id);
            } else {
                d = OustSdkApplication.getContext().getResources().getDrawable(id);
            }
            if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                txt.setBackgroundDrawable(d);
            } else {
                txt.setBackground(d);
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
    }

    public static void setLayoutBackgroud(View layout, int id) {
        try {
            Drawable d;
            int sdk = Build.VERSION.SDK_INT;
            if (sdk > Build.VERSION_CODES.LOLLIPOP) {
                d = OustSdkApplication.getContext().getDrawable(id);
            } else {
                d = OustSdkApplication.getContext().getResources().getDrawable(id);
            }
            if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                layout.setBackgroundDrawable(d);
            } else {
                layout.setBackground(d);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
    }


    public static void setLayoutBackgrouda(LinearLayout layout, int id) {
        try {
            Drawable d;
            int sdk = Build.VERSION.SDK_INT;
            if (sdk > Build.VERSION_CODES.LOLLIPOP) {
                d = OustSdkApplication.getContext().getDrawable(id);
            } else {
                d = OustSdkApplication.getContext().getResources().getDrawable(id);
            }
            if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                layout.setBackgroundDrawable(d);
            } else {
                layout.setBackground(d);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
    }

    public static AnimatorSet toucheffect(View view) {
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 0.94f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 0.96f);
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
        return scaleDown;
    }

    public static void sendPingApi() {
        try {
            PingApiRequest pingApiRequest = new PingApiRequest();
            if (OustAppState.getInstance().getActiveUser() != null && OustAppState.getInstance().getActiveUser().getStudentid() != null) {
                pingApiRequest.setStudentid(OustAppState.getInstance().getActiveUser().getStudentid());
            } else {
                ActiveUser activeUser = getActiveUserData();
                pingApiRequest.setStudentid(activeUser.getStudentid());
            }

            pingApiRequest.setDeviceInfoData(getDeviceInfo());
            pingApiRequest.setOnline(checkInternetStatus());
            pingApiRequest.setPingTimestamp(System.currentTimeMillis());
            Gson gson = new Gson();
            String str = gson.toJson(pingApiRequest);
            Log.d(TAG, "sendPingApi: requests:" + str);
            List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedPingRequests");
            if (requests == null) {
                requests = new ArrayList<>();
            }
            if (requests != null) {
                requests.add(str);
            }
            OustPreferences.saveLocalNotificationMsg("savedPingRequests", requests);
            Intent intent = new Intent(Intent.ACTION_SYNC, null, OustSdkApplication.getContext(), SendApiServices.class);
            OustSdkApplication.getContext().startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
    }

    public static void sendPingApiWithStudentId(String studenId) {
        try {
            Log.d(TAG, "sendPingApiWithStudentId: " + studenId);
            PingApiRequest pingApiRequest = new PingApiRequest();
            pingApiRequest.setStudentid(studenId);
            pingApiRequest.setDeviceInfoData(getDeviceInfo());
            pingApiRequest.setOnline(checkInternetStatus());
            pingApiRequest.setPingTimestamp(System.currentTimeMillis());
            Gson gson = new Gson();
            String str = gson.toJson(pingApiRequest);
            List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedPingRequests");
            if (requests == null) {
                requests = new ArrayList<>();
            }
            if (requests != null) {
                requests.add(str);
            }
            OustPreferences.saveLocalNotificationMsg("savedPingRequests", requests);
            OustPreferences.saveTimeForNotification("SentPingRequestTime", System.currentTimeMillis());
            Intent intent = new Intent(Intent.ACTION_SYNC, null, OustSdkApplication.getContext(), SendApiServices.class);
            OustSdkApplication.getContext().startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
    }

    public static String getMysteryAvatar() {

        String mysteryAvatar = "avatar-1.png";
        return mysteryAvatar;

    }


    public static int getColorBack(int id) {
        int colorId = 0;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (OustSdkApplication.getContext() != null) {
                    colorId = OustSdkApplication.getContext().getColor(id);
                }
            } else {
                if (OustSdkApplication.getContext() != null && OustSdkApplication.getContext().getResources() != null) {
                    colorId = OustSdkApplication.getContext().getResources().getColor(id);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
            return colorId;
        }
        return colorId;
    }

    public static void newActivityAnimationD(Intent intent, Activity activity) {
        try {
            int sdk = Build.VERSION.SDK_INT;
            if (sdk > Build.VERSION_CODES.JELLY_BEAN) {
                Bundle translateBundle = ActivityOptions.makeCustomAnimation(activity, R.anim.splashtolandingswitchanimb, R.anim.splashtolandingswitchanima).toBundle();
                activity.startActivity(intent, translateBundle);
            } else {
                activity.startActivity(intent);
            }
        } catch (Exception e) {
            activity.startActivity(intent);
        }
    }

    public static ActiveUser getActiveUserData(String jsonString) {
        if ((jsonString != null) && (!jsonString.isEmpty())) {
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(jsonString, ActiveUser.class);
        }
        return null;
    }

    public static ArrayList<CommonLandingData> getReminderNotification(String jsonString) {
        if ((jsonString != null) && (!jsonString.isEmpty())) {
            Gson gson = new GsonBuilder().create();
            Type listType = new TypeToken<ArrayList<CommonLandingData>>() {
            }.getType();
            return gson.fromJson(jsonString, listType);
        }
        return null;
    }

    public static ActiveUser getActiveUserData() {
        ActiveUser activeUser = OustAppState.getInstance().getActiveUser();
        if ((activeUser != null) && (activeUser.getStudentid() != null)) {
        } else {
            HttpManager.setBaseUrl();
            OustFirebaseTools.initFirebase();
            String activeUserGet = OustPreferences.get("userdata");
            activeUser = getActiveUserData(activeUserGet);
        }
        return activeUser;
    }

    public static DTOQuestions decryptQuestion(EncrypQuestions encrypQuestion, Cipher cipher) {
        DTOQuestions questions1 = new DTOQuestions();
        try {
            if (cipher == null) {
                cipher = Cipher.getInstance("AES");
                Key key = new SecretKeySpec(AES_ENCRYPTION_KEY.getBytes(UNICODE_FORMAT), AES_KEY);
                cipher.init(Cipher.DECRYPT_MODE, key);
            }
            Gson gson = new GsonBuilder().create();
            String questionToDecrypt = encrypQuestion.getEncryptedQuestions();
            String questionImage = encrypQuestion.getImage();
            String questionBgImage = encrypQuestion.getBgImg();
            questionToDecrypt = questionToDecrypt.replace(",type=0", "");
            byte[] base64TextToDecrypt = Base64.decode(questionToDecrypt, Base64.DEFAULT);
            byte[] decryptedByte = cipher.doFinal(base64TextToDecrypt);
            String decryptedText = new String(decryptedByte);
            questions1 = gson.fromJson(decryptedText, DTOQuestions.class);
            questions1.setImage(questionImage);
            questions1.setBgImg(questionBgImage);
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
        return questions1;
    }


    public static PlayResponse getDecryptedQuestion(PlayResponse playResponse) {
        try {
            Key key = new SecretKeySpec(AES_ENCRYPTION_KEY.getBytes(UNICODE_FORMAT), AES_KEY);
            DTOQuestions[] questionses = new DTOQuestions[playResponse.getEncrypQuestions().length];

            Cipher cipher = Cipher.getInstance(AES_KEY);
            cipher.init(Cipher.DECRYPT_MODE, key);

            EncrypQuestions[] encrypQuestions = playResponse.getEncrypQuestions();
            int questionIndex = 0;

            for (EncrypQuestions encryptedQuestion : encrypQuestions) {
                questionses[questionIndex++] = decryptQuestion(encryptedQuestion, cipher);
            }
            playResponse.setQuestions(questionses);
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
        return playResponse;
    }

    //--------------------------------------
//set background for diff view
    public static void setBtnBackgroud(Button btn, int id) {
        try {
            Drawable d;
            int sdk = Build.VERSION.SDK_INT;
            if (sdk > Build.VERSION_CODES.LOLLIPOP) {
                d = OustSdkApplication.getContext().getDrawable(id);
            } else {
                d = OustSdkApplication.getContext().getResources().getDrawable(id);
            }
            if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                btn.setBackgroundDrawable(d);
            } else {
                btn.setBackground(d);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
    }

    public static TextView setGroupsIconInActivity(TextView txtGroupAvatar, String groupName) {
        int sdk = android.os.Build.VERSION.SDK_INT;
        String imageName = OustSdkApplication.getContext().getString(R.string.announcement_bg);
        String pathName = Environment.getExternalStorageDirectory() + "/Oust/Images/" + imageName;
        Resources res = OustSdkApplication.getContext().getResources();
        Bitmap bitmap = BitmapFactory.decodeFile(pathName);
        BitmapDrawable bd = new BitmapDrawable(res, bitmap);

        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            txtGroupAvatar.setBackgroundDrawable(bd);
        } else {
            txtGroupAvatar.setBackground(bd);
        }
        if ((groupName != null) && (!groupName.isEmpty())) {
            if (groupName.length() >= 2) {
                txtGroupAvatar.setText(groupName.substring(0, 2).toUpperCase());
            } else if (groupName.length() == 1) {
                txtGroupAvatar.setText(groupName.substring(0, 1).toUpperCase());
            }
        }
        return txtGroupAvatar;
    }


    public static PopupWindow zoomImagePopup;

    public static void gifZoomPopup(final Drawable gif, final Activity activity2, final DialogKeyListener dialogKeyListener) {
        try {
            if ((zoomImagePopup != null) && (zoomImagePopup.isShowing())) {

            } else {
                View popUpView = activity2.getLayoutInflater().inflate(R.layout.gifzoom_popup, null);
                zoomImagePopup = createPopUp(popUpView);
                GifImageView mainzooming_img = popUpView.findViewById(R.id.mainzooming_img);
                mainzooming_img.setImageDrawable(gif);
                PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(mainzooming_img);
                ImageButton zooming_imgclose_btn = popUpView.findViewById(R.id.zooming_imgclose_btn);
                final RelativeLayout mainzoomimg_layout = popUpView.findViewById(R.id.mainzoomimg_layout);
                ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(mainzoomimg_layout, "scaleX", 0.0f, 1);
                ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(mainzoomimg_layout, "scaleY", 0.0f, 1);
                scaleDownX.setDuration(400);
                scaleDownY.setDuration(400);
                scaleDownX.setInterpolator(new DecelerateInterpolator());
                scaleDownY.setInterpolator(new DecelerateInterpolator());
                AnimatorSet scaleDown = new AnimatorSet();
                scaleDown.play(scaleDownX).with(scaleDownY);
                scaleDown.start();
                mainzoomimg_layout.setVisibility(View.VISIBLE);

                zoomImagePopup.setOnDismissListener(() -> {
                    if (dialogKeyListener != null) {
                        dialogKeyListener.onDialogClose();
                    }
                });

                zooming_imgclose_btn.setOnClickListener(view -> {
                    ObjectAnimator scaleDownX1 = ObjectAnimator.ofFloat(mainzoomimg_layout, "scaleX", 1.0f, 0);
                    ObjectAnimator scaleDownY1 = ObjectAnimator.ofFloat(mainzoomimg_layout, "scaleY", 1.0f, 0);
                    scaleDownX1.setDuration(350);
                    scaleDownY1.setDuration(350);
                    scaleDownX1.setInterpolator(new DecelerateInterpolator());
                    scaleDownY1.setInterpolator(new DecelerateInterpolator());
                    AnimatorSet scaleDown1 = new AnimatorSet();
                    scaleDown1.play(scaleDownX1).with(scaleDownY1);
                    scaleDown1.start();
                    scaleDown1.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            try {
                                if ((zoomImagePopup != null) && (zoomImagePopup.isShowing())) {
                                    zoomImagePopup.dismiss();
                                   /* ModuleOverViewFragment fragment = new ModuleOverViewFragment();
                                    fragment.showAllMediaFullImage();
                                    fragment.setTopContent();
                                    fragment.setTopContentTitle();*/
                                    Log.d(TAG, "onAnimationEnd:zoomout ");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                sendSentryException(e);
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {
                        }
                    });
                });
                popUpView.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(mainzoomimg_layout, "scaleX", 1.0f, 0);
                            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(mainzoomimg_layout, "scaleY", 1.0f, 0);
                            scaleDownX.setDuration(350);
                            scaleDownY.setDuration(350);
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
                                    if ((zoomImagePopup != null) && (zoomImagePopup.isShowing())) {
                                        zoomImagePopup.dismiss();
                                    }
                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {
                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {
                                }
                            });
                            return true;
                        }
                        return false;
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }

    }

    public static ActiveUser getActiveUser(SignInResponse signInResponse) {
        ActiveUser activeUser = new ActiveUser();
        try {
            activeUser.setStudentid(signInResponse.getStudentid());
            activeUser.setStudentKey(signInResponse.getStudentKey());
            activeUser.setUserDisplayName(signInResponse.getUserDisplayName());
            activeUser.setAppleStore(signInResponse.getAppleStore());
            activeUser.setDeviceIdentity(signInResponse.getDeviceIdentity());
            activeUser.setDevicePlatformName(signInResponse.getDevicePlatformName());
            activeUser.setEmail(signInResponse.getEmail());
            activeUser.setExpDate(signInResponse.getExpDate());
            activeUser.setGoogleStore(signInResponse.getGoogleStore());
            activeUser.setHa(signInResponse.getHa());
            activeUser.setHq(signInResponse.getHq());
            activeUser.setSession(signInResponse.getSession());
            activeUser.setSubject("");
            activeUser.setGrade("");
            activeUser.setNewAlert("");
            activeUser.setNewChallenge("");
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
        return activeUser;
    }

    public static ActiveUser getNewActiveUser(VerifyAndSignInResponse verifyAndSignInResponse) {
        ActiveUser activeUser = new ActiveUser();
        try {
            activeUser.setStudentid(verifyAndSignInResponse.getStudentid());
            activeUser.setStudentKey(verifyAndSignInResponse.getStudentKey());
            activeUser.setUserDisplayName(verifyAndSignInResponse.getUserDisplayName());
            activeUser.setAppleStore(verifyAndSignInResponse.getAppleStore());
            activeUser.setDeviceIdentity(verifyAndSignInResponse.getDeviceIdentity());
            activeUser.setDevicePlatformName(verifyAndSignInResponse.getDevicePlatformName());
            activeUser.setEmail(verifyAndSignInResponse.getEmail());
            activeUser.setExpDate(verifyAndSignInResponse.getExpDate());
            activeUser.setGoogleStore(verifyAndSignInResponse.getGoogleStore());
            activeUser.setHa(verifyAndSignInResponse.getHa());
            activeUser.setHq(verifyAndSignInResponse.getHq());
            activeUser.setSession(verifyAndSignInResponse.getSession());
            activeUser.setSubject("");
            activeUser.setGrade("");
            activeUser.setNewAlert("");
            activeUser.setNewChallenge("");
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
        return activeUser;
    }

    public static ActiveGame getActiveGameData(String jsonString) {
        if ((jsonString != null) && (!jsonString.isEmpty())) {
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(jsonString, ActiveGame.class);
        }
        return null;
    }

    public static AssessmentPlayResponse getAssessmentPlayResponse(String jsonString) {
        if ((jsonString != null) && (!jsonString.isEmpty())) {
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(jsonString, AssessmentPlayResponse.class);
        }
        return null;
    }

    public static String getuuId() {
        String uuid = "";
        try {
            uuid = Settings.Secure.getString(OustSdkApplication.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
        return uuid;
    }

    public static DeviceInfoData getDeviceInfo() {
        PackageInfo pInfo = null;
        try {
            pInfo = OustSdkApplication.getContext().getPackageManager().getPackageInfo(OustSdkApplication.getContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            sendSentryException(e);
        }
        DeviceInfoData deviceInfoData = new DeviceInfoData();
        try {
            String version = pInfo.versionName;
            String model = Build.DEVICE;
            String name = Build.MANUFACTURER;
            deviceInfoData.setVersion(version);
            deviceInfoData.setPlatform(getDevicePlatformFromPreference());
            deviceInfoData.setModel(model);
            deviceInfoData.setName(name);
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
        return deviceInfoData;
    }


    public static void setLayoutBackgroudDrawable(View view, Drawable d) {
        try {
            int sdk = Build.VERSION.SDK_INT;
            if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackgroundDrawable(d);
            } else {
                view.setBackground(d);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
    }

    //=================================================================================================================
//methode to be called on on any avtar click to fetch oust user info

    public static JSONObject getRequestObject(String jsonParams) {
        JSONObject jsonObject = null;
        Log.d(TAG, "Additional request data");
        try {
            if ((jsonParams != null) && (!jsonParams.isEmpty())) {
                jsonObject = new JSONObject(jsonParams);
            } else {
                jsonObject = new JSONObject();
            }
            PackageInfo pinfo = OustSdkApplication.getContext().getPackageManager().getPackageInfo(OustSdkApplication.getContext().getPackageName(), 0);
            jsonObject.put("appVersion", pinfo.versionName);
            jsonObject.put("devicePlatformName", "Android");
            String uuid = Settings.Secure.getString(OustSdkApplication.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            if ((uuid != null) && (!uuid.isEmpty())) {
                jsonObject.put("deviceIdentity", uuid);
            }
            if ((OustPreferences.get("gcmToken") != null)) {
                jsonObject.put("deviceToken", OustPreferences.get("gcmToken"));
            }
        } catch (PackageManager.NameNotFoundException | JSONException e) {
            e.printStackTrace();
            sendSentryException(e);
        }
        Log.d(TAG, "getRequestObject: " + jsonObject.toString());
        return jsonObject;
    }

    public static JSONObject getRequestObjectforJSONObject(JSONObject jsonParams) {
        JSONObject jsonObject = null;
        Log.d(TAG, "Additional request data");
        try {
            if (jsonParams != null) {
                jsonObject = jsonParams;
            } else {
                jsonObject = new JSONObject();
            }
            PackageInfo pinfo = OustSdkApplication.getContext().getPackageManager().getPackageInfo(OustSdkApplication.getContext().getPackageName(), 0);
            jsonObject.put("appVersion", pinfo.versionName);
            jsonObject.put("devicePlatformName", "Android");
            String uuid = Settings.Secure.getString(OustSdkApplication.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            if ((uuid != null) && (!uuid.isEmpty())) {
                jsonObject.put("deviceIdentity", uuid);
            }
            if ((OustPreferences.get("gcmToken") != null)) {
                jsonObject.put("deviceToken", OustPreferences.get("gcmToken"));
            }
        } catch (JSONException | PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            sendSentryException(e);
//            if (LogFlag.bLogOn) Log.d(TAG, e.toString());
        }
        return jsonObject;
    }

    public static void initServerWithNewEndPoints(String serverEndPoint, String firebaseEndpoint, String firebaseAppId, String firebaseAPIKey) {
        try {
            Log.e("Firebase", "Setting fcm parameters");
            if ((firebaseEndpoint != null) && (!firebaseEndpoint.isEmpty())) {
                OustPreferences.save("firebaseEndpoint", firebaseEndpoint);
            }

            if ((serverEndPoint != null) && (!serverEndPoint.isEmpty())) {
                OustPreferences.save("apiServerEndpoint", serverEndPoint);
            }
            if (firebaseAppId != null && !firebaseAppId.isEmpty()) {
                OustPreferences.save("firebaseAppId", firebaseAppId);
            }
            if (firebaseAPIKey != null && !firebaseAPIKey.isEmpty()) {
                OustPreferences.save("firebaseAPIKey", firebaseAPIKey);
            }
            Log.e("init server ", "serverend " + serverEndPoint + " end point " + firebaseEndpoint + " appid " + firebaseAppId + " apikey " + firebaseAPIKey);
            HttpManager.setBaseUrl();
            OustFirebaseTools.initFirebase();
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
    }

    public static void newActivityAnimation(Intent intent, Activity activity) {
        try {
            int sdk = Build.VERSION.SDK_INT;
            if (sdk > Build.VERSION_CODES.JELLY_BEAN) {
                Bundle translateBundle = ActivityOptions.makeCustomAnimation(activity, R.anim.event_animmovein, R.anim.event_animmoveout).toBundle();
                activity.startActivity(intent, translateBundle);
            } else {
                activity.startActivity(intent);
            }
        } catch (Exception e) {
            activity.startActivity(intent);
            e.printStackTrace();
            sendSentryException(e);
        }
    }

    public static PopupWindow createPopUp(View popUpView) {
        final PopupWindow popupWindow = new PopupWindow(popUpView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true); //Creation of popup
        try {
            popupWindow.showAtLocation(popUpView, Gravity.CENTER_VERTICAL, 0, 0);    // Displaying popup
            popUpView.setFocusableInTouchMode(true);
            popUpView.requestFocus();
            popUpView.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    if (popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                    return true;
                }
                return false;
            });

        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
        return popupWindow;
    }

    public static PopupWindow createPopWithoutBackButton(View popUpView) {
        final PopupWindow mpopup = new PopupWindow(popUpView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT); //Creation of popup
        try {
            mpopup.showAtLocation(popUpView, Gravity.CENTER_VERTICAL, 0, 0);    // Displaying popup
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
        return mpopup;
    }

    public static PopupWindow createFullScreenPopUp(View popUpView) {
        final PopupWindow mpopup = new PopupWindow(popUpView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true); //Creation of popup
        try {
//            mpopup.setAnimationStyle(R.anim.popup_appearanim);
            mpopup.showAtLocation(popUpView, Gravity.CENTER_VERTICAL, 0, 0);    // Displaying popup
            //mpopup.setAnimationStyle(R.anim.popup_appearanim);
            popUpView.setFocusableInTouchMode(true);
            popUpView.requestFocus();
            popUpView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                        if ((mpopup != null) && (mpopup.isShowing())) {
                            mpopup.dismiss();
                        }
                        return true;
                    }
                    return false;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
        return mpopup;
    }


    public static void popupAppearEffect(View view) {
        try {
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 0.2f, 1.0f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 0.2f, 1.0f);
            scaleDownX.setDuration(300);
            scaleDownY.setDuration(300);
            scaleDownX.setInterpolator(new DecelerateInterpolator());
            scaleDownY.setInterpolator(new DecelerateInterpolator());
            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleDownX).with(scaleDownY);
            scaleDown.start();
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
    }

    public static void setProgressbar(ProgressBar progressBar1) {
        progressBar = progressBar1;
    }

    public static void showProgressBar() {
        try {
            final Context context = OustSdkApplication.getContext();
            Handler mainHandler = new Handler(context.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    if (progressBar != null) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }
            };
            mainHandler.post(myRunnable);
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
    }

    public static String setTxtNotificationPeriod(String inDate) {
        String period = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));

        try {
            Date d1 = format.parse(TimeUtils.getDateAsString(inDate));
            Date d2 = format.parse(TimeUtils.getCurrentDateAsString());
            //in milliseconds
            long diff = d2.getTime() - d1.getTime();

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            if (diffDays != 0) {
                period = diffDays + " days ago ";
            } else if ((diffHours != 0)) {
                period = diffHours + " hours ago ";
            } else if ((diffMinutes != 0)) {
                period = diffMinutes + " minutes ago ";
            } else {
                period = diffSeconds + " seconds ago ";
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
            sendSentryException(e);
        }
        return period;
    }

    public static String setCplContentDate(String inDate) {
        String period = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));

        try {
            Date d2 = format.parse(TimeUtils.getDateAsString(inDate));
            Date d1 = format.parse(TimeUtils.getCurrentDateAsString());
            //in milliseconds
            long diff = d2.getTime() - d1.getTime();

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            if (diffDays != 0) {
                period = diffDays + "";
            } else if ((diffHours != 0)) {
                period = diffHours + "";
            } else if ((diffMinutes != 0)) {
                period = diffMinutes + "";
            } else {
                period = diffSeconds + "";
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
            sendSentryException(e);
        }
        return period;
    }


    public static void hideProgressbar() {
        try {
            final Context context = OustSdkApplication.getContext();
            Handler mainHandler = new Handler(context.getMainLooper());
            Runnable myRunnable = () -> {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            };
            mainHandler.post(myRunnable);
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
    }

    //get color and drawables
    public static Drawable getImgDrawable(int id) {
        Drawable d;
        int sdk = Build.VERSION.SDK_INT;
        if (sdk > Build.VERSION_CODES.LOLLIPOP) {
            d = OustSdkApplication.getContext().getDrawable(id);
        } else {
            d = OustSdkApplication.getContext().getResources().getDrawable(id);
        }
        return d;
    }

    public static void oustTouchEffect(View view, int time) {
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 0.9f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 0.9f);
        scaleDownX.setDuration(time);
        scaleDownY.setDuration(time);
        scaleDownX.setRepeatCount(1);
        scaleDownY.setRepeatCount(1);
        scaleDownX.setRepeatMode(ValueAnimator.REVERSE);
        scaleDownY.setRepeatMode(ValueAnimator.REVERSE);
        scaleDownX.setInterpolator(new DecelerateInterpolator());
        scaleDownY.setInterpolator(new DecelerateInterpolator());
        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleDownX).with(scaleDownY);
        scaleDown.start();
    }

    //methode to show toast message over main thread
    public static void showToast(String message) {
        try {
            if ((message != null) && (!message.isEmpty())) {
                if (toast != null) {
                    toast.cancel();
                    toast = null;
                }
                final Context context = OustSdkApplication.getContext();
                final CharSequence text = message;
                final int duration = Toast.LENGTH_SHORT;
                Handler mainHandler = new Handler(context.getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        toast = Toast.makeText(context, "" + text + "", duration);
                        toast.show();
                    }
                };
                mainHandler.post(myRunnable);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
    }

    public static void showCustomToast(final View layout, final Context context) {
        try {
            if (layout != null) {
                //final Context context = OustSdkApplication.getContext();
                //final CharSequence text = message;
                final int duration = Toast.LENGTH_LONG;
                Handler mainHandler = new Handler(context.getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        Toast toast = new Toast(context);
                        toast.setGravity(Gravity.FILL, 0, 0);
                        toast.setDuration(duration);
                        toast.setView(layout);
                        toast.show();

                        //Toast toast = Toast.makeText(context, "  " + text + "  ", duration);
                        //toast.show();
                    }
                };
                mainHandler.post(myRunnable);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
    }

    public static Bitmap getScreenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public static void setGroupImage(TextView txtGroupName, String initial) {
        try {
            if (initial.length() > 2) {
                char c = initial.toUpperCase().charAt(1);
                Log.e(TAG, "initial's character " + c);
                if (c < 'D') {
                    setTxtBackgroud(txtGroupName, R.drawable.orange_round_textview);
                } else if (c < 'I') {
                    setTxtBackgroud(txtGroupName, R.drawable.litegray_round_textview);
                } else if (c < 'O') {
                    setTxtBackgroud(txtGroupName, R.drawable.greenlite_round_textview);
                } else if (c < 'Z') {
                    setTxtBackgroud(txtGroupName, R.drawable.darkgreen_round_textview);
                } else {
                    setTxtBackgroud(txtGroupName, R.drawable.orangelite_round_textview);
                }
            } else {
                setTxtBackgroud(txtGroupName, R.drawable.orange_round_textview);
            }
            if (OustPreferences.get("toolbarColorCode") != null) {
                GradientDrawable bgShape = (GradientDrawable) txtGroupName.getBackground();
                bgShape.setColor(Color.parseColor(OustPreferences.get("toolbarColorCode")));
                Log.e(TAG, "text background panel color " + Color.parseColor(OustPreferences.get("toolbarColorCode")));
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
    }

    public static void getSpannedContent(String content, TextView textView) {
        String s2 = content.trim();
        try {
            while (s2.endsWith("<br />")) {
                s2 = s2.substring(0, s2.lastIndexOf("<br />"));
            }
            s2 = customizeListTags(s2);
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }

        //this is for if the tag <any character without space like this
        if (content.contains("<") && content.length() == 1) {
            textView.setText(content);
        } else if (content.contains("<")) {
            int index = content.indexOf("<");
            int nextLen = index + 1;
            if (nextLen < content.length()) {
                if (content.charAt(nextLen) == ' ' || Character.isLetter(content.charAt(nextLen))) {
                    Spanned s1 = Html.fromHtml(s2);
                    renderHtmlText(s1.toString(), textView);
                } else {
                    textView.setText(content);
                }
            } else {
                textView.setText(content);
            }
        } else {
            Spanned s1 = Html.fromHtml(s2);
            renderHtmlText(s1.toString(), textView);
        }
    }

    public static String getSpannedContentText(String content) {
        try {
            String s2 = content.trim();
            try {
                while (s2.endsWith("<br />")) {
                    s2 = s2.substring(0, s2.lastIndexOf("<br />"));
                }
                s2 = customizeListTags(s2);
            } catch (Exception e) {
                e.printStackTrace();
                sendSentryException(e);
            }

            //this is for if the tag <any character without space like this
            if (content.contains("<") && content.length() == 1) {
                return content;
            } else if (content.contains("<")) {
                int index = content.indexOf("<");
                int nextLen = index + 1;
                if (nextLen < content.length()) {
                    if (content.charAt(nextLen) == ' ' || Character.isLetter(content.charAt(nextLen))) {
                        Spanned s1 = Html.fromHtml(s2);
                        return renderHtmlText(s1.toString());
                    } else {
                        return content;
                    }
                } else {
                    return content;
                }
            } else {
                Spanned s1 = Html.fromHtml(s2);
                return renderHtmlText(s1.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
            return content;
        }
    }

    private static String customizeListTags(String html) {
        if (html == null) {
            return null;
        }
        html = html.replaceAll("<ul>", "OUSTVINULO");
        html = html.replaceAll("</ul>", "OUSTVINULC");
        html = html.replaceAll("<ol>", "OUSTVINOLO");
        html = html.replaceAll("</ol>", "OUSTVINOLC");
        html = html.replaceAll("<li>", "OUSTVINLIO");
        html = html.replaceAll("</li>", "OUSTVINLIC");
        return html;
    }

    private static String customizeListTagsA(String html) {
        if (html == null) {
            return null;
        }
        html = html.replaceAll("OUSTVINULO", "OUSTMYTAG OUSTVINULO OUSTMYTAG");
        html = html.replaceAll("OUSTVINULC", "OUSTMYTAG OUSTVINULC OUSTMYTAG");
        html = html.replaceAll("OUSTVINOLO", "OUSTMYTAG OUSTVINOLO OUSTMYTAG");
        html = html.replaceAll("OUSTVINOLC", "OUSTMYTAG OUSTVINOLC OUSTMYTAG");
        html = html.replaceAll("OUSTVINLIO", "OUSTMYTAG");
        html = html.replaceAll("OUSTVINLIC", "OUSTVINLIC");
        return html;
    }

    private static String mainTag;

    private static void renderHtmlText(String s1, TextView textView) {
        Editable editable = new SpannableStringBuilder("");
        int bulleteNo = 0;
        if ((s1.contains("OUSTVINULO")) || (s1.contains("OUSTVINOLO"))) {
            s1 = customizeListTagsA(s1);
            String[] split = s1.split("OUSTMYTAG");
            for (int i = 0; i < split.length; i++) {
                if (split[i].contains("OUSTVINULO") || split[i].contains("OUSTVINOLO")) {
                    if (split[i].contains("OUSTVINULO")) {
                        mainTag = "OUSTVINULO";
                    } else {
                        mainTag = "OUSTVINOLO";
                    }
                    bulleteNo = 0;
                } else if (split[i].contains("OUSTVINULC") || split[i].contains("OUSTVINOLC")) {
                    mainTag = split[i];
                    bulleteNo = 0;
                } else if (split[i].contains("OUSTVINLIO")) {
                } else if (split[i].contains("OUSTVINLIC")) {
                    String lineStr = split[i];
                    if ((lineStr != null) && (!lineStr.isEmpty())) {
                        lineStr = lineStr.replace("\tOUSTVINLIO", "");
                        lineStr = lineStr.replace("\tOUSTVINLIC", "");
                        lineStr = lineStr.replace("OUSTVINLIO\t", "");
                        lineStr = lineStr.replace("OUSTVINLIC\t", "");
                        lineStr = lineStr.replace("\t OUSTVINLIO", "");
                        lineStr = lineStr.replace("\t OUSTVINLIC", "");
                        lineStr = lineStr.replace("OUSTVINLIO \t", "");
                        lineStr = lineStr.replace("OUSTVINLIC \t", "");
                        lineStr = lineStr.replace("OUSTVINLIO", "");
                        lineStr = lineStr.replace("OUSTVINLIC", "");
                        editable.append(lineStr);
                        bulleteNo++;
                        if (mainTag.equals("OUSTVINULO")) {
                            editable.setSpan(new OustBulletSpan(), editable.length() - lineStr.length(), editable.length(), 0);
                        } else {
                            editable.setSpan(new NumberSpan(bulleteNo), editable.length() - lineStr.length(), editable.length(), 0);
                        }
                        editable.append("\n");
                    }
                } else {
                    String lineStr = split[i];
                    if ((lineStr != null) && (!lineStr.isEmpty()) && (!lineStr.equals("\t"))) {
                        lineStr = lineStr.replace("\tOUSTVINLIO>", "");
                        lineStr = lineStr.replace("\tOUSTVINLIC", "");
                        lineStr = lineStr.replace("OUSTVINLIO\t", "");
                        lineStr = lineStr.replace("OUSTVINLIC\t", "");
                        lineStr = lineStr.replace("\t OUSTVINLIO>", "");
                        lineStr = lineStr.replace("\t OUSTVINLIC", "");
                        lineStr = lineStr.replace("OUSTVINLIO \t", "");
                        lineStr = lineStr.replace("OUSTVINLIC \t", "");
                        lineStr = lineStr.replace("OUSTVINLIO", "");
                        lineStr = lineStr.replace("OUSTVINLIC", "");
                        lineStr = lineStr.replaceAll("<br />", "\n");
                        editable.append(lineStr);
                        if (i < split.length) {
                            editable.append("\n");
                        }
                    }
                }
            }
        } else {
            editable.append(s1);
        }
        textView.setText(editable);
    }

    private static String renderHtmlText(String s1) {
        try {
            Editable editable = new SpannableStringBuilder("");
            int bulleteNo = 0;
            if ((s1.contains("OUSTVINULO")) || (s1.contains("OUSTVINOLO"))) {
                s1 = customizeListTagsA(s1);
                String[] split = s1.split("OUSTMYTAG");
                for (int i = 0; i < split.length; i++) {
                    if (split[i].contains("OUSTVINULO") || split[i].contains("OUSTVINOLO")) {
                        if (split[i].contains("OUSTVINULO")) {
                            mainTag = "OUSTVINULO";
                        } else {
                            mainTag = "OUSTVINOLO";
                        }
                        bulleteNo = 0;
                    } else if (split[i].contains("OUSTVINULC") || split[i].contains("OUSTVINOLC")) {
                        mainTag = split[i];
                        bulleteNo = 0;
                    } else if (split[i].contains("OUSTVINLIO")) {
                    } else if (split[i].contains("OUSTVINLIC")) {
                        String lineStr = split[i];
                        if ((lineStr != null) && (!lineStr.isEmpty())) {
                            lineStr = lineStr.replace("\tOUSTVINLIO", "");
                            lineStr = lineStr.replace("\tOUSTVINLIC", "");
                            lineStr = lineStr.replace("OUSTVINLIO\t", "");
                            lineStr = lineStr.replace("OUSTVINLIC\t", "");
                            lineStr = lineStr.replace("\t OUSTVINLIO", "");
                            lineStr = lineStr.replace("\t OUSTVINLIC", "");
                            lineStr = lineStr.replace("OUSTVINLIO \t", "");
                            lineStr = lineStr.replace("OUSTVINLIC \t", "");
                            lineStr = lineStr.replace("OUSTVINLIO", "");
                            lineStr = lineStr.replace("OUSTVINLIC", "");
                            editable.append(lineStr);
                            bulleteNo++;
                            if (mainTag.equals("OUSTVINULO")) {
                                editable.setSpan(new OustBulletSpan(), editable.length() - lineStr.length(), editable.length(), 0);
                            } else {
                                editable.setSpan(new NumberSpan(bulleteNo), editable.length() - lineStr.length(), editable.length(), 0);
                            }
                            editable.append("\n");
                        }
                    } else {
                        String lineStr = split[i];
                        if ((lineStr != null) && (!lineStr.isEmpty()) && (!lineStr.equals("\t"))) {
                            lineStr = lineStr.replace("\tOUSTVINLIO>", "");
                            lineStr = lineStr.replace("\tOUSTVINLIC", "");
                            lineStr = lineStr.replace("OUSTVINLIO\t", "");
                            lineStr = lineStr.replace("OUSTVINLIC\t", "");
                            lineStr = lineStr.replace("\t OUSTVINLIO>", "");
                            lineStr = lineStr.replace("\t OUSTVINLIC", "");
                            lineStr = lineStr.replace("OUSTVINLIO \t", "");
                            lineStr = lineStr.replace("OUSTVINLIC \t", "");
                            lineStr = lineStr.replace("OUSTVINLIO", "");
                            lineStr = lineStr.replace("OUSTVINLIC", "");
                            lineStr = lineStr.replaceAll("<br />", "\n");
                            editable.append(lineStr);
                            if (i < split.length) {
                                editable.append("\n");
                            }
                        }
                    }
                }
            } else {
                editable.append(s1);
            }
            return editable.toString();
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
            return s1;
        }
    }


    //    textToSpeechInitialization
    public static TextToSpeech textToSpeech;
    public static TextToSpeech textToSpeechHindi;

    public static void speakInit() {
        try {
            if (isPackageInstalled(OustSdkApplication.getContext().getPackageManager(), "com.google.android.tts")) {
                OustPreferences.saveAppInstallVariable("isttsfileinstalled", true);
                if (textToSpeech == null) {
                    textToSpeech = new TextToSpeech(OustSdkApplication.getContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if (status != TextToSpeech.ERROR) {
                                if (status == TextToSpeech.SUCCESS) {
                                    int result = textToSpeech.setLanguage(new Locale("en", "IND", "variant"));
                                    textToSpeech.setSpeechRate(0.8f);
                                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                                        result = textToSpeech.setLanguage(Locale.getDefault());
                                    }
                                } else {
                                    Log.e("TTS", "Initilization Failed!");
                                }
                            }
                        }
                    }, "com.google.android.tts");
                }
                if (textToSpeechHindi == null) {
                    textToSpeechHindi = new TextToSpeech(OustSdkApplication.getContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int i) {
                            if (i != TextToSpeech.ERROR) {
                                if (i == TextToSpeech.SUCCESS) {
                                    int result = textToSpeechHindi.setLanguage(new Locale("hin", "IND", "variant"));
                                    textToSpeechHindi.setSpeechRate(0.8f);
                                } else {
                                    Log.e("TTS", "Initilization Failed!");
                                }
                            }
                        }
                    }, "com.google.android.tts");
                }
            } else {
                OustPreferences.saveAppInstallVariable("isttsfileinstalled", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
    }

    public static boolean isPackageInstalled(PackageManager pm, String packageName) {
        try {
            pm.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    public static TextToSpeech getSpeechEngin() {
        if (textToSpeech == null) {
            speakInit();
        }
        return textToSpeech;
    }

    public static TextToSpeech getHindiSpeechEngin() {
        if (textToSpeechHindi == null) {
            speakInit();
        }
        return textToSpeechHindi;
    }

    public static void stopSpeech() {
        try {
            Log.e("SPEECH", "stopSpeech() called");
            if (textToSpeech != null) {
                Log.e("SPEECH", "stopSpeech() textToSpeech!=null");
                textToSpeech.stop();
                textToSpeech.shutdown();
            }
            if (textToSpeechHindi != null) {
                Log.e("SPEECH", "stopSpeech() textToSpeechHindi!=null");
                textToSpeechHindi.stop();
                textToSpeechHindi.shutdown();
            }
        } catch (Exception e) {
            Log.e("SPEECH", "stopSpeech() exception occured", e);
            e.printStackTrace();
            sendSentryException(e);
        }
    }

    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    public static CommonResponse getCommonResponse(String jsonString) {
        if ((jsonString != null) && (!jsonString.isEmpty())) {
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(jsonString, CommonResponse.class);
        } else {
            return null;
        }
    }

    public static void createApplicationFolder() {
        File f = new File(Environment.getExternalStorageDirectory(), File.separator + Config.VIDEO_COMPRESSOR_APPLICATION_DIR_NAME);
        f.mkdirs();
        f = new File(Environment.getExternalStorageDirectory(), File.separator + Config.VIDEO_COMPRESSOR_APPLICATION_DIR_NAME + Config.VIDEO_COMPRESSOR_COMPRESSED_VIDEOS_DIR);
        f.mkdirs();
        f = new File(Environment.getExternalStorageDirectory(), File.separator + Config.VIDEO_COMPRESSOR_APPLICATION_DIR_NAME + Config.VIDEO_COMPRESSOR_TEMP_DIR);
        f.mkdirs();
    }

    public static void newActivityAnimationF(Intent intent, Context context) {
        try {
            int sdk = Build.VERSION.SDK_INT;
            if (sdk > Build.VERSION_CODES.JELLY_BEAN) {
                Bundle translateBundle = ActivityOptions.makeCustomAnimation(context, R.anim.anim_slidein, R.anim.anim_quizout).toBundle();
                context.startActivity(intent, translateBundle);
            } else {
                context.startActivity(intent);
            }
        } catch (Exception e) {
            context.startActivity(intent);
            e.printStackTrace();
            sendSentryException(e);
        }
    }

    @SuppressLint("NewApi")
    public static String encodeBase64(String plainText) {
        byte[] data = new byte[0];
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                data = plainText.getBytes(StandardCharsets.UTF_8);
            } else {
                data = plainText.getBytes("UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
        String base64 = Base64.encodeToString(data, Base64.DEFAULT);
        return base64;
    }

    @SuppressLint("NewApi")
    public static String decodeBase64(String base64) {
        byte[] data = Base64.decode(base64, Base64.DEFAULT);
        String text = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                text = new String(data, StandardCharsets.UTF_8);
            } else {
                text = new String(data, "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
        return text;
    }

    public static void removeTempProfile() {
        try {
            tempProfile = null;
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
    }

    public static void loadDataFromHtml(String option, TextView textView) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                textView.setText(Html.fromHtml(option, Html.FROM_HTML_MODE_COMPACT));
            } else {
                textView.setText(Html.fromHtml(option));
            }
        } catch (Exception e) {
            getSpannedContent(option, textView);
            e.printStackTrace();
            sendSentryException(e);
        }
    }

    public PaymentGetRSAMobileResponseData getPaymentGetRSAMobileResponseData(String jsonString) {
        if ((jsonString != null) && (!jsonString.isEmpty())) {
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(jsonString, PaymentGetRSAMobileResponseData.class);
        } else {
            return null;
        }
    }

    public static void clearAlldataAndlogout() {
        try {
            if (tempProfile != null) {
                tempProfile = null;
            }
            String gcmToken = OustPreferences.get("gcmToken");
            String city = OustPreferences.get("eventusercity");
            String countryCode = OustPreferences.get("countryCode");
//            List<String> requests= OustPreferences.getLoacalNotificationMsgs("savedSubmitRequest");

            int languageStatus = OustPreferences.getSavedInt("languageStatus");
            List<String> stringList = OustPreferences.getLoacalNotificationMsgs("saveduserdatalist");
            if (stringList == null) {
                stringList = new ArrayList<>();
            }

            boolean settingChallenge = OustPreferences.getAppInstallVariable("challengeNotificationDisable");
            boolean settingFriendC = OustPreferences.getAppInstallVariable("addFriendNotificationDisable");
            boolean settingContact = OustPreferences.getAppInstallVariable("contactDisabled");
            boolean settingOther = OustPreferences.getAppInstallVariable("otherNotificationDisable");
            boolean settingAll = OustPreferences.getAppInstallVariable("allNotificationDisable");
            boolean allresourcesDownloadeda = OustPreferences.getAppInstallVariable("allresourcesDownloadeda");
            OustFirebaseTools.resetFirebase();
            OustPreferences.clearAll();
            try {
                OustPreferences.saveAppInstallVariable("askedcityname", true);
                OustPreferences.saveAppInstallVariable("isBranchCheck", true);
                OustPreferences.saveAppInstallVariable("initalarm", true);
                OustPreferences.saveAppInstallVariable("neverrateus", true);
                OustPreferences.saveintVar("rateusstatus", 2);
                OustPreferences.saveAppInstallVariable("allresourcesDownloadeda", allresourcesDownloadeda);

                if (languageStatus > 0) {
                    OustPreferences.saveintVar("languageStatus", languageStatus);
                }
                OustPreferences.saveLocalNotificationMsg("saveduserdatalist", stringList);
                if (gcmToken != null) {
                    OustPreferences.save("gcmToken", gcmToken);
                }
                if (city != null) {
                    OustPreferences.save("eventusercity", city);
                }
                if (countryCode != null) {
                    OustPreferences.save("countryCode", countryCode);
                }
                OustPreferences.saveAppInstallVariable("issounddisable", OustAppState.getInstance().isSoundDisabled());
                OustPreferences.saveAppInstallVariable("challengeNotificationDisable", settingChallenge);
                OustPreferences.saveAppInstallVariable("contactDisabled", settingContact);
                OustPreferences.saveAppInstallVariable("addFriendNotificationDisable", settingFriendC);
                OustPreferences.saveAppInstallVariable("otherNotificationDisable", settingOther);
                OustPreferences.saveAppInstallVariable("allNotificationDisable", settingAll);
                OustPreferences.saveTimeForNotification("VideoOverlayCardCurrentPositionTime", 0);
                OustPreferences.saveAppInstallVariable("IsAssessmentPlaying", false);
//                if((requests!=null)&&(requests.size()>0)){
//                    OustPreferences.saveLocalNotificationMsg("savedSubmitRequest", requests);
//                }
            } catch (Exception e) {
                OustPreferences.clearAll();
                Log.e(TAG, "message" + e.getMessage());
                e.printStackTrace();
                sendSentryException(e);
            }
            OustPreferences.saveAppInstallVariable("initFirebaseTokenAlarm", true);
            //HttpManager.setBaseUrl();
            //OustFirebaseTools.initFirebase();
            OustAppState.getInstance().clearAll();
            UserBalanceState.getInstance().clearAll();
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
    }


    static File file;

    public static File getDataFromPrivateStorage(String fileName) {
        Log.d(TAG, "getDataFromPrivateStorage: " + fileName);
        String temp = null;
//        try {
//            EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
//            temp = enternalPrivateStorage.readSavedData("oustlearn_" + fileName);
//            byte[] pdfbyte = Base64.decode(temp, 0);
//            FileOutputStream outputStream = new FileOutputStream((new File(Environment.getExternalStorageDirectory(), "newfile.pdf")));
//            outputStream.write(pdfbyte);
//            outputStream.close();
//            file = new File(Environment.getExternalStorageDirectory(), "newfile.pdf");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        File sdfile = new File(OustSdkApplication.getContext().getFilesDir(), "oustlearn_" + fileName);
        if (sdfile.exists()) {
            FileInputStream inStream = null;
            try {
                byte[] pdfbyte = FileUtils.readFileToByteArray(sdfile);
                FileOutputStream outputStream = new FileOutputStream((new File(OustSdkApplication.getContext().getFilesDir(), "newfile.pdf")));
                outputStream.write(pdfbyte);
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                sendSentryException(e);
            }
            file = new File(OustSdkApplication.getContext().getFilesDir(), "newfile.pdf");

            Log.e("PDF", "if file exists? " + file.exists());
        }
        return file;
    }


    public static void handlePopup(CommonResponse commonResponse) {
        if (commonResponse != null) {
            if (!commonResponse.isSuccess()) {
                if (commonResponse.getPopup() != null) {
                    OustStaticVariableHandling.getInstance().setOustpopup(commonResponse.getPopup());
                    Intent intent = new Intent(OustSdkApplication.getContext(), PopupActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    OustSdkApplication.getContext().startActivity(intent);
                } else if ((commonResponse.getError() != null) && (!commonResponse.getError().isEmpty())) {
                    showToast(commonResponse.getError());
                } else {
                    showToast("Internal server error");
                }
            }
        }
    }

    public static JSONObject appendDeviceAndAppInfoInQueryParam() {
        JSONObject appMap = new JSONObject();
        try {
            PackageInfo pinfo = OustSdkApplication.getContext().getPackageManager().getPackageInfo(OustSdkApplication.getContext().getPackageName(), 0);
            appMap.put("appVersion", pinfo.versionName);
            appMap.put("devicePlatformName", "Android");
            String uuid = Settings.Secure.getString(OustSdkApplication.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            if ((uuid != null) && (!uuid.isEmpty())) {
                appMap.put("deviceIdentity", uuid);
            }
            if ((OustPreferences.get("gcmToken") != null)) {
                appMap.put("deviceToken", OustPreferences.get("gcmToken"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }

        return appMap;
    }

    public static HashMap appendDeviceAndAppInfoInQueryParam2() {
        HashMap appMap = new HashMap();
        try {
            PackageInfo pinfo = OustSdkApplication.getContext().getPackageManager().getPackageInfo(OustSdkApplication.getContext().getPackageName(), 0);
            appMap.put("appVersion", pinfo.versionName);
            appMap.put("devicePlatformName", "Android");
            String uuid = Settings.Secure.getString(OustSdkApplication.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            if ((uuid != null) && (!uuid.isEmpty())) {
                appMap.put("deviceIdentity", uuid);
            }
            if ((OustPreferences.get("gcmToken") != null)) {
                appMap.put("deviceToken", OustPreferences.get("gcmToken"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }

        return appMap;
    }

    public static ActiveGame getAcceptChallengeData(String jsonString) {
        if ((jsonString != null) && (!jsonString.isEmpty())) {
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(jsonString, ActiveGame.class);
        } else {
            return null;
        }
    }

    public static SubmitRequest getSubmit(String jsonString) {
        if ((jsonString != null) && (!jsonString.isEmpty())) {
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(jsonString, SubmitRequest.class);
        } else {
            return null;
        }
    }

    public static void setImage(ImageView imageView, String imageName) {
        if (imageView != null) {
            try {
                File file = new File(OustSdkApplication.getContext().getFilesDir(), imageName);
                //Log.d(TAG, "setImage: "+file.toString());
                if (file.exists()) {
                    Picasso.get().load(file).into(imageView);
                    //Picasso.get().load(file).into(imageView);
                } else {
                    setPicassoImage(imageView, imageName);
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendSentryException(e);
            }
        }
    }

    public static void setImage(CircleImageView imageView, String imageName) {
        if (imageView != null) {
            try {
                File file = new File(OustSdkApplication.getContext().getFilesDir(), imageName);
                if (file.exists()) {
                    Picasso.get().load(file).into(imageView);
                    //Picasso.get().load(file).into(imageView);
                } else {
                    setPicassoImage(imageView, imageName);
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendSentryException(e);
            }
        }
    }

    public static void setImageA(ImageView imageView, String imageName) {
        if (imageView != null) {
            try {
                if (imageName != null && !imageName.isEmpty()) {
                    File file = new File(OustSdkApplication.getContext().getFilesDir(), imageName);
                    if (file.exists()) {
                        Picasso.get().load(file).into(imageView);
                        //Picasso.get().load(file).into(imageView);
                    } else {
                        setPicassoImage(imageView, imageName);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendSentryException(e);
            }
        }
    }

    public static void setBackground(final View view, String imageName) {
        try {
            File file = new File(OustSdkApplication.getContext().getFilesDir(), imageName);

            /*Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    BitmapDrawable ob = new BitmapDrawable(OustSdkApplication.getContext().getResources(), bitmap);
                    int sdk = Build.VERSION.SDK_INT;
                    if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                        view.setBackgroundDrawable(ob);
                    } else {
                        view.setBackground(ob);
                    }
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };

            Picasso.get().load(file).into(target);*/

            Picasso.get().load(file).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    BitmapDrawable ob = new BitmapDrawable(OustSdkApplication.getContext().getResources(), bitmap);
                    int sdk = Build.VERSION.SDK_INT;
                    if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                        view.setBackgroundDrawable(ob);
                    } else {
                        view.setBackground(ob);
                    }
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
    }


    private static void setPicassoImage(ImageView imageView, String imageName) {
        try {
            String url = AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL + "AppResources/Android/All/Images/" + imageName;
            Picasso.get().load(url).into(imageView);

            //Picasso.with(OustSdkApplication.getContext()).load(url).into(imageView);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            sendSentryException(e);
        }
    }

    public static void LoadNormalImageFromPicasso(ImageView imageView, String imageName) {
        try {
            Picasso.get().load(imageName).into(imageView);
            //Picasso.get().load(imageName).into(imageView);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            sendSentryException(e);
        }
    }

    public static void LoadCircleImageFromPicasso(CircleImageView imageView, String imageName) {
        try {
            Picasso.get().load(imageName).into(imageView);
            //Picasso.get().load(imageName).into(imageView);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            sendSentryException(e);
        }
    }

    public static BitmapDrawable getImageDrawable(String imageName) {
        BitmapDrawable bd = null;
        try {
            File file = new File(OustSdkApplication.getContext().getFilesDir(), imageName);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
            if (bitmap != null)
                bd = new BitmapDrawable(OustSdkApplication.getContext().getResources(), bitmap);
        } catch (Exception | OutOfMemoryError e) {
            e.printStackTrace();
            Sentry.captureException(e);
            //sendSentryException(e);
            Log.i("memory leak ", e.toString());
        }
        return bd;
    }

    public static URL stringToURL(String urlString) {
        try {
            return new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            sendSentryException(e);
        }
        return null;
    }

    public static File getImageFile(String imageName) {
        File file = new File(OustSdkApplication.getContext().getFilesDir(), imageName);
        if (file.exists()) {
            return file;
        }
        return null;
    }

    public static boolean isImageAvailable(String icon) {
        String[] dirs = icon.split("/");
        String imageName = "";
        if (dirs.length > 0)
            imageName = dirs[dirs.length - 1];
        else
            imageName = icon;

        File file = new File(OustSdkApplication.getContext().getFilesDir(), imageName);
        if (file.exists()) {
//            Log.d(TAG, "isImageAvailable: " + icon + "");
            return true;
        }
        return false;
    }

    public static void setDrawableToImageView(ImageView imageView, BitmapDrawable bd) {
        if (imageView != null && bd != null) {
            int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                imageView.setBackgroundDrawable(bd);
            } else {
                imageView.setBackground(bd);
            }
        }
    }

    public static void setDownloadGifImage(GifImageView imageView) {
        try {
            File file = new File(OustSdkApplication.getContext().getFilesDir(), "white_to_green_new.gif");
            Uri uri = Uri.fromFile(file);
            imageView.setImageURI(uri);
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
    }

    public static void setDownloadGifImage(ImageView imageView) {
        try {
            File file = new File(OustSdkApplication.getContext().getFilesDir(), "white_to_green_new.gif");
            Uri uri = Uri.fromFile(file);
            imageView.setImageURI(uri);
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        Pattern EMAIL_ADDRESS
                = Pattern.compile(
                "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                        "\\@" +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                        "(" +
                        "\\." +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                        ")+");
        return EMAIL_ADDRESS.matcher(target).matches();
    }


    public static FastestFingerContestData getFastestFingerContestData(FastestFingerContestData fastestFingerContestData, Map<String, Object> ffcDataMap) {
        try {
            if (ffcDataMap.get("name") != null) {
                fastestFingerContestData.setName((String) ffcDataMap.get("name"));
            }
            if (ffcDataMap.get("description") != null) {
                fastestFingerContestData.setDescription((String) ffcDataMap.get("description"));
            }
            if (ffcDataMap.get("instructions") != null) {
                fastestFingerContestData.setInstructions((String) ffcDataMap.get("instructions"));
            }
            if (ffcDataMap.get("startTime") != null) {
                fastestFingerContestData.setStartTime((long) ffcDataMap.get("startTime"));
            }
            if (ffcDataMap.get("endTime") != null) {
                fastestFingerContestData.setEndTime((long) ffcDataMap.get("endTime"));
            }
            if (ffcDataMap.get("questionTime") != null) {
                fastestFingerContestData.setQuestionTime(((long) ffcDataMap.get("questionTime")));
            }
            if (ffcDataMap.get("restTime") != null) {
                fastestFingerContestData.setRestTime((long) ffcDataMap.get("restTime"));
            }
            if (ffcDataMap.get("updateChecksum") != null) {
                fastestFingerContestData.setUpdateChecksum((long) ffcDataMap.get("updateChecksum"));
            }
            if (ffcDataMap.get("banner") != null) {
                fastestFingerContestData.setJoinBanner((String) ffcDataMap.get("banner"));
            }
            if (ffcDataMap.get("playBanner") != null) {
                fastestFingerContestData.setPlayBanner((String) ffcDataMap.get("playBanner"));
            }
            if (ffcDataMap.get("reviewResultBanner") != null) {
                fastestFingerContestData.setRrBanner((String) ffcDataMap.get("reviewResultBanner"));
            }

            if (ffcDataMap.get("terms") != null) {
                fastestFingerContestData.setTerms((String) ffcDataMap.get("terms"));
            }
            if (ffcDataMap.get("userCount") != null) {
                fastestFingerContestData.setUserCount((long) ffcDataMap.get("userCount"));
            }
            if (ffcDataMap.get("bgImage") != null) {
                fastestFingerContestData.setBgImage((String) ffcDataMap.get("bgImage"));
            }
            if (ffcDataMap.get("questionTxtColor") != null) {
                fastestFingerContestData.setQuestionTxtColor((String) ffcDataMap.get("questionTxtColor"));
            }
            if (ffcDataMap.get("choiceTxtColor") != null) {
                fastestFingerContestData.setChoiceTxtColor((String) ffcDataMap.get("choiceTxtColor"));
            }

            if (ffcDataMap.get("choiceBgColorLight") != null) {
                fastestFingerContestData.setChoiceBgColorLight((String) ffcDataMap.get("choiceBgColorLight"));
            }
            if (ffcDataMap.get("choiceBgColorDark") != null) {
                fastestFingerContestData.setChoiceBgColorDark((String) ffcDataMap.get("choiceBgColorDark"));
            }
            if (ffcDataMap.get("choiceBgColorSelectedLight") != null) {
                fastestFingerContestData.setChoiceBgColorSelectedLight((String) ffcDataMap.get("choiceBgColorSelectedLight"));
            }
            if (ffcDataMap.get("choiceBgColorSelectedDark") != null) {
                fastestFingerContestData.setChoiceBgColorSelectedDark((String) ffcDataMap.get("choiceBgColorSelectedDark"));
            }

            if (ffcDataMap.get("progressBarColor") != null) {
                fastestFingerContestData.setProgressBarColor((String) ffcDataMap.get("progressBarColor"));
            }
            if (ffcDataMap.get("choiceBgColorLight") != null) {
                fastestFingerContestData.setChoiceBgColorLight((String) ffcDataMap.get("choiceBgColorLight"));
            }

            if (ffcDataMap.get("btnColorTop") != null) {
                fastestFingerContestData.setBtnColorTop((String) ffcDataMap.get("btnColorTop"));
            }
            if (ffcDataMap.get("btnColorBottom") != null) {
                fastestFingerContestData.setBtnColorBottom((String) ffcDataMap.get("btnColorBottom"));
            }

            if (ffcDataMap.get("btnTextColor") != null) {
                fastestFingerContestData.setBtnTextColor((String) ffcDataMap.get("btnTextColor"));
            }
            if (ffcDataMap.get("playBtnText") != null) {
                fastestFingerContestData.setPlayBtnText((String) ffcDataMap.get("playBtnText"));
            }

            if (ffcDataMap.get("registerBtnText") != null) {
                fastestFingerContestData.setRegisterBtnText((String) ffcDataMap.get("registerBtnText"));
            }
            if (ffcDataMap.get("warmupBtnText") != null) {
                fastestFingerContestData.setWarmupBtnText((String) ffcDataMap.get("warmupBtnText"));
            }
            if (ffcDataMap.get("hideConfirmButtom") != null) {
                fastestFingerContestData.setHideConfirmButtom((boolean) ffcDataMap.get("hideConfirmButtom"));
            }
            if (ffcDataMap.get("overallLBBtnText") != null) {
                fastestFingerContestData.setOverallLBBtnText((String) ffcDataMap.get("overallLBBtnText"));
            }
            if (ffcDataMap.get("questionLBBtnText") != null) {
                fastestFingerContestData.setQuestionLBBtnText((String) ffcDataMap.get("questionLBBtnText"));
            }
            if (ffcDataMap.get("constructingLBMessage") != null) {
                fastestFingerContestData.setConstructingLBMessage((String) ffcDataMap.get("constructingLBMessage"));
            }
            if (ffcDataMap.get("warmupSwitchTime") != null) {
                fastestFingerContestData.setWarmupSwitchTime((long) ffcDataMap.get("warmupSwitchTime"));
            }
            if (ffcDataMap.get("enableMusic") != null) {
                fastestFingerContestData.setEnableMusic((boolean) ffcDataMap.get("enableMusic"));
            }
            if (ffcDataMap.get("constructingLBTime") != null) {
                fastestFingerContestData.setConstructingLBTime((long) ffcDataMap.get("constructingLBTime"));
            }
            if (ffcDataMap.get("intermediateMessages") != null) {
                fastestFingerContestData.setIntermediateMessages((List<String>) ffcDataMap.get("intermediateMessages"));
            }
            if (ffcDataMap.get("termsLabel") != null) {
                fastestFingerContestData.setTermsLabel((String) ffcDataMap.get("termsLabel"));
            }
            if (ffcDataMap.get("noLBDataMessage") != null) {
                fastestFingerContestData.setNoLBDataMessage((String) ffcDataMap.get("noLBDataMessage"));
            }
            if (ffcDataMap.get("noInternetMsg") != null) {
                fastestFingerContestData.setNoInternetMsg((String) ffcDataMap.get("noInternetMsg"));
            }
            if (ffcDataMap.get("leaderboardToppersCount") != null) {
                fastestFingerContestData.setLeaderboardToppersCount((long) ffcDataMap.get("leaderboardToppersCount"));
            }
            if (ffcDataMap.get("leaderboardInfo") != null) {
                fastestFingerContestData.setLeaderboardInfo((String) ffcDataMap.get("leaderboardInfo"));
            }
            if (ffcDataMap.get("luckyWinnerInfo") != null) {
                Map<String, Object> luckyWinnerInfoMap = (Map<String, Object>) ffcDataMap.get("luckyWinnerInfo");
                if (luckyWinnerInfoMap != null) {
                    if (luckyWinnerInfoMap.get("message") != null) {
                        fastestFingerContestData.setLuckyWinnerInfoText((String) luckyWinnerInfoMap.get("message"));
                    }
                }
            }
            if (ffcDataMap.get("rewardWinnerInfo") != null) {
                Map<String, Object> rewardWinnerInfoMap = (Map<String, Object>) ffcDataMap.get("rewardWinnerInfo");
                if (rewardWinnerInfoMap != null) {
                    if (rewardWinnerInfoMap.get("count") != null) {
                        fastestFingerContestData.setRewardWinnerCount((long) rewardWinnerInfoMap.get("count"));
                    }
                    if (rewardWinnerInfoMap.get("popupContent") != null) {
                        fastestFingerContestData.setRewardWinnerContent((String) rewardWinnerInfoMap.get("popupContent"));
                    }
                    if (rewardWinnerInfoMap.get("icon") != null) {
                        fastestFingerContestData.setRewardWinnerIcon((String) rewardWinnerInfoMap.get("icon"));
                    }
                }
            }
            if (ffcDataMap.get("luckyWinnerCorrectCount") != null) {
                fastestFingerContestData.setLuckyWinnerCorrectCount((long) ffcDataMap.get("luckyWinnerCorrectCount"));
            }
            if (ffcDataMap.get("winnerInfo") != null) {
                fastestFingerContestData.setWinnerInfo((String) ffcDataMap.get("winnerInfo"));
            }
            if (ffcDataMap.get("rewardImage") != null) {
                fastestFingerContestData.setRewardImage((String) ffcDataMap.get("rewardImage"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
        return fastestFingerContestData;
    }


    public static String getEmojiEncodedString(EditText editText) {
        String str = editText.getText().toString().trim();
        try {
            if (!str.isEmpty()) {
                str = StringEscapeUtils.escapeJava(str);
                //str=str.replace("\\u","\\\\u");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
        return str;
    }

    public static String getEmojiDecodedString(String str) {
        try {
            if ((str != null) && (!str.isEmpty())) {
                // str=str.replace("\\\\u","\\u");
                str = StringEscapeUtils.unescapeJava(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
        return str;
    }

    public static long convertToLong(Object O) {
        try {
            if (O != null) {
                if (O.getClass().equals(Long.class)) {
                    return (long) O;
                } else if (O.getClass().equals(Integer.class)) {
                    return Long.valueOf("" + O);
                } else if (O.getClass().equals(String.class)) {
                    return Long.parseLong((String) O);
                } else if (O.getClass().equals(Double.class)) {
                    double d = (double) O;
                    return (long) d;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
        return 0;
    }

    public static long newConvertToLong(Object O) {
        try {
            if (O != null) {
                if (O.getClass().equals(Long.class)) {
                    return (long) O;
                } else if (O.getClass().equals(Integer.class)) {
                    return Long.valueOf("" + O);
                } else if (O.getClass().equals(String.class)) {
                    return Long.parseLong((String) O);
                } else if (O.getClass().equals(Double.class)) {
                    double d = (double) O;
                    return (long) d;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
        return 0;
    }

    public static String convertToStr(Object O) {
        try {
            if (O == null) {
                return null;
            }
            if (O.getClass().equals(String.class)) {
                return (String) O;
            } else if (O.getClass().equals(Long.class)) {
                return (long) O + "";
            } else if (O.getClass().equals(Double.class)) {
                return (double) O + "";
            } else if (O.getClass().equals(Boolean.class)) {
                return (boolean) O + "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
        return null;
    }

    public static boolean convertToBoolean(Object O) {
        try {
            if (O == null) {
                return false;
            }
            if (O.getClass().equals(Boolean.class)) {
                return (boolean) O;
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
        return false;
    }

    public static String timeStampToDate(long timestamp) {
        Format formatter = new SimpleDateFormat("dd MMM yyyy");
        String s = formatter.format(new Date(timestamp));
        return s;
    }

    public static String timeStampToDateDDMMYYYY(long timestamp) {
        // Log.d(TAG, "timeStampToDateDDMMYYYY: "+timestamp);
        Format formatter = new SimpleDateFormat("dd/MM/yyyy");
        String s = formatter.format(new Date(timestamp));
        return s;
    }

    public static String timeFormatForWD(long timestamp) {
        // Log.d(TAG, "timeStampToDateDDMMYYYY: "+timestamp);
        Format formatter = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String s = formatter.format(new Date(timestamp));
        return s;
    }

    public static String getDate(String millis) {
        //Log.d(TAG, "getDate: "+millis);
        String date = getFeedDate(Long.parseLong(millis));
        //Log.d(TAG, "getDate after conver: "+Long.parseLong(millis));
        return date;
    }

    public static String getDateTimeFromMilli(long timeStamp) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        } catch (Exception ex) {
            return "xx";
        }
    }


    public static String getDateTimeFromMilli2(long timeStamp) {
        try {
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
            Date now = new Date();
            String strDate = sdfDate.format(now);
            return strDate;
        } catch (Exception ex) {
            return "xx";
        }
    }

    public static long getFileSize(File file) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.close();
        return file.length();
    }

    public static boolean fileCopy(File dest, File source) {
        try {
            FileUtils.copyFile(source, dest);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            sendSentryException(e);
            return false;
        }
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
            if (sourceFile.exists()) {
                sourceFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    private static String getFeedDate(long createdAt) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

        String note_date = "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(createdAt);

        String currentDateTimeString = sdf.format(calendar.getTime());

        Calendar todayCal = Calendar.getInstance();
        if (todayCal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
            if (todayCal.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)) {
                if (todayCal.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)) {
                    return OustSdkApplication.getContext().getResources().getString(R.string.today_text) + " " + " " + currentDateTimeString;
                } else {
                    if (todayCal.get(Calendar.WEEK_OF_MONTH) == calendar.get(Calendar.WEEK_OF_MONTH)) {
                        return getWeekDay(calendar.get(Calendar.DAY_OF_WEEK)) + " " + currentDateTimeString;
                    } else {
                        return calendar.get(Calendar.DAY_OF_MONTH) + " " + getMonth(calendar.get(Calendar.MONTH)) + " " + currentDateTimeString;
                    }
                }
            } else {
                return calendar.get(Calendar.DAY_OF_MONTH) + " " + getMonth(calendar.get(Calendar.MONTH)) + " " + currentDateTimeString;
            }
        } else {
            return calendar.get(Calendar.DAY_OF_MONTH) + " " + getMonth(calendar.get(Calendar.MONTH)) + " " + currentDateTimeString;
        }
    }

    /**
     * returns name of day
     *
     * @param i is integer 0-6
     * @return name of the day
     */
    private static String getWeekDay(int i) {
        String day = "";
        switch (i) {
            case 1:
                day = "Sunday";
                break;
            case 2:
                day = "Monday";
                break;
            case 3:
                day = "Tuesday";
                break;
            case 4:
                day = "Wednesday";
                break;
            case 5:
                day = "Thursday";
                break;
            case 6:
                day = "Friday";
                break;
            case 7:
                day = "Saturday";
                break;

        }
        return day;
    }

    /**
     * returns mpnth of day
     *
     * @param i is integer 0-11
     * @return name of the day
     */
    private static String getMonth(int i) {
        String month = "";
        switch (i) {
            case 0:
                month = "Jan";
                break;
            case 1:
                month = "Feb";
                break;
            case 2:
                month = "Mar";
                break;
            case 3:
                month = "Apr";
                break;
            case 4:
                month = "May";
                break;
            case 5:
                month = "Jun";
                break;
            case 6:
                month = "Jul";
                break;
            case 7:
                month = "Aug";
                break;
            case 8:
                month = "Sep";
                break;
            case 9:
                month = "Oct";
                break;
            case 10:
                month = "Nov";
                break;
            case 11:
                month = "Dec";
                break;
        }
        return month;
    }

    public static boolean isCurrentQuater(String dateInMilisecond) {

        try {
            Calendar cal = Calendar.getInstance();
            int currentYear = cal.get(Calendar.YEAR);
            int currentMonth = cal.get(Calendar.MONTH);
            int currentQuater = ((currentMonth - 1) / 3) + 1;

            cal.setTimeInMillis(Long.parseLong(dateInMilisecond));
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int quater = ((month - 1) / 3) + 1;

            return currentYear == year && currentQuater == quater;
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
        return false;
    }

    public static boolean isCurrentMonth(String dateInMilisecond) {
        try {
            Calendar cal = Calendar.getInstance();
            int currentYear = cal.get(Calendar.YEAR);
            int currentMonth = cal.get(Calendar.MONTH);
            cal.setTimeInMillis(Long.parseLong(dateInMilisecond));
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            return currentYear == year && currentMonth == month;
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
        return false;
    }

    public static boolean isCurrentYear(String dateInMilisecond) {
        try {
            Calendar cal = Calendar.getInstance();
            int currentYear = cal.get(Calendar.YEAR);
            cal.setTimeInMillis(Long.parseLong(dateInMilisecond));
            int year = cal.get(Calendar.YEAR);
            return currentYear == year;
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
        return false;
    }

    public static ActivityManager.MemoryInfo getAvailableMemory(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }

    public static class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable mDivider;

        public SimpleDividerItemDecoration(Context context) {
            mDivider = context.getResources().getDrawable(R.drawable.line_divider);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }

    private static String[] suffix = new String[]{"", "K", "M", "B", "T"};
    private static int MAX_LENGTH = 4;

    public static String formatMilliinFormat1(double number) {
        String r = new DecimalFormat("##0E0").format(number);
        r = r.replaceAll("E[0-9]", suffix[Character.getNumericValue(r.charAt(r.length() - 1)) / 3]);
        while (r.length() > MAX_LENGTH || r.matches("[0-9]+\\.[a-z]")) {
            r = r.substring(0, r.length() - 2) + r.substring(r.length() - 1);
        }
        return r;
    }

    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();

    static {
        suffixes.put(1_000L, "K");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    public static String formatMilliinFormat(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return formatMilliinFormat(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + formatMilliinFormat(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return (truncated / 10d) + suffix;
    }

    private static char[] c = new char[]{'K', 'M', 'B', 'T'};


    public static String formatMilliinFormat1(long value) {
        return coolFormat(value, 0);
    }

    private static String coolFormat(double n, int iteration) {
        double d = ((long) n / 100) / 10.0;
        boolean isRound = (d * 10) % 10 == 0;//true if the decimal part is equal to 0 (then it's trimmed anyway)
        return (d < 1000 ? //this determines the class, i.e. 'k', 'm' etc
                ((d > 99.9 || isRound || (!isRound && d > 9.99) ? //this decides whether to trim the decimals
                        (int) d * 10 / 10 : d + "" // (int) d * 10 / 10 drops the decimal
                ) + "" + c[iteration])
                : coolFormat(d, iteration + 1));

    }

    public static BranchIoResponce decryptBranchData(BranchIoResponce branchIoResponce, Cipher cipher) {
        BranchIOEncryptedResponce branchIOEncryptedResponce = new BranchIOEncryptedResponce();
        try {
            if (cipher == null) {
                String s1 = "2kqwpC76oazdJnCI";
                Key key = new SecretKeySpec(s1.getBytes(UNICODE_FORMAT), AES_KEY);
                cipher = Cipher.getInstance(AES_KEY);
                cipher.init(Cipher.DECRYPT_MODE, key);
            }
            Gson gson = new GsonBuilder().create();
            String dataToDecrypt = branchIoResponce.getEncryptedData();
            dataToDecrypt = dataToDecrypt.replace(",type=0", "");
            byte[] base64TextToDecrypt = Base64.decode(dataToDecrypt, Base64.DEFAULT);
            byte[] decryptedByte = cipher.doFinal(base64TextToDecrypt);
            String decryptedText = new String(decryptedByte);
            branchIOEncryptedResponce = gson.fromJson(decryptedText, BranchIOEncryptedResponce.class);
            branchIoResponce.setOrgId(branchIOEncryptedResponce.getOrgId());
            branchIoResponce.setUserId(branchIOEncryptedResponce.getUserId());
            branchIoResponce.setPassword(branchIOEncryptedResponce.getPassword());
            branchIoResponce.setFname(branchIOEncryptedResponce.getFname());
            branchIoResponce.setLname(branchIOEncryptedResponce.getLname());
            branchIoResponce.setEmailId(branchIOEncryptedResponce.getEmailid());
            branchIoResponce.setMobileNum(branchIOEncryptedResponce.getMobile());
            branchIoResponce.setRmEmailid(branchIOEncryptedResponce.getRmEmailid());
            branchIoResponce.setProfileImage(branchIOEncryptedResponce.getProfileImage());

            branchIoResponce.setApplication(branchIOEncryptedResponce.getApplication());
            branchIoResponce.setApplicationId(branchIOEncryptedResponce.getApplicationId());

            branchIoResponce.setAppVersion(branchIOEncryptedResponce.getAppVersion());
            branchIoResponce.setUserAgent(branchIOEncryptedResponce.getUserAgent());

            branchIoResponce.setDeviceId(branchIOEncryptedResponce.getDeviceId());
            branchIoResponce.setTanentId(branchIOEncryptedResponce.getTokenId());

            branchIoResponce.setAuthorizationReq(branchIOEncryptedResponce.getAuthorizationReq());

        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
            //OustFlurryTools.getInstance().LogFlurryErrorEvent(OustTools.class.getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
        return branchIoResponce;
    }

    public static void setCustomBackgroundViewWithBorderColor(final View v, int borderColor) {
        Log.d(TAG, "setCustomBackgroundViewWithBorderColor: ");
        try {
            GradientDrawable shape = new GradientDrawable();
            Context ctx = OustSdkApplication.getContext();
            float dimension = ctx.getResources().getDimension(R.dimen.oustlayout_dimen8);
            int backgroundColor = ctx.getResources().getColor(R.color.whitea);
            int strokeWidth = (int) ctx.getResources().getDimension(R.dimen.oustlayout_dimen1);

            shape.setShape(GradientDrawable.RECTANGLE);
            shape.setCornerRadius(dimension);
            shape.setColor(backgroundColor);
            shape.setStroke(strokeWidth, borderColor);
            v.setBackground(shape);
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
    }

    public static void setCustomBackgroundViewDefault(final View v) {
        Log.d(TAG, "setCustomBackgroundViewDefault: ");
        try {
            GradientDrawable shape = new GradientDrawable();
            Context ctx = OustSdkApplication.getContext();
            float dimension = ctx.getResources().getDimension(R.dimen.oustlayout_dimen8);
            int backgroundColor = ctx.getResources().getColor(R.color.whitea);
            int strokeWidth = (int) ctx.getResources().getDimension(R.dimen.oustlayout_dimen1);

            shape.setShape(GradientDrawable.RECTANGLE);
            int borderColor = getColorBack(R.color.lgreen);
            if (OustPreferences.get("toolbarColorCode") != null) {
                //Log.d(TAG, "toolbarColorCode: " + OustPreferences.get("toolbarColorCode"));
                borderColor = Color.parseColor(OustPreferences.get("toolbarColorCode"));
            }
            shape.setCornerRadius(dimension);
            shape.setColor(backgroundColor);
            shape.setStroke(strokeWidth, borderColor);
            v.setBackground(shape);
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
    }

    public static void setCustomBackgroundColorView(final View v) {
        Log.d(TAG, "setCustomBackgroundViewDefault: ");
        try {
            GradientDrawable shape = new GradientDrawable();
            Context ctx = OustSdkApplication.getContext();
            float dimension = ctx.getResources().getDimension(R.dimen.oustlayout_dimen8);
            int backgroundColor = ctx.getResources().getColor(R.color.whitea);
            int strokeWidth = (int) ctx.getResources().getDimension(R.dimen.oustlayout_dimen1);

            shape.setShape(GradientDrawable.RECTANGLE);
            int borderColor = getColorBack(R.color.lgreen);
            if (OustPreferences.get("toolbarColorCode") != null) {
                //Log.d(TAG, "toolbarColorCode: " + OustPreferences.get("toolbarColorCode"));
                borderColor = Color.parseColor(OustPreferences.get("toolbarColorCode"));
            }
            shape.setCornerRadius(dimension);
            shape.setColor(backgroundColor);
            shape.setStroke(strokeWidth, borderColor);
            v.setBackground(shape);
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
    }

    /*public static void setConfigurableTickColor(Context ctx){
        Log.d(TAG, "setConfigurableTickColor: ");
        //Drawable backArrow = getResources().getDrawable(R.drawable.ic_arrow_back);
        try {
            int tenantColor = getColorBack(R.color.lgreen);
            if (OustPreferences.get("toolbarColorCode") != null) {
                Log.d(TAG, "toolbarColorCode: " + OustPreferences.get("toolbarColorCode"));
                tenantColor = Color.parseColor(OustPreferences.get("toolbarColorCode"));
            }

            final Drawable tick = ctx.getResources().getDrawable(R.drawable.ic_tick);
            tick.setColorFilter(tenantColor, PorterDuff.Mode.SRC_ATOP);
            DrawableCompat.setTint(tick, tenantColor);

            OustPreferences.saveAppInstallVariable("RegularModeTickColorChanged", true);
        }catch (Exception e){
            e.printStackTrace();
            sendSentryException(e);
        }
    }*/

    public static void setDefaultImageViewBackground(final ImageView v, Drawable drawable) {
        Log.d(TAG, "setDefaultImageViewBackground: ");
        try {
            //Context ctx = OustSdkApplication.getContext();

            int tenantColor = getColorBack(R.color.lgreen);
            if (OustPreferences.get("toolbarColorCode") != null) {
                //Log.d(TAG, "toolbarColorCode: " + OustPreferences.get("toolbarColorCode"));
                tenantColor = Color.parseColor(OustPreferences.get("toolbarColorCode"));
            }

            //final Drawable tick = ctx.getResources().getDrawable(R.drawable.ic_tick);
            drawable.setColorFilter(tenantColor, PorterDuff.Mode.SRC_ATOP);
            DrawableCompat.setTint(drawable, tenantColor);
            v.setImageDrawable(drawable);
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
    }

    public static void setRegularModeTick(final ImageView v, Drawable drawable) {
        Log.d(TAG, "setDefaultImageViewBackground: ");
        try {
            //Context ctx = OustSdkApplication.getContext();

            int tenantColor = getColorBack(R.color.lgreen);
            if (OustPreferences.get(AppConstants.StringConstants.RM_USER_STATUS_COLOR) != null) {
                tenantColor = Color.parseColor(OustPreferences.get(AppConstants.StringConstants.RM_USER_STATUS_COLOR));
            } else if (OustPreferences.get("toolbarColorCode") != null) {
                //Log.d(TAG, "toolbarColorCode: " + OustPreferences.get("toolbarColorCode"));
                tenantColor = Color.parseColor(OustPreferences.get("toolbarColorCode"));
            }

            //final Drawable tick = ctx.getResources().getDrawable(R.drawable.ic_tick);
            drawable.setColorFilter(tenantColor, PorterDuff.Mode.SRC_ATOP);
            DrawableCompat.setTint(drawable, tenantColor);
            v.setImageDrawable(drawable);
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
    }

    public static void setDefaultBackgroundColor(final View v) {
        Log.d(TAG, "setDefaultBackgroundColor: ");
        try {
            int tenantColor = getColorBack(R.color.lgreen);
            if (OustPreferences.get("toolbarColorCode") != null) {
                tenantColor = Color.parseColor(OustPreferences.get("toolbarColorCode"));
            }
            v.setBackgroundColor(tenantColor);
            //v.setBackground(tenantColor);
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
    }

    public static void setDefaultPrograssBar(ProgressBar progressbar) {
        Log.d(TAG, "setDefaultPrograssBar: ");
        try {
            progressbar.invalidate();
            int tenantColor = getColorBack(R.color.lgreen);
            if (OustPreferences.get("toolbarColorCode") != null) {
                tenantColor = Color.parseColor(OustPreferences.get("toolbarColorCode"));
            }
            PorterDuff.Mode mode = PorterDuff.Mode.SRC_OVER;
            LayerDrawable ld = (LayerDrawable) OustSdkApplication.getContext().getResources().getDrawable(R.drawable.regularmode_progressdrawable);
            final ClipDrawable d1 = (ClipDrawable) ld.findDrawableByLayerId(R.id.customPlayerProgress);
            d1.setColorFilter(tenantColor, mode);
            progressbar.setProgressDrawable(ld);
            //Log.e(TAG, "set setDefaultPrograssBar " + ld);
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
    }

    public static int getDpForPixel(int pixel) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixel, OustSdkApplication.getContext().getResources().getDisplayMetrics());
    }

    private static String getDevicePlatformFromPreference() {
        String devicePlatform = OustPreferences.get("devicePlatForm");
        try {
            Log.d(TAG, "getDevicePlatformFromPreference: " + devicePlatform);
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
        return (devicePlatform != null && !devicePlatform.isEmpty()) ? devicePlatform : "SDK";
    }

    public static JSONObject getRequestObjectWithPreference(String jsonParams) {
        JSONObject jsonObject = null;
        Log.d(TAG, "Additional request data");
        try {
            if ((jsonParams != null) && (!jsonParams.isEmpty())) {
                jsonObject = new JSONObject(jsonParams);
            } else {
                jsonObject = new JSONObject();
            }
            PackageInfo pinfo = OustSdkApplication.getContext().getPackageManager().getPackageInfo(OustSdkApplication.getContext().getPackageName(), 0);
            jsonObject.put("appVersion", pinfo.versionName);
            jsonObject.put("devicePlatformName", getDevicePlatformFromPreference());
            String uuid = Settings.Secure.getString(OustSdkApplication.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            if ((uuid != null) && (!uuid.isEmpty())) {
                jsonObject.put("deviceIdentity", uuid);
            }
            if ((OustPreferences.get("gcmToken") != null)) {
                jsonObject.put("deviceToken", OustPreferences.get("gcmToken"));
            }
        } catch (PackageManager.NameNotFoundException | JSONException e) {
            e.printStackTrace();
            sendSentryException(e);
        }
        //Log.d(TAG, "getRequestObject: "+jsonObject.toString());
        return jsonObject;
    }

    public static ArrayList<ClickedFeedData> getClickedFeedList(String jsonString) {
        if ((jsonString != null) && (!jsonString.isEmpty())) {
            return new Gson().fromJson(jsonString, new TypeToken<ArrayList<ClickedFeedData>>() {
            }.getType());
        }
        return null;
    }

    public static ArrayList<ViewedFeedData> getViewedFeedList(String jsonString) {
        if ((jsonString != null) && (!jsonString.isEmpty())) {
            return new Gson().fromJson(jsonString, new TypeToken<ArrayList<ViewedFeedData>>() {
            }.getType());
        }
        return null;
    }

    public static HashMap<String, ViewedFeedData> getViewedFeed(String jsonString) throws JSONException {
        HashMap<String, ViewedFeedData> getViewedFeed = new HashMap<>();
        if ((jsonString != null) && (!jsonString.isEmpty())) {


            HashMap<String, ViewedFeedData> map = new HashMap<>();
            JSONObject jObject = new JSONObject(jsonString);
            Iterator<?> keys = jObject.keys();

            while (keys.hasNext()) {
                String key = (String) keys.next();
                String value = jObject.getString(key);
                map.put(key, new Gson().fromJson(value, new TypeToken<ViewedFeedData>() {
                }.getType()));

            }

            getViewedFeed = map;

        }
        return getViewedFeed;
    }

    public static Drawable drawableColor(Drawable drawable) {

        DrawableCompat.setTint(
                DrawableCompat.wrap(drawable),
                OustResourceUtils.getColors()
        );

        return drawable;

    }

    public static Drawable drawableColor(Drawable drawable, int colorCode) {

        DrawableCompat.setTint(
                DrawableCompat.wrap(drawable),
                colorCode
        );

        return drawable;

    }

    public static void setLocale(Context context) {
        try {
            String language = Locale.getDefault().getLanguage();
            String selectedLanguage = LanguagePreferences.get("appSelectedLanguage");
            Log.d("test lang","INIII YANG DI SAVE "+ selectedLanguage + " --- " + language);
            Locale locale = null;
            if (selectedLanguage != null && !selectedLanguage.isEmpty() && !selectedLanguage.equals(language)) {
                Log.d("test lang","INIII YANG DI SAVE 999 "+ selectedLanguage + " --- " + language);
                if (selectedLanguage.equalsIgnoreCase("zh_TW")) {
                    locale = Locale.TAIWAN;
                } else {
                    Log.d("test lang","INIII YANG DI SAVE 0000 "+ selectedLanguage + " --- " + language);
                    locale = new Locale(selectedLanguage);
                }

                LanguagePreferences.save("appSelectedLanguage", selectedLanguage);
            }

            Log.d("test lang","INIII YANG DI SAVE 999 1203 "+ locale.getLanguage());

            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.setLocale(locale);
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());

        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }

    }

    public static String convertTimeForWD(String date) {

        Date initDate = null;
        try {
            initDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            sendSentryException(e);
        }


        Timestamp ts = null;
        ts = new Timestamp(initDate.getTime());
        return ts.getTime() + "";
    }

    public static long convertTimeToMiliWD(String date) {

        Date initDate = null;
        try {
            initDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            sendSentryException(e);
        }

        Timestamp ts = null;
        if (initDate != null) {
            ts = new java.sql.Timestamp(initDate.getTime());
        }

        return ts.getTime();
    }

    public static String numberToString(long number) {
        String numberToString = "" + number;
        if (number != 0) {
            String[] suffixes = new String[]{" K", " M", " B", " T", " Q"};
            for (int j = suffixes.length; j > 0; j--) {
                double unit = Math.pow(1000, j);
                if (number >= unit)
                    numberToString = new DecimalFormat("#.#").format(number / unit) + suffixes[--j];
            }
        }
        return numberToString;
    }

    public static String milliToDate(String milliseconds) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            return simpleDateFormat.format(new Date(Long.parseLong(milliseconds)));

        } catch (Exception e) {
            Log.e("OustSdk milliToDate ", "Error exception");
            e.printStackTrace();
            sendSentryException(e);
            return "";
        }


    }

    public static String newMilliToDate(String milliseconds) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return simpleDateFormat.format(new Date(Long.parseLong(milliseconds)));
        } catch (Exception e) {
            Log.e("OustSdk milliToDate ", "Error exception");
            e.printStackTrace();
            sendSentryException(e);
            return "";
        }


    }

    public static String returnValidString(String checkingString) {

        if (checkingString != null) {
            return checkingString;
        } else {
            return "";
        }

    }

    public static void makeLinkClickable(Context context, SpannableStringBuilder strBuilder, final URLSpan span, final String title) {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(@NonNull View view) {
                // Do something with span.getURL() to handle the link click...

                Intent redirectScreen = new Intent(context, RedirectWebView.class);
                redirectScreen.putExtra("url", span.getURL());
                redirectScreen.putExtra("feed_title", title);
                context.startActivity(redirectScreen);
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }

    public static String getFilePath(Context context, Uri uri) {

        Cursor cursor = null;
        final String[] projection = {
                MediaStore.MediaColumns.DISPLAY_NAME
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, null, null,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle((bitmap.getWidth() * 1.0f) / 2, (bitmap.getHeight() * 1.0f) / 2,
                (bitmap.getWidth() * 1.0f) / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

    public static void removeAllReminderNotification() {
        try {
            ArrayList<CommonLandingData> commonLandingDataArrayList = OustStaticVariableHandling.getInstance().getReminderNotification();
            if (commonLandingDataArrayList != null && commonLandingDataArrayList.size() != 0 && !commonLandingDataArrayList.isEmpty()) {
                for (CommonLandingData commonLandingData : commonLandingDataArrayList) {
                    String courseId = commonLandingData.getId();
                    int requestCode;
                    if (courseId.toUpperCase().contains("COURSE")) {
                        courseId = courseId.toUpperCase().replace("COURSE", "");
                        requestCode = Integer.parseInt("1" + courseId);
                    } else if (courseId.toUpperCase().contains("ASSESSMENT")) {
                        courseId = courseId.toUpperCase().replace("ASSESSMENT", "");
                        requestCode = Integer.parseInt("2" + courseId);
                    } else {
                        requestCode = Integer.parseInt("1" + courseId);
                    }
                    AlarmManager alarmMgr = (AlarmManager) OustSdkApplication.getContext().getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(OustSdkApplication.getContext(), ReminderNotificationManager.class);
                    PendingIntent pendingIntent;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        pendingIntent = PendingIntent.getBroadcast(OustSdkApplication.getContext(), requestCode, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_MUTABLE);
                    } else {
                        pendingIntent = PendingIntent.getBroadcast(OustSdkApplication.getContext(), requestCode, intent, PendingIntent.FLAG_NO_CREATE);
                    }
                    boolean isAlready = (pendingIntent != null);
                    if (isAlready) {
                        PendingIntent pendingIntentCancel;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            pendingIntentCancel = PendingIntent.getBroadcast(OustSdkApplication.getContext(), requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_MUTABLE);
                        } else {
                            pendingIntentCancel = PendingIntent.getBroadcast(OustSdkApplication.getContext(), requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                        }
                        assert alarmMgr != null;
                        alarmMgr.cancel(pendingIntentCancel);
                        pendingIntentCancel.cancel();
                    }

                    if (commonLandingDataArrayList.contains(commonLandingData)) {
                        commonLandingDataArrayList.remove(commonLandingData);
                        String reminderData = new Gson().toJson(commonLandingDataArrayList);

                        OustPreferences.save("reminderData", reminderData);
                        OustStaticVariableHandling.getInstance().setReminderNotification(commonLandingDataArrayList);
                    } else {
                        int position = findCommonLandingData(commonLandingDataArrayList, commonLandingData.getId());
                        if (position >= 0) {
                            commonLandingDataArrayList.remove(position);
                            String reminderData = new Gson().toJson(commonLandingDataArrayList);
                            OustPreferences.save("reminderData", reminderData);
                            OustStaticVariableHandling.getInstance().setReminderNotification(commonLandingDataArrayList);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
    }

    public static int findCommonLandingData(ArrayList<CommonLandingData> a, String id) {

        for (int i = 0; i < a.size(); i++)
            if (a.get(i).getId().equalsIgnoreCase(id))
                return i;

        return -1;
    }

    public static String getDescriptionHtmlFormat(String desc) {
        String tempDesc = null;
        if (desc != null && !desc.isEmpty()) {
            tempDesc = "<html><head>"
                    + "<style type=\"text/css\">ul,li,body{color: #000;}"
                    + "</style></head>"
                    + "<body >"
                    + desc
                    + "</body></html>";
        }
        return tempDesc;
    }

    public static String getTime(long contentDuration) {
        String totalTime = "1";
        int totalMinutes = 0;
        if (contentDuration > 0) {
            long minutes = TimeUnit.SECONDS.toMinutes(contentDuration);
            long seconds = TimeUnit.SECONDS.toSeconds(contentDuration) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(contentDuration));
            if (seconds > 10) {
                totalMinutes = totalMinutes + 1;
            }
            if (totalMinutes > 0) {
                totalTime = String.valueOf(minutes + 1);
            } else if (minutes == 0) {
                totalTime = String.valueOf(minutes + 1);
            } else {
                totalTime = String.valueOf(minutes);
            }
        }
        return totalTime;
    }

    public static HashMap<String, Object> getCleverTapProfileData() {
        HashMap<String, Object> profileUpdate = new HashMap<String, Object>();
        try {
            ActiveUser activeUser = getActiveUserData();
            // each of the below mentioned fields are optional
            profileUpdate.put("Name", activeUser.getUserDisplayName());    // String
            profileUpdate.put("Identity", activeUser.getStudentid());      // String or number
            profileUpdate.put("Email", activeUser.getEmail()); // Email address of the user
            profileUpdate.put("Phone", activeUser.getUserMobile());   // Phone (with the country code, starting with +)
            profileUpdate.put("Gender", activeUser.getUserGender());

            Log.d(TAG, "initData: CleverTap profile data" + profileUpdate.toString());
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }

        return profileUpdate;
    }

    public static HashMap<String, Object> getCleverTapEventData() {
        HashMap<String, Object> eventUpdate = new HashMap<String, Object>();
        try {
            //String model = Build.DEVICE;
            String name = Build.MANUFACTURER;

            ActiveUser activeUser = getActiveUserData();
            // each of the below mentioned fields are optional
            eventUpdate.put("TimeStamp", getDateTimeFromMilli(System.currentTimeMillis()));
            eventUpdate.put("Org ID", OustPreferences.get("tanentid"));
            eventUpdate.put("Org Name", OustPreferences.get("tanentid"));
            eventUpdate.put("User ID", activeUser.getStudentid());
            eventUpdate.put("User Name", activeUser.getUserDisplayName());
            eventUpdate.put("User Email ID", activeUser.getEmail());
            //eventUpdate.put("Role", "");
            //eventUpdate.put("Group ID", "");
            //eventUpdate.put("Group Name", "");
            eventUpdate.put("Device Type", "Android");
            eventUpdate.put("Device Name", name);
            //Log.d(TAG, "initData: CleverTap profile data"+eventUpdate.toString());
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }

        return eventUpdate;
    }

    public static void removeReminderNotification(String courseId) {
        try {
            if (courseId != null) {
                ArrayList<CommonLandingData> commonLandingDataArrayList = OustStaticVariableHandling.getInstance().getReminderNotification();
                int requestCode;
                if (courseId.toUpperCase().contains("COURSE")) {
                    courseId = courseId.toUpperCase().replace("COURSE", "");
                    requestCode = Integer.parseInt("1" + courseId);
                } else if (courseId.toUpperCase().contains("ASSESSMENT")) {
                    courseId = courseId.toUpperCase().replace("ASSESSMENT", "");
                    requestCode = Integer.parseInt("2" + courseId);
                } else {
                    requestCode = Integer.parseInt("1" + courseId);
                }
                AlarmManager alarmMgr = (AlarmManager) OustSdkApplication.getContext().getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(OustSdkApplication.getContext(), ReminderNotificationManager.class);
                PendingIntent pendingIntent;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    pendingIntent = PendingIntent.getBroadcast(OustSdkApplication.getContext(), requestCode, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_MUTABLE);
                } else {
                    pendingIntent = PendingIntent.getBroadcast(OustSdkApplication.getContext(), requestCode, intent, PendingIntent.FLAG_NO_CREATE);
                }

                boolean isAlready = (pendingIntent != null);
                if (isAlready) {
                    PendingIntent pendingIntentCancel;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        pendingIntentCancel = PendingIntent.getBroadcast(OustSdkApplication.getContext(), requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_MUTABLE);
                    } else {
                        pendingIntentCancel = PendingIntent.getBroadcast(OustSdkApplication.getContext(), requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                    }
                    assert alarmMgr != null;
                    alarmMgr.cancel(pendingIntentCancel);
                    pendingIntentCancel.cancel();
                }
                if (commonLandingDataArrayList != null && commonLandingDataArrayList.size() != 0) {
                    String reminderData = new Gson().toJson(commonLandingDataArrayList);
                    for (int i = 0; i < commonLandingDataArrayList.size(); i++) {
                        courseId = "COURSE" + courseId;
                        if (commonLandingDataArrayList.get(i).getId().equalsIgnoreCase(courseId)) {
                            commonLandingDataArrayList.remove(i);
                            OustPreferences.save("reminderData", reminderData);
                            OustStaticVariableHandling.getInstance().setReminderNotification(commonLandingDataArrayList);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
    }

    public static void getSpannedMathmlContent(String content, WebView webViewText, boolean showScrollBar) {
        try {
            final WebSettings webSettings = webViewText.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setBuiltInZoomControls(false);
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
            webSettings.setLoadWithOverviewMode(true);

//            webSettings.setDefaultFontSize(20);
            if (showScrollBar) {
                webViewText.setVerticalScrollBarEnabled(true);
                webViewText.setHorizontalScrollBarEnabled(true);
            } else {
                webViewText.setVerticalScrollBarEnabled(false);
                webViewText.setHorizontalScrollBarEnabled(false);
            }
//            webViewText.setInitialScale(90);
//            " <script id=\"MathJax-script\" charset=\"utf-8\" async type=\"text/javascript\" src=\"file:///android_assets/math.js\"></script>"

            webViewText.setBackgroundColor(Color.TRANSPARENT);
            String text = "<html><head>"
                    + "<script src=\"https://polyfill.io/v3/polyfill.min.js?features=es6\"></script>\n" +
                    "  <script id=\"MathJax-script\" async src=\"https://cdn.jsdelivr.net/npm/mathjax@3.0.1/es5/tex-mml-chtml.js\"></script>"
                    + "<style type=\"text/css\">ul,li,body{color: #000;}"
                    + "</style></head>"
                    + "<body><p>"
                    + content
                    + "</p></body></html>";

            Log.d(TAG, "getSpannedMathmlContent: text:" + text);
            webViewText.loadDataWithBaseURL(null, text, "text/html", "utf-8", null);
        } catch (Exception e) {
            e.printStackTrace();
            sendSentryException(e);
        }
    }

    public static void sendSentryException(Exception e) {
        try {
            if (e != null) {
                Sentry.captureException(e);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}
