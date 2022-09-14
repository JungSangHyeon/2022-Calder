package com.example.calder

import android.graphics.*
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    companion object {
        private const val frameDelay = 16L // 16ms. for 60 FPS

        private const val complexity = 5
        private const val canvasFillRatio = 0.8f
    }

    private var canvasSize = mutableStateOf<IntSize?>(null)
    private var compositionBitmap = mutableStateOf<Bitmap?>(null)
    private var firstInit = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LaunchedEffect(canvasSize.value) {
                canvasSize.value?.let { size ->
                    if (firstInit) {
                        firstInit = false
                        init(size)
                    }
                }
            }

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned {
                        canvasSize.value = it.size
                    }
            ) {
                compositionBitmap.value?.let {
                    drawImage(
                        image = it.asImageBitmap(),
                        topLeft = Offset(
                            center.x - it.width / 2,
                            center.y - it.height / 2
                        )
                    )
                }
            }
        }
    }

    private fun init(size: IntSize) {
        val adjustedSize = size.width.coerceAtMost(size.height) * canvasFillRatio
        val bitmapSize = RectF(0f, 0f, adjustedSize, adjustedSize)

        val compositionRectArray = (0..complexity).map {
            CompositionRect(bitmapSize)
        }

        lifecycleScope.launch {
            while (true) {
                val temp = Bitmap.createBitmap(
                    adjustedSize.toInt(),
                    adjustedSize.toInt(),
                    Bitmap.Config.ARGB_8888
                )
                Canvas(temp).run {
                    compositionRectArray.forEach {
                        it.applyTime()
                        it.draw(this)
                    }
                    this.drawRect(
                        bitmapSize,
                        Paint().apply {
                            style = Paint.Style.STROKE
                            color = Color.BLACK
                            strokeWidth = 10f
                        }
                    )
                }
                compositionBitmap.value = temp

                delay(frameDelay)
            }
        }
    }
}