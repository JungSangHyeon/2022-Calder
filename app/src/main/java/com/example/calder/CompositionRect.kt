package com.example.calder

import android.graphics.*

class CompositionRect(
    private val parentRect: RectF
) {
    enum class CompositionRectType { TOP, RIGHT, BOTTOM, LEFT }

    private val anchorPoint = Point(
        (0 until parentRect.width().toInt()).random(),
        (0 until parentRect.height().toInt()).random(),
    )

    private val compositionRectType = run{
        val temp = CompositionRectType.values().random()
        when{
            anchorPoint.x > parentRect.width()/2 && temp == CompositionRectType.LEFT -> CompositionRectType.RIGHT
            anchorPoint.x < parentRect.width()/2 && temp == CompositionRectType.RIGHT -> CompositionRectType.LEFT
            anchorPoint.y < parentRect.height()/2 && temp == CompositionRectType.BOTTOM -> CompositionRectType.TOP
            anchorPoint.y > parentRect.height()/2 && temp == CompositionRectType.TOP -> CompositionRectType.BOTTOM
            else -> temp
        }
    }

    private val rectPaint = Paint().apply {
        color = listOf( // Mondrian's color set
            Color.rgb(228, 55, 50),
            Color.rgb(53, 122, 190),
            Color.rgb(255, 255, 84),
            Color.WHITE
        ).random()
        strokeWidth = 10f
    }
    private val borderPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        this.strokeWidth = 10f
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
            val rect = getDrawRect()
            drawRect(rect, rectPaint)
            drawRect(rect, borderPaint)
        }
    }

    private fun getDrawRect() = when (compositionRectType) {
        CompositionRectType.TOP -> RectF(0f, 0f, parentRect.width(), anchorPoint.y + animateValue)
        CompositionRectType.RIGHT -> RectF(anchorPoint.x - animateValue, 0f, parentRect.width(), parentRect.height())
        CompositionRectType.BOTTOM -> RectF(0f, anchorPoint.y - animateValue, parentRect.width(), parentRect.height())
        CompositionRectType.LEFT -> RectF(0f, 0f, anchorPoint.x + animateValue, parentRect.height())
    }
}