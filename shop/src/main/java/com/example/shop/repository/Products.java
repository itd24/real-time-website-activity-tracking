package com.example.shop.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import com.example.shop.model.Product;

public class Products {

    // Static list of products
    private static final List<Product> products = new ArrayList<>();

    // Private constructor to prevent instantiation
    private Products() {
    }

    // Static block to initialize the repository with fake data
    static {
        products.add(new Product(1L, "Camera 1", "camera.jpg", 599.99, 0, 5 ));
        products.add(new Product(2L, "Camera 2", "camera2.jpg", 489.99, 899.99, 4 ));
        products.add(new Product(3L, "Camera 3", "camera3.jpg", 190.00, 0, 3 ));
        products.add(new Product(4L, "Camera 4", "camera4.jpg", 658.99, 0, 5 ));
        products.add(new Product(5L, "Camera 5", "camera5.jpg", 764.50, 955.66, 2 ));
        products.add(new Product(6L, "Camera 6", "camera6.jpg", 123.49, 0, 1 ));
        products.add(new Product(7L, "Camera 7", "camera7.jpg", 321.75, 777.77, 5 ));
        products.add(new Product(8L, "Camera 8", "camera8.jpg", 429.33, 0, 3 ));
        products.add(new Product(9L, "Camera 9", "camera9.jpg", 399.66, 0, 4 ));
        products.add(new Product(10L, "Lens 1", "lens.jpg", 55.20, 0, 4 ));
        products.add(new Product(11L, "Lens 2", "lens2.jpg", 80.99, 0, 3 ));
        products.add(new Product(12L, "Lens 3", "lens3.jpg", 99.34, 0, 5 ));
        products.add(new Product(13L, "Lens 4", "lens4.jpg", 33.78, 57.88, 2 ));
        products.add(new Product(14L, "Lens 5", "lens5.jpg", 201.99, 0, 5 ));
        products.add(new Product(15L, "Lens 6", "lens6.jpg", 105.56, 0, 5 ));
    }

    // Method to get all products
    public static List<Product> getAllProducts() {
        return new ArrayList<>(products); // Return a copy to avoid external modifications
    }

    public static Product getProductById(long productId) {
        Optional<Product> result = products.stream()
                                           .filter(product -> product.getId() == productId)
                                           .findFirst();
        if (result.isPresent()) {
            return result.get();
        } else {
            return null;
        }
    }

    // Method to search for products by keyword
    public static List<Product> searchProducts(String keyword) {
        return products.stream()
                .filter(product -> product.getName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    public static List<Product> getProductsByIds(List<Map.Entry<String, Integer>> popularProducts) {
        List<Product> result = new ArrayList<Product>();
        for (Map.Entry<String, Integer> productScore : popularProducts) {
            result.add(getProductById(Integer.parseInt(productScore.getKey())));
        }
        return result;
    }
}