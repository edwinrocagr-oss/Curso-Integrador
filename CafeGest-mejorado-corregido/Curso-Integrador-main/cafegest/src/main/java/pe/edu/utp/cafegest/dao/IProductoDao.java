package pe.edu.utp.cafegest.dao;

import pe.edu.utp.cafegest.model.Producto;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface IProductoDao {
    List<Producto> listar() throws SQLException;
    void guardar(Integer id, String nombre, String categoria, BigDecimal precio, int stock, int minimo) throws SQLException;
    void desactivar(int id) throws SQLException;
    void ajustar(int id, int delta, String observacion) throws SQLException;
}