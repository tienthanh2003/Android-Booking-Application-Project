package com.example.androidbookingapplicationproject;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.androidbookingapplicationproject.activities.UserListActivity;
import com.example.androidbookingapplicationproject.db.DatabaseHelper;
import com.example.androidbookingapplicationproject.models.ChatMessage;
import com.google.firebase.database.*;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "chat_channel";
    private static final String TAG = "MainActivity";

    private int userId;
    private String userName;
    private final String userRole = "Staff";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        userId = getIntent().getIntExtra("userId", -1);
        userName = getIntent().getStringExtra("userName");

        if (userId == -1) {
            Toast.makeText(this, "Không xác định được nhân viên!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        createNotificationChannel();
        checkPendingBookings();
        listenToNewMessages();

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        findViewById(R.id.cardManagePackages).setOnClickListener(v ->
                startActivity(new Intent(this, ManagePackagesActivity.class)));

        findViewById(R.id.cardManageFacilities).setOnClickListener(v ->
                startActivity(new Intent(this, ManageFacilitiesActivity.class)));

        findViewById(R.id.cardStaffNotifications).setOnClickListener(v -> {
            Intent intent = new Intent(this, StaffNotificationActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        findViewById(R.id.cardProfile).setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        findViewById(R.id.btnApprove).setOnClickListener(v ->
                startActivity(new Intent(this, BookingListActivity.class)));

        findViewById(R.id.cardSupport).setOnClickListener(v -> {
            Intent intent = new Intent(this, UserListActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("userName", userName);
            intent.putExtra("userRole", userRole);
            startActivity(intent);
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Kênh chat",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Thông báo tin nhắn mới");

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void listenToNewMessages() {
        String currentUserId = "staff_" + userId;
        DatabaseReference chatRef = FirebaseDatabase.getInstance()
                .getReference("Messages/conversations/" + currentUserId);

        chatRef.limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded: có tin nhắn mới");
                ChatMessage msg = snapshot.getValue(ChatMessage.class);

                if (msg != null && msg.senderId != null && !msg.senderId.equals(currentUserId)) {
                    if (!isMessageAlreadyNotified(msg)) {
                        showChatNotification(msg);
                        saveChatNotificationToSQLite(msg);
                    } else {
                        Log.d(TAG, "Tin nhắn đã được lưu trước đó, không thông báo lại");
                    }
                }
            }

            @Override public void onChildChanged(@NonNull DataSnapshot snapshot, String s) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot snapshot, String s) {}
            @Override public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Lỗi Firebase: " + error.getMessage());
            }
        });
    }

    private void showChatNotification(ChatMessage msg) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "Chưa được cấp quyền POST_NOTIFICATIONS");
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_chat)
                .setContentTitle("Tin nhắn từ " + msg.senderName)
                .setContentText(msg.message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat.from(this).notify((int) System.currentTimeMillis(), builder.build());
    }

    private boolean isMessageAlreadyNotified(ChatMessage msg) {
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            SQLiteDatabase db = dbHelper.openDatabase();

            Cursor cursor = db.rawQuery(
                    "SELECT 1 FROM Notifications WHERE UserId = ? AND Type = 'chat' AND Content = ? AND IsRead = 0",
                    new String[]{String.valueOf(userId), msg.message}
            );

            boolean exists = cursor.moveToFirst();
            cursor.close();
            db.close();
            return exists;
        } catch (Exception e) {
            Log.e(TAG, "Lỗi kiểm tra tin nhắn đã lưu: " + e.getMessage(), e);
            return true;
        }
    }

    private void saveChatNotificationToSQLite(ChatMessage msg) {
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            SQLiteDatabase db = dbHelper.openDatabase();

            ContentValues values = new ContentValues();
            values.put("UserId", userId);
            values.put("Title", "Tin nhắn mới từ " + msg.senderName);
            values.put("Content", msg.message);
            values.put("Type", "chat");
            values.put("IsRead", 0);
            values.putNull("RelatedId");

            db.insert("Notifications", null, values);
            db.close();
        } catch (Exception e) {
            Log.e(TAG, "Lỗi lưu thông báo chat vào SQLite: " + e.getMessage(), e);
        }
    }

    private void checkPendingBookings() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.openDatabase();

        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Bookings WHERE Status = 'Đã đặt'", null);

        if (cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            if (count > 0) {
                sendPendingNotification(count);

                String title = "📢 Có " + count + " đơn hàng đang chờ duyệt";
                String content = "Nhấn để kiểm tra và xử lý đơn hàng trong hệ thống.";

                Cursor staffCursor = db.rawQuery("SELECT UserId FROM User WHERE Role = 'Staff'", null);
                while (staffCursor.moveToNext()) {
                    int staffId = staffCursor.getInt(0);

                    Cursor existsCursor = db.rawQuery(
                            "SELECT 1 FROM Notifications WHERE UserId = ? AND Type = 'staff_pending' AND IsRead = 0",
                            new String[]{String.valueOf(staffId)}
                    );

                    boolean alreadyExists = existsCursor.moveToFirst();
                    existsCursor.close();

                    if (!alreadyExists) {
                        db.execSQL("INSERT INTO Notifications (UserId, Title, Content, Type, IsRead) VALUES (?, ?, ?, 'staff_pending', 0)",
                                new Object[]{staffId, title, content});
                    }
                }
                staffCursor.close();
            }
        }

        cursor.close();
        db.close();
    }

    private void sendPendingNotification(int count) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 123);
            return;
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, "pending_bookings_channel")
                        .setSmallIcon(R.drawable.ic_notifications)
                        .setContentTitle("📢 Có " + count + " đơn hàng đang chờ duyệt")
                        .setContentText("Nhấn để xem chi tiết trong hệ thống quản trị.")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        NotificationManagerCompat.from(this).notify(2001, builder.build());
    }
}
