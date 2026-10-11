package cl.duoc.vista;

import cl.duoc.model.Prestamo;
import cl.duoc.model.PrestamoDetalle;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class TablaPrestamos extends JScrollPane {
    private final DefaultTableModel modelo =
            Ui.modelo("ID", "Libro", "Estudiante", "Prestado el", "Vence / Devuelto el", "Estado", "Días");
    private final JTable tabla = new JTable(modelo);

    public TablaPrestamos() {
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setViewportView(tabla);
    }

    public void mostrar(List<PrestamoDetalle> lista) {
        modelo.setRowCount(0);
        for (PrestamoDetalle d : lista) {
            Prestamo p = d.prestamo();
            modelo.addRow(new Object[]{p.getId(), d.tituloLibro(), d.nombreEstudiante(),
                    p.getFechaPrestamo(), p.getFechaEstimada(), p.getEstado(), p.diasPrestado()});
        }
    }

    // id del préstamo seleccionado, o -1 si no hay ninguno
    public int idSeleccionado() {
        int fila = tabla.getSelectedRow();
        return fila == -1 ? -1 : (int) modelo.getValueAt(fila, 0);
    }
}
