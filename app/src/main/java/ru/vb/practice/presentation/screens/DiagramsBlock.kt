package ru.vb.practice.presentation.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import ru.vb.practice.R
import ru.vb.practice.data.AnalyticsData
import ru.vb.practice.presentation.commonBorder
import ru.vb.practice.presentation.shimmerEffect
import ru.vb.practice.ui.theme.BlueGray
import ru.vb.practice.ui.theme.CyanBlue
import ru.vb.practice.ui.theme.DarkBlueGray
import ru.vb.practice.ui.theme.Gray
import ru.vb.practice.ui.theme.NavyBlue
import ru.vb.practice.ui.theme.SkyBlue
import ru.vb.practice.ui.theme.White
import kotlin.math.cos
import kotlin.math.sin

@Preview(showBackground = true)
@Composable
fun PreviewDiagramsIsLoading() {
    Diagrams(true, null, stringResource(id = R.string.сurrent_revenue))
}

@Preview(showBackground = true)
@Composable
fun PreviewDiagrams() {
    Diagrams(false, null, stringResource(id = R.string.сurrent_revenue))
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Diagrams(isLoading: Boolean, data: AnalyticsData?, header: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .commonBorder()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = header, color = DarkBlueGray, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.size(12.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MultiStyleText(
                stringResource(id = R.string.visits),
                BlueGray,
                formatNumberWithSpaces(data?.visits ?: 0),
                DarkBlueGray,
                isLoading
            )
            MultiStyleText(
                stringResource(id = R.string.percentage_of_salary),
                BlueGray,
                (data?.salaryPercentage ?: 0).toString() + "%",
                DarkBlueGray,
                isLoading
            )
            MultiStyleText(
                stringResource(id = R.string.аverage_check) + ":",
                BlueGray,
                formatNumberWithSpaces(data?.averageCheck ?: 0),
                DarkBlueGray,
                isLoading
            )
        }
        Spacer(modifier = Modifier.size(12.dp))
        val segments = listOf(
            Segment(
                value = (data?.mainServicesRevenue?.toDouble() ?: 0.0).toFloat(),
                color = SkyBlue,
                isVisible = true,
                buttonText = stringResource(id = R.string.main_services)
            ),
            Segment(
                value = (data?.additionalServicesRevenue?.toDouble() ?: 0.0).toFloat(),
                color = CyanBlue,
                isVisible = true,
                buttonText = stringResource(id = R.string.additional_services)
            ),
            Segment(
                value = (data?.productRevenue?.toDouble() ?: 0.0).toFloat(),
                color = NavyBlue,
                isVisible = true,
                buttonText = stringResource(id = R.string.on_goods)
            )
        )

        DonutChart(
            segments = segments,
            isLoading = isLoading
        )
    }
}

data class Segment(
    val value: Float,
    val color: Color,
    val isVisible: Boolean,
    val buttonText: String
)

// Функция для генерации текстовых меток из значений сегментов
fun formatSegmentText(value: Float): String {
    return when {
        value >= 1_000_000_000 -> "${(value / 1_000_000_000).toInt()}B"
        value >= 1_000_000 -> "${(value / 1_000_000).toInt()}M"
        value >= 1_000 -> "${(value / 1_000).toInt()}k"
        else -> value.toInt().toString()
    }
}

