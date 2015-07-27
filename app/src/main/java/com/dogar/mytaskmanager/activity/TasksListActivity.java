package com.dogar.mytaskmanager.activity;

import java.util.ArrayList;
import java.util.List;

import com.dogar.mytaskmanager.R;
import com.dogar.mytaskmanager.adapters.TasksAdapter;
import com.dogar.mytaskmanager.model.AppInfo;

import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Debug;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.app.AppObservable;
import timber.log.Timber;


public class TasksListActivity extends AppCompatActivity {

	@Bind(R.id.process_list) RecyclerView    processList;
	private                  ActivityManager activityManager;
	private                  PackageManager  packageManager;

	private List<AppInfo> appInfos = new ArrayList<>();
	private TasksAdapter myTasksAdapter;

	final static String INFO_MESSAGE = "com.example.mytaskmanager.MESSAGE";
	final static String INFO_PACKAGE = "com.example.mytaskmanager.PACKAGE";


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.process_list);
		ButterKnife.bind(this);

		myTasksAdapter = new TasksAdapter(this, appInfos);
		processList.setLayoutManager(new LinearLayoutManager(this));
		processList.setAdapter(myTasksAdapter);

		getRunningApps().subscribe(observer);

		updateAvailableMemory();
	}

	private Observable<AppInfo> getRunningApps() {
		return Observable
				.create(new Observable.OnSubscribe<AppInfo>() {
					@Override
					public void call(Subscriber<? super AppInfo> subscriber) {
						activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
						packageManager = getPackageManager();
						Intent applicationIntent = new Intent(Intent.ACTION_MAIN);
						applicationIntent.addCategory(Intent.CATEGORY_LAUNCHER);

						List<ResolveInfo> launcherResolves = packageManager.queryIntentActivities(applicationIntent, 0);

						List<String> installedAppsPackages = new ArrayList<>();

						for (int i = 0; i < launcherResolves.size(); i++) {
							ComponentInfo info = launcherResolves.get(i).activityInfo;
							installedAppsPackages.add(info.packageName);
							Timber.i(info.packageName + "/" + info.name);
						}

						List<RunningAppProcessInfo> runningAppInfo = activityManager.getRunningAppProcesses();
						for (int i = 0; i < runningAppInfo.size(); i++) {
							if (installedAppsPackages.contains(runningAppInfo.get(i).processName)) {
								if (subscriber.isUnsubscribed()) {
									return;
								}
								AppInfo appInfo = new AppInfo();
								appInfo.setTaskName(runningAppInfo.get(i).processName);
								subscriber.onNext(appInfo);
							}
						}
						if (!subscriber.isUnsubscribed()) {
							subscriber.onCompleted();
						}
					}
				});
	}

	private Observer<AppInfo> observer = new Observer<AppInfo>() {

		@Override
		public void onCompleted() {
			myTasksAdapter.notifyDataSetChanged();
			Timber.i("Done!");
		}

		@Override
		public void onError(Throwable e) {
			Timber.e(e,"Error!");
		}

		@Override
		public void onNext(AppInfo appInfo) {
			appInfos.add(appInfo);
		}
	};

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

}
