package com.example.androidbookingapplicationproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);  // chắc chắn file này tồn tại

        // Set Spinner cho giới tính nếu có
        Spinner spinnerGender = findViewById(R.id.spinnerGender);
        if (spinnerGender != null) {
            String[] genderArray = {"Nam", "Nữ", "Khác"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    genderArray
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerGender.setAdapter(adapter);
        }

        // Tìm nút đăng ký
        Button btnSignUp = findViewById(R.id.btnSignUp);
        if (btnSignUp != null) {
            btnSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Xử lý đăng ký, ví dụ hiển thị Toast
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                    // Ví dụ chuyển về LoginActivity sau đăng ký thành công
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }
}
