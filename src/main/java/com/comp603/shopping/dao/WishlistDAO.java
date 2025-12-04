package com.comp603.shopping.dao;

import com.comp603.shopping.config.DBManager;
import com.comp603.shopping.models.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WishlistDAO {

    public boolean addToWishlist(int userId, int productId) {
        if (isInWishlist(userId, productId)) {
            return false; // Already exists
        }
        String sql = "INSERT INTO WISHLIST (USER_ID, PRODUCT_ID) VALUES (?, ?)";
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

    public boolean removeFromWishlist(int userId, int productId) {
        String sql = "DELETE FROM WISHLIST WHERE USER_ID = ? AND PRODUCT_ID = ?";
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

    public boolean isInWishlist(int userId, int productId) {
        String sql = "SELECT 1 FROM WISHLIST WHERE USER_ID = ? AND PRODUCT_ID = ?";
        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, productId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Product> getWishlist(int userId) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.* FROM WISHLIST w JOIN PRODUCTS p ON w.PRODUCT_ID = p.PRODUCT_ID WHERE w.USER_ID = ?";

        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("PRODUCT_ID"),
                        rs.getString("NAME"),
                        rs.getString("DESCRIPTION"),
                        rs.getDouble("PRICE"),
                        rs.getInt("STOCK_QUANTITY"),
                        rs.getString("IMAGE_PATH"),
                        rs.getString("CATEGORY"),
                        rs.getInt("SALES_VOLUME"));
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
}
