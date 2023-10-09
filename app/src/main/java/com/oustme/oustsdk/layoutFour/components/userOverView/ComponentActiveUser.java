package com.oustme.oustsdk.layoutFour.components.userOverView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.Html;
import android.text.Spannable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.UserSettingActivity;
import com.oustme.oustsdk.customviews.CircleImageView;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.layoutFour.components.myTask.MyTaskActivity;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;


public class ComponentActiveUser extends LinearLayout {

    private TextView txtUser;
    private TextView txtCoin;
    private TextView txtPending;
    private CircleImageView ivAvatar;
    private int color;

    public ComponentActiveUser(Context context, @Nullable AttributeSet attrs) {

        super(context, attrs);
        initView();
    }

    private void initView() {

        try {

            getColors();

            setOrientation(LinearLayout.HORIZONTAL);
            setGravity(Gravity.CENTER_VERTICAL);

            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.component_user_activity, this, true);
            txtUser = findViewById(R.id.user_name);
            txtCoin = findViewById(R.id.coins_text);

            try {
                ImageView img_coin = findViewById(R.id.img_coin);
                if (OustPreferences.getAppInstallVariable("showCorn")) {
                    img_coin.setImageResource(R.drawable.ic_coins_corn);
                } else {
                    img_coin.setImageResource(R.drawable.ic_coins_golden);
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

            txtPending = findViewById(R.id.pending_count);
            ivAvatar = findViewById(R.id.user_avatar);
            TextView btnMyTask = findViewById(R.id.mytask);
            GradientDrawable goButtonBg = (GradientDrawable) btnMyTask.getBackground();
            goButtonBg.setStroke(3, getResources().getColor(R.color.primary_text));
            btnMyTask.setBackground(goButtonBg);


            ivAvatar.setBorderColor(color);

            ivAvatar.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), UserSettingActivity.class);
                getContext().startActivity(intent);
            });

            btnMyTask.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), MyTaskActivity.class);
                getContext().startActivity(intent);
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void getColors() {
        try {
            String themeColorString = OustPreferences.get("toolbarColorCode");
            if ((themeColorString != null) && (!themeColorString.isEmpty())) {
                if (OustPreferences.get("toolbarColorCode") != null) {
                    color = Color.parseColor(OustPreferences.get("toolbarColorCode"));
                }
            }

            if (color == 0)
                color = OustSdkTools.getColorBack(R.color.lgreen);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }


    }

    public void setUserData(ActiveUser activeUser, HashMap<String, CommonLandingData> commonLanding) {
        try {
            if (activeUser != null) {
                String greetingWithName = getResources().getString(R.string.welcome) + " " + activeUser.getUserDisplayName();
                if (activeUser.getAvatar() != null && !activeUser.getAvatar().isEmpty()) {
                    Picasso.get().load(activeUser.getAvatar()).placeholder(R.drawable.ic_user_avatar).networkPolicy(NetworkPolicy.OFFLINE).into(ivAvatar);
                }
                txtUser.setText(greetingWithName);
            }

            if (commonLanding != null) {
                long pendingCount = 0;
                long coins = 0;
                for (String key : commonLanding.keySet()) {
                    if (commonLanding.get(key) != null) {
                        if (Objects.requireNonNull(commonLanding.get(key)).getCompletionPercentage() < 100) {
                            pendingCount++;
                        } else {
                            coins += Objects.requireNonNull(commonLanding.get(key)).getUserOc();
                        }
                    }
                }

                String pendingTask = getResources().getString(R.string.you_have) + "<b> " + pendingCount + " </b>" +
                        getResources().getString(R.string.pending_tasks);
                txtCoin.setText(String.valueOf(coins));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    txtPending.setText(Html.fromHtml(pendingTask, Html.FROM_HTML_MODE_COMPACT));
                } else {
                    txtPending.setText(Html.fromHtml(pendingTask));
                }

                //txtRank.setText(String.valueOf(commonLanding.getRank()));
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }


    }
}
