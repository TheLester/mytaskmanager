package com.dogar.mytaskmanager.di.component;


import com.dogar.mytaskmanager.di.module.ListAppModule;
import com.dogar.mytaskmanager.fragment.AppListFragment;

import dagger.Component;

@Component(modules = {
		ListAppModule.class
})
public interface AppListComponent extends MainGraph {
	void inject(AppListFragment appListFragment);
}
