package com.dogar.mytaskmanager.di.component;

import com.dogar.mytaskmanager.di.module.ListAppModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
		ListAppModule.class,
})
public interface AppComponent extends MainGraph {
}