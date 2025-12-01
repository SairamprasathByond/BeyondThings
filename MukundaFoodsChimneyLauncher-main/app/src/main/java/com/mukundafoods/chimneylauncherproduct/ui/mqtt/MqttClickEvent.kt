package com.mukundafoods.chimneylauncherproduct.ui.mqtt

import com.google.gson.annotations.SerializedName

data class MqttClickEvent(
    @SerializedName("time") val time: Long,
    @SerializedName("sln") val sln: String,
    @SerializedName("lon") val lon: String?,
    @SerializedName("pType") val pType: String = "Chimney",
    @SerializedName("sv") val sv: String,
    @SerializedName("hb") val hb: Boolean,
    @SerializedName("appName") val appName: String = "ByProd",
    @SerializedName("screen") val screen: String,
    @SerializedName("clickEvent") val clickEvent: String,
    @SerializedName("subEvent") val subEvent: String,
)
