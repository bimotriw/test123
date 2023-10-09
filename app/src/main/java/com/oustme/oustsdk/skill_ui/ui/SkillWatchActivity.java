package com.oustme.oustsdk.skill_ui.ui;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SkillWatchActivity extends AppCompatActivity {

    ImageView close_screen;
    TextView not_used_tv;
    TextView timer_status_tv;
    ImageView timer_circle_iv;
    ImageView play_iv;
    TextView timer_minute_tv;
    TextView timer_secconds_tv;
    TextView count_down_timer_tv;

    long millisecond = 6000;
    //boolean isStart = true;
    long count = 0;
    TextToSpeech textToSpeech;

    CountDownTimer countDownTimer;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OustSdkTools.setLocale(SkillWatchActivity.this);
        setContentView(R.layout.activity_skill_watch);

        count_down_timer_tv = findViewById(R.id.count_down_timer_tv);
        timer_secconds_tv = findViewById(R.id.timer_secconds_tv);
        timer_minute_tv = findViewById(R.id.timer_minute_tv);
        play_iv = findViewById(R.id.play_iv);
        timer_circle_iv = findViewById(R.id.timer_circle_iv);
        timer_status_tv = findViewById(R.id.timer_status_tv);
        not_used_tv = findViewById(R.id.not_used_tv);
        close_screen = findViewById(R.id.close_screen);

        close_screen.setOnClickListener(v -> SkillWatchActivity.this.finish());
        setIconColor("#000000");
        mediaPlayer = MediaPlayer.create(this, R.raw.beep);

        textToSpeech = new TextToSpeech(getApplicationContext(), status -> {
            if (status != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(Locale.getDefault());
            }
        });

        play_iv.setOnClickListener(v -> {

            timer_secconds_tv.setVisibility(View.VISIBLE);
            play_iv.setVisibility(View.GONE);
            timer_circle_iv.setAlpha(1.0f);
            if (count == 0) {

                timer_status_tv.setText(getResources().getString(R.string.prepare_text));
                setIconColor("#DC8744");
            }
            if (count == 1) {

                timer_status_tv.setText(getResources().getString(R.string.go_text));
            }

            countDownTimer(millisecond);
        });

        timer_circle_iv.setOnClickListener(v -> {

            if (play_iv.getVisibility() == View.GONE) {
                play_iv.setVisibility(View.VISIBLE);
                timer_status_tv.setText(getResources().getString(R.string.pause));
                timer_circle_iv.setAlpha(0.5f);
                timerPause();
            }
        });
    }

    private void countDownTimer(long milliseconds) {

        countDownTimer = new CountDownTimer(milliseconds, 1000) {

            public void onTick(long millisUntilFinished) {

                millisecond = millisUntilFinished;

                if (count == 1) {
                    timer_minute_tv.setVisibility(View.VISIBLE);
                }

                String minute = String.format(Locale.getDefault(), "%02d :", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished));
                String seconds = String.format(Locale.getDefault(), "%02d ", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

                String textSeconds = String.format(Locale.getDefault(), "%d ", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                if (millisUntilFinished <= 6000 && millisUntilFinished > 2000) {
                    if (count == 1) {
                        setIconColor("#E82D2E");
                    }
                    textToSpeech.speak(textSeconds, TextToSpeech.QUEUE_FLUSH, null);
                }

                if (millisUntilFinished <= 2000) {
                    if (count == 1) {
                        setIconColor("#E82D2E");
                    }
                    if (mediaPlayer != null) {
                        mediaPlayer.start();
                    }


                }
                timer_minute_tv.setText(minute);
                timer_secconds_tv.setText(seconds);


            }

            public void onFinish() {
                count++;
                if (count == 1) {

                    timer_status_tv.setText(getResources().getString(R.string.go_text));
                    setIconColor("#34C759");
                    millisecond = 26000;
                    countDownTimer(millisecond);
                }

                if (count == 2) {

                    timer_status_tv.setText(getResources().getString(R.string.done_text));
                    setIconColor("#000000");
                    timer_minute_tv.setVisibility(View.INVISIBLE);
                    timer_secconds_tv.setVisibility(View.INVISIBLE);
                    play_iv.setVisibility(View.VISIBLE);
                    millisecond = 6000;
                    count = 0;
                }

            }
        }.start();

    }

    private void setIconColor(String color) {
        Drawable drawable = getResources().getDrawable(R.drawable.ic_skill_timer_circle);
        DrawableCompat.setTint(
                DrawableCompat.wrap(drawable),
                Color.parseColor(color)
        );

        timer_circle_iv.setImageDrawable(drawable);

    }

    public void timerPause() {
        countDownTimer.cancel();
    }

    @Override
    protected void onPause() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        if (countDownTimer != null) {
            timerPause();
        }

        if (mediaPlayer != null) {
            mediaPlayer.pause();
            mediaPlayer.stop();
        }
        super.onPause();
    }
}
