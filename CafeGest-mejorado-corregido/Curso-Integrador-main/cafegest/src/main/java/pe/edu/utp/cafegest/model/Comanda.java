package pe.edu.utp.cafegest.model;

import java.sql.Timestamp;

public record Comanda(int id, Integer ventaId, String mesa, String pedido, EstadoComanda estado, Timestamp fecha) {}