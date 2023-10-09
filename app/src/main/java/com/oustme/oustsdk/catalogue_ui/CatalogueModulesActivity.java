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
import com.oustme.oustsdk.catalogue_ui.adapter.CatalogueModuleAdapter;
import com.oustme.oustsdk.catalogue_ui.model.CatalogueModule;
import com.oustme.oustsdk.catalogue_ui.model.CatalogueModuleUpdate;
import com.oustme.oustsdk.customviews.LayoutSwitcher;
import com.oustme.oustsdk.filter.FilterDialogFragment;
import com.oustme.oustsdk.filter.FilterForAll;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
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

public class CatalogueModulesActivity extends BaseActivity {

    Toolbar toolbar_lay;
    ImageView toolbar_close_icon, iv_sort, iv_filter;
    TextView catalogue_name, info_text;
    LinearLayout data_layout;
    RecyclerView modules_recyclerview;
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

    CatalogueModuleAdapter catalogueModuleAdapter;
    ActiveUser activeUser;
    ArrayList<CatalogueModule> catalogueModuleArrayList;
    ArrayList<CatalogueModule> catalogueModuleFilterList;
    String catalogueCategoryName;

    private FragmentManager fragmentManager;
    FilterDialogFragment filterDialog;

    private ArrayList<FilterForAll> filterCategories;
    private ArrayList<FilterForAll> filterCategories1;
    private ArrayList<FilterForAll> selectedFeedFilter;

    boolean isSkillEnabled;


