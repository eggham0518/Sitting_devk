package com.example.sitting_devk.constant;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public interface Constants {

    SimpleDateFormat xAxisDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    DecimalFormat decimalFormat = new DecimalFormat("#.#");
}
