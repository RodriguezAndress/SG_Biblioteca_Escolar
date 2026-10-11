package cl.duoc.controlador;

import cl.duoc.dao.CategoriaDAO;
import cl.duoc.dao.LibroDAO;
import cl.duoc.model.Libro;

import java.sql.SQLException;
import java.util.List;

public class LibroControlador extends ControladorBase {
    private final LibroDAO libroDAO = new LibroDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    public List<String> categorias() throws SQLException {
        return categoriaDAO.listarNombres();
    }

    // Lectura: la puede usar cualquier rol
    public List<Libro> listar() throws SQLException {
        return libroDAO.listarTodos();
    }
    public List<Libro> buscar(String texto) throws SQLException {
        return libroDAO.buscar(texto == null ? "" : texto.trim());
    }
    public void registrar(String titulo, String autor, String isbn, String editorial,
                          String stockTexto, String categoria) throws SQLException {
        exigirBibliotecario();
        int stock = parsearEntero(stockTexto, "El stock");
        if (stock < 1) {
            throw new IllegalArgumentException("Debes registrar al menos un ejemplar.");
        }
        libroDAO.guardar(new Libro(titulo, autor, isbn, opcional(editorial), stock, opcional(categoria)));
    }

    public void editar(int id, String titulo, String autor, String isbn,
                       String editorial, String categoria) throws SQLException {
        exigirBibliotecario();
        Libro libro = libroDAO.buscarPorId(id)
                .orElseThrow(() -> new IllegalStateException("El libro no existe."));
        libro.actualizarDatos(titulo, autor, isbn, opcional(editorial), opcional(categoria));
        libroDAO.actualizar(libro);
    }

    public void eliminar(int id) throws SQLException {
        exigirBibliotecario();
        libroDAO.eliminar(id);
    }

    public void agregarEjemplares(int id, String cantidadTexto) throws SQLException {
        exigirBibliotecario();
        libroDAO.agregarEjemplares(id, parsearEntero(cantidadTexto, "La cantidad"));
    }
}
