package com.oustme.oustsdk.layoutFour.newnoticeBoard.data.handlers;

import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBTopicData;

public class NewNBDataHandler {

    private static NewNBDataHandler mInstance;
    private NewNBDataHandler(){}

    public static NewNBDataHandler getInstance() {
        if (mInstance == null) {
            mInstance = new NewNBDataHandler();
        }
        return mInstance;
    }

    private NewNBTopicData nbTopicData;


    public NewNBTopicData getNbTopicData() {
        return nbTopicData;
    }

    public void setNbTopicData(NewNBTopicData nbTopicData) {
        this.nbTopicData = nbTopicData;
    }
}
