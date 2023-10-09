package com.oustme.oustsdk.activity.common.leaderboard.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcelable;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.cardview.widget.CardView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.oustme.oustsdk.LeaderBoardSearch;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.UserProfileActivity;
import com.oustme.oustsdk.activity.common.leaderboard.adapter.CheckableSpinnerAdapter;
import com.oustme.oustsdk.activity.common.leaderboard.adapter.NewLeaderBoardAdapter;
import com.oustme.oustsdk.activity.common.leaderboard.adapter.SortListAdapter;
import com.oustme.oustsdk.activity.common.leaderboard.model.GroupDataResponse;
import com.oustme.oustsdk.activity.common.leaderboard.model.SortCheckData;
import com.oustme.oustsdk.activity.common.leaderboard.presenter.NewLeaderBoardPresenter;
import com.oustme.oustsdk.activity.common.leaderboard.view.AdapterCallback;
import com.oustme.oustsdk.activity.common.leaderboard.view.NewLeaderBoardView;
import com.oustme.oustsdk.adapter.common.LeaderBoardFilterSortAdapter;
import com.oustme.oustsdk.base.BaseActivity;
import com.oustme.oustsdk.customviews.CircleImageView;
import com.oustme.oustsdk.customviews.CustomTextView;
import com.oustme.oustsdk.response.common.LeaderBoardDataRow;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_COURSE_LB;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.TOOL_BAR_COLOR_CODE;

public class NewLeaderBoardActivity extends BaseActivity implements NewLeaderBoardView, AdapterCallback {
    private ActionBar mActionBar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private List<LeaderBoardDataRow> mLeaderBoardDataRowList;
    private RecyclerView mRecyclerViewLeaderList;
    private NewLeaderBoardAdapter mNewLeaderBoardAdapter;
    private NewLeaderBoardPresenter mNewLeaderBoardPresenter;
    private ProgressBar mLeaderBoardProgressBar;
    private CustomTextView mTextViewFirstRank, mTextViewSecondRank, mTextViewThirdRank;
    private CustomTextView mTextViewFirstName, mTextViewFirstCoins, mTextViewSecondName, mTextViewSecondCoins, mTextViewThirdName, mTextViewThirdCoins, mTextViewFirstlbDetails, mTextViewSecondlbDetails, mTextViewThirdlbDetails, mTextViewlbDetails;
    private CircleImageView mImageViewSecondRank, mImageViewFirstRank, mImageViewThirdRank, mImageViewSelfProfile;
    private ImageView mImageViewBanner;
    private LinearLayout mLinearLayoutRanksCard, mLinearLayoutSelfData;
    private String toolbarColorCode;
    private CustomTextView mTextViewSelfCoinsCount, mTextViewSelfName;
    private List<LeaderBoardDataRow> mRankersList;
    private LinearLayout mLinearLayoutSort, mLinearLayoutFilter;
    private ListView mListViewFilter;
    private Spinner mListViewSort, mGroupSpinner;
    private String[] mFilterListItems, mSortListItems;
    private ImageView mImageViewSort, mImageViewFilter;
    private CustomTextView mTextViewSort;
    private List<LeaderBoardDataRow> mOriginalList;
    private CardView mCardViewSecondRank, mCardViewThirdRank, mCardViewFirstRank;
    private LeaderBoardDataRow leaderBoardDataRow1, leaderBoardDataRow2, leaderBoardDataRow3;
    private View onlyOneRank, onlyTwoRank;
    private DecimalFormat formatter;
    private ArrayList<SortCheckData> list;
    private SortListAdapter sortListAdapter;
    private ArrayList<LeaderBoardDataRow> mOriginalList2;
    private LeaderBoardDataRow mCurrentUserData;
    private String mStudentId;
    private LinearLayout mLinearLayoutSelfCoins;
    private CustomTextView mTextViewSelfRankValue;
    private Toolbar toolbar;
    private LinearLayout mLinearLayoutGroupList, mLinearLayoutSort2;

    @Override
    protected int getContentView() {
        return R.layout.activity_new_leader_board;
    }

