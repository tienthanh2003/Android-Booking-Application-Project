package com.example.androidbookingapplicationproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidbookingapplicationproject.db.DatabaseHelper;

import java.util.ArrayList;

public class ManageFacilitiesActivity extends AppCompatActivity {

    private ListView listFacilities;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> facilityList;
    private ArrayList<Integer> facilityIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_facilities);

        listFacilities = findViewById(R.id.listFacilities);
        Button btnAddFacility = findViewById(R.id.btnAddFacility);
        Button btnBack = findViewById(R.id.btnBack);

        loadFacilities();

        btnAddFacility.setOnClickListener(v -> showAddDialog());
        btnBack.setOnClickListener(v -> finish());

        listFacilities.setOnItemClickListener((parent, view, position, id) -> {
            int facilityId = facilityIds.get(position);
            showEditDeleteDialog(facilityId);
        });
    }

    private void loadFacilities() {
        facilityList = new ArrayList<>();
        facilityIds = new ArrayList<>();

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.openDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM Facilities", null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("FacilityId"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("Name"));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow("Price"));

            facilityList.add(name + " - " + price + " VND");
            facilityIds.add(id);
        }

        cursor.close();
        db.close();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, facilityList);
        listFacilities.setAdapter(adapter);
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm tiện ích");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 10);

        final EditText etName = new EditText(this);
        etName.setHint("Tên tiện ích");
        layout.addView(etName);

        final EditText etPrice = new EditText(this);
        etPrice.setHint("Giá (VND)");
        etPrice.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(etPrice);

        builder.setView(layout);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            try {
                String name = etName.getText().toString().trim();
                double price = Double.parseDouble(etPrice.getText().toString().trim());

                DatabaseHelper dbHelper = new DatabaseHelper(this);
                SQLiteDatabase db = dbHelper.openDatabase();

                db.execSQL("INSERT INTO Facilities (Name, Price) VALUES (?, ?)",
                        new Object[]{name, price});

                db.close();
                loadFacilities();
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void showEditDeleteDialog(int facilityId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tuỳ chọn");

        builder.setItems(new String[]{"📝 Sửa", "🗑️ Xoá"}, (dialog, which) -> {
            if (which == 0) showEditDialog(facilityId);
            else confirmDelete(facilityId);
        });

        builder.show();
    }

    private void showEditDialog(int facilityId) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.openDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM Facilities WHERE FacilityId = ?", new String[]{String.valueOf(facilityId)});
        if (!cursor.moveToFirst()) return;

        String currentName = cursor.getString(cursor.getColumnIndexOrThrow("Name"));
        double currentPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("Price"));

        cursor.close();
        db.close();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sửa tiện ích");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText etName = new EditText(this);
        etName.setText(currentName);
        layout.addView(etName);

        final EditText etPrice = new EditText(this);
        etPrice.setText(String.valueOf(currentPrice));
        etPrice.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(etPrice);

        builder.setView(layout);

        builder.setPositiveButton("Cập nhật", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            double price = Double.parseDouble(etPrice.getText().toString().trim());

            SQLiteDatabase db1 = dbHelper.openDatabase();
            db1.execSQL("UPDATE Facilities SET Name = ?, Price = ? WHERE FacilityId = ?",
                    new Object[]{name, price, facilityId});
            db1.close();

            loadFacilities();
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void confirmDelete(int facilityId) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa tiện ích")
                .setMessage("Bạn có chắc chắn muốn xóa tiện ích này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    DatabaseHelper dbHelper = new DatabaseHelper(this);
                    SQLiteDatabase db = dbHelper.openDatabase();
                    db.execSQL("DELETE FROM Facilities WHERE FacilityId = ?", new Object[]{facilityId});
                    db.close();
                    loadFacilities();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
