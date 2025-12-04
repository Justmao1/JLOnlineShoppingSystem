package com.comp603.shopping.gui.panels;

import com.comp603.shopping.events.PaymentMethodListener;
import com.comp603.shopping.events.PaymentMethodManager;
import com.comp603.shopping.gui.MainFrame;

import com.comp603.shopping.dao.OrderDAO;
import com.comp603.shopping.dao.PaymentMethodDAO;
import com.comp603.shopping.dao.ProductDAO;
import com.comp603.shopping.models.Order;
import com.comp603.shopping.models.OrderItem;
import com.comp603.shopping.services.CreditCardStrategy;
import com.comp603.shopping.services.PaymentStrategy;
import com.comp603.shopping.services.WalletStrategy;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class CheckoutPanel extends JPanel implements PaymentMethodListener {

    private MainFrame mainFrame;
    private JComboBox<String> paymentMethodCombo;
    private JPanel cardPanel;
    private JPanel walletPanel;
    private PaymentMethodDAO paymentMethodDAO;
    
    // Card Fields
    private DefaultListModel<PaymentMethodDAO.PaymentMethod> cardListModel;
    private JList<PaymentMethodDAO.PaymentMethod> cardList;
    private JButton addCardButton;

    // Wallet Fields
    private JLabel balanceLabel;

    public CheckoutPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.paymentMethodDAO = new PaymentMethodDAO();
        setLayout(new BorderLayout());

        // Register as a listener for payment method changes
        PaymentMethodManager.getInstance().addListener(this);

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
        cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBorder(BorderFactory.createTitledBorder("Card Details"));
        
        // Card List
        cardListModel = new DefaultListModel<>();
        cardList = new JList<>(cardListModel);
        cardList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cardList.setVisibleRowCount(5);
        loadExistingCards();
        
        JScrollPane scrollPane = new JScrollPane(cardList);
        cardPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addCardButton = new JButton("Add Card");
        buttonPanel.add(addCardButton);
        cardPanel.add(buttonPanel, BorderLayout.SOUTH);

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
        
        addCardButton.addActionListener(e -> showAddCardDialog());
        
        payButton.addActionListener(e -> processPayment());
    }
    
    private void loadExistingCards() {
        cardListModel.clear();
        int userId = mainFrame.getAuthService().getCurrentUser().getUserId();
        List<PaymentMethodDAO.PaymentMethod> paymentMethods = paymentMethodDAO.getPaymentMethods(userId);
        
        for (PaymentMethodDAO.PaymentMethod method : paymentMethods) {
            cardListModel.addElement(method);
        }
    }
    
    private void showAddCardDialog() {
        JPanel panel = new JPanel(new GridLayout(6, 1));
        JTextField cardField = new JTextField();
        JTextField cvvField = new JTextField();
        JTextField expiryField = new JTextField();
        
        panel.add(new JLabel("Card Number: (16 digits)"));
        panel.add(cardField);
        panel.add(new JLabel("CVV: (3 or 4 digits)"));
        panel.add(cvvField);
        panel.add(new JLabel("Expiry (MM/YY):"));
        panel.add(expiryField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Card", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String card = cardField.getText().trim();
            String cvv = cvvField.getText().trim();
            String expiry = expiryField.getText().trim();
            
            // Validate card information
            if (!isValidCardInformation(card, cvv, expiry)) {
                JOptionPane.showMessageDialog(this, 
                    "Invalid card information. Please check your card details.", 
                    "Invalid Information", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!card.isEmpty() && !expiry.isEmpty()) {
                int userId = mainFrame.getAuthService().getCurrentUser().getUserId();
                if (paymentMethodDAO.addPaymentMethod(userId, card, expiry)) {
                    loadExistingCards(); // Refresh the list
                    // Notify other panels of the change
                    PaymentMethodManager.getInstance().notifyPaymentMethodsChanged();
                    JOptionPane.showMessageDialog(this, "Card Added!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add card.");
                }
            }
        }
    }
    
    private boolean isValidCardInformation(String cardNumber, String cvv, String expiryDate) {
        return isValidCardNumber(cardNumber) && isValidCVV(cvv) && isValidExpiryDate(expiryDate);
    }

    private boolean isValidCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            return false;
        }
        
        // Remove dashes and spaces for validation
        String cleanCardNumber = cardNumber.replaceAll("[\\s-]", "");
        
        // Check if it's all digits and has 16 digits (standard credit card length)
        return cleanCardNumber.matches("\\d{16}");
    }
    
    private boolean isValidCVV(String cvv) {
        if (cvv == null || cvv.isEmpty()) {
            return false;
        }
        
        // CVV should be 3 or 4 digits
        return cvv.matches("\\d{3,4}");
    }
    
    private boolean isValidExpiryDate(String expiryDate) {
        if (expiryDate == null || expiryDate.isEmpty()) {
            return false;
        }
        
        // Check format MM/YY
        if (!expiryDate.matches("\\d{2}/\\d{2}")) {
            return false;
        }
        
        // Parse month and year
        String[] parts = expiryDate.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]);
        
        // Check valid month (01-12)
        if (month < 1 || month > 12) {
            return false;
        }
        
        // Get current date
        java.time.LocalDate currentDate = java.time.LocalDate.now();
        int currentYear = currentDate.getYear() % 100; // Last two digits of year
        int currentMonth = currentDate.getMonthValue();
        
        // As per requirements, expiry date must be current month or later
        // If year is before current year, it's invalid
        if (year < currentYear) {
            return false;
        }
        
        // If year is current year but month is before current month, it's invalid
        if (year == currentYear && month < currentMonth) {
            return false;
        }
        
        return true;
    }

    private void processPayment() {
        String method = (String) paymentMethodCombo.getSelectedItem();
        double amount = mainFrame.getCart().getTotal();
        PaymentStrategy strategy = null;

        if ("Credit Card".equals(method)) {
            PaymentMethodDAO.PaymentMethod selectedMethod = cardList.getSelectedValue();
            
            if (selectedMethod == null) {
                JOptionPane.showMessageDialog(this, 
                    "No card selected. Please select a card or add a new one.", 
                    "No Card Selected", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // For credit card strategy, we'll use placeholder values since we're not storing them
            // In a real application, you would decrypt/store securely the actual card details
            strategy = new CreditCardStrategy(
                    "User Name", // Simplified
                    "1234567890123456", // Placeholder
                    "123", // Placeholder
                    selectedMethod.getExpiryDate());
        } else {
            strategy = new WalletStrategy(mainFrame.getAuthService().getCurrentUser());
        }

        if (strategy.pay(amount)) {
            // Payment Successful -> Create Order
            if (createOrder()) {
                JOptionPane.showMessageDialog(this, "Payment Successful! Order Placed.");
                mainFrame.getCart().clear();
                mainFrame.updateCartCount();
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
    
    @Override
    public void onPaymentMethodsChanged() {
        // Reload data when notified of changes
        loadExistingCards();
    }
    
    @Override
    public void removeNotify() {
        // Unregister when panel is removed
        PaymentMethodManager.getInstance().removeListener(this);
        super.removeNotify();
    }
}