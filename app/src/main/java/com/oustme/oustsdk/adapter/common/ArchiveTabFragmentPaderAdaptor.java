package com.oustme.oustsdk.adapter.common;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.oustme.oustsdk.fragments.assessments.ArchiveAssessmentFragment;
import com.oustme.oustsdk.fragments.courses.ArchiveCourseFragment;
import com.oustme.oustsdk.interfaces.common.NewLandingInterface;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;

/**
 * Created by shilpysamaddar on 23/03/17.
 */

public class ArchiveTabFragmentPaderAdaptor extends FragmentStatePagerAdapter {

    private final String TAG = NewTabFragmentPagerAdapter.class.getName();

    private int mNumOfTabs;
    private ActiveUser activeUser;

    private NewLandingInterface newLandingInterface;

    public ArchiveTabFragmentPaderAdaptor(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.activeUser = OustAppState.getInstance().getActiveUser();
        this.newLandingInterface = newLandingInterface;
    }

    public void notifyChanges(int mNumOfTabs){
        this.mNumOfTabs=mNumOfTabs;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {

        if(position==0) {
            ArchiveCourseFragment landingFragment = new ArchiveCourseFragment();
            return landingFragment;
        }
        else if(position==1) {
            ArchiveAssessmentFragment landingFragment1 = new ArchiveAssessmentFragment();
//                landingFragment1.setNewLandingInterface(newLandingInterface);
            return landingFragment1;
        }
        else {
            return null;

        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}

