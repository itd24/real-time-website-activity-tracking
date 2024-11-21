package com.example.shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.shop.model.Product;
import com.example.shop.repository.Products;
import com.example.shop.service.ShoppingCart;

import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;
import java.util.Map;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/")
public class ShopController {

    @GetMapping("/list")
    public String showProductList(@RequestParam(value = "searchkeyword", required = false) String searchKeyword,
            Model model) {
        List<Product> products;
        if (searchKeyword != null)
            products = Products.searchProducts(searchKeyword);
        else
            products = Products.getAllProducts();
        model.addAttribute("products", products);
        model.addAttribute("searchKeyword", searchKeyword);
        model.addAttribute("cartAmount", ShoppingCart.getInstance().getCartAmount());
        return "product-list";
    }

    @GetMapping("/detail/{productId}")
    public String showProductDetail(@PathVariable("productId") Long productId, Model model) {
        // Fetch product details by ID
        Product product = Products.getProductById(productId);
        model.addAttribute("product", product);
        model.addAttribute("cartAmount", ShoppingCart.getInstance().getCartAmount());
        return "product-detail";
    }

    @GetMapping("/shopping-cart")
    public String updateShoppingCart(@RequestParam(value = "addProduct", required = false) Long productId,
            @RequestParam(value = "quantity", required = false, defaultValue = "1") int quantity,
            Model model) {
        // Add the product to the shopping cart
        if (productId != null) {
            ShoppingCart.getInstance().addItem(productId, quantity);
        }

        // Get cart items with quantities
        Map<Product, Integer> cart = ShoppingCart.getInstance().getCartItems();
        model.addAttribute("cart", cart);

        // Calculate total price
        double totalPrice = cart.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                .sum();
        model.addAttribute("totalPrice", totalPrice);

        model.addAttribute("cartAmount", ShoppingCart.getInstance().getCartAmount());
        return "shopping-cart";
    }
}