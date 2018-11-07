package com.example.hyojung.quest;

import java.util.ArrayList;

public class ChatLog extends ArrayList<String> {
    private long chatIndex;
    private long myId, otherId;

    public ChatLog(long chatIndex, long myId, long otherId) {
        this.chatIndex = chatIndex;
        this.myId = myId;
        this.otherId = otherId;
    }
}
