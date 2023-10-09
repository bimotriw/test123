package com.oustme.oustsdk.layoutFour.components.leaderBoard;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.clevertap.android.sdk.CleverTapAPI;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.layoutFour.data.response.GroupDataList;
import com.oustme.oustsdk.layoutFour.data.response.LeaderBoardDataList;
import com.oustme.oustsdk.layoutFour.interfaces.LeaderBoardCallBacks;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class CommonLeaderBoardActivity extends AppCompatActivity implements LeaderBoardCallBacks {

    LinearLayout container;
    LinearLayout commonLeaderBoardLay;
    TextView screen_name;
    int color;
    int bgColor;
    String containerType;
    String containerName;
    long containerContentId;
    boolean isFilterAvailable, isSortAvailable, isSearchAvailable;
    ComponentLeaderBoard componentLeaderBoard;
    LeaderBoardViewModel leaderBoardViewModel;
    ArrayList<LeaderBoardDataList> leaderBoardDataRowList;

    RelativeLayout branding_mani_layout;
    ImageView branding_bg;
    ImageView branding_icon;
    TextView branding_percentage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        setContentView(R.layout.activity_common_leader_board);
        if (OustSdkApplication.getContext() == null) {
            OustSdkApplication.setmContext(CommonLeaderBoardActivity.this);
        }
        OustSdkTools.setLocale(CommonLeaderBoardActivity.this);
        getColors();
        initView();
        initData();
    }

    private void getColors() {
        try {
            color = OustResourceUtils.getColors();
            bgColor = OustResourceUtils.getToolBarBgColor();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            color = OustSdkTools.getColorBack(R.color.lgreen);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY);
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.leaderboard_menu, menu);

        MenuItem itemFilter = menu.findItem(R.id.action_filter);
        Drawable filterDrawable = getResources().getDrawable(R.drawable.ic_filter);
        itemFilter.setIcon(OustResourceUtils.setDefaultDrawableColor(filterDrawable, color));
        itemFilter.setVisible(isFilterAvailable());

        MenuItem itemSort = menu.findItem(R.id.action_sort);
        Drawable sortDrawable = getResources().getDrawable(R.drawable.ic_sort);
        itemSort.setIcon(OustResourceUtils.setDefaultDrawableColor(sortDrawable, color));
        itemSort.setVisible(isSortAvailable());

        MenuItem itemSearch = menu.findItem(R.id.action_search);
        itemSearch.getIcon().setColorFilter(colorFilter);
        itemSearch.setVisible(isSearchAvailable());

        try {
            SearchManager manager = (SearchManager) getApplicationContext().getSystemService(Context.SEARCH_SERVICE);
            SearchView search = (SearchView) itemSearch.getActionView();
            search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

            EditText searchEditText = search.findViewById(androidx.appcompat.R.id.search_src_text);
            searchEditText.setTextColor(getResources().getColor(R.color.primary_text));
            searchEditText.setHintTextColor(getResources().getColor(R.color.unselected_text));
            ImageView searchIcon = search.findViewById(androidx.appcompat.R.id.search_button);
            searchIcon.setImageDrawable(OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.search_oust), color));


            search.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    screen_name.setVisibility(View.GONE);
                    itemFilter.setVisible(false);
                    itemSort.setVisible(false);

                    if (componentLeaderBoard != null) {
                        componentLeaderBoard.handleFocus();
                    }
                    try {
                        CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(OustSdkApplication.getContext());
                        HashMap<String, Object> eventUpdate = OustSdkTools.getCleverTapEventData();
                        eventUpdate.put("ClickedOnLeaderBoard", true);
                        eventUpdate.put("Searched", true);
                        Log.d("TAG", "CleverTap instance: " + eventUpdate.toString());
                        if (clevertapDefaultInstance != null) {
                            clevertapDefaultInstance.pushEvent("Leaderboard_Search_Click", eventUpdate);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
            });

            search.setOnCloseListener(() -> {
                screen_name.setVisibility(View.VISIBLE);
                itemFilter.setVisible(isFilterAvailable());
                itemSort.setVisible(isSortAvailable());

                if (componentLeaderBoard != null) {
                    componentLeaderBoard.removeSearch();
                }
                return false;
            });

            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {

                    if (componentLeaderBoard != null) {
                        componentLeaderBoard.handleSearch(query);
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    if (componentLeaderBoard != null) {
                        componentLeaderBoard.handleSearch(newText);
                    }
                    return false;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (itemId == R.id.action_filter) {
            if (componentLeaderBoard != null) {
                try {
                    CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(OustSdkApplication.getContext());
                    HashMap<String, Object> eventUpdate = OustSdkTools.getCleverTapEventData();
                    eventUpdate.put("ClickedOnLeaderBoard", true);
                    Log.d("TAG", "CleverTap instance: " + eventUpdate.toString());
                    if (clevertapDefaultInstance != null) {
                        clevertapDefaultInstance.pushEvent("Leaderboard_Sort_Click", eventUpdate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                componentLeaderBoard.openDialogFragment();
            }

        } else if (itemId == R.id.action_sort) {
            if (componentLeaderBoard != null) {
                componentLeaderBoard.openDialogFragmentSort();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {

        try {
            container = findViewById(R.id.container);
            Toolbar toolbar = findViewById(R.id.toolbar_lay);
            screen_name = findViewById(R.id.screen_name);
            ImageView back_button = findViewById(R.id.back_button);

            //Branding loader
            branding_mani_layout = findViewById(R.id.branding_main_layout);
            branding_bg = findViewById(R.id.branding_bg);
            branding_icon = findViewById(R.id.brand_loader);
            branding_percentage = findViewById(R.id.percentage_text);
            //End

            setSupportActionBar(toolbar);
            screen_name.setText(getResources().getString(R.string.leader_board_title));
            toolbar.setBackgroundColor(bgColor);
            toolbar.setTitle("");
            screen_name.setTextColor(color);
            OustResourceUtils.setDefaultDrawableColor(back_button.getDrawable(), color);

            commonLeaderBoardLay = findViewById(R.id.common_leaderboard_lay);

            back_button.setOnClickListener(v -> onBackPressed());

            String tenantId = OustPreferences.get("tanentid");

            if (tenantId != null && !tenantId.isEmpty()) {
                File brandingBg = new File(OustSdkApplication.getContext().getFilesDir(),
                        ("oustlearn_" + tenantId.toUpperCase().trim() + "splashScreen"));

                if (brandingBg.exists() && OustPreferences.getAppInstallVariable(IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED)) {
                    Picasso.get().load(brandingBg).into(branding_bg);
                } else {
                    String tenantBgImage = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "appImages/splash/org/" + (tenantId.toUpperCase().trim()) + "/android/bgImage";
                    Picasso.get().load(tenantBgImage).into(branding_bg);
                }

                File brandingLoader = new File(OustSdkApplication.getContext().getFilesDir(), ("oustlearn_" + tenantId.toUpperCase().trim() + "splashIcon"));
                if (brandingLoader.exists()) {
                    Picasso.get().load(brandingLoader).into(branding_icon);
                } else {
                    String tenantIcon = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "appImages/splash/org/" + (tenantId.toUpperCase().trim()) + "/android/icon";
                    Picasso.get().load(tenantIcon).error(getResources().getDrawable(R.drawable.app_icon)).into(branding_icon);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void initData() {
        try {
            branding_mani_layout.setVisibility(View.VISIBLE);
            Bundle containerBundle = getIntent().getExtras();
            if (containerBundle != null) {
                containerType = containerBundle.getString("containerType");
                containerName = containerBundle.getString("contentName");
                containerContentId = containerBundle.getLong("containerContentId");
                if (containerType != null) {
                    fetchLeaderBoardData();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void fetchLeaderBoardData() {
        try {
            leaderBoardViewModel = new ViewModelProvider(this).get(LeaderBoardViewModel.class);
            leaderBoardViewModel.init(containerType, containerContentId);
            leaderBoardViewModel.getLeaderBoardResponse().observe(this, leaderBoardResponse -> {

                if (componentLeaderBoard == null) {
                    branding_mani_layout.setVisibility(View.GONE);
//                    layout_loader.setVisibility(View.GONE);
                    componentLeaderBoard = new ComponentLeaderBoard(CommonLeaderBoardActivity.this, null);
                    container.addView(componentLeaderBoard);
                }
                OustStaticVariableHandling.getInstance().setSortPosition(-1);
                if (leaderBoardResponse != null) {
                    leaderBoardDataRowList = leaderBoardResponse.getLeaderBoardDataList();
                    componentLeaderBoard.setContentType(containerType);
                    componentLeaderBoard.setCallBackForView(this);
                    componentLeaderBoard.setData(leaderBoardResponse);
                    componentLeaderBoard.setFilter(getSupportFragmentManager(), leaderBoardResponse.getGroupLbDataList(), leaderBoardResponse.isFilterImplemented());
                    componentLeaderBoard.setSort(getSupportFragmentManager());

                    if (leaderBoardResponse.isFilterImplemented() && leaderBoardResponse.getFilterGroup() != null && leaderBoardResponse.getFilterGroup().getGroupName() != null)
                        screen_name.setText(leaderBoardResponse.getFilterGroup().getGroupName());
                    else {
                        if (containerName != null && !containerName.isEmpty()) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                screen_name.setText(Html.fromHtml(containerName, Html.FROM_HTML_MODE_COMPACT));
                            } else {
                                screen_name.setText(Html.fromHtml(containerName));
                            }
                        } else {
                            screen_name.setText(getResources().getString(R.string.leader_board_title));
                        }
                    }

                    if (leaderBoardResponse.getGroupLbDataList() != null && leaderBoardResponse.getGroupLbDataList().size() != 0) {
                        isFilterAvailable = true;
                        invalidateOptionsMenu();
                    }
                } else {
                    componentLeaderBoard.setData(null);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private boolean isFilterAvailable() {
        return isFilterAvailable;
    }

    private boolean isSortAvailable() {
        return isSortAvailable;
    }

    private boolean isSearchAvailable() {
        return isSearchAvailable;
    }

    @Override
    public void groupFilterData(GroupDataList filterGroup) {
        try {
            if (filterGroup != null && leaderBoardViewModel != null) {
                leaderBoardViewModel.groupDataFilter(filterGroup);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onClickListener(int position) {

        try {
            if (leaderBoardViewModel != null) {

                if (position == 0) {
                    Collections.sort(leaderBoardDataRowList, allRankSorter);
                } else if (position == 1) {
                    Collections.sort(leaderBoardDataRowList, allNameSorter);
                } else if (position == 2) {
                    Collections.sort(leaderBoardDataRowList, allXpSorter);
                } else {
                    Collections.sort(leaderBoardDataRowList, allRankSorter);
                }
                Log.d("thesorteddatais ", "" + leaderBoardDataRowList);

                OustStaticVariableHandling.getInstance().setSortPosition(position);

                if (componentLeaderBoard == null) {
                    componentLeaderBoard = new ComponentLeaderBoard(CommonLeaderBoardActivity.this, null);
                    container.addView(componentLeaderBoard);
                }
                componentLeaderBoard.setCallBackForView(this);
                componentLeaderBoard.setSort(getSupportFragmentManager());
                componentLeaderBoard.setSortData(leaderBoardDataRowList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void hideFilterSort(boolean b) {
        isSortAvailable = b;
        isSearchAvailable = b;
        invalidateOptionsMenu();
    }

    public Comparator<LeaderBoardDataList> allRankSorter = new Comparator<LeaderBoardDataList>() {
        @Override
        public int compare(LeaderBoardDataList s1, LeaderBoardDataList s2) {
            if (Integer.parseInt(String.valueOf(s1.getRank())) < Integer.parseInt(String.valueOf(s2.getRank()))) {
                return -1;
            } else if (Integer.parseInt(String.valueOf(s1.getRank())) > Integer.parseInt(String.valueOf(s2.getRank()))) {
                return 1;
            } else if (Integer.parseInt(String.valueOf(s1.getRank())) == Integer.parseInt(String.valueOf(s2.getRank()))) {
                return 0;
            }
            return 0;
        }
    };

    public Comparator<LeaderBoardDataList> allXpSorter = new Comparator<LeaderBoardDataList>() {
        public int compare(LeaderBoardDataList s1, LeaderBoardDataList s2) {
            return (int) (s2.getScore() - s1.getScore());
        }
    };
    public Comparator<LeaderBoardDataList> allNameSorter = new Comparator<LeaderBoardDataList>() {
        public int compare(LeaderBoardDataList s1, LeaderBoardDataList s2) {
            if (s1.getDisplayName() != null && s2.getDisplayName() != null) {
                return (s1.getDisplayName().toLowerCase()).compareTo(s2.getDisplayName().toLowerCase());
            }
            return 0;
        }
    };
}

