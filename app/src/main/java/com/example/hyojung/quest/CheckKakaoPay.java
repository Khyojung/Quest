package com.example.hyojung.quest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

public class CheckKakaoPay extends AppCompatActivity {

    final static int EXIT = 0;
    long point;
    Button button_point_refresh, button_point_charge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kakao_pay_point_check);
        button_point_refresh = (Button)findViewById(R.id.button_point_refresh);
        button_point_charge = (Button)findViewById(R.id.button_point_charge);
    }

    @Override
    public void onBackPressed() {
        setResult(EXIT);
        finish();
    }
}
