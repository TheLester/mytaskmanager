package com.dogar.mytaskmanager.adapters;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dogar.mytaskmanager.R;
import com.dogar.mytaskmanager.model.AppInfo;
import com.facebook.drawee.view.SimpleDraweeView;

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
				.inflate(R.layout.process_card, parent, false);

		TaskVH vh = new TaskVH(v);
		return vh;
	}

	@Override
	public void onBindViewHolder(TaskVH holder, int position) {
		AppInfo appInfo = tasks.get(position);
		holder.processInfo.setText(appInfo.getTaskName());
		//	holder.processIcon.setImageURI(taskInfo.getIconURI());

	}

	@Override
	public int getItemCount() {
		return tasks.size();
	}

	static class TaskVH extends RecyclerView.ViewHolder {
		@Bind(R.id.process_icon)      SimpleDraweeView processIcon;
		@Bind(R.id.process_info_text) TextView         processInfo;

		public TaskVH(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
