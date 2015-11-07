package com.dogar.mytaskmanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.dogar.mytaskmanager.R;
import com.dogar.mytaskmanager.eventbus.EventHolder;
import com.dogar.mytaskmanager.fragment.AboutFragment;
import com.dogar.mytaskmanager.fragment.AppListFragment;
import com.dogar.mytaskmanager.fragment.SystemInfoFragment;

import de.greenrobot.event.EventBus;

public class MainActivity extends BaseActivity {
	private boolean showMenu = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		changeFragment(AppListFragment.newInstance(), true, false, AnimationDirection.FROM_BOTTOM_TO_TOP);
	}

	public void setShowMenu(boolean showMenu) {
		this.showMenu = showMenu;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem register = menu.findItem(R.id.overflowOption);
		register.setVisible(showMenu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
			case R.id.selectAll:
				EventBus.getDefault().post(new EventHolder.SelectAllAppsEvent());
				return true;
			case R.id.deselectAll:
				EventBus.getDefault().post(new EventHolder.DeselectAllAppsEvent());
				return true;
			case R.id.systemInfo:
				changeFragment(SystemInfoFragment.newInstance(), true, true, AnimationDirection.FROM_TOP_TO_BOTTOM);
				return true;
			case R.id.settings:
				startActivity(new Intent(this, SettingsActivity.class));
				return true;
			case R.id.about:
				changeFragment(AboutFragment.newInstance(), true, true, AnimationDirection.FROM_BOTTOM_TO_TOP);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}


}
