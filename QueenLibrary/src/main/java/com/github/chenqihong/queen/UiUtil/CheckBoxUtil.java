package com.github.chenqihong.queen.UiUtil;

import org.json.JSONObject;

/**
 * checkbox动作收集（暂不收集）
 */
public class CheckBoxUtil {
	public static JSONObject checkBoxInfoGenerated(String checkBoxId, boolean check){
		long time = System.currentTimeMillis();
		JSONObject object = null;
		try{
			object = new JSONObject();
			object.put("operTime", time);
			object.put("operType", "r");
			if(check){
				object.put("operAction", "s");
			}else{
				object.put("operAction", "c");
			}

			object.put("operId",checkBoxId);
			object.put("operVal", null);
		}catch(Exception e){
			
		}
		return object;
	}

}
