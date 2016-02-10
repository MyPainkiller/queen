package com.github.chenqihong.queen.Geolocation;

/**
 * 获取一次地理位置的接口
 * Interface to get location once.
 *
 * Created by abby on 16/1/31.
 */
public abstract class ILocation {
    public abstract void getLocationOnce(GeoLocationListener listener);

}
