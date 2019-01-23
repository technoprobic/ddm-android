package com.technoprobic.ddm.ddm.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.technoprobic.ddm.ddm.R;
import com.technoprobic.ddm.ddm.adapter.SensorAdapter;
import com.technoprobic.ddm.ddm.adapter.SensorDataCaptureSessionsAdapter;
import com.technoprobic.ddm.ddm.model.SensorDataCaptureSession;
import com.technoprobic.ddm.ddm.model.SensorItem;
import com.technoprobic.ddm.ddm.ui.CaptureSensorDataActivity;
import com.technoprobic.ddm.ddm.ui.CaptureSensorDataFragment;
import com.technoprobic.ddm.ddm.ui.SensorDataCaptureSessionDetailActivity;
import com.technoprobic.ddm.ddm.ui.SensorDataCaptureSessionDetailFragment;
import com.technoprobic.ddm.ddm.ui.SensorDataCaptureSessionsFragment;
import com.technoprobic.ddm.ddm.ui.SensorListFragment;
import com.technoprobic.ddm.ddm.ui.SettingsActivity;
import com.technoprobic.ddm.ddm.utils.BuyerListenerServerService;
import com.technoprobic.ddm.ddm.utils.GeneralUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorAdapter.AdapterLaunchCaptureSensorDataCallback,
        SensorDataCaptureSessionsAdapter.AdapterLaunchSensorDataCaptureSessionCallback {

    private boolean isTwoPaneView;

    private Context mContext;

    private View view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;


        // check for phone or tablet
        if ( findViewById(R.id.container_detail ) != null ) { // view only exists in the two pane tablet view
            isTwoPaneView = true;
        } else { // phone view
            isTwoPaneView = false;
        }

        //create view pager and adapter
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(new SensorListFragment(), getResources().getString(R.string.store_sensor_data_tab_label));
        viewPagerAdapter.addFragment(new SensorDataCaptureSessionsFragment(), getResources().getString(R.string.stored_sensor_data_tab_label));
        viewPager.setAdapter(viewPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        IntentFilter intentFilter = new IntentFilter(BuyerListenerServerService.BROADCAST_SERVER_START_STOP);
        LocalBroadcastManager.getInstance(this).registerReceiver(serverStartStopMessageReceiver, intentFilter);
        intentFilter = new IntentFilter(BuyerListenerServerService.BROADCAST_DOWNLOAD);
        LocalBroadcastManager.getInstance(this).registerReceiver(downloadMessageReceiver, intentFilter);

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
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
                if (GeneralUtils.isIotaAddressAvailable(mContext)) {
                    if (GeneralUtils.isConnectedToNetwork(mContext)) {
                        Intent startBuyerListenerServer = new Intent(this, BuyerListenerServerService.class);
                        startService(startBuyerListenerServer);
                    } else {
                        Toast.makeText(mContext, getResources().getString(R.string.check_network_connection), Toast.LENGTH_LONG).show();
                    }
                    } else {
                    Toast.makeText(mContext, getResources().getString(R.string.enter_iota), Toast.LENGTH_LONG).show();
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

    @Override
    protected void onResume() {
        super.onResume();

    }

    // adapter callback to launch Capture Sensor Data screen
    public void launchCaptureSensorData(SensorItem sensorItem) {

        if (sensorItem != null) {

            if (isTwoPaneView) {

                // launch in detail container
                Bundle bundle = new Bundle();
                bundle.putParcelable(CaptureSensorDataFragment.BUNDLE_KEY, sensorItem);

                CaptureSensorDataFragment captureSensorDataFragment = new CaptureSensorDataFragment();
                captureSensorDataFragment.setArguments(bundle);
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container_detail, captureSensorDataFragment)
                        .commit();

            } else {

                // launch in capture sensor data activity
                Intent intent = new Intent(mContext, CaptureSensorDataActivity.class);
                intent.putExtra(CaptureSensorDataFragment.BUNDLE_KEY, sensorItem);

                //Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle();
                //mContext.startActivity(intent, bundle);
                mContext.startActivity(intent);

            }

        }

    }

    // adapter callback to launch Sensor Data Capture Session Detail screen
    public void launchSensorDataCaptureSession(SensorDataCaptureSession sensorDataCaptureSession) {

        if (sensorDataCaptureSession != null) {

            if (isTwoPaneView) {

                // launch in detail container
                Bundle bundle = new Bundle();
                bundle.putParcelable(SensorDataCaptureSessionDetailFragment.BUNDLE_KEY, sensorDataCaptureSession);

                SensorDataCaptureSessionDetailFragment sensorDataCaptureSessionDetailFragment = new SensorDataCaptureSessionDetailFragment();
                sensorDataCaptureSessionDetailFragment.setArguments(bundle);
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container_detail, sensorDataCaptureSessionDetailFragment)
                        .commit();

            } else {

                // launch in capture sensor data activity
                Intent intent = new Intent(mContext, SensorDataCaptureSessionDetailActivity.class);
                intent.putExtra(SensorDataCaptureSessionDetailFragment.BUNDLE_KEY, sensorDataCaptureSession);

                //Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle();
                //mContext.startActivity(intent, bundle);

                mContext.startActivity(intent);

            }

        }

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
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(serverStartStopMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(downloadMessageReceiver);

    }
}
