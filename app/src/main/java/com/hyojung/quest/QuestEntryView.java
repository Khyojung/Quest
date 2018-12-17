package com.hyojung.quest;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.hyojung.quest.Queries.QuestQuery;
import com.example.hyojung.quest.R;

import java.util.ArrayList;

public class QuestEntryView extends LinearLayout {
    private static final long serialVersionUID = 1L;

    QuestQuery questQuery;

    public QuestEntryView(Context context, QuestQuery questQuery) {
        super(context);
        this.questQuery = questQuery;
    }

    public QuestQuery getQuestQuery() {
        return questQuery;
    }

    public boolean equalEntry(QuestQuery otherQuestQuery) {
        return this.questQuery.equals(otherQuestQuery);
    }

    public QuestEntryView inflate() {
        ArrayList<String> entryInfo = questQuery.getQuestInfo();
        LayoutInflater.from(super.getContext()).inflate(R.layout.quest_entry, this, true);
        ((TextView)this.findViewById(R.id.text_entry_title)).setText(entryInfo.get(0));
        ((TextView)this.findViewById(R.id.text_entry_area)).setText(entryInfo.get(1));
        ((TextView)this.findViewById(R.id.text_entry_reward)).setText(entryInfo.get(2));
        return this;
    }
}
