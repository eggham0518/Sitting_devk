package com.example.sitting_devk.chart;

import android.content.Context;
import android.renderscript.ScriptGroup;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.sitting_devk.R;
import com.example.sitting_devk.constant.Constants;
import com.example.sitting_devk.customRenderer.CustomBottomBarChartRenderer;
import com.example.sitting_devk.customRenderer.CustomXAxisRenderer;
import com.example.sitting_devk.databinding.ActivityMainBinding;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.nio.channels.spi.AbstractInterruptibleChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.example.sitting_devk.constant.Constants.xAxisDateFormat;

public class BarChart_bottom {
    private BarChart barChart;
    private Context context;
    private List<Highlight> highlightList = new ArrayList<>();
    private ActivityMainBinding binding;

    public BarChart_bottom(Context context, ActivityMainBinding binding) {
        this.context = context;
        this.binding = binding;
        this.barChart = binding.barChartBottom;
    }

    public void setBarChart() {
        setCustomRenderer(barChart);
        setDefalutSettings();
        setAxisSettings();
        barChart.setData(getBarData());
        setHighlights();
        setTotaltSittingTime();
        setLongestSitting();
        barChart.invalidate();
    }

    private void setHighlights() {
        Highlight[] resultArray = new Highlight[highlightList.size()];
        resultArray = highlightList.toArray(resultArray);
        barChart.highlightValues(resultArray);
    }

    private void setCustomRenderer(BarChart barChart) {
        ChartAnimator chartAnimator = barChart.getAnimator();
        ViewPortHandler viewPortHandler = barChart.getViewPortHandler();
        Transformer transformer = barChart.getTransformer(barChart.getAxisLeft().getAxisDependency());


        CustomBottomBarChartRenderer customBottomBarChartRenderer = new CustomBottomBarChartRenderer(context, barChart, chartAnimator, viewPortHandler);
        barChart.setRenderer(customBottomBarChartRenderer);

        CustomXAxisRenderer customXAxisRenderer = new CustomXAxisRenderer(context, viewPortHandler, barChart.getXAxis(), transformer);
        barChart.setXAxisRenderer(customXAxisRenderer);
    }

    private void setDefalutSettings() {
        barChart.getLegend().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.setScaleEnabled(true);
        barChart.setExtraBottomOffset(12f);
        barChart.setDragXEnabled(true);
        barChart.setDragYEnabled(false);
        barChart.setHighlightPerDragEnabled(false);
        barChart.setHighlightPerTapEnabled(false);
    }

    private void setAxisSettings() {
        setXAxis(barChart);
        setYLAxis(barChart);
        setYRAxis(barChart);
    }

    private void setXAxis(BarChart barChart) {
        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(true);
        xAxis.setLabelCount(3, true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(13f);
        xAxis.setTextColor(getColorVal(R.color.navy_light2));
    }

    private void setYLAxis(BarChart barChart) {
        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setDrawAxisLine(false);
        yAxis.setAxisLineWidth(1);
        yAxis.setAxisLineColor(getColorVal(R.color.navy_dark));
        yAxis.setDrawGridLines(false);
        yAxis.setDrawLabels(false);
    }

    private void setYRAxis(BarChart barChart) {
        YAxis yAxis = barChart.getAxisRight();
        yAxis.setDrawAxisLine(false);
        yAxis.setAxisLineWidth(1);
        yAxis.setAxisLineColor(getColorVal(R.color.navy_dark));
        yAxis.setDrawGridLines(false);
        yAxis.setDrawLabels(false);
    }

    private BarData getBarData() {
        BarData barData = new BarData(getBarDataSet());
        barData.setBarWidth(3f);
        barData.setDrawValues(false);
        barData.setHighlightEnabled(true);
        return barData;
    }

    private BarDataSet getBarDataSet() {
        BarDataSet barDataSet = new BarDataSet(getBarEntryList(), "");
        barDataSet.setHighLightColor(getColorVal(R.color.navy_dark));
        barDataSet.setColor(getColorVal(R.color.ivory_dark));
        return barDataSet;
    }

    private ArrayList<BarEntry> getBarEntryList() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < 1440; i++) {
            entries.add(i, getBarEntry(i));
        }
        return entries;
    }

    private BarEntry getBarEntry(int idx) {
        float verticalHeight = 12;
        boolean isSitting;
        if (idx <= 540) {
            isSitting = false;
        } else if (idx <= 1050) {
            isSitting = true;
        } else if (idx <= 1200) {
            isSitting = false;
        } else if (idx <= 1300) {
            isSitting = true;
        } else {
            isSitting = false;
        }

        if (isSitting) {
            Highlight highlight = new Highlight(idx, verticalHeight, 0);
            highlight.setDataIndex(0);
            highlightList.add(highlight);
        }

        return new BarEntry(idx, verticalHeight, isSitting);
    }

    private void setTotaltSittingTime() {
        float totalTime = highlightList.size() / 60f;
        binding.tvTotalTimeBottom.setText(Constants.decimalFormat.format(totalTime));
    }

    private void setLongestSitting() {
        List<Integer> consecutiveTime = new ArrayList<>();

        int sittingMin = 0;
        float lastTime = highlightList.get(0).getX() - 1;
        float currnetTime;


        for (Highlight highlight : highlightList) {
            currnetTime = highlight.getX();

            if (currnetTime - lastTime > 1) {
                consecutiveTime.add(sittingMin);
                sittingMin = 0;
            }

            lastTime = currnetTime;
            sittingMin++;
        }

        Collections.sort(consecutiveTime);
        float longestTime = consecutiveTime.get(0) / 60f;
        binding.tvLongestSitting.setText(String.format("%s hr", Constants.decimalFormat.format(longestTime)));
    }

    private int getColorVal(int colorId) {
        return ContextCompat.getColor(context, colorId);
    }
}
