package cl.duoc.dao;

import cl.duoc.conexion.ConexionDB;
import cl.duoc.model.Estudiante;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EstudianteDAO {
    public void guardar(Connection con, Estudiante e) throws SQLException {
        String sql = "INSERT INTO biblioteca.estudiantes (nombre, rut, curso, correo) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, e.getNombre());
            ps.setString(2, e.getRut());
            ps.setString(3, e.getCurso());
            ps.setString(4, e.getEmail());
            ps.executeUpdate();
        }
    }

    public List<Estudiante> listarTodos() throws SQLException {
        List<Estudiante> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, rut, curso, correo FROM biblioteca.estudiantes ORDER BY nombre";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // La tabla estudiantes no guarda contraseña: va null
                lista.add(new Estudiante(rs.getInt("id"), rs.getString("nombre"), rs.getString("rut"),
                        rs.getString("correo"), null, rs.getString("curso")));
            }
        }
        return lista;
    }
}