    @Override
    protected void initView() {
        try{
            OustSdkTools.setLocale(NewLeaderBoardActivity.this);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        mCollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout);
        mCollapsingToolbarLayout.setTitleEnabled(false);
        toolbar = findViewById(R.id.collapsing_toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(5.0f);
        }
        toolbar.setTitle(getString(R.string.leader_board_title));
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        //mActionBar = getSupportActionBar();
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(5.0f);
        }
        try {
            Field mCollapseIcon = toolbar.getClass().getDeclaredField("mCollapseIcon");
            mCollapseIcon.setAccessible(true);
            Drawable drw = (Drawable) mCollapseIcon.get(toolbar);
            drw.setColorFilter(getResources().getColor(R.color.white_pressed), PorterDuff.Mode.SRC_IN);
            //drw.setTint(getResources().getColor(R.color.white_pressed));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        /*if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }*/

        mRecyclerViewLeaderList = findViewById(R.id.collapsing_toolbar_recycler_view);
        mRecyclerViewLeaderList.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewLeaderList.setItemAnimator(new DefaultItemAnimator());
        mLeaderBoardProgressBar = findViewById(R.id.leaderBoardProgressBar);

        mImageViewBanner = findViewById(R.id.imageViewBanner);
        mImageViewBanner.setVisibility(View.GONE);
        mLinearLayoutRanksCard = findViewById(R.id.linearLayoutRanksCard);
        mLinearLayoutRanksCard.setVisibility(View.GONE);

        mTextViewFirstName = findViewById(R.id.textViewFirstName);
        mTextViewFirstCoins = findViewById(R.id.textViewFirstCoins);
        mImageViewFirstRank = findViewById(R.id.imageViewFirstRank);
        mTextViewFirstRank = findViewById(R.id.firstRank);

        mTextViewSecondName = findViewById(R.id.textViewSecondName);
        mTextViewSecondCoins = findViewById(R.id.textViewSecondCoins);
        mImageViewSecondRank = findViewById(R.id.imageViewSecondRank);
        mTextViewSecondRank = findViewById(R.id.secondRank);


        mTextViewThirdName = findViewById(R.id.textViewThirdName);
        mTextViewThirdCoins = findViewById(R.id.textViewThirdCoins);
        mImageViewThirdRank = findViewById(R.id.imageViewThirdRank);
        mTextViewThirdRank = findViewById(R.id.thirdRank);

        mTextViewlbDetails = findViewById(R.id.textViewlbDetails);
        mTextViewFirstlbDetails = findViewById(R.id.textViewFirstlbDetails);
        mTextViewSecondlbDetails = findViewById(R.id.textViewSecondlbDetails);
        mTextViewThirdlbDetails = findViewById(R.id.textViewThirdlbDetails);


        mLinearLayoutSelfData = findViewById(R.id.linearLayoutSelfData);
        mTextViewSelfName = findViewById(R.id.textViewSelfName);
        mTextViewSelfCoinsCount = findViewById(R.id.textViewSelfCoinsCount);
        //mTextViewSelfRank = findViewById(R.id.textViewSelfRank);
        mImageViewSelfProfile = findViewById(R.id.imageViewSelfProfile);
        mLinearLayoutSelfCoins = findViewById(R.id.linearLayoutSelfCoins);
        mTextViewSelfRankValue = findViewById(R.id.textViewSelfRankValue);

        mLinearLayoutSort = findViewById(R.id.linearLayoutSort2);
        mLinearLayoutFilter = findViewById(R.id.linearLayoutFilter);

        mImageViewSort = findViewById(R.id.imageViewSort);
        mImageViewFilter = findViewById(R.id.imageViewFilter);

        mListViewFilter = findViewById(R.id.filterListView);
        mListViewSort = findViewById(R.id.sortListView);
        mTextViewSort = findViewById(R.id.textViewSort);

        mGroupSpinner = findViewById(R.id.groupsort);
        mLinearLayoutGroupList = findViewById(R.id.linearLayoutGroupList);

        mCardViewFirstRank = findViewById(R.id.cardViewFirstRank);
        mCardViewSecondRank = findViewById(R.id.cardViewSecondRank);
        mCardViewThirdRank = findViewById(R.id.cardViewThirdRank);

        onlyOneRank = findViewById(R.id.layoutOnlyOneRank);
        onlyTwoRank = findViewById(R.id.layoutOnlyTwoRank);

        mLinearLayoutSort2 = findViewById(R.id.linearLayoutSort2);

    }

    private String lppathId;
    private String lpName, lpBgImage;
    private boolean isCourseLB;

    @Override
    protected void initData() {
        Intent CallingIntent = getIntent();
        formatter = new DecimalFormat("##,##,###");
        toolbarColorCode = OustPreferences.get(TOOL_BAR_COLOR_CODE);
        lpName = CallingIntent.getStringExtra("lpname");
        lppathId = CallingIntent.getStringExtra("lpleaderboardId");
        lpBgImage = CallingIntent.getStringExtra("coursebgImg");
        isCourseLB = CallingIntent.getBooleanExtra(IS_COURSE_LB, false);
        try {
            if (toolbarColorCode != null && !toolbarColorCode.trim().isEmpty()) {
                toolbar.setBackgroundColor(Color.parseColor(toolbarColorCode));
                mLinearLayoutSelfData.setBackgroundColor(Color.parseColor(toolbarColorCode));
                //mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(toolbarColorCode)));
                mLeaderBoardProgressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor(toolbarColorCode), PorterDuff.Mode.SRC_IN);
            } else {
                toolbar.setBackgroundColor(getResources().getColor(R.color.lgreen));
                mLinearLayoutSelfData.setBackgroundColor(getResources().getColor(R.color.lgreen));
                mLeaderBoardProgressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.lgreen), PorterDuff.Mode.SRC_IN);
                //mActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.lgreen)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        mNewLeaderBoardPresenter = new NewLeaderBoardPresenter(this);
        if (OustSdkTools.checkInternetStatus()) {
            if (!isCourseLB) {
                mNewLeaderBoardPresenter.getRankersData();
            } else {
                mNewLeaderBoardPresenter.getCourseLB(lppathId);
            }
        } else {
            OustSdkTools.showToast(getString(R.string.no_internet_connection));
        }
        mNewLeaderBoardPresenter.getUserData();

