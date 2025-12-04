package com.comp603.shopping.gui.dialogs;

import com.comp603.shopping.models.Product;
import javax.swing.*;
import java.awt.*;

public class ProductDialog extends JDialog {

    private JTextField nameField;
    private JTextField descField;
    private JTextField priceField;
    private JTextField stockField;
    private JComboBox<String> categoryCombo;
    private String selectedImagePath;
    private JLabel imageLabel;
    private boolean confirmed = false;
    private Product product;

    public ProductDialog(Frame owner, Product productToEdit) {
        super(owner, productToEdit == null ? "Add Product" : "Edit Product", true);
        this.product = productToEdit;

        // Fixed size instead of pack()
        setSize(600, 450);
        setLayout(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        nameField = new JTextField();
        descField = new JTextField();
        priceField = new JTextField();
        stockField = new JTextField();
        categoryCombo = new JComboBox<>(
                new String[] { "Books", "Sports & Outdoors", "Electronics", "CDs", "Clothing" });

        JButton selectImageButton = new JButton("Select Image");
        imageLabel = new JLabel("No image selected");

        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(descField);
        formPanel.add(new JLabel("Price:"));
        formPanel.add(priceField);
        formPanel.add(new JLabel("Stock:"));
        formPanel.add(stockField);
        formPanel.add(new JLabel("Category:"));
        formPanel.add(categoryCombo);
        formPanel.add(selectImageButton);
        formPanel.add(imageLabel);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Logic
        selectImageButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                java.io.File selectedFile = fileChooser.getSelectedFile();
                try {
                    String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                    java.nio.file.Path destPath = java.nio.file.Paths.get("images", fileName);
                    if (!java.nio.file.Files.exists(destPath.getParent())) {
                        java.nio.file.Files.createDirectories(destPath.getParent());
                    }
                    java.nio.file.Files.copy(selectedFile.toPath(), destPath,
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    selectedImagePath = "images/" + fileName;

                    // Update label with truncated path and tooltip
                    imageLabel.setText(truncateMiddle(selectedFile.getName(), 30));
                    imageLabel.setToolTipText(selectedImagePath);

                } catch (java.io.IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error uploading image: " + ex.getMessage());
                }
            }
        });

        if (product != null) {
            populateFields();
        }

        saveButton.addActionListener(e -> {
            if (validateFields()) {
                confirmed = true;
                dispose();
            }
        });

        cancelButton.addActionListener(e -> dispose());

        // Removed pack() to respect setSize()
        setLocationRelativeTo(owner);
    }

    private void populateFields() {
        nameField.setText(product.getName());
        descField.setText(product.getDescription());
        priceField.setText(String.valueOf(product.getPrice()));
        stockField.setText(String.valueOf(product.getStockQuantity()));
        selectedImagePath = product.getImagePath();
        if (selectedImagePath != null) {
            imageLabel.setText(truncateMiddle(selectedImagePath, 30));
            imageLabel.setToolTipText(selectedImagePath);
        }
        categoryCombo.setSelectedItem(product.getCategory());
    }

    private String truncateMiddle(String text, int maxLen) {
        if (text == null || text.length() <= maxLen) {
            return text;
        }
        int partLen = (maxLen - 3) / 2;
        return text.substring(0, partLen) + "..." + text.substring(text.length() - partLen);
    }

    private boolean validateFields() {
        try {
            Double.parseDouble(priceField.getText());
            Integer.parseInt(stockField.getText());
            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format for Price or Stock.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Product getProduct() {
        String name = nameField.getText();
        String desc = descField.getText();
        double price = Double.parseDouble(priceField.getText());
        int stock = Integer.parseInt(stockField.getText());
        String category = (String) categoryCombo.getSelectedItem();

        // Preserve ID and Sales Volume if editing, else 0
        int id = (product == null) ? 0 : product.getProductId();
        int sales = (product == null) ? 0 : product.getSalesVolume();

        return new Product(id, name, desc, price, stock, selectedImagePath, category, sales);
    }
}
