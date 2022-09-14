package com.example.calder

import android.graphics.*
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    companion object {
        private const val frameDelay = 16L // 16ms. for 60 FPS

        private const val canvasFillRatio = 0.8f
    }

    private var canvasSize = mutableStateOf<IntSize?>(null)
    private var compositionBitmap = mutableStateOf<Bitmap?>(null)
    private var drawJob: Job? = null

    private var complexity = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LaunchedEffect(canvasSize.value) {
                canvasSize.value?.let { size ->
                    if (drawJob == null) {
                        init(size)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_baseline_refresh_24),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(androidx.compose.ui.graphics.Color.Gray),
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(20))
                            .clickable { refresh() }
                            .border(
                                1.dp,
                                androidx.compose.ui.graphics.Color.LightGray,
                                RoundedCornerShape(20)
                            )
                            .padding(16.dp)
                    )
                }
            }

        }
    }

    private fun refresh() {
        complexity = (1..10).random()
        canvasSize.value?.let { size ->
            drawJob?.cancel()
            init(size)
        }
    }

    private fun init(size: IntSize) {
        val adjustedSize = size.width.coerceAtMost(size.height) * canvasFillRatio
        val bitmapSize = RectF(0f, 0f, adjustedSize, adjustedSize)

        val compositionRectArray = (0 until complexity).map {
            CompositionRect(bitmapSize)
        }

        drawJob = lifecycleScope.launch {
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