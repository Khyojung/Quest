package com.example.hyojung.quest.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hyojung.quest.BitmapMaker;
import com.example.hyojung.quest.JSON.JSONArrayParser;
import com.example.hyojung.quest.Queries.QuestQuery;
import com.example.hyojung.quest.JSON.JSONSendTask;
import com.example.hyojung.quest.QuestEntryView;
import com.example.hyojung.quest.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class MainActivity extends AppCompatActivity {

    public static final int MODIFY_USER_INFO = 0, ADD_QUEST = 1, VIEW_QUEST = 2, CHECK_KAKAO_PAY = 3, CHAT_TEST = 4;
    public static final int REFRESH_TABLE = 1;

    LinearLayout questMainLayout;
    ArrayList<QuestEntryView> questList;
    int questCount = 3;
    FloatingActionButton fab;

    long userID;
    String kakaoNickName, profileImagePath;

    TextView profileNickName;
    Bitmap bitmap_kakao, bitmap_spare;
    ImageView profileImage;

    Button button_userinfomodify, button_continue_trade, button_finished_trade,
            button_charge_send, button_logout, button_exit, button_developer_test;

    Handler tableRefreshHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            if (message.what == REFRESH_TABLE) {
                new RefreshTableTask();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!this.loginCheck(getIntent())) {
            Toast.makeText(this, "로그인에 문제가 발생하였습니다.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            setResult(LoginActivity.LOGIN_FAILURE);
            finish();
        }

        questMainLayout = (LinearLayout) findViewById(R.id.quest_main_layout);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        tableRefreshHandler.sendEmptyMessage(REFRESH_TABLE);

        this.setProfile();
        button_userinfomodify = (Button) findViewById(R.id.button_userinfomodify);
        button_continue_trade = (Button) findViewById(R.id.button_continue_trade);
        button_finished_trade = (Button) findViewById(R.id.button_finished_trade);
        button_charge_send = (Button) findViewById(R.id.button_charge_send);
        button_logout = (Button) findViewById(R.id.button_logout);
        button_exit = (Button) findViewById(R.id.button_exit);
        button_developer_test = (Button) findViewById(R.id.button_developer_test);

        questList = new ArrayList<QuestEntryView>();

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
            case MODIFY_USER_INFO:
                if (resultCode == ModifyUserInfo.DEFAULT) {
                    profileNickName.setText(kakaoNickName);
                    profileImage.setImageBitmap(bitmap_kakao);
                    break;
                }
                if ((resultCode & ModifyUserInfo.USE_SPARE_NICKNAME) == ModifyUserInfo.USE_SPARE_NICKNAME) {
                    profileNickName.setText(data.getStringExtra("spareName"));
                }
                if ((resultCode & ModifyUserInfo.USE_SPARE_PROFILE) == ModifyUserInfo.USE_SPARE_PROFILE) {
                    bitmap_spare = BitmapMaker.byteArrayToBitmap(data.getByteArrayExtra("spareBitmap"));
                    profileImage.setImageBitmap(bitmap_spare);
                }
                break;
            case ADD_QUEST:
                if (resultCode == AddQuest.QUEST_ADDED) {     // Intent로 받아온 정보로 엔트리 추가
                    QuestQuery newQuestQuery = new QuestQuery();
                    newQuestQuery.setRequester(this.userID);
                    newQuestQuery.setQuestInfo(data.getStringExtra("Title")
                            , data.getStringExtra("Area")
                            , data.getStringExtra("Reward")
                            , data.getStringExtra("Comment"));
                    new JSONSendTask(newQuestQuery, QuestQuery.UPLOADED);
                    QuestEntryView newQuestEntryView = new QuestEntryView(getApplicationContext(), newQuestQuery);
                    newQuestEntryView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            viewQuestEntry(((QuestEntryView) v).getQuestQuery());
                        }
                    });
                    this.addQuestEntryIntoViewAndList(newQuestEntryView);
                }
                break;
            case VIEW_QUEST:
                if (resultCode == ViewQuest.QUEST_REQUEST_CANCELED) {
                    QuestQuery beDeletedEntry = (QuestQuery) data.getSerializableExtra("rc");
                    new JSONSendTask(beDeletedEntry, QuestQuery.CANCELED, tableRefreshHandler);
                }
                else {
                    tableRefreshHandler.sendEmptyMessage(REFRESH_TABLE);
                }
                break;
            case CHECK_KAKAO_PAY:
                if (resultCode == CheckKakaoPay.EXIT) {

                }
                break;
            case CHAT_TEST:
                if (requestCode == ChatRoom.EXIT) {

                }
                break;
            default:
        }
    }

    public boolean loginCheck(Intent intent) {
        if (intent == null) {
            return false;
        }
        this.userID = intent.getLongExtra("kakaoID", -1);
        this.kakaoNickName = intent.getStringExtra("kakaoNickName");
        this.profileImagePath = intent.getStringExtra("kakaoProfileImage");
        return userID != -1;
    }

    public void setProfile() {
        profileNickName = (TextView) findViewById(R.id.text_userNickName);
        profileNickName.setText(this.kakaoNickName);
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
                    bitmap_kakao = BitmapFactory.decodeStream(input);
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
            bitmap_kakao = BitmapMaker.resizeForProfile(bitmap_kakao);
            profileImage.setImageBitmap(bitmap_kakao);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void addQuestEntryIntoViewAndList(QuestEntryView questEntryView) {
        questList.add(questEntryView);
        questMainLayout.addView(questEntryView.inflate());
    }

    public void viewQuestEntry(QuestQuery questQuery) {
        Intent intent = new Intent(getApplicationContext(), ViewQuest.class);
        intent.putExtra("entry", questQuery);
        intent.putExtra("viewerId", userID);
        this.startActivityForResult(intent, VIEW_QUEST);
    }

    public void onClickedInMenu(View v) {
        switch (v.getId()) {
            case R.id.button_userinfomodify:
                Intent intent = new Intent(MainActivity.this, ModifyUserInfo.class);
                intent.putExtra("bitmap", BitmapMaker.BitmapToByteArray(bitmap_kakao));
                startActivityForResult(intent, MODIFY_USER_INFO);
                slideInit();
                break;
            case R.id.button_continue_trade:
                for (int i = 0; i < questList.size(); i++) {
                    QuestQuery tempEntry = questList.get(i).getQuestQuery();
                    if ((tempEntry.getRequester() == this.userID
                            || tempEntry.getRespondent().contains(this.userID)
                            || tempEntry.getAcceptor() == this.userID)
                            && tempEntry.getState() < QuestQuery.COMPLETED) {      // 완료되지 않은 거래만 출력
                        questMainLayout.getChildAt(i).setVisibility(LinearLayout.VISIBLE);
                    } else {
                        questMainLayout.getChildAt(i).setVisibility(LinearLayout.GONE);
                    }
                }
                slideInit();
                break;
            case R.id.button_finished_trade:
                for (int i = 0; i < questList.size(); i++) {
                    QuestQuery tempEntry = questList.get(i).getQuestQuery();
                    if ((tempEntry.getRequester() == this.userID
                            || tempEntry.getRespondent().contains(this.userID)
                            || tempEntry.getAcceptor() == this.userID)
                            && tempEntry.getState() == QuestQuery.COMPLETED) {      // 완료된 거래만 출력
                        questMainLayout.getChildAt(i).setVisibility(LinearLayout.VISIBLE);
                    } else {
                        questMainLayout.getChildAt(i).setVisibility(LinearLayout.GONE);
                    }
                }
                slideInit();
                break;
            case R.id.button_charge_send:
                Intent payIntent = new Intent(MainActivity.this, CheckKakaoPay.class);
                startActivityForResult(payIntent, CHECK_KAKAO_PAY);
                slideInit();
                break;
            case R.id.button_logout:
                setResult(LoginActivity.LOGOUT);
                finish();
                break;
            case R.id.button_exit:
                setResult(LoginActivity.EXIT);
                finish();
                break;
            case R.id.button_developer_test:
                Intent chatIntent = new Intent(MainActivity.this, ChatRoom.class);
                chatIntent.putExtra("myId", this.userID);
                chatIntent.putExtra("otherId", (long) 123123);
                startActivityForResult(chatIntent, CHAT_TEST);
                slideInit();
                break;
            default:
        }
    }

    public long getUserID() {
        return this.userID;
    }

    public void slideInit() {
        ((DrawerLayout) findViewById(R.id.quest_drawer)).closeDrawer(((LinearLayout) findViewById(R.id.slide_menu)));
    }

    @Override
    public void onBackPressed() {
        setResult(LoginActivity.EXIT);
        finish();
    }

    public class RefreshTableTask extends AsyncTask<Void, Void, Void> {

        public RefreshTableTask() {
            this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JSONObject jsonObject = null;
            try {
                HttpURLConnection conn = this.setConnection("http://168.188.127.175:3000/tables");
                jsonObject = new JSONObject();
                jsonObject.put("uid", userID);
                jsonObject.put("tablePass", "request quest tables");
                this.sendJSONObject(conn, jsonObject);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                bufferedReader.close();
                final String resultLine = stringBuilder.toString();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initQuestView(resultLine);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        private HttpURLConnection setConnection(String string) {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(string);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Cache-Control", "no-cache");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.connect();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return conn;
        }

        private void sendJSONObject(HttpURLConnection conn, JSONObject jsonObject) {
            try {
                OutputStreamWriter dataOutputStream = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
                dataOutputStream.write(jsonObject.toString());
                dataOutputStream.flush();
                dataOutputStream.close();
                conn.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void initQuestView(String result) {
            questMainLayout.removeAllViewsInLayout();
            Log.i("result" , result);
            JSONArrayParser jsonArrayParser = new JSONArrayParser(result);
            ArrayList<LinkedHashMap<String, Object>> jsonList = jsonArrayParser.parse();
            for (int i = 0; i < jsonList.size(); i++) {
                HashMap<String, Object> tableEntry = jsonList.get(i);
                QuestQuery questEntry = new QuestQuery();
                questEntry.setQuestIndex((Long) tableEntry.get("tid"));
                questEntry.setRequester((Long) tableEntry.get("quester"));
                questEntry.setQuestInfo((String) tableEntry.get("title")
                        , (String) tableEntry.get("place")
                        , String.valueOf(tableEntry.get("pay"))
                        , (String) tableEntry.get("comment"));
                questEntry.setState(((Long) tableEntry.get("state")).intValue());
                if (questEntry.getState() != QuestQuery.CANCELED) {
                    QuestEntryView newQuestEntryView = new QuestEntryView(getApplicationContext(), questEntry);
                    newQuestEntryView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            viewQuestEntry(((QuestEntryView) v).getQuestQuery());
                        }
                    });
                    addQuestEntryIntoViewAndList(newQuestEntryView);
                }
            }
        }
    }
}
