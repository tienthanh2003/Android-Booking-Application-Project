package com.example.androidbookingapplicationproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
<<<<<<< Updated upstream
=======
import android.widget.CheckBox;
>>>>>>> Stashed changes

import androidx.appcompat.app.AppCompatActivity;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView tvPackageName, tvDescription, tvPrice, tvCapacity;
<<<<<<< Updated upstream
    private Button btnBack, btnAddToCart;
=======
    private TextView tvHours, tvTotalPrice;
    private Button btnBack, btnAddToCart;
    private Button btnDecreaseHours, btnIncreaseHours;
    private CheckBox checkboxWhiteboard, checkboxTV, checkboxNetwork;
>>>>>>> Stashed changes
    
    private int packageId;
    private String packageName, packageType;
    private int packageCapacity;
    private double packagePrice;
<<<<<<< Updated upstream
=======
    private int currentHours = 1;
    
    private static final double WHITEBOARD_PRICE = 10.0;
    private static final double TV_PRICE = 15.0;
    private static final double NETWORK_PRICE = 5.0;
>>>>>>> Stashed changes

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
<<<<<<< Updated upstream
=======
            // Basic info views
>>>>>>> Stashed changes
            tvPackageName = findViewById(R.id.tvPackageName);
            tvDescription = findViewById(R.id.tvDescription);
            tvPrice = findViewById(R.id.tvPrice);
            tvCapacity = findViewById(R.id.tvCapacity);
<<<<<<< Updated upstream
            btnBack = findViewById(R.id.btnBack);
            btnAddToCart = findViewById(R.id.btnAddToCart);

        // Check if critical views are found
        if (tvPackageName == null || btnBack == null) {
            throw new RuntimeException("Critical views not found in layout");
        }

    } catch (Exception e) {
        throw new RuntimeException("Failed to initialize views: " + e.getMessage());
    }
=======
            
            // Hour controls
            btnDecreaseHours = findViewById(R.id.btnDecreaseHours);
            btnIncreaseHours = findViewById(R.id.btnIncreaseHours);
            tvHours = findViewById(R.id.tvHours);
            
            // Add-on checkboxes
            checkboxWhiteboard = findViewById(R.id.checkboxWhiteboard);
            checkboxTV = findViewById(R.id.checkboxTV);
            checkboxNetwork = findViewById(R.id.checkboxNetwork);
            
            // Action buttons
            btnBack = findViewById(R.id.btnBack);
            btnAddToCart = findViewById(R.id.btnAddToCart);

            // Check if critical views are found
            if (tvPackageName == null || btnBack == null) {
                throw new RuntimeException("Critical views not found in layout");
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize views: " + e.getMessage());
        }
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> finish());
            }
            
            if (btnAddToCart != null) {
                btnAddToCart.setOnClickListener(v -> {
                    // TODO: Implement add to cart functionality
                    Toast.makeText(this, "Đã thêm vào giỏ hàng: " + (packageName != null ? packageName : "Sản phẩm"), Toast.LENGTH_SHORT).show();
                });
            }
=======
            // Back button - make it more visible
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> finish());
                // Make back button more visible
                btnBack.setBackgroundColor(0xFFFFFFFF); // White background
                btnBack.setPadding(8, 8, 8, 8);
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
            
            // Checkbox listeners
            if (checkboxWhiteboard != null) {
                checkboxWhiteboard.setOnCheckedChangeListener((buttonView, isChecked) -> updateTotalPrice());
            }
            
            if (checkboxTV != null) {
                checkboxTV.setOnCheckedChangeListener((buttonView, isChecked) -> updateTotalPrice());
            }
            
            if (checkboxNetwork != null) {
                checkboxNetwork.setOnCheckedChangeListener((buttonView, isChecked) -> updateTotalPrice());
            }
            
            // Add to cart button
            if (btnAddToCart != null) {
                btnAddToCart.setOnClickListener(v -> {
                    addToCart();
                });
            }
            
            // Initialize hour display
            updateHourDisplay();
            updateTotalPrice();
            
>>>>>>> Stashed changes
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi cài đặt sự kiện", Toast.LENGTH_SHORT).show();
        }
    }
<<<<<<< Updated upstream
=======
    
    private void updateHourDisplay() {
        if (tvHours != null) {
            tvHours.setText(String.valueOf(currentHours));
        }
    }
    
    private void updateTotalPrice() {
        try {
            double basePrice = packagePrice * currentHours;
            double addonPrice = 0;
            
            if (checkboxWhiteboard != null && checkboxWhiteboard.isChecked()) {
                addonPrice += WHITEBOARD_PRICE * currentHours;
            }
            
            if (checkboxTV != null && checkboxTV.isChecked()) {
                addonPrice += TV_PRICE * currentHours;
            }
            
            if (checkboxNetwork != null && checkboxNetwork.isChecked()) {
                addonPrice += NETWORK_PRICE * currentHours;
            }
            
            double totalPrice = basePrice + addonPrice;
            
            // Update price display - you might need to add a total price TextView
            if (tvPrice != null) {
                tvPrice.setText(String.format("%.0fk/giờ (Tổng: %.0fk)", packagePrice, totalPrice));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void addToCart() {
        try {
            StringBuilder message = new StringBuilder();
            message.append("Đã thêm vào giỏ hàng:\n");
            message.append(packageName != null ? packageName : "Sản phẩm");
            message.append("\n");
            message.append(currentHours).append(" giờ");
            
            if (checkboxWhiteboard != null && checkboxWhiteboard.isChecked()) {
                message.append("\n+ Bảng trắng");
            }
            
            if (checkboxTV != null && checkboxTV.isChecked()) {
                message.append("\n+ Television");
            }
            
            if (checkboxNetwork != null && checkboxNetwork.isChecked()) {
                message.append("\n+ Network");
            }
            
            // Calculate and show total price
            double totalPrice = calculateTotalPrice();
            message.append(String.format("\nTổng: %.0fk", totalPrice));
            
            Toast.makeText(this, message.toString(), Toast.LENGTH_LONG).show();
            
            // Redirect back to product list after 2 seconds
            findViewById(android.R.id.content).postDelayed(() -> {
                finish(); // Go back to product list
            }, 2000);
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        }
    }
    
    private double calculateTotalPrice() {
        double basePrice = packagePrice * currentHours;
        double addonPrice = 0;
        
        if (checkboxWhiteboard != null && checkboxWhiteboard.isChecked()) {
            addonPrice += WHITEBOARD_PRICE * currentHours;
        }
        
        if (checkboxTV != null && checkboxTV.isChecked()) {
            addonPrice += TV_PRICE * currentHours;
        }
        
        if (checkboxNetwork != null && checkboxNetwork.isChecked()) {
            addonPrice += NETWORK_PRICE * currentHours;
        }
        
        return basePrice + addonPrice;
    }
>>>>>>> Stashed changes
}
