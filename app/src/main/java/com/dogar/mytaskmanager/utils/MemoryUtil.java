package com.dogar.mytaskmanager.utils;

import android.content.Context;
import android.content.res.Resources;

import com.dogar.mytaskmanager.R;

public class MemoryUtil {
	public static final long MB = 1024;//*KB
	public static final long GB = MB * 1024;


	public static String formatMemSize(Context context, long sizeInKb) {
		Resources res = context.getResources();
		if ((sizeInKb >= MB) && (sizeInKb < GB)) {
			return (sizeInKb / MB) + res.getString(R.string.memory_mb);
		} else if ((sizeInKb >= GB)) {
			return (sizeInKb / GB) + res.getString(R.string.memory_gb);
		}
		return sizeInKb + res.getString(R.string.memory_kb);
	}
}
