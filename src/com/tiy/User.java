package com.tiy;

/**
 * Created by Paul Dennis on 1/4/2017.
 */
public class User {

    private int userId;
    private String userName;
    private String fullName;

    public User (int userId, String userName, String fullName) {
        this.fullName = fullName;
        this.userName = userName;
        this.userId = userId;
    }

    public User (int userId, String userName) {
        this.userName = userName;
        fullName = userName.split("@")[0];
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
