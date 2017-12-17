package com.khokhlov.khokhlovart.price_watcher;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;

public class AddActivity extends AppCompatActivity {
    public String link;
    private EditText etLink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        etLink       = (EditText) findViewById(R.id.add_link);
        Button btn_add = (Button) findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_link(etLink.getText().toString());
                etLink.setText("");
            }
        });

        Button btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button btnToSite = (Button) findViewById(R.id.btn_to_site);
        btnToSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://price-watcher.ru/home"));
                startActivity(browserIntent);
            }
        });
    }


    //********************************************************************************************************************
    private void add_link(String add_link){
        link = add_link;
        getSupportLoaderManager().restartLoader(MainActivity.LOADER_ADD, null, new LoaderManager.LoaderCallbacks() {
            @Override
            public Loader onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader(getApplicationContext()) {
                    @Override
                    public Boolean loadInBackground() {
                        try {
                                App apl = (App) getApplication();
                                HashMap mp = new HashMap();
                                mp.put("userToken", ((App) getApplicationContext()).getAuthToken().toString());
                                mp.put("link", link);
                                AuthRes res = (AuthRes) (apl).getApi().add_link(mp).execute().body();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader loader, Object data) {
                Log.e("LOGTEST:", data.toString());
                Toast.makeText(getBaseContext(), R.string.link_added, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoaderReset(Loader loader) {}

        }).forceLoad();
    }
}
