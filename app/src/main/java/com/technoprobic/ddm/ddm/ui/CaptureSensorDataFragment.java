package com.technoprobic.ddm.ddm.ui;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.technoprobic.ddm.ddm.utils.AppExecutors;
import com.technoprobic.ddm.ddm.R;
import com.technoprobic.ddm.ddm.model.AppDatabase;
import com.technoprobic.ddm.ddm.model.SensorData;
import com.technoprobic.ddm.ddm.model.SensorDataCaptureSession;
import com.technoprobic.ddm.ddm.model.SensorItem;
import com.technoprobic.ddm.ddm.utils.GeneralUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

public class CaptureSensorDataFragment extends Fragment implements SensorEventListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {

    public static final String BUNDLE_KEY = "SensorItem";

    private final String TAG = this.getClass().getSimpleName();

    private Context mContext;

    private SensorItem sensorItem;
    private TextView tvSensorName;
    private EditText etUserSessionDescription;
    private static final int MINIMUM_USER_SESSION_DESCRIPTION_LENGTH = 3;
    private EditText etUserCaptureFrequency;
    private CheckBox checkboxIncludeLocation;
    private Button btnStartCapture;
    private Button btnStopCapture;
    private LinearLayout llSensorDataDisplay;
    private LinearLayout llLocationDataDisplay;
    private TextView tvLatitude;
    private TextView tvLongitude;
    private TextView tvSensorValue1;
    private TextView tvSensorValue2;
    private TextView tvSensorValue3;
    private TextView tvSensorValue4;
    private TextView tvSensorValue5;
    private TextView tvSensorValue6;
    private boolean inCaptureMode;

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest locationRequest;
    private Location mLastLocation;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private int sensorCaptureFrequency = 1000; // default to 1 second intervals

    private Handler handler;
    private boolean captureFrequencyThresholdReached;

    private SensorDataCaptureSession currentSensorDataCaptureSession;

    private SharedPreferences userPreferences;
    private static final String userPreferenceFile = GeneralUtils.userPreferenceFile;
    private String userId;
    private long sessionId;

    private AppDatabase mDb;


    public CaptureSensorDataFragment() {
        // Required empty public constructor
    }

    public static CaptureSensorDataFragment newInstance(String param1, String param2) {
        CaptureSensorDataFragment fragment = new CaptureSensorDataFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_capture_sensor_data, container, false);

        tvSensorName = rootView.findViewById(R.id.tv_current_sensor_name);

        if ( getArguments() != null ) {
            //Log.d(TAG, "arguments key: " + getArguments().containsKey(CaptureSensorDataFragment.BUNDLE_KEY) );
            sensorItem = getArguments().getParcelable(BUNDLE_KEY);

            tvSensorName.setText(sensorItem.getSensorName());
        }

