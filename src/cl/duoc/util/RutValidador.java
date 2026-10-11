package cl.duoc.util;

public class RutValidador {
    public static boolean validar(String rut) {
        if (rut == null || rut.isBlank()) {
            return false;
        }
        String limpio = rut.replace(".", "").replace("-", "").trim().toUpperCase();
        return limpio.matches("\\d{7,8}[0-9K]");   // solo formato: el dígito verificador no se comprueba
    }

    public static String normalizar(String rut) {
        if (rut == null || rut.isBlank()) {
            return "";
        }
        String limpio = rut.replace(".", "").replace("-", "").trim().toUpperCase();
        return limpio.substring(0, limpio.length() - 1) + "-" + limpio.substring(limpio.length() - 1);
    }
}
