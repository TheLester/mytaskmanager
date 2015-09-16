package com.dogar.mytaskmanager.activity;

import android.app.ActivityManager;
import android.os.Bundle;
import android.os.Handler;

import com.dogar.mytaskmanager.App;
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
        App.getInstance().component().inject(this);
        runOnUiThread(updateTask);
    }

    private void runUpdateRamInfoTask() {
        handler.postDelayed(updateTask, PERIOD_RAM_INFO_UPDATE);
    }
}
