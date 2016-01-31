package com.github.chenqihong.queen.Device;

import android.content.Context;

/**
 * Created by ChenQihong on 2016/1/29.
 */
public class Device {
    private Context mContext;
    private static Device mInstance;

    public static Device getInstance(Context context){
        if(null == mInstance) {
            mInstance = new Device(context);
        }

        return mInstance;
    }

    private Device(Context context){
        mContext = context;
    }

    public String getMacAddress(){
        return com.github.chenqihong.queen.Device.DeviceUtils.getMacAddress(mContext);
    }

    public String getImei(){
        return DeviceUtils.getDeviceIMEI(mContext);
    }

    public String getImsi(){
        return DeviceUtils.getDeviceIMSI(mContext);
    }

    public String getDeviceModel(){
        return DeviceUtils.getDeviceModel();
    }

    public String getManufacturer(){
        return DeviceUtils.getManufacturer();
    }

    public int getScreenWidth(){
        return DeviceUtils.getScreenWidth(mContext);
    }

    public int getScreenHeight(){
        return DeviceUtils.getScreenHeight(mContext);
    }

    public String getAppVersionName(){
        return DeviceUtils.getAppVersionName(mContext);
    }

    public int getAppVersionCode(){
        return DeviceUtils.getAppVersionCode(mContext);
    }

    public String getDeviceId(){
        return DeviceUtils.getAppVersionName(mContext);
    }

    public String getNetworkType(){
        return DeviceUtils.getNetworkType(mContext);
    }

    public String getLocalIpAddress(){
        return DeviceUtils.getLocalIpAddress(mContext);
    }

    public String getAndroidId(){
        return DeviceUtils.getAndroidId(mContext);
    }

    public String getPhoneNo(){
        return DeviceUtils.getPhoneNo(mContext);
    }

    public String getAppName(){
        return DeviceUtils.getAppPackageName(mContext);
    }

    public String getPlatform(){
        return DeviceUtils.getPlatform();
    }

    public String getCellLocation(){
        return DeviceUtils.getCellLocation(mContext);
    }

    public boolean isEmulator(){
        return DeviceUtils.isEmulator(mContext);
    }

    public String getOsVersion(){
        return DeviceUtils.getOsVersion();
    }

    public String getPhoneModel(){
        return null;
    }

    public String getOsType(){
        return null;
    }
}
