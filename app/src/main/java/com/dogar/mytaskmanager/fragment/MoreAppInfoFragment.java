package com.dogar.mytaskmanager.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.dogar.mytaskmanager.R;
import com.kogitune.activity_transition.fragment.ExitFragmentTransition;
import com.kogitune.activity_transition.fragment.FragmentTransition;

import butterknife.Bind;

public class MoreAppInfoFragment extends BaseFragment {
	@Bind(R.id.imgAppIconInfo)
	ImageView appIcon;
	@Override
	protected int getLayoutResourceId() {
		return R.layout.fragment_more_app_info;
	}
	public static MoreAppInfoFragment newInstance() {
		return new MoreAppInfoFragment();
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		final ExitFragmentTransition exitFragmentTransition = FragmentTransition.with(this).to(appIcon).start(savedInstanceState);
		exitFragmentTransition.startExitListening();
	}
}
