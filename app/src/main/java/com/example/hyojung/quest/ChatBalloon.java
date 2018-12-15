package com.example.hyojung.quest;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    public void resize() {
        final int displayWidth = getResources().getDisplayMetrics().widthPixels;
        final LinearLayout boxLayout = (LinearLayout)this.findViewById(R.id.chat_totalchatbox);
        ViewTreeObserver observer = boxLayout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (boxLayout.getWidth() > displayWidth * 0.8) {
                    boxLayout.getLayoutParams().width = (int)(displayWidth * 0.8);
                    boxLayout.requestLayout();
                }
            }
        });
    }
    public ChatBalloon inflate(Context context) {
        ChatBalloon thisFrame = (ChatBalloon) LayoutInflater.from(context).inflate(R.layout.chat_other_balloon, this, true);
        LinearLayout textList = ((LinearLayout)thisFrame.findViewById(R.id.balloon_text_list));
        for (int i = 0; i < inputString.size(); i++) {
            TextView tempText = (TextView) View.inflate(context, R.layout.chat_balloon, null);
            tempText.setText(inputString.get(i));
            textList.addView(tempText);
            textList.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }
        return thisFrame;
    }

}
