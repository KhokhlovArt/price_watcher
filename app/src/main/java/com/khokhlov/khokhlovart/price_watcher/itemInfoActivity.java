package com.khokhlov.khokhlovart.price_watcher;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class itemInfoActivity extends AppCompatActivity {
    private TabLayout tab;
    private ViewPager pager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_info);
        tab   = (TabLayout) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);

        Bundle b = getIntent().getExtras();
        Item i = null;
        if(b != null) {i = (Item) b.getSerializable(MainActivity.CHEK_ITEM);}

        pager.setAdapter(new ItemPagerAdapter(getSupportFragmentManager(), getResources(), i));
        tab.setupWithViewPager(pager);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}