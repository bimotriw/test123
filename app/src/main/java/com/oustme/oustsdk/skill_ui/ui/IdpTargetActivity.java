package com.oustme.oustsdk.skill_ui.ui;

import static android.graphics.Typeface.BOLD;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.skill_ui.FullScreenVideoActivity;
import com.oustme.oustsdk.skill_ui.adapter.IdpAnalyticsAdapter;
import com.oustme.oustsdk.skill_ui.model.CardCommonDataMap;
import com.oustme.oustsdk.skill_ui.model.CardInfo;
import com.oustme.oustsdk.skill_ui.model.CardMediaList;
import com.oustme.oustsdk.skill_ui.model.IdpAnalyticsResponse;
import com.oustme.oustsdk.skill_ui.model.UserSkillData;
import com.oustme.oustsdk.skill_ui.model.UserSoccerIDPSkillAnalyticsData;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class IdpTargetActivity extends AppCompatActivity {

    Toolbar toolbar_lay;
    FrameLayout toolbar_close_icon;
    FrameLayout leadboard_lay;
    TextView skill_category;
    TextView skill_description;
    CardView image_container;
    FrameLayout image_container_frame;
    ImageView skill_image_thumbnail;
    RecyclerView idp_list_rv;
    ImageView skill_bg;
    UserSkillData skillData, userSkillData;

    String skillName = "", categoryName;
    long skillId;
    ActiveUser activeUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OustSdkTools.setLocale(IdpTargetActivity.this);
        setContentView(R.layout.activity_idp_target);

        skill_bg = findViewById(R.id.skill_bg);
        idp_list_rv = findViewById(R.id.idp_list_rv);
        skill_image_thumbnail = findViewById(R.id.skill_image_thumbnail);
        image_container_frame = findViewById(R.id.image_container_frame);
        image_container = findViewById(R.id.image_container);
        skill_description = findViewById(R.id.skill_description);
        skill_category = findViewById(R.id.skill_category);
        leadboard_lay = findViewById(R.id.leadboard_lay);
        toolbar_close_icon = findViewById(R.id.toolbar_close_icon);
        toolbar_lay = findViewById(R.id.toolbar_lay);

        String toolbarColorCode = OustPreferences.get("toolbarColorCode");
        toolbar_lay.setBackgroundColor(Color.parseColor(toolbarColorCode));

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            skillName = bundle.getString("skillName", "");
            categoryName = bundle.getString("category", "");
            skillId = bundle.getLong("skillId", 0);
        }

        initData();
    }

    private void initData() {

        activeUser = OustAppState.getInstance().getActiveUser();
        if (activeUser != null) {
            if (skillId != 0) {
                if (OustSdkTools.checkInternetStatus()) {
                    getUserSkillData(activeUser);
                }

                leadboard_lay.setOnClickListener(v -> {
                    Intent leaderIntent = new Intent(IdpTargetActivity.this, SkillLeaderBoardActivity.class);
                    leaderIntent.putExtra("skillId", skillId);
                    startActivity(leaderIntent);
                });
            }
        } else {
            IdpTargetActivity.this.finish();
        }

        toolbar_close_icon.setOnClickListener(v -> IdpTargetActivity.this.finish());

    }

    public void getUserSkillData(ActiveUser activeUser) {
        try {
            String node = "/soccerSkill/soccerSkill" + skillId;
            ValueEventListener skillListner = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {

                        final Map<String, Object> skillProgessData = (Map<String, Object>) dataSnapshot.getValue();
                        try {
                            if (skillProgessData != null) {


                                Gson gson = new Gson();
                                HashMap<String, Object> cardCommonDataMap;
                                CardCommonDataMap cardCommonDataMapObject = new CardCommonDataMap();
                                if (skillProgessData.get("cardCommonDataMap") != null) {
                                    cardCommonDataMap = (HashMap<String, Object>) skillProgessData.get("cardCommonDataMap");

                                    if (cardCommonDataMap != null) {
                                        for (String id : cardCommonDataMap.keySet()) {

                                            if (id.equals("attemptCount")) {
                                                cardCommonDataMapObject.setAttemptCount((long) cardCommonDataMap.get("attemptCount"));

                                            } else if (id.equals("userBestScore")) {
                                                cardCommonDataMapObject.setUserBestScore((long) cardCommonDataMap.get("userBestScore"));
                                            } else {
                                                try {
                                                    long cardId = Long.parseLong(id);
                                                    if (cardId != 0) {
                                                        final HashMap<String, CardInfo> cardInfoHashMap = (HashMap<String, CardInfo>) cardCommonDataMap.get(id);
                                                        JsonElement cardElement = gson.toJsonTree(cardInfoHashMap);
                                                        CardInfo cardInfo = gson.fromJson(cardElement, CardInfo.class);
                                                        cardCommonDataMapObject.setCardInfo(cardInfo);

                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                                }
                                            }


                                        }
                                    }


                                }
                                JsonElement skillElement = gson.toJsonTree(skillProgessData);
                                skillData = gson.fromJson(skillElement, UserSkillData.class);
                                skillData.setCardCommonDataMap(cardCommonDataMapObject);
                                setData(null, skillData);
                                String node = "/landingPage/" + activeUser.getStudentKey() + "/soccerSkill/soccerSkill" + skillId;
                                ValueEventListener skillListner = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        try {

                                            final Map<String, Object> skillProgessData = (Map<String, Object>) dataSnapshot.getValue();
                                            try {
                                                if (skillProgessData != null) {


                                                    Gson gson = new Gson();
                                                    HashMap<String, Object> cardCommonDataMap;
                                                    CardCommonDataMap cardCommonDataMapObject = new CardCommonDataMap();
                                                    if (skillProgessData.get("cardCommonDataMap") != null) {
                                                        cardCommonDataMap = (HashMap<String, Object>) skillProgessData.get("cardCommonDataMap");

                                                        if (cardCommonDataMap != null) {
                                                            for (String id : cardCommonDataMap.keySet()) {

                                                                if (id.equals("attemptCount")) {
                                                                    cardCommonDataMapObject.setAttemptCount((long) cardCommonDataMap.get("attemptCount"));

                                                                } else if (id.equals("userBestScore")) {
                                                                    cardCommonDataMapObject.setUserBestScore((long) cardCommonDataMap.get("userBestScore"));
                                                                } else {
                                                                    try {
                                                                        long cardId = Long.parseLong(id);
                                                                        if (cardId != 0) {
                                                                            final HashMap<String, CardInfo> cardInfoHashMap = (HashMap<String, CardInfo>) cardCommonDataMap.get(id);
                                                                            JsonElement cardElement = gson.toJsonTree(cardInfoHashMap);
                                                                            CardInfo cardInfo = gson.fromJson(cardElement, CardInfo.class);
                                                                            cardCommonDataMapObject.setCardInfo(cardInfo);

                                                                        }
                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                                                    }
                                                                }


                                                            }
                                                        }


                                                    }
                                                    JsonElement skillElement = gson.toJsonTree(skillProgessData);
                                                    userSkillData = gson.fromJson(skillElement, UserSkillData.class);
                                                    userSkillData.setCardCommonDataMap(cardCommonDataMapObject);
                                                    //setData(userSkillData, skillData);

                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                };
                                OustFirebaseTools.getRootRef().child(node).addListenerForSingleValueEvent(skillListner);
                                OustFirebaseTools.getRootRef().child(node).keepSynced(true);


                            }
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            OustFirebaseTools.getRootRef().child(node).addListenerForSingleValueEvent(skillListner);
            OustFirebaseTools.getRootRef().child(node).keepSynced(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setData(UserSkillData userSkillData, UserSkillData skillData) {

        if (skillData != null) {
            String skillNametext = skillData.getSoccerSkillName();
            if (skillNametext.contains("<br/>")) {
                skillNametext = skillNametext.replace("<br/>", "");
            }
            if (skillNametext.contains("<br>")) {
                skillNametext = skillNametext.replace("<br>", "");
            }
            //  skill_title.setText(skillNametext);
            if (categoryName.contains("<br/>")) {
                categoryName = categoryName.replace("<br/>", "");
            }
            if (categoryName.contains("<br>")) {
                categoryName = categoryName.replace("<br>", "");
            }
            String category = getResources().getString(R.string.category_text) + " : " + categoryName;
            int length = skillNametext.length();
            String skillDes = skillData.getSoccerSkillDescription();
            if (skillDes.contains("<br/>")) {
                skillDes = skillDes.replace("<br/>", "");
            }
            if (skillDes.contains("<br>")) {
                skillDes = skillDes.replace("<br>", "");
            }
            String skillName = skillNametext + " : " + skillDes;
            //    skills_name.setText(skillName);
            Spannable yourScoreSpan = new SpannableString(skillName);
            yourScoreSpan.setSpan(new StyleSpan(BOLD), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            skill_description.setText(yourScoreSpan);
            skill_category.setText(category);


            if (skillData.getBgImg() != null && !skillData.getBgImg().isEmpty()) {
                Picasso.get().load(skillData.getBgImg()).error(R.drawable.skill_bg).into(skill_bg);
            }

            String imageUrl = "";
            if (skillData.getThumbnailImg() != null && !skillData.getThumbnailImg().isEmpty()) {
                imageUrl = skillData.getThumbnailImg();

                Picasso.get().load(skillData.getThumbnailImg())
                        .into(skill_image_thumbnail);
            }

            if (skillData.isShowLeaderboard()) {
                leadboard_lay.setVisibility(VISIBLE);
            } else {
                leadboard_lay.setVisibility(GONE);
            }

            long cardId = 0;
            if (skillData.getCardCommonDataMap() != null) {

                if (skillData.getCardCommonDataMap().getCardInfo() != null) {
                    cardId = skillData.getCardCommonDataMap().getCardInfo().getCardId();
                    if (skillData.getCardCommonDataMap().getCardInfo().getCardMediaList() != null
                            && skillData.getCardCommonDataMap().getCardInfo().getCardMediaList().size() != 0) {

                        CardMediaList cardMediaList = skillData.getCardCommonDataMap().getCardInfo().getCardMediaList().get(0);

                        if (cardMediaList.getMediaThumbnail() != null && !cardMediaList.getMediaThumbnail().isEmpty()) {
                            imageUrl = cardMediaList.getMediaThumbnail();

                            Picasso.get().load(cardMediaList.getMediaThumbnail())
                                    .into(skill_image_thumbnail);
                        } /*else {
                            //prepareVideo(cardMediaList.getData());
                            skill_video_thumbnail.setVisibility(GONE);
                        }*/
                        if (cardMediaList.getMediaType() != null && (cardMediaList.getMediaType().equalsIgnoreCase("VIDEO") || cardMediaList.getMediaType().contains("VIDEO"))) {
                            if (cardMediaList.getMediaPrivacy() != null && cardMediaList.getMediaPrivacy().equalsIgnoreCase("PRIVATE")) {


                                image_container_frame.setOnClickListener(v -> {
                                    Intent video = new Intent(IdpTargetActivity.this, FullScreenVideoActivity.class);
                                    video.putExtra("videoName", cardMediaList.getData());
                                    video.putExtra("videoType", "Private");
                                    startActivity(video);
                                });

                            } else {
                                image_container_frame.setOnClickListener(v -> {
                                    Intent video = new Intent(IdpTargetActivity.this, FullScreenVideoActivity.class);
                                    video.putExtra("videoName", cardMediaList.getData());
                                    video.putExtra("videoType", "Public");
                                    startActivity(video);
                                });


                                // Toast.makeText(SkillDetailActivity.this, "public", Toast.LENGTH_SHORT).show();

                            }

                        }

                    }
                }/* else {
                    play_button.setVisibility(GONE);
                    play_thumbnail.setVisibility(GONE);
                }
*/

            } /*else {
                play_button.setVisibility(GONE);
                play_thumbnail.setVisibility(GONE);
            }*/

            apiCallForIdpAnalytics();
        }


    }

    private void apiCallForIdpAnalytics() {

        String skill_analytics_url = OustSdkApplication.getContext().getResources().getString(R.string.skill_idp_analytics_url);
        skill_analytics_url = skill_analytics_url.replace("{userId}", activeUser.getStudentid());
        skill_analytics_url = skill_analytics_url.replace("{soccerSkillId}", "" + skillId);
        skill_analytics_url = HttpManager.getAbsoluteUrl(skill_analytics_url);
        JSONObject jsonParams = new JSONObject();

        ApiCallUtils.doNetworkCall(Request.Method.GET, skill_analytics_url, OustSdkTools.getRequestObjectforJSONObject(jsonParams),
                new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.optBoolean("success")) {
                                IdpAnalyticsResponse idpAnalyticsResponse = new Gson().fromJson(response.toString(), IdpAnalyticsResponse.class);

                                if (idpAnalyticsResponse != null &&
                                        idpAnalyticsResponse.getUserSoccerIDPSkillAnalyticsData() != null &&
                                        idpAnalyticsResponse.getUserSoccerIDPSkillAnalyticsData().size() != 0) {

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                               /* Collections.sort(userSkillDataArrayList,
                                                                       UserSkillData.skillDataComparator);*/
                                        Collections.sort(idpAnalyticsResponse.getUserSoccerIDPSkillAnalyticsData(),
                                                Comparator.comparingLong(UserSoccerIDPSkillAnalyticsData::getIdpTargetDate));
                                    }


                                    IdpAnalyticsAdapter adapter = new IdpAnalyticsAdapter();
                                    Collections.reverse(idpAnalyticsResponse.getUserSoccerIDPSkillAnalyticsData());
                                    adapter.setIdpAnalyticsAdapter(idpAnalyticsResponse.getUserSoccerIDPSkillAnalyticsData(), IdpTargetActivity.this);

                                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(IdpTargetActivity.this);
                                    idp_list_rv.setLayoutManager(mLayoutManager);
                                    idp_list_rv.setItemAnimator(new DefaultItemAnimator());
                                    idp_list_rv.setAdapter(adapter);


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
                        OustSdkTools.showToast("API Failed");
                    }
                });


    }
}
