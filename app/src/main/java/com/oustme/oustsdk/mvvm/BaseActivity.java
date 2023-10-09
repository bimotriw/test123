package com.oustme.oustsdk.mvvm;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity<VM extends BaseViewModel> extends AppCompatActivity {

    protected VM viewModel;

    @NonNull
    protected abstract VM createViewModel(Bundle bundle);

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        viewModel = createViewModel(bundle);
    }
}
