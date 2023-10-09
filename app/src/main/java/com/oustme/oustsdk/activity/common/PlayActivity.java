package com.oustme.oustsdk.activity.common;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.assessments.QuestionActivity;
import com.oustme.oustsdk.request.AnyOpenRequest;
import com.oustme.oustsdk.request.AssmntGamePlayRequest;
import com.oustme.oustsdk.request.CreateGameRequest;
import com.oustme.oustsdk.request.SubmitRequest;
import com.oustme.oustsdk.response.assessment.CreateGameResponse;
import com.oustme.oustsdk.response.assessment.GamePoints;
import com.oustme.oustsdk.response.assessment.SubmitResponse;
import com.oustme.oustsdk.response.common.AnyOpenResponse;
import com.oustme.oustsdk.response.common.GameType;
import com.oustme.oustsdk.response.common.PlayResponse;
import com.oustme.oustsdk.response.common.Popup;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.NetworkUtil;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustGATools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by admin on 30/09/17.
 */

public class PlayActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = PlayActivity.class.getName();
    public final String INIT_GAME_BACKGROUND_THREAD_ID = "START_GAME";

    private ActiveUser activeUser;
    private ActiveGame activeGame;

    private AnyOpenResponse anyOpenResponse;
    private PlayResponse playResponse;
    private CreateGameResponse createGameResponse;
    private GamePoints gamePoints;

    private TextView txtChallengerName, txtOpponentName, txtGroupAvatarName, txtChallengerAvatarName, txtGroupName, txtUserPlace, txtVs;

    private ImageView imgChallengerAvatar, imgOpponentAvatar, play_soundbtn;
    private ImageButton txtGroupAvatar, txtChallengerAvatar, closeButton;

    private RelativeLayout closeBtnLayout, challengerLayout, opponentLayout, vsLayout, playswipe_refresherlayout, playswipe_refreshersublayout;

    private SwipeRefreshLayout playswipe_refresher;

    private Button go;

    boolean isNetworkAvailable = false;
    private MediaPlayer mediaPlayer;
    private boolean gobuttonPressed = false;
    private boolean isgameLoaded = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OustSdkTools.setLocale(PlayActivity.this);
        setContentView(R.layout.playfragment);
        initViews();
        initPlayFragment();
