package com.dogar.mytaskmanager.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
}
