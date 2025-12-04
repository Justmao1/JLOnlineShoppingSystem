package com.comp603.shopping.gui.dialogs;

import com.comp603.shopping.gui.MainFrame;
import com.comp603.shopping.gui.panels.OrderHistoryPanel;
import com.comp603.shopping.gui.panels.ProfilePanel;
import com.comp603.shopping.gui.panels.WalletPanel;
import com.comp603.shopping.gui.panels.WishlistPanel;

import javax.swing.*;
import java.awt.*;

public class MyAccountDialog extends JDialog {

    private MainFrame mainFrame;

    public MyAccountDialog(MainFrame mainFrame) {
        super(mainFrame, "My Account", true);
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());
        setSize(800, 600);
        setLocationRelativeTo(mainFrame);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Profile", new ProfilePanel(mainFrame));
        tabbedPane.addTab("Order History", new OrderHistoryPanel(mainFrame));
        tabbedPane.addTab("Wallet", new WalletPanel(mainFrame));
        tabbedPane.addTab("Wishlist", new WishlistPanel(mainFrame));

        add(tabbedPane, BorderLayout.CENTER);

        // Logout Button at the bottom
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            dispose();
            mainFrame.logout();
        });
        bottomPanel.add(logoutButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}
