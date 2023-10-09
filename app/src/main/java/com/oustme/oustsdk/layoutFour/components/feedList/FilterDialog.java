package com.oustme.oustsdk.layoutFour.components.feedList;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.common.FilterCategory;
import com.oustme.oustsdk.layoutFour.components.feedList.adapter.FeedFilterAdapter;
import com.oustme.oustsdk.utils.OustResourceUtils;

import java.util.ArrayList;

public class FilterDialog extends BottomSheetDialogFragment {

    private static final String ARG_FILTERS = "filters";
    private static final String ARG_SELECTED_FILTERS = "selected filter";
    private ArrayList<FilterCategory> filterCategories;
    private ArrayList<FilterCategory> selectedFilterCategories;
    private RecyclerView rvFilter;
    private ImageView btnClose;
    private TextView btnOk;
    private TextView btnCancel;
    private FilterDialogCallback callback;

    public static FilterDialog newInstance(ArrayList<FilterCategory> filterCategories, ArrayList<FilterCategory> selectedFilter, FilterDialogCallback callback) {
        FilterDialog filterDialog = new FilterDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ARG_FILTERS, filterCategories);
        bundle.putParcelableArrayList(ARG_SELECTED_FILTERS, selectedFilter);
        filterDialog.setArguments(bundle);
        filterDialog.setCallback(callback);
        filterDialog.setCancelable(false);
        return filterDialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_filter_bottom_sheet, container,
                false);

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        btnClose = view.findViewById(R.id.btn_close);
        btnOk = view.findViewById(R.id.btn_ok);
        btnCancel = view.findViewById(R.id.btn_cancel);
        rvFilter = view.findViewById(R.id.rv_filter);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null)
                    callback.onCanceld();
                dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null)
                    callback.onOkClicked(selectedFilterCategories);
                dismiss();
            }
        });

        this.getDialog().getWindow().setBackgroundDrawableResource(R.drawable.transparent_dialog);

        Bundle arguments = getArguments();
        if (arguments == null)
            return;
        filterCategories = arguments.getParcelableArrayList(ARG_FILTERS);
        selectedFilterCategories = arguments.getParcelableArrayList(ARG_SELECTED_FILTERS);

        if (selectedFilterCategories == null)
            selectedFilterCategories = new ArrayList<>();

        setClicks();

        FeedFilterAdapter filterAdapter = new FeedFilterAdapter(filterCategories, selectedFilterCategories);
        rvFilter.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration itemDecor = new DividerItemDecoration(rvFilter.getContext(), DividerItemDecoration.VERTICAL);
        Drawable mDivider = ContextCompat.getDrawable(rvFilter.getContext(), R.drawable.line_divider);
        itemDecor.setDrawable(mDivider);
        rvFilter.addItemDecoration(itemDecor);
        filterAdapter.setCallback(new FeedFilterAdapter.FeedFilterAdapterCallback() {
            @Override
            public void onItemClicked(ArrayList<FilterCategory> selectedFilter) {
                selectedFilterCategories = new ArrayList<>(selectedFilter);
                setClicks();
            }
        });
        rvFilter.setAdapter(filterAdapter);

    }

    public interface FilterDialogCallback {
        void onOkClicked(ArrayList<FilterCategory> selectedFilter);

        void onCanceld();

    }

    private void setCallback(FilterDialogCallback callback) {
        this.callback = callback;
    }

    private void enable(TextView textView) {
        textView.setBackground(OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.bg_button_unselect_grey)));
        textView.setClickable(true);
    }

    private void disable(TextView textView, boolean disableClick) {
        textView.setBackground(getResources().getDrawable(R.drawable.bg_button_unselect_grey));
        if (disableClick)
            textView.setClickable(false);
    }

    private void setClicks(){
        if (selectedFilterCategories != null && selectedFilterCategories.size() > 0) {
            enable(btnOk);
        }
        else {
            disable(btnOk, true);
            disable(btnCancel,false);
        }
    }
}

