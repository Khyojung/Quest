package com.hyojung.quest.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyojung.quest.BitmapMaker;
import com.hyojung.quest.GPSInfomation;
import com.hyojung.quest.GlobalApplication;
import com.hyojung.quest.JSON.JSONArrayParser;
import com.hyojung.quest.Queries.QuestQuery;
import com.hyojung.quest.JSON.JSONSendTask;
import com.hyojung.quest.QuestEntryView;
import com.example.hyojung.quest.R;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

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

    public static final int MODIFY_USER_INFO = 0, ADD_QUEST = 1, VIEW_QUEST = 2, IN_APP_PAY = 3, CHAT_TEST = 4;

    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayout questMainLayout;
    ArrayList<QuestEntryView> questList;
    FloatingActionButton fab;

    private long userID;
    private String kakaoNickName, profileImagePath;

    private GPSInfomation gpsInfomation;
    private boolean isAccessFineLocation = false;
    private boolean isAccessCoarseLocation = false;
    private boolean isPermission = false;

    TextView profileNickName;
    Bitmap bitmap_kakao, bitmap_spare;
    ImageView profileImage;

    Button button_userinfomodify, button_uploadedQuest, button_continueQuest, button_completedQuest,
            button_charge_send, button_logout, button_exit;

    Handler tableRefreshHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            new RefreshTableTask(gpsInfomation.getUserLocation(), message.what);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GlobalApplication.setCurrentActivity(this);
        this.callPermission();
        if (this.isPermission) {
            gpsInfomation = new GPSInfomation(MainActivity.this);
        }

        if (!this.loginCheck(getIntent())) {
            Toast.makeText(this, "로그인에 문제가 발생하였습니다.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                tableRefreshHandler.sendEmptyMessage(QuestQuery.UPLOADED);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        questMainLayout = (LinearLayout) findViewById(R.id.quest_main_layout);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        tableRefreshHandler.sendEmptyMessage(QuestQuery.UPLOADED);

        if (this.userID != 0) {
            this.setProfile();
        }

        button_userinfomodify = (Button) findViewById(R.id.button_userinfomodify);
        button_uploadedQuest = (Button) findViewById(R.id.button_uploadedQuest);
        button_continueQuest = (Button) findViewById(R.id.button_continueQuest);
        button_completedQuest = (Button) findViewById(R.id.button_completeQuest);
        button_charge_send = (Button) findViewById(R.id.button_charge_send);
        button_logout = (Button) findViewById(R.id.button_logout);
        button_exit = (Button) findViewById(R.id.button_exit);


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
                    QuestQuery newQuestQuery = new QuestQuery(this.userID);
                    newQuestQuery.setQuestInfo(data.getStringExtra("Title")
                            , data.getStringExtra("Place")
                            , data.getStringExtra("Reward")
                            , data.getStringExtra("Comment"));
                    newQuestQuery.setPosition(data.getDoubleArrayExtra("Position"));
                    newQuestQuery.setState(userID, QuestQuery.CONTINUING);
                    new JSONSendTask(newQuestQuery);
                    QuestEntryView newQuestEntryView = new QuestEntryView(getApplicationContext(), newQuestQuery);
                    newQuestEntryView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            viewQuestEntry(((QuestEntryView) v).getQuestQuery());
                        }
                    });
                    tableRefreshHandler.sendEmptyMessage(QuestQuery.UPLOADED);
                }
                break;
            case VIEW_QUEST:
                if (resultCode != ViewQuest.BACK_PRESSED) {
                    QuestQuery beUpdatedQuest = (QuestQuery) data.getSerializableExtra("resultUpdate");
                    new JSONSendTask(beUpdatedQuest, tableRefreshHandler);
                }
                else {
                    tableRefreshHandler.sendEmptyMessage(QuestQuery.UPLOADED);
                }
                break;
            case IN_APP_PAY:
                if (resultCode == InAppPayment.EXIT) {

                }
                break;
            case CHAT_TEST:
                if (resultCode == ChatRoom.EXIT) {

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
        return this.userID != -1;
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
        Intent intent = new Intent(MainActivity.this, ViewQuest.class);
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
            case R.id.button_uploadedQuest:
                this.tableRefreshHandler.sendEmptyMessage(QuestQuery.UPLOADED); // 수락 가능한
                slideInit();
                break;
            case R.id.button_continueQuest:
                this.tableRefreshHandler.sendEmptyMessage(QuestQuery.ACCEPTED); // 진행중인
                slideInit();
                break;
            case R.id.button_completeQuest:
                this.tableRefreshHandler.sendEmptyMessage(QuestQuery.COMPLETED); // 완료된 거래만 출력
                slideInit();
                break;
            case R.id.button_charge_send:
                Intent payIntent = new Intent(MainActivity.this, InAppPayment.class);
                startActivityForResult(payIntent, IN_APP_PAY);
                slideInit();
                break;
            case R.id.button_logout:
                UserManagement.requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
                        Intent logoutIntent = new Intent(GlobalApplication.getCurrentActivity(), LoginActivity.class);
                        startActivity(logoutIntent);
                        finish();
                    }
                });
                break;
            case R.id.button_exit:
                finish();
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == 1000
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            isAccessFineLocation = true;

        } else if (requestCode == 1001
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            isAccessCoarseLocation = true;
        }

        if (isAccessFineLocation && isAccessCoarseLocation) {
            isPermission = true;
        }
    }

    // 전화번호 권한 요청
    private void callPermission() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1000);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1001);
        } else {
            isPermission = true;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public class RefreshTableTask extends AsyncTask<Void, Void, Void> {

        Location location = null;
        int questStatus = 0;

        public RefreshTableTask(Location gpsLocation, int questStatus) {
            this.location = gpsLocation;
            this.questStatus = questStatus;
            this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JSONObject jsonObject = null;
            try {
                HttpURLConnection conn = this.setConnection("http://168.188.127.175:3000/tables");
                jsonObject = new JSONObject();
                jsonObject.put("uid", userID);
                jsonObject.put("ordered", (this.location != null && questStatus == QuestQuery.UPLOADED));
                jsonObject.put("status", this.questStatus);
                if (this.location != null) {
                    jsonObject.put("latitude", this.location.getLatitude());
                    jsonObject.put("longitude", this.location.getLongitude());
                }

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
            JSONArrayParser jsonArrayParser = new JSONArrayParser(result);
            ArrayList<LinkedHashMap<String, Object>> jsonList = jsonArrayParser.parse();
            for (int i = 0; i < jsonList.size(); i++) {
                HashMap<String, Object> tableEntry = jsonList.get(i);
                QuestQuery questEntry = new QuestQuery((Long) tableEntry.get("quester"));
                questEntry.setState(questEntry.getQuester(), ((Long) tableEntry.get("quester_state")).intValue());
                if (tableEntry.get("questee") != null) {
                    questEntry.setQuestee((Long) tableEntry.get("questee"));
                    questEntry.setState(questEntry.getQuestee(), ((Long) tableEntry.get("questee_state")).intValue());
                }
                questEntry.setQuestIndex((Long) tableEntry.get("tid"));
                questEntry.setQuestInfo((String) tableEntry.get("title")
                        , (String) tableEntry.get("place")
                        , String.valueOf(tableEntry.get("pay"))
                        , (String) tableEntry.get("comment"));
                Object latitude = tableEntry.get("latitude"), longitude = tableEntry.get("longitude");
                if (latitude instanceof Long) {
                    latitude = ((Long)latitude).doubleValue();
                }
                if (longitude instanceof Long) {
                    longitude = ((Long)longitude).doubleValue();
                }
                questEntry.setPosition(new double[] {(Double)latitude, (Double)longitude});

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
