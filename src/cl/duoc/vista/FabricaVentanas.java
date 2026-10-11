package cl.duoc.vista;

import cl.duoc.model.Usuario;

import javax.swing.*;

public final class FabricaVentanas {
    private FabricaVentanas() {}

    public static JFrame crear(Usuario usuario) {
        return switch (usuario.getRol()) {
            case BIBLIOTECARIO -> new VentanaPrincipal(usuario);
            case ESTUDIANTE -> new VentanaEstudiante(usuario);
        };
    }
}