    @Override
    protected int getContentView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_catalogue_modules;
    }

    @Override
    protected void initView() {

        try {

            if (OustSdkApplication.getContext() == null) {
                OustSdkApplication.setmContext(CatalogueModulesActivity.this);
            }
            OustSdkTools.setLocale(CatalogueModulesActivity.this);
//            OustGATools.getInstance().reportPageViewToGoogle(CatalogueModulesActivity.this, "Oust Catalogue Modules List Page");
            toolbar_lay = findViewById(R.id.toolbar_lay);
            toolbar_close_icon = findViewById(R.id.toolbar_close_icon);
            catalogue_name = findViewById(R.id.catalogue_name);
            data_layout = findViewById(R.id.data_layout);
            iv_sort = findViewById(R.id.iv_sort);
            iv_filter = findViewById(R.id.iv_filter);
            layout_switcher = findViewById(R.id.layout_switcher);
            search_view_catalogue = findViewById(R.id.search_view_catalogue);
            modules_recyclerview = findViewById(R.id.modules_recyclerview);
            info_text = findViewById(R.id.info_text);

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
            activeUser = OustAppState.getInstance().getActiveUser();
            fetchLayoutData();
            setFilterData();

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
                // sort();
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
                if (catalogueModuleAdapter != null) {
                    catalogueModuleAdapter.getFilter().filter("");
                }


                return false;
            });

            search_view_catalogue.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if (catalogueModuleAdapter != null) {
                        catalogueModuleAdapter.getFilter().filter(query);
                    }


                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (catalogueModuleAdapter != null) {
                        catalogueModuleAdapter.getFilter().filter(newText);
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

        if (!isConnected) {
            //handle network connection
            info_text.setVisibility(View.GONE);
            branding_mani_layout.setVisibility(View.GONE);
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
                catalogueModuleArrayList = (ArrayList<CatalogueModule>) bundle.getSerializable("moduleList");
                catalogueCategoryName = bundle.getString("categoryName");
                isSkillEnabled = bundle.getBoolean("isSkillEnabled");
            }

            if (catalogueModuleArrayList != null && catalogueModuleArrayList.size() != 0) {

                catalogueModuleAdapter = new CatalogueModuleAdapter();
                //sort();
                setAdapter();
                String moduleText;
                if (catalogueModuleArrayList.size() == 1) {
                    moduleText = getResources().getString(R.string.module) + " (1)";
                } else {
                    moduleText = getResources().getString(R.string.modules_text) + " (" + catalogueModuleArrayList.size() + ")";
                }
                catalogue_name.setText(moduleText);

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

            ArrayList<CatalogueModule> catalogueModuleSetList = catalogueModuleArrayList;
            if (catalogueModuleFilterList != null) {
                catalogueModuleSetList = catalogueModuleFilterList;
            }

            if (catalogueModuleFilterList != null) {
                catalogueModuleSetList = catalogueModuleFilterList;
            }
            try {

                if (!isFirstTime) {
                    if (!isAscending) {
                        OustResourceUtils.setDefaultDrawableColor(iv_sort.getDrawable(), getResources().getColor(R.color.unselected_text));


                        if (catalogueModuleSetList != null && catalogueModuleSetList.size() != 0) {
                            Collections.sort(catalogueModuleSetList, CatalogueModule.catalogueDescending);
                        }


                    } else {
                        OustResourceUtils.setDefaultDrawableColor(iv_sort);

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
            setModulesAdapters(catalogueModuleSetList);

            if ((catalogueModuleSetList == null || catalogueModuleSetList.size() == 0)) {
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
                mLayoutManager = new LinearLayoutManager(CatalogueModulesActivity.this, LinearLayoutManager.VERTICAL, false);
                break;
            case LayoutType.GRID:
                mLayoutManager = new GridLayoutManager(CatalogueModulesActivity.this, 2, LinearLayoutManager.VERTICAL, false);
                break;
        }
    }

    private void setModulesAdapters(ArrayList<CatalogueModule> catalogueModuleArrayList) {

        if (catalogueModuleAdapter == null)
            catalogueModuleAdapter = new CatalogueModuleAdapter();
        if (mLayoutManager == null)
            mLayoutManager = new LinearLayoutManager(CatalogueModulesActivity.this);
        int screenWidth = 0;
      /*  if(type==2){
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            screenWidth = displayMetrics.widthPixels;
        }*/

        modules_recyclerview.setLayoutManager(mLayoutManager);
        modules_recyclerview.setItemAnimator(new DefaultItemAnimator());
        modules_recyclerview.removeAllViews();
        catalogueModuleAdapter.setCatalogueModuleArrayList(catalogueModuleArrayList, CatalogueModulesActivity.this, type);
        if (activeUser != null) {
            catalogueModuleAdapter.setActiveUser(activeUser);
        }
        catalogueModuleAdapter.setCatalogueCategoryName(catalogueCategoryName);
        modules_recyclerview.setAdapter(catalogueModuleAdapter);
        catalogueModuleAdapter.notifyDataSetChanged();
        if (searchEditText != null && searchEditText.getText().toString() != null) {
            catalogueModuleAdapter.getFilter().filter(searchEditText.getText().toString());
        }
        modules_recyclerview.setVisibility(View.VISIBLE);

    }

    private void sort() {

        if (!isAscending) {
            OustResourceUtils.setDefaultDrawableColor(iv_sort.getDrawable(), getResources().getColor(R.color.unselected_text));

            Collections.sort(catalogueModuleArrayList, CatalogueModule.catalogueDescending);

        } else {
            OustResourceUtils.setDefaultDrawableColor(iv_sort);
            Collections.sort(catalogueModuleArrayList, CatalogueModule.catalogueAscending);

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
           /* if(isSkillEnabled){
                filterCategories.add(new FilterForAll("Skills", 3));
            }*/
        }
        if (filterCategories1 == null) {
            filterCategories1 = new ArrayList<>();
            filterCategories1.add(new FilterForAll(getResources().getString(R.string.all) + "", 5));
            filterCategories1.add(new FilterForAll("Not Started", 6));
            filterCategories1.add(new FilterForAll("In Progress", 7));
            filterCategories1.add(new FilterForAll("Completed", 8));
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

            HashMap<String, HashMap<String, CatalogueModule>> filterModuleHashMap = new HashMap<>();
            boolean statusFilter = false;
            boolean courseFilter = false;
            boolean assessmentFilter = false;
            boolean skillFilter = false;

            if (filter != null && filter.size() != 0) {

                Collections.sort(filter);

                for (int filterForAll : filter) {
                    if (filterForAll == 6 || filterForAll == 7 || filterForAll == 8 || filterForAll == 9 || filterForAll == 10) {
                        statusFilter = true;

                    }

                    if (filterForAll == 1) {
                        courseFilter = true;
                    }


                    if (filterForAll == 2) {
                        assessmentFilter = true;
                    }
                }

                for (int filterForAll : filter) {


                    if (catalogueModuleArrayList != null && catalogueModuleArrayList.size() != 0) {
                        ArrayList<CatalogueModule> copyList = catalogueModuleArrayList;

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

                catalogueModuleFilterList = null;
                //sort();
                setAdapter();
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
}
