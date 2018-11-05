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
import android.widget.Button;
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

    Button button_userinfomodify, button_continue_trade, button_finished_trade, button_charge_send, button_logout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        questMainLayout = (LinearLayout)findViewById(R.id.quest_main_layout);

        button_userinfomodify = (Button)findViewById(R.id.button_userinfomodify);
        button_continue_trade = (Button)findViewById(R.id.button_continue_trade);
        button_finished_trade = (Button)findViewById(R.id.button_finished_trade);
        button_charge_send = (Button)findViewById(R.id.button_charge_send);
        button_logout = (Button)findViewById(R.id.button_logout);
        fab = (FloatingActionButton)findViewById(R.id.fab);

        questList = new ArrayList<QuestEntry>();

        for (int i = 0; i < questCount; i++) {                      // 퀘스트 엔트리 생성
            QuestEntry questEntry = new QuestEntry(getApplicationContext());
            questEntry.setQuestIndex(questList.size());
            questEntry.setRequester(this.userName);
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
                    newQuestEntry.setQuestIndex(questList.size());          // 거래번호
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

    public void onClickedInMenu(View v) {
        switch (v.getId()) {
            case R.id.button_userinfomodify:
                break;
            case R.id.button_continue_trade:
                questMainLayout.removeAllViewsInLayout();
                for (int i = 0; i < questList.size(); i++) {
                    QuestEntry tempEntry = questList.get(i);
                    if ((tempEntry.getRequester().equals(userName)
                            || tempEntry.getRespondent().contains(userName)
                            || tempEntry.getAcceptor().equals(userName))
                            && tempEntry.getState() < QuestEntry.COMPLETED) {      // 완료되지 않은 거래만 출력
                        questMainLayout.addView(tempEntry);
                    }
                }

               break;
            case R.id.button_finished_trade:
                questMainLayout.removeAllViewsInLayout();
                for (int i = 0; i < questList.size(); i++) {
                    QuestEntry tempEntry = questList.get(i);
                    if ((tempEntry.getRequester().equals(userName)
                            || tempEntry.getRespondent().contains(userName)
                            || tempEntry.getAcceptor().equals(userName))
                            && tempEntry.getState() == QuestEntry.COMPLETED) {      // 완료된 거래만 출력
                        questMainLayout.addView(tempEntry);
                    }
                }
                break;
            case R.id.button_charge_send:
                break;
            case R.id.button_logout:
                break;
            default:
                return;
        }
    }

    public void slideInit() {
        LinearLayout slide = (LinearLayout)findViewById(R.id.slide_menu);
        for (float i = 0; i >= -slide.getWidth(); i--) {
            slide.setTranslationX(i);
        }
    }

}
