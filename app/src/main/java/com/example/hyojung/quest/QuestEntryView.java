package com.example.hyojung.quest;

import android.content.Context;
import android.os.Parcel;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.io.Serializable;
import java.util.ArrayList;

public class QuestEntryView extends LinearLayout {
    private static final long serialVersionUID = 1L;

    QuestEntry questEntry;

    public QuestEntryView(Context context, QuestEntry questEntry) {
        super(context);
        this.questEntry = questEntry;
    }

    public QuestEntry getQuestEntry() {
        return questEntry;
    }

    public QuestEntryView inflate(Context context) {
        ArrayList<String> entryInfo = questEntry.getQuestInfo();
        LayoutInflater.from(context).inflate(R.layout.quest_entry, this, true);
        ((TextView)this.findViewById(R.id.text_entry_title)).setText(entryInfo.get(0));
        ((TextView)this.findViewById(R.id.text_entry_area)).setText(entryInfo.get(1));
        ((TextView)this.findViewById(R.id.text_entry_reward)).setText(entryInfo.get(2));
        return this;
    }
}
