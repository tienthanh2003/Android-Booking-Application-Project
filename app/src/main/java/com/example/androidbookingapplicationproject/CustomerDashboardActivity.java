package com.example.androidbookingapplicationproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class CustomerDashboardActivity extends AppCompatActivity {

    private Button btnLogout;
    TextView tvCustomerName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_dashboard); // nhớ đặt đúng layout
        tvCustomerName = findViewById(R.id.tvCustomerName);

        TextView tvCustomerName = findViewById(R.id.tvCustomerName);
        String name = getIntent().getStringExtra("userName");
        if (name != null) {
            tvCustomerName.setText(name);
        }

        btnLogout = findViewById(R.id.btnLogout);
        CardView cardMap = findViewById(R.id.cardMap);
        cardMap.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerDashboardActivity.this, MapActivity.class);
            startActivity(intent);
        });


        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại màn hình đăng nhập
                Intent intent = new Intent(CustomerDashboardActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Xóa back stack
                startActivity(intent);
            }
        });

        CardView cardCart = findViewById(R.id.cardCart);
        cardCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerDashboardActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });
    }
}
