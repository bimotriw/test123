package com.oustme.oustsdk.layoutFour.components.myTask.fragment;

import android.os.Bundle;
import android.util.Log;

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
import com.oustme.oustsdk.filter.FilterDialogFragment;
import com.oustme.oustsdk.filter.FilterForAll;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.layoutFour.interfaces.SwipeRefreshHandling;
import com.oustme.oustsdk.my_tasks.adapter.TaskHashMapAdapter;
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

public class CourseModuleFragment extends Fragment {

    NestedScrollView nested_view;
    LinearLayout data_layout;
    TextView pending_course_module;
    ImageView iv_sort;
    ImageView iv_filter;
    LayoutSwitcher layout_switcher;
    RecyclerView course_list_rv;
    View no_data_layout;
    ImageView no_image;
    TextView no_data_content;
    RecyclerView.LayoutManager mCourseLayoutManager;
    FragmentManager fragmentManager;
    FilterDialogFragment filterDialog;
    ArrayList<FilterForAll> filterCategories;
    ArrayList<FilterForAll> filterCategories1;
    ArrayList<FilterForAll> selectedFeedFilter = new ArrayList<>();

    ArrayList<CommonLandingData> courseList = new ArrayList<>();
    ArrayList<CommonLandingData> tempCourseList = new ArrayList<>();
    ArrayList<CommonLandingData> courseFilterList;
    Map<String, ArrayList<CommonLandingData>> allCourseSortedMap;
    ArrayList<String> keyList = new ArrayList<>();
    int localCourseCount = 0;

    TaskHashMapAdapter adapter;
    int type = 1;
    private boolean isAscending = false;
    SwipeRefreshHandling swipeRefreshHandling;

    public CourseModuleFragment() {

    }

    public CourseModuleFragment(ArrayList<CommonLandingData> courseList, SwipeRefreshHandling swipeRefreshHandling) {
        this.courseList = courseList;
        this.swipeRefreshHandling = swipeRefreshHandling;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_course_module, container, false);
        nested_view = view.findViewById(R.id.nested_view);
        data_layout = view.findViewById(R.id.data_layout);
        pending_course_module = view.findViewById(R.id.pending_course_module);
        iv_sort = view.findViewById(R.id.iv_sort);
        iv_filter = view.findViewById(R.id.iv_filter);
        layout_switcher = view.findViewById(R.id.layout_switcher);
        course_list_rv = view.findViewById(R.id.course_list_rv);
        course_list_rv.setNestedScrollingEnabled(false);
        no_data_layout = view.findViewById(R.id.no_course_data_layout);
        no_image = no_data_layout.findViewById(R.id.no_image);
        no_data_content = no_data_layout.findViewById(R.id.no_data_content);