//        OustGATools.getInstance().reportPageViewToGoogle(PlayActivity.this, "Play Page");

    }

    private void initViews() {
        txtChallengerName = findViewById(R.id.txtChallengerName);
        txtOpponentName = findViewById(R.id.txtOpponentName);
        txtGroupAvatarName = findViewById(R.id.txtGroupAvatarName);
        txtChallengerAvatarName = findViewById(R.id.txtChallengerAvatarName);
        txtGroupName = findViewById(R.id.txtGroupName);
        txtUserPlace = findViewById(R.id.txtUserPlace);
        txtVs = findViewById(R.id.txtVs);

        imgChallengerAvatar = findViewById(R.id.imgChallengerAvatar);
        imgOpponentAvatar = findViewById(R.id.imgOpponentAvatar);
        play_soundbtn = findViewById(R.id.play_soundbtn);
        txtGroupAvatar = findViewById(R.id.txtGroupAvatar);
        txtChallengerAvatar = findViewById(R.id.txtChallengerAvatar);
        closeButton = findViewById(R.id.closeButton);

        closeBtnLayout = findViewById(R.id.closeBtnLayout);
        challengerLayout = findViewById(R.id.challengerLayout);
        opponentLayout = findViewById(R.id.opponentLayout);
        vsLayout = findViewById(R.id.vsLayout);
        playswipe_refresherlayout = findViewById(R.id.playswipe_refresherlayout);
        playswipe_refreshersublayout = findViewById(R.id.playswipe_refreshersublayout);
        playswipe_refresher = findViewById(R.id.playswipe_refresher);
        go = findViewById(R.id.go);

        OustSdkTools.setImage(txtGroupAvatar, getResources().getString(R.string.announcement_bg));
        OustSdkTools.setImage(txtChallengerAvatar, getResources().getString(R.string.announcement_bg));
        OustSdkTools.setImage(closeButton, getResources().getString(R.string.closesymbol));

        go.setOnClickListener(this);
        closeButton.setOnClickListener(this);
        closeBtnLayout.setOnClickListener(this);
        play_soundbtn.setOnClickListener(this);
    }

    public void initPlayFragment() {
        try {
            setRefresher();

            //submit last time saved game.
            if (OustPreferences.getSavedInt("askedwritepermission") == 0) {
                OustPreferences.saveintVar("askedwritepermission", 1);
            }
            OustAppState.getInstance().setAssessmentGame(false);
            closeBtnLayout.setVisibility(View.VISIBLE);
            Intent CallingIntent = getIntent();
            activeGame = OustSdkTools.getAcceptChallengeData(CallingIntent.getStringExtra("ActiveGame"));
            activeUser = OustAppState.getInstance().getActiveUser();
            if (activeUser == null) {
                activeUser = OustAppState.getInstance().getActiveUser();
            }
            soundMeth();

            play_soundbtn.setOnClickListener(this);
            setChallengerName(txtChallengerName);
            setChallengerAvatar();
            setOpponentAvatar();
            setOpponentName();
            setFontName();
            submitLastGame();
            StartAnim();
            OustAppState.getInstance().setShouldLoadGameInfoFrom_LearningNode(false);
        } catch (Exception e) {
        }
    }

    //================================================================================================================================================
    private void setRefresher() {
        playswipe_refresher.setColorSchemeColors(OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen));
        playswipe_refreshersublayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        playswipe_refresher.post(new Runnable() {
            @Override
            public void run() {
                playswipe_refresher.setRefreshing(false);
                playswipe_refresherlayout.setVisibility(View.GONE);
            }
        });
    }

    public void showPlayLoader() {
        try {
            if (playswipe_refresherlayout != null) {
                playswipe_refresherlayout.setVisibility(View.VISIBLE);
            }
            if (playswipe_refresher != null) {
                playswipe_refresher.setRefreshing(true);
            }
        } catch (Exception e) {

        }
    }

    public void hidePlayLoader() {
        try {
            if (playswipe_refresherlayout != null) {
                playswipe_refresherlayout.setVisibility(View.GONE);
            }
            if (playswipe_refresher != null) {
                playswipe_refresher.setRefreshing(false);
            }
        } catch (Exception e) {

        }
    }

    //================================================================================================================================================
    public void onResume() {
        super.onResume();
        OustAppState.getInstance().setHasPopup(true);
    }

    //methode to sound related task
    public void soundMeth() {
        if (!OustAppState.getInstance().isSoundDisabled()) {
            play_soundbtn.setImageResource(R.drawable.soundon);
        } else {
            play_soundbtn.setImageResource(R.drawable.soundoff);
        }
        mediaPlayer = new MediaPlayer();
        playAudio("go_btn.mp3");
    }

    private void playAudio(final String filename) {
        if (!OustAppState.getInstance().isSoundDisabled()) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    try {
                        File tempMp3 = new File(OustSdkApplication.getContext().getFilesDir(), filename);
                        mediaPlayer.reset();
                        FileInputStream fis = new FileInputStream(tempMp3);
                        mediaPlayer.setDataSource(fis.getFD());
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mediaPlayer.prepare();
                        mediaPlayer.start();


                    } catch (Exception e) {
                    }
                }
            });
        }
    }

    //================================================================================================================================================
    public void StartAnim() {
        try {
            Animation moveright = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.animmove);
            challengerLayout.startAnimation(moveright);
            Animation moveleft = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.animmoveleft);
            opponentLayout.startAnimation(moveleft);

            Animation alphainc = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.animalpha);
            go.startAnimation(alphainc);

            Animation scale = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.animscale);
            vsLayout.startAnimation(scale);
        } catch (Exception e) {
        }

    }

    private void setFontName() {
        try {
            txtChallengerName.setTypeface(OustSdkTools.getAvenirLTStdHeavy());
            txtOpponentName.setTypeface(OustSdkTools.getAvenirLTStdHeavy());
            txtGroupName.setTypeface(OustSdkTools.getAvenirLTStdHeavy());
            txtVs.setTypeface(OustSdkTools.getAvenirLTStdHeavy());
            go.setTypeface(OustSdkTools.getAvenirLTStdHeavy());

            go.setText(getResources().getString(R.string.go_text));
            txtVs.setText(getResources().getString(R.string.vs_text));
        } catch (Exception e) {
        }
    }

    private void setChallengerName(TextView txtChallengerName) {
        try {
            if ((activeGame.getGameType() == GameType.ACCEPTCONTACTSCHALLENGE)) {
                txtChallengerName.setText(activeUser.getUserDisplayName());
            } else if ((activeGame.getGameType() == GameType.CONTACTSCHALLENGE)) {
                txtChallengerName.setVisibility(View.VISIBLE);
                txtChallengerName.setText(activeUser.getUserDisplayName());
            } else if ((activeGame.getGameType() == GameType.GROUP)) {
                txtChallengerName.setVisibility(View.VISIBLE);
                txtChallengerName.setText(activeUser.getUserDisplayName());
            } else {
                txtChallengerName.setText(activeUser.getUserDisplayName());
            }
        } catch (Exception e) {
        }
    }


    public void setChallengerAvatar() {
        try {
            if (activeGame.getGameType() == GameType.ACCEPTCONTACTSCHALLENGE) {
                // Show created avatar
                ImageButton txtChallengerAvatar = findViewById(R.id.txtChallengerAvatar);
                txtChallengerAvatarName.setVisibility(View.VISIBLE);
                txtChallengerAvatar.setVisibility(View.VISIBLE);
                String contactChallengerName = activeUser.getUserDisplayName();
                txtChallengerAvatarName.setText(contactChallengerName.substring(0, 2).toUpperCase());
            } else {
                imgChallengerAvatar.setVisibility(View.VISIBLE);
                if (OustSdkTools.tempProfile == null) {
                    if (null != activeUser.getAvatar()) {
                        Picasso.get()
                                .load(activeUser.getAvatar())
                                .into(imgChallengerAvatar);
                    }
                } else {
                    imgChallengerAvatar.setImageBitmap(OustSdkTools.tempProfile);
                }
            }
        } catch (Exception e) {
        }

    }


    public void setOpponentName() {
        try {
            Log.d(TAG, activeGame.toString());
            if (activeGame.getGameType() == GameType.ACCEPT) {
                txtOpponentName.setVisibility(View.VISIBLE);
                txtOpponentName.setText(activeGame.getOpponentDisplayName());
            } else if (activeGame.getGameType() == GameType.GROUP) {
                txtGroupName.setVisibility(View.VISIBLE);
                txtGroupName.setText(activeGame.getOpponentDisplayName());
            } else if (activeGame.getGameType() == GameType.CONTACTSCHALLENGE) {
                txtGroupName.setVisibility(View.VISIBLE);
                txtGroupName.setText(activeGame.getOpponentDisplayName());
            } else if (activeGame.getGameType() == GameType.ACCEPTCONTACTSCHALLENGE) {
                txtGroupName.setVisibility(View.VISIBLE);
                txtGroupName.setText(activeGame.getOpponentDisplayName());
            } else if (activeGame.getGameType() == GameType.CHALLENGE) {
                txtOpponentName.setVisibility(View.VISIBLE);
                txtOpponentName.setText(activeGame.getOpponentDisplayName());
            } else if (activeGame.getGameType() == GameType.REMATCH) {
                txtOpponentName.setVisibility(View.VISIBLE);
                txtOpponentName.setText(activeGame.getOpponentDisplayName());
            } else {
                txtOpponentName.setVisibility(View.VISIBLE);
                txtOpponentName.setText(activeGame.getOpponentDisplayName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public void setOpponentAvatar() {
        String groupName;
        try {
            Log.d(TAG + "activeGame", activeGame.toString());
            if (activeGame.getGameType() == GameType.GROUP) {
                txtGroupAvatar.setVisibility(View.VISIBLE);
                txtGroupAvatarName.setVisibility(View.VISIBLE);
                groupName = activeGame.getOpponentAvatar();
                txtGroupAvatarName.setText(groupName.substring(0, 2).toUpperCase());
            } else {
                if (activeGame.getGroupId() != null && !activeGame.getGroupId().equals("")) {
                    txtGroupAvatar.setVisibility(View.VISIBLE);
                    txtGroupAvatarName.setVisibility(View.VISIBLE);
                    groupName = activeGame.getOpponentDisplayName();
                    txtGroupAvatarName.setText(groupName.substring(0, 2).toUpperCase());
                } else {
                    if (activeGame.getGameType() != GameType.CONTACTSCHALLENGE) {
                        imgOpponentAvatar.setVisibility(View.VISIBLE);
                        if (activeGame.getOpponentAvatar() != null && activeGame.getOpponentAvatar().startsWith("http")) {
                            Picasso.get()
                                    .load(activeGame.getOpponentAvatar())
                                    .into(imgOpponentAvatar);
                        } else {
                            Picasso.get()
                                    .load(getString(R.string.oust_user_avatar_link) + activeGame.getOpponentAvatar())
                                    .into(imgOpponentAvatar);
                        }
                    }
                    /* For choosing Contact opponent avatar symbol setting here*/
                    else {
                        txtGroupAvatarName.setVisibility(View.VISIBLE);
                        txtGroupAvatar.setVisibility(View.VISIBLE);
                        String contactOpponentName = activeGame.getOpponentDisplayName();
                        txtGroupAvatarName.setText(contactOpponentName.substring(0, 2).toUpperCase());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.go) {
            try {
                OustSdkTools.oustTouchEffect(view, 100);
                networkStatus();
                if (!isNetworkAvailable) {
                    closeBtnLayout.setVisibility(View.VISIBLE);
                    return;
                }
                go.setEnabled(false);
                closeBtnLayout.setVisibility(View.GONE);
                showPlayLoader();
                gobuttonPressed = true;
                if (isgameLoaded) {
                    questionProcessFinish();
                }
            } catch (Exception e) {
            }
        } else if (view.getId() == R.id.closeBtnLayout) {
            onBackPressed();
        } else if (view.getId() == R.id.closeButton) {
            onBackPressed();
        } else if (view.getId() == R.id.play_soundbtn) {
            if (!OustAppState.getInstance().isSoundDisabled()) {
                OustAppState.getInstance().setIsSoundDisabled(true);
                OustPreferences.saveAppInstallVariable("issounddisable", true);
                play_soundbtn.setImageResource(R.drawable.soundoff);
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                }
            } else {
                OustAppState.getInstance().setIsSoundDisabled(false);
                OustPreferences.saveAppInstallVariable("issounddisable", false);
                play_soundbtn.setImageResource(R.drawable.soundon);
            }
        }
    }

    //================================================================================================================================================
//start game request related methode
    public void initGame() {
        try {
            AnyOpenRequest anyOpenRequest = new AnyOpenRequest();
            CreateGameRequest createGameRequest = new CreateGameRequest();
            if (mediaPlayer != null) {
                mediaPlayer.setLooping(false);
                if (!OustAppState.getInstance().isSoundDisabled()) {
                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                    }
                }
            }
            if ((activeGame.getGameType() == GameType.CHALLENGE)) {
                createGameRequest = initFriendChallenge();
            } else if ((activeGame.getGameType() == GameType.CONTACTSCHALLENGE)) {
                createGameRequest = initContactChallenge();
            } else if ((activeGame.getGameType() == GameType.REMATCH)) {
                createGameRequest = initRematch();
            } else if ((activeGame.getGameType() == GameType.MYSTERY)) {
                anyOpenRequest = initMysteryGame();
            } else if ((activeGame.getGameType() == GameType.GROUP)) {
                createGameRequest = initGroupChallenge();
            }
            startGame(anyOpenRequest, createGameRequest);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public CreateGameRequest initFriendChallenge() {
        CreateGameRequest createGameRequest = new CreateGameRequest();
        try {
            createGameRequest.setTopic(activeGame.getTopic());
            createGameRequest.setGrade(activeGame.getGrade());
            createGameRequest.setSubject(activeGame.getSubject());
            createGameRequest.setChallengerid(activeGame.getChallengerid());
            createGameRequest.setGuestUser(activeGame.isGuestUser());
            createGameRequest.setOpponentid(activeGame.getOpponentid());
            createGameRequest.setRematch(activeGame.isRematch());
            createGameRequest.setModuleId(activeGame.getModuleId());
            if (activeGame.isLpGame()) {
                createGameRequest.setLpId(activeGame.getLpid());
                OustAppState.getInstance().setShouldLoadGameInfoFrom_LearningNode(true);
            }
            Log.d(TAG, "CreeateGameRequest-->" + createGameRequest.toString());
        } catch (Exception e) {
        }
        return createGameRequest;
    }

    public CreateGameRequest initContactChallenge() {
        CreateGameRequest createGameRequest = new CreateGameRequest();
        createGameRequest.setTopic(activeGame.getTopic());
        createGameRequest.setGrade(activeGame.getGrade());
        createGameRequest.setSubject(activeGame.getSubject());
        createGameRequest.setChallengerid(activeGame.getChallengerid());
        createGameRequest.setGuestUser(activeGame.isGuestUser());
        createGameRequest.setOpponentid(activeGame.getOpponentid());
        createGameRequest.setRematch(activeGame.isRematch());
        createGameRequest.setModuleId(activeGame.getModuleId());
        Log.d(TAG, createGameRequest.toString());
        return createGameRequest;
    }

    public CreateGameRequest initRematch() {
        CreateGameRequest createGameRequest = new CreateGameRequest();
        try {
            createGameRequest.setTopic(activeGame.getTopic());
            createGameRequest.setGrade(activeGame.getGrade());
            createGameRequest.setSubject(activeGame.getSubject());
            createGameRequest.setChallengerid(activeGame.getChallengerid());
            createGameRequest.setGuestUser(activeGame.isGuestUser());
            createGameRequest.setOpponentid(activeGame.getOpponentid());
            createGameRequest.setRematch(activeGame.isRematch());
            createGameRequest.setModuleId(activeGame.getModuleId());
            createGameRequest.setGameid(activeGame.getGameid());
            if (activeGame.isLpGame()) {
                createGameRequest.setLpId(activeGame.getLpid());
                OustAppState.getInstance().setShouldLoadGameInfoFrom_LearningNode(true);
            }
            Log.d(TAG, createGameRequest.toString());

        } catch (Exception e) {
        }
        return createGameRequest;
    }

    public AnyOpenRequest initMysteryGame() {
        AnyOpenRequest anyOpenRequest = new AnyOpenRequest();
        anyOpenRequest.setStudentid(activeGame.getStudentid());
        anyOpenRequest.setGrade(activeGame.getGrade());
        anyOpenRequest.setSubject(activeGame.getSubject());
        anyOpenRequest.setTopic(activeGame.getTopic());
        anyOpenRequest.setModuleId(activeGame.getModuleId());
        if (activeGame.isLpGame()) {
            anyOpenRequest.setLpId(activeGame.getLpid());
            OustAppState.getInstance().setShouldLoadGameInfoFrom_LearningNode(true);
        }
        return anyOpenRequest;
    }


    public CreateGameRequest initGroupChallenge() {
        CreateGameRequest createGameRequest = new CreateGameRequest();
        try {
            createGameRequest.setTopic(activeGame.getTopic());
            createGameRequest.setGrade(activeGame.getGrade());
            createGameRequest.setSubject(activeGame.getSubject());
            createGameRequest.setChallengerid(activeGame.getChallengerid());
            createGameRequest.setGuestUser(activeGame.isGuestUser());
            createGameRequest.setOpponentid(null);
            createGameRequest.setRematch(activeGame.isRematch());
            createGameRequest.setModuleId(activeGame.getModuleId());
            if (activeGame.isLpGame()) {
                createGameRequest.setLpId(activeGame.getLpid());
                OustAppState.getInstance().setShouldLoadGameInfoFrom_LearningNode(true);
            }
            Log.d(TAG, createGameRequest.toString());
        } catch (Exception e) {
        }
        return createGameRequest;
    }

    //================================================================================================================================================
//actual game start methode
    public void startGame(AnyOpenRequest anyOpenRequest, CreateGameRequest createGameRequest) {
        try {
            if ((activeGame.getGameType() == GameType.ACCEPT) || (activeGame.getGameType() == GameType.ACCEPTCONTACTSCHALLENGE)) {
                acceptChallenge();
            } else if (activeGame.getGameType() == GameType.MYSTERY) {
                getMysteryGame(anyOpenRequest);
            } else if (activeGame.getGameType() == GameType.GROUP) {
                createNewGame(createGameRequest);
            } else if (activeGame.getGameType() == GameType.REMATCH) {
                createNewGame(createGameRequest);
            } else {
                createNewGame(createGameRequest);
            }
        } catch (Exception e) {
        }
    }

    //accept challenge methode which will not affect current selected module.
    private void acceptChallenge() {
        try {
            if (activeGame.getGroupId().equals("0") || activeGame.getGroupId().isEmpty() || activeGame.getGroupId() == null) {
                getGamePoints();
            } else {
                getPlayResponse();
            }
        } catch (Exception e) {

        }
    }

    private void getGamePoints() {
        try {
            String get_game_points_url = OustSdkApplication.getContext().getResources().getString(R.string.get_game_points);
            get_game_points_url = get_game_points_url.replace("gameid", String.valueOf(activeGame.getGameid()));
            get_game_points_url = get_game_points_url.replace("studentid", activeGame.getOpponentid());
            JSONObject jsonParams = OustSdkTools.getRequestObjectforJSONObject(null);
            get_game_points_url = HttpManager.getAbsoluteUrl(get_game_points_url);

            ApiCallUtils.doNetworkCall(Request.Method.GET, get_game_points_url, jsonParams, new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Gson gson = new Gson();
                        gamePoints = gson.fromJson(response.toString(), GamePoints.class);
                    } catch (Exception e) {
                    }
                    if (gamePoints != null) {
                        getPlayResponse();
                    } else {
                        restApiFailedDuetoNet();
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    restApiFailedDuetoNet();
                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, get_game_points_url, jsonParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Gson gson = new Gson();
                        gamePoints = gson.fromJson(response.toString(), GamePoints.class);
                    } catch (Exception e) {
                    }
                    if (gamePoints != null) {
                        getPlayResponse();
                    } else {
                        restApiFailedDuetoNet();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    restApiFailedDuetoNet();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
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
        }
    }


    private void getPlayResponse() {
        try {
            String play_game_url = OustSdkApplication.getContext().getResources().getString(R.string.play_game);

            AssmntGamePlayRequest assmntGamePlayRequest = new AssmntGamePlayRequest();
            assmntGamePlayRequest.setGameid(activeGame.getGameid());
            assmntGamePlayRequest.setStudentid(OustAppState.getInstance().getActiveUser().getStudentid());
            assmntGamePlayRequest.setOnlyQId(true);
            assmntGamePlayRequest.setDevicePlatformName("android");
            final Gson gson = new Gson();
            String jsonParams = gson.toJson(assmntGamePlayRequest);

            play_game_url = HttpManager.getAbsoluteUrl(play_game_url);

            ApiCallUtils.doNetworkCall(Request.Method.POST, play_game_url, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    playResponse = gson.fromJson(response.toString(), PlayResponse.class);
                    gotPlayResponse();
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    gotPlayResponse();
                }
            });


        } catch (Exception e) {
            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, play_game_url, jsonParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    playResponse = gson.fromJson(response.toString(), PlayResponse.class);
                    gotPlayResponse();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    gotPlayResponse();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
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

        }
    }

    private void gotPlayResponse() {
        try {
            if (null != playResponse) {
                if (playResponse.isSuccess()) {
                    if (playResponse.getEncrypQuestions() != null) {
                        questionProcessFinish();
                    } else {
                        showToastErrorMsg(getResources().getString(R.string.server_error_no_qstn));
                    }
                } else {
                    if (playResponse.getPopup() != null) {
                        showPopup(playResponse.getPopup());
                    } else {
                        showToastErrorMsg(playResponse.getError());
                    }
                }
            } else {
                restApiFailedDuetoNet();
            }
        } catch (Exception e) {
        }
    }

    private void showPopup(Popup popup) {
        try {
            hidePlayLoader();
            go.setEnabled(true);
            isgameLoaded = false;
            gobuttonPressed = false;
            OustStaticVariableHandling.getInstance().setOustpopup(popup);
            OustAppState.getInstance().setHasPopup(false);
            Gson gson = new GsonBuilder().create();
            Intent intent = new Intent(PlayActivity.this, PopupActivity.class);
            intent.putExtra("ActiveGame", gson.toJson(this.activeGame));
            startActivity(intent);
            finish();
        } catch (Exception e) {
        }
    }


    private void getMysteryGame(AnyOpenRequest anyOpenRequest) {
        try {
            Gson gson = new GsonBuilder().create();
            String jsonDataReq = gson.toJson(anyOpenRequest);
            JSONObject jsonParams = OustSdkTools.getRequestObject(jsonDataReq);

            String anyopen_game_url = getResources().getString(R.string.anyopen_game);
            anyopen_game_url = HttpManager.getAbsoluteUrl(anyopen_game_url);

            ApiCallUtils.doNetworkCall(Request.Method.POST, anyopen_game_url, jsonParams, new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new GsonBuilder().create();
                    anyOpenResponse = gson.fromJson(response.toString(), AnyOpenResponse.class);
                    gotAnyOpenGameResponse();
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    gotAnyOpenGameResponse();
                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, anyopen_game_url, jsonParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new GsonBuilder().create();
                    anyOpenResponse = gson.fromJson(response.toString(), AnyOpenResponse.class);
                    gotAnyOpenGameResponse();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    gotAnyOpenGameResponse();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
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

        }
    }

    private void gotAnyOpenGameResponse() {
        try {
            if (anyOpenResponse != null) {
                if (anyOpenResponse.isSuccess()) {
                    if (!activeUser.getStudentid().equalsIgnoreCase(anyOpenResponse.getChallengerId())) {
                        activeGame.setOpponentAvatar(anyOpenResponse.getChallengerAvatar());
                        activeGame.setOpponentDisplayName(anyOpenResponse.getChallengerDisplayName());
                        activeGame.setOpponentid(anyOpenResponse.getChallengerId());
                    }
                    if (anyOpenResponse.getChallengerId() != null) {
                        activeGame.setGameid(anyOpenResponse.getGameid());
                        getGamePoints();
                    } else {
                        getPlayResponse();
                    }
                } else {
                    if (anyOpenResponse != null) {
                        if (!anyOpenResponse.isSuccess()) {
                            if (anyOpenResponse.getPopup() != null) {
                                hidePlayLoader();
                                go.setEnabled(true);
                                isgameLoaded = false;
                                gobuttonPressed = false;
                                OustStaticVariableHandling.getInstance().setOustpopup(anyOpenResponse.getPopup());
                                Gson gson = new GsonBuilder().create();
                                Intent intent = new Intent(OustSdkApplication.getContext(), PopupActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("ActiveGame", gson.toJson(this.activeGame));
                                OustSdkApplication.getContext().startActivity(intent);
                            } else if ((anyOpenResponse.getError() != null) && (!anyOpenResponse.getError().isEmpty())) {
                                OustSdkTools.showToast(anyOpenResponse.getError());
                            } else {
                                OustSdkTools.showToast(getResources().getString(R.string.internal_server_error));
                            }
                        }
                    }
                }
            } else {
                restApiFailedDuetoNet();
            }
        } catch (Exception e) {
        }
    }

    public void rematchGame() {
        try {
            getPlayResponse();
        } catch (Exception e) {
        }
    }

    public void createNewGame(CreateGameRequest createGameRequest) {
        try {
            Gson gson = new GsonBuilder().create();
            String jsonDataReq = gson.toJson(createGameRequest);
            JSONObject jsonParams = OustSdkTools.getRequestObject(jsonDataReq);

            String create_game_url = getResources().getString(R.string.create_game);
            create_game_url = HttpManager.getAbsoluteUrl(create_game_url);

            ApiCallUtils.doNetworkCall(Request.Method.POST, create_game_url, jsonParams, new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    createGameResponse = gson.fromJson(response.toString(), CreateGameResponse.class);
                    gotCreateGameResponse();
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    gotCreateGameResponse();
                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, create_game_url, jsonParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    createGameResponse = gson.fromJson(response.toString(), CreateGameResponse.class);
                    gotCreateGameResponse();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    gotCreateGameResponse();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
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
        }
    }

    private void gotCreateGameResponse() {
        try {
            if (createGameResponse != null) {
                if (createGameResponse.isSuccess()) {
                    Log.d(TAG, createGameResponse.toString());
                    activeGame.setGameid("" + createGameResponse.getGameid());
                    getPlayResponse();
                } else {
                    OustSdkTools.handlePopup(createGameResponse);
                }
            } else {
                restApiFailedDuetoNet();
            }
        } catch (Exception e) {
        }
    }

    public void restApiFailedDuetoNet() {
        try {
            OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg));
            PlayActivity.this.finish();
        } catch (Exception e) {
        }
    }

    //==============================================================================================================
    public void questionProcessFinish() {
        //this you will received submitResponse fired from async class of onPostExecute(submitResponse) method.
        // call Question page
        isgameLoaded = true;
        try {
            if (gobuttonPressed) {
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                }
                finish();
                hidePlayLoader();
                Gson gson = new GsonBuilder().create();
                Intent intent = new Intent(this, QuestionActivity.class);
                intent.putExtra("ActiveUser", gson.toJson(activeUser));
                intent.putExtra("ActiveGame", gson.toJson(this.activeGame));
                intent.putExtra("CreateGameResponse", gson.toJson(this.createGameResponse));
                OustAppState.getInstance().setPlayResponse(this.playResponse);
                intent.putExtra("GamePoints", gson.toJson(this.gamePoints));
                startActivity(intent);
            }
        } catch (Exception e) {
        }

    }

    public void showToastErrorMsg(String error) {
        hidePlayLoader();
        go.setEnabled(true);
        closeBtnLayout.setVisibility(View.VISIBLE);
        OustSdkTools.showToast("  " + error + " ");
    }


    public void networkStatus() {
        String status = NetworkUtil.getConnectivityStatusString(this);
        Log.d(TAG, "Network Availability");
        Log.d(TAG, status);
        switch (status) {
            case "Connected to Internet with Mobile Data":
                isNetworkAvailable = true;
                break;
            case "Connected to Internet with WIFI":
                isNetworkAvailable = true;
                break;
            default:
                OustSdkTools.getInstance().showToast(status);
                isNetworkAvailable = false;
                break;
        }

    }

    @Override
    public void onBackPressed() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
        }
        cancleGame();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "OnPause calling-->");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "OnDestroy calling -->");
    }
//==========================================================================================================================
//cancle game methodes.

    private void cancleGame() {
        try {
            View popUpView = getLayoutInflater().inflate(R.layout.rejectgamepopup, null);
            final PopupWindow plaayCanclePopup = OustSdkTools.createPopUp(popUpView);
            final Button btnYes = popUpView.findViewById(R.id.btnYes);
            final Button btnNo = popUpView.findViewById(R.id.btnNo);
            final ImageButton btnClose = popUpView.findViewById(R.id.btnClose);
            TextView popupTitle = popUpView.findViewById(R.id.txtTitle);
            TextView popupContent = popUpView.findViewById(R.id.txtRejectChallengeMsg);

            btnYes.setTypeface(OustSdkTools.getAvenirLTStdMedium());
            btnNo.setTypeface(OustSdkTools.getAvenirLTStdMedium());
            popupContent.setTypeface(OustSdkTools.getAvenirLTStdMedium());
            popupTitle.setTypeface(OustSdkTools.getAvenirLTStdMedium());

            popupTitle.setText(getResources().getString(R.string.cancel_game));
            popupContent.setText(getResources().getString(R.string.cancel_game_confirmation));
            btnYes.setText(getResources().getString(R.string.yes));
            btnNo.setText(getResources().getString(R.string.no));

            OustPreferences.saveAppInstallVariable("isContactPopup", true);
            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if ((activeGame.getGameType() == GameType.CHALLENGE) || (activeGame.getGameType() == GameType.CONTACTSCHALLENGE)) {
                            cancleGameApi();
                        } else if ((activeGame.getGameType() == GameType.MYSTERY) &&
                                (activeGame.getOpponentid() != null) &&
                                (!activeGame.getOpponentid().equalsIgnoreCase("mystery")) &&
                                (!activeGame.getOpponentid().isEmpty())) {
                            reopenGameApi();
                        } else {
                            PlayActivity.this.finish();
                        }
                        plaayCanclePopup.dismiss();
                    } catch (Exception e) {
                    }
                }
            });

            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    plaayCanclePopup.dismiss();
                }
            });

            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    plaayCanclePopup.dismiss();
                }
            });
        } catch (Exception e) {
        }
    }

    private void cancleGameApi() {
        try {
            gobuttonPressed = false;
            if (activeGame.getGameid() != null && (!activeGame.getGameid().equalsIgnoreCase(""))) {
                if (activeGame.getGameid() != null) {
                    String cancleGameUrl = getResources().getString(R.string.cancleGamereso);
                    cancleGameUrl = cancleGameUrl.replace("{gameid}", activeGame.getGameid());

                    JSONObject jsonParams = OustSdkTools.getRequestObjectforJSONObject(null);
                    cancleGameUrl = HttpManager.getAbsoluteUrl(cancleGameUrl);

                    ApiCallUtils.doNetworkCall(Request.Method.DELETE, cancleGameUrl, jsonParams, new ApiCallUtils.NetworkCallback() {
                        @Override
                        public void onResponse(JSONObject response) {

                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });


                    /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.DELETE, cancleGameUrl, jsonParams, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
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
                }
            }
            OustAppState.getInstance().setHasPopup(false);
            finish();
        } catch (Exception e) {
        }
    }

    private void reopenGameApi() {
        try {
            gobuttonPressed = false;
            if (activeGame.getGameid() != null && (!activeGame.getGameid().equalsIgnoreCase(""))) {
                if (activeGame.getGameid() != null) {
                    String reopengameurl = getResources().getString(R.string.reopengameurl);
                    reopengameurl = HttpManager.getAbsoluteUrl(reopengameurl);

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("studentid", activeUser.getStudentid());
                    jsonObject.put("gameId", activeGame.getGameid());
                    JSONObject jsonParams = OustSdkTools.getRequestObjectforJSONObject(jsonObject);

                    ApiCallUtils.doNetworkCall(Request.Method.DELETE, reopengameurl, jsonParams, new ApiCallUtils.NetworkCallback() {
                        @Override
                        public void onResponse(JSONObject response) {

                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });


                    /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.DELETE, reopengameurl, jsonParams, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
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
                }
            }
            OustAppState.getInstance().setHasPopup(false);
            finish();
        } catch (Exception e) {
        }
    }

    //==============================================================================================================
    //submit last played geme if any(not submitted last time)
    private void submitLastGame() {
        try {
            networkStatus();
            if (!isNetworkAvailable) {
                OustSdkTools.showToast(getResources().getString(R.string.no_internet_connection));
                return;
            }
            String lastgame = OustPreferences.get("lastgamesubmitrequest");
            if ((lastgame != null) && (!lastgame.equalsIgnoreCase(""))) {
                OustSdkTools.showToast(getResources().getString(R.string.previousGameSubmit));
                Gson gson = new GsonBuilder().create();
                SubmitRequest submitRequest = gson.fromJson(lastgame, SubmitRequest.class);
                if (submitRequest != null) {
                    submitScore(submitRequest);
                } else {
                    initGame();
                }
            } else {
                initGame();
            }
        } catch (Exception e) {
            initGame();
        }
    }

    public void submitScore(SubmitRequest submitRequest) {
        try {

            Gson gson = new GsonBuilder().create();
            String jsonParams = gson.toJson(submitRequest);
            JSONObject parsedJsonParams = OustSdkTools.getRequestObject(jsonParams);
            String submitGameUrl = OustSdkApplication.getContext().getResources().getString(R.string.submit_game);
            submitGameUrl = HttpManager.getAbsoluteUrl(submitGameUrl);

            /*JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, submitGameUrl, parsedJsonParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        Gson gson = new GsonBuilder().create();
                        SubmitResponse submitResponse = gson.fromJson(response.toString(), SubmitResponse.class);
                        if (submitResponse.isSuccess()) {
                            OustPreferences.save("lastgamesubmitrequest", "");
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
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
            jsonObjectRequest.setShouldCache(false);
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjectRequest, "first");*/

            ApiCallUtils.doNetworkCallForSubmitGame(Request.Method.POST, submitGameUrl, parsedJsonParams, new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        Gson gson = new GsonBuilder().create();
                        SubmitResponse submitResponse = gson.fromJson(response.toString(), SubmitResponse.class);
                        if (submitResponse.isSuccess()) {
                            OustPreferences.save("lastgamesubmitrequest", "");
                        }
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });


            initGame();
        } catch (Exception e) {
            initGame();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            PlayActivity.this.finish();
        } catch (Exception e) {
        }
    }
}
