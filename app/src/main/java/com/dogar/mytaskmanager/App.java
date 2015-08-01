package com.dogar.mytaskmanager;

import android.app.Application;

import com.dogar.mytaskmanager.di.component.AppComponent;
import com.dogar.mytaskmanager.di.component.DaggerAppComponent;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import timber.log.Timber;

public class App extends Application {
	private static App sInstance;

	private AppComponent mComponent;

	public static App getInstance() {
		return sInstance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		sInstance = this;
		Timber.plant(new Timber.DebugTree());
		buildComponentAndInject();
	}

	public void buildComponentAndInject() {
		mComponent = ComponentInitializer.init(this);
	}
	public AppComponent component() {
		return mComponent;
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public final static class ComponentInitializer {

		public static AppComponent init(App app) {
			return DaggerAppComponent.builder().build();
		}

	}
}
