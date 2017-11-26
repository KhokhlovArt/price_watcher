package com.example.khokhlovart.price_watcher;

/**
 * Created by Dom on 26.11.2017.
 */
public class Shop {
    public int id;
    public String domain;
    public boolean parserState ;
    Shop(int id, String domain, boolean parserState)
    {
        this.id          = id;
        this.domain      = domain;
        this.parserState = parserState;
    }
}


//        "id": 31,
//        "domain": "ru.aliexpress.com",
//        "parserState": true