package ru.vb.practice.presentation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import ru.vb.practice.ui.theme.BlueGray
import ru.vb.practice.ui.theme.Gray
import ru.vb.practice.ui.theme.LightGray
import ru.vb.practice.ui.theme.White

// Shimmer Effect

fun Modifier.shimmerEffect(toShow: Boolean, stripeWidthDp: Dp = 22.dp): Modifier = composed {
    if (toShow) {
        var size by remember { mutableStateOf(IntSize.Zero) }
        val transition = rememberInfiniteTransition(label = "")

        // Получение текущей плотности экрана для преобразования Dp в пиксели
        val density = LocalDensity.current.density
        val stripeWidthPx = with(LocalDensity.current) { stripeWidthDp.toPx() }

        // Анимация для перемещения полоски слева направо
        val startOffsetX by transition.animateFloat(
            initialValue = -stripeWidthPx,
            targetValue = size.width.toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing) // Время и плавность анимации
            ),
            label = ""
        )

        this
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Gray,
                        Gray,
                        White,
                        Gray,
                        Gray
                    ),
                    start = Offset(startOffsetX, 0f),
                    end = Offset(startOffsetX + stripeWidthPx, size.height.toFloat())
                )
            )
            .onGloballyPositioned {
                size = it.size
            }
    } else Modifier
}

// border for blocks
fun Modifier.commonBorder(): Modifier = this
    .border(width = 1.dp, color = LightGray, shape = RoundedCornerShape(12.dp))
