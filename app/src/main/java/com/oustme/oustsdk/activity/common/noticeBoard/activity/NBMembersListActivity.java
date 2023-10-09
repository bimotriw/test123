
package com.oustme.oustsdk.activity.common.noticeBoard.activity;

import android.graphics.Color;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.noticeBoard.adapters.NBGroupMembersAdapter;
import com.oustme.oustsdk.activity.common.noticeBoard.adapters.NBMembersAdapter;
import com.oustme.oustsdk.activity.common.noticeBoard.callBacks.AdapterPostionNotifier;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBGroupData;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBMemberData;
import com.oustme.oustsdk.activity.common.noticeBoard.presenters.NBMembersListPresenter;
import com.oustme.oustsdk.activity.common.noticeBoard.view.NBMembersListView;
import com.oustme.oustsdk.base.BaseActivity;
import com.oustme.oustsdk.interfaces.common.FeedClickListener;
import com.oustme.oustsdk.request.ViewTracker;
import com.oustme.oustsdk.room.dto.DTONewFeed;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStrings;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NBMembersListActivity extends BaseActivity implements NBMembersListView, AdapterPostionNotifier,FeedClickListener {

    private RecyclerView nb_members_rv, nb_group_rv;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout ll_progress, ll_paging_loader;
    private LinearLayout no_data_layout;
    private Toolbar toolbar;
    private NBMembersListPresenter mPresenter;
    private NBMembersAdapter nbMembersAdapter;
    private NBGroupMembersAdapter nbGroupMembersAdapter;
    private TextView users_ul, users_tab_text, group_ul, group_tab_text;

    @Override
    protected int getContentView() {
        return R.layout.activity_nbmembers_list;
    }

    @Override
    protected void initView() {
        nb_members_rv = findViewById(R.id.nb_members_rv);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        ll_progress = findViewById(R.id.ll_progress);
        ll_paging_loader = findViewById(R.id.ll_paging_loader);
        no_data_layout = findViewById(R.id.no_data_layout);
        toolbar = findViewById(R.id.tabanim_toolbar);
        users_ul = findViewById(R.id.users_ul);
        users_tab_text = findViewById(R.id.users_tab_text);
        group_ul = findViewById(R.id.group_ul);
        group_tab_text = findViewById(R.id.group_tab_text);
        nb_group_rv = findViewById(R.id.nb_group_rv);
        setToolbar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void initData() {
        try {
            mPresenter = new NBMembersListPresenter(this);
            long nbId = getIntent().getLongExtra("nbId", 0);
            long groupId = getIntent().getLongExtra("groupId", 0);
            mPresenter.getNextData(nbId, groupId);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void initListener() {

    }

    @Override
    public void createLoader() {
        try {
            ll_progress.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setColorSchemeColors(OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen), OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen));
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void hideLoader() {
        try {
            ll_progress.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void showPaginationLoader() {
        ll_paging_loader.setVisibility(View.VISIBLE);
    }

    @Override
    public void hidePaginationLoader() {
        ll_paging_loader.setVisibility(View.GONE);
    }

    @Override
    public void noGroupDataFound() {
        group_tab_text.setVisibility(View.GONE);
        group_ul.setVisibility(View.GONE);
    }

    private void setToolbar() {
        try {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setTitle("");
            TextView titleTextView = toolbar.findViewById(R.id.title);
            titleTextView.setText(""+ OustStrings.getString("members"));
            String toolbarColorCode = OustPreferences.get("toolbarColorCode");
            if ((toolbarColorCode != null) && (!toolbarColorCode.isEmpty())) {
                toolbar.setBackgroundColor(Color.parseColor(toolbarColorCode));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void noDataFound() {
        no_data_layout.setVisibility(View.VISIBLE);
    }

    @Override
    public void setOrUpdateUsersAdapter(List<NBMemberData> nbMemberDataList) {
        try {
            if (nbMembersAdapter == null) {
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                nb_members_rv.setLayoutManager(mLayoutManager);
                nbMembersAdapter = new NBMembersAdapter(NBMembersListActivity.this, nbMemberDataList, NBMembersListActivity.this);
                nb_members_rv.setAdapter(nbMembersAdapter);

                ViewTracker viewTracker = new ViewTracker();
                viewTracker.setRecyclerView(nb_members_rv);
                viewTracker.setFeedClickListener(NBMembersListActivity.this);
                viewTracker.startTracking();

            } else {
                nbMembersAdapter.notifyListChnage(nbMemberDataList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
    List<NBGroupData> tempGroupDataList = new ArrayList<>();
    @Override
    public void setOrUpdateGroupAdapter(List<NBGroupData> nbGroupDataList, long nbId) {
        try {
            showGroupList(nbGroupDataList,nbId);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showGroupList(List<NBGroupData> nbGroupDataList, long nbId) {
        nbGroupMembersAdapter = new NBGroupMembersAdapter(this, nbGroupDataList);
        nbGroupMembersAdapter.setNbId(nbId);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        nb_group_rv.setLayoutManager(mLayoutManager);
        Objects.requireNonNull(this).runOnUiThread(() ->  nb_group_rv.setAdapter(nbGroupMembersAdapter));
        nbGroupMembersAdapter.notifyDataSetChanged();
    }

    @Override
    public void noUsersDataFound() {
        users_tab_text.setVisibility(View.GONE);
        users_ul.setVisibility(View.GONE);
    }

    @Override
    public void reachedAdpterEnd() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mPresenter != null) {
                    OustSdkTools.showToast(getResources().getString(R.string.loading));
                    mPresenter.getData();
                }
            }
        },1500);

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    @Override
    public void onFeedClick(long newFeedId, int cplId) {

    }

    @Override
    public void onFeedViewed(long position) {

    }

    @Override
    public void onFeedViewedOnScroll(int position) {
        if(mPresenter!=null){
            mPresenter.checkForMoreData(position);
        }
    }

    @Override
    public void cplFeedClicked(long cplId) {

    }

    @Override
    public void checkFeedData(long cplId, long feedId) {

    }

    @Override
    public void onRemoveVideo(long newFeedId) {

    }

    @Override
    public void onPlayVideo(int position, int lastPos) {

    }

    @Override
    public void refreshViews() {

    }

    @Override
    public void onFeedRewardCoinsUpdate(DTONewFeed newFeed) {

    }
}
