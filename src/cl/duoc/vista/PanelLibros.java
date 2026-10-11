package cl.duoc.vista;

import cl.duoc.controlador.LibroControlador;
import cl.duoc.model.Libro;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Objects;

public class PanelLibros extends JPanel implements Actualizable{
    private static final String TODAS = "Todas las categorías";

    private final LibroControlador libros = new LibroControlador();
    private final DefaultTableModel modelo =
            Ui.modelo("ID", "Título", "Autor", "Editorial", "ISBN", "Categoría", "Stock");
    private final JTable tabla = new JTable(modelo);
    private final JTextField txtBuscar = new JTextField(15);
    private final JComboBox<String> cbCategoria = new JComboBox<>();

    public PanelLibros(boolean soloLectura) {
        setLayout(new BorderLayout(5, 5));
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBuscar = new JButton("Buscar / Actualizar");
        barra.add(new JLabel("Buscar:"));
        barra.add(txtBuscar);
        barra.add(cbCategoria);
        barra.add(btnBuscar);
        btnBuscar.addActionListener(e -> actualizar());
        txtBuscar.addActionListener(e -> actualizar());

        if (!soloLectura) {
            JButton btnNuevo = new JButton("Nuevo");
            JButton btnEditar = new JButton("Editar");
            JButton btnEliminar = new JButton("Eliminar");
            JButton btnEjemplares = new JButton("+ Ejemplares");
            barra.add(btnNuevo);
            barra.add(btnEditar);
            barra.add(btnEliminar);
            barra.add(btnEjemplares);
            btnNuevo.addActionListener(e -> nuevo());
            btnEditar.addActionListener(e -> editar());
            btnEliminar.addActionListener(e -> eliminar());
            btnEjemplares.addActionListener(e -> agregarEjemplares());
        }

        add(barra, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        cbCategoria.addItem(TODAS);
        Tareas.ejecutar(libros::categorias, nombres -> {
            nombres.forEach(cbCategoria::addItem);
            cbCategoria.addActionListener(e -> actualizar());   // recién ahora, para no disparar cargas de más
        }, ex -> Ui.error(this, ex));

        actualizar();
    }

    @Override
    public void actualizar() {
        String texto = txtBuscar.getText();
        String categoria = (String) cbCategoria.getSelectedItem();
        Tareas.ejecutar(() -> {
            List<Libro> base = texto.isBlank() ? libros.listar() : libros.buscar(texto);
            if (categoria != null && !categoria.equals(TODAS)) {
                return base.stream().filter(l -> categoria.equals(l.getCategoria())).toList();
            }
            return base;
        }, this::mostrar, ex -> Ui.error(this, ex));
    }

    private void mostrar(List<Libro> lista) {
        modelo.setRowCount(0);
        for (Libro l : lista) {
            modelo.addRow(new Object[]{l.getId(), l.getTitulo(), l.getAutor(),
                    Objects.toString(l.getEditorial(), ""), l.getIsbn(),
                    Objects.toString(l.getCategoria(), ""), l.getStock()});
        }
    }

    private int idSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un libro de la tabla.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return -1;
        }
        return (int) modelo.getValueAt(fila, 0);
    }

    private JComboBox<String> comboCategorias() {
        JComboBox<String> cb = new JComboBox<>();
        cb.addItem("");   // sin categoría
        for (int i = 1; i < cbCategoria.getItemCount(); i++) {
            cb.addItem(cbCategoria.getItemAt(i));
        }
        return cb;
    }

    private void nuevo() {
        JTextField txtTitulo = new JTextField();
        JTextField txtAutor = new JTextField();
        JTextField txtIsbn = new JTextField();
        JTextField txtEditorial = new JTextField();
        JTextField txtStock = new JTextField("1");
        JComboBox<String> cbCat = comboCategorias();

        JPanel form = Ui.campos(
                new String[]{"Título:", "Autor:", "ISBN:", "Editorial (opcional):", "Stock:", "Categoría (opcional):"},
                new JComponent[]{txtTitulo, txtAutor, txtIsbn, txtEditorial, txtStock, cbCat});

        boolean ok = Ui.formulario(this, "Nuevo libro", form, () -> libros.registrar(
                txtTitulo.getText(), txtAutor.getText(), txtIsbn.getText(), txtEditorial.getText(),
                txtStock.getText(), (String) cbCat.getSelectedItem()));
        if (ok) {
            actualizar();
        }
    }

    private void editar() {
        int id = idSeleccionado();
        if (id == -1) {
            return;
        }
        int fila = tabla.getSelectedRow();
        JTextField txtTitulo = new JTextField(String.valueOf(modelo.getValueAt(fila, 1)));
        JTextField txtAutor = new JTextField(String.valueOf(modelo.getValueAt(fila, 2)));
        JTextField txtEditorial = new JTextField(String.valueOf(modelo.getValueAt(fila, 3)));
        JTextField txtIsbn = new JTextField(String.valueOf(modelo.getValueAt(fila, 4)));
        JComboBox<String> cbCat = comboCategorias();
        cbCat.setSelectedItem(String.valueOf(modelo.getValueAt(fila, 5)));

        JPanel form = Ui.campos(
                new String[]{"Título:", "Autor:", "Editorial (opcional):", "ISBN:", "Categoría (opcional):"},
                new JComponent[]{txtTitulo, txtAutor, txtEditorial, txtIsbn, cbCat});

        boolean ok = Ui.formulario(this, "Editar libro", form, () -> libros.editar(id,
                txtTitulo.getText(), txtAutor.getText(), txtIsbn.getText(), txtEditorial.getText(),
                (String) cbCat.getSelectedItem()));
        if (ok) {
            actualizar();
        }
    }

    private void eliminar() {
        int id = idSeleccionado();
        if (id == -1 || !Ui.confirmar(this, "¿Eliminar el libro " + id + "?")) {
            return;
        }
        try {
            libros.eliminar(id);
            actualizar();
        } catch (Exception ex) {
            Ui.error(this, ex);
        }
    }

    private void agregarEjemplares() {
        int id = idSeleccionado();
        if (id == -1) {
            return;
        }
        String cantidad = JOptionPane.showInputDialog(this, "¿Cuántos ejemplares nuevos llegaron?");
        if (cantidad == null) {
            return;
        }
        try {
            libros.agregarEjemplares(id, cantidad);
            actualizar();
        } catch (Exception ex) {
            Ui.error(this, ex);
        }
    }
}