@Composable
fun DonutChart(
    segments: List<Segment>,
    isLoading: Boolean
) {

    val minAngle = 36f // Минимальный угол для сегмента

    // Cписок видимости сегментов
    val visibilityState = remember { mutableStateOf(segments.map { it.isVisible }) }

    // Фильтрация видимых сегментов
    val visibleSegments = segments.filterIndexed { index, _ -> visibilityState.value[index] }

    // Вычисляем суммарное значение видимых сегментов
    val totalVisibleValue = visibleSegments.sumOf { it.value.toInt() }.toFloat()

    // Если все видимые сегменты имеют нулевое значение, отображаем один сегмент с минимальным углом
    val adjustedAngles = visibleSegments.map { segment ->
        // Угол, который сегмент занимает на диаграмме, пропорционален его значению относительно общей суммы видимых сегментов
        val rawAngle = if (totalVisibleValue == 0f) {
            0f
        } else {
            segment.value / totalVisibleValue * 360f
        }

        if (rawAngle < minAngle) minAngle else rawAngle
    }

    // Корректируем углы так, чтобы сумма всех углов была 360 градусов
    val totalAdjustedAngle = adjustedAngles.sum().takeIf { it > 0 } ?: 360f
    val correctionFactor = 360f / totalAdjustedAngle
    val finalAngles = adjustedAngles.map { it * correctionFactor }

    // Cписок для хранения углов сегментов, которые будут анимироваться
    val animatedSweepAngles = remember {
        List(segments.size) { Animatable(0f) }
    }

    // Cписок текстов для каждого сегмента, форматируя значения сегментов
    val segmentTexts = segments.map { formatSegmentText(it.value) }

    // Обрабатываем изменение видимости и обновляем анимации
    LaunchedEffect(segments, visibilityState.value) {
        segments.forEachIndexed { index, segment ->
            launch {
                val targetAngle = if (visibilityState.value[index]) {
                    val visibleIndex = visibleSegments.indexOf(segment)
                    if (visibleIndex != -1) finalAngles[visibleIndex] else 0f
                } else {
                    0f
                }
                if (!targetAngle.isNaN()) {
                    animatedSweepAngles[index].animateTo(
                        targetValue = targetAngle,
                        animationSpec = tween(durationMillis = 300, easing = FastOutLinearInEasing)
                    )
                } else {
                    animatedSweepAngles[index].snapTo(0f)
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            if (isLoading) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape)
                        .background(Gray)
                ) {
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .background(White)
                    )
                }
            } else {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    var startAngle = 270f
                    val strokeWidth = 100f
                    val radius = size.minDimension / 2 - strokeWidth / 2
                    val gapAngle = 1f

                    segments.forEachIndexed { index, segment ->
                        val sweepAngle = animatedSweepAngles[index].value - gapAngle
                        if (sweepAngle > 0) {
                            drawArc(
                                color = segment.color,
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                topLeft = Offset(size.width / 2 - radius, size.height / 2 - radius),
                                size = Size(radius * 2, radius * 2),
                                style = Stroke(width = strokeWidth)
                            )

                            // Проверяем, виден ли сегмент, перед тем как рисовать текст
                            if (visibilityState.value[index]) {
                                val angleInRad = Math.toRadians((startAngle + sweepAngle / 2).toDouble())
                                val textX = (size.width / 2 + radius * cos(angleInRad)).toFloat()
                                val textY = (size.height / 2 + radius * sin(angleInRad)).toFloat() + 10

                                drawContext.canvas.nativeCanvas.apply {
                                    val textPaint = Paint().asFrameworkPaint().apply {
                                        isAntiAlias = true
                                        textSize = 30f
                                        color = android.graphics.Color.WHITE
                                        textAlign = android.graphics.Paint.Align.CENTER
                                    }
                                    drawText(segmentTexts[index], textX, textY, textPaint)
                                }
                            }

                            startAngle += sweepAngle + gapAngle
                        }
                    }
                }
            }

            // Отображение текста в центре диаграммы
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.salary),
                    fontSize = 12.sp,
                    color = BlueGray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.size(4.dp))
                LoadingText(
                    text = formatNumberWithSpaces(totalVisibleValue.toInt()),
                    fontSize = 12.sp,
                    color = DarkBlueGray,
                    isLoading = isLoading,
                    modifier = if (isLoading) Modifier.size(57.dp, 16.dp) else Modifier
                )
            }
        }

        // Кнопки для управления видимостью
        VisibilityControlButtons(
            segments = segments,
            visibilityState = visibilityState,
            isLoading = isLoading
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VisibilityControlButtons(
    segments: List<Segment>,
    visibilityState: MutableState<List<Boolean>>,
    isLoading: Boolean
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
    ) {
        segments.forEachIndexed { index, segment ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(if (isLoading) RoundedCornerShape(30.dp) else CircleShape)
                        .background(
                            if (isLoading) Color.Transparent else segment.color,
                            shape = CircleShape
                        )
                        .shimmerEffect(isLoading),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                if (!isLoading) {
                                    val newVisibilityState = visibilityState.value.toMutableList()
                                    if (newVisibilityState[index]) {
                                        newVisibilityState[index] = false
                                    } else {
                                        newVisibilityState[index] = true
                                    }
                                    visibilityState.value = newVisibilityState
                                }
                            }
                        )
                        .sizeIn(minWidth = 48.dp, minHeight = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        fontSize = 12.sp,
                        text = segment.buttonText,
                        color = BlueGray,
                        textDecoration = if (visibilityState.value[index])
                            TextDecoration.None else TextDecoration.LineThrough
                    )
                }
            }
        }
    }
}

@Composable
fun MultiStyleText(
    text1: String, color1: Color, text2: String, color2: Color, isLoading: Boolean
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text1, color = color1, fontSize = 12.sp
        )
        LoadingText(
            text = text2,
            fontSize = 12.sp,
            color = color2,
            isLoading = isLoading,
            modifier = if (isLoading) Modifier.size(32.dp, 16.dp) else Modifier
        )
    }
}
