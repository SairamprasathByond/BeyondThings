package com.mukundafoods.chimneylauncherproduct.ui.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mukundafoods.chimneylauncherproduct.ui.database.TestimonialEntity

@Dao
interface TestimonialsDao {

    @Query("SELECT *  FROM testimonialentity")
    suspend fun getTestimonials(): List<TestimonialEntity>

    @Insert
    suspend fun insertTestimonials(products: TestimonialEntity)


    @Query("DELETE FROM TestimonialEntity")
    suspend fun deleteAllTestimonials()
}