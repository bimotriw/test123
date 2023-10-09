package com.oustme.oustsdk.room;


import androidx.annotation.Keep;
import androidx.room.Entity;

/**
 * Created by shilpysamaddar on 03/04/17.
 */

@Keep
@Entity
class EntityMTFColumnData {
    private String mtfColMediaType;
    private String mtfColData;
    private String mtfColDataId;
    private String mtfColData_CDN;

    public String getMtfColData_CDN() {
        return mtfColData_CDN;
    }

    public void setMtfColData_CDN(String mtfColData_CDN) {
        this.mtfColData_CDN = mtfColData_CDN;
    }

    public String getMtfColMediaType() {
        return mtfColMediaType;
    }

    public void setMtfColMediaType(String mtfColMediaType) {
        this.mtfColMediaType = mtfColMediaType;
    }

    public String getMtfColData() {
        return mtfColData;
    }

    public void setMtfColData(String mtfColData) {
        this.mtfColData = mtfColData;
    }

    public String getMtfColDataId() {
        return mtfColDataId;
    }

    public void setMtfColDataId(String mtfColDataId) {
        this.mtfColDataId = mtfColDataId;
    }
}
