package cl.duoc.vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class Ui {
    private Ui() {}

    @FunctionalInterface
    public interface Accion {
        void ejecutar() throws Exception;
    }

    public static void error(Component padre, Exception ex) {
        if (ex instanceof IllegalArgumentException || ex instanceof IllegalStateException) {
            // validaciones y reglas del negocio
            JOptionPane.showMessageDialog(padre, ex.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ex.printStackTrace();
        String detalle = (ex instanceof SQLException) ? "Error de base de datos: " : "Error inesperado: ";
        JOptionPane.showMessageDialog(padre, detalle + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void info(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Listo", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirmar(Component padre, String mensaje) {
        return JOptionPane.showConfirmDialog(padre, mensaje, "Confirmar", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION;
    }

    // Muestra el formulario. Si "guardar" falla, avisa y lo reabre CON lo que ya se escribió
    public static boolean formulario(Component padre, String titulo, JComponent contenido, Accion guardar) {
        while (true) {
            int r = JOptionPane.showConfirmDialog(padre, contenido, titulo,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (r != JOptionPane.OK_OPTION) {
                return false;
            }
            try {
                guardar.ejecutar();
                return true;
            } catch (Exception ex) {
                error(padre, ex);
            }
        }
    }

    public static JPanel campos(String[] etiquetas, JComponent[] componentes) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        for (int i = 0; i < etiquetas.length; i++) {
            panel.add(new JLabel(etiquetas[i]));
            panel.add(componentes[i]);
        }
        return panel;
    }

    public static DefaultTableModel modelo(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }
}
