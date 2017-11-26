package com.example.khokhlovart.price_watcher;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.khokhlovart.price_watcher.Api.IApi;

import java.io.IOException;
import java.security.AccessControlContext;
import java.util.HashMap;
import java.util.List;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {
    private static Resources res;
    private static AccessControlContext cntxt;
    private SwipeRefreshLayout refreshLayout;
    ItemsAdaptor adaptor;
    private IApi api;
    private static Toolbar mActionBarToolbar;
    private ActionMode actionMode;
    public static final int LOADER_ITEMS = 0;
    public static final int LOADER_AUTH = 1;

private static AuthRes authRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 //getSupportActionBar().hide();
        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(mActionBarToolbar);
        setRes(getResources());
        cntxt = getContext();
        RecyclerView itemsRecyclerView = (RecyclerView) findViewById(R.id.items_recycler_view);
        itemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        api     = ((App) getApplication()).getApi();
        adaptor = new ItemsAdaptor();
        adaptor.setListener(new ItemsAdapterListener() {
            @Override
            public void onItemClick(Item item, int position) {
                if (isInActionMode()) {
                    adaptor.toggleSelection(position);
                }
            }

            @Override
            public void onItemLongClick(Item item, int position) {
                if (isInActionMode()) {
                    return;
                }
                //MainActivity.getToolbar().setVisibility(View.GONE);
                actionMode = ((AppCompatActivity) MainActivity.this).startSupportActionMode(actionModeCallback);
                adaptor.toggleSelection(position);
                actionMode.setTitle(getString(R.string.delete));
            }

            private boolean isInActionMode() {
                return actionMode != null;
            }
        });

        itemsRecyclerView.setAdapter(adaptor);

        //Auth();

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadItems();
                refreshLayout.setRefreshing(false);
            }
        });
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_login:
                Intent intent = new Intent(getBaseContext() , LoginActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static Resources getRes() {
        return res;
    }
    public static AccessControlContext getCntxt() { return cntxt;}
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
            adaptor.remove(adaptor.getSelectedItems().get(i));
        }
    }

    public void stopActionMode() {
        if (actionMode != null) { actionMode.finish(); }
    }

    public static Toolbar getToolbar(){ return mActionBarToolbar;}

    /********************************************************************************************************************
     ********************************  Loader-ы  ************************************************************************
     ********************************************************************************************************************/
    private void Auth() {
        getSupportLoaderManager().restartLoader(LOADER_AUTH, null, new LoaderManager.LoaderCallbacks<AuthRes>() {
            @Override
            public Loader<AuthRes> onCreateLoader(int id, Bundle args) {

                return new AsyncTaskLoader<AuthRes>(getApplicationContext()) {
                    @Override
                    public AuthRes loadInBackground() {
                        try {
                            HashMap mp = new HashMap();
                            mp.put("email", "Khokhlovart@gmail.com");
                            mp.put("password", "230988");
                            AuthRes res = (AuthRes) api.auth(mp).execute().body();
//                            AuthRes res = api.auth("Khokhlovart@gmail.com", "230988").execute().body();
                            return res;
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<AuthRes> loader, AuthRes data) {
                if (data != null) {
                    MainActivity.authRes = data;
                    ((App) getApplicationContext()).setAuthToken(data.token);
                }else {
                    Log.d("KhokhlovLog", "Invalid credentials! :-(");
                }

            }

            @Override
            public void onLoaderReset(Loader<AuthRes> loader) {

            }
        }).forceLoad();
    }
    //********************************************************************************************************************
    private void loadItems() {
        getSupportLoaderManager().restartLoader(LOADER_ITEMS, null, new LoaderManager.LoaderCallbacks<List<Item>>() {
            @Override
            public Loader<List<Item>> onCreateLoader(int id, Bundle args) {

                return new AsyncTaskLoader<List<Item>>(getApplicationContext()) {
                    @Override

                    public List<Item> loadInBackground() {
                        try {
                            List<Item> items = api.prices( ( (App)getApplicationContext()).getAuthToken()).execute().body();
                            return items;

                        } catch (IOException e) {
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
}
