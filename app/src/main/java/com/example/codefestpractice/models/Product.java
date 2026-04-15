package com.example.codefestpractice.models;

public class Product {

    private String productName;
    private double price;

    private int imageResource;

    public Product(String productName, double price, int imageResource) {
        this.productName = productName;
        this.price = price;
        this.imageResource = imageResource;
    }

    public String getProductName() {
        return productName;
    }

    public double getPrice() {
        return price;
    }

    public int getImageResource() {
        return imageResource;
    }

}
