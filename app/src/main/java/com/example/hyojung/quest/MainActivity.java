package com.example.hyojung.quest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    String userName = "김연훈";

    LinearLayout questMainLayout;
    ArrayList<QuestEntry> questList;
    int questCount = 3;
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

        for (int i = 0; i < questCount; i++) {                      // 퀘스트 엔트리 생성
            QuestEntry questEntry = new QuestEntry(getApplicationContext());
            questList.add(questEntry);
        }

        for (int i = 0; i < questList.size(); i++) {                // 레이아웃에 엔트리를 뷰로 추가
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
        switch (requestCode) {
            case ADD_QUEST:
                if (resultCode == Activity.RESULT_OK) {     // Intent로 받아온 정보로 엔트리 추가
                    QuestEntry newQuestEntry = new QuestEntry(getApplicationContext());
                    newQuestEntry.setRequester(userName);
                    newQuestEntry.setQuestInfo(data.getStringExtra("Title")
                                                ,data.getStringExtra("Area")
                                                ,data.getStringExtra("Reward")
                                                ,data.getStringExtra("Comment"));
                    questList.add(newQuestEntry);
                    questMainLayout.addView(newQuestEntry, 0);
                }
                break;
            default:
        }
    }
}
