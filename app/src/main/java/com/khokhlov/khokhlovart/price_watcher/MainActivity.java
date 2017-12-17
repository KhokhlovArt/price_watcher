package com.khokhlov.khokhlovart.price_watcher;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.khokhlov.khokhlovart.price_watcher.Api.IApi;

import java.io.IOException;
import java.io.Serializable;
import java.security.AccessControlContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {
    private static Resources res;
    private static AccessControlContext cntxt;
    private SwipeRefreshLayout refreshLayout;
    public ItemsAdaptor adaptor;
    private IApi api;
    private static Toolbar mActionBarToolbar;
    private ActionMode actionMode;
    private List<Integer> idItemsToDelete = new ArrayList<>();

    public static final int LOADER_ITEMS         = 0;
    public static final int LOADER_AUTH          = 1;
    public static final int LOADER_DELETE        = 2;
    public static final int LOADER_PRICE_HISTORY = 7;

    public static final String SENDER_ID = "829446502336";
    public static final String CHEK_ITEM = "chek_item";
private static AuthRes authRes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyGcmListenerService.notifivation_count = 0;
        setContentView(R.layout.activity_main);
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
                actionMode = ((AppCompatActivity) MainActivity.this).startSupportActionMode(actionModeCallback);
                adaptor.toggleSelection(position);
                actionMode.setTitle(getString(R.string.delete));
            }

            private boolean isInActionMode() {
                return actionMode != null;
            }
        });

        itemsRecyclerView.setAdapter(adaptor);

//        Auth();

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadItems();
                refreshLayout.setRefreshing(false);
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
            deleteItem(adaptor.getItemByPosition(adaptor.getSelectedItems().get(i)).id);
        }
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
                                mp.put("userToken", ((App) getApplicationContext()).getAuthToken().toString());
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
