package pe.edu.utp.cafegest.dao;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface IVentaDao {
    
    record ResumenVentas(int cantidad, BigDecimal total) {}
    ResumenVentas obtenerResumenHoy() throws SQLException;
    int obtenerTotalVentasHistoricas() throws SQLException;

    List<String[]> buscarVentas(LocalDate desde, LocalDate hasta, String usuarioFiltro) throws SQLException;
}