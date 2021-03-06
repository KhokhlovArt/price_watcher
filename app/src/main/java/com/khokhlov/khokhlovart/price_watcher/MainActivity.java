package com.khokhlov.khokhlovart.price_watcher;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.SystemClock;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.khokhlov.khokhlovart.price_watcher.Api.IApi;
import com.khokhlov.khokhlovart.price_watcher.ItemInfo.itemInfoActivity;
import com.khokhlov.khokhlovart.price_watcher.Models.ItemListModels;
import com.khokhlov.khokhlovart.price_watcher.Results.AuthRes;
import com.khokhlov.khokhlovart.price_watcher.Results.Item;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static Resources res;
    private SwipeRefreshLayout refreshLayout;
    public ItemsAdaptor adaptor;
    private IApi api;
    private ActionMode actionMode;
    private List<Integer> idItemsToDelete = new ArrayList<>();
    private SearchView mSearchView;
    public static final int LOADER_ITEMS         = 0;
    public static final int LOADER_AUTH          = 1;
    public static final int LOADER_DELETE        = 2;
    public static final int LOADER_ADD           = 3;
    public static final int LOADER_SIGNUP        = 4;
    public static final int LOADER_PRICE_HISTORY = 7;

    public static final String SENDER_ID = "829446502336";
    public static final String CHEK_ITEM = "chek_item";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyGcmListenerService.notifivation_count = 0;
        setContentView(R.layout.activity_main);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(getString(R.string.app_name));
        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(getBaseContext(),R.color.black));

        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(mActionBarToolbar);

        setRes(getResources());
        RecyclerView itemsRecyclerView = (RecyclerView) findViewById(R.id.items_recycler_view);
        itemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        api     = ((App) getApplication()).getApi();
        adaptor = new ItemsAdaptor(this, new ItemListModels());
        adaptor.setListener(new ItemsAdapterListener() {
            @Override
            public void onItemClick(Item item, int position) {

                if (isInActionMode()) {
                    adaptor.toggleSelection(position);
                }
                else
                {
                    if (adaptor.getItemViewType(position) != adaptor.HEAD_HOLDER_TYPE) {
                        Intent intent = new Intent(getBaseContext(), itemInfoActivity.class);
                        Bundle b = new Bundle();
                        b.putSerializable(CHEK_ITEM, (Serializable) adaptor.getItemByPosition(position));
                        intent.putExtras(b);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onItemLongClick(Item item, int position) {
                if (isInActionMode()) {
                    return;
                }
                actionMode = (MainActivity.this).startSupportActionMode(actionModeCallback);
                adaptor.toggleSelection(position);
                actionMode.setTitle(getString(R.string.delete));
            }

            private boolean isInActionMode() {
                return actionMode != null;
            }
        });
        itemsRecyclerView.setAdapter(adaptor);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mSearchView != null) {
                    mSearchView.setQuery("", false);
                    mSearchView.clearFocus();
                }

                loadItems();
                refreshLayout.setRefreshing(false);
            }
        });

        FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), AddActivity.class);
                startActivity(intent);
            }
        });
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (findViewById(R.id.menu_add) != null) {
                    findViewById(R.id.menu_add).setVisibility( (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) ? View.VISIBLE : View.INVISIBLE);
                }
            }
        });
        startService(new Intent(this, MyGcmListenerService.class));

