package com.dogar.mytaskmanager.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.dogar.mytaskmanager.R;
import com.dogar.mytaskmanager.adapters.TasksAdapter;
import com.dogar.mytaskmanager.di.component.DaggerAppListComponent;
import com.dogar.mytaskmanager.di.module.ListAppModule;
import com.dogar.mytaskmanager.model.AppInfo;
import com.dogar.mytaskmanager.mvp.AppListPresenter;
import com.dogar.mytaskmanager.mvp.impl.AppsListPresenterImpl;
import com.kogitune.activity_transition.fragment.FragmentTransitionLauncher;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import butterknife.Bind;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;
import timber.log.Timber;

public class AppListFragment extends BaseFragment implements AppListPresenter.View{

	private static final int REFRESH_ANIM_DELAY_MILLIS = 2000;
	@Bind(R.id.process_list)   RecyclerView          processList;
	@Bind(R.id.refresh_layout) MaterialRefreshLayout refreshLayout;


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
		refreshLayout.setMaterialRefreshListener(new RefresherListener());
		processList.setLayoutManager(new LinearLayoutManager(mActivity));
		processList.setItemAnimator(fadeInAnimator);
		processList.setAdapter(scaleInAnimationAdapter);
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
					FragmentTransitionLauncher
							.with(getActivity())
							.image(Glide.with(getActivity()).load(app.getIcon()).asBitmap().into(iconSize, iconSize).get())
							.from(iconHolder).prepare(toFragment);
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
	private class RefresherListener extends MaterialRefreshListener{

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
