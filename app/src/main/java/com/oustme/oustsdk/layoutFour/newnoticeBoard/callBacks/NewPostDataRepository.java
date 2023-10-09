package com.oustme.oustsdk.layoutFour.newnoticeBoard.callBacks;

import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBPostData;

import java.util.ArrayList;
import java.util.List;

public interface NewPostDataRepository {
    void gotAllPostData(ArrayList<NewNBPostData> nbPostDataList);

    //Todo : remove this as we don't need two func for same thing
    void gotPostData(NewNBPostData nbPostData);
}
