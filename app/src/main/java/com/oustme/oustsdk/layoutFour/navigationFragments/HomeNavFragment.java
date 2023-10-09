package com.oustme.oustsdk.layoutFour.navigationFragments;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.clevertap.android.sdk.CleverTapAPI;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.dialogs.LanguageBottomSheet;
import com.oustme.oustsdk.feed_ui.services.FeedUpdatingServices;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.interfaces.common.DataLoaderNotifier;
import com.oustme.oustsdk.interfaces.common.FeedClickListener;
import com.oustme.oustsdk.layoutFour.ComponentAccess;
import com.oustme.oustsdk.layoutFour.LandingActivity;
import com.oustme.oustsdk.layoutFour.components.dialogFragments.BottomFilterDialogFragment;
import com.oustme.oustsdk.layoutFour.components.feedList.AnnouncementFeedViewModel;
import com.oustme.oustsdk.layoutFour.components.feedList.FeedFilterViewModel;
import com.oustme.oustsdk.layoutFour.components.feedList.FeedViewModel;
import com.oustme.oustsdk.layoutFour.components.feedList.UserFeedFilters;
import com.oustme.oustsdk.layoutFour.components.feedList.adapter.FeedsRecyclerViewAdapter;
import com.oustme.oustsdk.layoutFour.components.myTask.MyTaskViewModel;
import com.oustme.oustsdk.layoutFour.components.myTask.Response.UserEarnedCoinsResponse;
import com.oustme.oustsdk.layoutFour.components.popularFeeds.PopularFeedAdapter;
import com.oustme.oustsdk.layoutFour.components.popularFeeds.PopularFeedViewModel;
import com.oustme.oustsdk.layoutFour.components.userOverView.ActiveUserViewModel;
import com.oustme.oustsdk.layoutFour.data.Navigation;
import com.oustme.oustsdk.request.ClickedFeedData;
import com.oustme.oustsdk.request.ViewTracker;
import com.oustme.oustsdk.request.ViewedFeedData;
import com.oustme.oustsdk.response.common.LanguageClass;
import com.oustme.oustsdk.room.dto.DTONewFeed;
import com.oustme.oustsdk.room.dto.DTOSpecialFeed;
import com.oustme.oustsdk.room.dto.DTOUserFeeds;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustDataHandler;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.ThreadPoolProvider;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.tools.filters.CommonLandingFilter;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeNavFragment extends Fragment implements ComponentAccess,
        FeedClickListener, DataLoaderNotifier, LandingActivity.RemoveFilterView {

    public static final String TAG = "HomeNavFragment";

    NestedScrollView nested_view;

    View user_component;
    TextView txtUser;
    TextView txtCoin;
    TextView txtPending;
    TextView goTxt;
    CircleImageView ivAvatar;

    //popularFeeds
    CardView announcement_component;
    RecyclerView rvPopularFeeds;
    TabLayout tabLayout;

    //feedList
    RecyclerView rvFeeds;
    TextView title;
    ImageView filter;
    View no_data_layout;
    TextView no_data_content;
    ChipGroup cgFilter;
    ProgressBar pbFeeds;

    //Models
    FeedViewModel feedViewModel;
    AnnouncementFeedViewModel announcementFeedViewModel;
    PopularFeedViewModel popularFeedViewModel;
    MyTaskViewModel myTaskViewModel;
    ActiveUser userdata;

    //common for all base fragment
    static final String ARG_NAV_ITEM = "navItem";
    LandingActivity landingActivity;
    Navigation navigation;
    HashMap<String, CommonLandingData> commonLandingDataHashMap;

    PopularFeedAdapter announcementAdapter;
    FeedsRecyclerViewAdapter feedsRecyclerViewAdapter;
    ViewTracker viewTracker;
    ArrayList<UserFeedFilters.FeedFilter> filterCategories;
    FragmentManager fragmentManager;
    ActiveUser activeUser;
    ArrayList<DTOUserFeeds.FeedList> feedList = new ArrayList<>();
    ArrayList<UserFeedFilters.FeedFilter> selectedFeedFilter;
    StringBuilder filtersUrl;
    int localFeedCount = 0;
    boolean isFilterApplicable;
    private CloseSearchIcon closeSearchIcon;
    private boolean isMultipleCpl = false;
    boolean isLanguageScreenEnabled, isCityScreenEnabled;
    LinearLayoutManager mLayoutManager;

    ArrayList<CommonLandingData> allModule = new ArrayList<>();
    ArrayList<CommonLandingData> allList = new ArrayList<>();
    ArrayList<CommonLandingData> courseList = new ArrayList<>();
    ArrayList<CommonLandingData> assessmentList = new ArrayList<>();
    ArrayList<CommonLandingData> cplList = new ArrayList<>();
    ArrayList<CommonLandingData> FFF_contest_List = new ArrayList<>();
    ArrayList<DTOSpecialFeed> dtoSpecialFeeds = new ArrayList<>();
    ArrayList<DTOSpecialFeed> tempDtoSpecialFeeds = new ArrayList<>();
    ArrayList<DTOSpecialFeed> tempDtoAnnouncementFeed = new ArrayList<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    // Auto Scroll
    final int delay = 3000; // 3 seconds
    final int scrollStep = 1;
    final Handler handler = new Handler();
    Runnable runnable;

    private static final int PAGE_START = 1;
    private float TOTAL_PAGES;
    int PAGE_SIZE = 15;
    private int currentPage = PAGE_START;
    boolean isLastPage = false, isLoading = false, loadFeedData = false;
    LinearLayoutManager linearLayoutManager;
    ProgressBar progressBar;
    long mLastTimeClicked = 0;
    TextView new_feeds;
    //End

    public HomeNavFragment() {
        // Required empty public constructor
    }

    public static HomeNavFragment newInstance(Navigation navigation) {
        HomeNavFragment fragment = new HomeNavFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_NAV_ITEM, navigation);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            navigation = getArguments().getParcelable(ARG_NAV_ITEM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_nav_2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
    }

    private void initView(View view) {
        try {
            user_component = view.findViewById(R.id.user_component);
            txtUser = user_component.findViewById(R.id.user_name);
            txtCoin = user_component.findViewById(R.id.coins_text);
            cgFilter = view.findViewById(R.id.cg_filter);
            pbFeeds = view.findViewById(R.id.pb_feeds);
            //client requirement
            try {
                ImageView img_coin = user_component.findViewById(R.id.img_coin);
                if (OustPreferences.getAppInstallVariable("showCorn")) {
                    img_coin.setImageResource(R.drawable.ic_coins_corn);
                } else {
                    img_coin.setImageResource(R.drawable.ic_coins_golden);
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
            txtPending = user_component.findViewById(R.id.pending_count);
            goTxt = user_component.findViewById(R.id.mytask);
            ivAvatar = user_component.findViewById(R.id.user_avatar);
            announcement_component = view.findViewById(R.id.announcement_component);
            rvPopularFeeds = view.findViewById(R.id.vp_popular_feeds_home_nav);
            tabLayout = view.findViewById(R.id.tab_layout);
            rvFeeds = view.findViewById(R.id.rv_feeds);
            title = view.findViewById(R.id.tv_title);
            filter = view.findViewById(R.id.iv_filter);
            no_data_layout = view.findViewById(R.id.no_data_layout);
            ImageView no_image = view.findViewById(R.id.no_image);
            no_data_content = view.findViewById(R.id.no_data_content);
            no_image.setImageDrawable(getResources().getDrawable(R.drawable.no_feed));
            no_data_content.setText(getResources().getString(R.string.no_feed_content));
            progressBar = view.findViewById(R.id.progressBar);
            new_feeds = view.findViewById(R.id.new_feeds);

            isMultipleCpl = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_MULTIPLE_CPL);
            announcement_component.setVisibility(GONE);
            filter.setVisibility(INVISIBLE);
            filter.setOnClickListener(v -> openFilterDialogue());

            String toolbarColorCode = OustPreferences.get("toolbarColorCode");

            pbFeeds.getIndeterminateDrawable().setColorFilter(Color.parseColor(toolbarColorCode), android.graphics.PorterDuff.Mode.MULTIPLY);
            pbFeeds.setVisibility(VISIBLE);
//            mLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
//            rvFeeds.setLayoutManager(mLayoutManager);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        goTxt.setOnClickListener(view1 -> {
            try {
                String getMenuName = landingActivity.getOnNavSelection();
                if (getMenuName != null) {
                    if (getMenuName.equalsIgnoreCase("Learning") || getMenuName.equalsIgnoreCase(getResources().getString(R.string.learning))) {
                        landingActivity.onNewSelection(1);
                    } else {
                        landingActivity.onNewSelection(0);
                    }
                } else {
                    landingActivity.onNewSelection(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        });
        try {
            final int radius = getResources().getDimensionPixelSize(R.dimen.radius);
            final int dotsHeight = getResources().getDimensionPixelSize(R.dimen.recycle_dots_height);
            final int color = ContextCompat.getColor(requireContext(), R.color.textBlack);
            rvPopularFeeds.addItemDecoration(new DotsIndicatorDecoration(radius, radius * 4, dotsHeight, color, color));
            new PagerSnapHelper().attachToRecyclerView(rvPopularFeeds);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        new_feeds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new_feeds.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.event_animmoveup));
                    new_feeds.setVisibility(GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                    new_feeds.setVisibility(GONE);
                }
                feedList = new ArrayList<>();
                localFeedCount = 0;
                currentPage = 1;
                if (feedsRecyclerViewAdapter != null) {
                    feedsRecyclerViewAdapter.clear();
                }
                isLoading = true;
                progressBar.setVisibility(VISIBLE);
                OustStaticVariableHandling.getInstance().setFeeds(new ArrayList<>());
                fetchAnnouncementFeeds();
                fetchUserFeeds("", currentPage);
            }
        });
        try {
            feedsRecyclerViewAdapter = new FeedsRecyclerViewAdapter();
            feedsRecyclerViewAdapter.setFeedsRecyclerAdapter(activeUser, initGlide(), getActivity(), this, isMultipleCpl, rvFeeds);
            linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            rvFeeds.setLayoutManager(linearLayoutManager);
            rvFeeds.setItemAnimator(new DefaultItemAnimator());
            rvFeeds.setAdapter(feedsRecyclerViewAdapter);
            rvFeeds.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    if (thumbnail != null) { // show the old thumbnail
//                        thumbnail.setVisibility(VISIBLE);
//                    }
                        // There's a special case when the end of the list has been reached.
                        try {
                            if (recyclerView != null && recyclerView.getVisibility() == VISIBLE) {
                                int firstVisibleItemPosition = ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findFirstVisibleItemPosition();
                                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                                analyzeAndAddViewData(firstVisibleItemPosition, lastVisibleItemPosition);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (!recyclerView.canScrollVertically(1)) {
                            try {
                                int endPosition = ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findLastVisibleItemPosition();
                                feedsRecyclerViewAdapter.playVideo(endPosition == feedList.size());
                            } catch (Exception e) {
                                feedsRecyclerViewAdapter.playVideo(false);
                            }
                        } else {
                            feedsRecyclerViewAdapter.playVideo(false);
                        }
                        if (loadFeedData && !rvFeeds.canScrollVertically(-1)) {
                            //recycler view is in firstPosition
                            loadFeedData = false;
                            Log.d("checkFirstVisible-:-", "Yes");
                            feedList = new ArrayList<>();
                            localFeedCount = 0;
                            currentPage = 1;
                            if (feedsRecyclerViewAdapter != null) {
                                feedsRecyclerViewAdapter.clear();
                            }
                            isLoading = true;
                            progressBar.setVisibility(VISIBLE);
                            try {
                                new_feeds.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.event_animmoveup));
                                new_feeds.setVisibility(GONE);
                            } catch (Exception e) {
                                e.printStackTrace();
                                new_feeds.setVisibility(GONE);
                            }
                            OustStaticVariableHandling.getInstance().setFeeds(new ArrayList<>());
                            fetchAnnouncementFeeds();
                            fetchUserFeeds("", currentPage);
                        }
                    }
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int visibleItemCount = Objects.requireNonNull(rvFeeds.getLayoutManager()).getChildCount();
                    int totalItemCount = rvFeeds.getLayoutManager().getItemCount();
                    int firstVisibleItemPosition = ((LinearLayoutManager) rvFeeds.getLayoutManager()).findFirstVisibleItemPosition();
                    if (!isLoading && !isLastPage) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                                && firstVisibleItemPosition >= 0) {
                            isLoading = true;
                            progressBar.setVisibility(VISIBLE);
                            currentPage += 1;
                            Log.d("checkingPagination->2 ", "" + currentPage);
                            fetchUserFeeds("", currentPage);
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            closeSearchIcon = (LandingActivity) context;
            landingActivity = (LandingActivity) context;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void initData() {
        try {
            userdata = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
            isLanguageScreenEnabled = OustPreferences.getAppInstallVariable("showLanguageScreen");
            isCityScreenEnabled = OustPreferences.getAppInstallVariable("showCityListScreenForCPL");
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (runnable != null) {
                handler.removeCallbacks(runnable);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        if (executorService != null) {
            executorService.shutdown();
        }
        ThreadPoolProvider.getInstance().shutDown();
    }

    private void callFeedsRefresh() {
        Log.d("regreshtime", "" + OustPreferences.getTimeForNotification("refreshTimeIs"));
        activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
        String message = null;
        if (activeUser != null) {
            message = "/landingPage/" + activeUser.getStudentKey() + "/feedRefreshTime";
            ValueEventListener userFeedsData = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        if (null != snapshot.getValue()) {
                            String refreshTime = (String) snapshot.getValue();
                            long refreshTimeLong = Long.parseLong(refreshTime);
                            if (refreshTimeLong > 0) {
                                if (refreshTimeLong > OustPreferences.getTimeForNotification("refreshTimeIs")) {
                                    if (SystemClock.elapsedRealtime() - mLastTimeClicked < 1500) {
                                        return;
                                    }
                                    try {
                                        new_feeds.setVisibility(VISIBLE);
                                        new_feeds.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.event_animmovedown));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        new_feeds.setVisibility(VISIBLE);
                                    }
                                    loadFeedData = true;
                                    if (!rvFeeds.canScrollVertically(-1)) {
                                        //recycler view is in firstPosition
                                        Log.d("checkFirstVisible-:-", "Yes");
                                        feedList = new ArrayList<>();
                                        localFeedCount = 0;
                                        currentPage = 1;
                                        if (feedsRecyclerViewAdapter != null) {
                                            feedsRecyclerViewAdapter.clear();
                                        }
                                        isLoading = true;
                                        progressBar.setVisibility(VISIBLE);
                                        try {
                                            new_feeds.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.event_animmoveup));
                                            new_feeds.setVisibility(GONE);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            new_feeds.setVisibility(GONE);
                                        }
                                        OustStaticVariableHandling.getInstance().setFeeds(new ArrayList<>());
                                        fetchAnnouncementFeeds();
                                        fetchUserFeeds("", currentPage);
                                    }
                                }
                                OustPreferences.saveTimeForNotification("refreshTimeIs", refreshTimeLong);
                            } else {
                                OustPreferences.saveTimeForNotification("refreshTimeIs", 0);
                            }
                        } else {
                            OustPreferences.saveTimeForNotification("refreshTimeIs", 0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "onCancelled: --> " + error.getMessage());
                    Toast.makeText(requireActivity(), "Something went wrong. . ", Toast.LENGTH_SHORT).show();
                }
            };
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
            OustFirebaseTools.getRootRef().child(message).addValueEventListener(userFeedsData);
            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(userFeedsData, message));
        }
    }

    private void setContentComponent() {
        try {
            cgFilter.removeAllViews();
            selectedFeedFilter = null;
            activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
            localFeedCount = 0;
            OustDataHandler.getInstance().setAllFeedsLoaded(false);
            OustPreferences.saveAppInstallVariable("feedScroll", true);
            feedList = new ArrayList<>();
            currentPage = 1;
            try {
                if (feedsRecyclerViewAdapter != null) {
                    feedsRecyclerViewAdapter.clear();
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
            fetchActiveUserData();
            try {
                if (SystemClock.elapsedRealtime() - mLastTimeClicked < 3000) {
//                    swipe_container.setRefreshing(false);
                    return;
                }
                mLastTimeClicked = SystemClock.elapsedRealtime();
                OustStaticVariableHandling.getInstance().setFeeds(new ArrayList<>());
                fetchAnnouncementFeeds();
                fetchUserFeeds("", currentPage);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                runnable = new Runnable() {
                    int currentPosition = 0;

                    @Override
                    public void run() {
                        if (rvPopularFeeds != null && rvPopularFeeds.getAdapter() != null) {
                            rvPopularFeeds.smoothScrollToPosition(currentPosition);
                            currentPosition += scrollStep;
                            if (currentPosition >= rvPopularFeeds.getAdapter().getItemCount()) {
                                currentPosition = 0;
                            }
                            handler.postDelayed(this, delay);
                        }
                    }
                };
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void fetchAnnouncementFeeds() {
        try {
            announcementFeedViewModel = new ViewModelProvider(this).get(AnnouncementFeedViewModel.class);
            announcementFeedViewModel.init();
            announcementFeedViewModel.getAnnouncementFeedsList().observe(getViewLifecycleOwner(), dtoUserFeeds -> {
                if (dtoUserFeeds != null) {
                    if (dtoUserFeeds.getDtoSpecialFeed() != null && dtoUserFeeds.getDtoSpecialFeed().size() > 0) {
                        tempDtoSpecialFeeds.clear();
                        tempDtoAnnouncementFeed.clear();
                        announcement_component.setVisibility(View.VISIBLE);
                        tempDtoSpecialFeeds.addAll(dtoUserFeeds.getDtoSpecialFeed());
                        setAnnouncement(tempDtoSpecialFeeds);
                    } else {
                        hidePbFeedsLoader();
                        announcement_component.setVisibility(GONE);
                    }
                } else {
                    announcement_component.setVisibility(View.GONE);
                    hidePbFeedsLoader();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchActiveUserData() {
        try {
            user_component.setVisibility(VISIBLE);
            activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
            /*if (OustPreferences.get("userdata") != null && !OustPreferences.get("userdata").isEmpty()) {
            } else {
                ActiveUserViewModel activeUserViewModel = new ViewModelProvider(this).get(ActiveUserViewModel.class);
                activeUserViewModel.init();
                Log.d("layoutRefreshTime_0", "HOMEPAGE: " + OustPreferences.get("userdata"));
                activeUserViewModel.getmActiveUser().observe(getViewLifecycleOwner(), activeUserModel -> {
                    activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
                });
            }*/
            if (activeUser != null) {
                String greetingWithName = getResources().getString(R.string.welcome) + " " + activeUser.getUserDisplayName();
                if (activeUser.getAvatar() != null && !activeUser.getAvatar().isEmpty()) {
                    Picasso.get().load(activeUser.getAvatar()).placeholder(R.drawable.ic_user_avatar).networkPolicy(NetworkPolicy.OFFLINE).into(ivAvatar);
                }
                txtUser.setText(greetingWithName);
                getUserLanguage(activeUser.getStudentid());
            }

            myTaskViewModel = new ViewModelProvider(this).get(MyTaskViewModel.class);
            myTaskViewModel.init();
            myTaskViewModel.getTaskMap().observe(getViewLifecycleOwner(), commonLandingDataMap -> {
                commonLandingDataHashMap = commonLandingDataMap;
                OustDataHandler.getInstance().setCommonLandingDataHashMap(commonLandingDataHashMap);
                setUserLandingPageData(commonLandingDataHashMap);
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getUserLanguage(String studentId) {
        String getPointsUrl = OustSdkApplication.getContext().getResources().getString(R.string.get_student_language);
        getPointsUrl = getPointsUrl.replace("{studentId}", "arvind");
        try {
            getPointsUrl = HttpManager.getAbsoluteUrl(getPointsUrl);

            ApiCallUtils.doNetworkCall(Request.Method.GET, getPointsUrl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.toString());
                        JSONObject languageDataObject = jsonObject.getJSONObject("languageData");
                        int languageId = languageDataObject.getInt("languageId");
                        if (languageId == 0) {
                            showLanguageBottomSheet();
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showLanguageBottomSheet() {
        LanguageBottomSheet bottomSheet = new LanguageBottomSheet(true, true);
        bottomSheet.setCancelable(false);
        bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());
    }


    public void setUserLandingPageData(HashMap<String, CommonLandingData> stringCommonLandingDataHashMap) {
        try {
            if (stringCommonLandingDataHashMap != null) {
                long coins = 0;
                String shredPrefCoins;
                allModule = new ArrayList<>();
                ArrayList<CommonLandingData> allLists = new ArrayList<>(stringCommonLandingDataHashMap.values());
                Collections.sort(allLists, CommonLandingData.sortByDate);
                allList = (new ArrayList<>(allLists));

                CommonLandingFilter commonLandingFilter = new CommonLandingFilter();
                courseList = commonLandingFilter.getCourseModules(allList).get();

                commonLandingFilter = new CommonLandingFilter();
                assessmentList = commonLandingFilter.getAssessmentModules(allList).get();

                commonLandingFilter = new CommonLandingFilter();
                cplList = commonLandingFilter.getCplModules(allList).get();

                commonLandingFilter = new CommonLandingFilter();
                FFF_contest_List = commonLandingFilter.getFFFCModules(allList).get();

                if (courseList != null && courseList.size() != 0) {
                    allModule.addAll(courseList);
                }

                if (cplList != null && cplList.size() != 0) {
                    allModule.addAll(cplList);
                }

                if (assessmentList != null && assessmentList.size() != 0) {
                    allModule.addAll(assessmentList);
                }

                if (FFF_contest_List != null && FFF_contest_List.size() != 0) {
                    allModule.addAll(FFF_contest_List);
                }
                shredPrefCoins = OustPreferences.get("earnedUserCoins");
                if (shredPrefCoins != null && !shredPrefCoins.isEmpty()) {
                    coins = Long.parseLong(shredPrefCoins);
                }

                if (coins > 0) {
                    txtCoin.setVisibility(VISIBLE);
                    txtCoin.setText(String.valueOf(coins));
                }
                if (allModule.size() > 0) {
                    txtPending.setVisibility(VISIBLE);
                    String pendingTask = getResources().getString(R.string.you_have) + "<b> " + allModule.size() + " </b>" +
                            getResources().getString(R.string.pending_tasks);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        txtPending.setText(Html.fromHtml(pendingTask, Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        txtPending.setText(Html.fromHtml(pendingTask));
                    }
                } else {
                    txtPending.setVisibility(VISIBLE);
                    txtPending.setText(Html.fromHtml(getResources().getString(R.string.no_pending_content)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setAnnouncement(List<DTOSpecialFeed> feeds) {
        try {
            if (feeds.size() > 1) {
                if (runnable != null) {
                    handler.removeCallbacks(runnable);
                }
                handler.postDelayed(runnable, delay);
            } else {
                if (runnable != null) {
                    handler.removeCallbacks(runnable);
                }
            }
            if (feeds.size() > 0) {
                announcement_component.setVisibility(View.VISIBLE);
                announcementAdapter = new PopularFeedAdapter(feeds, getContext(), activeUser, this, isMultipleCpl);
                rvPopularFeeds.setAdapter(null);
                rvPopularFeeds.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true));
                rvPopularFeeds.setAdapter(announcementAdapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void fetchUserFeeds(String filtersUrl, int pageNum) {
        loadFeedData = false;
        userFeedsApiCall(filtersUrl, pageNum, "");
        FeedFilterViewModel feedFilterViewModel = new ViewModelProvider(this).get(FeedFilterViewModel.class);
        feedFilterViewModel.init(requireContext());
        feedFilterViewModel.getFeedFilters().observe(getViewLifecycleOwner(), filterCategories -> {
            setFilter(getParentFragmentManager(), filterCategories);
            try {
                if (filterCategories != null && filterCategories.size() != 0) {
                    filter.setVisibility(VISIBLE);
                } else {
                    hidePbFeedsLoader();
                    hideFilter();
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        });
    }

    private void userFeedsApiCall(String filtersUrl, int pageNum, String query) {
        try {
            OustPreferences.save("QuerySearch", query);
            feedViewModel = new ViewModelProvider(this).get(FeedViewModel.class);
            feedViewModel.init(filtersUrl, pageNum, query);
            feedViewModel.getUserFeedsList().observe(getViewLifecycleOwner(), dtoUserFeeds -> {
                if (dtoUserFeeds != null) {
                    try {
                        TOTAL_PAGES = (float) Math.ceil((float) dtoUserFeeds.getFeedCount() / PAGE_SIZE);
                        isLastPage = currentPage >= Math.round(TOTAL_PAGES);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (dtoUserFeeds.getDtoUserFeeds() != null && dtoUserFeeds.getDtoUserFeeds().size() > 0) {
                        // TODO UserFeeds API
                        no_data_layout.setVisibility(GONE);
                        String feedSize = getResources().getString(R.string.feeds_text) + "(" + dtoUserFeeds.getFeedCount() + ")";
                        title.setText(feedSize);
                        setUserFeeds(dtoUserFeeds.getDtoUserFeeds());
                    } else {
                        hidePbFeedsLoader();
                        hideFilter();
//                        title.setText(getResources().getString(R.string.feeds_text));
                        no_data_layout.setVisibility(View.VISIBLE);
                    }
                } else {
                    String feedSize = Objects.requireNonNull(requireActivity()).getResources().getString(R.string.feeds_text);
                    title.setText(feedSize);
                    no_data_layout.setVisibility(View.VISIBLE);
                    no_data_content.setText(getResources().getString(R.string.no_feed_content));
                    rvFeeds.setVisibility(GONE);
                    progressBar.setVisibility(GONE);
                    hidePbFeedsLoader();
                    no_data_layout.setVisibility(View.VISIBLE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setUserFeeds(ArrayList<DTOUserFeeds.FeedList> feedList) {
        try {
            this.feedList = feedList;
            if (feedList.size() != 0) {
                rvFeeds.setVisibility(View.VISIBLE);
            }
            loadFeedData();
            /*viewTracker = new ViewTracker();
            viewTracker.setRecyclerView(rvFeeds);
            viewTracker.setFeedClickListener(this);
            viewTracker.setFeedFromLanding(true, this);
            viewTracker.startTracking();*/
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void openFilterDialogue() {
        try {
            BottomFilterDialogFragment dialogFragment = BottomFilterDialogFragment.newInstance(filterCategories, selectedFeedFilter, this::filterFeed);
            dialogFragment.setStyle(BottomSheetDialogFragment.STYLE_NO_TITLE, R.style.BottomSheetDialogTheme);
            dialogFragment.show(fragmentManager, "Filter Dialog");
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void filterFeed(ArrayList<UserFeedFilters.FeedFilter> selectedFilter) {
        try {
            selectedFeedFilter = new ArrayList<>(selectedFilter);
            if (selectedFeedFilter.size() == 0) {
                isFilterApplicable = false;
                localFeedCount = 0;
                selectedFeedFilter = filterCategories;
                OustDataHandler.getInstance().setAllFeedsLoaded(false);
                fetchUserFeeds("", 1);
                return;
            }
            try {
                filtersUrl = new StringBuilder();
                for (int i = 0; i < selectedFeedFilter.size(); i++) {
                    try {
                        filtersUrl = filtersUrl.append("filterType=").append(selectedFeedFilter.get(i).getCategoryType()).append("&");
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }
                String url = filtersUrl.toString();
                if (url.endsWith("&")) {
                    url = url.substring(0, url.length() - 1) + "";
                }
                Log.d("checkFeeds", "3");
                try {
                    new_feeds.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.event_animmoveup));
                    new_feeds.setVisibility(GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                    new_feeds.setVisibility(GONE);
                }
                feedList = new ArrayList<>();
                localFeedCount = 0;
                currentPage = 1;
                if (feedsRecyclerViewAdapter != null) {
                    feedsRecyclerViewAdapter.clear();
                }
                isLoading = true;
                progressBar.setVisibility(VISIBLE);
                OustStaticVariableHandling.getInstance().setFeeds(new ArrayList<>());
                loadFeedData = false;
                userFeedsApiCall("&" + url, 0, "");
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
            try {
                if (selectedFeedFilter != null && selectedFeedFilter.size() > 0) {
                    if (selectedFeedFilter.get(0) != null && selectedFilter.get(0).categoryName.equalsIgnoreCase("all")) {
                        ArrayList<UserFeedFilters.FeedFilter> tempSelectedFeedFilter = new ArrayList<>();
                        tempSelectedFeedFilter.add(selectedFilter.get(0));
                        selectedFeedFilter.clear();
                        selectedFeedFilter = new ArrayList<>(tempSelectedFeedFilter);
                    }
                }
            } catch (Exception e) {
                selectedFeedFilter = new ArrayList<>(selectedFilter);
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
            addSelectedFilterTags();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void addSelectedFilterTags() {
        try {
            cgFilter.removeAllViews();
            for (UserFeedFilters.FeedFilter filterCategory : selectedFeedFilter) {
                cgFilter.addView(getChip(filterCategory));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private Chip getChip(UserFeedFilters.FeedFilter filterCategory) {
        Chip chip = new Chip(cgFilter.getContext());
        chip.setText(filterCategory.getCategoryName());
        chip.setClickable(false);
        chip.setCheckable(false);
        chip.setCloseIconVisible(true);
        chip.setTextAppearance(R.style.TextAppearance_Oust_Chip);

        chip.setCloseIcon(getResources().getDrawable(R.drawable.ic_close_circle));
        chip.setCloseIconTint(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        chip.setChipBackgroundColor(ColorStateList.valueOf(OustResourceUtils.getColors()));
        chip.setTag(filterCategory);
        chip.setOnCloseIconClickListener(v -> {
            UserFeedFilters.FeedFilter filterCategoryRemove = (UserFeedFilters.FeedFilter) v.getTag();
            selectedFeedFilter.remove(filterCategoryRemove);
            filterFeed(selectedFeedFilter);
            cgFilter.removeView(chip);
        });
        return chip;
    }

    @Override
    public void getNextData(String type) {

    }

    private void loadNextFeedData() {
        try {
            if (selectedFeedFilter.size() <= 0 || !isFilterApplicable) {
                loadFeedData();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onSearch(String query) {
        try {
            if (query != null) {
                try {
                    new_feeds.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.event_animmoveup));
                    new_feeds.setVisibility(GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                    new_feeds.setVisibility(GONE);
                }
                feedList = new ArrayList<>();
                localFeedCount = 0;
                currentPage = 1;
                if (feedsRecyclerViewAdapter != null) {
                    feedsRecyclerViewAdapter.clear();
                }
                isLoading = true;
                progressBar.setVisibility(VISIBLE);
                OustStaticVariableHandling.getInstance().setFeeds(new ArrayList<>());
                loadFeedData = false;
                if (SystemClock.elapsedRealtime() - mLastTimeClicked < 3000) {
                    return;
                }
                userFeedsApiCall("", currentPage, query);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void loadFeedData() {
        try {
            progressBar.setVisibility(GONE);
            feedsRecyclerViewAdapter.notifyFeedChange(feedList);
            isLoading = false;
            /*if (localFeedCount + 15 <= this.feedList.size()) {
                localFeedCount += 15;
                OustPreferences.saveAppInstallVariable("feedScroll", true);
                Log.d(TAG, "checkingPagination=>3: " + isLoading + localFeedCount);
            } else {
                localFeedCount = this.feedList.size();
                OustDataHandler.getInstance().setAllFeedsLoaded(true);
                Log.d(TAG, "checkingPagination=>4: " + isLoading + localFeedCount);
            }
            int startCount = (int) (((Math.ceil((localFeedCount * 1.0) / 15)) - 1) * 15);
            ArrayList<DTOUserFeeds.FeedList> loadedFeedList = new ArrayList<>(feedList.subList(startCount, localFeedCount));*/
            /*if (feedsRecyclerViewAdapter == null) {
                Log.d(TAG, "checkingPagination=>5: " + isLoading + localFeedCount);
//                Log.e(TAG, "feedscalling4: " + loadedFeedList);
//                feedsRecyclerViewAdapter = new FeedsRecyclerViewAdapter();
                try {
                    //TODO to stop the blinking the recyclerview //Start
//                ((SimpleItemAnimator) Objects.requireNonNull(rvFeeds.getItemAnimator())).setSupportsChangeAnimations(false);
                    rvFeeds.setItemAnimator(null);   //TODO END
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e(TAG, "feedscalling5: ");
            *//*feedHeader.setFeedType("Header");
            feedList.add(feedHeader);*//*
//            mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
//                rvFeeds.setLayoutManager(mLayoutManager);
//                feedsRecyclerViewAdapter.setFeedsRecyclerAdapter( activeUser, initGlide(), getActivity(), this, isMultipleCpl, rvFeeds);
                Objects.requireNonNull(requireActivity()).runOnUiThread(() -> {
//                    rvFeeds.setData(feedList);
//                    rvFeeds.setAdapter(feedsRecyclerViewAdapter);
                    feedsRecyclerViewAdapter.notifyFeedChange(feedList);
//                ViewCompat.setNestedScrollingEnabled(rvFeeds, false);
//                pbFeeds.setVisibility(GONE);
                    branding_mani_layout.setVisibility(GONE);
                });
            } else {
//                rvFeeds.addData(feedList);
                feedsRecyclerViewAdapter.notifyFeedChange(feedList);
                Log.d(TAG, "checkingPagination=>6: " + isLoading + localFeedCount + feedList.size());
            }*/
//            feedsRecyclerViewAdapter.notifyDataSetChanged();
//            feedList.get(0).setLike(true);
//            feedAdapter.updateFeedList(feedList);
            /*new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // TODO firebase response
                    feedList.get(0).setLike(true);
                    feedAdapter.updateFeedList(feedList);
                }
            }, 2000);*/
            no_data_layout.setVisibility(GONE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private RequestManager initGlide() {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.no_image)
                .error(R.drawable.no_image);

        return Glide.with(this)
                .setDefaultRequestOptions(options);
    }

    public void hidePbFeedsLoader() {
        try {
            pbFeeds.setVisibility(GONE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void hideFilter() {
        try {
            filter.setVisibility(INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setFilter(FragmentManager fragmentManager, ArrayList<UserFeedFilters.FeedFilter> filters) {
        try {
            this.fragmentManager = fragmentManager;
            this.filterCategories = filters;
            this.selectedFeedFilter = filters;

            if (this.filterCategories == null) {
                filter.setVisibility(INVISIBLE);
            } else {
                if (this.feedList != null && this.feedList.size() != 0) {
                    filter.setVisibility(View.VISIBLE);
                } else {
                    filter.setVisibility(INVISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onFeedClick(long newFeedId, int cplId) {
        try {
            CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(OustSdkApplication.getContext());
            HashMap<String, Object> eventUpdate = OustSdkTools.getCleverTapEventData();
            eventUpdate.put("FeedId", newFeedId);
            eventUpdate.put("ClickedOnFeed", true);
            Log.d(TAG, "CleverTap instance: " + eventUpdate);
            if (clevertapDefaultInstance != null) {
                clevertapDefaultInstance.pushEvent("Feed_Click", eventUpdate);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        try {
            ClickedFeedData clickedFeedData = new ClickedFeedData();
            clickedFeedData.setFeedId((int) newFeedId);
            clickedFeedData.setCplId(cplId);
            clickedFeedData.setClickedTimestamp("" + System.currentTimeMillis());
            String clickFeedList = OustPreferences.get("clickFeedList");
            ArrayList<ClickedFeedData> clickedFeedDataArrayList = OustSdkTools.getClickedFeedList(clickFeedList);
            if (clickedFeedDataArrayList == null) {
                clickedFeedDataArrayList = new ArrayList<>();
            }
            clickedFeedDataArrayList.add(clickedFeedData);

            OustPreferences.save("clickFeedList", new Gson().toJson(clickedFeedDataArrayList));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    @Override
    public void onFeedViewed(long newFeedId) {
        try {
            CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(OustSdkApplication.getContext());
            HashMap<String, Object> eventUpdate = OustSdkTools.getCleverTapEventData();
            eventUpdate.put("FeedId", newFeedId);
            Log.d(TAG, "CleverTap instance: " + eventUpdate);
            if (clevertapDefaultInstance != null) {
                clevertapDefaultInstance.pushEvent("Feed_View", eventUpdate);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        try {
            ViewedFeedData viewedFeedData = new ViewedFeedData();
            viewedFeedData.setFeedId((int) newFeedId);
            long milliSec = System.currentTimeMillis();
            viewedFeedData.setViewedTimestamp("" + milliSec);
            String viewFeedList = OustPreferences.get("viewFeedLIst");
            try {
                HashMap<String, ViewedFeedData> viewedFeedDataMap = OustSdkTools.getViewedFeed(viewFeedList);
                if (viewedFeedDataMap == null) {
                    viewedFeedDataMap = new HashMap<>();
                } else if (viewedFeedDataMap.isEmpty()) {
                    viewedFeedDataMap = new HashMap<>();
                }
                String objectKey = newFeedId + new SimpleDateFormat("MMMddyyyyHH:mm", Locale.getDefault()).format(new Date(milliSec));
                Log.d("FeedAnalytics", "Feed key " + objectKey);
                viewedFeedDataMap.put(objectKey, viewedFeedData);
                OustPreferences.save("viewFeedLIst", new Gson().toJson(viewedFeedDataMap));
            } catch (JSONException e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    @Override
    public void onFeedViewedOnScroll(int position) {
        if (feedsRecyclerViewAdapter != null) {
            feedsRecyclerViewAdapter.onFeedViewedInScroll(position);
        }
    }

    @Override
    public void cplFeedClicked(long cplId) {
    }

    @Override
    public void checkFeedData(final long cplId, final long feedId) {
    }

    @Override
    public void onRemoveVideo(long newFeedId) {
    }

    @Override
    public void onPlayVideo(int position, int lastPos) {
       /* try {
            Objects.requireNonNull(requireActivity()).runOnUiThread(() -> {
                if (feedAdapter != null) {
                    feedAdapter.playVideo(position, lastPos);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }*/
    }

    @Override
    public void refreshViews() {
        try {
            if (viewTracker != null) {
                viewTracker.startNewFeed();
            }
            OustPreferences.saveAppInstallVariable("feedScroll", true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onFeedRewardCoinsUpdate(DTONewFeed newFeed) {
        Log.d(TAG, "onFeedRewardCoinsUpdate: ");
        try {
            if (newFeed != null && newFeed.getFeedId() > 0 && newFeed.getFeedCoins() > 0 && !newFeed.isFeedCoinsAdded()) {
                Intent intent = new Intent(OustSdkApplication.getContext(), FeedUpdatingServices.class);
                intent.putExtra("feedId", (int) newFeed.getFeedId());
                intent.putExtra("feedCoins", newFeed.getFeedCoins());
                intent.putExtra("feedCoinsUpdate", true);
                requireActivity().startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void onScrollViewTopPosition() {
        try {
            if (closeSearchIcon != null) {
                closeSearchIcon.closeSearchIcon();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void removeFilterView() {
        try {
            isFilterApplicable = false;
            setContentComponent();
            callFeedsRefresh();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void analyzeAndAddViewData(int firstPosition, int lastPosition) {
        Log.d("onFeedViewedInScroll", "checking the con");
        ArrayList<Integer> fullyVisibleItems = new ArrayList<>();
        try {
            if (rvFeeds != null) {

                // Analyze all the views
                Rect rvRect = new Rect();
                rvFeeds.getGlobalVisibleRect(rvRect);

                for (int i = firstPosition; i <= lastPosition; i++) {
                    Rect rowRect = new Rect();
                    try {
                        Objects.requireNonNull(Objects.requireNonNull(rvFeeds.getLayoutManager()).findViewByPosition(i)).getGlobalVisibleRect(rowRect);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    int percentFirst;
                    int visibleHeightFirst;
                    if (rowRect.bottom >= rvRect.bottom) {
                        visibleHeightFirst = rvRect.bottom - rowRect.top;
                    } else {
                        visibleHeightFirst = rowRect.bottom - rvRect.top;
                    }
                    percentFirst = (visibleHeightFirst * 100) / Objects.requireNonNull(rvFeeds.getLayoutManager().findViewByPosition(i)).getHeight();

                    if (percentFirst > 100)
                        percentFirst = 100;

                    if (percentFirst >= 90) {
                        if (OustStaticVariableHandling.getInstance().getFeeds() != null && OustStaticVariableHandling.getInstance().getFeeds().get(i) != null) {
                            Log.d("FeedAnalytics-->>", "analyzeAndAddViewData: " + i + " --- FeedId:" + OustStaticVariableHandling.getInstance().getFeeds().get(i).getFeedId() + " --- FeedViewed:" + OustStaticVariableHandling.getInstance().getFeeds().get(i).isFeedViewed());
//                            if (!OustStaticVariableHandling.getInstance().getFeeds().get(i).isFeedViewed()) {
                            onFeedViewed(OustStaticVariableHandling.getInstance().getFeeds().get(i).getFeedId());
//                            }
                        } else {
                            // Log.e("ViewTracker", "view added - " + i);
                            onFeedViewedOnScroll(i);
                        }
                    }

                    if (percentFirst >= 75) {
                        fullyVisibleItems.add(i);
                    }
                }
                //TODO start code this is old code to play the video
                /*try {
                    if (fullyVisibleItems.size() != 0) {
                        final float y = rvFeeds.getChildAt(fullyVisibleItems.get(0)).getY();
                        Log.d(TAG, "position of video:" + fullyVisibleItems.get(0) + "pos is " + y);
                        onPlayVideo(fullyVisibleItems.get(0));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                // TODO END code
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void feedModifyItems(long position, long feedComment, long feedShareCount, boolean isFeedChange, long feedLikeCount, boolean isLikeClicked, boolean isClicked) {
        try {
            if (feedsRecyclerViewAdapter != null) {
                //Log.e(TAG, "feedModifyItems: position -> " + position + " feedComment-> " + feedComment + " feedShareCount-> " + feedShareCount);
                if (position != 0 && isFeedChange) {
                    feedsRecyclerViewAdapter.modifyItem(position, feedComment, feedShareCount, feedLikeCount, isLikeClicked, isClicked);
                }
                feedsRecyclerViewAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void feedRemoveItem(long position, boolean isFeedChange, boolean isFeedRemove) {
        try {
            if (feedsRecyclerViewAdapter != null) {
                if (position != 0 && isFeedRemove && isFeedChange) {
//                    feedsRecyclerViewAdapter.removeItem(position);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        try {
            isFilterApplicable = false;
            viewTracker = null;
            if (!hidden) {
                getUserEarnedCoins();
                removeFilterView();
                onScrollViewTopPosition();
            } else {
                try {
                    if (runnable != null) {
                        handler.removeCallbacks(runnable);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            }
            if (landingActivity != null) {
                landingActivity.startOustServices();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getUserEarnedCoins() {
        try {
            if (OustSdkTools.checkInternetStatus()) {
                String earnedCoinsUrl = OustSdkApplication.getContext().getResources().getString(R.string.user_earned_coins);
                earnedCoinsUrl = earnedCoinsUrl.replace("{userId}", activeUser.getStudentid());
                earnedCoinsUrl = HttpManager.getAbsoluteUrl(earnedCoinsUrl);
                Log.d(TAG, "getUserEarnedCoins: " + earnedCoinsUrl);

                ApiCallUtils.doNetworkCall(Request.Method.GET, earnedCoinsUrl, OustSdkTools.getRequestObject(""),
                        new ApiCallUtils.NetworkCallback() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    UserEarnedCoinsResponse userEarnedCoinsResponse;
                                    if (response.getBoolean("success")) {
                                        Gson gson = new Gson();
                                        userEarnedCoinsResponse = gson.fromJson(response.toString(), UserEarnedCoinsResponse.class);
                                        OustPreferences.save("earnedUserCoins", String.valueOf(userEarnedCoinsResponse.getScore()));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    OustSdkTools.sendSentryException(e);
                                }
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });
            } else {
                OustSdkTools.showToast(OustSdkApplication.getContext().getString(R.string.no_internet_connection));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}