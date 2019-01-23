package com.technoprobic.ddm.ddm.utils;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.technoprobic.ddm.ddm.R;

// todo - refactor from intentservice
public class BuyerListenerServerService extends IntentService {

    private static final String TAG = BuyerListenerServerService.class.getSimpleName();

    private static final String THREAD_NAME = "BuyerListenerServerServiceThread";

    public static final String BROADCAST_SERVER_START_STOP = "com.technoprobic.ddm.startStopServer";
    public static final String BROADCAST_USER_MESSAGE_KEY = "broadcast_user_message";

    public static final String BROADCAST_DOWNLOAD = "com.technoprobic.ddm.download";
    public static final String BROADCAST_DOWNLOAD_USER_MESSAGE_KEY = "broadcast_download_user_message";

    BuyerListenerServer buyerListenerServer;

    public BuyerListenerServerService() {
        super(THREAD_NAME);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        //Log.d(TAG, "onhandleintent: " + Thread.currentThread().getName());
        Intent localIntent = new Intent(BROADCAST_SERVER_START_STOP);

        if (buyerListenerServer == null) { // start server
            buyerListenerServer = new BuyerListenerServer(this);

            localIntent.putExtra(BROADCAST_USER_MESSAGE_KEY, getResources().getString(R.string.server_started_message));
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);

            while (true) { // block service from ending
                //
            }

        } else { // server already started
            localIntent.putExtra(BROADCAST_USER_MESSAGE_KEY, getResources().getString(R.string.server_started_message));
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //Log.d(TAG, "onDestroy: " + Thread.currentThread().getName());
        Intent localIntent = new Intent(BROADCAST_SERVER_START_STOP);
        localIntent.putExtra(BROADCAST_USER_MESSAGE_KEY, getResources().getString(R.string.server_stopped_message));
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);

        // shutdown buyerListenerServer
        if (buyerListenerServer != null) {
            buyerListenerServer.onDestroy();
        }

    }
}
