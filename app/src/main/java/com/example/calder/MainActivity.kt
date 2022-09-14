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


class MainActivity : ComponentActivity() {

    private var canvasSize = mutableStateOf<IntSize?>(null)
    private var compositionBitmap = mutableStateOf<Bitmap?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val compositionRectArray = (0 .. 4).map { CompositionRect( Rect(0, 0, 1000, 1000)) }
        val temp = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888)
        Canvas(temp).run {
            compositionRectArray.forEach {
                it.draw(this)
            }
        }
        compositionBitmap.value = temp

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
    parentRect: Rect
){
    enum class CompositionRectType{
        TOP, RIGHT, BOTTOM, LEFT
    }

    val compositionRectType = CompositionRectType.values().random()

    val anchorPoint = Point(
        (0 until parentRect.width()).random(),
        (0 until parentRect.height()).random(),
    )

    var defaultRect = when(compositionRectType){
        CompositionRectType.TOP -> Rect(0, 0, parentRect.width(), anchorPoint.y)
        CompositionRectType.RIGHT -> Rect(anchorPoint.x, 0, parentRect.width(), parentRect.height())
        CompositionRectType.BOTTOM -> Rect(0, anchorPoint.y, parentRect.width(), parentRect.height())
        CompositionRectType.LEFT -> Rect(0, 0, anchorPoint.x, parentRect.height())
    }

    var rectColor = Color.valueOf(
        getRandomColorValue(), getRandomColorValue(), getRandomColorValue()
    ).toArgb()

    fun draw(canvas: Canvas){
        canvas.run {
            drawRect(
                defaultRect,
                Paint().apply {
                    color = rectColor
                    xfermode = PorterDuffXfermode(PorterDuff.Mode.LIGHTEN)
                }
            )
        }
    }

    fun getRandomColorValue() = (0 until 255).random().toFloat()
}