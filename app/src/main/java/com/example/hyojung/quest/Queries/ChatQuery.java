package com.example.hyojung.quest.Queries;

public class ChatQuery extends Query {
    private static final long serialVersionUID = 1L;
    private long questId, senderId, receiverId;
    String message;

    public ChatQuery(long questId, long senderId, long receiverId, String message) {
        this.questId = questId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
    }

    public long getQuestId() {
        return questId;
    }

    public long getSenderId() {
        return senderId;
    }

    public long getReceiverId() {
        return receiverId;
    }

    public String getMessage() {
        return message;
    }
}
