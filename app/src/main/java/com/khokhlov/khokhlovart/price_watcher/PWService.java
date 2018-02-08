package com.khokhlov.khokhlovart.price_watcher;

import java.text.DecimalFormat;

/**
 * Created by Dom on 08.02.2018.
 */

public final class PWService {
    private PWService(){}

    public static String formatPrice( double num)
    {
        DecimalFormat decimal_formatter = new DecimalFormat("##,###.00");
        return decimal_formatter.format(num);
    }
}
