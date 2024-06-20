package com.example.api_demo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;


public class MainActivity extends AppCompatActivity {
    private TextView tvResponse;
    private CommonService commonService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnLogout = findViewById(R.id.btnLogout);
        tvResponse = findViewById(R.id.tvResponse);

        // Add logging interceptor to OkHttpClient
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }

                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };

        try {
            // Install the all-trusting trust manager
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
            httpClient.addInterceptor(loggingInterceptor); // Add the logging interceptor

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://bdsi-stg1-store.pivotree.io")
                    .addConverterFactory(SimpleXmlConverterFactory.create()) // Use SimpleXML Converter
                    .client(httpClient.build())
                    .build();

            commonService = retrofit.create(CommonService.class);

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    performLogin();
                }
            }); btnLogout.setOnClickListener(new View.OnClickListener() { // Add this block
                @Override
                public void onClick(View v) {
                    performLogout();
                }
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void performLogout() {
        LogoutRequest logoutRequest = new LogoutRequest("KOUSHIK.CMP", "India@123", "json");
        Call<LogoutResponse> call = commonService.logout(logoutRequest);

        call.enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LogoutResponse logoutResponse = response.body();
                    String displayText = "Is Logged Out: " + logoutResponse.getIsLoggedOut();
                    tvResponse.setText(displayText);
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        tvResponse.setText("Request failed: jai" + errorBody);
                    } catch (Exception e) {
                        e.printStackTrace();
                        tvResponse.setText("Request failed: Unable to parse error response");
                    }
                }
            }

            @Override
            public void onFailure(Call<LogoutResponse> call, Throwable t) {
                t.printStackTrace();
                tvResponse.setText("Request failed:  " + t.getMessage());
            }
        });
    }



    private void performLogin() {
        LoginRequest loginRequest = new LoginRequest("json", "KOUSHIK.CMP", "India@123");
        Call<LoginResponse> call = commonService.login(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    LoginResponse.Response responseBody = loginResponse.getResponse();
                    String displayText = "JSession ID: " + responseBody.getJSESSIONID() + "\nKey: " + responseBody.getKey() + "\nForce Password Change: " + responseBody.isForcePasswordChange();
                    tvResponse.setText(displayText);
                } else {
                    try {
                        // Log and display detailed error message
                        String errorBody = response.errorBody().string();
                        tvResponse.setText("Request failed: " + errorBody);
                    } catch (Exception e) {
                        e.printStackTrace();
                        tvResponse.setText("Request failed: Unable to parse error response");
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                t.printStackTrace();
                tvResponse.setText("Request failed: " + t.getMessage());
            }
        });
    }
}