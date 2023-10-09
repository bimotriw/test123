package com.oustme.oustsdk.calendar_ui.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.utils.OustResourceUtils;


public class CalendarDescriptionActivity extends AppCompatActivity {
    Toolbar toolbar_lay;
    FrameLayout toolbar_close_icon;
    TextView title_text;
    ImageView toolbar_meeting_desc_close;
    WebView title_web_view_text;
    String type;
    String desAndTrainerDes;
    String toolbarColorCode;
    String toolbarTextColorCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_description);

        toolbar_lay = findViewById(R.id.toolbar_des_lay);
        toolbar_close_icon = findViewById(R.id.toolbar_close_icon);
        title_text = findViewById(R.id.title_des_text);
        title_web_view_text = findViewById(R.id.trainer_web_txt);
        toolbar_meeting_desc_close = findViewById(R.id.toolbar_meeting_desc_close);

        Bundle dataBundle = getIntent().getExtras();
        if (dataBundle != null) {
            type = dataBundle.getString("type");
            desAndTrainerDes = dataBundle.getString("description");
        }

        if (OustPreferences.get("toolbarBgColor") != null && !OustPreferences.get("toolbarBgColor").isEmpty()) {
            toolbarColorCode = OustPreferences.get("toolbarBgColor");
        } else {
            toolbarColorCode = "#01b5a2";
        }
        if (OustPreferences.get("toolbarColorCode") != null && !OustPreferences.get("toolbarColorCode").isEmpty()) {
            toolbarTextColorCode = OustPreferences.get("toolbarColorCode");
        } else {
            toolbarTextColorCode = "#01b5a2";
        }

        toolbar_lay.setBackgroundColor(Color.parseColor(toolbarColorCode));
        title_text.setText(type);
        title_text.setTextColor(Color.parseColor(toolbarTextColorCode));
        OustResourceUtils.setDefaultDrawableColor(toolbar_meeting_desc_close.getDrawable(), Color.parseColor(toolbarTextColorCode));

        title_web_view_text.setBackgroundColor(Color.TRANSPARENT);
        String text = OustSdkTools.getDescriptionHtmlFormat(desAndTrainerDes);
        final WebSettings webSettings = title_web_view_text.getSettings();
        // Set the font size (in sp).
        webSettings.setDefaultFontSize(18);
        title_web_view_text.loadDataWithBaseURL(null, text, "text/html", "utf-8", null);

        toolbar_close_icon.setOnClickListener(v -> finish());
    }
}