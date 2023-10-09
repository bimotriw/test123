package com.oustme.oustsdk.layoutFour.interfaces;

import com.oustme.oustsdk.layoutFour.data.response.GroupDataList;

public interface LeaderBoardCallBacks {

    void groupFilterData(GroupDataList filterGroup);

    void onClickListener(int position);

    void hideFilterSort(boolean b);
}
