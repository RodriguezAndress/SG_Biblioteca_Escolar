package cl.duoc.dao;

import cl.duoc.conexion.ConexionDB;
import cl.duoc.model.Bibliotecario;
import cl.duoc.model.Estudiante;
import cl.duoc.model.Rol;
import cl.duoc.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UsuarioDAO {
    public Optional<Usuario> autenticar(String correo, String contraseña) throws SQLException {
        String sql = "SELECT id, nombre, rut, correo, `contraseña`, rol " +
                "FROM biblioteca.usuarios WHERE correo = ? AND `contraseña` = ?";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, correo);
            ps.setString(2, contraseña);

        try (ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) {
                return Optional.empty();//datos incorrectos
            }
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String rut = rs.getString("rut");
                String email = rs.getString("correo");
                String pass = rs.getString("contraseña");
                Rol rol = Rol.valueOf(rs.getString("rol").toUpperCase());

                if (rol == Rol.BIBLIOTECARIO) {
                    return Optional.of(new Bibliotecario(id, nombre, rut, email, pass));
                }
                return Optional.of(construirEstudiante(con, nombre, rut, email, pass));
            }
        }
    }
    private Estudiante construirEstudiante(Connection con, String nombre, String rut,
                String email, String password) throws SQLException {
        String sql = "SELECT id, curso FROM biblioteca.estudiantes WHERE rut = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, rut);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalStateException(
                            "El usuario " + email + " no tiene ficha de estudiante.");
                }
                return new Estudiante(rs.getInt("id"), nombre, rut, email, password,
                        rs.getString("curso"));
            }
        }
    }
    public void guardar(Connection con, Usuario u) throws SQLException {
        String sql = "INSERT INTO biblioteca.usuarios (nombre, rut, correo, `contraseña`, rol) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getRut());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getPassword());
            ps.setString(5, u.getRol().name());
            ps.executeUpdate();
        }
    }
}
