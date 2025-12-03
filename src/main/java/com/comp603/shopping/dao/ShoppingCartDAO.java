package com.comp603.shopping.dao;

import com.comp603.shopping.config.DBManager;
import com.comp603.shopping.models.DigitalProduct;
import com.comp603.shopping.models.PhysicalProduct;
import com.comp603.shopping.models.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.comp603.shopping.models.CartItem;

public class ShoppingCartDAO {

    public boolean addToCart(int userId, int productId, int quantity) {
        // Check if item already exists
        String checkSql = "SELECT QUANTITY FROM SHOPPING_CART WHERE USER_ID = ? AND PRODUCT_ID = ?";
        String updateSql = "UPDATE SHOPPING_CART SET QUANTITY = QUANTITY + ? WHERE USER_ID = ? AND PRODUCT_ID = ?";
        String insertSql = "INSERT INTO SHOPPING_CART (USER_ID, PRODUCT_ID, QUANTITY) VALUES (?, ?, ?)";

        try (Connection conn = DBManager.getConnection()) {
            // Check existence
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, userId);
                checkStmt.setInt(2, productId);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    // Update existing
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, quantity);
                        updateStmt.setInt(2, userId);
                        updateStmt.setInt(3, productId);
                        return updateStmt.executeUpdate() > 0;
                    }
                } else {
                    // Insert new
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setInt(1, userId);
                        insertStmt.setInt(2, productId);
                        insertStmt.setInt(3, quantity);
                        return insertStmt.executeUpdate() > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<CartItem> getCartItems(int userId) {
        List<CartItem> items = new ArrayList<>();
        String sql = "SELECT p.*, sc.QUANTITY as CART_QUANTITY FROM SHOPPING_CART sc " +
                "JOIN PRODUCTS p ON sc.PRODUCT_ID = p.PRODUCT_ID " +
                "WHERE sc.USER_ID = ?";

        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String type = rs.getString("TYPE");
                Product product;
                int quantity = rs.getInt("CART_QUANTITY");

                if ("PHYSICAL".equalsIgnoreCase(type)) {
                    product = new PhysicalProduct(
                            rs.getInt("PRODUCT_ID"),
                            rs.getString("NAME"),
                            rs.getString("DESCRIPTION"),
                            rs.getDouble("PRICE"),
                            rs.getInt("STOCK_QUANTITY"), // This is stock
                            rs.getDouble("WEIGHT"),
                            rs.getString("IMAGE_PATH"));
                } else {
                    product = new DigitalProduct(
                            rs.getInt("PRODUCT_ID"),
                            rs.getString("NAME"),
                            rs.getString("DESCRIPTION"),
                            rs.getDouble("PRICE"),
                            rs.getInt("STOCK_QUANTITY"), // This is stock
                            rs.getString("DOWNLOAD_LINK"),
                            rs.getString("IMAGE_PATH"));
                }
                items.add(new CartItem(product, quantity));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public boolean updateQuantity(int userId, int productId, int newQuantity) {
        String sql = "UPDATE SHOPPING_CART SET QUANTITY = ? WHERE USER_ID = ? AND PRODUCT_ID = ?";
        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newQuantity);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, productId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeFromCart(int userId, int productId) {
        String sql = "DELETE FROM SHOPPING_CART WHERE USER_ID = ? AND PRODUCT_ID = ?";
        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, productId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean clearCart(int userId) {
        String sql = "DELETE FROM SHOPPING_CART WHERE USER_ID = ?";
        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
