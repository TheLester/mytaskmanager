package com.dogar.mytaskmanager.di.component;


import com.dogar.mytaskmanager.di.module.ListAppModule;
import com.dogar.mytaskmanager.fragment.AppListFragment;
import com.dogar.mytaskmanager.mvp.AppListPresenter;

import javax.inject.Singleton;
import dagger.Component;

@Singleton
@Component( modules = {
		ListAppModule.class
})
public interface AppListComponent extends MainGraph {
	void inject(AppListFragment appListFragment);
}
