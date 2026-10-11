package cl.duoc.controlador;

import cl.duoc.dao.UsuarioDAO;
import cl.duoc.model.Usuario;

import java.sql.SQLException;
import java.util.Optional;

public class AuthControlador {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Optional<Usuario> iniciarSesion(String correo, String password) throws SQLException {
        if (correo == null || correo.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Ingresa tu correo y tu contraseña.");
        }
        Optional<Usuario> usuario = usuarioDAO.autenticar(correo.trim(), password);
        usuario.ifPresent(SesionActual.getInstancia()::iniciar);
        return usuario;
    }

    public void cerrarSesion() {
        SesionActual.getInstancia().cerrar();
    }
}
