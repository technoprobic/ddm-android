package com.technoprobic.ddm.ddm.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.technoprobic.ddm.ddm.utils.GeneralUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity(tableName = "SensorDataCaptureSession")
public class SensorDataCaptureSession implements Parcelable {

    // a sensor data capture session log

    @PrimaryKey(autoGenerate = true)
    private long sessionId;
    private String userId;
    private String userSessionDescription;
    private long sessionStartTime;
    private long sessionStopTime;
    private int sensorType;
    private String sensorName;
    private long captureFrequency;
    private boolean uploadedToDdm;
    private String marketplacePostHistory;
    private String ipfsHashes;
    private String ddm1Address;
    private String ddm2Address;
    private String ddm3address;
    private int purchaseCount;

    private String userSellerName;
    private String userSensorDescription;
    private String userIpAddress;
    private String userPublicIpAddress;
    private float userPrice;


    @Ignore
    public SensorDataCaptureSession() {

        this.uploadedToDdm = false;
        this.purchaseCount = 0;
    }

    public SensorDataCaptureSession(long sessionId, String userId, long sessionStartTime, long sessionStopTime,
                                    long captureFrequency, int sensorType, String sensorName) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.sessionStartTime = sessionStartTime;
        this.sessionStopTime = sessionStopTime;
        this.captureFrequency = captureFrequency;
        this.sensorType = sensorType;
        this.sensorName = sensorName;

        this.uploadedToDdm = false;
        this.marketplacePostHistory = "";
        this.ddm1Address = "";
        this.ddm2Address = "";
        this.ddm3address = "";
        this.purchaseCount = 0;
    }

    protected SensorDataCaptureSession(Parcel in) {
        sessionId = in.readLong();
        userId = in.readString();
        userSessionDescription = in.readString();
        sessionStartTime = in.readLong();
        sessionStopTime = in.readLong();
        sensorType = in.readInt();
        sensorName = in.readString();
        captureFrequency = in.readLong();
        uploadedToDdm = in.readByte() != 0;
        marketplacePostHistory = in.readString();
        ipfsHashes = in.readString();
        ddm1Address = in.readString();
        ddm2Address = in.readString();
        ddm3address = in.readString();
        purchaseCount = in.readInt();
        userSellerName = in.readString();
        userSensorDescription = in.readString();
        userIpAddress = in.readString();
        userPublicIpAddress = in.readString();
        userPrice = in.readFloat();
    }

    public static final Creator<SensorDataCaptureSession> CREATOR = new Creator<SensorDataCaptureSession>() {
        @Override
        public SensorDataCaptureSession createFromParcel(Parcel in) {
            return new SensorDataCaptureSession(in);
        }

        @Override
        public SensorDataCaptureSession[] newArray(int size) {
            return new SensorDataCaptureSession[size];
        }
    };

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

    public long getSessionStartTime() {
        return sessionStartTime;
    }

    public void setSessionStartTime(long sessionStartTime) {
        this.sessionStartTime = sessionStartTime;
    }

    public long getSessionStopTime() {
        return sessionStopTime;
    }

    public void setSessionStopTime(long sessionStopTime) {
        this.sessionStopTime = sessionStopTime;
    }

    public int getSensorType() {
        return sensorType;
    }

    public void setSensorType(int sensorType) {
        this.sensorType = sensorType;
    }

    public boolean isUploadedToDdm() {
        return uploadedToDdm;
    }

    public void setUploadedToDdm(boolean uploadedToDdm) {
        this.uploadedToDdm = uploadedToDdm;
    }

    public String getMarketplacePostHistory() {
        if (this.marketplacePostHistory != null) {
            return marketplacePostHistory;
        } else {
            return "";
        }
    }

    public void setMarketplacePostHistory(String marketplacePostHistory) {
        this.marketplacePostHistory = marketplacePostHistory;
    }

    public void appendToMarketplacePostHistory(String moreMarketplacePostHistory) {

        if (this.marketplacePostHistory != null) {
            this.marketplacePostHistory = this.marketplacePostHistory + "," + moreMarketplacePostHistory;
        } else {
            this.marketplacePostHistory = moreMarketplacePostHistory;
        }
    }

    public String getDdm1Address() {
        return ddm1Address;
    }

    public void setDdm1Address(String ddm1Address) {
        this.ddm1Address = ddm1Address;
    }

    public String getDdm2Address() {
        return ddm2Address;
    }

    public void setDdm2Address(String ddm2Address) {
        this.ddm2Address = ddm2Address;
    }

    public String getDdm3address() {
        return ddm3address;
    }

    public void setDdm3address(String ddm3address) {
        this.ddm3address = ddm3address;
    }

    public int getPurchaseCount() {
        return purchaseCount;
    }

    public void setPurchaseCount(int purchaseCount) {
        this.purchaseCount = purchaseCount;
    }

    public String getUserSellerName() {
        return userSellerName;
    }

    public void setUserSellerName(String userSellerName) {
        this.userSellerName = userSellerName;
    }

    public String getUserSensorDescription() {
        return userSensorDescription;
    }

    public void setUserSensorDescription(String userSensorDescription) {
        this.userSensorDescription = userSensorDescription;
    }

    public String getUserIpAddress() {
        return userIpAddress;
    }

    public void setUserIpAddress(String userIpAddress) {
        this.userIpAddress = userIpAddress;
    }

    public String getUserPublicIpAddress() {
        return userPublicIpAddress;
    }

    public void setUserPublicIpAddress(String userPublicIpAddress) {
        this.userPublicIpAddress = userPublicIpAddress;
    }

    public float getUserPrice() {
        return userPrice;
    }

    public void setUserPrice(float userPrice) {
        this.userPrice = userPrice;
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public long getCaptureFrequency() {
        return captureFrequency;
    }

    public void setCaptureFrequency(long captureFrequency) {
        this.captureFrequency = captureFrequency;
    }

    public String getUserSessionDescription() {
        return userSessionDescription;
    }

    public void setUserSessionDescription(String userSessionDescription) {
        this.userSessionDescription = userSessionDescription;
    }


    public String getFormattedSessionStartTime() {
        return GeneralUtils.getFormattedDateFromMillis(this.sessionStartTime);
    }

    public String getFormattedSessionStopTime() {
        return GeneralUtils.getFormattedDateFromMillis(this.sessionStopTime);
    }

    public String getIpfsHashes() {
        return ipfsHashes;
    }

    public void setIpfsHashes(String ipfsHashes) {
        this.ipfsHashes = ipfsHashes;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(sessionId);
        dest.writeString(userId);
        dest.writeString(userSessionDescription);
        dest.writeLong(sessionStartTime);
        dest.writeLong(sessionStopTime);
        dest.writeInt(sensorType);
        dest.writeString(sensorName);
        dest.writeLong(captureFrequency);
        dest.writeByte((byte) (uploadedToDdm ? 1 : 0));
        dest.writeString(marketplacePostHistory);
        dest.writeString(ipfsHashes);
        dest.writeString(ddm1Address);
        dest.writeString(ddm2Address);
        dest.writeString(ddm3address);
        dest.writeInt(purchaseCount);
        dest.writeString(userSellerName);
        dest.writeString(userSensorDescription);
        dest.writeString(userIpAddress);
        dest.writeString(userPublicIpAddress);
        dest.writeFloat(userPrice);
    }
}
