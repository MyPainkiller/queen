package com.github.chenqihong.queen.UiUtil;

import org.json.JSONObject;

public class TextViewUtil {
	public static int OPERATION_TEXT = 0;
	public static int OPERATION_PASSWORD = 1;
	public static JSONObject textViewInfoGenerated(String textViewId, String textViewValue, int operationType){
		//SimpleDateFormat format = new SimpleDateFormat("");
		long time = System.currentTimeMillis();
		JSONObject object = null;
		try{
			object = new JSONObject();
			object.put("operTime", time);
			if(OPERATION_PASSWORD == operationType){
				object.put("operType", "w");
			}else {
				object.put("operType", "t");
			}
			object.put("operAction", "o");
			object.put("operId",textViewId);
			object.put("operVal", textViewValue);
		}catch(Exception e){
			
		}
		return object;
	}

}
