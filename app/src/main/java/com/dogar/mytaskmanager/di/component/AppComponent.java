package com.dogar.mytaskmanager.di.component;

import com.dogar.mytaskmanager.di.module.AppModule;
import com.dogar.mytaskmanager.fragment.MoreAppInfoFragment;
import com.dogar.mytaskmanager.mvp.impl.AppsListPresenterImpl;
import com.dogar.mytaskmanager.service.CalculateRamService;

import dagger.Component;

@Component(modules = {
		AppModule.class,
})
public interface AppComponent extends MainGraph {
	void inject(AppsListPresenterImpl presenter);

	void inject(CalculateRamService calculateRamService);

	void inject(MoreAppInfoFragment moreAppInfoFragment);
}