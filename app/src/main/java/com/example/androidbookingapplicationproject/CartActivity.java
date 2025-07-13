package com.example.androidbookingapplicationproject;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidbookingapplicationproject.db.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    private int userId;
    private String email;
    private LinearLayout cartItemsContainer;
    private TextView tvTotalPrice;
    private Button btnCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        userId = getIntent().getIntExtra("userId", -1);
        email = getIntent().getStringExtra("email");

        if (userId == -1 || email == null) {
            Toast.makeText(this, "Không xác định được người dùng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        cartItemsContainer = findViewById(R.id.cartItemsContainer);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnCheckout = findViewById(R.id.btnCheckout);
        Button btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        btnCheckout.setOnClickListener(v -> showPaymentMethodDialog());

        loadCartItems(userId);
    }

    private void loadCartItems(int userId) {
        cartItemsContainer.removeAllViews();
        double totalAmount = 0;

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.openDatabase();

        Cursor cartCursor = db.rawQuery(
                "SELECT Cart.CartId, Cart.Quantity, Cart.BookingDate, Cart.StartTime, Cart.EndTime, " +
                        "Packages.Name AS PackageName, Packages.Price AS PackagePrice " +
                        "FROM Cart " +
                        "JOIN Packages ON Cart.PackageId = Packages.PackageId " +
                        "WHERE Cart.UserId = ?",
                new String[]{String.valueOf(userId)}
        );

        while (cartCursor.moveToNext()) {
            int cartId = cartCursor.getInt(cartCursor.getColumnIndexOrThrow("CartId"));
            String packageName = cartCursor.getString(cartCursor.getColumnIndexOrThrow("PackageName"));
            int quantity = cartCursor.getInt(cartCursor.getColumnIndexOrThrow("Quantity"));
            double price = cartCursor.getDouble(cartCursor.getColumnIndexOrThrow("PackagePrice"));
            String bookingDate = cartCursor.getString(cartCursor.getColumnIndexOrThrow("BookingDate"));
            String startTime = cartCursor.getString(cartCursor.getColumnIndexOrThrow("StartTime"));
            String endTime = cartCursor.getString(cartCursor.getColumnIndexOrThrow("EndTime"));

            // ✅ Tính số giờ dạng thập phân
            double durationHours = 1.0;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                Date start = sdf.parse(startTime);
                Date end = sdf.parse(endTime);
                if (start != null && end != null) {
                    long millis = end.getTime() - start.getTime();
                    durationHours = millis / (1000.0 * 60 * 60); // giữ số thập phân
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // ✅ Tính tiền gói
            double baseTotal = price;

            TextView itemView = new TextView(this);
            itemView.setText("📦 " + packageName + " x" + quantity + " - " + price + "đ\n📅 " + bookingDate + " ⏰ " + startTime + " → " + endTime);
            itemView.setTextSize(16f);
            itemView.setTextColor(Color.BLACK);
            itemView.setPadding(8, 12, 8, 4);
            cartItemsContainer.addView(itemView);

            // ✅ Tính tiền tiện ích
            Cursor facCursor = db.rawQuery(
                    "SELECT Facilities.Name, Facilities.Price, CartFacilities.Quantity " +
                            "FROM CartFacilities " +
                            "JOIN Facilities ON CartFacilities.FacilityId = Facilities.FacilityId " +
                            "WHERE CartFacilities.CartId = ?",
                    new String[]{String.valueOf(cartId)}
            );

            while (facCursor.moveToNext()) {
                String facName = facCursor.getString(0);
                double facPrice = facCursor.getDouble(1);
                int facQty = facCursor.getInt(2);
                double facTotal = facPrice * facQty;

                baseTotal += facTotal;

                TextView facView = new TextView(this);
                facView.setText("↳ " + facName + " x" + facQty + " (+ " + facTotal + "đ)");
                facView.setTextSize(14f);
                facView.setTextColor(Color.DKGRAY);
                facView.setPadding(24, 2, 8, 2);
                cartItemsContainer.addView(facView);
            }

            facCursor.close();

            // ✅ Tổng = (Gói + Tiện ích) * số giờ
            double itemTotal = baseTotal * durationHours * quantity;

            TextView totalItemView = new TextView(this);
            totalItemView.setText("👉 Tổng mục này: " + itemTotal + "đ (x " + durationHours + " giờ)");
            totalItemView.setTextSize(14f);
            totalItemView.setTextColor(Color.BLUE);
            totalItemView.setPadding(16, 8, 8, 8);
            cartItemsContainer.addView(totalItemView);

            // Nút xoá
            Button btnDelete = new Button(this);
            btnDelete.setText("❌ Xóa mục này");
            btnDelete.setTextColor(Color.WHITE);
            btnDelete.setBackgroundColor(Color.RED);
            btnDelete.setPadding(16, 8, 16, 8);
            btnDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("Xác nhận xoá")
                        .setMessage("Bạn có chắc chắn muốn xoá mục này khỏi giỏ hàng?")
                        .setPositiveButton("Xoá", (dialog, which) -> {
                            deleteCartItem(cartId);
                            loadCartItems(userId);
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            });
            cartItemsContainer.addView(btnDelete);

            View divider = new View(this);
            divider.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 1));
            divider.setBackgroundColor(Color.LTGRAY);
            cartItemsContainer.addView(divider);

            totalAmount += itemTotal;
        }

        cartCursor.close();
        db.close();

        tvTotalPrice.setText("Tổng: " + totalAmount + "đ");
    }


    private void deleteCartItem(int cartId) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.openDatabase();
        db.execSQL("DELETE FROM Cart WHERE CartId = ?", new Object[]{cartId});
        db.close();
    }

    private void showPaymentMethodDialog() {
        String[] methods = {"Chuyển khoản", "Tiền mặt khi đến", "Momo (chưa hỗ trợ)"};

        new AlertDialog.Builder(this)
                .setTitle("Chọn phương thức thanh toán")
                .setItems(methods, (dialog, which) -> {
                    String method = methods[which];
                    if (method.equals("Chuyển khoản")) {
                        showBankTransferInfoDialog(method);
                    } else {
                        proceedToOTP(method);
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showBankTransferInfoDialog(String method) {
        String message = "💳 Ngân hàng: Vietcombank\n" +
                "👤 Tên TK: CTY TNHH ABC\n" +
                "🔢 Số TK: 0123456789\n" +
                "📝 Nội dung: THANHTOAN_" + email;

        new AlertDialog.Builder(this)
                .setTitle("Thông tin chuyển khoản")
                .setMessage(message)
                .setPositiveButton("Tôi đã chuyển", (dialog, which) -> {
                    proceedToOTP(method);
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private void proceedToOTP(String method) {
        Intent intent = new Intent(this, OTPActivity.class);
        intent.putExtra("paymentMethod", method);
        intent.putExtra("userId", userId);
        intent.putExtra("email", email);
        startActivity(intent);
    }

}
