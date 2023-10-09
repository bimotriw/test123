package com.oustme.oustsdk.adapter.common;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.oustme.oustsdk.fragments.common.EventLeaderBoardFragment;
import com.oustme.oustsdk.response.common.LeaderBoardDataRow;

import java.util.List;

/**
 * Created by shilpysamaddar on 21/03/17.
 */

public class NewEventLeaderboardPagerAdapter extends FragmentStatePagerAdapter {

    private final String TAG = NewTabFragmentPagerAdapter.class.getName();
    private List<LeaderBoardDataRow> leaderBaordDataRowList;

    public NewEventLeaderboardPagerAdapter(FragmentManager fm, List<LeaderBoardDataRow> leaderBaordDataRowList) {
        super(fm);
        this.leaderBaordDataRowList=leaderBaordDataRowList;
    }

    public void notifyChanges(List<LeaderBoardDataRow> leaderBaordDataRowList) {
        this.leaderBaordDataRowList=leaderBaordDataRowList;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        EventLeaderBoardFragment fragment=new EventLeaderBoardFragment();
        fragment.setLeaderBaordDataRowList(leaderBaordDataRowList);
        fragment.setTabPosition(position);
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
