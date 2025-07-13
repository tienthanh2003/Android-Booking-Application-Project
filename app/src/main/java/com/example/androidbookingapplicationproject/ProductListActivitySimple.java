package com.example.androidbookingapplicationproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidbookingapplicationproject.adapter.ProductAdapter;
import com.example.androidbookingapplicationproject.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivitySimple extends AppCompatActivity {

    private static final String TAG = "ProductListSimple";
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate started");
        
        try {
            // Set a simple layout first
            setContentView(R.layout.activity_product_list);
            
            initViews();
            createSampleData();
            setupRecyclerView();
            
            Log.d(TAG, "Simple activity created successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error in simple activity", e);
            Toast.makeText(this, "Lỗi khởi tạo đơn giản: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewProducts);
        Button btnBack = findViewById(R.id.btnBack);
        
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
        
        productList = new ArrayList<>();
    }

    private void createSampleData() {
        productList.clear();
        productList.add(new Product(1, "Single Seat", 1, "Seat", 50));
        productList.add(new Product(2, "Double Seat", 2, "Seat", 90));
        productList.add(new Product(3, "Small Table", 4, "Table", 200));
        productList.add(new Product(4, "Large Table", 8, "Table", 350));
        
        Log.d(TAG, "Sample data created: " + productList.size() + " items");
    }

    private void setupRecyclerView() {
        if (recyclerView != null) {
            adapter = new ProductAdapter(this, productList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
            Log.d(TAG, "RecyclerView setup complete");
        } else {
            Log.e(TAG, "RecyclerView is null");
        }
    }
}
