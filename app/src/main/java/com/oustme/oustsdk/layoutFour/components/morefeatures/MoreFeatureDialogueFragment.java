package com.oustme.oustsdk.layoutFour.components.morefeatures;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.oustme.oustsdk.R;

public class MoreFeatureDialogueFragment extends BottomSheetDialogFragment {

    private RecyclerView rvMoreFeature;

    public static MoreFeatureDialogueFragment newInstance() {

        Bundle args = new Bundle();

        MoreFeatureDialogueFragment fragment = new MoreFeatureDialogueFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_more_features, container, false);
        rvMoreFeature = view.findViewById(R.id.rv_filter);


        return view;
    }
}
