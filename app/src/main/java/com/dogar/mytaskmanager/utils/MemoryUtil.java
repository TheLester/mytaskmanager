package com.dogar.mytaskmanager.utils;

import android.content.Context;
import android.content.res.Resources;

import com.dogar.mytaskmanager.R;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

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

	public static long getTotalRAMinKb() {
		long totRam = 0;
		RandomAccessFile reader = null;
		try {
			String load = null;
			reader = new RandomAccessFile("/proc/meminfo", "r");
			load = reader.readLine();

			// Get the Number value from the string
			Pattern p = Pattern.compile("(\\d+)");
			Matcher m = p.matcher(load);
			String value = "";
			while (m.find()) {
				value = m.group(1);
			}
			reader.close();
			Timber.i("String val "+value);
			totRam = Long.parseLong(value);

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return totRam;
	}
}
