package com.github.chenqihong.queen.CrashCollector;


import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Process;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 崩溃信息处理;
 * Self-defined crash handler.
 *
 * Created by Chen Qihong on 15/10/20.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";
    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;
    private OnUnCaughtExceptionListener mListener;
    private Context mContext;

    /**
     * 监听器接口
     * listener to listen the crash data.
     */
    public interface OnUnCaughtExceptionListener{

        /**
         * 获取exception信息
         * @param object exception元素
         */
        void onException(JSONObject object);
    }

    /**
     * CrashHandler初始化
     * Initialize of crash handler.
     *
     * @param context
     * @param onUnCaughtExceptionListener 监听器
     */
    public CrashHandler(Context context, OnUnCaughtExceptionListener onUnCaughtExceptionListener){
        mContext = context;
        mListener = onUnCaughtExceptionListener;
    }

    /**
     * 初始化，获取线程默认CrashHandler，并将本handler注册为默认handler
     * Get the default crash handler and register our handler to the default one.
     */
    public void init(){
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);

    }

    /**
     * 捕获未处理Exception后进行处理，并崩溃.这里我们将主线程等待1分钟的时间以便让数据传送出去
     * Handle the exception caught.
     * we get the data and make the main thread to wait 1 min for data
     * sending.
     *
     * @param thread 线程
     * @param ex 未捕获Exception
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        try {
        	long time = System.currentTimeMillis();

        	JSONObject object = new JSONObject();
        	try {
        		object.put("tm",time);
        		object.put("ta", mContext
                        .getPackageManager()
                        .getPackageInfo(mContext.getPackageName(), 0).packageName);

        		object.put("ac", "AE");
        		Throwable cause = ex.getCause();
        		StringWriter writer = new StringWriter();
        		PrintWriter printWriter = new PrintWriter(writer);
        		while(null != cause){
        			cause.printStackTrace(printWriter);
        			cause = cause.getCause();
        		}

        		object.put("er", writer.toString());
        	} catch (JSONException e) {
                Log.e(TAG, "CrashHandler: JSONException");
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "CrashHandler: PackageManager.NameNotFoundException");
        	}

        	mListener.onException(object);
            Thread.sleep(1000);//此处并不需要保证数据传送完成, 能传便传,不传便放弃.
            if(mDefaultCrashHandler != null){
            	mDefaultCrashHandler.uncaughtException(thread,ex);
            }else{
                Process.killProcess(Process.myPid());
            }

        } catch (Exception e) {
            Log.e(TAG, "CrashHandler: Unknow Error");
        }

    }
}
