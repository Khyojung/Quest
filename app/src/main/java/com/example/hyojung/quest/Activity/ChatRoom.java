package com.example.hyojung.quest.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.example.hyojung.quest.ChatBalloon;
import com.example.hyojung.quest.ChatLog;
import com.example.hyojung.quest.R;

public class ChatRoom extends AppCompatActivity {

    ChatLog chatLog;

    final public static int EXIT = 20;
    ScrollView chat_scroll;
    LinearLayout chatList;
    Button button_chat_send;
    EditText textbox_input;
    long myId, otherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quest_chat);
        Intent chatIntent = getIntent();
        chat_scroll = (ScrollView)findViewById(R.id.chat_scroll);
        chatList = (LinearLayout)findViewById(R.id.chat_list);
        button_chat_send = (Button)findViewById(R.id.button_chat_send);
        textbox_input = (EditText)findViewById(R.id.textbox_input_chat);

        button_chat_send.setVisibility(Button.GONE);
        textbox_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().replace(' ', '\0').isEmpty()) {
                    button_chat_send.setVisibility(Button.GONE);
                }
                else {
                    button_chat_send.setVisibility(Button.VISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().replace(' ', '\0').isEmpty()) {
                    button_chat_send.setVisibility(Button.GONE);
                }
                else {
                    button_chat_send.setVisibility(Button.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().replace(' ', '\0').isEmpty()) {
                    button_chat_send.setVisibility(Button.GONE);
                }
                else {
                    button_chat_send.setVisibility(Button.VISIBLE);
                }
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_chat_send:
                String text = textbox_input.getText().toString();
                ChatBalloon myBalloon = new ChatBalloon(getApplicationContext());
                myBalloon.addChatString(text);
                myBalloon = myBalloon.inflate(getApplicationContext());
                myBalloon.setMyChat();
                myBalloon.resize();
                chatList.addView(myBalloon);
                ChatBalloon otherBalloon = new ChatBalloon(getApplicationContext());
                otherBalloon.addChatString("Hello World!");
                otherBalloon = otherBalloon.inflate(getApplicationContext());
                otherBalloon.resize();
                chatList.addView(otherBalloon);

                chat_scroll.fullScroll(ScrollView.FOCUS_DOWN);
                break;
                default:
        }
    }



    @Override
    public void onBackPressed() {
        setResult(EXIT);
        finish();
    }
}
