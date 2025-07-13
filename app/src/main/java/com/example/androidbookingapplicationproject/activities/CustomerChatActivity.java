package com.example.androidbookingapplicationproject.activities;

import android.content.SharedPreferences;
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
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class CustomerChatActivity extends AppCompatActivity {

    private RecyclerView recyclerChat;
    private EditText etMessage;
    private Button btnSend;

    private List<ChatMessage> chatList = new ArrayList<>();
    private ChatAdapter adapter;
    private DatabaseReference chatRef;

    private int userId = -1;
    private String userName = "Unknown";
    private String userRole = "customer"; // ✅ luôn là customer

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

        // ✅ Lấy thông tin từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);
        userName = prefs.getString("userName", "Unknown");
        userRole = prefs.getString("userRole", "customer").toLowerCase();

        if (userId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // ✅ Dẫn đến conversation riêng
        chatRef = FirebaseDatabase.getInstance()
                .getReference("Messages")
                .child("conversations")
                .child("user_" + userId);

        // 🔁 Load tin nhắn realtime
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    ChatMessage msg = snap.getValue(ChatMessage.class);
                    if (msg != null) chatList.add(msg);
                }
                adapter.notifyDataSetChanged();
                if (!chatList.isEmpty()) {
                    recyclerChat.scrollToPosition(chatList.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("CustomerChat", "Firebase error: " + error.getMessage());
            }
        });

        // ✅ Gửi tin nhắn
        btnSend.setOnClickListener(v -> {
            String msg = etMessage.getText().toString().trim();
            if (!TextUtils.isEmpty(msg)) {
                String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

                ChatMessage userMsg = new ChatMessage(
                        "user_" + userId,
                        userRole,
                        userName,
                        msg,
                        time
                );

                chatRef.push().setValue(userMsg);
                etMessage.setText("");
            }
        });
    }
}
