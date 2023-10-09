package com.oustme.oustsdk.room;


import androidx.annotation.Keep;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by oust on 7/26/17.
 */

@Keep
@Entity
public class EntityLanguageDataClass{
    @PrimaryKey
    private int index;
    private String name;
    private String languagePerfix;
    private String hashmapStr;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguagePerfix() {
        return languagePerfix;
    }

    public void setLanguagePerfix(String languagePerfix) {
        this.languagePerfix = languagePerfix;
    }

    public String getHashmapStr() {
        return hashmapStr;
    }

    public void setHashmapStr(String hashmapStr) {
        this.hashmapStr = hashmapStr;
    }
}
