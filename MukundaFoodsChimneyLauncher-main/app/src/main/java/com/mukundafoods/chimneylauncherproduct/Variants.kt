package com.mukundafoods.chimneylauncherproduct

object Variants {
    const val IS_MQTT_SUPPORTED = true
    const val IS_MARKETING_APP = false
    const val IS_NORMAL_VARIANT_WITH_3_SPEED = true
}

interface FanVariant {
    val fanSpeed: Int
    val fanSpeedRenderingValue: Float
}

data class Normal(
    override val fanSpeed: Int = 3,
    override val fanSpeedRenderingValue: Float = 83f,
) : FanVariant

data class Bldc(
    override val fanSpeed: Int = 9,
    override val fanSpeedRenderingValue: Float = 28F,
) : FanVariant