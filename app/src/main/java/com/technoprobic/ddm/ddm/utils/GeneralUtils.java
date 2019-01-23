package com.technoprobic.ddm.ddm.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import com.technoprobic.ddm.ddm.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GeneralUtils {

    public static final String userPreferenceFile = "user_preference_file";

    public static String getFormattedDateFromMillis(long timeInMillis) {
        Date date = new Date(timeInMillis);
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        String dateFormatted = formatter.format(date);

        return dateFormatted;
    }

    public static boolean isConnectedToNetwork(Context context) {

        boolean isConnected = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            isConnected = true;
        } else {
            isConnected = false;
        }

        return isConnected;
    }

    public static boolean isIotaAddressAvailable(Context context) {
        boolean iotaAddressAvailable;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String iotaSeed = sharedPreferences.getString( context.getResources().getString(R.string.settings_iota_seed_key), "" );

        if ( iotaSeed.equals("") ) {
            iotaAddressAvailable = false;
        } else {
            iotaAddressAvailable = true;
        }

        return iotaAddressAvailable;
    }



}
