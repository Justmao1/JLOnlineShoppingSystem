package com.comp603.shopping.gui;

import com.comp603.shopping.models.ShoppingCart;
import com.comp603.shopping.services.AuthService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private AuthService authService;
    private ShoppingCart cart;
    private HeaderPanel headerPanel;

    public MainFrame() {
        super("Online Shopping System");
        this.authService = new AuthService();
        this.cart = new ShoppingCart();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1040, 700); // Increased size for better grid view
        setMinimumSize(new Dimension(1040, 700)); // Restrict minimum size
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header (North)
        headerPanel = new HeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        headerPanel.setVisible(false); // Hidden initially until login

        // Main Content (Center)
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Initialize Panels
        LoginPanel loginPanel = new LoginPanel(this);
        mainPanel.add(loginPanel, "LOGIN");

        add(mainPanel, BorderLayout.CENTER);

        // Start with Login
        showCard("LOGIN");
    }

    public void showCard(String cardName) {
        cardLayout.show(mainPanel, cardName);
    }

    public AuthService getAuthService() {
        return authService;
    }

    public ShoppingCart getCart() {
        return cart;
    }

    public void onLoginSuccess() {
        // Initialize other panels after login to ensure data is fresh

        if ("ADMIN".equalsIgnoreCase(authService.getCurrentUser().getRole())) {
            AdminDashboard adminDashboard = new AdminDashboard(this);
            mainPanel.add(adminDashboard, "ADMIN_DASHBOARD");
            showCard("ADMIN_DASHBOARD");
        } else {
            ProductListPanel productPanel = new ProductListPanel(this);
            mainPanel.add(productPanel, "PRODUCTS");

            // Set User ID in Cart
            cart.setUserId(authService.getCurrentUser().getUserId());

            headerPanel.updateUser(authService.getCurrentUser().getUsername());
            headerPanel.updateCartCount(
                    cart.getItems().stream().mapToInt(com.comp603.shopping.models.CartItem::getQuantity).sum());
            headerPanel.setVisible(true);

            showCard("PRODUCTS");
        }
    }

    public void logout() {
        authService.logout();
        cart.clear();
        headerPanel.setVisible(false);
        showCard("LOGIN");
    }

    public void showCart() {
        new CartDialog(this).setVisible(true);
    }

    public void startCheckout() {
        CheckoutPanel checkoutPanel = new CheckoutPanel(this);
        mainPanel.add(checkoutPanel, "CHECKOUT");
        showCard("CHECKOUT");
    }

    public void updateCartCount() {
        headerPanel.updateCartCount(cart.getItems().size());
    }

    public void openMyAccount() {
        new MyAccountDialog(this).setVisible(true);
    }

    public void performSearch(String keyword) {
        // Find ProductListPanel
        for (Component comp : mainPanel.getComponents()) {
            if (comp instanceof ProductListPanel && comp.isVisible()) {
                ProductListPanel panel = (ProductListPanel) comp;
                com.comp603.shopping.dao.ProductDAO dao = new com.comp603.shopping.dao.ProductDAO();
                panel.updateProductList(dao.searchProducts(keyword));
                return;
            }
        }
    }

    public void refreshView() {
        for (Component comp : mainPanel.getComponents()) {
            if (comp instanceof ProductListPanel) {
                ((ProductListPanel) comp).refreshProducts();
            }
        }
    }

    // Inner Class for Header
    private class HeaderPanel extends JPanel {
        private JLabel welcomeLabel;
        private JButton cartButton;
        private JTextField searchField;

        public HeaderPanel() {
            setLayout(new BorderLayout());
            setBackground(new Color(50, 50, 50)); // Dark background
            setBorder(new EmptyBorder(10, 20, 10, 20));

            // Left: Title
            JLabel titleLabel = new JLabel("CyberShop");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
            titleLabel.setForeground(Color.WHITE);
            add(titleLabel, BorderLayout.WEST);

            // Center: Search Bar
            JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            searchPanel.setOpaque(false);

            JButton homeButton = new JButton("🏠");
            homeButton.setToolTipText("Home");
            homeButton.setFocusPainted(false);
            homeButton.addActionListener(e -> {
                showCard("PRODUCTS");
                refreshView();
            });

            searchField = new JTextField(20);
            JButton searchButton = new JButton("Search");

            searchPanel.add(homeButton);
            searchPanel.add(searchField);
            searchPanel.add(searchButton);
            add(searchPanel, BorderLayout.CENTER);

            // Right: User Info & Actions
            JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
            rightPanel.setOpaque(false);

            welcomeLabel = new JLabel("Welcome");
            welcomeLabel.setForeground(Color.WHITE);
            welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));

            cartButton = new JButton("View Cart (0)");
            cartButton.setBackground(new Color(255, 165, 0)); // Orange
            cartButton.setForeground(Color.BLACK);
            cartButton.setFocusPainted(false);
            cartButton.addActionListener(e -> showCart());

            JButton myAccountButton = new JButton("My Account");
            myAccountButton.addActionListener(e -> MainFrame.this.openMyAccount());

            rightPanel.add(welcomeLabel);
            rightPanel.add(cartButton);
            rightPanel.add(myAccountButton);

            add(rightPanel, BorderLayout.EAST);

            // Search Actions
            searchButton.addActionListener(e -> performSearch(searchField.getText().trim()));
            searchField.addActionListener(e -> performSearch(searchField.getText().trim()));
        }

        public void updateUser(String username) {
            welcomeLabel.setText("Welcome, " + username);
        }

        public void updateCartCount(int count) {
            cartButton.setText("View Cart (" + count + ")");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}
