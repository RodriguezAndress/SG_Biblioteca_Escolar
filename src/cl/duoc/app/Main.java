package cl.duoc.app;

import cl.duoc.vista.VentanaLogin;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignorada) { }
        SwingUtilities.invokeLater(() -> new VentanaLogin().setVisible(true));
    }
}