package com.oustme.oustsdk.catalogue_ui;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.R;
import com.oustme.oustsdk.base.BaseActivity;
import com.oustme.oustsdk.catalogue_ui.adapter.CatalogueFolderAdapter;
import com.oustme.oustsdk.catalogue_ui.adapter.CatalogueModuleAdapter;
import com.oustme.oustsdk.catalogue_ui.model.CatalogueComponentModule;
import com.oustme.oustsdk.catalogue_ui.model.CatalogueModule;
import com.oustme.oustsdk.catalogue_ui.model.CatalogueModuleUpdate;
import com.oustme.oustsdk.customviews.LayoutSwitcher;
import com.oustme.oustsdk.filter.FilterDialogFragment;
import com.oustme.oustsdk.filter.FilterForAll;
import com.oustme.oustsdk.tools.OustGATools;
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

public class CatalogueModuleListActivity extends BaseActivity {

    Toolbar toolbar_lay;
    ImageView toolbar_close_icon, iv_sort, iv_filter;
    TextView catalogue_name, folder_text, see_all_folder, info_text, modules_text, see_all_modules;
    LinearLayout data_layout, folder_lay, modules_lay, folder_info, module_info;
    RecyclerView folder_recyclerview, modules_recyclerview;
    LayoutSwitcher layout_switcher;
    SearchView search_view_catalogue;
    EditText searchEditText;

    //Branding loader
    RelativeLayout branding_mani_layout;
    ImageView branding_bg;
    ImageView branding_icon;
    TextView branding_percentage;
    //End

    private FragmentManager fragmentManager;
    FilterDialogFragment filterDialog;


    private int color;
    int type = 1;
    private boolean isAscending = true;
    private boolean isFirstTime = true;
    boolean isModulesAvailable = false;

    //MVVM
    CatalogueViewModel catalogueViewModel;
    CatalogueComponentModule catalogueComponentModule;

    CatalogueFolderAdapter catalogueFolderAdapter;
    CatalogueModuleAdapter catalogueModuleAdapter;

    ArrayList<CatalogueModule> catalogueModuleList;
    ArrayList<CatalogueModule> catalogueFolderList;

    ArrayList<CatalogueModule> catalogueModuleFilterList;
    ArrayList<CatalogueModule> catalogueFolderFilterList;


    RecyclerView.LayoutManager mFolderLayoutManager;
    RecyclerView.LayoutManager mModulesLayoutManager;

    private ArrayList<FilterForAll> filterCategories;
    private ArrayList<FilterForAll> filterCategories1;
    private ArrayList<FilterForAll> selectedFeedFilter;


