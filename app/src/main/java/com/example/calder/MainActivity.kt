package com.example.calder

import android.R.attr.bitmap
import android.graphics.*
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.core.graphics.get
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    private var canvasSize = mutableStateOf<IntSize?>(null)
    private var compositionBitmap = mutableStateOf<Bitmap?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val compositionRectArray = (0 .. 4).map { CompositionRect( RectF(0f, 0f, 1000f, 1000f)) }

        lifecycleScope.launch{
            while (true){
                val temp = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888)
                Canvas(temp).run {
                    compositionRectArray.forEach {
                        it.applyTime()
                        it.draw(this)
                    }
                }
                compositionBitmap.value = temp

                delay(16)
            }
        }

        setContent {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned {
                        canvasSize.value = it.size
                    }
            ){
                compositionBitmap.value?.let {
                    drawImage(
                        image = it.asImageBitmap(),
                        topLeft = Offset(
                            center.x - it.width/2,
                            center.y - it.height/2
                        )
                    )
                }
            }
        }
    }
}

class CompositionRect(
    val parentRect: RectF
){
    enum class CompositionRectType{
        TOP, RIGHT, BOTTOM, LEFT
    }

    private val compositionRectType = CompositionRectType.values().random()
    private val anchorPoint = Point(
        (0 until parentRect.width().toInt()).random(),
        (0 until parentRect.height().toInt()).random(),
    )
    private fun defaultRect() = when(compositionRectType){
        CompositionRectType.TOP -> RectF(0f, 0f, parentRect.width(), anchorPoint.y + animateValue)
        CompositionRectType.RIGHT -> RectF(anchorPoint.x - animateValue, 0f, parentRect.width(), parentRect.height())
        CompositionRectType.BOTTOM -> RectF(0f, anchorPoint.y-animateValue, parentRect.width(), parentRect.height())
        CompositionRectType.LEFT -> RectF(0f, 0f, anchorPoint.x+animateValue, parentRect.height())
    }
    private val rectColor = Color.valueOf(
        getRandomColorValue(), getRandomColorValue(), getRandomColorValue()
    ).toArgb()

    private val absolute = (10..50).random()
    private val animateValueRange = (-absolute .. absolute)
    private var animateValueDiff = (-10..10).random()/10f
    private var animateValue = 0f

    fun applyTime() {
        animateValue += animateValueDiff
        if(!animateValueRange.contains(animateValue.toInt())){
            animateValueDiff *= -1
        }
    }

    fun draw(canvas: Canvas){
        canvas.run {
            drawRect(
                defaultRect(),
                Paint().apply {
                    color = rectColor
                    xfermode = PorterDuffXfermode(PorterDuff.Mode.LIGHTEN)
                }
            )
        }
    }

    fun getRandomColorValue() = (0 until 255).random().toFloat()
}