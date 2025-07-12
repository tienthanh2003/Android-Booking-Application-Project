package com.example.androidbookingapplicationproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private int userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        createNotificationChannel();
        checkPendingBookings();
        // Nút đăng xuất
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        userId = getIntent().getIntExtra("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "Không xác định được nhân viên!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        // Mở chức năng quản lý gói
        CardView cardManagePackages = findViewById(R.id.cardManagePackages);
        cardManagePackages.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ManagePackagesActivity.class);
            startActivity(intent);
        });

        CardView approveBooking = findViewById(R.id.btnApprove);
        approveBooking.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BookingListActivity.class);
            startActivity(intent);
        });
        CardView cardFacilities = findViewById(R.id.cardManageFacilities);
        cardFacilities.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ManageFacilitiesActivity.class);
            startActivity(intent);
        });
        CardView cardStaffNotifications = findViewById(R.id.cardStaffNotifications);
        cardStaffNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(this, StaffNotificationActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelId = "pending_bookings_channel";
            CharSequence name = "Thông báo duyệt đơn";
            String description = "Thông báo cho staff về đơn hàng chưa xử lý";
            int importance = android.app.NotificationManager.IMPORTANCE_HIGH;

            android.app.NotificationChannel channel = new android.app.NotificationChannel(channelId, name, importance);
            channel.setDescription(description);

            android.app.NotificationManager manager = getSystemService(android.app.NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
    private void sendPendingNotification(int count) {
        android.app.PendingIntent pendingIntent = android.app.PendingIntent.getActivity(
                this,
                0,
                new Intent(this, MainActivity.class),
                android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M ?
                        android.app.PendingIntent.FLAG_IMMUTABLE : 0
        );

        androidx.core.app.NotificationCompat.Builder builder =
                new androidx.core.app.NotificationCompat.Builder(this, "pending_bookings_channel")
                        .setSmallIcon(R.drawable.ic_notifications) // 👉 nhớ thêm icon này vào drawable
                        .setContentTitle("📢 Có " + count + " đơn hàng đang chờ duyệt")
                        .setContentText("Nhấn để xem chi tiết trong hệ thống quản trị.")
                        .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        androidx.core.app.NotificationManagerCompat notificationManager =
                androidx.core.app.NotificationManagerCompat.from(this);

        if (androidx.core.app.ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED
                || android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.TIRAMISU) {
            notificationManager.notify(2001, builder.build());
        } else {
            // Yêu cầu quyền nếu chưa cấp (Android 13+)
            requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 123);
        }
    }


    private void checkPendingBookings() {
        com.example.androidbookingapplicationproject.db.DatabaseHelper dbHelper =
                new com.example.androidbookingapplicationproject.db.DatabaseHelper(this);
        android.database.sqlite.SQLiteDatabase db = dbHelper.openDatabase();

        android.database.Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM Bookings WHERE Status = 'Đã đặt'",
                null
        );

        if (cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            if (count > 0) {
                // Gửi thông báo hệ thống
                sendPendingNotification(count);

                // Gửi vào bảng Notifications cho tất cả staff
                String title = "📢 Có " + count + " đơn hàng đang chờ duyệt";
                String content = "Nhấn để kiểm tra và xử lý đơn hàng trong hệ thống.";

                android.database.Cursor staffCursor = db.rawQuery(
                        "SELECT UserId FROM User WHERE Role = 'Staff'", null);

                while (staffCursor.moveToNext()) {
                    int staffId = staffCursor.getInt(0);

                    // Kiểm tra xem đã có thông báo chưa
                    android.database.Cursor existsCursor = db.rawQuery(
                            "SELECT 1 FROM Notifications " +
                                    "WHERE UserId = ? AND Type = 'staff_pending' AND IsRead = 0",
                            new String[]{String.valueOf(staffId)}
                    );

                    boolean alreadyExists = existsCursor.moveToFirst();
                    existsCursor.close();

                    if (!alreadyExists) {
                        // Chèn thông báo mới
                        db.execSQL("INSERT INTO Notifications (UserId, Title, Content, Type, IsRead) " +
                                        "VALUES (?, ?, ?, 'staff_pending', 0)",
                                new Object[]{staffId, title, content});
                    }
                }

                staffCursor.close();
            }
        }

        cursor.close();
        db.close();
    }



}
