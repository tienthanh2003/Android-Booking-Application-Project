package com.example.androidbookingapplicationproject;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidbookingapplicationproject.db.DatabaseHelper;

import java.util.Properties;
import java.util.Random;

import javax.mail.*;
import javax.mail.internet.*;

public class OTPActivity extends AppCompatActivity {

    private EditText etOTP;
    private Button btnVerify;
    private TextView tvInfo, tvResend;
    private String userEmail;
    private String paymentMethod;
    private int userId;
    private String generatedOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        etOTP = findViewById(R.id.etOtp);
        btnVerify = findViewById(R.id.btnVerifyOtp);
        tvInfo = findViewById(R.id.tvOtpMessage);
        tvResend = findViewById(R.id.tvResendOtp);

        userEmail = getIntent().getStringExtra("email");
        paymentMethod = getIntent().getStringExtra("paymentMethod");
        userId = getIntent().getIntExtra("userId", -1);

        generatedOTP = generateOTP();
        sendOTPEmail(userEmail, generatedOTP);

        tvInfo.setText("Mã OTP đã được gửi tới: " + userEmail);

        btnVerify.setOnClickListener(v -> {
            String inputOTP = etOTP.getText().toString().trim();
            if (inputOTP.equals(generatedOTP)) {
                Toast.makeText(this, "✅ Xác thực thành công!", Toast.LENGTH_LONG).show();
                insertBookingData();
                finish();
            } else {
                Toast.makeText(this, "❌ Mã OTP không đúng!", Toast.LENGTH_SHORT).show();
            }
        });

        tvResend.setOnClickListener(v -> {
            generatedOTP = generateOTP();
            sendOTPEmail(userEmail, generatedOTP);
            Toast.makeText(this, "📩 OTP mới đã được gửi!", Toast.LENGTH_SHORT).show();
        });
    }

    private String generateOTP() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    private void sendOTPEmail(String recipient, String otp) {
        AsyncTask.execute(() -> {
            final String senderEmail = "thanhconjmg2003@gmail.com";
            final String senderPassword = "pqpfzhdfghjfrodb"; // Mật khẩu ứng dụng

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPassword);
                }
            });

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(senderEmail));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
                message.setSubject("Mã OTP xác thực");
                message.setText("Mã OTP của bạn là: " + otp);

                Transport.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        });
    }

    private void insertBookingData() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.openDatabase();

        try {
            db.beginTransaction();

            Cursor cartCursor = db.rawQuery(
                    "SELECT * FROM Cart WHERE UserId = ?", new String[]{String.valueOf(userId)}
            );

            while (cartCursor.moveToNext()) {
                int cartId = cartCursor.getInt(cartCursor.getColumnIndexOrThrow("CartId"));
                int packageId = cartCursor.getInt(cartCursor.getColumnIndexOrThrow("PackageId"));
                int quantity = cartCursor.getInt(cartCursor.getColumnIndexOrThrow("Quantity"));
                String bookingDate = cartCursor.getString(cartCursor.getColumnIndexOrThrow("BookingDate"));
                String startTime = cartCursor.getString(cartCursor.getColumnIndexOrThrow("StartTime"));
                String endTime = cartCursor.getString(cartCursor.getColumnIndexOrThrow("EndTime"));

                double totalAmount = 0;

                // Tổng tiền gói
                Cursor packageCursor = db.rawQuery("SELECT Price FROM Packages WHERE PackageId = ?", new String[]{String.valueOf(packageId)});
                if (packageCursor.moveToFirst()) {
                    totalAmount += packageCursor.getDouble(0) * quantity;
                }
                packageCursor.close();

                // Tổng tiền tiện ích
                Cursor facCursor = db.rawQuery(
                        "SELECT Facilities.Price, CartFacilities.Quantity FROM CartFacilities " +
                                "JOIN Facilities ON CartFacilities.FacilityId = Facilities.FacilityId " +
                                "WHERE CartFacilities.CartId = ?",
                        new String[]{String.valueOf(cartId)}
                );

                while (facCursor.moveToNext()) {
                    double price = facCursor.getDouble(0);
                    int qty = facCursor.getInt(1);
                    totalAmount += price * qty;
                }
                facCursor.close();

                // Insert Booking
                db.execSQL("INSERT INTO Bookings (UserId, BookingDate, StartTime, EndTime, TotalAmount, Status, PaymentMethod, PaymentStatus) " +
                                "VALUES (?, ?, ?, ?, ?, 'Đã đặt', ?, 'Paid')",
                        new Object[]{userId, bookingDate, startTime, endTime, totalAmount, paymentMethod});

                Cursor lastBookingCursor = db.rawQuery("SELECT last_insert_rowid()", null);
                lastBookingCursor.moveToFirst();
                long bookingId = lastBookingCursor.getLong(0);
                lastBookingCursor.close();

                // Insert BookingDetails
                db.execSQL("INSERT INTO BookingDetails (BookingId, PackageId, Quantity, Subtotal) VALUES (?, ?, ?, ?)",
                        new Object[]{bookingId, packageId, quantity, totalAmount});

                Cursor lastDetailCursor = db.rawQuery("SELECT last_insert_rowid()", null);
                lastDetailCursor.moveToFirst();
                long detailId = lastDetailCursor.getLong(0);
                lastDetailCursor.close();

                // Insert BookingFacilities
                Cursor facilitiesCursor = db.rawQuery(
                        "SELECT FacilityId, Quantity FROM CartFacilities WHERE CartId = ?",
                        new String[]{String.valueOf(cartId)}
                );
                while (facilitiesCursor.moveToNext()) {
                    int facId = facilitiesCursor.getInt(0);
                    int qty = facilitiesCursor.getInt(1);
                    db.execSQL("INSERT INTO BookingFacilities (BookingDetailId, FacilityId, Quantity) VALUES (?, ?, ?)",
                            new Object[]{detailId, facId, qty});
                }
                facilitiesCursor.close();

                // Xoá giỏ hàng
                db.execSQL("DELETE FROM CartFacilities WHERE CartId = ?", new Object[]{cartId});
                db.execSQL("DELETE FROM Cart WHERE CartId = ?", new Object[]{cartId});

                // ✅ Gửi thông báo đến staff
                sendNotificationToStaff(db, bookingId);
            }

            cartCursor.close();
            db.setTransactionSuccessful();
            Toast.makeText(this, "🎉 Đặt hàng thành công!", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "❌ Lỗi khi lưu đơn: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            db.endTransaction();
            db.close();
        }
    }
    private void sendNotificationToStaff(SQLiteDatabase db, long bookingId) {
        Cursor staffCursor = db.rawQuery("SELECT UserId FROM User WHERE Role = 'staff'", null);
        while (staffCursor.moveToNext()) {
            int staffId = staffCursor.getInt(0);

            ContentValues notify = new ContentValues();
            notify.put("UserId", staffId);
            notify.put("Title", "Đơn hàng mới cần duyệt");
            notify.put("Content", "Khách hàng vừa đặt đơn mới. Vui lòng kiểm tra và duyệt.");
            notify.put("Type", "booking");
            notify.put("IsRead", 0);
            notify.put("RelatedId", bookingId);
            db.insert("Notifications", null, notify);
        }
        staffCursor.close();
    }

}
