package com.github.chenqihong.queen;

import android.app.Application;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.chenqihong.queen.ActivityInfoCollector.BackgroundMonitor;
import com.github.chenqihong.queen.ActivityInfoCollector.PageCollector;
import com.github.chenqihong.queen.Base.RSAUtils;
import com.github.chenqihong.queen.CrashCollector.CrashHandler;
import com.github.chenqihong.queen.Exception.NoUrlException;
import com.github.chenqihong.queen.Exception.UnInitException;
import com.github.chenqihong.queen.Watcher.IQueenWatcher;
import com.github.chenqihong.queen.Watcher.Observed;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/**
 * Queen对外接口
 * Encapsulated interface of Queen
 */
public class Queen {

	public static final String TAG = "Queen";

	/**
	 * 并发条件下的锁。
	 * LOCK for sync
	 */
	private final String LOCK = "lock";

	/**
	 * 行为JSON列表。
	 * List of JSON used for user behaviors
	 */
	private JSONArray mArray;

	/**
	 * Queen单例。
	 * Instance of Queen
	 */
	private static Queen sInstance;

	/**
	 * 前一个加载的页面，用来判断本次加载页面是否同前一次一样，避免重复收集。
	 * previous loaded activity stored to avoid to collect again
	 */
	private String mPrePageName;

	/**
	 * 数据发送接口。
	 * Object to send collected data.
	 */
	private DataSender mSender;

	/**
	 * 数据收集接口。
	 * Object to collect data.
	 */
	private DataCollector mCollector;

	/**
	 * 获取该页面View的层次栈。
	 * Stack used to store view
	 */
	private Stack<View> mViewStack;

	/**
	 * 初始页面。
	 * Collected view at initial time
	 */
	private View mInitialView;

	/**
	 * 判断Crash是否已经被发送，同一个Crash只可被发送一次。
	 * Boolean value used to ensure one crash one sending.
	 */
	private boolean isCrashHandlerSent = false;

	/**
	 * 判断CrashHandler是否已经初始化，避免重复初始化。
	 * Boolean value used to ensure CrashHandler has been initiated.
	 */
	private boolean isCrashHandlerStarted = false;

	/**
	 * 判断Url是否已经设值；
	 * Boolean value used to check whether the URL has been set;
	 */
	private boolean isUrlOptionSet = false;

	/**
	 * 判断Queen是否初始化
	 * Boolean value used to check whether Queen is initialized;
	 */
	private boolean isInited = false;

	/**
	 * 用于储存要规避的View；
	 */
	private ArrayList<View> mAvoidListView;

	/**
	 * 用于外部获取Queen状态的观察者
	 * Observer applied to
	 */
	private Observed mObserved;

	/**
	 * Queen必须为单例，一个应用只存在一个实例。
	 * @return Queen实例
	 */
	public static Queen getInstance(){
		if(sInstance == null){
			sInstance = new Queen();
		}
		return sInstance;
	}

	/**
	 * Queen初始化
	 *
	 * Initializaton of Queen
	 *
	 * sessionDomain 可选, 用于同后台数据进行匹配来确认用户请求了哪些数据。
	 * 注意：如果Queen没有成功初始化将影响到下列模块工作：
	 * 1. 后台数据采集
	 * 2. cookie数据采集
	 * 3. 数据发送
	 *
	 * sessionDomain is optional. it is applied to check which requests are required by users.
	 * Note that if Queen is not initialized successfully, modules below may not work normally.
	 * 1. back server data collection;
	 * 2. cookie collection;
	 * 3. data sending.
	 *
	 * @param sessionDomain cookie的域名 session domain of cookie
	 * @param application 布局内容 context of application
	 */
	public void init(String sessionDomain, Application application){
		try {
			if (null == application) {
				throw new UnInitException();
			}

			backgroundDataCollect(application);
			setDomain(sessionDomain);
			initUncaughtErrorMonitor(application);
			mSender = new DataSender(application);
			isInited = true;
		}catch (UnInitException e){
			Log.w(TAG, "Queen is not initialized", e);
		}
	}

