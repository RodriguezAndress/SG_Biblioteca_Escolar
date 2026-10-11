package cl.duoc.model;

import cl.duoc.util.RutValidador;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Usuario {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    private int id;
    private String nombre;
    private String rut;
    private String email;
    private Rol rol;
    private String password;

    public Usuario(String nombre, String rut, String email, Rol rol, String password) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        if (email == null || !validarCorreo(email)) {
            throw new IllegalArgumentException("Formato para e-mail no válido.");
        }
        if (!RutValidador.validar(rut)) {
            throw new IllegalArgumentException("El RUT ingresado no es válido.");
        }
        if (rol == null) {
            throw new IllegalArgumentException("El rol es obligatorio.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }

        this.nombre = nombre;
        this.rut = RutValidador.normalizar(rut);
        this.email = email;
        this.rol = rol;
        this.password = password;
    }
    public Usuario(int id, String nombre, String rut, String email, Rol rol, String password) {
        this.id = id;
        this.nombre = nombre;
        this.rut = rut;
        this.email = email;
        this.rol = rol;
        this.password = password;
    }

    private static boolean validarCorreo(String email) {
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public String getNombre() {return nombre;}
    public void setNombre(String nombre) {this.nombre = nombre;}
    public String getRut() {return rut;}
    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}
    public Rol getRol() {return rol;}
    public String getPassword() {return password;}


    public abstract boolean puedeAdministrar();
}
