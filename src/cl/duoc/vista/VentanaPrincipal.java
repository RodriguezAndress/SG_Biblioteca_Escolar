package cl.duoc.vista;
import cl.duoc.controlador.AuthControlador;
import cl.duoc.model.Usuario;

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {
    private final CardLayout cards = new CardLayout();
    private final JPanel centro = new JPanel(cards);
    private final JPanel nav = new JPanel(new GridLayout(0, 1, 5, 5));

    public VentanaPrincipal(Usuario usuario) {
        setTitle("Biblioteca Escolar - " + usuario.getNombre() + " (bibliotecario)");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        nav.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        seccion("Inicio", "INICIO", new PanelInicio(usuario));
        seccion("Libros", "LIBROS", new PanelLibros(false));
        seccion("Estudiantes", "ESTUDIANTES", new PanelEstudiantes());
        seccion("Préstamos", "PRESTAMOS", new PanelPrestamos());
        seccion("Reportes", "REPORTES", new PanelReportes());

        JButton btnSalir = new JButton("Cerrar sesión");
        btnSalir.addActionListener(e -> cerrarSesion());
        nav.add(btnSalir);

        JPanel lateral = new JPanel(new BorderLayout());
        lateral.add(nav, BorderLayout.NORTH);
        add(lateral, BorderLayout.WEST);
        add(centro, BorderLayout.CENTER);
    }

    private <P extends JPanel & Actualizable> void seccion(String texto, String clave, P panel) {
        centro.add(panel, clave);
        JButton boton = new JButton(texto);
        boton.addActionListener(e -> {
            cards.show(centro, clave);
            panel.actualizar();
        });
        nav.add(boton);
    }

    private void cerrarSesion() {
        new AuthControlador().cerrarSesion();
        dispose();
        new VentanaLogin().setVisible(true);
    }
}
