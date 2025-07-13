package com.example.androidbookingapplicationproject.models;

public class UserDisplay {
    public int userId;
    public String userName; // Đảm bảo tên này là `userName` để đồng bộ

    public UserDisplay(int userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    @Override
    public String toString() {
        return userName;
    }
}
