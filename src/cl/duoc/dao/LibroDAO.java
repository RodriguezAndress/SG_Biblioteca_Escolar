package cl.duoc.dao;

import cl.duoc.conexion.ConexionDB;
import cl.duoc.model.Libro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LibroDAO {

    private static final String SELECT_LIBROS =
            "SELECT l.id, l.titulo, l.autor, l.isbn, l.editorial, l.stock, c.nombre AS categoria "
                    + "FROM biblioteca.libros l "
                    + "LEFT JOIN biblioteca.categorias c ON l.id_categoria = c.id ";


    public Optional<Libro> buscarPorId (Integer id) throws SQLException {
        String sql = SELECT_LIBROS +  "WHERE l.id = ?";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
                return Optional.empty();
            }
        }
    }

    public List<Libro> listarTodos()throws SQLException {
        List<Libro> libros = new ArrayList<>();
        String sql = SELECT_LIBROS + "ORDER BY l.titulo";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                libros.add(mapear(rs));
            }
        }
        return libros;
    }
    public List<Libro> buscar(String texto) throws SQLException {
        List<Libro> libros = new ArrayList<>();
        String sql = SELECT_LIBROS + "WHERE l.titulo LIKE ? OR l.autor LIKE ? ORDER BY l.titulo";
        String patron = "%" + texto + "%";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, patron);
            ps.setString(2, patron);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    libros.add(mapear(rs));
                }
            }
        }
        return libros;
    }
    public boolean guardar(Libro libro) throws SQLException {
        String sql = "INSERT INTO biblioteca.libros (titulo, autor, isbn, editorial, stock, id_categoria) "
                + "VALUES (?, ?, ?, ?, ?, (SELECT id FROM biblioteca.categorias WHERE nombre = ?))";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getIsbn());
            ps.setString(4, libro.getEditorial());
            ps.setInt(5, libro.getStock());
            ps.setString(6, libro.getCategoria());
            return ps.executeUpdate() > 0;

        } catch (SQLIntegrityConstraintViolationException e) {
            // 1062 = valor duplicado en una columna UNIQUE (aquí, el ISBN)
            if (e.getErrorCode() == 1062) {
                throw new IllegalStateException("Ya existe un libro con el ISBN " + libro.getIsbn() + ".");
            }
            throw e;
        }
    }

    public boolean actualizar(Libro libro) throws SQLException {
        String sql = "UPDATE biblioteca.libros "
                + "SET titulo = ?, autor = ?, isbn = ?, editorial = ?, "
                + "id_categoria = (SELECT id FROM biblioteca.categorias WHERE nombre = ?) "
                + "WHERE id = ?";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getIsbn());
            ps.setString(4, libro.getEditorial());
            ps.setString(5, libro.getCategoria());
            ps.setInt(6, libro.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLIntegrityConstraintViolationException e) {
            if (e.getErrorCode() == 1062) {
                throw new IllegalStateException("Ya existe otro libro con el ISBN " + libro.getIsbn() + ".");
            }
            throw e;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sqlConteo = "SELECT COUNT(*) FROM biblioteca.prestamos WHERE id_libro = ?";
        String sqlDelete = "DELETE FROM biblioteca.libros WHERE id = ?";

        try (Connection con = ConexionDB.obtenerConexion()) {
            try (PreparedStatement ps = con.prepareStatement(sqlConteo)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    if (rs.getInt(1) > 0) {
                        throw new IllegalStateException(
                                "No se puede eliminar: el libro tiene préstamos registrados.");
                    }
                }
            }
            try (PreparedStatement ps = con.prepareStatement(sqlDelete)) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            }
        }
    }
    // La condición "stock > 0" hace la verificación y el descuento en un solo paso
    public boolean reducirStock(Connection con, int idLibro) throws SQLException {
        String sql = "UPDATE biblioteca.libros SET stock = stock - 1 WHERE id = ? AND stock > 0";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idLibro);
            return ps.executeUpdate() > 0;   // false = no había ejemplares disponibles
        }
    }

    public boolean aumentarStock(Connection con, int idLibro) throws SQLException {
        String sql = "UPDATE biblioteca.libros SET stock = stock + 1 WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idLibro);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean agregarEjemplares(int idLibro, int cantidad) throws SQLException {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que 0.");
        }
        String sql = "UPDATE biblioteca.libros SET stock = stock + ? WHERE id = ?";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, cantidad);
            ps.setInt(2, idLibro);
            return ps.executeUpdate() > 0;
        }
    }

    // Convierte una fila del ResultSet en un Libro
    private Libro mapear(ResultSet rs) throws SQLException {
        return new Libro(
                rs.getInt("id"),
                rs.getString("titulo"),
                rs.getString("autor"),
                rs.getString("isbn"),
                rs.getString("editorial"),
                rs.getInt("stock"),
                rs.getString("categoria"));
    }
}

