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
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.androidbookingapplicationproject.activities.UserListActivity;
import com.example.androidbookingapplicationproject.db.DatabaseHelper;

public class MainActivity extends AppCompatActivity {

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
        TextView tvStaffName = findViewById(R.id.tvStaffName);
        tvStaffName.setText(userName != null ? userName : "Nhân viên");

        createNotificationChannel();
        checkPendingBookings();

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
                    "pending_bookings_channel",
                    "Thông báo đơn hàng",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Thông báo các đơn hàng chờ xử lý");

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
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
