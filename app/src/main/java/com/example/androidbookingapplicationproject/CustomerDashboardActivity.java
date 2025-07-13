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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.androidbookingapplicationproject.db.DatabaseHelper;
import com.example.androidbookingapplicationproject.models.ChatMessage;
import com.google.firebase.database.*;

public class CustomerDashboardActivity extends AppCompatActivity {

    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 100;
    private Button btnLogout;
    private TextView tvCustomerName;
    private int userId;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_dashboard);

        tvCustomerName = findViewById(R.id.tvCustomerName);
        btnLogout = findViewById(R.id.btnLogout);

        userId = getIntent().getIntExtra("userId", -1);
        email = getIntent().getStringExtra("email");
        String name = getIntent().getStringExtra("userName");

        if (userId == -1 || email == null) {
            Toast.makeText(this, "Không xác định được người dùng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (name != null) {
            tvCustomerName.setText(name);
        }

        createNotificationChannel();
        checkNotificationPermissionAndNotify();

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        findViewById(R.id.cardNotification).setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificationActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        CardView cardProfile = findViewById(R.id.cardProfile);
        cardProfile.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerDashboardActivity.this, ProfileActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        CardView cardChat = findViewById(R.id.cardChat);
        cardChat.setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.androidbookingapplicationproject.activities.ChatActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("userName", tvCustomerName.getText().toString());
            intent.putExtra("userRole", "customer");
            startActivity(intent);
        });

        findViewById(R.id.cardMap).setOnClickListener(v ->
                startActivity(new Intent(this, MapActivity.class)));

        findViewById(R.id.cardCart).setOnClickListener(v -> {
            Intent intent = new Intent(this, CartActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        findViewById(R.id.cardSelectPackage).setOnClickListener(v -> {
            Intent intent = new Intent(this, SelectPackageActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        CardView cardHistory = findViewById(R.id.cardHistory);
        cardHistory.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookingHistoryActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

    }

    private void checkNotificationPermissionAndNotify() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE);
            } else {
                checkTodayBookingAndNotify();
                checkUnreadBookingNotification();
                checkUnpaidCartAndNotify();
                listenToNewMessages(); // ✅ Thêm lắng nghe tin nhắn
            }
        } else {
            checkTodayBookingAndNotify();
            checkUnreadBookingNotification();
            checkUnpaidCartAndNotify();
            listenToNewMessages(); // ✅ Android < 13 vẫn nghe tin nhắn
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkTodayBookingAndNotify();
                checkUnreadBookingNotification();
                checkUnpaidCartAndNotify();
                listenToNewMessages(); // ✅ Khi được cấp quyền
            } else {
                Toast.makeText(this, "Bạn đã từ chối quyền thông báo!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkUnpaidCartAndNotify() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.openDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM Cart WHERE UserId = ?",
                new String[]{String.valueOf(userId)}
        );

        if (cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            if (count > 0) {
                sendCartNotification(count);
            }
        }

        cursor.close();
        db.close();
    }

    private void checkUnreadBookingNotification() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.openDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT Title, Content FROM Notifications WHERE UserId = ? AND Type = 'booking' AND IsRead = 0",
                new String[]{String.valueOf(userId)}
        );

        if (cursor.moveToFirst()) {
            String title = cursor.getString(0);
            String content = cursor.getString(1);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "booking_channel")
                    .setSmallIcon(R.drawable.ic_notifications)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true);

            try {
                NotificationManagerCompat.from(this).notify(1002, builder.build());
            } catch (SecurityException e) {
                e.printStackTrace();
            }

            db.execSQL("UPDATE Notifications SET IsRead = 1 WHERE UserId = ? AND Type = 'booking'",
                    new Object[]{userId});
        }

        cursor.close();
        db.close();
    }

    private void checkTodayBookingAndNotify() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.openDatabase();

        String todayStr = new java.text.SimpleDateFormat("yyyy-M-d", java.util.Locale.getDefault()).format(new java.util.Date());

        Cursor cursor = db.rawQuery(
                "SELECT BookingId, BookingDate, StartTime FROM Bookings " +
                        "WHERE UserId = ? AND BookingDate LIKE ? AND Status = 'Đã duyệt'",
                new String[]{String.valueOf(userId), "%" + todayStr + "%"}
        );

        if (cursor.moveToFirst()) {
            int bookingId = cursor.getInt(0);
            String date = cursor.getString(1);
            String time = cursor.getString(2);

            String title = "⏰ Nhắc lịch đặt hôm nay";
            String content = "Bạn có lịch đặt vào " + time + " hôm nay (" + date + ").";

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "booking_channel")
                    .setSmallIcon(R.drawable.ic_notifications)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true);

            try {
                NotificationManagerCompat.from(this).notify(1003, builder.build());
            } catch (SecurityException e) {
                e.printStackTrace();
            }

            Cursor checkCursor = db.rawQuery(
                    "SELECT 1 FROM Notifications WHERE UserId = ? AND Type = 'reminder' AND RelatedId = ?",
                    new String[]{String.valueOf(userId), String.valueOf(bookingId)}
            );
            boolean alreadyNotified = checkCursor.moveToFirst();
            checkCursor.close();

            if (!alreadyNotified) {
                db.execSQL("INSERT INTO Notifications (UserId, Title, Content, Type, IsRead, RelatedId) " +
                                "VALUES (?, ?, ?, 'reminder', 0, ?)",
                        new Object[]{userId, title, content, bookingId});
            }
        }

        cursor.close();
        db.close();
    }

    private void sendCartNotification(int count) {
        String title = "Bạn còn " + count + " đơn hàng chưa thanh toán";
        String content = "Hãy hoàn tất thanh toán để giữ lịch đặt của bạn.";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "booking_channel")
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        try {
            NotificationManagerCompat.from(this).notify(1001, builder.build());
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.openDatabase();
        db.execSQL(
                "INSERT INTO Notifications (UserId, Title, Content, Type, IsRead) VALUES (?, ?, ?, ?, 0)",
                new Object[]{userId, title, content, "cart"}
        );
        db.close();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "booking_channel", "Booking Channel", NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Thông báo đơn đặt chưa thanh toán");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    // ✅ Thông báo tin nhắn mới khi login
    private void listenToNewMessages() {
        String currentUserId = "user_" + userId;
        DatabaseReference chatRef = FirebaseDatabase.getInstance()
                .getReference("Messages/conversations/" + currentUserId);

        chatRef.limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                ChatMessage msg = snapshot.getValue(ChatMessage.class);

                if (msg != null && msg.senderId != null && !msg.senderId.equals(currentUserId)) {
                    showChatNotification(msg);
                    saveChatNotificationToSQLite(msg);
                }
            }

            @Override public void onChildChanged(@NonNull DataSnapshot snapshot, String s) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot snapshot, String s) {}
            @Override public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Dashboard", "Lỗi Firebase: " + error.getMessage());
            }
        });
    }

    private void showChatNotification(ChatMessage msg) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "booking_channel")
                .setSmallIcon(R.drawable.ic_chat)
                .setContentTitle("Tin nhắn từ " + msg.senderName)
                .setContentText(msg.message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        NotificationManagerCompat.from(this).notify((int) System.currentTimeMillis(), builder.build());
    }

    private void saveChatNotificationToSQLite(ChatMessage msg) {
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            SQLiteDatabase db = dbHelper.openDatabase();

            Cursor cursor = db.rawQuery(
                    "SELECT 1 FROM Notifications WHERE UserId = ? AND Type = 'chat' AND Content = ?",
                    new String[]{String.valueOf(userId), msg.message}
            );

            if (!cursor.moveToFirst()) {
                ContentValues values = new ContentValues();
                values.put("UserId", userId);
                values.put("Title", "Tin nhắn mới từ " + msg.senderName);
                values.put("Content", msg.message);
                values.put("Type", "chat");
                values.put("IsRead", 0);
                values.putNull("RelatedId");

                db.insert("Notifications", null, values);
            }

            cursor.close();
            db.close();
        } catch (Exception e) {
            Log.e("Dashboard", "Lỗi lưu thông báo chat vào SQLite: " + e.getMessage(), e);
        }
    }
}
