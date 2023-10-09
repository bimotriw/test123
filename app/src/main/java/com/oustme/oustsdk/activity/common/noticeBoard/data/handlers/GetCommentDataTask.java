package com.oustme.oustsdk.activity.common.noticeBoard.data.handlers;

import android.os.AsyncTask;

import com.oustme.oustsdk.activity.common.noticeBoard.callBacks.CommentDataRepository;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBCommentData;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBPostData;

import java.util.List;

/**
 * Created by oust on 3/8/19.
 */

public class GetCommentDataTask extends AsyncTask<Long,Long,Void> {
    private CommentDataRepository commentDataRepository;

    public GetCommentDataTask(CommentDataRepository commentDataRepository) {
        this.commentDataRepository = commentDataRepository;
    }

    @Override
    protected Void doInBackground(Long... longs) {

        new NBFirebaseCommentData(longs[0], longs[1], "LIVE") {
            @Override
            public void notifyDataFound(NBPostData nbPostData) {

            }
            @Override
            public void notifyCommentDataFound(List<NBCommentData> nbCommentDataList) {
                if(nbCommentDataList!=null && nbCommentDataList.size()>0)
                    commentDataRepository.gotCommentData(nbCommentDataList.get(0));
            }
        };

        return null;
    }
}
