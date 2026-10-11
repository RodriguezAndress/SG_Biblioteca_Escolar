package cl.duoc.vista;

import cl.duoc.controlador.LibroControlador;
import cl.duoc.controlador.PrestamoControlador;
import cl.duoc.model.Usuario;

import javax.swing.*;
import java.awt.*;

public class PanelInicio extends JPanel implements Actualizable{
    private final LibroControlador libros = new LibroControlador();
    private final PrestamoControlador prestamos = new PrestamoControlador();
    private final JLabel lblResumen = new JLabel(" ", SwingConstants.CENTER);

    public PanelInicio(Usuario usuario) {
        setLayout(new GridBagLayout());
        JLabel bienvenida = new JLabel("Bienvenido/a, " + usuario.getNombre(), SwingConstants.CENTER);
        bienvenida.setFont(bienvenida.getFont().deriveFont(Font.BOLD, 22f));

        JPanel caja = new JPanel(new GridLayout(0, 1, 10, 10));
        caja.add(bienvenida);
        caja.add(lblResumen);
        add(caja);
        actualizar();
    }

    @Override
    public void actualizar() {
        Tareas.ejecutar(() -> new int[]{
                        libros.listar().size(),
                        prestamos.activos().size(),
                        prestamos.atrasados().size()},
                r -> lblResumen.setText("<html><div style='text-align:center'>Libros en catálogo: " + r[0]
                        + "<br>Préstamos activos: " + r[1]
                        + "<br>Préstamos atrasados: " + r[2] + "</div></html>"),
                ex -> Ui.error(this, ex));
    }
}
