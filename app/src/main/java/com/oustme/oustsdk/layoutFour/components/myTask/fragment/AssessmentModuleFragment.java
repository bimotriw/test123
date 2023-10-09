package com.oustme.oustsdk.layoutFour.components.myTask.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.R;
import com.oustme.oustsdk.catalogue_ui.model.CatalogueModuleUpdate;
import com.oustme.oustsdk.customviews.LayoutSwitcher;
import com.oustme.oustsdk.filter.FilterDialogFragment;
import com.oustme.oustsdk.filter.FilterForAll;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.layoutFour.interfaces.SwipeRefreshHandling;
import com.oustme.oustsdk.my_tasks.adapter.TaskHashMapModuleAdapter;
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

public class AssessmentModuleFragment extends Fragment {

    NestedScrollView nested_view;
    LinearLayout data_layout;
    TextView pending_assessment_module;
    ImageView iv_sort;
    ImageView iv_filter;
    LayoutSwitcher layout_switcher;
    RecyclerView assessment_list_rv;
    View no_data_layout;
    ImageView no_image;
    TextView no_data_content;

    RecyclerView.LayoutManager mAssessmentLayoutManager;
    FragmentManager fragmentManager;
    FilterDialogFragment filterDialog;
    ArrayList<FilterForAll> filterCategories;
    ArrayList<FilterForAll> filterCategories1;
    ArrayList<FilterForAll> selectedFeedFilter = new ArrayList<>();

    ArrayList<CommonLandingData> assessmentList = new ArrayList<>();
    ArrayList<CommonLandingData> tempAssessmentList = new ArrayList<>();
    ArrayList<CommonLandingData> assessmentFilterList;
    Map<String, ArrayList<CommonLandingData>> allAssessmentSortedMap;
    ArrayList<String> keyList = new ArrayList<>();
    int localAssessmentCount = 0;

    TaskHashMapModuleAdapter adapter;
    int type = 1;
    private boolean isAscending = false;

    SwipeRefreshHandling swipeRefreshHandling;

    public AssessmentModuleFragment() {

    }

    public AssessmentModuleFragment(ArrayList<CommonLandingData> assessmentList, SwipeRefreshHandling swipeRefreshHandling) {
        this.assessmentList = assessmentList;
        this.swipeRefreshHandling = swipeRefreshHandling;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_assessment_module, container, false);
        nested_view = view.findViewById(R.id.nested_view);
        data_layout = view.findViewById(R.id.data_layout);
        pending_assessment_module = view.findViewById(R.id.pending_assessment_module);
        iv_sort = view.findViewById(R.id.iv_sort);
        iv_filter = view.findViewById(R.id.iv_filter);
        layout_switcher = view.findViewById(R.id.layout_switcher);
        assessment_list_rv = view.findViewById(R.id.assessment_list_rv);
        assessment_list_rv.setNestedScrollingEnabled(false);
        no_data_layout = view.findViewById(R.id.no_assessment_data_layout);
        no_image = no_data_layout.findViewById(R.id.no_image);
        no_data_content = no_data_layout.findViewById(R.id.no_data_content);

        if (assessmentList != null && assessmentList.size() != 0) {
            setFilterData();
            showModuleCount(assessmentList, false);
            setFilter(getParentFragmentManager());
            setInitialDataSet(false);
        } else {
            no_data_layout.setVisibility(View.VISIBLE);
            no_image.setImageDrawable(getResources().getDrawable(R.drawable.no_pending_completed));
            no_data_content.setText(getResources().getString(R.string.no_pending_module_content_completed));
            data_layout.setVisibility(View.GONE);
        }

