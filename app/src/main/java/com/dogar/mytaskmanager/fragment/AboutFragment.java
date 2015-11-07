package com.dogar.mytaskmanager.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.dogar.mytaskmanager.R;
import com.dogar.mytaskmanager.eventbus.EventHolder;

import butterknife.Bind;
import butterknife.BindString;
import de.greenrobot.event.EventBus;

public class AboutFragment extends BaseFragment {
	public static Fragment newInstance() {
		return new AboutFragment();
	}

	@Bind(R.id.about_text_info) TextView aboutTv;

	@BindString(R.string.action_about) String about;

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setNavigationModeOn(about);
		aboutTv.setText(getInfoContent());
		aboutTv.setMovementMethod(LinkMovementMethod.getInstance());
	}

	@Override
	protected int getLayoutResourceId() {
		return R.layout.fragment_about;
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

	private CharSequence getInfoContent() {
		return Html.fromHtml("Tas Manager Application.\n " +
				"Developed by "
				+ " <i>Dmitry Dogar </i>. "
				+ "<br><b><u>Contact Information:</u></b>"
				+ "<br><a style=\"color: rgb(0,0,255)\" href=\"mailto:dimsdevelop@gmail.com\">Author's Em@il</a>"
				+ "<br><br>Open source at<br><a style=\"color: rgb(0,0,255)\" href=\"https://github.com/TheLester/mytaskmanager\">GitHub Page</a>");

	}
}
