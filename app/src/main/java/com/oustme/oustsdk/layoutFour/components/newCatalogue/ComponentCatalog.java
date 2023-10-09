package com.oustme.oustsdk.layoutFour.components.newCatalogue;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.R;
import com.oustme.oustsdk.catalogue_ui.CatalogueFolderListActivity;
import com.oustme.oustsdk.catalogue_ui.CatalogueModulesActivity;
import com.oustme.oustsdk.catalogue_ui.model.CatalogueComponentModule;
import com.oustme.oustsdk.catalogue_ui.model.CatalogueModule;
import com.oustme.oustsdk.customviews.LayoutSwitcher;
import com.oustme.oustsdk.firebase.common.FilterCategory;
import com.oustme.oustsdk.layoutFour.components.newCatalogue.adapter.CatalogueFolderAdapter;
import com.oustme.oustsdk.layoutFour.components.newCatalogue.adapter.CatalogueModuleAdapter;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.utils.LayoutType;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

public class ComponentCatalog extends LinearLayout {

    private static final String TAG = "ComponentCatalog";
    RelativeLayout catalogue_root;
    ImageView iv_sort, iv_filter;
    TextView folder_text, see_all_folder, modules_text, see_all_modules;
    LinearLayout data_layout, folder_lay, modules_lay, folder_info, module_info;
    RecyclerView folder_recyclerview, modules_recyclerview;
    LayoutSwitcher layout_switcher;

    //Branding loader
    RelativeLayout branding_mani_layout;
    ImageView branding_bg;
    ImageView branding_icon;
    TextView branding_percentage;
    //End

    View no_data_layout;
    ImageView no_image;
    TextView no_data_content;

    private FragmentManager fragmentManager;
    FilterDialogFragment filterDialog;

    int color;
    int type = 1;
    private boolean isAscending = true;
    private boolean isFirstTime = true;
    boolean isModulesAvailable = false;

    CatalogueComponentModule catalogueComponentModule;

    CatalogueFolderAdapter catalogueFolderAdapter;
    CatalogueModuleAdapter catalogueModuleAdapter;

    ArrayList<CatalogueModule> catalogueModuleList;
    ArrayList<CatalogueModule> catalogueFolderList;

    ArrayList<CatalogueModule> catalogueModuleFilterList;
    ArrayList<CatalogueModule> catalogueFolderFilterList;


    RecyclerView.LayoutManager mFolderLayoutManager;
    RecyclerView.LayoutManager mModulesLayoutManager;

    private ArrayList<FilterCategory> filterCategoriesType;
    private ArrayList<FilterCategory> filterCategoriesStatus;
    private ArrayList<FilterCategory> selectedFeedFilter;

    int screenWidth = 0;
    private boolean isRefreshedData;


    public ComponentCatalog(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.component_catalog_new, this, true);

