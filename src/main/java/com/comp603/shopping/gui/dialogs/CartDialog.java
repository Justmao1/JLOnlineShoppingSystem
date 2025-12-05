package com.comp603.shopping.gui.dialogs;

import com.comp603.shopping.gui.MainFrame;

import com.comp603.shopping.models.CartItem;
import com.comp603.shopping.models.Product;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.AbstractCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CartDialog extends JDialog {

    private MainFrame mainFrame;
    private DefaultTableModel tableModel;
    private JLabel totalLabel;
    private JTable cartTable;

    public CartDialog(MainFrame mainFrame) {
        super(mainFrame, "Your Cart", true); // Modal
        this.mainFrame = mainFrame;

        setSize(800, 500);
        setLocationRelativeTo(mainFrame);
        setLayout(new BorderLayout());

        // Cart Table
        String[] columnNames = { "Image", "Name", "Price", "Quantity", "Total", "Action" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3 || column == 5; // Quantity and Action are editable
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0)
                    return ImageIcon.class;
                if (columnIndex == 3)
                    return Integer.class;
                return Object.class;
            }
        };

        cartTable = new JTable(tableModel);
        cartTable.setRowHeight(60); // Taller rows for images

        // Renderers and Editors
        cartTable.getColumnModel().getColumn(0).setCellRenderer(new ImageRenderer());
        cartTable.getColumnModel().getColumn(3).setCellRenderer(new QuantityRenderer());
        cartTable.getColumnModel().getColumn(3).setCellEditor(new QuantityEditor());
        cartTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        cartTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));

        add(new JScrollPane(cartTable), BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        JButton closeButton = new JButton("Close");
        JButton checkoutButton = new JButton("Checkout");

        bottomPanel.add(totalLabel);
        bottomPanel.add(closeButton);
        bottomPanel.add(checkoutButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Actions
        closeButton.addActionListener(e -> dispose());

        checkoutButton.addActionListener(e -> {
            if (mainFrame.getCart().getItems().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Cart is empty!");
                return;
            }
            dispose(); // Close dialog
            // Trigger checkout in MainFrame
            mainFrame.startCheckout();
        });

        refreshCart();
    }

    private void refreshCart() {
        tableModel.setRowCount(0);
        List<CartItem> items = mainFrame.getCart().getItems();
        for (CartItem item : items) {
            Product p = item.getProduct();

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
                    icon, // Column 0: Image
                    p.getName(), // Column 1: Name
                    String.format("$%.2f", p.getPrice()), // Column 2: Price
                    item.getQuantity(), // Column 3: Quantity
                    String.format("$%.2f", item.getTotalPrice()), // Column 4: Total
                    "Remove" // Column 5: Action
            };
            tableModel.addRow(row);
        }
        updateTotalLabel();
    }

    private void updateTotalLabel() {
        totalLabel.setText(String.format("Total: $%.2f", mainFrame.getCart().getTotal()));
    }

    // --- Inner Classes for Table Customization ---

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

    // Panel with [-] [Value] [+]
    class QuantityPanel extends JPanel {
        JButton minusBtn;
        JLabel valueLabel;
        JButton plusBtn;

        public QuantityPanel() {
            setLayout(new BorderLayout());
            setOpaque(true);

            minusBtn = new JButton("-");
            plusBtn = new JButton("+");
            valueLabel = new JLabel("0", SwingConstants.CENTER);

            // Style buttons
            minusBtn.setFocusable(false);
            plusBtn.setFocusable(false);
            minusBtn.setPreferredSize(new Dimension(40, 0)); // Fixed width for hit detection
            plusBtn.setPreferredSize(new Dimension(40, 0));

            add(minusBtn, BorderLayout.WEST);
            add(valueLabel, BorderLayout.CENTER);
            add(plusBtn, BorderLayout.EAST);
        }

        public void setQuantity(int q) {
            valueLabel.setText(String.valueOf(q));
        }
    }

    class QuantityRenderer implements TableCellRenderer {
        QuantityPanel panel = new QuantityPanel();

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            int q = (value instanceof Integer) ? (Integer) value : 1;
            panel.setQuantity(q);

            if (isSelected) {
                panel.setBackground(table.getSelectionBackground());
                panel.valueLabel.setForeground(table.getSelectionForeground());
            } else {
                panel.setBackground(table.getBackground());
                panel.valueLabel.setForeground(table.getForeground());
            }
            return panel;
        }
    }

    class QuantityEditor extends AbstractCellEditor implements TableCellEditor {
        QuantityPanel panel = new QuantityPanel();
        int currentQuantity;

        public QuantityEditor() {
            // No listeners needed here as we handle clicks in isCellEditable
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column) {
            currentQuantity = (value instanceof Integer) ? (Integer) value : 1;
            panel.setQuantity(currentQuantity);
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return currentQuantity;
        }

        @Override
        public boolean isCellEditable(java.util.EventObject e) {
            if (e instanceof java.awt.event.MouseEvent) {
                java.awt.event.MouseEvent me = (java.awt.event.MouseEvent) e;
                JTable table = (JTable) me.getSource();
                int row = table.rowAtPoint(me.getPoint());
                int col = table.columnAtPoint(me.getPoint());

                if (col == 3) { // Quantity column
                    Rectangle cellRect = table.getCellRect(row, col, true);
                    Point p = me.getPoint();

                    // Translate to cell coordinates
                    int relativeX = p.x - cellRect.x;

                    // Check hit zones (assuming 40px buttons as defined in QuantityPanel)
                    if (relativeX < 40) {
                        // Minus Clicked
                        updateQuantity(row, -1);
                        return false; // Consume event, don't start editing
                    } else if (relativeX > cellRect.width - 40) {
                        // Plus Clicked
                        updateQuantity(row, 1);
                        return false; // Consume event, don't start editing
                    }
                }
            }
            return false; // Don't allow full editing (typing), just buttons
        }

        private void updateQuantity(int row, int delta) {
            if (row < 0 || row >= mainFrame.getCart().getItems().size())
                return;

            CartItem item = mainFrame.getCart().getItems().get(row);
            int currentQ = item.getQuantity();
            int newQ = currentQ + delta;

            if (newQ < 1)
                return; // Minimum 1

            // Validate Stock
            int currentStock = item.getProduct().getStockQuantity();
            // Ideally refresh stock from DB, but using cached for speed/simplicity as per
            // previous logic

            if (newQ > currentStock) {
                JOptionPane.showMessageDialog(null, "Only " + currentStock + " left in stock!", "Stock Limit",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Update DB
            int userId = mainFrame.getAuthService().getCurrentUser().getUserId();
            new com.comp603.shopping.dao.ShoppingCartDAO().updateQuantity(userId, item.getProduct().getProductId(),
                    newQ);

            // Update Model
            item.setQuantity(newQ);

            // Refresh UI
            tableModel.setValueAt(newQ, row, 3);
            tableModel.setValueAt(String.format("$%.2f", item.getTotalPrice()), row, 4);
            updateTotalLabel();
            mainFrame.updateCartCount();
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
                int row = cartTable.getSelectedRow();
                if (row >= 0 && row < mainFrame.getCart().getItems().size()) {
                    CartItem item = mainFrame.getCart().getItems().get(row);

                    // Remove from DB
                    mainFrame.getCart().removeProduct(item.getProduct());

                    // Refresh UI
                    refreshCart();
                    mainFrame.updateCartCount();
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
