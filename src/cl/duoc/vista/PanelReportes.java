package cl.duoc.vista;

import cl.duoc.controlador.EstudianteControlador;
import cl.duoc.controlador.PrestamoControlador;
import cl.duoc.model.Estudiante;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PanelReportes extends JPanel implements Actualizable{
    private final PrestamoControlador prestamos = new PrestamoControlador();
    private final EstudianteControlador estudiantes = new EstudianteControlador();

    private final DefaultTableModel modeloRanking = Ui.modelo("Puesto", "Título", "Veces prestado");
    private final TablaPrestamos tablaActivos = new TablaPrestamos();
    private final TablaPrestamos tablaHistorial = new TablaPrestamos();
    private final JComboBox<Estudiante> cbEstudiante = new JComboBox<>();

    public PanelReportes() {
        setLayout(new BorderLayout(5, 5));

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Libros más prestados", new JScrollPane(new JTable(modeloRanking)));
        pestanas.addTab("Libros en préstamo", tablaActivos);

        JPanel historial = new JPanel(new BorderLayout(5, 5));
        JPanel barraHistorial = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnVer = new JButton("Ver historial");
        barraHistorial.add(new JLabel("Estudiante:"));
        barraHistorial.add(cbEstudiante);
        barraHistorial.add(btnVer);
        historial.add(barraHistorial, BorderLayout.NORTH);
        historial.add(tablaHistorial, BorderLayout.CENTER);
        pestanas.addTab("Historial por estudiante", historial);

        JButton btnActualizar = new JButton("Actualizar reportes");
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barra.add(btnActualizar);

        add(barra, BorderLayout.NORTH);
        add(pestanas, BorderLayout.CENTER);

        btnActualizar.addActionListener(e -> actualizar());
        btnVer.addActionListener(e -> cargarHistorial());
        actualizar();
    }

    @Override
    public void actualizar() {
        Tareas.ejecutar(() -> prestamos.masPrestados(10), filas -> {
            modeloRanking.setRowCount(0);
            int puesto = 1;
            for (Object[] fila : filas) {
                modeloRanking.addRow(new Object[]{puesto++, fila[1], fila[2]});
            }
        }, ex -> Ui.error(this, ex));

        Tareas.ejecutar(prestamos::activos, tablaActivos::mostrar, ex -> Ui.error(this, ex));

        Tareas.ejecutar(estudiantes::listar, lista -> {
            cbEstudiante.removeAllItems();
            lista.forEach(cbEstudiante::addItem);
        }, ex -> Ui.error(this, ex));
    }

    private void cargarHistorial() {
        Estudiante estudiante = (Estudiante) cbEstudiante.getSelectedItem();
        if (estudiante == null) {
            return;
        }
        Tareas.ejecutar(() -> prestamos.historial(estudiante.getId()),
                tablaHistorial::mostrar, ex -> Ui.error(this, ex));
    }
}
