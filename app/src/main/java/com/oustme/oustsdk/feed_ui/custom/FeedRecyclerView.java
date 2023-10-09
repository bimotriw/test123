package com.oustme.oustsdk.feed_ui.custom;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.room.dto.DTONewFeed;

import java.util.ArrayList;

public class FeedRecyclerView extends RecyclerView {

    // vars
    private ArrayList<DTONewFeed> feedArrayList = new ArrayList<>();
    private Context context;


    public FeedRecyclerView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public FeedRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    private void init(Context context) {
        this.context = context.getApplicationContext();
        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);




        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {


                if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                    LinearLayoutManager layoutManager = ((LinearLayoutManager)recyclerView.getLayoutManager());
                    if (layoutManager != null) {
                        int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                        System.out.println("Feed name "+feedArrayList.get(firstVisiblePosition).getHeader());
                    }

                }

                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });



    }







    public void setFeedArrayList(ArrayList<DTONewFeed> feedArrayList) {
        this.feedArrayList = feedArrayList;
    }



}
