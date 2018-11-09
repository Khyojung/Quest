package com.example.hyojung.quest;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class JSONTask extends AsyncTask<String, String, String> {

    Query query;

    public JSONTask(Query query) {
        this.query = query;
    }


    private HttpURLConnection setConnection(String string) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(string);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept", "application/html");
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
            DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
            dataOutputStream.writeBytes(jsonObject.toString());
            dataOutputStream.flush();
            dataOutputStream.close();
            conn.getResponseCode();
            conn = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(String... args) {
        JSONObject jsonObject = null;
        if (query instanceof LoginQuery) {
            LoginQuery clientLoginQuery = (LoginQuery)query;
            try {
                HttpURLConnection conn = this.setConnection(args[0] + "/login");
                jsonObject = new JSONObject();
                jsonObject.put("uid", clientLoginQuery.get_id());
                jsonObject.put("nickname", clientLoginQuery.getNickName());
                jsonObject.put("imagePath", clientLoginQuery.getImagePath());
                this.sendJSONObject(conn, jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (query instanceof QuestEntry) {
            QuestEntry questEntry = (QuestEntry)query;
            try {
                HttpURLConnection conn = this.setConnection(args[0] + "/add-quest");
                jsonObject = new JSONObject();
                ArrayList<String> entryInfo = questEntry.getQuestInfo();
                jsonObject.put("title", entryInfo.get(0));
                jsonObject.put("place", entryInfo.get(1));
                jsonObject.put("pay", Integer.valueOf(entryInfo.get(2)));
                jsonObject.put("comment", entryInfo.get(3));
                jsonObject.put("quester", questEntry.getRequester());
                this.sendJSONObject(conn, jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (query instanceof ChatQuery) {
            ChatQuery clientChatQuery = (ChatQuery) query;
            try {
                HttpURLConnection conn = this.setConnection(args[0] + "/chat");
                jsonObject = new JSONObject();
                jsonObject.put("cid", clientChatQuery.getQuestId());
                jsonObject.put("sender", clientChatQuery.getSenderId());
                jsonObject.put("receiver", clientChatQuery.getReceiverId());
                jsonObject.put("message", clientChatQuery.getMessage());
                this.sendJSONObject(conn, jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject.toString();
    }
}
