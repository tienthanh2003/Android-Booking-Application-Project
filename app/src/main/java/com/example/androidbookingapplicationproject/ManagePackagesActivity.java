package com.example.androidbookingapplicationproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputType;

import com.example.androidbookingapplicationproject.db.DatabaseHelper;

import java.util.ArrayList;

public class ManagePackagesActivity extends AppCompatActivity {

    private Button btnAddPackage;
    private ListView listPackages;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> packageList;
    private ArrayList<Integer> packageIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_packages);

        btnAddPackage = findViewById(R.id.btnAddPackage);
        listPackages = findViewById(R.id.listPackages);

        loadPackages();

        btnAddPackage.setOnClickListener(v -> showAddDialog());

        listPackages.setOnItemClickListener((parent, view, position, id) -> {
            int packageId = packageIds.get(position);
            showEditDeleteDialog(packageId);
        });
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ManagePackagesActivity.this, MainActivity.class);
            startActivity(intent);
        });

    }

    private void loadPackages() {
        packageList = new ArrayList<>();
        packageIds = new ArrayList<>();

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.openDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM Packages", null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("PackageId"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("Name"));
            int capacity = cursor.getInt(cursor.getColumnIndexOrThrow("Capacity"));
            int price = cursor.getInt(cursor.getColumnIndexOrThrow("Price"));

            packageList.add(name + " - " + capacity + " chỗ - " + price);
            packageIds.add(id);
        }

        cursor.close();
        db.close();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, packageList);
        listPackages.setAdapter(adapter);
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm gói mới");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 10);

        // Tên gói
        final EditText etName = new EditText(this);
        etName.setHint("Tên gói");
        layout.addView(etName);

        // Số chỗ
        final EditText etCapacity = new EditText(this);
        etCapacity.setHint("Số chỗ");
        etCapacity.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(etCapacity);

        // Loại (Seat/Table)
        final Spinner spinnerType = new Spinner(this);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Seat", "Table"});
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(spinnerAdapter);
        layout.addView(spinnerType);

        // Giá
        final EditText etPrice = new EditText(this);
        etPrice.setHint("Giá (nghìn VND)");
        etPrice.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(etPrice);

        builder.setView(layout);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            try {
                String name = etName.getText().toString().trim();
                int capacity = Integer.parseInt(etCapacity.getText().toString().trim());
                String type = spinnerType.getSelectedItem().toString();
                double price = Double.parseDouble(etPrice.getText().toString().trim());

                DatabaseHelper dbHelper = new DatabaseHelper(this);
                SQLiteDatabase db = dbHelper.openDatabase();

                db.execSQL("INSERT INTO Packages (Name, Capacity, Type, Price) VALUES (?, ?, ?, ?)",
                        new Object[]{name, capacity, type, price});

                db.close();
                loadPackages();
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi thêm gói: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }


    private void showEditDeleteDialog(int packageId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tùy chọn");

        builder.setItems(new String[]{"📝 Sửa", "🗑️ Xóa"}, (dialog, which) -> {
            if (which == 0) showEditDialog(packageId);
            else confirmDelete(packageId);
        });

        builder.show();
    }

    private void showEditDialog(int packageId) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.openDatabase();

        // 🔁 Sửa "ID" → "PackageId"
        Cursor cursor = db.rawQuery("SELECT * FROM Packages WHERE PackageId = ?", new String[]{String.valueOf(packageId)});
        if (!cursor.moveToFirst()) return;

        String currentName = cursor.getString(cursor.getColumnIndexOrThrow("Name"));
        int currentCapacity = cursor.getInt(cursor.getColumnIndexOrThrow("Capacity"));
        int currentPrice = cursor.getInt(cursor.getColumnIndexOrThrow("Price"));

        cursor.close();
        db.close();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sửa gói");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText etName = new EditText(this);
        etName.setText(currentName);
        layout.addView(etName);

        final EditText etCapacity = new EditText(this);
        etCapacity.setText(String.valueOf(currentCapacity));
        layout.addView(etCapacity);

        final EditText etPrice = new EditText(this);
        etPrice.setText(String.valueOf(currentPrice));
        layout.addView(etPrice);

        builder.setView(layout);

        builder.setPositiveButton("Cập nhật", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            int capacity = Integer.parseInt(etCapacity.getText().toString().trim());
            int price = Integer.parseInt(etPrice.getText().toString().trim());

            SQLiteDatabase db1 = dbHelper.openDatabase();
            // 🔁 Sửa "ID" → "PackageId"
            db1.execSQL("UPDATE Packages SET Name=?, Capacity=?, Price=? WHERE PackageId=?",
                    new Object[]{name, capacity, price, packageId});
            db1.close();

            loadPackages();
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void confirmDelete(int packageId) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa gói")
                .setMessage("Bạn có chắc chắn muốn xóa gói này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    DatabaseHelper dbHelper = new DatabaseHelper(this);
                    SQLiteDatabase db = dbHelper.openDatabase();
                    // 🔁 Sửa "ID" → "PackageId"
                    db.execSQL("DELETE FROM Packages WHERE PackageId=?", new Object[]{packageId});
                    db.close();
                    loadPackages();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

}