package com.comp603.shopping.gui.dialogs;

import com.comp603.shopping.gui.MainFrame;
import com.comp603.shopping.models.Product;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ProductDetailsDialog extends JDialog {

    public ProductDetailsDialog(MainFrame mainFrame, Product product) {
        super(mainFrame, "Product Details", true);
        setLayout(new BorderLayout(20, 20));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));
        setSize(600, 400);
        setResizable(false);

        // Left: Image
        JLabel imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(250, 300));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        if (product.getImagePath() != null) {
            java.io.File imgFile = new java.io.File(product.getImagePath());
            if (imgFile.exists()) {
                ImageIcon icon = new ImageIcon(product.getImagePath());
                Image img = icon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH); // Keep aspect ratio logic
                                                                                             // if needed, simple scale
                                                                                             // for now
                imageLabel.setIcon(new ImageIcon(img));
            } else {
                imageLabel.setText("Image not found");
            }
        } else {
            imageLabel.setText("No Image");
        }
        add(imageLabel, BorderLayout.WEST);

        // Center: Details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));

        // Name
        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.add(nameLabel);
        detailsPanel.add(Box.createVerticalStrut(10));

        // Price and Sales
        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pricePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel priceLabel = new JLabel(String.format("$%.2f", product.getPrice()));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 20));
        priceLabel.setForeground(new Color(220, 20, 60)); // Crimson

        JLabel salesLabel = new JLabel("  |  Sales: " + product.getSalesVolume());
        salesLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        salesLabel.setForeground(Color.GRAY);

        pricePanel.add(priceLabel);
        pricePanel.add(salesLabel);
        detailsPanel.add(pricePanel);
        detailsPanel.add(Box.createVerticalStrut(20));

        // Description Label
        JLabel descHeader = new JLabel("Description:");
        descHeader.setFont(new Font("Arial", Font.BOLD, 14));
        descHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.add(descHeader);
        detailsPanel.add(Box.createVerticalStrut(5));

        // Description Text
        JTextArea descArea = new JTextArea(product.getDescription());
        descArea.setWrapStyleWord(true);
        descArea.setLineWrap(true);
        descArea.setEditable(false);
        descArea.setOpaque(false);
        descArea.setFont(new Font("Arial", Font.PLAIN, 14));
        descArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        JScrollPane scrollPane = new JScrollPane(descArea);
        scrollPane.setBorder(null);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.add(scrollPane);

        add(detailsPanel, BorderLayout.CENTER);

        // Bottom: Close Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(mainFrame);
    }
}
