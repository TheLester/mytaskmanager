package com.dogar.mytaskmanager.model;

import android.net.Uri;


import lombok.Data;

@Data
public class TaskInfo {
	private Uri iconURI;
	private String taskName;
}
