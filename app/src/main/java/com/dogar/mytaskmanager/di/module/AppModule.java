package com.dogar.mytaskmanager.di.module;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;

import com.dogar.mytaskmanager.App;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

	@Singleton
	@Provides
	public ActivityManager provideActivityManager(Context context) {
		return (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
	}

	@Singleton
	@Provides
	public PackageManager providePackageManager(Context context) {
		return context.getPackageManager();
	}
	@Singleton
	@Provides
	public Context provideContext() {
		return App.getInstance().getApplicationContext();
	}
}
