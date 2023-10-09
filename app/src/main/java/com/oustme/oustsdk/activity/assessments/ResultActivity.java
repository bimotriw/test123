package com.oustme.oustsdk.activity.assessments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.PopupActivity;
import com.oustme.oustsdk.activity.common.leaderboard.activity.GroupLBDataActivity;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.interfaces.common.OnNetworkChangeListener;
import com.oustme.oustsdk.request.AssmntGamePlayRequest;
import com.oustme.oustsdk.request.GroupResultRequest;
import com.oustme.oustsdk.request.SubmitRequest;
import com.oustme.oustsdk.response.assessment.CreateGameResponse;
import com.oustme.oustsdk.response.assessment.GamePoints;
import com.oustme.oustsdk.response.assessment.GroupResultResponse;
import com.oustme.oustsdk.response.assessment.UserGamePoints;
import com.oustme.oustsdk.response.common.CurrentGameInfo;
import com.oustme.oustsdk.response.common.GameType;
import com.oustme.oustsdk.response.common.OustPopupButton;
import com.oustme.oustsdk.response.common.OustPopupCategory;
import com.oustme.oustsdk.response.common.OustPopupType;
import com.oustme.oustsdk.response.common.PlayResponse;
import com.oustme.oustsdk.response.common.Popup;
import com.oustme.oustsdk.response.common.QuestionType;
import com.oustme.oustsdk.room.OustDBBuilder;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustCommonUtils;
import com.oustme.oustsdk.tools.OustGATools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static com.oustme.oustsdk.response.common.GameType.ACCEPT;
import static com.oustme.oustsdk.response.common.GameType.ACCEPTCONTACTSCHALLENGE;
import static com.oustme.oustsdk.response.common.GameType.CHALLENGE;
import static com.oustme.oustsdk.response.common.GameType.CONTACTSCHALLENGE;
import static com.oustme.oustsdk.response.common.GameType.GROUP;
import static com.oustme.oustsdk.response.common.GameType.MYSTERY;
import static com.oustme.oustsdk.response.common.GameType.REMATCH;

/**
 * Created by shilpysamaddar on 16/03/17.
 */

public class ResultActivity extends AppCompatActivity implements OnNetworkChangeListener,View.OnClickListener {

    private static final String TAG = "ResultActivity";
    
    private ActiveGame activeGame;
    private ActiveUser activeUser;

    private ImageView imageView,challengerAvatarImg,opponentAvatarImg,resultSymbol,challengeCup,registerImage,shareImage;
    private TextView groupAvatar,groupName,resultTitle,resultText,resultScore,rightUserScore,
            wrongUserScore,xpValueUserScore,rightOpponentScore,wrongOpponentScore,xpValueOpponentScore
            ,userName,opponentName,right,wrong,xpValue,txtShare,txtNotificationMessage,subjectname,
            modulename,topicname,modulecomplete_txt,xpValueOpponentScoreBottom,
            xpValueOpponentScoreCenter,wrongOpponentScoreBottom,wrongOpponentScoreCenter,
            rightOpponentBottom,rightOpponentCenter,xpValueUserScoreCenter,xpValueUserScoreBottom,
            wrongUserScoreBottom,wrongUserScoreCenter,rightUserBottom,xpValueBottom,
            xpValueCenter,wrongBottom,wrongCenter,rightBottom,rightCenter,rightUserCenter,
            loadingtxt,modulecompletelable,txtRegister,waitingTxt,versus;
    private RelativeLayout topLayout,scoreWaitingLayout,opponentAvatarImgLayout,scoreLayout,groupResultLayout
            ,resultTextLayout,mainLayout,rematchButtonlayout,waitingButtonlayout,registerButton,
            playAgainButtonlayout,modtxt,moduleprogress_layout,allscoreLayout,mainView,
            livecontestLayout,buttonLayout,grp_ref,check_ref;
    private View challengerAvatarLayout;
    private LinearLayout checkResultLayout,resultLayout,baseLayout;
    private Button answerButton,rematchButton,waitingButton,allScoresBtn,subscriberButton;
    private ImageButton btnClose,playAgainButton;
    private ListView groupList,allGroupList;
    private ProgressBar moduleprogress;


    private GroupResultResponse allGrpList;

    private PlayResponse playResponse;
    private SubmitRequest submitRequest;
    private GroupResultRequest groupResultRequest;

    private GamePoints gamePoints;
    private UserGamePoints userGamePoints;

    private long challengerScore;
    private long opponentScore;
    private long challengerFinalScore;
    private long opponentFinalScore;

    private Gson gson;
    private boolean isFinished = false;

    private CreateGameResponse createGameResponse;

    private MediaPlayer mediaPlayer;
    private FirebaseRefClass modulecompleteListClass;
    private boolean isSoundEnabled = false;

    private CurrentGameInfo currentGameInfo;

    private BitmapDrawable bd=OustSdkTools.getImageDrawable(getResources().getString(R.string.profile_image));
    private BitmapDrawable bd_loading=OustSdkTools.getImageDrawable(getResources().getString(R.string.profile_image_loading));



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.result);
        initViews();
        initResult();
