package com.oustme.oustsdk.layoutFour.components.dialogFragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.clevertap.android.sdk.CleverTapAPI;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.layoutFour.components.dialogFragments.adapter.SortFilterDialogAdapter;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.utils.OustResourceUtils;

import java.util.HashMap;
import java.util.Objects;


public class SortFilterDialogFragment extends BottomSheetDialogFragment {
    private static final String ARG_OPTIONS_DATA = "options_data";
    public static final String TAG = "SortFilterDialogFragment";
    LinearLayout dialog_ok;
    int sortPosition;
    private int selectedIndex; // add this
    private FilterDialogCallback1 callback;

    // TODO: Customize parameters
    public static SortFilterDialogFragment newInstance(String[] optionsData, FilterDialogCallback1 filterByGroup1) {
        final SortFilterDialogFragment fragment = new SortFilterDialogFragment();
        final Bundle args = new Bundle();
        args.putStringArray(ARG_OPTIONS_DATA, optionsData);
        fragment.setCallback(filterByGroup1);
        fragment.setArguments(args);
        return fragment;
    }

    public interface FilterDialogCallback1 {
        void onOkClicked(int position);
    }

    private void setCallback(FilterDialogCallback1 callback) {
        this.callback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bottom_sort_dialog_list_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        ImageView fragment_close = view.findViewById(R.id.fragment_close);
        RecyclerView rv_filter = view.findViewById(R.id.rv_filter);
        LinearLayout dialog_cancel = view.findViewById(R.id.dialog_cancel);
        dialog_ok = view.findViewById(R.id.dialog_ok);
        Bundle arguments = getArguments();
        String[] positionsData = Objects.requireNonNull(arguments).getStringArray(ARG_OPTIONS_DATA);

        disable(dialog_ok, false);

        if (OustStaticVariableHandling.getInstance().getSortPosition() != -1) {
            enable(dialog_ok);
        }

        rv_filter.setLayoutManager(new LinearLayoutManager(getContext()));
        SortFilterDialogAdapter itemAdapter = new SortFilterDialogAdapter(positionsData, new SortFilterDialogAdapter.SortFilterDialogListener() {
            @Override
            public void onItemClicked(int position) {
                if (OustStaticVariableHandling.getInstance().getSortPosition() != -1) {
                    sortPosition = position;
                    enable(dialog_ok);
                }
            }

        });
        rv_filter.setAdapter(itemAdapter);

        dialog_ok.setOnClickListener(v -> {
            if (callback != null) {
                callback.onOkClicked(sortPosition);
                try {
                    if (sortPosition == 1) {
                        try {
                            CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(OustSdkApplication.getContext());
                            HashMap<String, Object> eventUpdate = OustSdkTools.getCleverTapEventData();
                            eventUpdate.put("ClickedOnLeaderBoard", true);
                            eventUpdate.put("SortByRank", true);
                            Log.d("TAG", "CleverTap instance: " + eventUpdate.toString());
                            if (clevertapDefaultInstance != null) {
                                clevertapDefaultInstance.pushEvent("Leaderboard_SortByRank", eventUpdate);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    } else if (sortPosition == 2) {
                        try {
                            CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(OustSdkApplication.getContext());
                            HashMap<String, Object> eventUpdate = OustSdkTools.getCleverTapEventData();
                            eventUpdate.put("ClickedOnLeaderBoard", true);
                            eventUpdate.put("SortByName", true);
                            Log.d("TAG", "CleverTap instance: " + eventUpdate.toString());
                            if (clevertapDefaultInstance != null) {
                                clevertapDefaultInstance.pushEvent("Leaderboard_SortByName", eventUpdate);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    } else if (sortPosition == 3) {
                        try {
                            CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(OustSdkApplication.getContext());
                            HashMap<String, Object> eventUpdate = OustSdkTools.getCleverTapEventData();
                            eventUpdate.put("ClickedOnLeaderBoard", true);
                            eventUpdate.put("SortByScore", true);
                            Log.d("TAG", "CleverTap instance: " + eventUpdate.toString());
                            if (clevertapDefaultInstance != null) {
                                clevertapDefaultInstance.pushEvent("Leaderboard_SortByScore", eventUpdate);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }
            dismiss();
        });

        dialog_cancel.setOnClickListener(v -> dismiss());
        fragment_close.setOnClickListener(v -> dismiss());

    }

    private void enable(LinearLayout linearLayout) {
        linearLayout.setBackground(OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.button_rounded_ten_bg)));
        linearLayout.setClickable(true);
    }

    private void disable(LinearLayout linearLayout, boolean isClickable) {
        linearLayout.setBackground(getResources().getDrawable(R.drawable.button_rounded_ten_bg));
        linearLayout.setClickable(isClickable);
    }
}
