package com.technoprobic.ddm.ddm.utils;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.technoprobic.ddm.ddm.R;
import com.technoprobic.ddm.ddm.ui.MainActivity;

import static com.technoprobic.ddm.ddm.utils.GeneralUtils.BUYER_LISTENER_SERVER_CHANNEL_ID;

// todo - refactor from intentservice
public class BuyerListenerServerService extends IntentService {

    private static final String TAG = BuyerListenerServerService.class.getSimpleName();

    private static final String THREAD_NAME = "BuyerListenerServerServiceThread";

    public static final String BROADCAST_SERVER_START_STOP = "com.technoprobic.ddm.startStopServer";
    public static final String BROADCAST_USER_MESSAGE_KEY = "broadcast_user_message";

    public static final String BROADCAST_DOWNLOAD = "com.technoprobic.ddm.download";
    public static final String BROADCAST_DOWNLOAD_USER_MESSAGE_KEY = "broadcast_download_user_message";

    public static final Integer FOREGROUND_SERVICE_ID = 1;

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

        // create pending intent to launch main activity from notification
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        // create notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    BUYER_LISTENER_SERVER_CHANNEL_ID,
                    this.getResources().getString(R.string.server_started_message),
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager notificationManager = this.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(serviceChannel);
        }
        Notification notification = new NotificationCompat.Builder(this, BUYER_LISTENER_SERVER_CHANNEL_ID)
                .setContentTitle( getResources().getString(R.string.server_started_message))
                .setSmallIcon(R.drawable.ic_payment)
                .setContentIntent(pendingIntent)
                .build();

        // start foreground service
        startForeground(FOREGROUND_SERVICE_ID, notification);

        Intent localIntent = new Intent(BROADCAST_SERVER_START_STOP);

        // start server and broadcast server started message
        if (buyerListenerServer == null) {
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
