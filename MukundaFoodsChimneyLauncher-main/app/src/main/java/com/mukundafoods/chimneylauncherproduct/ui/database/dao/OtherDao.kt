package com.mukundafoods.chimneylauncherproduct.ui.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mukundafoods.chimneylauncherproduct.ui.database.OthersEntity

@Dao
interface OtherDao {

    @Query("SELECT *  FROM othersentity")
    suspend fun getOthers(): List<OthersEntity>

    @Insert
    suspend fun insertOthers(products: OthersEntity)


    @Query("DELETE FROM OthersEntity")
    suspend fun deleteAllOthers()
}