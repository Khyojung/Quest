package com.hyojung.quest.Queries;

public class PointQuery extends Query {
    private long kakaoID;
    private int addPoint;

    private int queryNumber;
    public static final int GET_POINT = 1, UPDATE_POINT = 2;

    public PointQuery(long userId, int addPoint, int queryNumber) {
        this.kakaoID = userId;
        this.addPoint = addPoint;
        this.queryNumber = queryNumber;
    }

    public long getKakaoID() {
        return kakaoID;
    }

    public int getAddPoint() {
        return addPoint;
    }

    public int getQueryNumber() {
        return queryNumber;
    }
}
