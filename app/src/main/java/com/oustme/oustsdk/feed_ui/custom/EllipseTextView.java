package com.oustme.oustsdk.feed_ui.custom;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;

import android.view.ViewTreeObserver;

import androidx.appcompat.widget.AppCompatTextView;

import com.oustme.oustsdk.tools.OustSdkTools;

public class EllipseTextView extends AppCompatTextView {

    private static final String TAG = EllipseTextView.class.getName();

    private int showingLine = 1;
    private int showingChar;
    private boolean isCharEnable;

    //private String showMore = "Show more";
    //private String showLess = "Show less";
    private String dotdot = "...";

    private int MAGIC_NUMBER = 5;

    //private int showMoreTextColor = Color.RED;
  //  private int showLessTextColor = Color.RED;

    //private String mainText;

    private boolean isAlreadySet;


    public EllipseTextView(Context context) {
        super(context);
    }

    public EllipseTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

      //  mainText = getText().toString();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
    }

    private void addShowMore() {
        ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                String text = getText().toString();
                if (!isAlreadySet) {
                    //mainText = getText().toString();
                    isAlreadySet = true;
                }
                StringBuilder showingText = new StringBuilder();
                if (isCharEnable) {
                    if (showingChar >= text.length()) {
                        try {
                            throw new Exception("Character count cannot be exceed total line count");
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    }
                    String newText = text.substring(0, showingChar);
                    newText += dotdot ;//showMore

                    SaveState.isCollapse = true;

                    setText(newText);
                    Log.d(TAG, "Text: " + newText);
                } else {

                    if (showingLine >= getLineCount()) {
                        try {
                            throw new Exception("Line Number cannot be exceed total line count");
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            Log.e(TAG, "Error: " + e.getMessage());
                        }
                        getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        return;
                    }
                    int start = 0;
                    int end;
                    for (int i = 0; i < showingLine; i++) {
                        end = getLayout().getLineEnd(i);
                        showingText.append(text.substring(start, end));
                        start = end;
                    }

                    //String newText = showingText.substring(0, showingText.length() - (dotdot.length() + showMore.length() + MAGIC_NUMBER));
                    String newText = showingText.substring(0, showingText.length() - (dotdot.length() + MAGIC_NUMBER));
                    Log.d(TAG, "Text: " + newText);
                    Log.d(TAG, "Text: " + showingText);
                    newText += dotdot ;//+showMore

                    SaveState.isCollapse = true;

                    setText(newText);
                }

               // setShowMoreColoringAndClickable();
                getViewTreeObserver().removeOnGlobalLayoutListener(this);

            }
        });


    }

  /*  private void setShowMoreColoringAndClickable() {
        final SpannableString spannableString = new SpannableString(getText());

        Log.d(TAG, "Text: " + getText());
        spannableString.setSpan(new ClickableSpan() {
                                    @Override
                                    public void updateDrawState(TextPaint ds) {
                                        ds.setUnderlineText(false);
                                    }

                                    @Override
                                    public void onClick(@Nullable View view) {
                                        setMaxLines(Integer.MAX_VALUE);
                                        setText(mainText);
                                        SaveState.isCollapse = false;
                                        //showLessButton();
                                        Log.d(TAG, "Item clicked: " + mainText);

                                    }
                                },
                getText().length() - (dotdot.length() ),
                //getText().length() - (dotdot.length() + showMore.length()),
                getText().length(), 0);

        spannableString.setSpan(new ForegroundColorSpan(showMoreTextColor),
                getText().length() - (dotdot.length() ),
                //getText().length() - (dotdot.length() + showMore.length()),
                getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        setMovementMethod(LinkMovementMethod.getInstance());
        setText(spannableString, TextView.BufferType.SPANNABLE);
    }*/

  /*  private void showLessButton() {

        String text = getText() + dotdot ;//+ showLess
        SpannableString spannableString = new SpannableString(text);

        spannableString.setSpan(new ClickableSpan() {
                                    @Override
                                    public void updateDrawState(TextPaint ds) {
                                        ds.setUnderlineText(false);
                                    }

                                    @Override
                                    public void onClick(@Nullable View view) {

                                        setMaxLines(showingLine);

                                        addShowMore();

                                        Log.d(TAG, "Item clicked: ");

                                    }
                                },
                //text.length() - (dotdot.length() + showLess.length()),
                text.length() - (dotdot.length() ),
                text.length(), 0);

        spannableString.setSpan(new ForegroundColorSpan(showLessTextColor),
                //text.length() - (dotdot.length() + showLess.length()),
                text.length() - (dotdot.length()),
                text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        setMovementMethod(LinkMovementMethod.getInstance());
        setText(spannableString, TextView.BufferType.SPANNABLE);
    }*/


    public void setShowingLine(int lineNumber) {
        if (lineNumber == 0) {
            try {
                throw new Exception("Line Number cannot be 0");
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            return;
        }

        isCharEnable = false;

        showingLine = lineNumber;

        setMaxLines(showingLine);

        if (SaveState.isCollapse) {
            addShowMore();
        } else {
            setMaxLines(Integer.MAX_VALUE);
         //   showLessButton();
        }

    }


  /*  public void setShowingChar(int character) {
        if (character == 0) {
            try {
                throw new Exception("Character length cannot be 0");
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            return;
        }

        isCharEnable = true;
        this.showingChar = character;

        if (SaveState.isCollapse) {
            addShowMore();
        } else {
            setMaxLines(Integer.MAX_VALUE);
          //  showLessButton();
        }
    }


    public void addShowMoreText(String text) {
       // showMore = text;
    }


    public void addShowLessText(String text) {
        //showLess = text;
    }


    public void setShowMoreColor(int color) {
        showMoreTextColor = color;
    }


    public void setShowLessTextColor(int color) {
        showLessTextColor = color;
    }*/

}
