package com.example.sitting_devk.viewModel;

import android.view.View;

import androidx.databinding.BaseObservable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

public abstract class BaseViewModel<B extends ViewDataBinding> extends BaseObservable {
    protected View view;
    protected B binding;

    public void attachView(View view) {
        this.view = view;
        binding = DataBindingUtil.getBinding(view);
    }

    public void detachView() {
        view = null;
        binding = null;
    }
}