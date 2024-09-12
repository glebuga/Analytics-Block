package ru.vb.practice.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import ru.vb.practice.R
import ru.vb.practice.data.AnalyticsData
import ru.vb.practice.presentation.AnalyticsUiState
import ru.vb.practice.presentation.MainViewModel
import ru.vb.practice.presentation.shimmerEffect
import ru.vb.practice.ui.theme.GeometriaMedium
import ru.vb.practice.ui.theme.Gray
import ru.vb.practice.ui.theme.White


@Preview(showBackground = true)
@Composable
fun MainScreen(viewModel: MainViewModel = koinViewModel()) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray)
    ) {
        CompositionLocalProvider(
            LocalTextStyle provides TextStyle(
                fontFamily = GeometriaMedium
            )
        ) {
            Column {
                Header()
                Body(viewModel)
            }
        }
    }
}

@Composable
fun Body(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val data = uiState.data
    val isLoading = uiState.isLoading
    val isDataFound = data != null

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(White)
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        Spacer(modifier = Modifier.size(12.dp))
        Analytics(isDataFound, viewModel, data, isLoading)
        Spacer(modifier = Modifier.size(12.dp))
        when {
            isLoading || isDataFound -> {
                ScrollableContent(isLoading, data)
            }
            else -> {
                Spacer(modifier = Modifier.size(34.dp))
                PlaceholderIcon()
            }
        }
    }
}

@Composable
fun PlaceholderIcon() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .size(242.dp, 186.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_plug),
            contentDescription = "",
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.Center),
            tint = Color.Unspecified
        )
    }
}

@Composable
fun ScrollableContent(isLoading: Boolean, data: AnalyticsData?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Sales(isLoading, data)
        Spacer(modifier = Modifier.size(8.dp))
        Numbers(isLoading, data)
        Spacer(modifier = Modifier.size(20.dp))
        Diagrams(isLoading, data, stringResource(id = R.string.сurrent_revenue))
        Spacer(modifier = Modifier.size(12.dp))
        Diagrams(isLoading, data, stringResource(id = R.string.maximum_goods))
        Spacer(modifier = Modifier.size(12.dp))
        Diagrams(isLoading, data, stringResource(id = R.string.minimum_goods))
        Spacer(modifier = Modifier.size(58.dp))
    }
}

@Composable
fun LoadingText(
    text: String,
    fontSize: TextUnit,
    color: Color,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontSize = fontSize,
        color = color,
        modifier = modifier
            .clip(if (isLoading) RoundedCornerShape(30.dp) else RoundedCornerShape(0.dp))
            .shimmerEffect(isLoading)
            .alpha(if (isLoading) 0f else 1f)
    )
}

fun formatNumberWithSpaces(value: Int): String {
    return String.format("%,d", value).replace(",", " ")
}

@Composable
fun Header() {
    Row(
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .padding(top = 44.dp, bottom = 12.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_salon),
            tint = Color.Unspecified,
            contentDescription = null
        )
        Column(modifier = Modifier.padding(start = 8.dp, top = 2.dp, bottom = 2.dp)) {
            Text(text = stringResource(id = R.string.salon), fontSize = 12.sp)
            Text(text = "TOPGUN Санкт-Петербург ТРК Лето", fontSize = 14.sp)
        }
    }
}
