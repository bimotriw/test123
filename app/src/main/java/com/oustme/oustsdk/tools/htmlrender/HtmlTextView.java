package com.oustme.oustsdk.tools.htmlrender;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;
import android.text.Html;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.oustme.oustsdk.tools.OustSdkTools;

import java.io.InputStream;
import java.util.Scanner;


/**
 * Created by admin on 14/03/17.
 */

public class HtmlTextView extends JellyBeanSpanFixTextView {

    public static final String TAG = "HtmlTextView";
    public static final boolean DEBUG = false;

    boolean linkHit;
    @Nullable
    private ClickableTableSpan clickableTableSpan;
    @Nullable
    private DrawTableLinkSpan drawTableLinkSpan;

    boolean dontConsumeNonUrlClicks = true;
    private boolean removeFromHtmlSpace = true;

    public HtmlTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public HtmlTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HtmlTextView(Context context) {
        super(context);
    }

    /**
     */
    public void setHtml(@RawRes int resId) {
        setHtml(resId, null);
    }

    /**
     */
    public void setHtml(@NonNull String html) {
        setHtml(html, null);
    }

    /**
     * Loads HTML from a raw resource, i.e., a HTML file in res/raw/.
     * This allows translatable resource (e.g., res/raw-de/ for german).
     * The containing HTML is parsed to Android's Spannable format and then displayed.
     *
     * @param resId       for example: R.raw.help
     * @param imageGetter for fetching images. Possible ImageGetter provided by this library:
     *                    HtmlLocalImageGetter and HtmlRemoteImageGetter
     */
    public void setHtml(@RawRes int resId, @Nullable Html.ImageGetter imageGetter) {
        InputStream inputStreamText = getContext().getResources().openRawResource(resId);

        setHtml(convertStreamToString(inputStreamText), imageGetter);
    }

    /**
     * Parses String containing HTML to Android's Spannable format and displays it in this TextView.
     * Using the implementation of Html.ImageGetter provided.
     *
     * @param html        String containing HTML, for example: "<b>Hello world!</b>"
     * @param imageGetter for fetching images. Possible ImageGetter provided by this library:
     *                    HtmlLocalImageGetter and HtmlRemoteImageGetter
     */
    public void setHtml(@NonNull String html, @Nullable Html.ImageGetter imageGetter) {
        try {
            final HtmlTagHandler htmlTagHandler = new HtmlTagHandler(getPaint());
            htmlTagHandler.setClickableTableSpan(clickableTableSpan);
            htmlTagHandler.setDrawTableLinkSpan(drawTableLinkSpan);

            html = htmlTagHandler.overrideTags(html);

            if (removeFromHtmlSpace) {
                setText(removeHtmlBottomPadding(Html.fromHtml(html, imageGetter, htmlTagHandler)));
            } else {
                setText(Html.fromHtml(html, imageGetter, htmlTagHandler));
            }

            // make links work
            setMovementMethod(LocalLinkMovementMethod.getInstance());
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    /**
     * Note that this must be called before setting text for it to work
     */
    public void setRemoveFromHtmlSpace(boolean removeFromHtmlSpace) {
        this.removeFromHtmlSpace = removeFromHtmlSpace;
    }

    public void setClickableTableSpan(@Nullable ClickableTableSpan clickableTableSpan) {
        this.clickableTableSpan = clickableTableSpan;
    }

    public void setDrawTableLinkSpan(@Nullable DrawTableLinkSpan drawTableLinkSpan) {
        this.drawTableLinkSpan = drawTableLinkSpan;
    }

    /**
     * http://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string
     */
    @NonNull
    static private String convertStreamToString(@NonNull InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    /**
     * Html.fromHtml sometimes adds extra space at the bottom.
     * This methods removes this space again.
     * See https://github.com/SufficientlySecure/html-textview/issues/19
     */
    @Nullable
    static private CharSequence removeHtmlBottomPadding(@Nullable CharSequence text) {
        if (text == null) {
            return null;
        }

        try{
        while (text.length() > 0 && text.charAt(text.length() - 1) == '\n') {
            text = text.subSequence(0, text.length() - 1);
        }
        }catch (Exception e){
            int n1=9;
            int n2=n1+3;
        }
        return text;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        linkHit = false;
        boolean res = super.onTouchEvent(event);

        if (dontConsumeNonUrlClicks) {
            return linkHit;
        }
        return res;
    }

}
