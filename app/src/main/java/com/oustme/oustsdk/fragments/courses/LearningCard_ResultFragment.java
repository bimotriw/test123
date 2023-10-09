package com.oustme.oustsdk.fragments.courses;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.COURSE_LEVEL_AUDIO_PATH;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.response.course.LearningCardResponceData;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.room.dto.DTOUserCourseData;
import com.oustme.oustsdk.sqlite.UserCourseScoreDatabaseHandler;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.appconstants.AppConstants;

import java.io.File;
import java.util.Objects;

/**
 * Created by shilpysamaddar on 14/03/17.
 */

public class LearningCard_ResultFragment extends Fragment implements View.OnClickListener {

    RelativeLayout learningquiz_mainlayout;
    TextView levelcomplete_texta, learningresult_scoretext, learningresult_octext, congratation_textviewa;//learningresult_allscoretext
    ImageView levelcomplete_leaderboardimg, result_backgroundimage, trophy, result_backgroundimage_background;
    LinearLayout ocmain_layout, learningresult_layout;
    View view_coins;
    LinearLayout xpmain_layout, result_finishbtn, result_retrybtn;
    ImageView replay_iv, thumbImage;
    TextView replay_tv;
    private boolean isScoreDisplaySecondTime;

    MediaPlayer levelAudioPlayer;

    private ActiveUser activeUser;
    private boolean enableLevelCompleteAudio;

    public boolean isEnableLevelCompleteAudio() {
        return enableLevelCompleteAudio;
    }

    public void setEnableLevelCompleteAudio(boolean enableLevelCompleteAudio) {
        Log.e("TAG", "setEnableLevelCompleteAudio: " + enableLevelCompleteAudio);
        this.enableLevelCompleteAudio = enableLevelCompleteAudio;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragementlearningcard_result, container, false);
        Objects.requireNonNull(requireActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initViews(view);
        showResult();
        return view;
    }

    private void initViews(View view) {
        result_finishbtn = view.findViewById(R.id.result_finishbtn);
        levelcomplete_texta = view.findViewById(R.id.levelcomplete_texta);
        learningresult_scoretext = view.findViewById(R.id.learningresult_scoretext);
        learningresult_octext = view.findViewById(R.id.learningresult_octext);
        learningresult_layout = view.findViewById(R.id.learningresult_layout);
        congratation_textviewa = view.findViewById(R.id.congratation_textviewa);
        levelcomplete_leaderboardimg = view.findViewById(R.id.levelcomplete_leaderboardimg);
        trophy = view.findViewById(R.id.trophy);
        learningquiz_mainlayout = view.findViewById(R.id.learningquiz_mainlayout);
        result_backgroundimage = view.findViewById(R.id.result_backgroundimage);
        result_backgroundimage_background = view.findViewById(R.id.result_backgroundimage_background);


        view_coins = view.findViewById(R.id.view_coins);
        ocmain_layout = view.findViewById(R.id.ocmain_layout);
        xpmain_layout = view.findViewById(R.id.xpmain_layout);
        replay_iv = view.findViewById(R.id.replay_iv);
        thumbImage = view.findViewById(R.id.thumbImage);
        replay_tv = view.findViewById(R.id.replay_tv);

        result_retrybtn = view.findViewById(R.id.result_retrybtn);

        setIconColor();

        if (isCourseComplete) {
            if (isScoreDisplaySecondTime) {
                learningresult_layout.setVisibility(View.VISIBLE);
            } else {
                learningresult_layout.setVisibility(View.GONE);
            }
        }

        if (!isValidLbResetPeriod()) {
            learningresult_layout.setVisibility(View.GONE);
        }

        OustSdkTools.setImage(levelcomplete_leaderboardimg, OustSdkApplication.getContext().getResources().getString(R.string.background_result));
        OustSdkTools.setImage(trophy, OustSdkApplication.getContext().getResources().getString(R.string.trophyyello));

        result_finishbtn.setOnClickListener(this);
        result_retrybtn.setOnClickListener(this);

    }

    private String courseColnId;

    public void setCourseColnId(String courseColnId) {
        this.courseColnId = courseColnId;
    }

    private String coursebgImg;

    public void setCoursebgImg(String coursebgImg) {
        this.coursebgImg = coursebgImg;
    }

    private String backgroundImage;

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    private boolean isCourseComplete;

    public void setCourseComplete(boolean isCourseComplete) {
        this.isCourseComplete = isCourseComplete;
    }

    public void setScoreVisibility(boolean isScoreDisplaySecondTime) {
        this.isScoreDisplaySecondTime = isScoreDisplaySecondTime;
    }

    private CourseDataClass courseDataClass;

