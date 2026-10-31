package pe.edu.utp.cafegest.dao;

import pe.edu.utp.cafegest.model.Comanda;
import pe.edu.utp.cafegest.model.EstadoComanda;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComandaDaoImpl implements IComandaDao {
    
    private final Connection conexion;

    public ComandaDaoImpl(Connection conexion) {
        this.conexion = conexion;
    }

    @Override
    public List<Comanda> listarActivas() throws SQLException {
        List<Comanda> lista = new ArrayList<>();
        try (PreparedStatement p = this.conexion.prepareStatement(
                "SELECT id, venta_id, mesa, pedido, estado, fecha FROM comandas " +
                "WHERE estado <> 'ENTREGADO' " +
                "ORDER BY CASE estado WHEN 'PENDIENTE' THEN 0 WHEN 'PREPARANDO' THEN 1 WHEN 'LISTO' THEN 2 ELSE 3 END, fecha ASC");
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                int ventaId = r.getInt("venta_id");
                Integer venta = r.wasNull() ? null : ventaId;
                EstadoComanda estado;
                try {
                    estado = EstadoComanda.valueOf(r.getString("estado"));
                } catch (IllegalArgumentException e) {
                    throw new SQLException("Estado inválido en la comanda #" + r.getInt("id") + ".", e);
                }
                lista.add(new Comanda(r.getInt("id"), venta, r.getString("mesa"),
                    r.getString("pedido"), estado, r.getTimestamp("fecha")));
            }
        }
        return lista;
    }

    @Override
    public void cambiarEstado(int id, EstadoComanda actual, EstadoComanda siguiente) throws SQLException {
        if (!actual.puedeAvanzarA(siguiente)) {
            throw new IllegalArgumentException("La comanda solo puede avanzar al siguiente estado.");
        }
        try (PreparedStatement p = this.conexion.prepareStatement(
                "UPDATE comandas SET estado = ? WHERE id = ? AND estado = ?")) {
            p.setString(1, siguiente.name());
            p.setInt(2, id);
            p.setString(3, actual.name());
            if (p.executeUpdate() != 1) {
                throw new SQLException("La comanda cambió o ya no está disponible. Actualiza la lista e inténtalo otra vez.");
            }
        }
    }
}