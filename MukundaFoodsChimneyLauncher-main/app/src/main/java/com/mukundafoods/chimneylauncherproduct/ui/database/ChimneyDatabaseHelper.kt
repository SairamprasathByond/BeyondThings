package com.mukundafoods.chimneylauncherproduct.ui.database

interface ChimneyDatabaseHelper {

    suspend fun getProducts(): List<ProductsEntity>
    suspend fun getBrochuresAndPriceLists(): List<BrochuersAndPriceListEntity>
    suspend fun getTestimonials(): List<TestimonialEntity>
    suspend fun getOthers(): List<OthersEntity>

    suspend fun insertProducts(products: ProductsEntity)
    suspend fun insertBrochuresAndPriceLists(brochuersAndPriceLists: BrochuersAndPriceListEntity)
    suspend fun insertTestimonials(testimonials: TestimonialEntity)
    suspend fun insertOthers(others: OthersEntity)

    suspend fun deleteProducts()
    suspend fun deleteBrochuresAndPriceLists()
    suspend fun deleteTestimonials()
    suspend fun deleteOthers()

    fun getMqttCachedData(): List<MqttMessageEntity>
    fun insertMqttData(mqttMessageEntity: MqttMessageEntity): Any?
    fun deleteMqttData(mqttMessageEntity: MqttMessageEntity)
}