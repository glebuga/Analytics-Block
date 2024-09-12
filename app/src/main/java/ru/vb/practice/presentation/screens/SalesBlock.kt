package ru.vb.practice.presentation.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.vb.practice.R
import ru.vb.practice.data.AnalyticsData
import ru.vb.practice.presentation.commonBorder
import ru.vb.practice.ui.theme.BlueGray
import ru.vb.practice.ui.theme.DarkBlueGray
import ru.vb.practice.ui.theme.LightGray
import ru.vb.practice.ui.theme.NavyBlue

@Preview(showBackground = true)
@Composable
fun PreviewSalesIsLoading() {
    Sales(true, null)
}

@Preview(showBackground = true)
@Composable
fun PreviewSales() {
    Sales(false, null)
}

@Composable
fun Sales(isLoading: Boolean, data: AnalyticsData?) {

    val percentage = if (data != null && data.targetProductSales > 0) {
        (data.productSales * 100) / data.targetProductSales
    } else {
        0
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .commonBorder()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(id = R.string.sales_of_goods),
            fontSize = 12.sp,
            color = BlueGray
        )
        LoadingText(
            text = formatNumberWithSpaces(data?.productSales ?: 0),
            fontSize = 16.sp,
            color = DarkBlueGray,
            isLoading = isLoading,
            modifier = Modifier
                .fillMaxWidth(0.5f)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LoadingText(
                text = "$percentage%",
                fontSize = 12.sp,
                color = NavyBlue,
                isLoading = isLoading,
                modifier = if (isLoading) Modifier.size(40.dp, 16.dp) else Modifier
            )
            LoadingText(
                text = "100%",
                fontSize = 12.sp,
                color = BlueGray,
                isLoading = isLoading,
                modifier = if (isLoading) Modifier.size(40.dp, 16.dp) else Modifier
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(LightGray, RoundedCornerShape(12.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(if (isLoading) 0.00f else percentage / 100f)
                    .padding(1.dp)
                    .height(4.dp)
                    .align(Alignment.CenterStart)
                    .clip(RoundedCornerShape(12.dp))
                    .background(NavyBlue)
            )
        }
    }
}

