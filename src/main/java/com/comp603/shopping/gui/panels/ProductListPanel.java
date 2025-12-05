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
    private java.util.List<Product> currentProducts;
    private boolean isFiltered = false;

    public ProductListPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.productDAO = new ProductDAO();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Fetch products for carousel (Use all products for random display)
        List<Product> allProducts = productDAO.getAllProducts();

        // Carousel
        carouselPanel = new CarouselPanel(allProducts);

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

        // Add some vertical spacing between carousel and sort panel
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(sortPanel);

        // Add smaller vertical spacing between sort panel and products
        JPanel spacer = new JPanel();
        spacer.setBackground(Color.WHITE);
        spacer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 5)); // Reduced from 10 to 5
        contentPanel.add(spacer);

        // Wrap the product container in a panel that will expand
        JPanel productWrapper = new JPanel(new BorderLayout());
        productWrapper.setBackground(Color.WHITE);
        productWrapper.add(productContainer, BorderLayout.NORTH);

        // Main panel that holds fixed height components and expanding product area
        // Main panel that holds fixed height components and expanding product area
        // Use a custom ScrollablePanel to ensure it tracks viewport width
        ScrollablePanel mainContent = new ScrollablePanel(new BorderLayout());
        mainContent.setBackground(Color.WHITE);
        mainContent.add(contentPanel, BorderLayout.NORTH);
        mainContent.add(productWrapper, BorderLayout.CENTER);

        // Scroll Pane
        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // Disable horizontal scroll
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Add listener to revalidate when resized (crucial for WrapLayout)
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                productContainer.revalidate();
                productContainer.repaint();
            }
        });
    }

    // Custom panel that implements Scrollable to force width to match viewport
    private class ScrollablePanel extends JPanel implements Scrollable {
        public ScrollablePanel(LayoutManager layout) {
            super(layout);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true; // Force width to match viewport
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false; // Allow height to expand
        }
    }

    private JPanel createSortPanel() {
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        sortPanel.setBackground(Color.WHITE);

        JLabel sortByLabel = new JLabel("Sort by:");
        JComboBox<String> sortByCombo = new JComboBox<>(new String[] { "Price", "Sales Volume" });
        JComboBox<String> orderCombo = new JComboBox<>(new String[] { "Ascending", "Descending" });
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
        this.currentProducts = products;
        this.isFiltered = true;
        renderProducts(products);
    }

    private void renderProducts(List<Product> products) {
        productContainer.removeAll();
        if (products == null || products.isEmpty()) {
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
        if (isFiltered && currentProducts != null) {
            java.util.List<Product> list = new java.util.ArrayList<>(currentProducts);
            java.util.Comparator<Product> comp;
            switch (currentSortBy) {
                case "PRICE":
                    comp = java.util.Comparator.comparingDouble(Product::getPrice);
                    break;
                case "SALES_VOLUME":
                    comp = java.util.Comparator.comparingInt(Product::getSalesVolume);
                    break;
                default:
                    comp = java.util.Comparator.comparingInt(Product::getProductId);
                    break;
            }
            if (!currentAscending) {
                comp = comp.reversed();
            }
            list.sort(comp);
            this.currentProducts = list;
            renderProducts(list);
            setCarouselVisible(false);
            return;
        }

        java.util.List<Product> productList;
        if ("PRODUCT_ID".equals(currentSortBy)) {
            productList = productDAO.getAllProducts();
        } else {
            productList = productDAO.getAllProductsSorted(currentSortBy, currentAscending);
        }
        this.currentProducts = null;
        this.isFiltered = false;
        renderProducts(productList);
        setCarouselVisible(true);
    }

    public void clearFilter() {
        this.isFiltered = false;
        this.currentProducts = null;
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
