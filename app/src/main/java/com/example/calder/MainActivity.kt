package com.example.calder

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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