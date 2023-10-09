package com.oustme.oustsdk.layoutFour.newnoticeBoard;

import android.content.Context;
import android.util.AttributeSet;
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
import com.oustme.oustsdk.layoutFour.newnoticeBoard.adapters.NewNBAllPostAdapter;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.adapters.NewNBTopicAdapter;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.callBacks.NewNBPostClickCallBack;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.request.NewPostViewData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBPostData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBTopicData;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.ArrayList;
import java.util.List;

public class NewComponentNoticeBoard extends LinearLayout implements NewNBPostClickCallBack {
    private RecyclerView rvNoticeBoard, rv_noticeboard_posts;
    private NewNBTopicAdapter nbTopicAdapter;
    TextView text_title_2, text_title;
    private NewNBAllPostAdapter nbAllPostAdapter;

    View no_data_layout;
    ImageView no_image;
    TextView no_data_content;

    public NewComponentNoticeBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.component_noticeboard_2, this, true);

        initViews();
    }

    private void initViews() {
        rvNoticeBoard = findViewById(R.id.rv_noticeboard);
        rv_noticeboard_posts = findViewById(R.id.rv_noticeboard_posts);
        text_title_2 = findViewById(R.id.text_title_2);
        text_title = findViewById(R.id.text_title);
        no_data_layout = findViewById(R.id.no_data_layout);
        no_image = no_data_layout.findViewById(R.id.no_image);
        no_data_content = no_data_layout.findViewById(R.id.no_data_content);

        no_image.setImageDrawable(getResources().getDrawable(R.drawable.no_noticeboard));
        no_data_content.setText(getResources().getString(R.string.no_noticeboard_content));
    }

    public void setData(ArrayList<NewNBTopicData> nbTopicDataArrayList) {
        no_data_layout.setVisibility(View.GONE);
        rvNoticeBoard.setVisibility(View.VISIBLE);
        rv_noticeboard_posts.setVisibility(View.VISIBLE);
        text_title_2.setVisibility(View.VISIBLE);
        text_title.setVisibility(View.VISIBLE);
        nbTopicAdapter = new NewNBTopicAdapter(getContext(), nbTopicDataArrayList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvNoticeBoard.setLayoutManager(mLayoutManager);
        rvNoticeBoard.setItemAnimator(new DefaultItemAnimator());
        rvNoticeBoard.setAdapter(nbTopicAdapter);
    }

    public void setNoDataImage() {
        no_data_layout.setVisibility(View.VISIBLE);
        rvNoticeBoard.setVisibility(View.GONE);
        rv_noticeboard_posts.setVisibility(View.GONE);
        text_title_2.setVisibility(View.GONE);
        text_title.setVisibility(View.GONE);
    }

    public void onNoticeBoardSearch(String query) {
        try {
//            if (nbTopicAdapter != null) {
//                nbTopicAdapter.getFilter().filter(query);
//            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setDataPosts(List<NewNBPostData> postDataList) {
      /*  nbAllPostAdapter = new NewNBAllPostAdapter(getContext(), postDataList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rv_noticeboard_posts.setLayoutManager(mLayoutManager);
        rv_noticeboard_posts.setItemAnimator(new DefaultItemAnimator());
        rv_noticeboard_posts.setAdapter(nbAllPostAdapter);*/
    }

    @Override
    public void onPostViewed(NewPostViewData postViewData) {

    }

    @Override
    public void onPostViewed(int position) {

    }

    @Override
    public void onPostLike(NewPostViewData postViewData) {

    }

    @Override
    public void onPostComment(NewPostViewData postViewData) {

    }

    @Override
    public void onPostCommentDelete(NewPostViewData postViewData) {

    }

    @Override
    public void onPostShare(NewPostViewData postViewData, View view) {

    }

    @Override
    public void onRequestPermissions(NewPostViewData postViewData, View view) {

    }
}
