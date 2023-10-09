package com.oustme.oustsdk.layoutFour.components.newCatalogue;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.oustme.oustsdk.catalogue_ui.model.CatalogueComponentModule;

public class CatalogViewModel extends ViewModel {

    private MutableLiveData<CatalogueComponentModule> catalogueMutableLiveData;
    private CatalogRepository mRepo;

    public void init() {
        if (catalogueMutableLiveData != null) {
            return;
        }
        mRepo = CatalogRepository.getInstance();
    }

    public MutableLiveData<CatalogueComponentModule> getCatalogs() {
        catalogueMutableLiveData = mRepo.getCatalogs();
        return catalogueMutableLiveData;
    }



}
