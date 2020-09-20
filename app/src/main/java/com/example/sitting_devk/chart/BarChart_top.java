package com.example.sitting_devk.chart;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.example.sitting_devk.R;
import com.example.sitting_devk.constant.Constants;
import com.example.sitting_devk.customRenderer.CustomBarChartRenderer;
import com.example.sitting_devk.databinding.ActivityMainBinding;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

public class BarChart_top {
    private Random random = new Random();
    private BarChart barChart;
    private Context context;
    private ActivityMainBinding binding;
    private ArrayList<BarEntry> entries;


    public BarChart_top(Context context, ActivityMainBinding binding) {
        this.binding = binding;
        this.barChart = binding.barChart;
        this.context = context;
    }

    public void setBarChart() {
        setCustomRenderer(barChart);
        setDefalutSettings();
        setAxisSettings();
        barChart.setData(getBarData());
        setTv_time();
        barChart.invalidate();
    }

    private void setCustomRenderer(BarChart barChart) {
        ChartAnimator chartAnimator = barChart.getAnimator();
        ViewPortHandler viewPortHandler = barChart.getViewPortHandler();
        CustomBarChartRenderer customBarChartRenderer = new CustomBarChartRenderer(context, barChart, chartAnimator, viewPortHandler);
        barChart.setRenderer(customBarChartRenderer);
    }

    private void setDefalutSettings() {
        barChart.getLegend().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.zoom(2.8f, 0, 0, 0);
        barChart.setScaleEnabled(false);
        barChart.setExtraBottomOffset(12f);
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
        xAxis.setDrawLabels(false);
        xAxis.setAxisMinimum(-0.9f);
        xAxis.setAxisMaximum(30f);
    }

    private void setYLAxis(BarChart barChart) {
        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setDrawAxisLine(false);
        yAxis.setDrawGridLines(false);
        yAxis.setDrawLabels(false);
        yAxis.setAxisMinimum(-2);
    }

    private void setYRAxis(BarChart barChart) {
        YAxis yAxis = barChart.getAxisRight();
        yAxis.setDrawAxisLine(false);
        yAxis.setDrawGridLines(false);
        yAxis.setDrawLabels(false);
    }

    private BarData getBarData() {
        BarData barData = new BarData(getBarDataSet());
        barData.setBarWidth(0.95f);
        barData.setDrawValues(false);
        barData.setHighlightEnabled(false);
        return barData;
    }

    private BarDataSet getBarDataSet() {
        BarDataSet barDataSet = new BarDataSet(getBarEntryList(), "");
        barDataSet.setColor(getColorVal(R.color.gray_dark2));
        return barDataSet;
    }

    private ArrayList<BarEntry> getBarEntryList() {
        entries = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            BarEntry barEntry = getBarEntry(i);
            entries.add(i, barEntry);
        }
        return entries;
    }

    private void setTv_time() {
        float totalTime = 0;
        float avgTime = 0;

        for (BarEntry barEntry : entries) {
            totalTime = totalTime + barEntry.getY();
        }

        avgTime = totalTime / 30;
        setAvgTime(avgTime);
        setTotlaTime(totalTime);
    }


    private void setAvgTime(float avgTime) {
        binding.tvAvgTimeDay.setText(Constants.decimalFormat.format(avgTime));
    }

    private void setTotlaTime(float totlaTime) {
        binding.tvTotalTimeMonth.setText(String.format("total: %s", Constants.decimalFormat.format(totlaTime)));
    }

    private BarEntry getBarEntry(int idx) {
        String month = String.valueOf(idx + 1);
        //double로 초기화 불가하여 float으로 받았습니다.
        return new BarEntry(idx, (float) getSittingTime(), month);
    }

    private double getSittingTime() {
        double time = 14 * random.nextDouble();
        return time;
    }

    private int getColorVal(int colorId) {
        return ContextCompat.getColor(context, colorId);
    }

}
