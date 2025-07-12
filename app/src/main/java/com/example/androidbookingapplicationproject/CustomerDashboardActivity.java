package com.example.androidbookingapplicationproject;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import com.example.androidbookingapplicationproject.db.DatabaseHelper;

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

        // Nhận thông tin người dùng từ LoginActivity
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

        createNotificationChannel(); // Tạo kênh thông báo
        checkNotificationPermissionAndNotify(); // Kiểm tra quyền trước khi gửi

        // Logout
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
    }

    // Gửi thông báo nếu chưa thanh toán
    private void checkNotificationPermissionAndNotify() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Yêu cầu quyền
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE);
            } else {
                checkUnpaidCartAndNotify();
            }
        } else {
            checkUnpaidCartAndNotify();
        }
    }

    // Gửi thông báo nếu có giỏ hàng chưa thanh toán
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

    // Gửi thông báo bằng NotificationManagerCompat
    private void sendCartNotification(int count) {
        String title = "Bạn còn " + count + " đơn hàng chưa thanh toán";
        String content = "Hãy hoàn tất thanh toán để giữ lịch đặt của bạn.";

        // ✅ Gửi notification
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
            Toast.makeText(this, "Không thể hiển thị thông báo. Hãy cấp quyền!", Toast.LENGTH_SHORT).show();
        }

        // ✅ Lưu thông báo vào database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.openDatabase();
        db.execSQL(
                "INSERT INTO Notifications (UserId, Title, Content, Type, IsRead) VALUES (?, ?, ?, ?, 0)",
                new Object[]{userId, title, content, "cart"}
        );
        db.close();
    }


    // Kết quả yêu cầu quyền từ người dùng
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkUnpaidCartAndNotify();
            } else {
                Toast.makeText(this, "Bạn đã từ chối quyền thông báo!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Tạo kênh thông báo cho Android O+
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "Booking Channel";
            String description = "Thông báo đơn đặt chưa thanh toán";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel("booking_channel", name, importance);
            channel.setDescription(description);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
