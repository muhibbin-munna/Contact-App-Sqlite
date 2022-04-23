package com.app.contactappsp.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class NetUtils {
    public static String policy_link = "https://www.google.com/";  //Replace with your privacy policy link...

    public static boolean isOnline(Context ctx) {
        NetworkInfo netInfo = ((ConnectivityManager) ctx.getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }


}