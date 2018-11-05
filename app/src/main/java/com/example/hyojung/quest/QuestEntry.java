package com.example.hyojung.quest;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class QuestEntry extends LinearLayout {

    private long questIndex;
    private LayoutInflater inflater;

    private String requester, acceptor;
    private ArrayList<String> respondent;
    private String title, area, reward, comment;

    public QuestEntry(Context context) {
        super(context);
        this.initEntry(context);
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public void addRespondent(String respondent) {
        this.respondent.add(respondent);
    }

    public void setAcceptor(String acceptor) {
        this.acceptor = acceptor;
    }

    public void setQuestInfo(String title, String area, String reward, String comment) {
        this.title = title;
        this.area = area;
        this.reward = reward;
        this.comment = comment;
        ((TextView)this.findViewById(R.id.text_entry_title)).setText(this.title);
        ((TextView)this.findViewById(R.id.text_entry_area)).setText(this.area);
        ((TextView)this.findViewById(R.id.text_entry_reward)).setText(this.reward);
    }

    public String getRequester() {
        return this.requester;
    }

    public ArrayList<String> getRespondent() {
        return this.respondent;
    }

    public String getAcceptor() {
        return this.acceptor;
    }

    public ArrayList<String> getQuestInfo() {
        ArrayList<String> info = new ArrayList<String>();
        info.add(this.title);
        info.add(this.area);
        info.add(this.reward);
        info.add(this.comment);
        return info;
    }

    private void initEntry(Context context) {
        respondent = new ArrayList<String>();
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.quest_entry, this, true);
    }
}
