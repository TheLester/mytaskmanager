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
	public static class ColorGeneratedEvent{
		public int color;
		public int darkColor;

		public ColorGeneratedEvent(int color, int darkColor) {
			this.color = color;
			this.darkColor = darkColor;
		}
	}
	public static class RamUpdateEvent{
		public long memoryUsed;

		public RamUpdateEvent(long memoryUsed) {
			this.memoryUsed = memoryUsed;
		}
	}
}
