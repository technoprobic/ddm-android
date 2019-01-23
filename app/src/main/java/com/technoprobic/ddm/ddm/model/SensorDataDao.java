package com.technoprobic.ddm.ddm.model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;


import java.util.List;

@Dao
public interface SensorDataDao {

    @Query("SELECT * FROM SensorData ORDER BY sessionId, timestamp")
    List<SensorData> loadAllSensorData();

    @Query("SELECT * FROM SensorData WHERE sessionId = :sessionId ORDER BY sessionId, timestamp")
    List<SensorData> loadSensorDataBySessionId(long sessionId);

    @Insert
    void insertSensorData(SensorData sensorData);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateSensorData(SensorData sensorData);

    @Delete
    void deleteSensorData(SensorData sensorData);

    // todo delete sensordata for a sessionId
    @Query("DELETE FROM SensorData WHERE sessionId = :sessionId")
    int deleteSensorDataBySessionId(long sessionId);

}
