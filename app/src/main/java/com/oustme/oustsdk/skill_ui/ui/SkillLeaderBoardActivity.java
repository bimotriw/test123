package com.oustme.oustsdk.skill_ui.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.skill_ui.adapter.SkillLeaderBoardAdapter;
import com.oustme.oustsdk.skill_ui.model.LeaderBoardData;
import com.oustme.oustsdk.skill_ui.model.LeaderBoardResponse;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SkillLeaderBoardActivity extends AppCompatActivity {

    Toolbar toolbar_lay;
    ImageView toolbar_close;
    FrameLayout skill_bg_lay;
    LinearLayout user_data_lay;
    RelativeLayout rank_1_layout;
    CircleImageView rank_1_user;
    TextView rank_1_text;
    RelativeLayout rank_2_layout;
    CircleImageView rank_2_user;
    TextView rank_2_text;
    RelativeLayout rank_3_layout;
    CircleImageView rank_3_user;
    TextView rank_3_text;
    CircleImageView self_user;
    TextView self_user_name;
    TextView self_user_rank;
    TextView self_user_score;
    RecyclerView leaderboard_rv;

    //screen_data
    ActiveUser activeUser;

    //intent_data
    long skillId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OustSdkTools.setLocale(SkillLeaderBoardActivity.this);
        setContentView(R.layout.activity_skill_leader_board);

        leaderboard_rv = findViewById(R.id.leaderboard_rv);
        toolbar_lay = findViewById(R.id.toolbar_lay);
        toolbar_close = findViewById(R.id.toolbar_close);
        user_data_lay = findViewById(R.id.user_data_lay);
        rank_1_layout = findViewById(R.id.rank_1_layout);
        rank_1_user = findViewById(R.id.rank_1_user);
        rank_1_text = findViewById(R.id.rank_1_text);
        rank_2_layout = findViewById(R.id.rank_2_layout);
        rank_2_user = findViewById(R.id.rank_2_user);
        rank_2_text = findViewById(R.id.rank_2_text);
        rank_3_layout = findViewById(R.id.rank_3_layout);
        rank_3_user = findViewById(R.id.rank_3_user);
        rank_3_text = findViewById(R.id.rank_3_text);
        self_user = findViewById(R.id.self_user);
        self_user_name = findViewById(R.id.self_user_name);
        self_user_rank = findViewById(R.id.self_user_rank);
        self_user_score = findViewById(R.id.self_user_score);

        String toolbarColorCode = OustPreferences.get("toolbarColorCode");
        toolbar_lay.setBackgroundColor(Color.parseColor(toolbarColorCode));
        // user_data_lay.setBackgroundColor(Color.parseColor(toolbarColorCode));

        initData();

        toolbar_close.setOnClickListener(v -> SkillLeaderBoardActivity.this.finish());
    }

    private void initData() {
        activeUser = OustAppState.getInstance().getActiveUser();
        if (activeUser != null) {

            Bundle dataBundle = getIntent().getExtras();
            if (dataBundle != null) {
                skillId = dataBundle.getLong("skillId");
            }

            if (skillId != 0) {
                apiCallForLeaderBoard();
            }

        } else {
            SkillLeaderBoardActivity.this.finish();
        }
    }


    private void apiCallForLeaderBoard() {

        String skill_leaderboard_url = OustSdkApplication.getContext().getResources().getString(R.string.skill_leaderboard_url);
        skill_leaderboard_url = skill_leaderboard_url.replace("{userId}", activeUser.getStudentid());
        skill_leaderboard_url = skill_leaderboard_url.replace("{soccerSkillId}", "" + skillId);
        skill_leaderboard_url = skill_leaderboard_url.replace("{topCount}", "" + 50);
        skill_leaderboard_url = HttpManager.getAbsoluteUrl(skill_leaderboard_url);
        JSONObject jsonParams = new JSONObject();

        ApiCallUtils.doNetworkCall(Request.Method.GET, skill_leaderboard_url, OustSdkTools.getRequestObjectforJSONObject(jsonParams),
                new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.optBoolean("success")) {

                                LeaderBoardResponse leaderBoardResponse = new Gson().fromJson(response.toString(), LeaderBoardResponse.class);

                                if (leaderBoardResponse != null) {

                                    if (leaderBoardResponse.getLeaderBoardDataList() != null && leaderBoardResponse.getLeaderBoardDataList().size() != 0) {

                                        ArrayList<LeaderBoardData> leaderBoardDataArrayList = leaderBoardResponse.getLeaderBoardDataList();
                                        if ((leaderBoardDataArrayList.size() - 1) >= 1) {
                                            String rank1_user_image = leaderBoardDataArrayList.get(0).getAvatar();
                                            String rank1_user_rank = leaderBoardDataArrayList.get(0).getRank() + "";

                                            if (rank1_user_image != null && !rank1_user_image.isEmpty()) {
                                                Picasso.get().load(rank1_user_image)
                                                        .into(rank_1_user);

                                            }

                                            rank_1_text.setText(rank1_user_rank);
                                        }
                                        if ((leaderBoardDataArrayList.size() - 1) >= 2) {
                                            String rank2_user_image = leaderBoardDataArrayList.get(1).getAvatar();
                                            String rank2_user_rank = leaderBoardDataArrayList.get(1).getRank() + "";

                                            if (rank2_user_image != null && !rank2_user_image.isEmpty()) {
                                                Picasso.get().load(rank2_user_image)
                                                        .into(rank_2_user);
                                            }

                                            rank_2_text.setText(rank2_user_rank);
                                        } else {
                                            rank_2_layout.setVisibility(View.GONE);
                                            rank_3_layout.setVisibility(View.GONE);
                                        }
                                        if ((leaderBoardDataArrayList.size() - 1) >= 3) {
                                            String rank3_user_image = leaderBoardDataArrayList.get(2).getAvatar();
                                            String rank3_user_rank = leaderBoardDataArrayList.get(2).getRank() + "";

                                            if (rank3_user_image != null && !rank3_user_image.isEmpty()) {
                                                Picasso.get().load(rank3_user_image)
                                                        .into(rank_3_user);
                                            }

                                            rank_3_text.setText(rank3_user_rank);
                                        } else {
                                            rank_3_layout.setVisibility(View.GONE);
                                        }

                                        String self_user_image = leaderBoardDataArrayList.get(leaderBoardDataArrayList.size() - 1).getAvatar();
                                        String self_user_ranks = "" + leaderBoardDataArrayList.get(leaderBoardDataArrayList.size() - 1).getRank() + "";
                                        String self_user_scores = "" + leaderBoardDataArrayList.get(leaderBoardDataArrayList.size() - 1).getUserScore() + "";

                                        if (leaderBoardDataArrayList.size() < 4) {
                                            user_data_lay.setVisibility(View.GONE);
                                        }

                                        if (self_user_image != null && !self_user_image.isEmpty()) {
                                            Picasso.get().load(self_user_image)
                                                    .into(self_user);
                                        }

                                        self_user_rank.setText(self_user_ranks);
                                        self_user_score.setText(self_user_scores);
                                        String userDisplayName = "" + activeUser.getUserDisplayName();
                                        self_user_name.setText(userDisplayName);

                                        SkillLeaderBoardAdapter adapter = new SkillLeaderBoardAdapter();
                                        adapter.setSkillLeaderBoardAdapter(leaderBoardDataArrayList, SkillLeaderBoardActivity.this);

                                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(SkillLeaderBoardActivity.this);
                                        leaderboard_rv.setLayoutManager(mLayoutManager);
                                        leaderboard_rv.setItemAnimator(new DefaultItemAnimator());
                                        leaderboard_rv.setAdapter(adapter);


                                    }


                                } else {
                                    OustSdkTools.showToast("Something went wrong");
                                }


                            } else {
                                if (response.optString("error") != null && !response.optString("error").isEmpty()) {
                                    OustSdkTools.showToast(response.optString("error"));
                                } else {
                                    OustSdkTools.showToast("Failed Response");
                                }

                            }
                        } catch (Exception e) {
                            OustSdkTools.showToast(e.getMessage());
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        OustSdkTools.showToast("Leaderboard API Failed");
                    }
                });

    }
}
