package com.example.sitting_devk.customRenderer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.example.sitting_devk.constant.Constants;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.renderer.YAxisRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CustomXAxisRenderer extends XAxisRenderer {
    DateFormat format = new SimpleDateFormat("mm",Locale.getDefault());

    public CustomXAxisRenderer(Context context, ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
        super(viewPortHandler, xAxis, trans);
    }

    protected void drawLabels(Canvas c, float pos, MPPointF anchor) {

        final float labelRotationAngleDegrees = mXAxis.getLabelRotationAngle();
        boolean centeringEnabled = mXAxis.isCenterAxisLabelsEnabled();

        float[] positions = new float[mXAxis.mEntryCount * 2];

        for (int i = 0; i < positions.length; i += 2) {

            // only fill x values
            if (centeringEnabled) {
                positions[i] = mXAxis.mCenteredEntries[i / 2];
            } else {
                positions[i] = mXAxis.mEntries[i / 2];
            }
        }

        mTrans.pointValuesToPixel(positions);
        int idx_label = 0;
        for (int i = 0; i < positions.length; i += 2) {

            float x = positions[i];

            if (mViewPortHandler.isInBoundsX(x)) {

                String label = mXAxis.getValueFormatter().getAxisLabel(mXAxis.mEntries[i / 2], mXAxis);
                if (mXAxis.isAvoidFirstLastClippingEnabled()) {

                    // avoid clipping of the last
                    if (i / 2 == mXAxis.mEntryCount - 1 && mXAxis.mEntryCount > 1) {
                        float width = Utils.calcTextWidth(mAxisLabelPaint, label);

                        if (width > mViewPortHandler.offsetRight() * 2
                                && x + width > mViewPortHandler.getChartWidth())
                            x -= width / 2;

                        // avoid clipping of the first
                    } else if (i == 0) {

                        float width = Utils.calcTextWidth(mAxisLabelPaint, label);
                        x += width / 2;
                    }
                }

                drawLabel(c, label, x, pos, anchor, labelRotationAngleDegrees, idx_label);
                idx_label++;

            }
        }
    }

    protected void drawLabel(Canvas c, String formattedLabel, float x, float y, MPPointF anchor, float angleDegrees, int idx_label) {
        formattedLabel = formattedLabel.replace(",", "");
        float time = Float.parseFloat(formattedLabel);
        String s = String.format("%06f", time);
        try {
            Date date = format.parse(s);
            String time_str = Constants.xAxisDateFormat.format(date);

            switch (idx_label) {
                case 0:
                    mAxisLabelPaint.setTextAlign(Paint.Align.LEFT);
                    break;
                case 1:
                    mAxisLabelPaint.setTextAlign(Paint.Align.CENTER);
                    break;
                case 2:
                    mAxisLabelPaint.setTextAlign(Paint.Align.RIGHT);
                    if (time == 1440) {
                        time_str = "24:00";
                    }
                    break;
            }

            float verticalMargin = Utils.convertPixelsToDp(60);
            c.drawText(time_str, x, y + verticalMargin, mAxisLabelPaint);
        } catch (ParseException e) {

        }
    }
}
