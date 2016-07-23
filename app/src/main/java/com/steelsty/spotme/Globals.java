package com.steelsty.spotme;

import android.content.Context;
import android.os.Build;

/**
 * Created by steelsty on 15/07/16.
 */

public class Globals {
    public static int mcc=404,mnc=46,cid=20792,lac=20081;
    public static int phone=0;
    public static int loc=0;
    public static double lat=0.0;
    public static String city="";
    public static String state="";
    public static String templat="",templng="";
    public static String place="";
    public static double lng=0.0;
    public static String address="";
    public static boolean check(Context c){
        boolean tg = c.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.M &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
        return tg;
    }
    public static boolean isset(){
        if(phone==0 || loc==0)
            return false;
        else
            return true;
    }
}
