package com.oustme.oustsdk.activity.common.leaderboard.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.NewLandingActivity;
import com.oustme.oustsdk.activity.common.leaderboard.adapter.NewLeaderBoardAdapter;
import com.oustme.oustsdk.base.BaseActivity;
import com.oustme.oustsdk.response.common.LeaderBoardDataRow;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.TOOL_BAR_COLOR_CODE;

public class GroupLBDataActivity extends BaseActivity {
    private static final String TAG = "GroupLBDataActivity";
    private RelativeLayout mRelativeLayoutSort;
    private List<LeaderBoardDataRow> mLeaderBoardDataRowList;
    private RecyclerView mRecyclerViewLbData;
    private NewLeaderBoardAdapter mNewLeaderBoardAdapter;
    private Spinner mListViewSort, mGroupSpinner;
    private List<LeaderBoardDataRow> mRankersList;
    private ActionBar mActionBar;
    private String toolbarColorCode;
    private LinearLayout mLinearLayoutSort;
    private String mGroupName;


    @Override
    protected int getContentView() {
        return R.layout.leader_board_content;
    }

    @Override
    protected void initView() {
        try{
            OustSdkTools.setLocale(GroupLBDataActivity.this);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        mRelativeLayoutSort = findViewById(R.id.linearLayoutSortFilterRoot);
        mRecyclerViewLbData = findViewById(R.id.collapsing_toolbar_recycler_view);
        mListViewSort = findViewById(R.id.sortListView);
        mLinearLayoutSort = findViewById(R.id.linearLayoutSort2);
        mActionBar = getSupportActionBar();
        toolbarColorCode = OustPreferences.get(TOOL_BAR_COLOR_CODE);
    }

    @Override
    protected void initData() {
        mLeaderBoardDataRowList = getIntent().getParcelableArrayListExtra("DATA");
        mGroupName = getIntent().getStringExtra("GPNAME");
        mRankersList = mLeaderBoardDataRowList;
        sortDataSetUp();
        if(mActionBar!=null)
        {
            mActionBar.setDisplayShowHomeEnabled(true);
            mActionBar.setDisplayHomeAsUpEnabled(true);
            if(mGroupName!=null){
                mActionBar.setTitle(mGroupName);
            }
            else {
                mActionBar.setTitle(getString(R.string.group_leaderboard));
            }
            try {
                if (toolbarColorCode != null) {
                    mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(toolbarColorCode)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
        mLeaderBoardDataRowList.remove(mLeaderBoardDataRowList.size()-1);
        mNewLeaderBoardAdapter = new NewLeaderBoardAdapter(this, mLeaderBoardDataRowList);
        mRecyclerViewLbData.setAdapter(mNewLeaderBoardAdapter);
        mNewLeaderBoardAdapter.notifyDataSetChanged();
        mRecyclerViewLbData.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewLbData.addItemDecoration(new OustSdkTools.SimpleDividerItemDecoration(this));
    }

    private void sortDataSetUp() {
        List<String> list = new ArrayList<String>();
        list.add("Rank");
        list.add("Points");
        list.add("Time");
        list.add("Name");

        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.sort_spinner, list);
        dataAdapter.setDropDownViewResource(R.layout.sortspinneritem);
        mListViewSort.setAdapter(dataAdapter);
        mListViewSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                String spinnerData = mListViewSort.getSelectedItem().toString();
                //enableDisableSortViews(true);
                if(spinnerData.equalsIgnoreCase("Name"))
                    sortByName(mRankersList);
                else if(spinnerData.equalsIgnoreCase("Points"))
                    sortByPoints(mRankersList);
                else if(spinnerData.equalsIgnoreCase("Rank"))
                    sortByRank(mRankersList);
                else if(spinnerData.equalsIgnoreCase("Time Completed")){
                    sortByTime(mRankersList);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {}
        });

    }

    @Override
    protected void initListener() {

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }
    private void sortByName(List<LeaderBoardDataRow> leaderBoardDataRowList){
        if(leaderBoardDataRowList!=null) {
            Collections.sort(leaderBoardDataRowList, new Comparator<LeaderBoardDataRow>() {
                public int compare(LeaderBoardDataRow v1, LeaderBoardDataRow v2) {
                    return v1.getDisplayName().toLowerCase().compareTo(v2.getDisplayName().toLowerCase());
                }
            });
            updateRecyclerView(leaderBoardDataRowList);
        }
    }

    private void sortByRank(List<LeaderBoardDataRow> leaderBoardDataRowList){
        if(leaderBoardDataRowList!=null) {
            Collections.sort(leaderBoardDataRowList, new Comparator<LeaderBoardDataRow>() {
                public int compare(LeaderBoardDataRow v1, LeaderBoardDataRow v2) {
                    return Integer.valueOf(v1.getRank()).compareTo(Integer.valueOf(v2.getRank()));
                }
            });
            updateRecyclerView(leaderBoardDataRowList);
        }
    }

    private void sortByPoints(List<LeaderBoardDataRow> leaderBoardDataRowList){
        if(leaderBoardDataRowList!=null) {
            Collections.sort(leaderBoardDataRowList);
            updateRecyclerView(leaderBoardDataRowList);
        }
    }

    private void  sortByTime(List<LeaderBoardDataRow> leaderBoardDataRowList){
        if(leaderBoardDataRowList!=null) {
            Collections.sort(leaderBoardDataRowList, new Comparator<LeaderBoardDataRow>() {
                public int compare(LeaderBoardDataRow v1, LeaderBoardDataRow v2) {
                    return Integer.valueOf(v1.getCompletionTime()).compareTo(Integer.valueOf(v2.getCompletionTime()));
                }
            });
            updateRecyclerView(leaderBoardDataRowList);
        }
    }

    private void updateRecyclerView(List<LeaderBoardDataRow> leaderBoardDataRowList) {
        if(mLeaderBoardDataRowList!=null && mLeaderBoardDataRowList.size()>0) {
            mNewLeaderBoardAdapter = new NewLeaderBoardAdapter(this, mLeaderBoardDataRowList);
            mRecyclerViewLbData.setAdapter(mNewLeaderBoardAdapter);
            mNewLeaderBoardAdapter.notifyDataSetChanged();
            mRecyclerViewLbData.addItemDecoration(new OustSdkTools.SimpleDividerItemDecoration(this));
            if(mLeaderBoardDataRowList.size()<=1){
                mRelativeLayoutSort.setVisibility(View.GONE);
                mLinearLayoutSort.setVisibility(View.GONE);
            }
            else {
                mRelativeLayoutSort.setVisibility(View.VISIBLE);
                mListViewSort.setVisibility(View.VISIBLE);
                mLinearLayoutSort.setVisibility(View.VISIBLE);
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
