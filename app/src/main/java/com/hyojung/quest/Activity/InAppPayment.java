package com.hyojung.quest.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.hyojung.quest.JSON.JSONArrayParser;
import com.hyojung.quest.JSON.JSONSendTask;
import com.hyojung.quest.Queries.PointQuery;
import com.example.hyojung.quest.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class InAppPayment extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    final public static int EXIT = 20;
    long userId;
    Button button_point_refresh, button_point_charge;
    TextView pointText;

    JSONSendTask jsonSendTask;

    private String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgCbQ18+WhdLmMa3oh2paJ" +
            "05VO2W0MwnlRjGcDpiRyykQjQCHNSSmbz0y8D1uVHhfFGLgVZ1qD1+P/lztgFJs2uYl3hOq0qSlsK3gFBmqQI55" +
            "GvmfQV3zjGV4CJDlRjpio6FqqqZfO0c7pH7HWsOYFF6/75kB8YzJ+MaRjLE35G0TXMGGIon9l73gsPsgSNBC7mj43hjv" +
            "zPQ8r8apM2bVjukQ3ubpC0oJs20dhePcYYaiOkeJ3COQGHwfbyL5N71AAbJiP75hqu4Y2IF2qCWOaAil4J54xc69GYkAqI" +
            "ZphAkAh5narFS6ghe7hklaI9Eb+GCYLf+WXkiVY9n3GQ8vWQIDAQAB";
    private BillingProcessor billingProcessor;

    private Handler pointRefreshHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            refreshPointText();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_point_view);
        button_point_refresh = (Button) findViewById(R.id.button_point_refresh);
        button_point_charge = (Button) findViewById(R.id.button_point_charge);
        pointText = (TextView) findViewById(R.id.text_quest_point);
        jsonSendTask = new JSONSendTask(new PointQuery(userId, 0, PointQuery.GET_POINT), pointRefreshHandler);

        billingProcessor = new BillingProcessor(this, publicKey, this);
        boolean isAvailable = BillingProcessor.isIabServiceAvailable(this);
        if (isAvailable) {
            billingProcessor.initialize();
        }

        button_point_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jsonSendTask = new JSONSendTask(new PointQuery(userId, 0, PointQuery.GET_POINT), pointRefreshHandler);
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(EXIT);
        finish();
    }

    public void refreshPointText() {
        JSONArrayParser jsonArrayParser = new JSONArrayParser(jsonSendTask.getResultJsonString());
        ArrayList<LinkedHashMap<String, Object>> jsonList = jsonArrayParser.parse();
        HashMap<String, Object> tableEntry = jsonList.get(0);
        pointText.setText(String.valueOf((Long)tableEntry.get("point")) + "원");
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        SkuDetails sku = billingProcessor.getPurchaseListingDetails(productId);
        int amount = Integer.parseInt(productId.substring(1));
        jsonSendTask = new JSONSendTask(new PointQuery(userId, amount, PointQuery.UPDATE_POINT), pointRefreshHandler);
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {

    }

    @Override
    public void onBillingInitialized() {
        button_point_charge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                billingProcessor.purchase(InAppPayment.this, "p10000", "");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (billingProcessor.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        if (billingProcessor != null) {
            billingProcessor.release();
        }
        super.onDestroy();
    }
}