	/**
	 * 设置Url，注意：如果没有提供明确的url，Queen将不发送收集的数据
	 *
	 * Set URL. Note that if URL was not set, Queen would never send collected data.
	 *
	 * @param url
	 */
	public void setUrl(@NonNull String url){
		try {
			if (!isInited || null == mSender) {
				throw new UnInitException();
			}
			if (null == url) {
				return;
			}

			mSender.setUrl(url);
			isUrlOptionSet = true;
		}catch (UnInitException e){
			Log.e(TAG, "Queen is not initialized");
		}
	}

	/**
	 * 设置RSA的public key， 注意：如果没有提供明确的public key，Queen将以明文方式发送数据
	 *
	 * Set Public key of RSA.Note that if public key is not available,
	 * Queen would send original data without encrypted
	 *
	 * @param publicKey
	 */
	public void setRSAPublicKey(@NonNull String publicKey){
		if(null == publicKey && "".equals(publicKey)){
			return;
		}

		RSAUtils.setPublicKey(publicKey);
	}

	/**
	 * 发送数据
	 * send data;
	 */
	public void sendData(){
		try {
			if (!isInited || null == mSender) {
				throw new UnInitException();
			}
			if (isUrlOptionSet) {
				mSender.sendData(mArray);
			} else {
				throw new NoUrlException("No URL Expected");
			}

		}catch (UnInitException e){
			Log.e(TAG, "Queen is not initialized");
		}catch(Exception e){
			Log.e(TAG, "sendData", e);
		}
	}

	/**
	 * 设置session Id 用于获取APP接收到的cookie来匹配后台数据
	 * Set session id, used to collect cookie requested by APP.
	 */
	public void setSessionId(List<HttpCookie> receivedCookies){
		try {
			if (!isInited || null == mSender) {
				throw new UnInitException();
			}
			mSender.setSessionId(receivedCookies);

		}catch (UnInitException e) {
			Log.e(TAG, "Queen is not initialized");
		}
	}

	/**
	 * 获取cookie域名, 用于从cookie中过滤出你需要的cookie;
	 * set Cookie's domain to filter cookie that you need;
	 *
	 * @param domain 域名
	 */
	public void setDomain(String domain){
		try {
			if (!isInited || null == mSender) {
				throw new UnInitException();
			}

			mSender.setDomain(domain);

		}catch (UnInitException e) {
			Log.e(TAG, "Queen is not initialized");
		}
	}

	/**
	 * 获取userId，用于识别该APP用户;
	 * get UserId, used to recognize user;
	 *
	 * @param userId
	 */
	public void setUserId(String userId){
		try {
			if (!isInited || null == mSender) {
				throw new UnInitException();
			}
			if(null != userId){
				mSender.setUserId(userId);
			}
		}catch (UnInitException e) {
			Log.e(TAG, "Queen is not initialized");
		}
	}

	/**
	 * 启动未捕获Exception监控;
	 * Monitor for uncaught Exception;
	 *
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
	 * app在退出前最后发送一段行为数据;
	 * Send the last data before exit;
	 *
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
	 * 用户交互数据收集
	 * User data obtaining(Button);
	 *
	 * @param buttonId button的标志; button mark;
	 * @param title button上的字面内容; button content;
	 * @param tag	button如果携带tag，收集tag; tag brought by button;
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
	 * User data obtaining(CheckBox);
	 *
	 * @param id checkbox的标志; mark;
	 */
	public void checkBoxCheckDataCollect(String id, String title, String tag, Context context) {
		synchronized(LOCK){
			mArray = mCollector.checkBoxCheckDataCollect(id, title, tag, context.toString(), mArray);
			bufferFullSend();
		}
	}

	/**
	 * 收集TextView的内容
	 * User data obtaining(TextView);
	 *
	 * @param id textView的标志; mark;
	 * @param title textView字面内容; title;
	 * @param tag	该textView tag; tag;
	 */
	public void textViewInfoDataCollect(String id, String title, String tag, Context context){
		synchronized(LOCK){
			mArray = mCollector.textViewInfoDataCollect(id, title, tag, context.toString(), mArray);
			bufferFullSend();
		}
	}

