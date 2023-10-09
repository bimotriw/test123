package com.oustme.oustsdk.adapter.FFContest;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.oustme.oustsdk.firebase.FFContest.FFCFirebaseQuestionResponse;
import com.oustme.oustsdk.firebase.FFContest.FFCFirebaseResponse;
import com.oustme.oustsdk.firebase.FFContest.FastestFingerContestData;
import com.oustme.oustsdk.fragments.FFContest.FFContestAnswerFragment;
import com.oustme.oustsdk.interfaces.common.FFContestAnswerCallback;

import java.util.List;

/**
 * Created by admin on 04/08/17.
 */

public class FFContestAnswerViewAdapter extends FragmentStatePagerAdapter {
    private FastestFingerContestData fastestFingerContestData;
    private FFContestAnswerCallback ffContestAnswerCallback;
    private List<FFCFirebaseResponse> ffcFirebaseResponseList;
    private List<FFCFirebaseQuestionResponse> ffcFirebaseQuestionResponseList;

    public FFContestAnswerViewAdapter(FragmentManager supportFragmentManager, FastestFingerContestData fastestFingerContestData,
                                      FFContestAnswerCallback ffContestAnswerCallback, List<FFCFirebaseResponse> ffcFirebaseResponseList, List<FFCFirebaseQuestionResponse> ffcFirebaseQuestionResponseList) {
        super(supportFragmentManager);
        this.ffContestAnswerCallback = ffContestAnswerCallback;
        this.fastestFingerContestData = fastestFingerContestData;
        this.ffcFirebaseResponseList = ffcFirebaseResponseList;
        this.ffcFirebaseQuestionResponseList = ffcFirebaseQuestionResponseList;

    }


    @Override
    public Fragment getItem(int position) {
        FFContestAnswerFragment ffContestAnswerFragment = new FFContestAnswerFragment();
        ffContestAnswerFragment.setFastestFingerContestData(fastestFingerContestData);
        ffContestAnswerFragment.setFfContestAnswerCallback(ffContestAnswerCallback);
        if (fastestFingerContestData != null && fastestFingerContestData.getqIds() != null)
            ffContestAnswerFragment.setQuestionNo(position, fastestFingerContestData.getqIds().size());
        if (ffcFirebaseQuestionResponseList != null && ffcFirebaseQuestionResponseList.get(position) != null)
            ffContestAnswerFragment.setFfcFirebaseQuestionResponse(ffcFirebaseQuestionResponseList.get(position));
        boolean gotMyData = false;
        try {
            if ((ffcFirebaseQuestionResponseList != null) && (ffcFirebaseQuestionResponseList.size() > position)) {
                ffContestAnswerFragment.setFfcFirebaseResponse(ffcFirebaseResponseList.get(position));
            }
            if (!gotMyData) {
                if ((fastestFingerContestData.getqIds() != null) && (fastestFingerContestData.getqIds().size() > position)) {
                    ffContestAnswerFragment.setFfcFirebaseResponse(ffcFirebaseResponseList.get(position));
                }
            }
        }catch (Exception e){}
        return ffContestAnswerFragment;
    }

    @Override
    public int getCount() {
        int n1 = 0;
        if ((fastestFingerContestData.getqIds() != null)) {
            n1 = fastestFingerContestData.getqIds().size();
        }
        return n1;
    }

}
