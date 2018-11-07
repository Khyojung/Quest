package com.example.hyojung.quest;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;

public class ChatBalloon extends LinearLayout {

    long chatterId;
    ArrayList<String> inputString;
    Date date;

    public ChatBalloon(Context context) {
        super(context);
        inputString = new ArrayList<String>();
        date = new Date(System.currentTimeMillis());
    }

    public void addChatString(String string) {
        inputString.add(string);
    }

    public void setMyChat() {
        ((ImageView)this.findViewById(R.id.chatter_profile)).setVisibility(ImageView.GONE);
        ((TextView)this.findViewById(R.id.chatter_nickname)).setVisibility(TextView.GONE);
        this.setGravity(Gravity.RIGHT);
    }

    public ChatBalloon inflate(Context context) {
        LinearLayout thisFrame = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.chat_other_balloon, this, true);
        for (int i = 0; i < inputString.size(); i++) {
            TextView tempText = (TextView) View.inflate(context, R.layout.chat_balloon, null);
            tempText.setText(inputString.get(i));
            ((LinearLayout)this.findViewById(R.id.balloon_text_list)).addView(tempText);
            ((LinearLayout)this.findViewById(R.id.balloon_text_list)).setLayoutParams(
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            );
        }
        return this;
    }

}
