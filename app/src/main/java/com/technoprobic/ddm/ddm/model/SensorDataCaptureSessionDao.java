package com.technoprobic.ddm.ddm.model;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface SensorDataCaptureSessionDao {

    @Query("SELECT * FROM SensorDataCaptureSession ORDER BY sessionId")
    LiveData<List<SensorDataCaptureSession>> loadAllSensorDataCaptureSessions();

    @Query("SELECT * FROM SensorDataCaptureSession ORDER BY sessionId")
    List<SensorDataCaptureSession> loadAllSensorDataCaptureSessionsWidget();

    @Query("SELECT * FROM SensorDataCaptureSession WHERE sessionId = :sessionId")
    SensorDataCaptureSession loadSensorDataCaptureSessionBySessionId(long sessionId);

    @Query("SELECT * FROM SensorDataCaptureSession ORDER BY sessionId DESC LIMIT 1")
    SensorDataCaptureSession loadHighestSessionIdSensorDataCaptureSession();

    @Query("SELECT * FROM SensorDataCaptureSession WHERE ipfsHashes LIKE '%' || :ipfsHash || '%' LIMIT 1")
    SensorDataCaptureSession loadSensorDataCaptureSessionByIpfsHash(String ipfsHash);

    @Insert
    void insertSensorDataCaptureSession(SensorDataCaptureSession sensorDataCaptureSession);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateSensorDataCaptureSession(SensorDataCaptureSession sensorDataCaptureSession);

    @Delete
    void deleteSensorDataCaptureSession(SensorDataCaptureSession sensorDataCaptureSession);

    // todo - implement delete sensordata for a sessionId
    @Query("DELETE FROM SensorDataCaptureSession WHERE sessionId = :sessionId")
    int deleteSensorDataCaptureSessionBySessionId(long sessionId);


}
