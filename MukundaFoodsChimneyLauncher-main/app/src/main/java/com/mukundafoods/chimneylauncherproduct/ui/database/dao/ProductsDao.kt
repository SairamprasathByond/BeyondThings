package com.mukundafoods.chimneylauncherproduct.ui.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mukundafoods.chimneylauncherproduct.ui.database.ProductsEntity

@Dao
interface ProductsDao {

    @Query("SELECT *  FROM productsentity")
    suspend fun getProducts(): List<ProductsEntity>

    @Insert
    suspend fun insertProcuts(products: ProductsEntity)


    @Query("DELETE FROM ProductsEntity")
    suspend fun deleteAllProducts()
}