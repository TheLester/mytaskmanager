package com.dogar.mytaskmanager.service;

import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Intent;
import android.support.v7.preference.PreferenceManager;

import com.dogar.mytaskmanager.Constants;
import com.dogar.mytaskmanager.R;
import com.dogar.mytaskmanager.TaskManagerApp;
import com.dogar.mytaskmanager.eventbus.EventHolder;
import com.dogar.mytaskmanager.utils.MemoryUtil;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

public class CalculateRamService extends IntentService {
	private static final String DEF_FREQUENCY = "1000";
	private boolean isCalculating;

	@Inject ActivityManager activityManager;

	public CalculateRamService() {
		super(CalculateRamService.class.getName());
	}

	public CalculateRamService(String name) {
		super(name);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		TaskManagerApp.getInstance().component().inject(this);
	}

	@Override
	public void onDestroy() {
		isCalculating = false;
		super.onDestroy();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		isCalculating = true;
		calculateRam();
	}

	private void calculateRam() {
		EventHolder.RamUpdateEvent event = new EventHolder.RamUpdateEvent(0);
		long frequency = getFrequency();
		while (isCalculating) {
			ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
			activityManager.getMemoryInfo(memoryInfo);
			long availableKbs = memoryInfo.availMem / Constants.KILOBYTE;
			long totalRam = MemoryUtil.getTotalRAMinKb();
			long used = totalRam - availableKbs;
			event.memoryUsed = used / Constants.KILOBYTE;
			long usedPercent = used / (totalRam / 100);
			event.memoryUsedPercent = usedPercent;
			try {
				Thread.sleep(frequency);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			EventBus.getDefault().post(event);
		}
	}

	private long getFrequency() {
		String frequencyInMs = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.opt_ram_key), DEF_FREQUENCY);
		Timber.i("freq = " + frequencyInMs);
		return Long.parseLong(frequencyInMs);
	}
}
