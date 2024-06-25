package com.example.api_demo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class MainActivity extends AppCompatActivity {
    private TextView tvResponse;
    private CommonService commonService;
    private String cookie = "JSESSIONID=ruaiHRRPFSrUx0xzOXhIUrnkieUyWyf9qtbABZX0.bdsi-stg-use1-app-01-p-stg1-app1"; // Replace with your actual cookie value

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
                    .client(httpClient.build())
                    .addConverterFactory(SimpleXmlConverterFactory.create()) // For XML// For JSON
                    .build();
            commonService = retrofit.create(CommonService.class);

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    performLogin();
                }
            });
            btnLogout.setOnClickListener(new View.OnClickListener() {
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
        LogoutRequest logoutRequest = new LogoutRequest("false", "KOUSHIK.CMP", "India@123", "json");
        Call<ResponseBody> call = commonService.logout(cookie, logoutRequest);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String jsonResponse = response.body().string();
                        JSONObject jsonObject = new JSONObject(jsonResponse);
                        String isLoggedOut = jsonObject.optString("is-logged-out");
                        String displayText = "Is Logged Out: " + isLoggedOut;
                        tvResponse.setText(displayText);
                    } catch (Exception e) {
                        e.printStackTrace();
                        tvResponse.setText("Request failed: Unable to parse JSON response");
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        tvResponse.setText("Request failed: " + errorBody);
                    } catch (Exception e) {
                        e.printStackTrace();
                        tvResponse.setText("Request failed: Unable to parse error response");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                tvResponse.setText("Request failed: " + t.getMessage());
            }
        });
    }
    private void performLogin() {
        LoginRequest loginRequest = new LoginRequest("json", "KOUSHIK.CMP" ,"India@123");
        Call<ResponseBody> call = commonService.login(loginRequest);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String xmlResponse = response.body().string();
                        String JSessionID = parseXmlResponse(xmlResponse, "JSESSIONID");
                        String key = parseXmlResponse(xmlResponse, "key");
                        String forcePasswordChange = parseXmlResponse(xmlResponse, "forcePasswordChange");
                        String displayText = "JSession ID: " + JSessionID + "\nKey: " + key + "\nForce Password Change: " + forcePasswordChange;
                        tvResponse.setText(displayText);
                    } catch (Exception e) {
                        e.printStackTrace();
                        tvResponse.setText("Request failed: Unable to parse XML response");
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        tvResponse.setText("Request failed: " + errorBody);
                    } catch (Exception e) {
                        e.printStackTrace();
                        tvResponse.setText("Request failed: Unable to parse error response");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                tvResponse.setText("Request failed: " + t.getMessage());
            }
        });
    }

    private String parseXmlResponse(String xmlResponse, String tagName) throws XmlPullParserException, IOException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(new StringReader(xmlResponse));

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && parser.getName().equals(tagName)) {
                parser.next();
                return parser.getText();
            }
            eventType = parser.next();
        }
        return null;
    }
}
