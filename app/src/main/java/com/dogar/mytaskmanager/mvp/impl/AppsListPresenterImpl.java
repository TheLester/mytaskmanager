package com.dogar.mytaskmanager.mvp.impl;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Debug;
import android.support.annotation.NonNull;

import com.dogar.mytaskmanager.App;
import com.dogar.mytaskmanager.eventbus.EventHolder;
import com.dogar.mytaskmanager.model.AppInfo;
import com.dogar.mytaskmanager.mvp.AppListPresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
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
	@Inject Context         context;


	public AppsListPresenterImpl(View mView) {
		this.mView = mView;
		App.getInstance().component().inject(this);
		initInstalledApps();

	}

	@Override
	public void onStart() {
		EventBus.getDefault().register(this);
	}

	@Override
	public void onStop() {
		EventBus.getDefault().unregister(this);
	}

	public void onEvent(EventHolder.MoreAppInfoRequestedEvent event) {
		mView.onLoadAppMoreInfo(event.appInfo, event.imageView);

	}

	@Override
	public void loadAppList() {
		loadAppsInfos();
	}

	@Override
	public void reloadAppList() {
		appInfos.clear();
		loadAppsInfos();
	}

	private void loadAppsInfos() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
			getNewRunningServicesObservable().subscribe(new AppsObserver());
		} else {
			getNewRunningAppsObservable().subscribe(new AppsObserver());
		}
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
								return buildAppInfo(runningAppProcessInfo.processName, runningAppProcessInfo.pid);
							}
						});
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	private Observable<AppInfo> getNewRunningServicesObservable() {
		return Observable.defer(new Func0<Observable<AppInfo>>() {
			@Override
			public Observable<AppInfo> call() {
				return getRunningServicesObservable();
			}

			@NonNull
			private Observable<AppInfo> getRunningServicesObservable() {
				return Observable.from(activityManager.getRunningServices(Integer.MAX_VALUE)).
						filter(new Func1<ActivityManager.RunningServiceInfo, Boolean>() {
							@Override
							public Boolean call(ActivityManager.RunningServiceInfo runningServiceProcessInfo) {
								return installedAppsPackages.contains(runningServiceProcessInfo.service.getPackageName());
							}
						})
						.distinct(new Func1<ActivityManager.RunningServiceInfo, Object>() {
							@Override
							public Object call(ActivityManager.RunningServiceInfo runningServiceInfo) {
								return null;
							}
						})
						.map(new Func1<ActivityManager.RunningServiceInfo, AppInfo>() {
							@Override
							public AppInfo call(ActivityManager.RunningServiceInfo runningServiceProcessInfo) {
								return buildAppInfo(runningServiceProcessInfo.service.getPackageName(), runningServiceProcessInfo.pid);
							}
						});
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@NonNull
	private AppInfo buildAppInfo(String packageName, int pid) {
		ApplicationInfo androidAppInfo = null;
		PackageInfo packageInfo = null;


		AppInfo appInfo = new AppInfo();
		appInfo.setPackageName(packageName);
		try {
			androidAppInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
			appInfo.setPid(pid);
			packageInfo = packageManager.getPackageInfo(packageName, 0);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
			Timber.e(e, "fail to retrieve info");
		}

		if (androidAppInfo != null) {
			appInfo.setTaskName(packageManager.getApplicationLabel(androidAppInfo).toString());
			appInfo.setIcon(getIconUri(androidAppInfo));
			appInfo.setCurrentApp(context.getPackageName().equals(packageName));
		}

		Debug.MemoryInfo memoryInfo = activityManager.getProcessMemoryInfo(new int[]{pid})[0];//todo add more app pids
		if (memoryInfo != null) {
			appInfo.setMemoryInKb(memoryInfo.getTotalPss());
		}
		if (packageInfo != null) {
			appInfo.setFirstInstallTimestamp(packageInfo.firstInstallTime);
			appInfo.setLastUpdateTimestamp(packageInfo.lastUpdateTime);
			appInfo.setVersion(packageInfo.versionName);
		}
		return appInfo;
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
		}
	}
}
