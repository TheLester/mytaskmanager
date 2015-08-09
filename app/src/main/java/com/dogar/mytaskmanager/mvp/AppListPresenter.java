package com.dogar.mytaskmanager.mvp;

import com.dogar.mytaskmanager.App;
import com.dogar.mytaskmanager.mvp.base.BasePresenter;
import com.dogar.mytaskmanager.mvp.base.BaseView;

public interface AppListPresenter extends BasePresenter{
    void loadAppList();
    void loadAppMoreInfo(App app);

    interface View extends BaseView{
        void onAppListLoaded();
    }
}
