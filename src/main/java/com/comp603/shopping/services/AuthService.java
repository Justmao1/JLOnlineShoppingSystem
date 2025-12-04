package com.comp603.shopping.services;

import com.comp603.shopping.dao.UserDAO;
import com.comp603.shopping.models.User;

public class AuthService {

    private UserDAO userDAO;
    private User currentUser;

    public AuthService() {
        this(new UserDAO());
    }

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public boolean login(String username, String password) {
        User user = userDAO.getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            this.currentUser = user;
            return true;
        }
        return false;
    }

    public boolean register(String username, String password, String email) {
        if (userDAO.getUserByUsername(username) != null) {
            return false; // Username already exists
        }
        User newUser = new User(0, username, password, email, "CUSTOMER", 0.0);
        return userDAO.createUser(newUser);
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}
