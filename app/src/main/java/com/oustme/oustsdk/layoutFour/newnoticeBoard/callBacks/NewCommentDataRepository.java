package com.oustme.oustsdk.layoutFour.newnoticeBoard.callBacks;


import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBCommentData;

public interface NewCommentDataRepository {
    void gotCommentData(NewNBCommentData nbCommentData);

}
