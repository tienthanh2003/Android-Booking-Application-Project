package com.example.androidbookingapplicationproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView tvPackageName, tvDescription, tvPrice, tvCapacity;
    private Button btnBack, btnAddToCart;
    
    private int packageId;
    private String packageName, packageType;
    private int packageCapacity;
    private double packagePrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_product_detail);

            initViews();
            getDataFromIntent();
            displayProductInfo();
            setupListeners();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi tải chi tiết sản phẩm: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish(); // Close activity if there's an error
        }
    }

    private void initViews() {
        try {
            tvPackageName = findViewById(R.id.tvPackageName);
            tvDescription = findViewById(R.id.tvDescription);
            tvPrice = findViewById(R.id.tvPrice);
            tvCapacity = findViewById(R.id.tvCapacity);
            btnBack = findViewById(R.id.btnBack);
            btnAddToCart = findViewById(R.id.btnAddToCart);

        // Check if critical views are found
        if (tvPackageName == null || btnBack == null) {
            throw new RuntimeException("Critical views not found in layout");
        }

    } catch (Exception e) {
        throw new RuntimeException("Failed to initialize views: " + e.getMessage());
    }
    }

    private void getDataFromIntent() {
        packageId = getIntent().getIntExtra("PACKAGE_ID", 0);
        packageName = getIntent().getStringExtra("PACKAGE_NAME");
        packageCapacity = getIntent().getIntExtra("PACKAGE_CAPACITY", 0);
        packageType = getIntent().getStringExtra("PACKAGE_TYPE");
        packagePrice = getIntent().getDoubleExtra("PACKAGE_PRICE", 0.0);
    }

    private void displayProductInfo() {
        try {
            if (tvPackageName != null) {
                tvPackageName.setText(packageName != null ? packageName : "Unknown Package");
            }
            if (tvCapacity != null) {
                tvCapacity.setText(String.valueOf(packageCapacity));
            }
            if (tvPrice != null) {
                tvPrice.setText(String.format("%.0fk/giờ", packagePrice));
            }
            
            // Create description
            String description;
            if (packageType != null && packageType.equals("Seat")) {
                description = packageCapacity + " chỗ ngồi làm việc";
            } else {
                if (packageCapacity <= 4) {
                    description = packageCapacity + " chỗ ngồi + 1 bàn làm việc";
                } else if (packageCapacity <= 6) {
                    description = packageCapacity + " chỗ ngồi + 1 bàn làm việc";
                } else {
                    description = packageCapacity + " chỗ ngồi + 2 bàn làm việc";
                }
            }
            
            if (tvDescription != null) {
                tvDescription.setText(description);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi hiển thị thông tin sản phẩm", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupListeners() {
        try {
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> finish());
            }
            
            if (btnAddToCart != null) {
                btnAddToCart.setOnClickListener(v -> {
                    // TODO: Implement add to cart functionality
                    Toast.makeText(this, "Đã thêm vào giỏ hàng: " + (packageName != null ? packageName : "Sản phẩm"), Toast.LENGTH_SHORT).show();
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi cài đặt sự kiện", Toast.LENGTH_SHORT).show();
        }
    }
}
