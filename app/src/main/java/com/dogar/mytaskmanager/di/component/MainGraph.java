package com.dogar.mytaskmanager.di.component;

import com.dogar.mytaskmanager.fragment.AppListFragment;

/**
 * Graph with all dependencies. Extend component from this graph
 */
public interface MainGraph {

    void inject(AppListFragment appListFragment);

}