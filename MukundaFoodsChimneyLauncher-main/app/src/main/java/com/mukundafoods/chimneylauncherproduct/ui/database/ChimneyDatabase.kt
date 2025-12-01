package com.mukundafoods.chimneylauncherproduct.ui.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mukundafoods.chimneylauncherproduct.ui.database.dao.BrochuresAndPriceListDao
import com.mukundafoods.chimneylauncherproduct.ui.database.dao.MqttMessageDao
import com.mukundafoods.chimneylauncherproduct.ui.database.dao.OtherDao
import com.mukundafoods.chimneylauncherproduct.ui.database.dao.ProductsDao
import com.mukundafoods.chimneylauncherproduct.ui.database.dao.TestimonialsDao

@Database(
    entities = [ProductsEntity::class, BrochuersAndPriceListEntity::class, TestimonialEntity::class, OthersEntity::class, MqttMessageEntity::class],
    version = 1
)
abstract class ChimneyDatabase : RoomDatabase() {
    abstract fun ProductsDao(): ProductsDao

    abstract fun BrochuresAndPriceListDao(): BrochuresAndPriceListDao

    abstract fun TestimonialDao(): TestimonialsDao

    abstract fun OthersDao(): OtherDao

    abstract fun MqttMessageDao() : MqttMessageDao
}