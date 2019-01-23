package com.technoprobic.ddm.ddm.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.technoprobic.ddm.ddm.R;
import com.technoprobic.ddm.ddm.model.SensorDataCaptureSession;
import com.technoprobic.ddm.ddm.model.SensorItem;
import com.technoprobic.ddm.ddm.ui.CaptureSensorDataActivity;
import com.technoprobic.ddm.ddm.ui.SensorDataCaptureSessionDetailActivity;
import com.technoprobic.ddm.ddm.ui.SensorDataCaptureSessionDetailFragment;

import java.util.List;

public class SensorDataCaptureSessionsAdapter extends RecyclerView.Adapter<SensorDataCaptureSessionsAdapter.ViewHolder> {

    Context mContext;
    List<SensorDataCaptureSession> sensorDataCaptureSessions;

    private AdapterLaunchSensorDataCaptureSessionCallback mAdapterLaunchSensorDataCaptureSessionCallback;

    public SensorDataCaptureSessionsAdapter(Context context, List<SensorDataCaptureSession> sensorDataCaptureSessions) {
        this.mContext = context;
        this.sensorDataCaptureSessions = sensorDataCaptureSessions;
    }

    public SensorDataCaptureSessionsAdapter(Context context) {
        this.mContext = context;

        // enforce callback interface implementation
        try {
            mAdapterLaunchSensorDataCaptureSessionCallback = (AdapterLaunchSensorDataCaptureSessionCallback) this.mContext;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement AdapterWebViewCallback");
        }

    }

    @NonNull
    @Override
    public SensorDataCaptureSessionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.sensor_data_capture_session_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SensorDataCaptureSessionsAdapter.ViewHolder holder, final int position) {

        holder.tvUserSessionDescription.setText(sensorDataCaptureSessions.get(position).getUserSessionDescription());
        holder.tvUserSessionDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchSensorDataCaptureDetailActivity(position);
            }
        });
        holder.tvSensorName.setText( sensorDataCaptureSessions.get(position).getSensorName()  );
        holder.tvSensorName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchSensorDataCaptureDetailActivity(position);
            }
        });
        holder.tvSessionStartTime.setText( sensorDataCaptureSessions.get(position).getFormattedSessionStartTime() );
        holder.tvSessionStopTime.setText( sensorDataCaptureSessions.get(position).getFormattedSessionStopTime() );

    }

    private void launchSensorDataCaptureDetailActivity(int position) {

        mAdapterLaunchSensorDataCaptureSessionCallback.launchSensorDataCaptureSession(sensorDataCaptureSessions.get(position));

    }

    public static interface AdapterLaunchSensorDataCaptureSessionCallback {
        void launchSensorDataCaptureSession(SensorDataCaptureSession sensorDataCaptureSession);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvUserSessionDescription;
        public TextView tvSensorName;
        public TextView tvSessionStartTime;
        public TextView tvSessionStopTime;

        View view;

        public ViewHolder(View itemView) {
            super(itemView);

            tvUserSessionDescription = (TextView) itemView.findViewById(R.id.tv_userSessionDescription);
            tvSensorName = (TextView) itemView.findViewById(R.id.tv_sessionSensorName);
            tvSessionStartTime = (TextView) itemView.findViewById(R.id.tv_sessionStartTime);
            tvSessionStopTime = (TextView) itemView.findViewById(R.id.tv_sessionStopTime);

            view = itemView;
        }
    }

    public void replaceSensorDataCaptureSessions(List<SensorDataCaptureSession> newSensorDataCaptureSessions) {
        if (newSensorDataCaptureSessions != null ) {
            this.sensorDataCaptureSessions = newSensorDataCaptureSessions;
            this.notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {

        if (sensorDataCaptureSessions != null) {
            return sensorDataCaptureSessions.size();
        } else {
            return 0;
        }
    }

}
