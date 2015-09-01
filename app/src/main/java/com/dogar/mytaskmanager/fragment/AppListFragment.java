package com.dogar.mytaskmanager.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dogar.mytaskmanager.R;
import com.dogar.mytaskmanager.adapters.TasksAdapter;
import com.dogar.mytaskmanager.di.component.DaggerAppListComponent;
import com.dogar.mytaskmanager.di.module.ListAppModule;
import com.dogar.mytaskmanager.model.AppInfo;
import com.dogar.mytaskmanager.mvp.AppListPresenter;
import com.dogar.mytaskmanager.mvp.impl.AppsListPresenterImpl;
import com.tuesda.walker.circlerefresh.CircleRefreshLayout;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import timber.log.Timber;

public class AppListFragment extends BaseFragment implements AppListPresenter.View, CircleRefreshLayout.OnCircleRefreshListener {

	@Bind(R.id.process_list)   RecyclerView        processList;
	@Bind(R.id.refresh_layout) CircleRefreshLayout refreshLayout;


	@Inject TasksAdapter          myTasksAdapter;
	@Inject AppsListPresenterImpl appsListPresenter;

	private boolean isRefreshing;

	public static Fragment newInstance() {
		return new AppListFragment();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		refreshLayout.setOnRefreshListener(this);

		DaggerAppListComponent.builder()
				.listAppModule(new ListAppModule(this, getActivity()))
				.build()
				.inject(this);

		processList.setLayoutManager(new LinearLayoutManager(mActivity));
		processList.setAdapter(myTasksAdapter);
		appsListPresenter.loadAppList();
	}

	protected int getLayoutResourceId() {
		return R.layout.fragment_apps_list;
	}


	@Override
	public void showProgress(boolean show) {

	}


	private void finishRefreshingWithDelay() {
		refreshLayout.postDelayed(new Runnable() {
			@Override
			public void run() {
				refreshLayout.finishRefreshing();
			}
		}, 2000);
	}


	@Override
	public void completeRefresh() {
		isRefreshing = false;
		myTasksAdapter.notifyDataSetChanged();
		Timber.i("Complete refresh!");
	}

	@Override
	public void refreshing() {
		Timber.i("Refreshing ...");
		isRefreshing = true;
		appsListPresenter.reloadAppList();

	}

	@Override
	public void onAppListLoaded(List<AppInfo> runningApps) {
		if (!runningApps.isEmpty() && isRefreshing) {
			finishRefreshingWithDelay();
		} else {
			myTasksAdapter.setTasks(runningApps);
			myTasksAdapter.notifyDataSetChanged();
		}
		Timber.i("Done!");
	}
}
