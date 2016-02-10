package com.github.chenqihong.queen.Geolocation;

import android.content.Context;

import java.util.Objects;

/**
 * 使用Location工厂类构建location用例.
 * 此处预留给不同的定位服务提供商如:google, 百度, 腾讯等.
 *
 * Here we use a factory to build location instance, and left position for
 * diverse location SDK providers, i.e. Google, Baidu, Tencent.
 *
 * Created by abby on 16/1/31.
 */
public class LocationFactory {
    public static final int TYPE_GOOGLE_GEO = 0;

    public static ILocation creator(int which, Context context){
        if(TYPE_GOOGLE_GEO == which){
            return new Geolocation(context);
        }
        return null;
    }

}
