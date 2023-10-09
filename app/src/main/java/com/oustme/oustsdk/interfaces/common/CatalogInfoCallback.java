package com.oustme.oustsdk.interfaces.common;


/**
 * Created by oust on 11/2/17.
 */

public interface CatalogInfoCallback {
    void openCatalogInfoDialog(String id, String type);
    void added(String id, String type);
}
