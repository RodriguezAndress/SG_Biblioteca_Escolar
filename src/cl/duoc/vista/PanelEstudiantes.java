package cl.duoc.vista;

import cl.duoc.controlador.EstudianteControlador;
import cl.duoc.controlador.PrestamoControlador;
import cl.duoc.model.Estudiante;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PanelEstudiantes extends JPanel implements Actualizable{
    private final EstudianteControlador estudiantes = new EstudianteControlador();
    private final PrestamoControlador prestamos = new PrestamoControlador();
    private final DefaultTableModel modelo = Ui.modelo("ID", "Nombre", "RUT", "Curso", "Correo");
    private final JTable tabla = new JTable(modelo);

    public PanelEstudiantes() {
        setLayout(new BorderLayout(5, 5));
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNuevo = new JButton("Nuevo");
        JButton btnHistorial = new JButton("Ver historial");
        JButton btnActualizar = new JButton("Actualizar");
        barra.add(btnNuevo);
        barra.add(btnHistorial);
        barra.add(btnActualizar);

        add(barra, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        btnNuevo.addActionListener(e -> nuevo());
        btnHistorial.addActionListener(e -> verHistorial());
        btnActualizar.addActionListener(e -> actualizar());
        actualizar();
    }

    @Override
    public void actualizar() {
        Tareas.ejecutar(estudiantes::listar, lista -> {
            modelo.setRowCount(0);
            for (Estudiante est : lista) {
                modelo.addRow(new Object[]{est.getId(), est.getNombre(), est.getRut(),
                        est.getCurso(), est.getEmail()});
            }
        }, ex -> Ui.error(this, ex));
    }

    private void nuevo() {
        JTextField txtNombre = new JTextField();
        JTextField txtRut = new JTextField();
        JTextField txtCorreo = new JTextField();
        JPasswordField txtPassword = new JPasswordField();
        JTextField txtCurso = new JTextField();

        JPanel form = Ui.campos(
                new String[]{"Nombre:", "RUT (ej: 12345678-5):", "Correo:", "Contraseña:", "Curso:"},
                new JComponent[]{txtNombre, txtRut, txtCorreo, txtPassword, txtCurso});

        boolean ok = Ui.formulario(this, "Nuevo estudiante", form, () -> estudiantes.registrar(
                txtNombre.getText(), txtRut.getText(), txtCorreo.getText(),
                new String(txtPassword.getPassword()), txtCurso.getText()));
        if (ok) {
            Ui.info(this, "Estudiante registrado.");
            actualizar();
        }
    }

    private void verHistorial() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un estudiante de la tabla.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) modelo.getValueAt(fila, 0);
        String nombre = String.valueOf(modelo.getValueAt(fila, 1));

        Tareas.ejecutar(() -> prestamos.historial(id), lista -> {
            TablaPrestamos historial = new TablaPrestamos();
            historial.mostrar(lista);
            historial.setPreferredSize(new Dimension(800, 300));
            JOptionPane.showMessageDialog(this, historial, "Historial de " + nombre,
                    JOptionPane.PLAIN_MESSAGE);
        }, ex -> Ui.error(this, ex));
    }
}
