package com.example.sitting_devk.customRenderer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.sitting_devk.R;
import com.example.sitting_devk.util.ColorUtil;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.model.GradientColor;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.utils.MPPointD;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class CustomBarChartRenderer extends BarChartRenderer {

    private RectF mBarShadowRectBuffer = new RectF();
    private Paint valuePaint = new Paint();
    private Paint zeroLinePaint = new Paint();

    public CustomBarChartRenderer(Context context, BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
        valuePaint.setTextSize(Utils.convertDpToPixel(16));
        valuePaint.setColor(ContextCompat.getColor(context, R.color.gray_dark2));
        valuePaint.setTextAlign(Paint.Align.CENTER);
        valuePaint.descent();
        valuePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        zeroLinePaint.setColor(ColorUtil.getColorUtil().getColorVal(context, R.color.white));
        zeroLinePaint.setStrokeWidth(Utils.convertDpToPixel(1f));
    }

    @Override
    protected void drawDataSet(Canvas c, IBarDataSet dataSet, int index) {
        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        mBarBorderPaint.setColor(dataSet.getBarBorderColor());
        mBarBorderPaint.setStrokeWidth(Utils.convertDpToPixel(dataSet.getBarBorderWidth()));

        final boolean drawBorder = dataSet.getBarBorderWidth() > 0.f;

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();


        // draw the bar shadow before the values

        mShadowPaint.setColor(dataSet.getBarShadowColor());

        BarData barData = mChart.getBarData();

        final float barWidth = barData.getBarWidth();
        final float barWidthHalf = barWidth / 2.0f;
        float x;

        for (int i = 0, count = Math.min((int) (Math.ceil((float) (dataSet.getEntryCount()) * phaseX)), dataSet.getEntryCount());
             i < count;
             i++) {

            BarEntry e = dataSet.getEntryForIndex(i);

            x = e.getX();

            mBarShadowRectBuffer.left = x - barWidthHalf;
            mBarShadowRectBuffer.right = x + barWidthHalf;

            trans.rectValueToPixel(mBarShadowRectBuffer);

            if (!mViewPortHandler.isInBoundsLeft(mBarShadowRectBuffer.right))
                continue;

            if (!mViewPortHandler.isInBoundsRight(mBarShadowRectBuffer.left))
                break;

            mBarShadowRectBuffer.top = mViewPortHandler.contentTop();
            mBarShadowRectBuffer.bottom = mViewPortHandler.contentBottom();

            if (mChart.isDrawBarShadowEnabled()) {
                c.drawRect(mBarShadowRectBuffer, mShadowPaint);
            }

            String day = String.valueOf(e.getData());
            float rectBottom = mBarShadowRectBuffer.bottom;
            float rectCenter = mBarShadowRectBuffer.centerX();
            drawMovingLabel(c, day, rectCenter, rectBottom);
        }

        // initialize the buffer
        BarBuffer buffer = mBarBuffers[index];
        buffer.setPhases(phaseX, phaseY);
        buffer.setDataSet(index);
        buffer.setInverted(mChart.isInverted(dataSet.getAxisDependency()));
        buffer.setBarWidth(mChart.getBarData().getBarWidth());

        buffer.feed(dataSet);

        trans.pointValuesToPixel(buffer.buffer);

        final boolean isSingleColor = dataSet.getColors().size() == 1;

        if (isSingleColor) {
            mRenderPaint.setColor(dataSet.getColor());
        }

        for (int j = 0; j < buffer.size(); j += 4) {

            if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2]))
                continue;

            if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                break;

            if (!isSingleColor) {
                // Set the color for the currently drawn value. If the index
                // is out of bounds, reuse colors.
                mRenderPaint.setColor(dataSet.getColor(j / 4));
            }

            if (dataSet.getGradientColor() != null) {
                GradientColor gradientColor = dataSet.getGradientColor();
                mRenderPaint.setShader(
                        new LinearGradient(
                                buffer.buffer[j],
                                buffer.buffer[j + 3],
                                buffer.buffer[j],
                                buffer.buffer[j + 1],
                                gradientColor.getStartColor(),
                                gradientColor.getEndColor(),
                                android.graphics.Shader.TileMode.MIRROR));
            }

            if (dataSet.getGradientColors() != null) {
                mRenderPaint.setShader(
                        new LinearGradient(
                                buffer.buffer[j],
                                buffer.buffer[j + 3],
                                buffer.buffer[j],
                                buffer.buffer[j + 1],
                                dataSet.getGradientColor(j / 4).getStartColor(),
                                dataSet.getGradientColor(j / 4).getEndColor(),
                                android.graphics.Shader.TileMode.MIRROR));
            }

            c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                    buffer.buffer[j + 3], mRenderPaint);

            renderZeroLine(c, buffer.buffer[j + 3]);
        }


    }

    private void drawMovingLabel(Canvas c, String month, float rectCenter, float rectBottom) {
        c.drawText(month, rectCenter, rectBottom, valuePaint);
    }

    private void renderZeroLine(Canvas c, float bottom) {
        c.drawLine(mViewPortHandler.contentLeft(), bottom, mViewPortHandler.contentRight(), bottom, zeroLinePaint);
    }
}
