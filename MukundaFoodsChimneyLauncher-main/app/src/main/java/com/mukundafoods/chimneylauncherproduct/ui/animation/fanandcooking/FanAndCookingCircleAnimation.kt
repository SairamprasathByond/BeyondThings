package com.mukundafoods.chimneylauncherproduct.ui.animation.fanandcooking

import android.graphics.Color
import android.view.animation.Animation
import android.view.animation.Transformation

class FanAndCookingCircleAnimation(circle: FanAndCookingCircle, newAngle: Int, isReset : Boolean) : Animation() {

    private var circle: FanAndCookingCircle? = null

    private var oldAngle = 360f
    private var newAngle = 0f
    private var isReset = false

    init {
        oldAngle = circle.getCircleAngle()
        this.newAngle = newAngle.toFloat()
        this.isReset = isReset
        this.circle = circle
    }

    override fun applyTransformation(interpolatedTime: Float, transformation: Transformation?) {
        val angle = if(isReset) (oldAngle + (newAngle - oldAngle) * interpolatedTime) else (oldAngle - (newAngle - oldAngle) * interpolatedTime)
        circle?.setCircleAngle(angle)
        circle?.setColor(if(isReset) Color.BLACK else Color.parseColor("#dac7b2"))
        circle?.requestLayout()
    }

    override fun setAnimationListener(listener: AnimationListener?) {
        super.setAnimationListener(listener)
    }
}