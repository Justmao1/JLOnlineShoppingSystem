package com.comp603.shopping.dao;

import com.comp603.shopping.config.DBManager;
import com.comp603.shopping.models.Order;
import com.comp603.shopping.models.OrderItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OrderDAO {

    public boolean createOrder(Order order) {
        Connection conn = null;
        PreparedStatement orderStmt = null;
        PreparedStatement itemStmt = null;
        ResultSet generatedKeys = null;

        String orderSql = "INSERT INTO ORDERS (USER_ID, TOTAL_AMOUNT, STATUS) VALUES (?, ?, ?)";
        String itemSql = "INSERT INTO ORDER_ITEMS (ORDER_ID, PRODUCT_ID, QUANTITY, PRICE_AT_PURCHASE) VALUES (?, ?, ?, ?)";

        try {
            conn = DBManager.getConnection();
            conn.setAutoCommit(false); // Start Transaction

            // 1. Insert Order
            orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            orderStmt.setInt(1, order.getUserId());
            orderStmt.setDouble(2, order.getTotalAmount());
            orderStmt.setString(3, order.getStatus());

            int affectedRows = orderStmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating order failed, no rows affected.");
            }

            generatedKeys = orderStmt.getGeneratedKeys();
            int orderId;
            if (generatedKeys.next()) {
                orderId = generatedKeys.getInt(1);
                order.setOrderId(orderId);
            } else {
                throw new SQLException("Creating order failed, no ID obtained.");
            }

            // 2. Insert Order Items
            itemStmt = conn.prepareStatement(itemSql);
            for (OrderItem item : order.getItems()) {
                itemStmt.setInt(1, orderId);
                itemStmt.setInt(2, item.getProductId());
                itemStmt.setInt(3, item.getQuantity());
                itemStmt.setDouble(4, item.getPriceAtPurchase());
                itemStmt.addBatch();
            }
            itemStmt.executeBatch();

            conn.commit(); // Commit Transaction
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            try {
                if (generatedKeys != null)
                    generatedKeys.close();
                if (orderStmt != null)
                    orderStmt.close();
                if (itemStmt != null)
                    itemStmt.close();
                if (conn != null)
                    conn.setAutoCommit(true); // Reset auto-commit
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public java.util.List<Order> getAllOrders() {
        java.util.List<Order> orders = new java.util.ArrayList<>();
        String sql = "SELECT o.ORDER_ID, o.TOTAL_AMOUNT, o.STATUS, o.ORDER_DATE, u.USERNAME " +
                "FROM ORDERS o JOIN USERS u ON o.USER_ID = u.USER_ID";

        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("ORDER_ID"));
                order.setTotalAmount(rs.getDouble("TOTAL_AMOUNT"));
                order.setStatus(rs.getString("STATUS"));
                order.setOrderDate(rs.getTimestamp("ORDER_DATE"));
                order.setCustomerName(rs.getString("USERNAME"));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public boolean updateOrderStatus(int orderId, String status) {
        String sql = "UPDATE ORDERS SET STATUS = ? WHERE ORDER_ID = ?";
        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, orderId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public java.util.List<Order> getOrdersByUserId(int userId) {
        java.util.List<Order> orders = new java.util.ArrayList<>();
        String sql = "SELECT * FROM ORDERS WHERE USER_ID = ?";

        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("ORDER_ID"));
                order.setUserId(rs.getInt("USER_ID"));
                order.setTotalAmount(rs.getDouble("TOTAL_AMOUNT"));
                order.setStatus(rs.getString("STATUS"));
                order.setOrderDate(rs.getTimestamp("ORDER_DATE"));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public boolean cancelOrder(int orderId) {
        // Can only cancel if status is PENDING or PAID
        String checkSql = "SELECT STATUS FROM ORDERS WHERE ORDER_ID = ?";
        String updateSql = "UPDATE ORDERS SET STATUS = 'CANCELLED' WHERE ORDER_ID = ?";

        try (Connection conn = DBManager.getConnection()) {
            // Check status
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, orderId);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    String status = rs.getString("STATUS");
                    if ("SHIPPED".equalsIgnoreCase(status) || "CANCELLED".equalsIgnoreCase(status)) {
                        return false; // Cannot cancel
                    }
                } else {
                    return false; // Order not found
                }
            }

            // Update status
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setInt(1, orderId);
                return updateStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public java.util.List<OrderItem> getOrderDetails(int orderId) {
        java.util.List<OrderItem> items = new java.util.ArrayList<>();
        String sql = "SELECT oi.*, p.NAME, p.IMAGE_PATH, p.PRICE FROM ORDER_ITEMS oi " +
                "JOIN PRODUCTS p ON oi.PRODUCT_ID = p.PRODUCT_ID " +
                "WHERE oi.ORDER_ID = ?";

        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.setItemId(rs.getInt("ITEM_ID"));
                item.setOrderId(rs.getInt("ORDER_ID"));
                item.setProductId(rs.getInt("PRODUCT_ID"));
                item.setQuantity(rs.getInt("QUANTITY"));
                item.setPriceAtPurchase(rs.getDouble("PRICE_AT_PURCHASE"));

                // Populate Product object for display
                com.comp603.shopping.models.Product product = new com.comp603.shopping.models.Product(
                        rs.getInt("PRODUCT_ID"),
                        rs.getString("NAME"),
                        "", // Description not needed
                        rs.getDouble("PRICE"), // Current price, though we use priceAtPurchase for calc
                        0, // Stock not needed
                        rs.getString("IMAGE_PATH"),
                        "", // Category not needed for order details
                        0 // Sales Volume not needed
                );
                item.setProduct(product);

                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
}
