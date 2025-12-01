package com.mukundafoods.chimneylauncherproduct.ui.animation.blower

import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.TextView

class BlowerSpeedAnimation(circle: BlowerSpeedCircle, newAngle: Int, fanStatus: TextView, fanSpeedStatus : TextView, fanLevel : TextView) : Animation() {

    private var circle: BlowerSpeedCircle? = null

    private var oldAngle = 0f
    private var newAngle = 0f
    private var fanStatus : TextView
    private var fanSpeedStatus : TextView
    private var fanLevel : TextView

    init {
        oldAngle = circle.getCircleAngle()
        this.newAngle = newAngle.toFloat()
        this.circle = circle
        this.fanStatus = fanStatus
        this.fanLevel = fanLevel
        this.fanSpeedStatus = fanSpeedStatus
    }

    override fun applyTransformation(interpolatedTime: Float, transformation: Transformation?) {

        val angle = oldAngle + (newAngle - oldAngle) * interpolatedTime
        circle?.setCircleAngle(angle, fanStatus, fanSpeedStatus, fanLevel)
        circle?.requestLayout()
    }
}