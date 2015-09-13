package com.dogar.mytaskmanager.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dogar.mytaskmanager.App;
import com.dogar.mytaskmanager.R;
import com.dogar.mytaskmanager.model.AppInfo;
import com.dogar.mytaskmanager.utils.CommonUtils;
import com.dogar.mytaskmanager.utils.MemoryUtil;
import com.kogitune.activity_transition.fragment.ExitFragmentTransition;
import com.kogitune.activity_transition.fragment.FragmentTransition;

import org.parceler.Parcels;

import javax.inject.Inject;

import butterknife.Bind;

public class MoreAppInfoFragment extends BaseFragment {
	public static final String APP_INFO_OBJ = "app_info";

	@Bind(R.id.imgAppIconInfo)
	ImageView appIcon;
	@Bind(R.id.packageName)
	TextView  packageName;
	@Bind(R.id.version)
	TextView  version;
	@Bind(R.id.installTime)
	TextView  installTime;
	@Bind(R.id.updateTime)
	TextView  updateTime;
	@Bind(R.id.tvPid)
	TextView  pid;
	@Bind(R.id.tvMemory)
	TextView  memory;

	@Inject
	Context context;

	@Override
	protected int getLayoutResourceId() {
		return R.layout.fragment_more_app_info;
	}

	public static MoreAppInfoFragment newInstance() {
		MoreAppInfoFragment moreAppInfoFragment = new MoreAppInfoFragment();
		return moreAppInfoFragment;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		App.getInstance().component().inject(this);
		final ExitFragmentTransition exitFragmentTransition = FragmentTransition.with(this).to(appIcon).start(savedInstanceState);
		exitFragmentTransition.startExitListening();
		fillViews();
	}

	private void fillViews() {
		AppInfo currentApp = Parcels.unwrap(getArguments().getParcelable(APP_INFO_OBJ));
		packageName.setText(currentApp.getPackageName());
		version.setText(currentApp.getVersion());
		installTime.setText(CommonUtils.getStringDate(currentApp.getFirstInstallTimestamp(), context));
		updateTime.setText(CommonUtils.getStringDate(currentApp.getLastUpdateTimestamp(), context));
		pid.setText(String.valueOf(currentApp.getPid()));
		memory.setText(MemoryUtil.formatMemSize(context, currentApp.getMemoryInKb()));
	}
}
