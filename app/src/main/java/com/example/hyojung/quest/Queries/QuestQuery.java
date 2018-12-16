package com.example.hyojung.quest.Queries;

import java.io.Serializable;
import java.util.ArrayList;

public class QuestQuery extends Query implements Serializable {
    private static final long serialVersionUID = 1L;

    public final static int CANCELED = -2, CREATED = -1, UPLOADED = 0, ACCEPTED = 1, COMPLETED = 2;
    private Long questIndex;
    private Long requester, acceptor;
    private String title, area, reward, comment;
    private double[] coordinate = new double[2];
    private int state = 0;

    public QuestQuery(long requester) {
        this.requester = requester;
        this.state = CREATED;
    }

    public void setQuestIndex(long index) {
        this.questIndex = index;
    }

    public void setAcceptor(long acceptor) {
        this.acceptor = acceptor;
        this.state = ACCEPTED;
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

    public void setCanceled() {
        this.state = CANCELED;
    }

    public void setCompleted() {
        this.state = COMPLETED;
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