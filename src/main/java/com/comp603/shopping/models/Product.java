package com.comp603.shopping.models;

/**
 * Abstract base class for all products.
 * Demonstrates Abstraction and Inheritance.
 */
public class Product {
    private int productId;
    private String name;
    private String description;
    private double price;
    private int stockQuantity;
    private String imagePath;
    private String category;
    private int salesVolume;

    public Product() {
    }

    public Product(int productId, String name, String description, double price, int stockQuantity, String imagePath,
            String category, int salesVolume) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.imagePath = imagePath;
        this.category = category;
        this.salesVolume = salesVolume;
    }

    public String getDisplayInfo() {
        return String.format("%s (%s) - $%.2f", name, category, price);
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getSalesVolume() {
        return salesVolume;
    }

    public void setSalesVolume(int salesVolume) {
        this.salesVolume = salesVolume;
    }
}
