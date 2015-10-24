package com.dogar.mytaskmanager.fragment;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.dogar.mytaskmanager.Constants;
import com.dogar.mytaskmanager.R;
import com.dogar.mytaskmanager.TaskManagerApp;
import com.dogar.mytaskmanager.eventbus.EventHolder;
import com.dogar.mytaskmanager.model.AppInfo;
import com.dogar.mytaskmanager.utils.CommonUtils;
import com.dogar.mytaskmanager.utils.MemoryUtil;
import com.github.clans.fab.FloatingActionMenu;
import com.kogitune.activity_transition.fragment.ExitFragmentTransition;
import com.kogitune.activity_transition.fragment.FragmentTransition;

import org.parceler.Parcels;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class MoreAppInfoFragment extends BaseFragment implements ExitFragmentTransition.ExitListener {
	public static final String APP_INFO_OBJ  = "app_info";
	public static final int    ANIM_DURATION = 1000;

	@Bind(R.id.imgAppIconInfo)
	ImageView          appIcon;
	@Bind(R.id.packageName)
	TextView           packageName;
	@Bind(R.id.version)
	TextView           version;
	@Bind(R.id.installTime)
	TextView           installTime;
	@Bind(R.id.updateTime)
	TextView           updateTime;
	@Bind(R.id.tvPid)
	TextView           pid;
	@Bind(R.id.tvMemory)
	TextView           memory;
	@Bind(R.id.detail_card_view)
	CardView           detailCard;
	@Bind(R.id.fabMoreInfo)
	FloatingActionMenu menu;
	@Inject
	Context            context;

	private AppInfo currentAppInfo;
	private int     screenHeight;

	public static MoreAppInfoFragment newInstance() {
		MoreAppInfoFragment moreAppInfoFragment = new MoreAppInfoFragment();
		return moreAppInfoFragment;
	}

	@Override
	protected int getLayoutResourceId() {
		return R.layout.fragment_more_app_info;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		currentAppInfo = Parcels.unwrap(getArguments().getParcelable(APP_INFO_OBJ));
		EventBus.getDefault().registerSticky(this);
	}

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		TaskManagerApp.getInstance().component().inject(this);
		final ExitFragmentTransition exitFragmentTransition = FragmentTransition.with(this).to(appIcon).start(savedInstanceState);
		exitFragmentTransition.setExitListener(this);
		exitFragmentTransition.startExitListening();
		initScreenHeight();
		fillViews();
		showMenuButton();
		animateInInfoPanel();
		setNavigationModeOn();
	}

	private void showMenuButton() {
		menu.hideMenuButton(false);
		menu.showMenuButton(true);
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
			menu.setZ(detailCard.getZ() + 1);
		}
	}

	@Override
	public void onFragmentExit() {
		animateOutInfoPanel();
		menu.hideMenuButton(true);
	}

	//eventbus methods
	public void onEventMainThread(EventHolder.ColorGeneratedEvent event) {
		if (event.color == Constants.UNDEFINED_VAL || event.darkColor == Constants.UNDEFINED_VAL) {
			return;
		}
		menu.setMenuButtonColorNormal(event.color);
		menu.setMenuButtonColorPressed(event.darkColor);
		getToolbar().setBackgroundColor(event.color);
	}

	@OnClick(R.id.btnShowInAndroidDetails)
	protected void showInAndroidDetails() {
		Intent appInfoIntent = new Intent();
		appInfoIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
		appInfoIntent.setData(Uri.fromParts("package", currentAppInfo.getPackageName(), null));
		startActivity(appInfoIntent);
	}

	@OnClick(R.id.btnKillCurrentApp)
	protected void killCurrentApp() {
		//TODO impl
	}

	private void fillViews() {
		packageName.setText(currentAppInfo.getPackageName());
		version.setText(currentAppInfo.getVersion());
		installTime.setText(CommonUtils.getStringDate(currentAppInfo.getFirstInstallTimestamp(), context));
		updateTime.setText(CommonUtils.getStringDate(currentAppInfo.getLastUpdateTimestamp(), context));
		pid.setText(String.valueOf(currentAppInfo.getPid()));
		memory.setText(MemoryUtil.formatMemSize(context, currentAppInfo.getMemoryInKb()));
	}

	private void animateInInfoPanel() {
		ViewTreeObserver viewTreeObserver = detailCard.getViewTreeObserver();
		if (viewTreeObserver.isAlive()) {
			viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
						appIcon.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					} else {
						appIcon.getViewTreeObserver().removeOnGlobalLayoutListener(this);
					}
					int viewHeight = detailCard.getHeight();
					ObjectAnimator moveAnim = ObjectAnimator.ofFloat(detailCard, "Y", screenHeight - viewHeight / 2 - getResources().getDimension(R.dimen.app_more_icon_size));
					moveAnim.setDuration(ANIM_DURATION);
					moveAnim.setInterpolator(new ReverseInterpolator());
					moveAnim.start();
				}
			});
		}
	}

	private void initScreenHeight() {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		screenHeight = displaymetrics.heightPixels;
	}

	private void animateOutInfoPanel() {
		ObjectAnimator moveAnim = ObjectAnimator.ofFloat(detailCard, "Y", screenHeight);
		moveAnim.setDuration(ANIM_DURATION);
		moveAnim.setInterpolator(new MaterialInterpolator());
		moveAnim.start();
	}

	private float applyMaterialInterpolation(float x) {
		return (float) (6 * Math.pow(x, 2) - 8 * Math.pow(x, 3) + 3 * Math.pow(x, 4));
	}

	private class MaterialInterpolator implements Interpolator {

		@Override
		public float getInterpolation(float input) {
			return applyMaterialInterpolation(input);
		}
	}

	private class ReverseInterpolator implements Interpolator {
		@Override
		public float getInterpolation(float paramFloat) {
			return Math.abs(applyMaterialInterpolation(paramFloat) - 1f);
		}
	}

}
