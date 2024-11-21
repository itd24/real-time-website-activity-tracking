package com.example.shop.model;

public class Product {
    private Long id;
    private String name;
    private String image;
    private double price;
    private double originalPrice;
    private int stars;

    public Product(Long id, String name, String image, double price, double originalPrice, int stars) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.image = image;
        this.originalPrice = originalPrice;
        this.stars = stars;
    }

    // Getters and setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getImage() { return image; }
    public double getPrice() { return price; }
    public double getOriginalPrice() { return originalPrice; }
    public int getStars() { return (stars > 5) ? 5 : stars; }

    public Boolean isOnSale() { return price < originalPrice; }
    public Boolean showStars() { return stars > 0; }
}