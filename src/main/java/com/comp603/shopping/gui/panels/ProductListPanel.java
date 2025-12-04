package com.comp603.shopping.gui.panels;

import com.comp603.shopping.gui.MainFrame;
import com.comp603.shopping.gui.components.ProductCard;
import com.comp603.shopping.gui.components.CarouselPanel;
import com.comp603.shopping.gui.utils.WrapLayout;
import com.comp603.shopping.dao.ProductDAO;
import com.comp603.shopping.models.Product;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ProductListPanel extends JPanel {

    private MainFrame mainFrame;
    private JPanel productContainer;
    private ProductDAO productDAO;
    private CarouselPanel carouselPanel;
    private String currentSortBy = "PRODUCT_ID"; // Default sort
    private boolean currentAscending = true; // Default order

    public ProductListPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.productDAO = new ProductDAO();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Fetch products for carousel (e.g., first 5)
        List<Product> allProducts = productDAO.getAllProducts();
        List<Product> hotProducts = allProducts.size() > 5 ? allProducts.subList(0, 5) : allProducts;

        // Carousel
        carouselPanel = new CarouselPanel(hotProducts);

        // Create sorting panel
        JPanel sortPanel = createSortPanel();

        // Product Container (Grid Layout)
        // Product Container (Wrap Layout)
        productContainer = new JPanel(new WrapLayout(FlowLayout.LEFT, 20, 20)); // Use custom WrapLayout
        productContainer.setBackground(Color.WHITE);
        productContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        refreshProducts();

        // Combined content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(carouselPanel);
        contentPanel.add(sortPanel);
        
        // Wrap the product container in a panel that will expand
        JPanel productWrapper = new JPanel(new BorderLayout());
        productWrapper.setBackground(Color.WHITE);
        productWrapper.add(productContainer, BorderLayout.NORTH);
        
        // Main panel that holds fixed height components and expanding product area
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(Color.WHITE);
        mainContent.add(contentPanel, BorderLayout.NORTH);
        mainContent.add(productWrapper, BorderLayout.CENTER);

        // Scroll Pane
        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // Disable horizontal scroll
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createSortPanel() {
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        sortPanel.setBackground(Color.WHITE);

        JLabel sortByLabel = new JLabel("Sort by:");
        JComboBox<String> sortByCombo = new JComboBox<>(new String[]{"Price", "Sales Volume"});
        JComboBox<String> orderCombo = new JComboBox<>(new String[]{"Ascending", "Descending"});
        JButton sortButton = new JButton("Apply Sort");

        sortPanel.add(sortByLabel);
        sortPanel.add(sortByCombo);
        sortPanel.add(orderCombo);
        sortPanel.add(sortButton);

        sortButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedSort = (String) sortByCombo.getSelectedItem();
                boolean ascending = "Ascending".equals(orderCombo.getSelectedItem());
                
                // Map user-friendly names to database column names
                switch (selectedSort) {
                    case "Price":
                        currentSortBy = "PRICE";
                        break;
                    case "Sales Volume":
                        currentSortBy = "SALES_VOLUME";
                        break;
                    default:
                        currentSortBy = "PRODUCT_ID"; // Default
                        break;
                }
                
                currentAscending = ascending;
                refreshProducts();
            }
        });

        return sortPanel;
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
        List<Product> productList;
        if ("PRODUCT_ID".equals(currentSortBy)) {
            // Default sorting (as originally implemented)
            productList = productDAO.getAllProducts();
        } else {
            // Sorted products
            productList = productDAO.getAllProductsSorted(currentSortBy, currentAscending);
        }
        updateProductList(productList);
        setCarouselVisible(true);
    }

    public void setCarouselVisible(boolean visible) {
        if (carouselPanel != null) {
            carouselPanel.setVisible(visible);
            revalidate();
            repaint();
        }
    }
}