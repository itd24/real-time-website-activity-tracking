package com.example.shop.service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.shop.model.Product;
import com.example.shop.repository.Products;

public class ShoppingCart {

    private static final ShoppingCart instance = new ShoppingCart();

    // Map to store productId and quantity
    private final Map<Long, Integer> cartItems = new HashMap<>();

    // Private constructor to prevent external instantiation
    private ShoppingCart() {
    }

    // Static method to get the singleton instance
    public static ShoppingCart getInstance() {
        return instance;
    }

    // Add item to the cart
    public void addItem(Long productId, int quantity) {
        cartItems.merge(productId, quantity, Integer::sum); // Adds to existing quantity
    }

    // Remove item from the cart
    public void removeItem(Long productId) {
        cartItems.remove(productId);
    }

    // Get all cart items
    public Map<Product, Integer> getCartItems() {        
        return cartItems.entrySet().stream()
            .collect(Collectors.toMap(
                entry -> Products.getProductById(entry.getKey()), // Fetch product by ID
                Map.Entry::getValue // Quantity
            ));
    }

    public int getCartAmount() {
        return cartItems.values().stream().mapToInt(Integer::intValue).sum();
    }

    // Clear cart
    public void clearCart() {
        cartItems.clear();
    }
}
