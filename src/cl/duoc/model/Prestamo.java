package cl.duoc.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Prestamo {
    private int id;
    private int idLibro;
    private int idEstudiante;
    private LocalDate fechaPrestamo;
    private LocalDate fechaEstimada;
    private LocalDate fechaDevolucionReal;


    public static final int PLAZO_MINIMO = 15;
    public static final int PLAZO_MAXIMO = 30;

    public Prestamo(int idEstudiante, int idLibro,
                    LocalDate fechaPrestamo, int diasPlazo) {
        if(fechaPrestamo == null) {
            throw new IllegalArgumentException("La fecha del prestamo es un campo obligatorio.");
        }
        if(diasPlazo < PLAZO_MINIMO || diasPlazo > PLAZO_MAXIMO) {
            throw new IllegalArgumentException("El plazo debe estar entre 15 y 30 días.");
        }
        this.idLibro = idLibro;
        this.idEstudiante = idEstudiante;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaEstimada = fechaPrestamo.plusDays(diasPlazo);
    }
    public Prestamo(int id, int idEstudiante, int idLibro, LocalDate fechaPrestamo,
                    LocalDate fechaEstimada, LocalDate fechaDevolucionReal) {
        this.id = id;
        this.idLibro = idLibro;
        this.idEstudiante = idEstudiante;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaEstimada = fechaEstimada;
        this.fechaDevolucionReal = fechaDevolucionReal;
    }
    public boolean estaActivo(){
        return fechaDevolucionReal == null;
    }

    public PrestamoEstado getEstado() {
        if (fechaDevolucionReal != null) return PrestamoEstado.DEVUELTO;
        if (LocalDate.now().isAfter(fechaEstimada)) return PrestamoEstado.ATRASADO;
        return PrestamoEstado.PRESTADO;
    }
    public void registrarDevolucion(LocalDate fecha){
        if(!estaActivo()){
            throw new IllegalStateException("El libro ya fue devuelto.");
        }
        if(fecha == null || fecha.isBefore(fechaPrestamo)){
            throw new IllegalArgumentException("La fecha de la devolucion no puede ser previa a la del prestamo");
        }
        this.fechaDevolucionReal = fecha;
    }
    //Dias que lleva el libro estando prestado
    public long diasPrestado(){
        LocalDate fin = (fechaDevolucionReal != null) ? fechaDevolucionReal : LocalDate.now();
        return ChronoUnit.DAYS.between(fechaPrestamo, fin);
    }

    public int getId() {return id;}
    public int getIdLibro() {return idLibro;}
    public int getIdEstudiante() {return idEstudiante;}
    public LocalDate getFechaPrestamo() {return fechaPrestamo;}
    public LocalDate getFechaEstimada() {return fechaEstimada;}
    public LocalDate getFechaDevolucionReal() {return fechaDevolucionReal;}


}
