package com.dogar.mytaskmanager.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dogar.mytaskmanager.R;
import com.dogar.mytaskmanager.adapters.TasksAdapter;
import com.dogar.mytaskmanager.di.component.DaggerAppListComponent;
import com.dogar.mytaskmanager.di.module.ListAppModule;
import com.dogar.mytaskmanager.model.AppInfo;
import com.dogar.mytaskmanager.mvp.AppListPresenter;
import com.dogar.mytaskmanager.mvp.impl.AppsListPresenterImpl;
import com.kogitune.activity_transition.fragment.FragmentTransitionLauncher;
import com.tuesda.walker.circlerefresh.CircleRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import butterknife.Bind;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;
import timber.log.Timber;

public class AppListFragment extends BaseFragment implements AppListPresenter.View, CircleRefreshLayout.OnCircleRefreshListener {

	private static final int REFRESH_ANIM_DELAY_MILLIS = 2000;
	@Bind(R.id.process_list)   RecyclerView        processList;
	@Bind(R.id.refresh_layout) CircleRefreshLayout refreshLayout;


	@Inject TasksAdapter            myTasksAdapter;
	@Inject AppsListPresenterImpl   appsListPresenter;
	@Inject ScaleInAnimationAdapter scaleInAnimationAdapter;
	@Inject FadeInAnimator          fadeInAnimator;

	private boolean isRefreshing;
	private List<AppInfo> appInfos = new ArrayList<>();

	public static Fragment newInstance() {
		return new AppListFragment();
	}

	protected int getLayoutResourceId() {
		return R.layout.fragment_apps_list;
	}

	@Override
	public void onStart() {
		super.onStart();
		appsListPresenter.onStart();
	}

	@Override
	public void onStop() {
		appsListPresenter.onStop();
		super.onStop();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		refreshLayout.setOnRefreshListener(this);

		DaggerAppListComponent.builder()
				.listAppModule(new ListAppModule(this, getActivity(), appInfos))
				.build()
				.inject(this);

		processList.setLayoutManager(new LinearLayoutManager(mActivity));
		processList.setItemAnimator(fadeInAnimator);
		processList.setAdapter(scaleInAnimationAdapter);
		appsListPresenter.loadAppList();
	}


	@Override
	public void showProgress(boolean show) {

	}

	@Override
	public void onLoadAppMoreInfo(AppInfo app, ImageView iconHolder) {
		final MoreAppInfoFragment toFragment = MoreAppInfoFragment.newInstance();
		int iconSize = getIconImageSize();
		FragmentTransitionLauncher
				.with(getActivity())
				.image(Glide.with(getActivity()).load(app.getIcon()).asBitmap().into(iconSize, iconSize).get())
				.from(iconHolder).prepare(toFragment);
		getFragmentManager().beginTransaction().replace(R.id.fragmentContainer, toFragment).addToBackStack(null).commit();
	}

	@Override
	public void completeRefresh() {
		isRefreshing = false;
		scaleInAnimationAdapter.notifyDataSetChanged();
		Timber.i("Complete refresh!");
	}

	@Override
	public void refreshing() {
		Timber.i("Refreshing ...");
		isRefreshing = true;
		appsListPresenter.reloadAppList();

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
}
