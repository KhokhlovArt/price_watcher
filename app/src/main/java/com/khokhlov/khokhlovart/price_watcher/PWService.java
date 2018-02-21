package com.khokhlov.khokhlovart.price_watcher;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;

public final class PWService {
    private PWService(){}

    public static String formatPrice( double num)
    {
        DecimalFormat decimal_formatter = new DecimalFormat("##,###.00");
        return decimal_formatter.format(num);
    }

}
