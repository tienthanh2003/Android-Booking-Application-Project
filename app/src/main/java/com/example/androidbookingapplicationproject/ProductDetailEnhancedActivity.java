package com.example.androidbookingapplicationproject;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
<<<<<<< Updated upstream
import android.widget.LinearLayout;
=======
import android.widget.ImageButton;
import android.widget.ImageView;
>>>>>>> Stashed changes
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
<<<<<<< Updated upstream
import androidx.appcompat.widget.SwitchCompat;
=======
>>>>>>> Stashed changes

public class ProductDetailEnhancedActivity extends AppCompatActivity {

    private static final String TAG = "ProductDetailEnhanced";
    
<<<<<<< Updated upstream
    private TextView tvPackageName, tvDescription, tvPrice, tvCapacity, tvProductIcon;
    private TextView tvHours, tvTotalPrice;
    private Button btnBack, btnShare, btnAddToCart, btnBookNow;
    private Button btnDecreaseHours, btnIncreaseHours;
    private SwitchCompat switchWhiteboard, switchProjector;
    private LinearLayout layoutWhiteboard, layoutProjector;
=======
    private TextView tvProductName, tvProductDescription, tvProductPrice, tvRating;
    private TextView tvQuantity, tvTotalPrice, tvQuantityLabel;
    private TextView btnDecrease, btnIncrease; // Changed to TextView
    private ImageButton btnBack;
    private Button btnAddToCart, btnBookNow;
    private ImageView ivProductImage;
    
>>>>>>> Stashed changes
    private int packageId;
    private String packageName, packageType;
    private int packageCapacity;
    private double packagePrice;
<<<<<<< Updated upstream
    private int currentHours = 1;
    private boolean whiteboardSelected = false;
    private boolean projectorSelected = false;
    
    private static final double WHITEBOARD_PRICE = 10.0;
    private static final double PROJECTOR_PRICE = 25.0;
=======
    private int currentQuantity = 1;
>>>>>>> Stashed changes

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
<<<<<<< Updated upstream
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
=======
            
            // Product info views
            ivProductImage = findViewById(R.id.ivProductImage);
            tvProductName = findViewById(R.id.tvProductName);
            tvProductDescription = findViewById(R.id.tvProductDescription);
            tvProductPrice = findViewById(R.id.tvProductPrice);
            tvRating = findViewById(R.id.tvRating);
            
            // Quantity views - now TextView instead of Button
            btnDecrease = findViewById(R.id.btnDecrease);
            btnIncrease = findViewById(R.id.btnIncrease);
            tvQuantity = findViewById(R.id.tvQuantity);
            tvTotalPrice = findViewById(R.id.tvTotalPrice);
            tvQuantityLabel = findViewById(R.id.tvQuantityLabel);
>>>>>>> Stashed changes
            
            // Action buttons
            btnAddToCart = findViewById(R.id.btnAddToCart);
            btnBookNow = findViewById(R.id.btnBookNow);
            
            // Check critical views
<<<<<<< Updated upstream
            if (tvPackageName == null || btnBack == null) {
                throw new RuntimeException("Critical views not found");
            }
            
=======
            if (tvProductName == null || btnBack == null) {
                throw new RuntimeException("Critical views not found");
            }
            
            if (btnDecrease == null || btnIncrease == null) {
                Log.e(TAG, "Quantity buttons not found!");
                Toast.makeText(this, "Lỗi: Không tìm thấy nút điều chỉnh số lượng", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "Quantity TextView buttons found successfully");
                Log.d(TAG, "Decrease button text: " + btnDecrease.getText());
                Log.d(TAG, "Increase button text: " + btnIncrease.getText());
            }
            
>>>>>>> Stashed changes
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
            
<<<<<<< Updated upstream
            Log.d(TAG, "Package data: " + packageName + ", Price: " + packagePrice + ", Type: " + packageType);
=======
            // Set default values if no data passed
            if (packageName == null || packageName.isEmpty()) {
                packageName = "Gói 1 ghế";
                packagePrice = 50.0;
                packageCapacity = 1;
                packageType = "Seat";
            }
            
