package cl.duoc.controlador;

import cl.duoc.model.Usuario;

public abstract class ControladorBase {
    protected Usuario usuarioActual() {
        Usuario u = SesionActual.getInstancia().getUsuario();
        if (u == null) {
            throw new IllegalStateException("Debes iniciar sesión.");
        }
        return u;
    }
    protected void exigirBibliotecario() {
        if (!usuarioActual().puedeAdministrar()) {
            throw new IllegalStateException("Esta acción es solo para el bibliotecario.");
        }
    }
    protected static int parsearEntero(String texto, String campo) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException | NullPointerException e) {
            throw new IllegalArgumentException(campo + " debe ser un número entero.");
        }
    }
    // Campos opcionales: vacío -> null
    protected static String opcional(String texto) {
        return (texto == null || texto.isBlank()) ? null : texto.trim();
    }
}
