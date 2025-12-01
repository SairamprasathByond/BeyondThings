package com.mukundafoods.chimneylauncherproduct.ui.database

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ChimneyDatabaseHelperImpl(private val appDatabase: ChimneyDatabase) : ChimneyDatabaseHelper {
    override suspend fun getProducts(): List<ProductsEntity> {
        return appDatabase.ProductsDao().getProducts()
    }

    override suspend fun getBrochuresAndPriceLists(): List<BrochuersAndPriceListEntity> {
        return appDatabase.BrochuresAndPriceListDao().getBrochuresAndPriceLists()
    }

    override suspend fun getTestimonials(): List<TestimonialEntity> {
        return appDatabase.TestimonialDao().getTestimonials()
    }

    override suspend fun getOthers(): List<OthersEntity> {
        return appDatabase.OthersDao().getOthers()
    }

    override suspend fun insertProducts(products: ProductsEntity) {
        appDatabase.ProductsDao().insertProcuts(products)
    }

    override suspend fun insertBrochuresAndPriceLists(brochuersAndPriceLists: BrochuersAndPriceListEntity) {
        appDatabase.BrochuresAndPriceListDao().insertBrochuresAndPriceLists(brochuersAndPriceLists)
    }

    override suspend fun insertTestimonials(testimonials: TestimonialEntity) {
        appDatabase.TestimonialDao().insertTestimonials(testimonials)
    }

    override suspend fun insertOthers(others: OthersEntity) {
        appDatabase.OthersDao().insertOthers(others)
    }

    override suspend fun deleteProducts() {
        appDatabase.ProductsDao().deleteAllProducts()
    }

    override suspend fun deleteBrochuresAndPriceLists() {
        appDatabase.BrochuresAndPriceListDao().deleteAllBrochuresAndPriceLists()
    }

    override suspend fun deleteTestimonials() {
        appDatabase.TestimonialDao().deleteAllTestimonials()
    }

    override suspend fun deleteOthers() {
        appDatabase.OthersDao().deleteAllOthers()
    }

    override fun getMqttCachedData(): List<MqttMessageEntity> {
        var result = emptyList<MqttMessageEntity>()
        val job = CoroutineScope(Dispatchers.IO).async {
            result = appDatabase.MqttMessageDao().getMqttCachedData()
        }
        runBlocking {
            job.join()
        }

        return result
    }

    override fun insertMqttData(mqttMessageEntity: MqttMessageEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            appDatabase.MqttMessageDao().insertMqttData(mqttMessageEntity)
        }

    }

    override fun deleteMqttData(mqttMessageEntity: MqttMessageEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            appDatabase.MqttMessageDao().deleteMqttData(mqttMessageEntity)
        }
    }
}