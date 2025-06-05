package com.example.androidbookingapplicationproject;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Xử lý Spinner giới tính
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

        // Xử lý DatePickerDialog cho EditText ngày sinh
        EditText etDob = findViewById(R.id.etDob);
        if (etDob != null) {
            etDob.setOnClickListener(v -> showDatePickerDialog());
        }

        // Xử lý nút đăng ký
        Button btnSignUp = findViewById(R.id.btnSignUp);
        if (btnSignUp != null) {
            btnSignUp.setOnClickListener(view -> {
                Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            });
        }
    }

    private void showDatePickerDialog() {
        // Lấy ngày hiện tại làm giá trị mặc định
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Tạo DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Định dạng ngày tháng năm: dd/MM/yyyy
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    EditText etDob = findViewById(R.id.etDob);
                    etDob.setText(selectedDate);
                },
                year, month, day
        );

        // Thiết lập ngày tối đa là ngày hiện tại (không cho chọn ngày sinh trong tương lai)
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        datePickerDialog.show();
    }
}