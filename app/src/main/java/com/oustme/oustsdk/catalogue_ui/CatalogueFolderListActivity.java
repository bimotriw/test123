package com.oustme.oustsdk.catalogue_ui;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED;

import android.graphics.Color;
import android.os.Bundle;
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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.R;
import com.oustme.oustsdk.base.BaseActivity;
import com.oustme.oustsdk.catalogue_ui.adapter.CatalogueFolderAdapter;
import com.oustme.oustsdk.catalogue_ui.model.CatalogueModule;
import com.oustme.oustsdk.customviews.LayoutSwitcher;
import com.oustme.oustsdk.filter.FilterDialogFragment;
import com.oustme.oustsdk.filter.FilterForAll;
import com.oustme.oustsdk.tools.OustGATools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.utils.LayoutType;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

public class CatalogueFolderListActivity extends BaseActivity {

    Toolbar toolbar_lay;
    ImageView toolbar_close_icon, iv_sort, iv_filter;
    TextView catalogue_name, info_text;
    LinearLayout data_layout;
    RecyclerView folder_recyclerview;
    LayoutSwitcher layout_switcher;
    SearchView search_view_catalogue;
    EditText searchEditText;

    //Branding loader
    RelativeLayout branding_mani_layout;
    ImageView branding_bg;
    ImageView branding_icon;
    TextView branding_percentage;
    //End
    RecyclerView.LayoutManager mLayoutManager;

    private int color;
    private int type;
    private boolean isFirstTime = true;
    private boolean isAscending = true;

    CatalogueFolderAdapter catalogueFolderAdapter;
    ArrayList<CatalogueModule> catalogueFolderList;
    ArrayList<CatalogueModule> catalogueFolderFilterList;

    private ArrayList<FilterForAll> filterCategories;
    private ArrayList<FilterForAll> filterCategories1;
    private ArrayList<FilterForAll> selectedFeedFilter;

    private FragmentManager fragmentManager;
    FilterDialogFragment filterDialog;

