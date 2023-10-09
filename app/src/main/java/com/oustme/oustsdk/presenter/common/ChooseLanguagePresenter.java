package com.oustme.oustsdk.presenter.common;

import android.util.Log;

import com.google.gson.Gson;
import com.oustme.oustsdk.activity.common.ChooseLanguageActivity;
import com.oustme.oustsdk.response.common.LanguageClass;
import com.oustme.oustsdk.response.common.LanguagesClasses;
import com.oustme.oustsdk.tools.LanguagePreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChooseLanguagePresenter {
    String languageJson = "["
            + "{ \"index\": 1, \"name\": \"English\", \"prefix\": \"en\", \"fileName\": \"englishStr.properties\",\"countryCode\": \"gb\" },"
            + "{ \"index\": 2, \"name\": \"Kannada\", \"prefix\": \"kn\", \"fileName\": \"kannadaStr.properties\",\"countryCode\": \"IN\" },"
            + "{ \"index\": 3, \"name\": \"Japanese\", \"prefix\": \"ja\", \"fileName\": \"japaneseStr.properties\" ,\"countryCode\": \"jp\"},"
            + "{ \"index\": 4, \"name\": \"Thai\", \"prefix\": \"th\", \"fileName\": \"thaiStr.properties\",\"countryCode\": \"th\" },"
            + "{ \"index\": 5, \"name\": \"Korean\", \"prefix\": \"ko\", \"fileName\": \"koreanStr.properties\" ,\"countryCode\": \"kr\"},"
            + "{ \"index\": 6, \"name\": \"Chinese(Traditional Taiwan)\", \"prefix\": \"zh_TW\", \"fileName\": \"chineseStr.properties\",\"countryCode\": \"tw\" },"
            + "{ \"index\": 7, \"name\": \"Vietnamese\", \"prefix\": \"vi\", \"fileName\": \"vietnameseStr.properties\",\"countryCode\": \"vietnam\" },"
            + "{ \"index\": 8, \"name\": \"Bahasa\", \"prefix\": \"in\", \"fileName\": \"bahasaStr.properties\",\"countryCode\": \"id\" },"
            + "{ \"index\": 9, \"name\": \"Spanish\", \"prefix\": \"es\", \"fileName\": \"spanishStr.properties\" ,\"countryCode\": \"spain\"}"
            + "]";

    private List<LanguageClass> languageClasses;
    private List<String> languages;
    private String languagePrefix;
    private ChooseLanguageActivity mView;
    private LanguageClass currentSelectedLanguage;
    public ChooseLanguagePresenter(ChooseLanguageActivity view){
        mView = view;
    }

    public void getLanguageData(String langListStr, String langStr) {
        /* init variables */
        languageClasses = new ArrayList<LanguageClass>();
        languages = new ArrayList<>();
        languagePrefix = LanguagePreferences.get("appSelectedLanguage");

        /* set default value for language prefix */
        if (languagePrefix == null || languagePrefix.isEmpty()) {
            languagePrefix = Locale.getDefault().getLanguage();
        }
        /* load lang from json string if language list is not provided */
        if (langListStr == null || langListStr.isEmpty()) {
            parseLanguageJson();
        }else{
            Gson gson = new Gson();
            LanguagesClasses classes = gson.fromJson(langListStr, LanguagesClasses.class);
            languageClasses = classes.getLanguageClasses();

            if (languageClasses != null) {
                for (LanguageClass langClass : languageClasses) {
                    languages.add(langClass.getName());
                    if (langStr.equalsIgnoreCase(langClass.getLanguagePerfix())) {
                        langStr = langClass.getName();
                    }
                }
            } else {
                parseLanguageJson();
            }

            if (languagePrefix == null || languagePrefix.isEmpty()) {
                languagePrefix = Locale.getDefault().getLanguage();
            }
            Log.d("choose lang ","send to language goal");
        }

        Log.d("choose lang ","languages "+languagePrefix);
        mView.populateLanguage(languages, languagePrefix, languageClasses);
    }

    /**
     *
     * populate language classes variable with the data
     * from the json string
     *
     */
    private void parseLanguageJson() {
        LanguagesClasses classes = new LanguagesClasses();
        try {
            JSONArray languageArray = new JSONArray(languageJson);
            for (int i = 0; i < languageArray.length(); i++) {
                JSONObject languageObject = languageArray.getJSONObject(i);
                LanguageClass languageClass = new LanguageClass();
                languageClass.setIndex(languageObject.getInt("index"));
                languageClass.setName(languageObject.getString("name"));
                languageClass.setLanguagePerfix(languageObject.getString("prefix"));
                languageClass.setFileName(languageObject.getString("fileName"));
                languageClass.setCountryCode(languageObject.getString("countryCode"));
                languageClasses.add(languageClass);
                classes.setLanguageClasses(languageClasses);
//                languages.add(languageObject.getString("name"));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void setLanguageStart(String str, String laungeStr) {
        currentSelectedLanguage = new LanguageClass();
        if ((languageClasses != null) && (languageClasses.size() > 0)) {
            for (int i = 0; i < languageClasses.size(); i++) {
                if (str.equalsIgnoreCase(languageClasses.get(i).getName())) {
                    currentSelectedLanguage = languageClasses.get(i);
                }
            }
            if (!(currentSelectedLanguage.getLanguagePerfix()).equalsIgnoreCase(laungeStr)) {
                mView.checkLanguageAvailability(currentSelectedLanguage.getLanguagePerfix(), currentSelectedLanguage);
            }
        }
    }

}

