package com.dogar.mytaskmanager.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.dogar.mytaskmanager.R;
import com.dogar.mytaskmanager.eventbus.EventHolder;

import butterknife.Bind;
import butterknife.BindString;
import de.greenrobot.event.EventBus;

import static com.dogar.mytaskmanager.Constants.BRAND;
import static com.dogar.mytaskmanager.Constants.DEVICE;
import static com.dogar.mytaskmanager.Constants.DISPLAY;
import static com.dogar.mytaskmanager.Constants.HARDWARE;
import static com.dogar.mytaskmanager.Constants.HOST;
import static com.dogar.mytaskmanager.Constants.MANUFACTURER;
import static com.dogar.mytaskmanager.Constants.MODEL;
import static com.dogar.mytaskmanager.Constants.OSVERSION;
import static com.dogar.mytaskmanager.Constants.PRODUCT;
import static com.dogar.mytaskmanager.Constants.RELEASE;
import static com.dogar.mytaskmanager.Constants.SERIAL;
import static com.dogar.mytaskmanager.Constants.USER;

public class SystemInfoFragment extends BaseFragment {
	//todo data binding

	@Bind(R.id.osVersionInfo)                TextView osVersion;
	@Bind(R.id.releaseInfo)                  TextView release;
	@Bind(R.id.deviceInfo)                   TextView device;
	@Bind(R.id.modelInfo)                    TextView model;
	@Bind(R.id.productInfo)                  TextView product;
	@Bind(R.id.brandInfo)                    TextView brand;
	@Bind(R.id.displayInfo)                  TextView display;
	@Bind(R.id.hardwareInfo)                 TextView hardware;
	@Bind(R.id.manufacturerInfo)             TextView manufacturer;
	@Bind(R.id.serialInfo)                   TextView serial;
	@Bind(R.id.userInfo)                     TextView user;
	@Bind(R.id.hostInfo)                     TextView host;
	@BindString(R.string.action_system_info) String   systemInfo;

	public static Fragment newInstance() {
		return new SystemInfoFragment();
	}

	@Override
	protected int getLayoutResourceId() {
		return R.layout.fragment_system_info;
	}

	@Override
	public void onStart() {
		super.onStart();
		EventBus.getDefault().register(this);
	}

	@Override
	public void onPause() {
		EventBus.getDefault().unregister(this);
		super.onPause();
	}

	public void onEvent(EventHolder.BackPressedEvent event) {
		goBack();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setNavigationModeOn(systemInfo);
		osVersion.setText(OSVERSION);
		release.setText(RELEASE);
		device.setText(DEVICE);
		model.setText(MODEL);
		product.setText(PRODUCT);
		brand.setText(BRAND);
		display.setText(DISPLAY);
		hardware.setText(HARDWARE);
		manufacturer.setText(MANUFACTURER);
		serial.setText(SERIAL);
		user.setText(USER);
		host.setText(HOST);
	}
}
