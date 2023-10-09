package com.oustme.oustsdk.activity.common.noticeBoard.data.handlers;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.oustme.oustsdk.activity.common.noticeBoard.model.request.PostViewData;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBCommentData;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBReplyData;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


/**
 * Created by oust on 2/26/19.
 */

public abstract class NBFirebaseCommentData extends NoticeBoardPostDb {

    private final String POST_COMMENT_NODE = "/userPostComment/post{postId}/comments";
    private final String NB_COMMENT_NODE = "/userPostComment/post{postId}/comments/comment{commentId}";

    private long postId;
    private String firebaseListenerType;
    private long commentId;

    public NBFirebaseCommentData(long postId, int commentCount, String firebaseListenerType) {
        super(commentCount);
        this.postId = postId;
        this.firebaseListenerType = firebaseListenerType;
    }

    public NBFirebaseCommentData(long postId, String firebaseListenerType) {
        this.postId = postId;
        this.firebaseListenerType = firebaseListenerType;
    }

    public NBFirebaseCommentData(long postId, long commentId, String firebaseListenerType) {
        this.postId = postId;
        this.firebaseListenerType = firebaseListenerType;
        this.commentId = commentId;

        getCommentData();
    }

    private void getCommentData() {
        String message = NB_COMMENT_NODE;
        message = message.replace("{postId}", postId + "");
        message = message.replace("{commentId}", commentId + "");

        ValueEventListener myassessmentListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        Map<String, Object> commentMap = (Map<String, Object>) dataSnapshot.getValue();
                        if (commentMap != null) {
                            NBCommentData nbCommentData = new NBCommentData();
                            nbCommentData.initData(commentMap, postId);
                            List<PostViewData> postViewDataList = RoomHelper.getPostViewReplyDataByPostId(postId, commentId);
                            if (postViewDataList != null && postViewDataList.size() > 0) {
                                if (nbCommentData.getNbReplyData() == null) {
                                    nbCommentData.setNbReplyData(new ArrayList<NBReplyData>());
                                }
                                for (int i = 0; i < postViewDataList.size(); i++) {
                                    nbCommentData.getNbReplyData().add(postViewDataList.get(i).getNbReplyData());
                                }
                            }

                            try {
                                List<PostViewData> deletedPostViewDataList = RoomHelper.getPostViewDeletedReplyDataByPostId(postId, commentId);
                                if (deletedPostViewDataList != null && deletedPostViewDataList.size() > 0) {
                                    if (nbCommentData.getNbReplyData() != null && nbCommentData.getNbReplyData().size() > 0) {
                                        for (int i = 0; i < nbCommentData.getNbReplyData().size(); i++) {
                                            for (int j = 0; j < deletedPostViewDataList.size(); j++)
                                                if (nbCommentData.getNbReplyData().get(i).getId() == deletedPostViewDataList.get(j).getNbReplyData().getId()) {
                                                    nbCommentData.getNbReplyData().remove(i);
                                                }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }
                            if (nbCommentData.getNbReplyData() != null) {
                                Collections.sort(nbCommentData.getNbReplyData(), nbReplyDataComparator);
                            }
                            List<NBCommentData> list = new ArrayList<>();
                            list.add(nbCommentData);
                            notifyCommentDataFound(list);
                        } else {
                            notifyCommentDataFound(null);
                        }
                    } else {
                        notifyCommentDataFound(null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    notifyCommentDataFound(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        if (firebaseListenerType.equals(FIREBASE_SINGLETON)) {
            addSingletonListenerToFireBase("commentedOn", message, myassessmentListener);
        } else {
            addLiveListenerToFireBase("commentedOn", message, myassessmentListener);
        }

    }

    public void getPostCommentData() {
        try {
            String message = POST_COMMENT_NODE;
            message = message.replace("{postId}", postId + "");
            ValueEventListener myassessmentListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue() != null) {
                            Map<String, Object> lpMap = (Map<String, Object>) dataSnapshot.getValue();
                            if (lpMap != null) {
                                List<NBCommentData> nbCommentDataList = new ArrayList<>();
                                for (String commentKey : lpMap.keySet()) {
                                    Map<String, Object> commentMap = (Map<String, Object>) lpMap.get(commentKey);
                                    if (commentMap != null) {
                                        NBCommentData nbCommentData = new NBCommentData();
                                        nbCommentData.initData(commentMap, postId);
                                        List<PostViewData> postViewDataList = RoomHelper.getPostViewReplyDataByPostId(postId, nbCommentData.getId());
                                        if (postViewDataList != null && postViewDataList.size() > 0) {
                                            if (nbCommentData.getNbReplyData() == null) {
                                                nbCommentData.setNbReplyData(new ArrayList<NBReplyData>());
                                            }
                                            for (int i = 0; i < postViewDataList.size(); i++) {
                                                nbCommentData.getNbReplyData().add(postViewDataList.get(i).getNbReplyData());
                                            }
                                        }
                                        try {
                                            List<PostViewData> deletedPostViewDataList = RoomHelper.getPostViewDeletedReplyDataByPostId(postId, commentId);
                                            if (deletedPostViewDataList != null && deletedPostViewDataList.size() > 0) {
                                                if (nbCommentData.getNbReplyData() != null && nbCommentData.getNbReplyData().size() > 0) {
                                                    for (int i = 0; i < nbCommentData.getNbReplyData().size(); i++) {
                                                        for (int j = 0; j < deletedPostViewDataList.size(); j++)
                                                            if (nbCommentData.getNbReplyData().get(i).getId() == deletedPostViewDataList.get(j).getNbReplyData().getId()) {
                                                                nbCommentData.getNbReplyData().remove(i);
                                                            }
                                                    }
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                        }
                                        if (nbCommentData.getNbReplyData() != null) {
                                            Collections.sort(nbCommentData.getNbReplyData(), nbReplyDataComparator);
                                        }
                                        nbCommentDataList.add(nbCommentData);
                                    }
                                }
                                notifyCommentDataFound(nbCommentDataList);
                            } else {
                                notifyCommentDataFound(null);
                            }
                        } else {
                            notifyCommentDataFound(null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        notifyCommentDataFound(null);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            };
            if (firebaseListenerType.equals(FIREBASE_SINGLETON)) {
                addSingletonListenerToFireBase("commentedOn", message, myassessmentListener);
            } else {
                addLiveListenerToFireBase("commentedOn", message, myassessmentListener);
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public Comparator<NBReplyData> nbReplyDataComparator = new Comparator<NBReplyData>() {
        public int compare(NBReplyData s1, NBReplyData s2) {
            return Long.valueOf(s2.getReplied_on()).compareTo(Long.valueOf(s1.getReplied_on()));
        }
    };

    public abstract void notifyCommentDataFound(List<NBCommentData> nbCommentDataList);

}
