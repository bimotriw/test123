package com.oustme.oustsdk.data.handlers;

/**
 * Created by oust on 7/31/18.
 */

public enum ModuleType {
    COURSE("course"),
    ASSESSMENT("assessment"),
    COURSECOLLECTION("course_collection");
    private String moduleType;

    public String getModuleType() {
        return moduleType;
    }

    ModuleType(String moduleType) {
        this.moduleType = moduleType;
    }
}
