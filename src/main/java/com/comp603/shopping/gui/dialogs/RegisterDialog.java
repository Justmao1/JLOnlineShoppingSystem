package com.comp603.shopping.gui.dialogs;

import com.comp603.shopping.dao.UserDAO;
import com.comp603.shopping.gui.MainFrame;
import javax.swing.*;
import java.awt.*;

public class RegisterDialog extends JDialog {

    private JTextField userField;
    private JPasswordField passField;
    private JPasswordField confirmPassField;
    private UserDAO userDAO;

    public RegisterDialog(JFrame parent) {
        super(parent, "Register New Account", true);
        this.userDAO = new UserDAO();

        setSize(350, 250);
        setLocationRelativeTo(parent);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Username:"), gbc);

        userField = new JTextField(15);
        gbc.gridx = 1;
        add(userField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Password:"), gbc);

        passField = new JPasswordField(15);
        gbc.gridx = 1;
        add(passField, gbc);

        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Confirm Password:"), gbc);

        confirmPassField = new JPasswordField(15);
        gbc.gridx = 1;
        add(confirmPassField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton registerButton = new JButton("Register");
        JButton cancelButton = new JButton("Cancel");
        JButton backButton = new JButton("Back");

        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        // Actions
        cancelButton.addActionListener(e -> dispose());
        
        backButton.addActionListener(e -> {
            dispose();
            // Show login panel when back button is clicked
            if (parent instanceof MainFrame) {
                ((MainFrame) parent).showCard("LOGIN");
            }
        });

        registerButton.addActionListener(e -> register());
    }

    private void register() {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword());
        String confirmPass = new String(confirmPassField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (userDAO.getUserByUsername(username) != null) {
            JOptionPane.showMessageDialog(this, "Username already exists!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (userDAO.registerUser(username, password)) {
            JOptionPane.showMessageDialog(this, "Registration Successful! Please login.");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Registration Failed!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}