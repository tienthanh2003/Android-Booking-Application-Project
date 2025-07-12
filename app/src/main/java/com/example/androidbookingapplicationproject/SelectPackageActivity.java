package com.example.androidbookingapplicationproject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidbookingapplicationproject.db.DatabaseHelper;
import java.util.*;

public class SelectPackageActivity extends AppCompatActivity {

    private Spinner spinnerPackages;
    private Button btnChoosePackage;
    private EditText etBookingDate, etStartTime, etEndTime;
    private ArrayList<Integer> packageIds = new ArrayList<>();
    private int currentUserId = 1; // Giá trị mặc định cho demo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_package);

        spinnerPackages = findViewById(R.id.spinnerPackages);
        btnChoosePackage = findViewById(R.id.btnChoosePackage);
        etBookingDate = findViewById(R.id.etBookingDate);
        etStartTime = findViewById(R.id.etStartTime);
        etEndTime = findViewById(R.id.etEndTime);

        // ✅ Lấy userId từ Intent
        currentUserId = getIntent().getIntExtra("userId", -1);
        if (currentUserId == -1) {
            Toast.makeText(this, "Không xác định được người dùng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadPackages();
        setupDateAndTimePickers();

        btnChoosePackage.setOnClickListener(v -> {
            int selectedIndex = spinnerPackages.getSelectedItemPosition();
            if (selectedIndex >= 0) {
                int packageId = packageIds.get(selectedIndex);
                showFacilityDialog(packageId);
            }
        });
    }


    private void setupDateAndTimePickers() {
        Calendar calendar = Calendar.getInstance();

        etBookingDate.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                etBookingDate.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        etStartTime.setOnClickListener(v -> {
            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                etStartTime.setText(String.format("%02d:%02d", hourOfDay, minute));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        });

        etEndTime.setOnClickListener(v -> {
            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                etEndTime.setText(String.format("%02d:%02d", hourOfDay, minute));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        });
    }

    private void loadPackages() {
        ArrayList<String> packageList = new ArrayList<>();
        packageIds.clear();

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.openDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Packages", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("PackageId"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("Name"));
            int capacity = cursor.getInt(cursor.getColumnIndexOrThrow("Capacity"));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow("Price"));
            packageList.add(name + " - " + capacity + " chỗ - " + price);
            packageIds.add(id);
        }

        cursor.close();
        db.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, packageList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPackages.setAdapter(adapter);
    }

    private void showFacilityDialog(int packageId) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.openDatabase();

        ArrayList<Integer> facilityIds = new ArrayList<>();
        ArrayList<String> facilityNames = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM Facilities", null);
        while (cursor.moveToNext()) {
            facilityIds.add(cursor.getInt(cursor.getColumnIndexOrThrow("FacilityId")));
            facilityNames.add(cursor.getString(cursor.getColumnIndexOrThrow("Name")));
        }
        cursor.close();
        db.close();

        boolean[] checkedItems = new boolean[facilityNames.size()];

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn tiện ích kèm theo");
        builder.setMultiChoiceItems(facilityNames.toArray(new String[0]), checkedItems, (dialog, which, isChecked) -> checkedItems[which] = isChecked);

        builder.setPositiveButton("Thêm vào giỏ", (dialog, which) -> {
            String bookingDate = etBookingDate.getText().toString();
            String startTime = etStartTime.getText().toString();
            String endTime = etEndTime.getText().toString();

            if (bookingDate.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn đầy đủ ngày và thời gian!", Toast.LENGTH_SHORT).show();
                return;
            }

            long cartId = insertCart(packageId, bookingDate, startTime, endTime);
            for (int i = 0; i < checkedItems.length; i++) {
                if (checkedItems[i]) {
                    insertCartFacility(cartId, facilityIds.get(i));
                }
            }
            Toast.makeText(this, "Đã thêm vào giỏ", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private long insertCart(int packageId, String bookingDate, String startTime, String endTime) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.openDatabase();
        db.execSQL("INSERT INTO Cart (UserId, PackageId, BookingDate, StartTime, EndTime, Quantity) VALUES (?, ?, ?, ?, ?, 1)",
                new Object[]{currentUserId, packageId, bookingDate, startTime, endTime});
        Cursor cursor = db.rawQuery("SELECT last_insert_rowid()", null);
        cursor.moveToFirst();
        long cartId = cursor.getLong(0);
        cursor.close();
        db.close();
        return cartId;
    }

    private void insertCartFacility(long cartId, int facilityId) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.openDatabase();
        db.execSQL("INSERT INTO CartFacilities (CartId, FacilityId, Quantity) VALUES (?, ?, 1)", new Object[]{cartId, facilityId});
        db.close();
    }
}
