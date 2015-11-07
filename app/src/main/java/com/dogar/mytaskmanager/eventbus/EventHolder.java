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
		public long memoryUsedPercent;
		public long memoryUsed;
		public RamUpdateEvent(long memoryUsedPercent) {
			this.memoryUsedPercent = memoryUsedPercent;
		}
	}

	public static class BackPressedEvent {
	}

	public static class SelectAllAppsEvent {
	}

	public static class DeselectAllAppsEvent {
	}

	public static class AppKilledEvent {
		public String packageName;

		public AppKilledEvent(String packageName) {
			this.packageName = packageName;
		}
	}

}
