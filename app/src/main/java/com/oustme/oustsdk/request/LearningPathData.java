package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 10/03/17.
 */

@Keep
public class LearningPathData {
    private String pathData;
    private String fileName;

    public String getPathData() {
        return pathData;
    }

    public void setPathData(String pathData) {
        this.pathData = pathData;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "LearningPathData{" +
                "pathData='" + pathData + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
