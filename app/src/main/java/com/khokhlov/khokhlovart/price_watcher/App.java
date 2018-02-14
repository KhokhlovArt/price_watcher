package com.khokhlov.khokhlovart.price_watcher;

import android.app.Application;
import android.text.TextUtils;

import com.khokhlov.khokhlovart.price_watcher.Api.IApi;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Dom on 26.11.2017.
 */

public class App extends Application {
    private IApi api;
    private static final String PREFERENCES_SESSION     = "session";
    public  static final String KEY_AUTH_TOKEN          = "userToken";
    public  static final String KEY_AUTH_USER_EMAIL     = "userEmail";
    public  static final String KEY_AUTH_USER_GCM_Token = "userGCMToken";
    public  static final String OPTIONS_NOTIFICATION    = "notification";
    public  static final String IS_CHANGE_ITEM          = "is_change_item";
    @Override
    public void onCreate() {
        super.onCreate();
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS+03:00")
                .create();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
//                .addInterceptor(new AuthInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://price-watcher.ru/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                //.client(client)
                .client(getUnsafeOkHttpClient())
                .build();

        api = retrofit.create(IApi.class);
    }


    public static OkHttpClient getUnsafeOkHttpClient() {

        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] chain,
                        String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] chain,
                        String authType) throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[0];
                }
            } };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts,
                    new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext
                    .getSocketFactory();
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient = okHttpClient.newBuilder()
                    .sslSocketFactory(sslSocketFactory)
                    .addInterceptor(interceptor)
                    .hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER).build();

            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public IApi getApi() {
        return api;
    }

    public void setPreferences(String preferences, String text) {
        getSharedPreferences(PREFERENCES_SESSION, MODE_PRIVATE).edit().putString(preferences, text).apply();
    }

    public void deletePreferences(String preferences) {
        getSharedPreferences(PREFERENCES_SESSION, MODE_PRIVATE).edit().remove(preferences).commit();
    }

    public String getPreferences(String preferences) {
        return getSharedPreferences(PREFERENCES_SESSION, MODE_PRIVATE).getString(preferences, "");
    }

    public boolean isLoggedIn() {
        return !TextUtils.isEmpty(getPreferences(KEY_AUTH_TOKEN));
    }

//    private class AuthInterceptor implements Interceptor {
//        @Override
//        public Response intercept(Chain chain) throws IOException {
//            Request originalRequest = chain.request();
//            HttpUrl url = originalRequest.url().newBuilder().addQueryParameter("userToken", getAuthToken()).build();
//            return chain.proceed(originalRequest.newBuilder().url(url).build());
//        }
//    }
}
