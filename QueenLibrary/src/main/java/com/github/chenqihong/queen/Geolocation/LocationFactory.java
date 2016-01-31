package com.github.chenqihong.queen.Geolocation;

import android.content.Context;

import java.util.Objects;

/**
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
