package com.dogar.mytaskmanager;

import android.os.Bundle;
import android.app.Activity;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

public class ProcInfoActivity extends Activity {
	private  String packageName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_proc_info);
//
//		Intent myIntent = this.getIntent();
//		String info = myIntent.getStringExtra(TasksListActivity.INFO_MESSAGE);
//		packageName = myIntent.getStringExtra(TasksListActivity.INFO_PACKAGE);
//		TextView information = (TextView) findViewById(R.id.information);
//		information.setText(info);
//
//		ImageView icon = (ImageView) findViewById(R.id.infoIcon);
//
//		try {
//			icon.setImageDrawable(getPackageManager().getApplicationIcon(
//					packageName));
//		} catch (NameNotFoundException e) {
//			icon.setImageDrawable(getPackageManager().getDefaultActivityIcon());
//		}
//
//
//		final ActivityManager localActivityManager = (ActivityManager) this
//				.getSystemService("activity");
//		Button kill = (Button) findViewById(R.id.killprocess);
//		kill.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				localActivityManager.killBackgroundProcesses(packageName);
//				NavUtils.navigateUpFromSameTask(ProcInfoActivity.this);
//			}
//		});
//		Button openAppInfo = (Button) findViewById(R.id.openappinfo);
//		openAppInfo.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent appInfoIntent = new Intent();
//				appInfoIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
//				appInfoIntent.setData(Uri.fromParts("package", packageName, null));
//				startActivity(appInfoIntent);
//			}
//		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
