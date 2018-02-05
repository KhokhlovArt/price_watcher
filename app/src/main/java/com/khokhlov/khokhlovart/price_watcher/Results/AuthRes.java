package com.khokhlov.khokhlovart.price_watcher.Results;

/**
 * Created by Dom on 26.11.2017.
 */

public class AuthRes {
    public String status;
    public String token;

    AuthRes(String status, String token)
    {
        this.status = status;
        this.token  = token;
    }
}
