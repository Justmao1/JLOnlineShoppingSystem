package com.comp603.shopping.gui.panels;

import com.comp603.shopping.gui.MainFrame;

import com.comp603.shopping.models.Product;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CartPanel extends JPanel {

    private MainFrame mainFrame;
    private DefaultTableModel tableModel;
    private JLabel totalLabel;

    public CartPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("Back to Products");
        headerPanel.add(backButton);
        add(headerPanel, BorderLayout.NORTH);

        // Cart Table
        String[] columnNames = { "Name", "Price", "Type" };
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable cartTable = new JTable(tableModel);
        add(new JScrollPane(cartTable), BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalLabel = new JLabel("Total: $0.00");
        JButton checkoutButton = new JButton("Checkout");

        bottomPanel.add(totalLabel);
        bottomPanel.add(checkoutButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Actions
        backButton.addActionListener(e -> mainFrame.showCard("PRODUCTS"));

        checkoutButton.addActionListener(e -> {
            if (mainFrame.getCart().getItems().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Cart is empty!");
                return;
            }
            // Create and show Checkout Panel dynamically to ensure latest cart data
            CheckoutPanel checkoutPanel = new CheckoutPanel(mainFrame);
            mainFrame.add(checkoutPanel, "CHECKOUT"); // Add to CardLayout
            mainFrame.showCard("CHECKOUT");
        });

        // Refresh data when panel is shown
        this.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent event) {
                refreshCart();
            }

            public void ancestorRemoved(javax.swing.event.AncestorEvent event) {
            }

            public void ancestorMoved(javax.swing.event.AncestorEvent event) {
            }
        });
    }

    private void refreshCart() {
        tableModel.setRowCount(0);
        java.util.List<com.comp603.shopping.models.CartItem> items = mainFrame.getCart().getItems();
        for (com.comp603.shopping.models.CartItem item : items) {
            Product p = item.getProduct();
            Object[] row = {
                    p.getName(),
                    String.format("$%.2f", p.getPrice()),
                    p instanceof com.comp603.shopping.models.PhysicalProduct ? "Physical" : "Digital"
            };
            tableModel.addRow(row);
        }
        totalLabel.setText(String.format("Total: $%.2f", mainFrame.getCart().getTotal()));
    }
}
