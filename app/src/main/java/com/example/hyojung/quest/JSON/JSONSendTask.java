package com.example.hyojung.quest.JSON;

import android.os.AsyncTask;
import android.os.Handler;

import com.example.hyojung.quest.Activity.MainActivity;
import com.example.hyojung.quest.Queries.ChatQuery;
import com.example.hyojung.quest.Queries.LoginQuery;
import com.example.hyojung.quest.Queries.Query;
import com.example.hyojung.quest.Queries.QuestQuery;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class JSONSendTask extends AsyncTask<Void, Void, Void> {

    Query query;
    String urlString = "http://168.188.127.175:3000";
    int taskType = 0;
    Handler handler;

    public JSONSendTask(Query query, int taskType) {
        this.query = query;
        this.taskType = taskType;
        this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public JSONSendTask(Query query, int taskType, Handler postHandler) {
        this.query = query;
        this.taskType = taskType;
        this.handler = postHandler;
        this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

    private String sendJSONObject(HttpURLConnection conn, JSONObject jsonObject) {
        String result = "";
        try {
            OutputStreamWriter dataOutputStream = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            dataOutputStream.write(jsonObject.toString());
            dataOutputStream.flush();
            dataOutputStream.close();

            InputStream inputStream = conn.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            result = stringBuilder.toString();
            if (result.equals("CANCELED")) {
                handler.sendEmptyMessage(MainActivity.REFRESH_TABLE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        HttpURLConnection conn = null;
        JSONObject jsonObject = new JSONObject();
        if (query instanceof LoginQuery) {
            LoginQuery clientLoginQuery = (LoginQuery)query;
            conn = this.setConnection(urlString + "/login");
            jsonObject = new JSONObject();
            try {
                jsonObject.put("uid", clientLoginQuery.get_id());
                jsonObject.put("nickname", clientLoginQuery.getNickName());
                jsonObject.put("imagePath", clientLoginQuery.getImagePath());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (query instanceof QuestQuery) {
            QuestQuery clientQuestQuery = (QuestQuery)query;
            String route = "";
            switch (taskType) {
                case QuestQuery.UPLOADED:
                    route = "/addQuest";
                    break;
                case QuestQuery.CANCELED:
                    route = "/cancelQuest";
                    break;
                    default:
            }
            conn = this.setConnection(urlString + route);
            try {
                ArrayList<String> questInfo = clientQuestQuery.getQuestInfo();
                if (taskType == QuestQuery.CANCELED) {
                    jsonObject.put("tid", clientQuestQuery.getQuestIndex());
                }
                jsonObject.put("title", questInfo.get(0));
                jsonObject.put("place", questInfo.get(1));
                jsonObject.put("pay", Long.valueOf(questInfo.get(2)));
                jsonObject.put("comment", questInfo.get(3));
                jsonObject.put("requester", clientQuestQuery.getRequester());
                jsonObject.put("state", clientQuestQuery.getState());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (query instanceof ChatQuery) {
            ChatQuery clientChatQuery = (ChatQuery) query;
            conn = this.setConnection(urlString + "/chat");
            try {
                jsonObject.put("cid", clientChatQuery.getQuestId());
                jsonObject.put("sender", clientChatQuery.getSenderId());
                jsonObject.put("receiver", clientChatQuery.getReceiverId());
                jsonObject.put("message", clientChatQuery.getMessage());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        this.sendJSONObject(conn, jsonObject);
        return null;
    }
}