package com.oustme.oustsdk.activity.common.languageSelector.view;

import com.google.gson.Gson;
import com.oustme.oustsdk.activity.common.languageSelector.model.response.LanguageListResponse;

import org.json.JSONObject;

public interface LanguageView {
     interface LView{
        void updateLanguage(LanguageListResponse languageListResponse, String mSelectedLanguage, int mSelectedLanguageId, String mCplIDForLanguage);
        void onErrorCPLData();
        void showProgressBar(int type);
        void hideProgressBar(int type);
        void onLanguageSelected();
        void onError(String error);
        void getLanguages();
        void extractCplLanguageData(JSONObject response, Gson mGson);
    }
}
