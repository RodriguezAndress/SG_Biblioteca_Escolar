package cl.duoc.vista;

import cl.duoc.controlador.EstudianteControlador;
import cl.duoc.controlador.LibroControlador;
import cl.duoc.controlador.PrestamoControlador;
import cl.duoc.model.Estudiante;
import cl.duoc.model.Libro;
import cl.duoc.model.Prestamo;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class PanelPrestamos extends JPanel implements Actualizable{
    private final PrestamoControlador prestamos = new PrestamoControlador();
    private final EstudianteControlador estudiantes = new EstudianteControlador();
    private final LibroControlador libros = new LibroControlador();

    private final TablaPrestamos tabla = new TablaPrestamos();
    private final JComboBox<String> cbFiltro = new JComboBox<>(new String[]{"Todos", "Activos", "Atrasados"});

    public PanelPrestamos() {
        setLayout(new BorderLayout(5, 5));

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNuevo = new JButton("Nuevo préstamo");
        JButton btnDevolver = new JButton("Registrar devolución");
        JButton btnActualizar = new JButton("Actualizar");
        barra.add(btnNuevo);
        barra.add(btnDevolver);
        barra.add(new JLabel("Mostrar:"));
        barra.add(cbFiltro);
        barra.add(btnActualizar);

        add(barra, BorderLayout.NORTH);
        add(tabla, BorderLayout.CENTER);

        btnNuevo.addActionListener(e -> nuevo());
        btnDevolver.addActionListener(e -> devolver());
        btnActualizar.addActionListener(e -> actualizar());
        cbFiltro.addActionListener(e -> actualizar());
        actualizar();
    }

    @Override
    public void actualizar() {
        String filtro = (String) cbFiltro.getSelectedItem();
        Tareas.ejecutar(() -> {
            if ("Activos".equals(filtro)) {
                return prestamos.activos();
            }
            if ("Atrasados".equals(filtro)) {
                return prestamos.atrasados();
            }
            return prestamos.todos();
        }, tabla::mostrar, ex -> Ui.error(this, ex));
    }

    private void nuevo() {
        List<Estudiante> listaEstudiantes;
        List<Libro> disponibles;
        try {
            listaEstudiantes = estudiantes.listar();
            disponibles = libros.listar().stream().filter(l -> l.getStock() > 0).toList();
        } catch (Exception ex) {
            Ui.error(this, ex);
            return;
        }
        if (listaEstudiantes.isEmpty() || disponibles.isEmpty()) {
            Ui.error(this, new IllegalStateException(
                    "Debe haber al menos un estudiante y un libro con ejemplares disponibles."));
            return;
        }

        JComboBox<Estudiante> cbEstudiante = new JComboBox<>(listaEstudiantes.toArray(new Estudiante[0]));
        JComboBox<Libro> cbLibro = new JComboBox<>(disponibles.toArray(new Libro[0]));
        JSpinner spPlazo = new JSpinner(new SpinnerNumberModel(
                Prestamo.PLAZO_MINIMO, Prestamo.PLAZO_MINIMO, Prestamo.PLAZO_MAXIMO, 1));
        JLabel lblVence = new JLabel();

        // El vencimiento se calcula solo y se actualiza al cambiar el plazo
        Runnable calcular = () -> lblVence.setText(
                LocalDate.now().plusDays((Integer) spPlazo.getValue()).toString());
        spPlazo.addChangeListener(e -> calcular.run());
        calcular.run();

        JPanel form = Ui.campos(
                new String[]{"Estudiante:", "Libro:", "Plazo (días, 15 a 30):", "Vence el:"},
                new JComponent[]{cbEstudiante, cbLibro, spPlazo, lblVence});

        boolean ok = Ui.formulario(this, "Nuevo préstamo", form, () -> {
            Estudiante estudiante = (Estudiante) cbEstudiante.getSelectedItem();
            Libro libro = (Libro) cbLibro.getSelectedItem();
            if (estudiante == null || libro == null) {
                throw new IllegalArgumentException("Selecciona un estudiante y un libro.");
            }
            Prestamo p = prestamos.prestar(estudiante.getId(), libro.getId(), (Integer) spPlazo.getValue());
            Ui.info(this, "Préstamo registrado. Fecha de vencimiento: " + p.getFechaEstimada());
        });
        if (ok) {
            actualizar();
        }
    }

    private void devolver() {
        int id = tabla.idSeleccionado();
        if (id == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un préstamo de la tabla.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!Ui.confirmar(this, "¿Registrar la devolución del préstamo " + id + "?")) {
            return;
        }
        try {
            prestamos.devolver(id);
            Ui.info(this, "Devolución registrada.");
            actualizar();
        } catch (Exception ex) {
            Ui.error(this, ex);
        }
    }
}
