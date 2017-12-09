package com.khokhlov.khokhlovart.price_watcher;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Dom on 03.12.2017.
 */

public class PriceHistoryItem implements Serializable {
    @SerializedName("changeDate")
    public String changeDate;
    @SerializedName("priceValue")
    public Double priceValue;

    PriceHistoryItem(String changeDate, Double priceValue)
    {
        this.changeDate = changeDate;
        this.priceValue = priceValue;
    }

}
