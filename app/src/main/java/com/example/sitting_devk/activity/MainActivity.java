package com.example.sitting_devk.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.sitting_devk.R;
import com.example.sitting_devk.chart.BarChart_bottom;
import com.example.sitting_devk.chart.BarChart_top;
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

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
    }

    private void init() {
        setBinding();
        setCharts();
    }

    private void setCharts() {
        new BarChart_top(this, binding).setBarChart();
        new BarChart_bottom(this, binding).setBarChart();
    }
}