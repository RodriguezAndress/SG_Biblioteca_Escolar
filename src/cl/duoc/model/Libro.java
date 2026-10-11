package cl.duoc.model;

public class Libro {
    private int id;
    private String titulo;
    private String autor;
    private String isbn;
    private String editorial;
    private int stock;
    private String categoria;

    private static void validarDatos(String titulo, String autor, String isbn) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El título del libro no puede estar vacío.");
        }
        if (autor == null || autor.isBlank()) {
            throw new IllegalArgumentException("El nombre del autor no puede estar vacío.");
        }
        if (isbn == null || isbn.isBlank()) {
            throw new IllegalArgumentException("El código ISBN no puede estar vacío.");
        }
    }

    public Libro(String titulo, String autor, String isbn, String editorial, int stock ,String categoria) {
        validarDatos(titulo,autor,isbn);
        if(stock < 0){
            throw new IllegalArgumentException("No hay stock disponible de este ejemplar.");
        }

        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.editorial = editorial;
        this.stock = stock;
        this.categoria = categoria;
    }
    public Libro(int id, String titulo, String autor, String isbn, String editorial, int stock ,String categoria) {
        this(titulo,autor,isbn,editorial, stock, categoria);
        this.id = id;
    }
    public void actualizarDatos(String titulo, String autor, String isbn, String editorial, String categoria) {
        validarDatos(titulo, autor, isbn);
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.editorial = editorial;
        this.categoria = categoria;
    }

    public int getId() {return id;}
    public String getTitulo() {return titulo;}
    public String getAutor() {return autor;}
    public String getIsbn() {return isbn;}
    public String getEditorial() {return editorial;}
    public String getCategoria() {return categoria;}
    public int getStock() {return stock;}

    @Override
    public String toString() {
        return titulo + " (stock: " + stock + ")";
    }

}
