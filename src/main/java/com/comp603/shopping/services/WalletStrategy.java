package com.comp603.shopping.services;

import com.comp603.shopping.dao.UserDAO;
import com.comp603.shopping.models.User;

public class WalletStrategy implements PaymentStrategy {

    private User user;
    private UserDAO userDAO;

    public WalletStrategy(User user) {
        this(user, new UserDAO());
    }

    public WalletStrategy(User user, UserDAO userDAO) {
        this.user = user;
        this.userDAO = userDAO;
    }

    @Override
    public boolean pay(double amount) {
        if (user.getBalance() >= amount) {
            double newBalance = user.getBalance() - amount;
            // Update balance in DB
            boolean success = userDAO.updateUserBalance(user.getUserId(), newBalance);
            if (success) {
                user.setBalance(newBalance); // Update local object
                System.out.println(amount + " paid using Wallet.");
                return true;
            }
        } else {
            System.out.println("Insufficient balance in Wallet!");
        }
        return false;
    }
}
