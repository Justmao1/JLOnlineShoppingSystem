package com.comp603.shopping.dao;

import com.comp603.shopping.config.DBManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PaymentMethodDAO {

    public boolean addPaymentMethod(int userId, String cardNumber, String expiryDate) {
        String sql = "INSERT INTO PAYMENT_METHODS (USER_ID, CARD_NUMBER, EXPIRY_DATE) VALUES (?, ?, ?)";
        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, cardNumber);
            pstmt.setString(3, expiryDate);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<PaymentMethod> getPaymentMethods(int userId) {
        List<PaymentMethod> methods = new ArrayList<>();
        String sql = "SELECT PAYMENT_METHOD_ID, CARD_NUMBER, EXPIRY_DATE FROM PAYMENT_METHODS WHERE USER_ID = ?";
        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("PAYMENT_METHOD_ID");
                String cardNumber = rs.getString("CARD_NUMBER");
                String expiryDate = rs.getString("EXPIRY_DATE");
                
                // Mask card number for display
                String maskedCard = cardNumber;
                if (cardNumber.length() > 4) {
                    maskedCard = "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
                }
                
                methods.add(new PaymentMethod(id, maskedCard, expiryDate));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return methods;
    }
    
    public boolean deletePaymentMethod(int paymentMethodId) {
        String sql = "DELETE FROM PAYMENT_METHODS WHERE PAYMENT_METHOD_ID = ?";
        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, paymentMethodId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Inner class to represent a payment method
    public static class PaymentMethod {
        private int id;
        private String cardNumber;
        private String expiryDate;
        
        public PaymentMethod(int id, String cardNumber, String expiryDate) {
            this.id = id;
            this.cardNumber = cardNumber;
            this.expiryDate = expiryDate;
        }
        
        public int getId() {
            return id;
        }
        
        public String getCardNumber() {
            return cardNumber;
        }
        
        public String getExpiryDate() {
            return expiryDate;
        }
        
        @Override
        public String toString() {
            return cardNumber + " (Expires: " + expiryDate + ")";
        }
    }
}