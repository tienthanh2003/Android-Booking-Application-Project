package com.example.androidbookingapplicationproject;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidbookingapplicationproject.db.DatabaseHelper;

import java.io.File;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    EditText etName, etEmail, etPhone, etPassword;
    Button btnSave;
    ImageView imgAvatar;
    TextView tvUsername;
    SQLiteDatabase db;
    int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Lấy userId từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentUserId = prefs.getInt("userId", -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ánh xạ view
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        btnSave = findViewById(R.id.btnSave);
        imgAvatar = findViewById(R.id.imgAvatar);
        tvUsername = findViewById(R.id.tvUsername);

        // Mở database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        try {
            dbHelper.createDatabase();
            db = dbHelper.openDatabase();
        } catch (IOException e) {
            throw new RuntimeException("Database open error", e);
        }

        loadUserInfo();

        btnSave.setOnClickListener(v -> updateUserInfo());
    }

    private void loadUserInfo() {
        Cursor cursor = db.rawQuery("SELECT * FROM Users WHERE UserId = ?", new String[]{String.valueOf(currentUserId)});
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("Name"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("Email"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("Phone"));
            String avatarPath = cursor.getColumnIndex("Avatar") != -1
                    ? cursor.getString(cursor.getColumnIndexOrThrow("Avatar"))
                    : null;

            etName.setText(name);
            etEmail.setText(email);
            etPhone.setText(phone);
            tvUsername.setText(name);

            // Hiển thị avatar nếu có
            if (avatarPath != null && !avatarPath.isEmpty()) {
                File avatarFile = new File(avatarPath);
                if (avatarFile.exists()) {
                    imgAvatar.setImageBitmap(BitmapFactory.decodeFile(avatarPath));
                }
            }

            cursor.close();
        }
    }

    private void updateUserInfo() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ họ tên và số điện thoại", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put("Name", name);
        values.put("Phone", phone);
        if (!password.isEmpty()) {
            values.put("Password", password);
        }

        int result = db.update("Users", values, "UserId = ?", new String[]{String.valueOf(currentUserId)});
        if (result > 0) {
            Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            etPassword.setText("");
        } else {
            Toast.makeText(this, "Không thể cập nhật", Toast.LENGTH_SHORT).show();
        }
    }
}
