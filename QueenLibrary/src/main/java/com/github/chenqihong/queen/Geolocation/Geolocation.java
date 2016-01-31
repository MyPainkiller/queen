package com.github.chenqihong.queen.Geolocation;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by abby on 16/1/31.
 */
public class Geolocation extends ILocation {
    private LocationListener mListener;
    private LocationManager mLocationManager;
    private Location mLocation;
    private String mProvider;

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
        mLocationManager.requestLocationUpdates(mProvider, 3000, 10, mListener);
    }
}
