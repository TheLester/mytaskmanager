package com.dogar.mytaskmanager.di.module;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;

import com.dogar.mytaskmanager.App;
import com.dogar.mytaskmanager.adapters.TasksAdapter;
import com.dogar.mytaskmanager.mvp.AppListPresenter;
import com.dogar.mytaskmanager.mvp.impl.AppsListPresenterImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Singleton
@Module
public class ListAppModule {
	private AppListPresenter.View appListView;

	public ListAppModule(AppListPresenter.View appListView) {
		this.appListView = appListView;
	}

	@Provides
	public TasksAdapter provideTasksAdapter(Context context) {
		return new TasksAdapter(context);
	}


	@Provides
	public AppListPresenter provideAppListPresenter() {
		return new AppsListPresenterImpl(appListView);
	}

}
