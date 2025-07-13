package com.example.androidbookingapplicationproject.models;

public class ChatMessage {
    public String senderId;     // "user_1", "staff_2"
    public String senderRole;   // "customer" hoặc "staff"
    public String senderName;   // Ví dụ: "Nguyen Van A"
    public String message;
    public String timestamp;

    public ChatMessage() {}

    public ChatMessage(String senderId, String senderRole, String senderName, String message, String timestamp) {
        this.senderId = senderId;
        this.senderRole = senderRole;
        this.senderName = senderName;
        this.message = message;
        this.timestamp = timestamp;
    }
}