    @Override
    protected int getContentView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_catalogue_module_list;
    }

    @Override
    protected void initView() {

        try {

            if (OustSdkApplication.getContext() == null) {
                OustSdkApplication.setmContext(CatalogueModuleListActivity.this);
            }
            OustSdkTools.setLocale(CatalogueModuleListActivity.this);
//            OustGATools.getInstance().reportPageViewToGoogle(CatalogueModuleListActivity.this, "Oust Catalogue module Page");
            toolbar_lay = findViewById(R.id.toolbar_lay);
            toolbar_close_icon = findViewById(R.id.toolbar_close_icon);
            catalogue_name = findViewById(R.id.catalogue_name);
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
            info_text = findViewById(R.id.info_text);
            layout_switcher = findViewById(R.id.layout_switcher);
            search_view_catalogue = findViewById(R.id.search_view_catalogue);

            //Branding loader
            branding_mani_layout = findViewById(R.id.branding_main_layout);
            branding_bg = findViewById(R.id.branding_bg);
            branding_icon = findViewById(R.id.brand_loader);
            branding_percentage = findViewById(R.id.percentage_text);
            //End

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

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    @Override
    protected void initData() {

        try {
            getColors();
            setToolbarAndIconColor();
            setFilter(getSupportFragmentManager());


            fetchLayoutData();

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    @Override
    protected void initListener() {

        try {
            onNetworkConnectionChanged(OustSdkTools.checkInternetStatus());
            toolbar_close_icon.setOnClickListener(v -> onBackPressed());

            layout_switcher.setCallback(Type -> {
                type = Type;
                //sort();
                setAdapter();
            });

            see_all_folder.setOnClickListener(v -> {
                if (catalogueFolderAdapter != null) {
                    if (catalogueFolderAdapter.getCatalogueModuleArrayList() != null &&
                            catalogueFolderAdapter.getCatalogueModuleArrayList().size() != 0) {
                        Intent folderIntent = new Intent(CatalogueModuleListActivity.this, CatalogueFolderListActivity.class);
                        folderIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("folderList", catalogueFolderAdapter.getCatalogueModuleArrayList());
                        folderIntent.putExtras(bundle);
                        startActivity(folderIntent);
                    }
                }
            });

            see_all_modules.setOnClickListener(v -> {
                if (catalogueModuleAdapter != null) {
                    if (catalogueModuleAdapter.getCatalogueModuleArrayList() != null &&
                            catalogueModuleAdapter.getCatalogueModuleArrayList().size() != 0) {
                        Intent folderIntent = new Intent(CatalogueModuleListActivity.this, CatalogueModulesActivity.class);
                        folderIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("moduleList", catalogueModuleAdapter.getCatalogueModuleArrayList());
                        bundle.putString("categoryName", catalogueComponentModule.getCatalogueCategoryName());
                        bundle.putBoolean("isSkillEnabled", catalogueComponentModule.isSkillEnabled());
                        folderIntent.putExtras(bundle);
                        startActivity(folderIntent);
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

            searchEditText = search_view_catalogue.findViewById(androidx.appcompat.R.id.search_src_text);
            searchEditText.setTextColor(Color.WHITE);
            searchEditText.setHintTextColor(Color.WHITE);

            search_view_catalogue.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    catalogue_name.setVisibility(View.GONE);
                }
            });

            search_view_catalogue.setOnCloseListener(() -> {
                catalogue_name.setVisibility(View.VISIBLE);
                folder_info.setVisibility(View.VISIBLE);
                module_info.setVisibility(View.VISIBLE);

                if (catalogueModuleAdapter != null) {
                    catalogueModuleAdapter.getFilter().filter("");

                }

                if (catalogueFolderAdapter != null) {
                    catalogueFolderAdapter.getFilter().filter("");

                }

                return false;
            });

            search_view_catalogue.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {

                    if (catalogueModuleAdapter != null) {
                        catalogueModuleAdapter.getFilter().filter(query);

                    }

                    if (catalogueFolderAdapter != null) {
                        catalogueFolderAdapter.getFilter().filter(query);


                    }

                    folder_info.setVisibility(View.GONE);
                    module_info.setVisibility(View.GONE);


                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    if (catalogueModuleAdapter != null) {
                        catalogueModuleAdapter.getFilter().filter(newText);

                    }

                    if (catalogueFolderAdapter != null) {
                        catalogueFolderAdapter.getFilter().filter(newText);

                    }


                    folder_info.setVisibility(View.GONE);
                    module_info.setVisibility(View.GONE);
                    return false;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

        if (!isConnected) {
            //handle network connection
           /* SnackBar.with(CatalogueModuleListActivity.this,null)
                    .type(Type.ERROR)
                    .textAlign(Align.CENTER)
                    .message(getResources().getString(R.string.retry_internet_msg))
                    .duration(Duration.LONG)
                    .show();*/

            info_text.setVisibility(View.GONE);
            branding_mani_layout.setVisibility(View.GONE);

        }/*else{
            SnackBar.with(CatalogueModuleListActivity.this,null)
                    .type(Type.SUCCESS)
                    .textAlign(Align.CENTER)
                    .message("You are online now")
                    .duration(Duration.SHORT)
                    .show();
            fetchLayoutData();
        }*/

    }

    private void getColors() {
        try {
            color = OustResourceUtils.getColors();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void setToolbarAndIconColor() {

        try {

            toolbar_lay.setBackgroundColor(color);
            setSupportActionBar(toolbar_lay);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }


    }

    private void fetchLayoutData() {
        try {
            Bundle bundle = getIntent().getExtras();
            if (catalogueViewModel != null) {
                getViewModelStore().clear();
                this.catalogueViewModel = null;
            }
            catalogueViewModel = new ViewModelProvider(this).get(CatalogueViewModel.class);
            catalogueViewModel.init(bundle);
            catalogueViewModel.getBaseComponentModuleMutableLiveData().observe(this, catalogueComponentModule -> {
                if (catalogueComponentModule == null)
                    return;

                this.catalogueComponentModule = catalogueComponentModule;
                setData(catalogueComponentModule);

            });


        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setData(CatalogueComponentModule catalogueComponentModule) {

        try {

            if (catalogueComponentModule != null) {

                setFilterData();

                if (catalogueComponentModule.getCatalogueCategoryName() != null
                        && !catalogueComponentModule.getCatalogueCategoryName().isEmpty()) {
                    catalogue_name.setText(catalogueComponentModule.getCatalogueCategoryName());
                    catalogue_name.setSelected(true);
                }

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

                    data_layout.setVisibility(View.VISIBLE);
                    info_text.setVisibility(View.GONE);


                } else {
                    info_text.setText(getResources().getString(R.string.no_modules_available));
                    data_layout.setVisibility(View.GONE);
                    info_text.setVisibility(View.VISIBLE);
                }
                branding_mani_layout.setVisibility(View.GONE);

            }


        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate() != null) {
            CatalogueModuleUpdate catalogueModuleUpdate = OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate();
            if (catalogueModuleUpdate.isUpdated()) {

                if (catalogueModuleUpdate.getType() != null) {
                    if (catalogueModuleUpdate.getType().equalsIgnoreCase("Module")) {

                        if (catalogueModuleAdapter != null) {
                            if (catalogueModuleAdapter.getCatalogueModuleArrayList() != null && catalogueModuleAdapter.getCatalogueModuleArrayList().size() != 0) {
                                if (catalogueModuleUpdate.getPosition() < catalogueModuleAdapter.getCatalogueModuleArrayList().size()) {
                                    catalogueModuleAdapter.modifyItem(catalogueModuleUpdate.getPosition(), catalogueModuleUpdate.getCatalogueModule());
                                    catalogueModuleAdapter.notifyDataSetChanged();
                                }
                            }
                        }

                    }
                }

            }

            OustStaticVariableHandling.getInstance().setCatalogueModuleUpdate(null);
        }
    }

    private void setAdapter() {
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
            }

            try {

                if (!isFirstTime) {
                    if (!isAscending) {
                        OustResourceUtils.setDefaultDrawableColor(iv_sort.getDrawable(), getResources().getColor(R.color.unselected_text));
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
                info_text.setText(getResources().getString(R.string.no_modules_available));
                info_text.setVisibility(View.VISIBLE);
            } else {
                info_text.setVisibility(View.GONE);
            }
            branding_mani_layout.setVisibility(View.GONE);

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
                    //mFolderLayoutManager = new GridLayoutManager(CatalogueModuleListActivity.this, 2, LinearLayoutManager.HORIZONTAL, false);
                    mFolderLayoutManager = new LinearLayoutManager(CatalogueModuleListActivity.this, LinearLayoutManager.VERTICAL, false);
                }
                mModulesLayoutManager = new LinearLayoutManager(CatalogueModuleListActivity.this, LinearLayoutManager.VERTICAL, false);

                if (catalogueFolderList != null && catalogueFolderList.size() < 3) {
                    mFolderLayoutManager = new LinearLayoutManager(CatalogueModuleListActivity.this, LinearLayoutManager.VERTICAL, false);
                }
                OustStaticVariableHandling.getInstance().setCatalogueGridView(false);

                break;
            case LayoutType.GRID:
                mFolderLayoutManager = new GridLayoutManager(CatalogueModuleListActivity.this, 2, LinearLayoutManager.VERTICAL, false);
                mModulesLayoutManager = new GridLayoutManager(CatalogueModuleListActivity.this, 2, LinearLayoutManager.VERTICAL, false);
                OustStaticVariableHandling.getInstance().setCatalogueGridView(true);
                break;
        }
    }

    private void setFolderAdapters(ArrayList<CatalogueModule> catalogueFolderList) {

        try {
            if (catalogueFolderAdapter == null)
                catalogueFolderAdapter = new CatalogueFolderAdapter();
            if (mFolderLayoutManager == null)
                mFolderLayoutManager = new LinearLayoutManager(CatalogueModuleListActivity.this);
            int screenWidth = 0;
            if (isModulesAvailable && type != 2) {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                screenWidth = displayMetrics.widthPixels;
                mFolderLayoutManager = new GridLayoutManager(CatalogueModuleListActivity.this, 2, GridLayoutManager.HORIZONTAL, false);
            }

            if (catalogueFolderList != null && catalogueFolderList.size() != 0) {
                if (catalogueFolderList.size() < 3) {
                    mFolderLayoutManager = new LinearLayoutManager(CatalogueModuleListActivity.this, LinearLayoutManager.VERTICAL, false);
                }
                folder_lay.setVisibility(View.VISIBLE);
                folder_recyclerview.setLayoutManager(mFolderLayoutManager);
                folder_recyclerview.setItemAnimator(new DefaultItemAnimator());
                folder_recyclerview.removeAllViews();
                catalogueFolderAdapter.setCatalogueFolderAdapter(catalogueFolderList, CatalogueModuleListActivity.this, screenWidth, type);
                folder_recyclerview.setAdapter(catalogueFolderAdapter);
                catalogueFolderAdapter.notifyDataSetChanged();
                if (searchEditText != null) {
                    searchEditText.getText().toString();
                    catalogueFolderAdapter.getFilter().filter(searchEditText.getText().toString());
                }
                folder_recyclerview.setVisibility(View.VISIBLE);
                info_text.setVisibility(View.GONE);
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
                    info_text.setText(getResources().getString(R.string.no_modules_available));
                    info_text.setVisibility(View.VISIBLE);
                    branding_mani_layout.setVisibility(View.GONE);
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
                mModulesLayoutManager = new LinearLayoutManager(CatalogueModuleListActivity.this);

            if (catalogueModuleList != null && catalogueModuleList.size() != 0) {
                modules_lay.setVisibility(View.VISIBLE);
                modules_recyclerview.setLayoutManager(mModulesLayoutManager);
                modules_recyclerview.setItemAnimator(new DefaultItemAnimator());
                modules_recyclerview.removeAllViews();
                catalogueModuleAdapter.setCatalogueModuleArrayList(catalogueModuleList, CatalogueModuleListActivity.this, type);
                if (catalogueComponentModule != null) {
                    if (catalogueComponentModule.getActiveUser() != null) {
                        catalogueModuleAdapter.setActiveUser(catalogueComponentModule.getActiveUser());
                    }
                }
                catalogueModuleAdapter.setCatalogueCategoryName(catalogueComponentModule.getCatalogueCategoryName());
                modules_recyclerview.setAdapter(catalogueModuleAdapter);
                catalogueModuleAdapter.notifyDataSetChanged();
                modules_recyclerview.setVisibility(View.VISIBLE);
                if (searchEditText != null) {
                    searchEditText.getText().toString();
                    catalogueModuleAdapter.getFilter().filter(searchEditText.getText().toString());
                }
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

    public void setFilter(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    private void setFilterData() {
        if (filterCategories == null) {
            filterCategories = new ArrayList<>();
            filterCategories.add(new FilterForAll(getResources().getString(R.string.all) + "", 0));
            filterCategories.add(new FilterForAll(getResources().getString(R.string.courses_text) + "", 1));
            filterCategories.add(new FilterForAll(getResources().getString(R.string.assessments_heading) + "", 2));
           /* if(catalogueComponentModule!=null){
                if(catalogueComponentModule.isSkillEnabled()){
                    filterCategories.add(new FilterForAll("Skills", 3));
                }
            }*/

            filterCategories.add(new FilterForAll(getResources().getString(R.string.folders_text) + "", 4));
        }
        if (filterCategories1 == null) {
            filterCategories1 = new ArrayList<>();
            filterCategories1.add(new FilterForAll(getResources().getString(R.string.all) + "", 5));
            filterCategories1.add(new FilterForAll("Not Started", 6));
            filterCategories1.add(new FilterForAll("In Progress", 7));
            filterCategories1.add(new FilterForAll("Completed", 8));
            filterCategories1.add(new FilterForAll("Viewed", 9));
            filterCategories1.add(new FilterForAll("Not Viewed", 10));
        }

        if (filterCategories != null && filterCategories.size() != 0 && filterCategories1 != null && filterCategories1.size() != 0) {
            selectedFeedFilter = new ArrayList<>();
            selectedFeedFilter.addAll(filterCategories);
            selectedFeedFilter.addAll(filterCategories1);
        }
    }

    private void openFilterDialogue() {

        try {
            filterDialog = FilterDialogFragment.newInstance(filterCategories, filterCategories1, selectedFeedFilter, selectedFilter -> {
                selectedFeedFilter = selectedFilter;
                if (selectedFilter == null || selectedFilter.size() == 0) {
                    catalogueModuleFilterList = null;
                    catalogueFolderFilterList = null;
                    //sort();
                    OustResourceUtils.setDefaultDrawableColor(iv_filter.getDrawable(), getResources().getColor(R.color.unselected_text));
                    setAdapter();
                } else {
                    ArrayList<Integer> filterCategoryList = new ArrayList<>();
                    for (FilterForAll filter : selectedFilter) {
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
            boolean statusFilterForModules = false;
            boolean statusFilterForFolder = false;
            boolean courseFilter = false;
            boolean assessmentFilter = false;
            boolean skillFilter = false;
            boolean folderFilter = false;

            if (filter != null && filter.size() != 0) {

                Collections.sort(filter);

                for (int filterForAll : filter) {

                    if (filterForAll == 0 || filterForAll == 4) {
                        folderFilter = true;
                    }
                    if (filterForAll == 0 || filterForAll == 1) {
                        courseFilter = true;
                    }

                    if (filterForAll == 0 || filterForAll == 2) {
                        assessmentFilter = true;
                    }

                    if ((filterForAll == 5 || filterForAll == 6 || filterForAll == 7 || filterForAll == 8) && (courseFilter || assessmentFilter)) {
                        statusFilterForModules = true;
                    }

                    if ((filterForAll == 5 || filterForAll == 9 || filterForAll == 10) && folderFilter) {
                        statusFilterForFolder = true;
                    }
                }

                for (int filterForAll : filter) {

                    if (catalogueFolderList != null && catalogueFolderList.size() != 0) {
                        ArrayList<CatalogueModule> copyList = catalogueFolderList;
                        for (int i = 0; i < copyList.size(); i++) {

                            CatalogueModule catalogueModule = copyList.get(i);
                            if (folderFilter) {

                                if (statusFilterForFolder) {

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

                            if (courseFilter || assessmentFilter) {
                                if (statusFilterForModules) {
                                    if (filterForAll == 6) {
                                        if (catalogueModule.getContentType() != null && catalogueModule.getContentType().equalsIgnoreCase("COURSE") && courseFilter) {

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

                                        } else if (assessmentFilter && catalogueModule.getContentType() != null && catalogueModule.getContentType().equalsIgnoreCase("ASSESSMENT")) {
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
                                        if (catalogueModule.getContentType() != null && catalogueModule.getContentType().equalsIgnoreCase("COURSE") && courseFilter) {

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

                                        } else if (assessmentFilter && catalogueModule.getContentType() != null && catalogueModule.getContentType().equalsIgnoreCase("ASSESSMENT")) {

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
                                        if (catalogueModule.getContentType() != null && catalogueModule.getContentType().equalsIgnoreCase("COURSE") && courseFilter) {
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
                                        } else if (assessmentFilter && catalogueModule.getContentType() != null && catalogueModule.getContentType().equalsIgnoreCase("ASSESSMENT")) {
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


}
