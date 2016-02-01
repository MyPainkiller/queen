package com.github.chenqihong.queen;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.chenqihong.queen.ActivityInfoCollector.BackgroundMonitor;
import com.github.chenqihong.queen.ActivityInfoCollector.PageCollector;
import com.github.chenqihong.queen.CrashCollector.CrashHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpCookie;
import java.util.List;
import java.util.Stack;

/**
 * Beacon对外接口
 */
public class Queen {
	public static final String TAG = "Queen";
	/**
	 * 并发条件下的锁。
	 */
	private String LOCK = "lock";

	/**
	 * 行为JSON列表。
	 */
	private JSONArray mArray;

	/**
	 * Beacon单例。
	 */
	private static Queen sInstance;

	/**
	 * 前一个加载的页面，用来判断本次加载页面是否同前一次一样，避免重复收集。
	 */
	private String mPrePageName;

	/**
	 * 数据发送接口。
	 */
	private DataSender mSender;

	/**
	 * 数据收集接口。
	 */
	private DataCollector mCollector;

	/**
	 * 获取该页面View的层次栈。
	 */
	private Stack<View> mViewStack;

	/**
	 * 初始页面。
	 */
	private View mInitialView;

	/**
	 * 判断Crash是否已经被发送，同一个Crash只可被发送一次。
	 */
	private boolean isCrashHandlerSent = false;

	/**
	 * 判断CrashHandler是否以及初始化，避免重复初始化。
	 */
	private boolean isCrashHandlerStarted = false;

	/**
	 * Beacon必须为单例，一个应用只存在一个实例。
	 * @return Beacon实例
	 */
	public static Queen getInstance(){
		if(sInstance == null){
			sInstance = new Queen();
		}
		return sInstance;
	}

	/**
	 * Beacon初始化
	 * @param sessionDomain cookie的域名
	 * @param application 布局内容
	 */
	public void init(String sessionDomain, Application application){
		backgroundDataCollect(application);
		setDomain(sessionDomain);
		initUncaughtErrorMonitor(application);
		mSender = new DataSender(application);
	}

	/**
	 * 发送数据
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public void sendData(){
		try{
			mSender.sendData(mArray);
		}catch(Exception e){
			Log.e(TAG, "sendData");
			//Log.e(TAG, "Error: sendData", e);
		}
	}

	/**
	 * 设置session Id
	 */
	public void setSessionId(List<HttpCookie> receivedCookies){
		if(null != mSender){
			mSender.setSessionId(receivedCookies);
		}
	}

	/**
	 * 获取cookie域名
	 * @param domain 域名
	 */
	public void setDomain(String domain){
		if(null != mSender){
			mSender.setDomain(domain);
		}
	}

	/**
	 * 获取userId
	 * @param userId
	 */
	public void setUserId(String userId){
		if(null != userId && null != mSender){
			mSender.setUserId(userId);
		}
	}

	/**
	 * 启动未捕获Exception监控
	 * @param context
	 */
	public void initUncaughtErrorMonitor(final Context context){
		synchronized(LOCK){
			if(!isCrashHandlerStarted) {
				CrashHandler crashHandler = new CrashHandler(context, new CrashHandler.OnUnCaughtExceptionListener() {
					public void onException(JSONObject object) {
						if (!isCrashHandlerSent) {
							isCrashHandlerSent = true;
							mArray = mCollector.insertCrashHandler(object, mArray);
							sendData();
						}
					}
				});
				crashHandler.init();
				isCrashHandlerStarted = true;
			}
		}
	}

	/**
	 * app在退出前最后发送一段行为数据
	 * @param context
	 */
	public void appExitSend(Context context){
		try {
			mArray = mCollector.appExitLogHandle(context, mArray);
			sendData();
			mArray = new JSONArray();
			Thread.sleep(300); //延误300毫秒关闭，给数据发送争取时间
		} catch (Exception e) {
			Log.e(TAG, "appExitSend: unknown error");
		}
		
	}

