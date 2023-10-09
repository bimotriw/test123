package com.oustme.oustsdk.layoutFour.components.myTask.fragment.playList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.R;
import com.oustme.oustsdk.catalogue_ui.model.CatalogueModuleUpdate;
import com.oustme.oustsdk.customviews.LayoutSwitcher;
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

public class CplModuleFragment extends Fragment {

    NestedScrollView nested_view;
    LinearLayout data_layout;
    TextView pending_course_module;
    ImageView iv_sort;
    LayoutSwitcher layout_switcher;
    RecyclerView cpl_list_rv;
    TextView no_module;

    RecyclerView.LayoutManager mCplLayoutManager;
    FragmentManager fragmentManager;
    ArrayList<FilterForAll> filterCategories;
    ArrayList<FilterForAll> filterCategories1;
    ArrayList<FilterForAll> selectedCplFilter = new ArrayList<>();

    ArrayList<CommonLandingData> cplList;
    ArrayList<CommonLandingData> cplFilterList;
    Map<String, ArrayList<CommonLandingData>> allCplSortedMap;
    ArrayList<String> keyList = new ArrayList<>();
    int localCplCount = 0;

    TaskHashMapCplAdapter cplAdapter;
    int type = 1;
    private boolean isAscending = false;
    SwipeRefreshHandling swipeRefreshHandling;

    public CplModuleFragment(ArrayList<CommonLandingData> cplList, SwipeRefreshHandling swipeRefreshHandling) {
        this.cplList = cplList;
        this.swipeRefreshHandling = swipeRefreshHandling;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cpl_module, container, false);
        nested_view = view.findViewById(R.id.cpl_nested_view);
        data_layout = view.findViewById(R.id.cpl_data_layout);
        pending_course_module = view.findViewById(R.id.cpl_pending_course_module);
        iv_sort = view.findViewById(R.id.cpl_iv_sort);
        layout_switcher = view.findViewById(R.id.cpl_layout_switcher);
        cpl_list_rv = view.findViewById(R.id.cpl_list_rv);
        cpl_list_rv.setNestedScrollingEnabled(false);
        no_module = view.findViewById(R.id.cpl_no_module);

        if (cplList != null && cplList.size() != 0) {
            String pending_course = getResources().getString(R.string.you_have) + " " + cplList.size() + " " + getResources().getString(R.string.cpl_pending);
            if (cplList.size() > 1) {
                pending_course = getResources().getString(R.string.you_have) + " " + cplList.size() + " " + getResources().getString(R.string.cpls_pending);
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

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) cpl_list_rv.getLayoutManager();
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
                        if (!OustPreferences.getAppInstallVariable("allCplLoad") && OustPreferences.getAppInstallVariable("cplScroll")) {
                            try {
                                OustPreferences.saveAppInstallVariable("cplScroll", false);
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

        if (filterCategories != null && filterCategories.size() != 0 && filterCategories1.size() != 0) {
            selectedCplFilter = new ArrayList<>();
            selectedCplFilter.addAll(filterCategories);
            selectedCplFilter.addAll(filterCategories1);
        }
    }


    private void setInitialDataSet() {
        try {
            cplAdapter = null;
            localCplCount = 0;
            ArrayList<CommonLandingData> cplSetList = cplList;
            CommonLandingFilter commonLandingFilter;
            HashMap<String, ArrayList<CommonLandingData>> courseHashMap;

            if (cplFilterList != null && cplFilterList.size() != 0) {
                cplSetList = cplFilterList;
            }

            commonLandingFilter = new CommonLandingFilter();
            courseHashMap = commonLandingFilter.getCplModulesHashMap(cplSetList);
            if (!isAscending) {
                OustResourceUtils.setDefaultDrawableColor(iv_sort.getDrawable(), getResources().getColor(R.color.unselected_text));
                allCplSortedMap = new TreeMap<>(Collections.reverseOrder());
            } else {
                OustResourceUtils.setDefaultDrawableColor(iv_sort);
                allCplSortedMap = new TreeMap<>();
            }

            allCplSortedMap.putAll(courseHashMap);
            keyList = new ArrayList<>(allCplSortedMap.keySet());
            if (allCplSortedMap != null && allCplSortedMap.size() != 0) {
                loadData();
            } else {
                no_module.setVisibility(View.VISIBLE);
                cpl_list_rv.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void layoutSwitcher() {
        try {
            if (cplAdapter != null) {
                cplAdapter.setType(type);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void loadData() {
        try {
            if (localCplCount + 5 < allCplSortedMap.size()) {
                localCplCount += 5;
                OustPreferences.saveAppInstallVariable("cplScroll", true);
            } else {
                localCplCount = allCplSortedMap.size();
                OustPreferences.saveAppInstallVariable("allCplLoad", true);
            }
            keyList = new ArrayList<>(allCplSortedMap.keySet());
            int startCount = (int) (((Math.ceil((localCplCount * 1.0) / 5)) - 1) * 5);

            ArrayList<String> loadedKeyList = new ArrayList<>(keyList.subList(startCount, localCplCount));
            Map<String, ArrayList<CommonLandingData>> loadedModuleMap = new HashMap<>();
            for (String key : loadedKeyList) {
                loadedModuleMap.put(key, allCplSortedMap.get(key));
            }

            if (mCplLayoutManager == null) {
                mCplLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            }

            if (cplAdapter == null) {
                cplAdapter = new TaskHashMapCplAdapter();
                if (cpl_list_rv.getLayoutManager() == null) {
                    cpl_list_rv.setLayoutManager(mCplLayoutManager);
                }
                cplAdapter.setTaskRecyclerAdapter(loadedModuleMap, getActivity(), type);
                cpl_list_rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                cplAdapter.setTaskModuleHashMap(allCplSortedMap);
                Objects.requireNonNull(requireActivity()).runOnUiThread(() -> cpl_list_rv.setAdapter(cplAdapter));
            } else {
                cplAdapter.notifyTaskHashMap(loadedModuleMap);
            }

            no_module.setVisibility(View.GONE);
            data_layout.setVisibility(View.VISIBLE);
            cpl_list_rv.setVisibility(View.VISIBLE);

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
                if (catalogueModuleUpdate.getType() != null && !catalogueModuleUpdate.getType().contains("CPL")) {
                    if (cplAdapter != null) {
                        if (cplAdapter.getList() != null && cplAdapter.getList().size() != 0) {
                            if (catalogueModuleUpdate.getParentPosition() < cplAdapter.getList().size()) {
                                cplAdapter.modifyItem(catalogueModuleUpdate.getParentPosition(), catalogueModuleUpdate);
                                cplAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
            OustStaticVariableHandling.getInstance().setCatalogueModuleUpdate(null);
        }
    }

    public void setCplData(String query) {
        try {
            if (query != null) {
                if (cplAdapter != null) {
                    if (cplList != null) {
                        cplAdapter.getFilter().filter(query);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
