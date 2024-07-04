package com.example.coffee_shop;

public class CartItem {
    public String getproduct;
    private String name;
    private double price;
    private int quality;
    private String image;

    public CartItem(String name, int quality, double price) {
        this.name = name;
        this.price = price;
        this.quality = quality;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

}
