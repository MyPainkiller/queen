package com.github.chenqihong.queen.Geolocation;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * 调用Google 定位SDK实现定位
 * Make location instance using Google Location SDK.
 *
 * Created by abby on 16/1/31.
 */
public class Geolocation extends ILocation {
    private LocationListener mListener;
    private LocationManager mLocationManager;
    private Location mLocation;
    private String mProvider;

    /**
     * Initialize
     * @param context
     */
    public Geolocation(Context context){
        mLocationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setCostAllowed(true);
        criteria.setAltitudeRequired(false);
        criteria.setAltitudeRequired(false);
        mProvider = mLocationManager.getBestProvider(criteria, true);
    }

    /**
     * 获取定位信息一次,之后立刻停止定位.防止过多性能消耗.
     * Get Location once and stop at once after the data got.
     * @param listener 获取信息变化监听
     */
    @Override
    public void getLocationOnce(final GeoLocationListener listener){
        //noinspection ResourceType
        mListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                listener.onReceivedLocation(location);
                mLocationManager.removeUpdates(mListener);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        if(null != mProvider) {
            mLocationManager.requestLocationUpdates(mProvider, 3000, 10, mListener);
        }
    }
}
