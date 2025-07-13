package com.example.androidbookingapplicationproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidbookingapplicationproject.adapter.ProductAdapterSimple;
import com.example.androidbookingapplicationproject.model.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Backup simple version of ProductListActivity
 * Use this if the main activity crashes
 */
public class ProductListActivityBackup extends AppCompatActivity {

    private static final String TAG = "ProductListBackup";
    private RecyclerView recyclerView;
    private ProductAdapterSimple adapter;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate started");
        
        try {
            // Use simple layout
            setContentView(R.layout.activity_product_list_simple);
            
            initViews();
            createSampleData();
            setupRecyclerView();
            
            Toast.makeText(this, "Đã tải thành công với " + productList.size() + " sản phẩm", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Backup activity created successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error in backup activity", e);
            Toast.makeText(this, "Lỗi backup: " + e.getMessage(), Toast.LENGTH_LONG).show();
            
            // Create a really minimal fallback
            createMinimalFallback();
        }
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewProducts);
        Button btnBack = findViewById(R.id.btnBack);
        
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                Log.d(TAG, "Back button clicked");
                finish();
            });
        }
        
        productList = new ArrayList<>();
    }

    private void createSampleData() {
        productList.clear();
        
        // Simple test data
        productList.add(new Product(1, "Ghế Đơn", 1, "Seat", 50));
        productList.add(new Product(2, "Ghế Đôi", 2, "Seat", 90));
        productList.add(new Product(3, "Bàn Nhỏ", 4, "Table", 200));
        productList.add(new Product(4, "Bàn Lớn", 8, "Table", 350));
        
        Log.d(TAG, "Sample data created: " + productList.size() + " items");
    }

    private void setupRecyclerView() {
        if (recyclerView != null) {
            try {
                adapter = new ProductAdapterSimple(this, productList);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(adapter);
                Log.d(TAG, "RecyclerView setup complete");
            } catch (Exception e) {
                Log.e(TAG, "Error setting up RecyclerView", e);
                throw e;
            }
        } else {
            Log.e(TAG, "RecyclerView is null");
            throw new RuntimeException("RecyclerView not found");
        }
    }
    
    private void createMinimalFallback() {
        Log.d(TAG, "Creating minimal fallback");
        try {
            // Create a simple text-based list
            setContentView(android.R.layout.simple_list_item_1);
            Toast.makeText(this, "Chế độ tối giản - Vui lòng quay lại sau", Toast.LENGTH_LONG).show();
            
            // Auto finish after 3 seconds
            findViewById(android.R.id.content).postDelayed(() -> finish(), 3000);
            
        } catch (Exception e) {
            Log.e(TAG, "Even minimal fallback failed", e);
            finish();
        }
    }
}
