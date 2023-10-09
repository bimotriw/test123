package com.oustme.oustsdk.adapter.common;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.oustme.oustsdk.fragments.assessments.UpAssessmentFragment;
import com.oustme.oustsdk.fragments.courses.UpCourseFragment;
import com.oustme.oustsdk.response.common.FriendProfileResponceRow;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;

import java.util.List;

/**
 * Created by shilpysamaddar on 21/03/17.
 */

public class UpNewTabFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs = 0;
    private ActiveUser activeUser;
    private List<FriendProfileResponceRow> friendProfileResponceRowList;

    public UpNewTabFragmentPagerAdapter(FragmentManager fm, int NumOfTabs, List<FriendProfileResponceRow> friendProfileResponceRowList) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.activeUser = OustAppState.getInstance().getActiveUser();
        this.friendProfileResponceRowList = friendProfileResponceRowList;
    }

    public void notifyChanges(int mNumOfTabs) {
        this.mNumOfTabs = mNumOfTabs;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {

        if (position == 0) {
            UpCourseFragment upCourseFragment = new UpCourseFragment();
            upCourseFragment.setUserProfileData(friendProfileResponceRowList);
            return upCourseFragment;
        }
        if (position == 1) {
            // not used now
            UpAssessmentFragment upAssesmentFragment = new UpAssessmentFragment();
            upAssesmentFragment.setUserProfileData(null);
            return upAssesmentFragment;
        } else return null;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
