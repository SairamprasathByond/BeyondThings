package com.mukundafoods.chimneylauncherproduct.ui.database

import android.content.Context
import androidx.room.Room

object ChimneyDatabaseBuilder {

    private var INSTANCE: ChimneyDatabase? = null

    fun getInstance(context: Context): ChimneyDatabase {
        if (INSTANCE == null) {
            synchronized(ChimneyDatabase::class) {
                INSTANCE = buildRoomDB(context)
            }
        }
        return INSTANCE!!
    }

    private fun buildRoomDB(context: Context) =
        Room.databaseBuilder(
            context.applicationContext,
            ChimneyDatabase::class.java,
            "chimney_db"
        ).build()

}