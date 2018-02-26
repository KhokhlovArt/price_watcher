package com.khokhlov.khokhlovart.price_watcher.ItemInfo;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.khokhlov.khokhlovart.price_watcher.MainActivity;
import com.khokhlov.khokhlovart.price_watcher.R;
import com.khokhlov.khokhlovart.price_watcher.Results.Item;

public class itemInfoActivity extends AppCompatActivity {
    private TabLayout tab;
    private ViewPager pager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_info);

//////////////////////////////////////////////////////////////////////////
///////////////// Вдруг захотим вернуть табы /////////////////////////////
//////////////////////////////////////////////////////////////////////////
        tab   = (TabLayout) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);

        Bundle b = getIntent().getExtras();
        Item i = null;
        if(b != null) {i = (Item) b.getSerializable(MainActivity.CHEK_ITEM);}

        pager.setAdapter(new ItemPagerAdapter(getSupportFragmentManager(), getResources(), i));
        tab.setupWithViewPager(pager);
//////////////////////////////////////////////////////////////////////////

getSupportFragmentManager().beginTransaction().add(R.id.fragment_layout, new itemInfoFragment()).commit();

        BottomNavigationView bottomNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                handleBottomNavigationItemSelected(item);
                return true;
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void handleBottomNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_info:
                switchFragment(new itemInfoFragment());
                break;
            case R.id.menu_graph:
                switchFragment(new itemGraphFragment());
                break;
        }
    }

    private void switchFragment(Fragment fragment){
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_layout, fragment).commit();
        //getSupportFragmentManager().beginTransaction().add(R.id.fragment_layout, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
