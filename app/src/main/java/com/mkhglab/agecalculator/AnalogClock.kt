package com.mkhglab.agecalculator

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import android.graphics.Paint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.compose.material3.MaterialTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.time.LocalTime
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.ui.graphics.toArgb

@Composable
fun AnalogClock(
    clockSize: Dp,
    modifier: Modifier = Modifier,
    clockColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    borderColor: Color = MaterialTheme.colorScheme.primary,
    tickColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    handColor: Color = MaterialTheme.colorScheme.onSurface,
    secondHandColor: Color = MaterialTheme.colorScheme.secondary,
    numberColor: Color = MaterialTheme.colorScheme.onSurface,
    numberSize: TextUnit = 18.sp,
    showNumbers: Boolean = true
) {
    val time by rememberCurrentTime()

    Canvas(modifier = modifier.size(clockSize)) {
        val canvasSize = this.size
        val radius = canvasSize.minDimension / 2f
        val center = this.center
        val numberPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = numberColor.toArgb()
            textAlign = Paint.Align.CENTER
            textSize = numberSize.toPx()
        }

        drawCircle(color = clockColor, radius = radius)
        drawCircle(color = borderColor, radius = radius, style = Stroke(width = radius * 0.04f))

        repeat(60) { tick ->
            val angleRad = Math.toRadians((tick * 6f - 90f).toDouble())
            val major = tick % 5 == 0
            val lineLength = if (major) radius * 0.14f else radius * 0.07f
            val start = Offset(
                x = center.x + cos(angleRad).toFloat() * (radius - lineLength),
                y = center.y + sin(angleRad).toFloat() * (radius - lineLength)
            )
            val end = Offset(
                x = center.x + cos(angleRad).toFloat() * (radius - 6f),
                y = center.y + sin(angleRad).toFloat() * (radius - 6f)
            )
            drawLine(
                color = tickColor,
                start = start,
                end = end,
                strokeWidth = if (major) 5f else 2f,
                cap = StrokeCap.Round
            )
        }

        if (showNumbers) {
            val numberRadius = radius * 0.78f
            for (number in 1..12) {
                val angleDeg = number * 30f - 90f
                val angleRad = Math.toRadians(angleDeg.toDouble())
                val x = center.x + cos(angleRad).toFloat() * numberRadius
                val y = center.y + sin(angleRad).toFloat() * numberRadius + numberPaint.textSize / 3f
                drawContext.canvas.nativeCanvas.drawText(number.toString(), x, y, numberPaint)
            }
        }

        val hourAngle = ((time.hour % 12) + time.minute / 60f + time.second / 3600f) * 30f - 90f
        val minuteAngle = (time.minute + time.second / 60f) * 6f - 90f
        val secondAngle = time.second * 6f - 90f

        fun handEnd(angle: Float, handLength: Float): Offset {
            val rad = Math.toRadians(angle.toDouble())
            return Offset(
                x = center.x + cos(rad).toFloat() * handLength,
                y = center.y + sin(rad).toFloat() * handLength
            )
        }

        drawLine(
            color = handColor,
            start = center,
            end = handEnd(hourAngle, radius * 0.45f),
            strokeWidth = radius * 0.05f,
            cap = StrokeCap.Round
        )

        drawLine(
            color = handColor,
            start = center,
            end = handEnd(minuteAngle, radius * 0.65f),
            strokeWidth = radius * 0.035f,
            cap = StrokeCap.Round
        )

        drawLine(
            color = secondHandColor,
            start = center,
            end = handEnd(secondAngle, radius * 0.75f),
            strokeWidth = radius * 0.02f,
            cap = StrokeCap.Round
        )

        drawCircle(color = secondHandColor, radius = radius * 0.06f, center = center)
    }
}

@Composable
private fun rememberCurrentTime(): State<LocalTime> {
    return produceState(initialValue = LocalTime.now().withNano(0)) {
        while (isActive) {
            val now = LocalTime.now().withNano(0)
            value = now
            val millisToNextSecond = 1_000 - (System.currentTimeMillis() % 1_000)
            delay(millisToNextSecond)
        }
    }
}
