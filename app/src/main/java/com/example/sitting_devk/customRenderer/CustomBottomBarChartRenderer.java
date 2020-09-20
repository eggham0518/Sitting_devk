package com.example.sitting_devk.customRenderer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import com.example.sitting_devk.R;
import com.example.sitting_devk.util.ColorUtil;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.highlight.Range;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.model.GradientColor;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class CustomBottomBarChartRenderer extends BarChartRenderer {
    private Paint mAxisLinePaint = new Paint();
    private Paint centerLinePaint = new Paint();
    private Paint thinLinePaint = new Paint();


    public CustomBottomBarChartRenderer(Context context, BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);

        int axisColor = ColorUtil.getColorUtil().getColorVal(context, R.color.navy_light);
        mAxisLinePaint.setColor(axisColor);
        mAxisLinePaint.setStrokeWidth(Utils.convertDpToPixel(3));

        centerLinePaint.setColor(axisColor);
        centerLinePaint.setStrokeWidth(Utils.convertDpToPixel(1.5f));

        thinLinePaint.setColor(axisColor);
        thinLinePaint.setStrokeWidth(Utils.convertDpToPixel(1f));

        Paint timePaint = new Paint();
        timePaint.setColor(axisColor);
        timePaint.setTextSize(Utils.convertDpToPixel(12f));
        timePaint.descent();

    }

    private RectF mBarShadowRectBuffer = new RectF();

    @Override
    protected void drawDataSet(Canvas c, IBarDataSet dataSet, int index) {
        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        mBarBorderPaint.setColor(dataSet.getBarBorderColor());
        mBarBorderPaint.setStrokeWidth(Utils.convertDpToPixel(dataSet.getBarBorderWidth()));

        final boolean drawBorder = dataSet.getBarBorderWidth() > 0.f;

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        // draw the bar shadow before the values
        if (mChart.isDrawBarShadowEnabled()) {
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

                c.drawRect(mBarShadowRectBuffer, mShadowPaint);
            }
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


            float axisVerticalMargin = Utils.convertPixelsToDp(40);

            float left = buffer.buffer[j];
            float top = mViewPortHandler.contentTop() + axisVerticalMargin;
            float right = buffer.buffer[j + 2];
            float bottom = mViewPortHandler.contentBottom() - axisVerticalMargin;

            renderBar(c, left, top, right, bottom);

            if (drawBorder) {
                c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3], mBarBorderPaint);
            }

        }
    }

    private void renderThinLine(Canvas c) {

        float verticalMargin = Utils.convertPixelsToDp(15);

        float xpos_left = (mViewPortHandler.contentLeft() + mViewPortHandler.getContentCenter().getX()) / 2;
        float xpos_right = (mViewPortHandler.contentRight() + mViewPortHandler.getContentCenter().getX()) / 2;

        c.drawLine(xpos_left, mViewPortHandler.contentTop() + verticalMargin, xpos_left,
                mViewPortHandler.contentBottom() - verticalMargin, thinLinePaint);

        c.drawLine(xpos_right, mViewPortHandler.contentTop() + verticalMargin, xpos_right,
                mViewPortHandler.contentBottom() - verticalMargin, thinLinePaint);
    }

    private void renderBar(Canvas c, float left, float top, float right, float bottom) {
        c.drawRect(left, top, right, bottom, mRenderPaint);

    }

    private void renderCenterLine(Canvas c) {
        float verticalMargin = Utils.convertPixelsToDp(15);
        c.drawLine(mViewPortHandler.getContentCenter().getX(), mViewPortHandler.contentTop() + verticalMargin,
                mViewPortHandler.getContentCenter().getX(), mViewPortHandler.contentBottom() - verticalMargin,
                centerLinePaint);
    }

    public void renderAxisLine(Canvas c) {
        c.drawLine(mViewPortHandler.contentLeft(), mViewPortHandler.contentTop(), mViewPortHandler.contentLeft(),
                mViewPortHandler.contentBottom(), mAxisLinePaint);

        c.drawLine(mViewPortHandler.contentRight(), mViewPortHandler.contentTop(), mViewPortHandler.contentRight(),
                mViewPortHandler.contentBottom(), mAxisLinePaint);
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {
        BarData barData = mChart.getBarData();

        for (Highlight high : indices) {

            IBarDataSet set = barData.getDataSetByIndex(high.getDataSetIndex());

            if (set == null || !set.isHighlightEnabled())
                continue;

            BarEntry e = set.getEntryForXValue(high.getX(), high.getY());

            if (!isInBoundsX(e, set))
                continue;

            Transformer trans = mChart.getTransformer(set.getAxisDependency());

            mHighlightPaint.setColor(set.getHighLightColor());
            mHighlightPaint.setAlpha(255);

            boolean isStack = (high.getStackIndex() >= 0 && e.isStacked()) ? true : false;

            final float y1;
            final float y2;

            if (isStack) {

                if (mChart.isHighlightFullBarEnabled()) {

                    y1 = e.getPositiveSum();
                    y2 = -e.getNegativeSum();

                } else {

                    Range range = e.getRanges()[high.getStackIndex()];

                    y1 = range.from;
                    y2 = range.to;
                }

            } else {
                y1 = e.getY();
                y2 = 0.f;
            }

            prepareBarHighlight(e.getX(), y1, y2, barData.getBarWidth() / 2f, trans);

            setHighlightDrawPos(high, mBarRect);

            float verticalMargin = Utils.convertPixelsToDp(40);
            mBarRect.set(mBarRect.left, mViewPortHandler.contentTop() + verticalMargin, mBarRect.right, mViewPortHandler.contentBottom() - verticalMargin);
            c.drawRect(mBarRect, mHighlightPaint);

            renderCenterLine(c);
            renderThinLine(c);
            renderAxisLine(c);
        }
    }
}
