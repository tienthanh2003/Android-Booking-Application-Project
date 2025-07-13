package com.example.androidbookingapplicationproject.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidbookingapplicationproject.R;
import com.example.androidbookingapplicationproject.adapters.ChatAdapter;
import com.example.androidbookingapplicationproject.models.ChatMessage;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";

    private List<ChatMessage> chatList = new ArrayList<>();
    private ChatAdapter adapter;
    private DatabaseReference chatRef;

    private int userId = -1;
    private String userName = "Unknown";
    private String userRole = "customer"; // Default role

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ Khởi tạo Firebase
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_chat);

        // Khởi tạo view
        RecyclerView recyclerChat = findViewById(R.id.recyclerChat);
        EditText etMessage = findViewById(R.id.etMessage);
        Button btnSend = findViewById(R.id.btnSend);

        // Setup RecyclerView
        recyclerChat.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatAdapter(chatList);
        recyclerChat.setAdapter(adapter);

        // 🔐 Lấy dữ liệu từ Intent
        userId = getIntent().getIntExtra("userId", -1);
        userName = getIntent().getStringExtra("userName");
        userRole = getIntent().getStringExtra("userRole");

        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (userName == null || userName.trim().isEmpty()) {
            userName = "Người dùng";
        }

        if (userRole == null || userRole.trim().isEmpty()) {
            userRole = "customer";
        } else {
            userRole = userRole.toLowerCase(Locale.ROOT);
        }

        Log.d(TAG, "User info - ID: " + userId + ", Name: " + userName + ", Role: " + userRole);

        // Firebase path
        chatRef = FirebaseDatabase.getInstance()
                .getReference("Messages/conversations/user_" + userId);

        // Lắng nghe tin nhắn realtime
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    ChatMessage msg = snap.getValue(ChatMessage.class);
                    if (msg != null) {
                        chatList.add(msg);
                    }
                }
                adapter.notifyDataSetChanged();
                if (!chatList.isEmpty()) {
                    recyclerChat.scrollToPosition(chatList.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Lỗi khi tải tin nhắn: " + error.getMessage());
            }
        });

        // Gửi tin nhắn
        btnSend.setOnClickListener(v -> {
            String msg = etMessage.getText().toString().trim();
            if (!TextUtils.isEmpty(msg)) {
                String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

                ChatMessage chatMessage = new ChatMessage(
                        "user_" + userId,
                        userRole,
                        userName,
                        msg,
                        time
                );

                chatRef.push().setValue(chatMessage);
                etMessage.setText("");
            } else {
                Toast.makeText(this, "Tin nhắn không được để trống", Toast.LENGTH_SHORT).show();
            }
        });
    }
}