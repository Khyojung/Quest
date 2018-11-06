package com.example.hyojung.quest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    LinearLayout questMainLayout;
    ArrayList<QuestEntry> questList;
    int questCount = 3;
    final static int USER_INFO_CHANGE = 0, ADD_QUEST = 1, VIEW_QUEST = 2;
    FloatingActionButton fab;

    long userID;
    String userNickName, profileImagePath;

    TextView profileNickName;
    Bitmap bitmap = null;
    ImageView profileImage;

    Button button_userinfomodify, button_continue_trade, button_finished_trade,
            button_charge_send, button_logout, button_exit;

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
        profileImage = (ImageView) findViewById(R.id.image_profile);
        this.setProfileImage();
    }

    public void setProfileImage() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(profileImagePath);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();

                    InputStream input = connection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(input);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        try {
            thread.join();
            int imageWidth = bitmap.getWidth(), imageHeight = bitmap.getHeight();
            int maxLength = Math.max(imageWidth, imageHeight), minLength = Math.min(imageWidth, imageHeight);
            int startPosition = (maxLength - minLength) / 2;
            bitmap = Bitmap.createBitmap(bitmap, imageWidth > imageHeight ? startPosition : 0,  imageHeight > imageWidth ? startPosition : 0, minLength, minLength);
            profileImage.setImageBitmap(bitmap);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
        button_exit = (Button)findViewById(R.id.button_exit);

        questList = new ArrayList<QuestEntry>();

        for (int i = 0; i < questCount; i++) {                      // 퀘스트 엔트리 생성
            QuestEntry questEntry = new QuestEntry(getApplicationContext());
            questEntry.setQuestIndex(questList.size());
            questEntry.setRequester(this.userID);
            questEntry.setQuestInfo(i + "번째 요청 : 아메리카노", "충남대 5호관 604호", "0"+"원", "테스트용");
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
            case USER_INFO_CHANGE:
                break;
            case ADD_QUEST:
                if (resultCode == AddQuest.QUEST_ADDED) {     // Intent로 받아온 정보로 엔트리 추가
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
            case VIEW_QUEST:
                break;
            default:
        }
    }

    public void onClickedQuestEntry(View v) {
        QuestEntry selectedEntry = new QuestEntry(getApplicationContext());
        if (v.getId() == R.id.quest_main_layout) {
            Toast.makeText(this, "entry clicked.", Toast.LENGTH_LONG).show();
            /*  issue #3
            Intent intent = new Intent(MainActivity.this, ViewQuest.class);
            intent.putExtra("entry", selectedEntry);
            intent.putExtra("viewerId", userID);
            startActivityForResult(intent, VIEW_QUEST);
            */
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
            case R.id.button_exit:
                setResult(LoginActivity.EXIT);
                finish();
                break;
            default:
        }
    }

    public long getUserID() {
        return this.userID;
    }

    public void slideInit() {
        ((DrawerLayout)findViewById(R.id.quest_drawer)).closeDrawer(((LinearLayout)findViewById(R.id.slide_menu)));
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
