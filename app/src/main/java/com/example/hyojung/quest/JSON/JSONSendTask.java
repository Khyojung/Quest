package com.example.hyojung.quest.JSON;

import android.os.AsyncTask;

import com.example.hyojung.quest.Queries.ChatQuery;
import com.example.hyojung.quest.Queries.LoginQuery;
import com.example.hyojung.quest.Queries.Query;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

public class JSONSendTask extends AsyncTask<Void, String, Void> {

    Query query;
    String urlString = "http://168.188.127.175:3000";

    public JSONSendTask(Query query) {
        this.query = query;
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
    protected Void doInBackground(Void... voids) {
        JSONObject jsonObject = null;
        if (query instanceof LoginQuery) {
            LoginQuery clientLoginQuery = (LoginQuery)query;
            HttpURLConnection conn = this.setConnection(urlString + "/login");
            jsonObject = new JSONObject();
            jsonObject.put("uid", clientLoginQuery.get_id());
            jsonObject.put("nickname", clientLoginQuery.getNickName());
            jsonObject.put("imagePath", clientLoginQuery.getImagePath());
            this.sendJSONObject(conn, jsonObject);
            conn = null;
        }
        else if (query instanceof ChatQuery) {
            ChatQuery clientChatQuery = (ChatQuery) query;
            HttpURLConnection conn = this.setConnection(urlString + "/chat");
            jsonObject = new JSONObject();
            jsonObject.put("cid", clientChatQuery.getQuestId());
            jsonObject.put("sender", clientChatQuery.getSenderId());
            jsonObject.put("receiver", clientChatQuery.getReceiverId());
            jsonObject.put("message", clientChatQuery.getMessage());
            this.sendJSONObject(conn, jsonObject);
            conn = null;
        }
        return null;
    }

}