package com.oustme.oustsdk.survey_ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.response.common.OustPopupCategory;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustGATools;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;

import java.util.Objects;


public class SurveyPopUpActivity extends AppCompatActivity {

    TextView completed_text;
    TextView title;
    LinearLayout coins_layout;
    TextView user_earned_coins;
    ImageView thumbImage;
    FrameLayout survey_action_layout;

    long feedID, feedComment;
    boolean isFeedChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        OustSdkTools.setLocale(SurveyPopUpActivity.this);
        setContentView(R.layout.activity_survey_pop_up);

        completed_text = findViewById(R.id.completed_text);
        title = findViewById(R.id.title);
        coins_layout = findViewById(R.id.coins_layout);
        user_earned_coins = findViewById(R.id.user_earned_coins);
        thumbImage = findViewById(R.id.thumbImage);
        survey_action_layout = findViewById(R.id.survey_action_layout);

//        OustGATools.getInstance().reportPageViewToGoogle(SurveyPopUpActivity.this, "Oust Survey Popup Page");

        setIconColors();
        initFunctions();


    }

    private void setIconColors() {

        Drawable drawable = getResources().getDrawable(R.drawable.course_button_bg);
        survey_action_layout.setBackground(OustSdkTools.drawableColor(drawable));

        Drawable thumbDrawable = getResources().getDrawable(R.drawable.ic_thumbs_up);
        thumbImage.setImageDrawable(OustSdkTools.drawableColor(thumbDrawable));
    }

    private void initFunctions() {

        Intent intent = getIntent();
        if (intent != null) {

            if (intent.hasExtra("FeedId")) {
                feedID = intent.getLongExtra("FeedId", 0);
            }
            if (intent.hasExtra("FeedComment")) {
                feedComment = intent.getLongExtra("FeedComment", 0);
            }
            if (intent.hasExtra("isFeedChange")) {
                isFeedChange = intent.getBooleanExtra("isFeedChange", false);
            }

            if (intent.hasExtra("isSurvey") && intent.hasExtra("Coins") && intent.getLongExtra("Coins", 0) > 0) {
                String coinsText = "" + intent.getLongExtra("Coins", 0);
                user_earned_coins.setText(coinsText);
                coins_layout.setVisibility(View.VISIBLE);
            }

            String surveyTitle = intent.getStringExtra("surveyName");

            if (surveyTitle != null && !surveyTitle.isEmpty()) {
                title.setText(surveyTitle);
            }
        }

        try {
            if (OustStaticVariableHandling.getInstance().getOustpopup() != null) {

                if (OustStaticVariableHandling.getInstance().getOustpopup().getContent() != null) {
                    completed_text.setVisibility(View.VISIBLE);
                    completed_text.setText(OustStaticVariableHandling.getInstance().getOustpopup().getContent());
                } else {
                    completed_text.setVisibility(View.GONE);
                }

                if (OustStaticVariableHandling.getInstance().getOustpopup().getCategory() == OustPopupCategory.NOACTION) {
                    if (!OustAppState.getInstance().isSoundDisabled()) {
                        if (RoomHelper.getResourceDataModel("new_badge_levelup") != null) {
                            String filepath = RoomHelper.getResourceDataModel("new_badge_levelup").getFile();
                            MediaPlayer mediaPlayer = MediaPlayer.create(this, Uri.parse(filepath));
                            mediaPlayer.setLooping(false);
                            if (!mediaPlayer.isPlaying()) {
                                mediaPlayer.start();

                            }

                        }
                    }
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        survey_action_layout.setOnClickListener(v -> backScreen());

    }

    @Override
    public void onBackPressed() {
        backScreen();
    }

    private void backScreen() {
        try {
            if (feedID != 0) {
                Intent data = new Intent();
                data.putExtra("FeedPosition", feedID);
                data.putExtra("FeedRemove", true);
                data.putExtra("FeedComment", feedComment);
                data.putExtra("isFeedChange", isFeedChange);
                setResult(1444, data);

            } else {
                Intent data = new Intent();
                data.putExtra("FeedPosition", feedID);
                data.putExtra("FeedRemove", false);
                data.putExtra("isFeedChange", false);
                setResult(1444, data);
            }
            SurveyPopUpActivity.this.finish();
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
