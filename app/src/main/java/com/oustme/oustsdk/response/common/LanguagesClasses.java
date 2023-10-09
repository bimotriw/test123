package com.oustme.oustsdk.response.common;

import androidx.annotation.Keep;

import java.util.List;

/**
 * Created by shilpysamaddar on 07/03/17.
 */

@Keep
public class LanguagesClasses {
    private long lastTimestamp;
    List<LanguageClass> languageClasses;

    public long getLastTimestamp() {
        return lastTimestamp;
    }

    public void setLastTimestamp(long lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }

    public List<LanguageClass> getLanguageClasses() {
        return languageClasses;
    }

    public void setLanguageClasses(List<LanguageClass> languageClasses) {
        this.languageClasses = languageClasses;
    }

    @Override
    public String toString() {
        return "LanguagesClasses{" +
                "lastTimestamp=" + lastTimestamp +
                ", languageClasses=" + languageClasses +
                '}';
    }
}
