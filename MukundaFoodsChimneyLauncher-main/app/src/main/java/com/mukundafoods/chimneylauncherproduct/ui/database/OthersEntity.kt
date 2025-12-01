package com.mukundafoods.chimneylauncherproduct.ui.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class OthersEntity @JvmOverloads constructor(
    @PrimaryKey
    @ColumnInfo(name = "id") var id : String,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "image") var image: String,
    @ColumnInfo(name = "files") var files: String,
    @ColumnInfo(name = "version") var version: Int = -1,
)