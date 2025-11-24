package com.example.unimusicapp

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val UnimusicIcon: ImageVector = ImageVector.Builder(
    name = "UnimusicIcon",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f
).apply {
    path(
        stroke = SolidColor(Color.White),
        strokeLineWidth = 2.5f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
        fill = null,
        pathFillType = PathFillType.NonZero
    ) {
        moveTo(4f, 12f); lineTo(4f, 20f)
        moveTo(8f, 8f); lineTo(8f, 20f)
        moveTo(12f, 4f); lineTo(12f, 20f)
        moveTo(16f, 8f); lineTo(16f, 20f)
        moveTo(20f, 12f); lineTo(20f, 20f)
    }
}.build()
