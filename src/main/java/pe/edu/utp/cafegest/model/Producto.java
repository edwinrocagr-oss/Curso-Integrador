package pe.edu.utp.cafegest.model;
import java.math.BigDecimal;
public record Producto(int id, String nombre, String categoria, BigDecimal precio, int stock, int minimo, boolean activo) {
    @Override public String toString() { return nombre + " · S/ " + precio + " · stock " + stock; }
}
