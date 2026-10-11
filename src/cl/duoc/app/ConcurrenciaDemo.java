package cl.duoc.app;

import cl.duoc.controlador.AuthControlador;
import cl.duoc.controlador.LibroControlador;
import cl.duoc.controlador.PrestamoControlador;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

// Demostración de concurrencia: 10 hilos intentan prestar el único ejemplar de un libro.
// Resultado esperado: 1 préstamo exitoso y 9 rechazados. Crea datos de prueba (ISBN "DEMO-...").
public class ConcurrenciaDemo {
    public static void main(String[] args) throws Exception {
        new AuthControlador().iniciarSesion("antonia@correo.cl", "clave123").orElseThrow(() -> new IllegalStateException("Login fallido: revisa correo y contraseña"));

        LibroControlador libros = new LibroControlador();
        String isbn = "DEMO-" + System.currentTimeMillis();
        libros.registrar("Libro de prueba", "Autor", isbn, "", "1", "");   // 1 solo ejemplar
        int idLibro = libros.buscar("Libro de prueba").stream()
                .filter(l -> isbn.equals(l.getIsbn()))
                .findFirst().orElseThrow().getId();

        PrestamoControlador prestamos = new PrestamoControlador();
        ExecutorService pool = Executors.newFixedThreadPool(10);
        AtomicInteger exitos = new AtomicInteger();
        AtomicInteger rechazos = new AtomicInteger();

        for (int i = 0; i < 10; i++) {
            pool.submit(() -> {
                try {
                    prestamos.prestar(1, idLibro, 15);   // estudiante 1 = Carlos Ruiz
                    exitos.incrementAndGet();
                } catch (Exception e) {
                    rechazos.incrementAndGet();
                }
            });
        }
        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.SECONDS);

        System.out.println("Préstamos exitosos: " + exitos + " (esperado: 1)");
        System.out.println("Rechazados: " + rechazos + " (esperado: 9)");
    }
}
