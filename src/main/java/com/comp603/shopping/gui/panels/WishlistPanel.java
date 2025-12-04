package com.comp603.shopping.gui.panels;

import com.comp603.shopping.gui.MainFrame;

import com.comp603.shopping.gui.components.ProductCard;

import com.comp603.shopping.dao.WishlistDAO;
import com.comp603.shopping.models.Product;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class WishlistPanel extends JPanel {

    private MainFrame mainFrame;
    private WishlistDAO wishlistDAO;
    private JPanel gridPanel;

    public WishlistPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.wishlistDAO = new WishlistDAO();
        setLayout(new BorderLayout());

        gridPanel = new JPanel(new GridLayout(0, 3, 10, 10)); // 3 columns
        add(new JScrollPane(gridPanel), BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh");
        add(refreshButton, BorderLayout.SOUTH);

        loadWishlist();

        refreshButton.addActionListener(e -> loadWishlist());
    }

    private void loadWishlist() {
        gridPanel.removeAll();
        int userId = mainFrame.getAuthService().getCurrentUser().getUserId();
        List<Product> products = wishlistDAO.getWishlist(userId);

        for (Product product : products) {
            ProductCard card = new ProductCard(product, mainFrame);
            gridPanel.add(card);
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }
}
