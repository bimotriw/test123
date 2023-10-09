package com.oustme.oustsdk.layoutFour.newnoticeBoard.view;

import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBGroupData;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBMemberData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBGroupData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBMemberData;

import java.util.List;


public interface NewNBMembersListView {
    void noDataFound();
    void setOrUpdateUsersAdapter(List<NewNBMemberData> nbMemberDataList);
    void setOrUpdateGroupAdapter(List<NewNBGroupData> nbGroupDataList, long nbId);
    void createLoader();
    void hideLoader();
    void showPaginationLoader();
    void hidePaginationLoader();

    void noGroupDataFound();
    void noUsersDataFound();
}
