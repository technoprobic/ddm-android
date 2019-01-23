package com.technoprobic.ddm.ddm.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.technoprobic.ddm.ddm.R;
import com.technoprobic.ddm.ddm.model.SensorItem;
import com.technoprobic.ddm.ddm.ui.CaptureSensorDataActivity;
import com.technoprobic.ddm.ddm.ui.CaptureSensorDataFragment;

import java.util.List;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.ViewHolder> {

    Context mContext;
    List<SensorItem> sensorItems;

    private AdapterLaunchCaptureSensorDataCallback mAdapterLaunchCaptureSensorDataCallback;

    public SensorAdapter(Context context, List<SensorItem> sensorItems) {
        this.mContext = context;
        this.sensorItems = sensorItems;

        // enforce callback interface implementation
        try {
            mAdapterLaunchCaptureSensorDataCallback = (AdapterLaunchCaptureSensorDataCallback) this.mContext;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement AdapterWebViewCallback");
        }

    }

    @NonNull
    @Override
    public SensorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.sensor_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SensorAdapter.ViewHolder holder, final int position) {

        holder.tvSensorName.setText(sensorItems.get(position).getSensorName());
        holder.tvSensorName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                launchCaptureSensorDataActivity(position);

            }
        });

    }

    private void launchCaptureSensorDataActivity(int position) {

        mAdapterLaunchCaptureSensorDataCallback.launchCaptureSensorData(sensorItems.get(position));

    }

    public static interface AdapterLaunchCaptureSensorDataCallback {
        void launchCaptureSensorData(SensorItem sensorItem);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvSensorName;

        View view;

        public ViewHolder(View itemView) {
            super(itemView);

            tvSensorName = (TextView) itemView.findViewById(R.id.tv_sensorName);

            view = itemView;
        }
    }

    public void replaceSensors(List<SensorItem> newSensorItems) {

        if (newSensorItems != null ) {
            this.sensorItems = newSensorItems;
            this.notifyDataSetChanged();
        }

    }

    @Override
    public int getItemCount() {
        return sensorItems.size();
    }

}
