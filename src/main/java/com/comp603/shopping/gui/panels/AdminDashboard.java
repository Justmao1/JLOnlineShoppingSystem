package com.comp603.shopping.gui.panels;

import com.comp603.shopping.gui.MainFrame;
import com.comp603.shopping.gui.dialogs.ProductDialog;
import com.comp603.shopping.gui.dialogs.OrderDetailDialog;
import com.comp603.shopping.dao.OrderDAO;
import com.comp603.shopping.dao.ProductDAO;
import com.comp603.shopping.models.Order;
import com.comp603.shopping.models.Product;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AdminDashboard extends JPanel {

    private MainFrame mainFrame;
    private JTable productTable;
    private DefaultTableModel productTableModel;
    private ProductDAO productDAO;

    private JTable orderTable;
    private DefaultTableModel orderTableModel;
    private OrderDAO orderDAO;

    public AdminDashboard(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.productDAO = new ProductDAO();
        this.orderDAO = new OrderDAO();

        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutButton = new JButton("Logout");
        headerPanel.add(new JLabel("Admin Dashboard"));
        headerPanel.add(logoutButton);
        add(headerPanel, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Product Management", createProductPanel());
        tabbedPane.addTab("Order Management", createOrderPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // Actions
        logoutButton.addActionListener(e -> mainFrame.logout());
    }

    private JPanel createProductPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table
        String[] columnNames = { "Image", "ID", "Name", "Category", "Price", "Stock", "Sales" };
        productTableModel = new DefaultTableModel(columnNames, 0) {
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
        productTable = new JTable(productTableModel);
        productTable.setRowHeight(60);
        productTable.getColumnModel().getColumn(0).setCellRenderer(new ImageRenderer());

        loadProducts();

        panel.add(new JScrollPane(productTable), BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Product");
        JButton deleteButton = new JButton("Delete Product");
        JButton editButton = new JButton("Edit Product");

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Button Actions
        addButton.addActionListener(e -> {
            ProductDialog dialog = new ProductDialog(mainFrame, null);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                try {
                    productDAO.addProduct(dialog.getProduct());
                    loadProducts();
                    JOptionPane.showMessageDialog(this, "Product added!");
                } catch (java.sql.SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Failed to add product: " + ex.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = productTable.getSelectedRow();
            if (selectedRow >= 0) {
                int productId = (int) productTableModel.getValueAt(selectedRow, 1); // ID is at index 1
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete Product ID: " + productId + "?", "Confirm Delete",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        productDAO.deleteProduct(productId);
                        loadProducts();
                        JOptionPane.showMessageDialog(this, "Product deleted successfully.");
                    } catch (java.sql.SQLException ex) {
                        ex.printStackTrace();
                        String message = ex.getMessage();
                        if (message != null && (message.contains("foreign key") || message.contains("constraint"))) {
                            JOptionPane.showMessageDialog(this,
                                    "Cannot delete this product because it is associated with existing orders or other data.",
                                    "Deletion Failed",
                                    JOptionPane.WARNING_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(this,
                                    "Failed to delete product: " + ex.getMessage(), "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a product.");
            }
        });

        editButton.addActionListener(e -> {
            int selectedRow = productTable.getSelectedRow();
            if (selectedRow >= 0) {
                int productId = (int) productTableModel.getValueAt(selectedRow, 1); // ID is at index 1
                // Find product object
                List<Product> products = productDAO.getAllProducts();
                Product selectedProduct = products.stream().filter(p -> p.getProductId() == productId).findFirst()
                        .orElse(null);

                if (selectedProduct != null) {
                    ProductDialog dialog = new ProductDialog(mainFrame, selectedProduct);
                    dialog.setVisible(true);
                    if (dialog.isConfirmed()) {
                        try {
                            productDAO.updateProduct(dialog.getProduct());
                            loadProducts();
                            JOptionPane.showMessageDialog(this, "Product updated!");
                        } catch (java.sql.SQLException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(this, "Failed to update product: " + ex.getMessage(), "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a product.");
            }
        });

        return panel;
    }

    private JPanel createOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table
        String[] columnNames = { "Order ID", "Customer", "Total Amount", "Status", "Date", "Action" };
        orderTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Action column is editable for button click
            }
        };
        orderTable = new JTable(orderTableModel);

        orderTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        orderTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));

        loadOrders();

        panel.add(new JScrollPane(orderTable), BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton shipButton = new JButton("Ship Order");
        JButton refreshButton = new JButton("Refresh");

        buttonPanel.add(shipButton);
        buttonPanel.add(refreshButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Actions
        shipButton.addActionListener(e -> {
            int selectedRow = orderTable.getSelectedRow();
            if (selectedRow >= 0) {
                int orderId = (int) orderTableModel.getValueAt(selectedRow, 0);
                if (orderDAO.updateOrderStatus(orderId, "SHIPPED")) {
                    loadOrders();
                    JOptionPane.showMessageDialog(this, "Order marked as Shipped!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update order.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an order.");
            }
        });

        refreshButton.addActionListener(e -> loadOrders());

        return panel;
    }

    private void loadProducts() {
        productTableModel.setRowCount(0);
        List<Product> products = productDAO.getAllProducts();
        for (Product p : products) {
            // Load Image
            ImageIcon icon = null;
            if (p.getImagePath() != null) {
                java.io.File imgFile = new java.io.File(p.getImagePath());
                if (imgFile.exists()) {
                    ImageIcon originalIcon = new ImageIcon(p.getImagePath());
                    int iw = originalIcon.getIconWidth();
                    int ih = originalIcon.getIconHeight();
                    int maxW = 50;
                    int maxH = 50;
                    double r = Math.min((double) maxW / iw, (double) maxH / ih);
                    int nw = Math.max(1, (int) (iw * r));
                    int nh = Math.max(1, (int) (ih * r));
                    Image img = originalIcon.getImage().getScaledInstance(nw, nh, Image.SCALE_SMOOTH);
                    icon = new ImageIcon(img);
                }
            }

            Object[] row = {
                    icon,
                    p.getProductId(),
                    p.getName(),
                    p.getCategory(),
                    p.getPrice(),
                    p.getStockQuantity(),
                    p.getSalesVolume()
            };
            productTableModel.addRow(row);
        }
    }

    private void loadOrders() {
        orderTableModel.setRowCount(0);
        List<Order> orders = orderDAO.getAllOrders();
        for (Order o : orders) {
            Object[] row = {
                    o.getOrderId(),
                    o.getCustomerName(),
                    o.getTotalAmount(),
                    o.getStatus(),
                    o.getOrderDate(),
                    "View Details"
            };
            orderTableModel.addRow(row);
        }
    }

    // --- Renderers and Editors ---

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

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                int row = orderTable.getSelectedRow();
                int orderId = (int) orderTableModel.getValueAt(row, 0);

                // Find Order Object
                Order selectedOrder = orderDAO.getAllOrders().stream()
                        .filter(o -> o.getOrderId() == orderId)
                        .findFirst()
                        .orElse(null);

                if (selectedOrder != null) {
                    new OrderDetailDialog(mainFrame, selectedOrder).setVisible(true);
                }
            }
            isPushed = false;
            return label;
        }

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
}
