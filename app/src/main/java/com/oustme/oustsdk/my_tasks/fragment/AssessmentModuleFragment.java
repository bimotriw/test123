package com.oustme.oustsdk.my_tasks.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.R;
import com.oustme.oustsdk.catalogue_ui.model.CatalogueModuleUpdate;
import com.oustme.oustsdk.customviews.LayoutSwitcher;
import com.oustme.oustsdk.filter.FilterDialogFragment;
import com.oustme.oustsdk.filter.FilterForAll;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.my_tasks.MyTasksScreen;
import com.oustme.oustsdk.my_tasks.adapter.TaskHashMapModuleAdapter;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.filters.CommonLandingFilter;
import com.oustme.oustsdk.utils.OustResourceUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class AssessmentModuleFragment extends Fragment {

    private ArrayList<CommonLandingData> assessmentList = new ArrayList<>();
    private ArrayList<CommonLandingData> assessmentFilterList;
    LinearLayout data_layout;
    TextView pending_assessment_module;
    ImageView iv_sort;
    ImageView iv_filter;
    LayoutSwitcher layout_switcher;
    RecyclerView assessment_list_rv;
    TextView no_module;

    RecyclerView.LayoutManager mAssessmentLayoutManager;
    private FragmentManager fragmentManager;
    FilterDialogFragment filterDialog;
    private ArrayList<FilterForAll> filterCategories;
    private ArrayList<FilterForAll> filterCategories1;
    private ArrayList<FilterForAll> selectedFeedFilter = new ArrayList<>();


    TaskHashMapModuleAdapter adapter;
    int type = 1;
    private boolean isAscending = false;
    private boolean isFilter = false;

    public AssessmentModuleFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try {
            MyTasksScreen myTasksScreen = (MyTasksScreen) getActivity();
            assert myTasksScreen != null;
            assessmentList = myTasksScreen.getAssessmentList();

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        View view = inflater.inflate(R.layout.fragment_assessment_module, container, false);
        data_layout = view.findViewById(R.id.data_layout);
        pending_assessment_module = view.findViewById(R.id.pending_assessment_module);
        iv_sort = view.findViewById(R.id.iv_sort);
        iv_filter = view.findViewById(R.id.iv_filter);
        layout_switcher = view.findViewById(R.id.layout_switcher);
        assessment_list_rv = view.findViewById(R.id.assessment_list_rv);
        assessment_list_rv.setNestedScrollingEnabled(false);
        no_module = view.findViewById(R.id.no_module);

        if (assessmentList != null && assessmentList.size() != 0) {
            isFilter = true;
            String pending_assessment = getResources().getString(R.string.you_have)+" " + assessmentList.size() + " "+getResources().getString(R.string.assessment_pending);

            if (assessmentList.size() > 1) {
                pending_assessment = getResources().getString(R.string.you_have)+" " + assessmentList.size() + " "+getResources().getString(R.string.assessments_pending);
            }

            setFilterData();

            pending_assessment_module.setText(pending_assessment);
            setFilter(getParentFragmentManager());
            setAdapter();

        } else {

            no_module.setVisibility(View.VISIBLE);
            data_layout.setVisibility(View.GONE);

        }

        iv_sort.setOnClickListener(v -> {
            isAscending = !isAscending;
            setAdapter();
        });

        layout_switcher.setCallback(Type -> {
            type = Type;
            setAdapter();
        });

        iv_filter.setOnClickListener(v -> openFilterDialogue());

        return view;
    }

    public void setFilter(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    private void setFilterData() {

        if (filterCategories == null) {
            filterCategories = new ArrayList<>();
          /*  filterCategories.add(new FilterForAll(getResources().getString(R.string.all) + "", 0));
            filterCategories.add(new FilterForAll(getResources().getString(R.string.courses_text) + "", 1));
            filterCategories.add(new FilterForAll(getResources().getString(R.string.webinar_text) + "", 2));
            filterCategories.add(new FilterForAll(getResources().getString(R.string.classroom_text) + "", 3));*/
        }
        if (filterCategories1 == null) {
            filterCategories1 = new ArrayList<>();
            filterCategories1.add(new FilterForAll(getResources().getString(R.string.all) + "", 5));
            filterCategories1.add(new FilterForAll("Not Started", 6));
            filterCategories1.add(new FilterForAll("In Progress", 7));

        }

        if(filterCategories1!=null&&filterCategories1.size()!=0){
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
                    setAdapter();
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

    private void setAdapter() {
        try {
            ArrayList<CommonLandingData> assessmentSetList = assessmentList;
            Map<String,ArrayList<CommonLandingData>> assessmentSetMap = new TreeMap<>();
            Map<String,ArrayList<CommonLandingData>> assessmentSortedMap = assessmentSetMap ;
            CommonLandingFilter commonLandingFilter = new CommonLandingFilter();
            HashMap<String,ArrayList<CommonLandingData>> assessmentHashMap;

            if(adapter!=null){
                //assessmentSetList = adapter.getList();
                assessmentSetMap = adapter.getList();
                assessmentSetList = new ArrayList<>();
                for (String key : assessmentSetMap.keySet()) {
                    ArrayList<CommonLandingData> valueList = assessmentSetMap.get(key);
                    if (valueList != null && valueList.size() != 0) {
                        assessmentSetList.addAll(valueList);
                    }
                }
            }

            if (assessmentFilterList != null) {
                assessmentSetList = assessmentFilterList;
            }


            if (!isAscending) {
                OustResourceUtils.setDefaultDrawableColor(iv_sort.getDrawable(), getResources().getColor(R.color.unselected_text));
                commonLandingFilter = new CommonLandingFilter();
                /*Collections.sort(assessmentSetList,CommonLandingData.sortByDate);
                Collections.reverse(assessmentSetList);*/
                assessmentHashMap = commonLandingFilter.getAssessmentModulesHashMap(assessmentSetList);
                assessmentSortedMap = new TreeMap<>(Collections.reverseOrder());
                assessmentSortedMap.putAll(assessmentHashMap);
            } else {
                OustResourceUtils.setDefaultDrawableColor(iv_sort);
                commonLandingFilter = new CommonLandingFilter();
                //Collections.sort(assessmentSetList,CommonLandingData.sortByDate);
                assessmentHashMap = commonLandingFilter.getAssessmentModulesHashMap(assessmentSetList);
                assessmentSortedMap = new TreeMap<>(assessmentHashMap);
            }

            if (assessmentSortedMap.size() == 0) {
                no_module.setVisibility(View.VISIBLE);
            } else {
                no_module.setVisibility(View.GONE);
            }

            //setLayoutManager();
            setAssessmentAdapter(assessmentSortedMap);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void setAssessmentAdapter(Map<String,ArrayList<CommonLandingData>> assessmentMap) {

        if (adapter == null)
            adapter = new TaskHashMapModuleAdapter();

        if (mAssessmentLayoutManager == null)
            mAssessmentLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL, false);

        if (assessmentMap != null && assessmentMap.size() != 0) {
            ArrayList<String> keyList = new ArrayList<>();
            for (Map.Entry<String, ArrayList<CommonLandingData>> taskList : assessmentMap.entrySet()) {
                String dateKey = taskList.getKey();
                keyList.add(dateKey);
            }

            assessment_list_rv.setLayoutManager(mAssessmentLayoutManager);
            assessment_list_rv.setItemAnimator(new DefaultItemAnimator());
            assessment_list_rv.removeAllViews();
            adapter.setTaskRecyclerAdapter(assessmentMap, getActivity(), type, isAscending);
            assessment_list_rv.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            no_module.setVisibility(View.GONE);
            data_layout.setVisibility(View.VISIBLE);
            assessment_list_rv.setVisibility(View.VISIBLE);

        } else {
            no_module.setVisibility(View.VISIBLE);
            if(!isFilter){
                data_layout.setVisibility(View.GONE);
            }else{
                assessment_list_rv.setVisibility(View.GONE);
            }

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
                                if (commonLandingData.getType() != null && commonLandingData.getType().equalsIgnoreCase("COURSE")) {

                                    if (commonLandingData.isEnrolled() && commonLandingData.getCompletionPercentage() == 0) {
                                        filterModuleHashMap.put("Module" + commonLandingData.getId(), commonLandingData);
                                    } else if (!commonLandingData.isEnrolled()) {
                                        filterModuleHashMap.put("Module" + commonLandingData.getId(), commonLandingData);
                                    }

                                } else {

                                    if (!commonLandingData.isEnrolled()) {
                                        filterModuleHashMap.put("Module" + commonLandingData.getId(), commonLandingData);
                                    }

                                }

                            }

                            if (filterForAll == 7) {
                                if (commonLandingData.getType() != null && commonLandingData.getType().equalsIgnoreCase("COURSE")) {

                                    if (commonLandingData.isEnrolled() && commonLandingData.getCompletionPercentage() > 0 &&
                                            commonLandingData.getCompletionPercentage() < 100) {
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

                //sort();
                setAdapter();

            } else {
                assessmentFilterList = null;
                setAdapter();
            }

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

                if (catalogueModuleUpdate.getType() != null&&catalogueModuleUpdate.getType().contains("ASSESSMENT")) {
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

}
