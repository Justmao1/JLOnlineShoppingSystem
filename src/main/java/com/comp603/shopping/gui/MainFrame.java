package com.comp603.shopping.gui;

import com.comp603.shopping.models.ShoppingCart;
import com.comp603.shopping.services.AuthService;
import com.comp603.shopping.gui.panels.*;
import com.comp603.shopping.gui.dialogs.*;
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

        // Main Content (Center)
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Initialize Panels
        LoginPanel loginPanel = new LoginPanel(this);
        mainPanel.add(loginPanel, "LOGIN");

        // Show products by default (guest mode)
        ProductListPanel productPanel = new ProductListPanel(this);
        mainPanel.add(productPanel, "PRODUCTS");

        add(mainPanel, BorderLayout.CENTER);

        // Start with Products page
        showCard("PRODUCTS");
    }

    public void showCard(String cardName) {
        cardLayout.show(mainPanel, cardName);

        if ("ADMIN_DASHBOARD".equals(cardName)) {
            headerPanel.setVisible(false);
            return;
        }

        headerPanel.setVisible(true);

        // 控制HeaderPanel中的认证按钮显示/隐藏
        if ("LOGIN".equals(cardName)) {
            headerPanel.showAuthButtons(false);
            // Clear login form when showing login panel
            for (Component comp : mainPanel.getComponents()) {
                if (comp instanceof LoginPanel) {
                    ((LoginPanel) comp).setVisible(true); // This will trigger clearing the fields
                    break;
                }
            }
        } else {
            headerPanel.showAuthButtons(true);
        }
    }

    public AuthService getAuthService() {
        return authService;
    }

    public ShoppingCart getCart() {
        return cart;
    }

    public boolean isLoggedIn() {
        return authService.getCurrentUser() != null;
    }

    public void onLoginSuccess(boolean isAdminView) {
        // Initialize other panels after login to ensure data is fresh

        if (isAdminView && "ADMIN".equalsIgnoreCase(authService.getCurrentUser().getRole())) {
            AdminDashboard adminDashboard = new AdminDashboard(this);
            mainPanel.add(adminDashboard, "ADMIN_DASHBOARD");
            showCard("ADMIN_DASHBOARD");
        } else {
            // Set User ID in Cart
            cart.setUserId(authService.getCurrentUser().getUserId());

            headerPanel.updateUser(authService.getCurrentUser().getUsername());
            headerPanel.updateCartCount(
                    cart.getItems().stream().mapToInt(com.comp603.shopping.models.CartItem::getQuantity).sum());

            showCard("PRODUCTS");
        }
    }

    public void logout() {
        authService.logout();
        cart.clear();
        headerPanel.resetToGuestMode();
        showCard("PRODUCTS");
    }

    public void showCart() {
        if (!isLoggedIn()) {
            JOptionPane.showMessageDialog(this, "Please log in to view your cart.", "Login Required",
                    JOptionPane.WARNING_MESSAGE);
            showCard("LOGIN");
            return;
        }
        new CartDialog(this).setVisible(true);
    }

    public void startCheckout() {
        if (!isLoggedIn()) {
            JOptionPane.showMessageDialog(this, "Please log in to checkout.", "Login Required",
                    JOptionPane.WARNING_MESSAGE);
            showCard("LOGIN");
            return;
        }
        CheckoutPanel checkoutPanel = new CheckoutPanel(this);
        mainPanel.add(checkoutPanel, "CHECKOUT");
        showCard("CHECKOUT");
    }

    public void updateCartCount() {
        if (isLoggedIn()) {
            headerPanel.updateCartCount(cart.getItems().size());
        }
    }

    public void openMyAccount() {
        if (!isLoggedIn()) {
            JOptionPane.showMessageDialog(this, "Please log in to access your account.", "Login Required",
                    JOptionPane.WARNING_MESSAGE);
            showCard("LOGIN");
            return;
        }
        new MyAccountDialog(this).setVisible(true);
    }

    public void performSearch(String keyword) {
        // Find ProductListPanel
        for (Component comp : mainPanel.getComponents()) {
            if (comp instanceof ProductListPanel && comp.isVisible()) {
                ProductListPanel panel = (ProductListPanel) comp;
                com.comp603.shopping.dao.ProductDAO dao = new com.comp603.shopping.dao.ProductDAO();
                panel.updateProductList(dao.searchProducts(keyword));
                panel.setCarouselVisible(false);
                // Clear the search field after performing search
                headerPanel.clearSearchField();
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
        private JButton loginButton;
        private JButton myAccountButton;

        public HeaderPanel() {
            setLayout(new BorderLayout());
            setBackground(new Color(50, 50, 50)); // Dark background
            setBorder(new EmptyBorder(10, 20, 10, 20));

            // Left: Title
            JLabel titleLabel = new JLabel("JL Online Store");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
            titleLabel.setForeground(Color.WHITE);

            // Create a panel for the left side containing title, home button and search
            JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            leftPanel.setOpaque(false);
            leftPanel.add(titleLabel);

            // Home button
            JButton homeButton = new JButton("🏠");
            homeButton.setToolTipText("Home");
            homeButton.setFocusPainted(false);
            homeButton.addActionListener(e -> {
                showCard("PRODUCTS");
                refreshView();
                clearSearchField(); // Clear search field when going home
            });

            // Search components
            searchField = new JTextField(18);
            searchField.setText("Type here to search");
            searchField.setForeground(Color.GRAY);
            searchField.addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent e) {
                    if (searchField.getText().equals("Type here to search")) {
                        searchField.setText("");
                        searchField.setForeground(Color.BLACK);
                    }
                }

                @Override
                public void focusLost(java.awt.event.FocusEvent e) {
                    if (searchField.getText().isEmpty()) {
                        searchField.setText("Type here to search");
                        searchField.setForeground(Color.GRAY);
                    }
                }
            });
            JButton searchButton = new JButton("Search");

            // Add home button and search components to the left panel
            leftPanel.add(homeButton);
            leftPanel.add(searchField);
            leftPanel.add(searchButton);

            add(leftPanel, BorderLayout.WEST);

            // Right: User Info & Actions
            JPanel rightActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
            rightActionPanel.setOpaque(false);

            welcomeLabel = new JLabel("Welcome");
            welcomeLabel.setForeground(Color.WHITE);
            welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            welcomeLabel.setVisible(false); // Hide by default in guest mode

            cartButton = new JButton("View Cart (0)");
            cartButton.setBackground(new Color(255, 165, 0)); // Orange
            cartButton.setForeground(Color.BLACK);
            cartButton.setFocusPainted(false);
            cartButton.addActionListener(e -> showCart());

            loginButton = new JButton("Log In");
            loginButton.addActionListener(e -> showCard("LOGIN"));

            myAccountButton = new JButton("My Account");
            myAccountButton.addActionListener(e -> MainFrame.this.openMyAccount());
            myAccountButton.setVisible(false); // Hidden by default in guest mode

            // Add components in strict order: Welcome -> Cart -> Auth (Login/MyAccount)
            rightActionPanel.add(welcomeLabel);
            rightActionPanel.add(cartButton);
            rightActionPanel.add(loginButton);
            rightActionPanel.add(myAccountButton);

            add(rightActionPanel, BorderLayout.EAST);

            // Search Actions
            searchButton.addActionListener(e -> {
                String text = searchField.getText().trim();
                if (!text.equals("Type here to search") && !text.isEmpty()) {
                    performSearch(text);
                }
            });

            searchField.addActionListener(e -> {
                String text = searchField.getText().trim();
                if (!text.equals("Type here to search") && !text.isEmpty()) {
                    performSearch(text);
                }
            });
        }

        public void updateUser(String username) {
            welcomeLabel.setText("Welcome, " + username);
            welcomeLabel.setVisible(true);
            loginButton.setVisible(false); // Hide login button when user logs in
            myAccountButton.setVisible(true);
        }

        public void updateCartCount(int count) {
            cartButton.setText("View Cart (" + count + ")");
        }

        public void resetToGuestMode() {
            welcomeLabel.setVisible(false);
            loginButton.setVisible(true); // Show login button in guest mode
            cartButton.setText("View Cart (0)");
            myAccountButton.setVisible(false);
        }

        public void showAuthButtons(boolean show) {
            cartButton.setVisible(show);
            loginButton.setVisible(show && !isLoggedIn()); // Only show login when not logged in
            myAccountButton.setVisible(show && isLoggedIn());
            welcomeLabel.setVisible(show && isLoggedIn());
        }

        public void clearSearchField() {
            searchField.setText("");
            if (!searchField.isFocusOwner()) {
                searchField.setText("Type here to search");
                searchField.setForeground(Color.GRAY);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}