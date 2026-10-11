package cl.duoc.vista;

import cl.duoc.controlador.PrestamoControlador;

import javax.swing.*;
import java.awt.*;

public class PanelHistorialPropio extends JPanel implements Actualizable {
    private final PrestamoControlador prestamos = new PrestamoControlador();
    private final TablaPrestamos tabla = new TablaPrestamos();

    public PanelHistorialPropio() {
        setLayout(new BorderLayout(5, 5));
        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> actualizar());
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barra.add(btnActualizar);
        add(barra, BorderLayout.NORTH);
        add(tabla, BorderLayout.CENTER);
        actualizar();
    }

    @Override
    public void actualizar() {
        Tareas.ejecutar(prestamos::miHistorial, tabla::mostrar, ex -> Ui.error(this, ex));
    }
}
