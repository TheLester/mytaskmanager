package com.dogar.mytaskmanager.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;

import com.dogar.mytaskmanager.R;
import com.dogar.mytaskmanager.fragment.SettingsFragment;

import butterknife.BindString;

public class SettingsActivity extends BaseActivity {
	@BindString(R.string.action_settings) String settings;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		changeFragment(SettingsFragment.newInstance(), false, false, AnimationDirection.FROM_BOTTOM_TO_TOP);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		titleTv.setText(settings);
	}

	@Override
	public void setShowMenu(boolean showMenu) {
		//nothing
	}
}
