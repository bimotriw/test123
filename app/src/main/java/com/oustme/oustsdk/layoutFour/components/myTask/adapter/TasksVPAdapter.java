package com.oustme.oustsdk.layoutFour.components.myTask.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class TasksVPAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public TasksVPAdapter(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    public void addFragment(Fragment fragment, String title) {
        try {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String character = "";
        if (mFragmentTitleList.size() != 0) {
            character = mFragmentTitleList.get(position);
        }
        return character;
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
