package com.dogar.mytaskmanager.di.module;

import android.content.Context;

import com.dogar.mytaskmanager.adapters.TasksAdapter;
import com.dogar.mytaskmanager.mvp.AppListPresenter;
import com.dogar.mytaskmanager.mvp.impl.AppsListPresenterImpl;

import dagger.Module;
import dagger.Provides;

@Module
public class ListAppModule {
	private AppListPresenter.View appListView;
	private Context context;
	public ListAppModule(AppListPresenter.View appListView,Context context) {
		this.appListView = appListView;
		this.context = context;
	}

	@Provides
	public TasksAdapter provideTasksAdapter() {
		return new TasksAdapter(context);
	}

	@Provides
	public AppsListPresenterImpl provideAppListPresenter() {
		return new AppsListPresenterImpl(appListView);
	}

}
