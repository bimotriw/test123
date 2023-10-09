package com.oustme.oustsdk.firebase.assessment;

import androidx.annotation.Keep;

import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.Map;


/**
 * Created by oust on 10/11/18.
 */

@Keep
public class AssesmentResultBadge {
    private long to, from;
    private String image, label;

    public long getTo() {
        return to;
    }

    public void setTo(long to) {
        this.to = to;
    }

    public long getFrom() {
        return from;
    }

    public void setFrom(long from) {
        this.from = from;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    protected void init(Map<Object, Object> badgeAllocatedMap) {
        try {
            if (badgeAllocatedMap != null) {
                if (badgeAllocatedMap.containsKey("to")) {
                    this.to = OustSdkTools.newConvertToLong(badgeAllocatedMap.get("to"));
                }
                if (badgeAllocatedMap.containsKey("from")) {
                    this.from = OustSdkTools.newConvertToLong(badgeAllocatedMap.get("from"));
                }
                if (badgeAllocatedMap.containsKey("image")) {
                    this.image = (String) badgeAllocatedMap.get("image");
                }
                if (badgeAllocatedMap.containsKey("label")) {
                    this.label = (String) badgeAllocatedMap.get("label");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
