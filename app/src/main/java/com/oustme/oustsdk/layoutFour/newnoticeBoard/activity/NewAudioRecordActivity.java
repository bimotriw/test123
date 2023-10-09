package com.oustme.oustsdk.layoutFour.newnoticeBoard.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.leaderboard.activity.GroupLBDataActivity;
import com.oustme.oustsdk.customviews.CustomTextView;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.io.IOException;
import java.util.Random;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class NewAudioRecordActivity extends AppCompatActivity {

    ImageView buttonStart, buttonStop, buttonConfirm, buttonDelete, buttonPause, buttonPlay;
    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder;
    Random random;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    public static final int RequestPermissionCode = 1;
    MediaPlayer mediaPlayer;
    CountDownTimer countDownTimer;
    CustomTextView mTextViewTimer;
    private CustomTextView textViewLeft, customTextViewRight;
    MediaPlayer record_mediaPlayer;
    private int cnt;
    private boolean paused;
    private int length;
    private SeekBar seekBar;
    private boolean isAudioPlaying;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            OustSdkTools.setLocale(NewAudioRecordActivity.this);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        setContentView(R.layout.activity_audio_record);
        buttonStart = findViewById(R.id.startRecording);
        buttonStop = findViewById(R.id.stopRecording);
        buttonConfirm = findViewById(R.id.confirmRecording);
        buttonPause = findViewById(R.id.pauseRecording);
        buttonPlay = findViewById(R.id.startPlay);
        buttonPause.setVisibility(View.GONE);
        buttonConfirm.setVisibility(View.GONE);
        buttonDelete = findViewById(R.id.deleteRecord);
        seekBar = findViewById(R.id.seekBar);
        buttonStop.setEnabled(false);
        //buttonDelete = findViewById(R.id.delete)
        mTextViewTimer = findViewById(R.id.textViewTimer);
        textViewLeft = findViewById(R.id.textViewLeftText);
        customTextViewRight = findViewById(R.id.textViewRightText);
        random = new Random();
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission()) {
                    AudioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + CreateRandomAudioFileName(5) + "AudioRecording.3gp";
                    MediaRecorderReady();
                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    //textViewLeft.setText("Pause");
                    startTimer(Long.MAX_VALUE);
                    buttonStop.setEnabled(true);
                    buttonStart.setEnabled(false);
                } else {
                    requestPermission();
                }

            }
        });

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPlaying();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", AudioSavePathInDevice);
                setResult(Activity.RESULT_OK, returnIntent);
                if (countDownTimer != null)
                    countDownTimer.cancel();
                finish();

            }
        });
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaRecorder.stop();
                buttonPause.setVisibility(View.GONE);
                buttonStop.setVisibility(View.GONE);
                buttonStart.setVisibility(View.GONE);
                buttonPlay.setVisibility(View.VISIBLE);
                buttonDelete.setVisibility(View.VISIBLE);
                buttonConfirm.setVisibility(View.VISIBLE);
                textViewLeft.setText(""+getResources().getString(R.string.play_text));
                customTextViewRight.setText(""+getResources().getString(R.string.confirm));
                if (countDownTimer != null)
                    countDownTimer.cancel();

            }
        });
        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isAudioPlaying || paused) {
                    buttonPlay.setVisibility(View.GONE);
                    buttonPause.setVisibility(View.VISIBLE);
                    textViewLeft.setText(""+getResources().getString(R.string.pause));
                    //textViewLeft.setText("Pause");
                    playRecording("");
                } else {
                    // buttonPlay.setVisibility(View.VISIBLE);
                    // buttonPause.setVisibility(View.GONE);
                    //pauseRecording();
                }
            }
        });
        buttonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                record_mediaPlayer.pause();
                buttonStart.setEnabled(true);
                buttonStart.setVisibility(View.GONE);
                buttonPlay.setEnabled(true);
                buttonPlay.setVisibility(View.VISIBLE);
                buttonPause.setVisibility(View.GONE);
                pauseRecording();
            }
        });
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioSavePathInDevice = null;
                buttonDelete.setVisibility(View.GONE);
                buttonStart.setVisibility(View.VISIBLE);
                buttonStop.setVisibility(View.VISIBLE);
                buttonStop.setEnabled(false);
                buttonPause.setVisibility(View.GONE);
                buttonPlay.setVisibility(View.GONE);
                buttonConfirm.setVisibility(View.GONE);
                mTextViewTimer.setText("");
                textViewLeft.setText(""+getResources().getString(R.string.record));
                customTextViewRight.setText(""+getResources().getString(R.string.stop));
                buttonStart.setEnabled(true);
                seekBar.setVisibility(View.GONE);
            }
        });
    }

    private void stopPlaying() {
        if (record_mediaPlayer != null) {
            record_mediaPlayer.stop();
            record_mediaPlayer.release();
            record_mediaPlayer = null;
        }

    }

    private void startTimer(Long Value) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
            cnt = 0;
        }
        mTextViewTimer.setVisibility(View.VISIBLE);
        countDownTimer = new CountDownTimer(Value, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                cnt++;
                String time = new Integer(cnt).toString();
                long millis = cnt;
                int seconds = (int) (millis / 60);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                mTextViewTimer.setText(String.format("%02d:%02d", seconds, millis));
            }

            @Override
            public void onFinish() {
                //buttonPlay.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    private void startTimer2(Long Value) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
            cnt = 0;
        }
        countDownTimer = new CountDownTimer(Value, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                cnt++;
                String time = new Integer(cnt).toString();
                long millis = cnt;
                int seconds = (int) (millis / 60);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                mTextViewTimer.setText(String.format("%02d:%02d", seconds, millis));
            }

            @Override
            public void onFinish() {
                //buttonPlay.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    public void MediaRecorderReady() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    private void pauseRecording() {
        try {
            if (record_mediaPlayer != null) {
                //delete.setClickable(true);
                paused = true;
                record_mediaPlayer.pause();
                length = record_mediaPlayer.getCurrentPosition();
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                textViewLeft.setText(""+getResources().getString(R.string.play));
                //Toast.makeText(getActivity(), "audio paused", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void playRecording(String audioPath) {
        try {
            isAudioPlaying = true;
            mTextViewTimer.setVisibility(View.GONE);
            seekBar.setVisibility(View.VISIBLE);
            seekBar.getProgressDrawable().setColorFilter(Color.parseColor("#CC00FF"), PorterDuff.Mode.MULTIPLY);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                seekBar.getThumb().setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.MULTIPLY);
            }
            if (paused && record_mediaPlayer != null) {
                record_mediaPlayer.seekTo(length);
                record_mediaPlayer.start();
                seekBar.setProgress(length + 1);
            } else {
                seekBar.setProgress(0);
                record_mediaPlayer = new MediaPlayer();
                try {
                    record_mediaPlayer.setDataSource(AudioSavePathInDevice);
                    record_mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                final long totalDuration = record_mediaPlayer.getDuration() / 1000;
                int minutes = (int) (totalDuration / 60);
                int seconds = (int) totalDuration % 60;
                //mTextViewTimer.setText(String.format("%02d:%02d", minutes, seconds));
                record_mediaPlayer.start();
                seekBar.setMax((int) totalDuration);
                //startTimer2(totalDuration);
                record_mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        isAudioPlaying = false;
                        buttonPlay.setEnabled(true);
                        buttonPlay.setVisibility(View.VISIBLE);
                        buttonPause.setVisibility(View.GONE);
                        textViewLeft.setText(""+getResources().getString(R.string.play));
                        seekBar.setProgress((int) totalDuration);
                        paused = false;
                    }
                });
                mHandler = new Handler();
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (record_mediaPlayer != null) {
                            int mCurrentPosition = record_mediaPlayer.getCurrentPosition() / 1000;
                            seekBar.setProgress(mCurrentPosition + 1);
                        }

                        mHandler.postDelayed(this, 1000);
                    }
                });
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (record_mediaPlayer != null && fromUser) {
                            record_mediaPlayer.seekTo(progress * 1000);
                        }
                    }
                });
                   /* long totalDuration = record_mediaPlayer.getDuration() / 1000;
                    int minutes = (int) (totalDuration / 60);
                    int seconds = (int) totalDuration % 60;
                    mTextViewTimer.setText(String.format("%02d:%02d", minutes, seconds));
                    startTimer(totalDuration);*/
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public String CreateRandomAudioFileName(int string) {
        StringBuilder stringBuilder = new StringBuilder(string);
        int i = 0;
        while (i < string) {
            stringBuilder.append(RandomAudioFileName.charAt(random.nextInt(RandomAudioFileName.length())));

            i++;
        }
        return stringBuilder.toString();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(NewAudioRecordActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        paused = false;
        isAudioPlaying = false;
        try {
            if (mediaRecorder != null) {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(NewAudioRecordActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(NewAudioRecordActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }
}
