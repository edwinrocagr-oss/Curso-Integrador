package pe.edu.utp.cafegest.dao;

import pe.edu.utp.cafegest.model.Comanda;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComandaDaoImpl implements IComandaDao {
    
    private final Connection conexion;

    public ComandaDaoImpl(Connection conexion) {
        this.conexion = conexion;
    }

    @Override
    public List<Comanda> listarPendientes() throws SQLException {
        List<Comanda> lista = new ArrayList<>();
        // Solo traemos las que no están listas, ordenadas por la más antigua primero (FIFO)
        try (PreparedStatement p = this.conexion.prepareStatement("SELECT * FROM comandas WHERE estado = 'PENDIENTE' ORDER BY fecha ASC");
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                lista.add(new Comanda(
                    r.getInt("id"), r.getString("mesa"), r.getString("pedido"), 
                    r.getString("estado"), r.getTimestamp("fecha")
                ));
            }
        }
        return lista;
    }

    @Override
    public void registrar(String mesa, String pedido) throws SQLException {
        try (PreparedStatement p = this.conexion.prepareStatement("INSERT INTO comandas (mesa, pedido) VALUES (?, ?)")) {
            p.setString(1, mesa);
            p.setString(2, pedido);
            p.executeUpdate();
        }
    }

    @Override
    public void marcarCompletada(int id) throws SQLException {
        try (PreparedStatement p = this.conexion.prepareStatement("UPDATE comandas SET estado = 'LISTO' WHERE id = ?")) {
            p.setInt(1, id);
            p.executeUpdate();
        }
    }
}