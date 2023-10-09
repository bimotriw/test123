package com.oustme.oustsdk.feed_ui.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;

import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.material.snackbar.Snackbar;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.feed_ui.ui.RedirectWebView;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.utils.OustResourceUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class OustSdkTools {


    private static Snackbar snackbar;
    @SuppressLint("StaticFieldLeak")
    private static LinearLayout linearLayout;
    @SuppressLint("StaticFieldLeak")
    private static Activity activityContext;

    public static void setSnackbarElements(LinearLayout layout,Activity activity) {
        linearLayout = layout;
        activityContext = activity;
    }

    public static boolean checkInternetStatus() {
        String status = NetworkUtil.getConnectivityStatusString(activityContext);
        boolean isNetworkAvailable = false;
        try {
            switch (status) {
                case "Connected to Internet with Mobile Data":
                case "Connected to Internet with WIFI":
                    isNetworkAvailable = true;
                    break;

            }
            showSnackBarBasedonStatus(isNetworkAvailable);
        } catch (Exception e) {
            e.printStackTrace();
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
        }
        return isNetworkAvailable;
    }


    private static void showSnackBarBasedonStatus(boolean netStatus) {
        try {
            if (netStatus) {
                if (snackbar != null) {
                    if (snackbar.isShown()) {
                        snackbar.dismiss();
                    }
                }
            } else {
                String messageToDisplay = "No Internet Connection";
                snackbar = Snackbar
                        .make(linearLayout, messageToDisplay, Snackbar.LENGTH_INDEFINITE)
                        .setAction("DISMISS", view -> snackbar.dismiss());
                View view = snackbar.getView();
                //float height = view.getHeight();
                view.setScaleY((float) .8); // used for increase margin bottom
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    view.setBackgroundColor(activityContext.getColor(R.color.popupBackGround));
                }else{
                    view.setBackgroundColor(activityContext.getResources().getColor(R.color.popupBackGround));
                }
                //view.setY(15f);
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
                params.gravity = Gravity.BOTTOM;
                view.setLayoutParams(params);


                TextView snackbar_text =  view.findViewById(com.google.android.material.R.id.snackbar_text);
                TextView snackbar_action = view.findViewById(com.google.android.material.R.id.snackbar_action);
                snackbar_action.setTextSize(activityContext.getResources().getDimension(R.dimen.ousttext_dimen6));
                snackbar_text.setTextSize(activityContext.getResources().getDimension(R.dimen.ousttext_dimen6));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    snackbar_text.setTextColor(activityContext.getColor(android.R.color.white));
                }else{
                    snackbar_text.setTextColor(activityContext.getResources().getColor(android.R.color.white));
                }
                //snackbar_action.setTypeface(OustSdkTools.getAvenirLTStdMedium());
                //snackbar_text.setTypeface(OustSdkTools.getAvenirLTStdMedium());
                snackbar.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
        }
    }

    public static String returnValidString(String checkingString){

        if(checkingString!=null){
            return checkingString;
        }else{
            return "";
        }

    }

    public static boolean compareDrawable(Drawable d1, Drawable d2){
        try{
            Bitmap bitmap1 = getBitmap(d1);
            ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
            bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, stream1);
            stream1.flush();
            byte[] bitmapdata1 = stream1.toByteArray();
            stream1.close();

            Bitmap bitmap2 = getBitmap(d2);
            ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
            bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, stream2);
            stream2.flush();
            byte[] bitmapdata2 = stream2.toByteArray();
            stream2.close();

            return Arrays.equals(bitmapdata1, bitmapdata2);
        }
        catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
        }
        return false;
    }

    private static Bitmap getBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
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
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10.0);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    public static String milliToDate(String milliseconds){
        try{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy",Locale.getDefault());
            return simpleDateFormat.format(new Date(Long.parseLong(milliseconds)));

        }catch (Exception e){
            Log.e("OustSdk milliToDate ","Error exception");
            e.printStackTrace();
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
            return "";
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static RippleDrawable getBackgroundDrawable(int pressedColor, Drawable backgroundDrawable)
    {
        return new RippleDrawable(getPressedState(pressedColor), backgroundDrawable, null);
    }

    private static ColorStateList getPressedState(int pressedColor)
    {
        return new ColorStateList(new int[][]{ new int[]{}},new int[]{pressedColor});
    }

    public static URL stringToURL(String urlString) {
        try {
            return new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
        }
        return null;
    }


    public static Bitmap mark(Bitmap src, Context context) {
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.oust_logo_whiteboy);
        if(OustPreferences.get(AppConstants.StringConstants.TenantLogo)!=null)
        {
            try {
                URL url = new URL(OustPreferences.get(AppConstants.StringConstants.TenantLogo));
                icon = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch(IOException e) {
               e.printStackTrace();
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
            }
        }

        int w = src.getWidth();
        int h = src.getHeight();
        int pw= 0;
        int ph= 0;
        int nw = (w * 10)/100;
        int nh = (h * 10)/100;
        Bitmap result = Bitmap.createBitmap(w, h, icon.getConfig());
        Canvas canvas = new Canvas(result);
        Bitmap resized = Bitmap.createScaledBitmap(icon, nw, nh, true);

        canvas.drawBitmap(src, 0, 0, null);
        Paint paint = new Paint();

        paint.setTextSize(30);
        paint.setAntiAlias(true);
        paint.setUnderlineText(false);
        canvas.drawBitmap(resized,pw,ph,paint);
        return result;
    }

    public static void makeLinkClickable(Context context,SpannableStringBuilder strBuilder, final URLSpan span,final String title) {
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

    public static Drawable drawableColor(Drawable drawable) {

        DrawableCompat.setTint(
                DrawableCompat.wrap(drawable),
                OustResourceUtils.getColors()
        );

        return drawable;

    }

    public static String getYTVID(String youtubeKey) {

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

        return youtubeKey;
    }

    public static String numberToString(long number){
        String numberToString = ""+number;
        if(number!=0){
            String[]  suffixes = new String[] {" K", " M", " B", " T", " Q"};
            for (int j = suffixes.length;  j > 0;  j--)
            {
                double  unit = Math.pow(1000, j);
                if (number >= unit)
                    numberToString =  new DecimalFormat("#.#").format(number / unit) + suffixes[--j];
            }
        }

        return numberToString;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
            return null;
        }
    }

}
