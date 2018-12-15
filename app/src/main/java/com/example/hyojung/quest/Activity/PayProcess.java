package com.example.hyojung.quest.Activity;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.hyojung.quest.R;

public class PayProcess extends AppCompatActivity {

    IntentFilter intentFilter = new IntentFilter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kakaopay_connect);
    }

    public void setScheme(String host, String scheme) {
        intentFilter.addDataAuthority(host, scheme);
    }
}
