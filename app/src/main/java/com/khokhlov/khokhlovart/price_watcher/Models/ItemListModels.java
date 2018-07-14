package com.khokhlov.khokhlovart.price_watcher.Models;

import android.util.SparseBooleanArray;

import com.khokhlov.khokhlovart.price_watcher.Results.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemListModels {
    public ArrayList<Item> itemList = new ArrayList<>();
    public ArrayList<Item> fullItemList = new ArrayList<>();
    public SparseBooleanArray selectedItems = new SparseBooleanArray();
}
