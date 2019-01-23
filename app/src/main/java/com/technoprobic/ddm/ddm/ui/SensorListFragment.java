package com.technoprobic.ddm.ddm.ui;

import android.content.Context;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.technoprobic.ddm.ddm.R;
import com.technoprobic.ddm.ddm.adapter.SensorAdapter;
import com.technoprobic.ddm.ddm.model.SensorItem;

import java.util.ArrayList;
import java.util.List;

public class SensorListFragment extends Fragment {

    private final String TAG = this.getClass().getSimpleName();

    private Context mContext;

    private List<SensorItem> sensorItemList;
    private RecyclerView rvSensors;
    private SensorAdapter sensorAdapter;
    private static final String PRESERVE_RV_SENSORS_POSITION = "PRESERVE_RV_SENSORS_POSITION";
    private int rvSensorsPosition = 0;

    public SensorListFragment() {
        // Required empty public constructor
    }

    public static SensorListFragment newInstance(String param1, String param2) {
        SensorListFragment fragment = new SensorListFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_sensor_list, container, false);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(PRESERVE_RV_SENSORS_POSITION)) {
                rvSensorsPosition = savedInstanceState.getInt(PRESERVE_RV_SENSORS_POSITION);
            }
        }

        rvSensors = (RecyclerView) rootview.findViewById(R.id.rvSensors);

        sensorItemList = new ArrayList<>();
        List<android.hardware.Sensor> sensors = ((SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE)).getSensorList(android.hardware.Sensor.TYPE_ALL);
        for (android.hardware.Sensor sensor : sensors) {
            SensorItem tempSensorItem = new SensorItem(sensor.getName());
            tempSensorItem.setSensorType(sensor.getType());
            sensorItemList.add(tempSensorItem);

        }
        sensorAdapter = new SensorAdapter(mContext, sensorItemList);
        rvSensors.setLayoutManager(new LinearLayoutManager(mContext));
        rvSensors.setAdapter(sensorAdapter);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(PRESERVE_RV_SENSORS_POSITION)) {
                rvSensorsPosition = savedInstanceState.getInt(PRESERVE_RV_SENSORS_POSITION);
                if (rvSensorsPosition > 0 && rvSensorsPosition <= sensorItemList.size()) {
                    ((LinearLayoutManager) rvSensors.getLayoutManager()).scrollToPosition(rvSensorsPosition);
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

        outState.putInt(PRESERVE_RV_SENSORS_POSITION,
                ((LinearLayoutManager) rvSensors.getLayoutManager()).findFirstCompletelyVisibleItemPosition());

    }

}
