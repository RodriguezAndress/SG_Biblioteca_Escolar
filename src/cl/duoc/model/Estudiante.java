package cl.duoc.model;

public class Estudiante extends Usuario {
    private String curso;

    public Estudiante(String nombre, String rut, String email, String password , String curso) {
        super(nombre, rut, email, Rol.ESTUDIANTE, password);
        this.curso = validarCurso(curso);
    }
    //Constructor que toma datos desde DB con un id ya asignado
    public Estudiante(int id, String nombre, String rut, String email, String password, String curso) {
        super(id, nombre, rut, email, Rol.ESTUDIANTE, password);
        this.curso = validarCurso(curso);
    }
    private static String validarCurso(String curso) {
        if (curso == null || curso.isBlank()) {
            throw new IllegalArgumentException("El curso no puede estar vacío.");
        }
        return curso;
    }
    public String getCurso() {return curso;}


    @Override
    public boolean puedeAdministrar() {
        return false;
    }
    @Override
    public String toString() { return getNombre() + " (" + getRut() + ")";}

}
