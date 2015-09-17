package com.dogar.mytaskmanager.model;

import android.net.Uri;

import org.parceler.Parcel;

import lombok.Data;

@Data
@Parcel
public class AppInfo {
	Uri    icon;
	String taskName;
	String packageName;
	int    pid;
	int    memoryInKb;
	long   firstInstallTimestamp;
	long   lastUpdateTimestamp;
	String version;
	boolean isCurrentApp;
	boolean isChecked;

	public AppInfo() {
	}
}
