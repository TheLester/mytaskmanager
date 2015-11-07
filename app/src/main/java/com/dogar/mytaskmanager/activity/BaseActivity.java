package com.dogar.mytaskmanager.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.dogar.mytaskmanager.R;
import com.dogar.mytaskmanager.TaskManagerApp;
import com.dogar.mytaskmanager.eventbus.EventHolder;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract  class BaseActivity extends AppCompatActivity {
	@Bind(R.id.toolbar)      Toolbar  toolbar;
	@Bind(R.id.toolbarTitle) TextView titleTv;
	private FragmentManager mFm = getSupportFragmentManager();
	private Tracker mTracker;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base);
		ButterKnife.bind(this);
		setupToolbar();
		TaskManagerApp application = (TaskManagerApp) getApplication();
		mTracker = application.getTracker();

	}
	public  abstract void setShowMenu(boolean showMenu);
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}

	public Toolbar getToolbar() {
		return toolbar;
	}

	public void setTitle(String title) {
		titleTv.setText(title);
	}

	public void goToTop() {
		mFm.popBackStack(mFm.getBackStackEntryAt(0).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}
	public void popFragment(){
		mFm.popBackStack();
	}
	public void changeFragment(Fragment f, boolean addToBackStack, boolean animate, AnimationDirection direction) {
		if (f == null) {
			return;
		}
		FragmentTransaction ft = mFm.beginTransaction();

		// Animations
		if (animate) {
			switch (direction) {
				case FROM_RIGHT_TO_LEFT:
					ft.setCustomAnimations(R.anim.in_from_left, R.anim.out_to_left,
							R.anim.in_from_right, R.anim.out_to_right);
					break;
				case FROM_LEFT_TO_RIGHT:
					ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_right,
							R.anim.in_from_left, R.anim.out_to_left);
					break;
				case FROM_BOTTOM_TO_TOP:
					ft.setCustomAnimations(R.anim.in_from_down, R.anim.out_to_down,
							R.anim.in_from_up, R.anim.out_to_up);
					break;
				case FROM_TOP_TO_BOTTOM:
					ft.setCustomAnimations(R.anim.in_from_up, R.anim.out_to_up,
							R.anim.in_from_down, R.anim.out_to_down);
					break;
			}
		}

		// BackStack
		if (addToBackStack) {
			ft.addToBackStack(null);
		}

		// Adding fragment
		Fragment oldFragment = mFm.findFragmentById(R.id.fragmentContainer);
		if (oldFragment != null) {
			ft.remove(oldFragment);
		}
		ft.add(R.id.fragmentContainer, f);

		// Commit transaction
		ft.commit();
		mTracker.setScreenName("Image~" + f.getClass().getCanonicalName());
		mTracker.send(new HitBuilders.ScreenViewBuilder().build());
	}

	protected void setupToolbar() {
		setSupportActionBar(toolbar);
		ActionBar supportActionBar = getSupportActionBar();
		supportActionBar.setHomeAsUpIndicator(R.drawable.arrow_back);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EventBus.getDefault().post(new EventHolder.BackPressedEvent());
			}
		});
		supportActionBar.setHomeButtonEnabled(false);
		supportActionBar.setDisplayShowTitleEnabled(false);
	}

	protected enum AnimationDirection {
		FROM_LEFT_TO_RIGHT,
		FROM_RIGHT_TO_LEFT,
		FROM_TOP_TO_BOTTOM,
		FROM_BOTTOM_TO_TOP
	}

}