	/**
	 * 收集imageView的内容
	 * User data obtaining(ImageView);
	 *
	 * @param imageId imageView的标志; mark;
	 * @param title	imageView的标题; title;
	 * @param tag		imageView携带的tag; tag;
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
	 * Activity status obtaining
	 *
	 * @param activityName activity的标志;mark;
	 * @param activityTitle	activity的标题; title;
	 * @param activityTag	 activity的备注; tag;
	 * @param isOpen	activity的状态（打开和关闭）; status(open or closed);
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
	 * 监控APP是后台还是前台运行，用于监控APP的关闭与打开, 在用户看来,后台运行的APP同样处于关闭状态
	 * 所以,这里如果发现APP进入后台运行,Queen将看做APP关闭.
	 * To check whether this app is running on back ground or front ground. As user always make app
	 * run in back ground to close this APP, Queen take this status as the close of APP.
	 *
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
	 * 注册观察者
	 * register observer;
	 *
	 * @param watcher 观察者
	 */
	public void registerObserver(IQueenWatcher watcher){
		mObserved.registerObserver(watcher);
	}

	/**
	 * 取消观察者
	 * unregister observer;
	 *
	 * @param watcher 观察者
	 */
	public void unregisterObserver(IQueenWatcher watcher){
		mObserved.unregisterObserver(watcher);
	}

	/**
	 * 识别在View上所进行的动作
	 * Get the operation on the view.
	 *
	 * @param ev 动作; Operation;
	 * @param myView 执行动作的View; View on the screen;
	 * @param context
	 */
	public void recognizeViewEvent(MotionEvent ev, View myView, Context context){
		switch(ev.getAction()){
    	case MotionEvent.ACTION_DOWN:{
    		try{
    			mViewStack = new Stack<>();
    			final float pressX = ev.getRawX();
    			final float pressY = ev.getRawY();
    			findViewAtPosition(myView, (int)pressX, (int)pressY);
    			if(mViewStack.isEmpty()){
    				return;
    			}

    			mInitialView = ignoreView();
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
    			View view = ignoreView();
    			if(null == view){
    				return;
    			}
    			try{
					if(view instanceof CheckBox){
						CheckBox checkBox = (CheckBox)view;
						buttonPressDataCollect(checkBox.toString(), checkBox.isChecked() + "", (String) (checkBox.getTag()), context);
					}else if(view instanceof Button){
    					Button button = (Button)view;
    					buttonPressDataCollect(button.toString(), button.getText().toString(), (String) (button.getTag()), context);
    				}else if(view instanceof ImageView){
    					ImageView imageView = (ImageView)view;
    					imageViewPressDataCollect(imageView.toString(), null, null, context);
    				}else if(view instanceof TextView){
    					TextView text = (TextView)view;
						textViewInfoDataCollect(text.toString(), text.getText().toString(), (String) (text.getTag()), context);
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

	public void addAvoidView(View view){
		if(null == view || null == mAvoidListView){
			return;
		}

		mAvoidListView.add(view);
	}

	/**
	 * 忽略View，不搜集该View上的动作
	 * Ignore the view which is inserted in the avoided view list.
	 * @return
	 */
	private View ignoreView(){
		View view = mViewStack.pop();
		while(isAvoidView(view) && !mViewStack.isEmpty()){
			view = mViewStack.pop();
		}
		if(isAvoidView(view)){
			return null;
		}
		return view;
	}

	/**
	 * 判断View是否在avoid View list上
	 * Check whether the view is in the avoided view list;
	 *
	 * @param view
	 * @return
	 */
	private boolean isAvoidView(View view){
		Iterator<View> i = mAvoidListView.iterator();
		while (i.hasNext()){
			View avoidView = i.next();
			if(view == avoidView){
				return true;
			}
		}
		return false;
	}

	/**
	 * 通过用户动作的范围查找相应的View
	 * find view that the user interacts.
	 *
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
	 * Json列表满10条随即发送
	 * check whether the json array is full(10 pic)
	 */
	private void bufferFullSend(){
		if(10 <= mArray.length()){
			sendData();
			mObserved.notifyDataChanged("User Experience Log :" + mArray.toString());
			mArray = new JSONArray();
		}
	}

	/**
	 * Queen类初始化
	 * Initiation of Queen;
	 */
	private Queen(){
		mCollector = new DataCollector();
		mArray = new JSONArray();
		mViewStack = new Stack<View>();
		mAvoidListView = new ArrayList<>();
		mObserved = new Observed();
	}

}
