package cl.duoc.model;

public class Bibliotecario extends Usuario {

    public Bibliotecario(String nombre, String rut, String email, String password) {
        super(nombre, rut, email, Rol.BIBLIOTECARIO, password);
    }
    public Bibliotecario(int id, String nombre, String rut, String email, String password) {
        super(id, nombre, rut, email, Rol.BIBLIOTECARIO, password);
    }

    @Override
    public boolean puedeAdministrar() {
        return true;
    }
}
