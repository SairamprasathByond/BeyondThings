package com.mukundafoods.chimneylauncherproduct.ui.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mukundafoods.chimneylauncherproduct.ui.database.BrochuersAndPriceListEntity

@Dao
interface BrochuresAndPriceListDao {

    @Query("SELECT *  FROM brochuersandpricelistentity")
    suspend fun getBrochuresAndPriceLists(): List<BrochuersAndPriceListEntity>

    @Insert
    suspend fun insertBrochuresAndPriceLists(products: BrochuersAndPriceListEntity)


    @Query("DELETE FROM BrochuersAndPriceListEntity")
    suspend fun deleteAllBrochuresAndPriceLists()
}