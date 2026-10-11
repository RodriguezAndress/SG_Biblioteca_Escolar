package cl.duoc.controlador;

import cl.duoc.conexion.ConexionDB;
import cl.duoc.dao.LibroDAO;
import cl.duoc.dao.PrestamoDAO;
import cl.duoc.model.Prestamo;
import cl.duoc.model.PrestamoDetalle;
import cl.duoc.model.Usuario;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class PrestamoControlador extends ControladorBase {
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();
    private final LibroDAO libroDAO = new LibroDAO();

    public Prestamo prestar(int idEstudiante, int idLibro, int diasPlazo) throws SQLException {
        exigirBibliotecario();
        // Se crea ANTES de abrir la conexión: si el plazo no está entre 15 y 30, falla sin tocar la BD
        Prestamo prestamo = new Prestamo(idEstudiante, idLibro, LocalDate.now(), diasPlazo);

        try (Connection con = ConexionDB.obtenerConexion()) {
            con.setAutoCommit(false);
            try {
                // UPDATE condicional: verifica y descuenta en un solo paso atómico
                if (!libroDAO.reducirStock(con, idLibro)) {
                    throw new IllegalStateException("No hay ejemplares disponibles de este libro.");
                }
                prestamoDAO.registrar(con, prestamo);
                con.commit();
                return prestamo;
            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public void devolver(int idPrestamo) throws SQLException {
        exigirBibliotecario();
        LocalDate hoy = LocalDate.now();

        try (Connection con = ConexionDB.obtenerConexion()) {
            con.setAutoCommit(false);
            try {
                Prestamo prestamo = prestamoDAO.buscarPorId(con, idPrestamo)
                        .orElseThrow(() -> new IllegalStateException("El préstamo no existe."));
                prestamo.registrarDevolucion(hoy);   // valida que no esté devuelto ya
                prestamoDAO.registrarDevolucion(con, idPrestamo, hoy);
                libroDAO.aumentarStock(con, prestamo.getIdLibro());
                con.commit();
            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        }
    }

    // Un estudiante solo ve su historial. el bibliotecario ve el de cualquiera
    public List<PrestamoDetalle> historial(int idEstudiante) throws SQLException {
        Usuario quien = usuarioActual();
        if (!quien.puedeAdministrar() && quien.getId() != idEstudiante) {
            throw new IllegalStateException("Solo puedes ver tu propio historial.");
        }
        return prestamoDAO.historialPorEstudiante(idEstudiante);
    }
    public List<PrestamoDetalle> miHistorial() throws SQLException {
        return historial(usuarioActual().getId());
    }
    public List<PrestamoDetalle> todos() throws SQLException {
        exigirBibliotecario();
        return prestamoDAO.listarTodos();
    }
    public List<PrestamoDetalle> activos() throws SQLException {
        exigirBibliotecario();
        return prestamoDAO.listarActivos();
    }
    public List<PrestamoDetalle> atrasados() throws SQLException {
        exigirBibliotecario();
        return prestamoDAO.listarAtrasados();
    }
    public List<Object[]> masPrestados(int limite) throws SQLException {
        exigirBibliotecario();
        return prestamoDAO.librosMasPrestados(limite);
    }
}
