package com.comp603.shopping.gui.panels;

import com.comp603.shopping.gui.MainFrame;
import com.comp603.shopping.gui.dialogs.RegisterDialog;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginPanel extends JPanel {

    private JTextField userField;
    private JPasswordField passField;

    public LoginPanel(MainFrame mainFrame) {

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel titleLabel = new JLabel("Welcome to Online Store");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        add(new JLabel("Username:"), gbc);

        userField = new JTextField(15);
        gbc.gridx = 1;
        add(userField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        add(new JLabel("Password:"), gbc);

        passField = new JPasswordField(15);
        gbc.gridx = 1;
        add(passField, gbc);

        JCheckBox adminCheck = new JCheckBox("Login as Admin");
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        add(adminCheck, gbc);

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        registerButton.addActionListener(e -> new RegisterDialog(mainFrame).setVisible(true));

        loginButton.addActionListener((ActionEvent e) -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());
            boolean isAdminLogin = adminCheck.isSelected();

            if (mainFrame.getAuthService().login(user, pass)) {
                com.comp603.shopping.models.User currentUser = mainFrame.getAuthService().getCurrentUser();

                if (isAdminLogin) {
                    if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
                        JOptionPane.showMessageDialog(this, "Admin Login Successful!");
                        mainFrame.onLoginSuccess();
                    } else {
                        JOptionPane.showMessageDialog(this, "Access Denied: You are not an Admin.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        mainFrame.getAuthService().logout();
                    }
                } else {
                    // Customer Login
                    if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
                        // Optional: Allow admins to login as customers or warn them?
                        // For now, let's allow it but they see customer view.
                        JOptionPane.showMessageDialog(this, "Login Successful (Customer View)");
                        mainFrame.onLoginSuccess();
                    } else {
                        JOptionPane.showMessageDialog(this, "Login Successful!");
                        mainFrame.onLoginSuccess();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
