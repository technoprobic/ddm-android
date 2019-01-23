package com.technoprobic.ddm.ddm.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.technoprobic.ddm.ddm.R;
import com.technoprobic.ddm.ddm.adapter.SensorDataCaptureSessionsAdapter;
import com.technoprobic.ddm.ddm.model.AppDatabase;
import com.technoprobic.ddm.ddm.model.MainViewModel;
import com.technoprobic.ddm.ddm.model.SensorDataCaptureSession;

import java.util.List;

public class SensorDataCaptureSessionsFragment extends Fragment {

    private final String TAG = this.getClass().getSimpleName();

    private Context mContext;

    private AppDatabase mDb;
    private LiveData<List<SensorDataCaptureSession>> sensorDataCaptureSessionsList;
    private RecyclerView rvSensorDataCaptureSessions;
    private SensorDataCaptureSessionsAdapter sensorDataCaptureSessionAdapter;
    private static final String PRESERVE_RV_SENSOR_DATA_CAPTURE_SESSIONS_POSITION =
            "PRESERVE_RV_SENSOR_DATA_CAPTURE_SESSIONS_POSITION";
    private int rvSensorDataCaptureSessionsPosition = 0;


    public SensorDataCaptureSessionsFragment() {
        // Required empty public constructor
    }

    public static SensorDataCaptureSessionsFragment newInstance(String param1, String param2) {
        SensorDataCaptureSessionsFragment fragment = new SensorDataCaptureSessionsFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_display_sensor_data_capture_sessions, container, false);

        rvSensorDataCaptureSessions = (RecyclerView) rootview.findViewById(R.id.rvSensorDataCaptureSessions);

        sensorDataCaptureSessionAdapter = new SensorDataCaptureSessionsAdapter(mContext);
        rvSensorDataCaptureSessions.setLayoutManager(new LinearLayoutManager(mContext));
        rvSensorDataCaptureSessions.setAdapter(sensorDataCaptureSessionAdapter);

        mDb = AppDatabase.getInstance(mContext);
        //sensorDataCaptureSessionsList = mDb.sensorDataCaptureSessionDao().loadAllSensorDataCaptureSessions();
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getAllSensorDataCaptureSessions().observe(this, new Observer<List<SensorDataCaptureSession>>() {
            @Override
            public void onChanged(@Nullable List<SensorDataCaptureSession> sensorDataCaptureSessions) {
                sensorDataCaptureSessionAdapter.replaceSensorDataCaptureSessions(sensorDataCaptureSessions);
            }
        });

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(PRESERVE_RV_SENSOR_DATA_CAPTURE_SESSIONS_POSITION)) {
                rvSensorDataCaptureSessionsPosition = savedInstanceState.getInt(PRESERVE_RV_SENSOR_DATA_CAPTURE_SESSIONS_POSITION);
                if (rvSensorDataCaptureSessionsPosition > 0) {
                    ((LinearLayoutManager) rvSensorDataCaptureSessions.getLayoutManager()).scrollToPosition(rvSensorDataCaptureSessionsPosition);
                }

            }
        }

        return rootview;
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(PRESERVE_RV_SENSOR_DATA_CAPTURE_SESSIONS_POSITION,
                ((LinearLayoutManager) rvSensorDataCaptureSessions.getLayoutManager()).findFirstCompletelyVisibleItemPosition());

    }

}
