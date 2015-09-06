package com.dogar.mytaskmanager.eventbus;


import android.widget.ImageView;

import com.dogar.mytaskmanager.model.AppInfo;

public class EventHolder {
	public static class MoreAppInfoRequestedEvent{
		public AppInfo appInfo;
		public ImageView imageView;

		public MoreAppInfoRequestedEvent(AppInfo appInfo, ImageView imageView) {
			this.appInfo = appInfo;
			this.imageView = imageView;
		}
	}
}
