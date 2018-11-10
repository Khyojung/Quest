package com.example.hyojung.quest.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.hyojung.quest.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

public class CheckKakaoPay extends AppCompatActivity {

    final public static int EXIT = 0;
    long point;
    Button button_point_refresh, button_point_charge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kakao_pay_point_check);
        button_point_refresh = (Button)findViewById(R.id.button_point_refresh);
        button_point_charge = (Button)findViewById(R.id.button_point_charge);
        this.getKakaoPay();

    }

    public void getKakaoPay() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL kakaoHost = new URL("https://kapi.kakao.com/v1/payment/ready");
                    HttpsURLConnection myConnection = (HttpsURLConnection)kakaoHost.openConnection();
                    myConnection.setReadTimeout(30000);
                    myConnection.setConnectTimeout(30000);
                    myConnection.setRequestMethod("POST");
                    myConnection.addRequestProperty("Authorization", "KakaoAK 73699051106ef34cac309c8286bfe9c8");
                    myConnection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

                    myConnection.setRequestProperty("cid", "TC0ONETIME");
                    myConnection.setRequestProperty("partner_order_id", "sample_order_id");
                    myConnection.setRequestProperty("partner_user_id", "sample_user_id");
                    myConnection.setRequestProperty("item_name", "sample_QUEST");
                    myConnection.setRequestProperty("quantity", "1");
                    myConnection.setRequestProperty("total_amount", "1000");
                    myConnection.setRequestProperty("tax_free_amount", "0");
                    myConnection.setRequestProperty("approval_url", "https://www.naver.com");
                    myConnection.setRequestProperty("cancel_url", "https://www.naver.com");
                    myConnection.setRequestProperty("fail_url", "https://www.naver.com");
                    myConnection.setUseCaches(false);
                    myConnection.setDoOutput(true);
                    myConnection.setDoInput(true);
                    int result = myConnection.getResponseCode();
                    Log.i("CONN", String.valueOf(result));
                    if(result == 200) {
                        InputStream responseBody = myConnection.getInputStream();
                        InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                        JsonReader jsonReader = new JsonReader(responseBodyReader);
                        jsonReader.beginObject();
                        HashMap<String, String> jsonResult = new HashMap<String, String>();
                        while (jsonReader.hasNext()) {
                             jsonResult.put(jsonReader.nextName(), jsonReader.nextString());
                        }
                        String getScheme = jsonResult.get("android_app_scheme");
                        Intent payIntent = new Intent();
                        payIntent.setAction(Intent.ACTION_VIEW);
                        payIntent.setData(Uri.parse(getScheme));
                        startActivity(payIntent);
                    }
                    Log.i("CONN", "ABC");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(EXIT);
        finish();
    }
}
