package com.mukundafoods.chimneylauncherproduct.ui.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.mukundafoods.chimneylauncherproduct.ui.database.MqttMessageEntity

@Dao
interface MqttMessageDao {

    @Query("SELECT * FROM mqttmessageentity Limit 100")
    suspend fun getMqttCachedData(): List<MqttMessageEntity>

    @Insert
    fun insertMqttData(mqttData: MqttMessageEntity)


    @Delete
    suspend fun deleteMqttData(mqttData: MqttMessageEntity)
}