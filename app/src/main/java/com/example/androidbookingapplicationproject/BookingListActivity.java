package com.example.androidbookingapplicationproject;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidbookingapplicationproject.db.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class BookingListActivity extends AppCompatActivity {

    private ListView lvBookings;
    private ArrayAdapter<String> adapter;
    private List<String> displayList = new ArrayList<>();
    private List<Integer> bookingIds = new ArrayList<>(); // lưu bookingId tương ứng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_list); // tạo layout này

        lvBookings = findViewById(R.id.lvBookings);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        lvBookings.setAdapter(adapter);

        lvBookings.setOnItemClickListener((parent, view, position, id) -> {
            int bookingId = bookingIds.get(position);
            Intent intent = new Intent(this, ApproveBookingActivity.class);
            intent.putExtra("bookingId", bookingId);
            startActivity(intent);
        });

        loadPendingBookings();
    }

    private void loadPendingBookings() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.openDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT b.BookingId, u.Name, b.BookingDate FROM Bookings b JOIN User u ON b.UserId = u.UserId WHERE b.Status = 'Đã đặt'",
                null
        );

        displayList.clear();
        bookingIds.clear();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String date = cursor.getString(2);

            bookingIds.add(id);
            displayList.add("[" + id + "] " + name + " - " + date);
        }

        cursor.close();
        db.close();

        adapter.notifyDataSetChanged();
    }
}

