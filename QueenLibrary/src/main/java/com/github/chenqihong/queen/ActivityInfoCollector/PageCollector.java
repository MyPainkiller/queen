package com.github.chenqihong.queen.ActivityInfoCollector;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

/**
 * activity页面行为收集
 */
public class PageCollector {
	private static final String TAG = "PageCollector";
	private String mPageName;
	private String mTitle;
	private String mTag;
	private static PageCollector mInstance;

	/**
	 * 单例
	 * @return 单例
	 */
	public static PageCollector getInstance(){
		if(null == mInstance){
			mInstance = new PageCollector();
		}
		return mInstance;
	}

	/**
	 * activity打开
	 * @param pageId 标识
	 * @param title 标题
	 * @param tag 备注
	 * @return 动作元素
	 */
	public JSONObject pageOpenInfoGenerated(String pageId, String title, String tag){
		if(null == pageId && null == title && null == tag){
			return null;
		}
		long time = System.currentTimeMillis();
		JSONObject object = null;
		try{
			object = new JSONObject();
			object.put("tm", time);
			object.put("ac", "PO");
			mPageName = pageId;
			mTitle = title;
			mTag = tag;
			if(null != pageId){
				pageId = pageId.substring(pageId.lastIndexOf(".")+1, pageId.indexOf("@"));
				object.put("ta",pageId);
			}

			if(null != title){
				object.put("ti", title);
			}

			if(null != tag){
				object.put("tg", tag);
			}

		}catch(Exception e){
			Log.e(TAG, "pageOpenInfoGenerated: unknown error");
		}
		return object;
	}

	public String getPageName(){
		return mPageName;
	}
	
	public String getPageTitle(){
		return mTitle;
	}
	
	public String getPageTag(){
		return mTag;
	}

	/**
	 * activity关闭
	 * @param pageId 标识
	 * @param title 标题
	 * @param tag 备注
	 * @return 动作元素
	 */
	public JSONObject pageCloseInfoGenerated(String pageId, String title, String tag){
		if(null == pageId && null == title && null == tag){
			return null;
		}
		long time = System.currentTimeMillis();
		JSONObject object = null;
		try{
			object = new JSONObject();
			object.put("tm", time);
			object.put("ac", "PC");
			if(null != pageId){
				pageId = pageId.substring(pageId.lastIndexOf(".")+1, pageId.indexOf("@"));
				object.put("ta",pageId);
			}

			if(null != title){
				object.put("ti", title);
			}

			if(null != tag){
				object.put("tg", tag);
			}
		}catch(Exception e){
			
		}
		return object;
	}

	/**
	 * app关闭动作搜集
	 * @param context
	 * @return 动作元素
	 */
	public JSONObject appCloseEventGeneration(Context context){
		try{
			String appName = context.getPackageName();
			JSONObject object = new JSONObject();
			object.put("tm", System.currentTimeMillis());
			object.put("ta", appName);
			object.put("ac", "AC");
			return object;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	private PageCollector(){

	}

}