//        OustGATools.getInstance().reportPageViewToGoogle(ResultActivity.this,"ResultActivity");

    }

    private void initViews() {

        Log.d(TAG, "initViews: ");
        try{
            OustSdkTools.setLocale(ResultActivity.this);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        
        imageView= findViewById(R.id.resultSymbol);
        challengerAvatarImg= findViewById(R.id.challengerAvatarImg);
        opponentAvatarImg= findViewById(R.id.opponentAvatarImg);
        groupAvatar= findViewById(R.id.groupAvatar);
        groupName= findViewById(R.id.groupName);
        resultTitle= findViewById(R.id.resultTitle);
        resultText= findViewById(R.id.resultText);
        resultScore= findViewById(R.id.resultScore);
        topLayout= findViewById(R.id.topLayout);
        scoreWaitingLayout= findViewById(R.id.scoreWaitingLayout);
        challengerAvatarLayout=findViewById(R.id.challengerAvatarLayout);
        opponentAvatarImgLayout= findViewById(R.id.opponentAvatarImgLayout);
        scoreLayout= findViewById(R.id.scoreLayout);
        checkResultLayout= findViewById(R.id.checkResultLayout);
        groupResultLayout= findViewById(R.id.groupResultLayout);
        resultTextLayout= findViewById(R.id.resultTextLayout);
        resultLayout= findViewById(R.id.ResultLayout);
        mainLayout= findViewById(R.id.mainLayout);
        rightUserScore= findViewById(R.id.txtRightUserScore);
        wrongUserScore= findViewById(R.id.txtWrongUserScore);
        xpValueUserScore= findViewById(R.id.txtXpValueUserScore);
        rightOpponentScore= findViewById(R.id.txtRightOpponentScore);
        wrongOpponentScore= findViewById(R.id.txtWrongOpponentScore);
        xpValueOpponentScore= findViewById(R.id.txtXpValueOpponentScore);
        userName= findViewById(R.id.challengerName);
        opponentName= findViewById(R.id.opponentName);
        txtShare= findViewById(R.id.txtShare);
        answerButton= findViewById(R.id.answerButton);
        rematchButton= findViewById(R.id.rematchButton);
        rematchButtonlayout= findViewById(R.id.rematchButtonlayout);
        waitingButton= findViewById(R.id.waitingButton);
        waitingButtonlayout= findViewById(R.id.waitingButtonlayout);
        playAgainButton= findViewById(R.id.playAgainButton);
        OustSdkTools.setImage(playAgainButton,getResources().getString(R.string.planexticon));
        registerButton= findViewById(R.id.registerButton);
        resultSymbol= findViewById(R.id.resultSymbol);
        groupList= findViewById(R.id.groupList);
        txtNotificationMessage= findViewById(R.id.txtNotificationMessage);
        btnClose= findViewById(R.id.btnClose);
        moduleprogress= findViewById(R.id.moduleprogress);
        subjectname= findViewById(R.id.result_subjectname);
        modulename= findViewById(R.id.result_modulelongname);
        topicname= findViewById(R.id.result_topicname);
        modulecomplete_txt= findViewById(R.id.modulecomplete_txt);
        modtxt= findViewById(R.id.modtxt);
        moduleprogress_layout= findViewById(R.id.moduleprogress_layout);
        xpValueOpponentScoreBottom= findViewById(R.id.xpValueOpponentScoreBottom);
        xpValueOpponentScoreCenter= findViewById(R.id.xpValueOpponentScoreCenter);
        wrongOpponentScoreBottom= findViewById(R.id.wrongOpponentScoreBottom);
        wrongOpponentScoreCenter= findViewById(R.id.wrongOpponentScoreCenter);
        rightOpponentBottom= findViewById(R.id.rightOpponentBottom);
        rightOpponentCenter= findViewById(R.id.rightOpponentCenter);
        xpValueUserScoreCenter= findViewById(R.id.xpValueUserScoreCenter);
        xpValueUserScoreBottom= findViewById(R.id.xpValueUserScoreBottom);
        wrongUserScoreBottom= findViewById(R.id.wrongUserScoreBottom);
        wrongUserScoreCenter= findViewById(R.id.wrongUserScoreCenter);
        rightUserBottom= findViewById(R.id.rightUserBottom);
        xpValueBottom= findViewById(R.id.xpValueBottom);
        xpValueCenter= findViewById(R.id.xpValueCenter);
        wrongBottom= findViewById(R.id.wrongBottom);
        wrongCenter= findViewById(R.id.wrongCenter);
        rightBottom= findViewById(R.id.rightBottom);
        rightCenter= findViewById(R.id.rightCenter);
        rightUserCenter= findViewById(R.id.rightUserCenter);
        loadingtxt= findViewById(R.id.loadingtxt);
        baseLayout= findViewById(R.id.baseLayout);
        versus= findViewById(R.id.versus);
        modulecompletelable= findViewById(R.id.modulecompletelable);
        txtRegister= findViewById(R.id.txtRegister);
        allscoreLayout= findViewById(R.id.allscoreLayout);
        allScoresBtn= findViewById(R.id.allScoresBtn);
        allGroupList= findViewById(R.id.allGroupList);
        mainView= findViewById(R.id.mainScrollView);
        livecontestLayout= findViewById(R.id.livecontestLayout);
        waitingTxt= findViewById(R.id.waitingTxt);
        buttonLayout= findViewById(R.id.button_layout);
        grp_ref= findViewById(R.id.grp_ref);
        check_ref= findViewById(R.id.check_ref);
        subscriberButton= findViewById(R.id.subscriberButton);
        challengeCup= findViewById(R.id.challengeCup);
        registerImage= findViewById(R.id.registerImage);
        shareImage= findViewById(R.id.shareImage);

        OustSdkTools.setImage(challengeCup,getResources().getString(R.string.challenge_cup));
//        OustSdkTools.setBanner(registerImage,getResources().getString(R.string.registerImage));
        OustSdkTools.setImage(shareImage,getResources().getString(R.string.share_text));

        registerButton.setOnClickListener(this);
        allScoresBtn.setOnClickListener(this);
        rematchButton.setOnClickListener(this);
        playAgainButton.setOnClickListener(this);
        subscriberButton.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        answerButton.setOnClickListener(this);

    }

    public void initResult() {
        try {
            Log.d(TAG, "initResult: ");
            OustSdkTools.setSnackbarElements(baseLayout, ResultActivity.this);
            OustSdkTools.checkInternetStatus();

            OustAppState.getInstance().setHasPopup(false);
            challengerFinalScore = 0;
            opponentFinalScore = 0;
            Log.d(TAG, "Result loaded");


            isFinished = true;

            gson = new GsonBuilder().create();

            Intent CallingIntent = getIntent();
            activeUser=OustAppState.getInstance().getActiveUser();
            activeGame = gson.fromJson(CallingIntent.getStringExtra("GamePoints"),ActiveGame.class);
            gamePoints = gson.fromJson(CallingIntent.getStringExtra("GamePoints"),GamePoints.class);
            if (gamePoints != null) {
                isSoundEnabled = false;
                Log.d(TAG, gamePoints.toString());
            }
            if((CallingIntent.getStringExtra("UserGamePoints"))!=null) {
                userGamePoints =gson.fromJson(CallingIntent.getStringExtra("GamePoints"),UserGamePoints.class);
                if (userGamePoints != null) {
                    isSoundEnabled = false;
                }
            }

            submitRequest = gson.fromJson(CallingIntent.getStringExtra("GamePoints"),SubmitRequest.class);
            if (submitRequest != null) {
                Log.d(TAG, submitRequest.toString());
            }
            if (activeGame.getGameType() == ACCEPTCONTACTSCHALLENGE) {
                registerButton.setVisibility(View.VISIBLE);
            }
            playResponse = OustAppState.getInstance().getPlayResponse();
            OustAppState.getInstance().setPlayResponse(null);

            isSoundEnabled = CallingIntent.getBooleanExtra("ShouldMusicPlay", false);

            android.view.animation.Animation top_anim = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.animmovetop);
            checkResultLayout.startAnimation(top_anim);
            groupResultLayout.startAnimation(top_anim);
            topLayout.bringToFront();

            android.view.animation.Animation continewblick_anim = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.continewblick_anim);
            loadingtxt.setAnimation(continewblick_anim);
            mainView.setVisibility(View.VISIBLE);
            allGroupList.setVisibility(View.GONE);
            setTextStyle();
            setRematchButtonVisibility();
            setWaitingButtonVisibility();
            setPlayAgainButtonVisibility();
            setResultViewData();
            verifyResult();
            showstored_Popup();
            setGameInformation();
            OustAppState.getInstance().setAssessmentGame(false);

        }catch (Exception e){
        }
    }

    private void setTextStyle(){
        try {
            rematchButton.setText(getResources().getString(R.string.rematch));
            modulecompletelable.setText(getResources().getString(R.string.module_complete));

            rightCenter.setText(getResources().getString(R.string.right));
            wrongCenter.setText(getResources().getString(R.string.wrong));
            xpValueCenter.setText(getResources().getString(R.string.xp));
            rightUserCenter.setText(getResources().getString(R.string.right));
            wrongUserScoreCenter.setText(getResources().getString(R.string.wrong));
            xpValueUserScoreCenter.setText(getResources().getString(R.string.xp));
            rightOpponentCenter.setText(getResources().getString(R.string.right));
            wrongOpponentScoreCenter.setText(getResources().getString(R.string.wrong));
            xpValueOpponentScoreCenter.setText(getResources().getString(R.string.xp));

            txtRegister.setText(getResources().getString(R.string.register_text));
            answerButton.setText(getResources().getString(R.string.answer));
            txtShare.setText(getResources().getString(R.string.earn_rewards));

            allScoresBtn.setText(getResources().getString(R.string.all_scores));

        }catch (Exception e){

        }
    }

    private void setWaitingButtonVisibility() {
        try {
            if ((activeGame.getGameType() == MYSTERY)
                    || (activeGame.getGameType() == CHALLENGE)
                    || activeGame.getGameType() == GROUP
                    || activeGame.getGameType() == REMATCH
                    || activeGame.getGameType() == CONTACTSCHALLENGE) {
                if ((activeGame.getGameType() == MYSTERY && !activeGame.getOpponentid().equals("Mystery"))
                        || activeGame.getGameType() == GROUP) {
                    waitingButtonlayout.setVisibility(View.GONE);
                } else {
                    waitingButton.setText(getResources().getString(R.string.waiting));
                    waitingButtonlayout.setVisibility(View.VISIBLE);
                    playAgainButton.setVisibility(View.GONE);
                }
            }
        }catch (Exception e){

        }
    }

    private void setPlayAgainButtonVisibility() {
        playAgainButton.setVisibility(View.VISIBLE);
    }

    private void setRematchButtonVisibility() {
        try {
            if ((activeGame.getGameType() == ACCEPT) || ((activeGame.getGameType() == MYSTERY) && (!activeGame.getOpponentid().equals("Mystery")))) {
                if (!activeGame.getGroupId().isEmpty()) {
                    rematchButtonlayout.setVisibility(View.GONE);
                } else {
                    rematchButton.setText(getResources().getString(R.string.rematch));
                    rematchButtonlayout.setVisibility(View.VISIBLE);
                }
            } else {
                rematchButtonlayout.setVisibility(View.GONE);
            }
            if(OustStaticVariableHandling.getInstance().isEnterpriseUser()){
                rematchButton.setVisibility(View.GONE);
            }
        }catch (Exception e){
        }
    }

    public void verifyResult() {
        try {
            if (activeGame.getGameType() == REMATCH
                    || activeGame.getGameType() == CONTACTSCHALLENGE
                    || activeGame.getGameType() == MYSTERY
                    || activeGame.getGameType() == CHALLENGE
                    || activeGame.getGameType() == GROUP) {
                //ignore + assume completedgame
                if (activeGame.getGameType() == MYSTERY && !activeGame.getOpponentid().equals("Mystery")) {
                    resultScore.setVisibility(View.GONE);
                    scoreWaitingLayout.setVisibility(View.GONE);
                    scoreLayout.setVisibility(View.VISIBLE);
                    setOpponentScores();
                    setUserScores();
//                    checkResultLayout.setVisibility(View.VISIBLE);
                    setGroupsAndFriendsChallengeResult();
                } else {
                    resultScore.setVisibility(View.GONE);
                    scoreWaitingLayout.setVisibility(View.VISIBLE);
                    allscoreLayout.setVisibility(View.GONE);
                    scoreLayout.setVisibility(View.GONE);
                    showWaiting(imageView, resultTitle, resultText, resultTextLayout);
                }
            } else {

                if (!activeGame.getGroupId().isEmpty()) {
                    resultScore.setVisibility(View.GONE);
                    scoreWaitingLayout.setVisibility(View.VISIBLE);
                    scoreLayout.setVisibility(View.GONE);
                    showWaiting(imageView, resultTitle, resultText, resultTextLayout);
                } else {
                    resultScore.setVisibility(View.GONE);
                    scoreWaitingLayout.setVisibility(View.GONE);
                    allscoreLayout.setVisibility(View.GONE);
                    scoreLayout.setVisibility(View.VISIBLE);
                    setOpponentScores();
                    setUserScores();
//                    checkResultLayout.setVisibility(View.VISIBLE);
                    setGroupsAndFriendsChallengeResult();
                }
            }
        }catch (Exception e){

        }
    }

    private void setGroupsAndFriendsChallengeResult() {
        try {
            checkResultLayout.setVisibility(View.VISIBLE);
            if(waitingButtonlayout.getVisibility() == View.VISIBLE || rematchButtonlayout.getVisibility() == View.VISIBLE){
                check_ref.setVisibility(View.VISIBLE);
            }
            else {
                check_ref.setVisibility(View.GONE);
            }
            if (userGamePoints != null) {
                if (findWinner().equals("TIE")) {
                    showTie(imageView, resultTitle, resultText, resultTextLayout);
                } else if (findWinner().equals(activeGame.getStudentid())) {
                    showWin(imageView, resultTitle, resultText, resultTextLayout);
                } else {
                    showLoss(imageView, resultTitle, resultText, resultTextLayout);
                }
            } else {
                if (submitRequest.getWinner().equals("TIE")) {
                    showTie(imageView, resultTitle, resultText, resultTextLayout);
                } else if (submitRequest.getWinner().equals(activeGame.getStudentid())) {
                    showWin(imageView, resultTitle, resultText, resultTextLayout);
                } else {
                    showLoss(imageView, resultTitle, resultText, resultTextLayout);
                }
            }
        }catch (Exception e){

        }
    }

    public String findWinner() {
        try {

            for (int i = 0; i < 5; i++) {
                opponentScore = gamePoints.getPoints()[i].getPoint();
                opponentFinalScore += opponentScore;
                if (userGamePoints.getPoints() != null && userGamePoints.getPoints().length > 0) {
                    challengerScore = userGamePoints.getPoints()[i].getPoint();
                    challengerFinalScore += challengerScore;
                } else {
                    challengerScore = 0;
                    challengerFinalScore += challengerScore;
                }

            }
        } catch (Exception ex) {
            Log.e(TAG, "Error in gamepoints" + ex);
        }
        String winner = "-1";
        if (activeGame.getGameType() == MYSTERY && !activeGame.getOpponentid().equals("Mystery")) {
            if (opponentFinalScore == challengerFinalScore) {
                winner = "TIE";
            } else if (opponentFinalScore > challengerFinalScore) {
                winner = activeGame.getOpponentid();
            } else {
                winner = activeGame.getChallengerid();
            }
            return winner;
        } else if (activeGame.getGameType() == ACCEPT) {
            if (activeGame.getGroupId().isEmpty()) {
                if (opponentFinalScore == challengerFinalScore) {
                    winner = "TIE";
                } else if (opponentFinalScore > challengerFinalScore) {
                    winner = activeGame.getOpponentid();
                } else {
                    winner = activeGame.getChallengerid();
                }
                return winner;
            } else {
                return winner;
            }
        }
        return winner;
    }

    private void setUserScores() {
        int userCorrectAnswerCount = 0;
        int userWrongAnswerCount = 0;
        int userTotalScore = 0;

        if (userGamePoints != null) {
            for (int i = 0; i < userGamePoints.getPoints().length; i++) {
                userTotalScore += userGamePoints.getPoints()[i].getPoint();
                DTOQuestions questions=new DTOQuestions();
                if((playResponse.getqIdList()!=null)&&(playResponse.getqIdList().size()>0)){
                    questions= OustDBBuilder.getQuestionById(playResponse.getqIdList().get(i));
                }else {
                    questions = playResponse.getQuestionsByIndex(i);
                }
                if((playResponse!=null)&&(questions!=null)&& (questions!=null)&&(questions.getQuestionType()== QuestionType.SURVEY)){
                }else {
                    if (!userGamePoints.getPoints()[i].isCorrect()) {
                        userWrongAnswerCount++;
                    } else {
                        userCorrectAnswerCount++;
                    }
                }
            }
        } else {
            for (int i = 0; i < submitRequest.getScores().size(); i++) {
                userTotalScore += submitRequest.getScores().get(i).getScore();
                DTOQuestions questions=new DTOQuestions();
                if((playResponse.getqIdList()!=null)&&(playResponse.getqIdList().size()>0)){
                    questions=OustDBBuilder.getQuestionById(playResponse.getqIdList().get(i));
                }else {
                    questions = playResponse.getQuestionsByIndex(i);
                }
                if((playResponse!=null)&&(questions!=null)&& (questions.getQuestionType()!=null)&&(questions.getQuestionType()== QuestionType.SURVEY)){}else {
                    if (!submitRequest.getScores().get(i).isCorrect()) {
                        userWrongAnswerCount++;
                    } else {
                        userCorrectAnswerCount++;
                    }
                }
            }
        }
        rightUserScore.setText(userCorrectAnswerCount + "");
        wrongUserScore.setText(userWrongAnswerCount + "");
        xpValueUserScore.setText(userTotalScore + "");
    }

    private void setOpponentScores() {
        int opponentTotalScore = 0;
        int opponentCorrectAnswerCount = 0;
        int opponentWrongAnswerCount = 0;

        if (gamePoints != null && gamePoints.getPoints().length != 0) {

            for (int i = 0; i < 5; i++) {
                opponentTotalScore += gamePoints.getPoints()[i].getPoint();
                if (!gamePoints.getPoints()[i].isCorrect()) {
                    opponentWrongAnswerCount++;
                } else {
                    opponentCorrectAnswerCount++;
                }
            }
            rightOpponentScore.setText(opponentCorrectAnswerCount + "");
            wrongOpponentScore.setText(opponentWrongAnswerCount + "");
            xpValueOpponentScore.setText(opponentTotalScore + "");
        }

    }

    public void showWaiting(ImageView imageView, TextView resultTitle, TextView resultText, RelativeLayout resultTextLayout) {
        try {
            Log.d(TAG, "Active game ------------" + activeGame.toString());

            mediaPlayer = MediaPlayer.create(this, Uri.fromFile(OustCommonUtils.getAudioFile("game_comeback_resultcard")));
            playMusic();

            GameType gametype=activeGame.getGameType();

            if(waitingButtonlayout.getVisibility() == View.VISIBLE){
                check_ref.setVisibility(View.VISIBLE);
            }
            else {
                check_ref.setVisibility(View.GONE);
            }
            checkResultLayout.setVisibility(View.VISIBLE);
            android.view.animation.Animation top_anim = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.animmovetop);
            checkResultLayout.startAnimation(top_anim);
            isFinished = true;
            OustSdkTools.setImage(imageView,getResources().getString(R.string.come_back));
//                    resultTextLayout.setBackgroundColor(OustSdkTools.getColorBack(R.color.black_semi_transparent));
            resultTextLayout.setBackgroundColor(OustSdkTools.getColorBack(R.color.Black));
            resultTitle.setText(getResources().getString(R.string.well_done_text));
            resultText.setText(getResources().getString(R.string.challenge_notify));

            if ((activeGame.getChallengerScore() == null || activeGame.getChallengeResult() == null)
                    && (activeGame.getGameType() == MYSTERY) && (playResponse != null)) {
                right.setText(String.valueOf(playResponse.getCorrectAnswerCount()));
                wrong.setText(String.valueOf(playResponse.getWrongAnswerCount()));
                xpValue.setText(String.valueOf(submitRequest.getTotalscore()));

            }
            else {
                Log.d(TAG, "-----------------------------");
                int userCorrectAnswerCount = 0;
                int userWrongAnswerCount = 0;
                int userTotalScore = 0;

                if (userGamePoints != null) {
                    for (int i = 0; i < userGamePoints.getPoints().length; i++) {
                        userTotalScore += userGamePoints.getPoints()[i].getPoint();
                        if (!userGamePoints.getPoints()[i].isCorrect()) {
                            userWrongAnswerCount++;
                        } else {
                            userCorrectAnswerCount++;
                        }
                    }
                    right.setText(userCorrectAnswerCount + "");
                    wrong.setText(userWrongAnswerCount + "");
                    xpValue.setText(userTotalScore + "");
                } else {
                    if (playResponse != null) {
                        right.setText(String.valueOf(playResponse.getCorrectAnswerCount()));
                        wrong.setText(String.valueOf(playResponse.getWrongAnswerCount()));
                        xpValue.setText(String.valueOf(submitRequest.getTotalscore()));
                    }

                }
            }
        }catch (Exception e){}

    }

    private void playMusic() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    mediaPlayer.setLooping(false);
                    if ((!OustAppState.getInstance().isSoundDisabled())&&(isSoundEnabled)) {
                        mediaPlayer.prepare();
                        if (!mediaPlayer.isPlaying()) {
                            mediaPlayer.start();
                        }
                    }

                } catch (Exception e) {}
            }
        });

    }


    public void showTie(ImageView imageView, TextView resultTitle, TextView resultText, RelativeLayout resultTextLayout) {
        mediaPlayer = MediaPlayer.create(this, Uri.fromFile(OustCommonUtils.getAudioFile("game_tie_resultcard")));
        playMusic();
        OustSdkTools.setImage(imageView,getResources().getString(R.string.tie));
        resultTextLayout.setBackgroundColor(OustSdkTools.getColorBack(R.color.LiteGray));
        resultTitle.setText(getResources().getString(R.string.good_try));
        resultText.setText(getResources().getString(R.string.it_s_tie));
    }

    public void showWin(ImageView imageView, TextView resultTitle, TextView resultText, RelativeLayout resultTextLayout) {
        mediaPlayer = MediaPlayer.create(this, Uri.fromFile(OustCommonUtils.getAudioFile("win_game")));
        playMusic();

        String resultWinText = getResources().getString(R.string.congratulations_text);
        OustSdkTools.setImage(imageView,getResources().getString(R.string.win));
        resultTextLayout.setBackgroundColor(OustSdkTools.getColorBack(R.color.LiteGreen));
        resultTitle.setText(getResources().getString(R.string.you_won));
        resultText.setText(resultWinText+" " +activeUser.getUserDisplayName());
    }

    public void showLoss(ImageView imageView, TextView resultTitle, TextView resultText, RelativeLayout resultTextLayout) {

        mediaPlayer = MediaPlayer.create(this, Uri.fromFile(OustCommonUtils.getAudioFile("lose_game")));
        playMusic();
        OustSdkTools.setImage(imageView,getResources().getString(R.string.lose));
        resultTextLayout.setBackgroundColor(OustSdkTools.getColorBack(R.color.Orange));
        resultTitle.setText(getResources().getString(R.string.good_try));
        resultText.setText(getResources().getString(R.string.they_won));
    }

    void setResultViewData() {
        setUserName();
        setOpponentName();
        setChallengerAvatar();
        setOpponentAvatar();
    }

    private void setUserName() {
        GameType gametype=activeGame.getGameType();
        if(gametype==ACCEPTCONTACTSCHALLENGE) {
            userName.setText(activeUser.getUserDisplayName());
        }
        else if(gametype==ACCEPT) {
            userName.setText(activeUser.getUserDisplayName());
        }
        else if(gametype==MYSTERY) {
            userName.setText(activeUser.getUserDisplayName());
        }
        else if(gametype==CONTACTSCHALLENGE) {
            userName.setText(activeUser.getUserDisplayName());
        }
        else if(gametype==GROUP) {
//                userName.setText(createGameResponse.getChallengerDisplayName());
            userName.setText(activeUser.getUserDisplayName());
        }
        else if(gametype==CHALLENGE) {
            userName.setText(activeUser.getUserDisplayName());
        }
        else if(gametype==REMATCH) {
            userName.setText(activeUser.getUserDisplayName());
        }
        else {
            // Nothing for now !!!
        }

    }

    private void setOpponentName() {
        try {
            GameType gametype=activeGame.getGameType();
            if(gametype==GROUP) {
                groupName.setVisibility(View.VISIBLE);
                groupName.setText(activeGame.getOpponentDisplayName());
            }
            else if(gametype==CONTACTSCHALLENGE) {
                groupName.setVisibility(View.VISIBLE);
                if ((activeGame.getOpponentDisplayName() != null) && (!activeGame.getOpponentDisplayName().equalsIgnoreCase("null"))) {
                    groupName.setText(activeGame.getOpponentDisplayName());
                } else {
                    groupName.setText(activeGame.getOpponentid().substring(4));
                }
            }
            else {
                if (!activeGame.getGroupId().isEmpty()) {
                    groupName.setVisibility(View.VISIBLE);
                    groupName.setText(activeGame.getOpponentDisplayName());

                } else {
                    opponentName.setVisibility(View.VISIBLE);
                    opponentName.setText(activeGame.getOpponentDisplayName());
                }
            }
        }catch (Exception e){
        }
    }

    private void setOpponentAvatar() {
        try {
            Activity activity = ResultActivity.this;
            String contactOpponentName;
            GameType gametype=activeGame.getGameType();
            if(gametype==GROUP) {
                groupAvatar.setVisibility(View.VISIBLE);
                OustSdkTools.setGroupsIconInActivity(groupAvatar, activeGame.getOpponentDisplayName());
            }
            else {
                if (!activeGame.getGroupId().isEmpty()) {
                    groupAvatar.setVisibility(View.VISIBLE);
                    OustSdkTools.setGroupsIconInActivity(groupAvatar, activeGame.getOpponentDisplayName());
                } else {
                    if (!activeGame.getGameType().toString().equals("CONTACTSCHALLENGE")) {
                        opponentAvatarImgLayout.setVisibility(View.VISIBLE);
                        if (activeGame.getOpponentAvatar().startsWith("http")) {
                            Picasso.get()
                                    .load(activeGame.getOpponentAvatar())
                                    .placeholder(bd_loading).error(bd)
                                    .into(opponentAvatarImg);
                        } else {
                            Picasso.get()
                                    .load(getString(R.string.oust_user_avatar_link) + activeGame.getOpponentAvatar())
                                    .placeholder(bd_loading).error(bd)
                                    .into(opponentAvatarImg);
                        }

                    }
                    /* For choosing Contact opponent avatar symbol setting here*/
                    else {
                        groupAvatar.setVisibility(View.VISIBLE);
                        if (activeGame.getSMSContactChallenge().equals("true")) {
                            if (null == activeGame.getOpponentDisplayName() || activeGame.getOpponentDisplayName().equals("null")) {
                                contactOpponentName = OustStrings.getString("contactOppName");

                            } else {
                                contactOpponentName = activeGame.getOpponentDisplayName();
                            }
                        } else {
                            contactOpponentName = activeGame.getOpponentDisplayName();
                        }
                        OustSdkTools.setGroupsIconInActivity(groupAvatar, contactOpponentName);
                    }

                }
            }
        }catch (Exception e){

        }
    }

    private void setChallengerAvatar() {
        try {
            if (activeGame.getGameType() == ACCEPTCONTACTSCHALLENGE) {
                // Show created avatar
                TextView txtChallengerAvatar = findViewById(R.id.txtChallengerAvatar);
                txtChallengerAvatar.setVisibility(View.VISIBLE);
                OustSdkTools.setGroupsIconInActivity(txtChallengerAvatar,activeGame.getChallengerDisplayName());
            } else {
                challengerAvatarLayout.setVisibility(View.VISIBLE);
                if(OustSdkTools.tempProfile==null) {
                    Picasso.get().load(activeUser.getAvatar())
                            .placeholder(bd_loading).error(bd)
                            .into(challengerAvatarImg);
                }else {
                    challengerAvatarImg.setImageBitmap(OustSdkTools.tempProfile);
                }
            }
        }catch (Exception e){

        }

    }

    public void showModuleSelectPopup(){
        Popup popup = new Popup();
        popup.setHeader(OustStrings.getString("nomodule_header"));
        popup.setContent(OustStrings.getString("no_module_selected"));
        popup.setCategory(OustPopupCategory.REDIRECT);
        popup.setType(OustPopupType.NO_MODULE_SELECTED);
        List<OustPopupButton> buttons = new ArrayList<>();
        OustPopupButton btn1 = new OustPopupButton();
        btn1.setBtnText(getResources().getString(R.string.result_gotomydesk));
        buttons.add(btn1);
        popup.setButtons(buttons);

        OustStaticVariableHandling.getInstance().setOustpopup(popup);
        Intent intent = new Intent(ResultActivity.this, PopupActivity.class);
        Gson gson = new GsonBuilder().create();
        intent.putExtra("ActiveUser", gson.toJson(activeUser));
        intent.putExtra("ActiveGame", gson.toJson(activeGame));
//        LandingActivity.viewPager.setCurrentItem(0);
        ResultActivity.this.finish();
        startActivity(intent);
    }


    public Bitmap getScreenShot(View resultView) {
        Log.d(TAG, "getScreenShot");
        Bitmap screenBitmap = OustSdkTools.getInstance().getScreenShot(resultView);
        return  screenBitmap;
    }


    @Override
    protected void onResume() {
        mainView.setVisibility(View.VISIBLE);
        allGroupList.setVisibility(View.GONE);
        loadingtxt.setText("");
        super.onResume();
    }

    public void getPlayResponse() {
        try {

            AssmntGamePlayRequest assmntGamePlayRequest = new AssmntGamePlayRequest();
            assmntGamePlayRequest.setGameid(activeGame.getGameid());
            assmntGamePlayRequest.setStudentid(activeUser.getStudentid());
            assmntGamePlayRequest.setOnlyQId(true);
            assmntGamePlayRequest.setDevicePlatformName("android");
            final Gson gson = new Gson();
            String jsonParams = gson.toJson(assmntGamePlayRequest);

            String playGameUrl = OustSdkApplication.getContext().getResources().getString(R.string.play_game);
            playGameUrl = HttpManager.getAbsoluteUrl(playGameUrl);

            ApiCallUtils.doNetworkCall(Request.Method.POST, playGameUrl, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    PlayResponse playResponse=gson.fromJson(response.toString(), PlayResponse.class);
                    if ((null != playResponse)) {
                        getPlayResponseProcessFinish(playResponse);
                    }else {
                        OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg));
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, playGameUrl, OustSdkTools.getRequestObjectforJSONObject(jsonParams), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    PlayResponse playResponse=gson.fromJson(response.toString(), PlayResponse.class);
                    if ((null != playResponse)) {
                        getPlayResponseProcessFinish(playResponse);
                    }else {
                        OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg"));
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onFailure: POST REST Call: " + " Failed");
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<String, String>();
                    try {
                        params.put("api-key", OustPreferences.get("api_key"));
                        params.put("org-id", OustPreferences.get("tanentid"));
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    return params;
                }
            };
            jsonObjReq.setShouldCache(false);
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void getPlayResponseProcessFinish(final PlayResponse playResponse) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PlayResponse playResponse1 = OustSdkTools.getInstance().getDecryptedQuestion(playResponse);
                Intent intent = new Intent(ResultActivity.this, CheckAnswerActivity.class);
                intent.putExtra("ActiveUser", gson.toJson(activeUser));
                intent.putExtra("ActiveGame", gson.toJson(activeGame));
                intent.putExtra("CreateGameResponse", gson.toJson(createGameResponse));
                OustAppState.getInstance().setPlayResponse(playResponse1);
                //intent.putExtra("PlayResponse", gson.toJson(playResponse));
                intent.putExtra("SubmitRequest", gson.toJson(submitRequest));
                intent.putExtra("GamePoints", gson.toJson(gamePoints));
                if (userGamePoints != null) {
                    intent.putExtra("UserGamePoints", gson.toJson(userGamePoints));
                    if (userGamePoints.getPoints().length == 0) {
                        return;
                    }
                }
                ResultActivity.this.startActivity(intent);
            }
        });

    }

    @Override
    public void onChange(String status) {
        Log.d(TAG, "Network Availability");
        Log.d(TAG, status);
        OustSdkTools.getInstance().showToast(status);
    }


    @Override
    public void onBackPressed() {
        try {
            if(allGroupList.getVisibility()==View.VISIBLE){
                mainView.setVisibility(View.VISIBLE);
                livecontestLayout.setVisibility(View.VISIBLE);
                allGroupList.setVisibility(View.GONE);
            }else {
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                }
                super.onBackPressed();
            }
        }catch (Exception e){

        }
    }

    //===========================================================================================================
    //methode to decide from where cuming to result screen
    private void setGameInformation(){
        try {
            if (OustAppState.getInstance().getDecideResultGameInfo()==0) {
                //mystry and history game
                currentGameInfo=new CurrentGameInfo();
                currentGameInfo.setGrade(activeGame.getGrade());
                currentGameInfo.setSubject(activeGame.getSubject());
                currentGameInfo.setTopic(activeGame.getTopic());
                currentGameInfo.setModuleName(activeGame.getModuleName());
                currentGameInfo.setModuleId(activeGame.getModuleId());
                setGameInfo();
            }else if(OustAppState.getInstance().getDecideResultGameInfo()==1){
                //notification result
                if(activeGame.getLpid()==0) {
                    loadModuleinfoFromGameId();
                }else {
                    loadModuleinfoFromGameIdForLP();
                }
            } else{
                OustAppState.getInstance().setDecideResultGameInfo(0);
                moduleprogress_layout.setVisibility(View.GONE);
                modtxt.setVisibility(View.GONE);
                return;
            }
            OustAppState.getInstance().setDecideResultGameInfo(0);
        }catch (Exception e){

        }
    }
    //methodes to set game info
    private void setGameInfo(){
        try {
            if(OustStaticVariableHandling.getInstance().isEnterpriseUser()){
                loadModuleCompletPresentForEnterpriseUser(currentGameInfo.getModuleId());
            }else {
                if ((OustAppState.getInstance().getFun_modulesKeys() != null) && (OustAppState.getInstance().getFun_modulesKeys().size() > 0)) {
                    if (!OustAppState.getInstance().getFun_modulesKeys().containsKey(currentGameInfo.getModuleId())) {
                        if (OustAppState.getInstance().isShouldLoadGameInfoFrom_LearningNode()) {
                            OustAppState.getInstance().setShouldLoadGameInfoFrom_LearningNode(false);
                            if (activeGame.getLpid() != 0) {
                                loadModuleCompletPresentForLearning(currentGameInfo.getModuleId(), activeGame.getLpid() + "");
                            } else {
                                loadModuleCompletPresent(currentGameInfo.getModuleId());
                            }
                        } else {
                            if (activeGame.isLpGame()) {
                                if (activeGame.getLpid() != 0) {
                                    loadModuleCompletPresentForLearning(currentGameInfo.getModuleId(), activeGame.getLpid() + "");
                                } else {
                                    loadModuleCompletPresent(currentGameInfo.getModuleId());
                                }
                            } else {
                                loadModuleCompletPresent(currentGameInfo.getModuleId());
                            }
                        }
                    } else {
                        moduleprogress_layout.setVisibility(View.GONE);
                        modtxt.setVisibility(View.GONE);
                    }
                }
            }
            if (currentGameInfo.getModuleName() != null) {
                modulename.setText(currentGameInfo.getModuleName());
            }
            if (currentGameInfo.getTopic() != null) {
                topicname.setText(currentGameInfo.getTopic());
            }
            if ((currentGameInfo.getGrade() != null) && (!currentGameInfo.getGrade().equalsIgnoreCase(""))&&(!currentGameInfo.getGrade().equalsIgnoreCase("0"))) {
                if (Character.isDigit(currentGameInfo.getGrade().charAt(0))) {
                    if((currentGameInfo.getSubject()!=null)&&(!currentGameInfo.getSubject().isEmpty())) {
                        subjectname.setText(getResources().getString(R.string.moduleGradeTxt) + currentGameInfo.getGrade() + "  " + currentGameInfo.getSubject());
                    }else {
                        subjectname.setText(getResources().getString(R.string.moduleGradeTxt) + currentGameInfo.getGrade());
                    }
                } else {
                    if((currentGameInfo.getSubject()!=null)&&(!currentGameInfo.getSubject().isEmpty())) {
                        subjectname.setText(getResources().getString(R.string.moduleExamTxt) + currentGameInfo.getGrade() + "  " + currentGameInfo.getSubject());
                    }else {
                        subjectname.setText(getResources().getString(R.string.moduleExamTxt) + currentGameInfo.getGrade());
                    }
                }
            } else {
                if (currentGameInfo.getSubject() != null) {
                    subjectname.setText(currentGameInfo.getSubject());
                }
            }
        }catch (Exception e){}
    }

    public void setUpdateCompletePercentageData(int moduleComplete) {
        moduleprogress.setProgress(moduleComplete);
        modulecomplete_txt.setText("" + moduleComplete + "%");
    }
    public void loadModuleCompletPresentForEnterpriseUser(String moduleId){
        try {
            ValueEventListener modulecompletelistener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    try {
                        if (null != snapshot.getValue()) {
                            Object o3 = snapshot.getValue();
                            if (o3.getClass().equals(Long.class)) {
                                long s3 = (long) o3;
                                setUpdateCompletePercentageData((int) s3);
                            } else if (o3.getClass().equals(String.class)) {
                                String s1 = (String) o3;
                                setUpdateCompletePercentageData(Integer.parseInt(s1));
                            } else if (o3.getClass().equals(Double.class)) {
                                Double s3 = (Double) o3;
                                long l = (new Double(s3)).longValue();
                                setUpdateCompletePercentageData((int) l);
                            }
                        }
                    }catch (Exception e){}
                }
                @Override
                public void onCancelled(DatabaseError error) {}
            };
            String msg = "landingPage/" + activeUser.getStudentKey() + "/module/" + moduleId + "/userCompletionPercentage";
            OustFirebaseTools.getRootRef().child(msg).keepSynced(true);
            OustFirebaseTools.getRootRef().child(msg).addValueEventListener(modulecompletelistener);
            modulecompleteListClass=new FirebaseRefClass(modulecompletelistener,msg);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void loadModuleCompletPresent(String moduleId){
        try {
            ValueEventListener modulecompletelistener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (null != snapshot.getValue()) {
                        Log.d(TAG, snapshot.getValue().toString());
                        int moduleComplete = Integer.parseInt(snapshot.getValue().toString());
                        setUpdateCompletePercentageData(moduleComplete);
                    }
                }
                @Override
                public void onCancelled(DatabaseError error) {}
            };
            String msg = "mydesk/" + activeUser.getStudentKey() + "/modules/" + moduleId + "/percentageComp";
            OustFirebaseTools.getRootRef().child(msg).keepSynced(true);
            OustFirebaseTools.getRootRef().child(msg).addValueEventListener(modulecompletelistener);
            modulecompleteListClass=new FirebaseRefClass(modulecompletelistener,msg);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
    public void loadModuleCompletPresentForLearning(String moduleId,String learningPathKey){
        try {
            ValueEventListener modulecompletelistener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    try {
                        if (null != snapshot.getValue()) {
                            float f1=Float.valueOf((String) snapshot.getValue());
                            int moduleComplete = (int)(f1);
                            setUpdateCompletePercentageData(moduleComplete);
                        }
                    }catch (Exception e){}
                }
                @Override
                public void onCancelled(DatabaseError error) {}
            };
            String msg = "/userLearningPath/" + activeUser.getStudentKey() +"/lp"+learningPathKey+ "/modules/" + moduleId + "/moduleCompletionPercentage";
            OustFirebaseTools.getRootRef().child(msg).keepSynced(true);
            OustFirebaseTools.getRootRef().child(msg).addValueEventListener(modulecompletelistener);
            modulecompleteListClass=new FirebaseRefClass(modulecompletelistener,msg);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    //detach firebase listener for module complete
    @Override
    protected void onPause() {
        try {
            super.onPause();
            if (modulecompleteListClass != null){
                OustFirebaseTools.getRootRef().child(modulecompleteListClass.getFirebasePath()).removeEventListener(modulecompleteListClass.getEventListener());
            }
        }catch (Exception e){
        }
    }
    //load game imformation from firebase if null(from notification)
    private void loadModuleinfoFromGameId(){
        try {
            String  message = "/gameHistory/" + activeUser.getStudentKey() +"/"+ activeGame.getGameid();
            OustAppState.getInstance().setDecideResultGameInfo(0);
            ValueEventListener getGameinfo = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    try {
                        if (null != snapshot.getValue()) {
                            Map<String, Object> gameInfo = (Map<String, Object>) snapshot.getValue();
                            currentGameInfo=new CurrentGameInfo();
                            String grade=(String) gameInfo.get("grade");
                            if ((grade!=null)&&(!grade.isEmpty())){
                                if(!grade.equalsIgnoreCase("0")){
                                    currentGameInfo.setGrade((String) gameInfo.get("grade"));
                                }
                            }
                            currentGameInfo.setModuleName((String) gameInfo.get("moduleName"));
                            currentGameInfo.setTopic((String) gameInfo.get("topic"));
                            currentGameInfo.setSubject((String) gameInfo.get("subject"));
                            currentGameInfo.setModuleId((String) gameInfo.get("moduleId"));
                            setGameInfo();
                        }
                    }catch (Exception e){}
                }
                @Override
                public void onCancelled(DatabaseError error) {}
            };
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(getGameinfo);

        } catch (Exception e) {
            OustAppState.getInstance().setDecideResultGameInfo(0);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void loadModuleinfoFromGameIdForLP(){
        try {
            String  message = "/lpGameHistory/" + activeUser.getStudentKey() +"/"+ activeGame.getGameid();
            OustAppState.getInstance().setDecideResultGameInfo(0);
            ValueEventListener getGameinfo = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    try {
                        if (null != snapshot.getValue()) {
                            Map<String, Object> gameInfo = (Map<String, Object>) snapshot.getValue();
                            currentGameInfo=new CurrentGameInfo();
                            String grade=(String) gameInfo.get("grade");
                            if ((grade!=null)&&(!grade.isEmpty())){
                                if(!grade.equalsIgnoreCase("0")){
                                    currentGameInfo.setGrade((String) gameInfo.get("grade"));
                                }
                            }
                            currentGameInfo.setModuleName((String) gameInfo.get("moduleName"));
                            currentGameInfo.setTopic((String) gameInfo.get("topic"));
                            currentGameInfo.setSubject((String) gameInfo.get("subject"));
                            currentGameInfo.setModuleId((String) gameInfo.get("moduleId"));
                            setGameInfo();
                        }
                    }catch (Exception e){}
                }
                @Override
                public void onCancelled(DatabaseError error) {}
            };
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(getGameinfo);

        } catch (Exception e) {
            OustAppState.getInstance().setDecideResultGameInfo(0);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //==============================================================================================
//show stored popup during game play
    private void showstored_Popup(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Stack<Popup> popupStack = OustAppState.getInstance().getPopupStack();
                    if (popupStack != null) {
                        try {
                            while (!popupStack.isEmpty()) {
                                OustStaticVariableHandling.getInstance().setOustpopup(popupStack.pop());
                                Intent intent = new Intent(ResultActivity.this, PopupActivity.class);
                                startActivity(intent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    }
                } catch (Exception e) { }
            }
        }, 1500);

    }

    //======================================================================================
    //methode to get friend info


    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id==R.id.rematchButton) {
            try {
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                }
                if (!OustSdkTools.checkInternetStatus()) {
                    return;
                }
                if((activeUser.getModuleId()!=null)&&(!activeUser.getModuleId().isEmpty())) {
                    activeGame.setSubject(activeUser.getSubject());
                    activeGame.setTopic(activeUser.getTopic());
                    activeGame.setGrade(activeUser.getGrade());
                    activeGame.setModuleName(activeUser.getModuleName());
                    activeGame.setModuleId(activeUser.getModuleId());
                    if((OustPreferences.get("learningpathid")!=null)&&(!OustPreferences.get("learningpathid").isEmpty())){
                        activeGame.setIsLpGame(true);
                        activeGame.setLpid(Integer.parseInt(OustPreferences.get("learningpathid")));
                    }else {
                        activeGame.setIsLpGame(false);
                    }
                }else {
                    showModuleSelectPopup();
                }
            }catch (Exception e){
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
        if(id==R.id.subscriberButton) {
            if(mediaPlayer!=null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
            }
            //TODO
        }
        if(id==R.id.btnClose) {
            try {
                if(mediaPlayer!=null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                }
                ResultActivity.this.finish();
                onBackPressed();
            }catch (Exception e){

            }
        }

        if(id==R.id.answerButton){
            try {
                OustSdkTools.oustTouchEffect(v,100);
                if(mediaPlayer!=null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                }
                if (playResponse == null) {
                    if(!OustSdkTools.checkInternetStatus()){
                        return;
                    }
                    loadingtxt.setText(getResources().getString(R.string.fetching_question));
                    getPlayResponse();
                } else {
                    loadingtxt.setText(getResources().getString(R.string.fetching_question));
                    // oustAnalyticsTools.reportEvents(ResultActivity.this, getString(R.string.event_checkAnswers));
                    Intent intent = new Intent(this, CheckAnswerActivity.class);
                    intent.putExtra("ActiveUser", gson.toJson(OustAppState.getInstance().getActiveUser()));
                    intent.putExtra("ActiveGame", gson.toJson(activeGame));
                    intent.putExtra("CreateGameResponse", gson.toJson(createGameResponse));
                    OustAppState.getInstance().setPlayResponse(this.playResponse);
                    intent.putExtra("SubmitRequest", gson.toJson(submitRequest));
                    intent.putExtra("GamePoints", gson.toJson(gamePoints));
                    intent.putExtra("UserGamePoints", gson.toJson(userGamePoints));
                    this.startActivity(intent);
                }
            }catch (Exception e){
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }
}
