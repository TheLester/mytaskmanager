package com.dogar.mytaskmanager.di.module;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;

import com.dogar.mytaskmanager.App;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

	@Provides
	public ActivityManager provideActivityManager(Context context) {
		return (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
	}

	@Provides
	public PackageManager providePackageManager(Context context) {
		return context.getPackageManager();
	}

	@Provides
	public Context provideContext() {
		return App.getInstance().getApplicationContext();
	}

	@Provides
	public Handler provideHandler() {
		return new Handler();
	}

}
