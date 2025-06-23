package com.example.androidbookingapplicationproject;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidbookingapplicationproject.db.DatabaseHelper;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etFullname, etPassword, etPhone;
    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.etSignupEmail);
        etFullname = findViewById(R.id.etSignupFullname);
        etPassword = findViewById(R.id.etSignupPassword);
        etPhone = findViewById(R.id.etSignupUsername);
        btnSignUp = findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(v -> handleSignUp());
    }

    private void handleSignUp() {
        String email = etEmail.getText().toString().trim();
        String name = etFullname.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(name)
                || TextUtils.isEmpty(password) || TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            dbHelper.createDatabase();
            SQLiteDatabase db = dbHelper.openDatabase();

            Cursor check = db.rawQuery("SELECT 1 FROM Users WHERE Email = ?", new String[]{email});
            if (check.moveToFirst()) {
                Toast.makeText(this, "Email đã tồn tại!", Toast.LENGTH_SHORT).show();
                check.close();
                db.close();
                return;
            }
            check.close();

            ContentValues values = new ContentValues();
            values.put("Email", email);
            values.put("Password", password);
            values.put("Name", name);
            values.put("Phone", phone);
            values.put("Role", "user");

            long rowId = db.insert("Users", null, values);
            db.close();

            if (rowId != -1) {
                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Đăng ký thất bại! Không thể thêm vào DB", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi đăng ký: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