	/**
	 * 按钮数据收集
	 * @param buttonId button的标志
	 * @param title button上的字面内容
	 * @param tag	button如果携带tag，收集tag
	 * @param context
	 */
	public void buttonPressDataCollect(String buttonId, String title, String tag, Context context) {
		synchronized(LOCK){
			mArray = mCollector.buttonPressDataCollect(buttonId, title, tag, context.toString(), mArray);
			bufferFullSend();
		}
	}

	/**
	 * 收集checkbox内容
	 * @param checkBoxId checkbox的标志
	 * @param check 是否check
	 */
	public void checkBoxCheckDataCollect(String checkBoxId, boolean check) {
		synchronized(LOCK){
			mArray = mCollector.checkBoxCheckDataCollect(checkBoxId, check, mArray);
			bufferFullSend();
		}
	}

	/**
	 * 收集TextView的内容
	 * @param textViewName textView的标志
	 * @param textViewContent textView字面内容
	 * @param operationType	该textView可进行的操作
	 */
	public void textViewInfoDataCollect(String textViewName, String textViewContent, int operationType){
		synchronized(LOCK){
			mArray = mCollector.textViewInfoDataCollect(textViewName, textViewContent, operationType, mArray);
			bufferFullSend();
		}
	}

	/**
	 * 收集imageView的内容
	 * @param imageId imageView的标志
	 * @param title	imageView的标题
	 * @param tag		imageView携带的tag
	 * @param context
	 */
	public void imageViewPressDataCollect(String imageId, String title, String tag, Context context){
		synchronized(LOCK){
			mArray = mCollector.
					imageViewPressDataCollect(imageId, title, tag, context.toString(), mArray);
			bufferFullSend();
		}
	}

	/**
	 * 收集Activity状态
	 * @param activityName activity的标志
	 * @param activityTitle	activity的标题
	 * @param activityTag	 activity的备注
	 * @param isOpen	activity的状态（打开和关闭）
	 * @param context
	 */
	public void activityDataCollect
			(String activityName, String activityTitle, String activityTag,
			 boolean isOpen, Context context) {
		if(isOpen){
			if(activityName.equals(mPrePageName)){ //同一个界面不可能启动两次，判断为重复接口
				return;
			}
			mPrePageName = activityName;
		}else{
			mPrePageName = "";
		}
		synchronized(LOCK){
			if(isOpen){
				mArray = mCollector.
						activityOpenDataCollect(mArray, activityName, activityTitle, activityTag);

			}else{
				mArray = mCollector.
						activityCloseDataCollect(mArray, activityName, activityTitle, activityTag);

			}
			bufferFullSend();
		}
	}

	/**
	 * 监控APP是后台还是前台运行，用于监控APP的关闭与打开
	 * @param context
	 */
	public void backgroundDataCollect(final Context context){
		mCollector.backgroundDataCollect(context, new BackgroundMonitor.OnAppStatusChangeListener() {


			@Override
			public void onStatusChanged(JSONObject object, boolean isClosed) {
				if (isClosed) {
					activityDataCollect(PageCollector.getInstance().getPageName(),
							PageCollector.getInstance().getPageTitle(),
							PageCollector.getInstance().getPageTag(),
							false, context);
				}
				synchronized (LOCK) {
					mArray.put(object);
					bufferFullSend();
				}

			}
		});
	}

