package com.oustme.oustsdk.layoutFour.components.dialogFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.layoutFour.components.dialogFragments.adapter.GroupFilterDialogAdapter;
import com.oustme.oustsdk.layoutFour.data.response.GroupDataList;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.utils.OustResourceUtils;

import java.util.ArrayList;

public class GroupFilterDialogFragment extends BottomSheetDialogFragment {

    //private static final String ARG_ITEM_COUNT = "item_count";
    private static final String ARG_FILTERS = "filters";
    private static final String ARG_SELECTED_FILTERS = "selected filter";

    private ArrayList<GroupDataList> selectedFilterCategories;

    private FilterDialogCallback callback;
    private LinearLayout dialog_ok;

    // TODO: Customize parameters
    public static GroupFilterDialogFragment newInstance(ArrayList<GroupDataList> filterCategories,
                                                        ArrayList<GroupDataList> selectedFilter,
                                                        FilterDialogCallback callback) {
        final GroupFilterDialogFragment fragment = new GroupFilterDialogFragment();
        final Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_FILTERS, filterCategories);
        args.putParcelableArrayList(ARG_SELECTED_FILTERS, selectedFilter);
        fragment.setArguments(args);
        fragment.setCallback(callback);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bottom_filter_dialog_list_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        try {

            //TextView fragment_title = view.findViewById(R.id.fragment_title);
            ImageView fragment_close = view.findViewById(R.id.fragment_close);
            RecyclerView rv_filter = view.findViewById(R.id.rv_filter);
            rv_filter.setNestedScrollingEnabled(true);
            LinearLayout dialog_cancel = view.findViewById(R.id.dialog_cancel);
            dialog_ok = view.findViewById(R.id.dialog_ok);


            disable(dialog_cancel,true);
            disable(dialog_ok,false);

            Bundle arguments = getArguments();
            if (arguments == null)
                return;
            ArrayList<GroupDataList> filterCategories = arguments.getParcelableArrayList(ARG_FILTERS);
            selectedFilterCategories = arguments.getParcelableArrayList(ARG_SELECTED_FILTERS);

            if (selectedFilterCategories == null)
                selectedFilterCategories = new ArrayList<>();

            setClicks();

            if (filterCategories != null && filterCategories.size() > 0) {
                GroupFilterDialogAdapter filterAdapter = new GroupFilterDialogAdapter(filterCategories, selectedFilterCategories);
                filterAdapter.setCallback(filterAdapterCallback);
                rv_filter.setAdapter(filterAdapter);
            }

            dialog_ok.setOnClickListener(v -> {
                if (callback != null)
                    callback.onOkClicked(selectedFilterCategories);
                dismiss();
            });

            dialog_cancel.setOnClickListener(v -> dismiss());

            fragment_close.setOnClickListener(v -> dismiss());




        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public interface FilterDialogCallback {
        void onOkClicked(ArrayList<GroupDataList> selectedFilter);
    }

    private void setCallback(FilterDialogCallback callback) {
        this.callback = callback;
    }

    private void enable(LinearLayout linearLayout) {
        linearLayout.setBackground(OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.button_rounded_ten_bg)));
        linearLayout.setClickable(true);
    }

    private void disable(LinearLayout linearLayout,boolean isClickable) {
        linearLayout.setBackground(getResources().getDrawable(R.drawable.button_rounded_ten_bg));
        linearLayout.setClickable(isClickable);
    }

    private void setClicks() {
        if (selectedFilterCategories != null && selectedFilterCategories.size() > 0) {
            enable(dialog_ok);
        } else {
            disable(dialog_ok, true);
        }
    }

    GroupFilterDialogAdapter.FilterAdapterCallback filterAdapterCallback =
            new GroupFilterDialogAdapter.FilterAdapterCallback() {
                @Override
                public void onItemClicked(ArrayList<GroupDataList> selectedFilter) {
                    selectedFilterCategories = new ArrayList<>(selectedFilter);
                    setClicks();
                }
            };



}

