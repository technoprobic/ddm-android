package com.technoprobic.ddm.ddm.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "SensorData")
public class SensorData {

    // a "row" of sensor data

    @PrimaryKey(autoGenerate = true)
    private int id;
    private long sessionId;
    private String userId;
    private long timestamp;
    private int sensorType;
    private Double latitude;
    private Double longitude;
    private float sensorValue1;
    private String sensorValue1Description;
    private float sensorValue2;
    private String sensorValue2Description;
    private float sensorValue3;
    private String sensorValue3Description;
    private float sensorValue4;
    private String sensorValue4Description;
    private float sensorValue5;
    private String sensorValue5Description;
    private float sensorValue6;
    private String sensorValue6Description;

    @Ignore
    public SensorData() {

    }

    public SensorData(long sessionId, String userId, long timestamp, int sensorType, float sensorValue1, String sensorValue1Description) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.timestamp = timestamp;
        this.sensorType = sensorType;
        this.sensorValue1 = sensorValue1;
        this.sensorValue1Description = sensorValue1Description;

        this.latitude = 0.0;
        this.longitude = 0.0;
    }

    @Ignore
    public SensorData(long sessionId, String userId, long timestamp, int sensorType, Double latitude, Double longitude, float sensorValue1, String sensorValue1Description) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.timestamp = timestamp;
        this.sensorType = sensorType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.sensorValue1 = sensorValue1;
        this.sensorValue1Description = sensorValue1Description;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getSensorType() {
        return sensorType;
    }

    public void setSensorType(int sensorType) {
        this.sensorType = sensorType;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public float getSensorValue1() {
        return sensorValue1;
    }

    public void setSensorValue1(float sensorValue1) {
        this.sensorValue1 = sensorValue1;
    }

    public String getSensorValue1Description() {
        return sensorValue1Description;
    }

    public void setSensorValue1Description(String sensorValue1Description) {
        this.sensorValue1Description = sensorValue1Description;
    }

    public float getSensorValue2() {
        return sensorValue2;
    }

    public void setSensorValue2(float sensorValue2) {
        this.sensorValue2 = sensorValue2;
    }

    public String getSensorValue2Description() {
        return sensorValue2Description;
    }

    public void setSensorValue2Description(String sensorValue2Description) {
        this.sensorValue2Description = sensorValue2Description;
    }

    public float getSensorValue3() {
        return sensorValue3;
    }

    public void setSensorValue3(float sensorValue3) {
        this.sensorValue3 = sensorValue3;
    }

    public String getSensorValue3Description() {
        return sensorValue3Description;
    }

    public void setSensorValue3Description(String sensorValue3Description) {
        this.sensorValue3Description = sensorValue3Description;
    }

    public float getSensorValue4() {
        return sensorValue4;
    }

    public void setSensorValue4(float sensorValue4) {
        this.sensorValue4 = sensorValue4;
    }

    public String getSensorValue4Description() {
        return sensorValue4Description;
    }

    public void setSensorValue4Description(String sensorValue4Description) {
        this.sensorValue4Description = sensorValue4Description;
    }

    public float getSensorValue5() {
        return sensorValue5;
    }

    public void setSensorValue5(float sensorValue5) {
        this.sensorValue5 = sensorValue5;
    }

    public String getSensorValue5Description() {
        return sensorValue5Description;
    }

    public void setSensorValue5Description(String sensorValue5Description) {
        this.sensorValue5Description = sensorValue5Description;
    }

    public float getSensorValue6() {
        return sensorValue6;
    }

    public void setSensorValue6(float sensorValue6) {
        this.sensorValue6 = sensorValue6;
    }

    public String getSensorValue6Description() {
        return sensorValue6Description;
    }

    public void setSensorValue6Description(String sensorValue6Description) {
        this.sensorValue6Description = sensorValue6Description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
