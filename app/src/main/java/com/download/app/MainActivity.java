package com.download.app;

import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.download.entities.FileInfo;
import com.download.services.DownloadService;
import com.imooc.DownLoad.R;

public class MainActivity extends Activity {

	public static MainActivity mMainActivity = null;
	private ListView mListView = null;
	private List<FileInfo> mFileInfoList = null;
	private FileListAdapter mAdapter = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mListView = (ListView) findViewById(R.id.lv_downLoad);
        mFileInfoList = new ArrayList<FileInfo>();
        
        // 初始化文件信息对象
        FileInfo fileInfo = null;
        // 为方便测试，用Tomcat作服务器
        for (int i = 0; i < 13; i++)
		{
        	fileInfo = new FileInfo(i, 
            		"http://www.imooc.com/mobile/mukewang.apk",
            		"imooc" + i + ".apk", 0, 0);
        	mFileInfoList.add(fileInfo);
		}
        
        mAdapter = new FileListAdapter(this, mFileInfoList);
        mListView.setAdapter(mAdapter);
        
        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadService.ACTION_UPDATE);
        filter.addAction(DownloadService.ACTION_FINISHED);
        registerReceiver(mReceiver, filter);
        
        mMainActivity = this;

	    verifyStoragePermissions(MainActivity.this);
    }
    
    protected void onDestroy() 
    {
    	super.onDestroy();
    	unregisterReceiver(mReceiver);
    }
    
    /** 
     * 更新UI的广播接收器
     */
    BroadcastReceiver mReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if (DownloadService.ACTION_UPDATE.equals(intent.getAction()))
			{
				int finised = intent.getIntExtra("finished", 0);
				int id = intent.getIntExtra("id", 0);
				mAdapter.updateProgress(id, finised);
				Log.i("mReceiver", id + "-finised = " + finised);
			}
			else if (DownloadService.ACTION_FINISHED.equals(intent.getAction()))
			{
				// 下载结束
				FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
				mAdapter.updateProgress(fileInfo.getId(), 0);
				Toast.makeText(MainActivity.this, 
						mFileInfoList.get(fileInfo.getId()).getFileName() + "下载完毕", 
						0).show();
			}
		}
	};
	
	/**
	 * 监听返回键
	 * @see android.app.Activity#onKeyUp(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) 
	{
//		if (KeyEvent.KEYCODE_BACK == keyCode && mStartBtn != null)   // 按了返回键时应暂停下载
		{
//			mStopBtn.performClick();  // 模拟按下暂停按钮
		}

		return super.onKeyUp(keyCode, event);
	}

	// Storage Permissions
	private static final int REQUEST_EXTERNAL_STORAGE = 1;
	private static String[] PERMISSIONS_STORAGE = {
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE};

	/**
	 * Checks if the app has permission to write to device storage
	 * <p/>
	 * If the app does not has permission then the user will be prompted to
	 * grant permissions
	 *
	 * @param activity
	 */
	public static void verifyStoragePermissions(Activity activity) {
		// Check if we have write permission
		int permission = ActivityCompat.checkSelfPermission(activity,
				Manifest.permission.WRITE_EXTERNAL_STORAGE);

		if (permission != PackageManager.PERMISSION_GRANTED) {
			// We don't have permission so prompt the user
			ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
					REQUEST_EXTERNAL_STORAGE);
		}
	}
}
