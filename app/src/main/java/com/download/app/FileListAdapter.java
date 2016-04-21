/*
 * @Title FileListAdapter.java
 * @Copyright Copyright 2010-2015 Yann Software Co,.Ltd All Rights Reserved.
 * @Descriptio
 * @author Yann
 * @date 2015-8-9 上午11:37:18
 * @version 1.0
 */
package com.download.app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.download.entities.FileInfo;
import com.download.services.DownloadService;
import com.imooc.DownLoad.R;

import java.util.List;

/** 
 * 类注释
 * @author Yann
 * @date 2015-8-9 上午11:37:18
 */
public class FileListAdapter extends BaseAdapter
{
	private Context mContext;
	private List<FileInfo> mList;
	
	public FileListAdapter(Context context, List<FileInfo> fileInfos)
	{
		this.mContext = context;
		this.mList = fileInfos;
	}
	
	/**
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount()
	{
		return mList.size();
	}

	/**
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewHolder = null;
		final FileInfo fileInfo = mList.get(position);
		
		if (convertView != null)
		{
			viewHolder = (ViewHolder) convertView.getTag();
			
			if (!viewHolder.mFileName.getTag().equals(Integer.valueOf(fileInfo.getId())))
			{
				convertView = null;
			}
		}
		
		if (null == convertView)
		{
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(R.layout.item, parent, false);
			
			viewHolder = new ViewHolder(
					(TextView) convertView.findViewById(R.id.tv_fileName),
					(ProgressBar) convertView.findViewById(R.id.pb_progress),
					(Button) convertView.findViewById(R.id.btn_start),
					(Button) convertView.findViewById(R.id.btn_stop)
					);
			convertView.setTag(viewHolder);
			
			viewHolder.mFileName.setText(fileInfo.getFileName());
			viewHolder.mProgressBar.setMax(100);
			viewHolder.mStartBtn.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					// 通知Service开始下载
					Intent intent = new Intent(mContext, DownloadService.class);
					intent.setAction(DownloadService.ACTION_START);
					intent.putExtra("fileInfo", fileInfo);
					mContext.startService(intent);
				}
			});
			viewHolder.mStopBtn.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Intent intent = new Intent(mContext, DownloadService.class);
					intent.setAction(DownloadService.ACTION_STOP);
					intent.putExtra("fileInfo", fileInfo);
					mContext.startService(intent);
				}
			});
			
			// 将viewHolder.mFileName的Tag设为fileInfo的ID，用于唯一标识viewHolder.mFileName
			viewHolder.mFileName.setTag(Integer.valueOf(fileInfo.getId()));
		}
		
		viewHolder.mProgressBar.setProgress(fileInfo.getFinished());
		
		return convertView;
	}

	/** 
	 * 更新列表项中的进度条
	 * @param id
	 * @param progress
	 * @return void
	 * @author Yann
	 * @date 2015-8-9 下午1:34:14
	 */ 
	public void updateProgress(int id, int progress)
	{
		FileInfo fileInfo = mList.get(id);
		fileInfo.setFinished(progress);
		notifyDataSetChanged();
	}
	
	private static class ViewHolder
	{
		TextView mFileName;
		ProgressBar mProgressBar;
		Button mStartBtn;
		Button mStopBtn;
		
		/** 
		 *@param mFileName
		 *@param mProgressBar
		 *@param mStartBtn
		 *@param mStopBtn
		 */
		public ViewHolder(TextView mFileName, ProgressBar mProgressBar,
				Button mStartBtn, Button mStopBtn)
		{
			this.mFileName = mFileName;
			this.mProgressBar = mProgressBar;
			this.mStartBtn = mStartBtn;
			this.mStopBtn = mStopBtn;
		}
	}
	
	/**
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position)
	{
		return null;
	}

	/**
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position)
	{
		return 0;
	}
}
