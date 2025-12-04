package com.comp603.shopping.gui.panels;

import com.comp603.shopping.gui.MainFrame;
import com.comp603.shopping.gui.components.ProductCard;
import com.comp603.shopping.gui.components.CarouselPanel;
import com.comp603.shopping.gui.utils.WrapLayout;
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
        // Product Container (Wrap Layout)
        productContainer = new JPanel(new WrapLayout(FlowLayout.LEFT, 20, 20)); // Use custom WrapLayout
        productContainer.setBackground(Color.WHITE);
        productContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        refreshProducts();

        // Fetch products for carousel (e.g., first 5)
        List<Product> allProducts = productDAO.getAllProducts();
        List<Product> hotProducts = allProducts.size() > 5 ? allProducts.subList(0, 5) : allProducts;

        // Carousel
        CarouselPanel carouselPanel = new CarouselPanel(hotProducts);

        // Combined Panel for ScrollPane
        JPanel scrollableContent = new JPanel(new BorderLayout());
        scrollableContent.add(carouselPanel, BorderLayout.NORTH);
        scrollableContent.add(productContainer, BorderLayout.CENTER);

        // Scroll Pane
        JScrollPane scrollPane = new JScrollPane(scrollableContent);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // Disable horizontal scroll
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