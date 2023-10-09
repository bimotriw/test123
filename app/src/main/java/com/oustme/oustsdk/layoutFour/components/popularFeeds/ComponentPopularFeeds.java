package com.oustme.oustsdk.layoutFour.components.popularFeeds;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.room.dto.DTOSpecialFeed;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.utils.OustResourceUtils;

import java.util.List;

public class ComponentPopularFeeds extends LinearLayout {
    private final ViewPager rvPopularFeeds;
    public ComponentPopularFeeds(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        Log.d("TAG", "ComponentPopularFeeds: ");
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.component_popular_feeds, this, true);

        OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.selected_dot));

        rvPopularFeeds = findViewById(R.id.vp_popular_feeds);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(rvPopularFeeds, true);

    }

    public void setFeeds(FragmentManager childFragmentManager, List<DTOSpecialFeed> feeds){
        try{
            rvPopularFeeds.removeAllViews();
            //if(adapter==null) {
                //Log.d("TAG", "setFeeds: adapter null");
              //  adapter = new PopularFeedAdapter(feeds, getContext()/*childFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT*/);
            //}
//            adapter.setPopularFeeds(feeds);
            //rvPopularFeeds.setAdapter(null);
           // rvPopularFeeds.setAdapter(adapter);
        }catch(Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
