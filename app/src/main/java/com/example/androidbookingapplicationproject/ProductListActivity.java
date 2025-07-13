package com.example.androidbookingapplicationproject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidbookingapplicationproject.adapter.ProductAdapter;
import com.example.androidbookingapplicationproject.db.DatabaseHelper;
import com.example.androidbookingapplicationproject.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    private static final String TAG = "ProductListActivity";
    
    private RecyclerView recyclerViewProducts;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private List<Product> filteredProductList;
    
    private EditText etSearch;
    private Button chipAll, chipSeats, chipTables;
    private Button btnBack, btnCart;
    private LinearLayout layoutEmptyState;
    
    private DatabaseHelper databaseHelper;
    private String currentFilter = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate started");
        setContentView(R.layout.activity_product_list);

        try {
            initViews();
            setupRecyclerView();
            setupListeners();
            loadProductsFromDatabase();
            testBasicFunctionality(); // Add this for debugging
            Log.d(TAG, "onCreate completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Lỗi khởi tạo: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initViews() {
        Log.d(TAG, "initViews started");
        
        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        etSearch = findViewById(R.id.etSearch);
        chipAll = findViewById(R.id.chipAll);
        chipSeats = findViewById(R.id.chipSeats);
        chipTables = findViewById(R.id.chipTables);
        btnBack = findViewById(R.id.btnBack);
        btnCart = findViewById(R.id.btnCart);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        
        // Check if all views are found
        if (recyclerViewProducts == null) Log.e(TAG, "recyclerViewProducts not found");
        if (etSearch == null) Log.e(TAG, "etSearch not found");
        if (chipAll == null) Log.e(TAG, "chipAll not found");
        if (chipSeats == null) Log.e(TAG, "chipSeats not found");
        if (chipTables == null) Log.e(TAG, "chipTables not found");
        if (btnBack == null) Log.e(TAG, "btnBack not found");
        if (btnCart == null) Log.e(TAG, "btnCart not found");
        if (layoutEmptyState == null) Log.e(TAG, "layoutEmptyState not found");
        
        databaseHelper = new DatabaseHelper(this);
        productList = new ArrayList<>();
        filteredProductList = new ArrayList<>();
        
        // Set initial button states
        if (chipAll != null) chipAll.setSelected(true);
        
        Log.d(TAG, "initViews completed");
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(this, filteredProductList);
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProducts.setAdapter(productAdapter);
    }

    private void setupListeners() {
        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Cart button
        btnCart.setOnClickListener(v -> {
            // Navigate to cart
            Toast.makeText(this, "Giỏ hàng (sẽ implement sau)", Toast.LENGTH_SHORT).show();
        });

        // Search functionality
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Filter chips
        chipAll.setOnClickListener(v -> {
            // Reset all button states
            chipAll.setSelected(true);
            chipSeats.setSelected(false);
            chipTables.setSelected(false);
            currentFilter = "all";
            filterProducts();
        });

        chipSeats.setOnClickListener(v -> {
            chipSeats.setSelected(true);
            chipAll.setSelected(false);
            chipTables.setSelected(false);
            currentFilter = "seat";
            filterProducts();
        });

        chipTables.setOnClickListener(v -> {
            chipTables.setSelected(true);
            chipAll.setSelected(false);
            chipSeats.setSelected(false);
            currentFilter = "table";
            filterProducts();
        });
    }

    private void loadProductsFromDatabase() {
        productList.clear();
        
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            dbHelper.createDatabase();
            SQLiteDatabase db = dbHelper.openDatabase();
            
            Cursor cursor = db.rawQuery("SELECT * FROM Packages ORDER BY Type, Capacity", null);
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    try {
                        int packageId = cursor.getInt(cursor.getColumnIndexOrThrow("PackageId"));
                        String name = cursor.getString(cursor.getColumnIndexOrThrow("Name"));
                        int capacity = cursor.getInt(cursor.getColumnIndexOrThrow("Capacity"));
                        String type = cursor.getString(cursor.getColumnIndexOrThrow("Type"));
                        double price = cursor.getDouble(cursor.getColumnIndexOrThrow("Price"));
                        
                        Product product = new Product(packageId, name, capacity, type, price);
                        productList.add(product);
                    } catch (IllegalArgumentException e) {
                        // Skip invalid rows - column not found
                        continue;
                    } catch (Exception e) {
                        // Skip other invalid rows
                        continue;
                    }
                }
                cursor.close();
            }
            
            db.close();
            
            // If no data, add some sample data
            if (productList.isEmpty()) {
                addSampleData();
            }
            
            filterProducts(); // Apply initial filter
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi load dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            // Add sample data if database fails
            addSampleData();
            filterProducts();
        }
    }

    private void addSampleData() {
        // Thêm dữ liệu mẫu khi database trống hoặc lỗi
        productList.clear();
        
        // Gói ghế đơn
        productList.add(new Product(1, "Single Seat Package", 1, "Seat", 50));
        productList.add(new Product(2, "Double Seat Package", 2, "Seat", 90));
        productList.add(new Product(3, "Triple Seat Package", 3, "Seat", 130));
        
        // Gói bàn
        productList.add(new Product(4, "Small Table Package", 4, "Table", 200));
        productList.add(new Product(5, "Medium Table Package", 6, "Table", 280));
        productList.add(new Product(6, "Large Table Package", 8, "Table", 350));
        productList.add(new Product(7, "Extra Large Table Package", 10, "Table", 450));
        
        Toast.makeText(this, "Đã tải dữ liệu mẫu", Toast.LENGTH_SHORT).show();
    }
    
    // Test method to check basic functionality
    private void testBasicFunctionality() {
        Log.d(TAG, "Testing basic functionality");
        
        // Test data creation
        try {
            Product testProduct = new Product(1, "Test Product", 4, "Table", 100.0);
            Log.d(TAG, "Product created: " + testProduct.getName());
        } catch (Exception e) {
            Log.e(TAG, "Error creating test product", e);
        }
        
        // Test adapter
        try {
            if (productAdapter != null) {
                Log.d(TAG, "Adapter exists, item count: " + productAdapter.getItemCount());
            } else {
                Log.e(TAG, "Adapter is null");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error testing adapter", e);
        }
    }

    private void filterProducts() {
        filteredProductList.clear();
        String searchText = etSearch.getText().toString().toLowerCase().trim();
        
        for (Product product : productList) {
            boolean matchesSearch = searchText.isEmpty() || 
                product.getName().toLowerCase().contains(searchText) ||
                product.getDescription().toLowerCase().contains(searchText);
                
            boolean matchesFilter = currentFilter.equals("all") ||
                (currentFilter.equals("seat") && product.getType().equals("Seat")) ||
                (currentFilter.equals("table") && product.getType().equals("Table"));
                
            if (matchesSearch && matchesFilter) {
                filteredProductList.add(product);
            }
        }
        
        // Update UI
        if (filteredProductList.isEmpty()) {
            recyclerViewProducts.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerViewProducts.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
        }
        
        productAdapter.updateData(filteredProductList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning from detail screen
        loadProductsFromDatabase();
    }
}
