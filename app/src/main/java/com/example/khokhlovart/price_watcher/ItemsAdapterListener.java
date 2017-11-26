package com.example.khokhlovart.price_watcher;

/**
 * Created by Dom on 25.11.2017.
 */

public interface ItemsAdapterListener {
    void onItemClick(Item item, int position);
    void onItemLongClick(Item item, int position);
}
