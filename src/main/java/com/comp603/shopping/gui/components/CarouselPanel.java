package com.comp603.shopping.gui.components;

import com.comp603.shopping.models.Product;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CarouselPanel extends JPanel {

    private List<Product> products;
    private int currentIndex = 0;
    private Timer timer;
    private JLabel imageLabel;
    private JLabel nameLabel;
    private JLabel priceLabel;

    public CarouselPanel(List<Product> products) {
        this.products = products;
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));
        setPreferredSize(new Dimension(800, 300)); // Fixed height for carousel
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        if (products == null || products.isEmpty()) {
            add(new JLabel("No hot products available", SwingConstants.CENTER), BorderLayout.CENTER);
            return;
        }

        // Center: Image
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(imageLabel, BorderLayout.CENTER);

        // Overlay Panel (Bottom)
        // Overlay Panel (Bottom)
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setOpaque(false); // Transparent background
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        nameLabel = new JLabel();
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        nameLabel.setForeground(Color.BLACK); // Black text for light background
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);

        priceLabel = new JLabel();
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        priceLabel.setForeground(new Color(255, 69, 0)); // Red-Orange for price
        priceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        priceLabel.setHorizontalAlignment(SwingConstants.CENTER);

        infoPanel.add(nameLabel);
        infoPanel.add(priceLabel);

        // Wrapper for info panel to place it at the bottom
        JPanel bottomWrapper = new JPanel(new BorderLayout());
        bottomWrapper.setOpaque(false);
        bottomWrapper.add(infoPanel, BorderLayout.SOUTH);

        // We need to use JLayeredPane or just add it to SOUTH of main layout
        // For simplicity in BorderLayout, let's put info at SOUTH
        add(infoPanel, BorderLayout.SOUTH);

        // Navigation Buttons
        JButton prevButton = createNavButton("<");
        JButton nextButton = createNavButton(">");

        prevButton.addActionListener(e -> showPrevious());
        nextButton.addActionListener(e -> showNext());

        add(prevButton, BorderLayout.WEST);
        add(nextButton, BorderLayout.EAST);

        // Initial Display
        updateDisplay();

        // Auto-rotate timer (3 seconds)
        timer = new Timer(3000, e -> showNext());
        timer.start();
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 24));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setForeground(Color.DARK_GRAY);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void showNext() {
        currentIndex = (currentIndex + 1) % products.size();
        updateDisplay();
    }

    private void showPrevious() {
        currentIndex = (currentIndex - 1 + products.size()) % products.size();
        updateDisplay();
    }

    private void updateDisplay() {
        if (products.isEmpty())
            return;

        Product p = products.get(currentIndex);
        nameLabel.setText(p.getName());
        priceLabel.setText(String.format("$%.2f", p.getPrice()));

        // Load Image
        String imagePath = p.getImagePath();
        boolean imageLoaded = false;
        if (imagePath != null && !imagePath.isEmpty()) {
            java.io.File imgFile = new java.io.File(imagePath);
            if (imgFile.exists()) {
                ImageIcon icon = new ImageIcon(imagePath);
                // Scale to fit height
                Image img = icon.getImage().getScaledInstance(-1, 200, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(img));
                imageLabel.setText("");
                imageLoaded = true;
            }
        }

        if (!imageLoaded) {
            imageLabel.setIcon(null);
            imageLabel.setText(p.getName());
            imageLabel.setFont(new Font("Arial", Font.BOLD, 40));
            imageLabel.setForeground(Color.GRAY);
        }
    }
}
