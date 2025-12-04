package com.comp603.shopping;

import com.comp603.shopping.gui.MainFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Ensure GUI is created on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Set System Look and Feel for better native look
                javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());

                // Customize JTable selection colors
                javax.swing.UIManager.put("Table.selectionBackground", new java.awt.Color(230, 230, 230));
                javax.swing.UIManager.put("Table.selectionForeground", java.awt.Color.BLACK);
            } catch (Exception e) {
                e.printStackTrace();
            }

            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
