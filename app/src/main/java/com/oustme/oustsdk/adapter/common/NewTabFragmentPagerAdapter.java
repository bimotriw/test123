package com.oustme.oustsdk.adapter.common;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;


import com.oustme.oustsdk.fragments.assessments.LandingAssessmentFragment;
import com.oustme.oustsdk.fragments.courses.LandingCourseFragment;
import com.oustme.oustsdk.interfaces.common.DataLoaderNotifier;
import com.oustme.oustsdk.interfaces.common.NewLandingInterface;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStrings;

/**
 * Created by shilpysamaddar on 08/03/17.
 */

public class NewTabFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private final String TAG = NewTabFragmentPagerAdapter.class.getName();
    private int mNumOfTabs;
    private NewLandingInterface newLandingInterface;
    private boolean conatinCourse, containAssessment;
    private int courseAddedd, assessmentAdded;
    private DataLoaderNotifier dataLoaderNotifier;
    private LandingCourseFragment courseFragment;
    private LandingAssessmentFragment assessmentFragment;

    public LandingCourseFragment getCourseFragment() {
        return courseFragment;
    }

    public LandingAssessmentFragment getAssessmentFragment() {
        return assessmentFragment;
    }

    public NewTabFragmentPagerAdapter(FragmentManager fm, int NumOfTabs, NewLandingInterface newLandingInterface, String tabNameStr) {
        super(fm);
        if(tabNameStr.equalsIgnoreCase(OustStrings.getString("courses_text"))){
            conatinCourse=true;
        }else if(tabNameStr.equalsIgnoreCase(OustStrings.getString("challenges_text"))){
            containAssessment=true;
        }
        this.mNumOfTabs = NumOfTabs;
        this.newLandingInterface = newLandingInterface;
    }

    public void setDataLoaderNotifier(DataLoaderNotifier dataLoaderNotifier){
        this.dataLoaderNotifier=dataLoaderNotifier;
    }


    public void notifyChanges(int mNumOfTabs,String tabNameStr){
        this.mNumOfTabs=mNumOfTabs;
        if(tabNameStr.equalsIgnoreCase(OustStrings.getString("courses_text"))){
            conatinCourse=true;
        }else if(tabNameStr.equalsIgnoreCase(OustStrings.getString("challenges_text"))){
            containAssessment=true;
        }
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        try {
            if (position == 0) {
                if(conatinCourse&&(((courseAddedd==0)&&(assessmentAdded==0))||((courseAddedd==1)&&(assessmentAdded!=1)))) {
                    courseAddedd=1;
                        courseFragment = new LandingCourseFragment();
                        courseFragment.setDataLoaderNotifier(dataLoaderNotifier);
                        courseFragment.setNewLandingInterface(newLandingInterface);
                    fragment = courseFragment;
                }else if(containAssessment&&(((assessmentAdded==0)&&(courseAddedd==0))||((assessmentAdded==1)&&(courseAddedd!=1)))){
                    assessmentAdded=1;
                        assessmentFragment = new LandingAssessmentFragment();
                        assessmentFragment.setNewLandingInterface(newLandingInterface);
                        assessmentFragment.setDataLoaderNotifier(dataLoaderNotifier);
                    fragment = assessmentFragment;
                }
            } else if (position == 1) {
                if(conatinCourse&&(((courseAddedd==0)&&(assessmentAdded==1))||((courseAddedd==2)&&(assessmentAdded==1)))) {
                    courseAddedd=2;
                        courseFragment = new LandingCourseFragment();
                        courseFragment.setNewLandingInterface(newLandingInterface);
                        courseFragment.setDataLoaderNotifier(dataLoaderNotifier);
                        OustAppState.getInstance().setLandingFragmentInit(true);
                    fragment = courseFragment;
                }else if(containAssessment&&(((assessmentAdded==0)&&(courseAddedd==1))||((courseAddedd==1)&&(assessmentAdded==2)))){
                    assessmentAdded=2;
                        assessmentFragment = new LandingAssessmentFragment();
                        assessmentFragment.setNewLandingInterface(newLandingInterface);
                        assessmentFragment.setDataLoaderNotifier(dataLoaderNotifier);
                    fragment = assessmentFragment;
                }
            }
            return fragment;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return fragment;
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        try{
            super.finishUpdate(container);
        } catch (NullPointerException nullPointerException){
            System.out.println("Catch the NullPointerException in FragmentPagerAdapter.finishUpdate");
        }
    }
    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
