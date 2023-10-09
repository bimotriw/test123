package com.oustme.oustsdk.interfaces.common;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.oustme.oustsdk.firebase.common.CommonLandingData;

import java.util.List;

/**
 * Created by oust on 1/22/18.
 */

public class CommonLandingDataCallback extends DiffUtil.Callback {

    private  List<CommonLandingData> mOldList;
    private  List<CommonLandingData> mNewList;

    public CommonLandingDataCallback(List<CommonLandingData> mOldList, List<CommonLandingData> mNewList) {
        this.mOldList = mOldList;
        this.mNewList = mNewList;
    }

    @Override
    public int getOldListSize() {
        return mOldList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        if(mOldList!=null && mOldList.get(oldItemPosition)!=null && mNewList!=null && mNewList.get(newItemPosition)!=null) {
            return mOldList.get(oldItemPosition).getId() == mNewList.get(
                    newItemPosition).getId();
        }else{
            return false;
        }
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        final CommonLandingData oldEmployee = mOldList.get(oldItemPosition);
        final CommonLandingData newEmployee = mNewList.get(newItemPosition);
        if(oldEmployee!=null && newEmployee!=null) {
            return oldEmployee.getName().equals(newEmployee.getName());
        }
        return false;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
