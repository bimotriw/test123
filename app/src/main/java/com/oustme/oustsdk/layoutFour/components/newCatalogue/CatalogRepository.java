package com.oustme.oustsdk.layoutFour.components.newCatalogue;

import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.catalogue_ui.model.CatalogueComponentModule;
import com.oustme.oustsdk.catalogue_ui.model.CatalogueModule;
import com.oustme.oustsdk.layoutFour.data.CatalogueItemData;
import com.oustme.oustsdk.response.catalogue.CatalogueResponse;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CatalogRepository {

    private static final String TAG = "CatalogRepository";
    private MutableLiveData<CatalogueComponentModule> liveData;
    private CatalogueComponentModule catalogueComponentModule;
    private static CatalogRepository instance;
    private ActiveUser activeUser;
    private HashMap<String, ArrayList<CatalogueModule>> catalogueHashMap = new HashMap<>();
    private final ArrayList<CatalogueItemData> catalogueItemDataList = new ArrayList<>();
    long catalogueId;

    public static CatalogRepository getInstance() {
        if (instance == null)
            instance = new CatalogRepository();
        return instance;
    }

    public MutableLiveData<CatalogueComponentModule> getCatalogs() {
        liveData = new MutableLiveData<>();
        fetchCatalogs();
        return liveData;
    }

    private void fetchCatalogs() {
        activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
        getDataFromDB(OustPreferences.getTimeForNotification("catalogueId"));
        getCatalogueId();
    }

    private void getDataFromDB(long catalogueId) {
        List<CatalogueItemData> entityCatalogueItemDataList = RoomHelper.getEntityCatalogueItemDatas(catalogueId);
        if (entityCatalogueItemDataList.size() > 0) {
            for (CatalogueItemData catalogueItemData : entityCatalogueItemDataList) {

                if (catalogueItemData != null) {
                    CatalogueModule catalogueModule = new CatalogueModule();
                    catalogueModule.setName(catalogueItemData.getName());
                    catalogueModule.setBanner(catalogueItemData.getBanner());
                    catalogueModule.setIcon(catalogueItemData.getIcon());
                    catalogueModule.setDescription(catalogueItemData.getDescription());
                    catalogueModule.setContentId(catalogueItemData.getContentId());
                    catalogueModule.setViewStatus(catalogueItemData.getViewStatus());
                    catalogueModule.setContentType("CATEGORY");
                    if (catalogueItemData.getCategoryItemData() != null && catalogueItemData.getCategoryItemData().size() != 0) {
                        catalogueModule.setNumOfModules(catalogueItemData.getCategoryItemData().size());
                    }
                    storeDataAsHashMap(catalogueModule);
                }
            }
        }
    }

    private void getDataFromServer() {
        try {
            catalogueId = OustPreferences.getTimeForNotification("catalogueId");
            if (catalogueId != 0) {
                catalogueComponentModule = new CatalogueComponentModule();
                catalogueHashMap = new HashMap<>();
                String catalog_list_url = OustSdkApplication.getContext().getResources().getString(R.string.catalogue_list_url_v2);
                catalog_list_url = catalog_list_url.replace("{catalogueId}", ("" + catalogueId));
                catalog_list_url = catalog_list_url.replace("{userId}", activeUser.getStudentid());
                catalog_list_url = HttpManager.getAbsoluteUrl(catalog_list_url);
                JSONObject jsonParams = OustSdkTools.getRequestObjectforJSONObject(null);
                Log.e(TAG, "catalogue - getDataFromServer-->: " + catalog_list_url);

                ApiCallUtils.doNetworkCall(Request.Method.GET, catalog_list_url, jsonParams, new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.optBoolean("success")) {
                                extractCommonData(response, catalogueId);
                            } else {
                                OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.error_message));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.no_internet_connection));
                    }
                });
            } else {
                Log.d(TAG, "getDataFromServer: catalogueId is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void extractCommonData(JSONObject response, long catalogueId) {
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
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        CatalogueItemData catalogueItemData = extractCatalogueItem(jsonObject, "categoryItemData");
                        catalogueItemDataList.add(catalogueItemData);
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
                            catalogueModule.setRecurring(j.optBoolean("recurring"));
                            JSONArray contentCategoryData = j.optJSONArray("categoryItemData");
                            if (contentCategoryData != null && contentCategoryData.length() != 0) {
                                catalogueModule.setNumOfModules(contentCategoryData.length());
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

        RoomHelper.addorUpdateCatalogueItemData(catalogueItemDataList, catalogueId);
    }

    private CatalogueItemData extractCatalogueItem(JSONObject jsonObject, String catalogNode) {
        CatalogueItemData catalogueItem = new CatalogueItemData();
        if (jsonObject != null) {
            try {
                if (jsonObject.has("name"))
                    catalogueItem.setName(jsonObject.getString("name"));
                if (jsonObject.has("banner"))
                    catalogueItem.setBanner(jsonObject.getString("banner"));
                if (jsonObject.has("icon"))
                    catalogueItem.setIcon(jsonObject.getString("icon"));
                if (jsonObject.has("thumbnail"))
                    catalogueItem.setThumbnail(jsonObject.getString("thumbnail"));
                if (jsonObject.has("description"))
                    catalogueItem.setDescription(jsonObject.getString("description"));
                if (jsonObject.has("contentId"))
                    catalogueItem.setContentId(jsonObject.getLong("contentId"));
                if (jsonObject.has("contentType"))
                    catalogueItem.setContentType(jsonObject.getString("contentType"));
                if (jsonObject.has("trendingPoints"))
                    catalogueItem.setTrendingPoints(jsonObject.getInt("trendingPoints"));
                if (jsonObject.has("numOfEnrolledUsers"))
                    catalogueItem.setNumOfEnrolledUsers(jsonObject.getInt("numOfEnrolledUsers"));
                if (jsonObject.has("oustCoins"))
                    catalogueItem.setOustCoins(jsonObject.getInt("oustCoins"));
                if (jsonObject.has("viewStatus"))
                    catalogueItem.setViewStatus(jsonObject.getString("viewStatus"));
                if (jsonObject.has("recurring"))
                    catalogueItem.setRecurring(jsonObject.getBoolean("recurring"));


                Object categoryItemJSON = null;
                if (!TextUtils.isEmpty(catalogNode) && jsonObject.has(catalogNode))
                    categoryItemJSON = jsonObject.get(catalogNode);

                if (categoryItemJSON instanceof JSONArray) {
                    JSONArray categoryItemDataList = (JSONArray) categoryItemJSON;
                    ArrayList<CatalogueItemData> catalogueItemDataList = new ArrayList<>();
                    for (int i = 0; i < categoryItemDataList.length(); i++) {
                        JSONObject itemDataJSONObject = categoryItemDataList.getJSONObject(i);

                        CatalogueItemData innerCatalogueItem = extractCatalogueItem(itemDataJSONObject, "");
                        catalogueItemDataList.add(innerCatalogueItem);
                    }

                    catalogueItem.setCategoryItemData(catalogueItemDataList);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        }
        return catalogueItem;
    }


    private void getCatalogueId() {
        try {
            if (OustSdkTools.checkInternetStatus()) {
                String tenantName = OustPreferences.get("tanentid").trim();
                String catalogueModuleUrl = OustSdkApplication.getContext().getResources().getString(R.string.get_catalogue_id);
                catalogueModuleUrl = catalogueModuleUrl.replace("{orgId}", tenantName);
                catalogueModuleUrl = catalogueModuleUrl.replace("{userId}", activeUser.getStudentid());
                catalogueModuleUrl = HttpManager.getAbsoluteUrl(catalogueModuleUrl);
                Log.d(TAG, "getCatalogueId-->: " + catalogueModuleUrl);

                ApiCallUtils.doNetworkCall(Request.Method.GET, catalogueModuleUrl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "onResponse: --> " + response);
                        Gson gson = new Gson();
                        CatalogueResponse catalogueResponse = gson.fromJson(response.toString(), CatalogueResponse.class);
                        if (catalogueResponse.getSuccess()) {
                            if (catalogueResponse.getCatalogueDetailsList() != null && !catalogueResponse.getCatalogueDetailsList().isEmpty()) {
                                if (catalogueResponse.getCatalogueDetailsList().get(0) != null && catalogueResponse.getCatalogueDetailsList().get(0).getCatalogueId() > 0) {
                                    OustPreferences.saveTimeForNotification("catalogueId", catalogueResponse.getCatalogueDetailsList().get(0).getCatalogueId());
                                    getDataFromServer();
                                } else {
                                    OustPreferences.saveTimeForNotification("catalogueId", 0);
                                    liveData.postValue(null);
                                }
                            } else {
                                OustPreferences.saveTimeForNotification("catalogueId", 0);
                                liveData.postValue(null);
                            }
                        } else {
                            OustPreferences.saveTimeForNotification("catalogueId", 0);
                            liveData.postValue(null);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        OustPreferences.saveTimeForNotification("catalogueId", 0);
                        liveData.postValue(null);
                    }
                });
            } else {
                OustPreferences.saveTimeForNotification("catalogueId", 0);
                liveData.postValue(null);
            }
        } catch (Exception e) {
            OustPreferences.saveTimeForNotification("catalogueId", 0);
            liveData.postValue(null);
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
                for (ArrayList<CatalogueModule> list : catalogueHashMap.values()) {
                    if (list.size() == 0) {
                        Objects.requireNonNull(catalogueHashMap.get("Folder")).add(catalogueModule);
                    } else {
                        for (int i = 0; i < list.size(); i++) {
//                            if (!String.valueOf(list.get(i).getContentId()).equals(String.valueOf(catalogueModule.getContentId()))) {
                            if (list.get(i).getContentId() != catalogueModule.getContentId()) {
                                Objects.requireNonNull(catalogueHashMap.get("Folder")).add(catalogueModule);
                                break;
                            }
                        }
                    }
                }
            } else {
                if (catalogueHashMap.size() != 0) {
                    if (catalogueHashMap.get("Module") == null) {
                        catalogueHashMap.put("Module", new ArrayList<>());
                    }

                } else {
                    catalogueHashMap.put("Module", new ArrayList<>());
                }
                Log.e(TAG, "storeDataAsHashMap: size-||-> " + catalogueHashMap.size());
                for (ArrayList<CatalogueModule> list : catalogueHashMap.values()) {
                    if (list.size() == 0) {
                        Objects.requireNonNull(catalogueHashMap.get("Module")).add(catalogueModule);
                    } else {
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getContentId() != catalogueModule.getContentId()) {
                                Objects.requireNonNull(catalogueHashMap.get("Module")).add(catalogueModule);
                                break;
                            }
                        }
                    }
                }
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