    public void setCourseDataClass(CourseDataClass courseDataClass) {
        this.courseDataClass = courseDataClass;
    }

    private int totalOc = 0;
    private int totalXp = 0;

    public void setTotalOc(long totalOc) {
        this.totalOc = (int) totalOc;
    }

    private int courseTotalXp = 0;
    private int courseTotalOc = 0;

    public void setCourseTotalOc(int courseTotalOc) {
        this.courseTotalOc = courseTotalOc;
    }

    public void setCourseTotalXp(int courseTotalXp) {
        this.courseTotalXp = courseTotalXp;
    }


    private LearningCardResponceData[] learningCardResponceDatas;

    public void setLearningCardResponceDatas(LearningCardResponceData[] learningCardResponceDatas) {
        this.learningCardResponceDatas = learningCardResponceDatas;
    }

    private LearningModuleInterface learningModuleInterface;

    public void setLearningModuleInterface(LearningModuleInterface learningModuleInterface) {
        this.learningModuleInterface = learningModuleInterface;
    }

    private CourseLevelClass courseLevelClass;

    public void setCourseLevelClass(CourseLevelClass courseLevelClass) {
        this.courseLevelClass = courseLevelClass;
    }

    public void showResult() {
        try {
            activeUser = OustAppState.getInstance().getActiveUser();
            if ((activeUser != null) && (activeUser.getStudentid() != null)) {
            } else {
                String activeUserGet = OustPreferences.get("userdata");
                activeUser = OustSdkTools.getActiveUserData(activeUserGet);
            }

            DTOUserCourseData userCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
            setViewBackgroundImage();


            if (userCourseData != null && userCourseData.getUserLevelDataList() != null &&
                    userCourseData.getUserLevelDataList().size() != 0 &&
                    userCourseData.getUserLevelDataList().get((int) courseLevelClass.getSequence() - 1) != null) {
                totalXp = (int) userCourseData.getUserLevelDataList().get((int) courseLevelClass.getSequence() - 1).getXp();
                boolean isLevelAlreadyCompleted = userCourseData.getUserLevelDataList().get((int) courseLevelClass.getSequence() - 1).isLevelCompleted();
                if (isLevelAlreadyCompleted) {
                    learningresult_layout.setVisibility(View.GONE);
                }
            } else {
                if (learningCardResponceDatas != null) {
                    for (int i = 0; i < learningCardResponceDatas.length; i++) {
                        if (learningCardResponceDatas[i] != null) {
                            if (learningCardResponceDatas[i].getCourseId() == 0) {
                                try {
                                    int currentLevelNo = (int) (courseLevelClass.getSequence() - 1);
                                    assert userCourseData != null;
                                    totalXp += userCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).getOc();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    OustSdkTools.sendSentryException(e);
                                }
                            } else {
                                totalXp += learningCardResponceDatas[i].getXp();
                            }
                        }
                    }
                }
            }

            if (totalOc > 0) {
                ocmain_layout.setVisibility(View.VISIBLE);
                view_coins.setVisibility(View.VISIBLE);
                String yourCoinsText = "" + totalOc + "/" + courseTotalOc;
                if (yourCoinsText.contains("/")) {
                    String[] spilt = yourCoinsText.split("/");
                    Spannable yourScoreSpan = new SpannableString(yourCoinsText);
                    yourScoreSpan.setSpan(new RelativeSizeSpan(0.75f), spilt[0].length(), yourCoinsText.length(), 0);
                    learningresult_octext.setText(yourScoreSpan);
                } else {
                    learningresult_octext.setText(yourCoinsText);
                }
            } else {
                view_coins.setVisibility(View.GONE);
            }
            if ((courseDataClass.isZeroXpForLCard() && courseDataClass.isZeroXpForQCard()) || (courseTotalXp == 0)) {
                xpmain_layout.setVisibility(View.GONE);
                view_coins.setVisibility(View.GONE);
                setUserCurrentRating(true);
            } else {
                setUserCurrentRating(false);
            }
            String levelCompleted;
            if (courseDataClass != null && courseDataClass.getCourseLevelClassList() != null && courseDataClass.getCourseLevelClassList().size() == 1) {
                levelCompleted = courseLevelClass.getLevelName().trim() + " : " + getResources().getString(R.string.completed);
            } else {
                levelCompleted = getResources().getString(R.string.level) + " " + courseLevelClass.getSequence() + " : " + getResources().getString(R.string.completed);
            }
            levelcomplete_texta.setText(levelCompleted);
            setCardAnimation();
            playLevelCompleteAudio();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        try {
            DTOUserCourseData dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
            if (dtoUserCourseData != null && dtoUserCourseData.getUserLevelDataList() != null) {
                for (int i = 0; i < dtoUserCourseData.getUserLevelDataList().size(); i++) {
                    if (dtoUserCourseData.getUserLevelDataList().get(i).getLevelId() == courseLevelClass.getLevelId()) {
                        Log.d("TAG", "showResult: getLevelId --> " + dtoUserCourseData.getUserLevelDataList().get(i).getLevelId());
                        if (!dtoUserCourseData.getUserLevelDataList().get(i).isLevelCompleted()) {
                            dtoUserCourseData.getUserLevelDataList().get(i).setLevelCompleted(true);
                        }
                        if (!dtoUserCourseData.getUserLevelDataList().get(i).isLastCardComplete()) {
                            dtoUserCourseData.getUserLevelDataList().get(i).setIslastCardComplete(true);
                        }
                        RoomHelper.addorUpdateScoreDataClass(dtoUserCourseData);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void playLevelCompleteAudio() {
        Log.e("TAG", "playLevelCompleteAudio: " + enableLevelCompleteAudio);
        if (enableLevelCompleteAudio) {
            new Handler().postDelayed(() -> {
                try {
                    //String filename = OustPreferences.get(COURSE_LEVEL_AUDIO_PATH);
                    boolean isPlayDefault = false;
                    if (OustPreferences.get(COURSE_LEVEL_AUDIO_PATH) != null) {
                        File file = new File(OustSdkApplication.getContext().getFilesDir(), "oustlearn_" + OustMediaTools.getMediaFileName(OustPreferences.get(COURSE_LEVEL_AUDIO_PATH)));
                        if (file.exists()) {
                            levelAudioPlayer = MediaPlayer.create(getActivity(), Uri.fromFile(file));
                            levelAudioPlayer.start();
                        } else {
                            isPlayDefault = true;
                        }
                    } else {
                        isPlayDefault = true;
                    }

                    if (isPlayDefault) {
                        File file2 = new File(OustSdkApplication.getContext().getFilesDir(), "game_comeback_resultcard.mp3");
                        if (file2.exists()) {
                            Log.e("TAG", "playLevelCompleteAudio:------- " + file2);
                            levelAudioPlayer = MediaPlayer.create(getActivity(), Uri.fromFile(file2));
                            levelAudioPlayer.start();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            }, 100);
        }
    }

    private void setCoinsAnimation() {
        Log.e("TAG", "setCoinsAnimation");
        ValueAnimator animator1 = new ValueAnimator();
        animator1.setObjectValues(0, (totalXp));
        animator1.setDuration(1200);
        animator1.addUpdateListener(animation -> learningresult_scoretext.setText("" + (((int) animation.getAnimatedValue()))));
        animator1.start();
        animator1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                String yourScoreText = "" + totalXp + "/" + courseTotalXp;
                if (yourScoreText.contains("/")) {
                    String[] spilt = yourScoreText.split("/");
                    Spannable yourScoreSpan = new SpannableString(yourScoreText);
                    yourScoreSpan.setSpan(new RelativeSizeSpan(0.75f), spilt[0].length(), yourScoreText.length(), 0);
                    learningresult_scoretext.setText(yourScoreSpan);
                } else {
                    learningresult_scoretext.setText(yourScoreText);
                }

                String yourCoinsText = "" + totalOc + "/" + courseTotalOc;
                if (yourCoinsText.contains("/")) {
                    String[] spilt = yourCoinsText.split("/");
                    Spannable yourScoreSpan = new SpannableString(yourCoinsText);
                    yourScoreSpan.setSpan(new RelativeSizeSpan(0.75f), spilt[0].length(), yourCoinsText.length(), 0);
                    learningresult_octext.setText(yourScoreSpan);
                } else {
                    learningresult_octext.setText(yourCoinsText);
                }

                setLBIconAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
    }

    private void setUserCurrentRating(boolean showFullStar) {
        float myXp = (float) totalXp;
        float totalXp = (float) courseTotalXp;
        float rating = ((myXp / totalXp) * 5);
        if (showFullStar) {
            rating = 5;
        } else {
            rating = Math.round(rating);
        }
        if (rating > 0 || rating == 0) {
            congratation_textviewa.setText(OustStrings.getString("startover_resultmsg"));
        }
        if (rating > 1) {
            congratation_textviewa.setText(OustStrings.getString("tryharder_resultmsg"));
        }
        if (rating > 2) {
            congratation_textviewa.setText(OustStrings.getString("greatjob_result"));
        }
        if (rating > 3) {
            congratation_textviewa.setText(OustStrings.getString("fantastic_resultmsg"));
        }
        if (rating > 4) {
            congratation_textviewa.setText(OustStrings.getString("congratulations_text"));
        }
    }

    @Override
    public void onClick(final View v) {
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(v, "scaleX", 1.0f, 0.9f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(v, "scaleY", 1.0f, 0.9f);
        scaleDownX.setDuration(200);
        scaleDownY.setDuration(200);
        scaleDownX.setRepeatCount(1);
        scaleDownY.setRepeatCount(1);
        scaleDownX.setRepeatMode(ValueAnimator.REVERSE);
        scaleDownY.setRepeatMode(ValueAnimator.REVERSE);
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
                animFinish(v);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
    }

    private void animFinish(View view) {
        try {
            int id = view.getId();
            if (id == R.id.result_finishbtn) {
                learningModuleInterface.endActivity();
            } else if (id == R.id.result_retrybtn) {
                learningModuleInterface.restartActivity();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setViewBackgroundImage() {
        try {
            if (backgroundImage != null && !backgroundImage.isEmpty()) {
                result_backgroundimage_background.setVisibility(View.VISIBLE);
                Glide.with(Objects.requireNonNull(requireActivity())).load(backgroundImage).diskCacheStrategy(DiskCacheStrategy.DATA).into(result_backgroundimage_background);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            if (levelAudioPlayer != null && levelAudioPlayer.isPlaying()) {
                levelAudioPlayer.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (levelAudioPlayer != null && levelAudioPlayer.isPlaying()) {
                levelAudioPlayer.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setCardAnimation() {
        try {
            learningquiz_mainlayout.setAlpha(0);
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(learningquiz_mainlayout, "scaleX", 0.4f, 1.0f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(learningquiz_mainlayout, "scaleY", 0.4f, 1.0f);
            ObjectAnimator scaleDownZ = ObjectAnimator.ofFloat(learningquiz_mainlayout, "alpha", 0.0f, 1.0f);
            scaleDownX.setDuration(400);
            scaleDownY.setDuration(400);
            scaleDownZ.setDuration(150);
            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleDownX).with(scaleDownY).with(scaleDownZ);
            scaleDown.setStartDelay(800);
            scaleDown.setInterpolator(new DecelerateInterpolator());
            scaleDown.start();
            scaleDown.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    setCoinsAnimation();
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setLBIconAnimation() {
        try {
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(levelcomplete_leaderboardimg, "scaleX", 0.9f, 1.1f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(levelcomplete_leaderboardimg, "scaleY", 0.9f, 1.1f);
            scaleDownX.setDuration(800);
            scaleDownY.setDuration(800);
            scaleDownX.setRepeatMode(ValueAnimator.REVERSE);
            scaleDownY.setRepeatMode(ValueAnimator.REVERSE);
            scaleDownX.setRepeatCount(ValueAnimator.INFINITE);
            scaleDownY.setRepeatCount(ValueAnimator.INFINITE);
            scaleDownX.setInterpolator(new LinearInterpolator());
            scaleDownY.setInterpolator(new LinearInterpolator());
            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleDownX).with(scaleDownY);
            scaleDown.setStartDelay(1500);
            scaleDown.start();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean isValidLbResetPeriod() {
        try {
            long courseUniqNo = OustStaticVariableHandling.getInstance().getCourseUniqNo();
            DTOUserCourseData userCourseData = new UserCourseScoreDatabaseHandler().getScoreById(courseUniqNo);

            String lbResetPeriod = OustPreferences.get(AppConstants.StringConstants.LB_RESET_PERIOD);
            lbResetPeriod = lbResetPeriod.toUpperCase();
            String addedOn = userCourseData.getAddedOn();
            if (addedOn == null || addedOn.isEmpty()) {
                addedOn = userCourseData.getEnrollmentDate();
            }

            switch (lbResetPeriod) {
                case "MONTHLY":
                    return OustSdkTools.isCurrentMonth(addedOn);
                case "QUARTERLY":
                    return OustSdkTools.isCurrentQuater(addedOn);
                case "YEARLY":
                    return OustSdkTools.isCurrentYear(addedOn);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        return true;
    }

    private void setIconColor() {

        Drawable drawable = getResources().getDrawable(R.drawable.course_button_bg);
        Drawable replay = getResources().getDrawable(R.drawable.ic_replay);
        Drawable thumbDrawable = getResources().getDrawable(R.drawable.ic_thumbs_up);

        result_finishbtn.setBackground(OustSdkTools.drawableColor(drawable));
        replay_iv.setImageDrawable(OustSdkTools.drawableColor(replay));
        thumbImage.setImageDrawable(OustSdkTools.drawableColor(thumbDrawable));
        String toolbarColorCode = OustPreferences.get("toolbarColorCode");

        if (toolbarColorCode == null || toolbarColorCode.isEmpty()) {
            toolbarColorCode = "#01b5a2";
        }
        replay_tv.setTextColor(Color.parseColor(toolbarColorCode));
    }
}

