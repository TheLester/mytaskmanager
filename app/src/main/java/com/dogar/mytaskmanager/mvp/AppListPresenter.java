package com.dogar.mytaskmanager.mvp;

import com.dogar.mytaskmanager.App;
import com.dogar.mytaskmanager.model.AppInfo;
import com.dogar.mytaskmanager.mvp.base.BasePresenter;
import com.dogar.mytaskmanager.mvp.base.BaseView;

import java.util.List;

public interface AppListPresenter extends BasePresenter{
    void loadAppList();
    void reloadAppList();
    void loadAppMoreInfo(App app);

    interface View extends BaseView{
        void onAppListLoaded(List<AppInfo> runningApps);
    }
}
