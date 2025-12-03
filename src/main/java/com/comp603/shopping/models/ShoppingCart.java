package com.comp603.shopping.models;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private List<CartItem> items;
    private int userId;
    private com.comp603.shopping.dao.ShoppingCartDAO cartDAO;

    public ShoppingCart() {
        this.items = new ArrayList<>();
        this.cartDAO = new com.comp603.shopping.dao.ShoppingCartDAO();
    }

    public void setUserId(int userId) {
        this.userId = userId;
        loadItemsFromDB();
    }

    private void loadItemsFromDB() {
        if (userId > 0) {
            this.items = cartDAO.getCartItems(userId);
        }
    }

    public void addProduct(Product product) {
        if (userId > 0) {
            cartDAO.addToCart(userId, product.getProductId(), 1);
            loadItemsFromDB(); // Reload to get fresh state
        } else {
            // Fallback for guest/testing
            for (CartItem item : items) {
                if (item.getProduct().getProductId() == product.getProductId()) {
                    item.setQuantity(item.getQuantity() + 1);
                    return;
                }
            }
            items.add(new CartItem(product, 1));
        }
    }

    public void removeProduct(Product product) {
        if (userId > 0) {
            cartDAO.removeFromCart(userId, product.getProductId());
            loadItemsFromDB();
        } else {
            items.removeIf(item -> item.getProduct().getProductId() == product.getProductId());
        }
    }

    public List<CartItem> getItems() {
        if (userId > 0) {
            loadItemsFromDB(); // Ensure fresh data
        }
        return items;
    }

    public double getTotal() {
        return items.stream().mapToDouble(CartItem::getTotalPrice).sum();
    }

    public void clear() {
        if (userId > 0) {
            cartDAO.clearCart(userId);
            loadItemsFromDB();
        } else {
            items.clear();
        }
    }
}
