package com.comp603.shopping.gui.panels;

import com.comp603.shopping.events.PaymentMethodListener;
import com.comp603.shopping.events.PaymentMethodManager;
import com.comp603.shopping.gui.MainFrame;

import com.comp603.shopping.dao.PaymentMethodDAO;
import com.comp603.shopping.dao.UserDAO;
import com.comp603.shopping.models.User;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class WalletPanel extends JPanel implements PaymentMethodListener {

    private MainFrame mainFrame;
    private UserDAO userDAO;
    private PaymentMethodDAO paymentMethodDAO;
    private JLabel balanceLabel;
    private DefaultListModel<PaymentMethodDAO.PaymentMethod> paymentListModel;
    private JList<PaymentMethodDAO.PaymentMethod> paymentList;

    public WalletPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.userDAO = new UserDAO();
        this.paymentMethodDAO = new PaymentMethodDAO();
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Register as a listener for payment method changes
        PaymentMethodManager.getInstance().addListener(this);

        // Balance Section
        JPanel balancePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        balanceLabel = new JLabel("Current Balance: $0.00");
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 18));
        JButton topUpButton = new JButton("Top Up");
        balancePanel.add(balanceLabel);
        balancePanel.add(topUpButton);
        add(balancePanel, BorderLayout.NORTH);

        // Payment Methods Section
        JPanel paymentPanel = new JPanel(new BorderLayout());
        paymentPanel.setBorder(BorderFactory.createTitledBorder("Saved Payment Methods"));
        paymentListModel = new DefaultListModel<>();
        paymentList = new JList<>(paymentListModel);
        paymentPanel.add(new JScrollPane(paymentList), BorderLayout.CENTER);

        // Button panel for payment methods
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addCardButton = new JButton("Add New Card");
        JButton deleteCardButton = new JButton("Delete Card");
        buttonPanel.add(addCardButton);
        buttonPanel.add(deleteCardButton);
        paymentPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(paymentPanel, BorderLayout.CENTER);

        loadData();

        topUpButton.addActionListener(e -> topUp());
        addCardButton.addActionListener(e -> addCard());
        deleteCardButton.addActionListener(e -> deleteCard());
    }

    private void loadData() {
        User user = mainFrame.getAuthService().getCurrentUser();
        if (user != null) {
            balanceLabel.setText(String.format("Current Balance: $%.2f", user.getBalance()));

            paymentListModel.clear();
            for (PaymentMethodDAO.PaymentMethod method : paymentMethodDAO.getPaymentMethods(user.getUserId())) {
                paymentListModel.addElement(method);
            }
        }
    }

    private void topUp() {
        // Check if there are any cards available
        if (paymentListModel.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No credit cards available. Please add a credit card before topping up your account.", 
                "No Credit Card", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create a dialog for top-up with card selection
        JPanel panel = new JPanel(new GridLayout(2, 2));
        JTextField amountField = new JTextField();
        
        panel.add(new JLabel("Amount:"));
        panel.add(amountField);
        panel.add(new JLabel("Select Card:"));
        
        // Create a combo box with available cards
        JComboBox<PaymentMethodDAO.PaymentMethod> cardCombo = new JComboBox<>();
        for (int i = 0; i < paymentListModel.size(); i++) {
            cardCombo.addItem(paymentListModel.getElementAt(i));
        }
        panel.add(cardCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Top Up Account", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String amountStr = amountField.getText().trim();
            
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Amount must be greater than zero.", "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                PaymentMethodDAO.PaymentMethod selectedCard = (PaymentMethodDAO.PaymentMethod) cardCombo.getSelectedItem();
                if (selectedCard == null) {
                    JOptionPane.showMessageDialog(this, "Please select a card.", "No Card Selected", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Process the top-up
                User user = mainFrame.getAuthService().getCurrentUser();
                double newBalance = user.getBalance() + amount;
                if (userDAO.updateUserBalance(user.getUserId(), newBalance)) {
                    user.setBalance(newBalance);
                    loadData();
                    JOptionPane.showMessageDialog(this, "Top Up Successful!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update balance.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount format.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addCard() {
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
            
            // Validate card information using the same logic as in CheckoutPanel
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
                    loadData();
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

    private void deleteCard() {
        PaymentMethodDAO.PaymentMethod selectedMethod = paymentList.getSelectedValue();
        if (selectedMethod == null) {
            JOptionPane.showMessageDialog(this, "Please select a card to delete.", "No Card Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this card?\n" + selectedMethod.toString(), 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            if (paymentMethodDAO.deletePaymentMethod(selectedMethod.getId())) {
                paymentListModel.removeElement(selectedMethod);
                // Notify other panels of the change
                PaymentMethodManager.getInstance().notifyPaymentMethodsChanged();
                JOptionPane.showMessageDialog(this, "Card deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete card.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    @Override
    public void onPaymentMethodsChanged() {
        // Reload data when notified of changes
        loadData();
    }
    
    @Override
    public void removeNotify() {
        // Unregister when panel is removed
        PaymentMethodManager.getInstance().removeListener(this);
        super.removeNotify();
    }
}