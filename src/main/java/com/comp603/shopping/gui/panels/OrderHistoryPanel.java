package com.comp603.shopping.gui.panels;

import com.comp603.shopping.gui.MainFrame;

import com.comp603.shopping.dao.OrderDAO;
import com.comp603.shopping.models.Order;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrderHistoryPanel extends JPanel {
    // Panel for displaying order history

    private MainFrame mainFrame;
    private OrderDAO orderDAO;
    private JTable orderTable;
    private DefaultTableModel tableModel;

    public OrderHistoryPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.orderDAO = new OrderDAO();
        setLayout(new BorderLayout());

        String[] columnNames = { "Order ID", "Date", "Total Amount", "Status" };
        tableModel = new DefaultTableModel(columnNames, 0);
        orderTable = new JTable(tableModel);

        add(new JScrollPane(orderTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton refreshButton = new JButton("Refresh");
        JButton cancelButton = new JButton("Cancel Order");
        buttonPanel.add(refreshButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadOrders();

        refreshButton.addActionListener(e -> loadOrders());
        cancelButton.addActionListener(e -> cancelSelectedOrder());
    }

    private void loadOrders() {
        tableModel.setRowCount(0);
        int userId = mainFrame.getAuthService().getCurrentUser().getUserId();
        List<Order> orders = orderDAO.getOrdersByUserId(userId);
        for (Order order : orders) {
            Object[] row = {
                    order.getOrderId(),
                    order.getOrderDate(),
                    order.getTotalAmount(),
                    order.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void cancelSelectedOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow >= 0) {
            int orderId = (int) tableModel.getValueAt(selectedRow, 0);
            String status = (String) tableModel.getValueAt(selectedRow, 3);

            if ("SHIPPED".equalsIgnoreCase(status) || "CANCELLED".equalsIgnoreCase(status)) {
                JOptionPane.showMessageDialog(this, "Cannot cancel order with status: " + status);
                return;
            }

            if (orderDAO.cancelOrder(orderId)) {
                JOptionPane.showMessageDialog(this, "Order Cancelled Successfully!");
                loadOrders();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to cancel order.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an order to cancel.");
        }
    }
}
