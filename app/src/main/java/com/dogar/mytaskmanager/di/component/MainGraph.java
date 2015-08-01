package com.dogar.mytaskmanager.di.component;

import com.dogar.mytaskmanager.activity.TasksListActivity;

/**
 * Graph with all dependencies. Extend component from this graph
 */
public interface MainGraph {

    void inject(TasksListActivity tasksListActivity);

}