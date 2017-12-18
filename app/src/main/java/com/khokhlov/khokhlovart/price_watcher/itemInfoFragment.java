package com.khokhlov.khokhlovart.price_watcher;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;

/**
 * Created by Dom on 03.12.2017.
 */

public class itemInfoFragment extends Fragment {
    private Item item;
    private TextView shop_lbl_val;
    private TextView discription_lbl_val;
    private TextView link_lbl_val;
    private TextView date_lbl_val;

    private TextView isHave_lbl_val;
    private TextView price_lbl_val;

    private void setItem(Item item) {
        this.item = item;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (item == null){setItem(ItemPagerAdapter.item);}
        return  inflater.inflate(R.layout.items_info_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (item == null){setItem(ItemPagerAdapter.item);}

        shop_lbl_val         = (TextView)       view.findViewById(R.id.shop_lbl_val);
        discription_lbl_val  = (TextView)       view.findViewById(R.id.discription_lbl_val);
        link_lbl_val         = (TextView)       view.findViewById(R.id.link_lbl_val);
        date_lbl_val         = (TextView)       view.findViewById(R.id.date_lbl_val);
        isHave_lbl_val       = (TextView)       view.findViewById(R.id.isHave_lbl_val);
        price_lbl_val        = (TextView)       view.findViewById(R.id.price_lbl_val);

        shop_lbl_val.setText(item.shop.domain);
        discription_lbl_val.setText(item.description);
        //link_lbl_val.setText(item.shop.domain);
        date_lbl_val.setText((item.createDate == null) ? "" : new SimpleDateFormat("dd-MM-yyyy HH:mm").format(item.createDate) );
        isHave_lbl_val.setText(item.inStock ? R.string.yes : R.string.no);
        price_lbl_val.setText(String.format("%.2f",item.price));
        isHave_lbl_val.setTextColor(item.inStock ? MainActivity.getRes().getColor(R.color.colorAccent) : MainActivity.getRes().getColor(R.color.rowToDelete));


        link_lbl_val.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.link));
                startActivity(browserIntent);
            }
        });
    }
}
