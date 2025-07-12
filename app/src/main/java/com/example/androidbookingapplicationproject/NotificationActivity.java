package com.example.androidbookingapplicationproject;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidbookingapplicationproject.db.DatabaseHelper;

public class NotificationActivity extends AppCompatActivity {

    private int userId;
    private ListView lvNotifications;
    private SimpleCursorAdapter adapter;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        lvNotifications = findViewById(R.id.lvNotifications);
        userId = getIntent().getIntExtra("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy userId", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.openDatabase();

        loadNotifications();
    }

    private void loadNotifications() {
        Cursor cursor = db.rawQuery(
                "SELECT NotificationId AS _id, Title, Content, CreatedAt FROM Notifications WHERE UserId = ? ORDER BY CreatedAt DESC",
                new String[]{String.valueOf(userId)}
        );

        adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                cursor,
                new String[]{"Title", "Content"},
                new int[]{android.R.id.text1, android.R.id.text2},
                0
        );

        lvNotifications.setAdapter(adapter);

        lvNotifications.setOnItemClickListener((parent, view, position, id) -> {
            db.execSQL("UPDATE Notifications SET IsRead = 1 WHERE NotificationId = ?", new Object[]{id});
            Toast.makeText(this, "Đã đọc thông báo #" + id, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
