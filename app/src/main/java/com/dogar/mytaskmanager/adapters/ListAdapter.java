package com.dogar.mytaskmanager.adapters;

import java.util.List;

import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.dogar.mytaskmanager.R;

public class ListAdapter extends ArrayAdapter<RunningAppProcessInfo> {

	private final Context context;
	private  List<RunningAppProcessInfo> values;

	public ListAdapter(Context context, List<RunningAppProcessInfo> values) {
		super(context, R.layout.main, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.main, parent, false);
		TextView appName = (TextView) rowView.findViewById(R.id.appNameText);
		ImageView appIcon = (ImageView) rowView.findViewById(R.id.imageView);
		appName.setText(getName(position));
		appIcon.setImageDrawable(getIcon(position));
		return rowView;
	}

	private String getName(int position) {
		ApplicationInfo ai;
		PackageManager pm = context.getPackageManager();
		try {
			ai = pm.getApplicationInfo(values.get(position).processName, 0);
			
		} catch (final NameNotFoundException e) {
			ai = null;
		}
		return (String) (ai != null ? pm.getApplicationLabel(ai)
				: values.get(position).processName.split("\\.")[values
						.get(position).processName.split("\\.").length - 1]);
	}

	private Drawable getIcon(int position) {
		Drawable icon = null;
		PackageManager pm = context.getPackageManager();
		try {
			icon = pm.getApplicationIcon(values.get(position).processName);
		} catch (NameNotFoundException e) {
			icon=pm.getDefaultActivityIcon();
		}
		return icon;
		
	}
}
