package com.dogar.mytaskmanager.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dogar.mytaskmanager.TaskManagerApp;
import com.dogar.mytaskmanager.activity.BaseActivity;
import com.squareup.leakcanary.RefWatcher;

import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {
	protected BaseActivity mActivity;
	protected View         mRootView;

	protected abstract int getLayoutResourceId();

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (BaseActivity) activity;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(getLayoutResourceId(), container, false);
		injectViews(mRootView);
		return mRootView;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		RefWatcher refWatcher = TaskManagerApp.getRefWatcher(getActivity());
		refWatcher.watch(this);
	}
	protected ActionBar getToolbar(){
		return ((BaseActivity) getActivity()).getSupportActionBar();
	}
	private void injectViews(final View view) {
		ButterKnife.bind(this, view);
	}

}