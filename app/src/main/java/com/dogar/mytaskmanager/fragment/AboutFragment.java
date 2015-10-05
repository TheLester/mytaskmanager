package com.dogar.mytaskmanager.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.dogar.mytaskmanager.R;

public class AboutFragment extends BaseFragment {
    public static Fragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_about;
    }
}
