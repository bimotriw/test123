package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

/**
 * Created by oust on 10/21/17.
 */

@Keep
public class DrawerItemData {
    private String topic;
    private int type;
    private String subTopic;
    private String conditionText;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSubTopic() {
        return subTopic;
    }

    public void setSubTopic(String subTopic) {
        this.subTopic = subTopic;
    }

    public String getConditionText() {
        return conditionText;
    }

    public void setConditionText(String conditionText) {
        this.conditionText = conditionText;
    }
}