	/**
	 * 识别在View上所进行的动作
	 * @param ev 动作
	 * @param myView 执行动作的View
	 * @param context
	 */
	public void recognizeViewEvent(MotionEvent ev, View myView, Context context){
		switch(ev.getAction()){
    	case MotionEvent.ACTION_DOWN:{
    		try{
    			mViewStack = new Stack<View>();
    			final float pressX = ev.getRawX();
    			final float pressY = ev.getRawY();
    			findViewAtPosition(myView, (int)pressX, (int)pressY);
    			if(mViewStack.isEmpty()){
    				return;
    			}
    			mInitialView = mViewStack.pop();
    			
    			mInitialView = ignoreView(mInitialView);
    		}catch(Exception e){
				Log.e(TAG, "recognizeViewEvent: unknown error");
    		}
    		
    		break;
    	}
    	case MotionEvent.ACTION_UP:{
    			mViewStack = new Stack<View>();
    			final float x = ev.getRawX();
    			final float y = ev.getRawY();
    			findViewAtPosition(myView, (int)x, (int)y);
    			if(mViewStack.isEmpty()){
    				return;
    			}
    			View view = mViewStack.pop();
				view = ignoreView(view);
    			
    			if(!isAvoidLeftSideView(view.toString()) && mViewStack.isEmpty()){
    				return;
    			}
    			
    			if(view != mInitialView){
    				return;
    			}
    			try{
    				if(view instanceof Button){
    					Button button = (Button)view;
    					buttonPressDataCollect(button.toString(), button.getText().toString(), (String)(button.getTag()), context);
    				}else if(view instanceof ImageView){
    					ImageView imageView = (ImageView)view;
    					imageViewPressDataCollect(imageView.toString(), null, null, context);
    				}else if(view instanceof TextView){
    					if(view instanceof EditText){
    						return;
    					}
    					//TextView text = (TextView)view;
						//TextView暂不做收集
    				}else{
    					buttonPressDataCollect(view.toString(), null, null, context);
    				}
    			}catch(Exception e){
					Log.e(TAG, "recognizeViewEvent: unknown error");
    			}
    		}
    		break;
    	}
	}

	/**
	 * 通过用户动作的范围查找相应的View
	 * @param parent 最上层View
	 * @param x 动作触摸点x坐标
	 * @param y 动作触摸点y坐标
	 */
    private void findViewAtPosition(View parent, int x, int y) {
    	int length = 1;
    	Rect rect = new Rect();
   		parent.getGlobalVisibleRect(rect);
    	if(parent instanceof ViewGroup){
    		length = ((ViewGroup)parent).getChildCount();
    	}
         for (int i = 0; i < length; i++) {
              if(parent instanceof ViewGroup){
            	  
            	  if(View.VISIBLE == parent.getVisibility()){
            		  View child = ((ViewGroup) parent).getChildAt(i);
            		  findViewAtPosition(child, x, y);
            	  }
               	} else {
               		if (rect.contains(x, y)) {  
               			if(View.VISIBLE == parent.getVisibility() && parent.isClickable()){
               				mViewStack.push(parent);
               			}
               		}
             }
                
         }
         
         if(parent.isClickable() 
        		 && rect.contains(x, y) 
        		 && View.VISIBLE == parent.getVisibility()){
        	 mViewStack.push(parent);
         }
    }

	/**
	 * 忽略View，不搜集该View上的动作
	 * @param view
	 * @return
	 */
    private View ignoreView(View view){
    	boolean isListView = false;
		while(!isAvoidListView(view.toString()) && !mViewStack.isEmpty()){
			view = mViewStack.pop();
			isListView = true;
		}
		
		if(!isListView){
			while(!isAvoidLeftSideView(view.toString()) && !mViewStack.isEmpty()){
				view = mViewStack.pop();
			}
		}
		
		return view;
    }

	/**
	 * 适配好期贷，手触摸listView动作搜集
	 * @param viewName
	 * @return
	 */
    private boolean isAvoidListView(String viewName){
    	if(viewName.contains("ListView")){
    		return false;
    	}
    	return true;
    }

	/**
	 * 适配好期贷，手触摸菜单栏动作搜集
	 * @param viewName
	 * @return
	 */
    private boolean isAvoidLeftSideView(String viewName){
    	if(viewName.contains("slide_left")){
    		return false;
    	}
    	return true;
    }

	/**
	 * Json列表满10条随即发送
	 */
	private void bufferFullSend(){
		if(10 <= mArray.length()){
			sendData();
			mArray = new JSONArray();
		}
	}

	/**
	 * Beacon类初始化
	 */
	private Queen(){
		mCollector = new DataCollector();
		mArray = new JSONArray();
		mViewStack = new Stack<View>();
	}

}
