package com.oustme.oustsdk.layoutFour.newnoticeBoard.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.R;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.adapters.NewNBCreatePostListAdapter;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBTopicData;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.utils.OustResourceUtils;

import java.util.ArrayList;

public class NewNBCreatePostList extends AppCompatActivity {
    private ArrayList<NewNBTopicData> nbTopicData1 = new ArrayList<>();
    private int sortPosition;
    private LinearLayout dialog_ok;
    private Toolbar toolbar;
    private TextView screen_name;
    private ImageView back_button;
    private int color;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nb_create_post_list);
        RecyclerView list_view = findViewById(R.id.list_view);
        dialog_ok = findViewById(R.id.dialog_ok);
        toolbar = findViewById(R.id.toolbar_notification_layout);
        back_button = findViewById(R.id.back_button);
        screen_name = findViewById(R.id.screen_name);

        try {
            nbTopicData1 = (ArrayList<NewNBTopicData>) getIntent().getSerializableExtra("listIs");

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        disable(dialog_ok);

        OustStaticVariableHandling.getInstance().setSortPosition(-1);

        if (OustStaticVariableHandling.getInstance().getSortPosition() != -1) {
            enable(dialog_ok);
        }

        getColors();

        setToolbar();

        back_button.setOnClickListener(v -> onBackPressed());

        NewNBCreatePostListAdapter newNBCreatePostListAdapter = new NewNBCreatePostListAdapter(nbTopicData1, NewNBCreatePostList.this, new NewNBCreatePostListAdapter.NewNBCreatePostItemListener() {
            @Override
            public void onItemClicked(int position) {
                if (OustStaticVariableHandling.getInstance().getSortPosition() != -1) {
                    sortPosition = position;
                    enable(dialog_ok);
                }
            }
        });

        dialog_ok.setOnClickListener(v -> {
            try {
                Log.d("NBTopicDetailed01-->", " " + nbTopicData1.get(sortPosition).getId());
                Intent intent = new Intent(NewNBCreatePostList.this, NewNBPostCreateActivity.class);
                if (nbTopicData1 != null) {
                    intent.putExtra("NBID", nbTopicData1.get(sortPosition).getId());
                    intent.putExtra("NBTitle", nbTopicData1.get(sortPosition).getTopic());
                    intent.putExtra("nbPostRewardOC", nbTopicData1.get(sortPosition).getNbPostRewardOC());
                }
                startActivity(intent);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(NewNBCreatePostList.this, LinearLayoutManager.VERTICAL, false);
        list_view.setLayoutManager(mLayoutManager);
        list_view.setItemAnimator(new DefaultItemAnimator());
        list_view.setAdapter(newNBCreatePostListAdapter);
    }

    private void setToolbar() {
        try {
            toolbar.setBackgroundColor(Color.WHITE);
            screen_name.setTextColor(color);
            screen_name.setText("CREATE POST");
            OustResourceUtils.setDefaultDrawableColor(back_button.getDrawable(), color);
            toolbar.setTitle("");
            setSupportActionBar(toolbar);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getColors() {
        try {
            if (OustPreferences.getAppInstallVariable("isLayout4")) {
                color = OustResourceUtils.getColors();
            } else {
                color = OustResourceUtils.getToolBarBgColor();

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void enable(LinearLayout linearLayout) {
        linearLayout.setBackground(OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.button_rounded_ten_bg)));
        linearLayout.setClickable(true);
    }

    private void disable(LinearLayout linearLayout) {
        linearLayout.setBackground(getResources().getDrawable(R.drawable.button_rounded_ten_bg));
        linearLayout.setClickable(false);
    }

}
