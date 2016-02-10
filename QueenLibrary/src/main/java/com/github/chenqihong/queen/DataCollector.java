package com.github.chenqihong.queen;

import android.content.Context;
import android.util.Log;

import com.github.chenqihong.queen.ActivityInfoCollector.BackgroundMonitor;
import com.github.chenqihong.queen.ActivityInfoCollector.PageCollector;
import com.github.chenqihong.queen.UiUtil.ButtonUtil;
import com.github.chenqihong.queen.UiUtil.CheckBoxUtil;
import com.github.chenqihong.queen.UiUtil.ImageViewUtil;
import com.github.chenqihong.queen.UiUtil.TextViewUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * 行为数据捕捉接口
 * Data Collection Instance;
 *
 */
public class DataCollector {
	private static final String TAG = "DataCollector";
	public DataCollector(){
	}

	/**
	 * Json列表插入
	 * Json Insertion
	 *
	 * @param object Json元素 Json Object;
	 * @param array Json列表 Json Array;
	 * @return 返回插好的Json列表 Inserted Json Array;
	 */
    private JSONArray InsertJSONObject(JSONObject object, JSONArray array){
    	if(null != object){
    		return array.put(object);
    	}else{
    		return array;
    	}
    }

	/**
	 * button点击行为收集
	 * button clicked data
	 *
	 * @param target button标志 the button mark;
	 * @param title button字面标题 info in the button;
	 * @param tag	button携带的tag the tag brought;
	 * @param activityName  button 所在activity activity which the button in;
	 * @param array 待插入的json列表 Json Array;
	 * @return 插好的json列表 Finished Json Array;
	 */
	public JSONArray buttonPressDataCollect(String target, String title, String tag, String activityName, JSONArray array) {
		JSONObject object = ButtonUtil.buttonInfoGenerated(target, title, tag, activityName);
		return InsertJSONObject(object, array);
	}

	/**
	 * imageView点击动作收集
	 * @param target	ImageView标志
	 * @param title	ImageView标题
	 * @param tag		ImageView携带的tag
	 * @param activityName	ImageView所在的Activity
	 * @param array	待插入的jsonArray
	 * @return 插好的jsonArray
	 */
	public JSONArray imageViewPressDataCollect(String target, String title, String tag, String activityName, JSONArray array) {
		JSONObject object = ImageViewUtil.imageViewInfoGenerated(target, title, tag, activityName);
		return InsertJSONObject(object, array);
	}

	/**
	 * CheckBox点击动作收集
	 * @param target CheckBox标志
	 * @param title CheckBox字面标题
	 * @param tag	CheckBox携带的tag
	 * @param activityName CheckBox所在的activity
	 * @param array 待插入的json列表
	 * @return 插好的json列表
	 */
	public JSONArray checkBoxCheckDataCollect(String target, String title, String tag, String activityName, JSONArray array) {
		JSONObject object = CheckBoxUtil.checkBoxClickInfoGeneration(target, title, tag, activityName);
		return InsertJSONObject(object, array);
	}

	/**
	 * TextView动作收集
	 * TextView clicked data
	 *
	 * @param target TextView标志; mark;
	 * @param title TextView字面标题; title;
	 * @param tag	TextView携带的tag; tag;
	 * @param activityName TextView所在的activity; activity
	 * @param array 待插入的json列表; Json Array;
	 * @return 插好的json列表; Finished Json Array
	 */
	public JSONArray textViewInfoDataCollect(String target, String title, String tag, String activityName, JSONArray array){
		JSONObject object = TextViewUtil.textViewInfoGenerated(target, title, tag, activityName);
		return InsertJSONObject(object, array);
	}

	/**
	 * activity打开状态收集
	 * activity open data
	 *
	 * @param array 待插入的Json列表
	 * @param activityName	activity标志
	 * @param activityTitle activity标题
	 * @param activityTag activity的备注
	 * @return 插好的Json列表
	 */
	public JSONArray activityOpenDataCollect(JSONArray array, String activityName, String activityTitle, String activityTag) {
		JSONObject object = PageCollector.getInstance().pageOpenInfoGenerated(activityName, activityTitle, activityTag);
		return InsertJSONObject(object, array);		
	}

	/**
	 * activity关闭状态收集
	 * @param array 待插入的Json列表
	 * @param activityName
	 * @param activityTitle
	 * @param activityTag
	 * @return 插好的Json列表
	 */
	public JSONArray activityCloseDataCollect(JSONArray array, String activityName, String activityTitle, String activityTag) {
		JSONObject object = PageCollector.getInstance().pageCloseInfoGenerated(activityName, activityTitle, activityTag);
		return InsertJSONObject(object, array);
	}

	/**
	 * 前后台监控启动
	 * @param context
	 * @param listener 监听器
	 */
	public void backgroundDataCollect(Context context, BackgroundMonitor.OnAppStatusChangeListener listener) {
		BackgroundMonitor monitor = BackgroundMonitor.getInstance();
		monitor.setAppStatusChangeListener(listener);	
		monitor.startBackgoundInfoCollector(context);
	}

	/**
	 * 插入crash事件，搜集crash
	 * @param object crash事件
	 * @param array 待插入的Json列表
	 * @return 插好的Json列表
	 */
	public JSONArray insertCrashHandler(JSONObject object, JSONArray array){
		return InsertJSONObject(object, array);
	}

	/**
	 * 退出APP事件收集
 	 * @param context
	 * @param array 待插入的Json列表
	 * @return 插好的Json列表
	 */
	public JSONArray appExitLogHandle(Context context, JSONArray array){
		JSONObject object = PageCollector.getInstance().appCloseEventGeneration(context);
		return InsertJSONObject(object, array);
	}


}
