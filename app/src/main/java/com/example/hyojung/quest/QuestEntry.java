package com.example.hyojung.quest;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class QuestEntry extends LinearLayout {

    TextView titleView, areaView, rewardView;
    String info;

    public QuestEntry(Context context) {
        super(context);
        this.idInit();
        this.inflateLayout(context);
    }

    public QuestEntry(Context context, String title, String area, String reward, String info) {
        super(context);
        this.idInit();
        titleView.setText(title);
        areaView.setText(area);
        rewardView.setText(reward);
        this.inflateLayout(context);
    }

    private void idInit() {
        titleView = findViewById(R.id.text_entry_title);
        areaView = findViewById(R.id.text_entry_area);
        rewardView = findViewById(R.id.text_entry_reward);
    }

    private void inflateLayout(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.quest_entry, this, true);
        this.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

}