            Log.d(TAG, "Package data received:");
            Log.d(TAG, "- ID: " + packageId);
            Log.d(TAG, "- Name: " + packageName);
            Log.d(TAG, "- Price: " + packagePrice);
            Log.d(TAG, "- Type: " + packageType);
            Log.d(TAG, "- Capacity: " + packageCapacity);
>>>>>>> Stashed changes
        } catch (Exception e) {
            Log.e(TAG, "Error getting intent data", e);
            Toast.makeText(this, "Lỗi tải thông tin sản phẩm", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayProductInfo() {
        try {
<<<<<<< Updated upstream
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
=======
            // Set product image based on type
            setProductImage();
            
            // Set basic info
            if (tvProductName != null) {
                tvProductName.setText(packageName != null ? packageName : "Unknown Package");
            }
            
            if (tvProductPrice != null) {
                tvProductPrice.setText(String.format("%.0f,000 VNĐ", packagePrice));
            }
            
            if (tvRating != null) {
                tvRating.setText("4.5");
>>>>>>> Stashed changes
            }
            
            // Create detailed description
            String description = createDescription();
<<<<<<< Updated upstream
            if (tvDescription != null) {
                tvDescription.setText(description);
            }
            
=======
            if (tvProductDescription != null) {
                tvProductDescription.setText(description);
            }
            
            // Update quantity display
            updateQuantityDisplay();
            
>>>>>>> Stashed changes
            Log.d(TAG, "Product info displayed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error displaying product info", e);
            Toast.makeText(this, "Lỗi hiển thị thông tin sản phẩm", Toast.LENGTH_SHORT).show();
        }
    }

<<<<<<< Updated upstream
=======
    private void setProductImage() {
        if (ivProductImage == null) return;
        
        try {
            // Set image based on package type and capacity
            int imageResource = R.drawable.ic_workspace; // default
            
            Log.d(TAG, "Setting image for type: " + packageType + ", capacity: " + packageCapacity);
            
            if (packageType != null && packageType.equals("Seat")) {
                // For seat packages, use chair images
                if (packageCapacity == 1) {
                    imageResource = R.drawable.ic_seat_single; // Single seat
                    Log.d(TAG, "Using single seat icon");
                } else {
                    imageResource = R.drawable.ic_seat_multiple; // Multiple seats
                    Log.d(TAG, "Using multiple seats icon");
                }
            } else {
                // For table packages, use table/workspace images
                if (packageCapacity <= 4) {
                    imageResource = R.drawable.ic_table; // Small table
                    Log.d(TAG, "Using table icon for small capacity");
                } else if (packageCapacity <= 8) {
                    imageResource = R.drawable.ic_workspace; // Medium workspace
                    Log.d(TAG, "Using workspace icon for medium capacity");
                } else {
                    imageResource = R.drawable.ic_booking; // Large conference
                    Log.d(TAG, "Using booking icon for large capacity");
                }
            }
            
            ivProductImage.setImageResource(imageResource);
            ivProductImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            ivProductImage.clearColorFilter();
            
            Log.d(TAG, "Product image set successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error setting product image", e);
            // Fallback to workspace icon
            ivProductImage.setImageResource(R.drawable.ic_workspace);
        }
    }

>>>>>>> Stashed changes
    private String createDescription() {
        StringBuilder description = new StringBuilder();
        
        if (packageType != null && packageType.equals("Seat")) {
<<<<<<< Updated upstream
            description.append("Gói ghế làm việc cá nhân với ")
                      .append(packageCapacity)
                      .append(" chỗ ngồi thoải mái. ");
            
            description.append("Phù hợp cho làm việc cá nhân, học tập hoặc họp nhóm nhỏ. ");
=======
            if (packageCapacity == 1) {
                description.append("Gói ghế làm việc cá nhân với 1 chỗ ngồi thoải mái. ");
                description.append("Phù hợp cho làm việc cá nhân, học tập hoặc nghỉ ngơi. ");
            } else {
                description.append("Gói ghế làm việc nhóm với ")
                          .append(packageCapacity)
                          .append(" chỗ ngồi thoải mái. ");
                description.append("Phù hợp cho làm việc nhóm hoặc họp nhóm nhỏ. ");
            }
>>>>>>> Stashed changes
        } else {
            description.append("Gói bàn làm việc nhóm với ")
                      .append(packageCapacity)
                      .append(" chỗ ngồi và ");
                      
            if (packageCapacity <= 4) {
<<<<<<< Updated upstream
                description.append("1 bàn làm việc lớn. ");
            } else if (packageCapacity <= 6) {
                description.append("1 bàn conference. ");
            } else {
                description.append("2 bàn làm việc kết nối. ");
            }
            
            description.append("Lý tưởng cho meeting, workshop và làm việc nhóm. ");
=======
                description.append("1 bàn làm việc nhỏ. ");
                description.append("Phù hợp cho nhóm nhỏ, thảo luận và brainstorming. ");
            } else if (packageCapacity <= 8) {
                description.append("1 bàn conference trung bình. ");
                description.append("Lý tưởng cho meeting, workshop và họp nhóm. ");
            } else {
                description.append("bàn conference lớn kết nối. ");
                description.append("Hoàn hảo cho hội nghị lớn và sự kiện. ");
            }
>>>>>>> Stashed changes
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
            
<<<<<<< Updated upstream
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
=======
            // Quantity adjustment TextView buttons
            if (btnDecrease != null) {
                btnDecrease.setOnClickListener(v -> {
                    Log.d(TAG, "Decrease TextView clicked. Current quantity: " + currentQuantity);
                    if (currentQuantity > 1) {
                        currentQuantity--;
                        updateQuantityDisplay();
                        updateTotalPrice();
                        Toast.makeText(this, "Đã giảm! Số lượng: " + currentQuantity, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Quantity decreased to: " + currentQuantity);
                    } else {
                        Toast.makeText(this, "Số lượng tối thiểu là 1", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.d(TAG, "Decrease TextView listener set");
            } else {
                Log.e(TAG, "btnDecrease TextView is null!");
            }
            
            if (btnIncrease != null) {
                btnIncrease.setOnClickListener(v -> {
                    Log.d(TAG, "Increase TextView clicked. Current quantity: " + currentQuantity);
                    if (currentQuantity < 10) { // Max 10 items
                        currentQuantity++;
                        updateQuantityDisplay();
                        updateTotalPrice();
                        Toast.makeText(this, "Đã tăng! Số lượng: " + currentQuantity, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Quantity increased to: " + currentQuantity);
                    } else {
                        Toast.makeText(this, "Số lượng tối đa là 10", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.d(TAG, "Increase TextView listener set");
            } else {
                Log.e(TAG, "btnIncrease TextView is null!");
>>>>>>> Stashed changes
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

<<<<<<< Updated upstream
    private void updateHourDisplay() {
        if (tvHours != null) {
            tvHours.setText(String.valueOf(currentHours));
=======
    private void updateQuantityDisplay() {
        if (tvQuantity != null) {
            tvQuantity.setText(String.valueOf(currentQuantity));
            Log.d(TAG, "Quantity display updated to: " + currentQuantity);
        } else {
            Log.e(TAG, "tvQuantity is null!");
        }
        
        if (tvQuantityLabel != null) {
            tvQuantityLabel.setText("Số lượng: " + currentQuantity);
>>>>>>> Stashed changes
        }
    }

    private void updateTotalPrice() {
        try {
<<<<<<< Updated upstream
            double basePrice = packagePrice * currentHours;
            double addonPrice = 0;
            
            if (whiteboardSelected) {
                addonPrice += WHITEBOARD_PRICE * currentHours;
            }
            
            if (projectorSelected) {
                addonPrice += PROJECTOR_PRICE * currentHours;
            }
            
            double totalPrice = basePrice + addonPrice;
=======
            double totalPrice = packagePrice * currentQuantity;
>>>>>>> Stashed changes
            
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
<<<<<<< Updated upstream
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
=======
            message.append("🛒 Đã thêm vào giỏ hàng:\n");
            message.append(packageName != null ? packageName : "Sản phẩm");
            message.append("\n📦 Số lượng: ").append(currentQuantity);
            
            double totalPrice = packagePrice * currentQuantity;
            message.append(String.format("\n💰 Tổng: %.0f,000 VNĐ", totalPrice));
>>>>>>> Stashed changes
            
            Toast.makeText(this, message.toString(), Toast.LENGTH_LONG).show();
            Log.d(TAG, "Added to cart: " + message.toString());
            
<<<<<<< Updated upstream
=======
            // Go back after showing message
            findViewById(android.R.id.content).postDelayed(() -> {
                finish();
            }, 2000);
            
>>>>>>> Stashed changes
        } catch (Exception e) {
            Log.e(TAG, "Error adding to cart", e);
            Toast.makeText(this, "Lỗi thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        }
    }

    private void bookNow() {
        try {
            StringBuilder message = new StringBuilder();
<<<<<<< Updated upstream
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
=======
            message.append("⚡ Đặt ngay thành công!\n");
            message.append(packageName != null ? packageName : "Sản phẩm");
            message.append("\n📦 Số lượng: ").append(currentQuantity);
            
            double totalPrice = packagePrice * currentQuantity;
            message.append(String.format("\n💰 Tổng: %.0f,000 VNĐ", totalPrice));
            message.append("\n\n✅ Đơn hàng đã được xác nhận!");
>>>>>>> Stashed changes
            
            Toast.makeText(this, message.toString(), Toast.LENGTH_LONG).show();
            Log.d(TAG, "Book now: " + message.toString());
            
<<<<<<< Updated upstream
            // TODO: Navigate to booking confirmation screen
=======
            // Go back after showing message
            findViewById(android.R.id.content).postDelayed(() -> {
                finish();
            }, 2500);
>>>>>>> Stashed changes
            
        } catch (Exception e) {
            Log.e(TAG, "Error booking now", e);
            Toast.makeText(this, "Lỗi đặt ngay", Toast.LENGTH_SHORT).show();
        }
    }
<<<<<<< Updated upstream

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
=======
>>>>>>> Stashed changes
}