    @Override
    protected int getContentView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_catalogue_folder_list;
    }

    @Override
    protected void initView() {

        try {

            if (OustSdkApplication.getContext() == null) {
                OustSdkApplication.setmContext(CatalogueFolderListActivity.this);
            }
            OustSdkTools.setLocale(CatalogueFolderListActivity.this);
//            OustGATools.getInstance().reportPageViewToGoogle(CatalogueFolderListActivity.this, "Oust Catalogue Folder Page");
            toolbar_lay = findViewById(R.id.toolbar_lay);
            toolbar_close_icon = findViewById(R.id.toolbar_close_icon);
            catalogue_name = findViewById(R.id.catalogue_name);
            data_layout = findViewById(R.id.data_layout);
            iv_sort = findViewById(R.id.iv_sort);
            iv_filter = findViewById(R.id.iv_filter);
            layout_switcher = findViewById(R.id.layout_switcher);
            folder_recyclerview = findViewById(R.id.folder_recyclerview);
            info_text = findViewById(R.id.info_text);
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
            setFilterData();

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

                if (catalogueFolderAdapter != null) {
                    catalogueFolderAdapter.getFilter().filter("");
                }
                return false;
            });

            search_view_catalogue.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {


                    if (catalogueFolderAdapter != null) {
                        catalogueFolderAdapter.getFilter().filter(query);
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {


                    if (catalogueFolderAdapter != null) {
                        catalogueFolderAdapter.getFilter().filter(newText);
                    }
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
        try {
            if (!isConnected) {
                //handle network connection
                info_text.setVisibility(View.GONE);
                branding_mani_layout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
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
            if (bundle != null) {
                catalogueFolderList = (ArrayList<CatalogueModule>) bundle.getSerializable("folderList");
            }

            if (catalogueFolderList != null && catalogueFolderList.size() != 0) {

                catalogueFolderAdapter = new CatalogueFolderAdapter();
                //sort();
                setAdapter();
                String folderText;
                if (catalogueFolderList.size() == 1) {
                    folderText = getResources().getString(R.string.folder_text) + " (1)";
                } else {
                    folderText = getResources().getString(R.string.folders_text) + " (" + catalogueFolderList.size() + ")";

                }

                catalogue_name.setText(folderText);

                data_layout.setVisibility(View.VISIBLE);
                info_text.setVisibility(View.GONE);

            } else {
                info_text.setText(getResources().getString(R.string.no_modules_available));
                data_layout.setVisibility(View.GONE);
                info_text.setVisibility(View.VISIBLE);
            }
            branding_mani_layout.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setAdapter() {
        try {
            ArrayList<CatalogueModule> catalogueFolderSetList = catalogueFolderList;

            if (catalogueFolderFilterList != null) {
                catalogueFolderSetList = catalogueFolderFilterList;
            }

            try {
                if (!isFirstTime) {
                    if (!isAscending) {
                        OustResourceUtils.setDefaultDrawableColor(iv_sort.getDrawable(), getResources().getColor(R.color.unselected_text));
                        if (catalogueFolderSetList != null && catalogueFolderSetList.size() != 0) {
                            Collections.sort(catalogueFolderSetList, CatalogueModule.catalogueDescending);
                        }


                    } else {
                        OustResourceUtils.setDefaultDrawableColor(iv_sort);
                        if (catalogueFolderSetList != null && catalogueFolderSetList.size() != 0) {
                            Collections.sort(catalogueFolderSetList, CatalogueModule.catalogueAscending);
                        }


                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

            setLayoutManager();
            setFolderAdapters(catalogueFolderSetList);

            if ((catalogueFolderSetList == null || catalogueFolderSetList.size() == 0)) {
                info_text.setText(getResources().getString(R.string.no_modules_available));
                info_text.setVisibility(View.VISIBLE);
                branding_mani_layout.setVisibility(View.GONE);
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
                mLayoutManager = new LinearLayoutManager(CatalogueFolderListActivity.this, LinearLayoutManager.VERTICAL, false);
                break;
            case LayoutType.GRID:
                mLayoutManager = new GridLayoutManager(CatalogueFolderListActivity.this, 2, LinearLayoutManager.VERTICAL, false);
                break;
        }
    }

    private void setFolderAdapters(ArrayList<CatalogueModule> catalogueFolderList) {

        if (catalogueFolderAdapter == null)
            catalogueFolderAdapter = new CatalogueFolderAdapter();
        if (mLayoutManager == null)
            mLayoutManager = new LinearLayoutManager(CatalogueFolderListActivity.this);
        int screenWidth = 0;
      /*  if(type==2){
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            screenWidth = displayMetrics.widthPixels;
        }*/

        folder_recyclerview.setLayoutManager(mLayoutManager);
        folder_recyclerview.setItemAnimator(new DefaultItemAnimator());
        folder_recyclerview.removeAllViews();
        catalogueFolderAdapter.setCatalogueFolderAdapter(catalogueFolderList, CatalogueFolderListActivity.this, screenWidth, type);
        folder_recyclerview.setAdapter(catalogueFolderAdapter);
        catalogueFolderAdapter.notifyDataSetChanged();
        if (searchEditText != null && searchEditText.getText().toString() != null) {
            catalogueFolderAdapter.getFilter().filter(searchEditText.getText().toString());
        }
        folder_recyclerview.setVisibility(View.VISIBLE);

    }


    public void setFilter(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    private void setFilterData() {
        if (filterCategories == null) {
            filterCategories = new ArrayList<>();
        }
        if (filterCategories1 == null) {
            filterCategories1 = new ArrayList<>();
            filterCategories1.add(new FilterForAll("All", 5));
            filterCategories1.add(new FilterForAll("Viewed", 9));
            filterCategories1.add(new FilterForAll("Not Viewed", 10));
        }

    }

    private void openFilterDialogue() {

        try {
            filterDialog = FilterDialogFragment.newInstance(filterCategories, filterCategories1, selectedFeedFilter, selectedFilter -> {
                selectedFeedFilter = selectedFilter;
                if (selectedFilter == null || selectedFilter.size() == 0) {
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

            if (filter != null && filter.size() != 0) {

                Collections.sort(filter);


                for (int filterForAll : filter) {

                    if (catalogueFolderList != null && catalogueFolderList.size() != 0) {
                        ArrayList<CatalogueModule> copyList = catalogueFolderList;
                        for (int i = 0; i < copyList.size(); i++) {

                            CatalogueModule catalogueModule = copyList.get(i);

                            if (filterForAll == 0 || filterForAll == 4) {
                                if (filterFolderHashMap.size() != 0) {
                                    if (filterFolderHashMap.get("Folder") == null) {
                                        filterFolderHashMap.put("Folder", new HashMap<>());
                                    }
                                } else {
                                    filterFolderHashMap.put("Folder", new HashMap<>());
                                }
                                Objects.requireNonNull(filterFolderHashMap.get("Folder")).put("Folder" + catalogueModule.getContentId(), catalogueModule);

                            }

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

                            }

                            if (filterForAll == 10) {
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


                //sort();
                setAdapter();
            } else {

                catalogueFolderFilterList = null;

                //sort();
                setAdapter();
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }
}
