package com.example.androidbookingapplicationproject;

import android.content.Intent;
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

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Nếu đã đăng nhập trước đó, vào thẳng MainActivity
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        if (prefs.getInt("userId", -1) != -1) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        btnLogin.setOnClickListener(view -> handleLogin());

        tvRegister.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void handleLogin() {
        String email = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ email và mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            dbHelper.createDatabase();
            SQLiteDatabase db = dbHelper.openDatabase();

            Cursor cursor = db.rawQuery(
                    "SELECT * FROM Users WHERE Email = ? AND Password = ?",
                    new String[]{email, password}
            );

            if (cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("Name"));
                String role = cursor.getString(cursor.getColumnIndexOrThrow("Role"));
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow("UserId"));

                // Lưu vào SharedPreferences
                getSharedPreferences("UserPrefs", MODE_PRIVATE)
                        .edit()
                        .putInt("userId", userId)
                        .apply();

                Toast.makeText(this, "Chào " + name + " (" + role + ")", Toast.LENGTH_LONG).show();

                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show();
            }

            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi đăng nhập: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
