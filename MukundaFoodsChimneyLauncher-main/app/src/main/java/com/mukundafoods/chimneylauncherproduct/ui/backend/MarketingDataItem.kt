package com.mukundafoods.chimneylauncherproduct.ui.backend

data class MarketingData (
    val data: List<MarketingDataItem>
)

data class MarketingDataItem(
    val id: String,
    val name: String,
    val image: String,
    val files: String,
    val version: Int
)