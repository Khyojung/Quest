package com.example.hyojung.quest.Queries;

import java.io.Serializable;
import java.util.ArrayList;

public class QuestQuery extends Query implements Serializable {
    private static final long serialVersionUID = 1L;

    public final static int CANCELED = -1, UPLOADED = 0, RESPONDED = 1, ACCEPTED = 2, NOT_PAID = 3, COMPLETED = 4;
    private Long questIndex;
    private Long requester, acceptor;
    private ArrayList<Long> respondent = new ArrayList<Long>();
    private String title, area, reward, comment;
    private double[] coordinate = new double[2];
    private int state = 0;

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
    }

    public void setPosition(double[] coordinate) {
        if (coordinate.length == 2) {
            System.arraycopy(coordinate, 0, this.coordinate, 0, 2);
        }
    }

    public double[] getPosition() {
        return this.coordinate;
    }

    public void setState(int state) {
            this.state = state;
        }

    public int getState(){
            return this.state;
        }

    public long getQuestIndex() {
        return this.questIndex;
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

    @Override
    public boolean equals(Object otherQuestEntry) {
        if (!(otherQuestEntry instanceof QuestQuery)) {
            return false;
        } else if (otherQuestEntry == null) {
            return false;
        }
        return this.questIndex == ((QuestQuery)otherQuestEntry).getQuestIndex();
    }
}