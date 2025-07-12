package com.example.androidbookingapplicationproject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidbookingapplicationproject.db.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class StaffNotificationActivity extends AppCompatActivity {

    private ListView lvNotifications;
    private ArrayAdapter<String> adapter;
    private List<String> notificationList = new ArrayList<>();
    private int staffId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_notification);

        lvNotifications = findViewById(R.id.lvStaffNotifications);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notificationList);
        lvNotifications.setAdapter(adapter);

        staffId = getIntent().getIntExtra("userId", -1);
        if (staffId != -1) {
            loadNotifications(staffId);
        }
    }

    private void loadNotifications(int userId) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.openDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT Title, Content FROM Notifications WHERE UserId = ? ORDER BY NotificationId DESC",
                new String[]{String.valueOf(userId)}
        );

        while (cursor.moveToNext()) {
            String title = cursor.getString(0);
            String content = cursor.getString(1);
            notificationList.add("🔔 " + title + "\n" + content);
        }

        cursor.close();
        db.close();
        adapter.notifyDataSetChanged();
    }
}
