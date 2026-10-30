package pe.edu.utp.cafegest.dao;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VentaDaoImpl implements IVentaDao {

    private final Connection conexion;

    // Inyección de dependencias
    public VentaDaoImpl(Connection conexion) {
        this.conexion = conexion;
    }

    @Override
    public ResumenVentas obtenerResumenHoy() throws SQLException {
        String sql = "SELECT COUNT(*), COALESCE(SUM(total),0) FROM ventas WHERE CAST(fecha AS DATE)=CURRENT_DATE";
        // Utilizamos la conexión inyectada, no Database.open()
        try (Statement s = this.conexion.createStatement();
             ResultSet r = s.executeQuery(sql)) {
            if (r.next()) {
                return new ResumenVentas(r.getInt(1), r.getBigDecimal(2));
            }
        }
        return new ResumenVentas(0, BigDecimal.ZERO);
    }

    @Override
    public int obtenerTotalVentasHistoricas() throws SQLException {
        String sql = "SELECT COUNT(*) FROM ventas";
        try (Statement s = this.conexion.createStatement();
             ResultSet r = s.executeQuery(sql)) {
            if (r.next()) {
                return r.getInt(1);
            }
        }
        return 0;
    }

    @Override
    public List<String[]> buscarVentas(LocalDate desde, LocalDate hasta, String usuarioFiltro) throws SQLException {
        List<String[]> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT id, fecha, usuario, total FROM ventas WHERE CAST(fecha AS DATE) >= ? AND CAST(fecha AS DATE) <= ?");
        
        boolean tieneFiltro = usuarioFiltro != null && !usuarioFiltro.isBlank();
        if (tieneFiltro) {
            sql.append(" AND LOWER(usuario) LIKE ?");
        }
        sql.append(" ORDER BY id DESC");

        try (PreparedStatement p = this.conexion.prepareStatement(sql.toString())) {
            // Asignamos las fechas convirtiendo LocalDate a java.sql.Date
            p.setDate(1, java.sql.Date.valueOf(desde));
            p.setDate(2, java.sql.Date.valueOf(hasta));
            
            if (tieneFiltro) {
                p.setString(3, "%" + usuarioFiltro.trim().toLowerCase() + "%");
            }
            
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    lista.add(new String[]{
                        r.getString("id"),
                        r.getString("fecha"),
                        r.getString("usuario"),
                        r.getBigDecimal("total").toPlainString() // Devolvemos el monto numérico limpio
                    });
                }
            }
        }
        return lista;
    }
}