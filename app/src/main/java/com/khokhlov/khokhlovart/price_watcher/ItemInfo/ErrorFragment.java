package com.khokhlov.khokhlovart.price_watcher.ItemInfo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.khokhlov.khokhlovart.price_watcher.R;

/**
 * Created by Dom on 03.12.2017.
 */

public class ErrorFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.error_layout, container, false);
    }
}
