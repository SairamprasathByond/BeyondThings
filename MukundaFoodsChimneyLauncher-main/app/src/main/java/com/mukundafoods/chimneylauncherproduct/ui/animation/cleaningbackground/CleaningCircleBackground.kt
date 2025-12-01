package com.mukundafoods.chimneylauncherproduct.ui.animation.cleaningbackground

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class CleaningCircleBackground(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint: Paint
    private val rect: RectF

    private var angle = 0f

    companion object {
        private const val START_ANGLE_POINT = 270f
    }

    init {
        val strokeWidth = 20F

        paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            setStrokeWidth(strokeWidth)
            color = Color.BLACK
        }

        val circleSize = 200F

        rect = RectF(
            strokeWidth, strokeWidth, circleSize + strokeWidth, circleSize + strokeWidth
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val circleSize = 200
        val strokeWidth = 20

        super.onMeasure(
            MeasureSpec.makeMeasureSpec(circleSize + 2 * strokeWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(circleSize + 2 * strokeWidth, MeasureSpec.EXACTLY)
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawArc(rect, START_ANGLE_POINT, angle, false, paint)
    }

    fun getCircleAngle(): Float {
        return angle
    }

    fun setCircleAngle(angle: Float) {
        this.angle = angle
    }

}