        mFilterListItems = new String[2];
        mFilterListItems[0] = ""+getResources().getString(R.string.name_text);
        mFilterListItems[1] = ""+getResources().getString(R.string.score_text);

        mSortListItems = new String[3];
        mSortListItems[0] = ""+getResources().getString(R.string.name_text);
        mSortListItems[1] = ""+getResources().getString(R.string.score_text);
        mSortListItems[2] = ""+getResources().getString(R.string.rank);


        list = new ArrayList<SortCheckData>();

        List<CheckableSpinnerAdapter.SpinnerItem<SortCheckData>> spinner_items = new ArrayList<>();

        SortCheckData sortCheckData = new SortCheckData();
        sortCheckData.setSelected(false);
        sortCheckData.setTitle(""+getResources().getString(R.string.name_text));
        list.add(sortCheckData);

        SortCheckData sortCheckData2 = new SortCheckData();
        sortCheckData2.setSelected(false);
        sortCheckData2.setTitle(getResources().getString(R.string.score_text));
        list.add(sortCheckData2);

        SortCheckData sortCheckData3 = new SortCheckData();
        sortCheckData3.setSelected(false);
        sortCheckData3.setTitle(getResources().getString(R.string.rank));
        list.add(sortCheckData3);

        // List<MyObject> all_objects = getMyObjects(); // from wherever
        for (SortCheckData o : list) {
            spinner_items.add(new CheckableSpinnerAdapter.SpinnerItem<>(o, o.getTitle()));
        }
        Set<SortCheckData> selected_items = new HashSet<>();
        //CheckableSpinnerAdapter adapter = new CheckableSpinnerAdapter<>(this, "sort", spinner_items, selected_items);
        //mListViewSort.setAdapter(adapter);

