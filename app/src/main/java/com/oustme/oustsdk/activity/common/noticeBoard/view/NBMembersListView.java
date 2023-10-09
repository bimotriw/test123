package com.oustme.oustsdk.activity.common.noticeBoard.view;

import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBGroupData;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBMemberData;

import java.util.List;

/**
 * Created by oust on 3/6/19.
 */

public interface NBMembersListView {
    void noDataFound();
    void setOrUpdateUsersAdapter(List<NBMemberData> nbMemberDataList);
    void setOrUpdateGroupAdapter(List<NBGroupData> nbGroupDataList, long nbId);
    void createLoader();
    void hideLoader();
    void showPaginationLoader();
    void hidePaginationLoader();

    void noGroupDataFound();
    void noUsersDataFound();
}
