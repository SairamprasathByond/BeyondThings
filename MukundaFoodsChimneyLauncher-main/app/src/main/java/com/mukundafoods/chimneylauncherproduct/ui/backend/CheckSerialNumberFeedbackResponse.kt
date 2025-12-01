package com.mukundafoods.chimneylauncherproduct.ui.backend

data class CheckSerialNumberFeedbackResponse(
    val status_code: Int,
    val message: String,
    val status: Boolean,
    val data : FeedbackData,
)

data class FeedbackData(
    val id : Int,
    val serial_number : String,
    val machine_number : String,
    val name : String,
    val mobile : String,
    val email : String,
    val city : String,
    val feedback : String,
    val status : Int,
    val created_at : String,
    val updated_at : String,
)
