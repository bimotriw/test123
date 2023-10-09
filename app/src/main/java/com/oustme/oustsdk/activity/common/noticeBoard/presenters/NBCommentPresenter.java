package com.oustme.oustsdk.activity.common.noticeBoard.presenters;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.oustme.oustsdk.activity.common.noticeBoard.callBacks.CommentDataRepository;
import com.oustme.oustsdk.activity.common.noticeBoard.data.handlers.GetCommentDataTask;
import com.oustme.oustsdk.activity.common.noticeBoard.model.request.PostViewData;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBCommentData;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBReplyData;
import com.oustme.oustsdk.activity.common.noticeBoard.view.NBCommentView;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by oust on 3/8/19.
 */

public class NBCommentPresenter implements CommentDataRepository{
    private NBCommentView nbCommentView;
    private long nbId,postId,commentId;
    private NBCommentData nbCommentData;

    public NBCommentPresenter(NBCommentView nbCommentView,long nbId,long postId,long commentid) {
        this.nbCommentView = nbCommentView;
        this.nbId = nbId;
        this.postId=postId;
        this.commentId=commentid;

        if(postId ==0 || commentId==0){
            nbCommentView.onErrorFound();
        }
        setUserData();
        getCommentData();
    }

    private void setUserData() {
        try {
            ActiveUser activeUser = OustAppState.getInstance().getActiveUser();
            if ((activeUser != null) && (activeUser.getStudentid() != null)) {
            } else {
                String activeUserGet = OustPreferences.get("userdata");
                activeUser = OustSdkTools.getActiveUserData(activeUserGet);
                OustFirebaseTools.initFirebase();
                OustAppState.getInstance().setActiveUser(activeUser);
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getCommentData() {
        new GetCommentDataTask(this).execute(postId,commentId);
    }

    @Override
    public void gotCommentData(NBCommentData nbCommentData) {
        try {
            this.nbCommentData = nbCommentData;
            nbCommentView.setCommentData(nbCommentData.getAvatar(), nbCommentData.getCommentedBy(), nbCommentData.getComment());
            nbCommentView.setOrUpdateAdapter(nbCommentData.getNbReplyData());
            nbCommentView.updateCommentsCount(nbCommentData.getNbReplyData().size());
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void checkIfReply(String reply) {
        try {
            if (reply != null && !reply.isEmpty()) {
                prepareReplyRequest(reply);
                nbCommentView.resetReplyText();
            } else {
                nbCommentView.noReplyAdded();
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void prepareReplyRequest(String reply) {
        try {
            PostViewData postViewData = new PostViewData(nbId, postId, System.currentTimeMillis());
            NBReplyData nbReplyData = new NBReplyData();
            nbReplyData.setReplied_on(System.currentTimeMillis());
            nbReplyData.setReplied_by(OustAppState.getInstance().getActiveUser().getStudentid());
            nbReplyData.setCommentId(commentId);
            nbReplyData.setAvatar(OustAppState.getInstance().getActiveUser().getAvatar());
            nbReplyData.setReply(reply);
            postViewData.setNbReplyData(nbReplyData);
            postViewData.setType("reply");

            if (nbCommentData.getNbReplyData() == null || nbCommentData.getNbReplyData().size() == 0) {
                List<NBReplyData> replyDataList = new ArrayList<>();
                replyDataList.add(0, nbReplyData);
                nbCommentData.setNbReplyData(replyDataList);
            } else {
                nbCommentData.getNbReplyData().add(0, nbReplyData);
            }
            nbCommentView.setOrUpdateAdapter(nbCommentData.getNbReplyData());

            addToActionTask(postViewData);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void addToActionTask(PostViewData postViewData) {
        new NBPostAddActionTask(postViewData).execute();
    }

    public void deleteReplyData(PostViewData postViewData) {
        try {
            postViewData.setNbId(nbId);
            postViewData.setPostid(postId);
            postViewData.getNbReplyData().setCommentId(commentId);

            nbCommentData.getNbReplyData().remove(postViewData.getNbReplyData());
            nbCommentView.setOrUpdateAdapter(nbCommentData.getNbReplyData());

            addToActionTask(postViewData);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public class NBPostAddActionTask extends AsyncTask<Void,Void,Void> {

        private PostViewData postViewData;

        public NBPostAddActionTask(PostViewData postViewData) {
            this.postViewData = postViewData;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                Gson gson = new Gson();
                String str = gson.toJson(postViewData);
                List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedNBRequests");
                if (requests != null) {
                    requests.add(str);
                }
                OustPreferences.saveLocalNotificationMsg("savedNBRequests", requests);
                //RealmHelper.addorUpdateNBPostDataModel(RealmModelConvertor.getRealmPostData(postViewData));
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(nbCommentView!=null){
                nbCommentView.startApiCalls();
            }
        }
    }


}