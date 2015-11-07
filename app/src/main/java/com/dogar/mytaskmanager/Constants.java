package com.dogar.mytaskmanager;

public abstract class Constants {
	public static final int  UNDEFINED_VAL = -1;
	public static final long KILOBYTE      = 1024L;

	public static String OSVERSION    = System.getProperty("os.version");
	public static String RELEASE      = android.os.Build.VERSION.RELEASE;
	public static String DEVICE       = android.os.Build.DEVICE;
	public static String MODEL        = android.os.Build.MODEL;
	public static String PRODUCT      = android.os.Build.PRODUCT;
	public static String BRAND        = android.os.Build.BRAND;
	public static String DISPLAY      = android.os.Build.DISPLAY;
	//	public static String CPU_ABI = Build.SUPPORTED_ABIS[0];
	public static String HARDWARE     = android.os.Build.HARDWARE;
	public static String MANUFACTURER = android.os.Build.MANUFACTURER;
	public static String SERIAL       = android.os.Build.SERIAL;
	public static String USER         = android.os.Build.USER;
	public static String HOST         = android.os.Build.HOST;

}
