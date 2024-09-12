package ru.vb.practice.presentation.screens

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.vb.practice.R
import ru.vb.practice.data.AnalyticsData
import ru.vb.practice.presentation.MainViewModel
import ru.vb.practice.ui.theme.BlueGray
import ru.vb.practice.ui.theme.DarkBlueGray
import ru.vb.practice.ui.theme.Gray
import ru.vb.practice.ui.theme.IndigoBlue
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun Analytics(
    isDataFound: Boolean,
    viewModel: MainViewModel,
    data: AnalyticsData?,
    isLoading: Boolean
) {
    val today = LocalDate.now()
    val defaultStartDate = today.minusMonths(1)
    var selectedStartDate by remember { mutableStateOf(defaultStartDate) }
    var selectedEndDate by remember { mutableStateOf(today) }

    val isDateChanged = selectedStartDate != defaultStartDate || selectedEndDate != today

    var showDateRangePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.аnalytica),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center),
                color = DarkBlueGray
            )
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.TopEnd)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Gray)
                        .clickable {
                            if (!isLoading) showDateRangePicker = true
                        }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_filter),
                        contentDescription = "date filters",
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.Center)
                    )
                }
                if (isDateChanged) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(IndigoBlue, shape = RoundedCornerShape(4.dp))
                            .align(Alignment.TopStart)
                    )
                }
            }
        }
    }
    Spacer(modifier = Modifier.size(4.dp))
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(
                id = if (isLoading) {
                    R.string.analysis_period
                } else if (isDataFound && data != null) {
                    R.string.analysis_period
                } else {
                    R.string.analysis_period_failed
                },
                selectedStartDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                selectedEndDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
            ),
            fontSize = 14.sp,
            color = BlueGray,
            modifier = Modifier.width(220.dp),
            textAlign = TextAlign.Center
        )
        if (!isDataFound && !isLoading) {
            Text(
                text = stringResource(id = R.string.changing_the_date),
                fontSize = 14.sp,
                color = BlueGray,
                textAlign = TextAlign.Center
            )
        }
    }

    if (showDateRangePicker) {
        DateRangePickerModal(
            onDateRangeSelected = { dateRange ->
                dateRange.first?.let {
                    selectedStartDate = LocalDate.ofEpochDay(it / (24 * 60 * 60 * 1000))
                }
                dateRange.second?.let {
                    selectedEndDate = LocalDate.ofEpochDay(it / (24 * 60 * 60 * 1000))
                }
                viewModel.getAnalyticsData(selectedStartDate, selectedEndDate)
                showDateRangePicker = false
            },
            onDismiss = {
                showDateRangePicker = false
            }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewDateRangePickerModal() {
    DateRangePickerModal(
        onDateRangeSelected = {},
        onDismiss = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerModal(
    onDateRangeSelected: (Pair<Long?, Long?>) -> Unit,
    onDismiss: () -> Unit
) {
    val today = LocalDate.now()
    val dateRangePickerState = rememberDateRangePickerState(
        selectableDates = object : androidx.compose.material3.SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                // Ограничиваем выбор дат будущим временем
                val selectedDate = LocalDate.ofEpochDay(utcTimeMillis / (24 * 60 * 60 * 1000))
                return !selectedDate.isAfter(today)
            }
        }
    )
    DatePickerDialog(
        modifier = Modifier.fillMaxSize().padding(40.dp),
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                modifier = Modifier.padding(end = 24.dp),
                onClick = {
                    val startDateMillis = dateRangePickerState.selectedStartDateMillis
                    val endDateMillis = dateRangePickerState.selectedEndDateMillis

                    // Проверяем, что обе даты выбраны
                    if (startDateMillis != null && endDateMillis != null) {
                        onDateRangeSelected(
                            Pair(startDateMillis, endDateMillis)
                        )
                    } else {
                        // Не передаем никакие значения, если даты не выбраны
                        onDateRangeSelected(Pair(null, null))
                    }
                    onDismiss()
                }
            ) {
                Text(text = stringResource(id = R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.cancel))
            }
        }
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            title = {
                Text(
                    text = stringResource(id = R.string.select_date),
                    modifier = Modifier.padding(
                        start = 24.dp,
                        bottom = 8.dp,
                        top = 12.dp,
                        end = 12.dp
                    )
                )
            },
            headline = {
                Column(
                    modifier = Modifier.padding(start = 24.dp, bottom = 8.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = dateRangePickerState.selectedStartDateMillis?.let {
                            DateFormat.format("dd MMM yyyy", it).toString()
                        } ?: stringResource(id = R.string.start_date)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = dateRangePickerState.selectedEndDateMillis?.let {
                            DateFormat.format("dd MMM yyyy", it).toString()
                        } ?: stringResource(id = R.string.end_date)
                    )
                }
            },
            showModeToggle = true,
            modifier = Modifier
                .height(500.dp)
        )
    }
}




