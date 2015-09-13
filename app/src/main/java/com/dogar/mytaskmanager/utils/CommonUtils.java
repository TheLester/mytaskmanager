package com.dogar.mytaskmanager.utils;

import android.content.Context;

import java.text.DateFormat;
import java.util.Date;

public class CommonUtils {
	public static String getStringDate(long timestamp, Context context) {
		Date date = new Date(timestamp);
		DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
		return dateFormat.format(date);
	}
}
