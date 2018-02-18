package com.khokhlov.khokhlovart.price_watcher.Models;

import android.util.SparseBooleanArray;

import com.khokhlov.khokhlovart.price_watcher.Results.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemListModels {
    public List<Item> itemList = new ArrayList<>();
    public SparseBooleanArray selectedItems = new SparseBooleanArray();
}
