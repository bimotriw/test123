package com.oustme.oustsdk.catalogue_ui;

import android.os.Bundle;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.oustme.oustsdk.catalogue_ui.model.CatalogueComponentModule;

public class CatalogueViewModel extends ViewModel {

    private MutableLiveData<CatalogueComponentModule> catalogueMutableLiveData;
    private CatalogueRepository catalogueRepository;
    Bundle bundleData;

    public void init(Bundle bundle) {

        if (catalogueMutableLiveData != null) {
            return;
        }

        bundleData = bundle;
        catalogueRepository = CatalogueRepository.getInstance();
        catalogueMutableLiveData = catalogueRepository.getLiveData(bundleData);
    }

    public MutableLiveData<CatalogueComponentModule> getBaseComponentModuleMutableLiveData() {
        return catalogueMutableLiveData;
    }
}
