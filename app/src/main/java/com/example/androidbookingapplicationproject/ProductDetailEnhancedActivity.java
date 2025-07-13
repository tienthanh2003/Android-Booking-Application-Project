package com.example.androidbookingapplicationproject;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class ProductDetailEnhancedActivity extends AppCompatActivity {

    private static final String TAG = "ProductDetailEnhanced";
    
    private TextView tvPackageName, tvDescription, tvPrice, tvCapacity, tvProductIcon;
    private TextView tvHours, tvTotalPrice;
    private Button btnBack, btnShare, btnAddToCart, btnBookNow;
    private Button btnDecreaseHours, btnIncreaseHours;
    private SwitchCompat switchWhiteboard, switchProjector;
    private LinearLayout layoutWhiteboard, layoutProjector;
    private int packageId;
    private String packageName, packageType;
    private int packageCapacity;
    private double packagePrice;
    private int currentHours = 1;
    private boolean whiteboardSelected = false;
    private boolean projectorSelected = false;
    
    private static final double WHITEBOARD_PRICE = 10.0;
    private static final double PROJECTOR_PRICE = 25.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate started");
        
        try {
            setContentView(R.layout.activity_product_detail_enhanced);
            
            initViews();
            getDataFromIntent();
            displayProductInfo();
            setupListeners();
            updateTotalPrice();
            
            Log.d(TAG, "Enhanced activity created successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error in enhanced activity", e);
            Toast.makeText(this, "Lỗi tải chi tiết sản phẩm: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initViews() {
        try {
            // Header views
            btnBack = findViewById(R.id.btnBack);
            btnShare = findViewById(R.id.btnShare);
            
            // Product info views
            tvProductIcon = findViewById(R.id.tvProductIcon);
            tvPackageName = findViewById(R.id.tvPackageName);
            tvDescription = findViewById(R.id.tvDescription);
            tvPrice = findViewById(R.id.tvPrice);
            tvCapacity = findViewById(R.id.tvCapacity);
            
            // Duration views
            btnDecreaseHours = findViewById(R.id.btnDecreaseHours);
            btnIncreaseHours = findViewById(R.id.btnIncreaseHours);
            tvHours = findViewById(R.id.tvHours);
            tvTotalPrice = findViewById(R.id.tvTotalPrice);
            
            // Add-on views
            switchWhiteboard = findViewById(R.id.switchWhiteboard);
            switchProjector = findViewById(R.id.switchProjector);
            layoutWhiteboard = findViewById(R.id.layoutWhiteboard);
            layoutProjector = findViewById(R.id.layoutProjector);
            
            // Action buttons
            btnAddToCart = findViewById(R.id.btnAddToCart);
            btnBookNow = findViewById(R.id.btnBookNow);
            
            // Check critical views
            if (tvPackageName == null || btnBack == null) {
                throw new RuntimeException("Critical views not found");
            }
            
            Log.d(TAG, "Views initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views", e);
            throw new RuntimeException("Failed to initialize views: " + e.getMessage());
        }
    }

    private void getDataFromIntent() {
        try {
            packageId = getIntent().getIntExtra("PACKAGE_ID", 0);
            packageName = getIntent().getStringExtra("PACKAGE_NAME");
            packageCapacity = getIntent().getIntExtra("PACKAGE_CAPACITY", 0);
            packageType = getIntent().getStringExtra("PACKAGE_TYPE");
            packagePrice = getIntent().getDoubleExtra("PACKAGE_PRICE", 0.0);
            
            Log.d(TAG, "Package data: " + packageName + ", Price: " + packagePrice + ", Type: " + packageType);
        } catch (Exception e) {
            Log.e(TAG, "Error getting intent data", e);
            Toast.makeText(this, "Lỗi tải thông tin sản phẩm", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayProductInfo() {
        try {
            // Set product icon based on type
            if (tvProductIcon != null) {
                String icon = (packageType != null && packageType.equals("Seat")) ? "🪑" : "🗂️";
                tvProductIcon.setText(icon);
            }
            
            // Set basic info
            if (tvPackageName != null) {
                tvPackageName.setText(packageName != null ? packageName : "Unknown Package");
            }
            
            if (tvCapacity != null) {
                tvCapacity.setText(String.valueOf(packageCapacity));
            }
            
            if (tvPrice != null) {
                tvPrice.setText(String.format("%.0fk", packagePrice));
            }
            
            // Create detailed description
            String description = createDescription();
            if (tvDescription != null) {
                tvDescription.setText(description);
            }
            
            Log.d(TAG, "Product info displayed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error displaying product info", e);
            Toast.makeText(this, "Lỗi hiển thị thông tin sản phẩm", Toast.LENGTH_SHORT).show();
        }
    }

    private String createDescription() {
        StringBuilder description = new StringBuilder();
        
        if (packageType != null && packageType.equals("Seat")) {
            description.append("Gói ghế làm việc cá nhân với ")
                      .append(packageCapacity)
                      .append(" chỗ ngồi thoải mái. ");
            
            description.append("Phù hợp cho làm việc cá nhân, học tập hoặc họp nhóm nhỏ. ");
        } else {
            description.append("Gói bàn làm việc nhóm với ")
                      .append(packageCapacity)
                      .append(" chỗ ngồi và ");
                      
            if (packageCapacity <= 4) {
                description.append("1 bàn làm việc lớn. ");
            } else if (packageCapacity <= 6) {
                description.append("1 bàn conference. ");
            } else {
                description.append("2 bàn làm việc kết nối. ");
            }
            
            description.append("Lý tưởng cho meeting, workshop và làm việc nhóm. ");
        }
        
        description.append("Bao gồm WiFi tốc độ cao, điều hòa không khí và ổ cắm điện.");
        
        return description.toString();
    }

    private void setupListeners() {
        try {
            // Back button
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> {
                    Log.d(TAG, "Back button clicked");
                    finish();
                });
            }
            
            // Share button
            if (btnShare != null) {
                btnShare.setOnClickListener(v -> {
                    Toast.makeText(this, "Tính năng chia sẻ sẽ được triển khai sau", Toast.LENGTH_SHORT).show();
                });
            }
            
            // Hour adjustment buttons
            if (btnDecreaseHours != null) {
                btnDecreaseHours.setOnClickListener(v -> {
                    if (currentHours > 1) {
                        currentHours--;
                        updateHourDisplay();
                        updateTotalPrice();
                    }
                });
            }
            
            if (btnIncreaseHours != null) {
                btnIncreaseHours.setOnClickListener(v -> {
                    if (currentHours < 12) { // Max 12 hours
                        currentHours++;
                        updateHourDisplay();
                        updateTotalPrice();
                    }
                });
            }
            
            // Add-on switches
            if (switchWhiteboard != null) {
                switchWhiteboard.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    whiteboardSelected = isChecked;
                    updateTotalPrice();
                });
            }
            
            if (switchProjector != null) {
                switchProjector.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    projectorSelected = isChecked;
                    updateTotalPrice();
                });
            }
            
            // Layout click listeners for add-ons
            if (layoutWhiteboard != null) {
                layoutWhiteboard.setOnClickListener(v -> {
                    if (switchWhiteboard != null) {
                        switchWhiteboard.setChecked(!switchWhiteboard.isChecked());
                    }
                });
            }
            
            if (layoutProjector != null) {
                layoutProjector.setOnClickListener(v -> {
                    if (switchProjector != null) {
                        switchProjector.setChecked(!switchProjector.isChecked());
                    }
                });
            }
            
            // Action buttons
            if (btnAddToCart != null) {
                btnAddToCart.setOnClickListener(v -> {
                    addToCart();
                });
            }
            
            if (btnBookNow != null) {
                btnBookNow.setOnClickListener(v -> {
                    bookNow();
                });
            }
            
            Log.d(TAG, "Listeners set up successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up listeners", e);
            Toast.makeText(this, "Lỗi cài đặt sự kiện", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateHourDisplay() {
        if (tvHours != null) {
            tvHours.setText(String.valueOf(currentHours));
        }
    }

    private void updateTotalPrice() {
        try {
            double basePrice = packagePrice * currentHours;
            double addonPrice = 0;
            
            if (whiteboardSelected) {
                addonPrice += WHITEBOARD_PRICE * currentHours;
            }
            
            if (projectorSelected) {
                addonPrice += PROJECTOR_PRICE * currentHours;
            }
            
            double totalPrice = basePrice + addonPrice;
            
            if (tvTotalPrice != null) {
                tvTotalPrice.setText(String.format("%.0fk", totalPrice));
            }
            
            Log.d(TAG, "Total price updated: " + totalPrice + "k");
        } catch (Exception e) {
            Log.e(TAG, "Error updating total price", e);
        }
    }

    private void addToCart() {
        try {
            StringBuilder message = new StringBuilder();
            message.append("Đã thêm vào giỏ hàng:\n");
            message.append(packageName != null ? packageName : "Sản phẩm");
            message.append("\n");
            message.append(currentHours).append(" giờ");
            
            if (whiteboardSelected) {
                message.append("\n+ Bảng trắng");
            }
            
            if (projectorSelected) {
                message.append("\n+ Máy chiếu");
            }
            
            Toast.makeText(this, message.toString(), Toast.LENGTH_LONG).show();
            Log.d(TAG, "Added to cart: " + message.toString());
            
        } catch (Exception e) {
            Log.e(TAG, "Error adding to cart", e);
            Toast.makeText(this, "Lỗi thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        }
    }

    private void bookNow() {
        try {
            StringBuilder message = new StringBuilder();
            message.append("Đặt ngay:\n");
            message.append(packageName != null ? packageName : "Sản phẩm");
            message.append("\n");
            message.append(currentHours).append(" giờ");
            
            if (whiteboardSelected) {
                message.append("\n+ Bảng trắng");
            }
            
            if (projectorSelected) {
                message.append("\n+ Máy chiếu");
            }
            
            double totalPrice = calculateTotalPrice();
            message.append(String.format("\nTổng: %.0fk", totalPrice));
            
            Toast.makeText(this, message.toString(), Toast.LENGTH_LONG).show();
            Log.d(TAG, "Book now: " + message.toString());
            
            // TODO: Navigate to booking confirmation screen
            
        } catch (Exception e) {
            Log.e(TAG, "Error booking now", e);
            Toast.makeText(this, "Lỗi đặt ngay", Toast.LENGTH_SHORT).show();
        }
    }

    private double calculateTotalPrice() {
        double basePrice = packagePrice * currentHours;
        double addonPrice = 0;
        
        if (whiteboardSelected) {
            addonPrice += WHITEBOARD_PRICE * currentHours;
        }
        
        if (projectorSelected) {
            addonPrice += PROJECTOR_PRICE * currentHours;
        }
        
        return basePrice + addonPrice;
    }
}
