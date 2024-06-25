package com.example.api_demo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;


import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class MainActivity extends AppCompatActivity {
    private TextView Response;
    private CommonService commonService;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "com.example.apidemo.PREFS";
    private static final String TOKEN_KEY = "m-service-token";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnLogin = findViewById(R.id.login);
        Button btnLogout = findViewById(R.id.logout);
        Response = findViewById(R.id.response);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };

        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0]);
            httpClient.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            httpClient.connectTimeout(30, TimeUnit.SECONDS);
            httpClient.readTimeout(30, TimeUnit.SECONDS);
            httpClient.addInterceptor(logging); // Add the logging interceptor

            httpClient.addInterceptor(chain -> {
                Request original = chain.request();
                String token = sharedPreferences.getString(TOKEN_KEY, null);
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json");
                if (token != null) {
                    requestBuilder.header("Cookie", "m-service-token=" + token);
                }
                Request request = requestBuilder
                        .method(original.method(), original.body())
                        .build();
                return chain.proceed(request);
            });

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://bdsi-stg1-store.pivotree.io/")
                    .addConverterFactory(SimpleXmlConverterFactory.create())
                    .client(httpClient.build())
                    .build();

            commonService = retrofit.create(CommonService.class);
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    performLogin();
                }
            });
            btnLogout.setOnClickListener(new View.OnClickListener() { // Add this block
                @Override
                public void onClick(View v) {
                    performLogout();
                }
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void performLogin() {
        LoginRequest loginRequest = new LoginRequest("json", "DISHAPANDIT.CMP", "India@901");
        Call<LoginResponse> call = commonService.login(loginRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    LoginResponse.Response responseBody = loginResponse.getResponse();
                    String token = responseBody.getJSESSIONID();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(TOKEN_KEY, token);
                    editor.apply();
                    String displayText = "response :" +"  " +"{" + "\n" + "JSESSIONID: " + responseBody.getJSESSIONID() +","+ "\nkey: " + responseBody.getKey() + ","+ "\nforcePasswordChange: " + responseBody.isForcePasswordChange() + "\n"+ "}";
                    Response.setText(displayText);

                } else {
                    try {
                        // Log and display detailed error message
                        String errorBody = response.errorBody().string();
                        Response.setText("Request failed: " + errorBody);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Response.setText("Request failed: Unable to parse error response");
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                t.printStackTrace();
                Response.setText("Request failed: " + t.getMessage());
            }
        });
    }

    private void performLogout() {
        LogoutRequest logoutRequest = new LogoutRequest("false", "DISHAPANDIT.CMP", "India@901", "json");
        Call<LogoutResponse> call = commonService.logout(logoutRequest);
        Log.d("LogoutRequest", "Request: " + new Gson().toJson(logoutRequest));
        call.enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                Log.d("LogoutResponse", "Response Code: " + response.code()); // Log response code
                Log.d("LogoutResponseHeaders", "Headers: " + response.headers().toString());
                Log.d("LogoutResponse", "response success: " + response.isSuccessful());
                if (response.isSuccessful() && response.body() != null) {
                    LogoutResponse logoutResponse = response.body();
                    Log.d("LogoutResponseBody", "Body: " + new Gson().toJson(logoutResponse));
                    String displayText = "is-logged-out: " + logoutResponse.getIsLoggedOut();
                    Response.setText(displayText);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove(TOKEN_KEY);
                    editor.apply();
                } else {
                    handleErrorResponse(response);
                }
            }
            @Override
            public void onFailure(Call<LogoutResponse> call, Throwable t) {
                t.printStackTrace();
                Log.e("LogoutFailure", "Request failed: unable to give response " + t.getMessage(), t);
                Response.setText("Request failed: unable to give response " + t.getMessage());
            }
        });
    }

    private void handleErrorResponse(Response<LogoutResponse> response) {
        try {
            if (response.errorBody() != null) {
                String errorBody = response.errorBody().string();
                Log.e("LogoutError", "Request failed: " + errorBody); // Log error body
                if (isJson(errorBody)) {
                    try {
                        JSONObject errorJson = new JSONObject(errorBody);
                        String errorMessage = errorJson.optString("error", "Unknown error");
                        Response.setText("Request failed: " + errorMessage);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Response.setText("Request failed: Unable to parse error response");
                    }
                } else {
                    Response.setText("Request failed with HTML response: " + errorBody);
                }
            } else {
                Log.e("LogoutError", "Request failed with no error body");
                Response.setText("Request failed: Unknown error");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("LogoutException", "Request failed: Unable to parse error response", e); // Log exception
            Response.setText("Request failed: Unable to parse error response");
        }

    }
    private boolean isJson(String str) {
        try {
            new JSONObject(str);
        } catch (JSONException ex) {
            try {
                new JSONArray(str);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }
}
