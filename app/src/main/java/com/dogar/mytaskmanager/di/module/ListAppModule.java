package com.dogar.mytaskmanager.di.module;

import android.content.Context;

import com.dogar.mytaskmanager.adapters.TasksAdapter;
import com.dogar.mytaskmanager.model.AppInfo;
import com.dogar.mytaskmanager.mvp.AppListPresenter;
import com.dogar.mytaskmanager.mvp.impl.AppsListPresenterImpl;

import java.util.List;

import dagger.Module;
import dagger.Provides;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;

@Module
public class ListAppModule {
	private AppListPresenter.View appListView;
	private Context               context;
	private List<AppInfo>         appInfoList;


	public ListAppModule(AppListPresenter.View appListView, Context context,List<AppInfo> appInfoList) {
		this.appListView = appListView;
		this.context = context;
		this.appInfoList = appInfoList;
	}

	@Provides
	public TasksAdapter provideTasksAdapter() {
		TasksAdapter mainAdapter = new TasksAdapter(context,appInfoList);
		return mainAdapter;
	}

	@Provides
	public FadeInAnimator provideFadeInAnimator() {
		return new FadeInAnimator();
	}

	@Provides
	public AlphaInAnimationAdapter provideAlphaInAnimationAdapter(TasksAdapter tasksAdapter) {
		AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(tasksAdapter);
		return alphaAdapter;
	}

	@Provides
	public ScaleInAnimationAdapter provideScaleInAnimationAdapter(AlphaInAnimationAdapter alphaInAnimationAdapter) {
		ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(alphaInAnimationAdapter);
		return  scaleAdapter;
	}
	@Provides
	public AppsListPresenterImpl provideAppListPresenter() {
		return new AppsListPresenterImpl(appListView);
	}
}
