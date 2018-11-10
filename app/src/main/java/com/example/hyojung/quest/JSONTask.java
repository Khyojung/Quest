package com.example.hyojung.quest;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.LinearLayout;

import com.example.hyojung.quest.Queries.ChatQuery;
import com.example.hyojung.quest.Queries.LoginQuery;
import com.example.hyojung.quest.Queries.Query;
import com.example.hyojung.quest.Queries.QuestQuery;
import com.example.hyojung.quest.Queries.RequestTableQuery;

import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class JSONTask extends AsyncTask<String, String, String> {

    Query query;
    Context context;

    public JSONTask(Query query, Context context) {
        this.query = query;
        context = context;
        this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "http://168.188.127.175:3000");
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

    @Override
    protected synchronized String doInBackground(String... args) {
        JSONObject jsonObject = null;
        if (query instanceof LoginQuery) {
            LoginQuery clientLoginQuery = (LoginQuery)query;
            HttpURLConnection conn = this.setConnection(args[0] + "/login");
            jsonObject = new JSONObject();
            jsonObject.put("uid", clientLoginQuery.get_id());
            jsonObject.put("nickname", clientLoginQuery.getNickName());
            jsonObject.put("imagePath", clientLoginQuery.getImagePath());
            this.sendJSONObject(conn, jsonObject);
            conn = null;
            return null;
        }
        else if (query instanceof RequestTableQuery) {
            try {
                HttpURLConnection conn = this.setConnection(args[0] + "/tables");
                jsonObject = new JSONObject();
                jsonObject.put("tablePass", "request quest tables");
                this.sendJSONObject(conn, jsonObject);
                Log.i("tableTask", "request");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }
                bufferedReader.close();
                return stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (query instanceof QuestQuery) {
            QuestQuery questQuery = (QuestQuery)query;
            HttpURLConnection conn = this.setConnection(args[0] + "/addQuest");
            jsonObject = new JSONObject();
            ArrayList<String> entryInfo = questQuery.getQuestInfo();
            jsonObject.put("title", entryInfo.get(0));
            jsonObject.put("place", entryInfo.get(1));
            jsonObject.put("pay", Integer.valueOf(entryInfo.get(2)));
            jsonObject.put("comment", entryInfo.get(3));
            jsonObject.put("requester", questQuery.getRequester());
            this.sendJSONObject(conn, jsonObject);
            conn = null;
            return null;
        }
        else if (query instanceof ChatQuery) {
            ChatQuery clientChatQuery = (ChatQuery) query;
            HttpURLConnection conn = this.setConnection(args[0] + "/chat");
            jsonObject = new JSONObject();
            jsonObject.put("cid", clientChatQuery.getQuestId());
            jsonObject.put("sender", clientChatQuery.getSenderId());
            jsonObject.put("receiver", clientChatQuery.getReceiverId());
            jsonObject.put("message", clientChatQuery.getMessage());
            this.sendJSONObject(conn, jsonObject);
            conn = null;
            return null;
        }
        return jsonObject.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        if (query instanceof RequestTableQuery && result != null) {
            ((MainActivity)context).initQuestView(result);
        }
    }
}