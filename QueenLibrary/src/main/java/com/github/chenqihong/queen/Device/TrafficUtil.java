package com.github.chenqihong.queen.Device;

import android.net.TrafficStats;

/**
 * 流量收集
 *
 * Traffic used in this APP.
 */
public class TrafficUtil {

	/**
	 * 获取app上传流量
	 * Get the tx traffic of this APP;
	 * @return
	 */
	public static long txBytesInfoGenerate(){
		return TrafficStats.getUidTxBytes(android.os.Process.myUid());
	}

	/**
	 * 获取app接收流量
	 * Get the rx traffic of this APP;
	 * @return
	 */
	public static long rxBytesInfoGenerate(){
		return TrafficStats.getUidRxBytes(android.os.Process.myUid());
	}

}