        getColors();
        initViews();
    }

    private void getColors() {
        try {
            color = OustResourceUtils.getColors();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            color = OustSdkTools.getColorBack(R.color.lgreen);
        }
    }

    private void initViews() {
        try {
            catalogue_root = findViewById(R.id.catalogue_root);
            data_layout = findViewById(R.id.data_layout);
            iv_sort = findViewById(R.id.iv_sort);
            iv_filter = findViewById(R.id.iv_filter);
            folder_lay = findViewById(R.id.folder_lay);
            folder_info = findViewById(R.id.folder_info);
            folder_text = findViewById(R.id.folder_text);
            see_all_folder = findViewById(R.id.see_all_folder);
            folder_recyclerview = findViewById(R.id.folder_recyclerview);
            modules_lay = findViewById(R.id.modules_lay);
            module_info = findViewById(R.id.module_info);
            modules_text = findViewById(R.id.modules_text);
            see_all_modules = findViewById(R.id.see_all_modules);
            modules_recyclerview = findViewById(R.id.modules_recyclerview);
            layout_switcher = findViewById(R.id.layout_switcher);

            no_data_layout = findViewById(R.id.no_data_layout);
            no_image = no_data_layout.findViewById(R.id.no_image);
            no_data_content = no_data_layout.findViewById(R.id.no_data_content);

            //Branding loader
            branding_mani_layout = findViewById(R.id.branding_main_layout);
            branding_bg = findViewById(R.id.branding_bg);
            branding_icon = findViewById(R.id.brand_loader);
            branding_percentage = findViewById(R.id.percentage_text);
            //End

            try {
                String tenantId = OustPreferences.get("tanentid");

                if (tenantId != null && !tenantId.isEmpty()) {
                    File brandingBg = new File(OustSdkApplication.getContext().getFilesDir(),
                            ("oustlearn_" + tenantId.toUpperCase().trim() + "splashScreen"));

                    if (brandingBg.exists() && OustPreferences.getAppInstallVariable(IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED)) {
                        Picasso.get().load(brandingBg).into(branding_bg);
                    } else {
                        String tenantBgImage = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "appImages/splash/org/" + (tenantId.toUpperCase().trim()) + "/android/bgImage";
                        Picasso.get().load(tenantBgImage).into(branding_bg);
                    }

                    File brandingLoader = new File(OustSdkApplication.getContext().getFilesDir(), ("oustlearn_" + tenantId.toUpperCase().trim() + "splashIcon"));
                    if (brandingLoader.exists()) {
                        Picasso.get().load(brandingLoader).into(branding_icon);
                    } else {
                        String tenantIcon = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "appImages/splash/org/" + (tenantId.toUpperCase().trim()) + "/android/icon";
                        Picasso.get().load(tenantIcon).error(getResources().getDrawable(R.drawable.app_icon)).into(branding_icon);
                    }
                }
                branding_mani_layout.setVisibility(VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }

            no_image.setImageDrawable(getResources().getDrawable(R.drawable.no_catalogue));
            no_data_content.setText(getResources().getString(R.string.no_catalogue));

            layout_switcher.setCallback(Type -> {
                type = Type;
                //sort();
                setAdapter();
            });

            see_all_folder.setOnClickListener(v -> {
                if (catalogueFolderAdapter != null) {
                    if (catalogueFolderAdapter.getCatalogueModuleArrayList() != null &&
                            catalogueFolderAdapter.getCatalogueModuleArrayList().size() != 0) {
                        Intent folderIntent = new Intent(getContext(), CatalogueFolderListActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("folderList", catalogueFolderAdapter.getCatalogueModuleArrayList());
                        folderIntent.putExtras(bundle);
                        getContext().startActivity(folderIntent);
                    }
                }
            });

            see_all_modules.setOnClickListener(v -> {
                if (catalogueModuleAdapter != null) {
                    if (catalogueModuleAdapter.getCatalogueModuleArrayList() != null &&
                            catalogueModuleAdapter.getCatalogueModuleArrayList().size() != 0) {
                        Intent folderIntent = new Intent(getContext(), CatalogueModulesActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("moduleList", catalogueModuleAdapter.getCatalogueModuleArrayList());
                        bundle.putString("categoryName", catalogueComponentModule.getCatalogueCategoryName());
                        bundle.putBoolean("isSkillEnabled", catalogueComponentModule.isSkillEnabled());
                        folderIntent.putExtras(bundle);
                        getContext().startActivity(folderIntent);
                    }
                }
            });

            iv_sort.setOnClickListener(v -> {
                isFirstTime = false;
                isAscending = !isAscending;
                //sort();
                setAdapter();
            });

            iv_filter.setOnClickListener(v -> openFilterDialogue());


        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }


    public void setData(CatalogueComponentModule catalogueComponentModule) {

        try {

            if (catalogueComponentModule != null) {

                setFilterData();

                HashMap<String, ArrayList<CatalogueModule>> catalogueHashMap = catalogueComponentModule.getCatalogueBaseHashMap();

                if (catalogueHashMap != null && catalogueHashMap.size() != 0) {

                    if (catalogueHashMap.get("Module") != null) {
                        catalogueModuleList = catalogueHashMap.get("Module");
                        if (catalogueModuleList != null && catalogueModuleList.size() != 0) {
                            isModulesAvailable = true;
                            catalogueModuleAdapter = new CatalogueModuleAdapter();
                        }
                    }

                    if (catalogueHashMap.get("Folder") != null) {
                        catalogueFolderList = catalogueHashMap.get("Folder");

                        if (catalogueFolderList != null && catalogueFolderList.size() != 0) {
                            catalogueFolderAdapter = new CatalogueFolderAdapter();
                        } else {
                            see_all_modules.setVisibility(View.GONE);
                        }
                    } else {
                        see_all_modules.setVisibility(View.GONE);
                    }

                    //sort();
                    setAdapter();
                    catalogue_root.setBackgroundColor(getResources().getColor(R.color.white));
                    data_layout.setVisibility(View.VISIBLE);
                    no_data_layout.setVisibility(View.GONE);
                    branding_mani_layout.setVisibility(View.GONE);
                } else {
                    setNoData();
                }
                branding_mani_layout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setNoData() {
        data_layout.setVisibility(GONE);
        branding_mani_layout.setVisibility(GONE);
        no_data_layout.setVisibility(VISIBLE);
    }

    private void setAdapter() {
        modules_recyclerview.setVisibility(VISIBLE);
        try {
            ArrayList<CatalogueModule> catalogueFolderSetList = catalogueFolderList;
            ArrayList<CatalogueModule> catalogueModuleSetList = catalogueModuleList;


            if (catalogueFolderFilterList != null) {
                catalogueFolderSetList = catalogueFolderFilterList;
            }

            if (catalogueModuleFilterList != null) {
                catalogueModuleSetList = catalogueModuleFilterList;
            }

            isModulesAvailable = catalogueModuleSetList != null && catalogueModuleSetList.size() != 0;

            if (!(catalogueFolderSetList != null && catalogueFolderSetList.size() != 0)) {
                folder_lay.setVisibility(View.GONE);
                no_data_layout.setVisibility(GONE);
            }

            try {
                if (!isFirstTime) {
                    if (!isAscending) {
                        OustResourceUtils.setDefaultDrawableColor(iv_sort.getDrawable(), Color.parseColor("#CECECE"));
                        if (catalogueFolderSetList != null && catalogueFolderSetList.size() != 0) {
                            Collections.sort(catalogueFolderSetList, CatalogueModule.catalogueDescending);
                        }

                        if (catalogueModuleSetList != null && catalogueModuleSetList.size() != 0) {
                            Collections.sort(catalogueModuleSetList, CatalogueModule.catalogueDescending);
                        }
                    } else {
                        OustResourceUtils.setDefaultDrawableColor(iv_sort);
                        if (catalogueFolderSetList != null && catalogueFolderSetList.size() != 0) {
                            Collections.sort(catalogueFolderSetList, CatalogueModule.catalogueAscending);
                        }

                        if (catalogueModuleSetList != null && catalogueModuleSetList.size() != 0) {
                            Collections.sort(catalogueModuleSetList, CatalogueModule.catalogueAscending);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }

            setLayoutManager();
            setFolderAdapters(catalogueFolderSetList);
            setModulesAdapters(catalogueModuleSetList);

            if ((catalogueFolderSetList == null || catalogueFolderSetList.size() == 0) && (catalogueModuleSetList == null || catalogueModuleSetList.size() == 0)) {
                folder_lay.setVisibility(View.GONE);
                modules_lay.setVisibility(View.GONE);
                see_all_folder.setVisibility(GONE);
                modules_recyclerview.setVisibility(GONE);
                no_data_layout.setVisibility(VISIBLE);
                branding_mani_layout.setVisibility(GONE);
                no_data_content.setText(getResources().getString(R.string.no_modules_available));
            } else {
                branding_mani_layout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setLayoutManager() {

        switch (type) {
            case LayoutType.LIST:
                if (isModulesAvailable) {
                    mFolderLayoutManager = null;

                } else {
                    //mFolderLayoutManager = new GridLayoutManager(getContext(), 2, LinearLayoutManager.HORIZONTAL, false);
                    mFolderLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                }
                mModulesLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

                if (catalogueFolderList != null && catalogueFolderList.size() < 3) {
                    mFolderLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                }
                OustStaticVariableHandling.getInstance().setCatalogueGridView(false);

                break;
            case LayoutType.GRID:
                mFolderLayoutManager = new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, false);
                mModulesLayoutManager = new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, false);
                OustStaticVariableHandling.getInstance().setCatalogueGridView(true);
                break;
        }
    }

    private void setFolderAdapters(ArrayList<CatalogueModule> catalogueFolderList) {
        try {
            if (catalogueFolderAdapter == null)
                catalogueFolderAdapter = new CatalogueFolderAdapter();
            if (mFolderLayoutManager == null)
                mFolderLayoutManager = new LinearLayoutManager(getContext());

            if (isModulesAvailable && type != 2) {
                mFolderLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.HORIZONTAL, false);
            }

            if (catalogueFolderList != null && catalogueFolderList.size() != 0) {
                if (catalogueFolderList.size() < 3) {
                    mFolderLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                }
                folder_lay.setVisibility(View.VISIBLE);
                folder_recyclerview.setLayoutManager(mFolderLayoutManager);
                folder_recyclerview.setItemAnimator(new DefaultItemAnimator());
                folder_recyclerview.removeAllViews();
                if (type == 2) {
                    OustStaticVariableHandling.getInstance().setCatalogueGridView(true);
                } else {
                    OustStaticVariableHandling.getInstance().setCatalogueGridView(false);
                }
                catalogueFolderAdapter.setCatalogueFolderAdapter(catalogueFolderList, getContext(), screenWidth, type);
                folder_recyclerview.setAdapter(catalogueFolderAdapter);
                catalogueFolderAdapter.notifyDataSetChanged();
                /*if (searchEditText != null && searchEditText.getText().toString() != null) {
                    catalogueFolderAdapter.getFilter().filter(searchEditText.getText().toString());
                }*/
                folder_recyclerview.setVisibility(View.VISIBLE);
                no_data_layout.setVisibility(GONE);
                branding_mani_layout.setVisibility(View.GONE);

                String folderText;
                if (catalogueFolderList.size() == 1) {
                    folderText = getResources().getString(R.string.folder_text) + " (1)";
                    see_all_folder.setVisibility(View.GONE);
                } else {
                    folderText = getResources().getString(R.string.folders_text) + " (" + catalogueFolderList.size() + ")";
                    if (catalogueFolderList.size() < 3) {
                        see_all_folder.setVisibility(View.GONE);
                    } else {
                        see_all_folder.setVisibility(View.VISIBLE);
                    }
                }

                folder_text.setText(folderText);
            } else {
                if (!isModulesAvailable) {
                    folder_lay.setVisibility(View.GONE);
                    modules_lay.setVisibility(View.GONE);
//                    data_layout.setVisibility(GONE);
                   /* info_text.setText(getResources().getString(R.string.no_modules_available));
                    info_text.setVisibility(View.VISIBLE);*/
//                    setNoData();
                    modules_recyclerview.setVisibility(GONE);
                    branding_mani_layout.setVisibility(GONE);
                    no_data_layout.setVisibility(VISIBLE);
                    no_data_content.setText(getResources().getString(R.string.no_modules_available));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }


    }

    private void setModulesAdapters(ArrayList<CatalogueModule> catalogueModuleList) {

        try {

            if (catalogueModuleAdapter == null)
                catalogueModuleAdapter = new CatalogueModuleAdapter();
            if (mModulesLayoutManager == null)
                mModulesLayoutManager = new LinearLayoutManager(getContext());

            if (catalogueModuleList != null && catalogueModuleList.size() != 0) {
                modules_lay.setVisibility(View.VISIBLE);
                modules_recyclerview.setLayoutManager(mModulesLayoutManager);
                modules_recyclerview.setItemAnimator(new DefaultItemAnimator());
                modules_recyclerview.removeAllViews();
                catalogueModuleAdapter.setCatalogueModuleArrayList(catalogueModuleList, getContext(), type);
                if (catalogueComponentModule != null) {
                    if (catalogueComponentModule.getActiveUser() != null) {
                        catalogueModuleAdapter.setActiveUser(catalogueComponentModule.getActiveUser());
                    }
                }
                catalogueModuleAdapter.setCatalogueCategoryName(catalogueComponentModule.getCatalogueCategoryName());
                modules_recyclerview.setAdapter(catalogueModuleAdapter);
                catalogueModuleAdapter.notifyDataSetChanged();
                modules_recyclerview.setVisibility(View.VISIBLE);
               /* if (searchEditText != null && searchEditText.getText().toString() != null) {
                    catalogueModuleAdapter.getFilter().filter(searchEditText.getText().toString());
                }*/
                String moduleText;
                if (catalogueModuleList.size() == 1) {
                    moduleText = getResources().getString(R.string.module) + " (1)";
                    see_all_modules.setVisibility(View.GONE);
                } else {
                    moduleText = getResources().getString(R.string.modules_text) + " (" + catalogueModuleList.size() + ")";
                    if (catalogueModuleList.size() < 3) {
                        see_all_modules.setVisibility(View.GONE);
                    } else {
                        see_all_modules.setVisibility(View.VISIBLE);
                    }
                }

                modules_text.setText(moduleText);
            } else {
                modules_lay.setVisibility(View.GONE);
            }


        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }


    }

    public void setFilter(FragmentManager fragmentManager, int screenWidth) {
        this.fragmentManager = fragmentManager;
        this.screenWidth = screenWidth;
    }

    private void setFilterData() {
        if (filterCategoriesType == null) {
            filterCategoriesType = new ArrayList<>();
            filterCategoriesType.add(new FilterCategory(getResources().getString(R.string.all) + "", 0));
            filterCategoriesType.add(new FilterCategory(getResources().getString(R.string.folders_text) + "", 4));
        }
        if (filterCategoriesStatus == null) {
            filterCategoriesStatus = new ArrayList<>();
            filterCategoriesStatus.add(new FilterCategory(getResources().getString(R.string.all) + "", 5));
           /* filterCategoriesStatus.add(new FilterCategory("Not Started", 6));
            filterCategoriesStatus.add(new FilterCategory("In Progress", 7));
            filterCategoriesStatus.add(new FilterCategory("Completed", 8));*/
            filterCategoriesStatus.add(new FilterCategory("Viewed", 9));
            filterCategoriesStatus.add(new FilterCategory("Not Viewed", 10));
        }

        if (filterCategoriesType != null && filterCategoriesType.size() != 0 && filterCategoriesStatus.size() != 0) {
            selectedFeedFilter = new ArrayList<>();
            selectedFeedFilter.addAll(filterCategoriesType);
            selectedFeedFilter.addAll(filterCategoriesStatus);
        }

    }

    private void openFilterDialogue() {
        try {
            filterDialog = FilterDialogFragment.newInstance(filterCategoriesType, filterCategoriesStatus, selectedFeedFilter, selectedFilter -> {
                selectedFeedFilter = selectedFilter;
                if (selectedFilter == null || selectedFilter.size() == 0) {
                    catalogueModuleFilterList = null;
                    catalogueFolderFilterList = null;
                    //sort();
                    OustResourceUtils.setDefaultDrawableColor(iv_filter.getDrawable(), Color.parseColor("#CECECE"));
                    setAdapter();
                } else {
                    ArrayList<Integer> filterCategoryList = new ArrayList<>();
                    for (FilterCategory filter : selectedFilter) {
                        filterCategoryList.add(filter.getCategoryType());
                    }
                    OustResourceUtils.setDefaultDrawableColor(iv_filter);

                    meetFilterCriteria(filterCategoryList);
                }
            });
            filterDialog.show(fragmentManager, "Filter Dialog");
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void meetFilterCriteria(ArrayList<Integer> filter) {
        try {
            HashMap<String, HashMap<String, CatalogueModule>> filterFolderHashMap = new HashMap<>();
            HashMap<String, HashMap<String, CatalogueModule>> filterModuleHashMap = new HashMap<>();
            boolean statusFilter = false;
            boolean courseFilter = false;
            boolean assessmentFilter = false;
            boolean skillFilter = false;
            boolean folderFilter = false;

            if (filter != null && filter.size() != 0) {

                Collections.sort(filter);

                for (int filterForAll : filter) {
                    if (filterForAll == 6 || filterForAll == 7 || filterForAll == 8 || filterForAll == 9 || filterForAll == 10) {
                        statusFilter = true;
                    }

                    if (filterForAll == 0 || filterForAll == 4) {
                        folderFilter = true;
                    }
                    if (filterForAll == 1) {
                        courseFilter = true;
                    }

                    if (filterForAll == 2) {
                        assessmentFilter = true;
                    }
                }

                for (int filterForAll : filter) {
                    if (catalogueFolderList != null && catalogueFolderList.size() != 0) {
                        ArrayList<CatalogueModule> copyList = catalogueFolderList;
                        for (int i = 0; i < copyList.size(); i++) {

                            CatalogueModule catalogueModule = copyList.get(i);

                            if (filterForAll == 0 || filterForAll == 4 || (folderFilter && statusFilter)) {

                                if (statusFilter) {
                                    if (filterForAll == 9) {
                                        if (catalogueModule.getViewStatus() != null && catalogueModule.getViewStatus().equalsIgnoreCase("SEEN")) {
                                            if (filterFolderHashMap.size() != 0) {
                                                if (filterFolderHashMap.get("Folder") == null) {
                                                    filterFolderHashMap.put("Folder", new HashMap<>());
                                                }
                                            } else {
                                                filterFolderHashMap.put("Folder", new HashMap<>());
                                            }
                                            Objects.requireNonNull(filterFolderHashMap.get("Folder")).put("Folder" + catalogueModule.getContentId(), catalogueModule);

                                        }

                                    } else if (filterForAll == 10) {
                                        if (catalogueModule.getViewStatus() != null && !catalogueModule.getViewStatus().equalsIgnoreCase("SEEN")) {
                                            if (filterFolderHashMap.size() != 0) {
                                                if (filterFolderHashMap.get("Folder") == null) {
                                                    filterFolderHashMap.put("Folder", new HashMap<>());
                                                }
                                            } else {
                                                filterFolderHashMap.put("Folder", new HashMap<>());
                                            }
                                            Objects.requireNonNull(filterFolderHashMap.get("Folder")).put("Folder" + catalogueModule.getContentId(), catalogueModule);
                                        }
                                    }
                                } else {
                                    if (filterFolderHashMap.size() != 0) {
                                        if (filterFolderHashMap.get("Folder") == null) {
                                            filterFolderHashMap.put("Folder", new HashMap<>());
                                        }
                                    } else {
                                        filterFolderHashMap.put("Folder", new HashMap<>());
                                    }
                                    Objects.requireNonNull(filterFolderHashMap.get("Folder")).put("Folder" + catalogueModule.getContentId(), catalogueModule);
                                }
                            }
                        }
                    }


                    if (catalogueModuleList != null && catalogueModuleList.size() != 0) {
                        ArrayList<CatalogueModule> copyList = catalogueModuleList;

                        for (int i = 0; i < copyList.size(); i++) {

                            CatalogueModule catalogueModule = copyList.get(i);

                            if ((!courseFilter && !assessmentFilter && statusFilter)) {
                                if (filterForAll == 6) {
                                    if (catalogueModule.getContentType() != null && catalogueModule.getContentType().equalsIgnoreCase("COURSE")) {

                                        if (catalogueModule.isEnrolled() && catalogueModule.getCompletionPercentage() == 0) {
                                            if (filterModuleHashMap.size() != 0) {
                                                if (filterModuleHashMap.get("Module") == null) {
                                                    filterModuleHashMap.put("Module", new HashMap<>());
                                                }
                                            } else {
                                                filterModuleHashMap.put("Module", new HashMap<>());
                                            }
                                            Objects.requireNonNull(filterModuleHashMap.get("Module")).put("Module" + catalogueModule.getContentId(), catalogueModule);

                                        } else if (!catalogueModule.isEnrolled()) {
                                            if (filterModuleHashMap.size() != 0) {
                                                if (filterModuleHashMap.get("Module") == null) {
                                                    filterModuleHashMap.put("Module", new HashMap<>());
                                                }
                                            } else {
                                                filterModuleHashMap.put("Module", new HashMap<>());
                                            }
                                            Objects.requireNonNull(filterModuleHashMap.get("Module")).put("Module" + catalogueModule.getContentId(), catalogueModule);

                                        }

                                    } else {

                                        if (catalogueModule.isEnrolled() &&
                                                (catalogueModule.getState() == null ||
                                                        (catalogueModule.getState() != null &&
                                                                (catalogueModule.getState().isEmpty() ||
                                                                        catalogueModule.getState().equalsIgnoreCase("null"))))) {
                                            if (filterModuleHashMap.size() != 0) {
                                                if (filterModuleHashMap.get("Module") == null) {
                                                    filterModuleHashMap.put("Module", new HashMap<>());
                                                }
                                            } else {
                                                filterModuleHashMap.put("Module", new HashMap<>());
                                            }
                                            Objects.requireNonNull(filterModuleHashMap.get("Module")).put("Module" + catalogueModule.getContentId(), catalogueModule);

                                        } else if (!catalogueModule.isEnrolled()) {
                                            if (filterModuleHashMap.size() != 0) {
                                                if (filterModuleHashMap.get("Module") == null) {
                                                    filterModuleHashMap.put("Module", new HashMap<>());
                                                }
                                            } else {
                                                filterModuleHashMap.put("Module", new HashMap<>());
                                            }
                                            Objects.requireNonNull(filterModuleHashMap.get("Module")).put("Module" + catalogueModule.getContentId(), catalogueModule);
                                        }
                                    }
                                } else if (filterForAll == 7) {
                                    if (catalogueModule.getContentType() != null && catalogueModule.getContentType().equalsIgnoreCase("COURSE")) {

                                        if (catalogueModule.isEnrolled() && catalogueModule.getCompletionPercentage() > 0 && catalogueModule.getCompletionPercentage() < 100) {
                                            if (filterModuleHashMap.size() != 0) {
                                                if (filterModuleHashMap.get("Module") == null) {
                                                    filterModuleHashMap.put("Module", new HashMap<>());
                                                }
                                            } else {
                                                filterModuleHashMap.put("Module", new HashMap<>());
                                            }
                                            Objects.requireNonNull(filterModuleHashMap.get("Module")).put("Module" + catalogueModule.getContentId(), catalogueModule);

                                        }

                                    } else {

                                        if (catalogueModule.isEnrolled() &&
                                                ((catalogueModule.getState() != null && catalogueModule.getState().equalsIgnoreCase("INPROGRESS")))) {
                                            if (filterModuleHashMap.size() != 0) {
                                                if (filterModuleHashMap.get("Module") == null) {
                                                    filterModuleHashMap.put("Module", new HashMap<>());
                                                }
                                            } else {
                                                filterModuleHashMap.put("Module", new HashMap<>());
                                            }
                                            Objects.requireNonNull(filterModuleHashMap.get("Module")).put("Module" + catalogueModule.getContentId(), catalogueModule);

                                        }

                                    }

                                } else if (filterForAll == 8) {
                                    if (catalogueModule.isEnrolled() && catalogueModule.getCompletionDateAndTime() != null && !catalogueModule.getCompletionDateAndTime().equalsIgnoreCase("null")) {
                                        if (filterModuleHashMap.size() != 0) {
                                            if (filterModuleHashMap.get("Module") == null) {
                                                filterModuleHashMap.put("Module", new HashMap<>());
                                            }
                                        } else {
                                            filterModuleHashMap.put("Module", new HashMap<>());
                                        }
                                        Objects.requireNonNull(filterModuleHashMap.get("Module")).put("Module" + catalogueModule.getContentId(), catalogueModule);

                                    }

                                } else if (filterForAll == 9) {
                                    if (catalogueModule.getViewStatus() != null && catalogueModule.getViewStatus().equalsIgnoreCase("SEEN")) {
                                        if (filterModuleHashMap.size() != 0) {
                                            if (filterModuleHashMap.get("Module") == null) {
                                                filterModuleHashMap.put("Module", new HashMap<>());
                                            }
                                        } else {
                                            filterModuleHashMap.put("Module", new HashMap<>());
                                        }
                                        Objects.requireNonNull(filterModuleHashMap.get("Module")).put("Module" + catalogueModule.getContentId(), catalogueModule);

                                    }

                                } else if (filterForAll == 10) {
                                    if (catalogueModule.getViewStatus() != null && !catalogueModule.getViewStatus().equalsIgnoreCase("SEEN")) {
                                        if (filterModuleHashMap.size() != 0) {
                                            if (filterModuleHashMap.get("Module") == null) {
                                                filterModuleHashMap.put("Module", new HashMap<>());
                                            }
                                        } else {
                                            filterModuleHashMap.put("Module", new HashMap<>());
                                        }
                                        Objects.requireNonNull(filterModuleHashMap.get("Module")).put("Module" + catalogueModule.getContentId(), catalogueModule);

                                    }

                                }

                            } else {
                                if (filterForAll == 1 || (courseFilter && statusFilter)) {
                                    if (catalogueModule.getContentType() != null && catalogueModule.getContentType().equalsIgnoreCase("COURSE")) {

                                        if (statusFilter) {
                                            if (filterForAll == 6) {
                                                if (catalogueModule.isEnrolled() && catalogueModule.getCompletionPercentage() == 0) {
                                                    if (filterModuleHashMap.size() != 0) {
                                                        if (filterModuleHashMap.get("Module") == null) {
                                                            filterModuleHashMap.put("Module", new HashMap<>());
                                                        }
                                                    } else {
                                                        filterModuleHashMap.put("Module", new HashMap<>());
                                                    }
                                                    Objects.requireNonNull(filterModuleHashMap.get("Module")).put("Module" + catalogueModule.getContentId(), catalogueModule);

                                                } else if (!catalogueModule.isEnrolled()) {
                                                    if (filterModuleHashMap.size() != 0) {
                                                        if (filterModuleHashMap.get("Module") == null) {
                                                            filterModuleHashMap.put("Module", new HashMap<>());
                                                        }
                                                    } else {
                                                        filterModuleHashMap.put("Module", new HashMap<>());
                                                    }
                                                    Objects.requireNonNull(filterModuleHashMap.get("Module")).put("Module" + catalogueModule.getContentId(), catalogueModule);

                                                }


                                            } else if (filterForAll == 7) {
                                                if (catalogueModule.getContentType() != null && catalogueModule.getContentType().equalsIgnoreCase("COURSE")) {

                                                    if (catalogueModule.isEnrolled() && catalogueModule.getCompletionPercentage() > 0 && catalogueModule.getCompletionPercentage() < 100) {
                                                        if (filterModuleHashMap.size() != 0) {
                                                            if (filterModuleHashMap.get("Module") == null) {
                                                                filterModuleHashMap.put("Module", new HashMap<>());
                                                            }
                                                        } else {
                                                            filterModuleHashMap.put("Module", new HashMap<>());
                                                        }
                                                        Objects.requireNonNull(filterModuleHashMap.get("Module")).put("Module" + catalogueModule.getContentId(), catalogueModule);

                                                    }

                                                }


                                            } else if (filterForAll == 8) {
                                                if (catalogueModule.isEnrolled() && catalogueModule.getCompletionDateAndTime() != null && !catalogueModule.getCompletionDateAndTime().equalsIgnoreCase("null")) {
                                                    if (filterModuleHashMap.size() != 0) {
                                                        if (filterModuleHashMap.get("Module") == null) {
                                                            filterModuleHashMap.put("Module", new HashMap<>());
                                                        }
                                                    } else {
                                                        filterModuleHashMap.put("Module", new HashMap<>());
                                                    }
                                                    Objects.requireNonNull(filterModuleHashMap.get("Module")).put("Module" + catalogueModule.getContentId(), catalogueModule);

                                                }

                                            } else if (filterForAll == 9) {
                                                if (catalogueModule.getViewStatus() != null && catalogueModule.getViewStatus().equalsIgnoreCase("SEEN")) {
                                                    if (filterModuleHashMap.size() != 0) {
                                                        if (filterModuleHashMap.get("Module") == null) {
                                                            filterModuleHashMap.put("Module", new HashMap<>());
                                                        }
                                                    } else {
                                                        filterModuleHashMap.put("Module", new HashMap<>());
                                                    }
                                                    Objects.requireNonNull(filterModuleHashMap.get("Module")).put("Module" + catalogueModule.getContentId(), catalogueModule);

                                                }

                                            } else if (filterForAll == 10) {
                                                if (catalogueModule.getViewStatus() != null && !catalogueModule.getViewStatus().equalsIgnoreCase("SEEN")) {
                                                    if (filterModuleHashMap.size() != 0) {
                                                        if (filterModuleHashMap.get("Module") == null) {
                                                            filterModuleHashMap.put("Module", new HashMap<>());
                                                        }
                                                    } else {
                                                        filterModuleHashMap.put("Module", new HashMap<>());
                                                    }
                                                    Objects.requireNonNull(filterModuleHashMap.get("Module")).put("Module" + catalogueModule.getContentId(), catalogueModule);

                                                }

                                            }
                                        } else {
                                            if (filterModuleHashMap.size() != 0) {
                                                if (filterModuleHashMap.get("Module") == null) {
                                                    filterModuleHashMap.put("Module", new HashMap<>());
                                                }
                                            } else {
                                                filterModuleHashMap.put("Module", new HashMap<>());
                                            }
                                            Objects.requireNonNull(filterModuleHashMap.get("Module")).put("Module" + catalogueModule.getContentId(), catalogueModule);
                                        }


                                    }

                                }


                                if (filterForAll == 2 || (assessmentFilter && statusFilter)) {

                                    if (catalogueModule.getContentType() != null && catalogueModule.getContentType().equalsIgnoreCase("ASSESSMENT")) {
                                        if (statusFilter) {
                                            if (filterForAll == 6) {
                                                if (catalogueModule.isEnrolled() &&
                                                        (catalogueModule.getState() == null ||
                                                                (catalogueModule.getState() != null &&
                                                                        (catalogueModule.getState().isEmpty() ||
                                                                                catalogueModule.getState().equalsIgnoreCase("null"))))) {
                                                    if (filterModuleHashMap.size() != 0) {
                                                        if (filterModuleHashMap.get("Module") == null) {
                                                            filterModuleHashMap.put("Module", new HashMap<>());
                                                        }
                                                    } else {
                                                        filterModuleHashMap.put("Module", new HashMap<>());
                                                    }
                                                    Objects.requireNonNull(filterModuleHashMap.get("Module")).put("Module" + catalogueModule.getContentId(), catalogueModule);

                                                } else if (!catalogueModule.isEnrolled()) {
                                                    if (filterModuleHashMap.size() != 0) {
                                                        if (filterModuleHashMap.get("Module") == null) {
                                                            filterModuleHashMap.put("Module", new HashMap<>());
                                                        }
                                                    } else {
                                                        filterModuleHashMap.put("Module", new HashMap<>());
                                                    }
                                                    Objects.requireNonNull(filterModuleHashMap.get("Module")).put("Module" + catalogueModule.getContentId(), catalogueModule);
                                                }
                                            } else if (filterForAll == 7) {
                                                if (catalogueModule.isEnrolled() &&
                                                        ((catalogueModule.getState() != null && catalogueModule.getState().equalsIgnoreCase("INPROGRESS")))) {
                                                    if (filterModuleHashMap.size() != 0) {
                                                        if (filterModuleHashMap.get("Module") == null) {
                                                            filterModuleHashMap.put("Module", new HashMap<>());
                                                        }
                                                    } else {
                                                        filterModuleHashMap.put("Module", new HashMap<>());
                                                    }
                                                    Objects.requireNonNull(filterModuleHashMap.get("Module")).put("Module" + catalogueModule.getContentId(), catalogueModule);
                                                }
                                            } else if (filterForAll == 8) {
                                                if (catalogueModule.isEnrolled() && catalogueModule.getCompletionDateAndTime() != null
                                                        && !catalogueModule.getCompletionDateAndTime().equalsIgnoreCase("null")) {
                                                    if (filterModuleHashMap.size() != 0) {
                                                        if (filterModuleHashMap.get("Module") == null) {
                                                            filterModuleHashMap.put("Module", new HashMap<>());
                                                        }
                                                    } else {
                                                        filterModuleHashMap.put("Module", new HashMap<>());
                                                    }
                                                    Objects.requireNonNull(filterModuleHashMap.get("Module")).put("Module" + catalogueModule.getContentId(), catalogueModule);
                                                }
                                            } else if (filterForAll == 9) {
                                                if (catalogueModule.getViewStatus() != null && catalogueModule.getViewStatus().equalsIgnoreCase("SEEN")) {
                                                    if (filterModuleHashMap.size() != 0) {
                                                        if (filterModuleHashMap.get("Module") == null) {
                                                            filterModuleHashMap.put("Module", new HashMap<>());
                                                        }
                                                    } else {
                                                        filterModuleHashMap.put("Module", new HashMap<>());
                                                    }
                                                    Objects.requireNonNull(filterModuleHashMap.get("Module")).put("Module" + catalogueModule.getContentId(), catalogueModule);
                                                }

                                            } else if (filterForAll == 10) {
                                                if (catalogueModule.getViewStatus() != null && !catalogueModule.getViewStatus().equalsIgnoreCase("SEEN")) {
                                                    if (filterModuleHashMap.size() != 0) {
                                                        if (filterModuleHashMap.get("Module") == null) {
                                                            filterModuleHashMap.put("Module", new HashMap<>());
                                                        }
                                                    } else {
                                                        filterModuleHashMap.put("Module", new HashMap<>());
                                                    }
                                                    Objects.requireNonNull(filterModuleHashMap.get("Module")).put("Module" + catalogueModule.getContentId(), catalogueModule);

                                                }
                                            }
                                        } else {
                                            if (filterModuleHashMap.size() != 0) {
                                                if (filterModuleHashMap.get("Module") == null) {
                                                    filterModuleHashMap.put("Module", new HashMap<>());
                                                }
                                            } else {
                                                filterModuleHashMap.put("Module", new HashMap<>());
                                            }
                                            Objects.requireNonNull(filterModuleHashMap.get("Module")).put("Module" + catalogueModule.getContentId(), catalogueModule);
                                        }
                                    }
                                }
                                if (filterForAll == 3 || (skillFilter && statusFilter)) {
                                    if (catalogueModule.getContentType() != null && catalogueModule.getContentType().equalsIgnoreCase("SOCCER_SKILL")) {
                                        if (statusFilter) {
                                            if (filterForAll == 9) {
                                                if (catalogueModule.getViewStatus() != null && catalogueModule.getViewStatus().equalsIgnoreCase("SEEN")) {
                                                    if (filterModuleHashMap.size() != 0) {
                                                        if (filterModuleHashMap.get("Module") == null) {
                                                            filterModuleHashMap.put("Module", new HashMap<>());
                                                        }
                                                    } else {
                                                        filterModuleHashMap.put("Module", new HashMap<>());
                                                    }
                                                    Objects.requireNonNull(filterModuleHashMap.get("Module")).put("Module" + catalogueModule.getContentId(), catalogueModule);

                                                }

                                            } else if (filterForAll == 10) {
                                                if (catalogueModule.getViewStatus() != null && !catalogueModule.getViewStatus().equalsIgnoreCase("SEEN")) {
                                                    if (filterModuleHashMap.size() != 0) {
                                                        if (filterModuleHashMap.get("Module") == null) {
                                                            filterModuleHashMap.put("Module", new HashMap<>());
                                                        }
                                                    } else {
                                                        filterModuleHashMap.put("Module", new HashMap<>());
                                                    }
                                                    Objects.requireNonNull(filterModuleHashMap.get("Module")).put("Module" + catalogueModule.getContentId(), catalogueModule);

                                                }

                                            }
                                        } else {
                                            if (filterModuleHashMap.size() != 0) {
                                                if (filterModuleHashMap.get("Module") == null) {
                                                    filterModuleHashMap.put("Module", new HashMap<>());
                                                }
                                            } else {
                                                filterModuleHashMap.put("Module", new HashMap<>());
                                            }
                                            Objects.requireNonNull(filterModuleHashMap.get("Module")).put("Module" + catalogueModule.getContentId(), catalogueModule);
                                        }

                                    }

                                }
                            }
                        }
                    }
                }

                if (filterFolderHashMap.size() != 0) {

                    if (filterFolderHashMap.get("Folder") != null && Objects.requireNonNull(filterFolderHashMap.get("Folder")).size() != 0) {
                        catalogueFolderFilterList = new ArrayList<>(Objects.requireNonNull(filterFolderHashMap.get("Folder")).values());
                    } else {
                        catalogueFolderFilterList = new ArrayList<>();
                    }

                } else {
                    catalogueFolderFilterList = new ArrayList<>();
                }

                if (filterModuleHashMap.size() != 0) {

                    if (filterModuleHashMap.get("Module") != null && Objects.requireNonNull(filterModuleHashMap.get("Module")).size() != 0) {
                        catalogueModuleFilterList = new ArrayList<>(Objects.requireNonNull(filterModuleHashMap.get("Module")).values());
                    } else {
                        catalogueModuleFilterList = new ArrayList<>();
                    }
                } else {
                    catalogueModuleFilterList = new ArrayList<>();
                }

                //sort();
                setAdapter();
            } else {

                catalogueFolderFilterList = null;
                catalogueModuleFilterList = null;
                //sort();
                setAdapter();
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    public void setRefreshing(boolean refreshing) {
        this.isRefreshedData = refreshing;
    }

    public boolean getRefreshing() {
        return isRefreshedData;
    }

    public void onCatalogSearch(String query) {
        try {
            if (query != null) {
                if (catalogueFolderAdapter != null) {
                    catalogueFolderAdapter.getFilter().filter(query);
                }
                if (catalogueModuleAdapter != null) {
                    catalogueModuleAdapter.getFilter().filter(query);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
