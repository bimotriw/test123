package com.oustme.oustsdk.layoutFour.newnoticeBoard.presenters;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.callBacks.NewCommentDataRepository;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.data.handlers.NewGetCommentDataTask;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.request.NewPostViewData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBCommentData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBReplyData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.view.NewNBCommentView;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.ArrayList;
import java.util.List;



public class NewNBCommentPresenter implements NewCommentDataRepository {
    private NewNBCommentView nbCommentView;
    private long nbId,postId,commentId;
    private NewNBCommentData nbCommentData;

    public NewNBCommentPresenter(NewNBCommentView nbCommentView,long nbId,long postId,long commentid) {
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
        new NewGetCommentDataTask(this).execute(postId,commentId);
    }

    @Override
    public void gotCommentData(NewNBCommentData nbCommentData) {
        try {
            this.nbCommentData = nbCommentData;
            nbCommentView.setCommentData(nbCommentData.getAvatar(), nbCommentData.getCommentedBy(), nbCommentData.getComment(), nbCommentData.getCommentedOn()
                    , nbCommentData.getDesignation(), nbCommentData.getUserRole());
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
            NewPostViewData postViewData = new NewPostViewData(nbId, postId, System.currentTimeMillis());
            NewNBReplyData nbReplyData = new NewNBReplyData();
            nbReplyData.setReplied_on(System.currentTimeMillis());
            nbReplyData.setReplied_by(OustAppState.getInstance().getActiveUser().getStudentid());
            nbReplyData.setCommentId(commentId);
            nbReplyData.setAvatar(OustAppState.getInstance().getActiveUser().getAvatar());
            nbReplyData.setReply(reply);
            postViewData.setNbReplyData(nbReplyData);
            postViewData.setType("reply");

            if (nbCommentData.getNbReplyData() == null || nbCommentData.getNbReplyData().size() == 0) {
                List<NewNBReplyData> replyDataList = new ArrayList<>();
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

    private void addToActionTask(NewPostViewData postViewData) {
        new NewNBPostAddActionTask(postViewData).execute();
    }

    public void deleteReplyData(NewPostViewData postViewData) {
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

    public class NewNBPostAddActionTask extends AsyncTask<Void,Void,Void> {

        private NewPostViewData postViewData;

        public NewNBPostAddActionTask(NewPostViewData postViewData) {
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