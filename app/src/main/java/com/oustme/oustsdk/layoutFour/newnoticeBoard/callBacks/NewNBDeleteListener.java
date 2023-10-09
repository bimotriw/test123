package com.oustme.oustsdk.layoutFour.newnoticeBoard.callBacks;

import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.request.NewPostViewData;


public interface NewNBDeleteListener {
    void onDelete(NewPostViewData postViewData);
    void onDeleteCancel();
}