        iv_sort.setOnClickListener(v -> {
            try {
                isAscending = !isAscending;
                if (tempAssessmentList != null && tempAssessmentList.size() > 0) {
                    setInitialDataSet(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        });

        layout_switcher.setCallback(Type -> {
            type = Type;
            layoutSwitcher();
        });

        iv_filter.setOnClickListener(v -> openFilterDialogue());

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) assessment_list_rv.getLayoutManager();
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
                        if (!OustPreferences.getAppInstallVariable("allAssessmentLoad") && OustPreferences.getAppInstallVariable("assessmentScroll")) {
                            try {
                                OustPreferences.saveAppInstallVariable("assessmentScroll", false);
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

    private void showModuleCount(ArrayList<CommonLandingData> assessmentList, boolean comingFromFilterData) {
        try {
            try {
                if (assessmentList != null && assessmentList.size() != 0) {
                    String pending_assessment = getResources().getString(R.string.you_have) + " " + assessmentList.size() + " " + getResources().getString(R.string.assessment_pending);
                    if (assessmentList.size() > 1) {
                        pending_assessment = getResources().getString(R.string.you_have) + " " + assessmentList.size() + " " + getResources().getString(R.string.assessments_pending);
                    }
                    pending_assessment_module.setText(pending_assessment);
                } else {
                    if (comingFromFilterData) {
                        String pending_course = getResources().getString(R.string.you_have) + " " + getResources().getString(R.string.no_mobules_pending);
                        pending_assessment_module.setText(pending_course);
                        no_data_layout.setVisibility(View.VISIBLE);
                        no_image.setImageDrawable(getResources().getDrawable(R.drawable.no_pending_completed));
                        no_data_content.setText(getResources().getString(R.string.no_pending_module_content_completed));
                    } else {
                        no_data_layout.setVisibility(View.VISIBLE);
                        no_image.setImageDrawable(getResources().getDrawable(R.drawable.no_pending_completed));
                        no_data_content.setText(getResources().getString(R.string.no_pending_module_content_completed));
                        data_layout.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setFilter(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    private void setFilterData() {

        if (filterCategories == null) {
            filterCategories = new ArrayList<>();
        }
        if (filterCategories1 == null) {
            filterCategories1 = new ArrayList<>();
            filterCategories1.add(new FilterForAll(getResources().getString(R.string.all) + "", 5));
            filterCategories1.add(new FilterForAll("Not Started", 6));
            filterCategories1.add(new FilterForAll("In Progress", 7));
        }

        if (filterCategories1 != null && filterCategories1.size() != 0) {
            selectedFeedFilter = new ArrayList<>();
            selectedFeedFilter.addAll(filterCategories);
            selectedFeedFilter.addAll(filterCategories1);
        }

    }

    private void openFilterDialogue() {
        try {
            filterDialog = FilterDialogFragment.newInstance(filterCategories, filterCategories1, selectedFeedFilter, selectedFilter -> {
                selectedFeedFilter = selectedFilter;
                if (selectedFilter == null || selectedFilter.size() == 0) {
                    assessmentFilterList = null;
                    OustResourceUtils.setDefaultDrawableColor(iv_filter.getDrawable(), getResources().getColor(R.color.unselected_text));
                    setInitialDataSet(false);
                } else {
                    ArrayList<Integer> filterCategoryList = new ArrayList<>();
                    for (FilterForAll filter : selectedFilter) {
                        filterCategoryList.add(filter.getCategoryType());
                    }
                    OustResourceUtils.setDefaultDrawableColor(iv_filter);
                    meetFilterCriteria(filterCategoryList);
                }
            });
            filterDialog.show(fragmentManager, "Filter Dialog");
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void setInitialDataSet(boolean comingFromFilterData) {
        try {
            adapter = null;
            localAssessmentCount = 0;
            ArrayList<CommonLandingData> assessmentSetList = assessmentList;
            CommonLandingFilter commonLandingFilter;
            HashMap<String, ArrayList<CommonLandingData>> assessmentHashMap;

            tempAssessmentList = assessmentSetList;
            if ((assessmentFilterList != null && assessmentFilterList.size() != 0) || comingFromFilterData) {
                tempAssessmentList = assessmentFilterList;
                assessmentSetList = assessmentFilterList;
                showModuleCount(assessmentFilterList, comingFromFilterData);
            }

            commonLandingFilter = new CommonLandingFilter();
//            Collections.sort(assessmentSetList, CommonLandingData.sortByDate);
            assessmentHashMap = commonLandingFilter.getAssessmentModulesHashMap(assessmentSetList);
            if (!isAscending) {
                OustResourceUtils.setDefaultDrawableColor(iv_sort.getDrawable(), getResources().getColor(R.color.unselected_text));
                allAssessmentSortedMap = new TreeMap<>(Collections.reverseOrder());
            } else {
                OustResourceUtils.setDefaultDrawableColor(iv_sort);
                allAssessmentSortedMap = new TreeMap<>();
            }

            allAssessmentSortedMap.putAll(assessmentHashMap);
            if (allAssessmentSortedMap.size() != 0) {
                loadData();
            } else {
                no_data_layout.setVisibility(View.VISIBLE);
                no_image.setImageDrawable(getResources().getDrawable(R.drawable.no_pending_completed));
                no_data_content.setText(getResources().getString(R.string.no_pending_module_content_completed));
                assessment_list_rv.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void layoutSwitcher() {
        try {
            if (adapter != null) {
                adapter.setType(type);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void loadData() {
        try {
            if (localAssessmentCount + 5 < allAssessmentSortedMap.size()) {
                localAssessmentCount += 5;
                OustPreferences.saveAppInstallVariable("assessmentScroll", true);
            } else {
                localAssessmentCount = allAssessmentSortedMap.size();
                OustPreferences.saveAppInstallVariable("allAssessmentLoad", true);
            }
            keyList = new ArrayList<>(allAssessmentSortedMap.keySet());
            int startCount = (int) (((Math.ceil((localAssessmentCount * 1.0) / 5)) - 1) * 5);
            ArrayList<String> loadedKeyList = new ArrayList<>(keyList.subList(startCount, localAssessmentCount));
            Map<String, ArrayList<CommonLandingData>> loadedModuleMap = new HashMap<>();
            for (String key : loadedKeyList) {
                loadedModuleMap.put(key, allAssessmentSortedMap.get(key));
            }

            if (mAssessmentLayoutManager == null)
                mAssessmentLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

            if (adapter == null) {
                adapter = new TaskHashMapModuleAdapter();
                if (assessment_list_rv.getLayoutManager() == null) {
                    assessment_list_rv.setLayoutManager(mAssessmentLayoutManager);
                }
                adapter.setTaskRecyclerAdapter(loadedModuleMap, getActivity(), type, isAscending);
                adapter.setTaskModuleHashMap(allAssessmentSortedMap);
                Objects.requireNonNull(requireActivity()).runOnUiThread(() -> assessment_list_rv.setAdapter(adapter));
            } else {
                adapter.notifyTaskHashMap(loadedModuleMap);
            }

            no_data_layout.setVisibility(View.GONE);
            data_layout.setVisibility(View.VISIBLE);
            assessment_list_rv.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void meetFilterCriteria(ArrayList<Integer> filter) {
        try {
            HashMap<String, CommonLandingData> filterModuleHashMap = new HashMap<>();
            if (filter != null && filter.size() != 0) {
                Collections.sort(filter);
                for (int filterForAll : filter) {
                    if (assessmentList != null && assessmentList.size() != 0) {
                        ArrayList<CommonLandingData> copyList = assessmentList;
                        for (int i = 0; i < copyList.size(); i++) {
                            CommonLandingData commonLandingData = copyList.get(i);
                            if (filterForAll == 6) {
                               /* if (commonLandingData.getType() != null && commonLandingData.getType().equalsIgnoreCase("ASSESSMENT")) {
                                     if (commonLandingData.isEnrolled() && commonLandingData.getCompletionPercentage() == 0) {
                                        filterModuleHashMap.put("Module" + commonLandingData.getId(), commonLandingData);
                                    } else if (!commonLandingData.isEnrolled()) {
                                        filterModuleHashMap.put("Module" + commonLandingData.getId(), commonLandingData);
                                    }
                                } else {
                                    if (!commonLandingData.isEnrolled()) {
                                        filterModuleHashMap.put("Module" + commonLandingData.getId(), commonLandingData);
                                    }
                                }*/
                                if (!commonLandingData.isEnrolled()) {
                                    filterModuleHashMap.put("Module" + commonLandingData.getId(), commonLandingData);
                                }
                            }

                            if (filterForAll == 7) {
                                if (commonLandingData.getType() != null && commonLandingData.getType().equalsIgnoreCase("ASSESSMENT")) {
//                                    && commonLandingData.getCompletionPercentage() > 0 && commonLandingData.getCompletionPercentage() < 100
                                    if (commonLandingData.isEnrolled()) {
                                        filterModuleHashMap.put("Module" + commonLandingData.getId(), commonLandingData);
                                    }
                                }
                            }
                        }
                    }
                }

                if (filterModuleHashMap.size() != 0) {
                    assessmentFilterList = new ArrayList<>(filterModuleHashMap.values());
                } else {
                    assessmentFilterList = new ArrayList<>();
                }
            } else {
                assessmentFilterList = null;
            }
            setInitialDataSet(true);
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
                if (catalogueModuleUpdate.getType() != null && catalogueModuleUpdate.getType().contains("ASSESSMENT")) {
                    if (adapter != null) {
                        if (adapter.getList() != null && adapter.getList().size() != 0) {
                            if (catalogueModuleUpdate.getParentPosition() < adapter.getList().size()) {
                                adapter.modifyItem(catalogueModuleUpdate.getParentPosition(), catalogueModuleUpdate);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
            OustStaticVariableHandling.getInstance().setCatalogueModuleUpdate(null);
        }
    }

    public void setAssessmentData(String query, boolean showData) {
        try {
            if (query != null) {
                if (adapter != null) {
                    if (assessmentList != null) {
                        adapter.getFilter().filter(query);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
