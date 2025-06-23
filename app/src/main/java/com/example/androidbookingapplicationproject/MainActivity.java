package com.example.androidbookingapplicationproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.androidbookingapplicationproject.db.DatabaseHelper;

public class MainActivity extends AppCompatActivity {

    private int currentUserId;
    private TextView tvStaffName;
    private CardView cardProfile;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentUserId = prefs.getInt("userId", -1);

        if (currentUserId == -1) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        tvStaffName = findViewById(R.id.tvStaffName);
        cardProfile = findViewById(R.id.cardProfile);
        btnLogout = findViewById(R.id.btnLogout);

        showUserName(currentUserId);

        cardProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.putExtra("userId", currentUserId);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            prefs.edit().clear().apply();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void showUserName(int userId) {
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            dbHelper.createDatabase();
            SQLiteDatabase db = dbHelper.openDatabase();

            Cursor cursor = db.rawQuery("SELECT Name FROM Users WHERE UserId = ?", new String[]{String.valueOf(userId)});
            if (cursor.moveToFirst()) {
                String name = cursor.getString(0);
                tvStaffName.setText(name);
            } else {
                tvStaffName.setText("Người dùng");
            }

            cursor.close();
            db.close();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi tải tên người dùng", Toast.LENGTH_SHORT).show();
        }
    }
}
