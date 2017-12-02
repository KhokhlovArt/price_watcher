package com.example.khokhlovart.price_watcher;

import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    private EditText etLogin;
    private EditText etPass;
    private Button btnEnter;
    private Button btnExit;
    private Button btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etLogin  = (EditText) findViewById(R.id.login);
        etPass   = (EditText) findViewById(R.id.pass);
        btnEnter = (Button)   findViewById(R.id.btn_enter);
        btnExit  = (Button)   findViewById(R.id.btn_logout);
        btnBack  = (Button)   findViewById(R.id.btn_back);
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Auth();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App apl = (App) getApplication();
                apl.deleteAuthToken();
                apl.deletePreferences(apl.KEY_AUTH_USER_EMAIL);
                finish();
            }
        });
        App apl = (App) getApplication();
        if(apl.isLoggedIn()){
            btnEnter.setVisibility(View.GONE);
            btnExit.setVisibility(View.VISIBLE);
            btnBack.setVisibility(View.VISIBLE);
            etLogin.setEnabled(false);
            etLogin.setText(apl.getPreferences(apl.KEY_AUTH_USER_EMAIL).toString());
            etPass.setEnabled(false);
            etPass.setText("********");
        }else{
            btnEnter.setVisibility(View.VISIBLE);
            btnExit.setVisibility(View.GONE);
            btnBack.setVisibility(View.GONE);
            etLogin.setEnabled(true);
            etLogin.setText("");
            etPass.setEnabled(true);
            etPass.setText("");
        }
    }

    /********************************************************************************************************************
     ********************************  Loader-ы  ************************************************************************
     ********************************************************************************************************************/
    private void Auth() {
        getSupportLoaderManager().restartLoader(MainActivity.LOADER_AUTH, null, new LoaderManager.LoaderCallbacks<AuthRes>() {
            @Override
            public Loader<AuthRes> onCreateLoader(int id, Bundle args) {

                return new AsyncTaskLoader<AuthRes>(LoginActivity.this) {
                    @Override
                    public AuthRes loadInBackground() {
                        try {

                            InstanceID instanceID = InstanceID.getInstance(getContext());
                            String token = null;
                            try {
                                token = instanceID.getToken(MainActivity.SENDER_ID,  GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (token == null)
                            {
                                return null;
                            }
                            HashMap mp = new HashMap();
                            mp.put("email",     etLogin.getText().toString());
                            mp.put("password",  etPass.getText().toString());
                            mp.put("gcm_token", token);
Log.e("!!!!--->", "token: " + token);
                            App apl = (App) getApplication();
                            AuthRes res = (AuthRes) (apl).getApi().auth(mp).execute().body();
                            apl.setPreferences(apl.KEY_AUTH_USER_EMAIL, etLogin.getText().toString());
                            apl.setPreferences(apl.KEY_AUTH_USER_GCM_Token, token.toString());
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
                App apl = (App) getApplication();
                if (data != null) {
                    apl.setAuthToken(data.token);
                    finish();
                }else {
                    apl.deletePreferences(apl.KEY_AUTH_USER_EMAIL);
                    apl.deletePreferences(apl.KEY_AUTH_USER_GCM_Token);
                    etPass.setText("");
                    Toast.makeText(getApplicationContext(), "Invalid credentials!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLoaderReset(Loader<AuthRes> loader) {

            }
        }).forceLoad();
    }

    //********************************************************************************************************************
//    public void getGSMToken() {
//        getSupportLoaderManager().restartLoader(MainActivity.LOADER_GET_GSM_TOKEN, null, new LoaderManager.LoaderCallbacks<Void>() {
//            @Override
//            public Loader<Void> onCreateLoader(int id, Bundle args) {
//
//                return new AsyncTaskLoader<Void>(getApplicationContext()) {
//                    @Override
//                    public Void loadInBackground() {
//                        InstanceID instanceID = InstanceID.getInstance(MainActivity.this);
//                        String token = null;
//                        try {
//                            token = instanceID.getToken(MainActivity.SENDER_ID,  GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        return null;
//                    }
//                };
//            }
//
//            @Override
//            public void onLoadFinished(Loader<Void> loader, Void data) {
//                Log.e("!!!!--->", "onCreate: " + data);
//            }
//
//            @Override
//            public void onLoaderReset(Loader<Void> loader) {}
//        }).forceLoad();
//    }
}
