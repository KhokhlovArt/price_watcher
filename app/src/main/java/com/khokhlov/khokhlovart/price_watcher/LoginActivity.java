package com.khokhlov.khokhlovart.price_watcher;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.khokhlov.khokhlovart.price_watcher.Results.AuthRes;
import com.khokhlov.khokhlovart.price_watcher.Results.SignupRes;

import java.io.IOException;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    private EditText etLogin;
    private EditText etPass;
    private Button btnEnter;
    private Button btnSignup;
    private Button btnExit;
    private Button btnBack;
    private TableRow rowWithEnter;
    private TableRow rowWithExit;
    private TableRow rowWithNotificationOpt;
    private Switch notificationSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etLogin   = (EditText) findViewById(R.id.login);
        etPass    = (EditText) findViewById(R.id.pass);
        btnEnter  = (Button)   findViewById(R.id.btn_enter);
        btnSignup = (Button)   findViewById(R.id.btn_signup);
        btnExit   = (Button)   findViewById(R.id.btn_logout);
        btnBack   = (Button)   findViewById(R.id.btn_back);

        rowWithEnter           = (TableRow) findViewById(R.id.row_with_entr_btn);
        rowWithExit            = (TableRow) findViewById(R.id.row_with_exit_btn);
        rowWithNotificationOpt = (TableRow) findViewById(R.id.row_with_notification_opt);

        notificationSwitch = (Switch)  findViewById(R.id.switch_notifications);

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Auth();
            }
        });
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((!etLogin.getText().toString().equals("")) && (!etPass.getText().toString().equals("")) &&
                    (etLogin.getText() != null) && (etPass.getText() != null)){
                    Signup();
                }
                /*DialogFragment dialog = new DialogFragment();
                dialog.show(getSupportFragmentManager(), "MyDialogFragmentTag");*/
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
            btnSignup.setVisibility(View.GONE);
            btnExit.setVisibility(View.VISIBLE);
            btnBack.setVisibility(View.VISIBLE);

            rowWithEnter.setVisibility(View.GONE);
            rowWithExit.setVisibility(View.VISIBLE);
            rowWithNotificationOpt.setVisibility(View.VISIBLE);

            etLogin.setEnabled(false);
            etLogin.setText(apl.getPreferences(apl.KEY_AUTH_USER_EMAIL).toString());
            etPass.setEnabled(false);
            etPass.setText("********");
        }else{
            btnEnter.setVisibility(View.VISIBLE);
            btnSignup.setVisibility(View.VISIBLE);
            btnExit.setVisibility(View.GONE);
            btnBack.setVisibility(View.GONE);

            rowWithEnter.setVisibility(View.VISIBLE);
            rowWithExit.setVisibility(View.GONE);
            rowWithNotificationOpt.setVisibility(View.GONE);

            etLogin.setEnabled(true);
            etLogin.setText("");
            etPass.setEnabled(true);
            etPass.setText("");
        }
        String notif_options = apl.getPreferences(apl.OPTIONS_NOTIFICATION);
        if (!notif_options.equals("true") && !notif_options.equals("false"))
        {
            apl.setPreferences(apl.OPTIONS_NOTIFICATION, "true");
        }
        notificationSwitch.setChecked(notif_options.equals("true"));
        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setNotificationOptions(b);
            }
        });
    }

    private void setNotificationOptions(boolean b)
    {
        App apl = (App) getApplication();
        apl.setPreferences(apl.OPTIONS_NOTIFICATION, b ? "true" : "false");
        //Перезапускаем Сервис
        //startService(new Intent(this, MyGcmListenerService.class).putExtra("is_need_notification", b));
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
                            mp.put("email",    etLogin.getText().toString());
                            mp.put("password", etPass.getText().toString());
                            mp.put("deviceId", token);

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

    private void Signup() {
        getSupportLoaderManager().restartLoader(MainActivity.LOADER_SIGNUP, null, new LoaderManager.LoaderCallbacks<SignupRes>() {
            @Override
            public Loader<SignupRes> onCreateLoader(int id, Bundle args) {

                return new AsyncTaskLoader<SignupRes>(LoginActivity.this) {
                    @Override
                    public SignupRes loadInBackground() {
                        try {
                            HashMap mp = new HashMap();
                            mp.put("email",     etLogin.getText().toString());
                            mp.put("password",  etPass.getText().toString());

                            App apl = (App) getApplication();
                            SignupRes res = (SignupRes) (apl).getApi().signup(mp).execute().body();

                            return res;
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<SignupRes> loader, SignupRes data) {

                if (data != null) {
                    if (data.status.equals("OK")) {
                        Toast.makeText(getBaseContext(), R.string.sign_tmp_text , Toast.LENGTH_LONG).show();
//                        DialogFragment dialog = new DialogFragment();
//                        dialog.show(getSupportFragmentManager(), "MyDialogFragmentTag");
                    }
                    else
                    {
                        switch (data.code) {
                            case 1: {
                                //1 - Такой пользователь уже зарегистрирован
                                Toast.makeText(getBaseContext(), R.string.signup_err_1 , Toast.LENGTH_SHORT).show();
                                break;
                            }
                            default: {
                                Toast.makeText(getBaseContext(), R.string.signup_err_default, Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    }
                }else {
                    Toast.makeText(getBaseContext(), R.string.signup_err_default, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLoaderReset(Loader<SignupRes> loader) {

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
