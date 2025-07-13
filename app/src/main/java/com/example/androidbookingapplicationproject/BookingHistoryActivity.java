package com.example.androidbookingapplicationproject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidbookingapplicationproject.db.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class BookingHistoryActivity extends AppCompatActivity {

    private ListView lvBookingHistory;
    private ArrayAdapter<String> adapter;
    private List<String> historyList = new ArrayList<>();
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);

        userId = getIntent().getIntExtra("userId", -1);
        lvBookingHistory = findViewById(R.id.lvBookingHistory);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historyList);
        lvBookingHistory.setAdapter(adapter);

        if (userId != -1) {
            loadBookingHistory();
        }
    }

    private void loadBookingHistory() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.openDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT b.BookingId, b.BookingDate, b.StartTime, b.EndTime, b.TotalAmount, b.Status, " +
                        "p.Name AS PackageName, bd.Quantity, bd.DetailId " +
                        "FROM Bookings b " +
                        "JOIN BookingDetails bd ON b.BookingId = bd.BookingId " +
                        "JOIN Packages p ON bd.PackageId = p.PackageId " +
                        "WHERE b.UserId = ? " +
                        "ORDER BY b.BookingDate DESC",
                new String[]{String.valueOf(userId)}
        );

        while (cursor.moveToNext()) {
            int bookingId = cursor.getInt(0);
            String date = cursor.getString(1);
            String startTime = cursor.getString(2);
            String endTime = cursor.getString(3);
            double total = cursor.getDouble(4);
            String status = cursor.getString(5);
            String packageName = cursor.getString(6);
            int quantity = cursor.getInt(7);
            int detailId = cursor.getInt(8);

            StringBuilder details = new StringBuilder();
            details.append("🆔 Mã đơn: ").append(bookingId)
                    .append("\n📅 ").append(date)
                    .append("\n⏰ ").append(startTime).append(" - ").append(endTime)
                    .append("\n📦 Gói: ").append(packageName).append(" x").append(quantity)
                    .append("\n💰 Tổng: ").append(total).append("đ")
                    .append("\n📌 Trạng thái: ").append(status);

            // Truy vấn tiện ích nếu có
            Cursor facCursor = db.rawQuery(
                    "SELECT f.Name, bf.Quantity FROM BookingFacilities bf " +
                            "JOIN Facilities f ON bf.FacilityId = f.FacilityId " +
                            "WHERE bf.BookingDetailId = ?",
                    new String[]{String.valueOf(detailId)}
            );

            while (facCursor.moveToNext()) {
                String facName = facCursor.getString(0);
                int facQty = facCursor.getInt(1);
                details.append("\n    ↳ ").append(facName).append(" x").append(facQty);
            }

            facCursor.close();
            historyList.add(details.toString());
        }

        cursor.close();
        db.close();
        adapter.notifyDataSetChanged();
    }
}
