package com.khokhlov.khokhlovart.price_watcher;

import java.io.Serializable;

/**
 * Created by Dom on 26.11.2017.
 */
public class Shop implements Serializable {
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