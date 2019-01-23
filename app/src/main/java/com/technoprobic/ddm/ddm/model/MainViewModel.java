package com.technoprobic.ddm.ddm.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<SensorDataCaptureSession>> sensorDataCaptureSessions;

    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        sensorDataCaptureSessions = database.sensorDataCaptureSessionDao().loadAllSensorDataCaptureSessions();

    }

    public LiveData<List<SensorDataCaptureSession>> getAllSensorDataCaptureSessions() {
        return sensorDataCaptureSessions;
    }

}
