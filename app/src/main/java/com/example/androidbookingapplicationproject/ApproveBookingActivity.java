package com.example.androidbookingapplicationproject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidbookingapplicationproject.db.DatabaseHelper;

public class ApproveBookingActivity extends AppCompatActivity {

    private int bookingId;
    private TextView tvCustomerName, tvBookingDate, tvTime, tvPackage, tvTotal;
    private LinearLayout layoutFacilities;
    private Button btnApprove, btnReject;
    private boolean isActionable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_booking);

        bookingId = getIntent().getIntExtra("bookingId", -1);
        if (bookingId == -1) {
            Toast.makeText(this, "Không tìm thấy booking!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ánh xạ View
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvBookingDate = findViewById(R.id.tvBookingDate);
        tvTime = findViewById(R.id.tvTime);
        tvPackage = findViewById(R.id.tvPackage);
        tvTotal = findViewById(R.id.tvTotal);
        layoutFacilities = findViewById(R.id.layoutFacilities);
        btnApprove = findViewById(R.id.btnApprove);
        btnReject = findViewById(R.id.btnReject);

        loadBookingDetail();

        btnApprove.setOnClickListener(v -> {
            if (isActionable) updateBookingStatus("Đã duyệt");
        });

        btnReject.setOnClickListener(v -> {
            if (isActionable) updateBookingStatus("Từ chối");
        });
    }

    private void loadBookingDetail() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.openDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT b.BookingDate, b.StartTime, b.EndTime, b.TotalAmount, u.Name, " +
                        "p.Name AS PackageName, b.Status " +
                        "FROM Bookings b " +
                        "JOIN User u ON b.UserId = u.UserId " +
                        "JOIN BookingDetails bd ON bd.BookingId = b.BookingId " +
                        "JOIN Packages p ON p.PackageId = bd.PackageId " +
                        "WHERE b.BookingId = ?",
                new String[]{String.valueOf(bookingId)}
        );

        if (cursor.moveToFirst()) {
            String customerName = cursor.getString(cursor.getColumnIndexOrThrow("Name"));
            String bookingDate = cursor.getString(cursor.getColumnIndexOrThrow("BookingDate"));
            String startTime = cursor.getString(cursor.getColumnIndexOrThrow("StartTime"));
            String endTime = cursor.getString(cursor.getColumnIndexOrThrow("EndTime"));
            double total = cursor.getDouble(cursor.getColumnIndexOrThrow("TotalAmount"));
            String packageName = cursor.getString(cursor.getColumnIndexOrThrow("PackageName"));
            String status = cursor.getString(cursor.getColumnIndexOrThrow("Status"));

            tvCustomerName.setText("👤 Khách hàng: " + customerName);
            tvBookingDate.setText("📅 Ngày: " + bookingDate);
            tvTime.setText("⏰ Thời gian: " + startTime + " - " + endTime);
            tvPackage.setText("📦 Gói: " + packageName);
            tvTotal.setText("💰 Tổng tiền: " + total + "đ");

            if (status != null && !status.equals("Đã đặt")) {
                // Đã được xử lý → vô hiệu hóa
                isActionable = false;
                btnApprove.setEnabled(false);
                btnReject.setEnabled(false);

                TextView tvNote = new TextView(this);
                tvNote.setText("⚠️ Đơn hàng này đã được xử lý (" + status + ").");
                tvNote.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                tvNote.setPadding(12, 16, 12, 12);
                layoutFacilities.addView(tvNote);
            }
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin chi tiết!", Toast.LENGTH_SHORT).show();
            cursor.close();
            db.close();
            finish();
            return;
        }

        cursor.close();

        // Tiện ích đi kèm
        Cursor detailCursor = db.rawQuery(
                "SELECT DetailId FROM BookingDetails WHERE BookingId = ?",
                new String[]{String.valueOf(bookingId)}
        );

        if (detailCursor.moveToFirst()) {
            int detailId = detailCursor.getInt(0);

            Cursor facCursor = db.rawQuery(
                    "SELECT f.Name, bf.Quantity FROM BookingFacilities bf " +
                            "JOIN Facilities f ON bf.FacilityId = f.FacilityId " +
                            "WHERE bf.BookingDetailId = ?",
                    new String[]{String.valueOf(detailId)}
            );

            while (facCursor.moveToNext()) {
                String facName = facCursor.getString(0);
                int facQty = facCursor.getInt(1);

                TextView facView = new TextView(this);
                facView.setText("↳ " + facName + " x" + facQty);
                facView.setTextSize(14f);
                facView.setTextColor(getResources().getColor(android.R.color.darker_gray));
                facView.setPadding(8, 4, 8, 4);

                layoutFacilities.addView(facView);
            }

            facCursor.close();
        }

        detailCursor.close();
        db.close();
    }

    private void updateBookingStatus(String status) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.openDatabase();

        try {
            // Cập nhật trạng thái đơn hàng
            ContentValues values = new ContentValues();
            values.put("Status", status);
            db.update("Bookings", values, "BookingId = ?", new String[]{String.valueOf(bookingId)});

            // Lấy thông tin UserId từ booking
            Cursor cursor = db.rawQuery(
                    "SELECT UserId FROM Bookings WHERE BookingId = ?",
                    new String[]{String.valueOf(bookingId)}
            );

            int userId = -1;
            if (cursor.moveToFirst()) {
                userId = cursor.getInt(0);
            }
            cursor.close();

            // Nếu tìm được userId thì tạo thông báo
            if (userId != -1) {
                String title = "Trạng thái đơn hàng đã thay đổi";
                String content;
                if (status.equals("Đã duyệt")) {
                    content = "Đơn hàng của bạn đã được duyệt. Cảm ơn bạn đã đặt lịch!";
                } else {
                    content = "Rất tiếc! Đơn hàng của bạn đã bị từ chối. Vui lòng đặt lại nếu cần.";
                }

                ContentValues notify = new ContentValues();
                notify.put("UserId", userId);
                notify.put("Title", title);
                notify.put("Content", content);
                notify.put("Type", "booking");
                notify.put("IsRead", 0);
                notify.put("RelatedId", bookingId); // liên kết để khách biết đơn nào

                // Chỉ thêm nếu chưa có thông báo cho booking này
                Cursor check = db.rawQuery(
                        "SELECT NotificationId FROM Notifications WHERE UserId = ? AND RelatedId = ? AND Type = 'booking'",
                        new String[]{String.valueOf(userId), String.valueOf(bookingId)}
                );
                if (!check.moveToFirst()) {
                    db.insert("Notifications", null, notify);
                }
                check.close();
            }

            Toast.makeText(this, "Đã cập nhật trạng thái: " + status, Toast.LENGTH_SHORT).show();
            finish();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi cập nhật trạng thái", Toast.LENGTH_SHORT).show();
        } finally {
            db.close();
        }
    }

}
