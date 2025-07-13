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

public class ProductAdapterSimple extends RecyclerView.Adapter<ProductAdapterSimple.ProductViewHolder> {

    private static final String TAG = "ProductAdapterSimple";
    private Context context;
    private List<Product> productList;

    public ProductAdapterSimple(Context context, List<Product> productList) {
        Log.d(TAG, "ProductAdapterSimple constructor called");
        this.context = context;
        this.productList = productList;
        Log.d(TAG, "ProductAdapterSimple created with " + (productList != null ? productList.size() : 0) + " items");
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder called");
        try {
            View view = LayoutInflater.from(context).inflate(R.layout.item_product_simple, parent, false);
            return new ProductViewHolder(view);
        } catch (Exception e) {
            Log.e(TAG, "Error creating view holder", e);
            // Fallback to simple view if layout fails
            TextView textView = new TextView(context);
            textView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 
                ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setPadding(16, 16, 16, 16);
            return new ProductViewHolder(textView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder called for position: " + position);
        
        if (position < 0 || position >= productList.size()) {
            Log.w(TAG, "Invalid position: " + position);
            return;
        }
        
        Product product = productList.get(position);
        if (product == null) {
            Log.w(TAG, "Product is null at position: " + position);
            return;
        }
        
        try {
            // Set data to views with null checks
            if (holder.tvIcon != null) {
                holder.tvIcon.setText(product.getIcon() != null ? product.getIcon() : "📦");
            }
            if (holder.tvPackageName != null) {
                holder.tvPackageName.setText(product.getName() != null ? product.getName() : "Unknown Package");
            }
            if (holder.tvDescription != null) {
                holder.tvDescription.setText(product.getDescription() != null ? product.getDescription() : "No description");
            }
            if (holder.tvType != null) {
                holder.tvType.setText(product.getType() != null ? product.getType().toUpperCase() : "UNKNOWN");
            }
            if (holder.tvPrice != null) {
                holder.tvPrice.setText(product.getFormattedPrice() != null ? product.getFormattedPrice() : "0k");
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
                        Log.e(TAG, "Both detail activities failed", ex);
                    }
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error binding data", e);
        }
    }

    @Override
    public int getItemCount() {
        int count = productList != null ? productList.size() : 0;
        Log.d(TAG, "getItemCount: " + count);
        return count;
    }

    public void updateData(List<Product> newProductList) {
        Log.d(TAG, "updateData called with " + (newProductList != null ? newProductList.size() : 0) + " items");
        this.productList = newProductList;
        notifyDataSetChanged();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvIcon, tvPackageName, tvDescription, tvType, tvPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            try {
                tvIcon = itemView.findViewById(R.id.tvIcon);
                tvPackageName = itemView.findViewById(R.id.tvPackageName);
                tvDescription = itemView.findViewById(R.id.tvDescription);
                tvType = itemView.findViewById(R.id.tvType);
                tvPrice = itemView.findViewById(R.id.tvPrice);
            } catch (Exception e) {
                Log.e("ProductViewHolder", "Error finding views", e);
            }
        }
    }
}
