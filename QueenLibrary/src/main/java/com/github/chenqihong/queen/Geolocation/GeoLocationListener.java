package com.github.chenqihong.queen.Geolocation;

/**
 * 地理信息变化监听, 将数据传送给需要的实例;
 * Listener to listen the change of location data;
 *
 * Created by abby on 16/1/31.
 */
public interface GeoLocationListener <T>{
    /**
     * 接收到新的地理信息后调用
     * @param locationData 地理信息数据
     */
    void onReceivedLocation(T locationData);
}
