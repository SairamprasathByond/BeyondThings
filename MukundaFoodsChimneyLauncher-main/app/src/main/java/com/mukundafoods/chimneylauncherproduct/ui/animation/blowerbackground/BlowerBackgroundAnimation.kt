package com.mukundafoods.chimneylauncherproduct.ui.animation.blowerbackground

import android.view.animation.Animation
import android.view.animation.Transformation

class BlowerBackgroundAnimation(circle: BlowerBackgroundCircle, newAngle: Int) : Animation() {

    private var circle: BlowerBackgroundCircle? = null

    private var oldAngle = 0f
    private var newAngle = 0f

    init {
        oldAngle = circle.getCircleAngle()
        this.newAngle = newAngle.toFloat()
        this.circle = circle

    }

    override fun applyTransformation(interpolatedTime: Float, transformation: Transformation?) {
        val angle = oldAngle + (newAngle - oldAngle) * interpolatedTime
        circle?.setCircleAngle(angle)
        circle?.requestLayout()
    }
}