package com.oustme.oustsdk.layoutFour.newnoticeBoard.data.handlers;

import android.os.AsyncTask;

import com.oustme.oustsdk.layoutFour.newnoticeBoard.callBacks.NewPostDataRepository;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.request.NewPostViewData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBCommentData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBPostData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.presenters.NewNBPostDetailsPresenter;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class NewGetPostDataTask extends AsyncTask<Long, Void, Void> {

    private NewPostDataRepository postDataRepository;

    public NewGetPostDataTask(NewPostDataRepository postDataRepository) {
        this.postDataRepository = postDataRepository;
    }

    @Override
    protected Void doInBackground(Long... longs) {
        new NewNBFirebasePostData(longs[0], 0, OustAppState.getInstance().getActiveUser().getStudentKey(), "LIVE") {
            @Override
            public void notifyPostDataFound(NewNBPostData nbPostData) {
                List<NewPostViewData> postViewDataList = RoomHelper.newGetPostViewDataById(nbPostData.getNbId(), nbPostData.getId());
                if (postViewDataList.size() > 0) {
                    for (int i = 0; i < postViewDataList.size(); i++) {
                        NewPostViewData postViewData = postViewDataList.get(i);
                        if (postViewData != null) {
                            if (postViewData.isPostTypeLike()) {
                                if (postViewData.isLike()) {
                                    nbPostData.incrementLikeCount();
                                } else {
                                    nbPostData.decrementLikeCount();
                                }
                                nbPostData.setHasLiked(postViewData.isLike());
                            } else if (postViewData.isPostTypeComment()) {
                                nbPostData.setHasCommented(true);
                                nbPostData.incrementCommentCount();
                                nbPostData.addOfflineComment(postViewData.getNbCommentData());
                            } else if (postViewData.isPostTypeShare()) {
                                nbPostData.setHasShared(true);
                                nbPostData.incrementShareCount();
                            } else if (postViewData.isPostTypeCommentDelete()) {
                                try {
                                    if (nbPostData.getNbCommentDataList() != null) {
                                        for (int k = 0; k < nbPostData.getNbCommentDataList().size(); k++) {
                                            if (nbPostData.getNbCommentDataList().get(k).getId() == postViewData.getNbCommentData().getId()) {
                                                nbPostData.getNbCommentDataList().remove(k);
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                }
                            }
                        }
                    }
                }
                if (nbPostData.getNbCommentDataList() != null) {
                    Collections.sort(nbPostData.getNbCommentDataList(), nbCommentDataComparator);
                }

                postDataRepository.gotPostData(nbPostData);
            }
        };

        return null;
    }

    public Comparator<NewNBCommentData> nbCommentDataComparator = new Comparator<NewNBCommentData>() {
        public int compare(NewNBCommentData s1, NewNBCommentData s2) {
            return Long.valueOf(s2.getCommentedOn()).compareTo(Long.valueOf(s1.getCommentedOn()));
        }
    };
}
