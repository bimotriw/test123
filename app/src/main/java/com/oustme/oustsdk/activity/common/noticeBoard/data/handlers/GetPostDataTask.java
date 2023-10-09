package com.oustme.oustsdk.activity.common.noticeBoard.data.handlers;

import android.os.AsyncTask;

import com.oustme.oustsdk.activity.common.noticeBoard.callBacks.PostDataRepository;
import com.oustme.oustsdk.activity.common.noticeBoard.model.request.PostViewData;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBCommentData;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBPostData;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by oust on 2/27/19.
 */

public class GetPostDataTask extends AsyncTask<Long,Void,Void> {

    private PostDataRepository postDataRepository;

    public GetPostDataTask(PostDataRepository postDataRepository) {
        this.postDataRepository = postDataRepository;
    }

    @Override
    protected Void doInBackground(Long... longs) {
        new NBFirebasePostData(longs[0],0, OustAppState.getInstance().getActiveUser().getStudentKey(),"LIVE") {
            @Override
            public void notifyPostDataFound(NBPostData nbPostData) {
                List<PostViewData> postViewDataList = RoomHelper.getPostViewDataById(nbPostData.getNbId(),nbPostData.getId());
                if(postViewDataList!=null && postViewDataList.size()>0){
                    for(int i=0;i<postViewDataList.size();i++){
                        PostViewData postViewData = postViewDataList.get(i);
                        if(postViewData!=null){
                            if(postViewData.isPostTypeLike()){
                                if(postViewData.isLike()){
                                    nbPostData.incrementLikeCount();
                                }else{
                                    nbPostData.decrementLikeCount();
                                }
                                nbPostData.setHasLiked(postViewData.isLike());
                            }else if(postViewData.isPostTypeComment()){
                                nbPostData.setHasCommented(true);
                                nbPostData.incrementCommentCount();
                                nbPostData.addOfflineComment(postViewData.getNbCommentData());
                            }else if(postViewData.isPostTypeShare()){
                                nbPostData.setHasShared(true);
                                nbPostData.incrementShareCount();
                            }else if(postViewData.isPostTypeCommentDelete()){
                                try {
                                    if (nbPostData.getNbCommentDataList() != null) {
                                        for (int k = 0; k < nbPostData.getNbCommentDataList().size(); k++) {
                                            if (nbPostData.getNbCommentDataList().get(k).getId() == postViewData.getNbCommentData().getId()) {
                                                nbPostData.getNbCommentDataList().remove(k);
                                            }
                                        }
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                }
                            }
                        }
                    }
                }
                if(nbPostData.getNbCommentDataList()!=null){
                    Collections.sort(nbPostData.getNbCommentDataList(),nbCommentDataComparator);
                }

                postDataRepository.gotPostData(nbPostData);

            }
        };

        return null;
    }
    public Comparator<NBCommentData> nbCommentDataComparator = new Comparator<NBCommentData>() {
        public int compare(NBCommentData s1, NBCommentData s2) {
            return Long.valueOf(s2.getCommentedOn()).compareTo(Long.valueOf(s1.getCommentedOn()));
        }
    };
}
