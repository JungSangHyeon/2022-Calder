package com.example.calder

import android.graphics.*

class CompositionRect(
    private val parentRect: RectF
) {
    enum class CompositionRectType { TOP, RIGHT, BOTTOM, LEFT }

    private val compositionRectType = CompositionRectType.values().random()

    private val anchorPoint = Point(
        (0 until parentRect.width().toInt()).random(),
        (0 until parentRect.height().toInt()).random(),
    )

    private val rectPaint = Paint().apply {
        color = Color.valueOf(getRandomColorValue(), getRandomColorValue(), getRandomColorValue()).toArgb()
        xfermode = PorterDuffXfermode(PorterDuff.Mode.LIGHTEN)
    }

    private val animateValueRangeAbsolute = (10..50).random()
    private val animateValueRange = (-animateValueRangeAbsolute..animateValueRangeAbsolute)
    private var animateValueDiff = (-10..10).random() / 10f
    private var animateValue = 0f

    fun applyTime() {
        animateValue += animateValueDiff
        if (!animateValueRange.contains(animateValue.toInt())) {
            animateValueDiff *= -1
        }
    }

    fun draw(canvas: Canvas) {
        canvas.run {
            drawRect(
                getDrawRect(),
                rectPaint
            )
        }
    }

    private fun getDrawRect() = when (compositionRectType) {
        CompositionRectType.TOP -> RectF(0f, 0f, parentRect.width(), anchorPoint.y + animateValue)
        CompositionRectType.RIGHT -> RectF(anchorPoint.x - animateValue, 0f, parentRect.width(), parentRect.height())
        CompositionRectType.BOTTOM -> RectF(0f, anchorPoint.y - animateValue, parentRect.width(), parentRect.height())
        CompositionRectType.LEFT -> RectF(0f, 0f, anchorPoint.x + animateValue, parentRect.height())
    }

    private fun getRandomColorValue() = (0 until 255).random().toFloat()
}