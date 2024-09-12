package ru.vb.practice.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import ru.vb.practice.R
import ru.vb.practice.data.AnalyticsData
import ru.vb.practice.presentation.commonBorder
import ru.vb.practice.ui.theme.BlueGray
import ru.vb.practice.ui.theme.BlueViolet
import ru.vb.practice.ui.theme.DarkBlueGray

@Preview(showBackground = true)
@Composable
fun PreviewNumbers() {
    Numbers(false, null)
}

@Preview(showBackground = true)
@Composable
fun PreviewNumbersIsLoading() {
    Numbers(true, null)
}

@Composable
fun Numbers(isLoading: Boolean, data: AnalyticsData?) {
    var showPopup by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .height(IntrinsicSize.Max)
            .fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            StatisticCard(
                title = stringResource(id = R.string.аverage_check),
                value = formatNumberWithSpaces(data?.averageCheck ?: 0),
                isLoading = isLoading,
                modifier = Modifier
                    .weight(1f)
            )
            StatisticCard(
                title = stringResource(id = R.string.percentag_of_visits),
                value = "${data?.visitPercentageWithAdditionalServices ?: 0}%",
                isLoading = isLoading,
                modifier = Modifier
                    .weight(1f)
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            StatisticCard(
                title = stringResource(id = R.string.revenue_per_day),
                value = formatNumberWithSpaces(data?.dailyRevenue ?: 0),
                isLoading = isLoading,
                modifier = Modifier
                    .weight(1f)
            )
            StatisticCard(
                title = stringResource(id = R.string.returnability),
                value = "${data?.returnRate ?: 0}%",
                isLoading = isLoading,
                isShowIconInfo = true,
                onIconClick = { showPopup = !showPopup },
                showPopup = showPopup,
                popupContent = {
                    Text(
                        text = stringResource(id = R.string.info_returnability),
                        color = Color.White,
                        fontSize = 10.sp,
                        modifier = Modifier
                            .fillMaxWidth(0.46f)
                            .background(
                                color = BlueViolet,
                                shape = RoundedCornerShape(3.dp)
                            )
                            .padding(4.dp)
                    )
                },
                modifier = Modifier
                    .weight(1f)
            )
        }
    }
}

@Composable
fun StatisticCard(
    title: String,
    value: String,
    isLoading: Boolean,
    isShowIconInfo: Boolean = false,
    onIconClick: (() -> Unit)? = null,
    showPopup: Boolean = false,
    popupContent: @Composable() (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val contentPadding = 16.dp
    Column(
        modifier = modifier
            .fillMaxHeight()
            .commonBorder()
    ) {
        Row {
            Text(
                text = title,
                fontSize = 12.sp,
                color = BlueGray,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = contentPadding, top = contentPadding)
            )
            if (isShowIconInfo) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(shape = RoundedCornerShape(12.dp))
                        .clickable { onIconClick?.invoke() }
                        .padding(contentPadding)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_info_square),
                        contentDescription = "",
                        tint = BlueGray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
        Spacer(
            modifier = Modifier
                .size(4.dp)
                .weight(1f)
        )
        LoadingText(
            text = value,
            fontSize = 16.sp,
            color = DarkBlueGray,
            isLoading = isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = contentPadding, bottom = contentPadding, end = contentPadding)
        )
        if (showPopup && isShowIconInfo && popupContent != null) {
            Popup(
                alignment = Alignment.TopEnd,
                offset = IntOffset(0, -140),
                onDismissRequest = { onIconClick?.invoke() }
            ) {
                popupContent()
            }
        }
    }
}

