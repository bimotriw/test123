package com.oustme.oustsdk.layoutFour.newnoticeBoard.presenters;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.layoutFour.navigationFragments.NewNoticeBoardFragment;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.callBacks.NewPostDataRepository;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.data.handlers.NewGetAllPostDataTask;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.data.handlers.NewNBDataHandler;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.request.NewPostViewData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBPostData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBTopicData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.view.NewNBTopicDetailView;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class NewNBTopicDetailPresenter implements NewPostDataRepository {
    private static final String TAG = "NewNBTopicDetailPre";
    private NewNBTopicDetailView nbTopicDetailView;
    private NewNBTopicData nbTopicData;
    int nbTopicDataSize = 0;
    int nbTopicDataPostSize = 0;
    ArrayList<NewNBPostData> nbPostDataTempList = new ArrayList<>();

    public NewNBTopicDetailPresenter(NewNBTopicDetailView nbTopicDetailView) {
        try {
            this.nbTopicDetailView = nbTopicDetailView;
            this.nbTopicDetailView.createLoader();
            setUserData();
            nbTopicData = NewNBDataHandler.getInstance().getNbTopicData();
            //NBDataHandler.getInstance().setNbTopicData(null);

            nbTopicDetailView.setData(nbTopicData);
            nbTopicDetailView.updateTopicBanner(nbTopicData.getIcon());
            if (nbTopicData.getTopic() != null) {
                nbTopicDetailView.setToolbarText(nbTopicData.getTopic());
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public NewNBTopicDetailPresenter(NewNoticeBoardFragment newNoticeBoardFragment) {
        try {
            this.nbTopicDetailView = newNoticeBoardFragment;
            this.nbTopicDetailView.createLoader();
            setUserData();
//            nbTopicData = NewNBDataHandler.getInstance().getNbTopicData();
            //NBDataHandler.getInstance().setNbTopicData(null);
//            nbTopicDetailView.setToolbarText(nbTopicData.getTopic());


//            newNoticeBoardFragment.setData(nbTopicData);
//            newNoticeBoardFragment.setToolbarText(nbTopicData.getTopic());
//            newNoticeBoardFragment.updateTopicBanner(nbTopicData.getIcon());
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

    public void getAllPostData(List<NewNBTopicData> nbTopicData1) {
        try {
            if (nbTopicData1 != null) {
                for (int i = 0; i < nbTopicData1.size(); i++) {
                    if (isNull(nbTopicData1.get(i)) || nbTopicData1.get(i).getPostUpdateDataSize() == 0) {
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
                    } else {
                        try {
                            OustStaticVariableHandling.getInstance().setNbPostSize(nbTopicData1.get(i).getPostUpdateDataSize());
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    }
                    nbTopicDataPostSize = nbTopicDataPostSize + nbTopicData1.get(i).getPostUpdateDataSize();
                    new NewGetAllPostDataTask(this).execute(nbTopicData1.get(i));
//                    nbTopicDetailView.OnErrorOccured("show");

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void getAllPostData() {
        if (isNull(nbTopicData) || nbTopicData.getPostUpdateDataSize() == 0) {
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
        } else {
            try {
                OustStaticVariableHandling.getInstance().setNbPostSize(nbTopicData.getPostUpdateDataSize());
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
        nbTopicDataPostSize = nbTopicDataPostSize + nbTopicData.getPostUpdateDataSize();

        new NewGetAllPostDataTask(this).execute(nbTopicData);
    }

    @Override
    public void gotAllPostData(ArrayList<NewNBPostData> nbPostDataList) {
        nbPostDataTempList.addAll(nbPostDataList);
        nbTopicDataSize = nbTopicDataSize + nbPostDataList.size();
        if (isNotNull(nbTopicDetailView)) {
            if (nbTopicDataSize == nbTopicDataPostSize) {
                createTotalNbPostData(nbPostDataTempList);
                nbTopicDataPostSize = 0;
                nbTopicDataSize = 0;
                nbPostDataTempList.clear();   // check here once
            }
        }
    }

    public Comparator<NewNBPostData> nbTopicDataComparator = new Comparator<NewNBPostData>() {
        public int compare(NewNBPostData s1, NewNBPostData s2) {
            return Long.valueOf(s2.getCreatedOn()).compareTo(Long.valueOf(s1.getCreatedOn()));
        }
    };

    private void createTotalNbPostData(ArrayList<NewNBPostData> nbPostDataTempList1) {
        try {
            ArrayList<NewNBPostData> nbTopicDataArrayList = new ArrayList<NewNBPostData>(nbPostDataTempList1);
            if (nbTopicDataArrayList != null && nbTopicDataArrayList.size() > 0) {
                Collections.sort(nbTopicDataArrayList, nbTopicDataComparator);
            }
            nbTopicDetailView.setOrUpdateAdapter(nbTopicDataArrayList);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void gotPostData(NewNBPostData nbPostData) {
    }

    public void addViewPostData(NewPostViewData postViewData) {
    }

    public void sendPostLikeData(NewPostViewData postViewData) {
        addToActionTask(postViewData);
    }

    private void addToActionTask(NewPostViewData postViewData) {
        new NewNBPostAddActionTask(postViewData).execute();
    }

    public void sendPostCommentData(NewPostViewData postViewData) {
        addToActionTask(postViewData);
    }

    public void sendPostShareData(NewPostViewData postViewData) {
        addToActionTask(postViewData);
    }

    protected boolean isNotNull(Object o) {
        return o != null;
    }

    protected boolean isNull(Object o) {
        return o == null;
    }

    public void deletePostComment(NewPostViewData postViewData) {
        getAllPostData();
        addToActionTask(postViewData);
    }


    public class NewNBPostAddActionTask extends AsyncTask<Void, Void, Void> {

        private NewPostViewData postViewData;

        public NewNBPostAddActionTask(NewPostViewData postViewData) {
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
