package com.technoprobic.ddm.ddm.model;

import android.os.Parcel;
import android.os.Parcelable;

public class SensorItem implements Parcelable {

    // a sensor on the android device

    private int sensorId;
    private String sensorName;
    private int sensorType;
    private int numSensorValues;

    public SensorItem(String sensorName) {
        this.sensorName = sensorName;
    }

    public SensorItem(int sensorId, String sensorName) {
        this.sensorId = sensorId;
        this.sensorName = sensorName;
    }

    public SensorItem(int sensorId, String sensorName, int sensorType) {
        this.sensorId = sensorId;
        this.sensorName = sensorName;
        this.sensorType = sensorType;
    }

    public SensorItem(int sensorId, String sensorName, int sensorType, int numSensorValues) {
        this.sensorId = sensorId;
        this.sensorName = sensorName;
        this.sensorType = sensorType;
        this.numSensorValues = numSensorValues;
    }

    public int getSensorId() {
        return sensorId;
    }

    public void setSensorId(int sensorId) {
        this.sensorId = sensorId;
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public int getSensorType() {
        return sensorType;
    }

    public void setSensorType(int sensorType) {
        this.sensorType = sensorType;
    }

    public int getNumSensorValues() {
        return numSensorValues;
    }

    public void setNumSensorValues(int numSensorValues) {
        this.numSensorValues = numSensorValues;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(sensorId);
        dest.writeString(sensorName);
        dest.writeInt(sensorType);
        dest.writeInt(numSensorValues);
    }
    protected SensorItem(Parcel in) {
        sensorId = in.readInt();
        sensorName = in.readString();
        sensorType = in.readInt();
        numSensorValues = in.readInt();
    }

    public static final Creator<SensorItem> CREATOR = new Creator<SensorItem>() {
        @Override
        public SensorItem createFromParcel(Parcel in) {
            return new SensorItem(in);
        }

        @Override
        public SensorItem[] newArray(int size) {
            return new SensorItem[size];
        }
    };

}
