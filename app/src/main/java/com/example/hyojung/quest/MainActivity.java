package com.example.hyojung.quest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    LinearLayout questMainLayout;
    ArrayList<QuestEntry> questList;
    final static int ADD_QUEST = 1;
    View mainView;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        questMainLayout = (LinearLayout)findViewById(R.id.quest_main_layout);
        fab = (FloatingActionButton)findViewById(R.id.fab);
        questList = new ArrayList<QuestEntry>();
        Intent intent = getIntent();

        for (int i = 0; i < questList.size(); i++) {
            questMainLayout.addView(questList.get(i));
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddQuest.class);
                startActivityForResult(intent, ADD_QUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ADD_QUEST:
                if (resultCode == Activity.RESULT_OK) {
                    QuestEntry newQuestEntry = new QuestEntry(getApplicationContext()
                            , data.getStringExtra("Title")
                            , data.getStringExtra("Area")
                            , data.getStringExtra("Reward")
                            , data.getStringExtra("Info"));
                    questList.add(newQuestEntry);
                    questMainLayout.addView(newQuestEntry);
                    // Issue #1
                }
                break;
            default:
        }
    }
}
