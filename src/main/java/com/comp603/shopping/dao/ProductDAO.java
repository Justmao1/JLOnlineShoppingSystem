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

public class ProductDAO {

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM PRODUCTS";

        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String type = rs.getString("TYPE");
                Product product;

                if ("PHYSICAL".equalsIgnoreCase(type)) {
                    product = new PhysicalProduct(
                            rs.getInt("PRODUCT_ID"),
                            rs.getString("NAME"),
                            rs.getString("DESCRIPTION"),
                            rs.getDouble("PRICE"),
                            rs.getInt("STOCK_QUANTITY"),
                            rs.getDouble("WEIGHT"),
                            rs.getString("IMAGE_PATH"));
                } else {
                    product = new DigitalProduct(
                            rs.getInt("PRODUCT_ID"),
                            rs.getString("NAME"),
                            rs.getString("DESCRIPTION"),
                            rs.getDouble("PRICE"),
                            rs.getInt("STOCK_QUANTITY"),
                            rs.getString("DOWNLOAD_LINK"),
                            rs.getString("IMAGE_PATH"));
                }
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
        String sql = "UPDATE PRODUCTS SET STOCK_QUANTITY = STOCK_QUANTITY - ? WHERE PRODUCT_ID = ? AND STOCK_QUANTITY >= ?";
        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, quantity);
            pstmt.setInt(2, productId);
            pstmt.setInt(3, quantity);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Product> searchProducts(String keyword) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM PRODUCTS WHERE LOWER(NAME) LIKE ?";

        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword.toLowerCase() + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String type = rs.getString("TYPE");
                Product product;

                if ("PHYSICAL".equalsIgnoreCase(type)) {
                    product = new PhysicalProduct(
                            rs.getInt("PRODUCT_ID"),
                            rs.getString("NAME"),
                            rs.getString("DESCRIPTION"),
                            rs.getDouble("PRICE"),
                            rs.getInt("STOCK_QUANTITY"),
                            rs.getDouble("WEIGHT"),
                            rs.getString("IMAGE_PATH"));
                } else {
                    product = new DigitalProduct(
                            rs.getInt("PRODUCT_ID"),
                            rs.getString("NAME"),
                            rs.getString("DESCRIPTION"),
                            rs.getDouble("PRICE"),
                            rs.getInt("STOCK_QUANTITY"),
                            rs.getString("DOWNLOAD_LINK"),
                            rs.getString("IMAGE_PATH"));
                }
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public boolean addProduct(Product product) {
        String sql = "INSERT INTO PRODUCTS (NAME, DESCRIPTION, PRICE, STOCK_QUANTITY, TYPE, WEIGHT, DOWNLOAD_LINK, IMAGE_PATH) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getDescription());
            pstmt.setDouble(3, product.getPrice());
            pstmt.setInt(4, product.getStockQuantity());

            if (product instanceof PhysicalProduct) {
                pstmt.setString(5, "PHYSICAL");
                pstmt.setDouble(6, ((PhysicalProduct) product).getWeight());
                pstmt.setString(7, null);
            } else {
                pstmt.setString(5, "DIGITAL");
                pstmt.setObject(6, null);
                pstmt.setString(7, ((DigitalProduct) product).getDownloadLink());
            }
            pstmt.setString(8, product.getImagePath());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateProduct(Product product) {
        String sql = "UPDATE PRODUCTS SET NAME = ?, DESCRIPTION = ?, PRICE = ?, STOCK_QUANTITY = ?, WEIGHT = ?, DOWNLOAD_LINK = ?, IMAGE_PATH = ? WHERE PRODUCT_ID = ?";
        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getDescription());
            pstmt.setDouble(3, product.getPrice());
            pstmt.setInt(4, product.getStockQuantity());

            if (product instanceof PhysicalProduct) {
                pstmt.setDouble(5, ((PhysicalProduct) product).getWeight());
                pstmt.setString(6, null);
            } else {
                pstmt.setObject(5, null);
                pstmt.setString(6, ((DigitalProduct) product).getDownloadLink());
            }
            pstmt.setString(7, product.getImagePath());
            pstmt.setInt(8, product.getProductId());

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
