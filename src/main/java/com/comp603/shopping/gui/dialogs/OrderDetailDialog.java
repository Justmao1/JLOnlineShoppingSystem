package com.comp603.shopping.gui.dialogs;

import com.comp603.shopping.dao.OrderDAO;
import com.comp603.shopping.models.Order;
import com.comp603.shopping.models.OrderItem;
import com.comp603.shopping.models.Product;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrderDetailDialog extends JDialog {

    public OrderDetailDialog(JFrame parent, Order order) {
        super(parent, "Order Details - #" + order.getOrderId(), true);
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        headerPanel.setBackground(new Color(245, 245, 245));

        headerPanel.add(new JLabel("Order ID: " + order.getOrderId()));
        headerPanel.add(new JLabel("Customer: " + order.getCustomerName()));
        headerPanel.add(new JLabel("Date: " + order.getOrderDate()));
        headerPanel.add(new JLabel("Status: " + order.getStatus()));

        add(headerPanel, BorderLayout.NORTH);

        // Items Table
        String[] columnNames = { "Image", "Product Name", "Unit Price", "Quantity", "Subtotal" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0)
                    return ImageIcon.class;
                return Object.class;
            }
        };

        JTable itemsTable = new JTable(tableModel);
        itemsTable.setRowHeight(60);
        itemsTable.getColumnModel().getColumn(0).setCellRenderer(new ImageRenderer());

        // Load Items
        OrderDAO orderDAO = new OrderDAO();
        List<OrderItem> items = orderDAO.getOrderDetails(order.getOrderId());

        for (OrderItem item : items) {
            Product p = item.getProduct();

            // Load Image
            ImageIcon icon = null;
            if (p != null && p.getImagePath() != null) {
                java.io.File imgFile = new java.io.File(p.getImagePath());
                if (imgFile.exists()) {
                    ImageIcon originalIcon = new ImageIcon(p.getImagePath());
                    Image img = originalIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                    icon = new ImageIcon(img);
                }
            }

            Object[] row = {
                    icon,
                    p != null ? p.getName() : "Unknown Product",
                    String.format("$%.2f", item.getPriceAtPurchase()),
                    item.getQuantity(),
                    String.format("$%.2f", item.getPriceAtPurchase() * item.getQuantity())
            };
            tableModel.addRow(row);
        }

        add(new JScrollPane(itemsTable), BorderLayout.CENTER);

        // Footer Panel
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel totalLabel = new JLabel("Total Amount: " + String.format("$%.2f", order.getTotalAmount()));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());

        footerPanel.add(totalLabel, BorderLayout.NORTH);
        footerPanel.add(closeButton, BorderLayout.SOUTH);

        add(footerPanel, BorderLayout.SOUTH);
    }

    // Reuse ImageRenderer logic
    class ImageRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            JLabel label = new JLabel();
            label.setHorizontalAlignment(JLabel.CENTER);
            if (value instanceof ImageIcon) {
                label.setIcon((ImageIcon) value);
            } else {
                label.setText("No Image");
            }
            return label;
        }
    }
}
