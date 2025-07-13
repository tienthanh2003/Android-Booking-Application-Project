package com.example.androidbookingapplicationproject.model;

public class Product {
    private int packageId;
    private String name;
    private int capacity;
    private String type;
    private double price;

    // Constructor
    public Product(int packageId, String name, int capacity, String type, double price) {
        this.packageId = packageId;
        this.name = name;
        this.capacity = capacity;
        this.type = type;
        this.price = price;
    }

    // Getters and Setters
    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // Helper method để format giá tiền
    public String getFormattedPrice() {
        return String.format("%.0fk", price);
    }

    // Helper method để tạo description
    public String getDescription() {
        if (type.equals("Seat")) {
            return capacity + " chỗ ngồi";
        } else {
            if (capacity <= 4) {
                return capacity + " chỗ + 1 bàn";
            } else if (capacity <= 6) {
                return capacity + " chỗ + 1 bàn";
            } else {
                return capacity + " chỗ + 2 bàn";
            }
        }
    }

    // Helper method để lấy icon
    public String getIcon() {
        return type.equals("Seat") ? "🪑" : "🗂️";
    }
}
