package com.mukundafoods.chimneylauncherproduct.ui.backend

data class CheckSerialNumberResponse(
    val status_code: Int,
    val message: String,
    val status: Boolean,
    val update_date : String,
    val data : Data,
)

data class Data(
    val serial_number : String,
    val machine_number : String,
)
