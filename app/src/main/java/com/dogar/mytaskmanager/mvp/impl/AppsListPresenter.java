package com.dogar.mytaskmanager.mvp.impl;

import android.content.Context;

import com.dogar.mytaskmanager.mvp.base.BasePresenter;

public class AppsListPresenter implements BasePresenter {
    private final View mView;

    public AppsListPresenter(View mView) {
        this.mView = mView;
    }

    @Override
    public void onStart(Context context) {

    }

    @Override
    public void onStop() {

    }
}
