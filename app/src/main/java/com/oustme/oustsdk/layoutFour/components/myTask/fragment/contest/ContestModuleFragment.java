package com.oustme.oustsdk.layoutFour.components.myTask.fragment.contest;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.catalogue_ui.model.CatalogueModuleUpdate;
import com.oustme.oustsdk.customviews.LayoutSwitcher;
import com.oustme.oustsdk.filter.FilterDialogFragment;
import com.oustme.oustsdk.filter.FilterForAll;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.layoutFour.interfaces.SwipeRefreshHandling;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.filters.CommonLandingFilter;
import com.oustme.oustsdk.utils.OustResourceUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class ContestModuleFragment extends Fragment {
    NestedScrollView nested_view;
    LinearLayout data_layout;
    TextView pending_course_module;
    ImageView iv_sort;
    LayoutSwitcher layout_switcher;
    RecyclerView contest_list_rv;
    TextView no_module;

    RecyclerView.LayoutManager mContestLayoutManager;
    FragmentManager fragmentManager;
    FilterDialogFragment filterDialog;
    ArrayList<FilterForAll> filterCategories;
    ArrayList<FilterForAll> filterCategories1;
    ArrayList<FilterForAll> selectedContestFilter = new ArrayList<>();

    ArrayList<CommonLandingData> contestList = new ArrayList<>();
    ArrayList<CommonLandingData> contestFilterList;
    Map<String, ArrayList<CommonLandingData>> allCotestSortedMap;
    ArrayList<String> keyList = new ArrayList<>();
    int localContestCount = 0;

    TaskHashMapContestAdapter contestAdapter;
    int type = 1;
    private boolean isAscending = false;
    SwipeRefreshHandling swipeRefreshHandling;

    public ContestModuleFragment() {

    }

    public ContestModuleFragment(ArrayList<CommonLandingData> contestList, SwipeRefreshHandling swipeRefreshHandling) {
        this.contestList = contestList;
        this.swipeRefreshHandling = swipeRefreshHandling;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_contest_module, container, false);
        nested_view = view.findViewById(R.id.contest_nested_view);
        data_layout = view.findViewById(R.id.contest_data_layout);
        pending_course_module = view.findViewById(R.id.contest_pending_course_module);
        iv_sort = view.findViewById(R.id.contest_iv_sort);
        layout_switcher = view.findViewById(R.id.contest_layout_switcher);
        contest_list_rv = view.findViewById(R.id.contest_list_rv);
        contest_list_rv.setNestedScrollingEnabled(false);
        no_module = view.findViewById(R.id.contest_no_module);

        if (contestList != null && contestList.size() != 0) {
            String pending_course = getResources().getString(R.string.you_have) + " " + contestList.size() + " " + getResources().getString(R.string.contest_pending);
            if (contestList.size() > 1) {
                pending_course = getResources().getString(R.string.you_have) + " " + contestList.size() + " " + getResources().getString(R.string.contests_pending);
            }
            setFilterData();
            pending_course_module.setText(pending_course);
            setFilter(getParentFragmentManager());
            setInitialDataSet();
        } else {
            no_module.setVisibility(View.VISIBLE);
            data_layout.setVisibility(View.GONE);
        }

        iv_sort.setOnClickListener(v -> {
            isAscending = !isAscending;
            setInitialDataSet();
        });

        layout_switcher.setCallback(Type -> {
            try {
                type = Type;
                layoutSwitcher();
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        });

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) contest_list_rv.getLayoutManager();
        nested_view.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            OustPreferences.saveAppInstallVariable("learningSwipe", false);
            if (swipeRefreshHandling != null) {
                swipeRefreshHandling.swipeRefresh(false);
            }
            if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                if (linearLayoutManager != null) {
                    int totalItemCount = linearLayoutManager.getItemCount();
                    int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (totalItemCount <= (lastVisibleItem + 10)) {
                        if (!OustPreferences.getAppInstallVariable("allContestLoad") && OustPreferences.getAppInstallVariable("contestScroll")) {
                            try {
                                OustPreferences.saveAppInstallVariable("contestScroll", false);
                                loadData();
                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }
                        }
                    }
                }
            } else if (scrollY == 0) {
                if (swipeRefreshHandling != null) {
                    swipeRefreshHandling.swipeRefresh(true);
                }
                OustPreferences.saveAppInstallVariable("learningSwipe", true);
            }
        });

        return view;
    }

    public void setFilter(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    private void setFilterData() {

        if (filterCategories == null) {
            filterCategories = new ArrayList<>();
            filterCategories.add(new FilterForAll(getResources().getString(R.string.all) + "", 0));
            filterCategories.add(new FilterForAll(getResources().getString(R.string.courses_text) + "", 1));
            filterCategories.add(new FilterForAll(getResources().getString(R.string.webinar_text) + "", 2));
            filterCategories.add(new FilterForAll(getResources().getString(R.string.classroom_text) + "", 3));
        }
        if (filterCategories1 == null) {
            filterCategories1 = new ArrayList<>();
            filterCategories1.add(new FilterForAll(getResources().getString(R.string.all) + "", 5));
            filterCategories1.add(new FilterForAll("Not Started", 6));
            filterCategories1.add(new FilterForAll("In Progress", 7));
        }

        if (filterCategories != null && filterCategories.size() != 0 && filterCategories1 != null && filterCategories1.size() != 0) {
            selectedContestFilter = new ArrayList<>();
            selectedContestFilter.addAll(filterCategories);
            selectedContestFilter.addAll(filterCategories1);
        }
    }

    private void setInitialDataSet() {
        try {
            contestAdapter = null;
            localContestCount = 0;
            ArrayList<CommonLandingData> contestSetList = contestList;
            CommonLandingFilter commonLandingFilter;
            HashMap<String, ArrayList<CommonLandingData>> contestHashMap;

            if (contestFilterList != null && contestFilterList.size() != 0) {
                contestSetList = contestFilterList;
            }

            commonLandingFilter = new CommonLandingFilter();
            Collections.sort(contestSetList, CommonLandingData.sortByDate);
            contestHashMap = commonLandingFilter.getContestModulesHashMap(contestSetList);
            if (!isAscending) {
                OustResourceUtils.setDefaultDrawableColor(iv_sort.getDrawable(), getResources().getColor(R.color.unselected_text));
                allCotestSortedMap = new TreeMap<>(Collections.reverseOrder());
            } else {
                OustResourceUtils.setDefaultDrawableColor(iv_sort);
                allCotestSortedMap = new TreeMap<>();
            }

            allCotestSortedMap.putAll(contestHashMap);
            keyList = new ArrayList<>(allCotestSortedMap.keySet());
            if (allCotestSortedMap != null && allCotestSortedMap.size() != 0) {
                loadData();
            } else {
                no_module.setVisibility(View.VISIBLE);
                contest_list_rv.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void layoutSwitcher() {
        try {
            if (contestAdapter != null) {
                contestAdapter.setType(type);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void loadData() {
        try {
            if (localContestCount + 5 < allCotestSortedMap.size()) {
                localContestCount += 5;
                OustPreferences.saveAppInstallVariable("contestScroll", true);
            } else {
                localContestCount = allCotestSortedMap.size();
                OustPreferences.saveAppInstallVariable("allContestLoad", true);
            }
            keyList = new ArrayList<>(allCotestSortedMap.keySet());
            int startCount = (int) (((Math.ceil((localContestCount * 1.0) / 5)) - 1) * 5);

            ArrayList<String> loadedKeyList = new ArrayList<>(keyList.subList(startCount, localContestCount));
            Map<String, ArrayList<CommonLandingData>> loadedModuleMap = new HashMap<>();
            for (String key : loadedKeyList) {
                loadedModuleMap.put(key, allCotestSortedMap.get(key));
            }

            if (mContestLayoutManager == null) {
                mContestLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            }

            if (contestAdapter == null) {
                contestAdapter = new TaskHashMapContestAdapter();
                if (contest_list_rv.getLayoutManager() == null) {
                    contest_list_rv.setLayoutManager(mContestLayoutManager);
                }
                contestAdapter.setTaskRecyclerAdapter(loadedModuleMap, getActivity(), type);
                contest_list_rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                contestAdapter.setTaskModuleHashMap(allCotestSortedMap);
                Objects.requireNonNull(requireActivity()).runOnUiThread(() -> contest_list_rv.setAdapter(contestAdapter));
            } else {
                contestAdapter.notifyTaskHashMap(loadedModuleMap);
            }

            no_module.setVisibility(View.GONE);
            data_layout.setVisibility(View.VISIBLE);
            contest_list_rv.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate() != null) {
            CatalogueModuleUpdate catalogueModuleUpdate = OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate();
            if (catalogueModuleUpdate.isUpdated()) {
                if (catalogueModuleUpdate.getType() != null && !catalogueModuleUpdate.getType().contains("FFF_CONTEXT")) {
                    if (contestAdapter != null) {
                        if (contestAdapter.getList() != null && contestAdapter.getList().size() != 0) {
                            if (catalogueModuleUpdate.getParentPosition() < contestAdapter.getList().size()) {
                                contestAdapter.modifyItem(catalogueModuleUpdate.getParentPosition(), catalogueModuleUpdate);
                                contestAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
            OustStaticVariableHandling.getInstance().setCatalogueModuleUpdate(null);
        }
    }

    public void setContestData(String query) {
        try {
            if (query != null) {
                if (contestAdapter != null) {
                    if (contestList != null) {
                        contestAdapter.getFilter().filter(query);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
