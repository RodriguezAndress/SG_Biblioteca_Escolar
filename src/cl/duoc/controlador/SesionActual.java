package cl.duoc.controlador;

import cl.duoc.model.Usuario;

public class SesionActual {
    private static SesionActual instancia;
    private volatile Usuario usuario;

    private SesionActual() {}

    public static synchronized SesionActual getInstancia() {
        if (instancia == null) {
            instancia = new SesionActual();
        }
        return instancia;
    }

    public Usuario getUsuario() { return usuario; }
    public void iniciar(Usuario usuario) { this.usuario = usuario; }
    public void cerrar() { this.usuario = null; }
}
