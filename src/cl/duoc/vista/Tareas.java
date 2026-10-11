package cl.duoc.vista;

import javax.swing.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public final class Tareas {
    private Tareas() {}

    public static <T> void ejecutar(Callable<T> tarea, Consumer<T> alExito, Consumer<Exception> alError) {
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return tarea.call();
            }

            @Override
            protected void done() {
                try {
                    alExito.accept(get());
                } catch (ExecutionException e) {
                    Throwable causa = e.getCause();
                    alError.accept(causa instanceof Exception ex ? ex : e);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }.execute();
    }
}
