package com.example.khokhlovart.price_watcher;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Dom on 25.11.2017.
 */

public class Item implements Serializable {
    public int id;
    public int userId;
    public Shop shop;

    //public String shop;
    public String description;
    public String link;
    @SerializedName("createDate")
    public Date createDate;
    @SerializedName("checkDate")
    public Date checkDate;
    @SerializedName("changeDate")
    public String changeDate;
    @SerializedName("inStock")
    public boolean inStock;
    public double price;

    public Item(String shop, String description, String link, boolean inStock, double price) {
        //this.shop        = shop;
        this.description  = description;
        this.link         = link;
        this.inStock      = inStock;
        this.price        = price;
    }
}


//"id": 44,
//        "userId": 2,
//        "shop": {
//        "id": 31,
//        "domain": "ru.aliexpress.com",
//        "parserState": true
//        },
//        "link": "https://ru.aliexpress.com/item/CHUWI-Hi8-Pro-Dual-OS-Tablet-PC-Windows-10-Android-5-1-Intel-Atom-X5-Z8350/32820683689.html?spm=a2g0v.search0103.3.63.urATec&ws_ab_test=searchweb0_0,searchweb201602_0_10152_10065_10151_10344_10068_10345_10342_10343_10340_10341_10543_10541_10562_10084_10083_10307_10301_10539_10312_10059_10313_10314_10534_10533_100031_10211_10603_10103_10128_10129_10594_10557_10169_10596_10595_10142_10107,searchweb201603_14,ppcSwitch_0&btsid=f2a63779-17c3-4b94-9362-2ae4e4e4e8fe&algo_expid=5af397e3-0486-4ea5-82d8-1e14200961af-7&algo_pvid=5af397e3-0486-4ea5-82d8-1e14200961af&rmStoreLevelAB=1",
//        "description": "Chuwi Hi8 Pro Двойной OS Tablet PC Windows 10 Android 5.1 Intel Atom X5-Z8350 Quad Core 2 ГБ Оперативная память 32 ГБ Оперативная память 1920x1200",
//        "price": 5839.31,
//        "createDate": "2017-11-24T11:00:37.491+03:00",
//        "checkDate": "2017-11-25T21:00:22.444+03:00",
//        "changeDate": "2017-11-24T11:00:37.491+03:00",
//        "available": true,
//        "inStock": true