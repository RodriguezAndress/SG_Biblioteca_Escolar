package cl.duoc.dao;

import cl.duoc.conexion.ConexionDB;
import cl.duoc.model.Prestamo;
import cl.duoc.model.PrestamoDetalle;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PrestamoDAO {
    private static final String SELECT_PRESTAMO =
            "SELECT id, id_estudiante, id_libro, fecha_prestamo, "
                    + "       fecha_devolucion, devuelto "
                    + "FROM biblioteca.prestamos ";

    private static final String SELECT_DETALLE =
            "SELECT p.id, p.id_estudiante, p.id_libro, p.fecha_prestamo, "
                    + "       p.fecha_devolucion, p.devuelto, "
                    + "       l.titulo, e.nombre AS nombre_estudiante "
                    + "FROM biblioteca.prestamos p "
                    + "JOIN biblioteca.libros l ON p.id_libro = l.id "
                    + "JOIN biblioteca.estudiantes e ON p.id_estudiante = e.id ";

    public boolean registrar(Connection con, Prestamo prestamo) throws SQLException {
        String sql = "INSERT INTO biblioteca.prestamos "
                + "(id_estudiante, id_libro, fecha_prestamo, fecha_devolucion) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, prestamo.getIdEstudiante());
            ps.setInt(2, prestamo.getIdLibro());
            ps.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
            ps.setDate(4, Date.valueOf(prestamo.getFechaEstimada()));
            return ps.executeUpdate() > 0;
        }
    }

    public boolean registrarDevolucion(Connection con, int idPrestamo, LocalDate fecha) throws SQLException {
        String sql = "UPDATE biblioteca.prestamos "
                + "SET devuelto = TRUE, fecha_devolucion = ? "
                + "WHERE id = ? AND devuelto = FALSE";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fecha));
            ps.setInt(2, idPrestamo);
            return ps.executeUpdate() > 0;   // false no existe o ya estaba devuelto
        }
    }

    public Optional<Prestamo> buscarPorId(Connection con, int id) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SELECT_PRESTAMO + "WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    public List<PrestamoDetalle> listarTodos() throws SQLException {
        return consultarDetalle("ORDER BY p.fecha_prestamo DESC", null);
    }
    public List<PrestamoDetalle> historialPorEstudiante(int idEstudiante) throws SQLException {
        return consultarDetalle("WHERE p.id_estudiante = ? ORDER BY p.fecha_prestamo DESC", idEstudiante);
    }
    public List<PrestamoDetalle> listarActivos() throws SQLException {
        return consultarDetalle("WHERE p.devuelto = FALSE ORDER BY p.fecha_devolucion", null);
    }
    public List<PrestamoDetalle> listarAtrasados() throws SQLException {
        return consultarDetalle(
                "WHERE p.devuelto = FALSE AND p.fecha_devolucion < CURDATE() ORDER BY p.fecha_devolucion", null);
    }

    // Cada fila: {id del libro, título, veces prestado}
    public List<Object[]> librosMasPrestados(int limite) throws SQLException {
        List<Object[]> ranking = new ArrayList<>();
        String sql = "SELECT l.id, l.titulo, COUNT(p.id) AS veces "
                + "FROM biblioteca.libros l "
                + "JOIN biblioteca.prestamos p ON p.id_libro = l.id "
                + "GROUP BY l.id, l.titulo "
                + "ORDER BY veces DESC, l.titulo "
                + "LIMIT ?";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ranking.add(new Object[]{rs.getInt("id"), rs.getString("titulo"), rs.getInt("veces")});
                }
            }
        }
        return ranking;
    }

    // Ejecuta SELECT_DETALLE
    private List<PrestamoDetalle> consultarDetalle(String filtro, Integer parametro) throws SQLException {
        List<PrestamoDetalle> lista = new ArrayList<>();
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(SELECT_DETALLE + filtro)) {

            if (parametro != null) {
                ps.setInt(1, parametro);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new PrestamoDetalle(
                            mapear(rs), rs.getString("titulo"), rs.getString("nombre_estudiante")));
                }
            }
        }
        return lista;
    }

    // Convierte una fila en un Prestamo
    private Prestamo mapear(ResultSet rs) throws SQLException {
        LocalDate fecha = rs.getDate("fecha_devolucion").toLocalDate();
        boolean devuelto = rs.getBoolean("devuelto");
        return new Prestamo(
                rs.getInt("id"),
                rs.getInt("id_estudiante"),
                rs.getInt("id_libro"),
                rs.getDate("fecha_prestamo").toLocalDate(),
                fecha,
                devuelto ? fecha : null);   // fechaDevolucionReal
    }
}
