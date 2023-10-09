package com.oustme.oustsdk.activity.common;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.adapter.common.EnterPriseLeaderBoardAdaptor;
import com.oustme.oustsdk.customviews.CustomSearchView;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.response.common.CourseLeaderBoardResponse;
import com.oustme.oustsdk.response.common.LeaderBoardDataRow;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustGATools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.filters.EnterpriseNameFilter;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by shilpysamaddar on 09/03/17.
 */

public class EnterpriseLeaderboard extends AppCompatActivity implements SearchView.OnQueryTextListener,
        CustomSearchView.OnSearchViewCollapsedEventListener, CustomSearchView.OnSearchViewExpandedEventListener {

    private Toolbar toolbar;
    private RecyclerView eventboard_leaderBoardRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView nodatatext, rank_text, name_text, ponts_text;
    private Spinner leaderboard_spinner;
    private LinearLayout labellayout;
    private LinearLayout eventboard_baseLayout;
    MenuItem oust;
    private MenuItem actionSearch;
    private View searchPlate;
    public CustomSearchView newSearchView;

    private List<LeaderBoardDataRow> leaderBaordDataRowList;
    private String spinnerData = "RANK";

    private ActiveUser activeUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        try {
            OustSdkTools.setLocale(EnterpriseLeaderboard.this);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        setContentView(R.layout.activity_enterprise_lb);
        initViews();
        initLBClass();
//        OustGATools.getInstance().reportPageViewToGoogle(EnterpriseLeaderboard.this, "Enterprise Leaderboard Page");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.alert_menu, menu);
            actionSearch = menu.findItem(R.id.action_search);
            showSearchIcon();

            //newSearchView = (CustomSearchView) MenuItemCompat.getActionView(actionSearch);
            newSearchView = (CustomSearchView) actionSearch.getActionView();

            newSearchView.setOnQueryTextListener(this);
            newSearchView.setQueryHint(getResources().getString(R.string.search_text));
            newSearchView.setVisibility(View.VISIBLE);
            newSearchView.requestFocusFromTouch();
            newSearchView.setOnSearchViewCollapsedEventListener(EnterpriseLeaderboard.this);
            newSearchView.setOnSearchViewExpandedEventListener(EnterpriseLeaderboard.this);
            View searchPlate = newSearchView.findViewById(R.id.search_plate);
            searchPlate.setBackgroundColor(OustSdkTools.getColorBack(R.color.black_semi_transparent));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void showSearchIcon() {
        try {
//                int courseSize=0;
//                if(favouriteCardsCourseDataList!=null){
//                    courseSize=favouriteCardsCourseDataList.size();
//                }
            actionSearch.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_search) {
            actionSearch.setVisible(true);
            searchPlate = newSearchView.findViewById(R.id.search_plate);
            searchPlate.setBackgroundColor(OustSdkTools.getColorBack(R.color.black_semi_transparent));
            return true;

        }
//        else if(itemId==R.id.oust) {
//            finish();
//            return true;
//        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void initViews() {
        toolbar = findViewById(R.id.tabanim_toolbar);
        eventboard_leaderBoardRecyclerView = findViewById(R.id.eventboard_leaderBoardList);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        nodatatext = findViewById(R.id.nodatatext);
        leaderboard_spinner = findViewById(R.id.leaderboard_spinner);
        labellayout = findViewById(R.id.labellayout);
        eventboard_baseLayout = findViewById(R.id.eventboard_baseLayout);
        rank_text = findViewById(R.id.rank_text);
        name_text = findViewById(R.id.name_text);
        ponts_text = findViewById(R.id.ponts_text);
        name_text.setText(getResources().getString(R.string.name_text));
        rank_text.setText(getResources().getString(R.string.rank));
        ponts_text.setText(getResources().getString(R.string.points_text));
    }

    public void initLBClass() {
        try {
            setToolBarColor();
            OustSdkTools.setSnackbarElements(eventboard_baseLayout, EnterpriseLeaderboard.this);
            activeUser = OustAppState.getInstance().getActiveUser();
            if ((activeUser != null) && (activeUser.getStudentid() != null)) {
                createLoader();
                getLeaderBoardData();
            } else {
                HttpManager.setBaseUrl();
                OustFirebaseTools.initFirebase();
                String activeUserGet = OustPreferences.get("userdata");
                activeUser = OustSdkTools.getActiveUserData(activeUserGet);
                createLoader();
                getLeaderBoardData();
            }
        } catch (Exception e) {
        }
    }

    private void setToolBarColor() {
        try {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
            TextView actionBarTitle = toolbar.findViewById(R.id.title);
            actionBarTitle.setText(getResources().getString(R.string.leader_board_title).toUpperCase());
            String toolbarColorCode = OustPreferences.get("toolbarColorCode");
            if ((toolbarColorCode != null) && (!toolbarColorCode.isEmpty())) {
                toolbar.setBackgroundColor(Color.parseColor(toolbarColorCode));
            }
        } catch (Exception e) {
        }
    }


    private void createLoader() {
        try {
            swipeRefreshLayout.setColorSchemeColors(OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen), OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen));
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    if (!isSearchOn) {
                        swipeRefreshLayout.setRefreshing(true);
                    }
                }
            });
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (!isSearchOn) {
                        getLeaderBoardData();
                    } else {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            });
        } catch (Exception e) {
        }
    }

    public void getLeaderBoardData() {
        try {
            if (!OustSdkTools.checkInternetStatus()) {
                return;
            }
            leaderBaordDataRowList = new ArrayList<>();
            String getLeaderboardUrl = OustSdkApplication.getContext().getResources().getString(R.string.get_lb_url);

            getLeaderboardUrl = HttpManager.getAbsoluteUrl(getLeaderboardUrl);

            ApiCallUtils.doNetworkCall(Request.Method.GET, getLeaderboardUrl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    CourseLeaderBoardResponse courseLeaderBoardResponce = new CourseLeaderBoardResponse();
                    courseLeaderBoardResponce = gson.fromJson(response.toString(), CourseLeaderBoardResponse.class);
                    if ((null != courseLeaderBoardResponce) && (courseLeaderBoardResponce.getLeaderBoardDataList() != null)) {
                        if (spinnerData.equals("RANK")) {
                            addItemsOnSpinner();
                        }
                        leaderBaordDataRowList = courseLeaderBoardResponce.getLeaderBoardDataList();
                        setSortedItem();
                        createList(leaderBaordDataRowList);
                    } else {
                        hideLoader();
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    hideLoader();
                    OustSdkTools.showToast(getResources().getString(R.string.error_message));
                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, getLeaderboardUrl, OustSdkTools.getRequestObject(null), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    CourseLeaderBoardResponse courseLeaderBoardResponce=new CourseLeaderBoardResponse();
                    courseLeaderBoardResponce=gson.fromJson(response.toString(),CourseLeaderBoardResponse.class);
                    if ((null != courseLeaderBoardResponce)&&(courseLeaderBoardResponce.getLeaderBoardDataList()!=null)) {
                        if(spinnerData.equals("RANK")) {
                            addItemsOnSpinner();
                        }
                        leaderBaordDataRowList=courseLeaderBoardResponce.getLeaderBoardDataList();
                        setSortedItem();
                        createList(leaderBaordDataRowList);
                    }else {
                        hideLoader();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideLoader();
                    OustSdkTools.showToast(getResources().getString(R.string.error_message));
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<String, String>();
                    try {
                        params.put("api-key", OustPreferences.get("api_key"));
                        params.put("org-id", OustPreferences.get("tanentid"));
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    return params;
                }
            };
            jsonObjReq.setShouldCache(false);
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void hideLoader() {
        try {
            swipeRefreshLayout.setRefreshing(false);
            OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg));
        } catch (Exception e) {
        }
    }


    public void createList(List<LeaderBoardDataRow> leaderBaordDataRowList) {
        try {
            swipeRefreshLayout.setRefreshing(false);
            if (((leaderBaordDataRowList != null) && (leaderBaordDataRowList.size() > 0))) {
                swipeRefreshLayout.setVisibility(View.VISIBLE);
                labellayout.setVisibility(View.VISIBLE);
                nodatatext.setVisibility(View.GONE);
                EnterPriseLeaderBoardAdaptor leaderBoardAllAdapter = new EnterPriseLeaderBoardAdaptor(leaderBaordDataRowList);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(EnterpriseLeaderboard.this);
                eventboard_leaderBoardRecyclerView.setLayoutManager(mLayoutManager);
                eventboard_leaderBoardRecyclerView.setItemAnimator(new DefaultItemAnimator());
                eventboard_leaderBoardRecyclerView.setAdapter(leaderBoardAllAdapter);
            } else {
                labellayout.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.GONE);
                nodatatext.setVisibility(View.VISIBLE);
                nodatatext.setText(getResources().getString(R.string.no_data_found));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public void addItemsOnSpinner() {
        try {
            List<String> list = new ArrayList<String>();
            list.add("RANK");
            list.add("NAME");
            list.add("Score");
            final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.simplespinner, list);
            dataAdapter.setDropDownViewResource(R.layout.simplespinneritem);
            leaderboard_spinner.setAdapter(dataAdapter);
            leaderboard_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    spinnerData = leaderboard_spinner.getSelectedItem().toString();
                    setSortedItem();
                    createList(leaderBaordDataRowList);
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public Comparator<LeaderBoardDataRow> allRankSorter = new Comparator<LeaderBoardDataRow>() {
        public int compare(LeaderBoardDataRow s1, LeaderBoardDataRow s2) {
            if (s1.getRank() != null && s2.getRank() != null) {
                if (Integer.parseInt(s1.getRank()) < Integer.parseInt(s2.getRank())) {
                    return -1;
                } else if (Integer.parseInt(s1.getRank()) > Integer.parseInt(s2.getRank())) {
                    return 1;
                } else if (Integer.parseInt(s1.getRank()) == Integer.parseInt(s2.getRank())) {
                    return 0;
                }
            }
            return 0;
        }
    };

    public Comparator<LeaderBoardDataRow> allXpSorter = new Comparator<LeaderBoardDataRow>() {
        public int compare(LeaderBoardDataRow s1, LeaderBoardDataRow s2) {
            return s2.getScore() - s1.getScore();
        }
    };
    public Comparator<LeaderBoardDataRow> allNameSorter = new Comparator<LeaderBoardDataRow>() {
        public int compare(LeaderBoardDataRow s1, LeaderBoardDataRow s2) {
            if (s1.getDisplayName() != null && s2.getDisplayName() != null) {
                return (s1.getDisplayName().toLowerCase()).compareTo(s2.getDisplayName().toLowerCase());
            }
            return 0;
        }
    };

    private void setSortedItem() {
        try {
            if (spinnerData.equals("NAME")) {
                Collections.sort(leaderBaordDataRowList, allNameSorter);
            } else if (spinnerData.equals("Score")) {
                Collections.sort(leaderBaordDataRowList, allXpSorter);
            } else if (spinnerData.equals("RANK")) {
                Collections.sort(leaderBaordDataRowList, allRankSorter);
            } else {
                Collections.sort(leaderBaordDataRowList, allRankSorter);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        if (!query.isEmpty()) {
        }
        return false;
    }

    private String searchText = "";

    @Override
    public boolean onQueryTextChange(String newText) {
        try {
            if (newText.isEmpty()) {
                searchText = "";
                createList(leaderBaordDataRowList);
            } else {
                searchText = newText;
                filterList(newText);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return true;
    }

    private void filterList(String searchText) {
        try {
            List<LeaderBoardDataRow> updatedleaderBaordDataRowList = new ArrayList<>();
            if ((leaderBaordDataRowList != null) && (leaderBaordDataRowList.size() > 0)) {
                EnterpriseNameFilter nameFilter = new EnterpriseNameFilter();
                updatedleaderBaordDataRowList = nameFilter.meetCriteria(leaderBaordDataRowList, searchText);
                createList(updatedleaderBaordDataRowList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean isSearchOn = false;


    @Override
    public void onSearchViewExpanded() {
        try {
            isSearchOn = true;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onSearchViewCollapsed() {
        isSearchOn = false;
        setSortedItem();
        createList(leaderBaordDataRowList);
    }
}
