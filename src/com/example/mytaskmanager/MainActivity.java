package com.example.mytaskmanager;

import java.util.List;

import com.example.mytaskmanager.adapters.ListAdapter;

import android.os.Bundle;
import android.os.Debug;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends ListActivity {
	private ActivityManager manager;
	private List<RunningAppProcessInfo> runningProcesses;
	private ListAdapter myListAdapter;
	final static String INFO_MESSAGE = "com.example.mytaskmanager.MESSAGE";
	final static String INFO_PACKAGE = "com.example.mytaskmanager.PACKAGE";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		runningProcesses = manager.getRunningAppProcesses();
		if (runningProcesses != null && runningProcesses.size() > 0) {
			myListAdapter = new ListAdapter(this, runningProcesses);
			setListAdapter(myListAdapter);

		}
		updateAvailableMemory();
	}

	private void updateAvailableMemory() {
		MemoryInfo mi = new MemoryInfo();
		manager.getMemoryInfo(mi);
		long availableMegs = mi.availMem / 1048576L;
		setTitle("MyTaskManager | " + availableMegs + " MB Available");
	}

	private StringBuffer getProcessInfoMemory(int procID) {
		MemoryInfo mi = new MemoryInfo();
		manager.getMemoryInfo(mi);
		Debug.MemoryInfo info = manager
				.getProcessMemoryInfo(new int[] { procID })[0];
		StringBuffer procMemoryInfo = new StringBuffer();

		procMemoryInfo.append("-------------------------------------");
		procMemoryInfo.append("Process memory information(in kB):\n");
		procMemoryInfo.append("Private dirty pages:");
		procMemoryInfo.append(info.getTotalPrivateDirty());
		procMemoryInfo.append("\n");
		procMemoryInfo.append("Proportional set size:");
		procMemoryInfo.append(info.getTotalPss());
		procMemoryInfo.append("\n");
		procMemoryInfo.append("Shared dirty pages:");
		procMemoryInfo.append(info.getTotalSharedDirty());
		procMemoryInfo.append("\n");
		return procMemoryInfo;

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		StringBuffer information = new StringBuffer();
		information.append("Package Name: ");
		information.append(((RunningAppProcessInfo) getListAdapter().getItem(
				position)).processName);
		information.append("\n");
		information.append("User ID: ");
		information.append(((RunningAppProcessInfo) getListAdapter().getItem(
				position)).uid);
		information.append("\n");
		information.append("Importance: ");
		information
				.append(getAppImportance(((RunningAppProcessInfo) getListAdapter()
						.getItem(position)).importance));
		information.append("\n");
		information.append("Process ID: ");
		information.append(((RunningAppProcessInfo) getListAdapter().getItem(
				position)).pid);
		information.append("\n");
		information.append("LRU: ");
		information.append(((RunningAppProcessInfo) getListAdapter().getItem(
				position)).lru);
		information.append("\n");
		information
				.append(getProcessInfoMemory(((RunningAppProcessInfo) getListAdapter()
						.getItem(position)).pid));
		sendInfo(
				((RunningAppProcessInfo) getListAdapter().getItem(position)).pid,
				((RunningAppProcessInfo) getListAdapter().getItem(position)).processName,
				information.toString());

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.about:
			createAbouteInfo();
			return true;
		case R.id.close:
			this.finish();
			return true;
		case R.id.refresh:
			this.refresh();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void refresh() {
		runningProcesses = manager.getRunningAppProcesses();
		myListAdapter.clear();
		myListAdapter.addAll(runningProcesses);
		myListAdapter.notifyDataSetChanged();
		updateAvailableMemory();

	}

	private void createAbouteInfo() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setIcon(R.drawable.ic_launcher)
				.setTitle("Task Manager")
				.setMessage(
						Html.fromHtml("TaskManager.Developed by:"
								+ " <i>Dmitri Dogar (AE-101)</i>. "
								+ "<br><b><u>Contact Information:</u></b>"
								+ "<br><a href=\"mailto:dimsdevelop@gmail.com\">Author's Em@il</a>"))
				.setPositiveButton("Back",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(final DialogInterface dialog,
									final int id) {
							}
						});
		final AlertDialog alert = builder.create();
		alert.show();
		((TextView) alert.findViewById(android.R.id.message))
				.setMovementMethod(LinkMovementMethod.getInstance());
	}

	private void sendInfo(int pid, String name, String data) {
		Intent intent = new Intent(this, ProcInfoActivity.class);
		intent.putExtra(INFO_PACKAGE, name);
		intent.putExtra(INFO_MESSAGE, data);
		startActivity(intent);
	}

	private String getAppImportance(int imp) {
		switch (imp) {
		case 100:
			return "Foreground Process";
		case 130:
			return "Perceptible Process";
		case 200:
			return "Visible Process";
		case 300:
			return "Service Process";
		case 400:
			return "Background Process";
		case 500:
			return "Empty Process";
		default:
			return "Some Process";
		}
	}

}
