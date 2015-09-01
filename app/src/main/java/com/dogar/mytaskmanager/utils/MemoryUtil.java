package com.dogar.mytaskmanager.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;

public class MemoryUtil {

	public static int getProcessRamInMb(int pid) {
		try {
			FileInputStream fileInputStream = new FileInputStream((new StringBuilder("/proc/")).append(pid).append("/statm").toString());

			BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
			String line = reader.readLine();
			while (line != null) {
				System.out.println(line);
				line = reader.readLine();
			}
			if(line!=null){
			int i = Integer.parseInt(line.split("\\s+")[5]);
			return i;}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static String formatMemSize(long size){
		String s = "";
		if (size >= 0x40000000L)
		{
			s = (new StringBuilder(String.valueOf(formatDecimal((double)size / (double)0x40000000L, 1)))).append("GB").toString();
		} else
		{
			if (size >= 0x100000L)
			{
				return (new StringBuilder(String.valueOf(formatDecimal((double)size / (double)0x100000L, 1)))).append("MB").toString();
			}
			if (size< 0x100000L)
			{
				return (new StringBuilder(String.valueOf(formatDecimal((double)size / (double)1024L, 1)))).append("KB").toString();
			}
		}
		return s;
	}
	private static String formatDecimal(double d, int i)
	{
		return (new BigDecimal(d)).setScale(i, 5).toString();
	}
}
