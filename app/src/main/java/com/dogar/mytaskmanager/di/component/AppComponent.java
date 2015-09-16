package com.dogar.mytaskmanager.di.component;

import com.dogar.mytaskmanager.activity.MainActivity;
import com.dogar.mytaskmanager.di.module.AppModule;
import com.dogar.mytaskmanager.fragment.MoreAppInfoFragment;
import com.dogar.mytaskmanager.mvp.impl.AppsListPresenterImpl;

import dagger.Component;

@Component(modules = {
		AppModule.class,
})
public interface AppComponent extends MainGraph {
	void inject(AppsListPresenterImpl presenter);

	void inject(MainActivity mainActivity);

	void inject(MoreAppInfoFragment moreAppInfoFragment);
}