        List<String> list = new ArrayList<String>();
        if (!isCourseLB) {
            list.add(""+getResources().getString(R.string.rank));
            list.add(""+getResources().getString(R.string.name_text));
            list.add(""+getResources().getString(R.string.score_text));
        } else {
            list.add(""+getResources().getString(R.string.rank));
            list.add(""+getResources().getString(R.string.score_text));
            list.add(""+getResources().getString(R.string.time_text));
            list.add(""+getResources().getString(R.string.name_text));
        }
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mLinearLayoutGroupList.getContext(), R.layout.sortspinneritem, list);
        //dataAdapter.setDropDownViewResource(R.layout.sortspinneritem);
        mListViewSort.setAdapter(dataAdapter);
        mListViewSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                String spinnerData = mListViewSort.getSelectedItem().toString();
                //enableDisableSortViews(true);
                if (spinnerData.equalsIgnoreCase(""+getResources().getString(R.string.name_text)))
                    sortByName(mRankersList);
                else if (spinnerData.equalsIgnoreCase(""+getResources().getString(R.string.score_text)))
                    sortByPoints(mRankersList);
                else if (spinnerData.equalsIgnoreCase(""+getResources().getString(R.string.rank)))
                    sortByRank(mRankersList);
                else if (spinnerData.equalsIgnoreCase(""+getResources().getString(R.string.time_text))) {
                    sortByTime(mRankersList);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        //updateSortItemsToAdapter(mSortListItems);
        updateFilterItemsToAdapter(mFilterListItems);
    }

    @Override
    protected void initListener() {
        mLinearLayoutFilter.setOnClickListener(v -> {
            //mListViewFilter.setVisibility(View.VISIBLE);
        });

        mLinearLayoutSort.setOnClickListener(v -> {
            //enableDisableSortViews(false);
            //sortByPoints(mRankersList);
        });

       /* mListViewSort.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               enableDisableSortViews(true);
                if(position==0)
                    sortByName(mRankersList);
                else if(position==1)
                    sortByPoints(mRankersList);
                else if(position==2) {
                    //sortByRank(mRankersList);
                    updateRecyclerView(mOriginalList);
                }
            }
        });*/

       /* mListViewSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)
                    sortByName(mRankersList);
                else if(position==1)
                    sortByPoints(mRankersList);
                else if(position==2)
                    updateRecyclerView(mOriginalList);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        onlyOneRank.setOnClickListener(v -> {
            if (leaderBoardDataRow1 != null)
                userDetailsIntent(leaderBoardDataRow1);
        });
        onlyTwoRank.setOnClickListener(v -> {
            if (leaderBoardDataRow2 != null)
                userDetailsIntent(leaderBoardDataRow2);
        });
        mCardViewFirstRank.setOnClickListener(v -> {
            if (leaderBoardDataRow1 != null)
                userDetailsIntent(leaderBoardDataRow1);
        });

        mCardViewSecondRank.setOnClickListener(v -> {
            if (leaderBoardDataRow2 != null)
                userDetailsIntent(leaderBoardDataRow2);
        });
        mCardViewThirdRank.setOnClickListener(v -> {
            if (leaderBoardDataRow3 != null)
                userDetailsIntent(leaderBoardDataRow3);
        });
    }

    private void enableDisableSortViews(boolean b) {
        if (b) {
            mListViewSort.setVisibility(View.GONE);
            mImageViewSort.setVisibility(View.VISIBLE);
            mTextViewSort.setVisibility(View.VISIBLE);
        } else {
            mListViewSort.setVisibility(View.VISIBLE);
            mImageViewSort.setVisibility(View.GONE);
            mTextViewSort.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    @Override
    public void getRankersData() {

    }

    @Override
    public void showError(String errorMessage) {
        OustSdkTools.showToast(errorMessage);
    }

    @Override
    public void updateRankersData(List<LeaderBoardDataRow> mLeaderBoardDataRowList) {
        this.mLeaderBoardDataRowList = mLeaderBoardDataRowList;
        mOriginalList2 = new ArrayList<LeaderBoardDataRow>(mLeaderBoardDataRowList);
        mCurrentUserData = mOriginalList2.get(mOriginalList2.size() - 1);
        int lastIndex = mLeaderBoardDataRowList.size() - 1;
        mOriginalList2.remove(lastIndex);
        this.mLeaderBoardDataRowList.remove(lastIndex);
        if (mCurrentUserData != null) {
            if (mCurrentUserData.getDisplayName() != null) {
                mTextViewSelfName.setText(mCurrentUserData.getDisplayName());
                mTextViewSelfName.setVisibility(View.VISIBLE);
            }
            if (mCurrentUserData.getRank() != null && !mCurrentUserData.getRank().equalsIgnoreCase("0")) {
                //mTextViewSelfRank.setText("Rank");
                //mTextViewSelfRank.setVisibility(View.VISIBLE);
                mTextViewSelfRankValue.setVisibility(View.VISIBLE);
                mTextViewSelfRankValue.setText(getResources().getString(R.string.rank)+" " + mCurrentUserData.getRank() + "");
            }
            if (mCurrentUserData.getScore() != 0) {
                //mTextViewSelfCoinsCount.setText(formatter.format(mCurrentUserData.getScore()));
                mTextViewSelfCoinsCount.setText(OustSdkTools.formatMilliinFormat(mCurrentUserData.getScore()));
                mTextViewSelfCoinsCount.setVisibility(View.VISIBLE);
                mLinearLayoutSelfCoins.setVisibility(View.VISIBLE);
            }

            if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_LB_USER_LOCATION) && mCurrentUserData.getLbDetails() != null && !mCurrentUserData.getLbDetails().isEmpty()) {
                mTextViewlbDetails.setText(mCurrentUserData.getLbDetails());
                mTextViewlbDetails.setVisibility(View.VISIBLE);
            }
        }

        if (mLeaderBoardDataRowList.size() < 3) {
            showHideillustration();
            if (mLeaderBoardDataRowList.size() == 1) {
                onlyOneRank.setVisibility(View.VISIBLE);

                CircleImageView avatar = findViewById(R.id.circleImageViewUserProfile);
                CustomTextView user_name = findViewById(R.id.textViewUserName);
                CustomTextView mTextViewUserRank = findViewById(R.id.textViewUserRank);
                CustomTextView mTextViewUserCoins = findViewById(R.id.textViewUserCoins);
                CustomTextView mTextViewUserlbDetails = findViewById(R.id.textViewDetails);

                if (mLeaderBoardDataRowList.get(0) != null) {
                    leaderBoardDataRow1 = mLeaderBoardDataRowList.get(0);
                    if (mLeaderBoardDataRowList.get(0).getAvatar() != null) {
                        OustSdkTools.LoadCircleImageFromPicasso(avatar, mLeaderBoardDataRowList.get(0).getAvatar());
                    }
                    if (mLeaderBoardDataRowList.get(0).getDisplayName() != null) {
                        user_name.setText(mLeaderBoardDataRowList.get(0).getDisplayName());
                    }
                    if (mLeaderBoardDataRowList.get(0).getRank() != null) {
                        mTextViewUserRank.setText(mLeaderBoardDataRowList.get(0).getRank());
                    }
                    if (mLeaderBoardDataRowList.get(0).getScore() != 0) {
                        String formattedScore;//= formatter.format(mLeaderBoardDataRowList.get(0).getScore());
                        formattedScore = OustSdkTools.formatMilliinFormat(mLeaderBoardDataRowList.get(0).getScore());
                        mTextViewUserCoins.setText(formattedScore);
                    }

                    if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_LB_USER_LOCATION) && leaderBoardDataRow1.getLbDetails() != null && !leaderBoardDataRow1.getLbDetails().isEmpty()) {
                        mTextViewUserlbDetails.setText(leaderBoardDataRow1.getLbDetails());
                        mTextViewUserlbDetails.setVisibility(View.VISIBLE);
                    }
                }

            } else if (mLeaderBoardDataRowList.size() == 2) {
                onlyOneRank.setVisibility(View.VISIBLE);
                onlyTwoRank.setVisibility(View.VISIBLE);

                CircleImageView avatar = findViewById(R.id.circleImageViewUserProfile);
                CustomTextView user_name = findViewById(R.id.textViewUserName);
                CustomTextView mTextViewUserRank = findViewById(R.id.textViewUserRank);
                CustomTextView mTextViewUserCoins = findViewById(R.id.textViewUserCoins);
                CustomTextView mTextViewUserlbDetails = findViewById(R.id.textViewDetails);

                if (mLeaderBoardDataRowList.get(0) != null) {
                    leaderBoardDataRow1 = mLeaderBoardDataRowList.get(0);

                    if (mLeaderBoardDataRowList.get(0).getAvatar() != null) {
                        OustSdkTools.LoadCircleImageFromPicasso(avatar, mLeaderBoardDataRowList.get(0).getAvatar());
                    }
                    if (mLeaderBoardDataRowList.get(0).getDisplayName() != null) {
                        user_name.setText(mLeaderBoardDataRowList.get(0).getDisplayName());
                    }
                    if (mLeaderBoardDataRowList.get(0).getRank() != null) {
                        mTextViewUserRank.setText(mLeaderBoardDataRowList.get(0).getRank());
                    }
                    if (mLeaderBoardDataRowList.get(0).getScore() != 0) {
                        String formattedScore;// = formatter.format(mLeaderBoardDataRowList.get(0).getScore());
                        formattedScore = OustSdkTools.formatMilliinFormat(mLeaderBoardDataRowList.get(0).getScore());
                        mTextViewUserCoins.setText(formattedScore);
                    }

                    if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_LB_USER_LOCATION) && leaderBoardDataRow1.getLbDetails() != null && !leaderBoardDataRow1.getLbDetails().isEmpty()) {
                        mTextViewUserlbDetails.setText(leaderBoardDataRow1.getLbDetails());
                        mTextViewUserlbDetails.setVisibility(View.VISIBLE);
                    }
                }

                CircleImageView avatar2 = findViewById(R.id.circleImageViewUserProfile2);
                CustomTextView user_name2 = findViewById(R.id.textViewUserName2);
                CustomTextView mTextViewUserRank2 = findViewById(R.id.textViewUserRank2);
                CustomTextView mTextViewUserCoins2 = findViewById(R.id.textViewUserCoins2);
                CustomTextView mTextViewUserlbDetails2 = findViewById(R.id.textViewDetails2);

                if (mLeaderBoardDataRowList.get(1) != null) {
                    leaderBoardDataRow2 = mLeaderBoardDataRowList.get(1);
                    if (mLeaderBoardDataRowList.get(1).getAvatar() != null) {
                        OustSdkTools.LoadCircleImageFromPicasso(avatar2, mLeaderBoardDataRowList.get(1).getAvatar());
                    }
                    if (mLeaderBoardDataRowList.get(1).getDisplayName() != null) {
                        user_name2.setText(mLeaderBoardDataRowList.get(1).getDisplayName());
                    }
                    if (mLeaderBoardDataRowList.get(1).getRank() != null) {
                        mTextViewUserRank2.setText(mLeaderBoardDataRowList.get(1).getRank());
                    }
                    if (mLeaderBoardDataRowList.get(1).getScore() != 0) {
                        String formattedScore;//= formatter.format(mLeaderBoardDataRowList.get(1).getScore());
                        formattedScore = OustSdkTools.formatMilliinFormat(mLeaderBoardDataRowList.get(1).getScore());
                        mTextViewUserCoins2.setText(formattedScore);
                    }

                    if (leaderBoardDataRow2.getLbDetails() != null && !leaderBoardDataRow2.getLbDetails().isEmpty()) {
                        mTextViewUserlbDetails2.setText(leaderBoardDataRow2.getLbDetails());
                        mTextViewUserlbDetails2.setVisibility(View.VISIBLE);
                    }
                }

            }
        } else {
            List<LeaderBoardDataRow> topThreeRankList = new ArrayList<>();
            if (mLeaderBoardDataRowList.size() >= 3) {
                for (int i = 0; i < 3; i++) {
                    topThreeRankList.add(mLeaderBoardDataRowList.get(0));
                    mLeaderBoardDataRowList.remove(0);
                }
                if (topThreeRankList != null) {
                    updateTopRankers(topThreeRankList);
                }
                mRankersList = mLeaderBoardDataRowList;
                mOriginalList = new ArrayList<LeaderBoardDataRow>(mLeaderBoardDataRowList);
                updateRecyclerView(mLeaderBoardDataRowList);

            }
        }
    }

    public void updateRecyclerView(List<LeaderBoardDataRow> mLeaderBoardDataRowList) {
        if (mLeaderBoardDataRowList != null && mLeaderBoardDataRowList.size() > 0) {
            mListViewSort.setVisibility(View.VISIBLE);
            mLinearLayoutSort.setVisibility(View.VISIBLE);
            mNewLeaderBoardAdapter = new NewLeaderBoardAdapter(this, mLeaderBoardDataRowList);
            mRecyclerViewLeaderList.setAdapter(mNewLeaderBoardAdapter);
            mNewLeaderBoardAdapter.notifyDataSetChanged();
            mRecyclerViewLeaderList.addItemDecoration(new OustSdkTools.SimpleDividerItemDecoration(this));
        } else {
            showHideillustration();
        }
    }

    @Override
    public void noLB() {
        mLinearLayoutSelfData.setVisibility(View.GONE);
        showHideillustration();
    }

    private void showHideillustration() {
        LinearLayout linearLayoutHeaderRoot = findViewById(R.id.linearLayoutHeaderRoot);
        RelativeLayout linearLayoutSortFilterRoot = findViewById(R.id.linearLayoutSortFilterRoot);
        linearLayoutHeaderRoot.setVisibility(View.GONE);
        linearLayoutSortFilterRoot.setVisibility(View.GONE);

        mRecyclerViewLeaderList.setVisibility(View.GONE);
        LinearLayout linearLayoutNoRankers = findViewById(R.id.linearLayoutNoRankers);
        linearLayoutNoRankers.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        mLeaderBoardProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showProgressBar() {
        mLeaderBoardProgressBar.setVisibility(View.VISIBLE);
    }

    private void updateTopRankers(List<LeaderBoardDataRow> mLeaderBoardDataRowList) {
        mLinearLayoutRanksCard.setVisibility(View.VISIBLE);
        leaderBoardDataRow1 = mLeaderBoardDataRowList.get(0);
        leaderBoardDataRow2 = mLeaderBoardDataRowList.get(1);
        leaderBoardDataRow3 = mLeaderBoardDataRowList.get(2);
        if (leaderBoardDataRow1 != null) {
            if (leaderBoardDataRow1.getDisplayName() != null) {
                mTextViewFirstName.setText(leaderBoardDataRow1.getDisplayName());
                mTextViewFirstName.setSelected(true);
            }

            String formattedScore;//= formatter.format(leaderBoardDataRow1.getScore());
            formattedScore = OustSdkTools.formatMilliinFormat(leaderBoardDataRow1.getScore());
            mTextViewFirstCoins.setText(formattedScore + " "+getResources().getString(R.string.score_text));
            if (leaderBoardDataRow1.getRank() != null) {
                mTextViewFirstRank.setText(leaderBoardDataRow1.getRank());
            }
            if (leaderBoardDataRow1.getAvatar() != null) {
                OustSdkTools.LoadCircleImageFromPicasso(mImageViewFirstRank, leaderBoardDataRow1.getAvatar());
            }
            else{
                OustSdkTools.LoadCircleImageFromPicasso(mImageViewFirstRank, AppConstants.StringConstants.DEFAULT_PROFILE_PIC);
            }

            if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_LB_USER_LOCATION) && leaderBoardDataRow1.getLbDetails() != null && !leaderBoardDataRow1.getLbDetails().isEmpty()) {
                mTextViewFirstlbDetails.setText(leaderBoardDataRow1.getLbDetails());
                mTextViewFirstlbDetails.setVisibility(View.VISIBLE);
            }
        }

        if (leaderBoardDataRow2 != null) {
            if (leaderBoardDataRow2.getDisplayName() != null) {
                mTextViewSecondName.setText(leaderBoardDataRow2.getDisplayName());
                mTextViewSecondName.setSelected(true);
            }

            //mTextViewSecondCoins.setText(""+leaderBoardDataRow2.getScore()+" Coins");

            String formattedScore;//= formatter.format(leaderBoardDataRow2.getScore());
            formattedScore = OustSdkTools.formatMilliinFormat(leaderBoardDataRow2.getScore());
            mTextViewSecondCoins.setText(formattedScore + " "+getResources().getString(R.string.score_text));
            if (leaderBoardDataRow2.getRank() != null) {
                mTextViewSecondRank.setText(leaderBoardDataRow2.getRank());
            }
            if (leaderBoardDataRow2.getAvatar() != null) {
                OustSdkTools.LoadCircleImageFromPicasso(mImageViewSecondRank, leaderBoardDataRow2.getAvatar());
            }
            else{
                OustSdkTools.LoadCircleImageFromPicasso(mImageViewSecondRank, AppConstants.StringConstants.DEFAULT_PROFILE_PIC);
            }

            if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_LB_USER_LOCATION) && leaderBoardDataRow2.getLbDetails() != null && !leaderBoardDataRow2.getLbDetails().isEmpty()) {
                mTextViewSecondlbDetails.setText(leaderBoardDataRow2.getLbDetails());
                mTextViewSecondlbDetails.setVisibility(View.VISIBLE);
            }
        }

        if (leaderBoardDataRow3 != null) {
            if (leaderBoardDataRow3.getDisplayName() != null) {
                mTextViewThirdName.setText(leaderBoardDataRow3.getDisplayName());
                mTextViewThirdName.setSelected(true);
            }

            //mTextViewThirdCoins.setText(""+leaderBoardDataRow3.getScore()+" Coins");

            String formattedScore = formatter.format(leaderBoardDataRow3.getScore());
            formattedScore = OustSdkTools.formatMilliinFormat(leaderBoardDataRow3.getScore());
            mTextViewThirdCoins.setText(formattedScore + " "+getResources().getString(R.string.score_text));

            if (leaderBoardDataRow3.getRank() != null) {
                mTextViewThirdRank.setText(leaderBoardDataRow3.getRank());
            }

            if (leaderBoardDataRow3.getAvatar() != null) {
                OustSdkTools.LoadCircleImageFromPicasso(mImageViewThirdRank, leaderBoardDataRow3.getAvatar());
            } else {
                OustSdkTools.LoadCircleImageFromPicasso(mImageViewThirdRank, AppConstants.StringConstants.DEFAULT_PROFILE_PIC);
            }

            if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_LB_USER_LOCATION) && leaderBoardDataRow3.getLbDetails() != null && !leaderBoardDataRow3.getLbDetails().isEmpty()) {
                mTextViewThirdlbDetails.setText(leaderBoardDataRow3.getLbDetails());
                mTextViewThirdlbDetails.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void updateUserData(ActiveUser activeUser) {
        if (activeUser != null) {
            mTextViewSelfName.setText(activeUser.getUserDisplayName());
            mStudentId = activeUser.getStudentid();
            if (OustPreferences.get("UserAvatar") != null) {
                OustSdkTools.LoadCircleImageFromPicasso(mImageViewSelfProfile, OustPreferences.get("UserAvatar"));
            }
            else{
                OustSdkTools.LoadCircleImageFromPicasso(mImageViewSelfProfile, AppConstants.StringConstants.DEFAULT_PROFILE_PIC);
            }
        }
    }

    @Override
    public void updateGroupData(final List<GroupDataResponse> mGroupDataResponses) {
        List<String> groupListName = new ArrayList<>();
        if(mGroupDataResponses!=null)
        Collections.sort(mGroupDataResponses, GroupDataResponse.groupNameComp);
        groupListName.add(""+getResources().getString(R.string.groups_tab_text));
        for (int i = 0; i < mGroupDataResponses.size(); i++) {
            groupListName.add(mGroupDataResponses.get(i).getGroupName());
        }
        if (mGroupDataResponses.size() > 0) {
            RelativeLayout linearLayoutSortFilterRoot = findViewById(R.id.linearLayoutSortFilterRoot);
            linearLayoutSortFilterRoot.setVisibility(View.VISIBLE);
        }
        //if(isCourseLB){
        mLinearLayoutGroupList.setVisibility(View.VISIBLE);
        final ArrayAdapter<String> groupAdapter = new ArrayAdapter<String>(mLinearLayoutGroupList.getContext(), R.layout.sortspinneritem, groupListName);
        //groupAdapter.setDropDownViewResource(R.layout.sortspinneritem);
        mGroupSpinner.setAdapter(groupAdapter);
        mGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (arg2 != 0) {
                    if (isCourseLB) {
                        mNewLeaderBoardPresenter.getCourseLBGroupWise(lppathId, mGroupDataResponses.get(arg2 - 1).getGroupId() + "", mGroupDataResponses.get(arg2 - 1).getGroupName());
                    } else {
                        mNewLeaderBoardPresenter.getOverallLBGroupWise(mGroupDataResponses.get(arg2 - 1).getGroupId() + "", mGroupDataResponses.get(arg2 - 1).getGroupName());
                        //OustSdkTools.showToast("Working on it");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        //}

    }

    private void sortByName(List<LeaderBoardDataRow> leaderBoardDataRowList) {
        if (leaderBoardDataRowList != null) {
            Collections.sort(leaderBoardDataRowList, new Comparator<LeaderBoardDataRow>() {
                public int compare(LeaderBoardDataRow v1, LeaderBoardDataRow v2) {
                    return v1.getDisplayName().toLowerCase().compareTo(v2.getDisplayName().toLowerCase());
                }
            });
            updateRecyclerView(leaderBoardDataRowList);
        }
    }

    private void sortByRank(List<LeaderBoardDataRow> leaderBoardDataRowList) {
        if (leaderBoardDataRowList != null) {
            Collections.sort(leaderBoardDataRowList, new Comparator<LeaderBoardDataRow>() {
                public int compare(LeaderBoardDataRow v1, LeaderBoardDataRow v2) {
                    return Integer.valueOf(v1.getRank()).compareTo(Integer.valueOf(v2.getRank()));
                }
            });
            updateRecyclerView(leaderBoardDataRowList);
        }
    }

    private void sortByPoints(List<LeaderBoardDataRow> leaderBoardDataRowList) {
        if (leaderBoardDataRowList != null) {
            Collections.sort(leaderBoardDataRowList);
            updateRecyclerView(leaderBoardDataRowList);
        }
    }

    private void sortByTime(List<LeaderBoardDataRow> leaderBoardDataRowList) {
        if (leaderBoardDataRowList != null) {
            Collections.sort(leaderBoardDataRowList, new Comparator<LeaderBoardDataRow>() {
                public int compare(LeaderBoardDataRow v1, LeaderBoardDataRow v2) {
                    return Integer.valueOf(v1.getCompletionTime()).compareTo(Integer.valueOf(v2.getCompletionTime()));
                }
            });
            updateRecyclerView(leaderBoardDataRowList);
        }
    }

    private void updateSortItemsToAdapter(String[] mSortListItems) {
        LeaderBoardFilterSortAdapter adapter = new LeaderBoardFilterSortAdapter(this, mSortListItems);
        //mListViewSort.setAdapter(adapter);
    }

    private void updateFilterItemsToAdapter(String[] mFilterListItems) {
        LeaderBoardFilterSortAdapter adapter = new LeaderBoardFilterSortAdapter(this, mFilterListItems);
        mListViewFilter.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (mLinearLayoutGroupList.getVisibility() == View.VISIBLE) {
                if (mGroupSpinner != null) {
                    mGroupSpinner.setSelection(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "Create options menu");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.alert_menu, menu);
        try {
            final MenuItem searchViewItem = menu.findItem(R.id.action_search);
            searchViewItem.setVisible(true);
            //final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);

            final SearchView searchView = (SearchView) searchViewItem.getActionView();
            ImageView searchClose = searchView.findViewById(R.id.search_close_btn);
            searchClose.setImageResource(R.drawable.ic_close_img);
            ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
            searchIcon.setImageResource(R.drawable.search);
            SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
            searchAutoComplete.setHintTextColor(Color.WHITE);
            searchAutoComplete.setTextColor(Color.WHITE);
            searchView.setQueryHint(""+getResources().getString(R.string.search_text));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchView.clearFocus();
                    //searchView.removeAllViews();
                    if (mOriginalList2 != null && mOriginalList2.size() > 0) {
                        searchViewItem.collapseActionView();
                        Intent intent = new Intent(NewLeaderBoardActivity.this, LeaderBoardSearch.class);
                        intent.putParcelableArrayListExtra(AppConstants.StringConstants.LEADER_BOARD_DATA, mOriginalList2);
                        intent.putExtra(AppConstants.StringConstants.QUERY, query);
                        startActivity(intent);
                    } else {
                        OustSdkTools.showToast(getString(R.string.no_data_found));
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //enableDisableSortViews(true);
    }

    private void updateFilter(String query) {
        if (mLeaderBoardDataRowList != null && mLeaderBoardDataRowList.size() > 0) {
            List<LeaderBoardDataRow> list_search_LeaderBoard = new ArrayList<LeaderBoardDataRow>();

            for (LeaderBoardDataRow leaderData : mLeaderBoardDataRowList)
                if (leaderData.getDisplayName().contains(query)) {
                    list_search_LeaderBoard.add(leaderData);
                }

            if (list_search_LeaderBoard != null && list_search_LeaderBoard.size() > 0) {
                updateRecyclerView(list_search_LeaderBoard);
            } else {
                OustSdkTools.showToast(getString(R.string.no_data_found));
            }
        }
    }

    private void userDetailsIntent(LeaderBoardDataRow leaderBoardDataRow) {
        try {
            /*if(isLearningPathLeaderBoard==1) {*/
            if (leaderBoardDataRow != null) {
                if (!leaderBoardDataRow.getUserid().equalsIgnoreCase(OustSdkTools.getActiveUserData().getStudentid())) {
                    Intent intent = new Intent(NewLeaderBoardActivity.this, UserProfileActivity.class);
                    intent.putExtra("avatar", ("" + leaderBoardDataRow.getAvatar()));
                    intent.putExtra("name", ("" + leaderBoardDataRow.getDisplayName()));
                    intent.putExtra("studentId", ("" + leaderBoardDataRow.getUserid()));
                    intent.putExtra("xp", ("" + leaderBoardDataRow.getScore()));
                    intent.putExtra("rank", ("" + leaderBoardDataRow.getRank()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    // }
                }/*else {
                if(!allArrayList.get(position).getStudentid().equalsIgnoreCase(OustSdkTools.getActiveUserData().getStudentid())) {
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra("avatar", ("" + allArrayList.get(position).getAvatar()));
                    intent.putExtra("name", ("" + allArrayList.get(position).getUserDisplayName()));
                    intent.putExtra("studentId", ("" + allArrayList.get(position).getStudentid()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }*/
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void updateGroupLBData(List<LeaderBoardDataRow> mLeaderBoardDataRowList, String groupName) {
        Intent intent = new Intent(NewLeaderBoardActivity.this, GroupLBDataActivity.class);
        intent.putExtra("GPNAME", groupName);
        intent.putParcelableArrayListExtra("DATA", (ArrayList<? extends Parcelable>) mLeaderBoardDataRowList);
        startActivity(intent);
    }

    @Override
    public void updatePosition(int pos) {
        for (int i = 0; i < list.size(); i++) {
            if (pos == i)
                list.get(i).setSelected(true);
            else
                list.get(i).setSelected(false);
        }
        //sortListAdapter.notifyDataSetChanged();
        if (!isCourseLB) {
            if (pos == 0)
                sortByName(mRankersList);
            else if (pos == 1)
                sortByPoints(mRankersList);
            else if (pos == 2)
                updateRecyclerView(mOriginalList);
        } else {
            if (pos == 0)
                sortByPoints(mRankersList);
            else if (pos == 1)
                sortByTime(mRankersList);
            else if (pos == 2) {
                updateRecyclerView(mOriginalList);
            }
        }

    }
}
