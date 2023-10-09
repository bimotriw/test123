package com.oustme.oustsdk.fragments.common;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.adapter.common.EventLeaderboardFragmentAdaptor;
import com.oustme.oustsdk.response.common.LeaderBoardDataRow;
import com.oustme.oustsdk.tools.OustStrings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by shilpysamaddar on 21/03/17.
 */

public class EventLeaderBoardFragment extends Fragment {

    RecyclerView eventboard_leaderBoardRecyclerView;
    TextView textView,nouser_text;

    private List<LeaderBoardDataRow> leaderBaordDataRowList;
    public void setLeaderBaordDataRowList(List<LeaderBoardDataRow> leaderBaordDataRowList) {
        this.leaderBaordDataRowList = leaderBaordDataRowList;
    }

    private int tabPosition;
    public void setTabPosition(int tabPosition) {
        this.tabPosition = tabPosition;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.notificationsfragment,container,false);

        initViews(view);
        initEventLeaderboardFragment();
        return view;
    }

    private void initViews(View view) {
        nouser_text= view.findViewById(R.id.nouser_text);
        textView= view.findViewById(R.id.noAlertsBtnText);
        eventboard_leaderBoardRecyclerView= view.findViewById(R.id.alertsList);
    }

    public void initEventLeaderboardFragment(){
        createList();
    }

    private void createList(){
        if (((leaderBaordDataRowList!=null)&&(leaderBaordDataRowList.size()>0))) {
            textView.setVisibility(View.GONE);
            boolean isTimeBasedLB=false;
            if(tabPosition==1){
                isTimeBasedLB=true;
                LBListFilter lbListFilter = new LBListFilter();
                leaderBaordDataRowList = lbListFilter.meetCriteria(leaderBaordDataRowList);
                Collections.sort(leaderBaordDataRowList, listSort);
            }
            if((leaderBaordDataRowList!=null)&&(leaderBaordDataRowList.size()>0)) {
                EventLeaderboardFragmentAdaptor leaderBoardAllAdapter = new EventLeaderboardFragmentAdaptor(leaderBaordDataRowList, isTimeBasedLB);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                eventboard_leaderBoardRecyclerView.setLayoutManager(mLayoutManager);
                eventboard_leaderBoardRecyclerView.setItemAnimator(new DefaultItemAnimator());
                eventboard_leaderBoardRecyclerView.setAdapter(leaderBoardAllAdapter);
            } else {
                nouser_text.setVisibility(View.VISIBLE);
                nouser_text.setText(OustStrings.getString("nouser_leaderboard_text"));
            }

        }else {
            textView.setVisibility(View.VISIBLE);
            textView.setText(OustStrings.getString("no_data_found"));
        }
    }

    public Comparator<LeaderBoardDataRow> listSort = new Comparator<LeaderBoardDataRow>() {
        public int compare(LeaderBoardDataRow s1, LeaderBoardDataRow s2) {
            if(s1.getCompletionTime()==null){
                return -1;
            }
            if(s2.getCompletionTime()==null){
                return 1;
            }
            if(s1.getCompletionTime()==s2.getCompletionTime()){
                return 0;
            }
            return s1.getCompletionTime().compareTo(s2.getCompletionTime());
        }
    };

    public class LBListFilter {
        public List<LeaderBoardDataRow> meetCriteria(List<LeaderBoardDataRow> allLBList) {
            List<LeaderBoardDataRow> newList = new ArrayList<>();
            for (LeaderBoardDataRow dt : allLBList) {
                if((dt!=null)&&(dt.getCompletionTime()!=null)&&(!dt.getCompletionTime().isEmpty())) {
                    newList.add(dt);
                }
            }
            return newList;
        }
    }
}
