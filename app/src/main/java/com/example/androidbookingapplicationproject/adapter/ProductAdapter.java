package com.example.androidbookingapplicationproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidbookingapplicationproject.ProductDetailActivity;
import com.example.androidbookingapplicationproject.ProductDetailEnhancedActivity;
import com.example.androidbookingapplicationproject.R;
import com.example.androidbookingapplicationproject.model.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private static final String TAG = "ProductAdapter";
    private Context context;
    private List<Product> productList;

    public ProductAdapter(Context context, List<Product> productList) {
        Log.d(TAG, "ProductAdapter constructor called");
        this.context = context;
        this.productList = productList;
        Log.d(TAG, "ProductAdapter created with " + (productList != null ? productList.size() : 0) + " items");
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        if (position < 0 || position >= productList.size()) {
            return;
        }
        
        Product product = productList.get(position);
        if (product == null) {
            return;
        }
        
        // Set data to views with null checks
        holder.tvIcon.setText(product.getIcon() != null ? product.getIcon() : "📦");
        holder.tvPackageName.setText(product.getName() != null ? product.getName() : "Unknown Package");
        holder.tvDescription.setText(product.getDescription() != null ? product.getDescription() : "No description");
        holder.tvType.setText(product.getType() != null ? product.getType().toUpperCase() : "UNKNOWN");
        holder.tvPrice.setText(product.getFormattedPrice() != null ? product.getFormattedPrice() : "0k");

        // Set type badge color
        try {
            if (product.getType().equals("Seat")) {
                holder.tvType.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_dark));
            } else {
                holder.tvType.setBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_dark));
            }
        } catch (Exception e) {
            // Fallback to default colors if there's an issue
            holder.tvType.setBackgroundColor(0xFF3F51B5);
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(context, ProductDetailEnhancedActivity.class);
                intent.putExtra("PACKAGE_ID", product.getPackageId());
                intent.putExtra("PACKAGE_NAME", product.getName());
                intent.putExtra("PACKAGE_CAPACITY", product.getCapacity());
                intent.putExtra("PACKAGE_TYPE", product.getType());
                intent.putExtra("PACKAGE_PRICE", product.getPrice());
                context.startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Error starting enhanced activity, trying fallback", e);
                // Fallback to original detail activity
                try {
                    Intent intent = new Intent(context, ProductDetailActivity.class);
                    intent.putExtra("PACKAGE_ID", product.getPackageId());
                    intent.putExtra("PACKAGE_NAME", product.getName());
                    intent.putExtra("PACKAGE_CAPACITY", product.getCapacity());
                    intent.putExtra("PACKAGE_TYPE", product.getType());
                    intent.putExtra("PACKAGE_PRICE", product.getPrice());
                    context.startActivity(intent);
                } catch (Exception ex) {
                    Log.e(TAG, "Both activities failed", ex);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // Method to update data
    public void updateData(List<Product> newProductList) {
        this.productList = newProductList;
        notifyDataSetChanged();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvIcon, tvPackageName, tvDescription, tvType, tvPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIcon = itemView.findViewById(R.id.tvIcon);
            tvPackageName = itemView.findViewById(R.id.tvPackageName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvType = itemView.findViewById(R.id.tvType);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}
