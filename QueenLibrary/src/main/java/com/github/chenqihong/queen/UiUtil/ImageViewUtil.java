package com.github.chenqihong.queen.UiUtil;

import android.util.Log;

import org.json.JSONObject;

/**
 * ImageView动作封装
 */
public class ImageViewUtil {
	private static final String TAG = "ImageViewUtil";
	/**
	 * ImageView动作封装
	 * @param target 标识
	 * @param title 标题
	 * @param tag	备注
	 * @param activityName 所在activity
	 * @return 动作元素
	 */
	public static JSONObject imageViewInfoGenerated(String target, String title, String tag, String activityName){
		long time = System.currentTimeMillis();
		JSONObject object = null;
		try{
			object = new JSONObject();
			object.put("tm", time);
			if(null != title){
				object.put("ti", title);
			}
			
			object.put("ac", "BC");
			
			if(null != target){
				String[] splittedStr = target.split("app:id/");
				if(null != splittedStr && 2 <= splittedStr.length){
					int length = splittedStr[1].length();
					String tarStr = splittedStr[1].substring(0, length-1);
					if(null != tarStr){
						object.put("ta", tarStr);
					}
				}else{
					object.put("ta", target);
				}
			}
			
			if(null != tag){
				object.put("tg", tag);
			}
			
			if(null != activityName){
				activityName = activityName.substring(activityName.lastIndexOf(".")+1, activityName.indexOf("@"));
				object.put("pi", activityName);
			}
		}catch(Exception e){
			Log.e(TAG, "imageViewInfoGenerated: unknown error");
		}
		return object;
	}

}
