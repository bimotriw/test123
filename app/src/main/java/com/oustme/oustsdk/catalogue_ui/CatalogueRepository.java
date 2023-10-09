package com.oustme.oustsdk.catalogue_ui;

import android.os.Bundle;
import android.util.Log;


import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.catalogue_ui.model.CatalogueComponentModule;
import com.oustme.oustsdk.catalogue_ui.model.CatalogueModule;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class CatalogueRepository {

    private static CatalogueRepository instance;
    private MutableLiveData<CatalogueComponentModule> liveData;
    private CatalogueComponentModule catalogueComponentModule;

    ActiveUser activeUser;
    long catalogue_category_id;
    boolean overAllCatalogue = false;
    private HashMap<String, ArrayList<CatalogueModule>> catalogueHashMap = new HashMap<>();

    private CatalogueRepository() {
    }

    public static CatalogueRepository getInstance() {
        if (instance == null)
            instance = new CatalogueRepository();
        return instance;
    }

    public MutableLiveData<CatalogueComponentModule> getLiveData(Bundle bundle) {
        liveData = new MutableLiveData<>();
        fetchBundleData(bundle);
        return liveData;
    }

    private void fetchBundleData(Bundle dataBundle) {

        if (dataBundle != null) {
            //handle
            catalogue_category_id = dataBundle.getLong("catalog_id");
            overAllCatalogue = dataBundle.getBoolean("overAllCatalogue");

        }

        activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
        if (activeUser != null) {

            catalogueComponentModule = new CatalogueComponentModule();
            catalogueHashMap = new HashMap<>();

            if (overAllCatalogue) {
                apiCallForGetCatalogueBaseModules();
            } else if (catalogue_category_id != 0) {
                apiCallForGetCatalogueModules(catalogue_category_id);
            }

        }

    }

    private void apiCallForGetCatalogueBaseModules() {
        try {
            Log.d("TAG", "apiCallForGetCatalogueBaseModules: ");
            long catalogueId = OustPreferences.getTimeForNotification("catalogueId");
            if (catalogueId != 0 && OustSdkApplication.getContext() != null) {
                String catalog_content_url = OustSdkApplication.getContext().getResources().getString(R.string.catalogue_list_url_v2);
                catalog_content_url = catalog_content_url.replace("{catalogueId}", ("" + catalogueId));
                catalog_content_url = catalog_content_url.replace("{userId}", activeUser.getStudentid());
                catalog_content_url = HttpManager.getAbsoluteUrl(catalog_content_url);
                JSONObject jsonParams = OustSdkTools.getRequestObjectforJSONObject(null);

                ApiCallUtils.doNetworkCall(Request.Method.GET, catalog_content_url, jsonParams, new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response != null && response.optBoolean("success")) {
                                extractDataForBase(response);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.something_went_wrong));
                        setLiveData();
                    }
                });
            } else {
                setLiveData();
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void apiCallForGetCatalogueModules(long catalogue_category_id) {
        try {
            Log.d("TAG", "apiCallForGetCatalogueModules: ");
            long catalogueId = OustPreferences.getTimeForNotification("catalogueId");
            if (catalogueId != 0 && OustSdkApplication.getContext() != null) {
                String catalog_content_url = OustSdkApplication.getContext().getResources().getString(R.string.contentCateogory_url_v2);
                catalog_content_url = catalog_content_url.replace("{catalogueId}", ("" + catalogueId));
                catalog_content_url = catalog_content_url.replace("{ccId}", ("" + catalogue_category_id));
                catalog_content_url = catalog_content_url.replace("{userId}", activeUser.getStudentid());
                catalog_content_url = HttpManager.getAbsoluteUrl(catalog_content_url);
                JSONObject jsonParams = OustSdkTools.getRequestObjectforJSONObject(null);

                ApiCallUtils.doNetworkCall(Request.Method.GET, catalog_content_url, jsonParams, new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("TAG", "onResponse: " + response.toString());
                            if (response != null && response.optBoolean("success")) {
                                extractDataFromResponse(response, catalogueId);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.something_went_wrong));
                        setLiveData();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void extractDataForBase(JSONObject response) {

        try {

            if (catalogueComponentModule != null) {

                String catalogueName = OustPreferences.get("catalogueName");
                if (catalogueName != null && !catalogueName.isEmpty()) {

                    catalogueComponentModule.setCatalogueCategoryName(catalogueName);
                } else {
                    catalogueName = OustPreferences.get("catalogueNameStatic");

                    if (catalogueName != null && !catalogueName.isEmpty()) {

                        catalogueComponentModule.setCatalogueCategoryName(catalogueName);
                    } else {
                        catalogueComponentModule.setCatalogueCategoryName("");
                    }
                }
                catalogueComponentModule.setBanner(null);
                catalogueComponentModule.setIcon(null);
                catalogueComponentModule.setDescription("");

                JSONArray jsonArray = response.optJSONArray("categoryDataList");
                if (jsonArray != null && jsonArray.length() > 0) {

                    for (int i = 0; i < jsonArray.length(); i++) {


                        JSONObject j = jsonArray.optJSONObject(i);
                        if (j != null) {
                            CatalogueModule catalogueModule = new CatalogueModule();
                            catalogueModule.setName(j.optString("name"));
                            catalogueModule.setBanner(j.optString("banner"));
                            catalogueModule.setIcon(j.optString("icon"));
                            catalogueModule.setThumbnail(j.optString("thumbnail"));
                            catalogueModule.setDescription(j.optString("description"));
                            catalogueModule.setContentId(j.optLong("contentId"));
                            catalogueModule.setViewStatus(j.optString("viewStatus"));
                            catalogueModule.setContentType("CATEGORY");
                            JSONArray contentCategoryData = j.optJSONArray("categoryItemData");
                            if (contentCategoryData != null && contentCategoryData.length() != 0) {
                                catalogueModule.setNumOfModules(contentCategoryData.length());
                            }

                            try {
                                Log.d("Catalogue", "extractDataForBase: banner:" + catalogueModule.getBanner());
                                Log.d("Catalogue", "extractDataForBase: icon:" + catalogueModule.getIcon());
                                Log.d("Catalogue", "extractDataForBase: thumbnail:" + catalogueModule.getThumbnail());
                                //Log.d("Catalogue", "extractDataForBase: ");
                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }
                            storeDataAsHashMap(catalogueModule);
                        }
                    }
                }
                setLiveData();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void extractDataFromResponse(JSONObject response, long catalogueId) {
        try {
            if (catalogueComponentModule != null) {
                catalogueComponentModule.setCatalogueCategoryName(response.optString("name"));
                catalogueComponentModule.setBanner(response.optString("banner"));
                catalogueComponentModule.setIcon(response.optString("icon"));
                catalogueComponentModule.setDescription(response.optString("description"));

                JSONArray jsonArray = response.optJSONArray("categoryItemDataList");
                if (jsonArray != null && jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject j = jsonArray.optJSONObject(i);
                        if (j != null) {
                            CatalogueModule catalogueModule = new CatalogueModule();
                            catalogueModule.setName(j.optString("name"));
                            catalogueModule.setBanner(j.optString("banner"));
                            catalogueModule.setIcon(j.optString("icon"));
                            catalogueModule.setThumbnail(j.optString("thumbnail"));
                            catalogueModule.setDescription(j.optString("description"));
                            catalogueModule.setContentId(j.optLong("contentId"));
                            catalogueModule.setContentType(j.optString("contentType"));
                            if (catalogueModule.getContentType() != null && catalogueModule.getContentType().equalsIgnoreCase("SOCCER_SKILL")) {
                                if (catalogueComponentModule != null) {
                                    catalogueComponentModule.setSkillEnabled(true);
                                }
                            }
                            catalogueModule.setTrendingPoints(j.optLong("trendingPoints"));
                            catalogueModule.setNumOfEnrolledUsers(j.optLong("numOfEnrolledUsers"));
                            catalogueModule.setOustCoins(j.optLong("oustCoins"));
                            catalogueModule.setViewStatus(j.optString("viewStatus"));
                            catalogueModule.setCatalogueId(catalogueId);
                            catalogueModule.setCatalogueCategoryId(catalogue_category_id);
                            catalogueModule.setNumOfModules(j.optLong("numOfModules"));
                            catalogueModule.setDistributeTS(j.optString("distributeTS"));
                            catalogueModule.setCompletionDateAndTime(j.optString("completionDateAndTime"));//backend sending null
                            catalogueModule.setContentDuration(j.optDouble("contentDuration"));
                            catalogueModule.setCompletionPercentage(j.optLong("completionPercentage"));
                            catalogueModule.setMode(j.optString("mode"));
                            catalogueModule.setAssessmentScore(j.optLong("assessmentScore"));
                            catalogueModule.setState(j.optString("state"));
                            catalogueModule.setEnrolled(j.optBoolean("enrolled"));
                            catalogueModule.setPassed(j.optBoolean("passed"));
                            if (j.has("showAssessmentResultScore")) {
                                catalogueModule.setShowAssessmentResultScore(j.optBoolean("showAssessmentResultScore"));
                            }
                            if (j.has("recurring")) {
                                catalogueModule.setRecurring(j.optBoolean("recurring"));
                            }
                            if (j.has("distributedId")) {
                                catalogueModule.setDistributedId(j.optLong("distributedId"));
                            }

                            /*try {
                                Log.d("Catalogue", "extractDataForBase: banner:" + catalogueModule.getBanner());
                                Log.d("Catalogue", "extractDataForBase: icon:" + catalogueModule.getIcon());
                                //Log.d("Catalogue", "extractDataForBase: ");
                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }*/
                            storeDataAsHashMap(catalogueModule);
                        }
                    }
                }
                setLiveData();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void storeDataAsHashMap(CatalogueModule catalogueModule) {

        try {
            if (catalogueModule.getContentType() != null && catalogueModule.getContentType().equalsIgnoreCase("CATEGORY")) {
                if (catalogueHashMap.size() != 0) {
                    if (catalogueHashMap.get("Folder") == null) {
                        catalogueHashMap.put("Folder", new ArrayList<>());
                    }

                } else {
                    catalogueHashMap.put("Folder", new ArrayList<>());

                }
                Objects.requireNonNull(catalogueHashMap.get("Folder")).add(catalogueModule);

            } else {

                if (catalogueHashMap.size() != 0) {
                    if (catalogueHashMap.get("Module") == null) {
                        catalogueHashMap.put("Module", new ArrayList<>());
                    }

                } else {
                    catalogueHashMap.put("Module", new ArrayList<>());
                }
                Objects.requireNonNull(catalogueHashMap.get("Module")).add(catalogueModule);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setLiveData() {
        try {
            catalogueComponentModule.setActiveUser(activeUser);
            catalogueComponentModule.setCatalogueBaseHashMap(catalogueHashMap);
            liveData.postValue(catalogueComponentModule);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
