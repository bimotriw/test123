package com.oustme.oustsdk.my_tasks.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.my_tasks.MyTasksScreen;
import com.oustme.oustsdk.my_tasks.adapter.TaskModuleAdapter;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.ArrayList;


public class SurveyModuleFragment extends Fragment {

    private ArrayList<CommonLandingData> surveyList = new ArrayList<>();
    TextView no_module;
    RecyclerView survey_list_rv;

    public SurveyModuleFragment() {
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
            surveyList = myTasksScreen.getSurveyList();

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        View view = inflater.inflate(R.layout.fragment_new_module, container, false);
        survey_list_rv = view.findViewById(R.id.survey_list_rv);
        no_module = view.findViewById(R.id.no_module);

        if (surveyList != null && surveyList.size() != 0) {

            TaskModuleAdapter adapter = new TaskModuleAdapter();
            adapter.setTaskRecyclerAdapter(surveyList, getActivity(), 1, 0);


            no_module.setVisibility(View.GONE);
            survey_list_rv.setVisibility(View.VISIBLE);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            survey_list_rv.setLayoutManager(mLayoutManager);
            survey_list_rv.setItemAnimator(new DefaultItemAnimator());
            survey_list_rv.setAdapter(adapter);

        } else {

            no_module.setVisibility(View.VISIBLE);
            survey_list_rv.setVisibility(View.GONE);

        }
        return view;
    }
}
