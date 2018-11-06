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

    public final static int UPLOADED = 0, RESPONDED = 1, ACCEPTED = 2, COMPLETED = 3;
    private long requester, acceptor;
    private ArrayList<Long> respondent;
    private String title, area, reward, comment;
    private int state;

    public QuestEntry(Context context) {
        super(context);
        this.initEntry(context);
    }

    public void setQuestIndex(long index) {
        this.questIndex = index;
    }

    public void setRequester(long requester) {
        this.requester = requester;
    }

    public void addRespondent(long respondent) {
        this.respondent.add(respondent);
    }

    public void setAcceptor(long acceptor) {
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

    public void setState(int state) {
        this.state = state;
    }

    public int getState(){
        return this.state;
    }

    public long getRequester() {
        return this.requester;
    }

    public ArrayList<Long> getRespondent() {
        return this.respondent;
    }

    public long getAcceptor() {
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
        this.state = 0;
        respondent = new ArrayList<Long>();
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.quest_entry, this, true);
    }
}
