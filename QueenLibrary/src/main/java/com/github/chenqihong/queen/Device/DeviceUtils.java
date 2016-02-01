package com.github.chenqihong.queen.Device;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.UUID;


/**
 * Created by ChenQihong on 2016/1/29.
 */
public class DeviceUtils {
    private static final String TAG = "DeviceUtil";

    private static final String SHAREDPREF_NAME = "device";
    private static final String SHAREDPREF_KEY = "deviceId";
    private static int sBatteryLevel = 0;

    public static boolean checkPermission(Context context, String permName){
        return context.getPackageManager().checkPermission(permName, context.getPackageName())
                == PackageManager.PERMISSION_GRANTED;
    }
    public static String getMacAddress(Context context){
        String macAddress = null;
        if(!checkPermission(context, Manifest.permission.ACCESS_WIFI_STATE)){
            return macAddress;
        }

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if(null == wifiManager){
            return macAddress;
        }

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if(null == wifiInfo){
            return macAddress;
        }

        macAddress = wifiInfo.getMacAddress();
        return macAddress.toUpperCase();
    }

    public static String getDeviceIMEI(Context context){
        String imei = null;
        if(!checkPermission(context, Manifest.permission.READ_PHONE_STATE)){
            return imei;
        }

        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        if(null == telephonyManager){
            return imei;
        }

        imei = telephonyManager.getDeviceId();
        return imei.toUpperCase();
    }

    public static String getDeviceIMSI(Context context){
        String imsi = null;
        if (!checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
            return imsi;
        }

        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        if(null == telephonyManager){
            return imsi;
        }

        imsi = telephonyManager.getSubscriberId();
        return imsi.toUpperCase();
    }

    public static String getDeviceModel(){
        return Build.MODEL;
    }

    public static String getManufacturer(){
        return Build.MANUFACTURER;
    }

    public static int getScreenHeight(Context context){
        DisplayMetrics displayMetrics =context.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    public static int getScreenWidth(Context context){
        DisplayMetrics displayMetrics =context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    public static String getAppVersionName(Context context){
        String versionName = "";
        PackageManager packageManager = context.getPackageManager();
        try{
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        }catch (PackageManager.NameNotFoundException e){
            Log.e(TAG, "Cannot get app version name", e);
        }
        return versionName;
    }

    public static int getAppVersionCode(Context context){
        int versionCode = -1;
        PackageManager packageManager = context.getPackageManager();
        try{
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        }catch (PackageManager.NameNotFoundException e){
            Log.e(TAG, "Cannot get app version name", e);
        }
        return versionCode;
    }

    public static String getDeviceId(Context context){
        String deviceId = null;

        deviceId = loadDeviceId(context);
        if(null == deviceId){
            deviceId = generateDeviceId();
        }

        saveDeviceId(deviceId, context);
        return deviceId;
    }

    public static String getNetworkType(Context context){

        if(!isNetworkConnected(context)){
            return "NONE";
        }

        ConnectivityManager connectivityManager = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if(null == connectivityManager){
            return null;
        }
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(ConnectivityManager.TYPE_WIFI == networkInfo.getType()){
            return "WIFI";
        }

        if(!checkPermission(context, Manifest.permission.READ_PHONE_STATE)){
            return null;
        }

        TelephonyManager telephonyManager = (TelephonyManager)context
                .getSystemService(Context.TELEPHONY_SERVICE);

        if(null == telephonyManager){
            return null;
        }

        switch (telephonyManager.getNetworkType()){
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "MOBILE-2G";
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "MOBILE-3G";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "MOBILE-4G";
            default:
                return "MOBILE-UNKNOWN";
        }
    }

    public static String getLocalIpAddress(Context context){
        if(!checkPermission(context, Manifest.permission.INTERNET)){
            return null;
        }

        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            if (en == null) {
                return "";
            }

            while (en.hasMoreElements()) {
                NetworkInterface intf = en.nextElement();
                Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();

                while (enumIpAddr.hasMoreElements()) {
                    InetAddress inetAddress = enumIpAddr.nextElement();

                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Cannot get local ipaddress", e);
        }

        return null;
    }

    public static String getOsVersion() {
        return Build.VERSION.RELEASE;
    }

    public static int getBatteryLevel(){
        return sBatteryLevel;
    }

    /**
     * 获取包名
     */
    public static String getAppPackageName(Context context) {
        return context.getPackageName();
    }

    public static String getPlatform() {
        return "Android";
    }

    private static boolean isNetworkConnected(Context context){
        boolean isNetworkConnected = false;

        if(!checkPermission(context, Manifest.permission.ACCESS_NETWORK_STATE)){
            return false;
        }

        ConnectivityManager connectivityManager = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if(null == connectivityManager){
            return false;
        }
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (null != networkInfo && networkInfo.isConnected());
    }

    public static String getAndroidId(Context context) {

        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return (androidId == null ? "" : androidId).toUpperCase();
    }

    public static String getPhoneNo(Context context) {

        if (!checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
            return "";
        }

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null) {
            return "";
        }

        String phoneNo = tm.getLine1Number();
        return phoneNo == null ? "" : phoneNo;
    }

    public static boolean isEmulator(Context context) {

        if (Build.MODEL.equals("sdk") || Build.MODEL.equals("google_sdk")) {
            return true;
        }

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null) {
            return false;
        }
        String imei = tm.getDeviceId();
        if ((imei != null && imei.equals("000000000000000"))) {
            return true;
        }

        return false;
    }

    public static String getCellLocation(Context context) {

        if (!checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
            return "";
        }

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null) {
            return "";
        }

        CellLocation cellLocation = tm.getCellLocation();
        if (cellLocation == null) {
            return "";
        }
        return cellLocation.toString();
    }


    private static String generateDeviceId(){
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    private static void saveDeviceId(String deviceId, Context context){
        SharedPreferences preferences =
                context.getSharedPreferences(SHAREDPREF_NAME, Context.MODE_PRIVATE);

        preferences.edit().putString(SHAREDPREF_KEY, deviceId);
        preferences.edit().commit();

    }

    private static String loadDeviceId(Context context){
        SharedPreferences preferences =
                context.getSharedPreferences(SHAREDPREF_NAME, Context.MODE_PRIVATE);

        return preferences.getString(SHAREDPREF_KEY, null);
    }

    /**
     * 注册电池监听广播
     */
    public static void registerBatteryReceiver(Context context) {

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);

        BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                sBatteryLevel = (int) (100f * intent
                        .getIntExtra(BatteryManager.EXTRA_LEVEL, 0) / intent.getIntExtra
                        (BatteryManager.EXTRA_SCALE, 100));
            }
        };
        context.registerReceiver(batteryReceiver, filter);
    }


}
