package com.github.chenqihong.queen.ActivityInfoCollector;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

import org.json.JSONObject;

import java.util.List;

/**
 * 前后台监控
 * Back or front ground monitor
 */
public class BackgroundMonitor {
	/**
	 * 是否在后台运行的判断
	 */
	private boolean isBackGround;

	/**
	 * 每次检查与前一次状态是否相同的判断
	 */
	private boolean isDiff;

	/**
	 * 状态监听
	 */
	private OnAppStatusChangeListener mListener;

	/**
	 * 后台监听线程
	 */
	private BackGroundCheckThread mThread;

	/**
	 * 监听单例
	 */
	private static BackgroundMonitor mInstance;

	/**
	 * 前后台单例实现，只能有一个监控类
	 * @return 监控实例
	 */
	public static BackgroundMonitor getInstance(){
		if(null == mInstance){
			mInstance = new BackgroundMonitor();
		}
		return mInstance;
	}

	/**
	 * 监控状态变化监听
	 * @param listener 监听器
	 */
	public void setAppStatusChangeListener(OnAppStatusChangeListener listener) {
		mListener = listener;
	}

	/**
	 * 监听器接口
	 */
	public interface OnAppStatusChangeListener{

		/**
		 * 当状态改变时传回行为元素，以及状态标识
		 * @param object 行为元素
		 * @param isClosed 状态标识
		 */
		void onStatusChanged(JSONObject object, boolean isClosed);
	}

	/**
	 * 开始监控
	 * @param context
	 */
	public void startBackgoundInfoCollector(Context context){
		if(null != mThread){
			return;
		}
		if(null == mListener){
			return;
		}
		mThread = new BackGroundCheckThread(context, context.getPackageName());
		mThread.start();
	}

	/**
	 * 判断APP是否在后台运行
	 * @param appName app名字
	 * @param context
	 * @return 是否在后台运行的标志
	 */
	public boolean isApplicationBroughtToBackground(String appName, Context context){
		ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();
		if(null == appProcesses){
			return false;
		}
		for(RunningAppProcessInfo appProcess:appProcesses){
			if(appProcess.processName.equals(appName)){
				if(appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND){
					return true;
				}else{
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * 监控后台线程
	 */
	private class BackGroundCheckThread extends Thread {
		private Context mContext;
		private String mAppName;
		public BackGroundCheckThread(Context context, String appName){
			mContext = context;
			mAppName = appName;
			
			//第一次启动发送启动消息
			try{
				long time = System.currentTimeMillis();
				JSONObject object = new JSONObject();
				object.put("tm", time);
				object.put("ac", "AO");
				object.put("ta",mAppName);
				mListener.onStatusChanged(object, true);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		@Override
		public void run(){
			while(true){
				try {
					isBackGround = isApplicationBroughtToBackground(mAppName, mContext);
					if(isBackGround != isDiff){
						long time = System.currentTimeMillis();
						JSONObject object = null;
						object = new JSONObject();
						object.put("tm", time);
						if(isBackGround){
							object.put("ac", "AC");
						}else {
							object.put("ac", "AO");
						}
						object.put("ta",mAppName);
						if(isBackGround){
							mListener.onStatusChanged(object, true);
						}else{
							mListener.onStatusChanged(object, false);
						}
					}
					isDiff = isBackGround;
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private BackgroundMonitor(){

	}

}
