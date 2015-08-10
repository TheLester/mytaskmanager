package com.dogar.mytaskmanager.mvp.impl;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.dogar.mytaskmanager.App;
import com.dogar.mytaskmanager.di.module.ListAppModule;
import com.dogar.mytaskmanager.model.AppInfo;
import com.dogar.mytaskmanager.mvp.AppListPresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class AppsListPresenterImpl implements AppListPresenter {
	private final View mView;
	private List<String>  installedAppsPackages = new ArrayList<>();
	private List<AppInfo> appInfos              = new ArrayList<>();

	@Inject PackageManager  packageManager;
	@Inject ActivityManager activityManager;

	public AppsListPresenterImpl(View mView) {
		this.mView = mView;
		initInstalledApps();
		App.getInstance().component().inject(this);
	}



	@Override
	public void loadAppList() {
		getNewRunningAppsObservable().subscribe(new AppsObserver());
	}

	@Override
	public void reloadAppList() {
		appInfos.clear();
		getNewRunningAppsObservable().subscribe(new AppsObserver());
	}

	@Override
	public void loadAppMoreInfo(App app) {

	}

	@Override
	public void onStart(Context context) {

	}

	@Override
	public void onStop() {

	}

	private Observable<AppInfo> getNewRunningAppsObservable() {
		return Observable.defer(new Func0<Observable<AppInfo>>() {
			@Override
			public Observable<AppInfo> call() {
				return getRunningAppsObservable();
			}

			@NonNull
			private Observable<AppInfo> getRunningAppsObservable() {
				return Observable.from(activityManager.getRunningAppProcesses()).
						filter(new Func1<ActivityManager.RunningAppProcessInfo, Boolean>() {
							@Override
							public Boolean call(ActivityManager.RunningAppProcessInfo runningAppProcessInfo) {
								return installedAppsPackages.contains(runningAppProcessInfo.processName);
							}
						})
						.map(new Func1<ActivityManager.RunningAppProcessInfo, AppInfo>() {
							@Override
							public AppInfo call(ActivityManager.RunningAppProcessInfo runningAppProcessInfo) {
								ApplicationInfo androidAppInfo = null;
								AppInfo appInfo = new AppInfo();
								try {
									androidAppInfo = packageManager.getApplicationInfo(runningAppProcessInfo.processName, PackageManager.GET_META_DATA);
									appInfo.setPid(runningAppProcessInfo.pid);
								} catch (PackageManager.NameNotFoundException e) {
									e.printStackTrace();
								}

								if (androidAppInfo != null) {
									appInfo.setTaskName(packageManager.getApplicationLabel(androidAppInfo).toString());
									appInfo.setIcon(getIconUri(androidAppInfo));
								}
								return appInfo;
							}
						});
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	private class AppsObserver implements Observer<AppInfo> {

		@Override
		public void onCompleted() {
			mView.onAppListLoaded(appInfos);
		}

		@Override
		public void onError(Throwable e) {
			Timber.e(e, "Error!");
		}

		@Override
		public void onNext(AppInfo appInfo) {
			appInfos.add(appInfo);
		}
	}

	private Uri getIconUri(ApplicationInfo applicationInfo) {
		Uri iconUri = null;
		if (applicationInfo.icon != 0) {
			iconUri = Uri.parse(new StringBuffer().append("android.resource://").append(applicationInfo.packageName).
					append("/").append(applicationInfo.icon).toString());
		}
		return iconUri;
	}
	private void initInstalledApps() {
		Intent applicationIntent = new Intent(Intent.ACTION_MAIN);
		applicationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> launcherResolves = packageManager.queryIntentActivities(applicationIntent, 0);
		for (int i = 0; i < launcherResolves.size(); i++) {
			ComponentInfo info = launcherResolves.get(i).activityInfo;
			installedAppsPackages.add(info.packageName);
			Timber.i(info.packageName + "/" + info.name);
		}
	}
}
