package com.molohala.grow.designsystem.specific.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisConfig
import co.yml.charts.axis.AxisData
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.molohala.grow.common.chart.GrowChartData
import com.molohala.grow.designsystem.foundation.GrowTheme
import com.molohala.grow.designsystem.foundation.util.GrowPreviews
import com.molohala.grow.designsystem.foundation.util.pxToDp

@Composable
fun GrowChart(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFFF8125),
    background: Color = GrowTheme.colorScheme.backgroundAlt,
    labelColor: Color = GrowTheme.colorScheme.textNormal,
    chartData: GrowChartData
) {

    var size by remember {
        mutableStateOf(IntSize.Zero)
    }

    val steps = 3
    val xAxisData = AxisData.Builder()
        .steps(chartData.points.size - 1)
        .labelData { i -> chartData.points[i].description }
        .axisStepSize(size.width.pxToDp() / chartData.points.size)
        .labelAndAxisLinePadding(8.dp)
        .axisConfig(
            config = AxisConfig(
                isAxisLineRequired = false,
            )
        )
        .axisLabelColor(labelColor)
        .backgroundColor(background)
        .build()

    val yAxisData = AxisData.Builder()
        .steps(steps)
        .labelData { i ->
            val yScale = 100 / steps
            (i * yScale).toString()
        }
        .labelAndAxisLinePadding(8.dp)
        .axisConfig(
            config = AxisConfig(
                isAxisLineRequired = false
            )
        )
        .backgroundColor(background)
        .axisLabelColor(labelColor)
        .build()
    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = chartData.points,
                    LineStyle(
                        color = color
                    ),
                    selectionHighlightPoint = SelectionHighlightPoint(
                        color = color
                    ),
                    shadowUnderLine = ShadowUnderLine(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                color.copy(alpha = 0.4f),
                                color.copy(alpha = 0f)
                            )
                        ),
                        alpha = 1f
                    )
                )
            ),
        ),
        isZoomAllowed = false,
        paddingRight = 0.dp,
        paddingTop = 16.dp,
        bottomPadding = 0.dp,
        containerPaddingEnd = 0.dp,
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines(
            enableVerticalLines = false
        ),
        backgroundColor = background
    )
    LineChart(
        modifier = modifier
            .fillMaxWidth()
            .background(background)
            .onSizeChanged {
                size = it
            },
        lineChartData = lineChartData
    )
}

@Composable
@GrowPreviews
fun ChartPreview() {
    GrowTheme {
        GrowChart(
            modifier = Modifier
                .height(200.dp),
            chartData = GrowChartData.dummy,
            background = GrowTheme.colorScheme.backgroundAlt
        )
    }
}