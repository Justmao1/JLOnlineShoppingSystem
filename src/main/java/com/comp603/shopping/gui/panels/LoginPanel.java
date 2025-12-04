package com.comp603.shopping.gui.panels;

import com.comp603.shopping.gui.MainFrame;
import com.comp603.shopping.gui.dialogs.RegisterDialog;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginPanel extends JPanel {

    private JTextField userField;
    private JPasswordField passField;
    private MainFrame mainFrame;

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel titleLabel = new JLabel("Welcome to Online Store");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        add(new JLabel("Username:"), gbc);

        userField = new JTextField(15);
        userField.setText(""); // Ensure field is empty when panel is created
        gbc.gridx = 1;
        add(userField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        add(new JLabel("Password:"), gbc);

        passField = new JPasswordField(15);
        passField.setText(""); // Ensure field is empty when panel is created
        gbc.gridx = 1;
        add(passField, gbc);

        JCheckBox adminCheck = new JCheckBox("Login as Admin");
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        add(adminCheck, gbc);

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");
        JButton backButton = new JButton("Back");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        registerButton.addActionListener(e -> new RegisterDialog(mainFrame).setVisible(true));

        backButton.addActionListener(e -> {
            clearFields(); // Clear fields when going back
            mainFrame.showCard("PRODUCTS");
        });

        loginButton.addActionListener((ActionEvent e) -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());
            boolean isAdminLogin = adminCheck.isSelected();

            if (mainFrame.getAuthService().login(user, pass)) {
                clearFields(); // Clear fields on successful login
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

    /**
     * Clear the username and password fields
     */
    private void clearFields() {
        userField.setText("");
        passField.setText("");
    }

    /**
     * Override setVisible to clear fields each time the panel becomes visible
     */
    @Override
    public void setVisible(boolean aFlag) {
        if (aFlag) {
            clearFields();
        }
        super.setVisible(aFlag);
    }
}