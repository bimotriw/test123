package com.oustme.oustsdk.layoutFour.components.newCatalogue;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.common.FilterCategory;
import com.oustme.oustsdk.layoutFour.components.newCatalogue.adapter.FeedFilterAdapter;
import com.oustme.oustsdk.utils.OustResourceUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class FilterDialogFragment extends DialogFragment {


    private static final String ARG_FILTERS = "filters";
    private static final String ARG_FILTERS1 = "filters1";
    private static final String ARG_SELECTED_FILTERS = "selected filter";
    private ArrayList<FilterCategory> filterCategories;
    private ArrayList<FilterCategory> filterCategories1;
    private HashMap<String, ArrayList<FilterCategory>> filterCategoriesMap;
    private ArrayList<FilterCategory> selectedFilterCategories;

    private FilterDialogCallback callback;

    private RecyclerView rvFilter;
    private TextView tvType, tvStatus;
    private ImageView btnOk;


    public static FilterDialogFragment newInstance(ArrayList<FilterCategory> filterCategories, ArrayList<FilterCategory> filterCategories1, ArrayList<FilterCategory> selectedFilter, FilterDialogCallback callback) {
        FilterDialogFragment filterDialog = new FilterDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ARG_FILTERS, filterCategories);
        bundle.putParcelableArrayList(ARG_FILTERS1, filterCategories1);
        bundle.putParcelableArrayList(ARG_SELECTED_FILTERS, selectedFilter);
        filterDialog.setArguments(bundle);
        filterDialog.setCallback(callback);
        filterDialog.setCancelable(false);
        return filterDialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_filter_dialog, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.bg_dialog_rounded_padded_white);
        }

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        btnOk = view.findViewById(R.id.btn_ok);
        rvFilter = view.findViewById(R.id.rv_filter);
        tvType = view.findViewById(R.id.tv_type);
        tvStatus = view.findViewById(R.id.tv_status);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null)
                    callback.onOkClicked(selectedFilterCategories);
                dismiss();
            }
        });


        Bundle arguments = getArguments();
        if (arguments == null)
            return;
        filterCategories = arguments.getParcelableArrayList(ARG_FILTERS);
        filterCategories1 = arguments.getParcelableArrayList(ARG_FILTERS1);
        selectedFilterCategories = arguments.getParcelableArrayList(ARG_SELECTED_FILTERS);

        if (selectedFilterCategories == null)
            selectedFilterCategories = new ArrayList<>();

        setClicks();


        tvType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedFilterAdapter filterAdapter = new FeedFilterAdapter(filterCategories, selectedFilterCategories);
                filterAdapter.setCallback(filterAdapterCallback);
                rvFilter.setAdapter(filterAdapter);
                tvType.setBackgroundColor(Color.WHITE);
                tvStatus.setBackgroundColor(Color.TRANSPARENT);
            }
        });

        tvStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedFilterAdapter filterAdapter = new FeedFilterAdapter(filterCategories1, selectedFilterCategories);
                filterAdapter.setCallback(filterAdapterCallback);
                rvFilter.setAdapter(filterAdapter);
                tvStatus.setBackgroundColor(Color.WHITE);
                tvType.setBackgroundColor(Color.TRANSPARENT);
            }
        });


        if (filterCategories != null && filterCategories.size() > 0) {
            FeedFilterAdapter filterAdapter = new FeedFilterAdapter(filterCategories, selectedFilterCategories);
            filterAdapter.setCallback(filterAdapterCallback);
            rvFilter.setLayoutManager(new LinearLayoutManager(getContext()));
            rvFilter.setAdapter(filterAdapter);
            tvType.setBackgroundColor(Color.WHITE);
            tvStatus.setBackgroundColor(Color.TRANSPARENT);
        } else {
            tvType.setVisibility(View.GONE);
        }

        if (filterCategories1 != null && filterCategories1.size() > 0) {
            FeedFilterAdapter filterAdapter = new FeedFilterAdapter(filterCategories1, selectedFilterCategories);
            filterAdapter.setCallback(filterAdapterCallback);
            rvFilter.setLayoutManager(new LinearLayoutManager(getContext()));
            rvFilter.setAdapter(filterAdapter);
            tvStatus.setBackgroundColor(Color.WHITE);
            tvType.setBackgroundColor(Color.TRANSPARENT);
        } else {
            tvStatus.setVisibility(View.GONE);
        }

        setCancelable(false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle);

    }


    public interface FilterDialogCallback {
        void onOkClicked(ArrayList<FilterCategory> selectedFilter);
    }

    private void setCallback(FilterDialogCallback callback) {
        this.callback = callback;
    }

    private void enable(ImageView textView) {
        textView.setBackground(OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.bg_button_unselect_grey)));
        textView.setClickable(true);
    }

    private void disable(ImageView textView, boolean disableClick) {
        textView.setBackground(getResources().getDrawable(R.drawable.bg_button_unselect_grey));
        textView.setClickable(true);
    }

    private void setClicks() {
        if (selectedFilterCategories != null && selectedFilterCategories.size() > 0) {
            enable(btnOk);
        } else {
            disable(btnOk, true);
        }
    }

    FeedFilterAdapter.FeedFilterAdapterCallback filterAdapterCallback =
            new FeedFilterAdapter.FeedFilterAdapterCallback() {
                @Override
                public void onItemClicked(ArrayList<FilterCategory> selectedFilter) {
                    selectedFilterCategories = new ArrayList<>(selectedFilter);
                    setClicks();
                }
            };

}
