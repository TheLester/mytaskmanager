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
import com.dogar.mytaskmanager.model.AppInfo;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

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
		holder.appMemoryInfo.setText(appInfo.getMemoryInfo());
		holder.appCpuInfo.setText("dsaas");

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

	static class TaskVH extends RecyclerView.ViewHolder {
		@Bind(R.id.process_icon)    ImageView appIcon;
		@Bind(R.id.app_info_name)   TextView  appInfo;
		@Bind(R.id.app_info_memory) TextView  appMemoryInfo;
		@Bind(R.id.app_info_cpu)    TextView  appCpuInfo;

		public TaskVH(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
