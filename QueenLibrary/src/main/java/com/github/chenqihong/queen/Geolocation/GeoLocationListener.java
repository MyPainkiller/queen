package com.github.chenqihong.queen.Geolocation;

/**
 * Created by abby on 16/1/31.
 */
public interface GeoLocationListener <T>{
    void onReceivedLocation(T locationData);
}
