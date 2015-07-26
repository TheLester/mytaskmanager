package com.dogar.mytaskmanager;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

import timber.log.Timber;

public class App extends Application {

	@Override
	public void onCreate() {
		Timber.plant(new Timber.DebugTree());
		Fresco.initialize(this);
	}
}
