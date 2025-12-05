package com.comp603.shopping.gui.components;

import com.comp603.shopping.gui.MainFrame;

import com.comp603.shopping.models.Product;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class ProductCard extends JPanel {

    private com.comp603.shopping.dao.WishlistDAO wishlistDAO;

    public ProductCard(Product product, MainFrame mainFrame) {

        this.wishlistDAO = new com.comp603.shopping.dao.WishlistDAO();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
        Dimension cardSize = new Dimension(220, 300);
        setPreferredSize(cardSize);
        setMinimumSize(cardSize);
        setMaximumSize(cardSize);

        // Image Panel
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(Color.WHITE);
        imagePanel.setPreferredSize(new Dimension(180, 150));
        imagePanel.setMaximumSize(new Dimension(180, 150));
        imagePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        String imagePath = product.getImagePath();
        boolean imageLoaded = false;

        if (imagePath != null && !imagePath.isEmpty()) {
            java.io.File imgFile = new java.io.File(imagePath);
            if (imgFile.exists()) {
                ImageIcon icon = new ImageIcon(imagePath);

                // Calculate new dimensions maintaining aspect ratio
                int imgWidth = icon.getIconWidth();
                int imgHeight = icon.getIconHeight();
                double ratio = Math.min(180.0 / imgWidth, 150.0 / imgHeight);
                int newWidth = (int) (imgWidth * ratio);
                int newHeight = (int) (imgHeight * ratio);

                Image img = icon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(img));
                imageLoaded = true;
            }
        }

        if (!imageLoaded) {
            imageLabel.setText(String.valueOf(product.getName().charAt(0)).toUpperCase());
            imageLabel.setFont(new Font("Arial", Font.BOLD, 60));
            imageLabel.setForeground(Color.DARK_GRAY);
        }

        imagePanel.add(imageLabel, BorderLayout.CENTER);

        // Star Button
        JToggleButton starButton = new JToggleButton("★");
        starButton.setMargin(new Insets(0, 0, 0, 0));
        starButton.setContentAreaFilled(false);
        starButton.setBorderPainted(false);
        starButton.setFocusPainted(false);
        starButton.setFont(new Font("SansSerif", Font.BOLD, 24));

        // Check if in wishlist (only if logged in)
        if (mainFrame.isLoggedIn()) {
            int userId = mainFrame.getAuthService().getCurrentUser().getUserId();
            if (wishlistDAO.isInWishlist(userId, product.getProductId())) {
                starButton.setSelected(true);
                starButton.setForeground(Color.ORANGE);
            } else {
                starButton.setSelected(false);
                starButton.setForeground(Color.GRAY);
            }
        } else {
            starButton.setSelected(false);
            starButton.setForeground(Color.GRAY);
        }

        starButton.addActionListener(e -> {
            if (!mainFrame.isLoggedIn()) {
                JOptionPane.showMessageDialog(this, "Please log in to add items to your wishlist.", "Login Required",
                        JOptionPane.WARNING_MESSAGE);
                mainFrame.showCard("LOGIN");
                starButton.setSelected(!starButton.isSelected()); // Revert selection
                return;
            }

            int userId = mainFrame.getAuthService().getCurrentUser().getUserId();
            if (starButton.isSelected()) {
                if (wishlistDAO.addToWishlist(userId, product.getProductId())) {
                    starButton.setForeground(Color.ORANGE);
                }
            } else {
                if (wishlistDAO.removeFromWishlist(userId, product.getProductId())) {
                    starButton.setForeground(Color.GRAY);
                }
            }
        });

        // Name
        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Category
        JLabel categoryLabel = new JLabel(product.getCategory());
        categoryLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        categoryLabel.setForeground(Color.DARK_GRAY);
        categoryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Price
        JLabel priceLabel = new JLabel(String.format("$%.2f", product.getPrice()));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        priceLabel.setForeground(new Color(220, 20, 60)); // Crimson Red
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Sales Volume
        JLabel stockLabel = new JLabel("Sales Volume: " + product.getSalesVolume());
        stockLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        stockLabel.setForeground(Color.GRAY);
        stockLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add to Cart Button
        JButton addToCartButton = new JButton("Add to Cart");
        addToCartButton.setBackground(new Color(255, 165, 0)); // Orange
        addToCartButton.setFocusPainted(false);

        if (product.getStockQuantity() <= 0) {
            addToCartButton.setText("Out of Stock");
            addToCartButton.setEnabled(false);
            addToCartButton.setBackground(Color.GRAY);
        }

        addToCartButton.addActionListener(e -> {
            if (!mainFrame.isLoggedIn()) {
                JOptionPane.showMessageDialog(this, "Please log in to add items to your cart.", "Login Required",
                        JOptionPane.WARNING_MESSAGE);
                mainFrame.showCard("LOGIN");
                return;
            }

            mainFrame.getCart().addProduct(product);
            mainFrame.updateCartCount();
            JOptionPane.showMessageDialog(this, "Added to cart!");
        });

        // Action Panel (Star + Add to Cart)
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        actionPanel.setOpaque(false);
        actionPanel.add(starButton);
        actionPanel.add(addToCartButton);
        actionPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Layout Spacing
        add(Box.createVerticalStrut(10));
        add(imagePanel);
        add(Box.createVerticalStrut(10));
        add(nameLabel);
        add(Box.createVerticalStrut(2));
        add(categoryLabel);
        add(Box.createVerticalStrut(5));
        add(priceLabel);
        add(Box.createVerticalStrut(5));
        add(stockLabel);
        add(Box.createVerticalStrut(10));
        add(actionPanel);
        add(Box.createVerticalStrut(10));
        // Mouse Listener for Details
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new com.comp603.shopping.gui.dialogs.ProductDetailsDialog(mainFrame, product).setVisible(true);
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                setBorder(new LineBorder(new Color(100, 149, 237), 2, true)); // Cornflower Blue highlight
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
            }
        });
    }
}
