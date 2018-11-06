package com.example.hyojung.quest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    LinearLayout questMainLayout;
    ArrayList<QuestEntry> questList;
    int questCount = 3;
    final static int ADD_QUEST = 1;
    FloatingActionButton fab;

    long userID;
    String userNickName, profileImagePath;

    TextView profileNickName;
    ImageView profileImage;
    Button button_userinfomodify, button_continue_trade, button_finished_trade, button_charge_send, button_logout;

    public boolean loginCheck(Intent intent) {
        if (intent == null) {
            return false;
        }
        this.userID = intent.getLongExtra("kakaoID",0);
        this.userNickName = intent.getStringExtra("kakaoNickName");
        this.profileImagePath = intent.getStringExtra("kakaoProfileImage");
        return userID != 0;
    }

    public void setProfile() {
        profileNickName = (TextView)findViewById(R.id.text_userNickName);
        profileNickName.setText(this.userNickName);
        profileImage = (ImageView)findViewById(R.id.image_profile);
        try {
            URL url = new URL(this.profileImagePath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            profileImage.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, userNickName + ", " + profileImagePath, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!this.loginCheck(getIntent())) {
            Intent intent = new Intent();
            setResult(LoginActivity.LOGIN_FAILURE);
            Toast.makeText(this, "로그인에 문제가 발생하였습니다.", Toast.LENGTH_LONG).show();
            finish();
        }

        questMainLayout = (LinearLayout)findViewById(R.id.quest_main_layout);
        fab = (FloatingActionButton)findViewById(R.id.fab);

        this.setProfile();
        button_userinfomodify = (Button)findViewById(R.id.button_userinfomodify);
        button_continue_trade = (Button)findViewById(R.id.button_continue_trade);
        button_finished_trade = (Button)findViewById(R.id.button_finished_trade);
        button_charge_send = (Button)findViewById(R.id.button_charge_send);
        button_logout = (Button)findViewById(R.id.button_logout);

        questList = new ArrayList<QuestEntry>();

        for (int i = 0; i < questCount; i++) {                      // 퀘스트 엔트리 생성
            QuestEntry questEntry = new QuestEntry(getApplicationContext());
            questEntry.setQuestIndex(questList.size());
            questEntry.setRequester(this.userID);
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
                    newQuestEntry.setRequester(this.userID);
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
                    if ((tempEntry.getRequester() == this.userID
                            || tempEntry.getRespondent().contains(this.userID)
                            || tempEntry.getAcceptor() == this.userID)
                            && tempEntry.getState() < QuestEntry.COMPLETED) {      // 완료되지 않은 거래만 출력
                        questMainLayout.addView(tempEntry);
                    }
                }
                slideInit();
               break;
            case R.id.button_finished_trade:
                questMainLayout.removeAllViewsInLayout();
                for (int i = 0; i < questList.size(); i++) {
                    QuestEntry tempEntry = questList.get(i);
                    if ((tempEntry.getRequester() == this.userID
                            || tempEntry.getRespondent().contains(this.userID)
                            || tempEntry.getAcceptor() == this.userID)
                            && tempEntry.getState() == QuestEntry.COMPLETED) {      // 완료된 거래만 출력
                        questMainLayout.addView(tempEntry);
                    }
                }
                slideInit();
                break;
            case R.id.button_charge_send:
                break;
            case R.id.button_logout:
                setResult(LoginActivity.LOGOUT);
                finish();
                break;
            default:
                return;
        }
    }

    public void slideInit() {
        ((DrawerLayout)findViewById(R.id.quest_drawer)).closeDrawer(((LinearLayout)findViewById(R.id.slide_menu)));
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
