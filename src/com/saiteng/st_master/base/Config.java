package com.saiteng.st_master.base;

import com.saiteng.st_master.view.ST_InfoDialog;
import android.content.Context;
import android.os.Handler;

public class Config{
	public static String url                     = "http://192.168.0.63:8080/NA721/";
    public static Context mManagecontext         = null;
    public static Handler mhandler               = null;
    public static String diviceIMEI                = null;
    public static String divicenum                = null;
    public static String divicename                = null;
    public static Handler mTrackContext          = null;
    public static Handler mGenzongContext        = null;
    public static double mLatitude               = 0;  
    public static double mLongitude              = 0;
    public static double mGZLatitude             = 0;
    public static double mGZLongitude            = 0;
    public static String pwd                     = null;
    public static String username                = null;
    public static ST_InfoDialog InfoDialog       =null;
    public static String tacketime               =null;
    
    public static String ip = null;
    public static int port=20086;
 

    public static int SocketOk = 100;
    public static int Socketerror=101;
    public static int Socketlogin=102;

    public static boolean disconn = false;
    public static String imei = null;
    
    //短信控制wifi,gps开关命令
    public static String order_gpson = "pw,523681,gpssd,1#";
    public static String revice_gpson = "gpssd:1";
    public static String order_gpsoff = "pw,523681,gpssd,0#";
    public static String revice_gpsoff = "gpssd:1";
    
    public static String order_wifion = "pw,523681,wifi,1#";
    public static String revice_wifion = "wifi:1";
    public static String order_wifioff = "pw,523681,wifi,0#";
    public static String revice_wifioff = "wifi:1";
} 
