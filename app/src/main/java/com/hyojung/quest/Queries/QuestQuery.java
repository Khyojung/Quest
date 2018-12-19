package com.hyojung.quest.Queries;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

public class QuestQuery extends Query implements Serializable {
    private static final long serialVersionUID = 1L;

    public final static int DESTROYED = 0, CREATED = 1, UPLOADED = 2, ACCEPTED = 3, COMPLETED = 4;
    public final static int NOT_UPDATED = 0, CONTINUING = 1, DESTROY_AGREED = 2, COMPLETE_AGREED = 3;

    private Long questIndex, quester, questee;

    private String title, area, reward, comment;
    private double[] coordinate = new double[2];
    private int state = 0;

    private int quester_state, questee_state = NOT_UPDATED;

    public QuestQuery(Long quester) {
        this.quester = quester;
        this.state = CREATED;
        this.stateRefresh();
    }

    public void setQuestIndex(Long index) {
        this.questIndex = index;
    }

    public void setQuestee(Long questee) {
        this.questee = questee;
        this.state = ACCEPTED;
        this.questee_state = CONTINUING;
        this.stateRefresh();
    }

    public void setState(final long userID, final int userState) {
        Log.i("userID", String.valueOf(userID));
        if (userID == quester) {
            quester_state = userState;
        }
        else if (userID == questee) {
            questee_state = userState;
        }
        this.stateRefresh();
    }

    private void stateRefresh() {
        if (quester_state == CONTINUING && questee_state == CONTINUING) {
            this.state = ACCEPTED;
        }
        else if (quester_state == CONTINUING && questee_state == NOT_UPDATED) {
            this.state = UPLOADED;
        }
        else if (quester_state == DESTROY_AGREED && questee_state == DESTROY_AGREED) {
            this.state = DESTROYED;
        }
        else if (quester_state == COMPLETE_AGREED && questee_state == COMPLETE_AGREED) {
            this.state = COMPLETED;
        }
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

    public int getState(){
            return this.state;
        }

    public int getQuesterState() {
        return quester_state;
    }

    public int getQuesteeState() {
        return questee_state;
    }

    public long getQuestIndex() {
        return this.questIndex;
    }

    public Long getQuester() {
        return this.quester;
    }

    public Long getQuestee() {
        return this.questee;
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

    public void setCanceled() {
        this.state = DESTROYED;
    }
}