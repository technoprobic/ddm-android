package com.technoprobic.ddm.ddm.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.technoprobic.ddm.ddm.R;
import com.technoprobic.ddm.ddm.model.SensorItem;
import com.technoprobic.ddm.ddm.utils.BuyerListenerServerService;
import com.technoprobic.ddm.ddm.utils.GeneralUtils;

public class CaptureSensorDataActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    private SensorItem sensorItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_sensor_data);

        Intent intent = getIntent();
        if (intent != null) {
            sensorItem = intent.getParcelableExtra(CaptureSensorDataFragment.BUNDLE_KEY);
            //Log.d(TAG, "sensor_item name" + sensorItem.getSensorName());
        } else {
            //
        }

        Bundle bundle = new Bundle();
        bundle.putParcelable(CaptureSensorDataFragment.BUNDLE_KEY, sensorItem);

        if (savedInstanceState == null) {
            CaptureSensorDataFragment captureSensorDataFragment = new CaptureSensorDataFragment();
            captureSensorDataFragment.setArguments(bundle);
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.sensor_data_capture_container, captureSensorDataFragment)
                    .commit();
        }

        IntentFilter intentFilter = new IntentFilter(BuyerListenerServerService.BROADCAST_SERVER_START_STOP);
        LocalBroadcastManager.getInstance(this).registerReceiver(serverStartStopMessageReceiver, intentFilter);
        intentFilter = new IntentFilter(BuyerListenerServerService.BROADCAST_DOWNLOAD);
        LocalBroadcastManager.getInstance(this).registerReceiver(downloadMessageReceiver, intentFilter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch ( id) {

            case R.id.action_start_server: {
                if (GeneralUtils.isIotaAddressAvailable(this)) {
                    if (GeneralUtils.isConnectedToNetwork(this)) {
                        Intent startBuyerListenerServer = new Intent(this, BuyerListenerServerService.class);
                        startService(startBuyerListenerServer);
                    } else {
                        Toast.makeText(this, getResources().getString(R.string.check_network_connection), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, getResources().getString(R.string.enter_iota), Toast.LENGTH_LONG).show();
                }
                return true;
            }

            case R.id.action_stop_server: {
                Intent stopBuyerListenerServer = new Intent(this, BuyerListenerServerService.class);
                stopService(stopBuyerListenerServer);
                return true;
            }

            case R.id.action_settings: {
                Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(startSettingsActivity);
                return true;
            }

        }

        return super.onOptionsItemSelected(item);
    }

    // receive BuyerListenerServerService start/stop messages
    private BroadcastReceiver serverStartStopMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String serverStartStopMessage = intent.getStringExtra(BuyerListenerServerService.BROADCAST_USER_MESSAGE_KEY);
            Toast.makeText(context, serverStartStopMessage, Toast.LENGTH_LONG).show();

        }
    };

    // receive BuyerListenerServerService download broadcast
    private BroadcastReceiver downloadMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String downloadMessage = intent.getStringExtra(BuyerListenerServerService.BROADCAST_DOWNLOAD_USER_MESSAGE_KEY);
            Toast.makeText(context, downloadMessage, Toast.LENGTH_LONG).show();

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(serverStartStopMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(downloadMessageReceiver);

    }

}
