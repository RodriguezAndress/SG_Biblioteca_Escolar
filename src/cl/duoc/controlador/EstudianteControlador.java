package cl.duoc.controlador;

import cl.duoc.conexion.ConexionDB;
import cl.duoc.dao.EstudianteDAO;
import cl.duoc.dao.UsuarioDAO;
import cl.duoc.model.Estudiante;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

public class EstudianteControlador extends ControladorBase{
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final EstudianteDAO estudianteDAO = new EstudianteDAO();

    // Escribe en las dos tablas (usuarios y estudiantes) dentro de una transacción
    public void registrar(String nombre, String rut, String correo,
                          String password, String curso) throws SQLException {
        exigirBibliotecario();
        Estudiante estudiante = new Estudiante(nombre, rut, correo, password, curso); // valida todo

        try (Connection con = ConexionDB.obtenerConexion()) {
            con.setAutoCommit(false);
            try {
                usuarioDAO.guardar(con, estudiante);
                estudianteDAO.guardar(con, estudiante);
                con.commit();
            } catch (SQLIntegrityConstraintViolationException e) {
                con.rollback();
                throw new IllegalStateException("Ya existe un usuario o estudiante con ese RUT.");
            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        }
    }
    public List<Estudiante> listar() throws SQLException {
        exigirBibliotecario();
        return estudianteDAO.listarTodos();
    }

}
