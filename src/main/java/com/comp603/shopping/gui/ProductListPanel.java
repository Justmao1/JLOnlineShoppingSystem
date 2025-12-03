package com.comp603.shopping.gui;

import com.comp603.shopping.dao.ProductDAO;
import com.comp603.shopping.models.Product;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ProductListPanel extends JPanel {

    private MainFrame mainFrame;
    private JPanel productContainer;
    private ProductDAO productDAO;

    public ProductListPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.productDAO = new ProductDAO();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Product Container (Grid Layout)
        productContainer = new JPanel(new GridLayout(0, 3, 20, 20)); // 3 columns, gaps
        productContainer.setBackground(Color.WHITE);
        productContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        refreshProducts();

        // Scroll Pane
        JScrollPane scrollPane = new JScrollPane(productContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void updateProductList(List<Product> products) {
        productContainer.removeAll();

        if (products.isEmpty()) {
            JLabel notFoundLabel = new JLabel("No products found.");
            notFoundLabel.setFont(new Font("Arial", Font.BOLD, 18));
            notFoundLabel.setHorizontalAlignment(SwingConstants.CENTER);
            productContainer.add(notFoundLabel);
        } else {
            for (Product p : products) {
                ProductCard card = new ProductCard(p, mainFrame);
                productContainer.add(card);
            }
        }
        productContainer.revalidate();
        productContainer.repaint();
    }

    public void refreshProducts() {
        List<Product> productList = productDAO.getAllProducts();
        updateProductList(productList);
    }
}
