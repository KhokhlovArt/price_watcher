package com.khokhlov.khokhlovart.price_watcher;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

        etLink                      = (EditText) findViewById(R.id.add_link);
        Button btn_add              = (Button) findViewById(R.id.btn_add);
        ImageButton btn_buffer_past = (ImageButton) findViewById(R.id.btn_buffer_past);

        btn_buffer_past.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence res = "";
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = clipboard.getPrimaryClip();
                if (clipData != null && clipData.getItemAt(0) != null) {res = clipData.getItemAt(0).getText();}
                etLink.setText(res);
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_link(etLink.getText().toString());
                etLink.setText("");
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.add_toolbar_main);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    //********************************************************************************************************************
    private void add_link(String add_link){
        link = add_link;
        getSupportLoaderManager().restartLoader(MainActivity.LOADER_ADD, null, new LoaderManager.LoaderCallbacks<AddResult>() {
            @Override
            public Loader<AddResult> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<AddResult>(getApplicationContext()) {
                    @Override
                    public AddResult loadInBackground() {
                        try {
                            App apl = (App) getApplication();
                            HashMap mp = new HashMap();
                            mp.put("userToken", ((App) getApplicationContext()).getAuthToken().toString());
                            mp.put("link", link);
                            AddResult res = (AddResult) (apl).getApi().add_link(mp).execute().body();
                            return res;

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<AddResult> loader, AddResult data) {
                if (data != null) {
                    if (data.status.equals("OK"))
                    {
                        switch (data.code) {
                            case 1: {
                                //1   - товар добавлен как новый
                                Toast.makeText(getApplication(), R.string.add_res_1 , Toast.LENGTH_SHORT).show();
                                break;
                            }
                            case 2: {
                                //2   - добавлен запрос магазина с этим товаром
                                Toast.makeText(getApplication(), R.string.add_res_2 , Toast.LENGTH_SHORT).show();
                                break;
                            }
                            case 3: {
                                //3   - пользователь привязан к уже существующему товару
                                Toast.makeText(getApplication(), R.string.add_res_1 , Toast.LENGTH_SHORT).show();
                                break;
                            }
                            case -1: {
                                //-1   - ошибка. У пользователя уже есть ссылка на такой товар
                                Toast.makeText(getApplication(), R.string.add_res_m1 , Toast.LENGTH_SHORT).show();
                                break;
                            }
                            default: {
                                Toast.makeText(getApplication(), R.string.add_res_default_err, Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplication(), "Error: " + data.message , Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplication(), R.string.add_res_default_err, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLoaderReset(Loader loader) {}

        }).forceLoad();
    }
//    private void add_link(String add_link){
//        link = add_link;
//        getSupportLoaderManager().restartLoader(MainActivity.LOADER_ADD, null, new LoaderManager.LoaderCallbacks() {
//            @Override
//            public Loader onCreateLoader(int id, Bundle args) {
//                return new AsyncTaskLoader(getApplicationContext()) {
//                    @Override
//                    public Boolean loadInBackground() {
//                        try {
//                                App apl = (App) getApplication();
//                                HashMap mp = new HashMap();
//                                mp.put("userToken", ((App) getApplicationContext()).getAuthToken().toString());
//                                mp.put("link", link);
//                                AuthRes res = (AuthRes) (apl).getApi().add_link(mp).execute().body();
//
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        return true;
//                    }
//                };
//            }
//
//            @Override
//            public void onLoadFinished(Loader loader, Object data) {
//                Log.e("LOGTEST:", data.toString());
//                Toast.makeText(getBaseContext(), R.string.link_added, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onLoaderReset(Loader loader) {}
//
//        }).forceLoad();
//    }
}
