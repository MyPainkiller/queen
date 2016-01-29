package com.github.chenqihong.queen.DeviceInfoCollector;

import android.net.TrafficStats;

/**
 * 流量收集
 */
public class TrafficUtil {

	/**
	 * 获取app上传流量
	 * @return
	 */
	public static long txBytesInfoGenerate(){
		return TrafficStats.getUidTxBytes(android.os.Process.myUid());
	}

	/**
	 * 获取app接收流量
	 * @return
	 */
	public static long rxBytesInfoGenerate(){
		return TrafficStats.getUidRxBytes(android.os.Process.myUid());
	}

}
