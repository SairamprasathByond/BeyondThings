package com.mukundafoods.chimneylauncherproduct.ui.animation.blower

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import com.mukundafoods.chimneylauncherproduct.ui.sharedpreference.Data

class BlowerSpeedCircle(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint: Paint
    private val rect: RectF

    private var angle = 0f

    companion object {
        private const val START_ANGLE_POINT = 145F
    }

    init {
        val strokeWidth = 20F

        paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            setStrokeWidth(strokeWidth)
            color = Color.WHITE
        }

        val circleSize = 150F

        rect = RectF(
            strokeWidth,
            strokeWidth,
            circleSize + strokeWidth,
            circleSize + strokeWidth
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val circleSize = 150
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

    fun setCircleAngle(
        angle: Float,
        fanStatus: TextView,
        fanSpeedStatus: TextView,
        fanSpeedLevel: TextView,
    ) {
        println("Narayan $angle")
        if (Data.getVariant() == 1) {
            if (angle <= 83) {
                fanSpeedLevel.text = "Level-1"
                fanSpeedStatus.text = "Slow"
            } else if (angle <= 166) {
                fanSpeedLevel.text = "Level-2"
                fanSpeedStatus.text = "Medium"
            } else {
                fanSpeedLevel.text = "Level-3"
                fanSpeedStatus.text = "High"
            }
        } else {
            if (angle <= 28) {
                fanSpeedLevel.text = "Level-1"
                fanSpeedStatus.text = "F1"
            } else if (angle <= 56) {
                fanSpeedLevel.text = "Level-2"
                fanSpeedStatus.text = "F2"
            } else if (angle <= 84) {
                fanSpeedLevel.text = "Level-3"
                fanSpeedStatus.text = "F3"
            } else if (angle <= 112) {
                fanSpeedLevel.text = "Level-4"
                fanSpeedStatus.text = "F4"
            } else if (angle <= 140) {
                fanSpeedLevel.text = "Level-5"
                fanSpeedStatus.text = "F5"
            } else if (angle <= 168) {
                fanSpeedLevel.text = "Level-6"
                fanSpeedStatus.text = "F6"
            } else if (angle <= 196) {
                fanSpeedLevel.text = "Level-7"
                fanSpeedStatus.text = "F7"
            }else if (angle <= 224) {
                fanSpeedLevel.text = "Level-8"
                fanSpeedStatus.text = "F8"
            } else {
                fanSpeedLevel.text = "Level-9"
                fanSpeedStatus.text = "F9"
            }
        }



        if (angle <= 0) {
            fanStatus.text = "On"
            fanSpeedStatus.text = "Off"
        } else {
            fanStatus.text = "Off"
        }

        this.angle = if (angle > 249) {
            249F
        } else if (angle <= 0) {
            0F
        } else angle
    }

}