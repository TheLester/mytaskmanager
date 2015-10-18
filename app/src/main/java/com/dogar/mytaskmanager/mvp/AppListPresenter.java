package com.dogar.mytaskmanager.mvp;

import android.widget.ImageView;

import com.dogar.mytaskmanager.model.AppInfo;
import com.dogar.mytaskmanager.mvp.base.BasePresenter;
import com.dogar.mytaskmanager.mvp.base.BaseView;

import java.util.List;

public interface AppListPresenter extends BasePresenter{
    void loadAppList();
    void reloadAppList();

    interface View extends BaseView{
        void onAppListLoaded(List<AppInfo> runningApps);
		void onNewRamInfo(long memoryUsed);
		void onLoadAppMoreInfo(AppInfo app,ImageView iconHolder);
    }
}
