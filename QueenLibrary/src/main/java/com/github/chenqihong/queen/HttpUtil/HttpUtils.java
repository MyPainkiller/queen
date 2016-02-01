package com.github.chenqihong.queen.HttpUtil;

import android.util.Log;

import com.github.chenqihong.queen.Base.AESUtils;
import com.github.chenqihong.queen.Base.RSAUtils;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.zip.GZIPOutputStream;

import static com.squareup.okhttp.MediaType.parse;

/**
 * HttpPost
 * @author ChenQihong
 *
 */

public class HttpUtils {

	private static String TAG = "HttpUtils";
	/**
	 * post
	 * @param url 后台地址
	 * @param params 参数封装
	 * @param array 动作列表
	 * @return 发送返回结果
	 * @throws Exception 错误
	 */
	public static String sendPost(String url,
			HashMap<String, Object> params, JSONArray array) throws Exception {
		
		String data = null;
		JSONObject paramObj = new JSONObject();
		if (params != null && !params.isEmpty()) {
			for (HashMap.Entry<String, Object> entry : params.entrySet()) {
				paramObj.put(entry.getKey(), entry.getValue());
			}
		}
		paramObj.put("ca", array);
		try{
			//Log.e("original", "original:" + paramObj.toString());
			byte[] finalEncodeData = null;
			if(RSAUtils.hasPublicKey()) {
				byte[] seed = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 16).getBytes();
				finalEncodeData = AESUtils.encrypt(seed, gzip(paramObj.toString().getBytes()));
			}else {
				finalEncodeData = gzip(paramObj.toString().getBytes());
			}

			OkHttpClient client = new OkHttpClient();
			RequestBody body = RequestBody.create(parse("application/oct-stream"), finalEncodeData);
			final Request request = new Request.Builder().url(url).post(body).build();
			client.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Request request, IOException e) {
					//不做任何处理
				}

				@Override
				public void onResponse(Response response) throws IOException {
					//不做任何处理
				}
			});
			

		}catch(Exception e){
			//此处不能使用MuLog，否则会产生循环
			Log.e(TAG, "Error: sendPost", e);
		}
		
		return data;
		
	}

	/**
	 * gzip压缩
	 * @param val 原始数据
	 * @return 压缩后的bytes数据
	 * @throws IOException 错误
	 */
	private static byte[] gzip(byte[] val) throws IOException {
		  ByteArrayOutputStream bos = new ByteArrayOutputStream(val.length);
		  GZIPOutputStream gos = null;
		  try {  
		   gos = new GZIPOutputStream(bos);
		   gos.write(val, 0, val.length);  
		   gos.finish();  
		   val = bos.toByteArray();  
		   bos.flush();  
		  } finally {  
			 if (gos != null){
				 //gos已经finish，不做任何处理
			 }
			 if (bos != null)  {
				 bos.close();
			 }
		  }  
		  return val;
	}


}