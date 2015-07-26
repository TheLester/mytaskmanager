package com.dogar.mytaskmanager.activity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dogar.mytaskmanager.ProcInfoActivity;
import com.dogar.mytaskmanager.R;
import com.dogar.mytaskmanager.adapters.TasksAdapter;
import com.dogar.mytaskmanager.model.TaskInfo;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;


public class TasksListActivity extends AppCompatActivity {

	@Bind(R.id.process_list) RecyclerView    processList;
	private                  ActivityManager activityManager;
	private                  PackageManager  packageManager;

	private List<TaskInfo> runningTasks = new ArrayList<>();
	private TasksAdapter myTasksAdapter;
	final static String INFO_MESSAGE = "com.example.mytaskmanager.MESSAGE";
	final static String INFO_PACKAGE = "com.example.mytaskmanager.PACKAGE";


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.process_list);
		ButterKnife.bind(this);


		obtainRunningTasksInfo();
		processList.setLayoutManager(new LinearLayoutManager(this));
		processList.setAdapter(new TasksAdapter(this, runningTasks));


		updateAvailableMemory();
	}

	private void obtainRunningTasksInfo() {
		activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		packageManager = getPackageManager();
		Intent applicationIntent = new Intent(Intent.ACTION_MAIN);
		applicationIntent.addCategory(Intent.CATEGORY_LAUNCHER);

		List<ResolveInfo> launcherResolves = packageManager.queryIntentActivities(applicationIntent, 0);

		List<String> installedAppsPackages = new ArrayList<>();

		for (int i = 0; i < launcherResolves.size(); i++) {
			ComponentInfo info = launcherResolves.get(i).activityInfo;
			installedAppsPackages.add(info.packageName);
			//	Timber.i(info.packageName + "/" + info.name);
		}

		List<RunningAppProcessInfo> runningAppInfo = activityManager.getRunningAppProcesses();
		for (int i = 0; i < runningAppInfo.size(); i++) {
			if (installedAppsPackages.contains(runningAppInfo.get(i).processName)) {
				TaskInfo taskInfo = new TaskInfo();
				taskInfo.setTaskName(runningAppInfo.get(i).processName);
				runningTasks.add(taskInfo);
//				taskInfo.setIconURI(runningAppProcessInfo.);
			}
		}
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

}
