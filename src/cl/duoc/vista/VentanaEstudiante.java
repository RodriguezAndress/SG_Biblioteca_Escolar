package cl.duoc.vista;

import cl.duoc.controlador.AuthControlador;
import cl.duoc.model.Usuario;

import javax.swing.*;
import java.awt.*;

public class VentanaEstudiante extends JFrame {
    public VentanaEstudiante(Usuario usuario) {
        setTitle("Biblioteca Escolar - " + usuario.getNombre() + " (estudiante)");
        setSize(900, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        PanelHistorialPropio historial = new PanelHistorialPropio();
        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Catálogo", new PanelLibros(true));    // solo lectura
        pestanas.addTab("Mis préstamos", historial);
        pestanas.addChangeListener(e -> {
            if (pestanas.getSelectedComponent() == historial) {
                historial.actualizar();
            }
        });

        JButton btnSalir = new JButton("Cerrar sesión");
        btnSalir.addActionListener(e -> {
            new AuthControlador().cerrarSesion();
            dispose();
            new VentanaLogin().setVisible(true);
        });
        JPanel sur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sur.add(btnSalir);

        add(pestanas, BorderLayout.CENTER);
        add(sur, BorderLayout.SOUTH);
    }
}
