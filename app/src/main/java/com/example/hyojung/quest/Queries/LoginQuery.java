package com.example.hyojung.quest.Queries;

public class LoginQuery extends Query {
    private static final long serialVersionUID = 23;
    private long _id;
    private String nickName;
    private String imagePath;

    public LoginQuery(long id, String nickName, String imagePath) {
        this._id = id;
        this.nickName = nickName;
        this.imagePath = imagePath;
    }

    public long get_id() {
        return _id;
    }

    public String getNickName() {
        return nickName;
    }

    public String getImagePath() {
        return imagePath;
    }
}
