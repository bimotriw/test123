package com.oustme.oustsdk.layoutFour.newnoticeBoard.data.handlers;

import android.os.AsyncTask;

import com.oustme.oustsdk.activity.common.noticeBoard.callBacks.CommentDataRepository;
import com.oustme.oustsdk.activity.common.noticeBoard.data.handlers.NBFirebaseCommentData;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBCommentData;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBPostData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.callBacks.NewCommentDataRepository;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBCommentData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBPostData;

import java.util.List;

/**
 * Created by oust on 3/8/19.
 */

public class NewGetCommentDataTask extends AsyncTask<Long,Long,Void> {
    private NewCommentDataRepository commentDataRepository;

    public NewGetCommentDataTask(NewCommentDataRepository commentDataRepository) {
        this.commentDataRepository = commentDataRepository;
    }

    @Override
    protected Void doInBackground(Long... longs) {

        new NewNBFirebaseCommentData(longs[0], longs[1], "LIVE") {
            @Override
            public void notifyDataFound(NewNBPostData nbPostData) {

            }
            @Override
            public void notifyCommentDataFound(List<NewNBCommentData> nbCommentDataList) {
                if(nbCommentDataList!=null && nbCommentDataList.size()>0)
                    commentDataRepository.gotCommentData(nbCommentDataList.get(0));
            }
        };

        return null;
    }
}
