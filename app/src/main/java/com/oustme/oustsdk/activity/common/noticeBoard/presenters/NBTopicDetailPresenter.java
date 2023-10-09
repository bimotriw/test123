package com.oustme.oustsdk.activity.common.noticeBoard.presenters;

import android.os.AsyncTask;
import android.util.Log;


import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.noticeBoard.callBacks.PostDataRepository;
import com.oustme.oustsdk.activity.common.noticeBoard.data.handlers.GetAllPostDataTask;
import com.oustme.oustsdk.activity.common.noticeBoard.data.handlers.NBDataHandler;
import com.oustme.oustsdk.activity.common.noticeBoard.model.request.PostViewData;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBPostData;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBTopicData;
import com.oustme.oustsdk.activity.common.noticeBoard.view.NBTopicDetailView;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;

import java.util.List;

/**
 * Created by oust on 2/20/19.
 */

public class NBTopicDetailPresenter implements PostDataRepository {
    private static final String TAG = "NBTopicDetailPresenter";
    private NBTopicDetailView nbTopicDetailView;
    private NBTopicData nbTopicData;

    public NBTopicDetailPresenter(NBTopicDetailView nbTopicDetailView) {
        try {
            this.nbTopicDetailView = nbTopicDetailView;
            this.nbTopicDetailView.createLoader();
            setUserData();
            nbTopicData = NBDataHandler.getInstance().getNbTopicData();
            //NBDataHandler.getInstance().setNbTopicData(null);

            nbTopicDetailView.setToolbarText(nbTopicData.getTopic());
            nbTopicDetailView.updateTopicBanner(nbTopicData.getBannerBg());
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
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
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void getAllPostData() {
        if (isNull(nbTopicData) || nbTopicData.getPostUpdateDataSize() == 0) {
            {
                try {
                    if (isNotNull(nbTopicDetailView)) {
                        try {
                            if (OustSdkApplication.getContext().getString(R.string.nb_no_posts_msg) != null) {
                                nbTopicDetailView.OnErrorOccured(OustSdkApplication.getContext().getString(R.string.nb_no_posts_msg));
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }
        } else {
            try {
                OustStaticVariableHandling.getInstance().setNbPostSize(nbTopicData.getPostUpdateDataSize());
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
        new GetAllPostDataTask(this).execute(nbTopicData);

    }

    @Override
    public void gotAllPostData(List<NBPostData> nbPostDataList) {
        Log.d(TAG, "gotAllPostData: " + nbPostDataList.size());
        if (isNotNull(nbTopicDetailView)) {
            nbTopicDetailView.setOrUpdateAdapter(nbPostDataList);
        }
    }

    @Override
    public void gotPostData(NBPostData nbPostData) {

    }

    public void addViewPostData(PostViewData postViewData) {
    }

    public void sendPostLikeData(PostViewData postViewData) {
        addToActionTask(postViewData);
    }

    private void addToActionTask(PostViewData postViewData) {
        new NBPostAddActionTask(postViewData).execute();
    }

    public void sendPostCommentData(PostViewData postViewData) {
        addToActionTask(postViewData);
    }

    public void sendPostShareData(PostViewData postViewData) {
        addToActionTask(postViewData);
    }

    protected boolean isNotNull(Object o) {
        return o != null;
    }

    protected boolean isNull(Object o) {
        return o == null;
    }

    public void deletePostComment(PostViewData postViewData) {
        getAllPostData();
        addToActionTask(postViewData);
    }


    public class NBPostAddActionTask extends AsyncTask<Void, Void, Void> {

        private PostViewData postViewData;

        public NBPostAddActionTask(PostViewData postViewData) {
            this.postViewData = postViewData;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
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
            if (nbTopicDetailView != null) {
                nbTopicDetailView.startApiCalls();
            }
        }
    }


}
