package com.example.sitting_devk.util;

import android.content.Context;

import androidx.core.content.ContextCompat;

public class ColorUtil {
    public static ColorUtil colorUtil = new ColorUtil();

    public static ColorUtil getColorUtil() {
        return colorUtil;
    }

    public int getColorVal(Context context, int colorId) {
        return ContextCompat.getColor(context, colorId);
    }
}
