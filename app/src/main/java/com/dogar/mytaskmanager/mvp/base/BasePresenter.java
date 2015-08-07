package com.dogar.mytaskmanager.mvp.base;

import android.content.Context;

public interface BasePresenter {
    void onStart(Context context);
    void onStop();
}
