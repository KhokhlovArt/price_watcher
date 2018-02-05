package com.khokhlov.khokhlovart.price_watcher;

import com.khokhlov.khokhlovart.price_watcher.Results.Item;

/**
 * Created by Dom on 25.11.2017.
 */

public interface ItemsAdapterListener {
    void onItemClick(Item item, int position);
    void onItemLongClick(Item item, int position);
}
