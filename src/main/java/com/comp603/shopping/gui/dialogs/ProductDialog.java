package com.comp603.shopping.gui.dialogs;

import com.comp603.shopping.models.DigitalProduct;
import com.comp603.shopping.models.PhysicalProduct;
import com.comp603.shopping.models.Product;
import javax.swing.*;
import java.awt.*;

public class ProductDialog extends JDialog {

    private JTextField nameField;
    private JTextField descField;
    private JTextField priceField;
    private JTextField stockField;
    private JComboBox<String> typeCombo;
    private JTextField extraField; // Weight or Link
    private JLabel extraLabel;
    private String selectedImagePath;
    private JLabel imageLabel;
    private boolean confirmed = false;
    private Product product;

    public ProductDialog(Frame owner, Product productToEdit) {
        super(owner, productToEdit == null ? "Add Product" : "Edit Product", true);
        this.product = productToEdit;

        setLayout(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10)); // Increased rows
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        nameField = new JTextField();
        descField = new JTextField();
        priceField = new JTextField();
        stockField = new JTextField();
        typeCombo = new JComboBox<>(new String[] { "Physical", "Digital" });
        extraField = new JTextField();
        extraLabel = new JLabel("Weight (kg):");

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
        formPanel.add(new JLabel("Type:"));
        formPanel.add(typeCombo);
        formPanel.add(extraLabel);
        formPanel.add(extraField);
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
        typeCombo.addActionListener(e -> {
            if ("Physical".equals(typeCombo.getSelectedItem())) {
                extraLabel.setText("Weight (kg):");
            } else {
                extraLabel.setText("Download Link:");
            }
        });

        selectImageButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                java.io.File selectedFile = fileChooser.getSelectedFile();
                try {
                    String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                    java.nio.file.Path destPath = java.nio.file.Paths.get("images", fileName);
                    java.nio.file.Files.copy(selectedFile.toPath(), destPath,
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    selectedImagePath = "images/" + fileName;
                    imageLabel.setText(selectedFile.getName());
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

        pack();
        setLocationRelativeTo(owner);
    }

    private void populateFields() {
        nameField.setText(product.getName());
        descField.setText(product.getDescription());
        priceField.setText(String.valueOf(product.getPrice()));
        stockField.setText(String.valueOf(product.getStockQuantity()));
        selectedImagePath = product.getImagePath();
        if (selectedImagePath != null) {
            imageLabel.setText(selectedImagePath);
        }

        if (product instanceof PhysicalProduct) {
            typeCombo.setSelectedItem("Physical");
            extraField.setText(String.valueOf(((PhysicalProduct) product).getWeight()));
        } else {
            typeCombo.setSelectedItem("Digital");
            extraField.setText(((DigitalProduct) product).getDownloadLink());
        }
        typeCombo.setEnabled(false); // Can't change type of existing product easily
    }

    private boolean validateFields() {
        try {
            Double.parseDouble(priceField.getText());
            Integer.parseInt(stockField.getText());
            if ("Physical".equals(typeCombo.getSelectedItem())) {
                Double.parseDouble(extraField.getText());
            }
            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format for Price, Stock, or Weight.", "Error",
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

        if ("Physical".equals(typeCombo.getSelectedItem())) {
            double weight = Double.parseDouble(extraField.getText());
            Product p = new PhysicalProduct(product == null ? 0 : product.getProductId(), name, desc, price, stock,
                    weight, selectedImagePath);
            return p;
        } else {
            String link = extraField.getText();
            Product p = new DigitalProduct(product == null ? 0 : product.getProductId(), name, desc, price, stock,
                    link, selectedImagePath);
            return p;
        }
    }
}
