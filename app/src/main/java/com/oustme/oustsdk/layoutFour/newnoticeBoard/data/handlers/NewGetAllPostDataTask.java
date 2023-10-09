package com.oustme.oustsdk.layoutFour.newnoticeBoard.data.handlers;

import android.os.AsyncTask;
import android.util.Log;


import com.oustme.oustsdk.layoutFour.newnoticeBoard.callBacks.NewPostDataRepository;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.request.NewPostViewData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBCommentData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBPostData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBTopicData;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class NewGetAllPostDataTask extends AsyncTask<NewNBTopicData, Void, Void> {

    private HashMap<Long, NewNBPostData> nbPostDataHashMap;
    private NewPostDataRepository postDataRepository;

    public NewGetAllPostDataTask(NewPostDataRepository postDataRepository) {
        this.postDataRepository = postDataRepository;
        this.nbPostDataHashMap = new HashMap<>();
    }

    @Override
    protected Void doInBackground(final NewNBTopicData... nbTopicData) {
        if (nbTopicData[0] != null && nbTopicData[0].getPostUpdateData() != null) {
            Log.d("NBTopicDetailed4-->", "" + nbTopicData);
            if (nbTopicData[0].getPostUpdateData() != null && nbTopicData[0].getPostUpdateData().size() > 0) {
                for (int i = 0; i < nbTopicData[0].getPostUpdateData().size(); i++) {
                    new NewNBFirebasePostData(nbTopicData[0].getPostUpdateData().get(i).getId(), 1, OustAppState.getInstance().getActiveUser().getStudentKey(), "SINGLETON") {
                        @Override
                        public void notifyPostDataFound(NewNBPostData nbPostData) {
                            List<NewPostViewData> postViewDataList = RoomHelper.newGetPostViewDataById(nbPostData.getNbId(), nbPostData.getId());
                            if (postViewDataList != null && postViewDataList.size() > 0) {
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

                            nbPostDataHashMap.put(nbPostData.getId(), nbPostData);
//                        Log.d("NBTopicDetailed004-->", "" + nbPostData.getId() + " : " + nbPostData);
                            if (nbPostDataHashMap.size() == nbTopicData[0].getPostUpdateData().size()) {
                                ArrayList<NewNBPostData> postDataList = new ArrayList<>(nbPostDataHashMap.values());
                                if (postDataList != null) {
                                    Collections.sort(postDataList, nbPostDateComparator);
                                    if (postDataRepository != null) {
                                        postDataRepository.gotAllPostData(postDataList);
                                    }
                                }
                            }
                        }
                    };
                }
            }
        }
        return null;
    }

    public Comparator<NewNBCommentData> nbCommentDataComparator = new Comparator<NewNBCommentData>() {
        public int compare(NewNBCommentData s1, NewNBCommentData s2) {
            return Long.valueOf(s2.getCommentedOn()).compareTo(Long.valueOf(s1.getCommentedOn()));
        }
    };

    public Comparator<NewNBPostData> nbPostDateComparator = new Comparator<NewNBPostData>() {
        public int compare(NewNBPostData s1, NewNBPostData s2) {
            return Long.valueOf(s2.getUpdatedOn()).compareTo(Long.valueOf(s1.getUpdatedOn()));
        }
    };

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

    }
}
