package com.example.androidbookingapplicationproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.cardview.widget.CardView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Nút đăng xuất
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Mở chức năng quản lý gói
        CardView cardManagePackages = findViewById(R.id.cardManagePackages);
        cardManagePackages.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ManagePackagesActivity.class);
            startActivity(intent);
        });

        CardView cardFacilities = findViewById(R.id.cardManageFacilities);
        cardFacilities.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ManageFacilitiesActivity.class);
            startActivity(intent);
        });

    }
}
