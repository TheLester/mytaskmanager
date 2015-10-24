package com.dogar.mytaskmanager.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.akexorcist.roundcornerprogressbar.IconRoundCornerProgressBar;
import com.bumptech.glide.Glide;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.dogar.mytaskmanager.Constants;
import com.dogar.mytaskmanager.R;
import com.dogar.mytaskmanager.adapters.TasksAdapter;
import com.dogar.mytaskmanager.di.component.DaggerAppListComponent;
import com.dogar.mytaskmanager.di.module.ListAppModule;
import com.dogar.mytaskmanager.eventbus.EventHolder;
import com.dogar.mytaskmanager.model.AppInfo;
import com.dogar.mytaskmanager.mvp.AppListPresenter;
import com.dogar.mytaskmanager.mvp.impl.AppsListPresenterImpl;
import com.dogar.mytaskmanager.utils.ToastUtils;
import com.kogitune.activity_transition.fragment.FragmentTransitionLauncher;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;
import timber.log.Timber;

public class AppListFragment extends BaseFragment implements AppListPresenter.View, SlidingUpPanelLayout.PanelSlideListener, IconRoundCornerProgressBar.OnIconClickListener {

	private static final int REFRESH_ANIM_DELAY_MILLIS = 2000;
	@Bind(R.id.process_list)       RecyclerView               processList;
	@Bind(R.id.refresh_layout)     MaterialRefreshLayout      refreshLayout;
	@Bind(R.id.sliding_layout)     SlidingUpPanelLayout       slidingUpPanelLayout;
	@Bind(R.id.progressExpandIcon) ImageView                  progressExpandIcon;
	@Bind(R.id.ramProgress)        IconRoundCornerProgressBar ramProgress;

	@Inject TasksAdapter            myTasksAdapter;
	@Inject AppsListPresenterImpl   appsListPresenter;
	@Inject ScaleInAnimationAdapter scaleInAnimationAdapter;
	@Inject FadeInAnimator          fadeInAnimator;
	@Inject Intent                  calculateRamIntent;

	@BindColor(R.color.md_green_500) int colorGreen;

	private boolean isRefreshing;
	private List<AppInfo> appInfos = new ArrayList<>();

	public static Fragment newInstance() {
		return new AppListFragment();
	}

	protected int getLayoutResourceId() {
		return R.layout.fragment_apps_list;
	}

	@Override
	public void onResume() {
		super.onResume();
		Timber.i("resume");
		appsListPresenter.onResume();
		getActivity().startService(calculateRamIntent);
	}

	@Override
	public void onPause() {
		Timber.i("pause");
		getActivity().stopService(calculateRamIntent);
		appsListPresenter.onPause();
		super.onPause();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DaggerAppListComponent.builder()
				.listAppModule(new ListAppModule(this, getActivity(), appInfos))
				.build()
				.inject(this);

		appsListPresenter.loadAppList();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		getToolbar().setBackgroundColor(colorGreen);
		refreshLayout.setMaterialRefreshListener(new RefresherListener());
		processList.setLayoutManager(new LinearLayoutManager(mActivity));
		processList.setItemAnimator(fadeInAnimator);
		processList.setAdapter(scaleInAnimationAdapter);
		slidingUpPanelLayout.setPanelSlideListener(this);
		slidingUpPanelLayout.setTouchEnabled(false);
		ramProgress.setOnIconClickListener(this);
		setNavigationModeOff();
	}


	@Override
	public void showProgress(boolean show) {

	}

	@Override
	public void onLoadAppMoreInfo(final AppInfo app, final ImageView iconHolder) {
		final MoreAppInfoFragment toFragment = MoreAppInfoFragment.newInstance();
		final int iconSize = getIconImageSize();
		new Thread() {
			@Override
			public void run() {
				try {
					Bitmap appIcon = Glide.with(getActivity()).load(app.getIcon()).asBitmap().into(iconSize, iconSize).get();
					FragmentTransitionLauncher
							.with(getActivity())
							.image(appIcon)
							.from(iconHolder).prepare(toFragment);

					new Palette.Builder(appIcon).generate(new Palette.PaletteAsyncListener() {
						@Override
						public void onGenerated(Palette palette) {
							EventBus.getDefault().postSticky(new EventHolder.ColorGeneratedEvent(
									palette.getVibrantColor(Constants.UNDEFINED_VAL),
									palette.getDarkVibrantColor(Constants.UNDEFINED_VAL)));
						}
					});
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				toFragment.getArguments().putParcelable(MoreAppInfoFragment.APP_INFO_OBJ, Parcels.wrap(app));
				getFragmentManager().beginTransaction().replace(R.id.fragmentContainer, toFragment).addToBackStack(null).commit();
			}
		}.start();
	}


	@Override
	public void onAppListLoaded(List<AppInfo> runningApps) {
		if (!runningApps.isEmpty()) {
			appInfos.clear();
			appInfos.addAll(runningApps);
			if (isRefreshing) {
				finishRefreshingWithDelay();
			} else {
				scaleInAnimationAdapter.notifyDataSetChanged();
			}
		}
		Timber.i("Done!");
	}

	@Override
	public void onNewRamInfo(long memoryUsed) {
		Timber.i("Get ram info -" + memoryUsed + "%");
		ramProgress.setProgress(memoryUsed);
	}

	@Override
	public void onPanelSlide(View panel, float slideOffset) {

	}

	@Override
	public void onPanelCollapsed(View panel) {

	}

	@Override
	public void onPanelExpanded(View panel) {
	}

	@Override
	public void onPanelAnchored(View panel) {

	}

	@Override
	public void onPanelHidden(View panel) {
	}

	/**
	 * On trim memory click
	 */
	@Override
	public void onIconClick() {
		ToastUtils.show(getActivity(), "bla");
	}

	@OnClick(R.id.progressExpandIcon)
	protected void expandInfoPanelClicked(ImageView view) {
		Animation animation = null;
		if (slidingUpPanelLayout.getPanelState().equals(SlidingUpPanelLayout.PanelState.EXPANDED)) {
			slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
			animation = AnimationUtils.loadAnimation(getActivity(), R.anim.drawable_rotate_down);
		} else {
			slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
			animation = AnimationUtils.loadAnimation(getActivity(), R.anim.drawable_rotate_up);
		}
		view.startAnimation(animation);


	}

	private void finishRefreshingWithDelay() {
		refreshLayout.postDelayed(new Runnable() {
			@Override
			public void run() {
				refreshLayout.finishRefreshing();
			}
		}, REFRESH_ANIM_DELAY_MILLIS);
	}

	private int getIconImageSize() {
		return (int) getResources().getDimension(R.dimen.app_card_icon_size);
	}


	private class RefresherListener extends MaterialRefreshListener {

		@Override
		public void onfinish() {
			isRefreshing = false;
			scaleInAnimationAdapter.notifyDataSetChanged();
			Timber.i("Complete refresh!");
		}

		@Override
		public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
			Timber.i("Refreshing ...");
			isRefreshing = true;
			appsListPresenter.reloadAppList();
		}
	}
}
