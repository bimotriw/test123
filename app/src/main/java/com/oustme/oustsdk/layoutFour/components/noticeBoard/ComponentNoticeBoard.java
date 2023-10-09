package com.oustme.oustsdk.layoutFour.components.noticeBoard;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.noticeBoard.adapters.NBTopicAdapter;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBTopicData;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.List;

public class ComponentNoticeBoard extends LinearLayout  {
    private RecyclerView rvNoticeBoard;
    private NBTopicAdapter nbTopicAdapter;

    View no_data_layout;
    ImageView no_image;
    TextView no_data_content;

    public ComponentNoticeBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.component_noticeboard, this, true);

        initViews();
    }

    private void initViews() {
        rvNoticeBoard = findViewById(R.id.rv_noticeboard);
        no_data_layout = findViewById(R.id.no_data_layout);
        no_image = no_data_layout.findViewById(R.id.no_image);
        no_data_content = no_data_layout.findViewById(R.id.no_data_content);

        no_image.setImageDrawable(getResources().getDrawable(R.drawable.no_noticeboard));
        no_data_content.setText(getResources().getString(R.string.no_noticeboard_content));
    }

    public void setData(List<NBTopicData> nbTopicDataArrayList) {
            no_data_layout.setVisibility(View.GONE);
            rvNoticeBoard.setVisibility(View.VISIBLE);
            nbTopicAdapter = new NBTopicAdapter(getContext(), nbTopicDataArrayList);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            rvNoticeBoard.setLayoutManager(mLayoutManager);
            rvNoticeBoard.setItemAnimator(new DefaultItemAnimator());
            rvNoticeBoard.setAdapter(nbTopicAdapter);
    }

    public void setNoDataImage() {
        no_data_layout.setVisibility(View.VISIBLE);
        rvNoticeBoard.setVisibility(View.GONE);
    }

    public void onNoticeBoardSearch(String query) {
        try {
            if (nbTopicAdapter != null){
                nbTopicAdapter.getFilter().filter(query);
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
