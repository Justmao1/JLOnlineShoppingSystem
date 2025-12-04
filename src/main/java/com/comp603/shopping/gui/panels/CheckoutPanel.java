package com.comp603.shopping.gui.panels;

import com.comp603.shopping.gui.MainFrame;

import com.comp603.shopping.dao.OrderDAO;
import com.comp603.shopping.dao.ProductDAO;
import com.comp603.shopping.models.Order;
import com.comp603.shopping.models.OrderItem;
import com.comp603.shopping.services.CreditCardStrategy;
import com.comp603.shopping.services.PaymentStrategy;
import com.comp603.shopping.services.WalletStrategy;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CheckoutPanel extends JPanel {

    private MainFrame mainFrame;
    private JComboBox<String> paymentMethodCombo;
    private JPanel cardPanel;
    private JPanel walletPanel;

    // Card Fields
    private JTextField cardNumberField;
    private JTextField cvvField;
    private JTextField expiryField;

    // Wallet Fields
    private JLabel balanceLabel;

    public CheckoutPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("Back to Cart");
        headerPanel.add(backButton);
        add(headerPanel, BorderLayout.NORTH);

        // Center Content
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Total Amount
        double total = mainFrame.getCart().getTotal();
        JLabel totalLabel = new JLabel(String.format("Total Amount: $%.2f", total));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        centerPanel.add(totalLabel, gbc);

        // Payment Method Selection
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        centerPanel.add(new JLabel("Select Payment Method:"), gbc);

        String[] methods = { "Credit Card", "Wallet" };
        paymentMethodCombo = new JComboBox<>(methods);
        gbc.gridx = 1;
        centerPanel.add(paymentMethodCombo, gbc);

        // Card Details Panel
        cardPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        cardPanel.setBorder(BorderFactory.createTitledBorder("Card Details"));
        cardPanel.add(new JLabel("Card Number:"));
        cardNumberField = new JTextField();
        cardPanel.add(cardNumberField);
        cardPanel.add(new JLabel("CVV:"));
        cvvField = new JTextField();
        cardPanel.add(cvvField);
        cardPanel.add(new JLabel("Expiry (MM/YY):"));
        expiryField = new JTextField();
        cardPanel.add(expiryField);

        // Wallet Details Panel
        walletPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        walletPanel.setBorder(BorderFactory.createTitledBorder("Wallet Details"));
        double balance = mainFrame.getAuthService().getCurrentUser().getBalance();
        balanceLabel = new JLabel(String.format("Current Balance: $%.2f", balance));
        walletPanel.add(balanceLabel);

        // Add Dynamic Panel Area
        JPanel dynamicArea = new JPanel(new CardLayout());
        dynamicArea.add(cardPanel, "Credit Card");
        dynamicArea.add(walletPanel, "Wallet");

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        centerPanel.add(dynamicArea, gbc);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel();
        JButton payButton = new JButton("Confirm Payment");
        bottomPanel.add(payButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Listeners
        backButton.addActionListener(e -> mainFrame.showCart());

        paymentMethodCombo.addActionListener(e -> {
            CardLayout cl = (CardLayout) dynamicArea.getLayout();
            cl.show(dynamicArea, (String) paymentMethodCombo.getSelectedItem());
        });

        payButton.addActionListener(e -> processPayment());
    }

    private void processPayment() {
        String method = (String) paymentMethodCombo.getSelectedItem();
        double amount = mainFrame.getCart().getTotal();
        PaymentStrategy strategy = null;

        if ("Credit Card".equals(method)) {
            strategy = new CreditCardStrategy(
                    "User Name", // Simplified
                    cardNumberField.getText(),
                    cvvField.getText(),
                    expiryField.getText());
        } else {
            strategy = new WalletStrategy(mainFrame.getAuthService().getCurrentUser());
        }

        if (strategy.pay(amount)) {
            // Payment Successful -> Create Order
            if (createOrder()) {
                JOptionPane.showMessageDialog(this, "Payment Successful! Order Placed.");
                mainFrame.getCart().clear();
                mainFrame.showCard("PRODUCTS");
            } else {
                JOptionPane.showMessageDialog(this, "Payment processed but Order creation failed!", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Payment Failed! Check details or balance.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean createOrder() {
        Order order = new Order();
        order.setUserId(mainFrame.getAuthService().getCurrentUser().getUserId());
        order.setTotalAmount(mainFrame.getCart().getTotal());
        order.setStatus("PAID");

        List<OrderItem> orderItems = new ArrayList<>();
        for (com.comp603.shopping.models.CartItem cartItem : mainFrame.getCart().getItems()) {
            OrderItem item = new OrderItem();
            item.setProductId(cartItem.getProduct().getProductId());
            item.setQuantity(cartItem.getQuantity());
            item.setPriceAtPurchase(cartItem.getProduct().getPrice());
            orderItems.add(item);
        }
        order.setItems(orderItems);

        OrderDAO orderDAO = new OrderDAO();
        if (orderDAO.createOrder(order)) {
            // Update Stock
            ProductDAO productDAO = new ProductDAO();
            boolean stockUpdated = true;
            for (com.comp603.shopping.models.CartItem cartItem : mainFrame.getCart().getItems()) {
                if (!productDAO.decreaseStock(cartItem.getProduct().getProductId(), cartItem.getQuantity())) {
                    stockUpdated = false;
                    // Ideally rollback order here, but for simplicity just flag error
                    System.err.println("Failed to decrease stock for product: " + cartItem.getProduct().getName());
                }
            }

            if (stockUpdated) {
                mainFrame.refreshView();
                return true;
            }
            return false; // Or partial success handling
        }
        return false;
    }
}
