package com.example.androidbookingapplicationproject;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidbookingapplicationproject.db.DatabaseHelper;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etFullName, etPassword, etPhone, etDob;
    private Spinner spinnerGender;
    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Ánh xạ view
        etEmail = findViewById(R.id.etSignupEmail);
        etFullName = findViewById(R.id.etSignupFullname);
        etPassword = findViewById(R.id.etSignupPassword);
        etPhone = findViewById(R.id.etSignupPhone);
        etDob = findViewById(R.id.etDob);
        spinnerGender = findViewById(R.id.spinnerGender);
        btnSignUp = findViewById(R.id.btnSignUp);

        // Spinner giới tính
        String[] genderArray = {"Nam", "Nữ", "Khác"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, genderArray
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);

        // Ngày sinh
        etDob.setOnClickListener(v -> showDatePickerDialog());

        // Sự kiện đăng ký
        btnSignUp.setOnClickListener(view -> handleRegister());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    etDob.setText(selectedDate);
                },
                year, month, day
        );
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void handleRegister() {
        String email = etEmail.getText().toString().trim();
        String fullname = etFullName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String dob = etDob.getText().toString().trim();
        String gender = spinnerGender.getSelectedItem().toString();

        // Kiểm tra dữ liệu
        // Kiểm tra dữ liệu
        if (email.isEmpty() || fullname.isEmpty() || password.isEmpty()
                || phone.isEmpty() || dob.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }


        if (password.length() < 6 || !password.matches(".*[A-Za-z].*") || !password.matches(".*\\d.*")) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự và bao gồm cả chữ và số", Toast.LENGTH_SHORT).show();
            return;
        }


        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            dbHelper.createDatabase();
            db = dbHelper.openDatabase();

            // Kiểm tra email đã tồn tại
            cursor = db.rawQuery("SELECT * FROM User WHERE Email = ?", new String[]{email});
            if (cursor.moveToFirst()) {
                Toast.makeText(this, "Email đã tồn tại!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Thêm người dùng
            String sql = "INSERT INTO User (Email, Name, Password, Phone, Dob, Gender, Role) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            db.execSQL(sql, new Object[]{email, fullname, password, phone, dob, gender, "customer"});

            Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi đăng ký: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
    }
}
