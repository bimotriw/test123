package com.oustme.oustsdk.fragments.courses.adaptive;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import androidx.fragment.app.Fragment;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
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

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.response.course.AdaptiveCourseDataModel;
import com.oustme.oustsdk.response.course.AdaptiveCourseLevelModel;
import com.oustme.oustsdk.response.course.LearningCardResponceData;
import com.oustme.oustsdk.response.course.UserCourseData;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.room.dto.DTOUserCourseData;
import com.oustme.oustsdk.sqlite.UserCourseScoreDatabaseHandler;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;


/**
 * Created by shilpysamaddar on 14/03/17.
 */

public class AdaptiveLearningCard_ResultFragment extends Fragment implements View.OnClickListener {

    RelativeLayout result_finishbtn, result_retrybtn, learningresult_layout, learningquiz_mainlayout;
    TextView levelcomplete_texta;
    TextView learningresult_scoretext;
    TextView learningresult_octext;
    TextView congratation_textviewa;
    TextView learningresult_allscoretext;
    TextView startgot_message;
    ImageView levelcomplete_leaderboardimg, result_backgroundimage, resultstar_imga, resultstar_imgb,
            resultstar_imgc, resultstar_imgd, resultstar_imge, trophy, result_backgroundimage_background;

    //TextView learningcardresult_coinsText, learningresult_allscoretext;

    LinearLayout ocmain_layout, xpmain_layout;

    private ActiveUser activeUser;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragementlearningcard_result, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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

        /*resultstar_imga = (ImageView) view.findViewById(R.id.resultstar_imga);
        resultstar_imgb = (ImageView) view.findViewById(R.id.resultstar_imgb);
        resultstar_imgc = (ImageView) view.findViewById(R.id.resultstar_imgc);
        resultstar_imgd = (ImageView) view.findViewById(R.id.resultstar_imgd);
        resultstar_imge = (ImageView) view.findViewById(R.id.resultstar_imge);
        startgot_message = (TextView) view.findViewById(R.id.startgot_message);*/

        ocmain_layout = view.findViewById(R.id.ocmain_layout);
        xpmain_layout = view.findViewById(R.id.xpmain_layout);

        //learningcardresult_coinsText = (TextView) view.findViewById(R.id.learningcardresult_coinsText);
        //learningresult_allscoretext = (TextView) view.findViewById(R.id.learningresult_allscoretext);

        result_retrybtn = view.findViewById(R.id.result_retrybtn);


        OustSdkTools.setImage(levelcomplete_leaderboardimg, OustSdkApplication.getContext().getResources().getString(R.string.background_result));
        OustSdkTools.setImage(trophy, OustSdkApplication.getContext().getResources().getString(R.string.trophyyello));
        OustSdkTools.setImage(resultstar_imga, OustSdkApplication.getContext().getResources().getString(R.string.popup_stara));
        OustSdkTools.setImage(resultstar_imgb, OustSdkApplication.getContext().getResources().getString(R.string.popup_stara));
        OustSdkTools.setImage(resultstar_imgc, OustSdkApplication.getContext().getResources().getString(R.string.popup_stara));
        OustSdkTools.setImage(resultstar_imgd, OustSdkApplication.getContext().getResources().getString(R.string.popup_stara));
        OustSdkTools.setImage(resultstar_imge, OustSdkApplication.getContext().getResources().getString(R.string.popup_stara));
        OustSdkTools.setImage(result_backgroundimage, OustSdkApplication.getContext().getResources().getString(R.string.bg_1));

