package com.dogar.mytaskmanager.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dogar.mytaskmanager.R;
import com.dogar.mytaskmanager.eventbus.EventHolder;
import com.dogar.mytaskmanager.model.AppInfo;
import com.dogar.mytaskmanager.utils.MemoryUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import timber.log.Timber;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskVH> {

	private Context       context;
	private List<AppInfo> tasks;

	public TasksAdapter(Context context, List<AppInfo> tasks) {
		this.context = context;
		this.tasks = tasks;
	}

	@Override
	public TaskVH onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.item_app_card, parent, false);

		TaskVH vh = new TaskVH(v);
		return vh;
	}

	@Override
	public void onBindViewHolder(TaskVH holder, int position) {
		AppInfo appInfo = tasks.get(position);
		holder.appInfo.setText(appInfo.getTaskName());
		holder.appMemoryInfo.setText(MemoryUtil.formatMemSize(context, appInfo.getMemoryInKb()));

		Glide.with(context)
				.load(appInfo.getIcon())
				.centerCrop()
				.crossFade()
				.into(holder.appIcon);

	}

	@Override
	public int getItemCount() {
		return tasks == null ? 0 : tasks.size();
	}

	class TaskVH extends RecyclerView.ViewHolder {
		@Bind(R.id.processIcon)         ImageView      appIcon;
		@Bind(R.id.tvAppInfoName)       TextView       appInfo;
		@Bind(R.id.tvAppInfoMemoryDesc) TextView       appMemoryInfo;

		public TaskVH(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

		@OnClick(R.id.btnMoreAppInfo)
		protected void moreInfoClicked() {
			int position = getAdapterPosition();
			if (position != RecyclerView.NO_POSITION) {
				AppInfo selectedApp = tasks.get(position);
				EventBus.getDefault().post(new EventHolder.MoreAppInfoRequestedEvent(selectedApp, appIcon));
				Timber.i("Test");
			}
		}
		@OnCheckedChanged(R.id.cbTaskBox)
		protected void checkedAppChanged(){

		}
	}
}
