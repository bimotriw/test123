package com.oustme.oustsdk.activity.common.noticeBoard.data.handlers;

import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBTopicData;
import com.oustme.oustsdk.tools.OustDataHandler;

/**
 * Created by oust on 2/20/19.
 */

public class NBDataHandler {

    private static NBDataHandler mInstance;
    private NBDataHandler(){}

    public static NBDataHandler getInstance() {
        if (mInstance == null) {
            mInstance = new NBDataHandler();
        }
        return mInstance;
    }

    private NBTopicData nbTopicData;


    public NBTopicData getNbTopicData() {
        return nbTopicData;
    }

    public void setNbTopicData(NBTopicData nbTopicData) {
        this.nbTopicData = nbTopicData;
    }
}
