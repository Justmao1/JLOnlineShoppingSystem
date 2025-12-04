package com.comp603.shopping;

import com.comp603.shopping.config.DBManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class CheckUsers {
    public static void main(String[] args) {
        System.out.println("Checking users in database...");
        
        try (Connection conn = DBManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM USERS")) {

            System.out.println("--------------------------------------------------------------------------------");
            System.out.printf("%-5s | %-15s | %-15s | %-10s | %-10s%n", "ID", "USERNAME", "PASSWORD", "ROLE", "BALANCE");
            System.out.println("--------------------------------------------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-5d | %-15s | %-15s | %-10s | %-10.2f%n",
                        rs.getInt("USER_ID"),
                        rs.getString("USERNAME"),
                        rs.getString("PASSWORD"),
                        rs.getString("ROLE"),
                        rs.getDouble("BALANCE"));
            }
            System.out.println("--------------------------------------------------------------------------------");

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // We need to exit explicitly because DBManager might keep the driver active
        System.exit(0);
    }
}
