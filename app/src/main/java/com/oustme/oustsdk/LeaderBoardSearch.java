package com.oustme.oustsdk;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.appcompat.widget.SearchView;
import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.oustme.oustsdk.activity.common.leaderboard.adapter.NewLeaderBoardAdapter;
import com.oustme.oustsdk.base.BaseActivity;
import com.oustme.oustsdk.customviews.CustomTextView;
import com.oustme.oustsdk.response.common.LeaderBoardDataRow;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;

import java.util.ArrayList;
import java.util.List;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.TOOL_BAR_COLOR_CODE;

public class LeaderBoardSearch extends BaseActivity {
    private static final String TAG = "LeaderBoardSearch";
    private RelativeLayout mLinearLayoutSortFilterRoot;
    private List<LeaderBoardDataRow> mRankersList, mLeaderBoardDataRowList;
    private RecyclerView mRecyclerViewLeaderList;
    private NewLeaderBoardAdapter mNewLeaderBoardAdapter;
    private CustomTextView mTextViewResult;
    private String mQuery;
    private LinearLayout mLinearLayoutHeaderRoot;
    private View mNoResult;
    private ActionBar mActionBar;
    private String toolbarColorCode;
    private SearchView searchView;
    private MenuItem searchViewItem;
    private boolean isSearched;

    @Override
    protected int getContentView() {
        return R.layout.leader_board_content;
    }

    @Override
    protected void initView() {
        mLinearLayoutSortFilterRoot = findViewById(R.id.linearLayoutSortFilterRoot);
        mLinearLayoutSortFilterRoot.setVisibility(View.GONE);
        mRecyclerViewLeaderList = findViewById(R.id.collapsing_toolbar_recycler_view);
        mRecyclerViewLeaderList.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewLeaderList.setItemAnimator(new DefaultItemAnimator());
        mTextViewResult = findViewById(R.id.textViewNoResults);
        mLinearLayoutHeaderRoot = findViewById(R.id.linearLayoutHeaderRoot);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(8, 12, 8, 2);
        mLinearLayoutHeaderRoot.setLayoutParams(params);
        mNoResult = findViewById(R.id.noResultView);
    }

    @Override
    protected void initData() {
        mLeaderBoardDataRowList = getIntent().getParcelableArrayListExtra(AppConstants.StringConstants.LEADER_BOARD_DATA);
        Log.d(TAG, "initData: "+mLeaderBoardDataRowList.size());
        mQuery = getIntent().getStringExtra(AppConstants.StringConstants.QUERY).trim();
        mActionBar = getSupportActionBar();
        if(mActionBar!=null)
        {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setDisplayShowHomeEnabled(true);
            mActionBar.setTitle(mQuery);
        }
        toolbarColorCode = OustPreferences.get(TOOL_BAR_COLOR_CODE);
        try {
            mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(toolbarColorCode)));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        if(mLeaderBoardDataRowList!=null && mQuery!=null)
        {
            updateFilter(mQuery);
        }
    }

    @Override
    protected void initListener() {

    }

    public void updateRecyclerView(List<LeaderBoardDataRow> mLeaderBoardDataRowList)
    {
        if(mLeaderBoardDataRowList!=null && mLeaderBoardDataRowList.size()>0) {
            mLinearLayoutHeaderRoot.setVisibility(View.VISIBLE);
            mNewLeaderBoardAdapter = new NewLeaderBoardAdapter(this, mLeaderBoardDataRowList);
            mRecyclerViewLeaderList.setAdapter(mNewLeaderBoardAdapter);
            mNewLeaderBoardAdapter.notifyDataSetChanged();
            mRecyclerViewLeaderList.addItemDecoration(new OustSdkTools.SimpleDividerItemDecoration(this));
        }
        else {
            showHideillustration();
        }
    }

    private void updateFilter(String query) {
        if(mLeaderBoardDataRowList!=null && mLeaderBoardDataRowList.size()>0){
            List<LeaderBoardDataRow> list_search_LeaderBoard = new ArrayList<LeaderBoardDataRow>();

            for(LeaderBoardDataRow leaderData : mLeaderBoardDataRowList)
                if(leaderData.getDisplayName().toLowerCase().contains(query.toLowerCase())){
                    list_search_LeaderBoard.add(leaderData);
                }

            if(list_search_LeaderBoard!=null && list_search_LeaderBoard.size()>0){
                mRecyclerViewLeaderList.setVisibility(View.VISIBLE);
                mLinearLayoutHeaderRoot.setVisibility(View.VISIBLE);
                mNoResult.setVisibility(View.GONE);
                updateRecyclerView(list_search_LeaderBoard);
            }else{
                mRecyclerViewLeaderList.setVisibility(View.GONE);
                mLinearLayoutHeaderRoot.setVisibility(View.GONE);
                mTextViewResult.setText(getString(R.string.no_results)+" \""+query+"\"");
                mNoResult.setVisibility(View.VISIBLE);
                //Toast.makeText(LeaderBoardSearch.this,"No data found",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showHideillustration() {
    }

    @Override
    public void onBackPressed() {
        finish();
        if (!searchView.isIconified())
        {
            finish();
            searchView.setIconified(true);
            searchView.onActionViewCollapsed();
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG,"Create options menu");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.alert_menu, menu);
        try{
            searchViewItem = menu.findItem(R.id.action_search);
            //searchViewItem.expandActionView();
            //searchViewItem.setTitle(mQuery);
            searchViewItem.setVisible(true);
            //searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);
            searchView = (SearchView) searchViewItem.getActionView();
            searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!searchView.isFocused()) {
                        searchViewItem.collapseActionView();
                        searchView.onActionViewCollapsed();
                        //viewManager.dismissSearchDropDownPopupWindow();
                    }
                }
            });

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchView.clearFocus();
                    //searchView.removeAllViews();
                    updateFilter(query.trim());
                    if (mActionBar != null) {
                        mActionBar.setTitle(mQuery);
                        searchView.clearFocus();
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });

            searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        Log.d(TAG, "onFocusChange: focus");
                    } else {
                        Log.d(TAG, "onFocusChange: no focus");
                    }
                }
            });

            searchViewItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    updateFilter(mQuery);
                    return true;
                }
            });

        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return true;
    }
}
