package com.example.androidbookingapplicationproject;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidbookingapplicationproject.db.DatabaseHelper;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    EditText etName, etEmail, etPhone, etPassword, etGender, etDob, etRole;
    Button btnSave;
    TextView tvUsername;
    SQLiteDatabase db;
    int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Lấy userId từ SharedPreferences

        currentUserId = getIntent().getIntExtra("userId", -1);

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
        etGender = findViewById(R.id.etGender);
        etDob = findViewById(R.id.etDob);
        etRole = findViewById(R.id.etRole);
        btnSave = findViewById(R.id.btnSave);
        tvUsername = findViewById(R.id.tvUsername);

        etRole.setEnabled(false);

        // Mở database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        try {
            dbHelper.createDatabase();
            db = dbHelper.openDatabase();
        } catch (IOException e) {
            throw new RuntimeException("Không thể mở database", e);
        }

        // Load dữ liệu người dùng
        loadUserInfo();

        // Sự kiện lưu thay đổi
        btnSave.setOnClickListener(v -> updateUserInfo());
    }

    private void loadUserInfo() {
        Cursor cursor = db.rawQuery("SELECT * FROM User WHERE UserId = ?", new String[]{String.valueOf(currentUserId)});
        if (cursor != null && cursor.moveToFirst()) {
            etName.setText(cursor.getString(cursor.getColumnIndexOrThrow("Name")));
            etEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow("Email")));
            etPhone.setText(cursor.getString(cursor.getColumnIndexOrThrow("Phone")));
            etGender.setText(cursor.getString(cursor.getColumnIndexOrThrow("Gender")));
            etDob.setText(cursor.getString(cursor.getColumnIndexOrThrow("Dob")));
            etRole.setText(cursor.getString(cursor.getColumnIndexOrThrow("Role")));
            tvUsername.setText(cursor.getString(cursor.getColumnIndexOrThrow("Name")));
            etPassword.setText("");
            cursor.close();
        }
    }

    private void updateUserInfo() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String gender = etGender.getText().toString().trim();
        String dob = etDob.getText().toString().trim();
        String role = etRole.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || gender.isEmpty() || dob.isEmpty() || role.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put("Name", name);
        values.put("Phone", phone);
        values.put("Gender", gender);
        values.put("Dob", dob);
        values.put("Role", role);

        if (!password.isEmpty()) {
            values.put("Password", password);
        }

        int result = db.update("User", values, "UserId = ?", new String[]{String.valueOf(currentUserId)});
        if (result > 0) {
            Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            etPassword.setText("");
            tvUsername.setText(name);
        } else {
            Toast.makeText(this, "Không thể cập nhật", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}
