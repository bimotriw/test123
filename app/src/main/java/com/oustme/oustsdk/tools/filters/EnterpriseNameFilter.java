package com.oustme.oustsdk.tools.filters;

import com.oustme.oustsdk.response.common.FavouriteCardsCourseData;
import com.oustme.oustsdk.response.common.LeaderBoardDataRow;
import com.oustme.oustsdk.response.course.FavCardDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shilpysamaddar on 30/11/17.
 */

public class EnterpriseNameFilter {
    public List<LeaderBoardDataRow> meetCriteria(List<LeaderBoardDataRow> allLeaderBoardDataList, String searchText){
        List<LeaderBoardDataRow> searchedNameOfLeaderboardDataList=new ArrayList<>();

        for(int i=0; i<allLeaderBoardDataList.size(); i++){
            LeaderBoardDataRow leaderBoardDataRow=new LeaderBoardDataRow();
                    if ((allLeaderBoardDataList.get(i).getDisplayName().toLowerCase()).contains(searchText.toLowerCase())){
                        leaderBoardDataRow=allLeaderBoardDataList.get(i);
                        searchedNameOfLeaderboardDataList.add(leaderBoardDataRow);
            }
        }
        return searchedNameOfLeaderboardDataList;
    }
}
