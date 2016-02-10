package com.github.chenqihong.queen.Device;

import android.app.Application;
import android.content.Context;

/**
 * Device 统一接口
 * Created by ChenQihong on 2016/1/29.
 */
public class Device {
    private Context mContext;
    private static Device mInstance;

    /**
     * Device 单例
     * Instance for Device
     * @param application
     * @return
     */
    public static Device getInstance(Application application){
        if(null == mInstance) {
            mInstance = new Device(application);
        }

        return mInstance;
    }

    /**
     * 初始化
     * @param context
     */
    private Device(Context context){
        mContext = context;
        DeviceUtils.registerBatteryReceiver(context);
    }

    /**
     * 获取MAC地址
     * @return
     */
    public String getMacAddress(){
        return com.github.chenqihong.queen.Device.DeviceUtils.getMacAddress(mContext);
    }

    /**
     * 获取IMEI
     * @return
     */
    public String getImei(){
        return DeviceUtils.getDeviceIMEI(mContext);
    }

    /**
     * 获取IMSI
     * @return
     */
    public String getImsi(){
        return DeviceUtils.getDeviceIMSI(mContext);
    }

    /**
     * 获取Device model;
     * @return
     */
    public String getDeviceModel(){
        return DeviceUtils.getDeviceModel();
    }

    /**
     * 获取手机生产商
     * @return
     */
    public String getManufacturer(){
        return DeviceUtils.getManufacturer();
    }

    /**
     * 获取屏幕宽度
     * @return
     */
    public int getScreenWidth(){
        return DeviceUtils.getScreenWidth(mContext);
    }

    /**
     * 获取屏幕高度
     * @return
     */
    public int getScreenHeight(){
        return DeviceUtils.getScreenHeight(mContext);
    }

    /**
     * 获取APP版本名字
     * @return
     */
    public String getAppVersionName(){
        return DeviceUtils.getAppVersionName(mContext);
    }

    /**
     * 获取APP版本号
     * @return
     */
    public int getAppVersionCode(){
        return DeviceUtils.getAppVersionCode(mContext);
    }

    /**
     * 获取Queen ID(Device ID)
     * get Queen ID;
     *
     * @return
     */
    public String getDeviceId(){
        return DeviceUtils.getAppVersionName(mContext);
    }

    /**
     * 获取网络类型
     * @return
     */
    public String getNetworkType(){
        return DeviceUtils.getNetworkType(mContext);
    }

    /**
     * 获取本地IP地址
     * @return
     */
    public String getLocalIpAddress(){
        return DeviceUtils.getLocalIpAddress(mContext);
    }

    /**
     * 获取Android ID
     * @return
     */
    public String getAndroidId(){
        return DeviceUtils.getAndroidId(mContext);
    }

    /**
     * 获取电话号码
     * @return
     */
    public String getPhoneNo(){
        return DeviceUtils.getPhoneNo(mContext);
    }

    /**
     * 获取APP名字
     * @return
     */
    public String getAppName(){
        return DeviceUtils.getAppPackageName(mContext);
    }

    /**
     * 获取平台名称
     * @return
     */
    public String getPlatform(){
        return DeviceUtils.getPlatform();
    }

    /**
     * 获取基站定位信息:CID,LAC等
     * @return
     */
    public String getCellLocation(){
        return DeviceUtils.getCellLocation(mContext);
    }

    /**
     * 获取模拟器信息
     * @return
     */
    public boolean isEmulator(){
        return DeviceUtils.isEmulator(mContext);
    }

    /**
     * 获取操作系统版本号
     * @return
     */
    public String getOsVersion(){
        return DeviceUtils.getOsVersion();
    }

    /**
     * 获取Phone model
     * @return
     */
    public String getPhoneModel(){
        return DeviceUtils.getDeviceModel();
    }

    /**
     * 获取OS类型
     * @return
     */
    public String getOsType(){
        return DeviceUtils.getPlatform();
    }

    /**
     * 获取电量信息
     * @return
     */
    public String getBatteryLevel(){
        return DeviceUtils.getBatteryLevel() + "";
    }

    /**
     * 获取Tx流量
     * @return
     */
    public long getTxTraffic(){
        return TrafficUtil.txBytesInfoGenerate();
    }

    /**
     * 获取Rx流量
     * @return
     */
    public long getRxTraffic(){
        return TrafficUtil.rxBytesInfoGenerate();
    }
}
