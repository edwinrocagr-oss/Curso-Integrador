package pe.edu.utp.cafegest.model;
import java.sql.Timestamp;

public record Comanda(int id, String mesa, String pedido, String estado, Timestamp fecha) {}