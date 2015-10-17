package com.dogar.mytaskmanager;

import android.app.Application;
import android.content.Context;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.crashlytics.android.Crashlytics;
import com.dogar.mytaskmanager.di.component.AppComponent;
import com.dogar.mytaskmanager.di.component.DaggerAppComponent;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import io.fabric.sdk.android.Fabric;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import timber.log.Timber;

public class TaskManagerApp extends Application {
	public static GoogleAnalytics analytics;
	public static Tracker         tracker;

	private static TaskManagerApp sInstance;

	private AppComponent mComponent;
	private RefWatcher   refWatcher;

	public static TaskManagerApp getInstance() {
		return sInstance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Fabric.with(this, new Crashlytics());
		sInstance = this;
		Timber.plant(new Timber.DebugTree());
		refWatcher = LeakCanary.install(this);
		TypefaceProvider.registerDefaultIconSets();
		initGoogleAnalytics();
		buildComponentAndInject();
	}

	private void initGoogleAnalytics() {
		analytics = GoogleAnalytics.getInstance(this);
		tracker = analytics.newTracker(R.xml.app_tracker);
		analytics.enableAutoActivityReports(this);
	}

	public void buildComponentAndInject() {
		mComponent = ComponentInitializer.init(this);
	}

	public AppComponent component() {
		return mComponent;
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public final static class ComponentInitializer {

		public static AppComponent init(TaskManagerApp taskManagerApp) {
			return DaggerAppComponent.builder().build();
		}
	}

	public static RefWatcher getRefWatcher(Context context) {
		TaskManagerApp application = (TaskManagerApp) context.getApplicationContext();
		return application.refWatcher;
	}
}