        etUserSessionDescription = rootView.findViewById(R.id.et_user_session_description);
        etUserSessionDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setUi();
            }
        });

        etUserCaptureFrequency = rootView.findViewById(R.id.et_user_session_capture_frequency);
        etUserCaptureFrequency.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (! hasFocus) {
                    updateUserCaptureFrequency();
                }
            }
        });

        checkboxIncludeLocation = rootView.findViewById(R.id.checkbox_include_location);
        checkboxIncludeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // is location permission granted and is location enabled
                if (checkboxIncludeLocation.isChecked()) {
                    boolean isLocationPermissionGranted = isLocationPermissionGranted();
                    if (! isLocationPermissionGranted) {
                        checkboxIncludeLocation.setChecked(false);
                    }
                    boolean isLocationEnabled = isLocationEnabled(mContext);
                    if (! isLocationEnabled(mContext)) {
                        Toast.makeText(mContext, getResources().getString(R.string.please_enable_location), Toast.LENGTH_LONG).show();
                        checkboxIncludeLocation.setChecked(false);
                    }
                }
            }
        });

        btnStartCapture = rootView.findViewById(R.id.btn_start_capture);
        btnStartCapture.setEnabled(false);
        btnStartCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSensorDataCapture();
            }
        });
        btnStopCapture = rootView.findViewById(R.id.btn_stop_capture);
        btnStopCapture.setEnabled(false);
        btnStopCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSensorDataCapture();
            }
        });
        inCaptureMode = false;

        handler = new Handler();

        userPreferences =  mContext.getSharedPreferences(userPreferenceFile, MODE_PRIVATE);
        userId = userPreferences.getString("userId", "");
        if (userId.equals("") ) {
            SharedPreferences.Editor editor = userPreferences.edit();
            Random rand = new Random();
            int baseUserIdInt = rand.nextInt(1000000);
            NumberFormat numberFormat = new DecimalFormat("0000000");
            String userIdString = numberFormat.format(baseUserIdInt);
            editor.putString("userId", userIdString);
            editor.commit();
            userId = userIdString;
        }

        llSensorDataDisplay = rootView.findViewById(R.id.ll_sensor_data_display);
        llLocationDataDisplay = rootView.findViewById(R.id.ll_location_data_display);
        tvLatitude = rootView.findViewById(R.id.tv_latitude);
        tvLongitude = rootView.findViewById(R.id.tv_longitude);
        tvSensorValue1 = rootView.findViewById(R.id.tv_sensorValue1);
        tvSensorValue2 = rootView.findViewById(R.id.tv_sensorValue2);
        tvSensorValue3 = rootView.findViewById(R.id.tv_sensorValue3);
        tvSensorValue4 = rootView.findViewById(R.id.tv_sensorValue4);
        tvSensorValue5 = rootView.findViewById(R.id.tv_sensorValue5);
        tvSensorValue6 = rootView.findViewById(R.id.tv_sensorValue6);

        mDb = AppDatabase.getInstance(mContext);

        return rootView;
    }

    private void setUi() {

        if (inCaptureMode) {
            btnStartCapture.setEnabled(false);
            btnStopCapture.setEnabled(true);
            checkboxIncludeLocation.setEnabled(false);
            etUserSessionDescription.setEnabled(false);
            etUserCaptureFrequency.setEnabled(false);
        } else if (etUserSessionDescription.getText().toString().trim().length() >= MINIMUM_USER_SESSION_DESCRIPTION_LENGTH) {
            btnStartCapture.setEnabled(true);
            btnStopCapture.setEnabled(false);
            checkboxIncludeLocation.setEnabled(true);
            etUserSessionDescription.setEnabled(true);
            etUserCaptureFrequency.setEnabled(true);
        } else {
            btnStartCapture.setEnabled(false);
            btnStopCapture.setEnabled(false);
            checkboxIncludeLocation.setEnabled(true);
            etUserSessionDescription.setEnabled(true);
            etUserCaptureFrequency.setEnabled(true);
        }

    }

    private void setSensorDataDisplayUi() {

        if (inCaptureMode) {
            clearSensorOutputTextViews();

            if (checkboxIncludeLocation.isChecked()) {
                llLocationDataDisplay.setVisibility(View.VISIBLE);
            } else {
                llLocationDataDisplay.setVisibility(View.GONE);
            }
            llSensorDataDisplay.setVisibility(View.VISIBLE);

        } else {
            llSensorDataDisplay.setVisibility(View.GONE);
        }

    }

    private void clearSensorOutputTextViews() {
        tvLatitude.setText("");
        tvLongitude.setText("");
        tvSensorValue1.setText("");
        tvSensorValue2.setText("");
        tvSensorValue3.setText("");
        tvSensorValue4.setText("");
        tvSensorValue5.setText("");
        tvSensorValue6.setText("");
    }

    private void updateUserCaptureFrequency() {

        int userCaptureFrequencyToInt;

        try {
            userCaptureFrequencyToInt = Integer.parseInt(etUserCaptureFrequency.getText().toString().trim() );
        } catch (NumberFormatException nfe) {
            userCaptureFrequencyToInt = 1000;
        }

        if (userCaptureFrequencyToInt <= 0) {
            userCaptureFrequencyToInt = 1000;
        }

        sensorCaptureFrequency = userCaptureFrequencyToInt;
        //etUserCaptureFrequency.setText( Integer.toString(sensorCaptureFrequency) );

     }

    private void startSensorDataCapture() {

        inCaptureMode = true;

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                sessionId = getNewSessionId();
                currentSensorDataCaptureSession = initializeSensorDataCaptureSession();
            }
        });

        startSensorListener();

        if (checkboxIncludeLocation.isChecked() &&  isLocationPermissionGranted() && isLocationEnabled(mContext)) {
            initializeLocationServices();
        }

        handler.post(sensorCaptureTimer);

        setUi();
        setSensorDataDisplayUi();
        enableKeepScreenOn();

    }

    private void stopSensorDataCapture() {

        inCaptureMode = false;

        stopSensorListener();
        if (checkboxIncludeLocation.isChecked() ) {
            stopLocationServices();
        }

        disableKeepScreenOn();

        handler.removeCallbacks(sensorCaptureTimer);

        setUi();
        setSensorDataDisplayUi();

        currentSensorDataCaptureSession.setSessionStopTime(System.currentTimeMillis());

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.sensorDataCaptureSessionDao().insertSensorDataCaptureSession(currentSensorDataCaptureSession);
            }
        });

        clearSensorOutputTextViews();
    }

    // timer for control of sensor readings
    private final Runnable sensorCaptureTimer = new Runnable() {
        @Override
        public void run() {
            captureFrequencyThresholdReached = true;
            handler.postDelayed(this, sensorCaptureFrequency);
        }
    };

    public void startSensorListener() {

        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        int sensorCaptureFrequencyinMicroseconds = sensorCaptureFrequency * 1000;

        if (mSensorManager != null) {
            List<Sensor> sensorsOfUserType = mSensorManager.getSensorList(sensorItem.getSensorType());
            Sensor userSensor = null;
            for  ( android.hardware.Sensor sensorOfUserType : sensorsOfUserType ) {
                if (sensorOfUserType.getName().equals(sensorItem.getSensorName())) {
                    userSensor = sensorOfUserType;
                    break;
                }
            }
            if (userSensor != null) {
                mSensorManager.registerListener(this, userSensor, sensorCaptureFrequencyinMicroseconds);
            } else {
                Log.d(TAG,"SensorStatus SensorItem unavailable");
            }

        } else {
            //Log.d(TAG, "msensormanager null");
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //todo - catch storage space exception

        if (captureFrequencyThresholdReached) {

            final SensorData currentSensorData = new SensorData();
            currentSensorData.setSessionId(sessionId);
            currentSensorData.setUserId(userId);
            currentSensorData.setSensorType(sensorItem.getSensorType());
            currentSensorData.setTimestamp(System.currentTimeMillis());

            if (checkboxIncludeLocation.isChecked() && mLastLocation != null) {
                currentSensorData.setLatitude(mLastLocation.getLatitude());
                currentSensorData.setLongitude(mLastLocation.getLongitude());

                tvLatitude.setText( Double.toString(mLastLocation.getLatitude()) );
                tvLongitude.setText( Double.toString(mLastLocation.getLongitude()) );

            } else {
                currentSensorData.setLatitude(0.0);
                currentSensorData.setLongitude(0.0);
            }
            // capture up to six sensor output values
            for (int i=0; i<event.values.length && i<=5; i++) {
                switch (i) {
                    case 0: {
                        currentSensorData.setSensorValue1(event.values[i]);
                        tvSensorValue1.setText( Double.toString( event.values[i] ) );
                        break;
                    }
                    case 1: {
                        currentSensorData.setSensorValue2(event.values[i]);
                        tvSensorValue2.setText( Double.toString( event.values[i] ) );
                        break;
                    }
                    case 2: {
                        currentSensorData.setSensorValue3(event.values[i]);
                        tvSensorValue3.setText( Double.toString( event.values[i] ) );
                        break;
                    }
                    case 3: {
                        currentSensorData.setSensorValue4(event.values[i]);
                        tvSensorValue4.setText( Double.toString( event.values[i] ) );
                        break;
                    }
                    case 4: {
                        currentSensorData.setSensorValue5(event.values[i]);
                        tvSensorValue5.setText( Double.toString( event.values[i] ) );
                        break;
                    }
                    case 5: {
                        currentSensorData.setSensorValue6(event.values[i]);
                        tvSensorValue6.setText( Double.toString( event.values[i] ) );
                        break;
                    }
                }
            }

            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.sensorDataDao().insertSensorData(currentSensorData);
                }
            });

            captureFrequencyThresholdReached = false;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //
    }

    public void stopSensorListener() {

        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }

    }

    public void initializeLocationServices() {

        if ( mGoogleApiClient == null && isLocationPermissionGranted() && isLocationEnabled(mContext) ) {
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .enableAutoManage(getActivity() , this )
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .build();
        } else {
            startLocationServices();
        }

    }

    public void startLocationServices() {

        if ( isLocationPermissionGranted() && isLocationEnabled(mContext) ) {

            try {
                locationRequest = new LocationRequest();
                locationRequest.setInterval(sensorCaptureFrequency);
                locationRequest.setFastestInterval( (int)(.75 * sensorCaptureFrequency) );
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
            } catch (SecurityException e) {
                Log.d(TAG, "startLocationServices location permission error: " + e.toString());
            }

        } else {
            String please_enable_location = getResources().getString(R.string.please_enable_location);
            Toast.makeText(mContext, please_enable_location, Toast.LENGTH_LONG).show();
        }

    }

    public void stopLocationServices() {

        if (isLocationPermissionGranted() && locationRequest != null) {

            if (mGoogleApiClient != null) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }
        }

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (isLocationPermissionGranted() && isLocationEnabled(mContext)) {
            startLocationServices();
        }

    }

    private void enableKeepScreenOn() {
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void disableKeepScreenOn() {
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private long getNewSessionId() {

        long newSessionId = 1;

        SensorDataCaptureSession tempSensorDataCaptureSession = mDb.sensorDataCaptureSessionDao().loadHighestSessionIdSensorDataCaptureSession();
        if (tempSensorDataCaptureSession != null) {
            long tempSessionId = tempSensorDataCaptureSession.getSessionId();
            newSessionId = tempSessionId + 1;
        }

        return  newSessionId;
    }

    private SensorDataCaptureSession initializeSensorDataCaptureSession() {

        SensorDataCaptureSession tempSensorDataCaptureSession = new SensorDataCaptureSession();

        tempSensorDataCaptureSession.setSessionId( sessionId );
        tempSensorDataCaptureSession.setUserId(userId);
        tempSensorDataCaptureSession.setUserSessionDescription(etUserSessionDescription.getText().toString().trim());
        tempSensorDataCaptureSession.setUploadedToDdm(false);
        tempSensorDataCaptureSession.setSensorType(sensorItem.getSensorType());
        tempSensorDataCaptureSession.setSensorName(sensorItem.getSensorName());
        tempSensorDataCaptureSession.setSessionStartTime(System.currentTimeMillis());
        tempSensorDataCaptureSession.setCaptureFrequency(sensorCaptureFrequency);

        return tempSensorDataCaptureSession;
    }

    @Override
    public void onPause() {

        // if app is no longer in foreground, close capture session
        if (inCaptureMode) {
            stopSensorDataCapture();
        }

        super.onPause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public boolean isLocationPermissionGranted() {

        boolean permissionGranted = false;

        if (Build.VERSION.SDK_INT >= 23) {

            int locationPermissionCheck = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);

            if (locationPermissionCheck == PackageManager.PERMISSION_GRANTED) {
                permissionGranted = true;
            } else {

                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                permissionGranted = false;
            }
        } else {

            permissionGranted = true;
        }
        return permissionGranted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case LOCATION_PERMISSION_REQUEST_CODE: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //
                    boolean isLocationEnabled = isLocationEnabled(mContext);
                    if (! isLocationEnabled) {
                        Toast.makeText(mContext, getResources().getString(R.string.please_enable_location), Toast.LENGTH_LONG).show();
                        checkboxIncludeLocation.setChecked(false);
                    } else {
                        checkboxIncludeLocation.setChecked(true);
                    }

                } else {
                    Toast.makeText(mContext, getResources().getString(R.string.please_permit_location), Toast.LENGTH_LONG).show();
                }

            }

        }

    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            return !TextUtils.isEmpty(locationProviders);
        }
    }


}