//        App apl = (App) getApplication();
//        String notif_options = apl.getPreferences(apl.OPTIONS_NOTIFICATION);
//        startService(new Intent(this, MyGcmListenerService.class).putExtra("is_need_notification", notif_options.equals("true")));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(((App)getApplication()).isLoggedIn()){
            loadItems();
        }else{
            Intent intent = new Intent(getBaseContext() , LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);


        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        //mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));
        if (mSearchView != null ) {

            EditText searchEditText = (EditText) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            searchEditText.setTextColor(getResources().getColor(R.color.colorAccent));
            searchEditText.setHintTextColor(getResources().getColor(R.color.colorAccentHint));

            mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            mSearchView.setIconifiedByDefault(false);

            SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
                public boolean onQueryTextChange(String newText) {
                    adaptor.filterItems(newText);
                    return true;
                }

                public boolean onQueryTextSubmit(String query) {
                    adaptor.filterItems(query);
                    return true;
                }
            };

            mSearchView.setOnQueryTextListener(queryTextListener);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_login: {
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
                return true;
                }
            case R.id.menu_add: {
                Intent intent = new Intent(getBaseContext(), AddActivity.class);
                startActivity(intent);
                return true;
                }
            case R.id.menu_search:{

                return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static Resources getRes() {
        return res;
    }
    public void setRes(Resources res) {
        this.res = res;
    }


    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_delete, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_delete:
                    deleteSelectedItems();
                    stopActionMode();
                    return true;
//                case R.id.menu_login:
//                    Intent intent = new Intent(getBaseContext() , LoginActivity.class);
//                    startActivity(intent);
//                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
//            Menu m = MainActivity.getToolbar().getMenu();
//            getMenuInflater().inflate(R.menu.menu_main_activity, m);
            adaptor.clearSelections();
            actionMode = null;
        }
    };

    private void deleteSelectedItems() {
        for (int i = adaptor.getSelectedItems().size() - 1; i >= 0; i--) {
            deleteItem(adaptor.getItemByPosition(adaptor.getSelectedItems().get(i)).id);
        }
        for (int i = adaptor.getSelectedItems().size() - 1; i >= 0; i--) {
            adaptor.remove(adaptor.getSelectedItems().get(i));
        }
    }

    public void stopActionMode() {
        if (actionMode != null) { actionMode.finish(); }
    }

    private void setVisibleInMainGIThred(final int id, final int visible)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View tmp = (View) findViewById(id);
                tmp.setVisibility(visible);
            }
        });
    }

    /********************************************************************************************************************
     ********************************  Loader-ы  ************************************************************************
     ********************************************************************************************************************/
    private void loadItems() {
        getSupportLoaderManager().restartLoader(LOADER_ITEMS, null, new LoaderManager.LoaderCallbacks<List<Item>>() {
            @Override
            public Loader<List<Item>> onCreateLoader(int id, Bundle args) {

                return new AsyncTaskLoader<List<Item>>(getApplicationContext()) {
                    @Override

                    public List<Item> loadInBackground() {
                        try {
                            setVisibleInMainGIThred(R.id.lbl_check_internet, View.GONE);
                            setVisibleInMainGIThred(R.id.items_recycler_view, View.GONE);
                            setVisibleInMainGIThred(R.id.load_img, View.VISIBLE);
                            App apl = (App) getApplicationContext();
                            List<Item> items = api.prices( apl.getPreferences(App.KEY_AUTH_TOKEN)).execute().body();
                            setVisibleInMainGIThred(R.id.load_img, View.GONE);
                            setVisibleInMainGIThred(R.id.items_recycler_view, View.VISIBLE);
                            return items;
                        } catch (IOException e) {
                            setVisibleInMainGIThred(R.id.load_img, View.GONE);
                            setVisibleInMainGIThred(R.id.lbl_check_internet, View.VISIBLE);
                            setVisibleInMainGIThred(R.id.items_recycler_view, View.GONE);
                            e.printStackTrace();
                            return null;
                        }
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<List<Item>> loader, List<Item> items) {
                if (items == null) {
                    //showError("Произошла ошибка");
                } else {
                    adaptor.setItems(items);
                }
            }

            @Override
            public void onLoaderReset(Loader<List<Item>> loader) {

            }
        }).forceLoad();
    }

    //********************************************************************************************************************
    private void deleteItem(int id){
        idItemsToDelete.add(id);
        getSupportLoaderManager().restartLoader(LOADER_DELETE, null, new LoaderManager.LoaderCallbacks() {
            @Override
            public Loader onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader(getApplicationContext()) {
                    @Override
                    public Boolean loadInBackground() {
                        try {
                            for (Integer itemId : idItemsToDelete)
                            {
                                HashMap mp = new HashMap();
                                App apl = (App) getApplicationContext();
                                mp.put("userToken", apl.getPreferences(App.KEY_AUTH_TOKEN).toString());
                                mp.put("priceId", itemId);
                                AuthRes res = (AuthRes) api.delete(mp).execute().body();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader loader, Object data) {}

            @Override
            public void onLoaderReset(Loader loader) {}

        }).forceLoad();
    }

}
