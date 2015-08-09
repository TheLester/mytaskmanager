package com.dogar.mytaskmanager.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.dogar.mytaskmanager.fragment.AppListFragment;

public class MainActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		changeFragment(AppListFragment.newInstance(), true, false, AnimationDirection.FROM_BOTTOM_TO_TOP);
	}
}
