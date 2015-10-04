package com.dogar.mytaskmanager.activity;

import android.app.ActivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

import com.dogar.mytaskmanager.R;
import com.dogar.mytaskmanager.TaskManagerApp;
import com.dogar.mytaskmanager.fragment.AppListFragment;
import com.dogar.mytaskmanager.utils.MemoryUtil;

import javax.inject.Inject;

public class MainActivity extends BaseActivity {
	public static final int PERIOD_RAM_INFO_UPDATE = 2000;

	@Inject ActivityManager activityManager;
	@Inject Handler         handler;

	Runnable updateTask = new Runnable() {
		@Override
		public void run() {
			ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
			activityManager.getMemoryInfo(memoryInfo);
			long availableKbs = memoryInfo.availMem / 1048L;
			final String ramUsage = MemoryUtil.formatMemSize(MainActivity.this, availableKbs);
			if (ramUsageLabel != null) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						ramUsageLabel.setText(ramUsage);
						runUpdateRamInfoTask();
					}
				});
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		changeFragment(AppListFragment.newInstance(), true, false, AnimationDirection.FROM_BOTTOM_TO_TOP);
		TaskManagerApp.getInstance().component().inject(this);
		runOnUiThread(updateTask);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void runUpdateRamInfoTask() {
		handler.postDelayed(updateTask, PERIOD_RAM_INFO_UPDATE);
	}
}
