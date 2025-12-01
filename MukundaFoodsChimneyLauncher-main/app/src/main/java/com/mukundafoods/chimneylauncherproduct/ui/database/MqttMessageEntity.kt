package com.mukundafoods.chimneylauncherproduct.ui.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MqttMessageEntity @JvmOverloads constructor(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")  var id : Int = 0,
    @ColumnInfo(name = "topic") var topic: String,
    @ColumnInfo(name = "payload") var payload : String,
)