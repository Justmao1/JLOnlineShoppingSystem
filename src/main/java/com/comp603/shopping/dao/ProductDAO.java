package com.comp603.shopping.dao;

import com.comp603.shopping.config.DBManager;
import com.comp603.shopping.models.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM PRODUCTS";

        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

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

    public boolean updateStock(int productId, int newQuantity) {
        String sql = "UPDATE PRODUCTS SET STOCK_QUANTITY = ? WHERE PRODUCT_ID = ?";
        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, newQuantity);
            pstmt.setInt(2, productId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean decreaseStock(int productId, int quantity) {
        String sql = "UPDATE PRODUCTS SET STOCK_QUANTITY = STOCK_QUANTITY - ?, SALES_VOLUME = SALES_VOLUME + ? WHERE PRODUCT_ID = ? AND STOCK_QUANTITY >= ?";
        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, quantity);
            pstmt.setInt(2, quantity); // Increase sales volume
            pstmt.setInt(3, productId);
            pstmt.setInt(4, quantity);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Product> searchProducts(String keyword) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM PRODUCTS WHERE LOWER(NAME) LIKE ? OR LOWER(CATEGORY) LIKE ?";

        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String search = "%" + keyword.toLowerCase() + "%";
            pstmt.setString(1, search);
            pstmt.setString(2, search);
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

    public boolean addProduct(Product product) {
        String sql = "INSERT INTO PRODUCTS (NAME, DESCRIPTION, PRICE, STOCK_QUANTITY, CATEGORY, SALES_VOLUME, IMAGE_PATH) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getDescription());
            pstmt.setDouble(3, product.getPrice());
            pstmt.setInt(4, product.getStockQuantity());
            pstmt.setString(5, product.getCategory());
            pstmt.setInt(6, product.getSalesVolume());
            pstmt.setString(7, product.getImagePath());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateProduct(Product product) {
        String sql = "UPDATE PRODUCTS SET NAME = ?, DESCRIPTION = ?, PRICE = ?, STOCK_QUANTITY = ?, CATEGORY = ?, IMAGE_PATH = ? WHERE PRODUCT_ID = ?";
        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getDescription());
            pstmt.setDouble(3, product.getPrice());
            pstmt.setInt(4, product.getStockQuantity());
            pstmt.setString(5, product.getCategory());
            pstmt.setString(6, product.getImagePath());
            pstmt.setInt(7, product.getProductId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteProduct(int productId) {
        String sql = "DELETE FROM PRODUCTS WHERE PRODUCT_ID = ?";
        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, productId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
