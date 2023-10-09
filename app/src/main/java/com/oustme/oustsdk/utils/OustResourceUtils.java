package com.oustme.oustsdk.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.Log;

import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.utils.svgUtils.SVG;
import com.oustme.oustsdk.utils.svgUtils.SVGParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class OustResourceUtils {

    private static int themeColor;
    private static int themeBgColor;

    public static void setImage(ImageView imageview, int resId) {
        InputStream imageStream = OustSdkApplication.getContext().getResources().openRawResource(resId);
        Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
        imageview.setImageBitmap(bitmap);
    }


    public static void setMenuIconForSVG(MenuItem menuItem, String navIcon) {
        if (navIcon != null && menuItem != null) {
            try {
                String[] dirs = navIcon.split("/");
                String imageName = "";
                if (dirs.length > 0)
                    imageName = dirs[dirs.length - 1];
                File file = new File(OustSdkApplication.getContext().getFilesDir(), imageName);
                if (file.exists()) {
                    SVG svg = SVGParser.getSVGFromInputStream(new FileInputStream(file), Color.parseColor("#819968"), Color.parseColor("#212121"));
                    Drawable drawable = svg.createPictureDrawable();
                    menuItem.setIcon(OustResourceUtils.setDefaultDrawableColor(drawable,Color.parseColor("#212121")));
                } else {
                    menuItem.setIcon(R.drawable.ic_engineering_24px);
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }

    public static void setMenuIcon(MenuItem menuItem, String navIcon) {

        if (navIcon != null && menuItem != null) {
            try {
                String[] dirs = navIcon.split("/");
                String imageName = "";
                if (dirs.length > 0)
                    imageName = dirs[dirs.length - 1];
                File file = new File(OustSdkApplication.getContext().getFilesDir(), imageName);
                if (file.exists()) {
                    Drawable drawable = Drawable.createFromPath(file.getAbsolutePath());
                    menuItem.setIcon(OustResourceUtils.setDefaultDrawableColor(drawable,Color.parseColor("#908F8F")));
                } else {
                    menuItem.setIcon(R.drawable.home);
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }


    public static void setMenuIconForSVG(ImageView imageView, String navIcon) {
        if (navIcon != null && imageView != null) {
            try {
                String[] dirs = navIcon.split("/");
                String imageName = "";
                if (dirs.length > 0)
                    imageName = dirs[dirs.length - 1];
                File file = new File(OustSdkApplication.getContext().getFilesDir(), imageName);
                if (file.exists()) {
                    SVG svg = SVGParser.getSVGFromInputStream(new FileInputStream(file), Color.parseColor("#819968"), Color.parseColor("#212121"));
                    Drawable drawable = svg.createPictureDrawable();
                    //imageView.setImageDrawable(drawable);
                    imageView.setImageDrawable(OustResourceUtils.setDefaultDrawableColor(drawable,Color.parseColor("#212121")));
                } else {
                    imageView.setImageResource(R.drawable.ic_engineering_24px);
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }

    public static void setMenuIcon(ImageView imageView, String navIcon) {
        if (navIcon != null && imageView != null) {
            try {
                String[] dirs = navIcon.split("/");
                String imageName = "";
                if (dirs.length > 0)
                    imageName = dirs[dirs.length - 1];
                File file = new File(OustSdkApplication.getContext().getFilesDir(), imageName);
                if (file.exists()) {
                    Drawable drawable = Drawable.createFromPath(file.getAbsolutePath());
                    imageView.setImageDrawable(OustResourceUtils.setDefaultDrawableColor(drawable,R.color.primary_text));
                } else {
                    imageView.setImageResource(R.drawable.home);
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    public static void setMenuIcon(MenuItem menuItem, int navIcon) {
        if (navIcon != 0 && menuItem != null) {
            try {
                Drawable drawable = OustSdkApplication.getContext().getResources().getDrawable(navIcon);
                setDefaultDrawableColor(drawable,R.color.primary_text);
                menuItem.setIcon(drawable);
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public static void setMenuIconSelected(MenuItem menuItem, int navIcon) {
        if (navIcon != 0 && menuItem != null) {
            try {
                Drawable drawable = OustSdkApplication.getContext().getResources().getDrawable(navIcon);
                setDefaultDrawableColor(drawable);
                menuItem.setIcon(drawable);
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }

    public static Drawable setTint(Drawable d, int color) {
        Drawable wrappedDrawable = DrawableCompat.wrap(d);
        DrawableCompat.setTint(wrappedDrawable, color);
        return wrappedDrawable;
    }

    public static void setMenuIconSelectedForSVG(MenuItem menuItem, String navIcon) {
        if (navIcon != null && menuItem != null) {
            try {
                String[] dirs = navIcon.split("/");
                String imageName = "";
                if (dirs.length > 0)
                    imageName = dirs[dirs.length - 1];
                File file = new File(OustSdkApplication.getContext().getFilesDir(), imageName);
                if (file.exists()) {
                    SVG svg = SVGParser.getSVGFromInputStream(new FileInputStream(file), Color.parseColor("#908F8F"), getColors());
                    Drawable drawable = svg.createPictureDrawable();
                    menuItem.setIcon(drawable);
                } else {
                    menuItem.setIcon(R.drawable.ic_engineering_24px);
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }

    public static void setMenuIconSelected(MenuItem menuItem, String navIcon) {
        if (navIcon != null && menuItem != null) {
            try {
                String[] dirs = navIcon.split("/");
                String imageName = "";
                if (dirs.length > 0)
                    imageName = dirs[dirs.length - 1];
                File file = new File(OustSdkApplication.getContext().getFilesDir(), imageName);
                if (file.exists()) {
                    Drawable drawable = Drawable.createFromPath(file.getAbsolutePath());
                    menuItem.setIcon(OustResourceUtils.setDefaultDrawableColor(drawable));
                } else {
                    menuItem.setIcon(R.drawable.home);
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }

    public static int getColors() {
        //Log.d("TAG", "getColors: ");
        themeColor = 0;
        try {
            String themeColorString = OustPreferences.get("toolbarColorCode");
            if ((themeColorString != null) && (!themeColorString.isEmpty())) {
                if (OustPreferences.get("toolbarColorCode") != null) {
                    themeColor = Color.parseColor(OustPreferences.get("toolbarColorCode"));
                }
            }

            if (themeColor == 0)
                themeColor = OustSdkTools.getColorBack(R.color.black);

        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return themeColor;
    }

    public static int getToolBarBgColor() {
        String themeColorString = OustPreferences.get("toolbarBgColor");
        themeBgColor = 0;
        if ((themeColorString != null) && (!themeColorString.isEmpty())) {
            if (OustPreferences.get("toolbarBgColor") != null) {
                themeBgColor = Color.parseColor(OustPreferences.get("toolbarBgColor"));
            }
        }

        if (themeBgColor == 0)
            themeBgColor = OustSdkTools.getColorBack(R.color.white);

        return themeBgColor;
    }

    private static int getThemeColor() {
        if (themeColor == 0)
            getColors();
        return themeColor;
    }

    public static void setTextViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(textView.getContext(), color), PorterDuff.Mode.SRC_IN));
            }
        }
    }


    public static void setDefaultDrawableColor(Drawable[] compoundDrawables) {

        for (Drawable drawable : compoundDrawables) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(getThemeColor(), PorterDuff.Mode.SRC_IN));
            }
        }
    }

    public static Drawable setDefaultDrawableColor(Drawable drawable) {

        if (drawable != null) {
            drawable.setColorFilter(new PorterDuffColorFilter(getThemeColor(), PorterDuff.Mode.SRC_IN));
        }
        return drawable;
    }

    public static Drawable setDefaultDrawableColor(Drawable drawable, int color) {

        if (drawable != null) {
            drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        }
        return drawable;
    }

    public static String getString(Context context, int id) {
        return context.getResources().getString(id);
    }

    public static String getValuedString(Context context, int id, int value) {
        String string = String.format(context.getResources().getString(id), value);
        return string;
    }

    public static int getScreenWidth() {
        int screenSize = OustSdkApplication.getContext().getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        return screenSize;
    }

    public static void setDefaultDrawableColor(ImageView imageView) {
        if (imageView != null) {
            imageView.setColorFilter(new PorterDuffColorFilter(getThemeColor(), PorterDuff.Mode.SRC_IN));
        }
    }

    public static ColorStateList getDefaultTint() {
        int[][] states = new int[][]{ // disabled
                new int[]{android.R.attr.state_checked}, // unchecked
                new int[]{-android.R.attr.state_checked} // unchecked // pressed
        };

        int[] colors = new int[]{
                getThemeColor(),
                Color.GRAY

        };

        ColorStateList colorStateList = new ColorStateList(states, colors);

        return colorStateList;
    }

}
