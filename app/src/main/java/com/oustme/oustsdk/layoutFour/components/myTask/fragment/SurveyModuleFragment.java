package com.oustme.oustsdk.layoutFour.components.myTask.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.firebase.common.FilterCategory;
import com.oustme.oustsdk.layoutFour.components.myTask.adapter.TaskModuleAdapterNested;
import com.oustme.oustsdk.layoutFour.components.newCatalogue.FilterDialogFragment;
import com.oustme.oustsdk.layoutFour.customViews.LayoutSwitcher;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.utils.LayoutType;
import com.oustme.oustsdk.utils.OustResourceUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SurveyModuleFragment extends Fragment {

    private static final String ARG_LANDING_DATA = "common landing data";
    private static final String TAG = "CourseModuleFragment";
    private ArrayList<CommonLandingData> allList = new ArrayList<>();

    TextView no_module;
    RecyclerView all_list_rv;
    LayoutSwitcher layoutSwitcher;
    private int type = LayoutType.LIST;
    private LinearLayoutManager mLayoutManager;
    private ImageView ivSort;
    private ImageView ivFilter;
    private boolean isAssending = true;
    private ArrayList<FilterCategory> filterCategories;
    private ArrayList<FilterCategory> filterCategories1;
    private ArrayList<FilterCategory> selectedFeedFilter;
    private ArrayList<CommonLandingData> filteredCommonLandingData;


    public SurveyModuleFragment() {
        // Required empty public constructor
    }

    public static SurveyModuleFragment getInstance(ArrayList<CommonLandingData> landingData) {

        SurveyModuleFragment fragment = new SurveyModuleFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_LANDING_DATA, landingData);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_module, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        no_module = view.findViewById(R.id.no_module);
        all_list_rv = view.findViewById(R.id.all_list_rv);
        layoutSwitcher = view.findViewById(R.id.layoutSwitcher);
        ivSort = view.findViewById(R.id.iv_sort);
        ivFilter = view.findViewById(R.id.iv_filter);

        layoutSwitcher.setCallback(new LayoutSwitcher.LayoutSwitcherCallback() {
            @Override
            public void onLayoutSelected(int Type) {
                type = Type;
                setAdapter();
            }
        });

        try {
            if (getArguments() != null) {
                allList = getArguments().getParcelableArrayList(ARG_LANDING_DATA);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }


        ivFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilteDialogue();
            }
        });

        ivSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAssending = !isAssending;
                sort();
                setAdapter();
            }
        });
        sort();
        setfilterData();
        setAdapter();
    }

    private void openFilteDialogue() {
        FilterDialogFragment filterDialog = FilterDialogFragment.newInstance(filterCategories, filterCategories1, selectedFeedFilter, new FilterDialogFragment.FilterDialogCallback() {
            @Override
            public void onOkClicked(ArrayList<FilterCategory> selectedFilter) {
                selectedFeedFilter = selectedFilter;
                if (selectedFilter == null || selectedFilter.size() == 0) {
//                    extractModulesNFolders();

                    sort();

                    setAdapter();
                } else {
//                    meetFilterCriteria(allList, selectedFeedFilter);
                }
            }
        });
        filterDialog.show(getChildFragmentManager(), "Filter Dialog");
    }

    private void setAdapter() {


        if (allList != null && allList.size() != 0) {
            TaskModuleAdapterNested adapter = new TaskModuleAdapterNested(allList, getContext(), type, isAssending);

            no_module.setVisibility(View.GONE);
            all_list_rv.setVisibility(View.VISIBLE);
            layoutSwitcher.setVisibility(View.VISIBLE);

            mLayoutManager = new LinearLayoutManager(getContext());
            all_list_rv.setLayoutManager(mLayoutManager);
            all_list_rv.setItemAnimator(new DefaultItemAnimator());
            all_list_rv.setAdapter(adapter);

        } else {
            no_module.setVisibility(View.VISIBLE);
            all_list_rv.setVisibility(View.GONE);
            layoutSwitcher.setVisibility(View.GONE);

        }

//        layoutSwitcher.setVisibility(View.GONE);
    }

    private void sort() {

        if (isAssending) {
            OustResourceUtils.setDefaultDrawableColor(ivSort.getDrawable(), Color.parseColor("#CECECE"));

            Collections.sort(allList, landingDataComparator);
        } else {
            OustResourceUtils.setDefaultDrawableColor(ivSort);

            Collections.sort(allList, landingDataComparator1);
        }
    }


    public Comparator<CommonLandingData> landingDataComparator = new Comparator<CommonLandingData>() {
        @Override
        public int compare(CommonLandingData o1, CommonLandingData o2) {
            try {

                long o2dt = Long.parseLong(o2.getAddedOn());
                long o1dt = Long.parseLong(o1.getAddedOn());
                return (int) (o2dt - o1dt);
            } catch (Exception e) {

            }
            return 0;
        }
    };

    public Comparator<CommonLandingData> landingDataComparator1 = new Comparator<CommonLandingData>() {
        @Override
        public int compare(CommonLandingData o1, CommonLandingData o2) {
            try {

                long o2dt = Long.parseLong(o2.getAddedOn());
                long o1dt = Long.parseLong(o1.getAddedOn());
                return (int) (o1dt - o2dt);
            } catch (Exception e) {

            }
            return 0;
        }
    };

    private void setfilterData() {
        /*if (filterCategories == null) {
            filterCategories = new ArrayList<>();
            filterCategories.add(new FilterCategory("All", 0));
            filterCategories.add(new FilterCategory("Course", 1));
            filterCategories.add(new FilterCategory("Assessment", 2));
            filterCategories.add(new FilterCategory("Folders", 3));
        }*/
        if (filterCategories1 == null) {
            filterCategories1 = new ArrayList<>();
            filterCategories1.add(new FilterCategory("All", 4));
            filterCategories1.add(new FilterCategory("Not Started", 5));
            filterCategories1.add(new FilterCategory("In Progress", 6));
            filterCategories1.add(new FilterCategory("Completed", 7));
        }

    }

    public void meetFilterCriteria(ArrayList<CommonLandingData> landingDataArrayList, ArrayList<FilterCategory> filterCategories) {
        filteredCommonLandingData = new ArrayList<>();
        for (FilterCategory filterCategory : filterCategories) {
            int type = filterCategory.getCategoryType();
            String filterStr = "";
            if (type == 1) {
                filterStr = "Course";
            } else if (type == 2) {
                filterStr = "Assessment";
            } else if (type == 3) {
                filterStr = "Category";
            } else if (type == 5) {
                filterStr = "Not Started";
            } else if (type == 6) {
                filterStr = "In Progress";
            } else if (type == 7) {
                filterStr = "Completed";
            } else if (type == 0 || type == 4) {
                //filterStr = "Ram";
                Log.d(TAG, "meetFilterCriteria: ");
            }
            if (filterStr != null && filterStr.length() > 0) {
                try {
                    for (CommonLandingData itemData : landingDataArrayList) {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            } else {
                filteredCommonLandingData = new ArrayList<>(allList);
            }
        }

        sort();

        setAdapter();

    }

}
