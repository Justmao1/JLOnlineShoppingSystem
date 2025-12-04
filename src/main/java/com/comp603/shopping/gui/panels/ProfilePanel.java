package com.comp603.shopping.gui.panels;

import com.comp603.shopping.gui.MainFrame;

import com.comp603.shopping.dao.UserDAO;
import com.comp603.shopping.models.User;
import javax.swing.*;
import java.awt.*;

public class ProfilePanel extends JPanel {

    private MainFrame mainFrame;
    private UserDAO userDAO;
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;

    public ProfilePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.userDAO = new UserDAO();
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        usernameField = new JTextField();
        emailField = new JTextField();
        passwordField = new JPasswordField();

        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("New Password:"));
        formPanel.add(passwordField);

        add(formPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save Changes");
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.CENTER);

        loadUserData();

        saveButton.addActionListener(e -> saveChanges());
    }

    private void loadUserData() {
        User user = mainFrame.getAuthService().getCurrentUser();
        if (user != null) {
            usernameField.setText(user.getUsername());
            emailField.setText(user.getEmail());
        }
    }

    private void saveChanges() {
        User user = mainFrame.getAuthService().getCurrentUser();
        if (user != null) {
            user.setUsername(usernameField.getText());
            user.setEmail(emailField.getText());

            if (userDAO.updateUser(user)) {
                String newPass = new String(passwordField.getPassword());
                if (!newPass.isEmpty()) {
                    userDAO.updatePassword(user.getUserId(), newPass);
                }
                JOptionPane.showMessageDialog(this, "Profile Updated Successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update profile.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
