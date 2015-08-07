package com.dogar.mytaskmanager.activity;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;

import com.dogar.mytaskmanager.App;
import com.dogar.mytaskmanager.R;
import com.dogar.mytaskmanager.adapters.TasksAdapter;
import com.dogar.mytaskmanager.model.AppInfo;
import com.tuesda.walker.circlerefresh.CircleRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;


public class TasksListActivity extends BaseActivity implements CircleRefreshLayout.OnCircleRefreshListener {

	@Bind(R.id.process_list)   RecyclerView        processList;
	@Bind(R.id.refresh_layout) CircleRefreshLayout refreshLayout;

	@Inject PackageManager  packageManager;
	@Inject ActivityManager activityManager;
	@Inject TasksAdapter    myTasksAdapter;

	private List<String>  installedAppsPackages = new ArrayList<>();
	private List<AppInfo> appInfos              = new ArrayList<>();

	private boolean isRefreshing;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_apps_list);
		ButterKnife.bind(this);
		refreshLayout.setOnRefreshListener(this);
		App.getInstance().component().inject(this);
		initInstalledApps();

		myTasksAdapter.setTasks(appInfos);
		processList.setLayoutManager(new LinearLayoutManager(this));
		processList.setAdapter(myTasksAdapter);

		getRunningApps().subscribe(new AppsObserver());

		updateAvailableMemory();
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

	private Observable<AppInfo> getRunningApps() {
		return Observable.defer(new Func0<Observable<AppInfo>>() {
			@Override
			public Observable<AppInfo> call() {
				return getRunningAppsObservable();
			}

			@NonNull
			private Observable<AppInfo> getRunningAppsObservable() {
				return Observable.from(activityManager.getRunningAppProcesses()).
						filter(new Func1<RunningAppProcessInfo, Boolean>() {
							@Override
							public Boolean call(RunningAppProcessInfo runningAppProcessInfo) {
								return installedAppsPackages.contains(runningAppProcessInfo.processName);
							}
						})
						.map(new Func1<RunningAppProcessInfo, AppInfo>() {
							@Override
							public AppInfo call(RunningAppProcessInfo runningAppProcessInfo) {
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

	private Uri getIconUri(ApplicationInfo applicationInfo) {
		Uri iconUri = null;
		if (applicationInfo.icon != 0) {
			iconUri = Uri.parse(new StringBuffer().append("android.resource://").append(applicationInfo.packageName).
					append("/").append(applicationInfo.icon).toString());
		}
		return iconUri;
	}

	private class AppsObserver implements Observer<AppInfo> {

		@Override
		public void onCompleted() {
			if (!appInfos.isEmpty() && isRefreshing) {
				finishRefreshingWithDelay();
			} else {
				myTasksAdapter.notifyDataSetChanged();
			}

			Timber.i("Done!");
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

	private void finishRefreshingWithDelay() {
		refreshLayout.postDelayed(new Runnable() {
			@Override
			public void run() {
				refreshLayout.finishRefreshing();
			}
		}, 2000);
	}

	private void updateAvailableMemory() {
		MemoryInfo mi = new MemoryInfo();
		activityManager.getMemoryInfo(mi);
		long availableMegs = mi.availMem / 1048576L;
		setTitle("MyTaskManager | " + availableMegs + " MB Available");
	}

	private StringBuffer getProcessInfoMemory(int procID) {
		MemoryInfo mi = new MemoryInfo();
		activityManager.getMemoryInfo(mi);
		Debug.MemoryInfo info = activityManager
				.getProcessMemoryInfo(new int[]{procID})[0];
		StringBuffer procMemoryInfo = new StringBuffer();

		procMemoryInfo.append("-------------------------------------\n");
		procMemoryInfo.append("Process memory information(in kB):\n");
		procMemoryInfo.append("Private dirty pages:");
		procMemoryInfo.append(info.getTotalPrivateDirty());
		procMemoryInfo.append("\n");
		procMemoryInfo.append("Proportional set size:");
		procMemoryInfo.append(info.getTotalPss());
		procMemoryInfo.append("\n");
		procMemoryInfo.append("Shared dirty pages:");
		procMemoryInfo.append(info.getTotalSharedDirty());
		procMemoryInfo.append("\n");
		return procMemoryInfo;

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
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
		appInfos.clear();
		getRunningApps().subscribe(new AppsObserver());

	}
}