        if (courseList != null && courseList.size() != 0) {
            setFilterData();
            showModuleCount(courseList, false);
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
                if (tempCourseList != null && tempCourseList.size() > 0) {
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

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) course_list_rv.getLayoutManager();
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
                        Log.e("TAG", "onCreateView: allCourseLoad--> " + OustPreferences.getAppInstallVariable("allCourseLoad") + "  courseScroll--> " + OustPreferences.getAppInstallVariable("courseScroll"));
                        if (!OustPreferences.getAppInstallVariable("allCourseLoad") && OustPreferences.getAppInstallVariable("courseScroll")) {
                            try {
                                OustPreferences.saveAppInstallVariable("courseScroll", false);
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

    private void showModuleCount(ArrayList<CommonLandingData> courseList, boolean comingFromFilterData) {
        try {
            if (courseList != null && courseList.size() != 0) {
                String pending_course = getResources().getString(R.string.you_have) + " " + courseList.size() + " " + getResources().getString(R.string.course_pending);
                if (courseList.size() > 1) {
                    pending_course = getResources().getString(R.string.you_have) + " " + courseList.size() + " " + getResources().getString(R.string.courses_pending);
                }
                pending_course_module.setText(pending_course);
            } else {
                if (comingFromFilterData) {
                    String pending_course = getResources().getString(R.string.you_have) + " " + getResources().getString(R.string.no_mobules_pending);
                    pending_course_module.setText(pending_course);
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
                    courseFilterList = null;
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
            localCourseCount = 0;
            ArrayList<CommonLandingData> courseSetList = courseList;
            CommonLandingFilter commonLandingFilter;
            HashMap<String, ArrayList<CommonLandingData>> courseHashMap;

            tempCourseList = courseSetList;
            if ((courseFilterList != null && courseFilterList.size() != 0) || comingFromFilterData) {
                courseSetList = courseFilterList;
                tempCourseList = courseFilterList;
                showModuleCount(courseSetList, comingFromFilterData);
            }

            commonLandingFilter = new CommonLandingFilter();
//            Collections.sort(courseSetList, CommonLandingData.sortByDate);
            courseHashMap = commonLandingFilter.getCourseModulesHashMap(courseSetList);
            if (!isAscending) {
                OustResourceUtils.setDefaultDrawableColor(iv_sort.getDrawable(), getResources().getColor(R.color.unselected_text));
                allCourseSortedMap = new TreeMap<>(Collections.reverseOrder());
            } else {
                OustResourceUtils.setDefaultDrawableColor(iv_sort);
                allCourseSortedMap = new TreeMap<>();
            }

            allCourseSortedMap.putAll(courseHashMap);
            if (allCourseSortedMap.size() != 0) {
                loadData();
            } else {
                no_data_layout.setVisibility(View.VISIBLE);
                no_image.setImageDrawable(getResources().getDrawable(R.drawable.no_pending_completed));
                no_data_content.setText(getResources().getString(R.string.no_pending_module_content_completed));
                course_list_rv.setVisibility(View.GONE);
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
            if (localCourseCount + 5 < allCourseSortedMap.size()) {
                localCourseCount += 5;
                OustPreferences.saveAppInstallVariable("allCourseLoad", false);
                OustPreferences.saveAppInstallVariable("courseScroll", true);
            } else {
                localCourseCount = allCourseSortedMap.size();
                OustPreferences.saveAppInstallVariable("allCourseLoad", true);
            }
            keyList = new ArrayList<>(allCourseSortedMap.keySet());
            int startCount = (int) (((Math.ceil((localCourseCount * 1.0) / 5)) - 1) * 5);

            ArrayList<String> loadedKeyList = new ArrayList<>(keyList.subList(startCount, localCourseCount));
            Map<String, ArrayList<CommonLandingData>> loadedModuleMap = new HashMap<>();
            for (String key : loadedKeyList) {
                if (key != null && allCourseSortedMap != null && allCourseSortedMap.get(key) != null
                        && allCourseSortedMap.get(key).size() != 0) {
                    for (int i = 0; i < allCourseSortedMap.get(key).size(); i++) {
                        if (!allCourseSortedMap.get(key).get(i).getType().contains("ASSESSMENT") && !allCourseSortedMap.get(key).get(i).getType().contains("MULTILINGUAL") && !allCourseSortedMap.get(key).get(i).getType().contains("CPL") && !allCourseSortedMap.get(key).get(i).getType().contains("FFF_CONTEXT")) {
                            loadedModuleMap.put(key, allCourseSortedMap.get(key));
                        } else {
                            Log.d("TAG", "loadData key-: " + key + "  allCourseSortedMap--> " + allCourseSortedMap.get(key).get(i).getType());
                        }
                    }
                }
            }

            if (mCourseLayoutManager == null)
                mCourseLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

            if (adapter == null) {
                adapter = new TaskHashMapAdapter();
                if (course_list_rv.getLayoutManager() == null) {
                    course_list_rv.setLayoutManager(mCourseLayoutManager);
                }
                adapter.setTaskModuleHashMap(allCourseSortedMap);
                adapter.setTaskRecyclerAdapter(loadedModuleMap, getActivity(), type, isAscending);
                Objects.requireNonNull(requireActivity()).runOnUiThread(() -> course_list_rv.setAdapter(adapter));
            } else {
                adapter.notifyTaskHashMap(loadedModuleMap);
            }

            no_data_layout.setVisibility(View.GONE);
            data_layout.setVisibility(View.VISIBLE);
            course_list_rv.setVisibility(View.VISIBLE);

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
                boolean statusFilter = false;
                boolean courseFilter = false;
                boolean webFilter = false;
                boolean classRoomFilter = false;
                for (int filterForAll : filter) {
                    if (filterForAll == 6 || filterForAll == 7) {
                        statusFilter = true;
                    }
                    if (filterForAll == 1) {
                        courseFilter = true;
                    }
                    if (filterForAll == 2) {
                        webFilter = true;
                    }
                    if (filterForAll == 3) {
                        classRoomFilter = true;
                    }
                }
                for (int filterForAll : filter) {
                    if (courseList != null && courseList.size() != 0) {
                        ArrayList<CommonLandingData> copyList = courseList;
                        for (int i = 0; i < copyList.size(); i++) {
                            CommonLandingData commonLandingData = copyList.get(i);
                            if (courseFilter || statusFilter) {
                                if (statusFilter && courseFilter) {
                                    if (filterForAll == 6) {
                                        if (commonLandingData.getType() != null && commonLandingData.getType().equalsIgnoreCase("COURSE")) {
                                            if (!commonLandingData.isEnrolled() && commonLandingData.getCompletionPercentage() == 0) {
                                                filterModuleHashMap.put("Module" + commonLandingData.getId(), commonLandingData);
                                            } /*else if (!commonLandingData.isEnrolled()) {
                                                filterModuleHashMap.put("Module" + commonLandingData.getId(), commonLandingData);
                                            }*/
                                        }
                                    }
                                    if (filterForAll == 7) {
                                        if (commonLandingData.getType() != null && commonLandingData.getType().equalsIgnoreCase("COURSE")) {
                                            if (commonLandingData.isEnrolled() || commonLandingData.getCompletionPercentage() > 0 &&
                                                    commonLandingData.getCompletionPercentage() < 100) {
                                                filterModuleHashMap.put("Module" + commonLandingData.getId(), commonLandingData);
                                            }
                                        }
                                    }
                                } else if (courseFilter) {
                                    if (filterForAll == 1) {
                                        if (commonLandingData.getType() != null && commonLandingData.getType().equalsIgnoreCase("COURSE")) {
                                            filterModuleHashMap.put("Module" + commonLandingData.getId(), commonLandingData);
                                        }
                                    }
                                }
                            }
                            if (webFilter || statusFilter) {
                                if (statusFilter && webFilter) {
                                    if (filterForAll == 6) {
                                        if (commonLandingData.getType() != null && commonLandingData.getType().equalsIgnoreCase("WEBINAR")
                                                && !commonLandingData.isEnrolled()) {
                                            filterModuleHashMap.put("Module" + commonLandingData.getId(), commonLandingData);
                                        }
                                    }
                                } else if (webFilter) {
                                    if (filterForAll == 2) {
                                        if (commonLandingData.getType() != null && commonLandingData.getType().equalsIgnoreCase("WEBINAR")) {
                                            filterModuleHashMap.put("Module" + commonLandingData.getId(), commonLandingData);
                                        }
                                    }
                                }
                            }

                            if (classRoomFilter || statusFilter) {
                                if (statusFilter && classRoomFilter) {
                                    if (filterForAll == 6) {
                                        if (commonLandingData.getType() != null && commonLandingData.getType().equalsIgnoreCase("CLASSROOM") && !commonLandingData.isEnrolled()) {
                                            filterModuleHashMap.put("Module" + commonLandingData.getId(), commonLandingData);
                                        }
                                    }
                                } else if (classRoomFilter) {
                                    if (filterForAll == 3) {
                                        if (commonLandingData.getType() != null && commonLandingData.getType().equalsIgnoreCase("CLASSROOM")) {
                                            filterModuleHashMap.put("Module" + commonLandingData.getId(), commonLandingData);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (filterModuleHashMap.size() != 0) {
                    courseFilterList = new ArrayList<>(filterModuleHashMap.values());
                } else {
                    courseFilterList = new ArrayList<>();
                }
            } else {
                courseFilterList = null;
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
                if (catalogueModuleUpdate.getType() != null && !catalogueModuleUpdate.getType().contains("COURSE")) {
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

    public void setCourseData(String query, boolean showData) {
        try {
            if (query != null) {
                if (!query.isEmpty()) {
                    showData = false;
                }
                if (adapter != null) {
                    if (showData) {
                        adapter.filterInvoke();
                    } else {
                        if (courseList != null) {
                            adapter.getFilter().filter(query);
                        }
                    }
                }
            }
        } catch (
                Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
