package com.mukundafoods.chimneylauncherproduct.ui.animation.cleaningbackground

import android.view.animation.Animation
import android.view.animation.Transformation

class CleaningAnimationBackground (circle: CleaningCircleBackground, newAngle: Int) : Animation() {

    private var circle: CleaningCircleBackground? = null

    private var oldAngle = 360f
    private var newAngle = 0f

    init {
        oldAngle = circle.getCircleAngle()
        this.newAngle = newAngle.toFloat()
        this.circle = circle
    }

    override fun applyTransformation(interpolatedTime: Float, transformation: Transformation?) {
        val angle = oldAngle - (newAngle - oldAngle) * interpolatedTime
        circle?.setCircleAngle(angle)
        circle?.requestLayout()
    }
}