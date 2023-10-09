package com.oustme.oustsdk.layoutFour.components.myTask.fragment.catalogue;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.catalogue_ui.model.CatalogueComponentModule;
import com.oustme.oustsdk.layoutFour.components.newCatalogue.CatalogViewModel;
import com.oustme.oustsdk.layoutFour.components.newCatalogue.ComponentCatalog;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.Objects;

public class CatalogueLearningFragment extends Fragment {

    private static final String TAG = "CatalogueFragment";

    //View initialize
    LinearLayout container;
    LinearLayout container_non_scrollable;

    //Dynamic component
    ComponentCatalog componentCatalog;
    CatalogueComponentModule mCatalogDetailData;


    public CatalogueLearningFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_catalogue_my_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
    }

    private void initView(View view) {
        try {
            container = view.findViewById(R.id.container);
            container_non_scrollable = view.findViewById(R.id.container_non_scrollable);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    private void initData() {
        try {
            componentCatalog = null;
            setContentComponent();

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void setContentComponent() {
        try {
            fetchCatalogue();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void fetchCatalogue() {

        Log.i(TAG, "fetchCatalogue");
        CatalogViewModel catalogViewModel = new ViewModelProvider(this).get(CatalogViewModel.class);
        catalogViewModel.init();
        catalogViewModel.getCatalogs().observe(getViewLifecycleOwner(), catalogDetailData -> {
            mCatalogDetailData = catalogDetailData;
            if (componentCatalog == null) {
                componentCatalog = new ComponentCatalog(getActivity(), null);
                container.addView(componentCatalog);
            }
            if (mCatalogDetailData != null) {
                componentCatalog.setRefreshing(true);
                componentCatalog.setData(mCatalogDetailData);
            } else {
                componentCatalog.setNoData();
            }

            DisplayMetrics displayMetrics = new DisplayMetrics();
            Objects.requireNonNull(requireActivity()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenWidth = displayMetrics.widthPixels;

            componentCatalog.setFilter(getChildFragmentManager(), screenWidth);
        });
    }

    public void setCatalogueData(String query) {
        try {
            if (query != null) {
                if (componentCatalog != null) {
                    componentCatalog.onCatalogSearch(query);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}