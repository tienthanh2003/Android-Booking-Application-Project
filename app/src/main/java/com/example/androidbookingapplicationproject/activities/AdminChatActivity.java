package com.example.androidbookingapplicationproject.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidbookingapplicationproject.R;
import com.example.androidbookingapplicationproject.adapters.ChatAdapter;
import com.example.androidbookingapplicationproject.models.ChatMessage;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class AdminChatActivity extends AppCompatActivity {

    private RecyclerView recyclerChat;
    private EditText etMessage;
    private Button btnSend;

    private final List<ChatMessage> chatList = new ArrayList<>();
    private ChatAdapter adapter;
    private DatabaseReference chatRef;

    private String selectedUserId = null;
    private String adminName = "Admin";
    private String senderId = "unknown";
    private String senderRole = "staff";

    private static final String TAG = "AdminChatActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerChat = findViewById(R.id.recyclerChat);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        recyclerChat.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatAdapter(chatList);
        recyclerChat.setAdapter(adapter);

        // Lấy thông tin từ SharedPreferences
        try {
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            senderId = prefs.getString("userId", "unknown");
            senderRole = prefs.getString("role", "staff").toLowerCase();
            adminName = prefs.getString("userName", "Admin");

            Log.d(TAG, "Sender Info - ID: " + senderId + ", Role: " + senderRole + ", Name: " + adminName);
        } catch (Exception e) {
            Log.e(TAG, "Lỗi lấy thông tin SharedPreferences", e);
        }

        // Lấy thông tin người dùng từ Intent
        int userId = getIntent().getIntExtra("userId", -1);
        String userName = getIntent().getStringExtra("userName");

        Log.d(TAG, "Received from intent - userId: " + userId + ", userName: " + userName);

        if (userId == -1 || userName == null || userName.trim().isEmpty()) {
            Toast.makeText(this, "Không có người dùng được chọn", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "userId hoặc userName không hợp lệ");
            finish();
            return;
        }

        selectedUserId = String.valueOf(userId);

        loadChatWithUser("user_" + selectedUserId);

        btnSend.setOnClickListener(v -> {
            String msg = etMessage.getText().toString().trim();
            if (!TextUtils.isEmpty(msg)) {
                String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                ChatMessage adminMsg = new ChatMessage(
                        senderId,
                        senderRole,
                        adminName,
                        msg,
                        time
                );

                FirebaseDatabase.getInstance()
                        .getReference("Messages/conversations")
                        .child("user_" + selectedUserId)
                        .push()
                        .setValue(adminMsg)
                        .addOnSuccessListener(unused -> Log.d(TAG, "Tin nhắn đã gửi"))
                        .addOnFailureListener(e -> Log.e(TAG, "Gửi tin nhắn thất bại", e));

                etMessage.setText("");
            } else {
                Log.w(TAG, "Tin nhắn rỗng không được gửi");
            }
        });
    }

    private void loadChatWithUser(String userKey) {
        chatList.clear();
        adapter.notifyDataSetChanged();

        chatRef = FirebaseDatabase.getInstance()
                .getReference("Messages/conversations")
                .child(userKey);

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    ChatMessage msg = snap.getValue(ChatMessage.class);
                    if (msg != null) {
                        chatList.add(msg);
                    } else {
                        Log.w(TAG, "Tin nhắn null bị bỏ qua");
                    }
                }
                adapter.notifyDataSetChanged();
                if (!chatList.isEmpty()) {
                    recyclerChat.scrollToPosition(chatList.size() - 1);
                }
                Log.d(TAG, "Số lượng tin nhắn tải về: " + chatList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminChatActivity.this, "Lỗi đọc tin nhắn", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Firebase read error: " + error.getMessage(), error.toException());
            }
        });
    }
}