        result_finishbtn.setOnClickListener(this);
        result_retrybtn.setOnClickListener(this);

    }

    private String backgroundImage;

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    private AdaptiveCourseDataModel courseDataClass;
    int scoredOc;

    public void setCourseDataClass(AdaptiveCourseDataModel courseDataClass) {
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

    private AdaptiveLearningModuleInterface adaptiveLearningModuleInterface;

    public void setAdaptiveLearningModuleInterface(AdaptiveLearningModuleInterface learningModuleInterface) {
        this.adaptiveLearningModuleInterface = learningModuleInterface;
    }

    private AdaptiveCourseLevelModel courseLevelClass;

    public void setCourseLevelClass(AdaptiveCourseLevelModel courseLevelClass) {
        this.courseLevelClass = courseLevelClass;
    }


//    ================================================================================================

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
            if (learningCardResponceDatas != null) {
                for (int i = 0; i < learningCardResponceDatas.length; i++) {
                    if (learningCardResponceDatas[i] != null) {
                        if (learningCardResponceDatas[i].getCourseId() == 0) {
                            try {
                                int currentLevelNo = (int) (courseLevelClass.getSequence() - 1);
                                totalXp += userCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).getOc();
                            } catch (Exception e) {
                            }
                        } else {
                            totalXp += learningCardResponceDatas[i].getXp();
                        }
                    }
                }
            }
            //learningresult_allscoretext.setText("/" + courseTotalXp);
            if (totalOc > 0) {
                ocmain_layout.setVisibility(View.VISIBLE);
                String yourCoinsText = "" + totalOc + "/" + courseTotalOc;
                if (yourCoinsText.contains("/")) {
                    String[] spilt = yourCoinsText.split("/");
                    Spannable yourScoreSpan = new SpannableString(yourCoinsText);
                    yourScoreSpan.setSpan(new RelativeSizeSpan(0.75f), spilt[0].length(), yourCoinsText.length(), 0);
                    learningresult_octext.setText(yourCoinsText);
                } else {
                    learningresult_octext.setText(yourCoinsText);
                }
            }
            if ((courseDataClass.isZeroXpForLCard() && courseDataClass.isZeroXpForQCard()) || (courseTotalXp == 0)) {
                xpmain_layout.setVisibility(View.GONE);
                setUserCurrentRating(true);
            } else {
                setUserCurrentRating(false);
            }
            levelcomplete_texta.setText("Level " + courseLevelClass.getSequence() + " Completed");

            if (!isValidLbResetPeriod()) {
                learningresult_layout.setVisibility(View.INVISIBLE);
            }

            //changeCoinText();
            setCardAnimation();
            playLevelCompleteAudio();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    /*private void changeCoinText() {
        if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 1) {
            learningcardresult_coinsText.setText(OustStrings.getString("credits"));
        }
    }*/

    private void playLevelCompleteAudio() {
        if (!courseDataClass.isDisableCourseCompleteAudio()) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        File file = new File(OustSdkApplication.getContext().getFilesDir(), "game_comeback_resultcard.mp3");
                        if (file != null && file.exists()) {
                            MediaPlayer player = MediaPlayer.create(getActivity(), Uri.fromFile(file));
                            player.start();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
            }, 700);
        }
    }

    private void setCoinsAnimation() {
        ValueAnimator animator1 = new ValueAnimator();
        animator1.setObjectValues(0, (totalScoredCoins));
        animator1.setDuration(1200);
        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                learningresult_scoretext.setText("" + (((int) animation.getAnimatedValue())));
            }
        });
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
                    learningresult_octext.setText(yourCoinsText);
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
            // resultstar_imga.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.popup_star));
            //OustSdkTools.setImage(resultstar_imga, OustSdkApplication.getContext().getResources().getString(R.string.popup_star));
        }
        if (rating > 1) {
            congratation_textviewa.setText(OustStrings.getString("tryharder_resultmsg"));
            //OustSdkTools.setImage(resultstar_imgb, OustSdkApplication.getContext().getResources().getString(R.string.popup_star));
        }
        if (rating > 2) {
            congratation_textviewa.setText(OustStrings.getString("greatjob_result"));
            //OustSdkTools.setImage(resultstar_imgc, OustSdkApplication.getContext().getResources().getString(R.string.popup_star));
        }
        if (rating > 3) {
            congratation_textviewa.setText(OustStrings.getString("fantastic_resultmsg"));
            //OustSdkTools.setImage(resultstar_imgd, OustSdkApplication.getContext().getResources().getString(R.string.popup_star));
        }
        if (rating > 4) {
            OustSdkTools.setImage(resultstar_imge, OustSdkApplication.getContext().getResources().getString(R.string.popup_star));
            //congratation_textviewa.setText(OustStrings.getString("learning_congratutationtext"));
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
                adaptiveLearningModuleInterface.levelComplete();
            } else if (id == R.id.result_retrybtn) {
                adaptiveLearningModuleInterface.restart();
                //learningModuleInterface.restartActivity();
            }
        } catch (Exception e) {
        }
    }

    private void setViewBackgroundImage() {
        try {
            OustSdkTools.setImage(result_backgroundimage, getResources().getString(R.string.bg_1));
            if (backgroundImage != null && !backgroundImage.isEmpty()) {
                result_backgroundimage_background.setVisibility(View.VISIBLE);
                if ((OustSdkTools.checkInternetStatus()) && (OustStaticVariableHandling.getInstance().isNetConnectionAvailable())) {
                    Picasso.get().load(backgroundImage).into(result_backgroundimage_background);
                } else {
                    Picasso.get().load(backgroundImage).networkPolicy(NetworkPolicy.OFFLINE).into(result_backgroundimage_background);
                }
            }
        } catch (Exception e) {
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
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
        }
    }

    int totalScoredCoins;

    public void setScoredOc(int totalPoints) {
        this.totalScoredCoins = totalPoints;
    }


    private boolean isValidLbResetPeriod() {
        try {
            long courseUniqNo = OustStaticVariableHandling.getInstance().getCourseUniqNo();
            DTOUserCourseData userCourseData = new UserCourseScoreDatabaseHandler().getScoreById(courseUniqNo);

            String lbResetPeriod = OustPreferences.get(AppConstants.StringConstants.LB_RESET_PERIOD);
            lbResetPeriod = lbResetPeriod.toUpperCase();
            String addedOn = userCourseData.getAddedOn();

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
}

