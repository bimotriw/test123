package com.oustme.oustsdk.layoutFour.navigationFragments;

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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.clevertap.android.sdk.CleverTapAPI;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.catalogue_ui.model.CatalogueComponentModule;
import com.oustme.oustsdk.layoutFour.components.newCatalogue.CatalogViewModel;
import com.oustme.oustsdk.layoutFour.components.newCatalogue.ComponentCatalog;
import com.oustme.oustsdk.layoutFour.data.Navigation;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.utils.OustResourceUtils;

import java.util.HashMap;
import java.util.Objects;


public class CatalogueFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = "CatalogueFragment";

    //View initialize
    SwipeRefreshLayout swipe_container;
    LinearLayout container;
    LinearLayout container_non_scrollable;

    //Dynamic component
    ComponentCatalog componentCatalog;
    CatalogueComponentModule mCatalogDetailData;

    //common for all base fragment
    private static final String ARG_NAV_ITEM = "navItem";

    public CatalogueFragment() {
        // Required empty public constructor
    }

    public static CatalogueFragment newInstance(Navigation navigation) {
        CatalogueFragment fragment = new CatalogueFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_NAV_ITEM, navigation);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_catalogue, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        try {
            CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(OustSdkApplication.getContext());
            HashMap<String, Object> eventUpdate = OustSdkTools.getCleverTapEventData();
            eventUpdate.put("ClickedOnCatalog", true);
            Log.d(TAG, "CleverTap instance: " + eventUpdate);
            if (clevertapDefaultInstance != null) {
                clevertapDefaultInstance.pushEvent("Catalog_Views", eventUpdate);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void initView(View view) {
        try {
            swipe_container = view.findViewById(R.id.swipe_container);
            container = view.findViewById(R.id.container);
//            container_non_scrollable = view.findViewById(R.id.container_non_scrollable);

            swipe_container.setOnRefreshListener(this);
            swipe_container.setColorSchemeColors(OustResourceUtils.getColors());

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onRefresh() {
        try {
            if (container != null) {
                container.removeAllViews();
            }
            componentCatalog = null;
            if (swipe_container.isRefreshing()) {
                swipe_container.setEnabled(false);
            }
            swipe_container.setRefreshing(false);
            setContentComponent();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
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
        try {
            Log.i(TAG, "fetchCatalogue");
            if (mCatalogDetailData != null && mCatalogDetailData.getCatalogueBaseHashMap() != null) {
                mCatalogDetailData.getCatalogueBaseHashMap().clear();
            }
            if (componentCatalog == null) {
                componentCatalog = new ComponentCatalog(getActivity(), null);
                container.addView(componentCatalog);
            }

            CatalogViewModel catalogViewModel = new ViewModelProvider(this).get(CatalogViewModel.class);
            catalogViewModel.init();
            catalogViewModel.getCatalogs().observe(getViewLifecycleOwner(), catalogDetailData -> {
                mCatalogDetailData = catalogDetailData;
                if (mCatalogDetailData != null) {
                    componentCatalog.setRefreshing(true);
                    componentCatalog.setData(mCatalogDetailData);
                } else {
                    componentCatalog.setNoData();
                }

                DisplayMetrics displayMetrics = new DisplayMetrics();
                Objects.requireNonNull(requireActivity()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int screenWidth = displayMetrics.widthPixels;
                swipe_container.setEnabled(true);
                componentCatalog.setFilter(getChildFragmentManager(), screenWidth);
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void onSearch(String query) {
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

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (container != null) {
                container.removeAllViews();
            }
            componentCatalog = null;
            setContentComponent();
        }
    }
}