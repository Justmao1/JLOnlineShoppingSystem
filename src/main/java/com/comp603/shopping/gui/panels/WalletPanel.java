package com.comp603.shopping.gui.panels;

import com.comp603.shopping.gui.MainFrame;

import com.comp603.shopping.dao.PaymentMethodDAO;
import com.comp603.shopping.dao.UserDAO;
import com.comp603.shopping.models.User;
import javax.swing.*;
import java.awt.*;

public class WalletPanel extends JPanel {

    private MainFrame mainFrame;
    private UserDAO userDAO;
    private PaymentMethodDAO paymentMethodDAO;
    private JLabel balanceLabel;
    private DefaultListModel<String> paymentListModel;

    public WalletPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.userDAO = new UserDAO();
        this.paymentMethodDAO = new PaymentMethodDAO();
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

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
        JList<String> paymentList = new JList<>(paymentListModel);
        paymentPanel.add(new JScrollPane(paymentList), BorderLayout.CENTER);

        JPanel addCardPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addCardButton = new JButton("Add New Card");
        addCardPanel.add(addCardButton);
        paymentPanel.add(addCardPanel, BorderLayout.SOUTH);

        add(paymentPanel, BorderLayout.CENTER);

        loadData();

        topUpButton.addActionListener(e -> topUp());
        addCardButton.addActionListener(e -> addCard());
    }

    private void loadData() {
        User user = mainFrame.getAuthService().getCurrentUser();
        if (user != null) {
            balanceLabel.setText(String.format("Current Balance: $%.2f", user.getBalance()));

            paymentListModel.clear();
            for (String method : paymentMethodDAO.getPaymentMethods(user.getUserId())) {
                paymentListModel.addElement(method);
            }
        }
    }

    private void topUp() {
        String amountStr = JOptionPane.showInputDialog(this, "Enter amount to top up:");
        if (amountStr != null) {
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount > 0) {
                    User user = mainFrame.getAuthService().getCurrentUser();
                    double newBalance = user.getBalance() + amount;
                    if (userDAO.updateUserBalance(user.getUserId(), newBalance)) {
                        user.setBalance(newBalance);
                        loadData();
                        JOptionPane.showMessageDialog(this, "Top Up Successful!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update balance.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid amount.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid number.");
            }
        }
    }

    private void addCard() {
        JPanel panel = new JPanel(new GridLayout(2, 2));
        JTextField cardField = new JTextField();
        JTextField expiryField = new JTextField();
        panel.add(new JLabel("Card Number:"));
        panel.add(cardField);
        panel.add(new JLabel("Expiry (MM/YY):"));
        panel.add(expiryField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Card", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String card = cardField.getText();
            String expiry = expiryField.getText();
            if (!card.isEmpty() && !expiry.isEmpty()) {
                int userId = mainFrame.getAuthService().getCurrentUser().getUserId();
                if (paymentMethodDAO.addPaymentMethod(userId, card, expiry)) {
                    loadData();
                    JOptionPane.showMessageDialog(this, "Card Added!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add card.");
                }
            }
        }
    }
}
